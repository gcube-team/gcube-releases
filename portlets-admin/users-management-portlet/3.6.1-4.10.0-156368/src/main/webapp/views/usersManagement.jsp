<%@page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@page import="com.liferay.portal.kernel.util.StringPool" %>
<%@page import="java.util.List"%>
<%@page import="com.liferay.portal.service.RoleLocalServiceUtil"%>
<%@page import="com.liferay.portal.model.Role"%>
<%@page import="com.liferay.portal.model.Team"%>
<%@page import="com.liferay.portal.service.TeamLocalServiceUtil" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<theme:defineObjects/>
<portlet:defineObjects />
<portlet:resourceURL var="loginURL" />

<p id="loginURL" hidden="true"><%=loginURL.toString() %></p>
<p id ="userID" hidden = "true"><%=user.getUserId()%></p>
<p id ="adminName" hidden = "true"><%=user.getFullName()%></p>
<p id ="groupName" hidden = "true"><%=themeDisplay.getLayout().getGroup().getName()%></p>
<p id="portletInfo" data-namespace="<portlet:namespace/>" data-loginurl="<portlet:resourceURL />"></p>

<!-- jQuery, bootstrap library -->
<script type="text/javascript">window.jQuery || document.write('<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js" type="text/javascript"><\/script>')</script>
<script src="https://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
<script src="<c:url value="/js/bootstrap.min.js" />"></script>

<!-- DataTables JS-->
<script src="<c:url value="/js/jquery_datatables/jquery.dataTables.js?01" />"></script>
<script src="<c:url value="/js/jquery_datatables/dataTables.responsive.min.js?01" />"></script>
<script src="<c:url value="/js/jquery_datatables/dataTables.buttons.min.js?01" />"></script>

<script src="<c:url value="/js/datatableutils.js?01" />"></script>
<script src="<c:url value="/js/ajaxcallpost.js?01" />"></script>
<script type="text/javascript">

<%
	long theGroupId = layout.getGroupId();
%>
	var nameSpace = $('#portletInfo').data('namespace');
	var loginURL = $('#portletInfo').data('loginurl');
	var portalName = "";
	
	var theGroupId = '<%=theGroupId%>';
	var usersTableData = [];//Read users' data from Liferay and initiate the usersTable
	var reqsTableData = [];//Read requests' data from Liferay and initiate the usersRequestsTable
	
	var usersTableDataForEditing = [];//Data of usersTable that will be manipulated
	var userTableUUIDsForEditing = [];
	
	var usersRequestsDataForEditing = [];//Data of usersTableRequests that will be manipulated
	var usersRequestsMembershipRequestsIdsForEditing = [];
	
	var deleteMode = 0;
	var acceptMode = 1;
	var refreshMode = 2;
	
	var deletePreviousRoles = false;
	
	var currentUsersTableRows = [];
	
	var automaticRejectionEmailTemplate;
	
	var sendCustomMailForMembershipRequestRejection = false;
	var customMailForMembershipRequestRejectionBody = "";
	
	var usersRequestsDetailModalWasOpen = false;
	//Team Management globals
	var groupTeamsTableData = [];
	var groupTeamsTableDataForEditing = [];
	var modeSiteTeams;
	var SITE_TEAMS_TABLE_CREATE_GROUP = 3;
	var SITE_TEAMS_TABLE_REFRESH = 2;
	var SITE_TEAMS_TABLE_EDIT_GROUP = 1;
	var SITE_TEAMS_TABLE_DELETE_GROUP = 0;
	var MASS_EDIT_USERS = 0;
	var ASSIGN_ROLES_TO_USERS = 1;
	var ASSIGN_TEAMS_TO_USERS = 2;
	
	var handlersAppliedToToolbarForFirstTime = false;
</script>

<%-- <portlet:renderURL var="anotherURL">
		<portlet:param name="jspPage" value="/teamsManagement.jsp" />
	</portlet:renderURL>
	
	<a href="<%= anotherURL %>">Go to another JSP</a> --%>
