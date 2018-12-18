package org.gcube.informationsystem.registry.impl.porttypes;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.contexts.GCUBEServiceContext;

import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.registry.impl.contexts.ServiceContext;
import org.gcube.informationsystem.registry.impl.preprocessing.filters.FilterManager;
import org.gcube.informationsystem.registry.impl.preprocessing.filters.FilterExecutor.InvalidFilterException;
import org.gcube.informationsystem.registry.impl.state.Definitions.ResourceMappings;
import org.gcube.informationsystem.registry.stubs.resourceregistration.CreateFault;
import org.gcube.informationsystem.registry.stubs.resourceregistration.CreateMessage;
import org.gcube.informationsystem.registry.stubs.resourceregistration.CreateResponse;
import org.gcube.informationsystem.registry.stubs.resourceregistration.InvalidResourceFault;
import org.gcube.informationsystem.registry.stubs.resourceregistration.RemoveFault;
import org.gcube.informationsystem.registry.stubs.resourceregistration.RemoveMessage;
import org.gcube.informationsystem.registry.stubs.resourceregistration.RemoveResponse;
import org.gcube.informationsystem.registry.stubs.resourceregistration.ResourceNotAcceptedFault;
import org.gcube.informationsystem.registry.stubs.resourceregistration.UpdateFault;
import org.gcube.informationsystem.registry.stubs.resourceregistration.UpdateMessage;
import org.gcube.informationsystem.registry.stubs.resourceregistration.UpdateResponse;

/**
 * 
 * Implementation of the ResourceRegistration portType
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class ResourceRegistration extends GCUBEPortType {

	protected final GCUBELog logger = new GCUBELog(ResourceRegistration.class);

	/** The UUIDGen */
	private static final UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

	
	/**
	 * Creates a new resource
	 * @param message
	 * @return
	 * @throws SchemaValidationFault
	 * @throws ResourceNotAcceptedFault
	 * @throws CreateFaultType
	 */
	public CreateResponse create(CreateMessage message) 
		throws InvalidResourceFault, ResourceNotAcceptedFault, CreateFault {
		
		logger.info("CreateResource operation invoked in scope " + ServiceContext.getContext().getScope());
		GCUBEResource resource = this.load(message.getType(),message.getProfile());		
		this.applyFilters(resource);	
		//create the new resource with the publisher
		try {
			new LocalResourceRegistration().create(resource);
		} catch (Exception e) {
			logger.error("Unable to register the resource", e);
			throw new CreateFault();
		}

		return new CreateResponse();
	}
	
	/**
	 * Updates an existing resource
	 * @param message
	 * @return
	 * @throws SchemaValidationFault
	 * @throws ResourceNotAcceptedFault
	 * @throws UpdateFaultType
	 */
	public UpdateResponse update(UpdateMessage message)
		throws InvalidResourceFault, ResourceNotAcceptedFault, UpdateFault {
		
		logger.info("UpdateResource operation invoked in scope " + ServiceContext.getContext().getScope());
		
		GCUBEResource resource = this.load(message.getType(), message.getXmlProfile(), message.getUniqueID());
		this.applyFilters(resource);
		//update the resource with the publisher
		try {
			new LocalResourceRegistration().update(resource);
		} catch (Exception e) {
			logger.error("Unable to update the resource", e);
			throw new UpdateFault();
		}
		return new UpdateResponse();
		
	}
	
	/**
	 * Removes a resource
	 * @param message id and type of the resource to remove
	 * @return an empty message
	 * @throws ResourceDoesNotExistFaultType if the resource does not exist
	 * @throws RemoveFaultType if an error occurs during the removal
	 */
	public RemoveResponse remove(RemoveMessage message)	throws RemoveFault,RemoteException {
		String ID = message.getUniqueID();
		String type = message.getType();
		logger.info("RemoveResource operation invoked on resource ID=" + ID	+ ", type=" + type);
		if (ID == null || ID.compareTo("") == 0) {
			logger.warn("Resource ID is missing, cannot manage the resource");
			throw new RemoteException("Resource ID is missing, cannot manage the resource");
		}
		try {
			new LocalResourceRegistration().remove(ID, type);
		} catch (Exception e) {
			logger.error("Unable to remove the resource " + ID, e);
			throw new UpdateFault();
		}

		return new RemoveResponse();
	}
	
	
	/**
	 * Loads the {@link GCUBEResource} from its serialization
	 * @param type the type of the resource to load
	 * @param profile the serialization
	 * @param id the optional identifier to assign
	 * @return the resource
	 * @throws InvalidResourceFault if the resource was null
	 * @throws ResourceNotAcceptedFault if the resource was not accepted
	 */
	private GCUBEResource load(String type, String profile, String ... id) 
		throws InvalidResourceFault,ResourceNotAcceptedFault {
		
		if (profile == null || profile.compareTo("") == 0) {			
			logger.error("The input resource is null");
			throw new InvalidResourceFault();
		}
		GCUBEResource resource;
		try {
			resource = ResourceMappings.valueOf(type).getResourceImplementation();
			resource.load(new BufferedReader(new InputStreamReader(	new ByteArrayInputStream(profile.getBytes("UTF-8")),"UTF-8")));
			//resource.addScope(ServiceContext.getContext().getScope());				
			
			// check the ID
			if (resource.getID() == null || resource.getID().compareTo("") == 0) {
				String newid = (id != null && id.length>0) ? id[0] : uuidgen.nextUUID();
				if (!resource.setID(newid)) {
					logger.error("Unable to set a new ID to the resource");
					throw new ResourceNotAcceptedFault();
				}
			}
			return resource;
		} catch (Exception ex) {
			logger.error("Error trying to load  profile", ex);
			throw new InvalidResourceFault();
		}		
	}
	
	/**
	 * Applies the configured filters to the resource
	 * @param resource the resource to filter
	 * @throws ResourceNotAcceptedFault if the resource was not accepted
	 */
	private void applyFilters(GCUBEResource resource) throws ResourceNotAcceptedFault {			
		// apply resource filter
		try {
			if (!FilterManager.getExecutor(resource.getType()).accept(resource)) {
				logger.warn("Resource " + resource.getID() + " NOT accepted ");
				throw new ResourceNotAcceptedFault();
			}
			logger.trace("Resource " + resource.getID() + " accepted ");
		} catch (InvalidFilterException e) {
			logger.warn("Invalid filter selected, the resource "+ resource.getID() + " CANNOT be filtered ");
		}
	}
		
	
	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

}
