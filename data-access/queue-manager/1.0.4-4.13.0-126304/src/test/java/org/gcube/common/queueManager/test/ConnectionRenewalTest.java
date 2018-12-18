package org.gcube.common.queueManager.test;

import javax.jms.JMSException;

import org.gcube.data.access.queueManager.QueueType;
import org.gcube.data.access.queueManager.impl.QueueConsumerFactory;
import org.gcube.data.access.queueManager.impl.QueueProducer;
import org.gcube.data.access.queueManager.impl.QueueProducerFactory;
import org.gcube.data.access.queueManager.model.RequestItem;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConnectionRenewalTest {


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {	
		TestCommon.consumerFactory=QueueConsumerFactory.get(TestCommon.config);
		TestCommon.producerFactory=QueueProducerFactory.get(TestCommon.config);
	}
	
	@Test
	public void testConnection() throws JMSException{
		QueueProducer<RequestItem> producer=TestCommon.producerFactory.getSubmitter(TestCommon.topics[0], QueueType.REQUEST);
		long startTime=System.currentTimeMillis();
		for(long waitTime=1000; waitTime<Long.MAX_VALUE; waitTime=waitTime*2){
			try{
				Thread.sleep(waitTime);
			}catch(InterruptedException e){
			}
			if(producer.isActive()){
				long elapsedTime=(System.currentTimeMillis()-startTime);
				System.out.println("Still Active after "+elapsedTime);
				if(elapsedTime>120000) {
					System.out.println("TEST OK, closing");
					break;
				}
			}else{
				System.out.println("INACTIVE after "+(System.currentTimeMillis()-startTime));
				Assert.fail();
				break;
			}
		}
		producer.close();
	}
	
}
