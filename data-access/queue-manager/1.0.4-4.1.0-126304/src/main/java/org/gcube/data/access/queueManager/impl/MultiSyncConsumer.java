package org.gcube.data.access.queueManager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.gcube.data.access.queueManager.QueueItemHandler;
import org.gcube.data.access.queueManager.QueueType;
import org.gcube.data.access.queueManager.model.QueueItem;
import org.gcube.data.access.queueManager.utils.Common;
import org.gcube.data.access.queueManager.utils.QueueXStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiSyncConsumer{

	
	public static enum QueueSelectionPolicy{
		RANDOM,ROUND_ROBIN,MOST_UNLOAD,MOST_LOAD
	}
	
	
	public static final long DEFAULT_WAIT_FOR_MESSAGE=3000;
	
	
	private static Logger logger = LoggerFactory.getLogger(MultiSyncConsumer.class);
	
	
	
	
	private Connection connection;
	private String serviceClass;
	private String serviceName;
	private ConcurrentHashMap<String, QueueItemHandler<? extends QueueItem>> topicCallbacks=new ConcurrentHashMap<String, QueueItemHandler<? extends QueueItem>>();
	private AtomicInteger roundRobinIndex=new AtomicInteger(0);
	private long waitForMessage=DEFAULT_WAIT_FOR_MESSAGE;
	private QueueType type;
	
	MultiSyncConsumer(Connection connection,String serviceClass,String serviceName,QueueType type) throws JMSException {
		this.connection=connection;
		this.serviceClass=serviceClass;
		this.serviceName=serviceName;
		this.type=type;
		this.connection.setClientID(this.toString());
		this.connection.start();
	}
	
	
	public Set<String> getTopics(){
		return topicCallbacks.keySet();
	}
	public Collection<QueueItemHandler<? extends QueueItem>> getCallbacks(){
		return topicCallbacks.values();
	}
	
	public void attachTopic(String topic,QueueItemHandler<? extends QueueItem> callback){
		topicCallbacks.put(topic, callback);
	}
	
	public int consumeMsg(QueueSelectionPolicy policy) throws Exception{		
		String topic=selectTopic(policy);
		return consumeMsg(topic,topicCallbacks.get(topic));
	}
	
	
	public int consumeMsg(String topic) throws Exception{
		if(!topicCallbacks.contains(topic)) throw new Exception("Topic "+topic+" not defined");
		return consumeMsg(topic,topicCallbacks.get(topic));
	}
	
	
	public void removeTopic(String topic){
		topicCallbacks.remove(topic);
	}
	
	private String selectTopic(QueueSelectionPolicy policy) throws Exception{
		logger.trace("Selecting topic, policy :"+policy);
		if(topicCallbacks.size()==0) throw new Exception("No topic defined");		
			switch(policy){
				case ROUND_ROBIN : 	{	ArrayList<String> topics=new ArrayList<String>(topicCallbacks.keySet());
								   		return topics.get(roundRobinIndex.getAndIncrement() % topics.size());
								   	}
								   	
				case RANDOM : 		{	ArrayList<String> topics=new ArrayList<String>(topicCallbacks.keySet());
										return topics.get((int)Math.round(topics.size()*Math.random()));
									}
				default : throw new Exception("Policy not yet implemented");
			}
	}
	
	private <T extends QueueItem> int consumeMsg(String topic,QueueItemHandler<T> handler) throws Exception{
		Session session=null;		
		int executed=0;
		String toUseTopic=Common.formTopic(serviceClass, serviceName, type, topic);
		try{
			session=connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			MessageConsumer consumer =session.createConsumer(session.createQueue(toUseTopic));		
			logger.trace("Requesting message to queue "+toUseTopic+", max wait time : "+waitForMessage);
			System.out.println("Requesting message to queue "+toUseTopic+", max wait time : "+waitForMessage);
			Message msg=consumer.receive(waitForMessage);
			if(msg!=null){
				logger.trace("Received msg :"+msg);
				T item=null;
				try {				 
					item=(T)QueueXStream.get().fromXML(((TextMessage)msg).getText());
					handler.handleQueueItem(item);
					msg.acknowledge();
					executed++;
				} catch (JMSException e) {				
					logger.error("Unable to get item from message "+msg,e);
					throw e;
				}catch (ClassCastException e){				
					logger.error("Unexpected type of item "+item,e);
					throw e;
				} catch (Exception e) {				
					logger.error("Unable to handle item from message "+item,e);
					throw e;
				}
			}
			return executed;
		}catch(Exception e){
			logger.error("Unexpected Exception ",e);
			throw e;
		}finally{
			if(session!=null)session.close();
		}
	}

	public void setWaitForMessage(long waitForMessage) {
		this.waitForMessage = waitForMessage;
	}
	public long getWaitForMessage() {
		return waitForMessage;
	}
	
	public void close() throws JMSException{
		for(QueueItemHandler<?> handler:topicCallbacks.values()){			
			handler.close();
		}
		this.connection.stop();
		this.connection.close();
	}

}
