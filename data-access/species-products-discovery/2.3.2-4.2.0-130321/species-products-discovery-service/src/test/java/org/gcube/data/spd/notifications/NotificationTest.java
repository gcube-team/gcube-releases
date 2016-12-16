package org.gcube.data.spd.notifications;

import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.notifier.ISNotifier;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.GCUBENotificationTopic;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationTest {

	private  static Logger logger = LoggerFactory.getLogger(NotificationTest.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		ISNotifier notifier = GHNContext.getImplementation(ISNotifier.class);
		//creating the topic object with the QName for GenericResource
		GCUBENotificationTopic topic= new GCUBENotificationTopic(new QName("http://gcube-system.org/namespaces/data/speciesproductsdiscovery", "UpdateTopic"));
		logger.trace("topic registered");
		//settign a precondition to get only the notification for Generic resource with SecondaryType ACTIVATIONRECORD_TYPE, name ACTIVATIONRECORD_NAME and the operationType create
		//(the XPath MUST return a boolean)
		//notificationTopic.setPrecondition("//profile[contains(.,'<SecondaryType>"+Constants.ACTIVATIONRECORD_TYPE+"</SecondaryType>') and contains(.,'<Name>"+Constants.ACTIVATIONRECORD_NAME+</Name>')] and //operationType/text()='create'");		
		//this parameter is set to true to receive past notification
		topic.setUseRenotifier(false);
		List<GCUBENotificationTopic> topics = Collections.singletonList(topic);
		notifier.registerToISNotification(new TestConsumer(), topics, new GCUBESecurityManagerImpl() {
			@Override
			public boolean isSecurityEnabled() {
				return false;
			}
		},  GCUBEScope.getScope("/gcube/devsec"));
		System.in.read();
		notifier.unregisterFromISNotification( new GCUBESecurityManagerImpl(){
		 
					@Override
					public boolean isSecurityEnabled() {
						// TODO Auto-generated method stub
						return false;
					}
				}, topics, GCUBEScope.getScope("/gcube/devsec"));

	}

}
