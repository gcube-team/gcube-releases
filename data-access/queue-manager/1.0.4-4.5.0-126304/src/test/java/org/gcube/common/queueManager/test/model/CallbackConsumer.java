package org.gcube.common.queueManager.test.model;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.JMSException;

import org.gcube.common.queueManager.test.TestCommon;
import org.gcube.data.access.queueManager.QueueItemHandler;
import org.gcube.data.access.queueManager.QueueType;
import org.gcube.data.access.queueManager.impl.QueueConsumer;
import org.gcube.data.access.queueManager.impl.QueueConsumerFactory;
import org.gcube.data.access.queueManager.model.CallBackItem;

public class CallbackConsumer implements QueueItemHandler<CallBackItem> {

	private static AtomicInteger consumedCallBacks=new AtomicInteger(0);
	
	
	public void handleQueueItem(CallBackItem item) throws Exception {
		System.out.println(this.hashCode()+" Received Callback "+item+", amount = "+consumedCallBacks.incrementAndGet());
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){
			
		}
	}
	public void close() {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args) throws JMSException{
		System.out.println("Starting Callback receivers ... ");
		TestCommon.consumerFactory=QueueConsumerFactory.get(TestCommon.config);
		ArrayList<QueueConsumer> consumers=new ArrayList<QueueConsumer>();
		for(String topic:TestCommon.topics)
			for(int i=0;i<TestCommon.consumerAmount;i++)
				consumers.add(TestCommon.consumerFactory.register(topic, QueueType.CALLBACK, new CallbackConsumer()));
		
//		consumers.add(TestCommon.consumerFactory.register(TestCommon.topics[0], QueueType.CALLBACK, new CallbackConsumer()));
		
		while(consumedCallBacks.get()<5){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("Closing Consumers..");
		for(QueueConsumer c:consumers){
			try{
				c.close();
			}catch(JMSException e){
				e.printStackTrace();
			}
		}
	}
	@Override
	public void onException(JMSException arg0) {
		// TODO Auto-generated method stub
		
	}
}
