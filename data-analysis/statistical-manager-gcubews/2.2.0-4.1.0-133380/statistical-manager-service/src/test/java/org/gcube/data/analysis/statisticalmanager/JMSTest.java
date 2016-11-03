package org.gcube.data.analysis.statisticalmanager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;

import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.common.core.scope.GCUBEScopeNotSupportedException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.access.queueManager.model.RequestItem;
import org.gcube.data.analysis.statisticalmanager.exception.StatisticalManagerException;
import org.gcube.data.analysis.statisticalmanager.experimentspace.ServiceQueueConsumer;
import org.gcube.data.analysis.statisticalmanager.experimentspace.ServiceQueueConsumer.ConsumerReport;
import org.gcube.data.analysis.statisticalmanager.persistence.ServiceQueueManager;
import org.gcube.data.analysis.statisticalmanager.stubs.SMComputationRequest;
import org.junit.Before;
import org.junit.Test;

public class JMSTest {

	private static int numberOfItems=10;
	
	
//	@Before
//	public void init() throws MalformedScopeExpressionException, JMSException, GCUBEScopeNotSupportedException, StatisticalManagerException{
//		ScopeProvider.instance.set("/gcube");
//		for(int i=0;i<numberOfItems;i++){
//			SMComputationRequest requestComputation=new SMComputationRequest(null, "", "request comp_"+i, "testUser");
//			
//			Map<String, Serializable> parameters = new HashMap<String, Serializable>();
//			parameters.put(Configuration.getProperty(Configuration.JMS_MESSAGE_REQUEST),requestComputation);
//			parameters.put(Configuration.getProperty(Configuration.JMS_MESSAGE_COMPUTATION_ID),"false comp");
//			parameters.put(Configuration.getProperty(Configuration.JMS_MESSAGE_SCOPE),ScopeProvider.instance.get());
//			ServiceQueueManager.sendItem(new RequestItem("CallScript", null, parameters));
//		}
//		System.out.println("Messages sent");
//	}
//
//	@Test
//	public void receive(){
//		System.out.println("Monitoring served requests");
//		ConsumerReport report=null;
//		do{
//			report=ServiceQueueConsumer.getReport();
//			System.out.println(report);
//			try{
//				Thread.sleep(1000);
//			}catch(InterruptedException e){
//				//
//			}
//		}while(report.getServedCount()!=numberOfItems);
//		
//	}
}
