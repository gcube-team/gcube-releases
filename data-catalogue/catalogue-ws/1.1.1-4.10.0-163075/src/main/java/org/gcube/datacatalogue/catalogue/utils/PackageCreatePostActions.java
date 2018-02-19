package org.gcube.datacatalogue.catalogue.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Actions to performa after a package has been correctly created on ckan.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class PackageCreatePostActions extends Thread {

	private String packageId;
	private String context;
	private String datasetUrl;
	private String token;
	private String username;
	private boolean isApplication;
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
	public PackageCreatePostActions(
			String username, 
			boolean isApplication, 
			String datasetUrl,
			String packageId, 
			String context, 
			String token, 
			JSONArray tags, 
			String title) {
		super();
		this.packageId = packageId;
		this.datasetUrl = datasetUrl;
		this.isApplication = isApplication;
		this.context = context;
		this.token = token;
		this.username = username;
		this.tags = tags;
		this.title = title;
	}

	@Override
	public void run() {

		try{

			ScopeProvider.instance.set(context);
			SecurityTokenProvider.instance.set(token);

			DataCatalogue utils = CatalogueUtils.getCatalogue();
			String apiKey = isApplication ? CatalogueUtils.fetchSysAPI(context) : utils.getApiKeyFromUsername(username);
			utils.setSearchableField(packageId, true);

			// add also this information as custom field
			if(datasetUrl == null)
				datasetUrl =  utils.getUnencryptedUrlFromDatasetIdOrName(packageId);
			Map<String, List<String>> addField = new HashMap<String, List<String>>();
			addField.put(ITEM_URL, Arrays.asList(datasetUrl));
			utils.patchProductCustomFields(packageId, apiKey, addField, false);

			String fullNameUser = null;
			if(!isApplication){
				JSONObject profile = CatalogueUtils.getUserProfile(username);
				JSONObject profileValues = (JSONObject)profile.get(Constants.RESULT_KEY);
				fullNameUser = (String) profileValues.get(Constants.FULLNAME_IN_PROFILE_KEY);
			}else{
				fullNameUser = username;
			}

			List<String> tagsList = null;

			if(tags != null){
				tagsList = new ArrayList<String>();
				for(int i = 0; i < tags.size(); i++){
					JSONObject obj = (JSONObject)(tags.get(i));
					tagsList.add((String)(obj.get("display_name")));
				}
			}

			// write notification post
			WritePostCatalogueManagerThread threadWritePost = 
					new WritePostCatalogueManagerThread(
							context, 
							title, 
							datasetUrl, 
							utils.isNotificationToUsersEnabled(),
							tagsList, 
							fullNameUser,
							token
							);
			threadWritePost.start();

		}catch(Exception e){
			logger.error("Error while executing post creation actions", e);
		}
	}

}
