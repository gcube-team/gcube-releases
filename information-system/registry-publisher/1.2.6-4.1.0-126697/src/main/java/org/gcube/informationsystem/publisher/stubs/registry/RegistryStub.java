package org.gcube.informationsystem.publisher.stubs.registry;

import static org.gcube.informationsystem.publisher.stubs.registry.RegistryConstants.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.gcube.informationsystem.publisher.stubs.registry.faults.CreateException;
import org.gcube.informationsystem.publisher.stubs.registry.faults.InvalidResourceException;
import org.gcube.informationsystem.publisher.stubs.registry.faults.RemoveException;
import org.gcube.informationsystem.publisher.stubs.registry.faults.ResourceDoesNotExistException;
import org.gcube.informationsystem.publisher.stubs.registry.faults.ResourceNotAcceptedException;
import org.gcube.informationsystem.publisher.stubs.registry.faults.UpdateException;

/**
 * A local interface to the resource discovery service.
 * 
 * 
 */
@WebService(name=portType,targetNamespace=target_namespace)
public interface RegistryStub {

	/**
	 * 
	 * @param profile the profile in xml
	 * @param type the type of the resource to store
	 * 
	 * @throws InvalidResourceException if the profile is not valid
	 * @throws ResourceNotAcceptedException if some filter is applied registry side
	 * @throws CreateException if something goes wrong on creation
	 */
	@WebMethod(operationName="create")
	@WebResult()
	void create(@WebParam(name="profile") String profile, @WebParam(name="type") String type )  throws InvalidResourceException,
																					ResourceNotAcceptedException, CreateException;
	/**
	 * 
	 * @param id the id of the resource to update
	 * @param type the type of the resource to update
	 * @param profile the profile in xml	 
	 *  
	 * @throws InvalidResourceException if the profile is not valid
	 * @throws ResourceNotAcceptedException if some filter is applied registry side
	 * @throws UpdateException if something goes wrong on update
	 */
	@WebMethod(operationName="update")
	@WebResult()
	void update(@WebParam(name="uniqueID") String id, @WebParam(name="type") String type, @WebParam(name="xmlProfile") String profile )  throws InvalidResourceException,
																					ResourceNotAcceptedException, UpdateException;
	/**
	 * 
	 * @param id the id of the resource to remove
	 * @param type the type of the resource to remove
	 *  
	 * @throws ResourceDoesNotExistException if the resource is not stored on the Collector
	 * @throws RemoveException if something goes wrong during deletion
	 */
	@WebMethod(operationName="remove")
	@WebResult()
	void remove(@WebParam(name="uniqueID") String id, @WebParam(name="type") String type)  throws ResourceDoesNotExistException, 
																								RemoveException;

	
}
