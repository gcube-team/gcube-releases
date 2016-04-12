package org.gcube.common.queueManager.test;

import javax.jms.JMSException;

import org.gcube.common.queueManager.test.model.CallbackConsumer;
import org.gcube.common.queueManager.test.model.LogConsumer;
import org.gcube.data.access.queueManager.QueueType;
import org.gcube.data.access.queueManager.impl.QueueConsumerFactory;
import org.gcube.data.access.queueManager.impl.QueueProducerFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class CallBacksAndLogs {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {	
		TestCommon.consumerFactory=QueueConsumerFactory.get(TestCommon.config);
		TestCommon.producerFactory=QueueProducerFactory.get(TestCommon.config);
	}
	
	@Test
	public void startReceivers(){
		for(String topic:TestCommon.topics){
			try{
				System.out.println("Subscribing to "+topic);
				for(int i=0;i<TestCommon.consumerAmount;i++){
					TestCommon.consumerFactory.register(topic, QueueType.CALLBACK, new CallbackConsumer());
					TestCommon.consumerFactory.register(topic, QueueType.LOG, new LogConsumer());					
				}
			}catch(JMSException e){
				e.printStackTrace();
			}
		}
		System.out.println("All subscribers generated");
		for(int i=0;i<10;i++)
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
	}
}
