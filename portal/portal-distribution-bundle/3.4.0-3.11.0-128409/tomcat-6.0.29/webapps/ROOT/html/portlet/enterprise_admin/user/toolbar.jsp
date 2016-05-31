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
String toolbarItem = ParamUtil.getString(request, "toolbarItem", "view-all");
%>

<div class="lfr-portlet-toolbar">
	<portlet:renderURL var="viewUsersURL">
		<portlet:param name="struts_action" value="/enterprise_admin/view" />
	</portlet:renderURL>

	<span class="lfr-toolbar-button view-button <%= toolbarItem.equals("view-all") ? "current" : StringPool.BLANK %>">
		<a href="<%= viewUsersURL %>"><liferay-ui:message key="view-all" /></a>
	</span>

	<c:if test="<%= PortalPermissionUtil.contains(permissionChecker, ActionKeys.ADD_USER) %>">
		<portlet:renderURL var="addUserURL">
			<portlet:param name="struts_action" value="/enterprise_admin/edit_user" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
		</portlet:renderURL>

		<span class="lfr-toolbar-button add-button <%= toolbarItem.equals("add") ? "current" : StringPool.BLANK %>"><a href="<%= addUserURL %>"><liferay-ui:message key="add" /></a></span>
	</c:if>

	<c:if test="<%= RoleLocalServiceUtil.hasUserRole(user.getUserId(), user.getCompanyId(), RoleConstants.ADMINISTRATOR, true) %>">
		<liferay-portlet:renderURL var="expandoURL" portletName="<%= PortletKeys.EXPANDO %>">
			<portlet:param name="struts_action" value="/expando/view_attributes" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="modelResource" value="<%= User.class.getName() %>" />
		</liferay-portlet:renderURL>
	</c:if>

	<c:if test="<%= PortalPermissionUtil.contains(permissionChecker, ActionKeys.EXPORT_USER) %>">
		<portlet:renderURL var="addUserURL">
			<portlet:param name="struts_action" value="/enterprise_admin/edit_user" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
		</portlet:renderURL>

		<span class="lfr-toolbar-button export-button"><a href="javascript:<portlet:namespace />exportUsers();"><liferay-ui:message key="export" /></a></span>
	</c:if>
</div>