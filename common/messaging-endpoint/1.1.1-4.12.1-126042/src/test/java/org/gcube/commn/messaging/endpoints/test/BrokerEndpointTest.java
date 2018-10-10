package org.gcube.commn.messaging.endpoints.test;

import junit.framework.Assert;

import org.gcube.common.messaging.endpoints.BrokerEndpoints;
import org.gcube.common.messaging.endpoints.BrokerNotConfiguredInScopeException;
import org.gcube.common.messaging.endpoints.ScheduledRetriever;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author andrea
 *
 */
public class BrokerEndpointTest {
	
	Logger logger = LoggerFactory.getLogger(BrokerEndpointTest.class);
	
	@Test
	public void testEndpoints(){
		
		ScopeProvider.instance.set("/gcube");
		try {
			ScheduledRetriever retriever = BrokerEndpoints.getRetriever(10, 10);
			Assert.assertNotNull(retriever.getEndpoints());
			
			for (String address : retriever.getEndpoints())
				logger.debug("Address = "+address);
			logger.debug("Failover="+retriever.getFailoverEndpoint());
			Assert.assertTrue(true);
			
		} catch (BrokerNotConfiguredInScopeException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
			
		}
	}
	

}
