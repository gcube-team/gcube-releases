package org.gcube.datacatalogue.catalogue.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.datacatalogue.catalogue.beans.resource.CustomField;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.UtilMethods;
import org.gcube.datacatalogue.metadatadiscovery.DataCalogueMetadataFormatReader;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataProfile;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.DataType;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataField;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataGrouping;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataTagging;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.NamespaceCategory;
import org.geojson.GeoJsonObject;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.trentorise.opendata.jackan.model.CkanGroup;

/**
 * Utils class and methods.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class CatalogueUtils {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CatalogueUtils.class);
	public static final String HELP_URL_GCUBE_CATALOGUE = "https://wiki.gcube-system.org/gcube/GCube_Data_Catalogue";
	public static final String HELP_KEY = "help";
	public static final String DATASET_KEY = "id";
	public static final String SUCCESS_KEY = "success";
	public static final String MESSAGE_ERROR_KEY = "message";
	public static final String EXTRA_KEY = "key";
	public static final String EXTRA_VALUE = "value";
	public static final String RESULT_KEY = "result";
	public static final String EXTRAS_KEY = "extras";
	public static final String TAGS_KEY = "tags";
	public static final String GROUPS_KEY = "groups";
	public static final String LICENSE_KEY = "license_id";
	public static final String AUTHOR_KEY = "author";
	public static final String RESOURCES_KEY = "resources";
	public static final String AUTHOR_EMAIL_KEY = "author_email";
	public static final String TYPE_KEY = "system:type";
	public static final String OWNER_ORG_KEY = "owner_org";
	public static final String TITLE_KEY = "title";
	public static final String VERSION_KEY = "version";
	public static final String SEPARATOR_MULTIPLE_VALUES_FIELD = ",";
	public static final int MAX_TAG_CHARS = 100;
	public static final short MAX_UPLOADABLE_FILE_SIZE_MB = 100;
	public static final String EMAIL_IN_PROFILE_KEY = "email";
	public static final String FULLNAME_IN_PROFILE_KEY = "fullname";
	private static final Object RESOURCE_NAME_KEY = "name";
	private static final Object RESOURCE_URL_KEY = "url";


	// =======================================================================
	// 							GET/PUT CATALOGUE	
	// =======================================================================

	/**
	 * Retrieve an instance of the library for the scope
	 * @param scope if it is null it is evaluated from the session
	 * @return
	 * @throws Exception 
	 */
	public static DataCatalogue getCatalogue(){

		try{
			String context = ScopeProvider.instance.get();
			logger.debug("Discovering ckan instance into scope " + context);
			return DataCatalogueFactory.getFactory().getUtilsPerScope(context);
		}catch(Exception e){
			logger.error("Unable to lookup catalogue object here ", e);
		}

		return null;
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
	 * @return
	 */
	public static List<String> getGroupHierarchyNames(String groupName, String username){

		List<String> toReturn = new ArrayList<String>();
		List<CkanGroup> ckanGroups = getCatalogue().getParentGroups(groupName, getCatalogue().getApiKeyFromUsername(username));
		if(ckanGroups != null && !ckanGroups.isEmpty()){
			for (CkanGroup ckanGroup : ckanGroups) {
				toReturn.add(ckanGroup.getName());
			}
		}

		return toReturn;

	}

	// =======================================================================
	// 							METADATA PROFILE STUFF		
	// =======================================================================

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

	// =======================================================================
	// 							JSON OBJECT MAPPINGS		
	// =======================================================================

	/**
	 * Create a string representing an error message on failure
	 * @param errorMessage
	 * @return
	 */
	public static String createJSONOnFailure(String errorMessage){

		JSONObject obj = new JSONObject();
		obj.put(HELP_KEY, HELP_URL_GCUBE_CATALOGUE);
		obj.put(SUCCESS_KEY, false);
		obj.put(MESSAGE_ERROR_KEY, errorMessage);
		return obj.toJSONString();

	}

	/**
	 * JSONObject containing minimum information to be set
	 * @return
	 */
	public static JSONObject createJSONObjectMin(boolean success, String errorMessage){

		JSONObject obj = new JSONObject();
		obj.put(HELP_KEY, HELP_URL_GCUBE_CATALOGUE);
		obj.put(SUCCESS_KEY, success);
		if(errorMessage != null)
			obj.put(MESSAGE_ERROR_KEY, errorMessage);
		return obj;

	}

	// =======================================================================
	// 							DELEGATE OPS TO CKAN	
	// =======================================================================

	/**
	 * Check resources have at least url/name
	 * @param json
	 * @param caller
	 * @throws Exception 
	 */
	public static void checkResourcesInformation(JSONObject dataset, Caller caller) throws Exception {

		JSONArray resources = (JSONArray)dataset.get(RESOURCES_KEY);

		if(resources == null || resources.isEmpty())
			return;
		else{

			Iterator it = resources.iterator();
			while (it.hasNext()) {
				JSONObject resource = (JSONObject) it.next();

				String name = (String)resource.get(RESOURCE_NAME_KEY);
				String url = (String)resource.get(RESOURCE_URL_KEY);

				if(url == null || name == null || url.isEmpty() || name.isEmpty())
					throw new Exception("Resources must have at least a name and an url field set!");

			}
		}

	}

	/**
	 * This method validates the incoming json, in this sense:
	 * <ul>
	 * <li> add author
	 * <li> add author email
	 * <li> check license (?)
	 * </ul>
	 * @param json
	 * @param caller
	 * @return
	 * @throws Exception 
	 */
	public static void checkBaseInformation(JSONObject dataset, Caller caller) throws Exception{

		JSONObject profile = getUserProfile(caller.getClient().getId());

		// check license
		String licenseId = (String)dataset.get(LICENSE_KEY);

		if(licenseId == null || licenseId.isEmpty())
			throw new Exception("You must specify a license identifier to be attached to the item. License list can be retrieved invoking license methods");

		// set author and author email
		JSONObject profileValues = (JSONObject)profile.get(RESULT_KEY);
		dataset.put(AUTHOR_KEY, profileValues.get(FULLNAME_IN_PROFILE_KEY));
		dataset.put(AUTHOR_EMAIL_KEY, profileValues.get(EMAIL_IN_PROFILE_KEY));

		// version
		String version = (String)dataset.get(VERSION_KEY);
		if(version == null || version.isEmpty()){
			version = "1";
			dataset.put(VERSION_KEY, version);
		}

		// owner organization must be specified if the token belongs to a VRE
		ScopeBean scopeBean = new ScopeBean(ScopeProvider.instance.get());
		String ownerOrgFromScope = scopeBean.name();
		boolean isVREToken = scopeBean.is(Type.VRE);
		String ownerOrg = (String)dataset.get(OWNER_ORG_KEY);

		if(isVREToken){
			dataset.put(OWNER_ORG_KEY, ownerOrgFromScope.toLowerCase().replace(" ", "_").replace("-", "_"));
		}else if(ownerOrg == null || ownerOrg.isEmpty())
			throw new Exception("You must specify the field owner_org in which the item should be published!");

	}

	/**
	 * This method validate the incoming json dataset wrt a metadata profile
	 * @param json
	 * @param caller
	 * @param profiles 
	 * @return
	 * @throws Exception 
	 */
	public static void validateAgainstProfile(JSONObject obj, Caller caller, List<String> profiles) throws Exception {

		JSONArray extrasArrayOriginal = (JSONArray)obj.get(EXTRAS_KEY);
		JSONArray groupsArrayOriginal = (JSONArray)obj.get(GROUPS_KEY);
		JSONArray tagsArrayOriginal = (JSONArray)obj.get(TAGS_KEY);

		if(extrasArrayOriginal == null || extrasArrayOriginal.isEmpty())
			throw new Exception("'extras' field is missing in context where metadata profile(s) are defined!");

		// get the metadata profile specifying the type
		CustomField metadataTypeCF = null;
		List<CustomField> customFields = new ArrayList<CustomField>(extrasArrayOriginal.size());
		Iterator iterator = extrasArrayOriginal.iterator();
		while (iterator.hasNext()) {
			JSONObject object = (JSONObject) iterator.next();
			CustomField cf = new CustomField(object);
			if(cf.getKey().equals(TYPE_KEY))
				metadataTypeCF = cf;
			else if(cf.getKey().equals(PackageCreatePostActions.ITEM_URL))
				continue;
			else
				customFields.add(cf);
		}

		if(metadataTypeCF == null)
			throw new Exception("'" + TYPE_KEY + "' extra field is missing in context where metadata profile(s) are defined!");

		if(groupsArrayOriginal == null)
			groupsArrayOriginal = new JSONArray();

		if(tagsArrayOriginal == null)
			tagsArrayOriginal = new JSONArray();

		// fetch the profile by metadata type specified above
		MetadataFormat profile = null;
		for (String profileName : profiles) {
			profile = getMetadataProfile(profileName);
			if(profile.getType().equals(metadataTypeCF.getValue()))
				break;
			else
				profile = null;
		}

		if(profile == null)
			throw new Exception("'" + TYPE_KEY + "' extra field's value specified as custom field doesn't match any of the profiles defined in this context!");
		else{

			JSONArray extrasArrayUpdated = null;
			List<MetadataField> metadataFields = profile.getMetadataFields();

			if(metadataFields == null || metadataFields.isEmpty())
				extrasArrayUpdated = extrasArrayOriginal;
			else{

				extrasArrayUpdated = new JSONArray();
				List<NamespaceCategory> categories = getNamespaceCategories();
				List<String> categoriesIds = new ArrayList<String>(categories == null ? 0 : categories.size());
				if(categoriesIds.isEmpty())
					logger.warn("No category defined in context " + ScopeProvider.instance.get());
				else{
					for (NamespaceCategory metadataCategory : categories)
						categoriesIds.add(metadataCategory.getId()); // save them later for matching with custom fields
				}

				// the list of already validated customFields
				List<CustomField> validatedCustomFields = new ArrayList<CustomField>(customFields.size());

				// keep track of mandatory fields and their cardinality
				Map<String, Integer> fieldsMandatoryLowerBoundMap = new HashMap<String, Integer>(metadataFields.size());
				Map<String, Integer> fieldsMandatoryUpperBoundMap = new HashMap<String, Integer>(metadataFields.size());
				Map<String, Integer> numberFieldsMandatorySameKeyMap = new HashMap<String, Integer>(metadataFields.size());

				// keep track of the groups that must be created AFTER validation but BEFORE item creation.
				List<String> groupsToCreateAfterValidation = new ArrayList<String>();

				// now validate fields
				int metadataIndex = 0;
				for (MetadataField metadataField : metadataFields) {

					int categoryIdIndex = categoriesIds.indexOf(metadataField.getCategoryRef());
					logger.debug("Found index for category " + metadataField.getCategoryRef() + " "  + categoryIdIndex);
					List<CustomField> validCFs = validateAgainstMetadataField(
							metadataIndex,
							categoryIdIndex,
							customFields, 
							tagsArrayOriginal, 
							groupsArrayOriginal, 
							metadataField, 
							categories, 
							fieldsMandatoryLowerBoundMap,
							fieldsMandatoryUpperBoundMap,
							numberFieldsMandatorySameKeyMap,
							groupsToCreateAfterValidation,
							caller.getClient().getId());
					validatedCustomFields.addAll(validCFs);
					metadataIndex++;

				}

				// check mandatory fields
				Iterator<Entry<String, Integer>> iteratorLowerBounds = fieldsMandatoryLowerBoundMap.entrySet().iterator();
				while (iteratorLowerBounds.hasNext()) {
					Map.Entry<java.lang.String, java.lang.Integer> entry = (Map.Entry<java.lang.String, java.lang.Integer>) iteratorLowerBounds
							.next();
					int lowerBound = entry.getValue();
					int upperBound = fieldsMandatoryUpperBoundMap.get(entry.getKey());
					int inserted = numberFieldsMandatorySameKeyMap.get(entry.getKey());

					logger.info("Field with key '" + entry.getKey() + "' has been found " + inserted + " times and its lower bound is " + lowerBound + " and upper bound " + upperBound);

					if(inserted < lowerBound || inserted > upperBound)
						throw new Exception("Field with key '" + entry.getKey() + "' is mandatory, but it's not present among the provided fields or its cardinality is not respected ([min = " + lowerBound + ", max=" + upperBound +"]).");
				}

				// sort validated custom fields and add to the extrasArrayUpdated json array 
				Collections.sort(validatedCustomFields);

				logger.debug("Sorted list of custom fields is " + validatedCustomFields);

				// add missing fields with no match (append them at the end, since no metadataIndex or categoryIndex was defined for them)
				for(CustomField cf : customFields)
					validatedCustomFields.add(cf);

				// convert back to json
				for (CustomField customField : validatedCustomFields) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put(EXTRA_KEY, customField.getQualifiedKey());
					jsonObj.put(EXTRA_VALUE, customField.getValue());
					extrasArrayUpdated.add(jsonObj);
				}

				// add metadata type field as last element
				JSONObject metadataTypeJSON = new JSONObject();
				metadataTypeJSON.put(EXTRA_KEY, metadataTypeCF.getKey());
				metadataTypeJSON.put(EXTRA_VALUE, metadataTypeCF.getValue());
				extrasArrayUpdated.add(metadataTypeJSON);

				// create groups
				for (String title : groupsToCreateAfterValidation){
					try {
						createGroupAsSysAdmin(title, title, "");
					} catch (Exception e) {
						logger.error("Failed to create group with title " + title, e);
					}
				}
			}

			// if there are no tags, throw an exception
			if(tagsArrayOriginal.isEmpty())
				throw new Exception("Please define at least one tag for this item!");

			obj.put(TAGS_KEY, tagsArrayOriginal);
			obj.put(GROUPS_KEY, groupsArrayOriginal);
			obj.put(EXTRAS_KEY, extrasArrayUpdated);

		}

	}


	/**
	 * Validate this field and generate a new value (or returns the same if there is nothing to update)
	 * @param metadataIndex
	 * @param categoryIndex
	 * @param customFields
	 * @param tagsArrayOriginal
	 * @param groupsArrayOriginal
	 * @param metadataField
	 * @param categories
	 * @param numberFieldsSameKeyMap 
	 * @param fieldsMandatoryLowerBoundMap 
	 * @return
	 * @throws Exception
	 */
	private static List<CustomField> validateAgainstMetadataField(
			int metadataIndex, 
			int categoryIndex, 
			List<CustomField> customFields, 
			JSONArray tagsArrayOriginal,
			JSONArray groupsArrayOriginal, 
			MetadataField metadataField, 
			List<NamespaceCategory> categories, 
			Map<String, Integer> fieldsMandatoryLowerBoundMap, 
			Map<String, Integer> fieldsMandatoryUpperBoundMap,
			Map<String, Integer> numberFieldsMandatorySameKeyMap,
			List<String> groupToCreate,
			String username) throws Exception {

		List<CustomField> toReturn = new ArrayList<CustomField>();

		String metadataFieldName = metadataField.getFieldName();
		int fieldsFoundWithThisKey = 0;

		Iterator<CustomField> iterator = customFields.iterator();
		while (iterator.hasNext()) {
			CustomField cf = (CustomField) iterator.next();
			if(cf.getKey().equals(metadataFieldName)){

				validate(cf, metadataField);
				fieldsFoundWithThisKey ++;
				cf.setIndexCategory(categoryIndex);
				cf.setIndexMetadataField(metadataIndex);
				checkAsGroup(cf, metadataField, groupsArrayOriginal, groupToCreate, username);
				checkAsTag(cf, metadataField, tagsArrayOriginal);
				toReturn.add(cf);
				iterator.remove();

			}
		}

		// in case of mandatory fields, keep track of the number of times they appear
		if(metadataField.getMandatory()){

			// lower bound
			int lowerBound = 1;
			if(fieldsMandatoryLowerBoundMap.containsKey(metadataFieldName))
				lowerBound = fieldsMandatoryLowerBoundMap.get(metadataFieldName) + 1;
			fieldsMandatoryLowerBoundMap.put(metadataFieldName, lowerBound);

			// upper bound
			boolean hasVocabulary = metadataField.getVocabulary() != null;
			int upperBound = hasVocabulary ? (metadataField.getVocabulary().isMultiSelection() ? metadataField.getVocabulary().getVocabularyFields().size() : 1) : 1;

			if(fieldsMandatoryUpperBoundMap.containsKey(metadataFieldName))
				upperBound += fieldsMandatoryUpperBoundMap.get(metadataFieldName);

			fieldsMandatoryUpperBoundMap.put(metadataFieldName, upperBound);

			// fields with this same key
			int countPerFields = fieldsFoundWithThisKey;
			if(numberFieldsMandatorySameKeyMap.containsKey(metadataFieldName))
				countPerFields += numberFieldsMandatorySameKeyMap.get(metadataFieldName);
			numberFieldsMandatorySameKeyMap.put(metadataFieldName, countPerFields);

		}

		// if there was no field with this key and it was not mandatory, just add an entry of the kind {"key": "key-value", "value" : ""}
		if(fieldsFoundWithThisKey == 0 && !metadataField.getMandatory()){

			toReturn.add(new CustomField(metadataField.getCategoryFieldQName(), "", -1, -1));

		}

		return toReturn;

	}

	/**
	 * Check if a tag must be generated
	 * @param fieldToValidate
	 * @param metadataField
	 * @param tagsArrayOriginal
	 */
	private static void checkAsTag(CustomField fieldToValidate,
			MetadataField metadataField, JSONArray tagsArrayOriginal) {
		MetadataTagging tagging = metadataField.getTagging();
		if(tagging != null){

			String tag = "";

			switch(tagging.getTaggingValue()){
			case onFieldName:
				tag = fieldToValidate.getKey();
				break;
			case onValue:
				tag = fieldToValidate.getValue();
				break;
			case onFieldName_onValue:
				tag = fieldToValidate.getKey() + tagging.getSeparator() + fieldToValidate.getValue();
				break;
			case onValue_onFieldName:
				tag = fieldToValidate.getValue() + tagging.getSeparator() + fieldToValidate.getKey();
				break;
			default: 
				return;
			}

			tag = tag.substring(0, MAX_TAG_CHARS > tag.length() ? tag.length() : MAX_TAG_CHARS);
			logger.debug("Tag is " + tag);

			JSONObject tagJSON = new JSONObject();
			tagJSON.put("name", tag);
			tagJSON.put("display_name", tag);
			tagsArrayOriginal.add(tagJSON);

		}

	}

	/**
	 * Check if a group must be generated
	 * @param fieldToValidate
	 * @param metadataField
	 * @param groupsArrayOriginal
	 */
	private static void checkAsGroup(CustomField fieldToValidate,
			MetadataField metadataField, JSONArray groupsArrayOriginal, List<String> groupToCreate, String username) {

		logger.debug("Custom field is " + fieldToValidate);
		logger.debug("MetadataField field is " + metadataField);
		logger.debug("JSONArray field is " + groupsArrayOriginal);

		MetadataGrouping grouping = metadataField.getGrouping();
		if(grouping != null){

			boolean propagateUp = grouping.getPropagateUp();
			final Set<String> groupNames = new HashSet<String>();

			switch(grouping.getGroupingValue()){
			case onFieldName:
				groupNames.add(fieldToValidate.getKey());
				break;
			case onValue:
				if(fieldToValidate.getValue() != null && !fieldToValidate.getValue().isEmpty())
					groupNames.add(fieldToValidate.getValue());
				break;
			case onFieldName_onValue:
			case onValue_onFieldName:
				groupNames.add(fieldToValidate.getKey());
				if(fieldToValidate.getValue() != null && !fieldToValidate.getValue().isEmpty())
					groupNames.add(fieldToValidate.getValue());
				break;
			default: 
				return;
			}

			for (String title : groupNames) {
				logger.debug("Adding group to which add this item " + UtilMethods.fromGroupTitleToName(title));
				JSONObject group = new JSONObject();
				group.put("name", UtilMethods.fromGroupTitleToName(title));
				if(propagateUp){
					List<String> parents = getGroupHierarchyNames(UtilMethods.fromGroupTitleToName(title), username);
					for (String parent : parents) {
						JSONObject groupP = new JSONObject();
						groupP.put("name", parent);
						groupsArrayOriginal.add(groupP);
					}
				}
				groupsArrayOriginal.add(group);
			}

			// force group creation if needed
			if(grouping.getCreate()){
				for (String title : groupNames)
					groupToCreate.add(title);
			}
		}

	}

	/**
	 * Validate the single field
	 * @param fieldToValidate
	 * @param metadataField
	 * @param isFirst 
	 * @return
	 * @throws Exception 
	 */
	private static void validate(CustomField fieldToValidate,
			MetadataField metadataField) throws Exception {

		DataType dataType = metadataField.getDataType();
		String regex = metadataField.getValidator() != null ? metadataField.getValidator().getRegularExpression() : null;
		boolean hasControlledVocabulary = metadataField.getVocabulary() != null;
		String value = fieldToValidate.getValue();
		String key = fieldToValidate.getKey();
		String defaultValue = metadataField.getDefaultValue();

		// replace key by prepending the qualified name of the category, if needed
		fieldToValidate.setQualifiedKey(metadataField.getCategoryFieldQName());

		if((value == null || value.isEmpty()))
			if(metadataField.getMandatory() || hasControlledVocabulary)
				throw new Exception("Mandatory field with name '" + key + "' doesn't have a value but it is mandatory or has a controlled vocabulary!");
			else {
				if(defaultValue != null && !defaultValue.isEmpty()){
					value = defaultValue;
					fieldToValidate.setValue(defaultValue);
				}
				return; // there is no need to check other stuff
			}

		switch(dataType){

		case String:
		case Text:

			if(regex != null && !value.matches(regex))
				throw new Exception("Field with key '" + key + "' doesn't match the provided regular expression (" + regex + ")!");

			if(hasControlledVocabulary){

				List<String> valuesVocabulary = metadataField.getVocabulary().getVocabularyFields();

				if(valuesVocabulary == null || valuesVocabulary.isEmpty())
					return;
				
				boolean match = false;
				for (String valueVocabulary : valuesVocabulary) {
					match = value.equals(valueVocabulary);
					if(match)
						break;
				}

				if(!match)
					throw new Exception("Field with key '" + key + "' has a value '" + value + "' but it doesn't match any of the vocabulary's values!");

			}

			break;
		case Time:

			if(!isValidDate(value))
				throw new Exception("Field with key '" + key + "' doesn't seem a valid time!");

			break;
		case Time_Interval:

			String[] timeValues = value.split("/");
			for (int i = 0; i < timeValues.length; i++) {
				String time = timeValues[i];
				if(!isValidDate(time))
					throw new Exception("Field with key '" + key + "' doesn't seem a valid time interval!");
			}

			break;
		case Times_ListOf:

			String[] timeIntervals = value.split(",");
			for (int i = 0; i < timeIntervals.length; i++) {
				String[] timeIntervalValues = timeIntervals[i].split("/");
				if(timeIntervalValues.length > 2)
					throw new Exception("Field with key '" + key + "' doesn't seem a valid list of times!");
				for (i = 0; i < timeIntervalValues.length; i++) {
					String time = timeIntervalValues[i];
					if(!isValidDate(time))
						throw new Exception("Field with key '" + key + "' doesn't seem a valid list of times!");
				}
			}

			break;
		case Boolean:

			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {

			}else 
				throw new Exception("Field with key '" + key + "' doesn't seem a valid boolean value!");

			break;
		case Number:

			if(!NumberUtils.isNumber(value))
				throw new Exception("Field's value with key '" + key + "' is not a valid number!");

			break;
		case GeoJSON:

			try{
				new ObjectMapper().readValue(fieldToValidate.getValue(), GeoJsonObject.class);
			}catch(Exception e){
				throw new Exception("GeoJSON field with key '" + key + "' seems not valid!");
			}

			break;
		default:
			break;
		}

	}

	private static final SimpleDateFormat DATE_SIMPLE = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DATE_HOUR_MINUTES = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/**
	 * Validate a time date against a formatter
	 * @param value
	 * @param formatter
	 * @return
	 */
	private static boolean isValidDate(String value) {

		try{
			DATE_HOUR_MINUTES.parse(value);
			return true;
		}catch(Exception e){
			logger.debug("failed to parse date with hours and minutes, trying the other one");
			try{
				DATE_SIMPLE.parse(value);
				return true;
			}catch(Exception e2){
				logger.warn("failed to parse date with simple format, returning false");
				return false;
			}
		}

	}

	/**
	 * Execute the get
	 * @param caller
	 * @param context
	 * @param method
	 * @param uriInfo
	 * @return
	 * @throws Exception 
	 */
	public static String delegateGet(Caller caller, String context, String method, UriInfo uriInfo){

		DataCatalogue catalogue = CatalogueUtils.getCatalogue();
		String username = caller.getClient().getId();

		if(catalogue == null){
			String msg  = "There is no catalogue instance in context " + context + " or a temporary problem arised.";
			logger.warn(msg);
			return CatalogueUtils.createJSONOnFailure(msg);
		}else{
			try(CloseableHttpClient client = HttpClientBuilder.create().build();){

				String authorization = catalogue.getApiKeyFromUsername(username);
				String requestPath = catalogue.getCatalogueUrl().endsWith("/") ? catalogue.getCatalogueUrl() : catalogue.getCatalogueUrl() + "/";
				requestPath += method;
				MultivaluedMap<String, String> undecodedParams = uriInfo.getQueryParameters(false);
				Iterator<Entry<String, List<String>>> iterator = undecodedParams.entrySet().iterator();

				while (iterator.hasNext()) {
					Map.Entry<java.lang.String, java.util.List<java.lang.String>> entry = (Map.Entry<java.lang.String, java.util.List<java.lang.String>>) iterator
							.next();

					if(entry.getKey().equals("gcube-token"))
						continue;
					else{

						List<String> values = entry.getValue();
						for (String value : values) {
							requestPath += entry.getKey() + "=" + value + "&";
						}
					}
				}

				if(requestPath.endsWith("&"))
					requestPath = requestPath.substring(0, requestPath.length() - 1); 
				HttpGet request = new HttpGet(requestPath);
				if(authorization != null)
					request.addHeader(Constants.AUTH_CKAN_HEADER, authorization);

				logger.debug("******* REQUEST URL IS " + requestPath);

				HttpEntity entityRes = client.execute(request).getEntity();
				String json = EntityUtils.toString(entityRes);

				// substitute "help" field
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(json);
				obj.put(HELP_KEY, HELP_URL_GCUBE_CATALOGUE);
				return obj.toJSONString();

			}catch(Exception e){
				logger.error("Failed to serve the request", e);
				return CatalogueUtils.createJSONOnFailure("Failed to serve the request: " + e.getMessage());
			}
		}

	}

	/**
	 * Execute the post
	 * @param caller
	 * @param context
	 * @param groupShow
	 * @param json
	 * @param uriInfo 
	 * @throws Exception 
	 */
	public static String delegatePost(Caller caller, String context,
			String method, String json, UriInfo uriInfo){

		String username = caller.getClient().getId();
		DataCatalogue catalogue = CatalogueUtils.getCatalogue();

		if(catalogue == null){
			String msg  = "There is no catalogue instance in context " + context + " or a temporary problem arised.";
			logger.warn(msg);
			return CatalogueUtils.createJSONOnFailure(msg);
		}else{

			try(CloseableHttpClient client = HttpClientBuilder.create().build();){

				String authorization = catalogue.getApiKeyFromUsername(username);
				String requestPath = catalogue.getCatalogueUrl().endsWith("/") ? catalogue.getCatalogueUrl() : catalogue.getCatalogueUrl() + "/";
				requestPath += method + "?";

				MultivaluedMap<String, String> undecodedParams = uriInfo.getQueryParameters(false);
				Iterator<Entry<String, List<String>>> iterator = undecodedParams.entrySet().iterator();

				while (iterator.hasNext()) {
					Map.Entry<java.lang.String, java.util.List<java.lang.String>> entry = (Map.Entry<java.lang.String, java.util.List<java.lang.String>>) iterator
							.next();

					if(entry.getKey().equals("gcube-token"))
						continue;
					else{

						List<String> values = entry.getValue();
						for (String value : values) {
							requestPath += entry.getKey() + "=" + value + "&";
						}
					}
				}

				if(requestPath.endsWith("&"))
					requestPath = requestPath.substring(0, requestPath.length() - 1); 

				logger.debug("POST request url is going to be " + requestPath);

				HttpPost request = new HttpPost(requestPath);
				request.addHeader(Constants.AUTH_CKAN_HEADER, authorization);
				logger.debug("Sending json to CKAN is " + json);
				StringEntity params = new StringEntity(json, ContentType.APPLICATION_JSON);
				request.setEntity(params);
				HttpEntity entityRes = client.execute(request).getEntity();
				String jsonRes = EntityUtils.toString(entityRes);

				logger.debug("Result from CKAN is " + jsonRes);

				// substitute "help" field
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(jsonRes);
				obj.put(HELP_KEY, HELP_URL_GCUBE_CATALOGUE);

				logger.debug("replaced information " + obj);
				return obj.toJSONString();

			}catch(Exception e){
				logger.error("Failed to serve the request", e);
				return CatalogueUtils.createJSONOnFailure("Failed to serve the request: " + e.getMessage());
			}
		}

	}

	/**
	 * Execute post with multipart (e.g. for resource upload)
	 * @param caller
	 * @param context
	 * @param resourceCreate
	 * @param multiPart
	 * @param uriInfo
	 * @return
	 */
	public static String delegatePost(Caller caller, String context,
			String method, FormDataMultiPart multiPart, UriInfo uriInfo) {

		String username = caller.getClient().getId();
		DataCatalogue catalogue = CatalogueUtils.getCatalogue();

		if(catalogue == null){
			String msg  = "There is no catalogue instance in context " + context + " or a temporary problem arised.";
			logger.warn(msg);
			return CatalogueUtils.createJSONOnFailure(msg);
		}else{

			try{

				String authorization = catalogue.getApiKeyFromUsername(username);
				String requestPath = catalogue.getCatalogueUrl().endsWith("/") ? catalogue.getCatalogueUrl() : catalogue.getCatalogueUrl() + "/";
				requestPath += method + "?";

				MultivaluedMap<String, String> undecodedParams = uriInfo.getQueryParameters(false);
				Iterator<Entry<String, List<String>>> iterator = undecodedParams.entrySet().iterator();

				while (iterator.hasNext()) {
					Map.Entry<java.lang.String, java.util.List<java.lang.String>> entry = (Map.Entry<java.lang.String, java.util.List<java.lang.String>>) iterator
							.next();

					if(entry.getKey().equals("gcube-token"))
						continue;
					else{

						List<String> values = entry.getValue();
						for (String value : values) {
							requestPath += entry.getKey() + "=" + value + "&";
						}
					}
				}

				if(requestPath.endsWith("&"))
					requestPath = requestPath.substring(0, requestPath.length() - 1); 

				logger.debug("POST request url is going to be " + requestPath);

				// use jersey client
				logger.debug("Sending multipart to CKAN " + multiPart);

				FormDataBodyPart upload = multiPart.getField("upload");
				if(upload != null){
					File file = upload.getValueAs(File.class);
					long fileLenghtBytes = file.length();
					long fileLenghtMegaByte = fileLenghtBytes >> 20;
				logger.debug("File lenght in MegaByte is " + fileLenghtMegaByte);

				if(fileLenghtMegaByte > MAX_UPLOADABLE_FILE_SIZE_MB)
					throw new Exception("Exceeding maximum uploadable file size!");

				}else
					throw new Exception("No 'upload' field has been provided!");

				Client client = ClientBuilder.newClient();
				client.register(MultiPartFeature.class);
				WebTarget webResource = client.target(requestPath);
				JSONObject jsonRes = 
						webResource
						.request(MediaType.APPLICATION_JSON)						
						.header(Constants.AUTH_CKAN_HEADER, authorization)
						.post(Entity.entity(multiPart, multiPart.getMediaType()), JSONObject.class);

				logger.debug("Result from CKAN is " + jsonRes);

				// substitute "help" field
				jsonRes.put(HELP_KEY, HELP_URL_GCUBE_CATALOGUE);
				return jsonRes.toJSONString();

			}catch(Exception e){
				logger.error("Failed to serve the request", e);
				return CatalogueUtils.createJSONOnFailure("Failed to serve the request: " + e.getMessage());
			}
		}
	}

	// =======================================================================
	// 							SOCIAL FACILITIES	
	// =======================================================================

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

}
