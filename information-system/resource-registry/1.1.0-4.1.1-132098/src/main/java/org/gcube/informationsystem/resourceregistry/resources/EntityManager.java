package org.gcube.informationsystem.resourceregistry.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.EntityManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.EntityPath;
import org.gcube.informationsystem.resourceregistry.resources.impl.EntityManagementImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * @author Lucio Lelii (lucio.lelii@isti.cnr.it)
 */
@Path(EntityPath.ENTITY_PATH_PART)
public class EntityManager {

	private static Logger logger = LoggerFactory.getLogger(SchemaManager.class);

	public static final String ID_PATH_PARAM = "id";
	public static final String TYPE_PATH_PARAM = "type";

	public static final String SOURCE_ID_PATH_PARAM = "sourceId";
	public static final String TARGET_ID_PATH_PARAM = "targetId";



	protected EntityManagement entityManager = new EntityManagementImpl();

	/* Facets Methods */
	/**
	 * e.g. PUT /resource-registry/entity/facet/ContactFacet?definition={...}
	 * @param type
	 * @param definition
	 * @return
	 * @throws EntityException
	 * @throws ResourceRegistryException
	 */
	@PUT
	@Path(EntityPath.FACET_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	public String createFacet(
			@PathParam(TYPE_PATH_PARAM) String type,
			@QueryParam(EntityPath.DEFINITION_PARAM) String definition)
			throws EntityException, ResourceRegistryException {
		logger.trace("requested facet creation for type {} defined by {} ",
				type, definition);
		return entityManager.createFacet(type, definition);
	}

	/**
	 * e.g. POST /resource-registry/entity/facet/4023d5b2-8601-47a5-83ef-49ffcbfc7d86?definition={...}
	 * @param uuid
	 * @param definition
	 * @return
	 * @throws FacetNotFoundException
	 * @throws ResourceRegistryException
	 */
	@POST
	@Path(EntityPath.FACET_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public String updateFacet(
			@PathParam(ID_PATH_PARAM) String uuid,
			@QueryParam(EntityPath.DEFINITION_PARAM) String definition)
			throws FacetNotFoundException, ResourceRegistryException {
		logger.trace("requested facet update for id {} with {}", uuid,
				definition);
		return entityManager.updateFacet(uuid, definition);
	}

	/**
	 * e.g. DELETE /resource-registry/entity/facet/4023d5b2-8601-47a5-83ef-49ffcbfc7d86
	 * @param uuid
	 * @return
	 * @throws FacetNotFoundException
	 * @throws ResourceRegistryException
	 */
	@DELETE
	@Path(EntityPath.FACET_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean deleteFacet(@PathParam(ID_PATH_PARAM) String uuid)
			throws FacetNotFoundException, ResourceRegistryException {
		logger.trace("Requested to delete Facet with id {}", uuid);
		return entityManager.deleteFacet(uuid);
	}

	/* Resources Methods */

	/**
	 * e.g. PUT /resource-registry/entity/resource/HostingNode?definition={...}
	 * @param type
	 * @param definition
	 * @return
	 * @throws FacetNotFoundException
	 * @throws ResourceRegistryException
	 */
	@PUT
	@Path(EntityPath.RESOURCE_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	public String createResource(
			@PathParam(TYPE_PATH_PARAM) String type,
			@QueryParam(EntityPath.DEFINITION_PARAM) String definition)
			throws FacetNotFoundException, ResourceRegistryException {
		logger.trace("requested resource creation for type {} with json {}",
				type, definition);
		return entityManager.createResource(type, definition);
	}

	/**
	 * e.g. DELETE /resource-registry/entity/resource/67062c11-9c3a-4906-870d-7df6a43408b0
	 * @param uuid
	 * @return
	 * @throws ResourceNotFoundException
	 * @throws Exception
	 */
	@DELETE
	@Path(EntityPath.RESOURCE_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean deleteResource(@PathParam(ID_PATH_PARAM) String uuid)
			throws ResourceNotFoundException, Exception {
		logger.trace("requested resource deletion for id {}", uuid);
		return entityManager.deleteResource(uuid);
	}

	/* Relations Methods */

	/**
	 * e.g. PUT /resource-registry/entity/consistOf/source/bbf80a93-2284-424a-930c-7ee20021aee1/target/f6931232-c034-4979-9b2f-7193d3fba7df?type=hasCreator&properties={...}
	 * @param resourceUUID
	 * @param facetUUID
	 * @param type
	 * @param properties
	 * @return
	 * @throws FacetNotFoundException
	 * @throws ResourceNotFoundException
	 * @throws ResourceRegistryException
	 */
	@PUT
	@Path(EntityPath.CONSISTS_OF_PATH_PART + "/"
			+ EntityPath.SOURCE_PATH_PART + "/{"
			+ SOURCE_ID_PATH_PARAM + "}/"
			+ EntityPath.TARGET_PATH_PART + "/{"
			+ TARGET_ID_PATH_PARAM + "}")
	public String attachFacet(
			@PathParam(SOURCE_ID_PATH_PARAM) String resourceUUID,
			@PathParam(TARGET_ID_PATH_PARAM) String facetUUID,
			@QueryParam(EntityPath.TYPE_PARAM) String type,
			@QueryParam(EntityPath.PROPERTIES_PARAM) String properties)
			throws FacetNotFoundException, ResourceNotFoundException,
			ResourceRegistryException {
		logger.trace(
				"requested to attach resource {} to facet {} ({} Type {}) with properties {}",
				resourceUUID, facetUUID, ConsistsOf.class.getSimpleName(),
				type, properties);
		return entityManager.attachFacet(resourceUUID, facetUUID, type,
				properties);
	}

	/**
	 *  e.g. DELETE /resource-registry/entity/consistOf/9bff49c8-c0a7-45de-827c-accb71defbd3
	 * @param consistOfUUID
	 * @return
	 * @throws ResourceRegistryException
	 */
	@DELETE
	@Path(EntityPath.CONSISTS_OF_PATH_PART + "/{" + ID_PATH_PARAM
			+ "}")
	public boolean detachFacet(@PathParam(ID_PATH_PARAM) String consistOfUUID)
			throws ResourceRegistryException {
		logger.trace("requested to detach {}", consistOfUUID);
		return entityManager.detachFacet(consistOfUUID);
	}

	/**
	 * e.g. PUT /resource-registry/entity/relatedTo/source/4a81008a-6300-4a32-857f-cebe3f7b2925/target/985f7cf9-b6fa-463b-86c8-84ab0a77deea?type=callsFor&properties={...}
	 * @param sourceResourceUUID
	 * @param targetResourceUUID
	 * @param type
	 * @param properties
	 * @return
	 * @throws ResourceNotFoundException
	 * @throws ResourceRegistryException
	 */
	@PUT
	@Path(EntityPath.IS_RELATED_TO_PATH_PART + "/"
			+ EntityPath.SOURCE_PATH_PART + "/{"
			+ SOURCE_ID_PATH_PARAM + "}/"
			+ EntityPath.TARGET_PATH_PART + "/{"
			+ TARGET_ID_PATH_PARAM + "}")
	public String attachResource(
			@PathParam(SOURCE_ID_PATH_PARAM) String sourceResourceUUID,
			@PathParam(TARGET_ID_PATH_PARAM) String targetResourceUUID,
			@QueryParam(EntityPath.TYPE_PARAM) String type,
			@QueryParam(EntityPath.PROPERTIES_PARAM) String properties)
			throws ResourceNotFoundException, ResourceRegistryException {
		logger.trace(
				"requested to attach source resource {} and target resource {} ({} Type {}) with properties {}",
				sourceResourceUUID, targetResourceUUID,
				IsRelatedTo.class.getSimpleName(), type, properties);
		return entityManager.attachResource(sourceResourceUUID,
				targetResourceUUID, type, properties);
	}

	/**
	 *  e.g. DELETE /resource-registry/entity/relatedTo/b3982715-a7aa-4530-9a5f-2f60008d256e
	 * @param relatedToUUID
	 * @return
	 * @throws ResourceRegistryException
	 */
	@DELETE
	@Path(EntityPath.IS_RELATED_TO_PATH_PART + "/{" + ID_PATH_PARAM
			+ "}")
	public boolean detachResource(@PathParam(ID_PATH_PARAM) String relatedToUUID)
			throws ResourceRegistryException {
		logger.trace("requested to detach {}", relatedToUUID);
		return entityManager.detachResource(relatedToUUID);
	}

}
