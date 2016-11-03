package org.gcube.portlets.user.gcubeloggedin.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.portal.mailing.EmailNotification;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.gcubeloggedin.client.LoggedinService;
import org.gcube.portlets.user.gcubeloggedin.shared.VObject;
import org.gcube.portlets.user.gcubeloggedin.shared.VObject.UserBelongingClient;
import org.gcube.portlets.user.gcubeloggedin.shared.VREClient;
import org.gcube.portlets.user.gcubewidgets.server.ScopeServiceImpl;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

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

		
		String name = currSite.getGroupName();
		String logoURL =  gm.getGroupLogoURL(currSite.getLogoId());
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
	@Override
	public boolean isLeaveButtonAvailable(String portalURL) {

		if(isWithinPortal()){
			String friendlyURL = ScopeServiceImpl.extractOrgFriendlyURL(portalURL);
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
			Boolean isMandatory = false;
			try{
				isMandatory = (Boolean) gm.readCustomAttr(currSite.getGroupId(), org.gcube.vomanagement.usermanagement.model.CustomAttributeKeys.MANDATORY.getKeyName());
			}catch(Exception e){
				_log.error("Unable to evaluate if the leave button can be added for the current group " + currSite.getGroupName(), e);
			}
			_log.debug("Is Leave button available in vre " + currSite.getGroupName() + " ? " + isMandatory);
			return !isMandatory;
		}else return true;
	}
	/**
	 *@return the redirect url if everything goes ok, null otherwise
	 */
	@Override
	public String removeUserFromVRE() {
		String username = getASLSession().getUsername();
		if (username.compareTo("test.user") == 0)
			return null;
		_log.debug("Going to remove user from the current Group: " + getCurrentGroupID() + ". Username is: " + username);
		UserManager userM = new LiferayUserManager();
		try {
			userM.dismissUserFromGroup(getCurrentGroupID(), userM.getUserId(username));
			sendUserUnregisteredNotification(username, getASLSession().getScope(), 
					PortalContext.getConfiguration().getGatewayURL(getThreadLocalRequest()),
					PortalContext.getConfiguration().getGatewayName(getThreadLocalRequest()));
			
			return PortalContext.getConfiguration().getSiteLandingPagePath(getThreadLocalRequest());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}
	/**
	 * Get the current group ID
	 * 
	 * @return the current group ID or null if an exception is thrown
	 * @throws Exception 
	 * @throws CurrentGroupRetrievalException 
	 */
	private long getCurrentGroupID(){
		GroupManager groupM = new LiferayGroupManager();
		ASLSession session = getASLSession();
		_log.debug("The current group NAME is --> " + session.getGroupName());	
		try {
			return groupM.getGroupId(session.getGroupName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			_log.trace("Development Mode ON");
			return false;
		}			
	}
	/**
	 * 
	 * @param scope .
	 * @param optionalMessage .
	 */
	public void sendUserUnregisteredNotification(String username, String scope, String portalbasicurl, String gatewayName) {
		ArrayList<String> adminEmails = getAdministratorsEmails(scope);
		UserManager um = new LiferayUserManager();
		GCubeUser currUser = null;
		try {
			currUser = um.getUserByUsername(username);
		} catch (Exception e) {

		}
		String name = currUser.getFirstName();
		String lastname = currUser.getLastName();

		StringBuffer body = new StringBuffer();
		body.append("<p>Dear manager of "+ scope +",<br />this email message was automatically generated by " + portalbasicurl +" to inform you that ");
		body.append("</p>");
		body.append("<p>");
		body.append("<b>"+name + " " + lastname +"</b> has left the following environment: ");
		body.append("<br /><br />");
		body.append("<b>" + scope+"</b>");
		body.append("<br />");
		body.append("<br />");
		body.append("<b>Username: </b>" + username);
		body.append("<br />");
		body.append("<b>e-mail: </b>" + currUser.getEmail());
		body.append("</p>");

		String[] allMails = new String[adminEmails.size()];

		adminEmails.toArray(allMails);

		EmailNotification mailToAdmin = new EmailNotification(allMails , "Unregistration from VRE", body.toString(), getThreadLocalRequest());

		mailToAdmin.sendEmail();
	}
}
