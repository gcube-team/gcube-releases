package org.gcube.common.informationsystem.client.queries;

import static org.junit.Assert.*;

import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.runninginstance.Endpoint;

/**
 * 
 * Unit test for {@link GCUBERIQuery} execution
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class RIQueryTest extends BaseExistClientTest {
	
	
	public void testExecuteQuery() {
		System.out.println("Testing GCUBERIQuery.class execution");
		GCUBERIQuery query;
		try {
			query = this.getQuery(GCUBERIQuery.class);
			query.addAtomicConditions(/*new AtomicCondition("//GHN/@UniqueID", GHNContext.getContext().getGHNID()), */
					new AtomicCondition("//ServiceClass", "WebApp"),
					new AtomicCondition("//ServiceName", "Fishery"));
			
			//query.addAtomicConditions(new AtomicCondition("/ID", "64d94dd0-8857-11e0-82a6-a83d707cabf3"));
			/*query.addAtomicConditions(new AtomicCondition("//GHN/@UniqueID", "4ed2e460-4a5e-11e0-b2ec-b25a903855c6"), 
					new AtomicCondition("//ServiceClass", "VREManagement"),
					new AtomicCondition("//ServiceName", "Deployer"));*/
			assertNotNull(query);
			System.out.println("Submitting the following query: \n" + query.getExpression() + "\n...in the scope " + scope.getName());
			List<GCUBERunningInstance> results = client.execute(query,scope);
			assertNotNull(results);
			//show all endpoints of all RIs
			for (GCUBERunningInstance instance : results) {
				assertNotNull("Null result retrieved inside the result set", instance);
				System.out.println("RI identifier " + instance.getID());
				for (Endpoint epr: instance.getAccessPoint().getRunningInstanceInterfaces().getEndpoint())
				System.out.println("RI endpoint " + epr.getValue());
				System.out.println("GHN ID " + instance.getGHNID());
			}
			System.out.println("The query was successfully executed");
			System.out.println("Number of results " + results.size());

		} catch (Exception e) {
			fail("Fail to execute the query: GCUBERIQuery.class");
			e.printStackTrace();
		}
		
	}

}
