package org.gcube.informationsystem.publisher;

import java.util.List;

import org.gcube.common.resources.gcore.Resource;

public interface RegistryPublisher {
	
	/** 
	 * The resource is created in the current scope and it is updated in the other scopes that are presents in the resource.
	 * If is a VRE scope then in the resource will be added also the VO and INFRA scopes if they are not already presents in the resource
	 * If is a VO scope then in the resource will be added also the INFRA scope if it is not already present in the resource
	 * @throws IllegalStateException if the current scope is incompatible with the scope defined in the resource or if the scope is already defined in the resource 
	 * @throws IllegalArgumentException if there are problems to contact the Registry service or the resource is not a valid resource
 	 */
	<T extends Resource> T create(T resource);
	
	/** 
	 * The resource is created in all VOs scopes that are presents in the resource.
	 * @throws IllegalStateException if scopes is null 
	 * @throws IllegalArgumentException if there are problems to contact the Registry service or the resource is not a valid resource
 	 */
	< T extends Resource> T vosCreate(T resource, List<String> scopes);
	
	/** 
	* The resource will be updated on all the scopes that are defined in the resource.
	 * If an updating operation fail. It will be repeated with best-effort delivery approach
	 * @throws IllegalStateException if the current scope is not defined in the resource. 
	 * @throws IllegalArgumentException if there are problems with the registry service or the resource is not a valid resource
	 */
	<T extends Resource> T update(T resource);
	
	/** 
	* The resource will be updated on all the VO scopes that are defined in the resource.
	 * If an updating operation fail. It will be repeated with best-effort delivery approach
	 * @throws IllegalStateException if the current scope is not defined in the resource. 
	 * @throws IllegalArgumentException if there are problems with the registry service or the resource is not a valid resource
	 */
	<T extends Resource> T vosUpdate(T resource);
	
	/**
	 * The resource will be removed from current scope.
	 * if the scope is the last scope in the resource, the profile will be deleted from IS else:
	 * if it is a VRE scope then the profile will be updated without the VRE scope; 
	 * if it is a VO scope but there is another VRE scope, belong to the VO, defined in the resource then throw IllegalArgumentException;
	 * if it is a INFRA scope but there is another VRE or VO scope , belong to the INFRA, defined in the resource then throw IllegalArgumentException.
	 * @throws IllegalArgumentException if the current scope is not defined in the resource or if there is another VRE scope defined in the resource
	 * @return the resource without the current scope if the remove operation has been successfully completed 
	 */
	 <T extends Resource> T remove(T resource);
	
}
