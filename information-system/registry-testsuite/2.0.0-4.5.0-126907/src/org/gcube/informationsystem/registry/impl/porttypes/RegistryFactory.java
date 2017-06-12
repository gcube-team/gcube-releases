package org.gcube.informationsystem.registry.impl.porttypes;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Calendar;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEServiceContext.Status;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.registry.impl.contexts.FactoryContext;
import org.gcube.informationsystem.registry.impl.contexts.ProfileContext;
import org.gcube.informationsystem.registry.impl.contexts.ServiceContext;
import org.gcube.informationsystem.registry.impl.state.ProfileResource;
import org.gcube.informationsystem.registry.impl.state.RegistryFactoryResource;
import org.gcube.informationsystem.registry.impl.state.Definitions.OperationType;
import org.gcube.informationsystem.registry.stubs.CreateResourceMessage;
import org.gcube.informationsystem.registry.stubs.ProfileAlreadyRegisteredFault;
import org.gcube.informationsystem.registry.stubs.RegistryProperty;
import org.gcube.informationsystem.registry.stubs.RemoveResourceMessage;
import org.gcube.informationsystem.registry.stubs.RemoveResourceResponse;
import org.gcube.informationsystem.registry.stubs.ResourceNotAcceptedFault;
import org.gcube.informationsystem.registry.stubs.SchemaValidationFault;
import org.gcube.informationsystem.registry.stubs.UpdateResourceMessage;
import org.gcube.informationsystem.registry.stubs.UpdateResourceResponse;
import org.gcube.informationsystem.registry.stubs.resourceregistration.CreateMessage;
import org.gcube.informationsystem.registry.stubs.resourceregistration.RemoveMessage;
import org.gcube.informationsystem.registry.stubs.resourceregistration.UpdateMessage;

import static org.gcube.informationsystem.registry.impl.state.Definitions.ResourceType;

/**
 * Implementation of the <em>Registry Factory</em> portType
 * 
 * @author Lucio Lelii, Manuele Simi (ISTI-CNR)
 * 
 */
public class RegistryFactory extends GCUBEPortType {
	

	/** Object logger */
	protected static final GCUBELog logger = new GCUBELog(RegistryFactory.class);

	
	/** the key used to label the Factory Resource */
	public static final String NOTIFICATOR_RESOURCE_KEY = "RegistryResource";

