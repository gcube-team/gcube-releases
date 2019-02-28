package org.gcube.portlets.widgets.wsthreddssync.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.common.portal.PortalContext;
import org.gcube.portal.wssynclibrary.shared.ItemNotSynched;
import org.gcube.portal.wssynclibrary.shared.WorkspaceFolderLocked;
import org.gcube.portal.wssynclibrary.shared.thredds.Sync_Status;
import org.gcube.portal.wssynclibrary.shared.thredds.ThCatalogueBean;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncFolderDescriptor;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncStatus;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSynchFolderConfiguration;
import org.gcube.portlets.widgets.wsthreddssync.client.rpc.ThreddsWorkspaceSyncService;
import org.gcube.portlets.widgets.wsthreddssync.shared.GcubeScope;
import org.gcube.portlets.widgets.wsthreddssync.shared.GcubeScopeType;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderConfiguration;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderDescriptor;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

// TODO: Auto-generated Javadoc
/**
 * The server side implementation of the RPC service.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 14, 2018
 */
@SuppressWarnings("serial")
public class ThreddsWorkspaceSyncServiceImpl extends RemoteServiceServlet implements ThreddsWorkspaceSyncService {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ThreddsWorkspaceSyncServiceImpl.class);

	/** The sync thredds. */
	private SyncronizeWithThredds syncThredds = null;

	/**
	 * Gets the sync service.
	 *
	 * @return the sync service
	 */
	public synchronized SyncronizeWithThredds getSyncService() {

		if(syncThredds==null)
			syncThredds = new SyncronizeWithThredds();

	  return syncThredds;

	}

	/**
	 * Checks if is within portal.
	 *
	 * @return true if you're running into the portal, false if in development
	 */
	public static boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		}
		catch (Exception ex) {
			logger.trace("Development Mode ON");
			return false;
		}
	}



	/**
	 * Do sync folder.
	 *
	 * @param folderId the folder id
	 * @param clientConfig the th config
	 * @return the th sync status
	 * @throws Exception the exception
	 */
	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsthreddssync.client.rpc.ThreddsWorkspaceSyncService#doSyncFolder(java.lang.String, org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderDescriptor)
	 */
	@Override
	public ThSyncStatus doSyncFolder(final String folderId, WsThreddsSynchFolderConfiguration clientConfig) throws Exception{
		logger.info("Performing doSyncFolder method on id: "+folderId +", config: "+clientConfig);

		try {
			GCubeUser user = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
			String targetScopeUserToken = null;
			ThSynchFolderConfiguration config = null;
			if(clientConfig!=null) {
				targetScopeUserToken = PortalContext.getConfiguration().getCurrentUserToken(clientConfig.getSelectedScope().getScopeName(), user.getUsername());
				config = BeanConverter.toThSynchFolderConfiguration(clientConfig, folderId, targetScopeUserToken);
				logger.debug("Creating server config "+config);
			}

			logger.info("Calling doSyncFolder on folderId: "+folderId +", config: "+config);
			String wsScope = PortalContext.getConfiguration().getCurrentScope(this.getThreadLocalRequest());
			String wsUserToken = PortalContext.getConfiguration().getCurrentUserToken(wsScope, user.getUsername());

			ThSyncStatus status = getSyncService().doSyncFolder(folderId, config, wsScope, wsUserToken);
			logger.debug("Returning for folderId "+folderId+" the syncStatus: "+status);

			return status;
		}catch (Exception e) {
			logger.error("Do sync Folder error: ",e);
			throw new Exception("Sorry, an error occurred during synchonization phase, try again later");
		}
	}


	/**
	 * Gets the available catalogues for scope.
	 *
	 * @param scope the scope
	 * @return the available catalogues for scope
	 * @throws Exception the exception
	 */
	@Override
	public List<ThCatalogueBean> getAvailableCataloguesForScope(String scope) throws Exception {

		if(scope==null)
			throw new Exception("Invalid scope null");

		GCubeUser user = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
		String wsScope = PortalContext.getConfiguration().getCurrentScope(this.getThreadLocalRequest());
		String wsUserToken = PortalContext.getConfiguration().getCurrentUserToken(wsScope, user.getUsername());
		String targetScopeUserToken = PortalContext.getConfiguration().getCurrentUserToken(scope, user.getUsername());
		List<ThCatalogueBean> listCtlgs = getSyncService().getAvailableCataloguesByToken(scope, wsUserToken, targetScopeUserToken);

		logger.debug("Retuning "+listCtlgs.size()+" Catalogues for scope: "+scope);
		if(logger.isDebugEnabled()){
			for (ThCatalogueBean thCatalogueBean : listCtlgs) {
				logger.debug(thCatalogueBean.toString());
			}
		}

		return listCtlgs;
	}


	/**
	 * Gets the list of VR es for logged user.
	 *
	 * @return the list of VR es for logged user
	 * @throws Exception the exception
	 */
	@Override
	public List<GcubeScope> getListOfScopesForLoggedUser() throws Exception{
		logger.debug("getListOfVREsForLoggedUser...: ");
		GCubeUser user = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
		long userId = user.getUserId();

		// Instanciate the manager
		GroupManager groupManager = new LiferayGroupManager();
		List<GcubeScope> listOfScopes = new ArrayList<GcubeScope>();

		if (!isWithinPortal()){
			listOfScopes.add(new GcubeScope("devVRE", "/gcube/devsec/devVRE", GcubeScopeType.VRE));
			listOfScopes.add(new GcubeScope("NextNext", "/gcube/devNext/NextNext", GcubeScopeType.VRE));
			listOfScopes.add(new GcubeScope("devNext", "/gcube/devNext", GcubeScopeType.VO));
			listOfScopes.add(new GcubeScope("devsec", "/gcube/devsec", GcubeScopeType.VO));
			listOfScopes.add(new GcubeScope("gcube", "/gcube", GcubeScopeType.ROOT));
			Collections.sort(listOfScopes);
			return listOfScopes;
		}

		try {

			List<GCubeGroup> listOfGroups = groupManager.listGroupsByUser(userId);
			for (GCubeGroup gCubeGroup : listOfGroups) {
				GcubeScopeType scopeType=null;
				if(groupManager.isVRE(gCubeGroup.getGroupId())){
					scopeType =  GcubeScopeType.VRE;
				}else if(groupManager.isVO(gCubeGroup.getGroupId())){
					scopeType =  GcubeScopeType.VO;
				}
//				}else if(groupManager.isRootVO(gCubeGroup.getGroupId())){
//					scopeType =  GcubeScopeType.ROOT;
//				}

				if(scopeType!=null){
					GcubeScope gcubeVRE = new GcubeScope(gCubeGroup.getGroupName(), groupManager.getInfrastructureScope(gCubeGroup.getGroupId()), scopeType);
					listOfScopes.add(gcubeVRE);
				}

			}

			//ADDING THE ROOT SCOPE
			String infraName = PortalContext.getConfiguration().getInfrastructureName();
			GcubeScope gcubeRoot = new GcubeScope(infraName, "/"+infraName, GcubeScopeType.ROOT);
			listOfScopes.add(gcubeRoot);


		}
		catch (UserRetrievalFault | UserManagementSystemException
						| GroupRetrievalFault e) {
			logger.error("Error occurred server-side getting VRE folders: ", e);
			throw new Exception("Sorry, an error occurred server-side getting VRE folders, try again later");
		}

		Collections.sort(listOfScopes);
		logger.info("Returning list of VREs: "+listOfScopes);
		return listOfScopes;
	}




	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsthreddssync.client.rpc.ThreddsWorkspaceSyncService#isItemSynched(java.lang.String)
	 */
	@Override
	public WsThreddsSynchFolderDescriptor isItemSynched(String folderId) throws WorkspaceFolderLocked, Exception{

		logger.debug("Performing isItemSynched for foldeId: "+folderId);

		try {

			GCubeUser user = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
			String wsScope = PortalContext.getConfiguration().getCurrentScope(this.getThreadLocalRequest());
			Sync_Status theStatus = getSyncService().getSynchedStatusFromItemProperty(folderId, wsScope, user.getUsername());

			if(theStatus!=null) {
				logger.info("Folder id: "+folderId+" is synched");
				String wsUserToken = PortalContext.getConfiguration().getCurrentUserToken(wsScope, user.getUsername());
				ThSyncFolderDescriptor serverFolderSync = getSyncService().checkItemSynched(folderId,wsScope,wsUserToken);
				WsThreddsSynchFolderDescriptor toWsThreddFolder = BeanConverter.toWsThreddsFolderConfig(serverFolderSync, theStatus);
				logger.debug("IsItemSynched for id: "+folderId +" returning: "+toWsThreddFolder);
				return toWsThreddFolder;
			}

			logger.info("Folder id: "+folderId+" is not synched, returning null descriptor");
			return null;

		} catch (ItemNotSynched e) {
			logger.info("The folderId: "+folderId +" is not synched, returning null FolderDescriptor");
			return null;

		} catch (WorkspaceFolderLocked e) {
			logger.warn(e.getMessage() +", sending exception to client...");
			throw new WorkspaceFolderLocked(e.getFolderId(), e.getMessage());

		}catch (Exception e) {
			logger.info("Error on isItemSynched for folderId: "+folderId, e);
			throw new Exception(e);
		}
	}


	/**
	 * Register callback for id.
	 *
	 * @param folderId the folder id
	 * @throws Exception the exception
	 */
	public void registerCallbackForId(String folderId) throws Exception{
		GCubeUser user = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
		String wsScope = PortalContext.getConfiguration().getCurrentScope(this.getThreadLocalRequest());
		String wsUserToken = PortalContext.getConfiguration().getCurrentUserToken(wsScope, user.getUsername());
		getSyncService().registerCallbackForId(folderId, wsScope, wsUserToken);
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsthreddssync.client.rpc.ThreddsWorkspaceSyncService#monitorSyncStatus(java.lang.String)
	 */
	@Override
	public ThSyncStatus monitorSyncStatus(String folderId) throws ItemNotSynched, Exception{
		GCubeUser user = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
		String wsScope = PortalContext.getConfiguration().getCurrentScope(this.getThreadLocalRequest());
		String wsUserToken = PortalContext.getConfiguration().getCurrentUserToken(wsScope, user.getUsername());
		return getSyncService().monitorSyncStatus(folderId, wsScope, wsUserToken);
	}


	/**
	 * Do sync folder.
	 *
	 * @param folderId the folder id
	 * @param clientConfig the th config
	 * @return the th sync status
	 * @throws Exception the exception
	 */
	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.wsthreddssync.client.rpc.ThreddsWorkspaceSyncService#doSyncFolder(java.lang.String, org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderDescriptor)
	 */
	@Override
	public Boolean doUnSyncFolder(final String folderId) throws Exception{
		logger.info("Performing unsync on folder id: "+folderId);

//		String scope = PortalContext.getConfiguration().getCurrentScope(this.getThreadLocalRequest());
//		GCubeUser username = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
//		String groupName = PortalContext.getConfiguration().getCurrentGroupName(this.getThreadLocalRequest());
//		new GcubeVRE(groupName, scope)
		try {
			GCubeUser user = PortalContext.getConfiguration().getCurrentUser(this.getThreadLocalRequest());
			String wsScope = PortalContext.getConfiguration().getCurrentScope(this.getThreadLocalRequest());
			String wsUserToken = PortalContext.getConfiguration().getCurrentUserToken(wsScope, user.getUsername());
			return getSyncService().doUnSync(folderId, false, wsScope, wsUserToken);
		}catch (Exception e) {
			logger.error("Do un sync Folder error: ",e);
			throw new Exception("Sorry, an error occurred on deleting sync configurations, refresh and try again later");
		}
	}

}
