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

<%@ include file="/html/portlet/enterprise_admin/init.jsp" %>

<%
String openSsoLoginUrl = ParamUtil.getString(request, "openSsoLoginUrl");
String openSsoLogoutUrl = ParamUtil.getString(request, "openSsoLogoutUrl");
String openSsoServiceUrl = ParamUtil.getString(request, "openSsoServiceUrl");
String openSsoScreenNameAttr = ParamUtil.getString(request, "openSsoScreenNameAttr");
String openSsoEmailAddressAttr = ParamUtil.getString(request, "openSsoEmailAddressAttr");
String openSsoFirstNameAttr = ParamUtil.getString(request, "openSsoFirstNameAttr");
String openSsoLastNameAttr = ParamUtil.getString(request, "openSsoLastNameAttr");

List<String> urls = new ArrayList<String>();

urls.add(openSsoLoginUrl);
urls.add(openSsoLogoutUrl);
urls.add(openSsoServiceUrl);
%>

<c:choose>
	<c:when test="<%= !OpenSSOUtil.isValidUrls(urls.toArray(new String[urls.size()])) %>">
		<liferay-ui:message key="liferay-has-failed-to-connect-to-the-opensso-server" />
	</c:when>
	<c:when test="<%= !OpenSSOUtil.isValidServiceUrl(openSsoServiceUrl) %>">
		<liferay-ui:message key="liferay-has-failed-to-connect-to-the-opensso-services" />
	</c:when>
	<c:when test="<%= Validator.isNull(openSsoScreenNameAttr) || Validator.isNull(openSsoEmailAddressAttr) || Validator.isNull(openSsoFirstNameAttr) || Validator.isNull(openSsoLastNameAttr) %>">
		<liferay-ui:message key="please-map-each-of-the-user-properties-screen-name,-email-address,-first-name,-and-last-name-to-an-opensso-attribute" />
	</c:when>
	<c:otherwise>
		<liferay-ui:message key="liferay-has-successfully-connected-to-the-opensso-server" />
	</c:otherwise>
</c:choose>