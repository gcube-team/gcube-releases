package org.gcube.dataanalysis.executor.messagequeue;

import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;

public class QueueManager {

	public ActiveMQConnectionFactory connectionFactory;
	public Connection connection;
	public Session session;
	public Destination destination;
	boolean transacted = false;
	public String mqurl;
	private String identifier;
	
	public void createAndConnect(String user,String password, String mqurl, String queueName) throws JMSException {
		this.mqurl=mqurl;
		connect(user,password,mqurl);
		session = connection.createSession(transacted, Session.CLIENT_ACKNOWLEDGE);
		this.identifier = ""+UUID.randomUUID();
		/*
		Hashtable properties = new Hashtable();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		properties.put(Context.PROVIDER_URL, mqurl);
		InitialContext context = new InitialContext(properties);
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
		
		destination = (Destination) context.lookup(queueName);
*/
//		destination = session.createQueue(queueName+"?consumer.prefetchSize=3");
		destination = session.createQueue(queueName+"?wireFormat.maxInactivityDurationInitalDelay=3600000&requestTimeout=240000&wireFormat.maxInactivityDuration=3600000");
	}
	
	public void destroy(){
		
	}
	
	private void connect(String user,String password, String mqurl) throws JMSException{
		connectionFactory = new ActiveMQConnectionFactory(user, password, mqurl);
		connectionFactory.getPrefetchPolicy().setQueuePrefetch(1);
		
//		Properties p = new Properties();
//		p.put("persistent", "false");
//		p.put("consumer.prefetchSize", "3");
//		p.put("ms.prefetchPolicy.all", "3");
//		p.put("cms.PrefetchPolicy.queuePrefetch", "3");
		
//		connectionFactory.setProperties(p);
		connection = connectionFactory.createConnection();
		connection.setClientID(identifier);
		connection.start();
	}

	public void closeSession() throws Exception{
//	session.unsubscribe(identifier);
	
	session.close();
	
	}
}