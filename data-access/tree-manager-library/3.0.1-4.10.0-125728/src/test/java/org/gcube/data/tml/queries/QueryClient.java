package org.gcube.data.tml.queries;

import static junit.framework.Assert.*;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class QueryClient {

	@BeforeClass
	public static void setup() {
		ScopeProvider.instance.set("/gcube/devNext");
	}
	
	@Test
	public void binderQuery() {
		
		StatefulQuery q = TServiceFactory.plugin("tree-repository");
		
		List<EndpointReference> refs = q.fire();
		
		System.out.println(refs);
		
		assertFalse(refs.isEmpty());
		
	}
	
	@Test
	public void readerQueries() {
		
		StatefulQuery q = TServiceFactory.readSource().withId("8b177323-53e5-4713-ac3c-180471b98ad3").build();
		
		List<EndpointReference> refs = q.fire();
		
		System.out.println(refs);
		
		assertFalse(refs.isEmpty());
		
		q = TServiceFactory.readSource().withName("Parachela collection from Itis with no id").build();
		
		refs = q.fire();
		
		System.out.println(refs);
		
		assertFalse(refs.isEmpty());
		
		q = TServiceFactory.readSource().withType(new QName("http://acme.org","sampledata")).build();
		
		refs = q.fire();
		
		System.out.println(refs);
		
		assertFalse(refs.isEmpty());
		
	}
}
