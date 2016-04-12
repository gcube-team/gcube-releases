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

<%@ include file="/html/taglib/init.jsp" %>

<%
String icon = (String)request.getAttribute("liferay-ui:icon-menu:icon");
String id = (String)request.getAttribute("liferay-ui:icon-menu:id");
String message = (String)request.getAttribute("liferay-ui:icon-menu:message");
String align = (String)request.getAttribute("liferay-ui:icon-menu:align");
String cssClass = GetterUtil.getString((String)request.getAttribute("liferay-ui:icon-menu:cssClass"));
boolean showArrow = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:icon-menu:showArrow"));
boolean showExpanded = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:icon-menu:showExpanded"));
%>

<c:choose>
	<c:when test="<%= showExpanded %>">
		<div class="lfr-component lfr-menu-list lfr-menu-expanded <%= align %> <%= cssClass %>" id="<%= id %>menu">
	</c:when>
	<c:otherwise >
		<ul class='lfr-component lfr-actions <%= align %> <%= cssClass %> <%= showArrow ? "show-arrow" : StringPool.BLANK %>'>

			<li class="lfr-trigger">
				<strong>
					<a class="nobr" href="javascript:;">
						<c:if test="<%= Validator.isNotNull(icon) %>">
							<img alt="" src="<%= icon %>" />
						</c:if>

						<liferay-ui:message key="<%= message %>" />
					</a>
				</strong>
	</c:otherwise>
</c:choose>

<ul>