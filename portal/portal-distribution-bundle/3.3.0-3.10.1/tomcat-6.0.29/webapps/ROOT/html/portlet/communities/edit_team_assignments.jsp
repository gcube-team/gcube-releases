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
String tabs1 = ParamUtil.getString(request, "tabs1", "current");

int cur = ParamUtil.getInteger(request, SearchContainer.DEFAULT_CUR_PARAM);

String redirect = ParamUtil.getString(request, "redirect");

Team team = (Team)request.getAttribute(WebKeys.TEAM);

Group group = GroupServiceUtil.getGroup(team.getGroupId());

Organization organization = null;

if (group.isOrganization()) {
	organization = OrganizationLocalServiceUtil.getOrganization(group.getOrganizationId());
}

PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("struts_action", "/communities/edit_team_assignments");
portletURL.setParameter("tabs1", tabs1);
portletURL.setParameter("redirect", redirect);
portletURL.setParameter("teamId", String.valueOf(team.getTeamId()));
%>

<liferay-ui:header
	backURL="<%= redirect %>"
	title="<%= team.getName() %>"
/>

<liferay-ui:tabs
	names="current,available"
	param="tabs1"
	url="<%= portletURL.toString() %>"
/>

<portlet:actionURL var="editAssignmentsURL">
	<portlet:param name="struts_action" value="/communities/edit_team_assignments" />
</portlet:actionURL>

<aui:form action="<%= editAssignmentsURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" />
	<aui:input name="tabs1" type="hidden" value="<%= tabs1 %>" />
	<aui:input name="assignmentsRedirect" type="hidden" />
	<aui:input name="teamId" type="hidden" value="<%= String.valueOf(team.getTeamId()) %>" />
	<aui:input name="addUserIds" type="hidden" />
	<aui:input name="removeUserIds" type="hidden" />

	<liferay-ui:search-container
		rowChecker="<%= new UserTeamChecker(renderResponse, team) %>"
		searchContainer="<%= new UserSearch(renderRequest, portletURL) %>"
	>
		<liferay-ui:search-form
			page="/html/portlet/enterprise_admin/user_search.jsp"
		/>

		<%
		UserSearchTerms searchTerms = (UserSearchTerms)searchContainer.getSearchTerms();

		LinkedHashMap userParams = new LinkedHashMap();

		if (group.isOrganization()) {
			userParams.put("usersOrgs", organization.getOrganizationId());
		}
		else {
			userParams.put("usersGroups", team.getGroupId());
		}

		if (tabs1.equals("current")) {
			userParams.put("usersTeams", team.getTeamId());
		}
		%>

		<liferay-ui:search-container-results>
			<%@ include file="/html/portlet/enterprise_admin/user_search_results.jspf" %>
		</liferay-ui:search-container-results>

		<liferay-ui:search-container-row
			className="com.liferay.portal.model.User"
			escapedModel="<%= true %>"
			keyProperty="userId"
			modelVar="user2"
		>
			<liferay-ui:search-container-column-text
				name="name"
				property="fullName"
			/>

			<liferay-ui:search-container-column-text
				name="screen-name"
				property="screenName"
			/>
		</liferay-ui:search-container-row>

		<div class="separator"><!-- --></div>

		<%
		String taglibOnClick = renderResponse.getNamespace() + "updateTeamUsers('" + portletURL.toString() + StringPool.AMPERSAND + renderResponse.getNamespace() + "cur=" + cur + "');";
		%>

		<aui:button onClick="<%= taglibOnClick %>" value="update-associations" />

		<br /><br />

		<liferay-ui:search-iterator />
	</liferay-ui:search-container>
</aui:form>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />updateTeamUsers',
		function(assignmentsRedirect) {
			document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "team_users";
			document.<portlet:namespace />fm.<portlet:namespace />assignmentsRedirect.value = assignmentsRedirect;
			document.<portlet:namespace />fm.<portlet:namespace />addUserIds.value = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, "<portlet:namespace />allRowIds");
			document.<portlet:namespace />fm.<portlet:namespace />removeUserIds.value = Liferay.Util.listUncheckedExcept(document.<portlet:namespace />fm, "<portlet:namespace />allRowIds");
			submitForm(document.<portlet:namespace />fm);
		},
		['liferay-util-list-fields']
	);
</aui:script>

<%
if (group.isOrganization()) {
	EnterpriseAdminUtil.addPortletBreadcrumbEntries(organization, request, renderResponse);
}
else {
	PortalUtil.addPortletBreadcrumbEntry(request, group.getDescriptiveName(), null);
}

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "manage-teams"), redirect);
PortalUtil.addPortletBreadcrumbEntry(request, team.getName(), null);
PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "assign-members"), currentURL);
%>