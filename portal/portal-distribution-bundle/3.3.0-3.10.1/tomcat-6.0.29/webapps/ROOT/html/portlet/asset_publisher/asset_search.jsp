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

<%@ include file="/html/portlet/asset_publisher/init.jsp" %>

<%
AssetSearch searchContainer = (AssetSearch)request.getAttribute("liferay-ui:search:searchContainer");

AssetDisplayTerms displayTerms = (AssetDisplayTerms)searchContainer.getDisplayTerms();
%>

<liferay-ui:search-toggle
	id="toggle_id_asset_search"
	displayTerms="<%= displayTerms %>"
	buttonLabel="search"
>
	<aui:fieldset>
		<aui:column>
			<aui:input name="<%= displayTerms.TITLE %>" size="20" type="text" value="<%= displayTerms.getTitle() %>" />
		</aui:column>

		<aui:column>
			<aui:input name="<%= displayTerms.DESCRIPTION %>" size="20" type="text" value="<%= displayTerms.getDescription() %>" />
		</aui:column>

		<aui:column>
			<aui:input name="<%= displayTerms.USER_NAME %>" size="20" type="text" value="<%= displayTerms.getUserName() %>" />
		</aui:column>

		<aui:column>
			<aui:select label="my-places" name="<%= displayTerms.GROUP_ID %>" showEmptyOption="<%= true %>">
				<c:if test="<%= themeDisplay.getCompanyGroupId() != scopeGroupId %>">
					<aui:option label="global" selected="<%= displayTerms.getGroupId() == themeDisplay.getCompanyGroupId() %>" value="<%= themeDisplay.getCompanyGroupId() %>" />
				</c:if>

				<aui:option label="<%= themeDisplay.getScopeGroupName() %>" selected="<%= displayTerms.getGroupId() == scopeGroupId %>" value="<%= scopeGroupId %>" />
			</aui:select>
		</aui:column>
	</aui:fieldset>
</liferay-ui:search-toggle>