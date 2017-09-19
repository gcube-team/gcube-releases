package org.gcube.datacatalogue.catalogue.ws;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.catalogue.utils.CatalogueUtils;
import org.gcube.datacatalogue.catalogue.utils.Constants;
import org.gcube.datacatalogue.catalogue.utils.Delegator;
import org.gcube.datacatalogue.catalogue.utils.PackageCreatePostActions;
import org.gcube.datacatalogue.catalogue.utils.Validator;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

@Path(Constants.ITEMS)
/**
 * Items service endpoint.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Item {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Item.class);

	@GET
	@Path(Constants.SHOW_METHOD)
	@Produces(MediaType.APPLICATION_JSON)
	public String show(@Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.package_show
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return Delegator.delegateGet(caller, context, Constants.ITEM_SHOW, uriInfo);

	}

	@SuppressWarnings("unchecked")
	@POST
	@Path(Constants.CREATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.package_create
		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();

		try{

			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject)parser.parse(json);

			// check base information (and set them if needed)
			Validator.checkBaseInformation(obj, caller);

			// check resources information (name and url must be there)
			Validator.checkResourcesInformation(obj, caller);

			// Check if there are profiles here
			List<String> profiles = CatalogueUtils.getProfilesNames();

			if(profiles != null && !profiles.isEmpty())
				Validator.validateAgainstProfile(obj, caller, profiles);

			obj = (JSONObject)parser.parse(Delegator.delegatePost(caller, context, Constants.ITEM_CREATE, obj.toJSONString(), uriInfo));

			// after creation, if it is ok ...
			if((boolean)obj.get(Constants.SUCCESS_KEY)){

				JSONObject result = (JSONObject)obj.get(Constants.RESULT_KEY);
				DataCatalogue utils = CatalogueUtils.getCatalogue();

				// add also this information as custom field
				String datasetUrl =  utils.getUnencryptedUrlFromDatasetIdOrName((String)(result.get(Constants.DATASET_KEY)));
				if(datasetUrl != null){
					JSONObject itemUrl = new JSONObject();
					itemUrl.put(Constants.EXTRA_KEY, PackageCreatePostActions.ITEM_URL);
					itemUrl.put(Constants.EXTRA_VALUE, datasetUrl);
					((JSONArray)((JSONObject)obj.get(Constants.RESULT_KEY)).get(Constants.EXTRAS_KEY)).add(itemUrl);
				}
				PackageCreatePostActions packagePostActions = new PackageCreatePostActions(
						username, 
						datasetUrl,
						(String)(result.get(Constants.DATASET_KEY)),
						context, 
						SecurityTokenProvider.instance.get(),
						(JSONArray)(result.get(Constants.TAGS_KEY)),
						(String)(result.get(Constants.TITLE_KEY))
						);

				packagePostActions.start();

			}

			return obj.toJSONString();

		}catch(Exception e){
			logger.error("Something went wrong... ", e);
			if(e instanceof ParseException)
				return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
			else
				return CatalogueUtils.createJSONOnFailure(e.getMessage());
		}

	}

	@DELETE
	@Path(Constants.DELETE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.package_delete
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return Delegator.delegatePost(caller, context, Constants.ITEM_DELETE, json, uriInfo);

	}

	@DELETE
	@Path(Constants.PURGE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String purge(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.dataset_purge
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return Delegator.delegatePost(caller, context, Constants.ITEM_PURGE, json, uriInfo);

	}

	// TODO PROFILE VALIDATION MUST BE PERFORMED HERE AS WELL
	//	@POST
	//	@Path(Constants.UPDATE_METHOD)
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public String update(String json){
	//
	//		
	//		// 1) Check if there are profiles here
	//		// 2) If there are profiles: match the record against them
	//		// 3) Else submit it
	//
	//		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.package_update
	//		Caller caller = AuthorizationProvider.instance.get();
	//		String context = ScopeProvider.instance.get();
	//		return CatalogueUtils.delegatePost(caller, context, Constants.ITEM_UPDATE, json);
	//
	//	}

	//	@POST
	//	@Path(Constants.PATCH_METHOD)
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public String patch(String json){
	//
	//		 
	//		// 1) Check if there are profiles here
	//		// 2) If there are profiles: match the record against them
	//		// 3) Else submit it
	//
	//		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.package_patch
	//		Caller caller = AuthorizationProvider.instance.get();
	//		String context = ScopeProvider.instance.get();
	//		return CatalogueUtils.delegatePost(caller, context, Constants.ITEM_PATCH, json);
	//
	//	}

}
