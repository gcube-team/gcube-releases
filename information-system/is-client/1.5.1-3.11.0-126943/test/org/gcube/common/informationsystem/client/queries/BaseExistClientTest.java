package org.gcube.common.informationsystem.client.queries;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.Calendar;

import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.ISQuery;
import org.gcube.common.core.informationsystem.client.ISClient.ISUnsupportedQueryException;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.informationsystem.client.eximpl.ExistClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class BaseExistClientTest {

	protected ISClient client;
	protected GCUBEScope scope;
	
	@Before
	public void setUp() throws Exception {
		//client = GHNContext.getImplementation(ISClient.class);
		client = new ExistClient();
		scope = GCUBEScope.getScope("/CNRPrivate");
		assertNotNull("Invalid scope", scope);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExecute() {
		long now = Calendar.getInstance().getTimeInMillis();
		this.testExecuteQuery();
		System.out.println("Query took " + (Calendar.getInstance().getTimeInMillis() - now) + " milliseconds");
	}
	
	public <RESULT, QUERY extends ISQuery<RESULT>> QUERY getQuery(Class<QUERY> type) throws Exception {
		try {
			return client.getQuery(type);
		} catch (ISUnsupportedQueryException e) {
			fail("Unsupported query: " + type.getClass().getName());
			throw e;
		} catch (InstantiationException e) {
			fail("Failed to instantiate the query: " + type.getClass().getName());
			throw e;
		} catch (IllegalAccessException e) {
			fail("Illegal Access to the query: " + type.getClass().getName());
			throw e;
		}
	}
	

	abstract void testExecuteQuery();

	@Test
	public void testExecuteByRef() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetQueryString() {
	
	}
	
	protected String toString(GCUBEResource resource) throws Exception {
		StringWriter writer = new StringWriter();
		resource.store(writer);
		return writer.toString();
	}

}
