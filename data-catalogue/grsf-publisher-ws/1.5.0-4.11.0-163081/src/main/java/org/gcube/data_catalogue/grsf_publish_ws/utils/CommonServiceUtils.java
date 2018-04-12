package org.gcube.data_catalogue.grsf_publish_ws.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CkanResource;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CustomField;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Group;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Tag;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.others.RefersToBean;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.others.Resource;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.others.TimeSeriesBean;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.record.Base;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.record.Common;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.record.FisheryRecord;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.record.StockRecord;
import org.gcube.data_catalogue.grsf_publish_ws.json.output.ResponseCreationBean;
import org.gcube.data_catalogue.grsf_publish_ws.utils.csv.ManageTimeSeriesThread;
import org.gcube.data_catalogue.grsf_publish_ws.utils.threads.AssociationToGroupThread;
import org.gcube.data_catalogue.grsf_publish_ws.utils.threads.WritePostCatalogueManagerThread;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.shared.ResourceBean;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.gcube.datacatalogue.common.Constants;
import org.gcube.datacatalogue.common.enums.Product_Type;
import org.gcube.datacatalogue.common.enums.Sources;
import org.gcube.datacatalogue.common.enums.Status;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanLicense;

/**
 * Services common utils.
 * @author Costantino Perciante at ISTI-CNR
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CommonServiceUtils {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CommonServiceUtils.class);
	private static final int TAG_MAX_SIZE = 100;
	private static Map<String, Boolean> extensionsCheck = new ConcurrentHashMap<>();

	/**
	 * Retrieve the list of licenses for stocks and fisheries
	 * @return
	 */
	public static Map<String, String> getLicenses(DataCatalogue catalogue){
		logger.info("Requested licenses...");
		Map<String, String> toReturn = new HashMap<String, String>();
		List<CkanLicense> licenses = catalogue.getLicenses();

		for (CkanLicense ckanLicense : licenses) {
			toReturn.put(ckanLicense.getId(), ckanLicense.getTitle());
		}
		return toReturn;
	}

	/**
	 * Validate an aggregated GRSF record. TODO use @Valid tags
	 * @throws Exception 
	 */
	public static void validateAggregatedRecord(Common record) throws Exception {

		List<RefersToBean> refersToList = record.getRefersTo();
		String shortTitle = record.getShortName();
		Boolean traceabilityFlag = record.isTraceabilityFlag();
		Status status = record.getStatus();

		if(refersToList == null || refersToList.isEmpty())
			throw new Exception("refers_to cannot be null/empty");

		if(traceabilityFlag == null)
			throw new Exception("traceability_flag cannot be null");

		if(shortTitle == null || shortTitle.isEmpty())
			throw new Exception("short_title cannot be null/empty");

		if(status == null)
			throw new Exception("status cannot be null/empty");

		// check if it is a stock and perform related checks
		if(record.getClass().equals(StockRecord.class)){

			StockRecord stock = (StockRecord) record;
			List<String> species = stock.getSpecies();
			if(species == null || species.isEmpty())
				throw new Exception("species cannot be null/empty in a GRSF record");
		}

		// check if it is a stock and perform related checks
		if(record.getClass().equals(FisheryRecord.class)){

			FisheryRecord fishery = (FisheryRecord) record;

			List<String> fishingArea = fishery.getFishingArea();
			List<String> jurisdictionArea = fishery.getJurisdictionArea();

			if((fishingArea == null || fishingArea.isEmpty()) && (jurisdictionArea == null || jurisdictionArea.isEmpty()))
				throw new Exception("fishing_area and jurisdiction_area cannot be null/empty at the same time!");
		}
	}

	/**
	 * Parse the record to look up tags, groups and resources
	 * @param tags
	 * @param skipTags
	 * @param groups
	 * @param skipGroups
	 * @param resources
	 * @param skipResources
	 * @param extras
	 * @param record
	 * @param username
	 * @param source
	 */
	public static void getTagsGroupsResourcesExtrasByRecord(
			Set<String> tags, 
			boolean skipTags,
			Set<String> groups, 
			boolean skipGroups,
			List<ResourceBean> resources,
			boolean skipResources,
			Map<String, List<String>> extras,
			Base record, 
			String username, 
			Sources source // it comes from the source type e.g., "grsf-", "ram-" ..
			){

		Class<?> current = record.getClass();
		do{
			Field[] fields = current.getDeclaredFields();
			for (Field field : fields) {

				if(!skipTags)
					getTagsByField(field, current, record, tags);

				if(!skipGroups)
					getGroupsByField(field, current, record, groups, source);

				getExtrasByField(field, current, record, extras, source);

				if(!skipResources)
					getResourcesByField(field, current, record, username, resources);

			}
		}
		while((current = current.getSuperclass())!=null); // start from the inherited class up to the Object.class

		logger.debug("Tags are " + tags);
		logger.debug("Groups are " + groups);
		logger.debug("Extras are " + extras);
		logger.debug("Resources without timeseries are " + resources);
	}

	/**
	 * Retrieve the list of tags for this object
	 */
	private static void getTagsByField(Field field, Class<?> current, Base record, Set<String> tags){
		if(field.isAnnotationPresent(Tag.class)){
			try{
				Object f = new PropertyDescriptor(field.getName(), current).getReadMethod().invoke(record);
				if(f != null){
					if(f instanceof List<?>){
						List asList = ((List) f);
						if(!asList.isEmpty()){

							logger.debug("The object annotated with @Tag is a list. Adding ... ");

							int elementsToConsider = asList.size();

							// check if it is a time series, in this take the last X elements
							if(asList.get(0).getClass().equals(TimeSeriesBean.class)){
								elementsToConsider = Math.min(elementsToConsider, Constants.TIME_SERIES_TAKE_LAST_VALUES);
								for (int i = 0; i < elementsToConsider; i++) {
									String finalTag = asList.get(i).toString().trim().replaceAll(Constants.REGEX_TAGS, "");
									if(finalTag.length() <= TAG_MAX_SIZE)
										tags.add(finalTag);	
								}
							}else{
								// else add all the available elements
								for (int i = 0; i < elementsToConsider; i++) {
									String finalTag = asList.get(i).toString().trim().replaceAll(Constants.REGEX_TAGS, "");
									if(finalTag.length() <= TAG_MAX_SIZE)
										tags.add(finalTag);	
								}
							}
						}
					}else{
						logger.debug("The object annotated with @Tag is a simple one. Adding ... ");
						String finalTag = f.toString().trim().replaceAll(Constants.REGEX_TAGS, "");
						logger.debug(finalTag);
						if(finalTag.length() <= TAG_MAX_SIZE)
							tags.add(finalTag);	
					}

				}
			}catch(Exception e){
				logger.error("Failed to read value for field " + field.getName() + " skipping", e);
			}
		}
	}

	/**
	 * Retrieve the list of groups' names for this object
	 */
	private static void getGroupsByField(Field field, Class<?> current, Base record, Set<String> groups, Sources source){
		if(field.isAnnotationPresent(Group.class)){
			String conditionToCheck = field.getAnnotation(Group.class).condition();
			String groupNameOverValue = field.getAnnotation(Group.class).groupNameOverValue();
			try{
				Object f = new PropertyDescriptor(field.getName(), current).getReadMethod().invoke(record);
				if(f != null){
					if(f instanceof List<?>){
						List asList = ((List) f);
						if(!asList.isEmpty()){

							logger.debug("The object annotated with @Group is a list. Adding ... ");

							// else add all the available elements
							for (int i = 0; i < asList.size(); i++) {
								boolean match = conditionToCheck.isEmpty() ? true : asList.get(i).toString().trim().matches(conditionToCheck);
								if(match){
									String groupName = groupNameOverValue.isEmpty() ?
											HelperMethods.getGroupNameOnCkan(source.toString().toLowerCase() + "-" + asList.get(i).toString().trim()) : 
												source.toString().toLowerCase() + "-" + groupNameOverValue;
											groups.add(groupName);	
								}
							}

						}
					}else{

						// also convert to the group name that should be on ckan
						boolean match = conditionToCheck.isEmpty() ? true : f.toString().trim().matches(conditionToCheck);
						if(match){

							String groupName = groupNameOverValue.isEmpty() ?
									HelperMethods.getGroupNameOnCkan(source.toString().toLowerCase()  + "-" + f.toString().trim()) :
										source.toString().toLowerCase() + "-" + groupNameOverValue;
									groups.add(groupName);

						}
					}
				}

			}catch(Exception e){
				logger.error("Failed to read value for field " + field.getName() + " skipping", e);
			}
		}

	}

	/**
	 * Retrieve the list of extras for this object
	 * @param source 
	 */
	private static void getExtrasByField(Field field, Class<?> current, Base record, Map<String, List<String>> extras, Sources source){
		if(field.isAnnotationPresent(CustomField.class)){
			try{
				Object f = new PropertyDescriptor(field.getName(), current).getReadMethod().invoke(record);
				String keyField = field.getAnnotation(CustomField.class).key();

				// manage no connections nor similar grsf records here for GRSF records only
				if(source.equals(Sources.GRSF) && keyField.equals(Constants.SIMILAR_GRSF_RECORDS_CUSTOM_KEY)){
					List asList = (List)f;
					if(asList == null || asList.isEmpty()){
						extras.put(keyField, Arrays.asList(Constants.NO_SIMILAR_GRSF_RECORDS));
						return;
					}

				}

				if(source.equals(Sources.GRSF) && keyField.equals(Constants.CONNECTED_CUSTOM_KEY)){
					List asList = (List)f;
					if(asList == null || asList.isEmpty()){
						extras.put(keyField, Arrays.asList(Constants.NO_CONNECTED_RECORDS));
						return;
					}
				}

				if(f != null){
					Set<String> valuesForKey = null;

					// check if the map already contains this key
					if(extras.containsKey(keyField))
						valuesForKey = new HashSet(extras.get(keyField));
					else
						valuesForKey = new HashSet<String>();

					if(f instanceof List<?>){
						logger.debug("The object " + field.getName() + " is a list and is annotated with @CustomField. Adding ...");
						List asList = (List)f;
						if(!asList.isEmpty()){

							int elementsToConsider = asList.size();

							// check if it is a time series, in this case take the last X elements 
							if(asList.get(0).getClass().equals(TimeSeriesBean.class)){
								elementsToConsider = Math.min(elementsToConsider, Constants.TIME_SERIES_TAKE_LAST_VALUES);
								for (int i = 0; i < elementsToConsider; i++) {
									// trim and remove html
									String clean = HelperMethods.removeHTML(asList.get(i).toString().trim()); 
									valuesForKey.add(clean);
								}
							}
							else
								for (int i = 0; i < elementsToConsider; i++) {
									String clean = HelperMethods.removeHTML(asList.get(i).toString().trim()); 
									valuesForKey.add(clean);
								}
						}

					}else{
						String clean = HelperMethods.removeHTML(f.toString().trim()); 
						valuesForKey.add(clean);
					}

					// add to the map
					extras.put(keyField, new ArrayList<String>(valuesForKey));
				}
			}catch(Exception e){
				logger.error("Failed to read value for field " + field.getName() + " skipping", e);
			}
		}
	}

	/**
	 * Retrieve the ResourceBean given the record (extract resources from Database Sources and Source of Information and others)
	 * @param record
	 * @param username
	 * @param tags 
	 * @param resources 
	 * @return 
	 */
	private static void getResourcesByField(Field field, Class<?> current, Base record, String username, List<ResourceBean> resources){
		if(field.isAnnotationPresent(CkanResource.class)){
			try{
				Object f = new PropertyDescriptor(field.getName(), current).getReadMethod().invoke(record);
				if(f != null){

					if(f instanceof List<?>){

						List<Resource> listOfResources = (List<Resource>)f;

						for (Resource resource : listOfResources) {
							resources.add(new ResourceBean(resource.getUrl(), resource.getName().toString(), resource.getDescription(), null, username, null, null));
						}

					}else{

						Resource res = (Resource)f;
						resources.add(new ResourceBean(res.getUrl(), res.getName().toString(), res.getDescription(), null, username, null, null));

					}
				}
			}catch(Exception e){
				logger.error("Failed to read value for field " + field.getName() + " skipping", e);
			}
		}
	}

	/**
	 * Evaluate if the user has the admin role
	 * Throws exception if he/she doesn't 
	 */
	public static void hasAdminRole(String username, DataCatalogue catalogue, String apiKey, String organization) throws Exception{

		String role = catalogue.getRoleOfUserInOrganization(username, organization, apiKey);
		logger.info("Role of the user " + username + " is " + role + " in " + organization);

		if(role == null || role.isEmpty() || !role.equalsIgnoreCase(RolesCkanGroupOrOrg.ADMIN.toString()))
			throw new Exception("You are not authorized to create a product. Please check you have the Catalogue-Administrator role!");

	}

	/**
	 * Check this record's name
	 * @param futureName
	 * @param catalogue
	 * @throws Exception on name check
	 */
	public static void checkName(String futureName, DataCatalogue catalogue) throws Exception {

		if(!HelperMethods.isNameValid(futureName)){
			throw new Exception("The 'uuid_knowledge_base' must contain only alphanumeric characters, and symbols like '.' or '_', '-'");
		}else{

			logger.debug("Checking if such name [" + futureName + "] doesn't exist ...");
			boolean alreadyExists = catalogue.existProductWithNameOrId(futureName);

			if(alreadyExists){
				logger.debug("A product with 'uuid_knowledge_base' " + futureName + " already exists");
				throw new Exception("A product with 'uuid_knowledge_base' " + futureName + " already exists");

			}
		}
	}

	/**
	 * Validate and check sources
	 * @param apiKey
	 * @param context
	 * @param contextServlet
	 * @param sourceInPath
	 * @param record
	 * @param resources 
	 * @param groups 
	 * @param customFields 
	 * @param tags 
	 * @param futureTitle 
	 * @param username 
	 * @throws Exception 
	 */
	public static void validateRecordAndMapFields(String apiKey, String context, ServletContext contextServlet,
			Sources sourceInPath, Common record, Product_Type productType, Set<String> tags, Map<String, List<String>> customFields, 
			Set<String> groups, List<ResourceBean> resources, String username, String futureTitle) throws Exception {

		// validate the record if it is a GRSF one and set the record type and in manage context
		// Status field is needed only in the Manage context for GRSF records
		if(context.equals((String)contextServlet.getInitParameter(HelperMethods.MANAGE_CONTEX_KEY))){
			if(sourceInPath.equals(Sources.GRSF)){

				List<RefersToBean> refersTo = record.getRefersTo();
				if(refersTo == null || refersTo.isEmpty())
					throw new Exception("refers_to is empty for a GRSF record");

				Set<String> sourcesList = new HashSet<String>();

				String databaseSource = "";
				// we have the id within the catalog of this record. This means that we can retrieve the record and its system:type
				for (RefersToBean refersToBean : refersTo) {
					String sourceOrganization = getRecordOrganization(refersToBean.getId(), apiKey, context);
					resources.add(new ResourceBean(refersToBean.getUrl(), sourceOrganization , "", null, username, null, null));
					sourcesList.add(sourceOrganization.toLowerCase());
					databaseSource += sourceOrganization + " ";
				}

				// create the Database Source information
				customFields.put(Constants.GRSF_DATABASE_SOURCE, Arrays.asList(databaseSource.trim()));

				// append to groups: we need to add this record to the correspondent group of the sources
				addRecordToGroupSources(groups, new ArrayList(sourcesList), productType, sourceInPath);

				// validate
				CommonServiceUtils.validateAggregatedRecord(record);
			}
		}

		// set the domain
		record.setDomain(productType.getOrigName());

		// set system type (it is equal to the GRSF Type for GRSF records, "Legacy" for source records)
		record.setSystemType(sourceInPath.equals(Sources.GRSF) ? 
				productType.equals(Product_Type.FISHERY) ? ((FisheryRecord)record).getType().getOrigName() : ((StockRecord)record).getType().getOrigName()
						: Constants.SYSTEM_TYPE_FOR_SOURCES_VALUE);

		logger.debug("Domain is " + productType.getOrigName() + " and system type " + record.getSystemType());

		// evaluate the custom fields/tags, resources and groups
		groups.add(sourceInPath.getOrigName().toLowerCase() + "-" + productType.getOrigName().toLowerCase()); //e.g. grsf-fishery
		boolean skipTags = !sourceInPath.equals(Sources.GRSF); // no tags for the Original records
		CommonServiceUtils.getTagsGroupsResourcesExtrasByRecord(tags, skipTags, groups, false, resources, false, customFields, record, username, sourceInPath);

	}

	/**
	 * Add the record to the group of sources
	 * @param groups
	 * @param sourcesList
	 * @param productType
	 * @param sourceInPath 
	 */
	private static void addRecordToGroupSources(Set<String> groups,
			List<String> sourcesList, Product_Type productType, Sources sourceInPath) {

		Collections.sort(sourcesList); // be sure the name are sorted because the groups have been generated this way
		String groupName = sourceInPath.getOrigName().toLowerCase() + "-" + productType.getOrigName().toLowerCase();
		for (String source : sourcesList) {
			groupName += "-" + source;
		}

		groups.add(groupName);
	}

	/**
	 * Fetch the system:type property from a record
	 * @param itemIdOrName
	 * @param apiKey
	 * @return null on error
	 * @throws Exception
	 */
	public static String getSystemTypeValue(String itemIdOrName, String apiKey, String context) throws Exception{

		DataCatalogue catalog = HelperMethods.getDataCatalogueRunningInstance(context);
		CkanDataset dataset = catalog.getDataset(itemIdOrName, apiKey);
		if(dataset == null)
			throw new Exception("Unable to find record with id or name " + itemIdOrName);
		String systemTypeValue = dataset.getExtrasAsHashMap().get(Constants.SYSTEM_TYPE_CUSTOM_KEY);
		if(systemTypeValue == null || systemTypeValue.isEmpty())
			throw new Exception(Constants.SYSTEM_TYPE_CUSTOM_KEY + " property not set in record " + itemIdOrName);
		else
			return systemTypeValue;

	}

	public static String getRecordOrganization(String itemIdOrName, String apiKey, String context) throws Exception{
		DataCatalogue catalog = HelperMethods.getDataCatalogueRunningInstance(context);
		CkanDataset dataset = catalog.getDataset(itemIdOrName, apiKey);
		if(dataset == null)
			throw new Exception("Unable to find record with id or name " + itemIdOrName);
		else
			return dataset.getOrganization().getTitle();
	}

	/**
	 * Actions to execute once the dataset has been updated or created.
	 * @param responseBean
	 * @param catalogue
	 * @param namespaces
	 * @param groups
	 * @param context
	 * @param token
	 * @param futureTitle
	 * @param authorFullname
	 * @param contextServlet 
	 * @param partialDescription 
	 * @throws InterruptedException 
	 */
	public static void actionsPostCreateOrUpdate(
			final String datasetId, final String futureName, final Common record, final String apiKey, final String username, final String organization, String itemUrl,
			ResponseCreationBean responseBean, final DataCatalogue catalogue, 
			Map<String, String> namespaces, final Set<String> groups, final String context,
			final String token, final String futureTitle, final String authorFullname, final ServletContext contextServlet, final boolean isUpdated, 
			String description) throws InterruptedException {

		// on create, we need to add the item url... the description can be set on create and update instead
		if(!isUpdated){
			itemUrl = catalogue.getUnencryptedUrlFromDatasetIdOrName(futureName);
			Map<String, List<String>> addField = new HashMap<String, List<String>>();
			String modifiedUUIDKey = namespaces.containsKey(Constants.ITEM_URL_FIELD) ? namespaces.get(Constants.ITEM_URL_FIELD) : Constants.ITEM_URL_FIELD;
			addField.put(modifiedUUIDKey, Arrays.asList(itemUrl));
			catalogue.patchProductCustomFields(datasetId, apiKey, addField, false);
		}

		// update description anyway
		description += "Record URL: " + itemUrl;
		JSONObject obj = new JSONObject();
		obj.put("notes", description);
		catalogue.patchProductWithJSON(datasetId, obj, apiKey);

		// set info in the response bean
		responseBean.setId(datasetId);
		responseBean.setItemUrl(itemUrl);
		responseBean.setKbUuid(record.getUuid());

		// it is needed...
		final String itemUrlForThread = itemUrl;

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// manage groups (wait thread to die: ckan doesn't support too much concurrency on same record ...)
					if(!groups.isEmpty()){
						logger.info("Launching thread for association to the list of groups " + groups);
						AssociationToGroupThread threadGroups = new AssociationToGroupThread(new ArrayList<String>(groups), datasetId, organization, username, catalogue, apiKey);
						threadGroups.start();
						threadGroups.join();
					}
					// manage time series as resources
					logger.info("Launching thread for time series handling");
					new ManageTimeSeriesThread(record, futureName, username, catalogue, context, token).start();

					// write a post if the product has been published in grsf context
					if(!isUpdated && context.equals((String)contextServlet.getInitParameter(HelperMethods.PUBLIC_CONTEX_KEY))){
						new WritePostCatalogueManagerThread(
								context, 
								token, 
								futureTitle, 
								itemUrlForThread, 
								true, 
								new ArrayList<String>(), 
								authorFullname).start();
						logger.info("Thread to write a post about the new product has been launched");
					}
				}catch (InterruptedException e) {
					logger.error("Error", e);
				}
			}
		}).start();
	}

	/**
	 * Extend roles to other organization
	 * @param username
	 * @param catalogue
	 * @param organization
	 * @param admin
	 */
	public static void extendRoleToOtherOrganizations(String username, DataCatalogue catalogue, String organization, RolesCkanGroupOrOrg admin) {

		logger.debug("Checking if role extension is needed here");
		if(extensionsCheck.containsKey(username) && extensionsCheck.get(username))
			return;
		else{
			catalogue.assignRolesOtherOrganization(username, organization, admin);
			extensionsCheck.put(username, true);
		}

	}

	/**
	 * Evaluate in which organization a record has to be published. The only exception is when grsf_admin is involved.
	 * @param organization
	 * @param sourceInPath
	 * @return
	 */
	public static String evaluateOrganization(String organization, Sources sourceInPath) {
		if(sourceInPath.equals(Sources.GRSF) && organization.equals(Constants.GRSF_ADMIN_ORGANIZATION_NAME))
			return Constants.GRSF_ADMIN_ORGANIZATION_NAME;
		else
			return sourceInPath.getOrigName().toLowerCase();
	}
}