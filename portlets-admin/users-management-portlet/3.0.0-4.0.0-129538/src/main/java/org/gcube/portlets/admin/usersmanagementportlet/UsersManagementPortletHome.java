package org.gcube.portlets.admin.usersmanagementportlet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.portal.mailing.EmailNotification;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.TeamRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeMembershipRequest;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.MembershipRequestStatus;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class UsersManagementPortletHome extends GenericPortlet {
	protected String viewTemplate;
	protected boolean once = true;
	private static Log _log = LogFactoryUtil.getLog(UsersManagementPortletHome.class);
	private static final int REJECT_MEMBERSHIP_REQUESTS_TABLE = 0;
	private static final int APPROVE_MEMBERSHIP_REQUESTS_TABLE = 1;
	private static final int REFRESH_MEMBERSHIP_REQUESTS_TABLE = 2;
	private static final int EDIT_CURRENT_USERS_TABLE= 1;
	private static final int REFRESH_CURRENT_USERS_TABLE = 2;
	private static final int SITE_TEAMS_TABLE_CREATE_GROUP = 3;
	private static final int REFRESH_SITE_TEAMS_TABLE = 2;
	private static final int EDIT_SITE_TEAMS_TABLE = 1;
	private static final int DELETE_SITE_TEAMS_TABLE = 0;
	private static final int MASS_EDIT_USERS = 0;
	private static final int ASSIGN_ROLES_TO_USERS = 1;
	private static final int ASSIGN_TEAMS_TO_USERS = 2;
	
	DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

	public void init() {
		viewTemplate = getInitParameter("view-template");
	}

	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		include(viewTemplate, renderRequest, renderResponse);
	}

	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
		PortletRequest portletRequest = (PortletRequest)request.getAttribute("javax.portlet.request");
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		long selfId = themeDisplay.getLayout().getUserId();

		//CurrentUsers
		boolean currentUsersTable = ParamUtil.getBoolean(request, "currentUsersTable");
		boolean deleteUsersFromCurrentUsersTable = ParamUtil.getBoolean(request, "deleteUsersFromCurrentUsersTable");
		int modeCurrentUsersTable = ParamUtil.getInteger(request, "modeCurrentUsersTable");
		long groupId = ParamUtil.getLong(request, "groupId");
		
		currentUsersTableSection(
				currentUsersTable, deleteUsersFromCurrentUsersTable,modeCurrentUsersTable,
				groupId, jsonObject, themeDisplay, portletRequest, request, selfId);

		//MembershipRequests
		boolean fetchUsersRequests = ParamUtil.getBoolean(request, "fetchUsersRequests");
		int modeMembershipRequestsTable = ParamUtil.getInteger(request, "modeMembershipRequestsTable");
		
		membershipRequestsSection(
				fetchUsersRequests, modeMembershipRequestsTable,
				themeDisplay, jsonObject, request, portletRequest,
				groupId);

		//count the membership requests
		boolean countUsersMembershipRequests = ParamUtil.getBoolean(request, "countUsersMembershipRequests");
		countUsersMembershipRequestsSection(countUsersMembershipRequests, jsonObject, groupId);
		
		//Site-teams handling
		boolean fetchAllSiteTeamsForTheCurrentGroup = ParamUtil.getBoolean(request, "fetchAllSiteTeamsForTheCurrentGroup");
		int modeSiteTeams = ParamUtil.getInteger(request, "modeSiteTeams");
		if(fetchAllSiteTeamsForTheCurrentGroup)
			try {
				siteTeamsForTheCurrentGroupSection(themeDisplay, request, selfId, modeSiteTeams, groupId, jsonObject);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		
		response.getWriter().println(jsonObject);
		super.serveResource(request, response);
	}

	protected void include(String path, RenderRequest renderRequest, RenderResponse renderResponse) throws IOException,PortletException { 
		String url = null;
		if (renderRequest.getParameter("jspPage") == null || renderRequest.getParameter("jspPage").equals("./"))
			url = path + "usersManagement.jsp";
		else
			url = path + renderRequest.getParameter("jspPage");

		PortletRequestDispatcher portletRequestDispatcher = getPortletContext().getRequestDispatcher(url);

		if (portletRequestDispatcher == null) {
			_log.error(path + " is not a valid include");
		}
		else {
			ScopeHelper.setContext(renderRequest); // <-- Static method which sets the username in the session and the scope depending on the context automatically	
			portletRequestDispatcher.include(renderRequest, renderResponse);
		}
	}

	protected void rejectMembershipRequests(
			PortletRequest portletRequest,
			ThemeDisplay themeDisplay, ResourceRequest request, long[] theReqIDs,
			long groupId, long managerId, boolean CustomRejectionEmailFromAdmin,
			String CustomRejectionEmailBodyFromAdmin)
					throws NumberFormatException, SystemException, PortalException, UserManagementSystemException, GroupRetrievalFault{
		UserManager um = new LiferayUserManager();
		List<GCubeMembershipRequest> reqs;
		GroupManager gm = new LiferayGroupManager();
		User managerUser = themeDisplay.getUser();
		String managerName = managerUser.getScreenName();
		String groupName = gm.getGroup(groupId).getGroupName();
		String managerFullName = managerUser.getFirstName();
		String managerEmail = EmailPartsConstruction.returnProperty("notificationSenderEmail");
		String groupNameForSubject = "";

		if(gm.isRootVO(groupId)) {
			groupNameForSubject += " Virtual Organization";
			groupName = "<b>" + groupName + "</b> Virtual Organization";
		} else if(gm.isVRE(groupId)) {
			groupNameForSubject += " Virtual Research Environment";
			groupName = "<b>" + groupName + "</b> Virtual Research Environment";
		}else{
			groupNameForSubject = groupName;
			_log.info("isRootVO: " + gm.isRootVO(groupId) + "\nisVRE: " + gm.isVRE(groupId));
		}
		
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(request);
		
		String portalComplete = "<b>" +PortalContext.getConfiguration().getGatewayURL(httpServletRequest) + "</b>";
		String gatewayNameForSubject = PortalContext.getConfiguration().getGatewayName(httpServletRequest);
		
		try {
			reqs = um.listMembershipRequestsByGroup(groupId);
			for(GCubeMembershipRequest req : reqs){
				for(long reqId : theReqIDs){
					if(req.getStatus() == MembershipRequestStatus.REQUEST && req.getMembershipRequestId() == reqId){
						GCubeUser replyUser = um.getUserById(managerId);
						String replyComment = "Membership Request rejected";
						@SuppressWarnings("unused")
						GCubeMembershipRequest gcmr = um.rejectMembershipRequest(req.getRequestingUser().getUserId(), groupId, replyUser.getUsername(), replyComment);
						String userName = req.getRequestingUser().getUsername();

						String emailProperBody = null;
						if(CustomRejectionEmailFromAdmin){
							emailProperBody = CustomRejectionEmailBodyFromAdmin;
						} else {
							String userFullName = req.getRequestingUser().getFullname();
							String userEmail = req.getRequestingUser().getEmail();
							String rejectionBody = "membershipRequestRejectionBody";
							emailProperBody = EmailPartsConstruction.sendMailForMembershipRequestAcceptanceOrRejection(
									rejectionBody,userFullName, groupName, 
									managerFullName, managerEmail,
									userEmail, portalComplete);
						}
						String emailRecipient = req.getRequestingUser().getEmail();
						String emailSubject = EmailPartsConstruction.subjectForMembershipRequestAcceptanceOrRejection(
								"membershipRequestRejectionSubject", gatewayNameForSubject, groupNameForSubject);
						String emailBody = emailProperBody;

						String[] emailRecipients= new String[]{emailRecipient};
						
						EmailNotification en = new EmailNotification(emailRecipients, emailSubject, emailBody, httpServletRequest);
						en.sendEmail();

						_log.info("Admin: " + managerName + " rejected the membership request of the user:" + userName + " for the site: " + groupName);
					}
				}
			}
		} catch (UserManagementPortalException e) {
			_log.info("MembershipRequest retrieval failure");
			e.printStackTrace();
		} catch (UserManagementSystemException e) {
			_log.info("MembershipRequest retrieval failure");
			e.printStackTrace();
		} catch (GroupRetrievalFault e) {
			_log.info("MembershipRequest retrieval failure");
			e.printStackTrace();
		} catch (UserRetrievalFault e) {
			_log.info("MembershipRequest retrieval failure");
			e.printStackTrace();
		}
	}

	protected void acceptMemebershipRequestAndAddUsersToGroup(PortletRequest portletRequest, ResourceRequest request, long[] reqIDs, Long groupId, Long managerId) throws NumberFormatException, PortalException, SystemException, UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		LiferayUserManager lum = new LiferayUserManager();
		List<GCubeMembershipRequest> membershipRequests = lum.listMembershipRequestsByGroup(groupId);
		GCubeUser manager = lum.getUserById(managerId);
		GroupManager gm = new LiferayGroupManager();
		String groupName = gm.getGroup(groupId).getGroupName();
		String groupNameForSubject = "";
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(request);
		
		if(gm.isRootVO(groupId)) {
			groupNameForSubject += " Virtual Organization";
			groupName = "<b>" + groupName + "</b> Virtual Organization";
		} else if(gm.isVRE(groupId)) {
			groupNameForSubject += " Virtual Research Environment";
			groupName = "<b>" + groupName + "</b> Virtual Research Environment";
		}else{
			groupNameForSubject = groupName;
			_log.info("isRootVO: " + gm.isRootVO(groupId) + "\nisVRE: " + gm.isVRE(groupId));
		}
		
		for(GCubeMembershipRequest gcmr : membershipRequests){
			for(long reqId : reqIDs){
				if(gcmr.getStatus() == MembershipRequestStatus.REQUEST && gcmr.getMembershipRequestId() == reqId){
					boolean addUserToGroup = true;
					try {
						String managerComments = "Membership request approved";
						@SuppressWarnings("unused")
						GCubeMembershipRequest gcmr2 = lum.acceptMembershipRequest(gcmr.getRequestingUser().getUserId(), groupId, addUserToGroup, manager.getUsername(), managerComments);
						lum.assignUserToGroup(groupId, gcmr.getRequestingUser().getUserId());
						String userName = gcmr.getRequestingUser().getUsername();
						String managerName = manager.getUsername();

						_log.info("Admin: " + managerName + " accepted user's : " + userName + " membership request for the site: " + groupName);

						String userFullName = gcmr.getRequestingUser().getFullname();
						String managerFullName = manager.getFullname();
						String managerEmail = manager.getEmail();
						String userEmail = gcmr.getRequestingUser().getEmail();

						String portalComplete = "<b>" + PortalContext.getConfiguration().getGatewayURL(httpServletRequest) + "</b>";
						String gatewayNameForSubject = PortalContext.getConfiguration().getGatewayName(httpServletRequest);
						
						String acceptBody = "membershipRequestAcceptanceBody";
						String properEmailBody = EmailPartsConstruction.sendMailForMembershipRequestAcceptanceOrRejection(acceptBody, userFullName, groupName, managerFullName, managerEmail, userEmail, portalComplete);
						String properEmailSubject = EmailPartsConstruction.subjectForMembershipRequestAcceptanceOrRejection(
								"membershipRequestAcceptanceSubject", gatewayNameForSubject, groupNameForSubject);

						String[] emailRecipients = new String[]{userEmail};
						EmailNotification en = 
								new EmailNotification(emailRecipients, properEmailSubject, properEmailBody, httpServletRequest);
						
						en.sendEmail();
					} catch (UserManagementPortalException e) {
						_log.info("User: " + gcmr.getRequestingUser().getUsername() + " wasn't added to the site: " + gm.getGroup(groupId).getGroupName());
						e.printStackTrace();
					}
				}
			}
		}
	}

	protected JSONArray numberOfRequestsForSpecificGroup(long groupId) throws PortalException, SystemException, UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault{
		JSONArray ja = JSONFactoryUtil.createJSONArray();
		UserManager um = new LiferayUserManager();
		List<GCubeMembershipRequest> gcmrList = new ArrayList<GCubeMembershipRequest>();
		for(GCubeMembershipRequest gcmr : um.listMembershipRequestsByGroup(groupId)){
			if(gcmr.getStatus() == MembershipRequestStatus.REQUEST){
				gcmrList.add(gcmr);
			}
		}
		ja.put(gcmrList.size());
		
		ja.put(EmailPartsConstruction.returnPortalName());
		
		return ja;
	}

	protected JSONArray currentGroupUsers(ThemeDisplay themeDisplay, long groupId, long selfId) throws GroupRetrievalFault, UserManagementSystemException, UserRetrievalFault{
		JSONArray ja = JSONFactoryUtil.createJSONArray();
		List<GCubeUser> users = new ArrayList<GCubeUser>();
		LiferayUserManager lm = new LiferayUserManager();

		try{
			users = lm.listUsersByGroup(groupId);

			User user = themeDisplay.getUser();

			if(users.size() > 0){
				for(GCubeUser someUser : users){
					JSONObject jo = JSONFactoryUtil.createJSONObject();
					try{
						List<GCubeMembershipRequest> gcmrs = lm.getMembershipRequests(someUser.getUserId(), groupId, MembershipRequestStatus.APPROVED);
						GCubeMembershipRequest mr = gcmrs.get(gcmrs.size()-1); 
						jo.put("requestDate", mr.getCreateDate());
						jo.put("validationDate", mr.getReplyDate());
						jo.put("requestComments", mr.getComment());
						jo.put("reqID", mr.getMembershipRequestId());
						jo.put("acceptanceAdmin", mr.getReplierUser().getUsername());
					}catch(Exception e){
						jo.put("requestDate", "-");
						jo.put("validationDate", "-");
						jo.put("requestComments", "-");
						jo.put("reqID", 0);
						jo.put("acceptanceAdmin", "-");
					}

					jo.put("userName", someUser.getUsername());
					jo.put("userFullName", someUser.getFullname());
					jo.put("userEmail", someUser.getEmail());
					jo.put("userId", someUser.getUserId());
					String isSelf = (someUser.getUserId() == selfId) ? "true" : "false";
					jo.put("isSelf", isSelf);

					LiferayRoleManager lrm = new LiferayRoleManager();
					List<GCubeRole> rolesList = lrm.listRolesByUserAndGroup(someUser.getUserId(), groupId);
					JSONArray ja2 = JSONFactoryUtil.createJSONArray();
					for(GCubeRole r : rolesList){
						ja2.put(r.getRoleName());
					}

					jo.put("userSiteRoles", ja2);
					
//					List<Team> teams = TeamLocalServiceUtil.getUserTeams(someUser.getUserId());
					
					List<GCubeTeam> gcubeTeams = lrm.listTeamsByUserAndGroup(someUser.getUserId(), groupId);
					JSONArray ja3 = JSONFactoryUtil.createJSONArray();
					for(GCubeTeam t : gcubeTeams){
						ja3.put(t.getTeamName());
					}
					
					jo.put("userTeams", ja3);
					
					ja.put(jo);
				}
				LiferayGroupManager lgm = new LiferayGroupManager();
				_log.info("User: " + user.getScreenName() + " is displaying users of the site: " + lgm.getGroup(groupId).getGroupName());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ja;
	}

	protected JSONArray currentGroupUsersRequests(ThemeDisplay themeDisplay, long groupId) throws GroupRetrievalFault, UserManagementSystemException, UserRetrievalFault{
		JSONArray ja = JSONFactoryUtil.createJSONArray();
		@SuppressWarnings("unused")
		List<GCubeUser> users = new ArrayList<GCubeUser>();
		LiferayUserManager lm = new LiferayUserManager();
		users = lm.listUsersByGroup(groupId);
		LiferayGroupManager lgm = new LiferayGroupManager();

		List<GCubeMembershipRequest> reqs = new ArrayList<GCubeMembershipRequest>();
		try{
			reqs = lm.listMembershipRequestsByGroup(groupId);
			if(reqs.size() > 0){
				for(GCubeMembershipRequest gcmr : reqs){
					if(gcmr.getStatus() == MembershipRequestStatus.REQUEST){
						JSONObject jo = JSONFactoryUtil.createJSONObject();
						jo.put("requestDate", gcmr.getCreateDate());
						jo.put("validationDate", gcmr.getReplyDate());
						jo.put("requestComments", gcmr.getComment());
						jo.put("requestId", gcmr.getMembershipRequestId());
						GCubeUser gcu = gcmr.getRequestingUser();
						jo.put("userName", gcu.getUsername());
						jo.put("userFullName", gcu.getFullname());
						jo.put("userEmail", gcu.getEmail());
						jo.put("userId", gcu.getUserId());
						LiferayRoleManager lrm = new LiferayRoleManager();
						List<GCubeRole> rolesList = lrm.listRolesByUserAndGroup(gcu.getUserId(), groupId);
						List<String> roles = new ArrayList<String>();
						for(GCubeRole gcr : rolesList){
							roles.add(gcr.getRoleName());
						}
						jo.put("userSiteRoles", roles.toString());
						ja.put(jo);
					}

				}
			}
			User managerUser = themeDisplay.getUser();
			String groupName = lgm.getGroup(groupId).getGroupName();
			_log.info("The admin: " + managerUser.getScreenName() + " displayed membershipRequests for the site: " + groupName);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ja;
	}

	protected JSONArray usersForCurrrentUsersTablePlusRoles(ThemeDisplay themeDisplay, Long groupId,
			long[] usersIDs, String[] theRoles, String[] usersTeams,
			boolean deletePreviousRoles, long selfId, int typeOfChangesUpponUserMode
			) throws SystemException, PortalException, UserManagementSystemException, UserRetrievalFault, RoleRetrievalFault, GroupRetrievalFault{
		User userManager = themeDisplay.getUser();
		LiferayGroupManager lgm = new LiferayGroupManager();
		String groupName = lgm.getGroup(groupId).getGroupName();

		LiferayRoleManager lrm = new LiferayRoleManager();
		LiferayUserManager lum = new LiferayUserManager();

		if(usersIDs.length == 1){//You can delete previous roles of a single user only.
			GCubeUser gcu = lum.getUserById(usersIDs[0]);
			long[] roleIDs = new long[theRoles.length];
			GCubeRole[] gCubeRoles = new GCubeRole[theRoles.length];
			
			for(int i=0;i<theRoles.length;i++){
				gCubeRoles[i] = lrm.getRole(theRoles[i].trim(), groupId);
				roleIDs[i] = gCubeRoles[i].getRoleId();
			}
			
			if(deletePreviousRoles && typeOfChangesUpponUserMode == MASS_EDIT_USERS){
				_log.info("MASS_EDIT_USERS");
				try{
					lrm.removeAllRolesFromUser(gcu.getUserId(), groupId);						
				}catch(Exception e){
					e.printStackTrace();
				}
				
				List<GCubeTeam> teams = lrm.listTeamsByGroup(groupId);
				if(teams.size() > 0 ){
					lrm.deleteUserTeams(gcu.getUserId(), teams);
				}
			} else if(deletePreviousRoles && typeOfChangesUpponUserMode == ASSIGN_ROLES_TO_USERS){
				_log.info("ASSIGN_ROLES_TO_USERS");
				try{
					lrm.removeAllRolesFromUser(gcu.getUserId(), groupId);						
				}catch(Exception e){
					e.printStackTrace();
				}
			} else if(deletePreviousRoles && typeOfChangesUpponUserMode == ASSIGN_TEAMS_TO_USERS){
				_log.info("ASSIGN_TEAMS_TO_USERS");
				
				List<GCubeTeam> teams = lrm.listTeamsByGroup(groupId);
				if(teams.size() > 0 ){
					lrm.deleteUserTeams(gcu.getUserId(), teams);
				}
			}
			
			long[] gCubeTeamIDs = new long[usersTeams.length];
			String[] gCubeTeamNames = new String[usersTeams.length];
			StringBuffer teamNames = new StringBuffer();
			
			for(int i=0; i<usersTeams.length; i++){
				GCubeTeam team = null;
				try {
					team = lrm.getTeam(groupId, usersTeams[i].trim());
					gCubeTeamIDs[i] = team.getTeamId();
					gCubeTeamNames[i] = team.getTeamName();
					if(i != usersTeams.length-1){
						teamNames.append(gCubeTeamNames[i]).append(", ");
					}else {
						teamNames.append(gCubeTeamNames[i]);
					}
					
					_log.info("Succeeded at retrieving team with name: " + usersTeams[i].trim() + " for the group: "+ groupName);
				} catch (TeamRetrievalFault e1) {
					_log.info("Failed at retrieving team with name: " + usersTeams[i].trim() + " for the group: "+ groupName);
					e1.printStackTrace();
				}
			}

			try {
				if(typeOfChangesUpponUserMode != ASSIGN_ROLES_TO_USERS){
					lrm.setUserTeams(gcu.getUserId(), gCubeTeamIDs);
					_log.info("User: " + gcu.getUsername() + " was added to following teams: " + teamNames);
				}
			} catch (TeamRetrievalFault e) {
				_log.info("User: " + gcu.getUsername() + " failed to be added to the following teams: " + teamNames);
				e.printStackTrace();
			}
			
			@SuppressWarnings("unused")
			boolean rolesAssignmentSucceeded  = lrm.assignRolesToUser(gcu.getUserId(), groupId, roleIDs);
			_log.info("User: " + userManager.getScreenName() + " is editing the roles of user: "+ gcu.getUsername() + " for the site: " + groupName + "and deletes all previous site-roles");
		}else{
			
			List<GCubeTeam> existingGCubeTeams = lrm.listTeamsByGroup(groupId);
			
			for(long uid : usersIDs){
				GCubeUser user = lum.getUserById(uid);
				GCubeUser gcu = lum.getUserByUsername(user.getUsername());

				List<GCubeUser> usersInTeam = new ArrayList<GCubeUser>();

				Set<Long> existingGCubeTeamsIDs = new HashSet<Long>();
				
				for(GCubeTeam gCubeTeam : existingGCubeTeams){
					try {
						usersInTeam = lum.listUsersByTeam(gCubeTeam.getTeamId());
						
						if(usersInTeam.contains(user)){
							existingGCubeTeamsIDs.add(gCubeTeam.getTeamId());
						}
					} catch (TeamRetrievalFault e) {
						_log.info("Failed to retrieve team with teamId: " + gCubeTeam.getTeamId());
						e.printStackTrace();
					}
				}
				
				long[] roleIDs = new long[theRoles.length];

				GCubeRole[] gCubeRoles = new GCubeRole[theRoles.length];
				for(int i=0;i<theRoles.length;i++){
					gCubeRoles[i] = lrm.getRole(theRoles[i].trim(), groupId);
					roleIDs[i] = gCubeRoles[i].getRoleId();
				}
				
				@SuppressWarnings("unused")
				boolean rolesAssignmentSucceeded  = lrm.assignRolesToUser(gcu.getUserId(), groupId, roleIDs);
				
				
				List<Long> gCubeTeamIDs = new ArrayList<Long>();//[usersTeams.length];
				String[] gCubeTeamNames = new String[usersTeams.length];
				StringBuffer teamNames = new StringBuffer();
				
				for(int i=0; i<usersTeams.length; i++){
					GCubeTeam team = null;
					try {
						team = lrm.getTeam(groupId, usersTeams[i].trim());
//						gCubeTeamIDs[i] = team.getTeamId();
						gCubeTeamIDs.add(team.getTeamId());
						gCubeTeamNames[i] = team.getTeamName();
						if(i != usersTeams.length-1){
							teamNames.append(gCubeTeamNames[i]).append(", ");
						}else {
							teamNames.append(gCubeTeamNames[i]);
						}
						
						_log.info("Succeeded at retrieving team with name: " + usersTeams[i].trim() + " for the group: "+ groupName);
					} catch (TeamRetrievalFault e1) {
						_log.info("Failed at retrieving team with name: " + usersTeams[i].trim() + " for the group: "+ groupName);
						e1.printStackTrace();
					}
				}
				
				existingGCubeTeamsIDs.addAll(gCubeTeamIDs);
				long[] gCubeTeamsIDsToBeAddedToTheUser = new long[existingGCubeTeamsIDs.size()];
				int i=0;
				for(Long l : existingGCubeTeamsIDs){
					gCubeTeamsIDsToBeAddedToTheUser[i] = l;
					i++;
				}
				
				try {
					lrm.setUserTeams(gcu.getUserId(), gCubeTeamsIDsToBeAddedToTheUser);
					_log.info("User: " + gcu.getUsername() + " was added to following teams: " + teamNames);
				} catch (TeamRetrievalFault e) {
					_log.info("User: " + gcu.getUsername() + " failed to be added to the following teams: " + teamNames);
					e.printStackTrace();
				}
				
				_log.info("User: " + userManager.getScreenName() + " is editing the roles of user: "+ user.getUsername() + " for the site: " + groupName);
			}
		}
		
		return currentGroupUsers(themeDisplay, groupId, selfId);
	}

	protected void removeUsersFromGroup(PortletRequest portletRequest, ResourceRequest request, ThemeDisplay themeDisplay, long groupId, long[] userIDs,
			long[] membershipRequestsIDs, boolean sendDismissalEmail)
					throws SystemException, PortalException, UserManagementSystemException,
					UserRetrievalFault, GroupRetrievalFault{
		
		UserManager um = new LiferayUserManager();
		GroupManager gm = new LiferayGroupManager();
		RoleManager rm = new LiferayRoleManager();

		User managerUser = themeDisplay.getUser();//Liferay function, must be changed
		String groupName = gm.getGroup(groupId).getGroupName();//GroupLocalServiceUtil.getGroup(groupId).getName();
		String groupNameForSubject = "";

		//Email

		if(gm.isRootVO(groupId)) {
			groupNameForSubject += " Virtual Organization";
			groupName = "<b>" + groupName + "</b> Virtual Organization";
		} else if(gm.isVRE(groupId)) {
			groupNameForSubject += " Virtual Research Environment";
			groupName = "<b>" + groupName + "</b> Virtual Research Environment";
		}else{
			groupNameForSubject = groupName;
			_log.info("isRootVO: " + gm.isRootVO(groupId) + "\nisVRE: " + gm.isVRE(groupId));
		}
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(request);
		
//		String portalComplete = "<b>" +PortalContext.getConfiguration().getGatewayName() + "</b>";
		String gateawayNameForSubject = PortalContext.getConfiguration().getGatewayName(httpServletRequest);
		
		String emailSubject = EmailPartsConstruction.subjectForUserDismissalFromSite(
				"userDismissalFromSiteSubject", gateawayNameForSubject, groupNameForSubject);
		String emailBody = java.text.MessageFormat.format(EmailPartsConstruction.returnProperty("userDismissalFromSiteBody"), groupName);

		List<String> recipients = new ArrayList<String>();
		for(int i = 0; i < membershipRequestsIDs.length; i++){
			GCubeMembershipRequest mr = um.getMembershipRequestsById(membershipRequestsIDs[i]);
			GCubeUser gcu = um.getUserById(userIDs[i]);
			um.dismissUserFromGroup(groupId, gcu.getUserId());
			
			if(sendDismissalEmail){
				String emailRecipient = gcu.getEmail();
				recipients.add(emailRecipient);
			}
			
			try {
				try{
					rm.removeAllRolesFromUser(gcu.getUserId(), groupId);
					_log.info("Removing all roles from user succeeded");
				}catch(Exception e){
					_log.info("Removing all roles from user failed");
					e.printStackTrace();
				}
				
				um.rejectMembershipRequest(mr.getRequestingUser().getUserId(), groupId, mr.getReplierUser().getUsername(), mr.getManagerReplyComment());

				_log.info("User: " + managerUser.getScreenName() + " dismissing the user: "+ gcu.getUsername() + " from the site: " + groupName);
			} catch (UserManagementPortalException e) {
				_log.info("Membership Request rejection failed");
				e.printStackTrace();
			}
		}
		
		try {
			if(recipients.size() > 0 && sendDismissalEmail){
				String[] theRecipients = new String[recipients.size()];
				theRecipients = recipients.toArray(theRecipients);
				EmailNotification en = new EmailNotification(theRecipients, emailSubject, emailBody, httpServletRequest);
				en.sendEmail();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	protected void currentUsersTableSection(
			boolean currentUsersTable,
			boolean deleteUsersFromCurrentUsersTable,
			int modeCurrentUsersTable,
			long groupId,
			JSONObject jsonObject,
			ThemeDisplay themeDisplay,
			PortletRequest portletRequest,
			ResourceRequest request, long selfId
			){
		if(currentUsersTable && !deleteUsersFromCurrentUsersTable){
			if(modeCurrentUsersTable == REFRESH_CURRENT_USERS_TABLE){
				try {
					jsonObject.put("currentUsers", currentGroupUsers(themeDisplay, groupId, selfId));
				} catch (GroupRetrievalFault e) {
					e.printStackTrace();
				} catch (UserManagementSystemException e) {
					e.printStackTrace();
				} catch (UserRetrievalFault e) {
					e.printStackTrace();
				}
			} else if(modeCurrentUsersTable == EDIT_CURRENT_USERS_TABLE){//edit
				try {
					long[] selectedUsers = ParamUtil.getLongValues(portletRequest, "selectedUsers[]");
					String[] usersRoles = ParamUtil.getParameterValues(portletRequest, "usersRoles[]");
					String[] usersTeams = ParamUtil.getParameterValues(portletRequest, "usersTeams[]");
					boolean deletePreviousRoles = ParamUtil.getBoolean(portletRequest, "deletePreviousRoles");
					int typeOfChangesUpponUserMode = ParamUtil.getInteger(portletRequest, "typeOfChangesUpponUserMode");
					jsonObject.put("currentUsers", usersForCurrrentUsersTablePlusRoles(themeDisplay, groupId, selectedUsers, usersRoles,
																						usersTeams, deletePreviousRoles, selfId,
																						typeOfChangesUpponUserMode));
				} catch (SystemException e) {
					e.printStackTrace();
				} catch (PortalException e) {
					e.printStackTrace();
				} catch (UserManagementSystemException e) {
					e.printStackTrace();
				} catch (UserRetrievalFault e) {
					e.printStackTrace();
				} catch (RoleRetrievalFault e) {
					e.printStackTrace();
				} catch (GroupRetrievalFault e) {
					e.printStackTrace();
				}
			}

		}else if(currentUsersTable && deleteUsersFromCurrentUsersTable){//delete

			try {
				long[] selectedUsers = ParamUtil.getLongValues(portletRequest, "selectedUsers[]");
				long[] membershipRequestsIDs = ParamUtil.getLongValues(request, "membershipRequestsIDs[]");
				boolean sendDismissalEmail = ParamUtil.getBoolean(portletRequest, "sendDismissalEmail");
				removeUsersFromGroup(portletRequest, request, themeDisplay, groupId, selectedUsers, membershipRequestsIDs, sendDismissalEmail);
				jsonObject.put("currentUsers", currentGroupUsers(themeDisplay, groupId, selfId));
			} catch (SystemException e) {
				e.printStackTrace();
			} catch (PortalException e) {
				e.printStackTrace();
			} catch (UserManagementSystemException e) {
				e.printStackTrace();
			} catch (UserRetrievalFault e) {
				e.printStackTrace();
			} catch (GroupRetrievalFault e) {
				e.printStackTrace();
			}
		}
	}

	protected void membershipRequestsSection(
			boolean fetchUsersRequests, int modeMembershipRequestsTable,
			ThemeDisplay themeDisplay, JSONObject jsonObject, ResourceRequest request,
			PortletRequest portletRequest, long groupId
			){
		if(fetchUsersRequests){
			if(modeMembershipRequestsTable == REFRESH_MEMBERSHIP_REQUESTS_TABLE){//Refresh
				try {
					jsonObject.put("currentUsersRequests", currentGroupUsersRequests(themeDisplay, groupId));
				} catch (GroupRetrievalFault e) {
					e.printStackTrace();
				} catch (UserManagementSystemException e) {
					e.printStackTrace();
				} catch (UserRetrievalFault e) {
					e.printStackTrace();
				}
			}else if(modeMembershipRequestsTable == APPROVE_MEMBERSHIP_REQUESTS_TABLE){//Accept
				try {
					long[] reqIDs = ParamUtil.getLongValues(portletRequest, "membershipRequestsIds[]");
					Long managerId = ParamUtil.getLong(request, "managerId");
					acceptMemebershipRequestAndAddUsersToGroup(portletRequest, request, reqIDs, groupId, managerId);
					jsonObject.put("currentUsersRequests", currentGroupUsersRequests(themeDisplay, groupId));
				}catch (SystemException e) {
					e.printStackTrace();
				}catch (GroupRetrievalFault e) {
					e.printStackTrace();
				} catch (UserManagementSystemException e) {
					e.printStackTrace();
				} catch (UserRetrievalFault e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (PortalException e) {
					e.printStackTrace();
				}
			}else if(modeMembershipRequestsTable == REJECT_MEMBERSHIP_REQUESTS_TABLE){//Reject
				try {
					long[] reqIDs = ParamUtil.getLongValues(portletRequest, "membershipRequestsIds[]");
					Long managerId = ParamUtil.getLong(request, "managerId");
					boolean CustomRejectionEmailFromAdmin = ParamUtil.getBoolean(request, "CustomRejectionEmailFromAdmin");
					String CustomRejectionEmailBodyFromAdmin = ParamUtil.getString(request, "CustomRejectionEmailBodyFromAdmin");
					_log.info(CustomRejectionEmailFromAdmin + " " + CustomRejectionEmailBodyFromAdmin);
					rejectMembershipRequests(portletRequest, themeDisplay, request, reqIDs, groupId, managerId, CustomRejectionEmailFromAdmin, CustomRejectionEmailBodyFromAdmin);
					jsonObject.put("currentUsersRequests", currentGroupUsersRequests(themeDisplay, groupId));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (SystemException e) {
					e.printStackTrace();
				} catch (PortalException e) {
					e.printStackTrace();
				} catch (GroupRetrievalFault e) {
					e.printStackTrace();
				} catch (UserManagementSystemException e) {
					e.printStackTrace();
				} catch (UserRetrievalFault e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void countUsersMembershipRequestsSection(
			boolean countUsersMembershipRequests,
			JSONObject jsonObject, long groupId){
		if(countUsersMembershipRequests){
			try {
				jsonObject.put("countUsersMembershipRequests", numberOfRequestsForSpecificGroup(groupId));
			} catch (PortalException e) {
				e.printStackTrace();
			} catch (SystemException e) {
				e.printStackTrace();
			} catch (UserManagementSystemException e) {
				e.printStackTrace();
			} catch (GroupRetrievalFault e) {
				e.printStackTrace();
			} catch (UserRetrievalFault e) {
				e.printStackTrace();
			}
		}
	}

	protected void siteTeamsForTheCurrentGroupSection(ThemeDisplay themeDisplay, ResourceRequest request, long managerID, int modeSiteTeams, long groupId, JSONObject jsonObject) throws SystemException{
		RoleManager rm = new LiferayRoleManager();
		
			if(modeSiteTeams == REFRESH_SITE_TEAMS_TABLE){
				fetchSiteTeams(groupId, jsonObject);
			} else if(modeSiteTeams == EDIT_SITE_TEAMS_TABLE){
				long siteTeamID = ParamUtil.getLong(request, "siteTeamID");
				String siteTeamName = ParamUtil.getString(request, "siteTeamName");
				String siteTeamDescription = ParamUtil.getString(request, "siteTeamDescription");
				
				try {
					String teamName = rm.getTeam(siteTeamID).getTeamName();
					try {
						rm.updateTeam(siteTeamID, siteTeamName, siteTeamDescription);
					} catch (TeamRetrievalFault e) {
						e.printStackTrace();
					}
					_log.debug("Edited team: " +teamName + " to Team Name: " + siteTeamName + " and Team Description: " + siteTeamDescription);
				} catch (UserManagementSystemException e1) {
					e1.printStackTrace();
				} catch (TeamRetrievalFault e1) {
					e1.printStackTrace();
				}
				fetchSiteTeams(groupId, jsonObject);
				
			} else if(modeSiteTeams == SITE_TEAMS_TABLE_CREATE_GROUP){
				String siteTeamName = ParamUtil.getString(request, "siteTeamName");
				String siteTeamDescription = ParamUtil.getString(request, "siteTeamDescription");
				long adminUserId = themeDisplay.getUserId();
				
				try {
					rm.createTeam(adminUserId, groupId, siteTeamName, siteTeamDescription);
					_log.debug("Added team with Name: " + siteTeamName + " and Team Description: " + siteTeamDescription);
				} catch (GroupRetrievalFault | TeamRetrievalFault | UserManagementSystemException e) {
					e.printStackTrace();
				}
				fetchSiteTeams(groupId, jsonObject);
				
			}
			else if(modeSiteTeams == DELETE_SITE_TEAMS_TABLE){
				long siteTeamID = ParamUtil.getLong(request, "siteTeamID");
				String teamName;
				try {
					teamName = rm.getTeam(siteTeamID).getTeamName();
					rm.deleteTeam(siteTeamID);
					_log.debug("Deleted team with Name: " + teamName);
				} catch (UserManagementSystemException | TeamRetrievalFault e) {
					e.printStackTrace();
				}
				fetchSiteTeams(groupId, jsonObject);
			}
	}
	
	void fetchSiteTeams(long groupId, JSONObject jsonObject){
		GroupManager gm = new LiferayGroupManager();
		RoleManager rm = new LiferayRoleManager();
		UserManager um = new LiferayUserManager();
		
		List<GCubeTeam> currentGroupTeams = new ArrayList<GCubeTeam>();
		try {
			currentGroupTeams = rm.listTeamsByGroup(groupId);
		} catch (GroupRetrievalFault e2) {
			e2.printStackTrace();
		}
		JSONArray ja = JSONFactoryUtil.createJSONArray();
		
		for(GCubeTeam siteTeam : currentGroupTeams){
			JSONObject jo = JSONFactoryUtil.createJSONObject();
			String siteTeamName = siteTeam.getTeamName();
			siteTeamName = siteTeamName.replace("'", "&#39;");//Escaping single quote char
			jo.put("Name", siteTeamName);
			jo.put("Description", siteTeam.getDescription());
			jo.put("CreationDate", df.format(siteTeam.getCreatedate()));
			jo.put("LastModificationDate", df.format(siteTeam.getModifiedDate()));
			
			int numberOfUsersInTeam = 0;
			List<GCubeUser> teamUsers = new ArrayList<GCubeUser>();
			try {
				teamUsers = um.listUsersByTeam(siteTeam.getTeamId());
				numberOfUsersInTeam = teamUsers.size();
			} catch (UserManagementSystemException | TeamRetrievalFault | UserRetrievalFault e) {
				e.printStackTrace();
			}
			jo.put("NumberOfUsers", numberOfUsersInTeam);
			jo.put("TeamID", siteTeam.getTeamId());
			
			JSONArray ja1 = JSONFactoryUtil.createJSONArray();

			List<GCubeUser> users = new ArrayList<GCubeUser>();
			
			try {
				users = um.listUsersByGroup(groupId);
			} catch (UserManagementSystemException | GroupRetrievalFault | UserRetrievalFault e1) {
				e1.printStackTrace();
			}
			
			for(GCubeUser u : users) {
				JSONObject jo1 = JSONFactoryUtil.createJSONObject();
				try {
					if(teamUsers.contains(u)){
						jo1.put("fullName", u.getFullname());
						jo1.put("screenName", u.getUsername());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				ja1.put(jo1);
			}
			jo.put("siteTeamUsers", ja1);
			try {
				jo.put("CreatorName", um.getUserById(siteTeam.getUserId()).getFullname());
			} catch (UserManagementSystemException | UserRetrievalFault e) {
				jo.put("CreatorName", "");
				e.printStackTrace();
			}
			ja.put(jo);
		}
		
		jsonObject.put("siteTeams", ja);
		String groupname = null;
		try {
			groupname = gm.getGroup(groupId).getGroupName();
		} catch (UserManagementSystemException | GroupRetrievalFault e) {
			e.printStackTrace();
		}
		_log.debug("Retrieving teams for the site: " + groupname);
	}
}