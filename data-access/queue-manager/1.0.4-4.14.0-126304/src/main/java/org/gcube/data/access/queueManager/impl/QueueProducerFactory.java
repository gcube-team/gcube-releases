package org.gcube.data.access.queueManager.impl;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.gcube.data.access.queueManager.FactoryConfiguration;
import org.gcube.data.access.queueManager.ProducerFactory;
import org.gcube.data.access.queueManager.QueueType;
import org.gcube.data.access.queueManager.utils.Common;

public class QueueProducerFactory implements ProducerFactory{

	private static QueueProducerFactory instance = null;
	
	public static synchronized QueueProducerFactory get(FactoryConfiguration configuration)throws JMSException{
		if(instance==null) instance = new QueueProducerFactory(configuration);
		return instance;
	}
	
	
	private ActiveMQConnectionFactory factory=null;
	private FactoryConfiguration config=null;
	private QueueProducerFactory(FactoryConfiguration configuration) throws JMSException{
		config=configuration;
		factory=new ActiveMQConnectionFactory(configuration.getUser(), configuration.getPassword(), configuration.getBrokerEndpoint());
	}
	
	
	
	public QueueProducer getSubmitter(String topic,QueueType type) throws JMSException {
		Connection connection=factory.createConnection();
		Session session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		topic=Common.formTopic(config.getServiceClass(), config.getServiceName(), type, topic);
		Destination dest=null;
		switch(type){
		case LOG : dest=session.createTopic(topic);
					break;
		default : dest=session.createQueue(topic);
					break;
		}
		MessageProducer publisher=session.createProducer(dest);
		return new QueueProducer (publisher,session,connection);
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}
}
