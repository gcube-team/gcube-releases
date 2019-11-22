package org.gcube.portlets.widgets.wsthreddssync.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.portal.wssynclibrary.shared.thredds.Sync_Status;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSyncFolderDescriptor;
import org.gcube.portal.wssynclibrary.shared.thredds.ThSynchFolderConfiguration;
import org.gcube.portlets.widgets.wsthreddssync.shared.GcubeScope;
import org.gcube.portlets.widgets.wsthreddssync.shared.GcubeScopeType;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderConfiguration;
import org.gcube.portlets.widgets.wsthreddssync.shared.WsThreddsSynchFolderDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class BeanConverter.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Aug 2, 2019
 */
public class BeanConverter {

	private static Logger logger = LoggerFactory.getLogger(BeanConverter.class);


	/**
	 * To ws thredds folder config.
	 *
	 * @param t the t
	 * @param theStatus the the status
	 * @return the ws thredds synch folder descriptor
	 */
	public static WsThreddsSynchFolderDescriptor toWsThreddsFolderConfig(ThSyncFolderDescriptor t,
			Sync_Status theStatus) {

		if (t == null)
			return null;

		WsThreddsSynchFolderDescriptor ws = new WsThreddsSynchFolderDescriptor();
		ws.setServerFolderDescriptor(t);
		ws.setSyncStatus(theStatus);

		// FROM TARGET TOKEN TO SCOPE
		// t.getConfiguration().getTargetToken()

		if (t.getConfiguration().getTargetToken() != null) {
			try {
				AuthorizationEntry entry = authorizationService().get(t.getConfiguration().getTargetToken());
				String scope = entry.getContext();
				GcubeScope selectedScope = new GcubeScope(toScopeTitle(scope), scope, toGcubeScope(scope));
				ws.setSelectedScope(selectedScope);
				logger.debug("Resolved SCOPE: " + selectedScope + " from token");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		return ws;

	}

	/**
	 * To scope title.
	 *
	 * @param scope the scope
	 * @return the string
	 */
	public static String toScopeTitle(String scope) {

		if (scope == null || scope.isEmpty())
			return null;

		return scope.substring(scope.lastIndexOf("/") + 1, scope.length());

	}

	/**
	 * To gcube scope.
	 *
	 * @param scopeName the scope name
	 * @return the gcube scope type
	 */
	public static GcubeScopeType toGcubeScope(String scopeName) {

		if (scopeName == null)
			return null;
		
		String[] components=scopeName.split("/");
		
		if (components.length<2 || components.length>4) 
			throw new IllegalArgumentException("scope "+scopeName+" is malformed");
		
		if(components.length>3) {
			return GcubeScopeType.VRE;
		}
		else if (components.length>2) {
			return GcubeScopeType.VO;
		}
		else {
			return GcubeScopeType.ROOT;
		}
	}

	/**
	 * To th synch folder configuration.
	 *
	 * @param t                    the t
	 * @param rootFolderId         the root folder id
	 * @param targetScopeUserToken the target scope user token
	 * @return the th synch folder configuration
	 */
	public static ThSynchFolderConfiguration toThSynchFolderConfiguration(WsThreddsSynchFolderConfiguration t,
			String rootFolderId, String targetScopeUserToken) {

		if (t == null)
			return null;

		ThSynchFolderConfiguration ts = new ThSynchFolderConfiguration();
		ts.setFilter(t.getFilter());
		ts.setRemotePath(t.getRemotePath());
		ts.setTargetToken(targetScopeUserToken);
		ts.setToCreateCatalogName(t.getCatalogName());
		ts.setRootFolderId(rootFolderId);
		return ts;

	};

}
