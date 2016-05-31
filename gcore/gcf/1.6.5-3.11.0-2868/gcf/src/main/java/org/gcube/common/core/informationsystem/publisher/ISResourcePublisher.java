package org.gcube.common.core.informationsystem.publisher;

import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;

/**
 * Defines a local interface for registering {@link GCUBEResource}
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public interface ISResourcePublisher {

	/**
	 * Registers a {@link GCUBEResource} on the Information System
	 * 
	 * @param resource the {@link GCUBEResource} to register
	 * @param scope the {@link GCUBEScope} in which to register the resource
	 * @param manager the {@link GCUBESecurityManager} for contacting the IS 
	 * @throws ISPublisherException Exception
	 */
	public void  register(GCUBEResource resource,GCUBEScope scope, GCUBESecurityManager manager)throws ISPublisherException;
	
	/**
	 * Removes a {@link GCUBEResource} from the Information System
	 * 
	 * @param ID the ID related to the {@link GCUBEResource} to remove
	 * @param type the {@link GCUBEResource} type to remove
	 * @param scope the registration {@link GCUBEScope}
	 * @param manager the {@link GCUBESecurityManager} for contacting the IS 
	 * @throws ISPublisherException Exception
	 */
	public  void remove(String ID,String type,GCUBEScope scope,GCUBESecurityManager manager) throws ISPublisherException;
	
	/**
	 * Updates a {@link GCUBEResource } in the Information System
	 * 
	 * @param resource the new {@link GCUBEResource} to update 
	 * @param manager the {@link GCUBESecurityManager} for contacting the IS 
	 * @throws ISPublisherException Exception
	 */
	public void update(GCUBEResource resource,GCUBEScope scope, GCUBESecurityManager manager) throws ISPublisherException;

}
