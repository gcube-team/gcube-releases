package org.gcube.common.informationsystem.publisher.impl.local;

import org.gcube.common.core.informationsystem.publisher.ISLocalPublisher.LocalProfileEvent;
import org.gcube.common.core.informationsystem.publisher.ISLocalPublisher.LocalProfileTopic;
import org.gcube.common.core.utils.events.GCUBEProducer;
import org.gcube.common.informationsystem.publisher.impl.registrations.handlers.BaseISPublisherHandler;

/**
 * 
 * Handler for local publishing
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
class LocalResourceHandler extends BaseISPublisherHandler {

    private GCUBEProducer<LocalProfileTopic, Object> eventProducer;
    private LocalProfileTopic topic;
    private LocalProfileEvent event;

    public LocalResourceHandler(final LocalProfileTopic topic, final LocalProfileEvent event, GCUBEProducer<LocalProfileTopic, Object> eventProducer) {
	this.topic = topic;
	this.event = event;
	this.eventProducer = eventProducer;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void submitRequest() throws Exception {	
	 this.eventProducer.notify(topic, event);		
    }

    @Override
    public String getResourceID() {
	return "Local event " + event.toString() + " for topic " +topic.toString();
    }

}
