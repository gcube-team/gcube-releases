package org.gcube.common.mycontainer.dependencies;

import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.informationsystem.notifier.ISNotifier;
import org.gcube.common.core.informationsystem.notifier.ISNotifierException;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.mycontainer.MyContainerDependencies;
import org.globus.wsrf.Topic;


/**
 * An {@link ISNotifier} that dispatches invocations to an implementation set on {@link MyContainerDependencies}, typically
 * a mock created in the scope of a test.
 * 
 * @author Fabio Simeoni
 *
 */
public class ProxyISNotifier implements ISNotifier {

	private ISNotifier notifier;
	
	public ProxyISNotifier() {
		notifier = MyContainerDependencies.resolve(ISNotifier.class);
	}
	
	
	public <T extends BaseNotificationConsumer> void registerToISNotification(
			T consumer, List<GCUBENotificationTopic> notifications,
			GCUBESecurityManager manager, GCUBEScope... scope)
			throws ISNotifierException {
		notifier.registerToISNotification(consumer, notifications, manager,
				scope);
	}

	public void unregisterFromISNotification(GCUBESecurityManager manager,
			List<GCUBENotificationTopic> notifications, GCUBEScope... scope)
			throws ISNotifierException {
		notifier.unregisterFromISNotification(manager, notifications, scope);
	}

	public void registerISNotification(EndpointReferenceType producerEPR,
			List<? extends Topic> notifications, GCUBESecurityManager manager,
			GCUBEScope... scope) throws ISNotifierException {
		notifier.registerISNotification(producerEPR, notifications, manager,
				scope);
	}

	public void unregisterISNotification(EndpointReferenceType producerEPR,
			List<? extends Topic> notifications, GCUBESecurityManager manager,
			GCUBEScope... scope) throws ISNotifierException {
		notifier.unregisterISNotification(producerEPR, notifications, manager,
				scope);
	}

	public boolean[] isTopicRegistered(GCUBESecurityManager securityManager,
			GCUBEScope scope, List<TopicData> topics)
			throws ISNotifierException {
		return notifier.isTopicRegistered(securityManager, scope, topics);
	}

	
}
