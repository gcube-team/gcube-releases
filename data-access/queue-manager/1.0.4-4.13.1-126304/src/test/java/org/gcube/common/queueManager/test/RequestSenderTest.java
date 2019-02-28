package org.gcube.common.queueManager.test;


import javax.jms.JMSException;

import org.gcube.common.queueManager.test.model.CallbackConsumer;
import org.gcube.common.queueManager.test.model.Executor;
import org.gcube.common.queueManager.test.model.LogConsumer;
import org.gcube.data.access.queueManager.QueueType;
import org.gcube.data.access.queueManager.impl.QueueConsumerFactory;
import org.gcube.data.access.queueManager.impl.QueueProducer;
import org.gcube.data.access.queueManager.impl.QueueProducerFactory;
import org.gcube.data.access.queueManager.model.RequestItem;
import org.junit.BeforeClass;
import org.junit.Test;


public class RequestSenderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {	
		TestCommon.consumerFactory=QueueConsumerFactory.get(TestCommon.config);
		TestCommon.producerFactory=QueueProducerFactory.get(TestCommon.config);
	}

	
	@Test
	public void sendExecutionRequests(){
		for(String topic:TestCommon.topics){
			try{
				System.out.println("Sending execution request to "+topic);
				QueueProducer<RequestItem> producer=TestCommon.producerFactory.getSubmitter(topic, QueueType.REQUEST);
				for(int execution=0;execution<TestCommon.numExecutions;execution++){
					RequestItem item=new RequestItem("someExecutionScriptToCall", null, null);
					System.out.println("Sending request N "+execution+", request :  "+item);
					producer.send(item);
				}
			}catch(JMSException e){
				e.printStackTrace();
			}
		}
	}
	
	
	
}
