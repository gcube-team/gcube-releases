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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.ResourceInitializer;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.ERPath;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall.HTTPMETHOD;
import org.gcube.informationsystem.resourceregistry.context.ContextUtility;
import org.gcube.informationsystem.resourceregistry.er.entity.FacetManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.ResourceManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.ConsistsOfManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.IsRelatedToManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Path(ERPath.ER_PATH_PART)
public class ERManager {

	private static Logger logger = LoggerFactory.getLogger(ERManager.class);

	public static final String ID_PATH_PARAM = "id";
	public static final String TYPE_PATH_PARAM = "type";

	/**
	 * e.g. PUT /resource-registry/er/facet/ContactFacet
	 * 
	 * BODY: {...}
	 * 
	 */
	@PUT
	@Path(ERPath.FACET_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8 })
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public Response createFacet(@PathParam(TYPE_PATH_PARAM) String type, String json)
			throws FacetAlreadyPresentException, ResourceRegistryException {
		CalledMethodProvider.instance.set(
				HTTPMETHOD.PUT.name() + " /" + ERPath.ER_PATH_PART + 
				"/" + ERPath.FACET_PATH_PART + "/" + type);
		logger.info("requested facet creation for type {}", type);
		logger.trace("requested facet creation for type {} defined by {} ", type, json);
		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setElementType(type);
		facetManagement.setJSON(json);
		String ret = facetManagement.create();
		return Response.status(Status.CREATED).entity(ret).type(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
				.build();
	}

	/**
	 * e.g. POST /resource-registry/er/facet/4023d5b2-8601-47a5-83ef-49ffcbfc7d86
	 * 
	 * BODY: {...}
	 * 
	 */
	@POST
	@Path(ERPath.FACET_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8 })
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String updateFacet(@PathParam(ID_PATH_PARAM) String uuid, String json)
			throws FacetNotFoundException, ResourceRegistryException {
		CalledMethodProvider.instance.set(
				HTTPMETHOD.POST.name() + " /" + ERPath.ER_PATH_PART + 
				"/" + ERPath.FACET_PATH_PART + "/{" + ID_PATH_PARAM + "}");
		logger.info("requested facet update for id {}", uuid);
		logger.trace("requested facet update for id {} with {}", uuid, json);
		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setUUID(UUID.fromString(uuid));
		facetManagement.setJSON(json);
		return facetManagement.update();
	}

	/**
	 * e.g. DELETE	/resource-registry/er/facet/4023d5b2-8601-47a5-83ef-49ffcbfc7d86
	 * 
	 */
	@DELETE
	@Path(ERPath.FACET_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean deleteFacet(@PathParam(ID_PATH_PARAM) String uuid)
			throws FacetNotFoundException, ResourceRegistryException {
		CalledMethodProvider.instance.set(
				HTTPMETHOD.DELETE.name() + " /" + ERPath.ER_PATH_PART + 
				"/"	+ ERPath.FACET_PATH_PART + "/{" + ID_PATH_PARAM + "}");
		logger.info("Requested to delete Facet with id {}", uuid);
		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setUUID(UUID.fromString(uuid));
		return facetManagement.delete();
	}

	/* Resources Methods */

	/**
	 * e.g. PUT /resource-registry/er/resource/HostingNode
	 * 
	 * BODY: {...}
	 * 
	 */
	@PUT
	@Path(ERPath.RESOURCE_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8 })
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public Response createResource(@PathParam(TYPE_PATH_PARAM) String type, String json)
			throws ResourceAlreadyPresentException, ResourceRegistryException {
		CalledMethodProvider.instance.set(
				HTTPMETHOD.PUT.name() + " /" + ERPath.ER_PATH_PART + 
				"/" + ERPath.RESOURCE_PATH_PART + "/" + type);
		logger.info("requested resource creation for type {}", type);
		logger.trace("requested resource creation for type {} with json {}", type, json);
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setElementType(type);
		resourceManagement.setJSON(json);
		String ret = resourceManagement.create();
		return Response.status(Status.CREATED).entity(ret).type(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
				.build();
	}

	/**
	 * e.g. POST /resource-registry/er/resource/67062c11-9c3a-4906-870d-7df6a43408b0
	 * 
	 * BODY: {...}
	 * 
	 */
	@POST
	@Path(ERPath.RESOURCE_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8 })
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String updateResource(@PathParam(ID_PATH_PARAM) String uuid, String json)
			throws ResourceNotFoundException, ResourceRegistryException {
		CalledMethodProvider.instance.set(
				HTTPMETHOD.POST.name() + " /" + ERPath.ER_PATH_PART + 
				"/" + ERPath.RESOURCE_PATH_PART + "/{" + ID_PATH_PARAM + "}");
		logger.info("requested resource update for id {}", uuid);
		logger.trace("requested resource update for id {} with {}", uuid, json);
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(UUID.fromString(uuid));
		resourceManagement.setJSON(json);
		return resourceManagement.update();
	}

	/**
	 * e.g. DELETE /resource-registry/er/resource/67062c11-9c3a-4906-870d-7df6a43408b0
	 * 
	 */
	@DELETE
	@Path(ERPath.RESOURCE_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean deleteResource(@PathParam(ID_PATH_PARAM) String uuid) throws ResourceNotFoundException, Exception {
		CalledMethodProvider.instance.set(
				HTTPMETHOD.DELETE.name() + " /" + ERPath.ER_PATH_PART + 
				"/"	+ ERPath.RESOURCE_PATH_PART + "/{" + ID_PATH_PARAM + "}");
		logger.info("requested resource deletion for id {}", uuid);
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(UUID.fromString(uuid));
		return resourceManagement.delete();
	}


	/**
	 * e.g. PUT /resource-registry/er/consistsOf/IsIdentifiedBy
	 * 
	 * BODY: {...}
	 * 
	 */
	@PUT
	@Path(ERPath.CONSISTS_OF_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8 })
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public Response createConsistsOf(@PathParam(TYPE_PATH_PARAM) String type, String json)
			throws ResourceAlreadyPresentException, ResourceRegistryException {
		CalledMethodProvider.instance.set(
				HTTPMETHOD.PUT.name() + " /" + ERPath.ER_PATH_PART + 
				"/" + ERPath.CONSISTS_OF_PATH_PART + "/" + type);
		logger.info("Requested to create {} {} of type {}", ConsistsOf.NAME, Relation.NAME, type);
		logger.trace("Requested to create {} {} of type {} : {}", ConsistsOf.NAME, Relation.NAME, type, json);
		ConsistsOfManagement consistsOfManagement = new ConsistsOfManagement();
		consistsOfManagement.setElementType(type);
		consistsOfManagement.setJSON(json);
		String ret = consistsOfManagement.create();
		return Response.status(Status.CREATED).entity(ret).type(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
				.build();
	}

	/**
	 * e.g. DELETE /resource-registry/er/consistsOf/9bff49c8-c0a7-45de-827c-accb71defbd3
	 * 
	 */
	@DELETE
	@Path(ERPath.CONSISTS_OF_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean detachFacet(@PathParam(ID_PATH_PARAM) String consistOfUUID) throws ResourceRegistryException {
		CalledMethodProvider.instance.set(
				HTTPMETHOD.DELETE.name() + " /" + ERPath.ER_PATH_PART + 
				"/" + ERPath.CONSISTS_OF_PATH_PART + "/{" + ID_PATH_PARAM + "}");
		logger.info("requested to detach {}", consistOfUUID);
		ConsistsOfManagement consistsOfManagement = new ConsistsOfManagement();
		consistsOfManagement.setUUID(UUID.fromString(consistOfUUID));
		return consistsOfManagement.delete();
	}

	/**
	 * e.g. PUT /resource-registry/er/isRelatedTo/Hosts
	 * 
	 * BODY: {...}
	 * 
	 */
	@PUT
	@Path(ERPath.IS_RELATED_TO_PATH_PART + "/{" + TYPE_PATH_PARAM + "}")
	@Consumes({ MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8 })
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public Response createIsRelatedTo(@PathParam(TYPE_PATH_PARAM) String type, String json)
			throws ResourceAlreadyPresentException, ResourceRegistryException {
		CalledMethodProvider.instance.set(
				HTTPMETHOD.PUT.name() + " /" + ERPath.ER_PATH_PART + 
				"/" + ERPath.IS_RELATED_TO_PATH_PART + "/" + type);
		logger.info("Requested to create {} {} of type {} : {}", IsRelatedTo.NAME, Relation.NAME, type, json);
		IsRelatedToManagement isRelatedToManagement = new IsRelatedToManagement();
		isRelatedToManagement.setElementType(type);
		isRelatedToManagement.setJSON(json);
		String ret = isRelatedToManagement.create();
		return Response.status(Status.CREATED).entity(ret).type(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
				.build();
	}

	/**
	 * e.g. DELETE /resource-registry/er/isRelatedTo/b3982715-a7aa-4530-9a5f-2f60008d256e
	 * 
	 */
	@DELETE
	@Path(ERPath.IS_RELATED_TO_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean detachResource(@PathParam(ID_PATH_PARAM) String relatedToUUID) throws ResourceRegistryException {
		CalledMethodProvider.instance.set(
				HTTPMETHOD.DELETE.name() + " /" + ERPath.ER_PATH_PART + 
				"/" + ERPath.IS_RELATED_TO_PATH_PART + "/{" + ID_PATH_PARAM + "}");
		logger.info("requested to detach {}", relatedToUUID);
		IsRelatedToManagement isRelatedToManagement = new IsRelatedToManagement();
		isRelatedToManagement.setUUID(UUID.fromString(relatedToUUID));
		return isRelatedToManagement.delete();
	}

	/**
	 * e.g POST /resource-registry/er/add/resource/67062c11-9c3a-4906-870d-7df6a43408b0
	 * 
	 */
	@POST
	@Path(ERPath.ADD_PATH_PART + "/" + ERPath.RESOURCE_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean addResourceToContext(@PathParam(ID_PATH_PARAM) String uuid)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		CalledMethodProvider.instance.set(
				HTTPMETHOD.POST.name() + " /" + ERPath.ER_PATH_PART + 
				"/" + ERPath.ADD_PATH_PART + "/" + ERPath.RESOURCE_PATH_PART + "/{" + ID_PATH_PARAM + "}");
		logger.info("requested to add {} with UUID {} to current context {}", Resource.NAME, uuid,
				ContextUtility.getCurrentContext());
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(UUID.fromString(uuid));
		return resourceManagement.addToContext();
	}

	/**
	 * e.g POST /resource-registry/er/add/facet/f6931232-c034-4979-9b2f-7193d3fba7df
	 * 
	 */
	@POST
	@Path(ERPath.ADD_PATH_PART + "/" + ERPath.FACET_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean addFacetToContext(@PathParam(ID_PATH_PARAM) String uuid)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException {
		CalledMethodProvider.instance.set(
				HTTPMETHOD.POST.name() + " /" + ERPath.ER_PATH_PART + 
				"/" + ERPath.ADD_PATH_PART + "/" + ERPath.FACET_PATH_PART + "/{" + ID_PATH_PARAM + "}");
		logger.info("requested to add {} with UUID {} to current context {}", Facet.NAME, uuid,
				ContextUtility.getCurrentContext());
		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setUUID(UUID.fromString(uuid));
		return facetManagement.addToContext();
	}

	/**
	 * e.g POST /resource-registry/er/remove/resource/67062c11-9c3a-4906-870d-7df6a43408b0
	 * 
	 */
	@POST
	@Path(ERPath.REMOVE_PATH_PART + "/" + ERPath.RESOURCE_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean removeResourceFromContext(@PathParam(ID_PATH_PARAM) String uuid)
			throws ResourceNotFoundException, ContextNotFoundException, ResourceRegistryException {
		CalledMethodProvider.instance.set(
				HTTPMETHOD.POST.name() + " /" + ERPath.ER_PATH_PART + 
				"/" + ERPath.REMOVE_PATH_PART + "/" + ERPath.RESOURCE_PATH_PART + "/{" + ID_PATH_PARAM + "}");
		logger.info("requested to remove {} with UUID {} from current context {}", Resource.NAME, uuid,
				ContextUtility.getCurrentContext());
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(UUID.fromString(uuid));
		return resourceManagement.removeFromContext();
	}

	/**
	 * e.g POST /resource-registry/er/remove/facet/f6931232-c034-4979-9b2f-7193d3fba7df
	 * 
	 */
	@POST
	@Path(ERPath.REMOVE_PATH_PART + "/" + ERPath.FACET_PATH_PART + "/{" + ID_PATH_PARAM + "}")
	public boolean removeFacetFromContext(@PathParam(ID_PATH_PARAM) String uuid)
			throws FacetNotFoundException, ContextNotFoundException, ResourceRegistryException {
		CalledMethodProvider.instance.set(
				HTTPMETHOD.POST.name() + " /" + ERPath.ER_PATH_PART + 
				"/" + ERPath.REMOVE_PATH_PART + "/" + ERPath.FACET_PATH_PART + "/{" + ID_PATH_PARAM + "}");
		logger.info("requested to remove {} with UUID {} from current context {}", Facet.NAME, uuid,
				ContextUtility.getCurrentContext());
		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setUUID(UUID.fromString(uuid));
		return facetManagement.removeFromContext();
	}

}
