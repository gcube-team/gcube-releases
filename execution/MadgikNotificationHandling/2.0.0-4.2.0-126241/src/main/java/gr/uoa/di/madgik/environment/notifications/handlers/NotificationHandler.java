package gr.uoa.di.madgik.environment.notifications.handlers;

import gr.uoa.di.madgik.environment.notifications.NotificationMessageListenerI;
import gr.uoa.di.madgik.environment.notifications.SubscriberToTopic;

import gr.uoa.di.madgik.environment.jms.JMSConnectionHandler;
import gr.uoa.di.madgik.environment.notifications.exceptions.FailedToCommunicateWithNotificationService;
import gr.uoa.di.madgik.environment.notifications.exceptions.FailedToRegisterToTopicException;
import gr.uoa.di.madgik.environment.notifications.exceptions.FailedToUnregisterFromTopicException;
import gr.uoa.di.madgik.environment.notifications.exceptions.TopicCreationException;
import gr.uoa.di.madgik.environment.notifications.model.TopicData;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLClassLoader;

public class NotificationHandler {
	
	//-- Make it singleton to give the right impression (although the used manager is also singleton)
	
	static NotificationHandler nh;
	
	String jmsLocation;
	
	public static NotificationHandler getInstance() {
		if (nh == null)
			nh = new NotificationHandler();
		return nh;
	}
	
	public void configureHandler(String jmsLocation) {
		nh.jmsLocation = jmsLocation;
	}
	
	public boolean[] isTopicRegistered(List<TopicData> topics) throws FailedToCommunicateWithNotificationService {
		return JMSConnectionHandler.getInstance(nh.jmsLocation).isTopicRegistered(topics);
	}
	
	public String registerNotificationTopics(TopicData topic) throws TopicCreationException {
		//Get the System Classloader
        ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();

        //Get the URLs
//        URL[] urls = ((URLClassLoader)sysClassLoader).getURLs();
//
//        for(int i=0; i< urls.length; i++)
//        {
//            System.out.println(urls[i].getFile());
//        }       
        Logger logger = LoggerFactory.getLogger(NotificationHandler.class.getName());
        logger.info("NotificationHandler: Registering Topic! ProducerId: " + topic.getProducerId() + "- TopicName: " + topic.getTopicName());
		Topic theTopic = JMSConnectionHandler.getInstance(nh.jmsLocation).createTopic(topic);
		try {
			String topicId = theTopic.getTopicName();
			logger.info("NotificationHandler: TopicId of created topic: " + topicId);
			return topicId;
		} catch (JMSException e) {
			throw new TopicCreationException(e);
		}
	}
	
	public SubscriberToTopic registerToNotificationTopic(TopicData topic, String listenerId, String subscriptionName, String selector, SubscriberToTopic subscriberToTopic, NotificationMessageListenerI listener) throws FailedToRegisterToTopicException {
		//MessageListener messageListener = (MessageListener) listener;
		Logger logger = LoggerFactory.getLogger(NotificationHandler.class.getName());
		logger.info("NotificationHandler: Registering to NotificationTopic - ProducerId" + topic.getProducerId() + " - topicName: " + topic.getTopicName() + " listenerId: listenerId - subscriptionName: " + subscriptionName);
		return JMSConnectionHandler.getInstance(nh.jmsLocation).listenToMessagesOfTopic(topic, listenerId, subscriptionName, selector, subscriberToTopic, listener);
	}
	
	public void unregisterNotificationTopic(TopicData topic) throws FailedToCommunicateWithNotificationService {
		JMSConnectionHandler.getInstance(nh.jmsLocation).deleteTopic(topic);
	}
	
	public void unregisterFromNotificationTopic(TopicData topic, String listenerId, SubscriberToTopic subscriberToTopic) throws FailedToUnregisterFromTopicException {
		JMSConnectionHandler.getInstance(nh.jmsLocation).unsubscribeFromTopic(topic, listenerId, subscriberToTopic);
	}
	
	public void sendNotificationToTopic(TopicData topicData, String textMessage, HashMap<String, String> propertiesNameValueMap) {
		Logger logger = LoggerFactory.getLogger(NotificationHandler.class);
		logger.info("NotificationHandler: Sending Notification To Topic, TopicName: " + topicData.getTopicName() + " - producerId: " + topicData.getProducerId());
		JMSConnectionHandler.getInstance(nh.jmsLocation).sendNotificationToTopic(topicData, textMessage, propertiesNameValueMap);
	}
	
//	public void sendNotificationToTopic(Topic topic, String textMessage, HashMap<String, String> propertiesNameValueMap) {
//		JMSConnectionManager.getInstance().sendNotificationToTopic(topic, textMessage, propertiesNameValueMap);
//	}

}
