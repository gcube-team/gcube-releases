package org.gcube.gcat.oldutils;

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

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang.math.NumberUtils;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.CatalogueUtilMethods;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.DataType;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataField;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataGrouping;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataTagging;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.NamespaceCategory;
import org.gcube.gcat.persistence.ckan.CKAN;
import org.gcube.gcat.persistence.ckan.CKANPackage;
import org.gcube.gcat.persistence.ckan.CKANUtility;
import org.gcube.gcat.profile.MetadataUtility;
import org.gcube.gcat.social.SocialService;
import org.geojson.GeoJsonObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.trentorise.opendata.jackan.model.CkanGroup;

/**
 * Validate creation item requests utilities.
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Validator {
	
	private static final Logger logger = LoggerFactory.getLogger(Validator.class);
	
	private static final SimpleDateFormat DATE_SIMPLE = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DATE_HOUR_MINUTES = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private static JSONObject getJSONObject(String json) {
		JSONParser parser = new JSONParser();
		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) parser.parse(json);
		} catch(ParseException e) {
			throw new BadRequestException(e.getMessage());
		}
		return jsonObject;
	}
	
	public static void validateAgainstProfile(String json) throws Exception {
		JSONObject jsonObject = getJSONObject(json);
		validateAgainstProfile(jsonObject);
	}
	
	/**
	 * This method validate the incoming json dataset wrt a metadata profile
	 * @param json
	 * @param caller
	 * @param profiles 
	 * @return
	 * @throws Exception 
	 */
	public static void validateAgainstProfile(JSONObject obj) throws Exception {
		
		JSONArray extrasArrayOriginal = (JSONArray) obj.get(CKANPackage.EXTRAS_KEY);
		JSONArray groupsArrayOriginal = (JSONArray) obj.get(CKANPackage.GROUPS_KEY);
		JSONArray tagsArrayOriginal = (JSONArray) obj.get(CKANPackage.TAGS_KEY);
		
		if(extrasArrayOriginal == null || extrasArrayOriginal.isEmpty()) {
			throw new BadRequestException("'extras' field is missing in context where metadata profile(s) are defined!");
		}
		
		if(groupsArrayOriginal == null) {
			groupsArrayOriginal = new JSONArray();
		}
		
		if(tagsArrayOriginal == null) {
			tagsArrayOriginal = new JSONArray();
		}
		
		// get the metadata profile specifying the type
		CustomField metadataTypeCF = null;
		List<CustomField> customFields = new ArrayList<CustomField>(extrasArrayOriginal.size());
		Iterator iterator = extrasArrayOriginal.iterator();
		while(iterator.hasNext()) {
			JSONObject object = (JSONObject) iterator.next();
			CustomField cf = new CustomField(object);
			if(cf.getKey().equals(CKANPackage.EXTRAS_KEY_VALUE_SYSTEM_TYPE)) {
				metadataTypeCF = cf;
			} else if(cf.getKey().equals(SocialService.ITEM_URL)) {
				continue;
			} else {
				customFields.add(cf);
			}
		}
		
		if(metadataTypeCF == null) {
			throw new BadRequestException("'" + CKANPackage.EXTRAS_KEY_VALUE_SYSTEM_TYPE
					+ "' extra field is missing in context where metadata profile(s) are defined!");
		}
		
		String profileName = metadataTypeCF.getValue();
		// fetch the profile by metadata type specified above
		MetadataUtility metadataUtility = MetadataUtility.getInstance();
		MetadataFormat profile = metadataUtility.getMetadataFormat(profileName);
		if(profile == null) {
			throw new BadRequestException("'" + CKANPackage.EXTRAS_KEY_VALUE_SYSTEM_TYPE + "' extra field's value ('"
					+ profileName
					+ "') specified as custom field doesn't match any of the profiles defined in this context!");
		} else {
			JSONArray extrasArrayUpdated = null;
			List<MetadataField> metadataFields = profile.getMetadataFields();
			
			if(metadataFields == null || metadataFields.isEmpty()) {
				extrasArrayUpdated = extrasArrayOriginal;
			} else {
				extrasArrayUpdated = new JSONArray();
				
				List<NamespaceCategory> categories = metadataUtility.getNamespaceCategories();
				logger.debug("Retrieved namespaces are {}", categories);
				List<String> categoriesIds = new ArrayList<String>(categories == null ? 0 : categories.size());
				if(categories == null || categories.isEmpty()) {
					logger.warn("No category defined in context {}", ScopeProvider.instance.get());
				} else {
					for(NamespaceCategory metadataCategory : categories) {
						categoriesIds.add(metadataCategory.getId()); // save them later for matching with custom fields
					}
				}
				
				// the list of already validated customFields
				List<CustomField> validatedCustomFields = new ArrayList<CustomField>(customFields.size());
				
				// keep track of mandatory fields and their cardinality
				Map<String,Integer> fieldsMandatoryLowerBoundMap = new HashMap<String,Integer>(metadataFields.size());
				Map<String,Integer> fieldsMandatoryUpperBoundMap = new HashMap<String,Integer>(metadataFields.size());
				Map<String,Integer> numberFieldsMandatorySameKeyMap = new HashMap<String,Integer>(
						metadataFields.size());
				
				// keep track of the groups that must be created AFTER validation but BEFORE item creation
				List<String> groupsToCreateAfterValidation = new ArrayList<String>();
				
				// now validate fields
				int metadataIndex = 0;
				for(MetadataField metadataField : metadataFields) {
					
					int categoryIdIndex = categoriesIds.indexOf(metadataField.getCategoryRef());
					logger.debug("Found index for category " + metadataField.getCategoryRef() + " " + categoryIdIndex);
					List<CustomField> validCFs = validateAgainstMetadataField(metadataIndex, categoryIdIndex,
							customFields, tagsArrayOriginal, groupsArrayOriginal, metadataField, categories,
							fieldsMandatoryLowerBoundMap, fieldsMandatoryUpperBoundMap, numberFieldsMandatorySameKeyMap,
							groupsToCreateAfterValidation);
					validatedCustomFields.addAll(validCFs);
					metadataIndex++;
					
				}
				
				// check mandatory fields
				Iterator<Entry<String,Integer>> iteratorLowerBounds = fieldsMandatoryLowerBoundMap.entrySet()
						.iterator();
				while(iteratorLowerBounds.hasNext()) {
					Map.Entry<java.lang.String,java.lang.Integer> entry = (Map.Entry<java.lang.String,java.lang.Integer>) iteratorLowerBounds
							.next();
					int lowerBound = entry.getValue();
					int upperBound = fieldsMandatoryUpperBoundMap.get(entry.getKey());
					int inserted = numberFieldsMandatorySameKeyMap.get(entry.getKey());
					
					logger.info("Field with key '" + entry.getKey() + "' has been found " + inserted
							+ " times and its lower bound is " + lowerBound + " and upper bound " + upperBound);
					
					if(inserted < lowerBound || inserted > upperBound) {
						throw new BadRequestException("Field with key '" + entry.getKey()
								+ "' is mandatory, but it's not present among the provided fields or its cardinality is not respected ([min = "
								+ lowerBound + ", max=" + upperBound + "]).");
					}
				}
				
				// if there are no tags, throw an exception
				if(tagsArrayOriginal.isEmpty()) {
					throw new BadRequestException("Please define at least one tag for this item!");
				}
				
				// sort validated custom fields and add to the extrasArrayUpdated json array 
				Collections.sort(validatedCustomFields);
				
				logger.debug("Sorted list of custom fields is " + validatedCustomFields);
				
				// add missing fields with no match (append them at the end, since no metadataIndex or categoryIndex was defined for them)
				for(CustomField cf : customFields)
					validatedCustomFields.add(cf);
				
				// convert back to json
				for(CustomField customField : validatedCustomFields) {
					JSONObject jsonObj = new JSONObject();
					jsonObj.put(CKANPackage.EXTRAS_KEY_KEY, customField.getQualifiedKey());
					jsonObj.put(CKANPackage.EXTRAS_VALUE_KEY, customField.getValue());
					extrasArrayUpdated.add(jsonObj);
				}
				
				// add metadata type field as last element
				JSONObject metadataTypeJSON = new JSONObject();
				metadataTypeJSON.put(CKANPackage.EXTRAS_KEY_KEY, metadataTypeCF.getKey());
				metadataTypeJSON.put(CKANPackage.EXTRAS_VALUE_KEY, metadataTypeCF.getValue());
				extrasArrayUpdated.add(metadataTypeJSON);
				
				// create groups
				for(String title : groupsToCreateAfterValidation) {
					try {
						createGroupAsSysAdmin(title, title, "");
					} catch(Exception e) {
						logger.error("Failed to create group with title " + title, e);
					}
				}
			}
			
			obj.put(CKANPackage.TAGS_KEY, tagsArrayOriginal);
			obj.put(CKANPackage.GROUPS_KEY, groupsArrayOriginal);
			obj.put(CKANPackage.EXTRAS_KEY, extrasArrayUpdated);
			
		}
		
	}
	
	/**
	 * Retrieve an instance of the library for the scope
	 * @param scope if it is null it is evaluated from the session
	 * @return
	 * @throws Exception 
	 */
	public static CkanGroup createGroupAsSysAdmin(String title, String groupName, String description) throws Exception {
		return CKAN.getCatalogue().createGroup(groupName, title, description);
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
	 * @param isApplication 
	 * @return
	 * @throws Exception
	 */
	private static List<CustomField> validateAgainstMetadataField(int metadataIndex, int categoryIndex,
			List<CustomField> customFields, JSONArray tagsArrayOriginal, JSONArray groupsArrayOriginal,
			MetadataField metadataField, List<NamespaceCategory> categories,
			Map<String,Integer> fieldsMandatoryLowerBoundMap, Map<String,Integer> fieldsMandatoryUpperBoundMap,
			Map<String,Integer> numberFieldsMandatorySameKeyMap, List<String> groupToCreate) throws Exception {
		
		List<CustomField> toReturn = new ArrayList<CustomField>();
		String metadataFieldName = metadataField.getCategoryFieldQName(); // get the qualified one, if any
		int fieldsFoundWithThisKey = 0;
		
		Iterator<CustomField> iterator = customFields.iterator();
		while(iterator.hasNext()) {
			CustomField cf = (CustomField) iterator.next();
			if(cf.getKey().equals(metadataFieldName)) {
				validate(cf, metadataField);
				fieldsFoundWithThisKey++;
				cf.setIndexCategory(categoryIndex);
				cf.setIndexMetadataField(metadataIndex);
				checkAsGroup(cf, metadataField, groupsArrayOriginal, groupToCreate);
				checkAsTag(cf, metadataField, tagsArrayOriginal);
				toReturn.add(cf);
				iterator.remove();
			}
		}
		
		// in case of mandatory fields, keep track of the number of times they appear
		if(metadataField.getMandatory()) {
			// lower bound
			int lowerBound = 1;
			if(fieldsMandatoryLowerBoundMap.containsKey(metadataFieldName))
				lowerBound = fieldsMandatoryLowerBoundMap.get(metadataFieldName) + 1;
			fieldsMandatoryLowerBoundMap.put(metadataFieldName, lowerBound);
			
			// upper bound
			boolean hasVocabulary = metadataField.getVocabulary() != null;
			int upperBound = hasVocabulary ? (metadataField.getVocabulary().isMultiSelection()
					? metadataField.getVocabulary().getVocabularyFields().size()
					: 1) : 1;
			
			if(fieldsMandatoryUpperBoundMap.containsKey(metadataFieldName))
				upperBound += fieldsMandatoryUpperBoundMap.get(metadataFieldName);
			
			fieldsMandatoryUpperBoundMap.put(metadataFieldName, upperBound);
			
			// fields with this same key
			int countPerFields = fieldsFoundWithThisKey;
			if(numberFieldsMandatorySameKeyMap.containsKey(metadataFieldName))
				countPerFields += numberFieldsMandatorySameKeyMap.get(metadataFieldName);
			numberFieldsMandatorySameKeyMap.put(metadataFieldName, countPerFields);
		}
		
		// if there was no field with this key and it was not mandatory, just add an entry of the kind {"key": "key-value", "value" : ""}. 
		// Sometimes it is important to view the field as empty.
		if(fieldsFoundWithThisKey == 0 && !metadataField.getMandatory()) {
			toReturn.add(new CustomField(metadataFieldName, "", -1, -1));
		}
		
		return toReturn;
		
	}
	
	public static final int MAX_TAG_CHARS = 100;
	
	/**
	 * Check if a tag must be generated
	 * @param fieldToValidate
	 * @param metadataField
	 * @param tagsArrayOriginal
	 */
	private static void checkAsTag(CustomField fieldToValidate, MetadataField metadataField,
			JSONArray tagsArrayOriginal) {
		MetadataTagging tagging = metadataField.getTagging();
		if(tagging != null) {
			
			String tag = "";
			
			switch(tagging.getTaggingValue()) {
				case onFieldName:
					tag = metadataField.getFieldName();
					break;
				case onValue:
					tag = fieldToValidate.getValue();
					break;
				case onFieldName_onValue:
					tag = metadataField.getFieldName() + tagging.getSeparator() + fieldToValidate.getValue();
					break;
				case onValue_onFieldName:
					tag = fieldToValidate.getValue() + tagging.getSeparator() + metadataField.getFieldName();
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
	 * @param isApplication 
	 * @throws Exception 
	 */
	private static void checkAsGroup(CustomField fieldToValidate, MetadataField metadataField,
			JSONArray groupsArrayOriginal, List<String> groupToCreate) throws Exception {
		
		logger.debug("Custom field is " + fieldToValidate);
		logger.debug("MetadataField field is " + metadataField);
		logger.debug("JSONArray field is " + groupsArrayOriginal);
		
		MetadataGrouping grouping = metadataField.getGrouping();
		if(grouping != null) {
			
			boolean propagateUp = grouping.getPropagateUp();
			final Set<String> groupNames = new HashSet<String>();
			
			switch(grouping.getGroupingValue()) {
				case onFieldName:
					groupNames.add(metadataField.getFieldName());
					break;
				case onValue:
					if(fieldToValidate.getValue() != null && !fieldToValidate.getValue().isEmpty())
						groupNames.add(fieldToValidate.getValue());
					break;
				case onFieldName_onValue:
				case onValue_onFieldName:
					groupNames.add(metadataField.getFieldName());
					if(fieldToValidate.getValue() != null && !fieldToValidate.getValue().isEmpty())
						groupNames.add(fieldToValidate.getValue());
					break;
				default:
					return;
			}
			
			for(String title : groupNames) {
				logger.debug("Adding group to which add this item " + CatalogueUtilMethods.fromGroupTitleToName(title));
				JSONObject group = new JSONObject();
				group.put("name", CatalogueUtilMethods.fromGroupTitleToName(title));
				if(propagateUp) {
					List<String> parents = Validator
							.getGroupHierarchyNames(CatalogueUtilMethods.fromGroupTitleToName(title));
					for(String parent : parents) {
						JSONObject groupP = new JSONObject();
						groupP.put("name", parent);
						groupsArrayOriginal.add(groupP);
					}
				}
				groupsArrayOriginal.add(group);
			}
			
			// force group creation if needed
			if(grouping.getCreate()) {
				for(String title : groupNames)
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
	private static void validate(CustomField fieldToValidate, MetadataField metadataField) throws Exception {
		
		DataType dataType = metadataField.getDataType();
		String regex = metadataField.getValidator() != null ? metadataField.getValidator().getRegularExpression()
				: null;
		boolean hasControlledVocabulary = metadataField.getVocabulary() != null;
		String value = fieldToValidate.getValue();
		String key = fieldToValidate.getKey();
		String defaultValue = metadataField.getDefaultValue();
		
		// replace key by prepending the qualified name of the category, if needed
		fieldToValidate.setQualifiedKey(metadataField.getCategoryFieldQName());
		
		if((value == null || value.isEmpty()))
			if(metadataField.getMandatory() || hasControlledVocabulary)
				throw new BadRequestException("Mandatory field with name '" + key
						+ "' doesn't have a value but it is mandatory or has a controlled vocabulary!");
			else {
				if(defaultValue != null && !defaultValue.isEmpty()) {
					value = defaultValue;
					fieldToValidate.setValue(defaultValue);
				}
				return; // there is no need to check other stuff
			}
		
		switch(dataType) {
			
			case String:
			case Text:
				
				if(regex != null && !value.matches(regex))
					throw new BadRequestException("Field with key '" + key + "' doesn't match the provided regular expression ("
							+ regex + ")!");
				
				if(hasControlledVocabulary) {
					
					List<String> valuesVocabulary = metadataField.getVocabulary().getVocabularyFields();
					
					if(valuesVocabulary == null || valuesVocabulary.isEmpty())
						return;
					
					boolean match = false;
					for(String valueVocabulary : valuesVocabulary) {
						match = value.equals(valueVocabulary);
						if(match)
							break;
					}
					
					if(!match)
						throw new BadRequestException("Field with key '" + key + "' has a value '" + value
								+ "' but it doesn't match any of the vocabulary's values (" + valuesVocabulary + ")!");
					
				}
				
				break;
			case Time:
				
				if(!isValidDate(value))
					throw new BadRequestException("Field with key '" + key + "' doesn't seem a valid time!");
				
				break;
			case Time_Interval:
				
				String[] timeValues = value.split("/");
				for(int i = 0; i < timeValues.length; i++) {
					String time = timeValues[i];
					if(!isValidDate(time))
						throw new BadRequestException("Field with key '" + key + "' doesn't seem a valid time interval!");
				}
				
				break;
			case Times_ListOf:
				
				String[] timeIntervals = value.split(",");
				for(int i = 0; i < timeIntervals.length; i++) {
					String[] timeIntervalValues = timeIntervals[i].split("/");
					if(timeIntervalValues.length > 2)
						throw new BadRequestException("Field with key '" + key + "' doesn't seem a valid list of times!");
					for(i = 0; i < timeIntervalValues.length; i++) {
						String time = timeIntervalValues[i];
						if(!isValidDate(time))
							throw new BadRequestException("Field with key '" + key + "' doesn't seem a valid list of times!");
					}
				}
				
				break;
			case Boolean:
				
				if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
					
				} else
					throw new BadRequestException("Field with key '" + key + "' doesn't seem a valid boolean value!");
				
				break;
			case Number:
				
				if(!NumberUtils.isNumber(value))
					throw new BadRequestException("Field's value with key '" + key + "' is not a valid number!");
				
				break;
			case GeoJSON:
				
				try {
					new ObjectMapper().readValue(fieldToValidate.getValue(), GeoJsonObject.class);
				} catch(Exception e) {
					throw new BadRequestException("GeoJSON field with key '" + key + "' seems not valid!");
				}
				
				break;
			default:
				break;
		}
		
	}
	
	/**
	 * Validate a time date against a formatter
	 * @param value
	 * @param formatter
	 * @return
	 */
	private static boolean isValidDate(String value) {
		
		try {
			DATE_HOUR_MINUTES.parse(value);
			return true;
		} catch(Exception e) {
			logger.debug("failed to parse date with hours and minutes, trying the other one");
			try {
				DATE_SIMPLE.parse(value);
				return true;
			} catch(Exception e2) {
				logger.warn("failed to parse date with simple format, returning false");
				return false;
			}
		}
		
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
		String apiKey = CKANUtility.getApiKey();
		List<CkanGroup> ckanGroups = CKAN.getCatalogue().getParentGroups(groupName, apiKey);
		if(ckanGroups != null && !ckanGroups.isEmpty()) {
			for(CkanGroup ckanGroup : ckanGroups) {
				toReturn.add(ckanGroup.getName());
			}
		}
		return toReturn;
	}
	
}
