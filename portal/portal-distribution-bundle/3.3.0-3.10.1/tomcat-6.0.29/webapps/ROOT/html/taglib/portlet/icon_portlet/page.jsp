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
Portlet portlet = (Portlet)request.getAttribute("liferay-portlet:icon_portlet:portlet");

boolean showPortletIcon = true;
String message = null;
String src = null;

if (portlet != null) {
	message = PortalUtil.getPortletTitle(portlet, application, locale);
	src = portlet.getContextPath() + portlet.getIcon();
}
else {
	showPortletIcon = portletDisplay.isShowPortletIcon();
	message = portletDisplay.getTitle();
	src = portletDisplay.getURLPortlet();
}
%>

<c:if test="<%= showPortletIcon %>">
	<liferay-ui:icon
		message="<%= message %>"
		src="<%= src %>"
	/>
</c:if>