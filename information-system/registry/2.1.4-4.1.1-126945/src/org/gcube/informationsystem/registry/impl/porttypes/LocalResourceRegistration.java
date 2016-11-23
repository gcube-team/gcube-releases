package org.gcube.informationsystem.registry.impl.porttypes;

import java.io.StringWriter;
import java.util.Calendar;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.publisher.ISResourcePublisher;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.registry.impl.contexts.ServiceContext;
import org.gcube.informationsystem.registry.impl.contexts.ServiceContext.RegistryTopic;
import org.gcube.informationsystem.registry.impl.local.LocalNotifier;
import org.gcube.informationsystem.registry.impl.postprocessing.remove.AvailablePurgers;
import org.gcube.informationsystem.registry.impl.postprocessing.remove.Purger;
import org.gcube.informationsystem.registry.impl.postprocessing.update.AvailableUpdaters;
import org.gcube.informationsystem.registry.impl.postprocessing.update.Updater;
import org.gcube.informationsystem.registry.impl.state.Definitions.OperationType;
import org.gcube.informationsystem.registry.impl.state.Definitions.ResourceMappings;

public class LocalResourceRegistration {
	
	protected final GCUBELog logger = new GCUBELog(ResourceRegistration.class);

	public void create(GCUBEResource resource) throws Exception {
		ISResourcePublisher publisher = GHNContext.getImplementation(ISResourcePublisher.class);						
		publisher.register(resource, ServiceContext.getContext().getScope(), ServiceContext.getContext());			
		logger.debug("Resource " + resource.getID() + " successfully created");
		logger.trace("Looking for updater for "+ resource.getType());
		Updater<?> updater = AvailableUpdaters.getPurger(resource.getType());
		if (updater != null) {
			try {
				logger.debug("Applying updater for "  + resource.getType());
				updater.update(resource.getClass().cast(resource), ServiceContext.getContext().getScope());
			} catch (Exception e) {
				logger.error("Error while updating the profiles related to the resource",	e);
			}
		} else
			logger.trace("No updater found");
		
		//let the notifiers know
		LocalNotifier.notifyEvent(resource, RegistryTopic.CREATE);
		StringWriter writer = new StringWriter();
		resource.store(writer);
		RegistryFactory.updateCounterInfo(resource.getID(), ResourceMappings.valueOf(
				resource.getType()), OperationType.create, 
				Calendar.getInstance(), writer.toString(), ServiceContext.getContext().getScope());
	}
	/**
	 * 
	 * @param id
	 * @param type
	 * @throws Exception
	 */
	public void remove(String id, String type) throws Exception {
		ISResourcePublisher publisher = GHNContext.getImplementation(ISResourcePublisher.class);									
		publisher.remove(id, type, ServiceContext.getContext().getScope(), ServiceContext.getContext());
		logger.debug("Resource " + id+ " successfully removed");
		// if there is a purger available, remove also the related RIs
		logger.trace("Looking for purger for "+ type);
		Purger<?> purger = AvailablePurgers.getPurger(type);
		if (purger != null) {
			try {
				logger.debug("Applying purger for "  + type);
				purger.purge(id, ServiceContext.getContext().getScope());
			} catch (Exception e) {
				logger.error("Error while removing the profiles related to the resource",	e);
			}
		} else
			logger.trace("No purger found");
		//let the notifiers know
		GCUBEResource resource = ResourceMappings.valueOf(type).getResourceImplementation();
		resource.setID(id);				
		LocalNotifier.notifyEvent(resource, RegistryTopic.REMOVE);
		RegistryFactory.updateCounterInfo(id, ResourceMappings.valueOf(type),OperationType.destroy, Calendar.getInstance(), null, ServiceContext.getContext().getScope());
	}
	
	public void update(GCUBEResource resource) throws Exception {
		ISResourcePublisher publisher = GHNContext.getImplementation(ISResourcePublisher.class);						
		publisher.register(resource, ServiceContext.getContext().getScope(), ServiceContext.getContext());			
		logger.debug("Resource " + resource.getID() + " successfully updated");
		logger.trace("Looking for updater for "+ resource.getType());
		Updater<?> updater = AvailableUpdaters.getPurger(resource.getType());
		if (updater != null) {
			try {
				logger.debug("Applying updater for "  + resource.getType());
				updater.update(resource.getClass().cast(resource), ServiceContext.getContext().getScope());
			} catch (Exception e) {
				logger.error("Error while updating the profiles related to the resource",	e);
			}
		} else
			logger.trace("No updater found");
		LocalNotifier.notifyEvent(resource, RegistryTopic.UPDATE);
		StringWriter writer = new StringWriter();
		resource.store(writer);
		RegistryFactory.updateCounterInfo(resource.getID(), ResourceMappings.valueOf(resource.getType()),
				OperationType.update, Calendar.getInstance(), writer.toString(), ServiceContext.getContext().getScope());
	
	}
}
