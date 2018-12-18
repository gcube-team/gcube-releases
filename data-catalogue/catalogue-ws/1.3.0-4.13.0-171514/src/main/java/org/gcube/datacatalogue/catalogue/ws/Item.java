package org.gcube.datacatalogue.catalogue.ws;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.gcube.datacatalogue.catalogue.entities.CatalogueItem;
import org.gcube.datacatalogue.catalogue.utils.CatalogueUtils;
import org.gcube.datacatalogue.catalogue.utils.Constants;
import org.gcube.datacatalogue.catalogue.utils.ContextUtils;
import org.gcube.datacatalogue.catalogue.utils.Delegator;
import org.gcube.datacatalogue.catalogue.utils.PackageCreatePostActions;
import org.gcube.datacatalogue.catalogue.utils.Validator;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.CatalogueUtilMethods;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanDataset;

@Path(Constants.ITEMS)
/**
 * Items service endpoint.
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class Item {
	
	private static final Logger logger = LoggerFactory.getLogger(Item.class);
	
	private static final String ID_NAME = "id";
	
	private static final String ID_PATH_PARAM = "id";
	
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
	@Path("{" + ID_PATH_PARAM + "}")
	@Produces(MediaType.APPLICATION_JSON)
	public String show(@PathParam(ID_PATH_PARAM) String itemId, @Context UriInfo uriInfo) {
		try {
			if(itemId.compareTo(Constants.SHOW_METHOD)==0) {
				return show(uriInfo);
			}
			CatalogueItem item = new CatalogueItem();
			item.setId(itemId);
			return item.read();
		} catch(Exception e) {
			logger.error("", e);
			return CatalogueUtils.createJSONOnFailure(e.toString());
		}
	}
	
	public String show(@Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.package_show
		try {
			String datasetId = CatalogueUtils.getIdFromUriInfo(ID_NAME, uriInfo);
			applicationChecks(datasetId, "You are not authorized to access this item");
		} catch(Exception e) {
			logger.error("", e);
			return CatalogueUtils.createJSONOnFailure(e.toString());
		}
		return Delegator.delegateGet(Constants.ITEM_SHOW, uriInfo);
	}
	
	@POST
	@Path(Constants.CREATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Deprecated
	public String oldCreate(String json, @Context UriInfo uriInfo) {
		return this.create(json, uriInfo);
	}
	
	
	@SuppressWarnings("unchecked")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.package_create
		try {
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(json);
			
			// check base information (and set them if needed)
			Validator.checkBaseInformation(obj);
			
			// check resources information (name and url must be there)
			Validator.checkResourcesInformation(obj);
			
			// Check if there are profiles here
			List<String> profiles = CatalogueUtils.getProfilesNames();
			
			if(profiles != null && !profiles.isEmpty()) {
				Validator.validateAgainstProfile(obj, profiles);
			}
			
			JSONParser resultParser = new JSONParser();
			JSONObject createdJSONObject = (JSONObject) resultParser
					.parse(Delegator.delegatePost(Constants.ITEM_CREATE, obj.toJSONString(), uriInfo));
			
			// after creation, if it is ok ...
			if((boolean) createdJSONObject.get(Constants.SUCCESS_KEY)) {
				
				JSONObject result = (JSONObject) createdJSONObject.get(Constants.RESULT_KEY);
				DataCatalogue dataCatalogue = CatalogueUtils.getCatalogue();
				
				// add also this information as custom field
				String datasetUrl = dataCatalogue
						.getUnencryptedUrlFromDatasetIdOrName((String) (result.get(Constants.DATASET_KEY)));
				if(datasetUrl != null) {
					JSONObject itemUrl = new JSONObject();
					itemUrl.put(Constants.EXTRA_KEY, PackageCreatePostActions.ITEM_URL);
					itemUrl.put(Constants.EXTRA_VALUE, datasetUrl);
					((JSONArray) ((JSONObject) createdJSONObject.get(Constants.RESULT_KEY)).get(Constants.EXTRAS_KEY))
							.add(itemUrl);
				}
				
				PackageCreatePostActions packagePostActions = new PackageCreatePostActions(datasetUrl,
						(String) (result.get(Constants.DATASET_KEY)), 
						(JSONArray) (result.get(Constants.TAGS_KEY)),
						(String) (result.get(Constants.TITLE_KEY)));
				
				packagePostActions.start();
				
			}
			return createdJSONObject.toJSONString();
			
		} catch(Exception e) {
			logger.error("Something went wrong... ", e);
			if(e instanceof ParseException) {
				return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
			} else {
				return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}
		
	}
	
	@DELETE
	@Path("{" + ID_PATH_PARAM + "}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(@PathParam(ID_PATH_PARAM) String itemId, String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.package_delete
		try {
			if(itemId.compareTo(Constants.DELETE_METHOD)==0) {
				itemId = CatalogueUtils.getIdFromJSONString(ID_NAME, json);
			}
			applicationChecks(itemId, "You cannot delete this item");
		} catch(Exception e) {
			logger.error("", e);
			if(e instanceof ParseException) {
				return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
			} else {
				return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}
		return Delegator.delegatePost(Constants.ITEM_DELETE, json, uriInfo);
		
	}
	
	
/*	@DELETE
	@Path(Constants.DELETE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.package_delete
		try {
			String datasetId = CatalogueUtils.getIdFromJSONString(ID_NAME, json);
			applicationChecks(datasetId, "You cannot delete this item");
		} catch(Exception e) {
			logger.error("", e);
			if(e instanceof ParseException) {
				return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
			} else {
				return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}
		return Delegator.delegatePost(Constants.ITEM_DELETE, json, uriInfo);
		
	}*/
	
	@DELETE
	@Path(Constants.PURGE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String purge(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.dataset_purge
		try {
			DataCatalogue dataCatalogue = CatalogueUtils.getCatalogue();
			String username = ContextUtils.getUsername();
			if(!dataCatalogue.isSysAdmin(username)) {
				String datasetId = CatalogueUtils.getIdFromJSONString(ID_NAME, json);
				if(ContextUtils.isApplication()) {
					applicationChecks(datasetId, "You cannot purge this item");
				} else {
					
					String userApiKey = dataCatalogue.getApiKeyFromUsername(ContextUtils.getUsername());
					CkanDataset item = dataCatalogue.getDataset(datasetId, userApiKey);
					
					String ownerId = item.getCreatorUserId();
					String organization = item.getOrganization().getName();
					
					// check user role here
					RolesCkanGroupOrOrg roleInOrganization = RolesCkanGroupOrOrg.convertFromCapacity(
							dataCatalogue.getRoleOfUserInOrganization(username, organization, userApiKey));
					
					boolean purged = false;
					if(roleInOrganization.equals(RolesCkanGroupOrOrg.MEMBER)) {
						throw new Exception("You have not enough priviliges to delete item with id " + datasetId);
					} else if(roleInOrganization.equals(RolesCkanGroupOrOrg.ADMIN)) {
						purged = dataCatalogue.deleteProduct(datasetId, userApiKey, true);
					} else {
						// we have an editor here; just check she owns the dataset
						String userIdCkan = dataCatalogue.getUserFromApiKey(userApiKey).getId();
						if(ownerId.equals(userIdCkan)) {
							purged = dataCatalogue.deleteProduct(datasetId, userApiKey, true);
						} else {
							throw new Exception("Editors can only remove their own items!");
						}
					}
					return CatalogueUtils.createJSONObjectMin(purged, null).toJSONString();
				}
			}
		} catch(Exception e) {
			logger.error("", e);
			if(e instanceof ParseException) {
				return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
			} else {
				return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}
		return Delegator.delegatePost(Constants.ITEM_PURGE, json, uriInfo);
	}
	
	@POST
	@Path(Constants.UPDATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String update(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.package_update
		try {
			String datasetId = CatalogueUtils.getIdFromJSONString(ID_NAME, json);
			applicationChecks(datasetId, "You cannot update this item");
			
			
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(json);
			
			// check base information (and set them if needed)
			Validator.checkBaseInformation(obj);
			
			// check resources information (name and url must be there)
			Validator.checkResourcesInformation(obj);
			
			// Check if there are profiles here
			List<String> profiles = CatalogueUtils.getProfilesNames();
			if(profiles != null && !profiles.isEmpty()) {
				Validator.validateAgainstProfile(obj, profiles);
			}

			return Delegator.delegatePost(Constants.ITEM_UPDATE, json, uriInfo);
			
		} catch (Exception e) {
			logger.error("", e);
			if(e instanceof ParseException) {
				return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
			} else {
				return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}
		
	}
	
	/*
	@POST
	@Path(Constants.PATCH_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String patch(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.patch.package_patch
		try {
			return Delegator.delegatePost(Constants.ITEM_PATCH, json, uriInfo);
		} catch (Exception e) {
			logger.error("", e);
			if(e instanceof ParseException) {
				return CatalogueUtils.createJSONOnFailure("Failed to parse incoming json!");
			} else {
				return CatalogueUtils.createJSONOnFailure(e.toString());
			}
		}
		
	}
	*/
}
