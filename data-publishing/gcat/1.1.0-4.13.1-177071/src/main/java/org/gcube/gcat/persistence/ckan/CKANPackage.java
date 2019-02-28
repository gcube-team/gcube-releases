package org.gcube.gcat.persistence.ckan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.gcat.annotation.PURGE;
import org.gcube.gcat.oldutils.Validator;
import org.gcube.gcat.profile.MetadataUtility;
import org.gcube.gcat.social.SocialService;
import org.gcube.gcat.utils.ContextUtility;
import org.gcube.gcat.utils.URIResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class CKANPackage extends CKAN {
	
	private static final Logger logger = LoggerFactory.getLogger(CKANPackage.class);
	/*
	// see https://docs.ckan.org/en/latest/api/#ckan.logic.action.get.package_list
	public static final String ITEM_LIST = CKAN.CKAN_API_PATH + "package_list";
	*/
	// see https://docs.ckan.org/en/latest/api/index.html#ckan.logic.action.get.package_search
	public static final String ITEM_LIST = CKAN.CKAN_API_PATH + "package_search";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.package_create
	public static final String ITEM_CREATE = CKAN.CKAN_API_PATH + "package_create";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.package_show
	public static final String ITEM_SHOW = CKAN.CKAN_API_PATH + "package_show";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.package_update
	public static final String ITEM_UPDATE = CKAN.CKAN_API_PATH + "package_update";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.patch.package_patch
	public static final String ITEM_PATCH = CKAN.CKAN_API_PATH + "package_patch";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.package_delete
	public static final String ITEM_DELETE = CKAN.CKAN_API_PATH + "package_delete";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.dataset_purge
	public static final String ITEM_PURGE = CKAN.CKAN_API_PATH + "dataset_purge";
	
	// limit in https://docs.ckan.org/en/latest/api/index.html#ckan.logic.action.get.package_search
	protected static final String ROWS_KEY = "rows"; 
	// offset in https://docs.ckan.org/en/latest/api/index.html#ckan.logic.action.get.package_search
	protected static final String START_KEY = "start";
	
	protected static final String Q_KEY = "q";
	protected static final String ORGANIZATION_FILTER_TEMPLATE = "organization:%s";
	
	protected static final String LICENSE_KEY = "license_id";
	
	protected static final String EXTRAS_ITEM_URL_KEY = "Item URL";
	
	protected static final String AUTHOR_KEY = "author";
	protected static final String AUTHOR_EMAIL_KEY = "author_email";
	protected static final String OWNER_ORG_KEY = "owner_org";
	protected static final String RESOURCES_KEY = "resources";
	protected static final String TITLE_KEY = "title";
	
	public static final String EXTRAS_KEY = "extras";
	public static final String EXTRAS_KEY_KEY = "key";
	public static final String EXTRAS_KEY_VALUE_SYSTEM_TYPE = "system:type";
	public static final String EXTRAS_VALUE_KEY = "value";
	
	// The 'results' array is included in the 'result' object for package_search
	private static final String RESULTS_KEY = "results";
	
	protected static final String PRIVATE_KEY = "private";
	protected static final String SEARCHABLE_KEY = "searchable";
	
	// protected static final String INCLUDE_PRIVATE_KEY = "include_private";
	// protected static final String INCLUDE_DRAFTS_KEY = "include_drafts";
	
	public static final String GROUPS_KEY = "groups";
	public static final String TAGS_KEY = "tags";
	
	protected final List<CKANResource> managedResources;
	
	protected String itemID;
	
	public CKANPackage() {
		super();
		LIST = ITEM_LIST;
		CREATE = ITEM_CREATE;
		READ = ITEM_SHOW;
		UPDATE = ITEM_UPDATE;
		PATCH = ITEM_PATCH;
		DELETE = ITEM_DELETE;
		PURGE = ITEM_PURGE;
		managedResources = new ArrayList<CKANResource>();
	}
	
	/*
	 * Return the CKAN organization name using the current context name
	 */
	protected String getOrganizationName(ScopeBean scopeBean) {
		String contextName = scopeBean.name();
		return contextName.toLowerCase().replace(" ", "_");
	}
	
	protected String getOrganizationName() {
		ScopeBean scopeBean = new ScopeBean(ContextUtility.getCurrentContext());
		return getOrganizationName(scopeBean);
	}
	
	public ObjectNode checkBaseInformation(String json) throws Exception {
		ObjectNode objectNode = (ObjectNode) mapper.readTree(json);
		
		objectNode = (ObjectNode) checkName(objectNode);
		
		// We need to enforce the itemID to properly manage resource persistence
		if(objectNode.has(ID_KEY)) {
			itemID = objectNode.get(ID_KEY).asText();
		}
		
		// To include private item in search result (e.g. listing) a private package must be searchable
		// The package it is jsut included in the search and listing results but remain private and cannot be accessed
		// if not authorized
		if(objectNode.has(PRIVATE_KEY)) {
			boolean privatePackage = objectNode.get(PRIVATE_KEY).asBoolean();
			if(privatePackage) {
				objectNode.put(SEARCHABLE_KEY, true);
			}
		}
		
		// check license
		String licenseId = null;
		if(objectNode.has(LICENSE_KEY)) {
			licenseId = objectNode.get(LICENSE_KEY).asText();
		}
		if(licenseId == null || licenseId.isEmpty()) {
			throw new BadRequestException(
					"You must specify a license identifier for the item. License list can be retrieved using licence collection");
		}
		
		JsonNode userJsonNode = CKANUtility.getCKANUser();
		objectNode.put(AUTHOR_KEY, userJsonNode.get(CKANUser.NAME).asText());
		objectNode.put(AUTHOR_EMAIL_KEY, userJsonNode.get(CKANUser.EMAIL).asText());
		
		// owner organization must be specified if the token belongs to a VRE
		ScopeBean scopeBean = new ScopeBean(ContextUtility.getCurrentContext());
		
		String gotOrganization = null;
		if(objectNode.has(OWNER_ORG_KEY)) {
			gotOrganization = objectNode.get(OWNER_ORG_KEY).asText();
		}
		
		if(scopeBean.is(Type.VRE)) {
			String organizationFromContext = getOrganizationName(scopeBean);
			if(gotOrganization != null) {
				if(gotOrganization.compareTo(organizationFromContext) != 0) {
					CKANOrganization ckanOrganization = new CKANOrganization();
					ckanOrganization.setName(organizationFromContext);
					ckanOrganization.read();
					String organizationID = null;
					if(ckanOrganization.result.has(ID_KEY)) {
						organizationID = ckanOrganization.result.get(ID_KEY).asText();
					}
					if(organizationID == null || gotOrganization.compareTo(organizationID) != 0) {
						throw new BadRequestException(
								"You can only publish in the Organization associate to the current VRE");
					}
				}
			} else {
				objectNode.put(OWNER_ORG_KEY, organizationFromContext);
			}
		} else {
			// TODO check if the requested organization context is a sub context of current context
			// TODO check if the check is correct for PARTHENOS
			if(gotOrganization == null) {
				throw new BadRequestException("You must specify an Organization usign " + OWNER_ORG_KEY + " field");
			}
		}
		
		return objectNode;
	}
	
	protected JsonNode validateJson(String json) {
		try {
			// check base information (and set them if needed)
			ObjectNode objectNode = checkBaseInformation(json);
			
			// Validating against profiles if any
			MetadataUtility metadataUtility = MetadataUtility.getInstance();
			if(!metadataUtility.getMetadataProfiles().isEmpty()) {
				Validator.validateAgainstProfile(getAsString(objectNode));
			}
			
			return objectNode;
		} catch(BadRequestException e) {
			throw e;
		} catch(Exception e) {
			throw new BadRequestException(e);
		}
	}
	
	@Override
	public String list(int limit, int offset) {
		Map<String,String> parameters = new HashMap<>();
		if(limit <= 0) {
			// According to CKAN documentation
			// the number of matching rows to return. There is a hard limit of 1000 datasets per query.
			// see https://docs.ckan.org/en/2.6/api/index.html#ckan.logic.action.get.package_search
			limit = 1000;
		}
		parameters.put(ROWS_KEY, String.valueOf(limit));

		if(offset < 0) {
			offset = 0;
		}
		parameters.put(START_KEY, String.valueOf(offset*limit));
		
		String organizationName = getOrganizationName();
		String organizationQueryString = String.format(ORGANIZATION_FILTER_TEMPLATE, organizationName);
		parameters.put(Q_KEY, organizationQueryString);
		
		// parameters.put(INCLUDE_PRIVATE_KEY, String.valueOf(true));
		
		// By default not including draft
		// parameters.put(INCLUDE_DRAFTS_KEY, String.valueOf(false));
		
		sendGetRequest(LIST, parameters);
		
		ArrayNode results = (ArrayNode) result.get(RESULTS_KEY);
		
		ArrayNode arrayNode = mapper.createArrayNode();
		for(JsonNode node : results) {
			try {
				String name = node.get(NAME_KEY).asText();
				arrayNode.add(name);
			}catch (Exception e) {
				try {
					logger.error("Unable to get the ID of {}. the result will not be included in the result", mapper.writeValueAsString(node));
				}catch (Exception ex) {
					logger.error("", ex);
				}
			}
		}
		
		return getAsString(arrayNode);
	}
	
	protected void rollbackManagedResources() {
		for(CKANResource ckanResource : managedResources) {
			try {
				ckanResource.rollback();
			}catch (Exception e) {
				logger.error("Unable to rollback resource {} to the original value", ckanResource.getResourceID());
			}
			
		}
	}
	
	protected ArrayNode createResources(ArrayNode resourcesToBeCreated) {
		ArrayNode created = mapper.createArrayNode();
		for(JsonNode resourceNode : resourcesToBeCreated) {
			CKANResource ckanResource = new CKANResource(itemID);
			String json = ckanResource.create(getAsString(resourceNode));
			created.add(getAsJsonNode(json));
			managedResources.add(ckanResource);
		}
		return created;
	}
	
	protected JsonNode addExtraField(JsonNode jsonNode, String key, String value) {
		ArrayNode extras = null;
		boolean found = false;
		if(jsonNode.has(EXTRAS_KEY)) {
			extras = (ArrayNode) jsonNode.get(EXTRAS_KEY);
			for(JsonNode extra : extras) {
				if(extra.has(EXTRAS_KEY_KEY) && extra.get(EXTRAS_KEY_KEY).asText().compareTo(key)==0) {
					((ObjectNode) extra).put(EXTRAS_VALUE_KEY, value);
					found = true;
					break;
				}
			}
		}else {
			extras = mapper.createArrayNode();
		}
		
		if(!found) {
			ObjectNode extra = mapper.createObjectNode();
			extra.put(EXTRAS_KEY_KEY, key);
			extra.put(EXTRAS_VALUE_KEY, value);
			extras.add(extra);
		}
		
		return jsonNode;
	}
	
	protected String addItemURLViaResolver(JsonNode jsonNode) {
		// Adding Item URL via Resolver
		URIResolver uriResolver = new URIResolver();
		String catalogueItemURL = uriResolver.getCatalogueItemURL(name);
		addExtraField(jsonNode, EXTRAS_ITEM_URL_KEY, catalogueItemURL);
		return catalogueItemURL;
	}
	
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.package_create
	@Override
	public String create(String json) {
		try {
			logger.debug("Going to create Item {}", json);
			
			JsonNode jsonNode = validateJson(json);
			
			ArrayNode resourcesToBeCreated = mapper.createArrayNode();
			if(jsonNode.has(RESOURCES_KEY)) {
				resourcesToBeCreated = (ArrayNode) jsonNode.get(RESOURCES_KEY);
				((ObjectNode) jsonNode).remove(RESOURCES_KEY);
			}
			
			String catalogueItemURL = addItemURLViaResolver(jsonNode);
			
			super.create(getAsString(jsonNode));
			
			this.itemID = result.get(ID_KEY).asText();
			ArrayNode created = createResources(resourcesToBeCreated);
			((ObjectNode) result).replace(RESOURCES_KEY, created);
			
			// Adding Item URL via Resolver as
			// ((ObjectNode) result).put(ITEM_URL_KEY, catalogueItemURL);
			
			// Actions performed after a package has been correctly created on ckan.
			String title = result.get(TITLE_KEY).asText();
			
			ArrayNode arrayNode = (ArrayNode) result.get(TAGS_KEY);
			SocialService packagePostActions = new SocialService(catalogueItemURL, name, arrayNode, title);
			packagePostActions.start();
			
			return getAsString(result);
		} catch(WebApplicationException e) {
			rollbackManagedResources();
			throw e;
		} catch(Exception e) {
			rollbackManagedResources();
			throw new InternalServerErrorException(e);
		}
	}
	
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.package_update
	@Override
	public String update(String json) {
		try {
			JsonNode jsonNode = validateJson(json);
			this.itemID = jsonNode.get(ID_KEY).asText();
			
			read();
			
			Map<String, CKANResource> originalResources = new HashMap<>(); 
			ArrayNode originalResourcesarrayNode =  (ArrayNode) result.get(RESOURCES_KEY);
			if(originalResources!=null) {
				for(JsonNode resourceNode : originalResourcesarrayNode) {
					CKANResource ckanResource = new CKANResource(itemID);
					ckanResource.setPreviousRepresentation(resourceNode);
					String resourceID = ckanResource.getResourceID();
					originalResources.put(resourceID, ckanResource);
				}
			}
			
					
			
			if(jsonNode.has(RESOURCES_KEY)) {
				ArrayNode resourcesToBeSend = mapper.createArrayNode();
				ArrayNode receivedResources = (ArrayNode) jsonNode.get(RESOURCES_KEY);
				for(JsonNode resourceNode : receivedResources) {
					CKANResource ckanResource = new CKANResource(itemID);
					String resourceId = CKANResource.extractResourceID(resourceNode);
					if(resourceId!=null) {
						if(originalResources.containsKey(resourceId)) {
							ckanResource = originalResources.get(resourceId);
							originalResources.remove(resourceId);
						}else {
							throw new BadRequestException("The content cotains a resource with id " + resourceId + " which does not exists") ;
						}
					}
					resourceNode = ckanResource.createOrUpdate(resourceNode);
					resourcesToBeSend.add(resourceNode);
					managedResources.add(ckanResource);
					
				}
				((ObjectNode) jsonNode).replace(RESOURCES_KEY, resourcesToBeSend);
			}
			
			addItemURLViaResolver(jsonNode);
			
			sendPostRequest(ITEM_UPDATE, getAsString(jsonNode));
			
			for(String resourceId : originalResources.keySet()) {
				CKANResource ckanResource = originalResources.get(resourceId);
				ckanResource.deleteFile();
			}
			
			/*
			// Adding Item URL via Resolver
			URIResolver uriResolver = new URIResolver();
			String catalogueItemURL = uriResolver.getCatalogueItemURL(name);
			((ObjectNode) result).put(ITEM_URL_KEY, catalogueItemURL);
			*/
			
			return getAsString(result);
		} catch(WebApplicationException e) {
			rollbackManagedResources();
			throw e;
		} catch(Exception e) {
			rollbackManagedResources();
			throw new InternalServerErrorException(e);
		}
	}
	
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.patch.package_patch
	@Override
	public String patch(String json) {
		String[] moreAllowed = new String[] {HEAD.class.getSimpleName(), GET.class.getSimpleName(),
				PUT.class.getSimpleName(), DELETE.class.getSimpleName(), PURGE.class.getSimpleName()};
		throw new NotAllowedException(OPTIONS.class.getSimpleName(), moreAllowed);
	}
	
	@Override
	protected void delete() {
		super.delete();
	}
	
	@Override
	public void purge() {
		try {
			delete();
		} catch(WebApplicationException e) {
			// If the item was deleted but not purged we obtain Not Found. This is accepted. The item has to be purged
			// with SysAdmin right.
			Status status = Status.fromStatusCode(e.getResponse().getStatusInfo().getStatusCode());
			if(status != Status.NOT_FOUND) {
				throw e;
			}
		}
		setApiKey(CKANUtility.getSysAdminAPI());
		read();
		if(result.has(RESOURCES_KEY)) {
			itemID = result.get(ID_KEY).asText();
			ArrayNode arrayNode = (ArrayNode) result.get(RESOURCES_KEY);
			for(JsonNode jsonNode : arrayNode) {
				CKANResource ckanResource = new CKANResource(itemID);
				ckanResource.setPreviousRepresentation(jsonNode);
				ckanResource.deleteFile(); // Only delete file is required because the item has been deleted
			}
		}
		super.purge();
	}
	
}
