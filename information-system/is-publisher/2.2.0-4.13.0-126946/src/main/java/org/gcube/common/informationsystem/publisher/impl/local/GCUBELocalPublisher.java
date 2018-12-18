package org.gcube.common.informationsystem.publisher.impl.local;

import java.util.Set;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.publisher.ISLocalPublisher;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.events.GCUBEProducer;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.publisher.impl.GCUBEPublisherException;
import org.gcube.common.informationsystem.publisher.impl.registrations.resources.ISRegistryInstance;
import org.gcube.common.informationsystem.publisher.impl.registrations.resources.ISRegistryInstanceGroup;
import org.gcube.common.informationsystem.publisher.impl.registrations.resources.ISRegistryLookup.NoRegistryAvailableException;

/**
 * Manager for publishing {@link GCUBEResource} as local events
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public final class GCUBELocalPublisher implements ISLocalPublisher {

    protected static final GCUBELog logger = new GCUBELog(GCUBELocalPublisher.class);

    private static GCUBELocalPublisher manager = new GCUBELocalPublisher();

    /** Internal {@link GCUBEProducer} for {@link LocalProfileEvent}. */
    protected static GCUBEProducer<LocalProfileTopic, Object> eventProducer;

    static {
	try {
	    eventProducer = new GCUBEProducer<LocalProfileTopic, Object>();
	} catch (Exception e) {
	}
    }

    public GCUBELocalPublisher() {
    }

    /**
     * Subscribes a consumer to all {@link LocalProfileEvent}.
     * 
     * @param consumer
     *            the consumer
     * @throws Exception
     *             if the subscription could not be completed.
     */
    public void subscribeLocalProfileEvents(LocalProfileConsumer consumer) throws GCUBEPublisherException {
	try {
	    eventProducer.subscribe(consumer, LocalProfileTopic.values());
	} catch (Exception e) {
	    logger.error("Unable to subscribe to local events ", e);
	    throw new GCUBEPublisherException("Unable to subscribe to local events " + e.getMessage());
	}
    }

    /**
     * Returns the local publisher
     * 
     * @return the publisher
     */
    public static GCUBELocalPublisher getManager() {
	return manager;
    }

    /**
     * Notifies that a @link {@link GCUBEResource} has been removed
     * 
     * @param resource
     *            the resource
     */
    public void notifyResourceRemoved(String ID, String type, GCUBEScope scope) throws Exception {
	logger.trace("Removing resource " + ID + " via local event");
	notify(LocalProfileTopic.REMOVED, new LocalProfileEvent(ID, type, scope));
    }

    /**
     * Notifies that a @link {@link GCUBEResource} has been created
     * 
     * @param resource  the resource
     */
    public void notifyResourceRegistered(GCUBEResource resource, GCUBEScope scope) throws Exception {
	logger.trace("Updating resource " + resource.getID() + " via local event");
	notify(LocalProfileTopic.REGISTERED, new LocalProfileEvent(resource, scope));
    }

    /**
     * Notifies that a @link {@link GCUBEResource} has been updated
     * 
     * @param resource the resource
     */
    public void notifyResourceUpdated(GCUBEResource resource, GCUBEScope scope) throws Exception {
	logger.trace("Publishing resource " + resource.getID() + " via local event");
	notify(LocalProfileTopic.UPDATED, new LocalProfileEvent(resource, scope));
    }

    /**
     * Checks if the local publishing is enabled or not for the given scope
     * 
     * @param scope the scope to check
     * @return <tt>true</tt> if the local publishing is enabled, <tt>false</tt> otherwise
     */
    public boolean isEnabled(String resourceType, GCUBEScope scope) {
	try {
	    Set<ISRegistryInstance> availableInstances = ISRegistryInstanceGroup.getInstanceGroup().getRegistryInstancesForTypeAndScope(resourceType, scope);
	    // check if a co-deployed instance is available and use it if so
	    for (ISRegistryInstance instance : availableInstances)
		if (instance.getEndpoint().toString().contains(GHNContext.getContext().getHostnameAndPort()))
		    return true;
	} catch (NoRegistryAvailableException e) {
	    logger.error("Unable to detect if the local publishing is enabled or not", e);
	}
	return false;
    }

    private void notify(final LocalProfileTopic topic, final LocalProfileEvent event) throws Exception {
	new LocalResourceHandler(topic, event, eventProducer).execute();
    }

}
