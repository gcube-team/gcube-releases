package org.gcube.informationsystem.resourceregistry.rest;

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
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.ERPath;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.er.entity.FacetManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.ResourceManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.ConsistsOfManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.IsRelatedToManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 * @author Lucio Lelii (ISTI - CNR)
 */
@Path(ERPath.ER_PATH_PART)
public class ERManager {

	private static Logger logger = LoggerFactory.getLogger(SchemaManager.class);

	public static final String ID_PATH_PARAM = "id";
	public static final String TYPE_PATH_PARAM = "type";

	public static final String SOURCE_ID_PATH_PARAM = "sourceId";
	public static final String TARGET_ID_PATH_PARAM = "targetId";

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
	@Path(ERPath.FACET_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public String createFacet(@PathParam(TYPE_PATH_PARAM) String type,
			String json) throws FacetAlreadyPresentException, ResourceRegistryException {
		logger.info("requested facet creation for type {} defined by {} ",
				type, json);
		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setElementType(type);
		facetManagement.setJSON(json);
		return facetManagement.create();
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
	@Path(ERPath.FACET_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public String updateFacet(@PathParam(ID_PATH_PARAM) String uuid, String json)
			throws FacetNotFoundException, ResourceRegistryException {
		logger.info("requested facet update for id {} with {}", uuid, json);
		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setUUID(UUID.fromString(uuid));
		facetManagement.setJSON(json);
		return facetManagement.update();
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
	@Path(ERPath.FACET_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean deleteFacet(@PathParam(ID_PATH_PARAM) String uuid)
			throws FacetNotFoundException, ResourceRegistryException {
		logger.info("Requested to delete Facet with id {}", uuid);
		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setUUID(UUID.fromString(uuid));
		return facetManagement.delete();
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
	@Path(ERPath.RESOURCE_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public String createResource(@PathParam(TYPE_PATH_PARAM) String type,
			String json) throws ResourceAlreadyPresentException, ResourceRegistryException {
		logger.info("requested resource creation for type {} with json {}",
				type, json);
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setElementType(type);
		resourceManagement.setJSON(json);
		return resourceManagement.create();
	}

	@POST
	@Path(ERPath.RESOURCE_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	@Produces(MediaType.APPLICATION_JSON)
	public String updateResource(@PathParam(ID_PATH_PARAM) String uuid,
			String json) throws ResourceNotFoundException,
			ResourceRegistryException {
		logger.info("requested resource update for id {} with {}", uuid, json);
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(UUID.fromString(uuid));
		resourceManagement.setJSON(json);
		return resourceManagement.update();
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
	@Path(ERPath.RESOURCE_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean deleteResource(@PathParam(ID_PATH_PARAM) String uuid)
			throws ResourceNotFoundException, Exception {
		logger.info("requested resource deletion for id {}", uuid);
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(UUID.fromString(uuid));
		return resourceManagement.delete();
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
	@Path(ERPath.CONSISTS_OF_PATH_PART + "/" + ERPath.SOURCE_PATH_PART
			+ "/{" + SOURCE_ID_PATH_PARAM + "}/" + ERPath.TARGET_PATH_PART
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
		ConsistsOfManagement consistsOfManagement = new ConsistsOfManagement();
		consistsOfManagement.setElementType(type);
		consistsOfManagement.setJSON(json);
		return consistsOfManagement.create(UUID.fromString(resourceUUID),
				UUID.fromString(facetUUID));
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
	@Path(ERPath.CONSISTS_OF_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean detachFacet(@PathParam(ID_PATH_PARAM) String consistOfUUID)
			throws ResourceRegistryException {
		logger.info("requested to detach {}", consistOfUUID);
		ConsistsOfManagement consistsOfManagement = new ConsistsOfManagement();
		consistsOfManagement.setUUID(UUID.fromString(consistOfUUID));
		return consistsOfManagement.delete();
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
	@Path(ERPath.IS_RELATED_TO_PATH_PART + "/"
			+ ERPath.SOURCE_PATH_PART + "/{" + SOURCE_ID_PATH_PARAM + "}/"
			+ ERPath.TARGET_PATH_PART + "/{" + TARGET_ID_PATH_PARAM + "}/{"
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
		
		IsRelatedToManagement isRelatedToManagement = new IsRelatedToManagement();
		isRelatedToManagement.setElementType(type);
		isRelatedToManagement.setJSON(json);
		
		return isRelatedToManagement.create(
				UUID.fromString(sourceResourceUUID),
				UUID.fromString(targetResourceUUID));
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
	@Path(ERPath.IS_RELATED_TO_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean detachResource(@PathParam(ID_PATH_PARAM) String relatedToUUID)
			throws ResourceRegistryException {
		logger.info("requested to detach {}", relatedToUUID);
		IsRelatedToManagement isRelatedToManagement = new IsRelatedToManagement();
		isRelatedToManagement.setUUID(UUID.fromString(relatedToUUID));
		return isRelatedToManagement.delete();
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
	@Path(ERPath.ADD_PATH_PART + "/" + ERPath.RESOURCE_PATH_PART + "/{"
			+ ID_PATH_PARAM + "}")
	public boolean addResourceToContext(@PathParam(ID_PATH_PARAM) String uuid)
			throws ResourceNotFoundException, ContextNotFoundException,
			ResourceRegistryException {
		logger.info("requested to add {} with UUID {} to current context {}",
				Resource.NAME, uuid, ContextUtility.getCurrentContext());
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(UUID.fromString(uuid));
		return resourceManagement.addToContext();
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
	@Path(ERPath.ADD_PATH_PART + "/" + ERPath.FACET_PATH_PART + "/{"
			+ ID_PATH_PARAM + "}")
	public boolean addFacetToContext(@PathParam(ID_PATH_PARAM) String uuid)
			throws FacetNotFoundException, ContextNotFoundException,
			ResourceRegistryException {
		logger.info("requested to add {} with UUID {} to current context {}",
				Facet.NAME, uuid, ContextUtility.getCurrentContext());
		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setUUID(UUID.fromString(uuid));
		return facetManagement.addToContext(); 
	}
	
	/**
	 * e.g POST
	 * /resource-registry/remove/resource/67062c11-9c3a-4906-870d-7df6a43408b0
	 * 
	 * @param uuid
	 * @return
	 * @throws ResourceNotFoundException
	 * @throws ContextNotFoundException
	 * @throws ResourceRegistryException
	 */
	@POST
	@Path(ERPath.REMOVE_PATH_PART + "/" + ERPath.RESOURCE_PATH_PART + "/{"
			+ ID_PATH_PARAM + "}")
	public boolean removeResourceFromContext(@PathParam(ID_PATH_PARAM) String uuid)
			throws ResourceNotFoundException, ContextNotFoundException,
			ResourceRegistryException {
		logger.info("requested to remove {} with UUID {} from current context {}",
				Resource.NAME, uuid, ContextUtility.getCurrentContext());
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(UUID.fromString(uuid));
		return resourceManagement.removeFromContext();
	}

	/**
	 * e.g POST
	 * /resource-registry/remove/facet/f6931232-c034-4979-9b2f-7193d3fba7df
	 * 
	 * @param uuid
	 * @return
	 * @throws FacetNotFoundException
	 * @throws ContextNotFoundException
	 * @throws ResourceRegistryException
	 */
	@POST
	@Path(ERPath.REMOVE_PATH_PART + "/" + ERPath.FACET_PATH_PART + "/{"
			+ ID_PATH_PARAM + "}")
	public boolean removeFacetFromContext(@PathParam(ID_PATH_PARAM) String uuid)
			throws FacetNotFoundException, ContextNotFoundException,
			ResourceRegistryException {
		logger.info("requested to remove {} with UUID {} from current context {}",
				Facet.NAME, uuid, ContextUtility.getCurrentContext());
		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setUUID(UUID.fromString(uuid));
		return facetManagement.removeFromContext(); 
	}
	

}
