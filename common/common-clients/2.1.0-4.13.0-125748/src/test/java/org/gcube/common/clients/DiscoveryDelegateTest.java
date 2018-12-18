package org.gcube.common.clients;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static junit.framework.Assert.*;
import static org.gcube.common.clients.cache.Key.*;
import static org.mockito.Mockito.*;

import org.gcube.common.clients.cache.DefaultEndpointCache;
import org.gcube.common.clients.cache.EndpointCache;
import org.gcube.common.clients.cache.Key;
import org.gcube.common.clients.config.DiscoveryConfig;
import org.gcube.common.clients.config.Property;
import org.gcube.common.clients.delegates.DiscoveryDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.delegates.ProxyPlugin;
import org.gcube.common.clients.delegates.Unrecoverable;
import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.clients.queries.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DiscoveryDelegateTest {
	
	@Mock Call<Object,Object> call;
	@Mock ProxyPlugin<Object,Object,Object> plugin;
	@Mock Query<Object> query;
	@Mock DiscoveryConfig<Object,Object> config;
	
	@Mock Object endpoint;
	@Mock Object endpoint2;
	
	@Mock(name="some address") Object address;
	@Mock(name="some address2") Object address2;
	
	@Mock(name="some value") Object value;
	@Mock(name="some value2") Object value2;
	
	@Mock(name="some recoverable fault") Exception recoverable;
	@Mock(name="some unrecoverable fault") Exception unrecoverable;;
	
	EndpointCache<Object> cache = new DefaultEndpointCache<Object>();
	Key key;
	
	ProxyDelegate<Object> delegate;
	
	@Unrecoverable
	public static class UnrecoverableFault extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	@Before
	@SuppressWarnings("all")
	public void setup() throws Exception {
	
		String serviceName = "some service";
		
		//common staging for delegate
		when(plugin.name()).thenReturn(serviceName);
		when(plugin.convert(recoverable,config)).thenReturn(recoverable);
		when(plugin.convert(unrecoverable,config)).thenReturn(new UnrecoverableFault());
		
		when(config.plugin()).thenReturn((ProxyPlugin)plugin);
		when(config.query()).thenReturn(query);
		when(config.cache()).thenReturn(cache);
		
		when(plugin.resolve(address,config)).thenReturn(endpoint);
		when(plugin.resolve(address2,config)).thenReturn(endpoint2);
		
		key = key(serviceName,query);
		
		delegate = new DiscoveryDelegate<Object,Object>(config);
		
	}

	@Test
	public void proxiesHandleResolutionErrors() throws Exception {
		
		Exception e1 = new Exception();
		Exception e2 = new Exception();
		//stage
		when(query.fire()).thenReturn(asList(address,address2));
		when(plugin.resolve(address,config)).thenThrow(e1);
		when(plugin.resolve(address2,config)).thenThrow(e2);
		
		try {
			delegate.make(call);
			fail();
		}
		catch(Exception e) {
			assertEquals(e2,e);
		}
		
	}
	
	@Test
	public void proxiesDiscoverCallAndCacheEndpoints() throws Exception {
		
		//stage
		when(query.fire()).thenReturn(asList(address));
		when(call.call(endpoint)).thenReturn(value);
		
		//exercise client
		Object output = delegate.make(call);

		//address is discovered, resolved into an endpoint and endpoint is called
		assertEquals(output,value);
		
		//endpoint is cached
		assertEquals(address,cache.get(key));
		
	}
	
	@Test
	public void proxiesUseLGEBeforeExecutingQueries() throws Exception {
		
		//stage
		when(query.fire()).thenReturn(asList(address));
		when(call.call(endpoint)).thenReturn(value);

		//cache LGE
		cache.put(key, address);

		//exercise client
		delegate.make(call);
		
		//query is never fired
		verify(query,never()).fire();

	}
	
	@Test
	public void proxiesStopAndClearCacheIfLGEFailsUnrecoverably() throws Exception {
		
		//stage
		when(call.call(endpoint)).thenThrow(unrecoverable);
		
		//cache LGE
		cache.put(key, address);
		
		try {
		   //exercise client
		   delegate.make(call);
		   fail();
		}
		catch(UnrecoverableFault fault) {
			//fault has been converted
		}
		
		//query was never fired
		verify(query,never()).fire();
		
		//cache has been cleared
		assertNull(cache.get(key));
				
	}
	
	@Test
	public void proxiesDiscoverOtherEndpointsWhenLGEFailsRecoverably() throws Exception {
		
		//stage
		when(query.fire()).thenReturn(asList(address,address2));
		when(call.call(endpoint)).thenThrow(recoverable);
		when(call.call(endpoint2)).thenReturn(value2);
		
		//cache LGE
		cache.put(key, address);
		
		//exercise client
		Object output = delegate.make(call);
	
		//LGE fails, query is executed, LGE is filtered from results 
		//and remaining endpoint is successfully invoked
		assertEquals(output,value2);
		
		//cache has new LGE
		assertEquals(address2,cache.get(key));

	}
	
	
	@Test
	public void proxiesReturnLGEFailureIfQueryFails() throws Exception {
		
		//stage
		when(query.fire()).thenThrow(new DiscoveryException("error"));
		when(call.call(endpoint)).thenThrow(recoverable);
		
		//cache LGE
		cache.put(key, address);
		
		//exercise client
		try {
			delegate.make(call);
			fail();
		}
		catch(DiscoveryException unexpected) {
			fail();
		}
		catch(Exception LGEfault) {
			//fault has been converted
		}

	}
	
	@Test
	public void proxiesReturnLGEFailureIfQueryHasNoResults() throws Exception {
		
		//stage
		when(query.fire()).thenReturn(emptyList());
		when(call.call(endpoint)).thenThrow(recoverable);
		
		//cache LGE
		cache.put(key, address);
		
		//exercise client
		try {
			delegate.make(call);
			fail();
		}
		catch(DiscoveryException unexpected) {
			fail();
		}
		catch(Exception LGEfault) {
			//fault has been converted
		}

	}
	
	@Test
	public void proxiesReturnQueryFailureIfLGEIsUndefined() throws Exception {
		
		//stage
		when(query.fire()).thenThrow(new DiscoveryException("error"));
		
		//exercise client
		try {
			delegate.make(call);
			fail();
		}
		catch(DiscoveryException fault) {}

	}
	
	@Test
	public void proxiesReturnFailureIfQueryHasNoResults() throws Exception {
		
		//stage
		when(query.fire()).thenReturn(emptyList());
		
		//exercise client
		try {
			delegate.make(call);
			fail();
		}
		catch(DiscoveryException fault) {}

	}
	
	@Test
	public void proxiesRecoverIfQueryResultFailsRecoverably() throws Exception {
		
		//stage
		when(query.fire()).thenReturn(asList(address,address2));
		when(call.call(endpoint)).thenThrow(recoverable);
		when(call.call(endpoint2)).thenReturn(value2);
		
		Object output = delegate.make(call);
		
		assertEquals(output,value2);
	}
	
	@Test
	public void proxiesStopIfQueryResultFailsUnrecoverably() throws Exception {
		
		//stage
		when(query.fire()).thenReturn(asList(address,address2));
		when(call.call(endpoint)).thenThrow(unrecoverable);
		when(call.call(endpoint2)).thenReturn(value2);
		
		try {
			delegate.make(call);
			fail();
		}
		catch(UnrecoverableFault fault){}
		
	}
	
	@Test
	public void proxiesStopIfCallFailsRecoverablyButSessionIsSticky() throws Exception {
		
		//stage
		when(query.fire()).thenReturn(asList(address,address2));
		
		when(call.call(endpoint)).thenThrow(recoverable);
		when(call.call(endpoint2)).thenReturn(value2);
		
		when(config.hasProperty(Property.sticky_session)).thenReturn(true);
		when(config.property(Property.sticky_session,Boolean.class)).thenReturn(true);
		
		try {
			delegate.make(call);
			fail();
		}
		catch(Exception fault){}
		
	}
	
}
