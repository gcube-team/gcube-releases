package org.gcube.datacatalogue.catalogue.ws;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.gcube.datacatalogue.catalogue.utils.CatalogueUtils;
import org.gcube.datacatalogue.catalogue.utils.Constants;
import org.gcube.datacatalogue.catalogue.utils.ContextUtils;
import org.gcube.datacatalogue.catalogue.utils.Delegator;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.CatalogueUtilMethods;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanResource;

@Path(Constants.RESOURCES)
/**
 * Resource service endpoint.
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class Resource {
	
	private static final Logger logger = LoggerFactory.getLogger(Resource.class);
	
	private static final String ID_NAME = "id";
	private static final String PACKAGE_ID_NAME = "package_id";
	
	private void applicationChecks(String datasetId, String authorizationErroMessage) throws Exception {
		if(ContextUtils.isApplication()) {
			logger.debug("Application Token Request");
			DataCatalogue dataCatalogue = CatalogueUtils.getCatalogue();
			CkanDataset dataset = dataCatalogue.getDataset(datasetId, CatalogueUtils.fetchSysAPI());
			
			String organization = CatalogueUtilMethods.getCKANOrganization();
			if(organization.equalsIgnoreCase(dataset.getOrganization().getName())
					&& ContextUtils.getUsername().equals(dataset.getAuthor())) {
				return;
			} 
			throw new Exception(authorizationErroMessage);
		}
	}
	
	@GET
	@Path(Constants.SHOW_METHOD)
	@Produces(MediaType.APPLICATION_JSON)
	public String show(@Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.resource_show
		try {
			String resourceId = CatalogueUtils.getIdFromUriInfo(ID_NAME, uriInfo);
			CkanResource resource = CatalogueUtils.getCatalogue().getResource(resourceId, CatalogueUtils.fetchSysAPI());
			applicationChecks(resource.getPackageId(), "You are not authorized to access this resource");
		} catch(Exception e) {
			logger.error("", e);
			return CatalogueUtils.createJSONOnFailure(e.toString());
		}
		return Delegator.delegateGet(Constants.RESOURCE_SHOW, uriInfo);
	}
	
	@POST
	@Path(Constants.CREATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.resource_create
		try {
			String datasetId = CatalogueUtils.getIdFromJSONString(PACKAGE_ID_NAME, json);
			applicationChecks(datasetId, "You cannot add a resource to this item");
		} catch(Exception e) {
			logger.error("", e);
			if(e instanceof ParseException) {
				return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
			} else {
				return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}
		return Delegator.delegatePost(Constants.RESOURCE_CREATE, json, uriInfo);
	}
	
	@POST
	@Path(Constants.CREATE_METHOD)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(FormDataMultiPart multiPart, @Context UriInfo uriInfo,
			@Context final HttpServletRequest request) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.resource_create
		try {
			String datasetId = (String) multiPart.getField(PACKAGE_ID_NAME).getValue(); // within the resource it is defined this way
			if(datasetId == null || datasetId.isEmpty()) {
				throw new Exception("'" + PACKAGE_ID_NAME +"' field is missing!");
			}
			applicationChecks(datasetId, "You cannot add a resource to this item");
		} catch(Exception e) {
			logger.error("", e);
			return CatalogueUtils.createJSONOnFailure(e.toString());
		}
		return Delegator.delegatePost(Constants.RESOURCE_CREATE, multiPart, uriInfo);
	}
	
	@DELETE
	@Path(Constants.DELETE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.resource_delete
		try {
			String resourceId = CatalogueUtils.getIdFromJSONString(ID_NAME, json);
			CkanResource resource = CatalogueUtils.getCatalogue().getResource(resourceId, CatalogueUtils.fetchSysAPI());
			applicationChecks(resource.getPackageId(), "You cannot delete this resource");
		} catch(Exception e) {
			logger.error("", e);
			if(e instanceof ParseException) {
				return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
			} else {
				return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}
		return Delegator.delegatePost(Constants.RESOURCE_DELETE, json, uriInfo);
	}
	
	@POST
	@Path(Constants.UPDATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String update(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.resource_update
		try {
			String resourceId = CatalogueUtils.getIdFromJSONString(ID_NAME, json);
			CkanResource resource = CatalogueUtils.getCatalogue().getResource(resourceId, CatalogueUtils.fetchSysAPI());
			applicationChecks(resource.getPackageId(), "You cannot update this resource");
		} catch(Exception e) {
			logger.error("", e);
			if(e instanceof ParseException) {
				return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
			} else {
				return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}
		return Delegator.delegatePost(Constants.RESOURCE_UPDATE, json, uriInfo);
	}
	
	@POST
	@Path(Constants.PATCH_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String patch(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.patch.resource_patch
		try {
			String resourceId = CatalogueUtils.getIdFromJSONString(ID_NAME, json);
			CkanResource resource = CatalogueUtils.getCatalogue().getResource(resourceId, CatalogueUtils.fetchSysAPI());
			applicationChecks(resource.getPackageId(), "You cannot patch this resource");
		} catch(Exception e) {
			logger.error("", e);
			if(e instanceof ParseException) {
				return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
			} else {
				return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}
		return Delegator.delegatePost(Constants.RESOURCE_PATCH, json, uriInfo);
	}
	
}
