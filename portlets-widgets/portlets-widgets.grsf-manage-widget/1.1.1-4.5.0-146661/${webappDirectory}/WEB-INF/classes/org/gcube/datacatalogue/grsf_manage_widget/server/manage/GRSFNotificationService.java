package org.gcube.datacatalogue.grsf_manage_widget.server.manage;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.ApplicationProfileScopePerUrlReader;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.UtilMethods;
import org.gcube.datacatalogue.grsf_manage_widget.client.GRSFManageWidgetService;
import org.gcube.datacatalogue.grsf_manage_widget.shared.GRSFStatus;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ManageProductBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ex.NoGRSFRecordException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

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
 * Endpoint for sending update records information to GRSF KB
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class GRSFNotificationService extends RemoteServiceServlet implements GRSFManageWidgetService{

	private static final long serialVersionUID = -4534905087994875893L;
	private static final Log logger = LogFactoryUtil.getLog(GRSFNotificationService.class);
	private static final String ANNOTATION_KEY = "Annotation on update";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final int MAX_TRIAL = 5;

	// info of the record to be shown at client-side
	private static final String STATUS_CUSTOM_FIELD_KEY = "Status";
	private static final String KB_UUID_FIELD_KEY = "UUID Knowledge Base";
	//	private static final String TYPE_FIELD_KEY = "Type";
	private static final String GRSF_TYPE_FIELD_KEY = "GRSF type";
	//	private static final String SHORT_TITLE_FIELD_KEY = "Short Title";
	//	private static final String SOURCES_TITLE_FIELD_KEY = "Source";

	// request post fields FORTH Service
	private static final String CATALOGUE_ID = "catalog_id";
	private static final String KB_ID = "record_id";
	private static final String PRODUCT_TYPE = "type";
	private static final String STATUS = "status";
	private static final String ANNOTATION = "annotation_msg";
	private static final String ERROR = "error";

	// discover the endpoint of the grsf updater on IS
	public static final String GRSF_UPDATER_SERVICE  = "GRSFUpdaterEndPoint";
	private static final String SERVICE_POST_METHOD = "/service/updater/post";

	// the error of the update on success
	private static final int STATUS_SUCCESS = 200;

	// GRSF update service information
	private static final String SERVICE_NAME = "GRSF Updater";
	private static final String SERVICE_CATEGORY = "Service";

	// request url
	public static final String GCUBE_REQUEST_URL = "gcube-request-url";

	// session info for user
	public static final String GRSF_ADMIN_SESSION_KEY = "IS_GRSF_ADMIN";
	private static final String GRSF_CATALOGUE_MANAGER_ROLE = "Catalogue Manager"; // managed as Team Role

	/**
	 * Instanciate the ckan util library.
	 * Since it needs the scope, we need to check if it is null or not
	 * @param discoverScope if you want to the discover the utils library in this specified scope
	 * @return
	 */
	public DataCatalogue getCatalogue(String discoverScope){
		String currentScope = getCurrentContext(getThreadLocalRequest(), false);
		DataCatalogue instance = null;
		try{
			String scopeInWhichDiscover = discoverScope != null && !discoverScope.isEmpty() ? discoverScope : currentScope;
			logger.debug("Discovering ckan utils library into scope " + scopeInWhichDiscover);
			instance = DataCatalogueFactory.getFactory().getUtilsPerScope(scopeInWhichDiscover);
		}catch(Exception e){
			logger.error("Unable to retrieve ckan utils. Error was " + e.toString());
		}
		return instance;
	}

	@Override
	public String notifyProductUpdate(ManageProductBean bean) {

		logger.info("Creating notification for the bean " + bean + " to send to the knowledge base");
		try{

			String context = getScopeFromClientUrl(getThreadLocalRequest());
			DataCatalogue catalogue = getCatalogue(context);

			// check if the base url of the service is in session
			String keyPerContext = UtilMethods.concatenateSessionKeyScope(GRSF_UPDATER_SERVICE, context);
			String baseUrl = (String)getThreadLocalRequest().getSession().getAttribute(keyPerContext);
			if(baseUrl == null ||  baseUrl.isEmpty()){
				baseUrl = GRSFNotificationService.discoverEndPoint(context);
				getThreadLocalRequest().getSession().setAttribute(keyPerContext, baseUrl);
			}
			return GRSFNotificationService.updateCatalogueRecord(baseUrl, bean, catalogue, getCurrentUser(getThreadLocalRequest()).getUsername());

		}catch(Exception e){
			logger.error("Unable to update the product.." + e.getMessage());
			return e.getMessage();
		}
	}

	@Override
	public ManageProductBean getProductBeanById(String productIdentifier) throws Exception {

		ManageProductBean toReturn = null;

		// retrieve scope per current portlet url
		String scopePerCurrentUrl = getScopeFromClientUrl(getThreadLocalRequest());
		DataCatalogue catalogue = getCatalogue(scopePerCurrentUrl);
		String username = getCurrentUser(getThreadLocalRequest()).getUsername();
		CkanDataset product = catalogue.getDataset(productIdentifier, catalogue.getApiKeyFromUsername(username));

		// it cannot be enabled in this case ...
		if(product == null)
			throw new Exception("Unable to retrieve information for the selected item, sorry");
		else{

			toReturn = new ManageProductBean();

			// check it is a grsf item, else..
			List<CkanGroup> groups = product.getGroups();
			boolean isGrsf = false;

			for (CkanGroup ckanGroup : groups) {
				logger.debug("Group has name " + ckanGroup.getName());
				if(ckanGroup.getName().toLowerCase().contains("grsf")){
					isGrsf = true;
					break;
				}
			}

			if(!isGrsf)
				throw new NoGRSFRecordException("This is not a GRSF Item");

			// get extras
			Map<String, String> extrasAsHashMap = product.getExtrasAsHashMap();
			String status = extrasAsHashMap.get(STATUS_CUSTOM_FIELD_KEY);
			String uuidKB = extrasAsHashMap.get(KB_UUID_FIELD_KEY);
			//			String productType = extras.get(TYPE_FIELD_KEY);
			String productGRSFType = extrasAsHashMap.get(GRSF_TYPE_FIELD_KEY);
			//			String semanticId = extras.get(productGRSFType + " id"); // i.e "Stock id" or "Fishery id"
			//			String shortTitle = extras.get(SHORT_TITLE_FIELD_KEY);
			//			String sources = extras.get(SOURCES_TITLE_FIELD_KEY);
			String title = product.getTitle();
			String description = product.getNotes();

			// fetch extras
			GenericResourceReaderExtras entries = new GenericResourceReaderExtras();
			Set<String> extrasToShow = entries.getLookedUpExtrasKeys();

			if(extrasToShow != null && !extrasToShow.isEmpty()){
				Map<String, String> extrasKeyValuePair = new HashMap<String, String>();
				List<CkanPair> extrasAsPairs = product.getExtras();
				for (CkanPair ckanPair : extrasAsPairs) {
					String key = ckanPair.getKey();
					String value = ckanPair.getValue();

					if(extrasToShow.contains(key)){
						String currentValueInMap = extrasKeyValuePair.get(key);
						if(currentValueInMap == null)
							currentValueInMap = value;
						else
							currentValueInMap += ", " +  value;
						extrasKeyValuePair.put(key, currentValueInMap);
					}		
				}
				toReturn.setExtrasIfAvailable(extrasKeyValuePair);
			}

			if(status == null || uuidKB == null)
				throw new Exception("Some information is missing in this record: Status = " + status + ", knowledge_base_uuid = " + uuidKB + 
						", and grsf type is = " + productGRSFType);

			toReturn.setCatalogueIdentifier(productIdentifier);
			toReturn.setCurrentStatus(GRSFStatus.fromString(status));
			toReturn.setKnowledgeBaseIdentifier(uuidKB);
			toReturn.setItemTitle(title);
			toReturn.setGrsfType(productGRSFType);
			toReturn.setDescription(description);

			logger.info("Returning item bean " + toReturn);

			return toReturn;
		}
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
			query.addCondition("$resource/Profile/Name/text() eq '"+ SERVICE_NAME +"'");
			query.addCondition("$resource/Profile/Category/text() eq '"+ SERVICE_CATEGORY +"'");
			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
			List<ServiceEndpoint> resources = client.submit(query);

			if (resources.size() == 0){
				logger.error("There is no Runtime Resource having name " + SERVICE_NAME +" and Category " + SERVICE_CATEGORY + " in this scope.");
				throw new Exception("There is no Runtime Resource having name " + SERVICE_NAME +" and Category " + SERVICE_CATEGORY + " in this scope.");
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
			obj.put(CATALOGUE_ID, bean.getCatalogueIdentifier());
			obj.put(KB_ID, bean.getKnowledgeBaseIdentifier());
			obj.put(PRODUCT_TYPE, bean.getGrsfType().toLowerCase());
			obj.put(STATUS, bean.getNewStatus().toString().toLowerCase());

			String annotation = bean.getAnnotation();
			if(annotation != null)
				obj.put(ANNOTATION, annotation.replaceAll("\"", ""));

			logger.debug("Update request looks like " + obj.toJSONString());

			HttpPost request = new HttpPost(serviceUrl + SERVICE_POST_METHOD);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/json");
			StringEntity params = new StringEntity(obj.toJSONString());
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);

			logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

			String result = EntityUtils.toString(response.getEntity());
			JSONParser parser = new JSONParser();
			JSONObject parsedJSON = (JSONObject)parser.parse(result);

			if(response.getStatusLine().getStatusCode() != STATUS_SUCCESS)
				throw new IllegalArgumentException(
						"Error while performing the update request: " + response.getStatusLine().getReasonPhrase() + 
						"and error in the result bean is " + parsedJSON.get(ERROR));

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
					if(ckanPair.getKey().equals(STATUS_CUSTOM_FIELD_KEY) && ckanPair.getValue().equals(bean.getCurrentStatus().toString()))
						continue;

					JSONObject obj = new JSONObject();
					obj.put("key", ckanPair.getKey());
					obj.put("value", ckanPair.getValue());
					customFieldsAsJson.add(obj);
				}

				// add the new one and the annotation message
				JSONObject newStatus = new JSONObject();
				newStatus.put("key", STATUS_CUSTOM_FIELD_KEY);
				newStatus.put("value", bean.getNewStatus().toString());
				customFieldsAsJson.add(newStatus);

				JSONObject newAnnotation = new JSONObject();
				newAnnotation.put("key", ANNOTATION_KEY);
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
			scopeToReturn = getCurrentContext(httpServletRequest, false);
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

		return httpServletRequest.getHeader(GCUBE_REQUEST_URL);
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


	@Override
	public boolean isAdminUser() {
		try{

			Boolean inSession = (Boolean)getThreadLocalRequest().getSession().getAttribute(GRSF_ADMIN_SESSION_KEY);

			if(inSession != null)
				return inSession;
			else{
				PortalContext pContext = PortalContext.getConfiguration();
				List<GCubeTeam> teamRoles = new LiferayRoleManager().listTeamsByUserAndGroup(pContext.getCurrentUser(getThreadLocalRequest()).getUserId(), pContext.getCurrentGroupId(getThreadLocalRequest()));
				boolean toSetInSession = false;
				for (GCubeTeam team : teamRoles) {
					if(team.getTeamName().equals(GRSF_CATALOGUE_MANAGER_ROLE)){
						toSetInSession = true;
						break;
					}
				}
				getThreadLocalRequest().getSession().setAttribute(GRSF_ADMIN_SESSION_KEY, toSetInSession);
				return toSetInSession;
			}
		}catch(Exception e){
			logger.error("Failed to check if the user has team " + GRSF_CATALOGUE_MANAGER_ROLE, e);
		}
		return false;
	}
}
