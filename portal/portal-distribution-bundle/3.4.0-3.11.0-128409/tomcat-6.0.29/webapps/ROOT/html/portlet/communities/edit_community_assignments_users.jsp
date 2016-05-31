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
String tabs2 = (String)request.getAttribute("edit_community_assignments.jsp-tabs2");

int cur = (Integer)request.getAttribute("edit_community_assignments.jsp-cur");

Group group = (Group)request.getAttribute("edit_community_assignments.jsp-group");

PortletURL portletURL = (PortletURL)request.getAttribute("edit_community_assignments.jsp-portletURL");
%>

<aui:input name="addUserIds" type="hidden" />
<aui:input name="removeUserIds" type="hidden" />

<liferay-ui:tabs
	names="current,available"
	param="tabs2"
	url="<%= portletURL.toString() %>"
/>

<liferay-ui:search-container
	rowChecker="<%= new UserGroupChecker(renderResponse, group) %>"
	searchContainer="<%= new UserSearch(renderRequest, portletURL) %>"
>
	<liferay-ui:search-form
		page="/html/portlet/enterprise_admin/user_search.jsp"
	/>

	<%
	UserSearchTerms searchTerms = (UserSearchTerms)searchContainer.getSearchTerms();

	LinkedHashMap userParams = new LinkedHashMap();

	if (tabs2.equals("current")) {
		userParams.put("usersGroups", new Long(group.getGroupId()));
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
		<liferay-ui:search-container-row-parameter
			name="group"
			value="<%= group %>"
		/>

		<liferay-ui:search-container-column-text
			name="name"
			property="fullName"
		/>

		<liferay-ui:search-container-column-text
			name="screen-name"
			orderable="<%= true %>"
			property="screenName"
		/>

		<c:if test='<%= tabs2.equals("current") %>'>
			<liferay-ui:search-container-column-text
				buffer="buffer"
				name="community-roles"
			>

				<%
				List userGroupRoles = UserGroupRoleLocalServiceUtil.getUserGroupRoles(user2.getUserId(), group.getGroupId());

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

			<liferay-ui:search-container-column-jsp
				align="right"
				path="/html/portlet/communities/user_action.jsp"
			/>
		</c:if>
	</liferay-ui:search-container-row>

	<div class="separator"><!-- --></div>

	<%
	String taglibOnClick = renderResponse.getNamespace() + "updateGroupUsers('" + portletURL.toString() + StringPool.AMPERSAND + renderResponse.getNamespace() + "cur=" + cur + "');";
	%>

	<aui:button onClick="<%= taglibOnClick %>" value="update-associations" />

	<br /><br />

	<liferay-ui:search-iterator />
</liferay-ui:search-container>