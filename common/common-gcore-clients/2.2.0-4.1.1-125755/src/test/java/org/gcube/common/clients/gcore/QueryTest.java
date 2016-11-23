package org.gcube.common.clients.gcore;

import static java.util.Arrays.*;
import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.InputStreamReader;
import java.util.List;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.clients.gcore.plugins.Plugin;
import org.gcube.common.clients.gcore.queries.ISFacade;
import org.gcube.common.clients.gcore.queries.StatefulQuery;
import org.gcube.common.clients.gcore.queries.StatelessQuery;
import org.gcube.common.clients.queries.ResultMatcher;
import org.gcube.common.core.informationsystem.client.RPDocument;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.resources.kxml.runninginstance.KGCUBERunningInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QueryTest {

	@Mock ISFacade facade;
	@Mock ISFacade statefulFacade;
	@Mock Plugin<Object,Object> plugin;
	@Mock ResultMatcher<GCUBERunningInstance> matcher;
	@Mock ResultMatcher<RPDocument> instanceMatcher;
	
	String name = "name";
	String serviceName = "servicename";
	String serviceclass = "serviceclass";
	
	StatelessQuery query;
	StatefulQuery statefulQuery;
	
	GCUBERunningInstance result = new KGCUBERunningInstance();
	
	@Mock RPDocument instance; 
	
	@Before
	@SuppressWarnings("all")
	public void setup() throws Exception {
		
		//stage mock plugin
		when(plugin.name()).thenReturn(name);
		when(plugin.serviceName()).thenReturn(serviceName);
		when(plugin.serviceClass()).thenReturn(serviceclass);
	
		when(facade.execute(any(Class.class),anyMap())).thenReturn(asList(result));
		when(statefulFacade.execute(any(Class.class),anyMap())).thenReturn(asList(instance));
		
		//parse sample results
		result.load(new InputStreamReader(this.getClass().getResourceAsStream("/ri.xml")));	
		
		EndpointReferenceType epr = new EndpointReferenceType();
		epr.setAddress(new AttributedURI("http://acme.org/wsrf/services"+name));
		
		when(instance.getEndpoint()).thenReturn(epr);
		
		query = new StatelessQuery(facade,plugin);
		statefulQuery = new StatefulQuery(statefulFacade, plugin);
	}
	
	@Test
	public void queriesAreValueObjects() {
	
		StatelessQuery query2 = new StatelessQuery(facade,plugin);
		
		assertEquals(query, query2);
		
		query.addCondition("a","val");
		query2.addCondition("a","val");
		
		assertEquals(query, query2);
	}
	
	@Test
	public void statefulQueriesAreValueObjects() {
	
		StatefulQuery statefulQuery2 = new StatefulQuery(statefulFacade,plugin);
		
		assertEquals(statefulQuery, statefulQuery2);
		
		statefulQuery.addCondition("a","val");
		statefulQuery2.addCondition("a","val");
		
		assertEquals(statefulQuery, statefulQuery2);
	}
	
	@Test
	public void queriesDispatchQueriesAndResults() {
	
		List<EndpointReferenceType> results = query.fire();
		
		assertTrue(results.size()==1);
		
	}
	
	@Test
	public void statefulQueriesDispatchQueriesAndResults() {
	
		List<EndpointReferenceType> results = statefulQuery.fire();
		
		assertTrue(results.size()==1);
		
	}
	
	@Test
	public void queriesExcludeResults() {
	
		when(plugin.name()).thenReturn("bad");
		
		List<EndpointReferenceType> results = query.fire();
		
		assertTrue(results.size()==0);
		
	}
	
	@Test
	public void statefulQueriesExcludeResults() {
	
		when(plugin.name()).thenReturn("bad");
		
		List<EndpointReferenceType> results = statefulQuery.fire();
		
		assertTrue(results.size()==0);
		
	}
	
	
	@Test
	public void queriesMatchResults() {
	
		when(matcher.match(result)).thenReturn(false);
		
		query.setMatcher(matcher);
		
		List<EndpointReferenceType> results = query.fire();
		
		assertTrue(results.size()==0);
		
	}
	
	@Test
	public void statefulQueriesMatchResults() {
	
		when(matcher.match(result)).thenReturn(false);
		
		statefulQuery.setMatcher(instanceMatcher);
		
		List<EndpointReferenceType> results = statefulQuery.fire();
		
		assertTrue(results.size()==0);
		
	}
}
