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

<%@ include file="/html/portlet/image_gallery/init.jsp" %>

<%
AssetRenderer assetRenderer = (AssetRenderer)request.getAttribute(WebKeys.ASSET_RENDERER);
int abstractLength = (Integer)request.getAttribute(WebKeys.ASSET_PUBLISHER_ABSTRACT_LENGTH);

IGImage image = (IGImage)request.getAttribute(WebKeys.IMAGE_GALLERY_IMAGE);

Image smallImage = ImageLocalServiceUtil.getImage(image.getSmallImageId());
%>

<c:if test="<%= smallImage != null %>">
	<aui:a href="<%= assetRenderer.getURLViewInContext((LiferayPortletRequest)renderRequest, (LiferayPortletResponse)renderResponse, StringPool.BLANK) %>">
		<img alt="<liferay-ui:message key="<%= assetRenderer.getViewInContextMessage() %>" />" class="asset-small-image" src="<%= themeDisplay.getPathImage() %>/image_gallery?img_id=<%= smallImage.getImageId() %>" style="text-align: left;" />
	</aui:a>
</c:if>

<p class="asset-description">
	<%= HtmlUtil.escape(StringUtil.shorten(image.getDescription(), abstractLength)) %>
</p>