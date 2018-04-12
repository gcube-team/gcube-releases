package org.gcube.portlets.user.questions.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portlets.user.questions.client.QuestionsService;
import org.gcube.portlets.user.questions.shared.GroupDTO;
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
 * 
 * @author massi
 *
 */
@SuppressWarnings("serial")
public class QuestionsServiceImpl extends RemoteServiceServlet implements QuestionsService {
	private static final Logger _log = LoggerFactory.getLogger(QuestionsServiceImpl.class);

	@Override
	public ArrayList<GroupDTO> getGroups() {		
		ArrayList<GroupDTO> toReturn = new ArrayList<>();	
		
		if (isWithinPortal()) {
			try {
			
				long vreGroupId = PortalContext.getConfiguration().getCurrentGroupId(getThreadLocalRequest());
				String vreFriendlyURL = new LiferayGroupManager().getGroup(vreGroupId).getFriendlyURL();
				StringBuffer pageToRedirectURL= new StringBuffer(GCubePortalConstants.PREFIX_GROUP_URL)
						.append(vreFriendlyURL)
						.append(GCubePortalConstants.GROUP_MEMBERS_FRIENDLY_URL)
						.append("?")
						.append(new String(Base64.encodeBase64(GCubeSocialNetworking.GROUP_MEMBERS_OID.getBytes())))
						.append("=");		
				//add the View Managers redirect (and -1 as groupID)
				String managerRedirectURL = new String(pageToRedirectURL);
				managerRedirectURL += new String(Base64.encodeBase64(("-100").getBytes()));
			
				toReturn.add(new GroupDTO(true, "View Managers", "No Desc", managerRedirectURL));
				List<GCubeTeam> groups = new LiferayRoleManager().listTeamsByGroup(vreGroupId);
				for (GCubeTeam g : groups) {
					String encodedTeamId = new String(Base64.encodeBase64((""+g.getTeamId()).getBytes()));
					String teamRedirectURL = pageToRedirectURL+encodedTeamId;
					toReturn.add(new GroupDTO(
							false, 
							g.getTeamName(),
							g.getDescription(), 
							teamRedirectURL));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		} else {
			toReturn.add(new GroupDTO(true, "View Managers ", "Desc", "URL"));
			for (int i = 0; i < 5; i++) {
				toReturn.add(new GroupDTO(false, "Group " + i, "Desc", "URL"));
			}
		}		
		return toReturn;
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
}
