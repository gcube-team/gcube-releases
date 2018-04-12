package org.gcube.informationsystem.publisher;

import java.util.List;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;

public interface ScopedPublisher {
	
	/** 
	 * The resource is created in all the scopes in the list and the resource is updated in all the other scopes that are presents in the resource
	 * @throws IllegalStateException if the current scope is incompatible with the scope defined in the resource 
	 * @throws IllegalArgumentException if there are problems to contact the Registry service or the resource is not a valid resource
 	 */
	public <T extends Resource> T create(T resource, List<String> scopes) throws RegistryNotFoundException;
	
	/** 
	 * The resource will be updated on all scopes that are defined in the resource.
	 * If an updating operation fail. It will be repeated with best-effort strategy.
	 * @throws IllegalStateException if the current scope is not defined in the resource. 
	 * @throws IllegalArgumentException if there are problems with the registry service or the resource is not a valid resource
	 */
	public <T extends Resource> T update(T resource) throws RegistryNotFoundException;
	
	/**
	 * Remove a resource from all the scopes in the input list, if and only if  all the scopes are defined in the resource 
	 * @throws IllegalArgumentException if one or more  scopes in the list are not defined in the resource 
	 * @return the resource without the current scope if the remove operation is succesfully
	 */
	public  <T extends Resource> T remove(T resource, List<String> scopes) throws RegistryNotFoundException;
	
}
