/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.api;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public interface EntityManagement {

	/* Facets Methods */

	public String createFacet(String facetType, String jsonRepresentation)
			throws EntityException, ResourceRegistryException;

	public String readFacet(String uuid) throws FacetNotFoundException,
			ResourceRegistryException;

	public String readFacet(String uuid, String facetType)
			throws FacetNotFoundException, ResourceRegistryException;

	public String updateFacet(String uuid, String jsonRepresentation)
			throws FacetNotFoundException, ResourceRegistryException;

	public boolean deleteFacet(String uuid) throws FacetNotFoundException,
			ResourceRegistryException;

	/* Resources Methods */

	public String createResource(String resourceType, String jsonRepresentation)
			throws ResourceRegistryException;

	public String readResource(String uuid) throws ResourceNotFoundException,
			ResourceRegistryException;

	public String readResource(String uuid, String resourceType)
			throws ResourceNotFoundException, ResourceRegistryException;

	public boolean deleteResource(String uuid)
			throws ResourceNotFoundException, ResourceRegistryException,
			ResourceRegistryException;

	/* Relations Methods */

	public String attachFacet(String resourceUUID, String facetUUID,
			String consistOfType, String jsonRepresentation)
			throws FacetNotFoundException, ResourceNotFoundException,
			ResourceRegistryException;

	public boolean detachFacet(String consistOfUUID)
			throws ResourceRegistryException;

	public String attachResource(String sourceResourceUUID,
			String targetResourceUUID, String relatedToType,
			String jsonRepresentation) throws ResourceNotFoundException,
			ResourceRegistryException;

	public boolean detachResource(String relatedToUUID)
			throws ResourceRegistryException;

}
