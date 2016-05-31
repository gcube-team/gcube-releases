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

<%@ include file="/html/portlet/enterprise_admin/init.jsp" %>

<%
String tabs2 = ParamUtil.getString(request, "tabs2");
String tabs3 = ParamUtil.getString(request, "tabs3");

PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("struts_action", "/enterprise_admin/view");
portletURL.setParameter("tabs1", tabs1);
portletURL.setParameter("tabs2", tabs2);
portletURL.setParameter("tabs3", tabs3);

pageContext.setAttribute("portletURL", portletURL);

String portletURLString = portletURL.toString();

request.setAttribute("view.jsp-portletURL", portletURL);
request.setAttribute("view.jsp-portletURLString", portletURLString);
%>

<aui:form action="<%= portletURLString %>" method="get" name="fm">
	<liferay-portlet:renderURLParams varImpl="portletURL" />
	<aui:input name="<%= Constants.CMD %>" type="hidden" />
	<aui:input name="tabs1" type="hidden" value="<%= tabs1 %>" />
	<aui:input name="tabs2" type="hidden" value="<%= tabs2 %>" />
	<aui:input name="tabs3" type="hidden" value="<%= tabs3 %>" />
	<aui:input name="redirect" type="hidden" value="<%= portletURLString %>" />

	<c:if test="<%= showTabs1 %>">
		<liferay-util:include page="/html/portlet/enterprise_admin/tabs1.jsp" />
	</c:if>

	<c:choose>
		<c:when test='<%= tabs1.equals("users") %>'>
			<liferay-util:include page="/html/portlet/enterprise_admin/view_users.jsp" />
		</c:when>
		<c:when test='<%= tabs1.equals("organizations") %>'>
			<liferay-util:include page="/html/portlet/enterprise_admin/view_organizations.jsp" />
		</c:when>
		<c:when test='<%= tabs1.equals("user-groups") %>'>
			<liferay-util:include page="/html/portlet/enterprise_admin/view_user_groups.jsp" />
		</c:when>
		<c:when test='<%= tabs1.equals("roles") %>'>
			<liferay-util:include page="/html/portlet/enterprise_admin/view_roles.jsp" />
		</c:when>
		<c:when test='<%= tabs1.equals("password-policies") %>'>
			<liferay-util:include page="/html/portlet/enterprise_admin/view_password_policies.jsp" />
		</c:when>
		<c:when test='<%= tabs1.equals("settings") %>'>
			<liferay-util:include page="/html/portlet/enterprise_admin/edit_settings.jsp" />
		</c:when>
		<c:when test='<%= tabs1.equals("monitoring") %>'>
			<liferay-util:include page="/html/portlet/enterprise_admin/view_monitoring.jsp" />
		</c:when>
		<c:when test='<%= tabs1.equals("plugins") %>'>

			<%
			PortletURL installPluginsURL = null;

			boolean showEditPluginHREF = true;
			boolean showReindexButton = false;
			%>

			<%@ include file="/html/portlet/enterprise_admin/plugins.jspf" %>
		</c:when>
	</c:choose>
</aui:form>

