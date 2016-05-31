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
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

IGImage image = (IGImage)row.getObject();
%>

<table class="lfr-table">
<tr>
	<td>
		<aui:a href='<%= themeDisplay.getPathImage() + "/image_gallery?img_id=" + image.getLargeImageId() + "&t=" + ImageServletTokenUtil.getToken(image.getLargeImageId()) %>' target="_blank">
			<img border="1" src="<%= themeDisplay.getPathImage() %>/image_gallery?img_id=<%= image.getSmallImageId() %>&t=<%= ImageServletTokenUtil.getToken(image.getSmallImageId()) %>" title="<%= image.getDescription() %>" />
		</aui:a>
	</td>
</tr>

<c:if test="<%= (image.getCustom1ImageId() > 0) || (image.getCustom2ImageId() > 0) %>">
	<tr>
		<td>
			<aui:a href='<%= themeDisplay.getPathImage() + "/image_gallery?img_id=" + image.getLargeImageId() + "&t=" + ImageServletTokenUtil.getToken(image.getLargeImageId()) %>' target="_blank">
				<liferay-ui:message key="original" />
			</aui:a>

			<c:if test="<%= image.getCustom1ImageId() > 0 %>">
				|

				<aui:a href='<%= themeDisplay.getPathImage() + "/image_gallery?img_id=" + image.getCustom1ImageId() + "&t=" + ImageServletTokenUtil.getToken(image.getCustom1ImageId()) %>' target="_blank">
					<liferay-ui:message key="size" /> 1
				</aui:a>
			</c:if>

			<c:if test="<%= image.getCustom2ImageId() > 0 %>">
				|

				<aui:a href='<%= themeDisplay.getPathImage() + "/image_gallery?img_id=" + image.getCustom2ImageId() + "&t=" + ImageServletTokenUtil.getToken(image.getCustom2ImageId()) %>' target="_blank">
					<liferay-ui:message key="size" /> 2
				</aui:a>
			</c:if>
		</td>
	</tr>
</c:if>

</table>