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

<%@ include file="/html/taglib/ui/user_display/init.jsp" %>

<%
long portraitId = 0;
String tokenId = StringPool.BLANK;

if (userDisplay != null) {
	portraitId = userDisplay.getPortraitId();
	tokenId = ImageServletTokenUtil.getToken(userDisplay.getPortraitId());
}

if (Validator.isNull(url) && (userDisplay != null)) {
	url = userDisplay.getDisplayURL(themeDisplay);
}
%>

<div class="taglib-user-display">
	<c:if test="<%= displayStyle == 1 %>">
		<table class="lfr-table">
		<tr>
			<td class="lfr-top">
	</c:if>

	<c:if test="<%= displayStyle == 2 %>">
		<%--<div style="white-space: nowrap;">--%>
		<div>
	</c:if>

	<div class="user-profile-image">

		<%
		String taglibAlt = (userDisplay != null) ? HtmlUtil.escapeAttribute(userDisplay.getFullName()) : LanguageUtil.get(pageContext, "generic-portrait");
		String taglibSrc = themeDisplay.getPathImage() + "/user_" + ((userDisplay != null) && userDisplay.isFemale() ? "female" : "male") + "_portrait?img_id=" + portraitId + "&t=" + tokenId;
		%>

		<aui:a href="<%= url %>">
			<img alt="<%= taglibAlt %>" class="avatar" src="<%= HtmlUtil.escape(taglibSrc) %>" width="65" />
		</aui:a>
	</div>

	<c:if test="<%= displayStyle == 1 %>">
		</td>
		<td class="lfr-top">
	</c:if>

	<c:if test="<%= displayStyle == 2 %>">
	</c:if>

	<div class="user-details">
		<c:choose>
			<c:when test="<%= userDisplay != null %>">
				<aui:a cssClass="user-name" href="<%= url %>">
					<%= HtmlUtil.escape(userDisplay.getFullName()) %>
				</aui:a>
			</c:when>
			<c:otherwise>
				<%= HtmlUtil.escape(userName) %>
			</c:otherwise>
		</c:choose>