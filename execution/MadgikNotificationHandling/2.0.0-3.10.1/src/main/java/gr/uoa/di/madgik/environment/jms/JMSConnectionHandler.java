package gr.uoa.di.madgik.environment.jms;


import gr.uoa.di.madgik.environment.notifications.NotificationMessageListenerI;
import gr.uoa.di.madgik.environment.notifications.SubscriberToTopic;
import gr.uoa.di.madgik.environment.notifications.exceptions.FailedToCommunicateWithNotificationService;
import gr.uoa.di.madgik.environment.notifications.exceptions.FailedToRegisterToTopicException;
import gr.uoa.di.madgik.environment.notifications.exceptions.FailedToUnregisterFromTopicException;
import gr.uoa.di.madgik.environment.notifications.exceptions.TopicCreationException;
import gr.uoa.di.madgik.environment.notifications.model.GeneralMessageListener;
import gr.uoa.di.madgik.environment.notifications.model.TopicData;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.TopicViewMBean;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMSConnectionHandler {

	static JMSConnectionHandler jmsManager = null;
	ConnectionFactory connectionFactory = null;
	//	Connection connection = null;
	static Logger logger = LoggerFactory.getLogger(JMSConnectionHandler.class.getName());
	

	protected JMSConnectionHandler(String jmsLocation) {
		Context jndiContext = null;

		/*
		 * Create a JNDI API InitialContext object
		 */
		//-- Configure JNDI with Map
		jndiContext = configureJNDIwithMap(jmsLocation);
		try {
			connectionFactory = (ConnectionFactory)jndiContext.lookup("ConnectionFactory");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			logger.warn("Look up failed", e);
		} 
	}

	public static JMSConnectionHandler getInstance(String jmsLocation) {
		if (jmsManager == null)
			jmsManager = new JMSConnectionHandler(jmsLocation);
		return jmsManager;
	}

	public Topic createTopic(TopicData topicData) throws TopicCreationException {
		Connection connection = null;
		try {
			connection = connectionFactory.createConnection();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic myTopic = session.createTopic(topicData.createTopicId());
			return myTopic;
		} catch (JMSException e) {
			throw new TopicCreationException(e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					logger.warn("Could not close connection", e);
				}
			}
		}
	}

	public void sendMessageForTopic(String topicName, String textMessage, HashMap<String, String> messageProperties) throws JMSException {
		Connection connection = connectionFactory.createConnection();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Topic topic = session.createTopic(topicName);
		MessageProducer producer = session.createProducer(topic);
		TextMessage message = session.createTextMessage();
		message.setText(textMessage);
		for (String key:messageProperties.keySet()) {
			message.setStringProperty(key, messageProperties.get(key));
		}
		producer.send(message);
		//finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					logger.warn("connection close failed",e);
				}
			}
		//}
	}

	public SubscriberToTopic listenToMessagesOfTopic(TopicData topicData, String clientId, String subscriptionName, String selector, SubscriberToTopic subToTopic, NotificationMessageListenerI listener) throws FailedToRegisterToTopicException {
		if (subToTopic == null) {
			Connection connection = null;
			try {
				connection = connectionFactory.createConnection();
				connection.setClientID(clientId);
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				String mySelector = "operationName = 'add'";		//-- example
				mySelector = selector;
				TopicSubscriber topicSubscriber;
				ActiveMQTopic topic = new ActiveMQTopic();
				topic.createDestination(topicData.createTopicId());
				topic.setPhysicalName(topicData.createTopicId());
				if (!mySelector.equals(""))
					topicSubscriber = session.createDurableSubscriber(topic, subscriptionName, mySelector, false);
				else
					topicSubscriber = session.createDurableSubscriber(topic, subscriptionName);
				
				
				/* Adjust message listener to jms message listener */
				/********************************************************************************/
				GeneralMessageListener generalListener = new GeneralMessageListener();
				generalListener.setNotificationMessageListener(listener);
				/********************************************************************************/
				
				topicSubscriber.setMessageListener(generalListener);
				
				SubscriberToTopic subscriber = new SubscriberToTopic();
				subscriber.setTopicSubscriber(topicSubscriber);
	
				connection.start();
				subscriber.setConnection(connection);
				return subscriber;
			} catch (JMSException e) {
				throw new FailedToRegisterToTopicException(e);
			} 
		} else {
			Connection connection = (Connection)subToTopic.getConnection();
			try {
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				String mySelector = "operationName = 'add'";		//-- example
				mySelector = selector;
				TopicSubscriber topicSubscriber;
				ActiveMQTopic topic = new ActiveMQTopic();
				topic.createDestination(topicData.createTopicId());
				topic.setPhysicalName(topicData.createTopicId());
				if (!mySelector.equals(""))
					topicSubscriber = session.createDurableSubscriber(topic, subscriptionName, mySelector, false);
				else
					topicSubscriber = session.createDurableSubscriber(topic, subscriptionName);
				
				
				/* Adjust message listener to jms message listener */
				/********************************************************************************/
				GeneralMessageListener generalListener = new GeneralMessageListener();
				generalListener.setNotificationMessageListener(listener);
				/********************************************************************************/
				
				topicSubscriber.setMessageListener(generalListener);
				
				SubscriberToTopic subscriber = new SubscriberToTopic();
				subscriber.setTopicSubscriber(topicSubscriber);
	
				connection.start();
				subscriber.setConnection(connection);
				return subscriber;
			} catch (JMSException e) {
				throw new FailedToRegisterToTopicException(e);
			} 
			}
			
		}
