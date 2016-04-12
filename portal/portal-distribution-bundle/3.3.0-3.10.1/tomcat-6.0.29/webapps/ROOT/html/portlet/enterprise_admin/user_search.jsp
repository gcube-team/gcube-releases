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
UserSearch searchContainer = (UserSearch)request.getAttribute("liferay-ui:search:searchContainer");

UserDisplayTerms displayTerms = (UserDisplayTerms)searchContainer.getDisplayTerms();
%>

<liferay-ui:search-toggle
	id="toggle_id_enterprise_admin_user_search"
	displayTerms="<%= displayTerms %>"
	buttonLabel="search"
>
	<aui:fieldset>
		<aui:column>
			<aui:input name="<%= displayTerms.FIRST_NAME %>" size="20" value="<%= displayTerms.getFirstName() %>" />

			<aui:input name="<%= displayTerms.SCREEN_NAME %>" size="20" value="<%= displayTerms.getScreenName() %>" />
		</aui:column>

		<aui:column>
			<aui:input name="<%= displayTerms.MIDDLE_NAME %>" size="20" value="<%= displayTerms.getMiddleName() %>" />

			<aui:input name="<%= displayTerms.EMAIL_ADDRESS %>" size="20" value="<%= displayTerms.getEmailAddress() %>" />
		</aui:column>

		<aui:column>
			<aui:input name="<%= displayTerms.LAST_NAME %>" size="20" value="<%= displayTerms.getLastName() %>" />

			<c:if test="<%= showActiveUserSelect %>">
				<aui:select name="<%= displayTerms.ACTIVE %>">
					<aui:option selected="<%= (!displayTerms.hasActive()) %>" value="" />
					<aui:option label="yes" selected="<%= (displayTerms.hasActive()) && displayTerms.isActive() %>" value="1" />
					<aui:option label="no" selected="<%= (displayTerms.hasActive()) && !displayTerms.isActive() %>" value="0" />
				</aui:select>
			</c:if>
		</aui:column>
	</aui:fieldset>
</liferay-ui:search-toggle>

<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
	<aui:script>
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace /><%= displayTerms.FIRST_NAME %>);
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace /><%= displayTerms.KEYWORDS %>);
	</aui:script>
</c:if>