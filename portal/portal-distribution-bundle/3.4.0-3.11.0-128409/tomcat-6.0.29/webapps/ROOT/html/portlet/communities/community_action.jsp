<%
/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
%>

<%@ include file="/html/portlet/communities/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

Object[] objArray = (Object[])row.getObject();

Group group = (Group)objArray[0];
String tabs1 = (String)objArray[1];
%>

<liferay-ui:icon-menu>
	<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, group.getGroupId(), ActionKeys.UPDATE) %>">
		<portlet:renderURL var="editURL">
			<portlet:param name="struts_action" value="/communities/edit_community" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="groupId" value="<%= String.valueOf(group.getGroupId()) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			image="edit"
			url="<%= editURL %>"
		/>
	</c:if>

	<%--<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, group.getGroupId(), ActionKeys.PERMISSIONS) %>">
		<liferay-security:permissionsURL
			modelResource="<%= Group.class.getName() %>"
			modelResourceDescription="<%= group.getName() %>"
			resourcePrimKey="<%= String.valueOf(group.getGroupId()) %>"
			var="permissionsURL"
		/>

		<liferay-ui:icon
	   		image="permissions"
			url="<%= permissionsURL %>"
		/>
	</c:if>--%>

	<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, group.getGroupId(), ActionKeys.MANAGE_LAYOUTS) %>">
		<portlet:renderURL var="managePagesURL">
			<portlet:param name="struts_action" value="/communities/edit_pages" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="groupId" value="<%= String.valueOf(group.getGroupId()) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			image="pages"
			message="manage-pages"
			url="<%= managePagesURL %>"
		/>
	</c:if>

	<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, group.getGroupId(), ActionKeys.MANAGE_TEAMS) %>">
		<portlet:renderURL var="manageTeamsURL">
			<portlet:param name="struts_action" value="/communities/view_teams" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="groupId" value="<%= String.valueOf(group.getGroupId()) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			image="group"
			message="manage-teams"
			url="<%= manageTeamsURL %>"
		/>
	</c:if>

	<c:if test="<%= permissionChecker.isCommunityOwner(group.getGroupId()) || GroupPermissionUtil.contains(permissionChecker, group.getGroupId(), ActionKeys.ASSIGN_USER_ROLES) %>">
		<portlet:renderURL var="assignUserRolesURL">
			<portlet:param name="struts_action" value="/communities/edit_user_roles" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="groupId" value="<%= String.valueOf(group.getGroupId()) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			image="assign_user_roles"
			url="<%= assignUserRolesURL %>"
		/>
	</c:if>

	<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, group.getGroupId(), ActionKeys.ASSIGN_MEMBERS) %>">
		<portlet:renderURL var="assignMembersURL">
			<portlet:param name="struts_action" value="/communities/edit_community_assignments" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="groupId" value="<%= String.valueOf(group.getGroupId()) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			image="assign"
			message="assign-members"
			url="<%= assignMembersURL %>"
		/>

		<c:if test="<%= group.getType() == GroupConstants.TYPE_COMMUNITY_RESTRICTED %>">
			<portlet:renderURL var="viewMembershipRequestsURL">
				<portlet:param name="struts_action" value="/communities/view_membership_requests" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="groupId" value="<%= String.valueOf(group.getGroupId()) %>" />
			</portlet:renderURL>

			<liferay-ui:icon
				image="manage_task"
				message="view-membership-requests"
				url="<%= viewMembershipRequestsURL %>"
			/>
		</c:if>
	</c:if>

	<c:choose>
		<c:when test='<%= tabs1.equals("communities-owned") || tabs1.equals("communities-joined") %>'>
			<c:if test="<%= (group.getType() == GroupConstants.TYPE_COMMUNITY_OPEN) || (group.getType() == GroupConstants.TYPE_COMMUNITY_RESTRICTED) %>">
				<portlet:actionURL var="leaveURL">
					<portlet:param name="struts_action" value="/communities/edit_community_assignments" />
					<portlet:param name="<%= Constants.CMD %>" value="group_users" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
					<portlet:param name="groupId" value="<%= String.valueOf(group.getGroupId()) %>" />
					<portlet:param name="removeUserIds" value="<%= String.valueOf(user.getUserId()) %>" />
				</portlet:actionURL>

				<liferay-ui:icon
					image="leave"
					url="<%= leaveURL %>"
				/>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="<%= !GroupLocalServiceUtil.hasUserGroup(user.getUserId(), group.getGroupId()) %>">
					<c:choose>
						<c:when test="<%= group.getType() == GroupConstants.TYPE_COMMUNITY_OPEN %>">
							<portlet:actionURL var="joinURL">
								<portlet:param name="struts_action" value="/communities/edit_community_assignments" />
								<portlet:param name="<%= Constants.CMD %>" value="group_users" />
								<portlet:param name="redirect" value="<%= currentURL %>" />
								<portlet:param name="groupId" value="<%= String.valueOf(group.getGroupId()) %>" />
								<portlet:param name="addUserIds" value="<%= String.valueOf(user.getUserId()) %>" />
							</portlet:actionURL>

							<liferay-ui:icon
								image="join"
								url="<%= joinURL %>"
							/>
						</c:when>
						<c:when test="<%= group.getType() == GroupConstants.TYPE_COMMUNITY_RESTRICTED %>">
							<portlet:renderURL var="membershipRequestURL">
								<portlet:param name="struts_action" value="/communities/post_membership_request" />
								<portlet:param name="redirect" value="<%= currentURL %>" />
								<portlet:param name="groupId" value="<%= String.valueOf(group.getGroupId()) %>" />
							</portlet:renderURL>

							<liferay-ui:icon
								image="post"
								message="request-membership"
								url="<%= membershipRequestURL %>"
							/>
						</c:when>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:if test="<%= (group.getType() == GroupConstants.TYPE_COMMUNITY_OPEN) || (group.getType() == GroupConstants.TYPE_COMMUNITY_RESTRICTED) %>">
						<portlet:actionURL var="leaveURL">
							<portlet:param name="struts_action" value="/communities/edit_community_assignments" />
							<portlet:param name="<%= Constants.CMD %>" value="group_users" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="groupId" value="<%= String.valueOf(group.getGroupId()) %>" />
							<portlet:param name="removeUserIds" value="<%= String.valueOf(user.getUserId()) %>" />
						</portlet:actionURL>

						<liferay-ui:icon
							image="leave"
							url="<%= leaveURL %>"
						/>
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>

	<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, group.getGroupId(), ActionKeys.DELETE) %>">
		<portlet:actionURL var="deleteURL">
			<portlet:param name="struts_action" value="/communities/edit_community" />
			<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE %>" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="groupId" value="<%= String.valueOf(group.getGroupId()) %>" />
		</portlet:actionURL>

		<liferay-ui:icon-delete url="<%= deleteURL %>" />
	</c:if>
</liferay-ui:icon-menu>