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
IGImage image = (IGImage)request.getAttribute(WebKeys.IMAGE_GALLERY_IMAGE);
%>

<img src="<%= themeDisplay.getPathImage() %>/image_gallery?img_id=<%= image.getLargeImageId() %>&amp;t=<%= ImageServletTokenUtil.getToken(image.getLargeImageId()) %>" />

<p class="asset-description"><%= HtmlUtil.escape(image.getDescription()) %></p>

<liferay-ui:custom-attributes-available className="<%= IGImage.class.getName() %>">
	<liferay-ui:custom-attribute-list
		className="<%= IGImage.class.getName() %>"
		classPK="<%= (image != null) ? image.getImageId() : 0 %>"
		editable="<%= false %>"
		label="<%= true %>"
	/>
</liferay-ui:custom-attributes-available>