<aui:script>
	function <portlet:namespace />deleteOrganization(organizationId) {
		<portlet:namespace />doDeleteOrganizationOrUserGroup('<%= Organization.class.getName() %>', organizationId);
	}

	function <portlet:namespace />deleteUserGroup(userGroupId) {
		<portlet:namespace />doDeleteOrganizationOrUserGroup('<%= UserGroup.class.getName() %>', userGroupId);
	}

	function <portlet:namespace />doDeleteOrganizationOrUserGroup(className, id) {
		var ids = id;

		<portlet:namespace />getUsersCount(
			className, ids, false,
			function(event, id, obj) {
				var responseData = this.get('responseData');
				var count = parseInt(responseData);

				if (count > 0) {
					<portlet:namespace />getUsersCount(
						className, ids, true,
						function(event, id, obj) {
							responseData = this.get('responseData')
							count = parseInt(responseData);

							if (count > 0) {
								if (confirm('<%= UnicodeLanguageUtil.get(pageContext, "are-you-sure-you-want-to-delete-this") %>')) {
									<portlet:namespace />doDeleteOrganizations(ids);
								}
							}
							else {
								var message = null;

								if (id && (id.split(",").length > 1)) {
									if (className == '<%= Organization.class.getName() %>') {
										message = '<%= UnicodeLanguageUtil.get(pageContext, "one-or-more-organizations-are-associated-with-deactivated-users.-do-you-want-to-proceed-with-deleting-the-selected-organizations-by-automatically-unassociating-the-deactivated-users") %>';
									}
									else {
										message = '<%= UnicodeLanguageUtil.get(pageContext, "one-or-more-user-groups-are-associated-with-deactivated-users.-do-you-want-to-proceed-with-deleting-the-selected-user-groups-by-automatically-unassociating-the-deactivated-users") %>';
									}
								}
								else {
									if (className == '<%= Organization.class.getName() %>') {
										message = '<%= UnicodeLanguageUtil.get(pageContext, "the-selected-organization-is-associated-with-deactivated-users.-do-you-want-to-proceed-with-deleting-the-selected-organization-by-automatically-unassociating-the-deactivated-users") %>';
									}
									else {
										message = '<%= UnicodeLanguageUtil.get(pageContext, "the-selected-user-group-is-associated-with-deactivated-users.-do-you-want-to-proceed-with-deleting-the-selected-user-group-by-automatically-unassociating-the-deactivated-users") %>';
									}
								}

								if (confirm(message)) {
									if (className == '<%= Organization.class.getName() %>') {
										<portlet:namespace />doDeleteOrganizations(ids);
									}
									else {
										<portlet:namespace />doDeleteUserGroups(ids);
									}
								}
							}
						}
					);
				}
				else {
					if (confirm('<%= UnicodeLanguageUtil.get(pageContext, "are-you-sure-you-want-to-delete-this") %>')) {
						if (className == '<%= Organization.class.getName() %>') {
							<portlet:namespace />doDeleteOrganizations(ids);
						}
						else {
							<portlet:namespace />doDeleteUserGroups(ids);
						}
					}
				}
			}
		);
	}

	function <portlet:namespace />doDeleteOrganizations(organizationIds) {
		document.<portlet:namespace />fm.method = "post";
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.DELETE %>";
		document.<portlet:namespace />fm.<portlet:namespace />redirect.value = document.<portlet:namespace />fm.<portlet:namespace />organizationsRedirect.value;
		document.<portlet:namespace />fm.<portlet:namespace />deleteOrganizationIds.value = organizationIds;
		submitForm(document.<portlet:namespace />fm, "<portlet:actionURL><portlet:param name="struts_action" value="/enterprise_admin/edit_organization" /></portlet:actionURL>");
	}

	function <portlet:namespace />doDeleteUserGroups(userGroupIds) {
		document.<portlet:namespace />fm.method = "post";
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.DELETE %>";
		document.<portlet:namespace />fm.<portlet:namespace />redirect.value = document.<portlet:namespace />fm.<portlet:namespace />userGroupsRedirect.value;
		document.<portlet:namespace />fm.<portlet:namespace />deleteUserGroupIds.value = userGroupIds;
		submitForm(document.<portlet:namespace />fm, "<portlet:actionURL><portlet:param name="struts_action" value="/enterprise_admin/edit_user_group" /></portlet:actionURL>");
	}

	function <portlet:namespace />exportUsers() {
		document.<portlet:namespace />fm.method = "post";
		submitForm(document.<portlet:namespace />fm, "<portlet:actionURL><portlet:param name="struts_action" value="/enterprise_admin/export_users" /></portlet:actionURL>&etag=0&strip=0&compress=0");
	}

	function <portlet:namespace />saveCompany() {
		document.<portlet:namespace />fm.method = "post";
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.UPDATE %>";

		var redirect = "<portlet:renderURL><portlet:param name="struts_action" value="/enterprise_admin/view" /><portlet:param name="tabs1" value="<%= tabs1 %>" /><portlet:param name="tabs2" value="<%= tabs2 %>" /><portlet:param name="tabs3" value="<%= tabs3 %>" /></portlet:renderURL>";

		if (location.hash) {
			redirect += location.hash.replace('#_LFR_FN_', '&<portlet:namespace />historyKey=');
		}

		document.<portlet:namespace />fm.<portlet:namespace />redirect.value = redirect;

		<portlet:namespace />saveLdap();
		<portlet:namespace />saveEmails();

		submitForm(document.<portlet:namespace />fm, "<portlet:actionURL><portlet:param name="struts_action" value="/enterprise_admin/edit_company" /></portlet:actionURL>");
	}

	Liferay.provide(
		window,
		'<portlet:namespace />deleteOrganizations',
		function() {
			var organizationIds = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, "<portlet:namespace />allRowIds");

			if (!organizationIds) {
				return;
			}

			<portlet:namespace />doDeleteOrganizationOrUserGroup('<%= Organization.class.getName() %>', organizationIds);
		},
		['liferay-util-list-fields']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />deleteUserGroups',
		function() {
			var userGroupIds = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, "<portlet:namespace />allRowIds");

			if (!userGroupIds) {
				return;
			}

			<portlet:namespace />doDeleteOrganizationOrUserGroup('<%= UserGroup.class.getName() %>', userGroupIds);
		},
		['liferay-util-list-fields']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />deleteUsers',
		function(cmd) {
			var deleteUsers = true;

			var deleteUserIds = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, "<portlet:namespace />allRowIds");

			if (!deleteUserIds) {
				deleteUsers = false;
			}
			else if (cmd == "<%= Constants.DEACTIVATE %>") {
				if (!confirm('<%= UnicodeLanguageUtil.get(pageContext, "are-you-sure-you-want-to-deactivate-the-selected-users") %>')) {
					deleteUsers = false;
				}
			}
			else if (cmd == "<%= Constants.DELETE %>") {
				if (!confirm('<%= UnicodeLanguageUtil.get(pageContext, "are-you-sure-you-want-to-permanently-delete-the-selected-users") %>')) {
					deleteUsers = false;
				}
			}

			if (deleteUsers) {
				document.<portlet:namespace />fm.method = "post";
				document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = cmd;
				document.<portlet:namespace />fm.<portlet:namespace />redirect.value = document.<portlet:namespace />fm.<portlet:namespace />usersRedirect.value;
				document.<portlet:namespace />fm.<portlet:namespace />deleteUserIds.value = deleteUserIds;
				submitForm(document.<portlet:namespace />fm, "<portlet:actionURL><portlet:param name="struts_action" value="/enterprise_admin/edit_user" /></portlet:actionURL>");
			}
		},
		['liferay-util-list-fields']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />getUsersCount',
		function(className, ids, active, callback) {
			var A = AUI();

			A.io.request(
				'<%= themeDisplay.getPathMain() %>/enterprise_admin/get_users_count',
				{
					data: {
						active: active,
						className: className,
						ids: ids
					},
					on: {
						success: callback
					}
				}
			);
		},
		['aui-io']
	);
</aui:script>

<%!
private static final long[] _DURATIONS = {300, 600, 1800, 3600, 7200, 10800, 21600};
%>