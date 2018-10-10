package org.gcube.datacatalogue.catalogue.ws;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanOrganization;

@Path(Constants.ORGANIZATIONS)
/**
 * Organizations service endpoint.
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class Organization {
	
	private static final Logger logger = LoggerFactory.getLogger(Organization.class);
	
	private static final String ID_NAME = "id";
	
	private void applicationChecks(String organizationId, String authorizationErroMessage) throws Exception {
		if(ContextUtils.isApplication()) {
			logger.debug("Application Token Request");
			DataCatalogue dataCatalogue = CatalogueUtils.getCatalogue();
			String organization = CatalogueUtilMethods.getCKANOrganization();
			
			CkanOrganization fetchedOrganization = dataCatalogue.getOrganizationByName(organizationId);
			
			if(organization.equalsIgnoreCase(fetchedOrganization.getName())) {
				return;
			}
			 
			throw new Exception(authorizationErroMessage);
		}
	}
	
	@GET
	@Path(Constants.SHOW_METHOD)
	@Produces(MediaType.APPLICATION_JSON)
	public String show(@Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.organization_show
		try {
			String organizationId = CatalogueUtils.getIdFromUriInfo(ID_NAME, uriInfo);
			applicationChecks(organizationId, "You are not authorized to access this organization");
		} catch(Exception e) {
			logger.error("", e);
			return CatalogueUtils.createJSONOnFailure(e.toString());
		}
		return Delegator.delegateGet(Constants.ORGANIZATION_SHOW, uriInfo);
	}
	
	@GET
	@Path(Constants.LIST_METHOD)
	@Produces(MediaType.APPLICATION_JSON)
	public String organizationList(@Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.organization_list
		return Delegator.delegateGet(Constants.ORGANIZATION_LIST, uriInfo);
		
	}
	
	@POST
	@Path(Constants.CREATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.organization_create
		return Delegator.delegatePost(Constants.ORGANIZATION_CREATE, json, uriInfo);
	}
	
	@DELETE
	@Path(Constants.DELETE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.organization_delete
		return Delegator.delegatePost(Constants.ORGANIZATION_DELETE, json, uriInfo);
	}
	
	@DELETE
	@Path(Constants.PURGE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String purge(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.organization_purge
		return Delegator.delegatePost(Constants.ORGANIZATION_PURGE, json, uriInfo);
	}
	
	@POST
	@Path(Constants.UPDATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String update(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.organization_update
		return Delegator.delegatePost(Constants.ORGANIZATION_UPDATE, json, uriInfo);
		
	}
	
	@POST
	@Path(Constants.PATCH_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String patch(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.patch.organization_patch
		return Delegator.delegatePost(Constants.ORGANIZATION_PATCH, json, uriInfo);
	}
	
}
