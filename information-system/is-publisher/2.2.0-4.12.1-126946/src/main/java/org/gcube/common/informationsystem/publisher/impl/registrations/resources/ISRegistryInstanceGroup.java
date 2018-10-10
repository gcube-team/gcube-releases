package org.gcube.common.informationsystem.publisher.impl.registrations.resources;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.informationsystem.publisher.impl.registrations.resources.ISRegistryLookup.NoRegistryAvailableException;

/**
 * List of ISRegistry instances currently available in the infrastructure
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class ISRegistryInstanceGroup {

	private Set<ISRegistryInstance> registries = Collections.synchronizedSet(new HashSet<ISRegistryInstance>());		
	
	private static ISRegistryInstanceGroup list = new ISRegistryInstanceGroup();
	
	private ISRegistryInstanceGroup() {}
	
	public static ISRegistryInstanceGroup getInstanceGroup() {
		return list;
	}
	
	/**
	 * Gets the list of instances managing the given resource type in the specified {@link GCUBEScope}
	 * @param type
	 * @param scope
	 * @return
	 * @throws NoRegistryAvailableException
	 */
	public Set<ISRegistryInstance> getRegistryInstancesForTypeAndScope(String type, GCUBEScope scope) throws NoRegistryAvailableException {
		Set<ISRegistryInstance> localSet = new HashSet<ISRegistryInstance>();
		for (ISRegistryInstance instance : this.registries) 
			if (instance.getManagedResourceTypes().contains(type) 
			 && ((instance.getScopes().contains(scope))
			 || ((scope.getType().compareTo(GCUBEScope.Type.VRE) == 0) && instance.getScopes().contains(scope.getEnclosingScope()))))
				localSet.add(instance);									
		return localSet;
	}
	
	/**
	 * Adds a new ISRegistry instance to use for the given resource type
	 * 
	 * @param instance the new instance 
	 */
	public void addRegistry(ISRegistryInstance instance) {
		
		//if the instance is already in the set, it is removed and then added again. 
		//In this way, if the list of resources managed by that instance changed, the changes are taken into consideration 
		if (! registries.contains(instance))
			registries.remove(instance);
	
		registries.add(instance);			
	}
		
}
