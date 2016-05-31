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

<%@ include file="/html/portlet/web_proxy/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");
%>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<aui:form action="<%= configurationURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<aui:fieldset>
		<aui:input cssClass="lfr-input-text-container" label="url" name="initUrl" value="<%= initUrl %>" />

		<aui:input cssClass="lfr-input-text-container" label='<%= LanguageUtil.get(pageContext, "scope") + " (" + LanguageUtil.get(pageContext, "regex") + ")" %>' name="scope" value="<%= scope %>" />

		<aui:input cssClass="lfr-input-text-container" name="proxyHost" value="<%= proxyHost %>" />

		<aui:input cssClass="lfr-input-text-container" name="proxyPort" value="<%= proxyPort %>" />

		<aui:select name="proxyAuthentication">
			<aui:option label="none" selected='<%= proxyAuthentication.equals("none") %>' />
			<aui:option label="basic" selected='<%= proxyAuthentication.equals("basic") %>' />
			<aui:option label="ntlm" selected='<%= proxyAuthentication.equals("ntlm") %>' />
		</aui:select>

		<aui:input cssClass="lfr-input-text-container" name="proxyAuthenticationUsername" value="<%= proxyAuthenticationUsername %>" />

		<aui:input cssClass="lfr-input-text-container" name="proxyAuthenticationPassword" value="<%= proxyAuthenticationPassword %>" />

		<aui:input cssClass="lfr-input-text-container" name="proxyAuthenticationHost" value="<%= proxyAuthenticationHost %>" />

		<aui:input cssClass="lfr-input-text-container" name="proxyAuthenticationDomain" value="<%= proxyAuthenticationDomain %>" />

		<aui:input cssClass="lfr-textarea-container" name="stylesheet" onKeyDown="Liferay.Util.checkTab(this); Liferay.Util.disableEsc();" type="textarea" value="<%= stylesheet %>" wrap="soft" />
	</aui:fieldset>

	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>

<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) || windowState.equals(LiferayWindowState.POP_UP) %>">
	<aui:script>
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />initUrl);
	</aui:script>
</c:if>