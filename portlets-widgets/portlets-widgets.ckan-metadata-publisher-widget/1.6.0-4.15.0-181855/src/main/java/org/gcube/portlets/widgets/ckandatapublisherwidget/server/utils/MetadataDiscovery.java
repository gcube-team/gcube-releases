package org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.CatalogueUtilMethods;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.SessionCatalogueAttributes;
import org.gcube.datacatalogue.metadatadiscovery.DataCalogueMetadataFormatReader;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataProfile;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataField;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataGrouping;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataTagging;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataValidator;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataVocabulary;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.NamespaceCategory;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.CategoryWrapper;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.DataTypeWrapper;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.FieldAsGroup;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.FieldAsTag;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.MetaDataProfileBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.MetadataFieldWrapper;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.TaggingGroupingValue;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Metadatadiscovery facility.
 * @author Costantino Perciante at ISTI-CNR  (costantino.perciante@isti.cnr.it)
 */
public class MetadataDiscovery {

	private static final Log logger = LogFactoryUtil.getLog(MetadataDiscovery.class);

	/**
	 * Returns the names of the metadata profiles in a given context
	 * @return
	 * @throws Exception 
	 */
	public static List<String> getProfilesNames(String context) throws Exception{
		String currentContext = ScopeProvider.instance.get();
		try{

			ScopeProvider.instance.set(context);
			List<String> toReturn = new ArrayList<String>();

			DataCalogueMetadataFormatReader
			reader = new DataCalogueMetadataFormatReader();

			List<MetadataProfile> listProfiles = reader.getListOfMetadataProfiles();

			if(listProfiles != null && !listProfiles.isEmpty()){
				for (MetadataProfile profile : listProfiles) {
					toReturn.add(profile.getName());
				}
			}

			return toReturn;
		}catch(Exception e){
			logger.error("Failed to fetch profiles", e);
		}finally{
			ScopeProvider.instance.set(currentContext);
		}
		return null;
	}

	/**
	 * Returns the source xml of the metadata profile (specified via name) in a given context
	 * @param profile name
	 * @return
	 * @throws Exception 
	 */
	public static String getProfileSource(String profileName, String context) throws Exception{

		String currentContext = ScopeProvider.instance.get();
		try{

			ScopeProvider.instance.set(context);
			DataCalogueMetadataFormatReader
			reader = new DataCalogueMetadataFormatReader();

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
		}catch(Exception e){
			logger.error("Failed to fetch profiles", e);
		}finally{
			ScopeProvider.instance.set(currentContext);
		}
		return null;
	}


