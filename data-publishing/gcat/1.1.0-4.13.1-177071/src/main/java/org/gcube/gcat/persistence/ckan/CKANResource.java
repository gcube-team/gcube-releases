package org.gcube.gcat.persistence.ckan;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.WebApplicationException;

import org.apache.commons.io.FilenameUtils;
import org.gcube.common.gxhttp.request.GXHTTPStringRequest;
import org.gcube.gcat.utils.Constants;
import org.gcube.gcat.utils.ContextUtility;
import org.gcube.gcat.utils.HTTPCall;
import org.gcube.gcat.workspace.CatalogueStorageHubManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class CKANResource extends CKAN {
	
	private static final Logger logger = LoggerFactory.getLogger(CKANResource.class);
	
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.resource_create
	public static final String RESOURCE_CREATE = CKAN.CKAN_API_PATH + "resource_create";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.resource_show
	public static final String RESOURCE_SHOW = CKAN.CKAN_API_PATH + "resource_show";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.resource_update
	public static final String RESOURCE_UPDATE = CKAN.CKAN_API_PATH + "resource_update";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.patch.resource_patch
	public static final String RESOURCE_PATCH = CKAN.CKAN_API_PATH + "resource_patch";
	// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.resource_delete
	public static final String RESOURCE_DELETE = CKAN.CKAN_API_PATH + "resource_delete";
	
	protected static final String URL_KEY = "url";
	
	private static final String RESOURCES_KEY = "resources";
	private static final String PACKAGE_ID_KEY = "package_id";
	private static final String MIME_TYPE_KEY = "mimetype";
	private static final String REVISION_ID_KEY = "revision_id";
	
	private static final String TEMP = "TEMP_";
	
	public final static String RESOURCE_NAME_REGEX = "^[\\s\\S]*$";
	
	/* TODO Remove this code ASAP. It requires a function from Storage HUB */
	private static final String URI_RESOLVER_STORAGE_HUB_HOST_PROD = "data.d4science.org";
	private static final String URI_RESOLVER_STORAGE_HUB_HOST_DEV = "data1-d.d4science.org";
	
	public static final String URI_RESOLVER_STORAGE_HUB_HOST;
	public static final String URI_RESOLVER_STORAGE_HUB_PATH = "/shub/";
	
	static {
		String context = ContextUtility.getCurrentContext();
		if(context.startsWith("/gcube")) {
			URI_RESOLVER_STORAGE_HUB_HOST = URI_RESOLVER_STORAGE_HUB_HOST_DEV;
		} else {
			URI_RESOLVER_STORAGE_HUB_HOST = URI_RESOLVER_STORAGE_HUB_HOST_PROD;
		}
		
	}
	/* TODO END Code to be Removed */
	
	protected String itemID;
	
	public String getItemID() {
		return itemID;
	}
	
	protected String resourceID;
	
	protected boolean persisted;
	protected URL persistedURL;
	
	protected String mimeType;
	
	protected JsonNode previousRepresentation;
	
	protected CatalogueStorageHubManagement storageHubManagement;
	
	public URL getPersistedURL() {
		return persistedURL;
	}
	
	public static String extractResourceID(JsonNode jsonNode) {
		String resourceID = null;
		if(jsonNode.has(ID_KEY)) {
			resourceID = jsonNode.get(ID_KEY).asText();
		}
		return resourceID;
	}
	
	public String getResourceID() {
		if(resourceID == null && previousRepresentation != null) {
			resourceID = CKANResource.extractResourceID(previousRepresentation);
		}
		return resourceID;
	}
	
	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}
	
	public void setPreviousRepresentation(JsonNode jsonNode) {
		validate(jsonNode);
		previousRepresentation = jsonNode;
	}
	
	public JsonNode getPreviousRepresentation() {
		if(previousRepresentation == null && resourceID != null) {
			sendGetRequest(READ, getMapWithID(resourceID));
			validate(result);
			previousRepresentation = result;
		}
		return previousRepresentation;
	}
	
	public CKANResource(String itemID) {
		super();
		this.nameRegex = RESOURCE_NAME_REGEX;
		this.itemID = itemID;
		CREATE = RESOURCE_CREATE;
		READ = RESOURCE_SHOW;
		UPDATE = RESOURCE_UPDATE;
		PATCH = RESOURCE_PATCH;
		DELETE = RESOURCE_DELETE;
		PURGE = null;
		persisted = false;
		previousRepresentation = null;
	}
	
	@Override
	public String list(int limit, int offeset) {
		return list();
	}
	
	public String list() {
		CKANPackage ckanPackage = new CKANPackage();
		ckanPackage.setName(itemID);
		String itemJson = ckanPackage.read();
		JsonNode item = getAsJsonNode(itemJson);
		JsonNode resources = item.get(RESOURCES_KEY);
		return getAsString(resources);
	}
	
	protected ObjectNode persistStorageFile(ObjectNode objectNode) {
		
		if(objectNode.has(URL_KEY)) {
			String urlString = objectNode.get(URL_KEY).asText();
			
			URL url;
			try {
				url = new URL(urlString);
			} catch(MalformedURLException e) {
				throw new BadRequestException(e);
			}
			
			url = copyStorageResource(url);
			
			if(name != null) {
				objectNode.put(NAME_KEY, name);
			}
			
			if(mimeType != null) {
				objectNode.put(MIME_TYPE_KEY, mimeType);
			}
			
			objectNode.put(URL_KEY, url.toString());
			return objectNode;
		}
		
		String error = String.format("The content must contains the %s property", URL_KEY);
		throw new BadRequestException(error);
		
	}
	
	protected ObjectNode validate(String json) throws MalformedURLException {
		JsonNode jsonNode = getAsJsonNode(json);
		return validate(jsonNode);
	}
	
	protected ObjectNode validate(JsonNode jsonNode) {
		
		ObjectNode objectNode = (ObjectNode) jsonNode;
		
		if(objectNode.has(PACKAGE_ID_KEY)) {
			String packageId = objectNode.get(PACKAGE_ID_KEY).asText();
			if(packageId.compareTo(itemID) != 0) {
				String error = String.format(
						"Item ID %s does not match %s which is the value of %s contained in the representation.",
						itemID, packageId, PACKAGE_ID_KEY);
				throw new BadRequestException(error);
			}
		} else {
			objectNode.put(PACKAGE_ID_KEY, itemID);
		}
		
		if(objectNode.has(ID_KEY)) {
			String gotId = objectNode.get(ID_KEY).asText();
			if(resourceID == null) {
				resourceID = gotId;
			} else {
				if(resourceID.compareTo(gotId) != 0) {
					String error = String.format(
							"Resource ID %s does not match %s which is the value of %s contained in the representation.",
							resourceID, gotId, ID_KEY);
					throw new BadRequestException(error);
				}
			}
		} else {
			resourceID = TEMP + UUID.randomUUID().toString();
			logger.trace(
					"The id of the resource with name {} for package {} has not been provided. It has been generated : {}",
					name, itemID, resourceID);
		}
		
		return objectNode;
	}
	
	protected URL getFinalURL(String url) {
		try {
			URL urlURL = new URL(url);
			return CKANResource.getFinalURL(urlURL);
		} catch(MalformedURLException e) {
			throw new BadRequestException(e);
		}
	}
	
	public static URL getFinalURL(URL url) {
		HTTPCall httpCall = new HTTPCall(url.toString());
		httpCall.setgCubeTargetService(false);
		URL finalURL = httpCall.getFinalURL(url);
		return finalURL;
	}
	
	protected boolean isStorageFile(URL url) {
		URL urlToCheck = getFinalURL(url);
		if(urlToCheck.getHost().compareTo(URI_RESOLVER_STORAGE_HUB_HOST) == 0) {
			if(urlToCheck.getPath().startsWith(URI_RESOLVER_STORAGE_HUB_PATH)) {
				persistedURL = urlToCheck;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if the URl is a workspace URL so that is has to copy the resource to guarantee 
	 * the resource remain persistent
	 * @param url the URL to check
	 * @return the public URL of the copied resource if any. It return the original URL otherwise
	 */
	protected URL copyStorageResource(URL url) {
		persistedURL = url;
		if(isStorageFile(persistedURL)) {
			storageHubManagement = new CatalogueStorageHubManagement();
			try {
				persistedURL = storageHubManagement.ensureResourcePersistence(persistedURL, itemID, resourceID);
				name = FilenameUtils.removeExtension(storageHubManagement.getOriginalFilename());
				mimeType = storageHubManagement.getMimeType();
				persisted = true;
			} catch(Exception e) {
				throw new InternalServerErrorException(e);
			}
		}
		return persistedURL;
	}
	
	protected void deleteStorageResource(URL url, String resourceID, String mimetype) {
		persistedURL = url;
		if(isStorageFile(persistedURL)) {
			try {
				GXHTTPStringRequest gxhttpStringRequest = GXHTTPStringRequest.newRequest(persistedURL.toString());
				HttpURLConnection httpURLConnection = gxhttpStringRequest.from(Constants.CATALOGUE_NAME).head();
				String storageHubContentType = httpURLConnection.getContentType().split(";")[0];
				if(mimetype.compareTo(storageHubContentType) != 0) {
					mimetype = storageHubContentType;
					// Using storage hub mimetype
				}
			} catch(Exception e) {
				// using provided mimetype
			}
			storageHubManagement = new CatalogueStorageHubManagement();
			try {
				storageHubManagement.deleteResourcePersistence(itemID, resourceID, mimetype);
			} catch(Exception e) {
				throw new InternalServerErrorException(e);
			}
		}
	}
	
	protected String create(JsonNode jsonNode) {
		try {
			ObjectNode objectNode = validate(jsonNode);
			objectNode = persistStorageFile(objectNode);
			String ret = super.create(getAsString(objectNode));
			if(persisted) {
				String gotResourceID = result.get(ID_KEY).asText();
				if(gotResourceID != null && gotResourceID.compareTo(resourceID) != 0) {
					resourceID = gotResourceID;
					String revisionID = result.get(REVISION_ID_KEY).asText();
					storageHubManagement.renameFile(resourceID, revisionID);
				}
			}
			return ret;
		} catch(WebApplicationException e) {
			// TODO Remove created file if any
			throw e;
		} catch(Exception e) {
			// TODO Remove created file if any
			throw new InternalServerErrorException(e);
		}
	}
	
	@Override
	public String create(String json) {
		JsonNode jsonNode = getAsJsonNode(json);
		return create(jsonNode);
	}
	
	@Override
	public String read() {
		return sendGetRequest(READ, getMapWithID(resourceID));
	}
	
	protected String update(JsonNode jsonNode) {
		ObjectNode resourceNode = (ObjectNode) jsonNode;
		// This cannot be moved outside otherwise we don't 
		resourceNode = validate(resourceNode);
		
		getPreviousRepresentation();
		
		String oldURL = previousRepresentation.get(CKANResource.URL_KEY).asText();
		String newURL = resourceNode.get(CKANResource.URL_KEY).asText();
		if(oldURL.compareTo(newURL) == 0) {
			logger.trace("The URL of the resource with id {} was not changed", resourceID);
		} else {
			logger.trace("The URL of resource with id {} has been changed the old URL was {}, the new URL is {}",
					resourceID, oldURL, newURL);
			resourceNode = persistStorageFile(resourceNode);
			/*
			try {
				URL urlOLD = new URL(oldURL);
				deleteStorageResource(urlOLD);
			}catch (Exception e) {
				logger.error("Unable to remove old file at URL {}", oldURL);
			}
			*/
		}
		String ret = super.update(getAsString(resourceNode));
		String revisionID = result.get(REVISION_ID_KEY).asText();
		storageHubManagement.addRevisionID(resourceID, revisionID);
		return ret;
	}
	
	@Override
	public String update(String json) {
		JsonNode jsonNode = getAsJsonNode(json);
		return update(jsonNode);
	}
	
	@Override
	public String patch(String json) {
		String[] moreAllowed = new String[] {HEAD.class.getSimpleName(), GET.class.getSimpleName(),
				PUT.class.getSimpleName(), DELETE.class.getSimpleName()};
		throw new NotAllowedException(OPTIONS.class.getSimpleName(), moreAllowed);
	}
	
	@Override
	public void delete(boolean purge) {
		delete();
	}
	
	@Override
	public void delete() {
		try {
			deleteFile();
			sendPostRequest(DELETE, createJsonNodeWithID(resourceID));
		} catch(WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			throw new WebApplicationException(e);
		}
	}
	
	@Override
	protected void purge() {
		String[] moreAllowed = new String[] {HEAD.class.getSimpleName(), GET.class.getSimpleName(),
				PUT.class.getSimpleName(), DELETE.class.getSimpleName()};
		throw new NotAllowedException(OPTIONS.class.getSimpleName(), moreAllowed);
	}
	
	public JsonNode createOrUpdate(JsonNode jsonNode) {
		ObjectNode resourceNode = (ObjectNode) jsonNode;
		if(resourceNode.has(ID_KEY)) {
			update(resourceNode);
		} else {
			create(resourceNode);
		}
		return result;
	}
	
	public void deleteFile() {
		try {
			getPreviousRepresentation();
			URL url = new URL(previousRepresentation.get(URL_KEY).asText());
			mimeType = previousRepresentation.get(MIME_TYPE_KEY).asText();
			deleteStorageResource(url, resourceID, mimeType);
		} catch(Exception e) {
			logger.error("Unable to delete resource {}",
					previousRepresentation != null ? getAsString(previousRepresentation) : "");
		}
	}
	
	public void rollback() {
		if(previousRepresentation != null) {
			update(previousRepresentation);
		} else {
			delete();
		}
	}
	
}
