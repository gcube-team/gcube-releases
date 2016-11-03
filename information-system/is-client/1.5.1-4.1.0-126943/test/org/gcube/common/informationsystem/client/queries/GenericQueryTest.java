package org.gcube.common.informationsystem.client.queries;

import static org.junit.Assert.fail;


import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;

public class GenericQueryTest extends BaseExistClientTest {

	@Override
	void testExecuteQuery() {

		GCUBEGenericQuery query;
		try {
			query = this.getQuery(GCUBEGenericQuery.class);
			query.setExpression(this.getAllRPIDsExpression());
			int i = 0;
			for (XMLResult rpd :  client.execute(query,scope)) {
				System.out.println(rpd.toString().trim());
				i++;
			}
			System.out.println("returned results " +i);
			
		} catch (Exception e) {
			fail("Failed to execute GCUBEGenericQuery.class");
			e.printStackTrace();
		}
		
	}

	private String getAllRPsExpression() {
		return "declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry'; "
		+ "declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider'; "
		+ "for $outer in collection(\"/db/Properties\")//Document, $result in  $outer/Data "
		+ "return $outer";
	}
	
	private String getAllRPIDsExpression() {
		return "declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry'; "
		+ "declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider'; "
		+ "for $outer in collection(\"/db/Properties\")//Document, $result in  $outer/Data  "
		//+ "where ($result//gc:RI/string() eq \"b5cab3d0-459c-11e0-a6fd-84083eefd186\") "
		+ "return $outer/ID/text()";
	}
	
}
