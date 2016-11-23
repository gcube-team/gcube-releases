package org.gcube.informationsystem.registry.impl.postprocessing.remove;

import java.util.Set;

import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;

/**
 * Base interface for purgers of {@link GCUBEResource}
 * 
 * @param <RESOURCE>
 * 
 * @author Manuele Simi (ISTI-CNR)
 */
public interface Purger<RESOURCE extends GCUBEResource> {

	/**
	 * Purges the RESOURCE
	 * @param resourceId the resource identifier
	 * @param scope the operational scope
	 * @return the identifiers of the deleted resources 
	 * @throws Exception if the purging operation fails
	 */
	public Set<String> purge(String resourceId, GCUBEScope scope) throws Exception;	
		

	/**
	 * Gets the type of resource managed by the purgerE
	 * @return the type
	 */
	public String getName();

}
