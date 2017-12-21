package org.gcube.datacatalogue.grsf_manage_widget.server.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.common.portal.PortalContext;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.UtilMethods;
import org.gcube.datacatalogue.common.Constants;
import org.gcube.datacatalogue.common.enums.Sources;
import org.gcube.datacatalogue.common.enums.Status;
import org.gcube.datacatalogue.grsf_manage_widget.client.GRSFManageWidgetService;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ManageProductBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.SimilarGRSFRecord;
import org.gcube.datacatalogue.grsf_manage_widget.shared.SourceRecord;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ex.NoGRSFRecordException;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GatewayRolesNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanPair;
import eu.trentorise.opendata.jackan.model.CkanResource;

/**
 * Endpoint for sending update records information to GRSF KnowledgeBase.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class GRSFNotificationService extends RemoteServiceServlet implements GRSFManageWidgetService{

	private static final long serialVersionUID = -4534905087994875893L;
	//private static final Log logger = LogFactoryUtil.getLog(GRSFNotificationService.class);
	private static final Logger logger = LoggerFactory.getLogger(GRSFNotificationService.class);
			
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
			logger.error("Unable to retrieve ckan utils. Error was " + e.toString());
			throw e;
		}
		return instance;
	}

	@Override
	public String notifyProductUpdate(ManageProductBean bean) {

		logger.info("Creating notification for the bean " + bean + " to send to the knowledge base");
		try{

			String context = Utils.getScopeFromClientUrl(getThreadLocalRequest());
			DataCatalogue catalogue = getCatalogue(context);

			// check if the base url of the service is in session
			String keyPerContext = UtilMethods.concatenateSessionKeyScope(Constants.GRSF_UPDATER_SERVICE, context);
			String baseUrl = (String)getThreadLocalRequest().getSession().getAttribute(keyPerContext);
			if(baseUrl == null ||  baseUrl.isEmpty()){
				baseUrl = Utils.discoverEndPoint(context);
				getThreadLocalRequest().getSession().setAttribute(keyPerContext, baseUrl);
			}
			return Utils.updateCatalogueRecord(baseUrl, bean, catalogue, Utils.getCurrentUser(getThreadLocalRequest()).getUsername());

		}catch(Exception e){
			logger.error("Unable to update the product.." + e.getMessage());
			return e.getMessage();
		}
	}

	@Override
	public ManageProductBean getProductBeanById(String productIdentifier) throws Exception {

		ManageProductBean toReturn = null;

		// retrieve scope per current portlet url
		String scopePerCurrentUrl = Utils.getScopeFromClientUrl(getThreadLocalRequest());
		DataCatalogue catalogue = getCatalogue(scopePerCurrentUrl);
		String username = Utils.getCurrentUser(getThreadLocalRequest()).getUsername();
		CkanDataset record = catalogue.getDataset(productIdentifier, catalogue.getApiKeyFromUsername(username));

		// it cannot be enabled in this case ...
		if(record == null)
			throw new Exception("Unable to retrieve information for the selected record, sorry");
		else{

			// check it is a grsf record (Source records have a different System Type)
			String systemType = record.getExtrasAsHashMap().get(Constants.SYSTEM_TYPE_CUSTOM_KEY);
			if(systemType == null || systemType.isEmpty() || systemType.equals(Constants.SYSTEM_TYPE_FOR_SOURCES_VALUE))
				throw new NoGRSFRecordException("This is not a GRSF Record");

			// get extras as hashmap and pairs
			List<CkanPair> extrasAsPairs = record.getExtras();

			// fetch map for namespaces
			Map<String, String> fieldsNamespacesMap = Utils.getFieldToFieldNameSpaceMapping(getThreadLocalRequest().getSession(),
					record.getExtrasAsHashMap().get(Constants.DOMAIN_CUSTOM_KEY).equals(Constants.STOCK_NAME_CUSTOM_KEY) ? Constants.GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_STOCK
							: Constants.GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_FISHERY);

			Map<String, List<String>> extrasWithoutNamespaces = Utils.replaceFieldsKey(extrasAsPairs, fieldsNamespacesMap);
			// get extras fields (wrt the mandatory ones) to show in the management panel TODO
			//			Utils.getExtrasToShow(); 
			String catalogueIdentifier = record.getId();
			String status = extrasWithoutNamespaces.get(Constants.STATUS_OF_THE_GRSF_RECORD_CUSTOM_KEY).get(0);
			String uuidKB = extrasWithoutNamespaces.get(Constants.UUID_KB_CUSTOM_KEY).get(0);
			String grsfDomain = extrasWithoutNamespaces.get(Constants.DOMAIN_CUSTOM_KEY).get(0);
			
			if(status == null || uuidKB == null)
				throw new Exception("Some information is missing in this record: Status = " + status + ", knowledge base uuid = " + uuidKB + 
						", and grsf domain is = " + grsfDomain);
			
			String semanticId = extrasWithoutNamespaces.get(Constants.GRSF_SEMANTIC_IDENTIFIER_CUSTOM_KEY).get(0);
			String shortName = extrasWithoutNamespaces.get(Constants.SHORT_NAME_CUSTOM_KEY).get(0);
			String grsfType = extrasWithoutNamespaces.get(Constants.GRSF_TYPE_CUSTOM_KEY).get(0);
			String grsfName = extrasWithoutNamespaces.get(grsfDomain.equals(Constants.STOCK_NAME_CUSTOM_KEY) ? 
					Constants.STOCK_NAME_CUSTOM_KEY : Constants.FISHERY_NAME_CUSTOM_KEY).get(0);
			boolean traceabilityFlag = extrasWithoutNamespaces.get(Constants.TRACEABILITY_FLAG_CUSTOM_KEY).get(0).equalsIgnoreCase("true");

			// Get similar GRSF records, if any (each of which should have name, description, url and id(i.e semantic identifier))
			List<String> similarGrsfRecordsAsStrings = 
					extrasWithoutNamespaces.containsKey(Constants.SIMILAR_GRSF_RECORDS_CUSTOM_KEY) ?
							extrasWithoutNamespaces.get(Constants.SIMILAR_GRSF_RECORDS_CUSTOM_KEY): null;

							List<SimilarGRSFRecord> similarRecords = new ArrayList<SimilarGRSFRecord>(0);
							if(similarGrsfRecordsAsStrings != null)
								for (String similarGRSFRecord : similarGrsfRecordsAsStrings) {
									similarRecords.add(Utils.similarGRSFRecordFromJson(similarGRSFRecord));
								}

							// Get sources
							List<CkanResource> resources = record.getResources();
							List<SourceRecord> sources = new ArrayList<SourceRecord>(3);
							for (CkanResource ckanResource : resources) {
								if(Sources.getListNames().contains(ckanResource.getName()))
									sources.add(new SourceRecord(ckanResource.getName(), ckanResource.getUrl()));
							}

							// set the values
							toReturn = new ManageProductBean(semanticId, catalogueIdentifier, uuidKB, grsfType, 
									grsfDomain, grsfName, shortName, traceabilityFlag, Status.fromString(status), null, 
									null, null, sources, similarRecords);

							logger.info("Returning item bean " + toReturn);

							return toReturn;
		}
	}

	@Override
	public boolean isAdminUser() {
		try{

			Boolean inSession = (Boolean)getThreadLocalRequest().getSession().getAttribute(Constants.GRSF_ADMIN_SESSION_KEY);
			
			if(inSession != null)
				return inSession;
			else{
				
				if(!Utils.isIntoPortal()){
					getThreadLocalRequest().getSession().setAttribute(Constants.GRSF_ADMIN_SESSION_KEY, true);
					return true;
				}
				
				PortalContext pContext = PortalContext.getConfiguration();
				RoleManager roleManager = new LiferayRoleManager();
				String username =  pContext.getCurrentUser(getThreadLocalRequest()).getUsername();
				long userId = pContext.getCurrentUser(getThreadLocalRequest()).getUserId();
				long groupId = pContext.getCurrentGroupId(getThreadLocalRequest());
				List<GCubeRole> vreRoles = roleManager.listRolesByUserAndGroup(userId, groupId);
				List<GCubeTeam> teamRoles = new LiferayRoleManager().listTeamsByUserAndGroup(userId, groupId);
				boolean toSetInSession = false;
				for (GCubeTeam team : teamRoles) {
					if(team.getTeamName().equals(Constants.GRSF_CATALOGUE_MANAGER_ROLE)){
						logger.info("User " + username + " is " + Constants.GRSF_CATALOGUE_MANAGER_ROLE);
						toSetInSession = true;
						break;
					}
				}

				if(!toSetInSession)
					for (GCubeRole gCubeTeam : vreRoles) {
						if(gCubeTeam.getRoleName().equals(GatewayRolesNames.VRE_MANAGER.getRoleName())){
							logger.info("User " + username + " is " + GatewayRolesNames.VRE_MANAGER.getRoleName());
							toSetInSession = true;
							break;
						}
					}

				getThreadLocalRequest().getSession().setAttribute(Constants.GRSF_ADMIN_SESSION_KEY, toSetInSession);
				return toSetInSession;
			}
		}catch(Exception e){
			logger.error("Failed to check if the user has team " + Constants.GRSF_CATALOGUE_MANAGER_ROLE 
					+ " or " + GatewayRolesNames.VRE_MANAGER.getRoleName() +"!", e);
		}
		return false;
	}

}
