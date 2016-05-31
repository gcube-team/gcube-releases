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
String tabs1 = ParamUtil.getString(request, "tabs1", "users");
String tabs2 = ParamUtil.getString(request, "tabs2", "current");

int cur = ParamUtil.getInteger(request, SearchContainer.DEFAULT_CUR_PARAM);

String redirect = ParamUtil.getString(request, "redirect");

Group group = (Group)request.getAttribute(WebKeys.GROUP);

User selUser = PortalUtil.getSelectedUser(request, false);

PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("struts_action", "/communities/edit_community_assignments");
portletURL.setParameter("tabs1", tabs1);
portletURL.setParameter("tabs2", tabs2);
portletURL.setParameter("redirect", redirect);
portletURL.setParameter("groupId", String.valueOf(group.getGroupId()));

request.setAttribute("edit_community_assignments.jsp-tabs1", tabs1);
request.setAttribute("edit_community_assignments.jsp-tabs2", tabs2);

request.setAttribute("edit_community_assignments.jsp-cur", cur);

request.setAttribute("edit_community_assignments.jsp-redirect", redirect);

request.setAttribute("edit_community_assignments.jsp-group", group);
request.setAttribute("edit_community_assignments.jsp-selUser", selUser);

request.setAttribute("edit_community_assignments.jsp-portletURL", portletURL);
%>

<c:choose>
	<c:when test="<%= selUser == null %>">
		<liferay-ui:header
			backURL="<%= redirect %>"
			title="<%= group.getDescriptiveName() %>"
		/>

		<liferay-ui:tabs
			names="users,organizations,user-groups"
			param="tabs1"
			url="<%= portletURL.toString() %>"
		/>
	</c:when>
	<c:otherwise>
		<liferay-ui:header
			backURL="<%= redirect %>"
			title="roles"
		/>
	</c:otherwise>
</c:choose>

<aui:form action="<%= portletURL.toString() %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" />
	<aui:input name="tabs1" type="hidden" value="<%= tabs1 %>" />
	<aui:input name="tabs2" type="hidden" value="<%= tabs2 %>" />
	<aui:input name="assignmentsRedirect" type="hidden" />
	<aui:input name="groupId" type="hidden" value="<%= String.valueOf(group.getGroupId()) %>" />

	<c:choose>
		<c:when test='<%= tabs1.equals("users") %>'>
			<c:choose>
				<c:when test="<%= selUser == null %>">
					<liferay-util:include page="/html/portlet/communities/edit_community_assignments_users.jsp" />
				</c:when>
				<c:otherwise>
					<liferay-util:include page="/html/portlet/communities/edit_community_assignments_users_roles.jsp" />
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:when test='<%= tabs1.equals("organizations") %>'>
			<liferay-util:include page="/html/portlet/communities/edit_community_assignments_organizations.jsp" />
		</c:when>
		<c:when test='<%= tabs1.equals("user-groups") %>'>
			<liferay-util:include page="/html/portlet/communities/edit_community_assignments_user_groups.jsp" />
		</c:when>
	</c:choose>
</aui:form>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />updateGroupOrganizations',
		function(assignmentsRedirect) {
			document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "group_organizations";
			document.<portlet:namespace />fm.<portlet:namespace />assignmentsRedirect.value = assignmentsRedirect;
			document.<portlet:namespace />fm.<portlet:namespace />addOrganizationIds.value = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, "<portlet:namespace />allRowIds");
			document.<portlet:namespace />fm.<portlet:namespace />removeOrganizationIds.value = Liferay.Util.listUncheckedExcept(document.<portlet:namespace />fm, "<portlet:namespace />allRowIds");
			submitForm(document.<portlet:namespace />fm, "<portlet:actionURL><portlet:param name="struts_action" value="/communities/edit_community_assignments" /></portlet:actionURL>");
		},
		['liferay-util-list-fields']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />updateGroupUserGroups',
		function(assignmentsRedirect) {
			document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "group_user_groups";
			document.<portlet:namespace />fm.<portlet:namespace />assignmentsRedirect.value = assignmentsRedirect;
			document.<portlet:namespace />fm.<portlet:namespace />addUserGroupIds.value = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, "<portlet:namespace />allRowIds");
			document.<portlet:namespace />fm.<portlet:namespace />removeUserGroupIds.value = Liferay.Util.listUncheckedExcept(document.<portlet:namespace />fm, "<portlet:namespace />allRowIds");
			submitForm(document.<portlet:namespace />fm, "<portlet:actionURL><portlet:param name="struts_action" value="/communities/edit_community_assignments" /></portlet:actionURL>");
		},
		['liferay-util-list-fields']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />updateGroupUsers',
		function(assignmentsRedirect) {
			document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "group_users";
			document.<portlet:namespace />fm.<portlet:namespace />assignmentsRedirect.value = assignmentsRedirect;
			document.<portlet:namespace />fm.<portlet:namespace />addUserIds.value = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, "<portlet:namespace />allRowIds");
			document.<portlet:namespace />fm.<portlet:namespace />removeUserIds.value = Liferay.Util.listUncheckedExcept(document.<portlet:namespace />fm, "<portlet:namespace />allRowIds");

			submitForm(document.<portlet:namespace />fm, "<portlet:actionURL><portlet:param name="struts_action" value="/communities/edit_community_assignments" /></portlet:actionURL>");
		},
		['liferay-util-list-fields']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />updateUserGroupRole',
		function(assignmentsRedirect) {
			document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "user_group_role";
			document.<portlet:namespace />fm.<portlet:namespace />assignmentsRedirect.value = assignmentsRedirect;
			document.<portlet:namespace />fm.<portlet:namespace />addRoleIds.value = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, "<portlet:namespace />allRowIds");
			document.<portlet:namespace />fm.<portlet:namespace />removeRoleIds.value = Liferay.Util.listUncheckedExcept(document.<portlet:namespace />fm, "<portlet:namespace />allRowIds");
			submitForm(document.<portlet:namespace />fm, "<portlet:actionURL><portlet:param name="struts_action" value="/communities/edit_community_assignments" /></portlet:actionURL>");
		},
		['liferay-util-list-fields']
	);
</aui:script>

<%
PortalUtil.addPortletBreadcrumbEntry(request, HtmlUtil.escape(group.getDescriptiveName()), null);
PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "assign-members"), currentURL);
%>