package org.gcube.data_catalogue.grsf_publish_ws.services;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.gcube.data_catalogue.grsf_publish_ws.utils.HelperMethods;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Sources;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Status;
import org.gcube.datacatalogue.ckanutillibrary.server.models.ResourceBean;
import org.slf4j.LoggerFactory;

/**
 * Services common utils.
 * @author Costantino Perciante at ISTI-CNR
 */
public class CommonServiceUtils {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CommonServiceUtils.class);
	private static final int TIME_SERIES_TAKE_LAST_VALUES = 5;
	private static final String REGEX_TAGS = "[^\\s\\w-_.]";

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
	 * @param groups
	 * @param record
	 * @param username
	 * @param resources
	 */
	public static void getTagsGroupsResourcesExtrasByRecord(
			Set<String> tags, 
			boolean skipTags,
			Set<String> groups, 
			List<ResourceBean> resources,
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
				getGroupsByField(field, current, record, groups, source);
				getExtrasByField(field, current, record, extras);
				getResourcesByField(field, current, record, username, resources);

			}
		}
		while((current = current.getSuperclass())!=null); // start from the inherited class up to the Object.class

		logger.info("Tags are " + tags);
		logger.info("Groups are " + groups);
		logger.info("Extras are " + extras);
		logger.info("Resources without timeseries are " + resources);
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
			try{
				Object f = new PropertyDescriptor(field.getName(), current).getReadMethod().invoke(record);
				if(f != null){
					if(f instanceof List<?>){
						List asList = ((List) f);
						if(!asList.isEmpty()){

							logger.debug("The object annotated with @Group is a list. Adding ... ");

							// else add all the available elements
							for (int i = 0; i < asList.size(); i++) {
								String groupName = HelperMethods.getGroupNameOnCkan(source.toString().toLowerCase() + "-" + asList.get(i).toString().trim());
								logger.debug(groupName);
								groups.add(groupName);	
							}

						}
					}else{

						// also convert to the group name that should be on ckan
						String groupName = HelperMethods.getGroupNameOnCkan(source.toString().toLowerCase()  + "-" + f.toString().trim());
						groups.add(groupName);
					}
				}

				// check if the field is an enumerator, and the enum class is also annotated with @Group
				if(field.getType().isEnum() && field.getType().isAnnotationPresent(Group.class)){

					logger.info("Class " + field.getClass().getSimpleName() + " has annotation @Group");

					// extract the name from the enum class and add it to the groups
					// also convert to the group name that should be on ckan
					String groupName = HelperMethods.getGroupNameOnCkan(source.toString().toLowerCase() + "-" + field.getType().getSimpleName());
					groups.add(groupName);

				}

			}catch(Exception e){
				logger.error("Failed to read value for field " + field.getName() + " skipping", e);
			}
		}
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
	 * @param groups 
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

}
