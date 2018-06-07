package org.gcube.informationsystem.registry.impl.local;

import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.informationsystem.registry.impl.contexts.ServiceContext;
import org.gcube.informationsystem.registry.impl.contexts.ServiceContext.RegistryTopic;

public class LocalNotifier {

	@SuppressWarnings("unchecked")
	public static void notifyEvent(GCUBEResource resource, RegistryTopic topic){
		
		GCUBEEvent<ServiceContext.RegistryTopic, GCUBEResource> event = new GCUBEEvent<ServiceContext.RegistryTopic, GCUBEResource>();
		event.setPayload(resource);
		ServiceContext.getContext().getTopicProducer().notify(topic, event);
		
	}
}
