package org.gcube.data.tml.proxies;

import static org.gcube.common.clients.delegates.MockDelegate.*;
import static org.gcube.data.tml.proxies.TServiceFactory.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.gcube.common.clients.delegates.Callback;
import org.gcube.common.clients.exceptions.InvalidRequestException;
import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.data.tml.stubs.TBinderStub;
import org.gcube.data.tml.stubs.Types.BindingsHolder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;


@RunWith(MockitoJUnitRunner.class)
public class BinderTest {
	
	//SUT
	DefaultTBinder proxy;
	
	//drives test
	@Mock TBinderStub endpoint;
	
	//test objects
	BindRequest params;
	List<Binding> bindings;
	BindingsHolder bindingsHolder = new BindingsHolder();
	@Mock SOAPFaultException failure;
	W3CEndpointReference address;
	String source = "id";
	
	
	@Before
	public void setup() throws Exception {
	
		proxy = new DefaultTBinder(mockDelegate(binderPlugin,endpoint));
		
		//setup test objects
		params = new BindRequest();
		address = new W3CEndpointReferenceBuilder().address("http://acme.org").build();
		bindings = Arrays.asList(new Binding(source,address,address));
		
		bindingsHolder.bindings=bindings;
	}
	
	@Test
	public void bindingParametersSerializeCorrectly() throws Exception {

		Document payload = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		
		BindRequest params = new BindRequest("foo", payload.createElement("foo"));
		
		StringWriter writer = new StringWriter();
		
		JAXBContext context = JAXBContext.newInstance(BindRequest.class);
		context.createMarshaller().marshal(params, writer);
		context.createUnmarshaller().unmarshal(new StringReader(writer.toString()));

	}

	@Test
	public void binderHandlesNullResponse() throws Exception {
		
		when(endpoint.bind(any(BindRequest.class))).thenReturn(null);
		
		List<Binding> bindings  = proxy.bind(params);
		
		assertTrue(bindings.isEmpty());
			
	}
	
	@Test
	public void binderHandlesInvalidRequest() throws Exception {
		
		when(endpoint.bind(any(BindRequest.class))).thenThrow(new InvalidRequestException("prob"));
		
		try {
			proxy.bind(params);
			fail();
		}
		catch(InvalidRequestException e) {}
			
	}
	
	@Test
	public void binderHandlesGenericFault() throws Exception {
		
		when(endpoint.bind(any(BindRequest.class))).thenThrow(failure);
		
		try {
			proxy.bind(params);
			fail();
		}
		catch(ServiceException e) {
			
			assertEquals(failure, e.getCause());
		
		}
			
	}
	
	@Test
	public void binderHandlesBindings() throws Exception {
		

		when(endpoint.bind(any(BindRequest.class))).thenReturn(bindingsHolder);
		
		List<Binding> bindings = proxy.bind(params);
		
		assertFalse(bindings.isEmpty());
		
		Binding binding = bindings.get(0);
		
		assertEquals(source, binding.source());

		assertEquals(address, binding.readerRef());
		assertEquals(address, binding.writerRef());
			
	}
	
	@Test
	public void binderHandlesAsyncPolling() throws Exception {
		
		when(endpoint.bind(any(BindRequest.class))).thenReturn(bindingsHolder);
		
		Future<List<Binding>> future  = proxy.bindAsync(params);
		
		List<Binding> bindings = future.get();
		
		assertFalse(bindings.isEmpty());
		
		Binding binding = bindings.get(0);
		
		assertEquals(source, binding.source());
		
		assertEquals(address, binding.readerRef());
		assertEquals(address, binding.writerRef());
			
	}
	
	@Test
	public void binderHandlesAsyncNotifications() throws Exception {
		
		when(endpoint.bind(any(BindRequest.class))).thenReturn(bindingsHolder);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		class Failure {
			Throwable is;
		};
		
		final Failure failure = new Failure();
		
		Callback<List<Binding>> callback = new Callback<List<Binding>>() {

			@Override
			public void done(List<Binding> bindings) {

				try {
					assertFalse(bindings.isEmpty());
					
					Binding binding = bindings.get(0);
					
					assertEquals(source, binding.source());
					assertEquals(address, binding.readerRef());
					assertEquals(address, binding.writerRef());	
				
				}
				catch(AssertionError error) {
					failure.is = error;
				}
				
				latch.countDown();

			}
			
			@Override
			public void onFailure(Throwable f) {
				failure.is = f;
				latch.countDown();
			}
			
			@Override
			public long timeout() {
				return 50;
			}
			
		};
		
		proxy.bindAsync(params,callback);

		//we're waiting no longer than this
		latch.await(1,TimeUnit.SECONDS);
		
		assertNull(failure.is);
		
		
			
	}
	
	
}
