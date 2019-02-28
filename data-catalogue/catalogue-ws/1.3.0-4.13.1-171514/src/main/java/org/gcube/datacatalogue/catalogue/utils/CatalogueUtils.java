package org.gcube.datacatalogue.catalogue.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.cache.Cache;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueRunningCluster;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.gcube.datacatalogue.metadatadiscovery.DataCalogueMetadataFormatReader;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataProfile;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.NamespaceCategory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanOrganization;

/**
 * Utils methods.
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
@SuppressWarnings("unchecked")
public class CatalogueUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(CatalogueUtils.class);
	
	/**
	 * Retrieve an instance of the library for the current scope
	 * @return
	 * @throws Exception 
	 */
	public static DataCatalogue getCatalogue() throws Exception {
		String context = ScopeProvider.instance.get();
		logger.debug("Discovering ckan instance in context {}", context);
		return DataCatalogueFactory.getFactory().getUtilsPerScope(context);
	}
	
	/**
	 * Get the group hierarchy
	 * @param groupName
	 * @param isApplication 
	 * @return
	 * @throws Exception 
	 */
	public static List<String> getGroupHierarchyNames(String groupName) throws Exception {
		List<String> toReturn = new ArrayList<String>();
		String apiKey = CatalogueUtils.getApiKey();
		List<CkanGroup> ckanGroups = getCatalogue().getParentGroups(groupName, apiKey);
		if(ckanGroups != null && !ckanGroups.isEmpty()) {
			for(CkanGroup ckanGroup : ckanGroups) {
				toReturn.add(ckanGroup.getName());
			}
		}
		return toReturn;
	}
	
	private static DataCalogueMetadataFormatReader getDataCalogueMetadataFormatReader() throws Exception {
		Cache<String, DataCalogueMetadataFormatReader> readerCache = CachesManager.getReaderCache();
		String context = ScopeProvider.instance.get();
		DataCalogueMetadataFormatReader reader;
		if(readerCache.containsKey(context))
			reader = (DataCalogueMetadataFormatReader) readerCache.get(context);
		else {
			reader = new DataCalogueMetadataFormatReader();
			readerCache.put(context, reader);
		}
		return reader;
	}
	
	/**
	 * Returns the names of the metadata profiles in a given context
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	public static List<String> getProfilesNames() throws Exception {
		
		DataCalogueMetadataFormatReader reader = getDataCalogueMetadataFormatReader();
		
		List<String> toReturn = new ArrayList<String>();
		List<MetadataProfile> listProfiles = reader.getListOfMetadataProfiles();
		
		if(listProfiles != null && !listProfiles.isEmpty()) {
			for(MetadataProfile profile : listProfiles) {
				toReturn.add(profile.getName());
			}
		}
		
		return toReturn;
	}
	
	/**
	 * Returns the source xml of the metadata profile (specified via name) in a given context
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	public static String getProfileSource(String profileName) throws Exception {
		
		DataCalogueMetadataFormatReader reader = getDataCalogueMetadataFormatReader();
		
		List<MetadataProfile> listProfiles = reader.getListOfMetadataProfiles();
		String xmlToReturn = null;
		
		if(listProfiles != null && !listProfiles.isEmpty()) {
			for(MetadataProfile profile : listProfiles) {
				if(profile.getName().equals(profileName)) {
					xmlToReturn = reader.getMetadataFormatForMetadataProfile(profile).getMetadataSource();
					break;
				}
			}
		}
		
		return xmlToReturn;
	}
	
	/**
	 * Returns the categories.
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	public static List<NamespaceCategory> getNamespaceCategories() throws Exception {
		DataCalogueMetadataFormatReader reader = getDataCalogueMetadataFormatReader();
		return reader.getListOfNamespaceCategories();
		
	}
	
	/**
	 * Returns the metadataform of the metadata profile (specified via name) in a given context
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	public static MetadataFormat getMetadataProfile(String profileName) throws Exception {
		
		DataCalogueMetadataFormatReader reader = getDataCalogueMetadataFormatReader();
		List<MetadataProfile> listProfiles = reader.getListOfMetadataProfiles();
		
		if(listProfiles != null && !listProfiles.isEmpty()) {
			for(MetadataProfile profile : listProfiles) {
				if(profile.getName().equals(profileName)) {
					return reader.getMetadataFormatForMetadataProfile(profile);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Create a string representing an error message on failure
	 * @param errorMessage
	 * @return
	 */
	public static String createJSONOnFailure(String errorMessage) {
		return createJSONObjectMin(false, errorMessage).toJSONString();
	}
	
	/**
	 * JSONObject containing minimum information to be set
	 * @return
	 */
	public static JSONObject createJSONObjectMin(boolean success, String errorMessage) {
		JSONObject obj = new JSONObject();
		obj.put(Constants.HELP_KEY, Constants.HELP_URL_GCUBE_CATALOGUE);
		obj.put(Constants.SUCCESS_KEY, success);
		if(errorMessage != null) {
			obj.put(Constants.MESSAGE_ERROR_KEY, errorMessage);
		}
		return obj;
	}
	
	/**
	 * Check if the create-item request can be executed
	 * @param username
	 * @param organization
	 * @throws Exception 
	 */
	public static void checkRole(String organization) throws Exception {
		DataCatalogue dataCatalogue = getCatalogue();
		
		// check organization exists
		CkanOrganization org = dataCatalogue.getOrganizationByName(organization);
		
		if(org == null) {
			throw new Exception("It seems that an organization with name " + organization + " doesn't exist!");
		}
		
		Map<String,Map<CkanOrganization,RolesCkanGroupOrOrg>> rolesPerOrganization = dataCatalogue
				.getUserRoleByOrganization(ContextUtils.getUsername(),
						dataCatalogue.getApiKeyFromUsername(ContextUtils.getUsername()));
		
		if(rolesPerOrganization.get(org.getName()).values().contains(RolesCkanGroupOrOrg.MEMBER)) {
			throw new Exception("It seems you are neither Catalogue-Admin nor Catalogue-Editor in organization "
					+ organization + "!");
		}
	}
	
	/**
	 * Execute the GET http request at this url, and return the result as string
	 * @return
	 * @throws Exception 
	 */
	public static JSONObject getUserProfile() throws Exception {
		
		ClientInfo clientInfo = AuthorizationProvider.instance.get().getClient();
		String username = clientInfo.getId();
		
		Cache<String,JSONObject> userCache = CachesManager.getUserCache();
		
		if(userCache.containsKey(username))
			return userCache.get(username);
		else {
			GcoreEndpointReaderSNL socialService = new GcoreEndpointReaderSNL();
			String socialServiceUrl = socialService.getServiceBasePath();
			String url = socialServiceUrl + "2/users/get-profile";
			try(CloseableHttpClient client = HttpClientBuilder.create().build();) {
				HttpGet getRequest = new HttpGet(url + "?gcube-token=" + SecurityTokenProvider.instance.get());
				HttpResponse response = client.execute(getRequest);
				JSONParser parser = new JSONParser();
				JSONObject profile = (JSONObject) parser.parse(EntityUtils.toString(response.getEntity()));
				userCache.put(username, profile);
				return profile;
			} catch(Exception e) {
				logger.error("error while performing get method " + e.toString());
				throw e;
			}
		}
	}
	
	/**
	 * Fetch the sysadmin key from the IS
	 * @return
	 * @throws Exception 
	 */
	public static String fetchSysAPI() throws Exception {
		DataCatalogueRunningCluster catalogueRunningInstance = new DataCatalogueRunningCluster(
				ContextUtils.getContext());
		return catalogueRunningInstance.getSysAdminToken();
	}
	
	public static String getApiKey() throws Exception {
		return ContextUtils.isApplication() ? fetchSysAPI()
				: getCatalogue().getApiKeyFromUsername(ContextUtils.getUsername());
	}
	
	public static String getIdFromUriInfo(String idName, UriInfo uriInfo) throws Exception {
		MultivaluedMap<String,String> queryParams = uriInfo.getQueryParameters(false);
		List<String> ids = queryParams.get(idName);
		if(ids == null || ids.isEmpty()) {
			throw new Exception("'" + idName + "' field is missing!");
		}else if(ids.size()>1) {
			throw new Exception("More than one '\" + idName + \"' has been provided!");
		}
		return ids.get(0);
	}
	
	public static String getIdFromJSONString(String idName, String jsonString) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
		String id = (String) jsonObject.get(idName);
		if(id == null || id.isEmpty()) {
			throw new Exception("'" + idName + "' field is missing!");
		}
		return id;
	}
	
}
