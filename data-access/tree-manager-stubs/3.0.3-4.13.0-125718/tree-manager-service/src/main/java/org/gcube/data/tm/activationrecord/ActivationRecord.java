/**
 * 
 */
package org.gcube.data.tm.activationrecord;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.tm.Constants;

/**
 * 
 * A wrapper of {@link GCUBEGenericResource}s that record 
 * client invocations of the T-Binder service.
 * 
 * @author Fabio Simeoni
 *
 */
public class ActivationRecord {

	private static JAXBContext binder;
	
	private GCUBEGenericResource resource;
	
	//helper
	private static synchronized JAXBContext dataBinder() throws Exception {
		if (binder==null) 
			binder = JAXBContext.newInstance(ActivationRecordBody.class);
		return binder;
	}
	
	private ActivationRecord(GCUBEGenericResource resource) {
		this.resource=resource;
	}
	
	/**
	 * Creates an instance around a given resource.
	 * @param resource the resource
	 * @return the instance
	 */
	public static ActivationRecord newInstance(GCUBEGenericResource resource) {
		return new ActivationRecord(resource);
	}
	
	/**
	 * Creates an instance from a name, description, and body for the resource.
	 * @param description the description
	 * @param body the body
	 * @return the instance
	 */
	public static ActivationRecord newInstance(String description, ActivationRecordBody body) {
		
		try {
			GCUBEGenericResource resource = GHNContext.getImplementation(GCUBEGenericResource.class);
			resource.setSecondaryType(Constants.ACTIVATIONRECORD_TYPE);
			resource.setName(Constants.ACTIVATIONRECORD_NAME);
			resource.setDescription(description);
			
			StringWriter w = new StringWriter();
			dataBinder().createMarshaller().marshal(body,w);
			resource.setBody(w.toString());
			return new ActivationRecord(resource);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * Returns the wrapped resource
	 * @return the resource
	 */
	public GCUBEGenericResource getResource() {
		return resource;
	}
	
	
	/**
	 * Returns the body of the wrapped resource as a {@link ActivationRecordBody}.
	 * @return the body
	 * @throws Exception if the body cannot be parsed into a {@link ActivationRecordBody}
	 */
	public synchronized ActivationRecordBody getBody() throws Exception {
		return (ActivationRecordBody) dataBinder().createUnmarshaller().unmarshal(new StringReader(resource.getBody()));
	}
	
	/**
	 * Publishes the record
	 * 
	 * @throws Exception if the record cannot be published
	 */
	public void publish() throws Exception {
		new Publisher().run();
	}

	/**
	 * Schedules the publication of the record
	 * 
	 * @param scheduler a pre-configured scheduler
	 * @throws Exception if the publication cannot be scheduled
	 */
	public void publish(GCUBEScheduledHandler<Void> scheduler) throws Exception {
		scheduler.setScheduled(new Publisher());
		scheduler.run();
	}

	//helper class: publishes activation records
	private class Publisher extends GCUBEHandler<Void> {
		@Override public void run() throws Exception {
			ISPublisher publisher = GHNContext.getImplementation(ISPublisher.class);
			GCUBEScope currentScope = GCUBEScope.getScope(ScopeProvider.instance.get());
			publisher.registerGCUBEResource(getResource(),currentScope,getSecurityManager());
			logger.info("published activation record "+getResource().getID()); 
			
		}
		
	}
}
