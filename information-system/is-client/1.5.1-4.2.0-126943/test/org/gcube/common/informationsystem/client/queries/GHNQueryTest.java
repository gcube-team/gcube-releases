package org.gcube.common.informationsystem.client.queries;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGHNQuery;
import org.gcube.common.core.resources.GCUBEHostingNode;


/**
 * 
 * Unit test for {@link GCUBEGHNQuery} execution
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class GHNQueryTest extends BaseExistClientTest{
	

	public void testExecuteQuery() {
		System.out.println("Testing GCUBEGHNQuery execution");
		GCUBEGHNQuery query;
		try {
			query = this.getQuery(GCUBEGHNQuery.class);
			assertNotNull(query);
			//query.addAtomicConditions(new AtomicCondition("/ID","48ff21a0-458d-11e0-8296-e240fd6fc1c8"));
			System.out.println("Submitting the following query: \n" + query.getExpression() + "\n...in the scope " + scope.getName());
			List<GCUBEHostingNode> results = client.execute(query,scope);
			assertNotNull(results);
			//show all endpoints of all RIs
			for (GCUBEHostingNode ghn : results) {
				assertNotNull("Null result retrieved inside the result set", ghn);
				System.out.println("Retrieved GHN \n" + this.toString(ghn));
			}
			System.out.println("The query was successfully executed");
			System.out.println("Number of results " + results.size());
		} catch (Exception e) {
			fail("Fail to execute the query: GCUBEGHNQuery.class");
			e.printStackTrace();
		}
		
	}


}
