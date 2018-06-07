package org.gcube.datacatalogue.catalogue.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.gcube.common.authorization.library.ClientType;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.utils.Caller;
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
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanOrganization;

/**
 * Utils methods.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings("unchecked")
public class CatalogueUtils {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CatalogueUtils.class);

	/**
	 * Retrieve an instance of the library for the current scope
	 * @return
	 * @throws Exception 
	 */
	public static DataCatalogue getCatalogue() throws Exception{

		String context = ScopeProvider.instance.get();
		logger.debug("Discovering ckan instance into scope " + context);
		return DataCatalogueFactory.getFactory().getUtilsPerScope(context);
	}

	/**
	 * Retrieve an instance of the library for the scope
	 * @param scope if it is null it is evaluated from the session
	 * @return
	 * @throws Exception 
	 */
	public static CkanGroup createGroupAsSysAdmin(String title, String groupName, String description) throws Exception{
		return getCatalogue().createGroup(groupName, title, description);
	}

	/**
	 * Get the group hierarchy
	 * @param groupName
	 * @param isApplication 
	 * @return
	 * @throws Exception 
	 */
	public static List<String> getGroupHierarchyNames(String groupName, String username, boolean isApplication) throws Exception{

		List<String> toReturn = new ArrayList<String>();
		String apiKey = isApplication ? fetchSysAPI(ScopeProvider.instance.get()): getCatalogue().getApiKeyFromUsername(username);
		List<CkanGroup> ckanGroups = getCatalogue().getParentGroups(groupName, apiKey);
		if(ckanGroups != null && !ckanGroups.isEmpty()){
			for (CkanGroup ckanGroup : ckanGroups) {
				toReturn.add(ckanGroup.getName());
			}
		}

		return toReturn;

	}

	/**
	 * Returns the names of the metadata profiles in a given context
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	public static List<String> getProfilesNames() throws Exception{

		Cache profilesCache = CachesManager.getCache(CachesManager.PROFILES_READERS_CACHE);
		String context = ScopeProvider.instance.get();
		List<String> toReturn = new ArrayList<String>();

		DataCalogueMetadataFormatReader reader;
		if(profilesCache.isKeyInCache(context))
			reader = (DataCalogueMetadataFormatReader) profilesCache.get(context).getObjectValue();
		else{
			reader = new DataCalogueMetadataFormatReader();
			profilesCache.put(new Element(context, reader));
		}

		List<MetadataProfile> listProfiles = reader.getListOfMetadataProfiles();

		if(listProfiles != null && !listProfiles.isEmpty()){
			for (MetadataProfile profile : listProfiles) {
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
	public static String getProfileSource(String profileName) throws Exception{

		Cache profilesCache = CachesManager.getCache(CachesManager.PROFILES_READERS_CACHE);
		String context = ScopeProvider.instance.get();

		DataCalogueMetadataFormatReader reader;
		if(profilesCache.isKeyInCache(context))
			reader = (DataCalogueMetadataFormatReader) profilesCache.get(context).getObjectValue();
		else{
			reader = new DataCalogueMetadataFormatReader();
			profilesCache.put(new Element(context, reader));
		}

		List<MetadataProfile> listProfiles = reader.getListOfMetadataProfiles();
		String xmlToReturn = null;

		if(listProfiles != null && !listProfiles.isEmpty()){
			for (MetadataProfile profile : listProfiles) {
				if(profile.getName().equals(profileName)){
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
	public static List<NamespaceCategory> getNamespaceCategories() throws Exception{

		Cache profilesCache = CachesManager.getCache(CachesManager.PROFILES_READERS_CACHE);
		String context = ScopeProvider.instance.get();

		DataCalogueMetadataFormatReader reader;
		if(profilesCache.isKeyInCache(context))
			reader = (DataCalogueMetadataFormatReader) profilesCache.get(context).getObjectValue();
		else{
			reader = new DataCalogueMetadataFormatReader();
			profilesCache.put(new Element(context, reader));
		}

		return reader.getListOfNamespaceCategories();

	}

	/**
	 * Returns the metadataform of the metadata profile (specified via name) in a given context
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	public static MetadataFormat getMetadataProfile(String profileName) throws Exception{

		Cache profilesCache = CachesManager.getCache(CachesManager.PROFILES_READERS_CACHE);
		String context = ScopeProvider.instance.get();

		DataCalogueMetadataFormatReader reader;
		if(profilesCache.isKeyInCache(context))
			reader = (DataCalogueMetadataFormatReader) profilesCache.get(context).getObjectValue();
		else{
			reader = new DataCalogueMetadataFormatReader();
			profilesCache.put(new Element(context, reader));
		}

		List<MetadataProfile> listProfiles = reader.getListOfMetadataProfiles();

		if(listProfiles != null && !listProfiles.isEmpty()){
			for (MetadataProfile profile : listProfiles) {
				if(profile.getName().equals(profileName)){
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
	public static String createJSONOnFailure(String errorMessage){
		return createJSONObjectMin(false, errorMessage).toJSONString();
	}

	/**
	 * JSONObject containing minimum information to be set
	 * @return
	 */
	public static JSONObject createJSONObjectMin(boolean success, String errorMessage){

		JSONObject obj = new JSONObject();
		obj.put(Constants.HELP_KEY, Constants.HELP_URL_GCUBE_CATALOGUE);
		obj.put(Constants.SUCCESS_KEY, success);
		if(errorMessage != null)
			obj.put(Constants.MESSAGE_ERROR_KEY, errorMessage);
		return obj;

	}

	/**
	 * Check if the create-item request can be executed
	 * @param username
	 * @param organization
	 * @throws Exception 
	 */
	public static void checkRole(String username, String organization) throws Exception {

		DataCatalogue catalogue = getCatalogue();

		// check organization exists
		CkanOrganization org = catalogue.getOrganizationByName(organization);

		if(org == null)
			throw new Exception("It seems that an organization with name " + organization + " doesn't exist!");	

		Map<String, Map<CkanOrganization, RolesCkanGroupOrOrg>> rolesPerOrganization = catalogue.getUserRoleByOrganization(username, catalogue.getApiKeyFromUsername(username));

		if(rolesPerOrganization.get(org.getName()).values().contains(RolesCkanGroupOrOrg.MEMBER))
			throw new Exception("It seems you are neither Catalogue-Admin nor Catalogue-Editor in organization " + organization + "!");	
	}


	/**
	 * Execute the GET http request at this url, and return the result as string
	 * @return
	 * @throws Exception 
	 */
	public static JSONObject getUserProfile(String userId) throws Exception{

		Cache profilesCache = CachesManager.getCache(CachesManager.PROFILES_USERS_CACHE);

		if(profilesCache.isKeyInCache(userId))
			return (JSONObject) profilesCache.get(userId).getObjectValue();
		else{
			GcoreEndpointReaderSNL socialService = new GcoreEndpointReaderSNL();
			String socialServiceUrl = socialService.getServiceBasePath();
			String url = socialServiceUrl + "2/users/get-profile";
			try(CloseableHttpClient client = HttpClientBuilder.create().build();){
				HttpGet getRequest = new HttpGet(url + "?gcube-token=" + SecurityTokenProvider.instance.get());
				HttpResponse response = client.execute(getRequest);
				JSONParser parser = new JSONParser();
				JSONObject profile = (JSONObject)parser.parse(EntityUtils.toString(response.getEntity()));
				profilesCache.put(new Element(userId, profile));
				return profile;
			}catch(Exception e){
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
	public static String fetchSysAPI(String context) throws Exception{

		DataCatalogueRunningCluster catalogueRunningInstance = new DataCatalogueRunningCluster(context);
		return catalogueRunningInstance.getSysAdminToken();

	}

	/**
	 * Check if the token belongs to an application token
	 * @param caller
	 * @return
	 */
	public static boolean isApplicationToken(Caller caller){

		return caller.getClient().getType().equals(ClientType.EXTERNALSERVICE);

	}

}
