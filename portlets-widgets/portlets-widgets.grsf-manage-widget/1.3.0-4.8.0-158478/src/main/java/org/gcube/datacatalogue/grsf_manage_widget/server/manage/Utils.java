package org.gcube.datacatalogue.grsf_manage_widget.server.manage;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.ApplicationProfileScopePerUrlReader;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.shared.ex.ApplicationProfileNotFoundException;
import org.gcube.datacatalogue.common.Constants;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ManageProductBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.SimilarGRSFRecord;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.liferay.portal.service.UserLocalServiceUtil;

import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpResponse;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpPost;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.StringEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.CloseableHttpClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.HttpClientBuilder;
import eu.trentorise.opendata.jackan.internal.org.apache.http.util.EntityUtils;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanPair;
import eu.trentorise.opendata.jackan.model.CkanTag;

/**
 * Utility methods for GRSF Management panel widget.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Utils {

	private static final String GENERIC_RESOURCE_NAME = "GRSFManageEntries";
	private static final String GENERIC_RESOURCE_SECONDARY_TYPE = "ApplicationProfile";
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String REGEX_UUID = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
	private static final int MAX_TRIAL = 5;

	/**
	 * Look up from the IS other information that can be potentially displayed in read only mode in the management panel.
	 * @return a list of extra keys to show.
	 */
	public static Set<String> getLookedUpExtrasKeys() {
		Set<String> lookedUpExtrasKeys = new HashSet<String>();
		String scope = ScopeProvider.instance.get();
		logger.debug("Trying to fetch applicationProfile profile from the infrastructure for " + GENERIC_RESOURCE_NAME + " scope: " +  scope);

		try {
			Query q = new QueryBox("for $profile in collection('/db/Profiles/GenericResource')//Resource " +
					"where $profile/Profile/SecondaryType/string() eq '"+ GENERIC_RESOURCE_SECONDARY_TYPE + "' and  $profile/Profile/Name/string() " +
					" eq '" + GENERIC_RESOURCE_NAME + "'" +
					"return $profile");

			DiscoveryClient<String> client = client();
			List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0) 
				throw new ApplicationProfileNotFoundException("Your applicationProfile is not registered in the infrastructure");
			else {

				String elem = appProfile.get(0);
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);

				List<String> currValue = null;
				currValue = helper.evaluate("/Resource/Profile/Body/text()");

				if (currValue != null && currValue.size() > 0) {
					String body = currValue.get(0);
					String[] splittedSet = body.split(",");
					if(splittedSet != null && splittedSet.length > 0)
						for (String entry : splittedSet) {
							String trimmed = entry.trim();
							if(trimmed.isEmpty())
								continue;
							lookedUpExtrasKeys.add(trimmed);
						}
				} 
			}

			logger.info("Extras entries are " + lookedUpExtrasKeys);
			return lookedUpExtrasKeys;
		} catch (Exception e) {
			logger.error("Error while trying to fetch applicationProfile profile from the infrastructure", e);
			return null;
		} 
	}

	/**
	 * Return a map for converting a key to a namespace:key format by reading a generic resource.
	 * @param httpSession 
	 * @return a map
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getFieldToFieldNameSpaceMapping(HttpSession httpSession, String resourceName){

		// check if this information is available in session
		String sessionKey = ScopeProvider.instance.get() + resourceName;
		if(httpSession.getAttribute(sessionKey) != null)
			return (Map<String, String>) httpSession.getAttribute(sessionKey);

		Map<String, String> namespacesMap = new HashMap<String, String>(); // e.g. fishery_identity:Short Title -> Short Title
		try {
			Query q = new QueryBox("for $profile in collection('/db/Profiles/GenericResource')//Resource " +
					"where $profile/Profile/SecondaryType/string() eq '"+ "ApplicationProfile" + "' and  $profile/Profile/Name/string() " +
					" eq '" + resourceName + "'" +
					"return $profile");

			DiscoveryClient<String> client = client();
			List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0) 
				throw new Exception("Your applicationProfile is not registered in the infrastructure");
			else {

				String elem = appProfile.get(0);
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);

				NodeList nodeListKeys = helper.evaluateForNodes("//originalKey");
				NodeList nodeListModifiedKeys = helper.evaluateForNodes("//modifiedKey");
				int sizeKeys = nodeListKeys != null ? nodeListKeys.getLength() : 0;
				int sizeKeysModifed = nodeListModifiedKeys != null ? nodeListModifiedKeys.getLength() : 0;
				if(sizeKeys != sizeKeysModifed)
					throw new Exception("Malformed XML");
				logger.debug("Size is " + sizeKeys);
				for (int i = 0; i < sizeKeys; i++) {
					namespacesMap.put(nodeListModifiedKeys.item(i).getTextContent(), nodeListKeys.item(i).getTextContent());
				}
			}
			logger.debug("Map is " + namespacesMap);
			httpSession.setAttribute(sessionKey, namespacesMap);
			return namespacesMap;
		} catch (Exception e) {
			logger.error("Error while trying to fetch applicationProfile profile from the infrastructure", e);
			return null;
		}
	}

	/**
	 * Replace the extras' keys if needed, e.g. fishery_identity:Short Title -> Short Title
	 * @param extrasAsPairs
	 * @param namespaces
	 * @return a map with replaced key value pairs
	 */
	public static Map<String, List<String>> replaceFieldsKey(List<CkanPair> extrasAsPairs,
			Map<String, String> namespaces) {

		Map<String, List<String>> toReturn = new HashMap<String, List<String>>();

		for (CkanPair ckanPair : extrasAsPairs) {
			String pairKey = ckanPair.getKey();
			String pairValue = ckanPair.getValue();
			String replacedKey = namespaces.containsKey(pairKey) ? namespaces.get(pairKey) : pairKey;

			List<String> values = null;
			if(toReturn.containsKey(replacedKey))
				values = toReturn.get(replacedKey);
			else
				values = new ArrayList<String>(1);

			values.add(pairValue);

			toReturn.put(replacedKey, values);
		}


		return toReturn;
	}

	/**
	 * Discover the service endpoint and return its url
	 * @param context
	 * @return the url of the service on success, null otherwise
	 */
	public static String discoverEndPoint(String context){

		String oldContext = ScopeProvider.instance.get();
		ScopeProvider.instance.set(context);
		String toReturn = null;
		try{
			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/text() eq '"+ Constants.SERVICE_NAME +"'");
			query.addCondition("$resource/Profile/Category/text() eq '"+ Constants.SERVICE_CATEGORY +"'");
			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
			List<ServiceEndpoint> resources = client.submit(query);

			if (resources.size() == 0){
				logger.error("There is no Runtime Resource having name " + Constants.SERVICE_NAME +" and Category " + Constants.SERVICE_CATEGORY + " in this scope.");
				throw new Exception("There is no Runtime Resource having name " + Constants.SERVICE_NAME +" and Category " + Constants.SERVICE_CATEGORY + " in this scope.");
			}
			else {

				for (ServiceEndpoint res : resources) {

					Iterator<AccessPoint> accessPointIterator = res.profile().accessPoints().iterator();

					while (accessPointIterator.hasNext()) {
						ServiceEndpoint.AccessPoint accessPoint = (ServiceEndpoint.AccessPoint) accessPointIterator
								.next();

						// return the path
						toReturn = accessPoint.address();
					}
				}
			}
		}catch(Exception e){
			logger.error("Unable to retrieve such service endpoint information!", e);
		}finally{
			if(oldContext != null && !oldContext.equals(context))
				ScopeProvider.instance.set(oldContext);
		}

		return toReturn;
	}

	/**
	 * Send an update for this bean
	 * @param baseUrl
	 * @param bean
	 * @param username 
	 * @param catalogue 
	 * @return true on success, false otherwise
	 */
	@SuppressWarnings("unchecked")
	public static String updateCatalogueRecord(String serviceUrl, ManageProductBean bean, DataCatalogue catalogue, String username){

		if(serviceUrl == null)
			throw new IllegalArgumentException("GRSF Updater service url cannot be null");

		if(bean == null)
			throw new IllegalArgumentException("Item bean to manage cannot be null");

		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();){

			JSONObject obj = new JSONObject();
			obj.put(Constants.CATALOGUE_ID, bean.getCatalogueIdentifier());
			obj.put(Constants.KB_ID, bean.getKnowledgeBaseIdentifier());
			obj.put(Constants.PRODUCT_TYPE, bean.getGrsfDomain().toLowerCase());
			obj.put(Constants.STATUS, bean.getNewStatus().toString().toLowerCase());

			String annotation = bean.getAnnotation();
			if(annotation != null)
				obj.put(Constants.ANNOTATION, annotation.replaceAll("\"", ""));

			logger.debug("Update request looks like " + obj.toJSONString());

			HttpPost request = new HttpPost(serviceUrl + Constants.SERVICE_POST_METHOD);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/json");
			StringEntity params = new StringEntity(obj.toJSONString());
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);

			logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

			String result = EntityUtils.toString(response.getEntity());
			JSONParser parser = new JSONParser();
			JSONObject parsedJSON = (JSONObject)parser.parse(result);

			if(response.getStatusLine().getStatusCode() != Constants.STATUS_SUCCESS)
				throw new IllegalArgumentException(
						"Error while performing the update request: " + response.getStatusLine().getReasonPhrase() + 
						"and error in the result bean is " + parsedJSON.get(Constants.ERROR));

			// patch the catalogue product
			return patchProduct(catalogue, bean, username);

		}catch(Exception e){
			logger.error("Unable to update this Item " + e.getMessage());
			return e.getMessage();
		}

	}

	/**
	 * Patch the product
	 * @param catalogue
	 * @param bean
	 * @param username
	 */
	@SuppressWarnings("unchecked")
	private static String patchProduct(DataCatalogue catalogue,
			ManageProductBean bean, String username) {

		logger.info("Going to patch record in the catalogue with identifier " + bean.getCatalogueIdentifier() + 
				" from user " + username);

		String apiKey = catalogue.getApiKeyFromUsername(username);
		CkanDataset dataset = catalogue.getDataset(bean.getCatalogueIdentifier(), apiKey);
		String errorMessage = null;

		for (int i = 0; i < MAX_TRIAL; i++) {

			try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();){

				JSONObject jsonRequest = new JSONObject();
				JSONArray tagsAsJson = new JSONArray();
				JSONArray groupsAsJson = new JSONArray();
				JSONArray customFieldsAsJson = new JSONArray();

				// manage the custom fields
				List<CkanPair> extras = dataset.getExtras();
				for (CkanPair ckanPair : extras) {
					if(ckanPair.getKey().equals(Constants.STATUS_OF_THE_GRSF_RECORD_CUSTOM_KEY) && ckanPair.getValue().equals(bean.getCurrentStatus().toString()))
						continue;

					JSONObject obj = new JSONObject();
					obj.put("key", ckanPair.getKey());
					obj.put("value", ckanPair.getValue());
					customFieldsAsJson.add(obj);
				}

				// add the new one and the annotation message
				JSONObject newStatus = new JSONObject();
				newStatus.put("key", Constants.STATUS_OF_THE_GRSF_RECORD_CUSTOM_KEY);
				newStatus.put("value", bean.getNewStatus().toString());
				customFieldsAsJson.add(newStatus);

				JSONObject newAnnotation = new JSONObject();
				newAnnotation.put("key", Constants.ANNOTATION_CUSTOM_KEY);
				newAnnotation.put("value", "date: " + DATE_FORMAT.format(new Date())
						+ ", admin: " +  new LiferayUserManager().getUserByUsername(username).getFullname()
						+ ", message: " + (bean.getAnnotation() != null ? bean.getAnnotation().replaceAll("\"", "") : "none")
						+ ", old status: " + bean.getCurrentStatus().toString()
						+ ", new status: " + bean.getNewStatus().toString()
						);
				customFieldsAsJson.add(newAnnotation);

				// manage the tags
				List<CkanTag> tags = dataset.getTags();

				for(CkanTag ckanTag : tags){
					if(!ckanTag.getName().equals(bean.getCurrentStatus().toString())){
						JSONObject obj = new JSONObject();
						obj.put("vocabulary_id", ckanTag.getVocabularyId());
						obj.put("state", ckanTag.getState().toString());
						obj.put("display_name", ckanTag.getDisplayName());
						obj.put("id", ckanTag.getId());
						obj.put("name", ckanTag.getName());
						tagsAsJson.add(obj);
					}
				}

				// add the new one
				JSONObject newTag = new JSONObject();
				newTag.put("name", bean.getNewStatus().toString());
				newTag.put("display_name", bean.getNewStatus().toString());
				tagsAsJson.add(newTag);

				// manage the groups
				List<CkanGroup> groups = dataset.getGroups();
				for (CkanGroup ckanGroup : groups) {
					if(!ckanGroup.getName().equals("grsf" + "-" + bean.getCurrentStatus().toString().toLowerCase())){
						JSONObject obj = new JSONObject();
						obj.put("name", ckanGroup.getName());
						groupsAsJson.add(obj);
					}
				}

				JSONObject newGroup = new JSONObject();
				newGroup.put("name", "grsf" + "-" + bean.getNewStatus().toString().toLowerCase());
				groupsAsJson.add(newGroup);

				// perform the request
				jsonRequest.put("id", bean.getCatalogueIdentifier());
				jsonRequest.put("tags", tagsAsJson);
				jsonRequest.put("extras", customFieldsAsJson);
				jsonRequest.put("groups", groupsAsJson);

				logger.debug("Request param is going to be " + jsonRequest);

				if((errorMessage = catalogue.patchProductWithJSON(bean.getCatalogueIdentifier(), jsonRequest, apiKey)) == null){
					logger.info("Record patched ...");
					break;
				}else
					continue; // retry

			}catch(Exception e){
				logger.error("Error while trying to patch grsf record (iteration " + i + " of " + MAX_TRIAL + ")" + e.getMessage());
				errorMessage = e.getMessage();
			}
		}
		return errorMessage;
	}

	/**
	 * Get the scope in which ckan information needs to be discovered from the url
	 * @param httpServletRequest
	 * @return
	 */
	public static String getScopeFromClientUrl(HttpServletRequest httpServletRequest){

		if(httpServletRequest == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		String scopeToReturn = null;
		try{
			String clientUrl = getCurrentClientUrl(httpServletRequest).split("\\?")[0];
			logger.debug("Client url is " + clientUrl);

			// check if this information is in session, otherwise set it and return
			HttpSession session = httpServletRequest.getSession();

			if((scopeToReturn = (String) session.getAttribute(clientUrl)) != null){
				logger.debug("Scope to return is " + scopeToReturn);
			}else{
				// ask to the ckan library and set it
				scopeToReturn = ApplicationProfileScopePerUrlReader.getScopePerUrl(clientUrl);
				logger.debug("Scope to return is " + scopeToReturn);
				session.setAttribute(clientUrl, scopeToReturn);
			}
		}catch(Exception e){
			scopeToReturn = getCurrentContext(httpServletRequest, true);
			logger.warn("Failed to determine the scope from the client url, returning the current one: " + scopeToReturn);
		}
		return scopeToReturn;
	}

	/**
	 * Needed to get the url of the client
	 * @param httpServletRequest the httpServletRequest object
	 * @return the instance of the user 
	 * @see the url at client side
	 */
	public static String getCurrentClientUrl(HttpServletRequest httpServletRequest) {
		if(httpServletRequest == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		return httpServletRequest.getHeader(Constants.GCUBE_REQUEST_URL);
	}

	/**
	 * Retrieve the current scope by using the portal manager
	 * @param b 
	 * @return a GcubeUser object
	 */
	public static String getCurrentContext(HttpServletRequest request, boolean setInThread){

		if(request == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		PortalContext pContext = PortalContext.getConfiguration();
		String context = pContext.getCurrentScope(request);
		logger.debug("Returning context " + context);

		if(context != null && setInThread)
			ScopeProvider.instance.set(context);

		return context;
	}

	/**
	 * Retrieve the current user by using the portal manager
	 * @return a GcubeUser object
	 */
	public static GCubeUser getCurrentUser(HttpServletRequest request){

		if(request == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		PortalContext pContext = PortalContext.getConfiguration();
		GCubeUser user = pContext.getCurrentUser(request);
		logger.debug("Returning user " + user);
		return user;
	}

	/**
	 * Given a semantic identifier, check if a record exists and return it
	 * @param suggestedRecordSemanticIdentifier
	 * @param catalogue
	 * @return CkanDataset
	 * @throws Exception in case no record matches the semantic identifier
	 */
	public static CkanDataset getRecordBySemanticIdentifier(
			String suggestedRecordSemanticIdentifier, DataCatalogue catalogue,
			String apiKey) throws Exception {

		if(suggestedRecordSemanticIdentifier == null || suggestedRecordSemanticIdentifier.isEmpty())
			throw new Exception(Constants.GRSF_SEMANTIC_IDENTIFIER_CUSTOM_KEY + " cannot be null or emtpy");

		String query = Constants.GRSF_SEMANTIC_IDENTIFIER_CUSTOM_KEY + "\"" + suggestedRecordSemanticIdentifier+ "\"";
		List<CkanDataset> datasets = catalogue.searchForPackageInOrganization(apiKey, query, 0, 10, Constants.GRSF_ADMIN_ORGANIZATION_NAME);

		if(datasets == null || datasets.isEmpty()){
			String message = "Unable to find dataset with such " + Constants.GRSF_SEMANTIC_IDENTIFIER_CUSTOM_KEY;
			logger.warn(message);
			throw new Exception(message); 
		}

		if(datasets.size() == 1)
			return datasets.get(0);
		else{

			// worst situation.. we need to check for the right one
			for(CkanDataset dataset: datasets) 
				for(CkanPair extra : dataset.getExtras())
					if(extra.getKey().contains(Constants.GRSF_SEMANTIC_IDENTIFIER_CUSTOM_KEY) && extra.getValue().equals(suggestedRecordSemanticIdentifier))
						return dataset;

		}

		// in the end ....
		throw new Exception("Unable to find record with " + Constants.GRSF_SEMANTIC_IDENTIFIER_CUSTOM_KEY + " equals to " + suggestedRecordSemanticIdentifier);

	}

	/**
	 * Exploits the fact that in GRSF the url of a record contains the name (which is unique) of the record itself
	 * @param url
	 * @param clg
	 * @return
	 */
	public static CkanDataset getDatasetFromUrl(String url, DataCatalogue clg, String apiKey){

		if(url == null || url.isEmpty())
			return null;

		// Parse url
		// Create a Pattern object
		Pattern r = Pattern.compile(REGEX_UUID);

		// Now create matcher object.
		Matcher m = r.matcher(url);
		if (m.find()) {
			String uuidFound = m.group();
			logger.debug("Found match for uuid " + uuidFound);
			return clg.getDataset(uuidFound, apiKey);
		}

		return null;
	}

	//	/**
	//	 * Get extra information to show in the management panel, if any
	//	 * @param extrasAsPairs
	//	 */
	//	public static void getExtrasToShow(List<CkanPair> extrasAsPairs, ){
	//		
	//		Set<String> extrasToShow = getLookedUpExtrasKeys();
	//		if(extrasToShow != null && !extrasToShow.isEmpty()){
	//			Map<String, String> extrasKeyValuePair = new HashMap<String, String>();
	//			 = product.getExtras();
	//			for (CkanPair ckanPair : extrasAsPairs) {
	//				String key = ckanPair.getKey();
	//				String value = ckanPair.getValue();
	//
	//				if(extrasToShow.contains(key)){
	//					String currentValueInMap = extrasKeyValuePair.get(key);
	//					if(currentValueInMap == null)
	//						currentValueInMap = value;
	//					else
	//						currentValueInMap += ", " +  value;
	//					extrasKeyValuePair.put(key, currentValueInMap);
	//				}		
	//			}
	//			toReturn.setExtrasIfAvailable(extrasKeyValuePair);
	//		}
	//		
	//	}
	
	/**
	 * Get a {@link SimilarGRSFRecord} from a json string
	 * @param json
	 * @return {@link SimilarGRSFRecord}
	 * @throws ParseException 
	 */
	public static SimilarGRSFRecord similarGRSFRecordFromJson(String json) throws ParseException{

		if(json == null)
			return null;

		JSONParser parser = new JSONParser();
		JSONObject object = (JSONObject)parser.parse(json);

		return new SimilarGRSFRecord(
				(String)object.get(Constants.SIMILAR_RECORDS_BEAN_FIELD_DESCRIPTION),
				(String)object.get(Constants.SIMILAR_RECORDS_BEAN_FIELD_IDENTIFIER),
				(String)object.get(Constants.SIMILAR_RECORDS_BEAN_FIELD_NAME),
				(String)object.get(Constants.SIMILAR_RECORDS_BEAN_FIELD_URL)
				);

	}
	
	/**
	 * Checks if is into portal.
	 *
	 * @return true, if is into portal
	 */
	public static boolean isIntoPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		}catch (Exception ex) {
			logger.debug("Development Mode ON");
			return false;
		}
	}

}
