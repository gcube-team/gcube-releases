package org.gcube.portlets.user.vremembers.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portlets.user.vremembers.client.MembersService;
import org.gcube.portlets.user.vremembers.shared.BelongingUser;
import org.gcube.portlets.user.vremembers.shared.VREGroup;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.TeamRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * @author Massimiliano Assante, ISTI-CNR
 */
@SuppressWarnings("serial")
public class MembersServiceImpl extends RemoteServiceServlet implements MembersService {
	private static final Logger _log = LoggerFactory.getLogger(MembersServiceImpl.class);

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
	@Override
	public VREGroup getVREGroupUsers(String groupId) {
		_log.info("Asking for members of team with id = " + groupId );
		ArrayList<BelongingUser> groupUsers = new ArrayList<BelongingUser>();
		long teamId = -1;
		try{
			teamId = Long.parseLong(groupId);
		} catch (NumberFormatException e) {
			_log.error("The groupId is not a valid long: " + groupId);
			return null;
		}	
		GCubeTeam team2Return = null;
		GCubeGroup parent = null;
		if (isWithinPortal()) {			
			List<GCubeUser> users = null;
			try {
				team2Return = new LiferayRoleManager().getTeam(teamId);
				parent = new LiferayGroupManager().getGroup( team2Return.getGroupId());
				users = new LiferayUserManager().listUsersByTeam(teamId);
			} catch (UserManagementSystemException | TeamRetrievalFault | UserRetrievalFault | GroupRetrievalFault e) {
				e.printStackTrace();
			}
			for (GCubeUser user : users) {
				if (user.getUsername().compareTo("test.user") != 0)  { //skip test.user
					groupUsers.add(
							new BelongingUser(
									user.getUsername(), 
									user.getFullname(), 
									user.getUserAvatarURL(), 
									user.getJobTitle(), 
									user.getLocation_industry(), getUserProfileLink(user.getUsername() ), true));
				}
			}
		} else { //development mode
			_log.info("Returning test team members with id = " + groupId );
			return new VREGroup(teamId, "TestTeam Name", "parent VRE", "Test Team Description", getTestUsers());
		}
		return new VREGroup(teamId, team2Return.getTeamName(), parent.getGroupName(), team2Return.getDescription(), groupUsers);
	}

	@Override
	public VREGroup getVREManagers() {
		ArrayList<BelongingUser> vreManagers = new ArrayList<BelongingUser>();
		long currGroupId = PortalContext.getConfiguration().getCurrentGroupId(getThreadLocalRequest());
		try {
			long roleId = new LiferayRoleManager().getRole(GCubeRole.VRE_MANAGER_LABEL, currGroupId).getRoleId();
			for (GCubeUser user : new LiferayUserManager().listUsersByGroupAndRole(currGroupId, roleId)) {
				if (user.getUsername().compareTo("test.user") != 0)  { //skip test.user
					vreManagers.add(
							new BelongingUser(
									user.getUsername(), 
									user.getFullname(), 
									user.getUserAvatarURL(), 
									user.getJobTitle(), 
									user.getLocation_industry(), getUserProfileLink(user.getUsername() ), true));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new VREGroup(-100,"", "", "", vreManagers);
	}

	/**
	 * 
	 * @param session the Asl Session
	 * @param withinPortal true when is on Liferay portal
	 * @return the users belonging to the current Site (VO/VRE) (scope)
	 */
	@Override
	public ArrayList<BelongingUser> getSiteUsers() {
		ArrayList<BelongingUser> portalUsers = new ArrayList<BelongingUser>();
		String scope = PortalContext.getConfiguration().getCurrentScope(getThreadLocalRequest());
		if (scope == null)
			return portalUsers;
		try {
			if (isWithinPortal()) {
				UserManager um = new LiferayUserManager();
				GroupManager gm = new LiferayGroupManager();
				
				List<GCubeUser> users = null;

				if (isInfrastructureScope()) {
					users = new ArrayList<GCubeUser>();
					return new ArrayList<BelongingUser>();
				}
				else { //must be in VRE
					//get the name from the scope
					String orgName = scope.substring(scope.lastIndexOf("/")+1, scope.length());
					//ask the users
					users = um.listUsersByGroup(gm.getGroupId(orgName));
				}
				
				for (GCubeUser user : users) {
					if (user.getUsername().compareTo("test.user") != 0)  { //skip test.user
						portalUsers.add(
								new BelongingUser(
										user.getUsername(), 
										user.getFullname(), 
										user.getUserAvatarURL(), 
										user.getJobTitle(), 
										user.getLocation_industry(), getUserProfileLink(user.getUsername() ), true));
					}
				}

			}
			else { //test users
				return getTestUsers();
			}
		} catch (Exception e) {
			_log.error("Error in server get all contacts ", e);
		}
		//users having photo go first
		Collections.sort(portalUsers);
		return portalUsers;
	}





	private String getUserProfileLink(String username) {
		return "profile?"+ new String(Base64.encodeBase64(GCubeSocialNetworking.USER_PROFILE_OID.getBytes()))+"="+new String(Base64.encodeBase64(username.getBytes()));
	}

	private  ArrayList<BelongingUser> getTestUsers() {
		ArrayList<BelongingUser> portalUsers = new ArrayList<BelongingUser>();
		portalUsers.add(new BelongingUser("massimiliano.assante", "Test User #1", "http://placehold.it/200x200", "headline", "isti", "",false));
		portalUsers.add(new BelongingUser("pino.assante", "Test Second User #2", "http://placehold.it/200x200", "headline1", "istitution complex", "",false));
		portalUsers.add(new BelongingUser("pino.pino", "With Photo Third User", "http://placehold.it/200x200", "hard worker", "acme Ltd",  "",true));
		portalUsers.add(new BelongingUser("giorgi.giorgi", "Test Fourth User", "http://placehold.it/200x200", "hard worker 3", "isti3",  "",false));
		portalUsers.add(new BelongingUser("pinetti.giorgi", "Test Fifth User", "http://placehold.it/200x200", "hard worker 4", "super acme Inc.",  "",false));
		portalUsers.add(new BelongingUser("massimiliano.pinetti", "Test Sixth User", "http://placehold.it/200x200", "hard worker the5th", "istiw", "", false));
		portalUsers.add(new BelongingUser("giorgi.assante", "Ninth Testing User", "http://placehold.it/200x200", "hard worker the9th", "istiw9",  "",false));
		portalUsers.add(new BelongingUser("massimiliano.giorgi", "Eighth Testing User", "http://placehold.it/200x200", "hard worker the8th", "istiw56", "", false));
		portalUsers.add(new BelongingUser("giogio.giorgi", "Seventh Test User", "http://placehold.it/200x200", "hard worker the7th", "istiw7", "", false));
		portalUsers.add(new BelongingUser("pino.pinetti", "Tenth Testing User Photoed", "http://placehold.it/200x200", "hard worker the10th", "istiw777",  "",true));
		return portalUsers;
	}
	/**
	 * Indicates whether the scope is the whole infrastructure.
	 * @return <code>true</code> if it is, <code>false</code> otherwise.
	 */
	private boolean isInfrastructureScope() {
		long currGroupId = PortalContext.getConfiguration().getCurrentGroupId(getThreadLocalRequest());
		try {
			return new LiferayGroupManager().isRootVO(currGroupId);
		} catch (UserManagementSystemException | GroupRetrievalFault e) {
			e.printStackTrace();
		}
		return false;
	}


}