<div id="blanket" class="">

	<p id="preloader" class="hiddenPreloader">
		<img src="<c:url value="/img/preloader.gif?01" />"/>
	</p>

<ul class="nav nav-tabs" id="myTab">
	<li id="userManagement" class="userManagementTab">
		<a href="#userManagementTab" data-toggle="tab" class="tabTitle">User Management</a>
		<a class="lineBeneathTabTitle">Add/remove users</a>
	</li>
	<li id="teamManagement" class="unhit">
		<a href="#teamManagementTab" data-toggle="tab" class="tabTitle">Group Management</a>
		<a class="lineBeneathTabTitle">Edit groups</a>
	</li>
	<li id="rejectedUsersRequestsManagement" class="">
		<a href="#rejectedUsersRequestsManagementTab" data-toggle="tab" class="tabTitle">Rejected requests</a>
		<a class="lineBeneathTabTitle">View rejected requests</a>
	</li>
</ul>
	 
<div class="tab-content" id="tabsForTables">
	<div class="tab-pane userManagementTab unhit" id="userManagementTab">

		<div id="usersManagementPortletContainer">
		
			<div id="InternalServerErrorModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
			  <div class="modal-header">
				<div id="blueLineBottom">
				    <span id="deleteRoleHeader">Internal server error</span>
				    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
			    </div>
			  </div>
			  <div class="modal-body">
			  	<p>An internal server error has occured. Please try again later</p>
			  </div>
			  <div class="modal-footer">
			    <button id="closeInternalServerModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">OK</button>
			  </div>
			</div>
		
			
			<div id="element" class="introLoading" hidden="true"></div>
			
					<table id="CurrentUsersTable" class="no-wrap " style="width: 100%;">
						<thead>
							<tr role="row">
								<th>
									<div><i class="icon-ok"></i></div>
								</th>
								<th>
									username
								</th>
								<th>
									email
								</th>
								<th>
									full name
								</th>
								<th>
									roles
								</th>
								<th>
									groups
								</th>
								<th>
									request date
								</th>
								<th>
									validation date
								</th>
								<th>
									acceptance manager
								</th>
							</tr>
						</thead>
					</table>
			
			<div id="changeUsersRolesModal" class="modal fade in" hidden="true" tabindex="-1" role="dialog"> <!-- style="width:700px;"> -->
			  <div class="modal-header">
			  	<div id="changeUsersRolesModalHeaderDiv">
			  		<div class="modalHeaderContainer">
			  			<span id="theHeader">Edit selected user(s)</span>
			  			<div class="modalSubHeader">Assign/remove roles and groups to the selected users</div>
			  		</div>
				    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
			  	</div>
			  </div>
			  <div class="modal-body">
			  	<div id="auxilliaryDiv">
				  	<span id="textAboveTagsInput">
				  		<div id="singleTagSection" class="hiddenSection">
				  			<p id="deleteTheRoles">Do you want to delete the user's previous roles and groups?</p>
				  			<label id="deleteTheRolesLabel" for="singleTag">Delete</label>
				  			<input type="checkbox" id="singleTag">
				  		</div>
				  		<div id="multipleTagsSection" class="hiddenSection alert alert-warning">
				  			<p id="multipleUsersRole">Selected roles and/or groups will be granted to all users, if they are not already associated to them.</p>
				  		</div>
				  		<div class="row usersEmailsTagsInModals">
				  			<label class="span3" for="userNamesTagsInput">users selected</label>
				  			<textarea id="userNamesList" class="" row="1"></textarea>
				  		</div>
				  		<div class="row">
				  			<label class="span3" for="roleList">roles</label>
				  			<textarea id="roleList" class="" row="1"></textarea>
				  		</div>
				  		<div class="row">
				  			<label class="span3" for="teamsList">groups</label>
				  			<textarea id="teamsList" class="" row="1"></textarea>
				  		</div>
				  	</span>
			  	</div>
			  </div>
			  <div class="modal-footer">
			    <button id="saveUsersRolesModal" class="btn btn-link btn-large">Save</button>
			    <button id="closeUsersRolesModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">Cancel</button>
			  </div>
			</div>
			
			<div id="assignUsersRolesModal" class="modal fade in" hidden="true"  tabindex="-1" role="dialog"> <!-- style="width:700px;"> -->
			  <div class="modal-header">
			  	<div id="changeUsersRolesModalHeaderDiv">
				  	<div class="modalHeaderContainer">
					    <span id="theHeader">Assign roles to selected users</span>
			  			<div class="modalSubHeader">Assign/remove roles to the selected users</div>
				    </div>
				    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
			  	</div>
			  </div>
			  <div class="modal-body">
			  	<div id="auxilliaryDivInAssignRolesModal">
				  	<span id="textAboveTagsInputInAssignRolesModal">
				  		<div id="singleTagSectionInAssignRolesModal" class="hiddenSection">
				  			<p id="deleteTheRolesInAssignRolesModal">Do you want to delete the user's previous roles?</p>
				  			<label id="deleteTheRolesLabelInAssignRolesModal" for="singleTagInAssignRolesModal">Delete</label>
				  			<input type="checkbox" id="singleTagInAssignRolesModal">
				  		</div>
				  		<div id="multipleTagsSectionInAssignRolesModal" class="hiddenSection alert alert-warning">
				  			<p id="multipleUsersRole">Selected roles will be granted to all users, if they are not already associated to them.</p>
				  		</div>
				  		<div class="row usersEmailsTagsInModals">
				  			<label class="span3" for="userNamesListInAssignRolesModal">users selected</label>
				  			<textarea id="userNamesListInAssignRolesModal" class="" row="1"></textarea>
				  		</div>
				  		<div class="row">
				  			<label class="span3" for="roleListInAssignRolesModal">roles</label>
				  			<textarea id="roleListInAssignRolesModal" class="" row="1"></textarea>
				  		</div>
				  	</span>
			  	</div>
			  </div>
			  <div class="modal-footer">
			    <button id="saveUsersRolesModalInAssignRolesModal" class="btn btn-link btn-large">Save</button>
			    <button id="closeUsersRolesModalInAssignRolesModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">Cancel</button>
			  </div>
			</div>
			
			<div id="assignUsersToGroupsModal" class="modal fade in" hidden="true" tabindex="-1" role="dialog"> <!-- style="width:700px;"> -->
			  <div class="modal-header">
			  	<div id="changeUsersRolesModalHeaderDiv">
			  		<div class="modalHeaderContainer">
				    	<span id="theHeader">Add selected users to groups</span>
			  			<div class="modalSubHeader">Add/remove groups to the selected users</div>
				    </div>
				    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
			  	</div>
			  </div>
			  <div class="modal-body">
			  	<div id="auxilliaryDiv">
				  	<span id="textAboveTagsInputInAssignUsersToGroupsModal">
				  		<div id="singleTagSectionInAssignUsersToGroupsModal" class="hiddenSection">
				  			<p id="deleteTheRolesInAssignUsersToGroupsModal">Do you want to delete the user's previous groups?</p>
				  			<label id="deleteTheRolesLabelInAssignUsersToGroupsModal" for="singleTagInAssignUsersToGroupsModal">Delete</label>
				  			<input type="checkbox" id="singleTagInAssignUsersToGroupsModal">
				  		</div>
				  		<div id="multipleTagsSectionInAssignUsersToGroupsModal" class="hiddenSection alert alert-warning">
				  			<p id="multipleUsersRoleInAssignUsersToGroupsModal">Selected groups will be granted to all users, if they are not already associated to them.</p>
				  		</div>
				  		<div class="row usersEmailsTagsInModals">
				  			<label class="span3" for="userNamesTagsInputInAssignUsersToGroupsModal">users selected</label>
				  			<textarea id="userNamesListInAssignUsersToGroupsModal" class="" row="1"></textarea>
				  		</div>
				  		<div class="row">
				  			<label class="span3" for="teamsListInAssignUsersToGroupsModal">groups</label>
				  			<textarea id="teamsListInAssignUsersToGroupsModal" class="" row="1"></textarea>
				  		</div>
				  	</span>
			  	</div>
			  </div>
			  <div class="modal-footer">
			    <button id="saveUsersTeamsInAssignUsersToGroupsModal" class="btn btn-link btn-large">Save</button>
			    <button id="closeUsersTeamsInAssignUsersToGroupsModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">Cancel</button>
			  </div>
			</div>
			
			<div id="auxilliaryEditModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
			  <div class="modal-header">
				<div id="blueLineBottom">
				    <span id="deleteRoleHeader"></span>
				    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
			    </div>
			  </div>
			  <div class="modal-body">
			  	<p>One or both roles, groups fields are empty.</p>
			  </div>
			  <div class="modal-footer">
			    <button id="closeInternalServerModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">OK</button>
			  </div>
			</div>
			
			<div id="usersRequestsModal" class="modal fade" hidden="true" tabindex="-1" role="dialog"> <!-- style="width:700px;"> -->
			  <div class="modal-header">
				<div class="modalHeaderContainer">
					<span id="userRequestsHeader">Join VRE requests <a id="reloadUsersRequestsTable" class="btn" data-toggle = "tooltip" data-placement = "right" data-original-title = "Refreshs users' membership requests table"><i id="refreshIcon" class="icon-refresh"></i></a></span>
			  		<div class="modalSubHeader">
			  			<span class="grantDenyClass">Grant/deny</span>
			  			<span class="denyClass hidden">Deny</span>
			  			 access to the VRE 
			  			<span class="groupNameSpan"></span>	 
			  		</div>
				</div>
			    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
			  </div>
			  <div class="modal-body">
				  <div id="usersRequestsTableContainer" class="table-responsive">
				  	<table id="usersRequestsTable" class="nowrap" style="width :100%">
							<thead>
								<tr role="row">
									<th>
										<div><i class="icon-ok"></i></div>
									</th>
									<th>
										username
									</th>
									<th>
										email
									</th>
									<th>
										full name
									</th>
									<th>
										messages
									</th>
									<th>
										request date
									</th>
								</tr>
							</thead>
						</table>
				  </div>
				  
				  <div id="requestsAcceptanceContainer" class="hideSection">
				  	<div id="border"></div>
				  	<div id="requestsAcceptanceToolbar">
				  		<div id="clickToGoBack">
				  			<i class="icon-arrow-left"></i>
				  			<span id="toolbarText">Back to users' requests</span>
				  		</div>
				  	</div>
				  	
				  	<br>
				  	<br>
				  	
				  	<div id="requestsAcceptanceBody">
				  		<div class="row recipients">
					  		<label class="span1">Το :</label>
					  		<textarea id="tagsForEmails"></textarea>
				  		</div>
				  		<div class="row-fluid emailCBCContainer">
				  			<label class="span1">BCC :</label>
				  			<div class="emailBCC">
				  				
				  			</div>
				  			<textarea id="BCCAdminsEmails"></textarea>
				  		</div>
				  		<div class="row-fluid emailSubjectContainer">
				  			<label class="span1">Subject :</label>
				  			<div class="span11 emailSubject">
				  				
				  			</div>
				  		</div>
				  		<div class="row">
				  			<label class="span1">Message:</label>
				  			<div id="emailForAcceptance" class="span11">
				  			</div>
				  		</div>
				  		<div class="row hideSection">
				  			<label class="span1">Message:</label>
				  			<div id="emailForRejection" class="span11">
				  				<button id="editEmailTemplate" data-toggle = "tooltip" data-placement = "top" data-original-title = "Edit email template">
				  					<i class="icon-edit"></i>
				  				</button>
				  				Dear Sir / Madam,<br><br>
				  				
				  				Your request for accessing the %site% at: %portalName% has been rejected by %adminName% 
				  			</div>
				  		</div>
				  	</div>
				  	
				  </div>
				  
			  </div>
			  <div class="modal-footer">
			  	<button id="sendAcceptance" class="btn btn-link btn-large allButtons hideButton">Send acceptance</button>
			  	<button id="sendRejection" class="btn btn-link btn-large allButtons hideButton">Reject</button>
			    <button id="acceptAll" class="btn btn-link btn-large allButtons">Accept All</button>
			    <button id="rejectAll" class="btn btn-link btn-large allButtons">Reject All</button>
			    <button id="closeUsersRolesModal" class="btn btn-link btn-large">Cancel</button>
			  </div>
			</div>
			
			<div id="acceptUsersRequestsModal" class="modal fade" hidden="true"  tabindex="-1" role="dialog">
			  <div class="modal-header">
				<div id="blueLineBottom">
				  	<div class="modalHeaderContainer">
				    	<span id="acceptRequestHeader">Accept users' requests</span>
			  			<div class="modalSubHeader">Make the selected users members of the VRE</div>
				    </div>
				    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
			    </div>
			  </div>
			  <div class="modal-body">
			  	<p>Are you sure you want to accept the selected users' membership requests?</p>
			  </div>
			  <div class="modal-footer">
			    <button id="acceptUsersRequestsOk" class="btn btn-link btn-large">OK</button>
			    <button id="closeUsersRolesModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">Cancel</button>
			  </div>
			</div>
			
			<div id="rejectUsersRequestsModal" class="modal fade" hidden="true"  tabindex="-1" role="dialog">
			  <div class="modal-header">
				<div id="blueLineBottom">
				  	<div class="modalHeaderContainer">
				    	<span id="acceptRequestHeader">Reject users' requests</span>
			  			<div class="modalSubHeader">Deny the selected users access to the VRE</div>
				    </div>
				    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
			    </div>
			  </div>
			  <div class="modal-body">
			  	<p>Are you sure you want to reject the selected users' membership requests?</p>
			  </div>
			  <div class="modal-footer">
			    <button id="rejectUsersRequestsOk" class="btn btn-link btn-large">OK</button>
			    <button id="" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">Cancel</button>
			  </div>
			</div>
			
			<div id="deleteUsersFromCurrentSiteModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
			  <div class="modal-header">
			  	<div id="blueLineBottom">
				  	<div class="modalHeaderContainer">
					    <span id="deleteCurrentSiteUsersHeader">Dismiss users</span>
			  			<div class="modalSubHeader">Remove selected users from current VRE</div>
				    </div>
				    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
				</div>
			  </div>
			  <div class="modal-body">
			  	<p>Are you sure you want to remove the selected users?</p>
			  	<br>
			  	<!-- <label id="labelRejectionEmail" for="sendAutomaticRejectionEmail">Notify users via email</label>
			  	<input type="checkBox" id="sendAutomaticRejectionEmail"> -->
			  </div>
			  <div class="modal-footer">
			    <button id="acceptDeleteUsersFromCurrentSiteModal" class="btn btn-link btn-large">OK</button>
			    <button id="closeDeleteUsersFromCurrentSiteModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">Cancel</button>
			  </div>
			</div>
			
			<div id="userDetailsModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
			  <div class="modal-header">
			  	<div id="blueLineBottom">
				  	<div class="modalHeaderContainer">
					    <span id="detailsFor">User Details</span>
					    <span id="userName" hidden="true"></span>
						<div class="modalSubHeader">User's full list of attributes</div>
					</div>
				    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
				</div>
			  </div>
			  <div class="modal-body">
			  
			  </div>
			  <div class="modal-footer">
				<button id="openEditModal" class="btn btn-link btn-large">Edit</button>
			    <button id="closeUSerDetailsModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">Close</button>
			  </div>
			</div>
			
			<div id="cannotRemoveSelfModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
			  <div class="modal-header">
			  	<div id="blueLineBottom">
				    <span id="detailsFor">Removing user from group violation</span><span id="userName"></span>
				    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
				</div>
			  </div>
			  <div class="modal-body">
			  	<p>You are not allowed to remove yourself from the current group.</p>
			  </div>
			  <div class="modal-footer">
			    <button id="closeUSerDetailsModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">Close</button>
			  </div>
			</div>
		</div>
	</div>
	
	<div class="tab-pane" id="teamManagementTab">
		<div id="usersManagementPortletContainerSiteTeamsEditMode">

	<div id="InternalServerErrorModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span id="deleteRoleHeader">Internal server error</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<p>An internal server error has occured. Please try again later</p>
	  </div>
	  <div class="modal-footer">
	    <button id="closeInternalServerModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">OK</button>
	  </div>
	</div>
	
	<div id="displayGroupTeamUsersModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
			<div class="modalHeaderContainer">
		    	<span id="teamPrefixHeader">Members of group </span><span id="teamNameHeader"></span>
				<div class="modalSubHeader">Users that are members of this group</div>
		    </div>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
		<table id="GroupTeamsTableUsers" class="no-wrap " style="width: 100%;">
			<thead>
				<tr role="row">
					<th>
						Full Name
					</th>
					<th>
						UserName
					</th>
				</tr>
			</thead>
		</table>
	  </div>
	  <div class="modal-footer">
		<button id="assignUsersToGroupModalBtn" class="btn btn-link btn-large">Add Users</button>
		<button id="closeGroupTeamUsersModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">Close</button>
	  </div>
	</div>

		<table id="GroupTeamsTable" class="no-wrap " style="width: 100%;">
			<thead>
				<tr role="row">
					<th>
					</th>
					<th>
						name
					</th>
					<th>
						description
					</th>
					<th>
						number of users
					</th>
					<th>
						creation date
					</th>
					<th>
						last modification date
					</th>
					<th>
						creator name
					</th>
				</tr>
			</thead>
		</table>
		
	<div id="editGroupTeamModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
			<div class="modalHeaderContainer">
		    	<span id="acceptRequestHeader">Edit selected group</span>
				<div class="modalSubHeader">Edit the selected group's attributes</div>
		    </div>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<div id="groupTeamAttributes">
	  		<div class="row-fluid">
	  			<div class="span3">
	  				<label for="EditGroupTeamName">Name:</label>
	  			</div>
	  			<div class="span9">
	  				<input type="text" id="EditGroupTeamName" class="span12" placeholder="Name of group"/>
	  			</div>
	  		</div>
	  		<div class="row-fluid">
	  			<div class="span3">
	  				<label for="EditGroupTeamDescription">Description:</label>
	  			</div>
	  			<div class="span9">
	  				<textarea id="EditGroupTeamDescription" class="span12" rows="4" placeholder="Describe the group here"></textarea>
	  			</div>
	  		</div>
	  	</div>
	  </div>
	  <div class="modal-footer">
	    <button id="acceptEditGroupTeamOk" class="btn btn-link btn-large">OK</button>
	    <button id="closeEditGroupModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">Cancel</button>
	  </div>
	</div>
	
	<div id="deleteGroupTeamModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
			<div class="modalHeaderContainer">
		    	<span id="acceptRequestHeader">Deleted selected group</span>
				<div class="modalSubHeader">The selected group will be removed from the list of available groups</div>
		    </div>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<p>Are you sure you want to delete the selected group?</p>
	  </div>
	  <div class="modal-footer">
	    <button id="acceptDeleteGroupTeamOk" class="btn btn-link btn-large">OK</button>
	    <button id="closeDeleteGroupTeamModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">Cancel</button>
	  </div>
	</div>
		
	<div id="newGroupTeamModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
			<div class="modalHeaderContainer">
			    <span id="acceptRequestHeader">New group</span>
				<div class="modalSubHeader">Create a new group</div>
		    </div>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<div id="addGroupTeamAttributes">
	  		<div class="row-fluid">
	  			<div class="span3">
	  				<label for="addGroupTeamName">Name:</label>
	  			</div>
	  			<div class="span9">
	  				<input type="text" id="addGroupTeamName" class="span12" placeholder="Name of group"/>
	  			</div>
	  		</div>
	  		<div class="row-fluid">
	  			<div class="span3">
	  				<label for="addGroupTeamDescription">Description:</label>
	  			</div>
	  			<div class="span9">
	  				<textarea id="addGroupTeamDescription" class="span12" rows="4" placeholder="Describe the group here"></textarea>
	  			</div>
	  		</div>
	  	</div>
	  </div>
	  <div class="modal-footer">
	    <button id="acceptCreateGroupTeamOk" class="btn btn-link btn-large">OK</button>
	    <button id="closeCreateGroupModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">Cancel</button>
	  </div>
	</div>
			
