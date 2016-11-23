package gr.uoa.di.madgik.environment.madgik.providers;

import java.util.HashMap;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.notifications.INotificationHandlingProvider;
import gr.uoa.di.madgik.environment.notifications.NotificationMessageListenerI;
import gr.uoa.di.madgik.environment.notifications.SubscriberToTopic;
import gr.uoa.di.madgik.environment.notifications.exceptions.FailedToCommunicateWithNotificationService;
import gr.uoa.di.madgik.environment.notifications.exceptions.FailedToRegisterToTopicException;
import gr.uoa.di.madgik.environment.notifications.exceptions.FailedToUnregisterFromTopicException;
import gr.uoa.di.madgik.environment.notifications.exceptions.IllegalTopicIdGivenException;
import gr.uoa.di.madgik.environment.notifications.exceptions.TopicCreationException;
import gr.uoa.di.madgik.environment.notifications.handlers.NotificationHandler;
import gr.uoa.di.madgik.environment.notifications.model.TopicData;

public class JMSNotificationHandlingProvider implements INotificationHandlingProvider {
	public static String JMSLocationHintName = "JMSHost";

	@Override
	public String registerNotificationTopic(String topicName,
			String producerId, EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		TopicData topicData = new TopicData(topicName, producerId);
		try {
			NotificationHandler.getInstance().configureHandler(GetJMSLocation(Hints));
			String topicId = NotificationHandler.getInstance().registerNotificationTopics(topicData);
			return topicId;
		} catch (TopicCreationException e) {
			throw new EnvironmentInformationSystemException("Error while registering to topic", e);
		}
	}

	@Override
	public SubscriberToTopic registerToNotificationTopic(String topicId, String listenerId,
			String subscriptionName, String selector, SubscriberToTopic subscriberToTopic, NotificationMessageListenerI messageListener,
			EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		String topicName;
		String producerId;
		
		try {
			topicName = TopicData.getTopicNameFromId(topicId);
			producerId = TopicData.getProviderIdFromId(topicId);
			TopicData topicData = new TopicData(topicName, producerId);
			NotificationHandler.getInstance().configureHandler(GetJMSLocation(Hints));
			return NotificationHandler.getInstance().registerToNotificationTopic(topicData, listenerId, subscriptionName, selector, subscriberToTopic, messageListener);
		} catch (IllegalTopicIdGivenException e) {
			throw new EnvironmentInformationSystemException(e.getMessage(), e);
		} catch (FailedToRegisterToTopicException e) {
			throw new EnvironmentInformationSystemException(e.getMessage(), e);
		}
		
	}

	@Override
	public void unregisterNotificationTopic(String topicId,
			EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		String topicName;
		String producerId;
		try {
			topicName = TopicData.getTopicNameFromId(topicId);
			producerId = TopicData.getProviderIdFromId(topicId);
			TopicData topicData = new TopicData(topicName, producerId);
			NotificationHandler.getInstance().configureHandler(GetJMSLocation(Hints));
			NotificationHandler.getInstance().unregisterNotificationTopic(topicData);
		} catch (IllegalTopicIdGivenException e) {
			throw new EnvironmentInformationSystemException(e.getMessage(), e);
		} catch (FailedToCommunicateWithNotificationService e) {
			throw new EnvironmentInformationSystemException(e.getMessage(), e);
		}
		
	}

	@Override
	public void unregisterFromNotificationTopic(String topicId,
			String listenerId, SubscriberToTopic subscriberToTopic, EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		String topicName;
		String producerId;
		try {
			topicName = TopicData.getTopicNameFromId(topicId);
			producerId = TopicData.getProviderIdFromId(topicId);
			TopicData topicData = new TopicData(topicName, producerId);
			NotificationHandler.getInstance().configureHandler(GetJMSLocation(Hints));
			NotificationHandler.getInstance().unregisterFromNotificationTopic(topicData, listenerId, subscriberToTopic);
		} catch (IllegalTopicIdGivenException e) {
			throw new EnvironmentInformationSystemException(e.getMessage(), e);
		} catch (FailedToUnregisterFromTopicException e) {
			throw new EnvironmentInformationSystemException(e.getMessage(), e);
		} 
		
	}

	@Override
	public void sendNotificationToTopic(String topicId, String textMessage,
			HashMap<String, String> propertiesNameValueMap,
			EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		String topicName;
		String producerId;
		try {
			topicName = TopicData.getTopicNameFromId(topicId);
			producerId = TopicData.getProviderIdFromId(topicId);
			TopicData topicData = new TopicData(topicName, producerId);
			NotificationHandler.getInstance().configureHandler(GetJMSLocation(Hints));
			NotificationHandler.getInstance().sendNotificationToTopic(topicData, textMessage, propertiesNameValueMap);
		} catch (IllegalTopicIdGivenException e) {
			throw new EnvironmentInformationSystemException(e.getMessage(), e);
		}
		
	}
	
	private static String GetStringHint(EnvHintCollection Hints, String hintName) {
		if (Hints == null || !Hints.HintExists(hintName))
			return null;
		String payload = Hints.GetHint(hintName).Hint.Payload;
		if (payload == null || payload.trim().length() == 0)
			return null;
		return payload;
	}
	
	private static String GetJMSLocation(EnvHintCollection Hints) {
		String hostL = GetStringHint(Hints, JMSNotificationHandlingProvider.JMSLocationHintName);
		return hostL;
	}

}
