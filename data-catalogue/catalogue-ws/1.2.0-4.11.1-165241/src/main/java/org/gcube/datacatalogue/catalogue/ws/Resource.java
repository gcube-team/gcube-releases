package org.gcube.datacatalogue.catalogue.ws;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datacatalogue.catalogue.utils.CatalogueUtils;
import org.gcube.datacatalogue.catalogue.utils.Constants;
import org.gcube.datacatalogue.catalogue.utils.Delegator;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.CatalogueUtilMethods;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanResource;

@Path(Constants.RESOURCES)
/**
 * Resource service endpoint.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Resource {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Resource.class);

	@GET
	@Path(Constants.SHOW_METHOD)
	@Produces(MediaType.APPLICATION_JSON)
	public String show(@Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.resource_show
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		String username = caller.getClient().getId();
		boolean isApplication = CatalogueUtils.isApplicationToken(caller);

		if(isApplication){
			try{
				DataCatalogue utils = CatalogueUtils.getCatalogue();
				String organization =  CatalogueUtilMethods.getOrganizationNameFromScope(context);
				String resourceId = null;

				MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(false);
				List<String> ids = queryParams.get("id"); 

				if(ids == null || ids.isEmpty())
					throw new Exception("'id' field is missing!");

				CkanResource resource = utils.getResource(resourceId, CatalogueUtils.fetchSysAPI(context));
				CkanDataset item = utils.getDataset(resource.getPackageId(), CatalogueUtils.fetchSysAPI(context));

				if(organization.equalsIgnoreCase(item.getOrganization().getName()) && username.equals(item.getAuthor())){
					return Delegator.delegateGet(caller, context, Constants.RESOURCE_SHOW, uriInfo, true);
				}else
					throw new Exception("You are not authorized to access this resource");
			} catch (Exception e) {
				return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}

		return Delegator.delegateGet(caller, context, Constants.RESOURCE_SHOW, uriInfo, isApplication);

	}

	@POST
	@Path(Constants.CREATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.resource_create
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		String username = caller.getClient().getId();
		boolean isApplication = CatalogueUtils.isApplicationToken(caller);

		if(!isApplication)
			return Delegator.delegatePost(caller, context, Constants.RESOURCE_CREATE, json, uriInfo, isApplication);
		else{
			try {
				DataCatalogue utils = CatalogueUtils.getCatalogue();

				// in this case we check the author has been filled with the same qualifier of this token: the same qualifier can be used in two different contexts
				String organization = CatalogueUtilMethods.getOrganizationNameFromScope(ScopeProvider.instance.get());
				String datasetId = null;

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject)parser.parse(json);

				datasetId = (String)obj.get("package_id"); // within the resource it is defined this way
				if(datasetId == null || datasetId.isEmpty())
					throw new Exception("'id' field is missing!");

				CkanDataset item = utils.getDataset(datasetId, CatalogueUtils.fetchSysAPI(context));

				if(organization.equalsIgnoreCase(item.getOrganization().getName()) && username.equals(item.getAuthor())){
					return Delegator.delegatePost(caller, context, Constants.RESOURCE_CREATE, json, uriInfo, true);
				}else
					throw new Exception("You cannot add a resource to this item");

			} catch (Exception e) {
				logger.error("Something went wrong... ", e);
				if(e instanceof ParseException)
					return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
				else
					return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}

	}

	@POST
	@Path(Constants.CREATE_METHOD)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(
			FormDataMultiPart multiPart, @Context UriInfo uriInfo,
			@Context final HttpServletRequest request
			){

		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		String username = caller.getClient().getId();
		boolean isApplication = CatalogueUtils.isApplicationToken(caller);	

		if(!isApplication)
			return Delegator.delegatePost(caller, context, Constants.RESOURCE_CREATE, multiPart, uriInfo, false);
		else{
			try {
				DataCatalogue utils = CatalogueUtils.getCatalogue();

				// in this case we check the author has been filled with the same qualifier of this token: the same qualifier can be used in two different contexts
				ScopeBean bean = new ScopeBean(ScopeProvider.instance.get());
				String organization = bean.name().toLowerCase().replace(" ", "_").replace("-", "_");
				String datasetId = null;

				datasetId = (String)multiPart.getField("package_id").getValue(); // within the resource it is defined this way
				if(datasetId == null || datasetId.isEmpty())
					throw new Exception("'id' field is missing!");

				CkanDataset item = utils.getDataset(datasetId, CatalogueUtils.fetchSysAPI(context));

				if(organization.equalsIgnoreCase(item.getOrganization().getName()) && username.equals(item.getAuthor())){
					return Delegator.delegatePost(caller, context, Constants.RESOURCE_CREATE, multiPart, uriInfo, true);
				}else
					throw new Exception("You cannot add a resource to this item");

			} catch (Exception e) {
				logger.error("Something went wrong... ", e);
				if(e instanceof ParseException)
					return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
				else
					return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}

	}

	@DELETE
	@Path(Constants.DELETE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.resource_delete
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		String username = caller.getClient().getId();
		boolean isApplication = CatalogueUtils.isApplicationToken(caller);

		if(!isApplication)
			return Delegator.delegatePost(caller, context, Constants.RESOURCE_DELETE, json, uriInfo, false);
		else{
			try {
				DataCatalogue utils = CatalogueUtils.getCatalogue();

				// in this case we check the author has been filled with the same qualifier of this token: the same qualifier can be used in two different contexts
				ScopeBean bean = new ScopeBean(ScopeProvider.instance.get());
				String organization = bean.name().toLowerCase().replace(" ", "_").replace("-", "_");

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject)parser.parse(json);

				String resourceId = (String)obj.get("id"); // within the resource it is defined this way
				if(resourceId == null || resourceId.isEmpty())
					throw new Exception("'id' field is missing!");

				CkanResource resource = utils.getResource(resourceId, CatalogueUtils.fetchSysAPI(context));
				CkanDataset item = utils.getDataset(resource.getPackageId(), CatalogueUtils.fetchSysAPI(context));

				if(organization.equalsIgnoreCase(item.getOrganization().getName()) && username.equals(item.getAuthor())){
					return Delegator.delegatePost(caller, context, Constants.RESOURCE_DELETE, json, uriInfo, true);
				}else
					throw new Exception("You cannot delete this resource");

			} catch (Exception e) {
				logger.error("Something went wrong... ", e);
				if(e instanceof ParseException)
					return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
				else
					return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}

	}

	@POST
	@Path(Constants.UPDATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String update(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.resource_update
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		String username = caller.getClient().getId();
		boolean isApplication = CatalogueUtils.isApplicationToken(caller);

		if(!isApplication)
			return Delegator.delegatePost(caller, context, Constants.RESOURCE_UPDATE, json, uriInfo, false);
		else{
			try {
				DataCatalogue utils = CatalogueUtils.getCatalogue();

				// in this case we check the author has been filled with the same qualifier of this token: the same qualifier can be used in two different contexts
				ScopeBean bean = new ScopeBean(ScopeProvider.instance.get());
				String organization = bean.name().toLowerCase().replace(" ", "_").replace("-", "_");

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject)parser.parse(json);

				String resourceId = (String)obj.get("id"); // within the resource it is defined this way
				if(resourceId == null || resourceId.isEmpty())
					throw new Exception("'id' field is missing!");

				CkanResource resource = utils.getResource(resourceId, CatalogueUtils.fetchSysAPI(context));
				CkanDataset item = utils.getDataset(resource.getPackageId(), CatalogueUtils.fetchSysAPI(context));

				if(organization.equalsIgnoreCase(item.getOrganization().getName()) && username.equals(item.getAuthor())){
					return Delegator.delegatePost(caller, context, Constants.RESOURCE_UPDATE, json, uriInfo, true);
				}else
					throw new Exception("You cannot update this resource");

			} catch (Exception e) {
				logger.error("Something went wrong... ", e);
				if(e instanceof ParseException)
					return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
				else
					return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}

	}

	@POST
	@Path(Constants.PATCH_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String patch(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.resource_patch
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		String username = caller.getClient().getId();
		boolean isApplication = CatalogueUtils.isApplicationToken(caller);

		if(!isApplication)
			return Delegator.delegatePost(caller, context, Constants.RESOURCE_PATCH, json, uriInfo, false);
		else{
			try {
				DataCatalogue utils = CatalogueUtils.getCatalogue();

				// in this case we check the author has been filled with the same qualifier of this token: the same qualifier can be used in two different contexts
				ScopeBean bean = new ScopeBean(ScopeProvider.instance.get());
				String organization = bean.name().toLowerCase().replace(" ", "_").replace("-", "_");

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject)parser.parse(json);

				String resourceId = (String)obj.get("id"); // within the resource it is defined this way
				if(resourceId == null || resourceId.isEmpty())
					throw new Exception("'id' field is missing!");

				CkanResource resource = utils.getResource(resourceId, CatalogueUtils.fetchSysAPI(context));
				CkanDataset item = utils.getDataset(resource.getPackageId(), CatalogueUtils.fetchSysAPI(context));

				if(organization.equalsIgnoreCase(item.getOrganization().getName()) && username.equals(item.getAuthor())){
					return Delegator.delegatePost(caller, context, Constants.RESOURCE_PATCH, json, uriInfo, true);
				}else
					throw new Exception("You cannot patch this resource");

			} catch (Exception e) {
				logger.error("Something went wrong... ", e);
				if(e instanceof ParseException)
					return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
				else
					return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}


	}

}