	private static RegistryFactoryResource singletonResource = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onInitialisation() throws Exception { 
		
		if (singletonResource != null)
			return;//cannot create the state twice
		logger.info("Initialising the factory state...");
		new Thread() {

			@Override
			public void run() {
				int attempts = 0;
				boolean created = false;
				while (attempts++ < 10) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						RegistryFactory.logger.error("Failed to sleep in between factory creation");	
						ServiceContext.getContext().setStatus(Status.FAILED);
						break;
					}
					try {
						for (GCUBEScope scope: ServiceContext.getContext().getInstance().getScopes().values()){
							RegistryFactory.logger.info("Creating the notification resource " + RegistryFactory.NOTIFICATOR_RESOURCE_KEY + " within the scope " + scope.getName());
							ServiceContext.getContext().setScope(scope);
							singletonResource = (RegistryFactoryResource) FactoryContext.getContext().getWSHome().create(FactoryContext.getContext().makeKey(RegistryFactory.NOTIFICATOR_RESOURCE_KEY));
							singletonResource.store();						
						}
						created = true;
						break;
					} catch (Exception e) {
						RegistryFactory.logger.error("Failed to create the resource", e);						
					}		
				}
				if (!created) 
					ServiceContext.getContext().setStatus(Status.FAILED);									
			}
			
		}.start();
				
	}

	/**
	 * 
	 * Creates a new {@link ProfileResource} and registers the {@link GCUBEResource} in the IS-IC 
	 * 
	 * @param inputMessage defined in the WSDL
	 * @return the registered profile 
	 * @throws SchemaValidationException if the string serialization of the resource is not valid
	 * @throws ResourceNotAcceptedFault it the resource is rejected when evaluating the resources' filters
	 * @throws RemoteException
	 * @throws ProfileAlreadyRegisteredFault
	 */	
	public String createResource(CreateResourceMessage mess)
			throws SchemaValidationFault, RemoteException, ProfileAlreadyRegisteredFault, ResourceNotAcceptedFault {

		logger.info("CreateResource operation invoked in scope " + ServiceContext.getContext().getScope());

		try {
		ResourceRegistration registration = new ResourceRegistration();
		CreateMessage message = new CreateMessage(mess.getProfile(), mess.getType());
		registration.create(message);
		} catch (Exception e) {
			logger.error("", e);
			throw new ResourceNotAcceptedFault();
		}
		return "";
		
	}

	/**
	 * Updates a {@link GCUBEResource}
	 * 
	 * @param mess Complex Object that contains a String representing the XML profile to update and the diligentID
	 * @return UpdateResourceResponse
	 * @throws RemoteException Exception
	 * @throws SchemaValidationException if the string serialization of the resource is not valid
	 * @throws ResourceNotAcceptedFault it the resource is rejected when evaluating the resources' filters
	 */
	
	public UpdateResourceResponse updateResource(UpdateResourceMessage mess)
			throws RemoteException, SchemaValidationFault, ResourceNotAcceptedFault, GCUBEFault {

		logger.info("UpdateResource operation invoked in scope " + ServiceContext.getContext().getScope());		

		try {
			ResourceRegistration registration = new ResourceRegistration();
			UpdateMessage message = new UpdateMessage(mess.getType(), mess.getUniqueID(), mess.getXmlProfile()); 
			registration.update(message);
		} catch (Exception e) {
			logger.error("", e);
			throw new ResourceNotAcceptedFault();
		}
		return new UpdateResourceResponse();
	}

	/**
	 * Removes a Resource profile identified by the given the resource ID
	 * 
	 * @param inputMessage defined into WSDL file
	 * @return RemoveResourceResponse
	 * @throws RemoteException
	 */
	
	public RemoveResourceResponse removeResource(
			RemoveResourceMessage mess) throws RemoteException,	GCUBEFault {

		logger.info("RemoveResource operation invoked in scope " + ServiceContext.getContext().getScope());
		
		try {
			ResourceRegistration registration = new ResourceRegistration();
			RemoveMessage message = new RemoveMessage(mess.getType(), mess.getUniqueID()); 
			registration.remove(message);
		} catch (Exception e) {
			logger.error("", e);
			throw new RemoteException();
		}

		return new RemoveResourceResponse();

	}

	/**
	 * Gets the profile resource 
	 * 
	 * @param id the Resource ID
	 * @return the resource or <tt>null</tt> if the resource has not been created yet
	 */
	protected synchronized ProfileResource getProfileResource(String id) {
		try {
			return (ProfileResource) ProfileContext.getContext().getWSHome()
					.find(ProfileContext.getContext().makeKey(id));
		} catch (Exception e) {
			logger.debug("A profile with the given id " + id+ " has not been created yet");
		}
		return null;

	}

	/**
	 * Checks whether a stateful resource with the given identifier exists 
	 * 
	 * @param id the resource's identifier
	 * @return <tt>true</tt> if the resource exists, <tt>false</tt> otherwise
	 */
	protected synchronized boolean isResourceCreated(String id) {
		if (getProfileResource(id) != null)
			return true;
		else
			return false;
	}

	/**
	 * Updates the RegistryFactoryResource RPs for notification
	 * 
	 * @param ID resource ID
	 * @param type the resource type
	 * @param operationType the type of Operation performed on the Profile
	 * @param updateTime the last operation Time
	 * @throws Exception if the update fails
	 */
	protected static synchronized void updateCounterInfo(String ID,
			ResourceType resType, OperationType opType, Calendar updateTime,
			String profile) throws Exception {
		//return;
		RegistryProperty property = new RegistryProperty();
		property.setUniqueID(ID);
		property.setProfile(profile);
		property.setOperationType(opType.name());
		property.setChangeTime(updateTime);
		// select the type of the resource to update
		logger.trace("Notifying about resource " + ID +", event: " + opType);
		for (Method method : getResource().getClass().getDeclaredMethods()) {
			if (method.getName().contains(resType.name())
					&& method.getName().contains("set")) {
				method.invoke(getResource(), property);
				break;
			}

		}
		getResource().store();
	}

	/**
	 * Gets the factory stateful resource
	 * 
	 * @return the resource
	 * @throws RemoteException if the stateful resource of the factory cannot be found in the home
	 * 
	 */
	private static RegistryFactoryResource getResource() throws RemoteException {
		if (singletonResource != null)
			return singletonResource;
		
		Object resource = null;
		try {
			resource = FactoryContext.getContext().getWSHome().find(
					FactoryContext.getContext().makeKey(NOTIFICATOR_RESOURCE_KEY));
		} catch (Exception e) {
			logger.error(" Unable to access resource", e);
		}

		RegistryFactoryResource factoryResource = (RegistryFactoryResource) resource;
		return factoryResource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

}

