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

<%@ include file="/html/portlet/document_library/init.jsp" %>

<%
DLFileVersion fileVersion = (DLFileVersion)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_VERSION);
%>

<div class="asset-resource-info">
	<aui:a href='<%= themeDisplay.getPortalURL() + themeDisplay.getPathContext() + "/documents/" + fileVersion.getGroupId() + StringPool.SLASH + fileVersion.getFolderId() + StringPool.SLASH + HttpUtil.encodeURL(HtmlUtil.unescape(fileVersion.getTitle())) %>'>
		<img class="dl-file-icon" src="<%= themeDisplay.getPathThemeImages() %>/file_system/small/<%= fileVersion.getIcon() %>.png" /><%= HtmlUtil.escape(fileVersion.getTitle()) %>
	</aui:a>
</div>

<p class="asset-description"><%= HtmlUtil.escape(fileVersion.getDescription()) %></p>

<liferay-ui:custom-attributes-available className="<%= DLFileEntry.class.getName() %>">
	<liferay-ui:custom-attribute-list
		className="<%= DLFileEntry.class.getName() %>"
		classPK="<%= (fileVersion != null) ? fileVersion.getFileVersionId() : 0 %>"
		editable="<%= false %>"
		label="<%= true %>"
	/>
</liferay-ui:custom-attributes-available>