package org.gcube.datacatalogue.grsf_manage_widget.server.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.CatalogueUtilMethods;
import org.gcube.datacatalogue.common.Constants;
import org.gcube.datacatalogue.common.enums.Product_Type;
import org.gcube.datacatalogue.common.enums.Sources;
import org.gcube.datacatalogue.common.enums.Status;
import org.gcube.datacatalogue.grsf_manage_widget.client.GRSFManageWidgetService;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ConnectedBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ManageProductBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.RevertableOperationInfo;
import org.gcube.datacatalogue.grsf_manage_widget.shared.RevertableOperations;
import org.gcube.datacatalogue.grsf_manage_widget.shared.SimilarGRSFRecord;
import org.gcube.datacatalogue.grsf_manage_widget.shared.SourceRecord;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ex.NoGRSFRecordException;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.CloseableHttpClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.HttpClientBuilder;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanPair;
import eu.trentorise.opendata.jackan.model.CkanResource;

/**
 * Endpoint for sending update records information to GRSF KnowledgeBase.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class GRSFNotificationService extends RemoteServiceServlet implements GRSFManageWidgetService{

	private static final long serialVersionUID = -4534905087994875893L;
	private static final Log logger = LogFactoryUtil.getLog(GRSFNotificationService.class);
	//private static final Logger logger = LoggerFactory.getLogger(GRSFNotificationService.class);

	/**
	 * Instanciate the ckan util library.
	 * Since it needs the scope, we need to check if it is null or not
	 * @param discoverScope if you want to the discover the utils library in this specified scope
	 * @return DataCatalogue object
	 * @throws Exception
	 */
	public DataCatalogue getCatalogue(String discoverScope) throws Exception{
		String currentScope = Utils.getCurrentContext(getThreadLocalRequest(), true);
		DataCatalogue instance = null;
		try{
			String scopeInWhichDiscover = discoverScope != null && !discoverScope.isEmpty() ? discoverScope : currentScope;
			logger.debug("Discovering ckan utils library into scope " + scopeInWhichDiscover);
			instance = DataCatalogueFactory.getFactory().getUtilsPerScope(scopeInWhichDiscover);
		}catch(Exception e){
			logger.error("Unable to retrieve ckan utils. Error was ", e);
			throw e;
		}
		return instance;
	}

	@Override
	public ManageProductBean getProductBeanById(String productIdentifier, boolean requestForRevertingMerge) throws Exception {

		ManageProductBean toReturn = null;

		// check into user's session first
		HttpSession httpSession = getThreadLocalRequest().getSession();

		// testing case...
		if(!Utils.isIntoPortal()){

			Thread.sleep(2000);

			toReturn = new ManageProductBean();
			toReturn.setCatalogueIdentifier(UUID.randomUUID().toString());
			List<ConnectedBean> connectTo = new ArrayList<>();
			// these are the records alread connected
			connectTo.add(new ConnectedBean(
					"uuid-of-a-connected-bean",
					"Random description",
					"Random shortName",
					"Random Title",
					"http://data.d4science.org/ctlg/GRSF_Admin/uuid-of-a-connected-bean",
					"semantic identifier of the record",
					"Fishery"
					));
			toReturn.setCurrentConnections(connectTo);

			// these are the "suggested connections"
			List<ConnectedBean> suggestionsForConnections = new ArrayList<>();
			suggestionsForConnections.add(new ConnectedBean(
					"uuid-of-a-connected-bean-suggested",
					"Random description",
					"Random shortName",
					"Random Title",
					"http://data.d4science.org/ctlg/GRSF_Admin/uuid-of-a-connected-bean-suggested",
					"semantic identifier of the record suggested for connection",
					"Fishery"
					));
			toReturn.setSuggestedByKnowledgeBaseConnections(suggestionsForConnections);
			toReturn.setDomain("Stock");
			toReturn.setCurrentGrsfType("Assessment Unit");
			toReturn.setKnowledgeBaseId("91f1e413-dc9f-3b4e-b1c5-0e8560177253");
			toReturn.setShortName("Widow rockfish - US West Coast");
			toReturn.setShortNameUpdated("Widow rockfish - US West Coast");
			toReturn.setTitle("sebastes entomelas FAO 77 FAO 67");
			toReturn.setTraceabilityFlag(true);
			toReturn.setCurrentStatus(Status.Pending);
			toReturn.setSemanticIdentifier("asfis:WRO+fao:67;FAO");
			ArrayList<SourceRecord> sources = new ArrayList<SourceRecord>();
			sources.add(new SourceRecord("RAM", "http://www.google.it"));
			sources.add(new SourceRecord("FIRMS", "http://www.google.it"));
			sources.add(new SourceRecord("FishSource", "http://www.google.it"));
			toReturn.setSources(sources);
			List<SimilarGRSFRecord> similarGrsfRecords = new ArrayList<SimilarGRSFRecord>();
			similarGrsfRecords.add(new SimilarGRSFRecord(
					"uuid-similar-record-1",
					"description similar record",
					"short name similar record 1",
					"title similar record 1",
					"http://data.d4science.org/ctlg/GRSF_Admin/uuid-similar-record-1",
					"semantic identifier record 1",
					"Stock 1"
					));
			similarGrsfRecords.add(new SimilarGRSFRecord(
					"uuid-similar-record-2",
					"description similar record",
					"short name similar record 2",
					"title similar record 2",
					"http://data.d4science.org/ctlg/GRSF_Admin/uuid-similar-record-2",
					"semantic identifier record 2",
					"Stock 2"
					));
			similarGrsfRecords.add(new SimilarGRSFRecord(
					"uuid-similar-record-3",
					"description similar record",
					"short name similar record 3",
					"title similar record 3",
					"http://data.d4science.org/ctlg/GRSF_Admin/uuid-similar-record-3",
					"semantic identifier record 3",
					"Stock 3"
					));
			similarGrsfRecords.add(new SimilarGRSFRecord(
					"uuid-similar-record-4",
					"description similar record",
					"short name similar record 4",
					"title similar record 4",
					"http://data.d4science.org/ctlg/GRSF_Admin/uuid-similar-record-4",
					"semantic identifier record 4",
					"Stock 4"
					));
			similarGrsfRecords.add(new SimilarGRSFRecord(
					"uuid-similar-record-5",
					"description similar record",
					"short name similar record 5",
					"title similar record 5",
					"http://data.d4science.org/ctlg/GRSF_Admin/uuid-similar-record-5",
					"semantic identifier record 5",
					"Stock 5"
					));
			similarGrsfRecords.add(new SimilarGRSFRecord(
					"uuid-similar-record-6",
					"description similar record",
					"short name similar record 6",
					"title similar record 6",
					"http://data.d4science.org/ctlg/GRSF_Admin/uuid-similar-record-6",
					"semantic identifier record 6",
					"Stock 6"
					));
			toReturn.setSimilarGrsfRecords(similarGrsfRecords);

		}else{

			String scopePerCurrentUrl = Utils.getScopeFromClientUrl(getThreadLocalRequest());
			DataCatalogue catalogue = getCatalogue(scopePerCurrentUrl);
			String username = Utils.getCurrentUser(getThreadLocalRequest()).getUsername();
			String apiKey = catalogue.getApiKeyFromUsername(username);
			CkanDataset record = catalogue.getDataset(productIdentifier, apiKey);

			// it cannot be enabled in this case ...
			if(record == null)
				throw new Exception("Unable to retrieve information for the selected record, sorry");
			else{

				logger.debug("Trying to fetch the record....");

				// check it is a grsf record (Source records have a different System Type)
				Map<String, String> extrasAsMap = record.getExtrasAsHashMap();

				String systemType = extrasAsMap.get(Constants.SYSTEM_TYPE_CUSTOM_KEY);
				if(systemType == null || systemType.isEmpty() || systemType.equals(Constants.SYSTEM_TYPE_FOR_SOURCES_VALUE))
					throw new NoGRSFRecordException("This is not a GRSF Record");

				boolean isStock = record.getExtrasAsHashMap().get(Constants.DOMAIN_CUSTOM_KEY).contains(Product_Type.STOCK.getOrigName());

				// fetch map for namespaces
				Map<String, String> fieldsNamespacesMap =
						Utils.getFieldToFieldNameSpaceMapping(httpSession, isStock ?
								Constants.GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_STOCK : Constants.GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_FISHERY);

				// get extras as pairs
				List<CkanPair> extrasAsPairs = record.getExtras();
				Map<String, List<String>> extrasWithoutNamespaces = Utils.replaceFieldsKey(extrasAsPairs, fieldsNamespacesMap);
				String catalogueIdentifier = record.getId();
				String description = record.getNotes();
				Status status = Status.fromString(extrasWithoutNamespaces.get(Constants.STATUS_OF_THE_GRSF_RECORD_CUSTOM_KEY).get(0));

				if(status.equals(Status.To_be_Merged) && !requestForRevertingMerge)
					throw new Exception("The record is locked due to a merge request in progress!");

				String uuidKB = extrasWithoutNamespaces.get(Constants.UUID_KB_CUSTOM_KEY).get(0);
				String grsfDomain = extrasWithoutNamespaces.get(Constants.DOMAIN_CUSTOM_KEY).get(0);
				String semanticId = extrasWithoutNamespaces.get(Constants.GRSF_SEMANTIC_IDENTIFIER_CUSTOM_KEY).get(0);
				String shortName = extrasWithoutNamespaces.get(Constants.SHORT_NAME_CUSTOM_KEY).get(0);
				String grsfType = extrasWithoutNamespaces.get(Constants.GRSF_TYPE_CUSTOM_KEY).get(0);
				String recordUrl = extrasWithoutNamespaces.get(Constants.ITEM_URL_FIELD).get(0);
				String grsfName = extrasWithoutNamespaces.get(grsfDomain.contains(Product_Type.STOCK.getOrigName()) ? Constants.STOCK_NAME_CUSTOM_KEY : Constants.FISHERY_NAME_CUSTOM_KEY).get(0);
				boolean traceabilityFlag = false;
				try{
					traceabilityFlag = extrasWithoutNamespaces.get(Constants.TRACEABILITY_FLAG_CUSTOM_KEY).get(0).equalsIgnoreCase("true");
				}catch(Exception e){
					logger.warn("Unable to fetch traceability flag. Setting it to false", e);
				}

				boolean sdgFlag = false;
				try{
					sdgFlag = extrasWithoutNamespaces.get(Constants.SDG_FLAG_CUSTOM_KEY).get(0).equalsIgnoreCase("true");
				}catch(Exception e){
					logger.warn("Unable to fetch sdg flag. Setting it to false", e);
				}

				// Get similar GRSF records, if any (each of which should have name, description, url and id(i.e semantic identifier))
				List<String> similarGrsfRecordsAsStrings = extrasWithoutNamespaces.containsKey(Constants.SIMILAR_GRSF_RECORDS_CUSTOM_KEY) ? extrasWithoutNamespaces.get(Constants.SIMILAR_GRSF_RECORDS_CUSTOM_KEY): null;

				List<SimilarGRSFRecord> similarRecords = new ArrayList<SimilarGRSFRecord>(0);
				if(similarGrsfRecordsAsStrings != null && !similarGrsfRecordsAsStrings.isEmpty()){
					if(!similarGrsfRecordsAsStrings.get(0).equals(Constants.NO_SIMILAR_GRSF_RECORDS)){
						for (String similarGRSFRecord : similarGrsfRecordsAsStrings) {
							similarRecords.add(Utils.similarGRSFRecordFromJson(similarGRSFRecord, catalogue, apiKey, httpSession));
						}
					}
				}

				logger.debug("SimilarGRSFRecords are " + similarRecords);

				// get connected records (and the proposed ones)
				List<String> connectedBeanUrls =
						extrasWithoutNamespaces.containsKey(Constants.CONNECTED_CUSTOM_KEY) ? extrasWithoutNamespaces.get(Constants.CONNECTED_CUSTOM_KEY): null;

						List<ConnectedBean> connectedBeans = new ArrayList<ConnectedBean>(0);
						if(connectedBeanUrls != null && !connectedBeanUrls.isEmpty()){
							if(!connectedBeanUrls.get(0).equals(Constants.NO_CONNECTED_RECORDS)){
								for (String connectedBean : connectedBeanUrls) {
									ConnectedBean builtBean = Utils.connectedBeanRecordFromUrl(connectedBean, catalogue, apiKey, httpSession);
									if(builtBean != null)
										connectedBeans.add(builtBean);
								}
							}
						}

						logger.debug("Already connected records are " + connectedBeans);

						// get the connections the knowledge base suggests
						List<ConnectedBean> suggestedConnectionsByKnowledgeBase = new ArrayList<ConnectedBean>(0);
						List<String> exploitedResourcesUrls =  isStock ?
								extrasWithoutNamespaces.containsKey(Constants.EXPLOITING_FISHERY_CUSTOM_KEY) ?
										extrasWithoutNamespaces.get(Constants.EXPLOITING_FISHERY_CUSTOM_KEY) : null:
											extrasWithoutNamespaces.containsKey(Constants.RESOURCES_EXPLOITED_CUSTOM_KEY) ?
													extrasWithoutNamespaces.get(Constants.RESOURCES_EXPLOITED_CUSTOM_KEY) : null;

										if(exploitedResourcesUrls != null && !exploitedResourcesUrls.isEmpty()){
											for (String exploited : exploitedResourcesUrls) {
												ConnectedBean builtBean = Utils.connectedBeanRecordFromUrl(exploited, catalogue, apiKey, httpSession);
												if(builtBean != null)
													suggestedConnectionsByKnowledgeBase.add(builtBean);
											}
										}

										logger.debug("Knowledge base suggests " + suggestedConnectionsByKnowledgeBase);

										// Get sources
										List<CkanResource> resources = record.getResources();
										List<SourceRecord> sources = new ArrayList<SourceRecord>(3);
										for (CkanResource ckanResource : resources) {
											if(Sources.getListNames().contains(ckanResource.getName()))
												sources.add(new SourceRecord(ckanResource.getName(), ckanResource.getUrl()));
										}

										// set the values
										toReturn = new ManageProductBean(
												semanticId, catalogueIdentifier, uuidKB, grsfType,
												grsfDomain, shortName, description, grsfName,traceabilityFlag, sdgFlag,
												status, recordUrl, sources, similarRecords,
												connectedBeans, suggestedConnectionsByKnowledgeBase);

			}
		}

		logger.debug("Returning item bean " + toReturn);
		return toReturn;
	}

	@Override
	public boolean isAdminUser() {
		try{
			Boolean inSession = (Boolean)getThreadLocalRequest().getSession().getAttribute(Constants.GRSF_ADMIN_SESSION_KEY);
			if(inSession != null)
				return inSession;
			else{

				boolean toSetInSession = false;
				if(!Utils.isIntoPortal()){
					toSetInSession = true;
				}else{
					PortalContext pContext = PortalContext.getConfiguration();
					RoleManager roleManager = new LiferayRoleManager();
					String username =  pContext.getCurrentUser(getThreadLocalRequest()).getUsername();
					long userId = pContext.getCurrentUser(getThreadLocalRequest()).getUserId();
					long groupId = pContext.getCurrentGroupId(getThreadLocalRequest());
					List<GCubeTeam> teamRolesByUser = roleManager.listTeamsByUserAndGroup(userId, groupId);
					toSetInSession = isEditor(username, teamRolesByUser) | isReviewer(username, teamRolesByUser);
				}
				getThreadLocalRequest().getSession().setAttribute(Constants.GRSF_ADMIN_SESSION_KEY, toSetInSession);
				return toSetInSession;
			}
		}catch(Exception e){
			logger.error("Failed to check if the user belongs to team " + Constants.GRSF_CATALOGUE_EDITOR_ROLE   + " or " + Constants.GRSF_CATALOGUE_REVIEWER_ROLE +"!", e);
		}
		return false;
	}

	@Override
	public void notifyProductUpdate(ManageProductBean bean) throws Exception{

		logger.info("Creating notification for the bean " + bean + " to send to the knowledge base");
		if(!Utils.isIntoPortal()){
			Thread.sleep(2500);
			return;
		}
		try{

			String context = Utils.getScopeFromClientUrl(getThreadLocalRequest());
			String token = SecurityTokenProvider.instance.get();
			DataCatalogue catalogue = getCatalogue(context);
			String administratorFullName = Utils.getCurrentUser(getThreadLocalRequest()).getFullname();
			String username = Utils.getCurrentUser(getThreadLocalRequest()).getUsername();

			// check if the base url of the service is in session
			String keyPerContext = CatalogueUtilMethods.concatenateSessionKeyScope(Constants.GRSF_UPDATER_SERVICE, context);
			HttpServletRequest threadRequest = getThreadLocalRequest();
			String baseUrl = (String)threadRequest.getSession().getAttribute(keyPerContext);
			if(baseUrl == null ||  baseUrl.isEmpty()){
				baseUrl = GRSFUpdaterServiceClient.discoverEndPoint(context);
				threadRequest.getSession().setAttribute(keyPerContext, baseUrl);
			}

			// remove it from the session
			String sessionProductKey = ScopeProvider.instance.get() + bean.getCatalogueIdentifier();
			threadRequest.getSession().removeAttribute(sessionProductKey);

			Utils.updateRecord(baseUrl, bean, catalogue, username, administratorFullName, threadRequest,
					PortalContext.getConfiguration().getCurrentGroupId(threadRequest), context, token);

		}catch(Exception e){
			logger.error("Unable to update the product", e);
			throw e;
		}
	}

	@Override
	public RevertableOperationInfo validateRevertOperation(String encryptedUrl) throws Exception {

		if(!Utils.isIntoPortal()){
			Thread.sleep(2000);

			// random result
			boolean throwException = Math.random() > 0.5;

			if(throwException)
				throw new Exception("Unable to parse the inserted url");

			String baseUrl = "url of the record here";
			String fullName = "Andrea Rossi";
			String usernameCurrent = "andrea.rossi";
			String uuid = UUID.randomUUID().toString();
			String adminInUrl = "costantino.perciante";
			String adminInUrlFullName = "Costantino Perciante";
			long timestamp = System.currentTimeMillis() - 1000 * (long)(Math.random() * 10 * 60 * 60);
			return new RevertableOperationInfo(
					baseUrl, fullName, usernameCurrent, uuid, adminInUrlFullName, adminInUrl, timestamp, RevertableOperations.MERGE);
		}

		PortalContext pContext = PortalContext.getConfiguration();
		String context = Utils.getScopeFromClientUrl(getThreadLocalRequest());
		RoleManager roleManager = new LiferayRoleManager();
		GCubeUser user =  pContext.getCurrentUser(getThreadLocalRequest());
		String username = user.getUsername();
		String fullName = user.getFullname();
		long userId = pContext.getCurrentUser(getThreadLocalRequest()).getUserId();
		long groupId = pContext.getCurrentGroupId(getThreadLocalRequest());
		List<GCubeTeam> teamRolesByUser = roleManager.listTeamsByUserAndGroup(userId, groupId);

		boolean isEditor = isEditor(username, teamRolesByUser);
		boolean isReviewer = isReviewer(username, teamRolesByUser);

		if(!(isEditor | isReviewer))
			throw new Exception("You are not allowed to perform this operation. You must be an editor or a reviewer!");

		// decrypt the url
		RevertOperationUrl decryptedUrl = new RevertOperationUrl(encryptedUrl);
		String userNameadminInUrl = decryptedUrl.getAdmin(); // this is the username
		String fullNameadminInUrl = new LiferayUserManager().getUserByUsername(userNameadminInUrl).getFullname(); // this is the fullname
		String uuid = decryptedUrl.getUuid();

		logger.info("User " + username + " has requested to invert an operation on record with id " + uuid + " and admin in url is " + userNameadminInUrl);

		// we need to check the timestamp (it has 24h validity)
		boolean isValidTimestamp = decryptedUrl.isTimestampValid();

		if(!isValidTimestamp)
			throw new Exception("This operation can no longer be reverted (link expired)!");

		DataCatalogue catalogue = getCatalogue(context);
		CkanDataset dataset = catalogue.getDataset(uuid, catalogue.getApiKeyFromUsername(username));
		Map<String, String> extras = dataset.getExtrasAsHashMap();
		String recordUrl = extras.get(Constants.ITEM_URL_FIELD);
		String currentStatus = extras.get(Constants.STATUS_OF_THE_GRSF_RECORD_CUSTOM_KEY);

		// check current record status
		if(!currentStatus.equals(Status.To_be_Merged.getOrigName()))
			throw new Exception("Record '" + dataset.getTitle()  + "' (" + recordUrl + ") is no longer involved in a merge operation!");

		// check if it is a reviewer, than he can do what he wants (no matter the admin)
		if(isReviewer){
			return new RevertableOperationInfo(recordUrl,
					fullName, username, uuid, fullNameadminInUrl, userNameadminInUrl, decryptedUrl.getTimestamp(), decryptedUrl.getOperation());
		}else{

			if(!username.equals(userNameadminInUrl))
				throw new Exception("You are not the editor allowed to perform this operation!");
			else
				return new RevertableOperationInfo(recordUrl,
						fullName, username, uuid, fullNameadminInUrl, userNameadminInUrl, decryptedUrl.getTimestamp(), decryptedUrl.getOperation());
		}

	}

	@Override
	public Boolean performRevertOperation(RevertableOperationInfo rInfo)
			throws Exception {

		if(!Utils.isIntoPortal()){
			// random result
			boolean toReturn = Math.random() > 0.5;

			if(toReturn){

				boolean throwException = Math.random() > 0.5;
				if(throwException)
					throw new Exception("Unable to execute request for XYZ");

			}
			return toReturn;
		}

		HttpServletRequest threadRequest = getThreadLocalRequest();
		String context = Utils.getScopeFromClientUrl(threadRequest);
		String token = SecurityTokenProvider.instance.get();

		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();){

			String keyPerContext = CatalogueUtilMethods.concatenateSessionKeyScope(Constants.GRSF_UPDATER_SERVICE, context);
			String baseUrl = (String)getThreadLocalRequest().getSession().getAttribute(keyPerContext);
			if(baseUrl == null ||  baseUrl.isEmpty()){
				baseUrl = GRSFUpdaterServiceClient.discoverEndPoint(context);
				getThreadLocalRequest().getSession().setAttribute(keyPerContext, baseUrl);
			}

			if(baseUrl == null ||  baseUrl.isEmpty())
				throw new Exception("Unable to discover grsf-updater service!");

			Utils.revertOperation(httpClient, baseUrl, threadRequest, rInfo, token, context,
					PortalContext.getConfiguration().getCurrentGroupId(threadRequest));

		}
		catch(Exception e){
			logger.error("Unable to revert operation ", e);
			throw e;
		}
		return true;
	}

	/**
	 * Check if the current user is an editor
	 * @param username
	 * @param teamRoles
	 * @return true if he/she is an editor, false otherwise
	 */
	private boolean isEditor(String username, List<GCubeTeam> teamRolesByUser){

		for (GCubeTeam team : teamRolesByUser) {
			if(team.getTeamName().equals(Constants.GRSF_CATALOGUE_EDITOR_ROLE)){
				logger.info("User " + username + " is allowed to modify GRSF records as editor");
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the current user is a reviewer
	 * @param username
	 * @param teamRoles
	 * @return true if he/she is an reviewer, false otherwise
	 */
	private boolean isReviewer(String username, List<GCubeTeam> teamRolesByUser){

		for (GCubeTeam team : teamRolesByUser) {
			if(team.getTeamName().equals(Constants.GRSF_CATALOGUE_REVIEWER_ROLE)){
				logger.info("User " + username + " is allowed to modify GRSF records as reviewer");
				return true;
			}
		}
		return false;
	}

	@Override
	public String checkIdentifierExists(String id)
			throws Exception {
		String scopePerCurrentUrl = Utils.getScopeFromClientUrl(getThreadLocalRequest());
		DataCatalogue catalogue = getCatalogue(scopePerCurrentUrl);
		String username = Utils.getCurrentUser(getThreadLocalRequest()).getUsername();
		CkanDataset dataset = catalogue.getDataset(id, catalogue.getApiKeyFromUsername(username));
		if(dataset == null)
			throw new Exception("A GRSF record with id " + id  + " doesn't exist");

		if(!dataset.getOrganization().getName().equals(Constants.GRSF_ADMIN_ORGANIZATION_NAME))
			throw new Exception("The suggested record is not a GRSF record");

		return dataset.getExtrasAsHashMap().get(Constants.ITEM_URL_FIELD);
	}

	@Override
	public String checkIdentifierExistsInDomain(String id,
			String acceptedDomain) throws Exception {

		if(!Utils.isIntoPortal()){
			boolean throwException = Math.random() > 0.5;

			// simulate some delay...
			Thread.sleep(2500);

			if(throwException)
				throw new Exception("The suggested record is not a GRSF record");

			return "http://data.d4science.org/catalogue/grsf_admin/" + id;
		}

		String scopePerCurrentUrl = Utils.getScopeFromClientUrl(getThreadLocalRequest());
		DataCatalogue catalogue = getCatalogue(scopePerCurrentUrl);
		String username = Utils.getCurrentUser(getThreadLocalRequest()).getUsername();
		CkanDataset dataset = catalogue.getDataset(id, catalogue.getApiKeyFromUsername(username));

		if(dataset == null)
			throw new Exception("A record with id " + id  + " doesn't exist");

		Map<String, String> extras = dataset.getExtrasAsHashMap();
		String systemType = extras.get(Constants.SYSTEM_TYPE_CUSTOM_KEY);
		String domain = extras.get(Constants.DOMAIN_CUSTOM_KEY);
		String url = extras.get(Constants.ITEM_URL_FIELD);

		if(systemType.equals(Constants.SYSTEM_TYPE_FOR_SOURCES_VALUE))
			throw new Exception("This record is not a GRSF record!");

		if(!acceptedDomain.equalsIgnoreCase(domain))
			throw new Exception("You are suggesting a " + domain + " record instead of a " +  acceptedDomain + " record!");

		return url;
	}

}
