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
String tabs1 = ParamUtil.getString(request, "tabs1", "general");
String tabs2 = ParamUtil.getString(request, "tabs2", "general");

String redirect = ParamUtil.getString(request, "redirect");
%>

<liferay-portlet:renderURL var="portletURL" portletConfiguration="true">
	<portlet:param name="tabs1" value="<%= tabs1 %>" />
	<portlet:param name="tabs2" value="<%= tabs2 %>" />
	<portlet:param name="redirect" value="<%= redirect %>" />
</liferay-portlet:renderURL>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<aui:form action="<%= configurationURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="tabs1" type="hidden" value="<%= tabs1 %>" />
	<aui:input name="tabs2" type="hidden" value="<%= tabs2 %>" />

	<liferay-ui:tabs
		names="general,email-notifications"
		param="tabs1"
		url="<%= portletURL %>"
	/>

	<c:choose>
		<c:when test='<%= tabs1.equals("email-notifications") %>'>

			<%
			String currentLanguageId = LanguageUtil.getLanguageId(request);
			Locale defaultLocale = LocaleUtil.getDefault();
			String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

			Locale[] locales = LanguageUtil.getAvailableLocales();

			String emailFromName = PrefsParamUtil.getString(preferences, request, "emailFromName");
			String emailFromAddress = PrefsParamUtil.getString(preferences, request, "emailFromAddress");

			String editorParam = StringPool.BLANK;
			String editorContent = StringPool.BLANK;
			%>

			<liferay-ui:tabs
				names="general,password-changed-notification,password-reset-notification"
				param="tabs2"
				url="<%= portletURL %>"
			/>

			<div class="portlet-msg-info">
				<liferay-ui:message key="enter-custom-values-or-leave-it-blank-to-use-the-default-portal-settings" />
			</div>

			<c:choose>
				<c:when test='<%= tabs2.equals("password-changed-notification") || tabs2.equals("password-reset-notification") %>'>

					<%
					String emailParam = "emailPasswordSent";

					if (tabs2.equals("password-reset-notification")) {
						emailParam = "emailPasswordReset";
					}

					String emailSubject = PrefsParamUtil.getString(preferences, request, emailParam + "Subject_" + currentLanguageId, StringPool.BLANK);
					String emailBody = PrefsParamUtil.getString(preferences, request, emailParam + "Body_" + currentLanguageId, StringPool.BLANK);

					editorParam = emailParam + "Body_" + currentLanguageId;
					editorContent = emailBody;
					%>

					<aui:fieldset>
						<aui:select label="language" name="languageId" onChange='<%= renderResponse.getNamespace() + "updateLanguage(this);" %>'>

							<%
							for (int i = 0; i < locales.length; i++) {
								String optionStyle = StringPool.BLANK;

								if (Validator.isNotNull(preferences.getValue(emailParam + "Subject_" + LocaleUtil.toLanguageId(locales[i]), StringPool.BLANK)) ||
									Validator.isNotNull(preferences.getValue(emailParam + "Body_" + LocaleUtil.toLanguageId(locales[i]), StringPool.BLANK))) {

									optionStyle = "style=\"font-weight: bold;\"";
								}
							%>

								<aui:option label="<%= locales[i].getDisplayName(locale) %>" selected="<%= currentLanguageId.equals(LocaleUtil.toLanguageId(locales[i])) %>" value="<%= LocaleUtil.toLanguageId(locales[i]) %>" />

							<%
							}
							%>

						</aui:select>

						<aui:input cssClass="lfr-input-text-container" label="subject" name='<%= emailParam + "Subject" + StringPool.UNDERLINE + currentLanguageId %>' value="<%= emailSubject %>" />

						<aui:field-wrapper label="body">
							<liferay-ui:input-editor editorImpl="<%= EDITOR_WYSIWYG_IMPL_KEY %>" />

							<aui:input name="<%= editorParam %>" type="hidden" />
						</aui:field-wrapper>
					</aui:fieldset>

					<div class="definition-of-terms">
						<h4><liferay-ui:message key="definition-of-terms" /></h4>

						<dl>
							<dt>
								[$FROM_ADDRESS$]
							</dt>
							<dd>
								<%= preferences.getValue("emailFromAddress", PrefsPropsUtil.getString(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_ADDRESS)) %>
							</dd>
							<dt>
								[$FROM_NAME$]
							</dt>
							<dd>
								<%= preferences.getValue("emailFromName", PrefsPropsUtil.getString(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_NAME)) %>
							</dd>

							<c:if test='<%= tabs2.equals("password-reset-notification") %>'>
								<dt>
									[$PASSWORD_RESET_URL$]
								</dt>
								<dd>
									<liferay-ui:message key="the-password-reset-url" />
								</dd>
							</c:if>

							<dt>
								[$PORTAL_URL$]
							</dt>
							<dd>
								<%= company.getVirtualHost() %>
							</dd>
							<dt>
								[$REMOTE_ADDRESS$]
							</dt>
							<dd>
								<liferay-ui:message key="the-browser's-remote-address" />
							</dd>
							<dt>
								[$REMOTE_HOST$]
							</dt>
							<dd>
								<liferay-ui:message key="the-browser's-remote-host" />
							</dd>

							<dt>
								[$TO_ADDRESS$]
							</dt>
							<dd>
								<liferay-ui:message key="the-address-of-the-email-recipient" />
							</dd>
							<dt>
								[$TO_NAME$]
							</dt>
							<dd>
								<liferay-ui:message key="the-name-of-the-email-recipient" />
							</dd>

							<dt>
								[$USER_AGENT$]
							</dt>
							<dd>
								<liferay-ui:message key="the-browser's-user-agent" />
							</dd>

							<dt>
								[$USER_ID$]
							</dt>
							<dd>
								<liferay-ui:message key="the-user-id" />
							</dd>

							<c:if test='<%= tabs2.equals("password-changed-notification") %>'>
								<dt>
									[$USER_PASSWORD$]
								</dt>
								<dd>
									<liferay-ui:message key="the-user-password" />
								</dd>
							</c:if>

							<dt>
								[$USER_SCREENNAME$]
							</dt>
							<dd>
								<liferay-ui:message key="the-user-screen-name" />
							</dd>
						</dl>
					</div>
				</c:when>
				<c:otherwise>
					<aui:fieldset>
						<aui:input cssClass="lfr-input-text-container" label="name" name="emailFromName" value="<%= emailFromName %>" />

						<liferay-ui:error key="emailFromAddress" message="please-enter-a-valid-email-address" />

						<aui:input cssClass="lfr-input-text-container" label="address" name="emailFromAddress" value="<%= emailFromAddress %>" />
					</aui:fieldset>
				</c:otherwise>
			</c:choose>

			<aui:script>
				function <portlet:namespace />initEditor() {
					return "<%= UnicodeFormatter.toString(editorContent) %>";
				}

				function <portlet:namespace />saveConfiguration() {
					<c:if test='<%= tabs2.endsWith("-notification") %>'>
						document.<portlet:namespace />fm.<portlet:namespace /><%= editorParam %>.value = window.<portlet:namespace />editor.getHTML();
					</c:if>

					submitForm(document.<portlet:namespace />fm);
				}

				function <portlet:namespace />updateLanguage() {
					document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = '';
					submitForm(document.<portlet:namespace />fm);
				}
			</aui:script>
		</c:when>
		<c:otherwise>
			<aui:fieldset>
				<aui:select label="authentication-type" name="authType">
					<aui:option label="default" value="" />
					<aui:option label="by-email-address" selected="<%= authType.equals(CompanyConstants.AUTH_TYPE_EA) %>" value="<%= CompanyConstants.AUTH_TYPE_EA %>" />
					<aui:option label="by-screen-name" selected="<%= authType.equals(CompanyConstants.AUTH_TYPE_SN) %>" value="<%= CompanyConstants.AUTH_TYPE_SN %>" />
					<aui:option label="by-user-id" selected="<%= authType.equals(CompanyConstants.AUTH_TYPE_ID) %>" value="<%= CompanyConstants.AUTH_TYPE_ID %>" />
				</aui:select>
			</aui:fieldset>
		</c:otherwise>
	</c:choose>

	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>

<%!
public static final String EDITOR_WYSIWYG_IMPL_KEY = "editor.wysiwyg.portal-web.docroot.html.portlet.login.configuration.jsp";
%>