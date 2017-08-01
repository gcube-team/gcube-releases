package org.gcube.data_catalogue.grsf_publish_ws.services;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CkanResource;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CustomField;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Group;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.Tag;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.Base;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.Common;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.FisheryRecord;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.RefersToBean;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.Resource;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.StockRecord;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.TimeSeriesBean;
import org.gcube.data_catalogue.grsf_publish_ws.json.output.ResponseCreationBean;
import org.gcube.data_catalogue.grsf_publish_ws.utils.HelperMethods;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Product_Type;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Sources;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Status;
import org.gcube.data_catalogue.grsf_publish_ws.utils.threads.AssociationToGroupThread;
import org.gcube.data_catalogue.grsf_publish_ws.utils.threads.ManageTimeSeriesThread;
import org.gcube.data_catalogue.grsf_publish_ws.utils.threads.WritePostCatalogueManagerThread;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.shared.ResourceBean;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.slf4j.LoggerFactory;

/**
 * Services common utils.
 * @author Costantino Perciante at ISTI-CNR
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CommonServiceUtils {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CommonServiceUtils.class);
	public static final String DEFAULT_LICENSE = "CC-BY-SA-4.0";
	public static final String GRSF_GROUP_NAME = "grsf-group";
	private static final int TIME_SERIES_TAKE_LAST_VALUES = 5;
	private static final String REGEX_TAGS = "[^\\s\\w-_.]";
	public static final String SYSTEM_TYPE = "system:type";

	// item url property
	public static final String ITEM_URL_FIELD = "UUID";

	/**
	 * Retrieve the list of licenses for stocks and fisheries
	 * @return
	 */
	public static Map<String, String> getLicenses(){
		Map<String, String> licenses = null;
		try{
			logger.info("Requested licenses...");
			licenses = HelperMethods.getLicenses(HelperMethods.getDataCatalogueRunningInstance(ScopeProvider.instance.get()));
		}catch(Exception e){
			logger.error("Failed to retrieve the list of licenses");
			return null;
		}
		return licenses;
	}

	/**
	 * Validate an aggregated GRSF record. TODO use @Valid tags
	 * @throws Exception 
	 */
	public static void validateAggregatedRecord(Common record) throws Exception {

		List<Resource<Sources>> databaseSources = record.getDatabaseSources();
		List<RefersToBean> refersToList = record.getRefersTo();
		String shortTitle = record.getShortTitle();
		Boolean traceabilityFlag = record.isTraceabilityFlag();
		Status status = record.getStatus();

		if(databaseSources == null || databaseSources.isEmpty())
			throw new Exception("database_sources cannot be null/empty");

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

				getExtrasByField(field, current, record, extras);

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
								elementsToConsider = Math.min(elementsToConsider, TIME_SERIES_TAKE_LAST_VALUES);
								for (int i = (asList.size() - elementsToConsider); i < asList.size(); i++) {
									String finalTag = asList.get(i).toString().trim().replaceAll(REGEX_TAGS, "");
									tags.add(finalTag);	
								}
							}else{
								// else add all the available elements
								for (int i = 0; i < elementsToConsider; i++) {
									String finalTag = asList.get(i).toString().trim().replaceAll(REGEX_TAGS, "");
									tags.add(finalTag);	
								}
							}
						}
					}else{
						logger.debug("The object annotated with @Tag is a simple one. Adding ... ");
						String finalTag = f.toString().trim().replaceAll(REGEX_TAGS, "");
						logger.debug(finalTag);
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

		// add the record among the source group (check for grsf-group)
		groups.add(source.equals(Sources.GRSF) ? CommonServiceUtils.GRSF_GROUP_NAME : source.getOrigName().toLowerCase());
	}

	/**
	 * Retrieve the list of extras for this object
	 */
	private static void getExtrasByField(Field field, Class<?> current, Base record, Map<String,List<String>> extras){
		if(field.isAnnotationPresent(CustomField.class)){
			try{
				Object f = new PropertyDescriptor(field.getName(), current).getReadMethod().invoke(record);
				String keyField = field.getAnnotation(CustomField.class).key();
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
								elementsToConsider = Math.min(elementsToConsider, TIME_SERIES_TAKE_LAST_VALUES);

								for (int i = (asList.size() - elementsToConsider); i < asList.size(); i++) {
									// trim and remove html
									String clean = HelperMethods.removeHTML(asList.get(i).toString().trim()); 
									valuesForKey.add(clean);
								}

							}else{	
								for (int i = 0; i < elementsToConsider; i++) {
									String clean = HelperMethods.removeHTML(asList.get(i).toString().trim()); 
									valuesForKey.add(clean);
								}
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
		//		Iterator<Entry<CkanOrganization, RolesCkanGroupOrOrg>> roles = catalogue.getUserRoleByOrganization(username, apiKey).get(organization).entrySet().iterator();
		//
		//		while (roles.hasNext()) {
		//			Map.Entry<CkanOrganization, RolesCkanGroupOrOrg> entry = (Map.Entry<CkanOrganization, RolesCkanGroupOrOrg>) roles
		//					.next();
		//			role = RolesCkanGroupOrOrg.convertToCkanCapacity(entry.getValue());
		//		}

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
	 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
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
	public static void validateRecordAndMapFields(String context, ServletContext contextServlet,
			Sources sourceInPath, Common record, Product_Type productType, Set<String> tags, Map<String, List<String>> customFields, Set<String> groups, List<ResourceBean> resources, String username, String futureTitle) throws Exception {

		// validate the record if it is a GRSF one and set the record type and in manage context
		// Status field is needed only in the Manage context for GRSF records
		if(context.equals((String)contextServlet.getInitParameter(HelperMethods.MANAGE_CONTEX_KEY))){
			if(sourceInPath.equals(Sources.GRSF)){
				//Evaluate the sources
				List<Resource<Sources>> recordSources = record.getDatabaseSources();
				String sources = "";
				for (Resource<Sources> resource : recordSources) {
					sources += resource.getName() + ", ";
				}
				sources = sources.endsWith(", ") ? sources.substring(0, sources.length() -2) : sources;
				record.setSourceType(sources);
				CommonServiceUtils.validateAggregatedRecord(record);
			}
		}


		// set the grsf type: fishery or stock
		record.setGrsfType(productType.getOrigName());

		// evaluate the custom fields/tags, resources and groups
		groups.add(sourceInPath.getOrigName().toLowerCase() + "-" + productType.getOrigName().toLowerCase()); //e.g. grsf-fishery
		boolean skipTags = !sourceInPath.equals(Sources.GRSF); // no tags for the Original records
		CommonServiceUtils.getTagsGroupsResourcesExtrasByRecord(tags, skipTags, groups, false, resources, false, customFields, record, username, sourceInPath);

		// manage the refers to
		if(sourceInPath.equals(Sources.GRSF)){
			List<RefersToBean> refersTo = record.getRefersTo();
			if(refersTo == null || refersTo.isEmpty())
				throw new Exception("refers_to is empty");
			for (RefersToBean refersToBean : refersTo) {
				resources.add(new ResourceBean(refersToBean.getUrl(), "Source of item " + futureTitle + " in the catalogue has id: "
						+ refersToBean.getId(), "Information of a source of the item " + futureTitle, null, username, null, null));
			}
		}

		// add the SYSTEM_TYPE
		customFields.put(CommonServiceUtils.SYSTEM_TYPE, Arrays.asList(sourceInPath.getOrigName()));
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
	 * @throws InterruptedException 
	 */
	public static void actionsPostCreateOrUpdate(
			String datasetId, String futureName, Common record, String apiKey, String username, String organization, String itemUrl,
			ResponseCreationBean responseBean, DataCatalogue catalogue, 
			Map<String, String> namespaces, Set<String> groups, String context,
			String token, String futureTitle, String authorFullname, ServletContext contextServlet, boolean isUpdated) throws InterruptedException {

		// on create, we need to add the item url
		if(!isUpdated){
			itemUrl = catalogue.getUnencryptedUrlFromDatasetIdOrName(futureName);
			Map<String, List<String>> addField = new HashMap<String, List<String>>();
			String modifiedUUIDKey = namespaces.containsKey(CommonServiceUtils.ITEM_URL_FIELD) ? namespaces.get(CommonServiceUtils.ITEM_URL_FIELD) : CommonServiceUtils.ITEM_URL_FIELD;
			addField.put(modifiedUUIDKey, Arrays.asList(itemUrl));
			catalogue.patchProductCustomFields(datasetId, apiKey, addField);
		}

		// set info in the response bean
		responseBean.setId(datasetId);
		responseBean.setItemUrl(itemUrl);
		responseBean.setKbUuid(record.getUuid());

		// manage groups (wait thread to die: ckan doesn't support too much concurrency on same record ...)
		if(!groups.isEmpty()){
			logger.info("Launching thread for association to the list of groups " + groups);
			AssociationToGroupThread threadGroups = new AssociationToGroupThread(new ArrayList<String>(groups), datasetId, organization, username, catalogue, apiKey);
			threadGroups.start();
			logger.debug("Waiting association thread to die..");
			threadGroups.join();
			logger.debug("Ok, it died"); 
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
					itemUrl, 
					false, 
					new ArrayList<String>(), 
					authorFullname).start();
			logger.info("Thread to write a post about the new product has been launched");
		}
	}
}