</div>
	</div>
	
	<div class="tab-pane" id="rejectedUsersRequestsManagementTab">
		<div id="rejectedUsersRequestsTableContainer" class="table-responsive">
		  	<table id="rejectedUsersRequestsTable" class="nowrap" style="width :100%">
				<thead>
					<tr role="row">
						<th>
						</th>
						<th>
							username
						</th>
						<th>
							email
						</th>
						<th>
							full name
						</th>
						<th>
							messages
						</th>
						<th>
							request date
						</th>
						<th>
							rejection date
						</th>
					</tr>
				</thead>
			</table>
		  </div>
	</div>
</div>

</div>
<!-- JS Globals -->
<script type="text/javascript">
	var userNames = [];
	var assignUsersToGroupModalBtnPressed = false;//assging users to a group
	var filterUserTableByUsersThatDontBelongInAGroup = false;//assging users to a group
	var timesClicked = 0;
	var CurrentUsersTablePages = -1;
	var UsersRequestsTablePages = -1;
	var GroupTeamsTablePages = -1;
	var GroupTeamsTableUsersTablePages = -1;
	var keepTrackOfUsersTableRow = -1;
	var rejectRequestEmailSubject = "";
	var userRequestRejectionEmailAdminsMailsCC;
	
</script>
<!-- Tagsinput -->
<script src="<c:url value="/js/src/bootstrap-tagsinput.js?01" />"></script>
<!-- jquery-textext-master -->
<script src="<c:url value="/js/jquery-textext-master/src/js/textext.core.js?01" />"></script>
<script src="<c:url value="/js/jquery-textext-master/src/js/textext.plugin.ajax.js?01" />"></script>
<script src="<c:url value="/js/jquery-textext-master/src/js/textext.plugin.arrow.js?01" />"></script>
<script src="<c:url value="/js/jquery-textext-master/src/js/textext.plugin.autocomplete.js?01" />"></script>
<script src="<c:url value="/js/jquery-textext-master/src/js/textext.plugin.clear.js?01" />"></script>
<script src="<c:url value="/js/jquery-textext-master/src/js/textext.plugin.filter.js?01" />"></script>
<script src="<c:url value="/js/jquery-textext-master/src/js/textext.plugin.focus.js?01" />"></script>
<script src="<c:url value="/js/jquery-textext-master/src/js/textext.plugin.prompt.js?01" />"></script>
<script src="<c:url value="/js/jquery_datatables/dataTables.responsive.js?01" />"></script>
<!-- Textext -->
<link href="<c:url value="/js/jquery-textext-master/src/css/textext.core.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/responsive.bootstrap.css?01" />" rel="stylesheet">
<link href="<c:url value="/js/jquery-textext-master/src/css/textext.plugin.arrow.css?01" />" rel="stylesheet">
<link href="<c:url value="/js/jquery-textext-master/src/css/textext.plugin.autocomplete.css?01" />" rel="stylesheet">
<link href="<c:url value="/js/jquery-textext-master/src/css/textext.plugin.clear.css?01" />" rel="stylesheet">
<link href="<c:url value="/js/jquery-textext-master/src/css/textext.plugin.focus.css?01" />" rel="stylesheet">
<link href="<c:url value="/js/jquery-textext-master/src/css/textext.plugin.prompt.css?01" />" rel="stylesheet">
<link href="<c:url value="/js/jquery-textext-master/src/css/textext.plugin.tags.css?01" />" rel="stylesheet">
<script src="<c:url value="/js/jquery-textext-master/src/js/textext.plugin.tags.js?01" />"></script>
<!-- Custom JS -->
<script src="<c:url value="/js/DataTableCSSArrowsIssueWorkArround.js?01" />"></script>
<script src="<c:url value="/js/navBar.js?01" />"></script>
<script src="<c:url value="/js/displayRolesOnHOver.js?01" />"></script>
<script src="<c:url value="/js/siteTeamsTableEvents.js?01" />"></script>
<script src="<c:url value="/js/tableEvents.js?01" />"></script>
<script src="<c:url value="/js/InitializeDataTables.js?01" />"></script>
<script src="<c:url value="/js/Toolbar.js?01" />"></script>
<script src="<c:url value="/js/TagFunctionalities.js?01" />"></script>
<script src="<c:url value="/js/togglePreloader.js?01" />"></script>
<script src="<c:url value="/js/tabs.js?01" />"></script>
<script src="<c:url value="/js/groupTeamsModal.js?01" />"></script>
<!-- Tagsinput -->
<script src="<c:url value="/js/tagsInputInSearchInput.js?01" />"></script>
<!-- DataTables CSS-->
<link href="<c:url value="/css/jquery_datatables/jquery.dataTables.css?01" />" rel="stylesheet">
<!-- Custom CSS -->
<link href="<c:url value="/css/table.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/navbar.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/toolbar.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/pagination.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/modal.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/Tags.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/preloader.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/tabs.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/tagsinputForSearchByGroup.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/selectAllCheckboxes.css?01" />" rel="stylesheet">
<!-- font-awesome -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
<script type="text/javascript">
$(document).ready(function () {
	$('#usersRequestsModal .modalSubHeader .groupNameSpan').text(' ' +  $('#groupName').text());
	automaticRejectionEmailTemplate = $('#emailForRejection');
	initializeCurrentUsersTable();
	fetchAllCurrentUsers(2, false, [], [], false, [], false);
	constructToolbarForCurrentUsersTable();
	countUsersMembershipRequests();
	searchInputFixForCurrentUsersTable();
	initializeMembershipRequestsTable();
	initializeRejectedMembershipRequestsTable();
	searchInputFixForRejectedUsersRequestsTable();
	fetchAllRejectedUsersRequests();
	$('#rejectedUsersRequestsManagement a[data-toggle="tab"]').on('shown', function(){
		fetchAllRejectedUsersRequests();
	});
	constructToolbarForMembershipRequestsTable();
	var theList = [];
	var teamList = [];
	
	searchInputFixForMembershipRequestsTable();
	navBar();
	//tableEvents();
	setTooltips();
	$('#myTab a:first').tab('show');
	tabsFunctionality();
	try{
		showPreloader();
		fetchRolesInitial();
	}catch(err){
		$('#InternalServerErrorModal').modal('show');
	}
});
displaySiteRolesOnHover();
</script>