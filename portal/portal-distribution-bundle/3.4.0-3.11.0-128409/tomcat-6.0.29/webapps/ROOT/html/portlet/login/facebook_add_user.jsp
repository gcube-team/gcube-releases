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

<%@ include file="/html/portlet/login/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");
%>

<div>
	<liferay-ui:message key="no-user-was-found-with-your-facebook-credentials.-would-you-like-to-import-this-user" />
</div>

<portlet:actionURL var="addUserURL">
	<portlet:param name="saveLastPath" value="0" />
	<portlet:param name="struts_action" value="/login/facebook_connect" />
</portlet:actionURL>

<aui:form action="<%= addUserURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.ADD %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<aui:layout>
		<aui:column columnWidth="10" first="true">
			<img alt="facebook user image" src="<%= FacebookConnectUtil.getProfileImageURL(renderRequest) %>" />
		</aui:column>
		<aui:column columnWidth="90" last="true">
			<aui:button-row>
				<aui:button type="submit" value="add-account" />
			</aui:button-row>
		</aui:column>
	</aui:layout>
</aui:form>

<liferay-util:include page="/html/portlet/login/navigation.jsp" />