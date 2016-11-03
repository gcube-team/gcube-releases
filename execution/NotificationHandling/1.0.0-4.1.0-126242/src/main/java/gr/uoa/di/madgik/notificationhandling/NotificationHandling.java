package gr.uoa.di.madgik.notificationhandling;

import java.util.HashMap;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.notifications.INotificationHandlingProvider;
import gr.uoa.di.madgik.environment.notifications.NotificationHandlingProvider;
import gr.uoa.di.madgik.environment.notifications.NotificationMessageListenerI;
import gr.uoa.di.madgik.environment.notifications.SubscriberToTopic;

public class NotificationHandling {
	
	private static INotificationHandlingProvider Provider = null;
	private static Object lockMe = new Object();
	
	public static void Init(String ProviderName, EnvHintCollection Hints) throws EnvironmentValidationException {
		synchronized (NotificationHandling.lockMe) {
			if (NotificationHandling.Provider == null)
				NotificationHandling.Provider = NotificationHandlingProvider.Init(ProviderName, Hints);
		}
	}
	
	public static String RegisterNotificationTopic(String topicName,
			String producerId, EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		if (NotificationHandling.Provider != null)
			return NotificationHandling.Provider.registerNotificationTopic(topicName, producerId, Hints);
		return null;
	}
	
	public static SubscriberToTopic RegisterToNotificationTopic(String topicId, String listenerId,
			String subscriptionName, String selector, SubscriberToTopic subscriberToTopic, NotificationMessageListenerI messageListener,
			EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		if (NotificationHandling.Provider != null)
			return NotificationHandling.Provider.registerToNotificationTopic(topicId, listenerId, subscriptionName, selector, subscriberToTopic, messageListener, Hints);
		return null;
	}
	
	public static void UnregisterNotificationTopic(String topicId, 
			EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		if (NotificationHandling.Provider != null)
			NotificationHandling.Provider.unregisterNotificationTopic(topicId, Hints);
	}
	
	public static void UnregisterFromNotificationTopic(String topicId, 
			String listenerId, SubscriberToTopic subscriberToTopic, EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		if (NotificationHandling.Provider != null)
			NotificationHandling.Provider.unregisterFromNotificationTopic(topicId, listenerId, subscriberToTopic, Hints);
	}
	
	public static void SendNotificationToTopic(String topicId, String textMessage,
			HashMap<String, String> propertiesNameValueMap, EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		if (NotificationHandling.Provider != null)
			NotificationHandling.Provider.sendNotificationToTopic(topicId, textMessage, propertiesNameValueMap, Hints);
	}

}
