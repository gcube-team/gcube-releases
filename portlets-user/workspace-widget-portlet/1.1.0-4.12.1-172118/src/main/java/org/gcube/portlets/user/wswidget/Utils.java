package org.gcube.portlets.user.wswidget;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.wswidget.shared.ItemType;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.util.PortalUtil;

public class Utils {
	private static Log _log = LogFactoryUtil.getLog(Utils.class);
	private final static String DEFAULT_ROLE = "OrganizationMember";	
	public static final String WORKSPACE_MY_SPECIAL_FOLDERS_PATH = "/Workspace/MySpecialFolders";
	public static final String SPECIAL_FOLDERS_NAME = "MySpecialFolders";
	public static final String VRE_FOLDERS_LABEL = "My VRE Folders";
	public static final String HOME_LABEL = "Home";
	
	public static final ItemType[] FOLDERS = new ItemType[] {ItemType.PRIVATE_FOLDER, ItemType.SHARED_FOLDER, ItemType.VRE_FOLDER};

	/**
	 * Checks if is folder.
	 *
	 * @param type
	 *            the type
	 * @return true, if is folder
	 */
	public static boolean isFolder(ItemType type) {
		for (ItemType folder : FOLDERS)
			if (type == folder)
				return true;
		return false;
	}
	
	public static GCubeUser getCurrentUser(RenderRequest request) {
		long userId;
		try {
			userId = PortalUtil.getUser(request).getUserId();
			return getCurrentUser(userId);
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	public static GCubeUser getCurrentUser(HttpServletRequest request) {
		long userId;
		try {
			userId = PortalUtil.getUser(request).getUserId();
			return getCurrentUser(userId);
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	public static GCubeUser getCurrentUser(ResourceRequest request) {
		long userId;
		try {
			userId = PortalUtil.getUser(request).getUserId();
			return getCurrentUser(userId);
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return null;		
	}
	

	public static GCubeUser getCurrentUser(long userId) {
		try {
			return new LiferayUserManager().getUserById(userId);
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return null;		
	}

	public static String getCurrentContext(ResourceRequest request) {
		long groupId = -1;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
			return getCurrentContext(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurrentContext(RenderRequest request) {
		long groupId = -1;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
			return getCurrentContext(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getCurrentContext(HttpServletRequest request) {
		long groupId = -1;
		try {
			groupId = PortalUtil.getScopeGroupId(request);
			return getCurrentContext(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurrentContext(long groupId) {
		try {
			PortalContext pContext = PortalContext.getConfiguration(); 
			return pContext.getCurrentScope(""+groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * Returns the gCube authorisation token for the given user 
	 * </p>	
	 * @param scope infrastrucure context (scope)
	 * @param username the GCubeUser username @see {@link GCubeUser}
	 * @return the Token for the user in the context, or <code>null</code> if a token for this user could not be found
	 */
	public static String getCurrentUserToken(String scope, String username) {
		String userToken = null;

		try {
			ScopeProvider.instance.set(scope);
			userToken = authorizationService().resolveTokenByUserAndContext(username, scope);
			SecurityTokenProvider.instance.set(userToken);
		} 
		catch (ObjectNotFound ex) {
			userToken = generateAuthorizationToken(username, scope);
			SecurityTokenProvider.instance.set(userToken);
			_log.debug("generateAuthorizationToken OK for " + username + " in scope " + scope);
		}			 
		catch (Exception e) {
			_log.error("Error while trying to generate token for user " + username + "in scope " + scope);
			e.printStackTrace();
			return null;
		}

		return userToken;
	}
	
	/**
	 * 
	 * @param username
	 * @param scope
	 * @throws Exception
	 */
	private static String generateAuthorizationToken(String username, String scope) {
		List<String> userRoles = new ArrayList<>();
		userRoles.add(DEFAULT_ROLE);
		String token;
		try {
			token = authorizationService().generateUserToken(new UserInfo(username, userRoles), scope);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return token;
	}

}