//		finally {
//			if (connection != null) {
//				try {
//					connection.close();
//				} catch (JMSException e) {
//					e.printStackTrace();
//				}
//			}
//		}


	public void unsubscribeFromTopic(TopicData topicData, String clientId, SubscriberToTopic subscriberToTopic) throws FailedToUnregisterFromTopicException {
		Connection connection = null;
		TopicSubscriber topicSubscriber = null;
		try {
			connection = (Connection) subscriberToTopic.getConnection();
			//connection.setClientID(clientId);
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			topicSubscriber = (TopicSubscriber)subscriberToTopic.getTopicSubscriber();
			
			topicSubscriber.close();
			
			session.unsubscribe(clientId);
			
		} catch (JMSException e) {
			throw new FailedToUnregisterFromTopicException(e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					logger.warn("connection close failed",e);
				}
			}
		}
	}
	
	
	public void sendNotificationToTopic(TopicData topicData, String textMessage, HashMap<String, String> propertiesNameValueMap) {
		Session session = null;
		MessageProducer producer = null;
		Connection connection = null;

		// Make also the topic creation
		
		try {
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic topic = session.createTopic(topicData.createTopicId());
		//	Topic topic = new ActiveMQTopic(topicData.createTopicId());
			producer = session.createProducer(topic);
			TextMessage message = session.createTextMessage();
			message.setText(textMessage);
			for (String propertyName:propertiesNameValueMap.keySet()) {
				message.setStringProperty(propertyName, propertiesNameValueMap.get(propertyName));
			}
			producer.send(message);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			logger.warn("send notification to topic failed",e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					logger.warn("connection close failed",e);
				}
			}
		}
		

	}
	
	public void sendNotificationToTopic(Topic topic, String textMessage, HashMap<String, String> propertiesNameValueMap) {
		Session session = null;
		MessageProducer producer = null;
		Connection connection = null;

		try {
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			producer = session.createProducer(topic);
			TextMessage message = session.createTextMessage();
			message.setText(textMessage);
			for (String propertyName:propertiesNameValueMap.keySet()) {
				message.setStringProperty(propertyName, propertiesNameValueMap.get(propertyName));
			}
			producer.send(message);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			logger.warn("send notification to topic failed",e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					logger.warn("connection close failed",e);
				}
			}
		}
		

	}
	

	public boolean[] isTopicRegistered(List<TopicData> topics) throws FailedToCommunicateWithNotificationService {
		boolean[] toReturn = new boolean[topics.size()];
		try {
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://dl09.di.uoa.gr:1099/jmxrmi");
			JMXConnector jmxc = JMXConnectorFactory.connect(url);
			MBeanServerConnection conn = jmxc.getMBeanServerConnection();
			ObjectName activeMQ = new ObjectName("org.apache.activemq:BrokerName=localhost,Type=Broker");
			activeMQ = new ObjectName("org.apache.activemq:BrokerName=localhost,Type=Broker");
			BrokerViewMBean mbean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(conn, activeMQ,BrokerViewMBean.class, true);

			for (int i = 0; i < topics.size(); i++) {
				String topicName = topics.get(i).getTopicName();
				boolean found = false;
				for (ObjectName name : mbean.getTopics()) {
					TopicViewMBean topicMbean = (TopicViewMBean)
							MBeanServerInvocationHandler.newProxyInstance(conn, name, TopicViewMBean.class, true);

					if (topicMbean.getName().equals(topicName)) {
						logger.debug("Found the topic!!... Deleting it...");
						found = true;
						break;
					}
				}
				toReturn[i] = found; 
			}
			return toReturn;
		} catch (Exception e) {
			throw new FailedToCommunicateWithNotificationService(e);
		}
	}

	public void deleteTopic(TopicData topicData) throws FailedToCommunicateWithNotificationService {
		try {
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://dl09.di.uoa.gr:1099/jmxrmi");
			JMXConnector jmxc = JMXConnectorFactory.connect(url);
			MBeanServerConnection conn = jmxc.getMBeanServerConnection();

			//			String operationName = "addTopic";
			//			String parameter = "MyNewTopic";
			ObjectName activeMQ = new ObjectName("org.apache.activemq:BrokerName=localhost,Type=Broker");
			//			Object[] params = {parameter};
			String[] sig = {"java.lang.String"};
			//			conn.invoke(activeMQ, operationName, params, sig);

			activeMQ = new ObjectName("org.apache.activemq:BrokerName=localhost,Type=Broker");
			BrokerViewMBean mbean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(conn, activeMQ,BrokerViewMBean.class, true);

			mbean.removeTopic(topicData.createTopicId());
			//			for (ObjectName name : mbean.getTopics()) {
			//			    TopicViewMBean topicMbean = (TopicViewMBean)
			//			           MBeanServerInvocationHandler.newProxyInstance(conn, name, TopicViewMBean.class, true);
			//
			//			    if (topicMbean.getName().equals(topicName)) {
			//			        logger.debug("Found the topic!!... Deleting it...");
			//			        mbean.removeTopic("MyNewTopic");
			//			    }
			//			} 
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new FailedToCommunicateWithNotificationService(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new FailedToCommunicateWithNotificationService(e);
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new FailedToCommunicateWithNotificationService(e);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new FailedToCommunicateWithNotificationService(e);
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new FailedToCommunicateWithNotificationService(e);
		} catch (MBeanException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new FailedToCommunicateWithNotificationService(e);
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new FailedToCommunicateWithNotificationService(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new FailedToCommunicateWithNotificationService(e);
		}
	}

	private BrokerViewMBean getActiveMQBroker() throws IOException, MalformedObjectNameException, NullPointerException {
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://dl09.di.uoa.gr:1099/jmxrmi");
		JMXConnector jmxc = JMXConnectorFactory.connect(url);
		MBeanServerConnection conn = jmxc.getMBeanServerConnection();
		ObjectName activeMQ = new ObjectName("org.apache.activemq:BrokerName=localhost,Type=Broker");
		BrokerViewMBean mbean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(conn, activeMQ,BrokerViewMBean.class, true);
		return mbean;
	}

	private static Context configureJNDIwithMap(String jmsLocation) {
		Properties props = new Properties();
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		
		props.setProperty(Context.PROVIDER_URL, /*"tcp://dl09.di.uoa.gr:61616"*/ jmsLocation);
		//		props.setProperty("topic.topicDestination", "topicName");
		try {
			javax.naming.Context ctx = new InitialContext(props);
			return ctx;
		} catch (NamingException e) {
			logger.warn("Initial Context failed",e);
		}
		return null;
	}

}
