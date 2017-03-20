package org.gcube.common.clients;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

import org.gcube.common.clients.config.EndpointConfig;
import org.gcube.common.clients.delegates.DirectDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.delegates.ProxyPlugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DirectDelegateTest {

	@Mock Call<Object,Object> call;
	@Mock ProxyPlugin<Object,Object,?> plugin;
	@Mock  EndpointConfig<Object,Object> config;
	@Mock(name="some address") Object address;
	@Mock Object endpoint;
	@Mock Object value;
	@Mock Exception original;
	@Mock Exception converted;
	
	
	ProxyDelegate<Object> delegate;
	
	@Before
	@SuppressWarnings("all")
	public void setup() throws Exception {
	
		//common configuration staging: mocking a delegate is not that immediate..
		when(config.plugin()).thenReturn((ProxyPlugin) plugin);
		when(config.address()).thenReturn(address);
		when(plugin.name()).thenReturn("some service");
		when(plugin.convert(original,config)).thenReturn(converted);
		when(plugin.resolve(address,config)).thenReturn(endpoint);

		//create subject-under-testing
		delegate = new DirectDelegate<Object,Object>(config);
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void proxiesHandleResolutionErrors() throws Exception {
		
		when(plugin.resolve(address,config)).thenThrow(new Exception());
		
		delegate.make(call);
		
	}
	
	@Test
	public void proxiesResolveAddressesAndMakeCalls() throws Exception {
		
		//stage call
		when(call.call(endpoint)).thenReturn(value);
		
		Object output = delegate.make(call);
		
		assertEquals(value,output);
		
		//note: this ensures that proxy has resolved address
		
	}
	
	@Test
	public void proxiesConvertAndReturnFaults() throws Exception {
		
		//stage call
		when(call.call(endpoint)).thenThrow(original);
		
		
		//exercise client passes on failures
		try {
			delegate.make(call);
			fail();
		}
		catch(Exception fault) {
			assertEquals(converted,fault);
		}
		
	}
	
}
