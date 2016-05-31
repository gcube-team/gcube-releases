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
PortletURL portletURL = (PortletURL)request.getAttribute("view.jsp-portletURL");
%>

<liferay-ui:error exception="<%= RequiredUserGroupException.class %>" message="you-cannot-delete-user-groups-that-have-users" />

<liferay-util:include page="/html/portlet/enterprise_admin/user_group/toolbar.jsp">
	<liferay-util:param name="toolbarItem" value="view-all" />
</liferay-util:include>

<%
RowChecker rowChecker = null;

if (PortalPermissionUtil.contains(permissionChecker, ActionKeys.ADD_USER_GROUP)) {
	rowChecker = new RowChecker(renderResponse);
}
%>

<liferay-ui:search-container
	rowChecker="<%= rowChecker %>"
	searchContainer="<%= new UserGroupSearch(renderRequest, portletURL) %>"
>
	<aui:input name="deleteUserGroupIds" type="hidden" />
	<aui:input name="userGroupsRedirect" type="hidden" value="<%= portletURL.toString() %>" />

	<liferay-ui:search-form
		page="/html/portlet/enterprise_admin/user_group_search.jsp"
	/>

	<%
	UserGroupSearchTerms searchTerms = (UserGroupSearchTerms)searchContainer.getSearchTerms();
	%>

	<liferay-ui:search-container-results
		results="<%= UserGroupLocalServiceUtil.search(company.getCompanyId(), searchTerms.getName(), searchTerms.getDescription(), null, searchContainer.getStart(), searchContainer.getEnd(), searchContainer.getOrderByComparator()) %>"
		total="<%= UserGroupLocalServiceUtil.searchCount(company.getCompanyId(), searchTerms.getName(), searchTerms.getDescription(), null) %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.portal.model.UserGroup"
		escapedModel="<%= true %>"
		keyProperty="userGroupId"
		modelVar="userGroup"
	>
		<portlet:renderURL var="rowURL">
			<portlet:param name="struts_action" value="/enterprise_admin/edit_user_group" />
			<portlet:param name="redirect" value="<%= searchContainer.getIteratorURL().toString() %>" />
			<portlet:param name="userGroupId" value="<%= String.valueOf(userGroup.getUserGroupId()) %>" />
		</portlet:renderURL>

		<liferay-ui:search-container-column-text
			href="<%= rowURL %>"
			name="name"
			orderable="<%= true %>"
			property="name"
		/>

		<liferay-ui:search-container-column-text
			href="<%= rowURL %>"
			name="description"
			orderable="<%= true %>"
			property="description"
		/>

		<liferay-ui:search-container-column-jsp
			align="right"
			path="/html/portlet/enterprise_admin/user_group_action.jsp"
		/>
	</liferay-ui:search-container-row>

	<div class="separator"><!-- --></div>

	<c:if test="<%= PortalPermissionUtil.contains(permissionChecker, ActionKeys.ADD_USER_GROUP) %>">
		<aui:button onClick='<%= renderResponse.getNamespace() + "deleteUserGroups();" %>' value="delete" />

		<br /><br />
	</c:if>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>