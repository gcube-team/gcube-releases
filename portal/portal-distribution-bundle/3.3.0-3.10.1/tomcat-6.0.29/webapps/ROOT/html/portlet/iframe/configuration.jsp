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

<%@ include file="/html/portlet/iframe/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String htmlAttributes =
	"alt=" + alt + "\n" +
	"border=" + border + "\n" +
	"bordercolor=" + bordercolor + "\n" +
	"frameborder=" + frameborder + "\n" +
	"hspace=" + hspace + "\n" +
	"longdesc=" + longdesc + "\n" +
	"scrolling=" + scrolling + "\n" +
	"vspace=" + vspace + "\n";
%>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<aui:form action="<%= configurationURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<aui:fieldset label="general">
		<aui:input cssClass="lfr-input-text-container" label="source-url" name="src" prefix='<%= relative ? "..." : StringPool.BLANK %>' type="text" value="<%= src %>" />

		<aui:input inlineLabel="left" label="relative-to-context-path" name="relative" type="checkbox" value="<%= relative %>" />
	</aui:fieldset>

	<aui:fieldset label="authentication">
		<aui:input inlineLabel="left" label="authenticate" name="auth" type="checkbox" value="<%= auth %>" />

		<div id="<portlet:namespace />authenticationOptions">
			<div class="portlet-msg-info" id="<portlet:namespace />currentLoginMsg">
				<c:choose>
					<c:when test="<%= IFrameUtil.isPasswordTokenEnabled(renderRequest) %>">
						<liferay-ui:message key="you-may-use-the-tokens-email-address-screen-name-userid-and-password" />
					</c:when>
					<c:otherwise>
						<liferay-ui:message key="you-may-use-the-tokens-email-address-screen-name-userid" />
					</c:otherwise>
				</c:choose>
			</div>

			<aui:select label="authentication-type" name="authType">
				<aui:option label="basic" selected='<%= authType.equals("basic") %>' />
				<aui:option label="form" selected='<%= authType.equals("form") %>' />
			</aui:select>

			<div id="<portlet:namespace />formAuthOptions">
				<aui:select name="formMethod">
					<aui:option label="get" selected='<%= formMethod.equals("get") %>' />
					<aui:option label="post" selected='<%= formMethod.equals("post") %>' />
				</aui:select>

				<aui:field-wrapper label="user-name">
					<table class="lfr-table">
					<tr>
						<td>
							<aui:input cssClass="lfr-input-text-container" label="field-name" name="userNameField" type="text" value="<%= userNameField %>" />
						</td>
						<td>
							<aui:input cssClass="lfr-input-text-container" label="value" name="userName" type="text" value="<%= userName %>" />
						</td>
					</tr>
					</table>
				</aui:field-wrapper>

				<aui:field-wrapper name="password">
					<table class="lfr-table">
					<tr>
						<td>
							<aui:input cssClass="lfr-input-text-container" label="field-name" name="passwordField" type="text" value="<%= passwordField %>" />
						</td>
						<td>
							<aui:input cssClass="lfr-input-text-container" label="value" name="password" type="text" value="<%= password %>" />
						</td>
					</tr>
					</table>

					<aui:input cssClass="lfr-input-text-container" name="hiddenVariables" type="text" value="<%= hiddenVariables %>" />
				</aui:field-wrapper>
			</div>

			<div id="<portlet:namespace />basicAuthOptions">
				<aui:input cssClass="lfr-input-text-container" name="userName" type="text" value="<%= userName %>" />

				<aui:input cssClass="lfr-input-text-container" name="password" type="text" value="<%= password %>" />
			</div>
		</div>
	</aui:fieldset>

	<aui:fieldset label="display-settings">
		<aui:input label="resize-automatically" name="resizeAutomatically" type="checkbox" value="<%= resizeAutomatically %>" />

		<div id="<portlet:namespace />displaySettings">
			<aui:input name="heightMaximized" type="text" value="<%= heightMaximized %>" />
			<aui:input name="heightNormal" type="text" value="<%= heightNormal %>" />
			<aui:input name="width" type="text" value="<%= width %>" />
		</div>
	</aui:fieldset>

	<aui:fieldset label="advanced">
		<aui:input cssClass="lfr-textarea-container" name="htmlAttributes" onKeyDown="Liferay.Util.checkTab(this); Liferay.Util.disableEsc();" type="textarea" value="<%= htmlAttributes %>" wrap="soft" />
	</aui:fieldset>

	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>

<aui:script>
	<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) || windowState.equals(LiferayWindowState.POP_UP) %>">
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />src);
	</c:if>

	Liferay.Util.toggleBoxes('<portlet:namespace />authCheckbox','<portlet:namespace />authenticationOptions');
	Liferay.Util.toggleBoxes('<portlet:namespace />resizeAutomaticallyCheckbox','<portlet:namespace />displaySettings', true);
	Liferay.Util.toggleSelectBox('<portlet:namespace />authType', 'form', '<portlet:namespace />formAuthOptions');
	Liferay.Util.toggleSelectBox('<portlet:namespace />authType', 'basic', '<portlet:namespace />basicAuthOptions');
</aui:script>