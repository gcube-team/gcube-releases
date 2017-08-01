package org.gcube.datatransfer.common.messaging.test;

import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.gcube.common.messaging.endpoints.EndpointRetriever;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;


public class QueryTest {
	
	@BeforeClass
	public static void setup() {
	
	}
	
	@Test
	public void allServiceEndpoints() throws Exception {
		
		EndpointRetriever retriever = new EndpointRetriever("/gcube/devsec");
		List<AccessPoint> accesspoints = retriever.retrieveMessageBrokerEndpoints();
		
		for (AccessPoint point : accesspoints) {
			System.out.println(point.address());
			
		}
	}
	
}
