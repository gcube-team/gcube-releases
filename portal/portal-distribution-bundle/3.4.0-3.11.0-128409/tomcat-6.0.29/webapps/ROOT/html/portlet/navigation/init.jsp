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

<%@ include file="/html/portlet/init.jsp" %>

<%
PortletPreferences preferences = renderRequest.getPreferences();

String portletResource = ParamUtil.getString(request, "portletResource");

if (Validator.isNotNull(portletResource)) {
	preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletResource);
}

String bulletStyle = GetterUtil.getString(preferences.getValue("bullet-style", null), "1");
String displayStyle = GetterUtil.getString(preferences.getValue("display-style", null), "1");

String headerType = GetterUtil.getString(preferences.getValue("header-type", null), "root-layout");

String rootLayoutType = GetterUtil.getString(preferences.getValue("root-layout-type", null), "absolute");
int rootLayoutLevel = GetterUtil.getInteger(preferences.getValue("root-layout-level", null), 1);

String includedLayouts = GetterUtil.getString(preferences.getValue("included-layouts", null), "current");

boolean nestedChildren = GetterUtil.getBoolean(preferences.getValue("nested-children", null), true);
%>