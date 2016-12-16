/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.api;

import java.util.UUID;

import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public interface EntityManagement {

	/* Facets Methods */

	public String createFacet(String facetType, String jsonRepresentation)
			throws EntityException, ResourceRegistryException;

	public String readFacet(UUID uuid) throws FacetNotFoundException,
			ResourceRegistryException;

	public String readFacet(UUID uuid, String facetType)
			throws FacetNotFoundException, ResourceRegistryException;

	public String updateFacet(UUID uuid, String jsonRepresentation)
			throws FacetNotFoundException, ResourceRegistryException;

	public boolean deleteFacet(UUID uuid) throws FacetNotFoundException,
			ResourceRegistryException;

	/* Resources Methods */

	public String createResource(String resourceType, String jsonRepresentation)
			throws ResourceRegistryException;

	public String readResource(UUID uuid) throws ResourceNotFoundException,
			ResourceRegistryException;

	public String readResource(UUID uuid, String resourceType)
			throws ResourceNotFoundException, ResourceRegistryException;

	/**
	 * Update a resource means update ConsistsOf relations and related Facets
	 * presents in the jsonRepresentation argument. All existent ConsistsOf
	 * relations and related Facets are keep as they are. All IsRelatedTo
	 * relations and related resources (if any) are ignored.
	 * 
	 * @param resourceUUID
	 * @param jsonRepresentation
	 * @return
	 * @throws ResourceNotFoundException
	 * @throws ResourceRegistryException
	 */
	public String updateResource(UUID resourceUUID, String jsonRepresentation)
			throws ResourceNotFoundException, ResourceRegistryException;

	public boolean deleteResource(UUID uuid) throws ResourceNotFoundException,
			ResourceRegistryException, ResourceRegistryException;

	/* Relations Methods */

	public String attachFacet(UUID resourceUUID, UUID facetUUID,
			String consistOfType, String jsonRepresentation)
			throws FacetNotFoundException, ResourceNotFoundException,
			ResourceRegistryException;

	public boolean detachFacet(UUID consistsOfUUID)
			throws ResourceRegistryException;

	public String attachResource(UUID sourceResourceUUID,
			UUID targetResourceUUID, String relatedToType,
			String jsonRepresentation) throws ResourceNotFoundException,
			ResourceRegistryException;

	public boolean detachResource(UUID isRelatedToUUID)
			throws ResourceRegistryException;

	/* Context toggle methods */
	public boolean addResourceToContext(UUID uuid)
			throws ResourceNotFoundException, ContextNotFoundException,
			ResourceRegistryException;

	public boolean addFacetToContext(UUID uuid) throws FacetNotFoundException,
			ContextNotFoundException, ResourceRegistryException;

}
