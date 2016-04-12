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
int abstractLength = (Integer)request.getAttribute(WebKeys.ASSET_PUBLISHER_ABSTRACT_LENGTH);
AssetRenderer assetRenderer = (AssetRenderer)request.getAttribute(WebKeys.ASSET_RENDERER);

DLFileVersion fileVersion = (DLFileVersion)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_VERSION);
%>

<c:if test="<%= fileVersion.isApproved() %>">
	<div class="asset-resource-info">
		<liferay-ui:icon
			image='<%= "../file_system/small/" + fileVersion.getIcon() %>'
			label="<%= true %>"
			message="<%= HtmlUtil.escape(fileVersion.getTitle()) %>"
			url='<%= themeDisplay.getPortalURL() + themeDisplay.getPathContext() + "/documents/" + fileVersion.getGroupId() + StringPool.SLASH + fileVersion.getFolderId() + StringPool.SLASH + HttpUtil.encodeURL(HtmlUtil.unescape(fileVersion.getTitle())) %>'
		/>
	</div>
</c:if>

<p class="asset-description">
	<%= HtmlUtil.escape(StringUtil.shorten(fileVersion.getDescription(), abstractLength)) %>
</p>