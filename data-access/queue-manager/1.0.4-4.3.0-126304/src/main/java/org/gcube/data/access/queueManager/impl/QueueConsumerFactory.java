package org.gcube.data.access.queueManager.impl;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.gcube.data.access.queueManager.ConsumerFactory;
import org.gcube.data.access.queueManager.FactoryConfiguration;
import org.gcube.data.access.queueManager.QueueItemHandler;
import org.gcube.data.access.queueManager.QueueType;
import org.gcube.data.access.queueManager.utils.Common;

public class QueueConsumerFactory implements ConsumerFactory{

	private static QueueConsumerFactory instance = null;
	
	
	
	
	public static synchronized QueueConsumerFactory get(FactoryConfiguration configuration)throws JMSException{
		if(instance==null) instance = new QueueConsumerFactory(configuration);
		return instance;
	}
	
	
	
	private ConcurrentHashMap<QueueType, ActiveMQConnectionFactory> factories=new ConcurrentHashMap<QueueType, ActiveMQConnectionFactory>();
	private FactoryConfiguration config=null;
	
	private QueueConsumerFactory(FactoryConfiguration configuration) throws JMSException{
		config=configuration;		
	}
	
	private synchronized ActiveMQConnectionFactory getFactory(QueueType type)throws JMSException{
		if(!factories.containsKey(type)){
			ActiveMQConnectionFactory toSet=null;
			switch(type){
				case LOG : toSet=new ActiveMQConnectionFactory(config.getUser(), config.getPassword(), config.getBrokerEndpoint());
							break;
				default : toSet=new ActiveMQConnectionFactory(config.getUser(), config.getPassword(), config.getBrokerEndpoint());
							break;
			}
			factories.put(type, toSet);
		}
		return factories.get(type);
	}
	
	/**
	 * Creates a receiver (or durable subscriber in case of topic).
	 * 
	 * If queueType is LOG then TOPIC is created.
	 * 
	 * 
	 */
	public QueueConsumer register(String topic,QueueType type,QueueItemHandler callback)
			throws JMSException{
		ActiveMQConnectionFactory factory=getFactory(type);
		//Determines session type
		Session session=null;
		Connection connection=null;
		switch(type){
			case LOG : 	connection=factory.createTopicConnection();
						connection.setClientID(callback.toString());
						connection.start();
						session=((TopicConnection)connection).createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
						break;
			default : 	connection=factory.createQueueConnection();
						
						connection.setClientID(callback.toString());
						connection.start();
						session=((QueueConnection)connection).createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
		}
		
		//Redelivery Policy AMQ Speciefic
		RedeliveryPolicy policy=((ActiveMQConnection)connection).getRedeliveryPolicy();
		policy.setInitialRedeliveryDelay(config.getInitialRedeliveryDelay());
		policy.setMaximumRedeliveries(config.getMaximumRedeliveries());
		policy.setUseExponentialBackOff(config.isUseExponentialRedelivery());
		
		//Create Specific Topic
		topic=Common.formTopic(config.getServiceClass(), config.getServiceName(), type, topic);
		
		QueueConsumer toReturn=new QueueConsumer(callback,session,connection);
		connection.setExceptionListener(callback);
		
		switch(type){
		case LOG : 	
					TopicSubscriber subscriber=((TopicSession)session).createDurableSubscriber(session.createTopic(topic),connection.getClientID());
					subscriber.setMessageListener(toReturn);
					break;
		default : 	
					QueueReceiver consumer=((QueueSession)session).createReceiver(session.createQueue(topic));
					consumer.setMessageListener(toReturn);
					
					
					break;
			}
		
		return toReturn;
	}
	
	
	
	public MultiSyncConsumer getMultiSyncConsumer(QueueType type)throws JMSException,Exception{
		if(type.equals(QueueType.LOG)) throw new Exception("QueueType not allowed for synchronous consumption");
		ActiveMQConnectionFactory factory=getFactory(type);
		return new MultiSyncConsumer(factory.createQueueConnection(), config.getServiceClass(), config.getServiceName(), type);
	}
	
	
	public void close() {
//		for(ActiveMQConnectionFactory factory:factories.values()){
//			factory.
//		}
	}

	
	
}
