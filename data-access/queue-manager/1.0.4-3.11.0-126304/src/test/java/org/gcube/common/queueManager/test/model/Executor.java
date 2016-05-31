package org.gcube.common.queueManager.test.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.JMSException;

import org.gcube.common.queueManager.test.TestCommon;
import org.gcube.data.access.queueManager.QueueItemHandler;
import org.gcube.data.access.queueManager.QueueType;
import org.gcube.data.access.queueManager.impl.QueueConsumer;
import org.gcube.data.access.queueManager.impl.QueueConsumerFactory;
import org.gcube.data.access.queueManager.impl.QueueProducer;
import org.gcube.data.access.queueManager.impl.QueueProducerFactory;
import org.gcube.data.access.queueManager.model.CallBackItem;
import org.gcube.data.access.queueManager.model.LogItem;
import org.gcube.data.access.queueManager.model.RemoteExecutionStatus;
import org.gcube.data.access.queueManager.model.RequestItem;

public class Executor implements QueueItemHandler<RequestItem> {

	private static AtomicInteger executedRequests=new AtomicInteger(0);
	private static final int totalSteps=1;	
	private static final long waitTimeInfraStep=1;
	
	private static HashMap<String,AtomicInteger> msgOccurrenceCount=new HashMap<String, AtomicInteger>();
	
	private QueueProducer<CallBackItem> callbackSender;
	private QueueProducer<LogItem> logSender;

	private boolean failRequests;
	
	private String topic;

	public Executor(String topic,QueueProducer<CallBackItem> callbackSender,
			QueueProducer<LogItem> logSender,boolean failRequests) {
		super();
		this.failRequests=failRequests;
		this.topic=topic;
		this.callbackSender = callbackSender;
		this.logSender = logSender;
	}


	public void handleQueueItem(RequestItem item) throws Exception {
//		System.out.println(this.topic+" Executor "+this.hashCode()+" : Handling item "+item);
		if(!msgOccurrenceCount.containsKey(item.getId())) msgOccurrenceCount.put(item.getId(), new AtomicInteger(0));
		System.out.println(item.getId()+" occurred "+msgOccurrenceCount.get(item.getId()).incrementAndGet()+"times");
		if(failRequests){
//			System.out.println(this.topic+" Executor "+this.hashCode()+" : FAIL item "+item);
			throw new Exception("Intentionally abrupted");
		}
		LogItem log=new LogItem(item.getId(), RemoteExecutionStatus.STARTED, "Started Execution");
		logSender.send(log);
//		System.out.println(this.topic+" Executor "+this.hashCode()+" : sent "+log);
		for(int i=0;i<totalSteps;i++){
			log=new LogItem(item.getId(), RemoteExecutionStatus.STARTED, "Current phase : "+i);
			logSender.send(log);
			System.out.println(this.topic+" Executor "+this.hashCode()+" : sent "+log);
			Thread.sleep(waitTimeInfraStep);
		}
		CallBackItem callback=new CallBackItem(item.getId(), null, RemoteExecutionStatus.COMPLETED, "Execution Complete");
		callbackSender.send(callback);
//		System.out.println(this.topic+" Executor  "+this.hashCode()+" : sent "+callback+", executed amount : "+executedRequests.incrementAndGet());
		
	}

	public void close() {
		try{
		callbackSender.close();
		logSender.close();
		}catch(JMSException e){
			System.err.println("Unable to close executor");
			e.printStackTrace();
		}
	}


	
	

	public static void main(String[] args) throws Exception{
		
		//Asynchronous usage
		
		System.out.println("Starting Executor ... ");
		TestCommon.consumerFactory=QueueConsumerFactory.get(TestCommon.config);
		TestCommon.producerFactory=QueueProducerFactory.get(TestCommon.config);
		ArrayList<QueueConsumer> executors=new ArrayList<QueueConsumer>();
		for(String topic:TestCommon.topics){
			for(int i=0;i<TestCommon.consumerAmount;i++){
				Executor executor=new Executor(topic,TestCommon.producerFactory.getSubmitter(topic, QueueType.CALLBACK),
						TestCommon.producerFactory.getSubmitter(topic, QueueType.LOG),true);
				executors.add(TestCommon.consumerFactory.register(topic, QueueType.REQUEST, executor));
			}
		}
		while(executedRequests.get()<5){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("Closing Consumers..");
		for(QueueConsumer c:executors){
			try{
				c.close();
			}catch(JMSException e){
				e.printStackTrace();
			}
		}
		
		
		// Synch
//		
//		System.out.println("Creating multisync executor ... ");
//		TestCommon.consumerFactory=QueueConsumerFactory.get(TestCommon.config);
//		TestCommon.producerFactory=QueueProducerFactory.get(TestCommon.config);
//		MultiSyncConsumer consumer=TestCommon.consumerFactory.getMultiSyncConsumer(QueueType.REQUEST);
//		for(int i =0; i<TestCommon.topics.length;i++){
//			String topic=TestCommon.topics[i];
//			Executor executor=new Executor(topic,TestCommon.producerFactory.getSubmitter(topic, QueueType.CALLBACK),
//					TestCommon.producerFactory.getSubmitter(topic, QueueType.LOG),false);
//			consumer.attachTopic(topic, executor);
//		}
////		consumer.setWaitForMessage(250);
//		long toRequestMessages=TestCommon.numExecutions*TestCommon.topicsAmount;
//		System.out.println("Requesting "+toRequestMessages+"msgs..");
//		for(int i=0;i<toRequestMessages;i++){
//			try{
//				consumer.consumeMsg(QueueSelectionPolicy.ROUND_ROBIN);
//			}catch(Exception e){
//				System.err.println("Failed execution ");
//			}
//		}
//		System.out.println("Closing...");
//		
//		consumer.close();
//		
	}


	@Override
	public void onException(JMSException arg0) {
		// TODO Auto-generated method stub
		
	}

}
