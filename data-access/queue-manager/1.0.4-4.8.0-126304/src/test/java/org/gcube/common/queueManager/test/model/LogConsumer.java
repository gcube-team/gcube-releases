package org.gcube.common.queueManager.test.model;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.JMSException;

import org.gcube.common.queueManager.test.TestCommon;
import org.gcube.data.access.queueManager.QueueItemHandler;
import org.gcube.data.access.queueManager.QueueType;
import org.gcube.data.access.queueManager.impl.QueueConsumer;
import org.gcube.data.access.queueManager.impl.QueueConsumerFactory;
import org.gcube.data.access.queueManager.model.LogItem;

public class LogConsumer implements QueueItemHandler<LogItem> {

	private static AtomicInteger consumedCallBacks=new AtomicInteger(0);
	
	public void handleQueueItem(LogItem item) throws Exception {
		System.out.println("Received Log item "+item+", amount = "+consumedCallBacks.incrementAndGet());
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){
			
		}
	}
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	
	public static void main(String[] args) throws JMSException{
		System.out.println("Starting Loggers ... ");
		TestCommon.consumerFactory=QueueConsumerFactory.get(TestCommon.config);
		ArrayList<QueueConsumer> consumers=new ArrayList<QueueConsumer>();
		for(String topic:TestCommon.topics)
			for(int i=0;i<TestCommon.consumerAmount;i++)
				consumers.add(TestCommon.consumerFactory.register(topic, QueueType.LOG, new LogConsumer()));
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
