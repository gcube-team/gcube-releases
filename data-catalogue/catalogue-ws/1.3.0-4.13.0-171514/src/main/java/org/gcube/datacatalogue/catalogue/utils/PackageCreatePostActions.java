package org.gcube.datacatalogue.catalogue.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Actions to perform after a package has been correctly created on ckan.
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class PackageCreatePostActions extends Thread {
	
	private String packageId;
	private String datasetUrl;
	private JSONArray tags;
	private String title;
	public static final String ITEM_URL = "Item URL";
	private static Logger logger = LoggerFactory.getLogger(PackageCreatePostActions.class);
	
	/**
	 * @param isApplication 
	 * @param packageId
	 * @param context
	 * @param tags 
	 * @param title 
	 */
	public PackageCreatePostActions(String datasetUrl, String packageId, JSONArray tags, String title) {
		super();
		this.packageId = packageId;
		this.datasetUrl = datasetUrl;
		this.tags = tags;
		this.title = title;
	}
	
	@Override
	public void run() {
		
		try {
			DataCatalogue dataCatalogue = CatalogueUtils.getCatalogue();
			
			if(!dataCatalogue.isSocialPostEnabled()){
				logger.info("Social Post are disabled in the context {}", ContextUtils.getContext());
				return;
			}
			
			
			String apiKey = CatalogueUtils.getApiKey();
			dataCatalogue.setSearchableField(packageId, true);
			
			// add also this information as custom field
			if(datasetUrl == null)
				datasetUrl = dataCatalogue.getUnencryptedUrlFromDatasetIdOrName(packageId);
			Map<String,List<String>> addField = new HashMap<String,List<String>>();
			addField.put(ITEM_URL, Arrays.asList(datasetUrl));
			dataCatalogue.patchProductCustomFields(packageId, apiKey, addField, false);
			
			String userFullName = ContextUtils.getUsername();
			
			if(!ContextUtils.isApplication()) {
				JSONObject profile = CatalogueUtils.getUserProfile();
				JSONObject profileValues = (JSONObject) profile.get(Constants.RESULT_KEY);
				userFullName = (String) profileValues.get(Constants.FULLNAME_IN_PROFILE_KEY);
			}
			
			List<String> tagsList = null;
			
			if(tags != null) {
				tagsList = new ArrayList<String>();
				for(int i = 0; i < tags.size(); i++) {
					JSONObject obj = (JSONObject) (tags.get(i));
					tagsList.add((String) (obj.get("display_name")));
				}
			}
			
			// write notification post
			WritePostCatalogueManagerThread threadWritePost = new WritePostCatalogueManagerThread(title, datasetUrl,
					dataCatalogue.isNotificationToUsersEnabled(), tagsList, userFullName);
			threadWritePost.start();
			
		} catch(Exception e) {
			logger.error("Error while executing post creation actions", e);
		}
	}
	
}
