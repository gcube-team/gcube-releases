package org.gcube.datatransfer.agent.test;

import java.util.List;

//import org.gcube.common.clients.ClientRuntime;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.RPDocument;
import org.gcube.common.core.informationsystem.client.queries.WSResourceQuery;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QueryTest {

	GCUBEClientLog logger = new GCUBEClientLog(QueryTest.class);
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception{
		//ClientRuntime.start();
		{
			GCUBEScope scope = GCUBEScope.getScope("/gcube/devNext");
			ISClient client = GHNContext.getImplementation(ISClient.class);
			WSResourceQuery query =client.getQuery(WSResourceQuery.class);
			query.addAtomicConditions(new AtomicCondition("//gc:ServiceName","tree-manager-service"));
			query.addAtomicConditions(new AtomicCondition("//gc:ServiceClass","DataAccess"));
			query.addAtomicConditions(new AtomicCondition("//*[local-name()='SourceId']","test-source"));
				List<RPDocument> array = client.execute(query,scope);
			}
		
	}

}