	/**
	 * Retrieve the list of metadata beans
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<MetaDataProfileBean> getMetadataProfilesList(String scope, HttpServletRequest request) throws Exception{

		List<MetaDataProfileBean> beans = new ArrayList<MetaDataProfileBean>();
		String username = GenericUtils.getCurrentUser(request).getUsername();
		logger.debug("User in session is " + username);

		// check the scope we need to discover
		String scopeInWhichDiscover = (scope != null && !scope.isEmpty()) ? scope : GenericUtils.getCurrentContext(request, false);

		logger.debug("Discovering into scope " + scopeInWhichDiscover);

		// scope in which we need to discover
		String keyPerScope = CatalogueUtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_PROFILES_KEY, scopeInWhichDiscover);

		HttpSession httpSession = request.getSession();

		if(httpSession.getAttribute(keyPerScope) != null){
			beans = (List<MetaDataProfileBean>)httpSession.getAttribute(keyPerScope);
			logger.debug("List of profiles was into session");
		}
		else{

			String oldScope = ScopeProvider.instance.get();
			try {

				ScopeProvider.instance.set(scopeInWhichDiscover);

				// TODO two reset methods could be added to force the reader to read again these information (after a while)
				DataCalogueMetadataFormatReader reader = new DataCalogueMetadataFormatReader();

				List<MetadataProfile> profiles = reader.getListOfMetadataProfiles();
				logger.debug("Profiles are " + profiles);

				List<NamespaceCategory> categories = reader.getListOfNamespaceCategories();
				if(categories == null)
					categories = new ArrayList<NamespaceCategory>();

				logger.debug("All Categories are " + categories);

				for (MetadataProfile profile : profiles) {

					logger.debug("Wrapping profile with name " + profile.getName() + " and type " + profile.getMetadataType());

					MetadataFormat metadata = reader.getMetadataFormatForMetadataProfile(profile);
					String type = metadata.getType();
					String title = 	profile.getName();
					List<MetadataField> fields = metadata.getMetadataFields();

					// we need to wrap the list of metadata and categories
					List<MetadataFieldWrapper> fieldsWrapper = new ArrayList<MetadataFieldWrapper>(fields != null ? fields.size() : 0);
					List<CategoryWrapper> categoriesWrapper = new ArrayList<CategoryWrapper>(categories.size());
					Map<String, CategoryWrapper> idToCategory = new HashMap<String, CategoryWrapper>(categories.size());

					// manage the categories
					for (NamespaceCategory category : categories) {
						CategoryWrapper categoryWrapped = new CategoryWrapper(category.getId(), category.getTitle(), category.getDescription());
						categoriesWrapper.add(categoryWrapped);
						idToCategory.put(category.getId(), categoryWrapped);
					}

					// also evaluate the fields for each category
					Map<String, List<MetadataFieldWrapper>> fieldsPerCategory = new HashMap<String, List<MetadataFieldWrapper>>(categoriesWrapper.size());

					// manage the fields
					if(fields != null)
						for(MetadataField metadataField: fields){

							MetadataFieldWrapper wrapperObj = new MetadataFieldWrapper();
							wrapperObj.setFieldNameFromCategory(metadataField.getCategoryFieldQName());
							wrapperObj.setType(DataTypeWrapper.valueOf(metadataField.getDataType().toString()));
							wrapperObj.setDefaultValue(metadataField.getDefaultValue());
							wrapperObj.setFieldName(metadataField.getFieldName());
							wrapperObj.setMandatory(metadataField.getMandatory());
							wrapperObj.setNote(metadataField.getNote());
							MetadataValidator validator = metadataField.getValidator();
							if(validator != null)
								wrapperObj.setValidator(validator.getRegularExpression());

							MetadataVocabulary vocabulary = metadataField.getVocabulary();

							if(vocabulary != null){
								wrapperObj.setVocabulary(vocabulary.getVocabularyFields());	
								wrapperObj.setMultiSelection(vocabulary.isMultiSelection());
							}

							MetadataTagging tagging = metadataField.getTagging();
							if(tagging != null){

								FieldAsTag tag = new FieldAsTag();
								tag.setCreate(tagging.getCreate());
								tag.setSeparator(tagging.getSeparator());
								tag.setTaggingValue(TaggingGroupingValue.valueOf(tagging.getTaggingValue().toString()));
								wrapperObj.setAsTag(tag);

							}

							MetadataGrouping grouping = metadataField.getGrouping();
							if(grouping != null){

								FieldAsGroup group = new FieldAsGroup();
								group.setCreate(grouping.getCreate());
								group.setPropagateUp(grouping.getPropagateUp());
								group.setGroupingValue(TaggingGroupingValue.valueOf(grouping.getGroupingValue().toString()));
								wrapperObj.setAsGroup(group);

							}

							// set to which category this field belongs to and vice-versa
							if(metadataField.getCategoryRef() != null){
								CategoryWrapper ownerCategory = idToCategory.get(metadataField.getCategoryRef());

								if(ownerCategory == null){
									logger.warn("A field with categoryref "  + metadataField.getCategoryRef() + " has been found, but"
											+ " such category is not defined within the namespaces");
								}else{

									wrapperObj.setOwnerCategory(ownerCategory);

									List<MetadataFieldWrapper> fieldsPerCategoryN = fieldsPerCategory.get(metadataField.getCategoryRef()); 
									if(fieldsPerCategoryN == null)
										fieldsPerCategoryN = new ArrayList<MetadataFieldWrapper>();

									fieldsPerCategoryN.add(wrapperObj);
									fieldsPerCategory.put(metadataField.getCategoryRef(), fieldsPerCategoryN);

									// instead of re-looping on the fieldsPerCategory map later, just set this potentially partial list
									ownerCategory.setFieldsForThisCategory(fieldsPerCategoryN);
								}
							}
							
							//Added by Francesco
							int maxOccurs = 1; //Default value is 1. A field should occur once.
							if(metadataField.getMaxOccurs()!=null) {
								try {
									//the field can appear an unlimited number of times.
									if(metadataField.getMaxOccurs().equals("*")) {
										maxOccurs = Integer.MAX_VALUE;
									}else {
										//the field must appear N times;
										maxOccurs = Integer.parseInt(metadataField.getMaxOccurs());
									}
								}catch (Exception e) {
									//silent
								}
								
								wrapperObj.setMaxOccurs(maxOccurs);
							}

							fieldsWrapper.add(wrapperObj);
						}

					// filter the categories without children here
					Iterator<CategoryWrapper> categoryToRemoveIT = categoriesWrapper.iterator();
					while (categoryToRemoveIT.hasNext()) {
						CategoryWrapper categoryWrapper = (CategoryWrapper) categoryToRemoveIT
								.next();
						if(categoryWrapper.getFieldsForThisCategory() == null)
							categoryToRemoveIT.remove();
					}

					MetaDataProfileBean bean = new MetaDataProfileBean(type, title, fieldsWrapper, categoriesWrapper);
					beans.add(bean);
				}

				logger.debug("List of beans is " + beans);
				httpSession.setAttribute(keyPerScope, beans);
				logger.info("List of profiles has been saved into session");

			} catch (Exception e) {
				logger.error("Error while retrieving metadata beans ", e);
				throw new Exception("Failed to parse Types: " + e.getMessage());
			}finally{
				ScopeProvider.instance.set(oldScope);
			}
		}

		return beans;
	}

}
