package gr.uoa.di.madgik.environment.notifications;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;

import java.util.HashMap;

public class NotificationHandlingProvider implements INotificationHandlingProvider {
	
	private static INotificationHandlingProvider StaticProvider = null;
	private static final Object lockMe = new Object();
	
	private INotificationHandlingProvider Provider = null;
	private EnvHintCollection InitHints = null;
	
	public static INotificationHandlingProvider Init(String ProviderName, EnvHintCollection Hints) throws EnvironmentValidationException {
		try {
			synchronized(NotificationHandlingProvider.lockMe) {
				if (NotificationHandlingProvider.StaticProvider == null) {
					if(ProviderName.equals(NotificationHandlingProvider.class.getName())) throw new EnvironmentValidationException("Class "+NotificationHandlingProvider.class.getName()+" cannot be defined as provider");
					Class<?> c=Class.forName(ProviderName);
					Object o=c.newInstance();
					if(!(o instanceof INotificationHandlingProvider)) throw new EnvironmentValidationException("");
					NotificationHandlingProvider prov=new NotificationHandlingProvider();
					prov.Provider=(INotificationHandlingProvider)o;
					prov.InitHints=Hints;
					NotificationHandlingProvider.StaticProvider=prov;
				}
			}
			return NotificationHandlingProvider.StaticProvider;
		}catch(Exception ex)
		{
			throw new EnvironmentValidationException("Could not initialize Notification System Provider", ex);
		}
	}
	
	public static boolean IsInit()
	{
		synchronized(NotificationHandlingProvider.lockMe)
		{
			return (NotificationHandlingProvider.StaticProvider!=null);
		}		
	}
	
	private EnvHintCollection MergeHints(EnvHintCollection Hints)
	{
		if(this.InitHints==null && Hints==null) return new EnvHintCollection();
		if(this.InitHints==null) return Hints;
		else if(Hints==null) return this.InitHints;
		else return this.InitHints.Merge(Hints);
	}

	@Override
	public String registerNotificationTopic(String topicName,
			String producerId, EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		return this.Provider.registerNotificationTopic(topicName, producerId, MergeHints(Hints));
	}

	@Override
	public SubscriberToTopic registerToNotificationTopic(String topicId, String listenerId,
			String subscriptionName, String selector, SubscriberToTopic subscriberToTopic, NotificationMessageListenerI messageListener,
			EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		return this.Provider.registerToNotificationTopic(topicId, listenerId, subscriptionName, selector, subscriberToTopic, messageListener, MergeHints(Hints));
	}

	@Override
	public void unregisterNotificationTopic(String topicId,
			EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		this.Provider.unregisterNotificationTopic(topicId, MergeHints(Hints));
	}

	@Override
	public void unregisterFromNotificationTopic(String topicId,
			String listenerId, SubscriberToTopic subscriberToTopic, EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		this.Provider.unregisterFromNotificationTopic(topicId, listenerId, subscriberToTopic, MergeHints(Hints));
	}

	@Override
	public void sendNotificationToTopic(String topicId, String textMessage,
			HashMap<String, String> propertiesNameValueMap, EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		this.Provider.sendNotificationToTopic(topicId, textMessage, propertiesNameValueMap, MergeHints(Hints));
	}

}
