package org.gcube.common.clients;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.StringWriter;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.gcube.common.clients.builders.AbstractBuilder;
import org.gcube.common.clients.builders.AbstractStatefulBuilder;
import org.gcube.common.clients.builders.AbstractStatelessBuilder;
import org.gcube.common.clients.builders.AddressingUtils;
import org.gcube.common.clients.builders.StatefulBuilderAPI;
import org.gcube.common.clients.builders.StatelessBuilderAPI;
import org.gcube.common.clients.cache.DefaultEndpointCache;
import org.gcube.common.clients.cache.EndpointCache;
import org.gcube.common.clients.config.DiscoveryConfig;
import org.gcube.common.clients.config.EndpointConfig;
import org.gcube.common.clients.config.Property;
import org.gcube.common.clients.config.ProxyConfig;
import org.gcube.common.clients.delegates.DirectDelegate;
import org.gcube.common.clients.delegates.DiscoveryDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.delegates.ProxyPlugin;
import org.gcube.common.clients.queries.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class BuildersTest {

	@Mock ProxyPlugin<W3CEndpointReference,Object,SampleProxy> plugin;
	
	@Mock(name="some cache") EndpointCache<W3CEndpointReference> cache;
	@Mock(name="sample query") Query<W3CEndpointReference> query;
	
	URI uriAddress = URI.create("http://foobar.org");
	
	Property<String> testProp = new Property<String>("name","value");
	
	StatelessBuilderAPI.Builder<SampleProxy> statelessBuilder;
	StatefulBuilderAPI.Builder<W3CEndpointReference,SampleProxy> statefulBuilder;
	
	//we build these proxies that give access to delegate so that we can make assertions
	class SampleProxy {
		
		ProxyDelegate<Object> delegate;
		
		public SampleProxy(ProxyDelegate<Object> delegate) {
			this.delegate=delegate;
		}
	}
	
	class SampleStatelessBuilder extends AbstractStatelessBuilder<W3CEndpointReference,Object,SampleProxy> {
		
		public SampleStatelessBuilder(EndpointCache<W3CEndpointReference> cache,Query<W3CEndpointReference> query, Property<?>...properties) {
			super(plugin, cache, query,properties);
		}
		
		@Override protected W3CEndpointReference convertAddress(W3CEndpointReference address) {
			return address;
		}
		
		@Override
		protected String contextPath() {
			return "/context/path/";
		}
	};
	
	class SampleStatefulBuilder extends AbstractStatefulBuilder<W3CEndpointReference,Object,SampleProxy> {
		
		public SampleStatefulBuilder(EndpointCache<W3CEndpointReference> cache, Property<?>...properties) {
			super(plugin,cache,properties);
		}
		
		@Override protected W3CEndpointReference convertAddress(W3CEndpointReference address) {
			return address;
		}
		
		@Override
		protected String contextPath() {
			return "/context/path/";
		}
	};
	
	@Before
	@SuppressWarnings("all")
	public void setup() throws Exception {
	
		when(plugin.name()).thenReturn("someservice");
		when(plugin.namespace()).thenReturn("http://acme.org");
		when(plugin.newProxy(any(ProxyDelegate.class))).thenAnswer(new Answer<SampleProxy>() {
			public SampleProxy answer(InvocationOnMock invocation) throws Throwable {
				return new SampleProxy((ProxyDelegate) invocation.getArguments()[0]);
			}
		});
		
	}
	
	@Test
	public void buildersBuildStatelessDirectProxy() {
		
		statelessBuilder = new SampleStatelessBuilder(cache,query);
		
		SampleProxy proxy = statelessBuilder.at(uriAddress).build();
		
		ProxyDelegate<Object> delegate = proxy.delegate;
		
		assertTrue(delegate instanceof DirectDelegate);
		
		ProxyConfig<?,Object> config = delegate.config();
		assertEquals(AbstractBuilder.defaultTimeout, config.timeout());
		assertEquals(plugin, config.plugin());
		assertTrue(config instanceof EndpointConfig);
		
		@SuppressWarnings("unchecked")
		EndpointConfig<W3CEndpointReference,Object> econfig = (EndpointConfig<W3CEndpointReference,Object>) config;
		
		StringWriter w1 = new StringWriter();
		StringWriter w2 = new StringWriter();
		AddressingUtils.address("/context/path/",plugin.name(),uriAddress).writeTo(new StreamResult(w1));
		econfig.address().writeTo(new StreamResult(w2));
		
		assertEquals(w1.toString(),w2.toString());
	}
	
	@Test
	public void buildersBuildStatefulDirectProxy() throws Exception {
		
		//with new timeout default
		statefulBuilder = new SampleStatefulBuilder(cache,Property.timeout(15,SECONDS));
		
		//with client-driven timeout
		SampleProxy proxy = statefulBuilder.at("key",uriAddress.toURL()).with(Property.timeout(20,SECONDS)).build();
		
		ProxyDelegate<Object> delegate = proxy.delegate;
		
		assertTrue(delegate instanceof DirectDelegate);
		
		ProxyConfig<?,Object> config = delegate.config();
		
		//client-driven timeout wins
		assertEquals((int)TimeUnit.SECONDS.toMillis(20), config.timeout());
		
		assertEquals(plugin, config.plugin());
		
		assertTrue(config instanceof EndpointConfig);
		
		@SuppressWarnings("unchecked")
		EndpointConfig<W3CEndpointReference,Object> econfig = (EndpointConfig<W3CEndpointReference,Object>) config;
		
		StringWriter w1 = new StringWriter();
		StringWriter w2 = new StringWriter();
		AddressingUtils.address("/context/path/",plugin.name(),plugin.namespace(),"key",uriAddress).writeTo(new StreamResult(w1));
		econfig.address().writeTo(new StreamResult(w2));
		
		System.out.println(w2.toString());
		assertEquals(w1.toString(),w2.toString());
	}
	
	@Test
	public void buildersBuildStatelessDiscoveryProxy() throws Exception {
		
		EndpointCache<W3CEndpointReference> newCache = new DefaultEndpointCache<W3CEndpointReference>();
		
		statelessBuilder = new SampleStatelessBuilder(newCache,query,Property.timeout(15,SECONDS),testProp);
		
		SampleProxy proxy = statelessBuilder.build();
		
		ProxyDelegate<Object> delegate = proxy.delegate;
		
		assertTrue(delegate instanceof DiscoveryDelegate);
		
		ProxyConfig<?,Object> config = delegate.config();
		
		assertEquals(TimeUnit.SECONDS.toMillis(15), config.timeout());
		assertTrue(config.hasProperty("name"));
		assertEquals(config.property("name",String.class),"value");
		
		assertEquals(plugin, config.plugin());

		assertTrue(config instanceof DiscoveryConfig);
		
		@SuppressWarnings("unchecked")
		DiscoveryConfig<W3CEndpointReference,Object> dconfig = (DiscoveryConfig<W3CEndpointReference,Object>) config;
		
		assertEquals(newCache, dconfig.cache());
		assertEquals(query, dconfig.query());
	}

	
	@Test
	public void buildersBuildStatefulDiscoveryProxy() {
		
		statefulBuilder = new SampleStatefulBuilder(cache);
		
		SampleProxy proxy = statefulBuilder.matching(query).with(testProp).withTimeout(15,SECONDS).build();
		
		ProxyDelegate<Object> delegate = proxy.delegate;
		
		assertTrue(delegate instanceof DiscoveryDelegate);
		
		ProxyConfig<?,Object> config = delegate.config();
		
		assertEquals(TimeUnit.SECONDS.toMillis(15), config.timeout());
		assertTrue(config.hasProperty(testProp.name()));
		assertEquals(config.property(testProp.name(),Object.class),testProp.value());
		
		assertEquals(plugin, config.plugin());

		assertTrue(config instanceof DiscoveryConfig);
		
		@SuppressWarnings("unchecked")
		DiscoveryConfig<W3CEndpointReference,Object> dconfig = (DiscoveryConfig<W3CEndpointReference,Object>) config;

		assertEquals(query, dconfig.query());
	}
}
