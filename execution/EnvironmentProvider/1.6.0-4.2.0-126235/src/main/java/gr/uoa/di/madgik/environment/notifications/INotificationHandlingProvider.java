package gr.uoa.di.madgik.environment.notifications;

import java.util.HashMap;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;

public interface INotificationHandlingProvider {
	
	public String registerNotificationTopic(String topicName, String producerId, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
	public SubscriberToTopic registerToNotificationTopic(String topicId, String listenerId, String subscriptionName, String selector, SubscriberToTopic subscriberToTopic, NotificationMessageListenerI messageListener, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
	public void unregisterNotificationTopic(String topicId, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
	public void unregisterFromNotificationTopic(String topicId, String listenerId, SubscriberToTopic subscriberToTopic, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
	public void sendNotificationToTopic(String topicId, String textMessage, HashMap<String, String> propertiesNameValueMap, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
}
