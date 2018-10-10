package org.gcube.common.core.informationsystem.publisher;


import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.state.GCUBEWSResource;

/**
 * 
 * Defines the local interface to feed an Information System in a gCube infrastructure. <br/>
 *  
 * An implementation of the ISPublisher is in charge of feeding the Information System with the provided {@link GCUBEWSResource}
 * and the service's states ({@link GCUBEWSResource})
 * 
 * @author Andrea Manzi, Manuele Simi(ISTI-CNR)
 *
 */
public interface ISPublisher {

	/**
	 * Publishes {@link GCUBEWSResource} registration on a Information System.
	 * 
	 * @param resource the {@link GCUBEWSResource} to register
	 * @param scope optional {@link GCUBEScope} (overrides the scope specified by the {@link GCUBEWSResource}) 
	 * @throws ISPublisherException Exception
	 */
	public void registerWSResource(GCUBEWSResource resource,GCUBEScope ...scope) throws ISPublisherException;

	/**
	 * Updates {@link GCUBEWSResource} registration on a Information System.
	 * 
	 * @param resource the {@link GCUBEWSResource} to register
	 * @param scope optional {@link GCUBEScope} (overrides the scope specified by the {@link GCUBEWSResource}) 
	 * @throws ISPublisherException Exception
	 */
	public void updateWSResource(GCUBEWSResource resource,GCUBEScope ...scope) throws ISPublisherException;
	
	/**
	 * Unpublishes the registration of a {@link GCUBEWSResource} form the Information System
	 * 
	 * @param resource the {@link GCUBEWSResource} to unregister
	 * @param scope (optional) {@link GCUBEScope} (overrides the scope specified by the {@link GCUBEWSResource}) 
	 * @throws ISPublisherException Exception
	 */
	public void removeWSResource(GCUBEWSResource resource, GCUBEScope ...scope) throws ISPublisherException;
		
	/**
	 * Registers a {@link GCUBEResource} on the Information System
	 * 
	 * @param resource the {@link GCUBEResource} to register
	 * @param scope the {@link GCUBEScope} in which to register the resource
	 * @param manager the {@link GCUBESecurityManager} for contacting the IS 
	 * @throws ISPublisherException Exception
	 */
	public String  registerGCUBEResource(GCUBEResource resource,GCUBEScope scope, GCUBESecurityManager manager)throws ISPublisherException;
	
	/**
	 * Removes a {@link GCUBEResource} from the Information System
	 * 
	 * @param ID the ID related to the {@link GCUBEResource} to remove
	 * @param type the {@link GCUBEResource} type to remove
	 * @param scope the registration {@link GCUBEScope}
	 * @param manager the {@link GCUBESecurityManager} for contacting the IS 
	 * @throws ISPublisherException Exception
	 */
	public  void removeGCUBEResource(String ID,String type,GCUBEScope scope,GCUBESecurityManager manager) throws ISPublisherException;
	
	/**
	 * Updates a {@link GCUBEResource } in the Information System
	 * 
	 * @param resource the new {@link GCUBEResource} to update 
	 * @param manager the {@link GCUBESecurityManager} for contacting the IS 
	 * @throws ISPublisherException Exception
	 */
	public void updateGCUBEResource(GCUBEResource resource,GCUBEScope scope,GCUBESecurityManager manager) throws ISPublisherException;
	
}
