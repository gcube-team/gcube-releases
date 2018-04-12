package org.gcube.datacatalogue.grsf_manage_widget.server.manage;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.ApplicationProfileScopePerUrlReader;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueRunningCluster;
import org.gcube.datacatalogue.common.Constants;
import org.gcube.datacatalogue.common.enums.Product_Type;
import org.gcube.datacatalogue.common.enums.Status;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ConnectedBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ManageProductBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.RevertableOperationInfo;
import org.gcube.datacatalogue.grsf_manage_widget.shared.SimilarGRSFRecord;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.liferay.portal.service.UserLocalServiceUtil;

import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.CloseableHttpClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.HttpClientBuilder;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanPair;

/**
 * Utility methods for GRSF Management panel widget.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Utils {

	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	private static final String REGEX_UUID = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

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

			// put them into session for speeding up the operations
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
	 * Get the extras of this dataset as hashmap
	 * @param extrasAsPairs
	 * @return
	 */
	public static Map<String, List<String>> getExtrasAsHashMap(List<CkanPair> extrasAsPairs){

		Map<String, List<String>> toReturn = new HashMap<String, List<String>>();

		for (CkanPair ckanPair : extrasAsPairs) {
			String pairKey = ckanPair.getKey();
			String pairValue = ckanPair.getValue();

			List<String> values = null;
			if(toReturn.containsKey(pairKey))
				values = toReturn.get(pairKey);
			else
				values = new ArrayList<String>(1);

			values.add(pairValue);

			toReturn.put(pairKey, values);
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
	public static void updateRecord(
			String serviceUrl, 
			final ManageProductBean bean, 
			final DataCatalogue catalogue, 
			final String username,
			final String fullName, 
			final HttpServletRequest httpServletRequest, 
			final long groupId, 
			final String context, 
			final String token) throws Exception{

		if(serviceUrl == null)
			throw new IllegalArgumentException("GRSF Updater service url cannot be null");

		if(bean == null)
			throw new IllegalArgumentException("Item bean to manage cannot be null");

		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();){

			// send update to the knowledge base
			GRSFUpdaterServiceClient.updateKB(httpClient, serviceUrl, bean, catalogue, username, fullName);
			
			// if there are merges, update the status of the other involved records
			if(bean.isMergesInvolved())
				updateStatusInvolvedRecords(bean, catalogue);

			// require social networking url
			final String baseUrlSocial = SocialCommunications.getBaseUrlSocialService(httpServletRequest);

			// and the user current browser url
			final String currentBrowserUrl = Utils.getCurrentClientUrl(httpServletRequest).split("\\?")[0]; // ignore other parameters		

			// manage interactions through a separated thread but set there security token and context (and then reset them)
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					ScopeProvider.instance.set(context);
					SecurityTokenProvider.instance.set(token);
					try{

						// send email to Editors and Reviewers
						SocialCommunications.sendEmailAdministrators(baseUrlSocial, bean, catalogue, username, fullName, 
								groupId, currentBrowserUrl, bean.isMergesInvolved());

						//	create a post about the operation 
						SocialCommunications.writeProductPost(baseUrlSocial, bean, username, fullName, false, currentBrowserUrl);

					}catch(Exception e){
						logger.error("Something failed while alerting editors/reviewers", e);
					}finally{
						ScopeProvider.instance.reset();
						SecurityTokenProvider.instance.reset();
					}
				}
			});
			t.start();

		}catch(Exception e){
			logger.error("Unable to update this Item ", e);
			throw e;
		}
	}

	/**
	 * Revert operation and alert admins/vre users
	 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
	 * @param httpClient
	 * @param baseUrl
	 * @param fullName
	 * @param uuid
	 */
	public static void revertOperation(CloseableHttpClient httpClient, String baseUrl, HttpServletRequest httpServletRequest,
			final RevertableOperationInfo rInfo, final String token, final String context, final long groupId) throws Exception{

		GRSFUpdaterServiceClient.revertOperation(httpClient, baseUrl, rInfo.getFullNameCurrentAdmin(), rInfo.getUuid());

		// require social networking url
		final String baseUrlSocial = SocialCommunications.getBaseUrlSocialService(httpServletRequest);

		// and the user current browser url
		final String currentBrowserUrl = Utils.getCurrentClientUrl(httpServletRequest).split("\\?")[0]; // ignore other parameters		
		
		// manage interactions through a separated thread but set there security token and context (and then reset them)
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				ScopeProvider.instance.set(context);
				SecurityTokenProvider.instance.set(token);
				try{

					// write post about this
					SocialCommunications.writePostOnRevert(baseUrlSocial, rInfo, false, currentBrowserUrl);

					// alert who's involved
					SocialCommunications.sendEmailAdministratorsOnOperationReverted(baseUrlSocial, rInfo, groupId);

				}catch(Exception e){
					logger.error("Something failed while alerting editors/reviewers", e);
				}finally{
					ScopeProvider.instance.reset();
					SecurityTokenProvider.instance.reset();
				}
			}

		});
		t.start();

	}

	/**
	 * Update the status of the involved records to "to be merged"
	 * @param bean
	 * @param catalogue
	 * @param username
	 * @param fullName
	 * @throws Exception 
	 */
	private static void updateStatusInvolvedRecords(ManageProductBean bean, DataCatalogue catalogue) throws Exception {

		String context = ScopeProvider.instance.get();
		String sysApi = fetchSysAPI(context);
		for(SimilarGRSFRecord s: bean.getSimilarGrsfRecords()){
			if(s.isSuggestedMerge()){
				String productId = s.getKnowledgeBaseId();
				Map<String, List<String>> updateStatus = new HashMap<String, List<String>>(1);
				updateStatus.put(Constants.STATUS_OF_THE_GRSF_RECORD_CUSTOM_KEY, Arrays.asList(Status.To_be_Merged.getOrigName()));
				catalogue.patchProductCustomFields(productId, sysApi, updateStatus, true);
			}
		}

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
	 * Exploits the fact that in GRSF the url of a record contains the name (which is unique) of the record itself
	 * @param url
	 * @param clg
	 * @return
	 * @throws Exception 
	 */
	public static CkanDataset getDatasetFromUrl(String url, DataCatalogue clg, String apiKey) throws Exception{

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

		throw new Exception("No record exists with such url " + url);
	}

	/**
	 * Exploits the fact that in GRSF the url of a record contains the name (which is unique) of the record itself
	 * @param url
	 * @param clg
	 * @return
	 */
	public static String getDatasetKnowledgeBaseIdFromUrl(String url){

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
			return uuidFound;
		}

		return null;
	}


	/**
	 * Get a {@link SimilarGRSFRecord} from a json string
	 * @param json
	 * @return {@link SimilarGRSFRecord}
	 * @throws ParseException 
	 */
	public static SimilarGRSFRecord similarGRSFRecordFromJson(String json, DataCatalogue ctl, String apiKey, HttpSession httpSession) throws ParseException{

		if(json == null)
			return null;

		JSONParser parser = new JSONParser();
		JSONObject object = (JSONObject)parser.parse(json);

		String uuid = getDatasetKnowledgeBaseIdFromUrl((String)object.get(Constants.SIMILAR_RECORDS_BEAN_FIELD_URL));
		CkanDataset dataset = ctl.getDataset(uuid, apiKey);
		
		boolean isStock = dataset.getExtrasAsHashMap().get(Constants.DOMAIN_CUSTOM_KEY).contains(Product_Type.STOCK.getOrigName());
		Map<String, String> fieldsNamespacesMap = 
				Utils.getFieldToFieldNameSpaceMapping(httpSession, isStock ? 
						Constants.GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_STOCK : Constants.GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_FISHERY);
		Map<String, List<String>> extrasWithoutNamespaces = Utils.replaceFieldsKey(dataset.getExtras(), fieldsNamespacesMap);

		return new SimilarGRSFRecord(
				uuid,
				(String)object.get(Constants.SIMILAR_RECORDS_BEAN_FIELD_DESCRIPTION),
				(String)object.get(Constants.SIMILAR_RECORDS_BEAN_FIELD_NAME),
				dataset.getTitle(),
				(String)object.get(Constants.SIMILAR_RECORDS_BEAN_FIELD_URL),
				extrasWithoutNamespaces.get(Constants.GRSF_SEMANTIC_IDENTIFIER_CUSTOM_KEY).get(0),
				extrasWithoutNamespaces.get(Constants.DOMAIN_CUSTOM_KEY).get(0)
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

	/**
	 * Get Connected bean from record
	 * @param httpSession 
	 * @param json
	 * @param sourceIdentifier
	 * @param sourceDomain
	 * @param grsfDomain 
	 * @return
	 * @throws ParseException
	 */
	public static ConnectedBean connectedBeanRecordFromUrl(
			String destUrl,
			DataCatalogue clg,
			String apiKey, 
			HttpSession httpSession
			) throws ParseException {

		if(destUrl == null)
			return null;

		String connectedBeanUuid = getDatasetKnowledgeBaseIdFromUrl(destUrl);
		CkanDataset destDataset = clg.getDataset(connectedBeanUuid, apiKey);
		boolean isStock = destDataset.getExtrasAsHashMap().get(Constants.DOMAIN_CUSTOM_KEY).contains(Product_Type.STOCK.getOrigName());
		Map<String, String> fieldsNamespacesMap = 
				Utils.getFieldToFieldNameSpaceMapping(httpSession, isStock ? 
						Constants.GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_STOCK : Constants.GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_FISHERY);
		Map<String, List<String>> extrasWithoutNamespaces = Utils.replaceFieldsKey(destDataset.getExtras(), fieldsNamespacesMap);
		String semanticId = extrasWithoutNamespaces.get(Constants.GRSF_SEMANTIC_IDENTIFIER_CUSTOM_KEY).get(0);
		String destDomain = extrasWithoutNamespaces.get(Constants.DOMAIN_CUSTOM_KEY).get(0);
		String shortName = extrasWithoutNamespaces.get(Constants.SHORT_NAME_CUSTOM_KEY).get(0);
		String description = destDataset.getNotes();

		return new ConnectedBean(
				connectedBeanUuid,
				description,
				shortName,
				destDataset.getTitle(),
				destUrl,
				semanticId,
				destDomain
				);

	}

	/**
	 * Fetch the sysadmin key from the IS for this catalogue
	 * @return
	 * @throws Exception 
	 */
	public static String fetchSysAPI(String context) throws Exception{
		DataCatalogueRunningCluster catalogueRunningInstance = new DataCatalogueRunningCluster(context);
		return catalogueRunningInstance.getSysAdminToken();
	}

}
