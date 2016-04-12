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
User selUser = (User)request.getAttribute("user.selUser");

List<Group> groups = (List<Group>)request.getAttribute("user.groups");
%>

<liferay-util:buffer var="removeGroupIcon">
	<liferay-ui:icon
		image="unlink"
		label="<%= true %>"
		message="remove"
	/>
</liferay-util:buffer>

<h3><liferay-ui:message key="communities" /></h3>

<liferay-ui:search-container
	id='<%= renderResponse.getNamespace() + "groupsSearchContainer" %>'
	headerNames="name,roles"
>
	<liferay-ui:search-container-results
		results="<%= groups %>"
		total="<%= groups.size() %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.portal.model.Group"
		escapedModel="<%= true %>"
		keyProperty="groupId"
		modelVar="group"
	>
		<liferay-ui:search-container-column-text
			name="name"
			value="<%= HtmlUtil.escape(group.getDescriptiveName()) %>"
		/>

		<liferay-ui:search-container-column-text
			buffer="buffer"
			name="roles"
		>

			<%
			List<UserGroupRole> userGroupRoles = UserGroupRoleLocalServiceUtil.getUserGroupRoles(selUser.getUserId(), group.getGroupId());

			Iterator itr = userGroupRoles.iterator();

			while (itr.hasNext()) {
				UserGroupRole userGroupRole = (UserGroupRole)itr.next();

				Role role = RoleLocalServiceUtil.getRole(userGroupRole.getRoleId());

				buffer.append(HtmlUtil.escape(role.getTitle(locale)));

				if (itr.hasNext()) {
					buffer.append(StringPool.COMMA_AND_SPACE);
				}
			}
			%>

		</liferay-ui:search-container-column-text>

		<c:if test="<%= !portletName.equals(PortletKeys.MY_ACCOUNT) %>">
			<liferay-ui:search-container-column-text>
				<a class="modify-link" data-rowId="<%= group.getGroupId() %>" href="javascript:;"><%= removeGroupIcon %></a>
			</liferay-ui:search-container-column-text>
		</c:if>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator paginate="<%= false %>" />
</liferay-ui:search-container>

<c:if test="<%= !portletName.equals(PortletKeys.MY_ACCOUNT) %>">
	<br />

	<liferay-ui:icon
		cssClass="modify-link"
		image="add"
		label="<%= true %>"
		message="select"
		url='<%= "javascript:" + renderResponse.getNamespace() + "openGroupSelector();" %>'
	/>
</c:if>

<aui:script>
	function <portlet:namespace />openGroupSelector() {
		var groupWindow = window.open('<portlet:renderURL windowState="<%= LiferayWindowState.POP_UP.toString() %>"><portlet:param name="struts_action" value="/enterprise_admin/select_community" /></portlet:renderURL>', 'group', 'directories=no,height=640,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=680');

		groupWindow.focus();
	}

	Liferay.provide(
		window,
		'<portlet:namespace />selectGroup',
		function(groupId, name) {
			var A = AUI();

			var searchContainer = Liferay.SearchContainer.get('<portlet:namespace />groupsSearchContainer');

			var rowColumns = [];

			rowColumns.push(name);
			rowColumns.push('<%= RoleConstants.COMMUNITY_MEMBER %>');
			rowColumns.push('<a class="modify-link" data-rowId="' + groupId + '" href="javascript:;"><%= UnicodeFormatter.toString(removeGroupIcon) %></a>');

			searchContainer.addRow(rowColumns, groupId);
			searchContainer.updateDataStore();

			<portlet:namespace />trackChanges();
		},
		['liferay-search-container']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />trackChanges',
		function() {
			var A = AUI();

			A.fire(
				'enterpriseAdmin:trackChanges',
				A.one('.selected .modify-link')
			);
		},
		['aui-base']
	);
</aui:script>

<aui:script use="liferay-search-container">
	var searchContainer = Liferay.SearchContainer.get('<portlet:namespace />groupsSearchContainer');

	searchContainer.get('contentBox').delegate(
		'click',
		function(event) {
			var link = event.currentTarget;
			var tr = link.ancestor('tr');

			searchContainer.deleteRow(tr, link.getAttribute('data-rowId'));

			<portlet:namespace />trackChanges();
		},
		'.modify-link'
	);
</aui:script>