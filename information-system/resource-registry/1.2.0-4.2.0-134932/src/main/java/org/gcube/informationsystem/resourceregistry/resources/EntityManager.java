package org.gcube.informationsystem.resourceregistry.resources;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.EntityManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.EntityException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.EntityPath;
import org.gcube.informationsystem.resourceregistry.resources.impl.EntityManagementImpl;
import org.gcube.informationsystem.resourceregistry.resources.utils.ContextUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 * @author Lucio Lelii (ISTI - CNR)
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
	 * e.g. PUT /resource-registry/entity/facet/ContactFacet
	 * 
	 * BODY: {...}
	 * 
	 * @param type
	 * @param definition
	 * @return
	 * @throws EntityException
	 * @throws ResourceRegistryException
	 */
	@PUT
	@Path(EntityPath.FACET_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public String createFacet(@PathParam(TYPE_PATH_PARAM) String type,
			String json) throws EntityException, ResourceRegistryException {
		logger.info("requested facet creation for type {} defined by {} ",
				type, json);
		return entityManager.createFacet(type, json);
	}

	/**
	 * e.g. POST /resource-registry/entity/facet/4023d5b2-8601-47a5-
	 * 83ef-49ffcbfc7d86
	 * 
	 * BODY: {...}
	 * 
	 * @param uuid
	 * @param definition
	 * @return
	 * @throws FacetNotFoundException
	 * @throws ResourceRegistryException
	 */
	@POST
	@Path(EntityPath.FACET_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public String updateFacet(@PathParam(ID_PATH_PARAM) String uuid, String json)
			throws FacetNotFoundException, ResourceRegistryException {
		logger.info("requested facet update for id {} with {}", uuid, json);
		return entityManager.updateFacet(UUID.fromString(uuid), json);
	}

	/**
	 * e.g. DELETE
	 * /resource-registry/entity/facet/4023d5b2-8601-47a5-83ef-49ffcbfc7d86
	 * 
	 * @param uuid
	 * @return
	 * @throws FacetNotFoundException
	 * @throws ResourceRegistryException
	 */
	@DELETE
	@Path(EntityPath.FACET_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean deleteFacet(@PathParam(ID_PATH_PARAM) String uuid)
			throws FacetNotFoundException, ResourceRegistryException {
		logger.info("Requested to delete Facet with id {}", uuid);
		return entityManager.deleteFacet(UUID.fromString(uuid));
	}

	/* Resources Methods */

	/**
	 * e.g. PUT /resource-registry/entity/resource/HostingNode
	 * 
	 * BODY: {...}
	 * 
	 * @param type
	 * @param definition
	 * @return
	 * @throws FacetNotFoundException
	 * @throws ResourceRegistryException
	 */
	@PUT
	@Path(EntityPath.RESOURCE_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public String createResource(@PathParam(TYPE_PATH_PARAM) String type,
			String json) throws FacetNotFoundException,
			ResourceRegistryException {
		logger.info("requested resource creation for type {} with json {}",
				type, json);
		return entityManager.createResource(type, json);
	}

	@POST
	@Path(EntityPath.RESOURCE_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public String updateResource(@PathParam(ID_PATH_PARAM) String uuid,
			String json) throws FacetNotFoundException,
			ResourceRegistryException {
		logger.info("requested resource update for id {} with {}", uuid, json);
		return entityManager.updateResource(UUID.fromString(uuid), json);
	}
	
	
	/**
	 * e.g. DELETE
	 * /resource-registry/entity/resource/67062c11-9c3a-4906-870d-7df6a43408b0
	 * 
	 * @param uuid
	 * @return
	 * @throws ResourceNotFoundException
	 * @throws Exception
	 */
	@DELETE
	@Path(EntityPath.RESOURCE_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean deleteResource(@PathParam(ID_PATH_PARAM) String uuid)
			throws ResourceNotFoundException, Exception {
		logger.info("requested resource deletion for id {}", uuid);
		return entityManager.deleteResource(UUID.fromString(uuid));
	}

	/* Relations Methods */

	/**
	 * e.g. PUT
	 * /resource-registry/entity/consistOf/source/bbf80a93-2284-424a-930c-
	 * 7ee20021aee1/target/f6931232-c034-4979-9b2f-7193d3fba7df/hasCreator
	 * 
	 * BODY: {}
	 * 
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
	@Path(EntityPath.CONSISTS_OF_PATH_PART + "/" + EntityPath.SOURCE_PATH_PART
			+ "/{" + SOURCE_ID_PATH_PARAM + "}/" + EntityPath.TARGET_PATH_PART
			+ "/{" + TARGET_ID_PATH_PARAM + "}/{" + TYPE_PATH_PARAM + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String attachFacet(
			@PathParam(SOURCE_ID_PATH_PARAM) String resourceUUID,
			@PathParam(TARGET_ID_PATH_PARAM) String facetUUID,
			@PathParam(TYPE_PATH_PARAM) String type, String json)
			throws FacetNotFoundException, ResourceNotFoundException,
			ResourceRegistryException {
		logger.info(
				"requested to attach resource {} to facet {} ({} Type {}) with properties {}",
				resourceUUID, facetUUID, ConsistsOf.class.getSimpleName(),
				type, json);
		return entityManager.attachFacet(UUID.fromString(resourceUUID),
				UUID.fromString(facetUUID), type, json);
	}

	/**
	 * e.g. DELETE /resource-registry/entity/consistOf/9bff49c8-c0a7-45de-827c-
	 * accb71defbd3
	 * 
	 * @param consistOfUUID
	 * @return
	 * @throws ResourceRegistryException
	 */
	@DELETE
	@Path(EntityPath.CONSISTS_OF_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean detachFacet(@PathParam(ID_PATH_PARAM) String consistOfUUID)
			throws ResourceRegistryException {
		logger.info("requested to detach {}", consistOfUUID);
		return entityManager.detachFacet(UUID.fromString(consistOfUUID));
	}

	/**
	 * e.g. PUT
	 * /resource-registry/entity/relatedTo/source/4a81008a-6300-4a32-857f
	 * -cebe3f7b2925/target/985f7cf9-b6fa-463b-86c8-84ab0a77deea/callsFor
	 * 
	 * BODY: {...}
	 * 
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
			+ EntityPath.SOURCE_PATH_PART + "/{" + SOURCE_ID_PATH_PARAM + "}/"
			+ EntityPath.TARGET_PATH_PART + "/{" + TARGET_ID_PATH_PARAM + "}/{"
			+ TYPE_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public String attachResource(
			@PathParam(SOURCE_ID_PATH_PARAM) String sourceResourceUUID,
			@PathParam(TARGET_ID_PATH_PARAM) String targetResourceUUID,
			@PathParam(TYPE_PATH_PARAM) String type, String json)
			throws ResourceNotFoundException, ResourceRegistryException {
		logger.info(
				"requested to attach source {} {} and target {} {} ({} Type {}) with properties {}",
				Resource.NAME, sourceResourceUUID, Resource.NAME,
				targetResourceUUID, IsRelatedTo.class.getSimpleName(), type,
				json);
		return entityManager.attachResource(
				UUID.fromString(sourceResourceUUID),
				UUID.fromString(targetResourceUUID), type, json);
	}

	/**
	 * e.g. DELETE
	 * /resource-registry/entity/relatedTo/b3982715-a7aa-4530-9a5f-2f60008d256e
	 * 
	 * @param relatedToUUID
	 * @return
	 * @throws ResourceRegistryException
	 */
	@DELETE
	@Path(EntityPath.IS_RELATED_TO_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean detachResource(@PathParam(ID_PATH_PARAM) String relatedToUUID)
			throws ResourceRegistryException {
		logger.info("requested to detach {}", relatedToUUID);
		return entityManager.detachResource(UUID.fromString(relatedToUUID));
	}

	/**
	 * e.g POST
	 * /resource-registry/add/resource/67062c11-9c3a-4906-870d-7df6a43408b0
	 * 
	 * @param uuid
	 * @return
	 * @throws ResourceNotFoundException
	 * @throws ContextNotFoundException
	 * @throws ResourceRegistryException
	 */
	@POST
	@Path(EntityPath.ADD_PATH_PART + "/" + EntityPath.RESOURCE_PATH_PART + "/{"
			+ ID_PATH_PARAM + "}")
	public boolean addResourceToContext(@PathParam(ID_PATH_PARAM) String uuid)
			throws ResourceNotFoundException, ContextNotFoundException,
			ResourceRegistryException {
		logger.info("requested to add {} with UUID {} to current context {}",
				Resource.NAME, uuid, ContextUtility.getCurrentContext());
		return entityManager.addResourceToContext(UUID.fromString(uuid));
	}

	/**
	 * e.g POST
	 * /resource-registry/add/facet/f6931232-c034-4979-9b2f-7193d3fba7df
	 * 
	 * @param uuid
	 * @return
	 * @throws FacetNotFoundException
	 * @throws ContextNotFoundException
	 * @throws ResourceRegistryException
	 */
	@POST
	@Path(EntityPath.ADD_PATH_PART + "/" + EntityPath.FACET_PATH_PART + "/{"
			+ ID_PATH_PARAM + "}")
	public boolean addFacetToContext(@PathParam(ID_PATH_PARAM) String uuid)
			throws FacetNotFoundException, ContextNotFoundException,
			ResourceRegistryException {
		logger.info("requested to add {} with UUID {} to current context {}",
				Facet.NAME, uuid, ContextUtility.getCurrentContext());
		return entityManager.addFacetToContext(UUID.fromString(uuid));
	}

}
