package org.gcube.portlets.user.gcubeloggedin.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.gcubeloggedin.client.LoggedinService;
import org.gcube.portlets.user.gcubeloggedin.shared.VObject;
import org.gcube.portlets.user.gcubeloggedin.shared.VObject.UserBelongingClient;
import org.gcube.portlets.user.gcubeloggedin.shared.VREClient;
import org.gcube.portlets.user.gcubewidgets.server.ScopeServiceImpl;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LoggedinServiceImpl extends RemoteServiceServlet implements LoggedinService {

	private static final Logger _log = LoggerFactory.getLogger(LoggedinServiceImpl.class);
	private static final String VRE_MANAGER_ROLE = "VRE-Manager";

	/**
	 * the current ASLSession
	 * @return .
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.warn("LOGGEDIN PORTLET: USER IS NULL \n\n SESSION ID READ: " +sessionID );
			user = "test.user";
			_log.warn("session ID= *" + sessionID + "*  user= *" + user + "*" );		

		}

		return SessionManager.getInstance().getASLSession(sessionID, user);

	}

	/**
	 * return the current selected VRE
	 */

	public VObject getSelectedRE(String portalURL) {
		ASLSession aslSession = getASLSession();
		String friendlyURL = ScopeServiceImpl.extractOrgFriendlyURL(portalURL);

		if (friendlyURL == null) {//the URL is not a portal URL, we are in devmode.
			return new VREClient("Test VRE Name", "", "" +
					"Fishery and Aquaculture Resources Management (FARM) Virtual Organisation</b>    The FARM Virtual Organisation is the <b><i>dynamic group of individuals</i></b> and/or <b><i>institutions</i></b>             defined around a set of <b><i>sharing rules</i></b> in which <b><i>resource providers</i></b> and <b><i>consumers</i></b>     specify clearly and carefully just what is shared, who is allowed to share, and the conditions under which sharing occurs to serve the needs of the     <b><i>Fisheries and Aquaculture Resources Management</i></b>.            "
					+ " This VO is conceived to support various application scenarios arising in the FARM Community including the production of Fisheries and Aquaculture Country Profiles, "
					+ "the management of catch statistics    including harmonisation, the dynamic generation of biodiversity maps and species distribution maps."
					+ "            This Virtual Organisation currently consists of:<ul>                <li> approximately <b><i>13 gCube nodes</i></b>, "
					+ "i.e. machines dedicated to run the gCube system;</li>   "
					+ "     <li> approximately <b><i>89 running instances</i></b>, i.e. running gCube services supporting the operation of the infrastructure;</li>  "
					+ "      <li> approximately <b><i>25 collections</i></b>, i.e. set of D4Science Information Objects including Earth images, AquaMaps, Graphs on catch statistics;</li>     "
					+ "   <li> approximately <b><i>66 metadata collections</i></b>, i.e. set of Metadata Objects describing the Information Objects through various features and schemas;</li> "
					+ "       <li> approximately <b><i>58 other resources</i></b> including transformation programs, index types, etc.</li></ul></div>"
					, "http://placehold.it/300x200", "", UserBelongingClient.BELONGING, false, true, true);
		}
		_log.trace("getting Selected Research Environment");
		GroupManager gm = new LiferayGroupManager();
		GCubeGroup currSite = null;
		try {			
			List<GCubeGroup> groups = gm.listGroups();
			for (GCubeGroup g : groups) {
				if (g.getFriendlyURL().compareTo(friendlyURL) == 0) {
					long groupId = g.getGroupId();		
					String scopeToSet = gm.getInfrastructureScope(groupId);
					getASLSession().setScope(scopeToSet);
					_log.info("GOT Selected Research Environment: " + scopeToSet);
					currSite = g;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/**
		 * set the current ORG bean in session
		 */
		aslSession.setAttribute(ScopeHelper.CURR_ORG, currSite);

		aslSession.setGroupModelInfos(currSite.getGroupName(), currSite.getGroupId());

		_log.trace("CURRENT ORG SET IN SESSION: " + currSite.getGroupName());

		long nowInMillis = new Date().getTime();
		String name = currSite.getGroupName();
		String logoURL =  "/image/layout_set_logo?img_id="+currSite.getLogoId()+"&t="+nowInMillis;
		String desc = "";
		//set the description for the vre
		if (currSite.getDescription() != null)	
			desc = currSite.getDescription();
		return new VREClient(name, "", desc, logoURL, "", UserBelongingClient.BELONGING, false, false, isCurrUserVREManager());
	}

	/**
	 * 
	 * @return
	 */
	private boolean isCurrUserVREManager() {
		ASLSession session = getASLSession();
		long userId;
		try {
			userId = new LiferayUserManager().getUserId(session.getUsername());
			long groupId = new LiferayGroupManager().getGroupIdFromInfrastructureScope(session.getScope());
			RoleManager rm = new LiferayRoleManager();
			long roleId = rm.getRoleIdByName(VRE_MANAGER_ROLE);
			boolean toReturn = rm.hasRole(userId, groupId, roleId);
			_log.debug("User " + session.getUsername() + " is " + VRE_MANAGER_ROLE + " for " + session.getScope() + "? -> " + toReturn);
			return toReturn;

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
	}

	protected static ArrayList<String> getAdministratorsEmails(String scope) {
		LiferayUserManager userManager = new LiferayUserManager();
		LiferayGroupManager groupManager = new LiferayGroupManager();
		long groupId = -1;
		try {
			List<GCubeGroup> allGroups = groupManager.listGroups();
			_log.debug("Number of groups retrieved: " + allGroups.size());
			for (int i = 0; i < allGroups.size(); i++) {
				long grId = allGroups.get(i).getGroupId();
				String groupScope = groupManager.getInfrastructureScope(grId);
				_log.debug("Comparing: " + groupScope + " " + scope);
				if (groupScope.equals(scope)) {
					groupId = allGroups.get(i).getGroupId();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Map<GCubeUser, List<GCubeRole>> usersAndRoles = null;
		try {
			usersAndRoles = userManager.listUsersAndRolesByGroup(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Set<GCubeUser> users = usersAndRoles.keySet();
		ArrayList<String> adminEmailsList = new ArrayList<String>();
		for (GCubeUser usr:users) {
			List<GCubeRole> roles = usersAndRoles.get(usr);
			for (int i = 0; i < roles.size(); i++) {
				if (roles.get(i).getRoleName().equals("VO-Admin") || roles.get(i).getRoleName().equals("VRE-Manager")) {
					adminEmailsList.add(usr.getEmail());
					_log.debug("Admin: " + usr.getFullname());
					break;
				}
			}
		}
		return adminEmailsList;
	}

	@Override
	public String saveVREDescription(String toSave) {
		try {
			String scope = getASLSession().getScope();
			GroupManager gm = new LiferayGroupManager();
			long groupId = gm.getGroupIdFromInfrastructureScope(scope);
			return gm.updateGroupDescription(groupId, toSave);
		} catch (Exception e) {		
			e.printStackTrace();
		}
		return null; 
	}


}
