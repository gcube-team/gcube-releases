package org.gcube.common.informationsystem.client.queries;

import static org.junit.Assert.*;

import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.RPDocument;
import org.gcube.common.core.informationsystem.client.queries.WSResourceQuery;

public class WSResourceQueryTest extends BaseExistClientTest {

	@Override
	void testExecuteQuery() {
			try {
				WSResourceQuery query = this.getQuery(WSResourceQuery.class);
				query.addAtomicConditions(new AtomicCondition("//gc:RI","b5cab3d0-459c-11e0-a6fd-84083eefd186"));
				System.out.println(query.getExpression());
				int i = 0;
				for (RPDocument d : client.execute(query,this.scope)) {
					try {
					i++;
					System.out.println("found resource" + d.getKey());
					System.out.println(".. published by " + d.getRIID());
					System.out.println(".. at " + d.getEndpoint().getAddress().toString());
					} catch (Exception e) {
						System.err.println("failed to read the resource");
						e.printStackTrace();
					}
				}
				System.out.println("returned resources " +i);
				
			} catch (Exception e) {
				fail("");
				e.printStackTrace();
			}
		
	}

}
