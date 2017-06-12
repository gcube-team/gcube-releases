package org.gcube.common.clients;

import static java.util.concurrent.TimeUnit.*;
import static junit.framework.Assert.*;
import static org.gcube.common.clients.delegates.MockDelegate.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.Callback;
import org.gcube.common.clients.delegates.ProxyPlugin;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class AsyncDelegateTest {

	AsyncProxyDelegate<Object> delegate;
	
	@Mock ProxyPlugin<Object,Object,?> plugin;
	@Mock Object endpoint;
	
	@Mock Call<Object,Object> call;
	
	@Mock Object value;
	@Mock Exception original;
	@Mock Exception converted;
	
	@Before
	@SuppressWarnings("all")
	public void setup() throws Exception {


		//create subject-under-testing
		delegate =new AsyncProxyDelegate<Object>(mockDelegate(plugin,endpoint));
		
		//common configuration staging: mocking a delegate is not that immediate..
		when(plugin.name()).thenReturn("some service");
		when(plugin.convert(original,delegate.config())).thenReturn(converted);

		
	}
	
	@Test
	public void asyncCallsReturnFutureValues() throws Exception {
		
		//stage call
		when(call.call(endpoint)).thenReturn(value);
		
		Future<Object> future = delegate.makeAsync(call);
		
		Object output =  future.get();
		
		assertEquals(value,output);
		
		assertFalse(future.isCancelled());
		
		assertTrue(future.isDone());
	}

	@Test
	public void asyncCallsTimeout() throws Exception {
		
		//stage call
		Answer<?> slowly = new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Thread.sleep(300); //simulate longer process within call timeout
				return value;
			}
		};
		when(call.call(endpoint)).thenAnswer(slowly);
		
		Future<Object> future = delegate.makeAsync(call);
		
		try {
			future.get(100,TimeUnit.MILLISECONDS);
			fail();
		}
		catch(TimeoutException e) {}
		
	}
	
	@Test
	public void asyncCallsExecuteInCallScope() throws Exception {
		
		final String scope = "a/b/c";
		ScopeProvider.instance.set(scope);
		
		//stage call
		Answer<?> checkingScope= new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				assertEquals(scope,ScopeProvider.instance.get());
				return value;
			}
		};
		
		when(call.call(endpoint)).thenAnswer(checkingScope);
		
		Future<Object> future = delegate.makeAsync(call);
		
		future.get();
	}
	
	@Test
	public void asyncCallReturnConvertedFaultsAsInnerCauses() throws Exception {
		
		//stage call
		when(call.call(endpoint)).thenThrow(original);
		
		Future<Object> future = delegate.makeAsync(call);
		
		try {
			future.get();
			fail();
		}
		catch(Exception fault) {
			assertEquals(converted,fault.getCause());
		}
		
	}

	@Test
	public void asyncCallsAreInterrupted() throws Exception {
		
		final String scope = "a/b/c";
		ScopeProvider.instance.set(scope);
		
		//stage call
		Answer<?> slowly = new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Thread.sleep(300); //simulate longer process within call timeout
				return value;
			}
		};
		when(call.call(endpoint)).thenAnswer(slowly);

		final Future<Object> future = delegate.makeAsync(call);		

		new Thread() {
			public void run() {
				
				future.cancel(true);
				
			};
		}.start();
		
		try {
			future.get();
			fail();
		}
		catch(CancellationException fault) {
			assertTrue(future.isCancelled());
		}
		
	}
	
	@Test
	public void callbacksGetResults() throws Exception {
		
		//stage call
		Answer<?> answer = new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return value;
			}
		};
				
		when(call.call(endpoint)).thenAnswer(answer);
		
		@SuppressWarnings("all")
		Callback<Object> callback = mock(Callback.class);

		Future<?> future = delegate.makeAsync(call,callback);		

		//make sure the callback has had time to arrive
		Thread.sleep(400);
		
		verify(callback).done(value);
		
		assertTrue(future.isDone());
		
	}
	
	@Test
	public void callbacksGetTimeoutErrors() throws Exception {
		
		//stage call
		Answer<?> slowly = new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Thread.sleep(1000);
				return value;
			}
		};
				
		when(call.call(endpoint)).thenAnswer(slowly);
		
		@SuppressWarnings("all")
		Callback<Object> callback = mock(Callback.class);
		when(callback.timeout()).thenReturn(100L);

		final CountDownLatch latch = new CountDownLatch(1);
		
		Answer<?> unblock = new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				latch.countDown();
				return value;
			}
		};
		
		doAnswer(unblock).when(callback).onFailure(any(TimeoutException.class));
		
		Future<?> future = delegate.makeAsync(call,callback);
	
		//makes sure callback has been invoked
		latch.await(5,SECONDS);
		
		assertTrue(future.isCancelled());
	}
	
	@Test
	public void callbacksGetFaults() throws Exception {
		
		when(call.call(endpoint)).thenThrow(original);
		
		@SuppressWarnings("all")
		Callback<Object> callback = mock(Callback.class);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		Answer<?> unblock = new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Throwable e = (Throwable) invocation.getArguments()[0];
				assertEquals(converted,e);
				//male sure this method has been invoked
				latch.countDown();
				return null;
			}
		};
		
		doAnswer(unblock).when(callback).onFailure(any(Throwable.class));
		
		delegate.makeAsync(call,callback);
		
		//makes sure callback has been invoked
		latch.await(1,SECONDS);
		
	}
	
}
