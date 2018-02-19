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
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datacatalogue.catalogue.utils.CatalogueUtils;
import org.gcube.datacatalogue.catalogue.utils.Constants;
import org.gcube.datacatalogue.catalogue.utils.Delegator;
import org.gcube.datacatalogue.catalogue.utils.PackageCreatePostActions;
import org.gcube.datacatalogue.catalogue.utils.Validator;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanDataset;

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
		boolean isApplication = CatalogueUtils.isApplicationToken(caller);
		return Delegator.delegateGet(caller, context, Constants.ITEM_SHOW, uriInfo, isApplication);

	}

	@SuppressWarnings("unchecked")
	@POST
	@Path(Constants.CREATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.package_create
		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId(); // in case of application token is the label of the token
		String context = ScopeProvider.instance.get();
		boolean isApplication = CatalogueUtils.isApplicationToken(caller);

		try{

			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject)parser.parse(json);

			// check base information (and set them if needed)
			Validator.checkBaseInformation(obj, caller, isApplication);

			// check resources information (name and url must be there)
			Validator.checkResourcesInformation(obj, caller);

			// Check if there are profiles here
			List<String> profiles = CatalogueUtils.getProfilesNames();

			if(profiles != null && !profiles.isEmpty())
				Validator.validateAgainstProfile(obj, caller, profiles, isApplication);

			obj = (JSONObject)parser.parse(Delegator.delegatePost(caller, context, Constants.ITEM_CREATE, obj.toJSONString(), uriInfo, isApplication));

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
						isApplication,
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
				return CatalogueUtils.createJSONOnFailure(e.toString());
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
		String username = caller.getClient().getId();
		boolean isApplication = CatalogueUtils.isApplicationToken(caller);

		if(!isApplication)
			return Delegator.delegatePost(caller, context, Constants.ITEM_DELETE, json, uriInfo, isApplication);
		else{
			try {
				DataCatalogue utils = CatalogueUtils.getCatalogue();

				// in this case we check the author has been filled with the same qualifier of this token: the same qualifier can be used in two different contexts
				ScopeBean bean = new ScopeBean(ScopeProvider.instance.get());
				String organization = bean.name().toLowerCase().replace(" ", "_").replace("-", "_");
				String datasetId = null;

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject)parser.parse(json);

				datasetId = (String)obj.get("id");
				if(datasetId == null || datasetId.isEmpty())
					throw new Exception("'id' field is missing!");

				CkanDataset item = utils.getDataset(datasetId, CatalogueUtils.fetchSysAPI(context));

				if(organization.equalsIgnoreCase(item.getOrganization().getName()) && username.equals(item.getAuthor())){
					return Delegator.delegatePost(caller, context, Constants.ITEM_DELETE, json, uriInfo, true);
				}else
					throw new Exception("You cannot delete this item");

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
	@Path(Constants.PURGE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String purge(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.dataset_purge
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		String username = caller.getClient().getId();

		// we need to extend this method wrt ckan: admins can purge the organization data, while editors just their own
		DataCatalogue utils = CatalogueUtils.getCatalogue();

		// we need also to check if the request comes from an application token
		boolean isApplication = CatalogueUtils.isApplicationToken(caller);

		if(isApplication){
			try {
				// in this case we check the author has been filled with the same qualifier of this token: the same qualifier can be used in two different contexts
				ScopeBean bean = new ScopeBean(ScopeProvider.instance.get());
				String organization = bean.name().toLowerCase().replace(" ", "_").replace("-", "_");
				String datasetId = null;

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject)parser.parse(json);

				datasetId = (String)obj.get("id");
				if(datasetId == null || datasetId.isEmpty())
					throw new Exception("'id' field is missing!");

				CkanDataset item = utils.getDataset(datasetId, CatalogueUtils.fetchSysAPI(context));

				if(organization.equalsIgnoreCase(item.getOrganization().getName()) && username.equals(item.getAuthor())){
					return Delegator.delegatePost(caller, context, Constants.ITEM_PURGE, json, uriInfo, true);
				}else
					throw new Exception("You cannot purge this item");

			} catch (Exception e) {
				logger.error("Something went wrong... ", e);
				if(e instanceof ParseException)
					return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
				else
					return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}else{

			// if sysadmin, just invoke ckan
			if(utils.isSysAdmin(username)){
				logger.debug("User  " + caller.getClient().getId() +  " seems a sysadmin");
				return Delegator.delegatePost(caller, context, Constants.ITEM_PURGE, json, uriInfo, false);
			}
			else{
				try {

					String datasetId = null;
					String ownerId = null;
					String organization = null;

					JSONParser parser = new JSONParser();
					JSONObject obj = (JSONObject)parser.parse(json);

					datasetId = (String)obj.get("id");
					if(datasetId == null || datasetId.isEmpty())
						throw new Exception("'id' field is missing!");

					String userApiKey = utils.getApiKeyFromUsername(username);
					CkanDataset item = utils.getDataset(datasetId, userApiKey);
					ownerId = item.getCreatorUserId();
					organization = item.getOrganization().getName();

					// check user role here
					RolesCkanGroupOrOrg roleInOrganization = RolesCkanGroupOrOrg.convertFromCapacity(utils.getRoleOfUserInOrganization(username, organization, userApiKey));

					boolean purged = false;
					if(roleInOrganization.equals(RolesCkanGroupOrOrg.MEMBER)){
						throw new Exception("You have not enough priviliges to delete item with id " + datasetId);
					}else if(roleInOrganization.equals(RolesCkanGroupOrOrg.ADMIN)){
						purged = utils.deleteProduct(datasetId, userApiKey, true);
					}else{
						// we have an editor here; just check she owns the dataset
						String userIdCkan = utils.getUserFromApiKey(userApiKey).getId();
						if(ownerId.equals(userIdCkan))
							purged = utils.deleteProduct(datasetId, userApiKey, true);
						else
							throw new Exception("Editors can only remove their own items!");
					}
					return CatalogueUtils.createJSONObjectMin(purged, null).toJSONString();

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
