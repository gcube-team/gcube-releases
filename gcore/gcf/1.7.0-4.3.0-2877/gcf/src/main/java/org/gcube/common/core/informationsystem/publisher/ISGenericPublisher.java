package org.gcube.common.core.informationsystem.publisher;

import java.util.List;

import org.gcube.common.core.scope.GCUBEScope;


/**
 * Generic publisher for {@link ISResource}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public interface ISGenericPublisher {

	/**
	 * Registers the resource in the IS
	 * @param resource the resource to register 
	 * @param scope the scope in which to perform the operation
	 * @throws ISPublisherException if the registration fails
	 */
	public void register(ISResource resource, GCUBEScope scope) throws ISPublisherException;	
	
	/**
	 * Registers a list of resources in the IS
	 * @param resources the resources to register 
	 * @param scope the scope in which to perform the operation
	 * @throws ISPublisherException if the registration fails
	 */
	public void register(List<ISResource> resources, GCUBEScope scope) throws ISPublisherException;	
	
	/**
	 * Updates the resource in the IS
	 * @param resource the resource to update 
	 * @param scope the scope in which to perform the operation
	 * @throws ISPublisherException if the update fails
	 */
	public void update(ISResource resource, GCUBEScope scope) throws ISPublisherException;
	
	/**
	 * Updates a list of resources in the IS
	 * @param resources the resources to update 
	 * @param scope the scope in which to perform the operation
	 * @throws ISPublisherException if the update fails
	 */
	public void update(List<ISResource> resources, GCUBEScope scope) throws ISPublisherException;
	
	
	/**
	 * Removes the resource from the IS
	 * @param resource the resource to remove 
	 * @param scope the scope in which to perform the operation
	 * @throws ISPublisherException if the remove fails
	 */
	public void remove(ISResource resource, GCUBEScope scope) throws ISPublisherException;
	
	/**
	 * Removes a list of resources from the IS
	 * @param resources the resources to remove 
	 * @param scope the scope in which to perform the operation
 	 * @throws ISPublisherException if the remove fails

	 */
	public void remove(List<ISResource> resources, GCUBEScope scope) throws ISPublisherException;

}
