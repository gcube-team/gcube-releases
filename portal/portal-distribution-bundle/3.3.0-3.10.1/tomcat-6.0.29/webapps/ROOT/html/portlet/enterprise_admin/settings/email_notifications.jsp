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

<h3><liferay-ui:message key="email-notifications" /></h3>

<%
String adminEmailFromName = ParamUtil.getString(request, "settings--" + PropsKeys.ADMIN_EMAIL_FROM_NAME + "--", PrefsPropsUtil.getString(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_NAME));
String adminEmailFromAddress = ParamUtil.getString(request, "settings--" + PropsKeys.ADMIN_EMAIL_FROM_ADDRESS + "--", PrefsPropsUtil.getString(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_ADDRESS));

boolean adminEmailUserAddedEnable = ParamUtil.getBoolean(request, "emailUserAddedEnable", PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_USER_ADDED_ENABLED));
String adminEmailUserAddedSubject = ParamUtil.getString(request, "emailUserAddedSubject", PrefsPropsUtil.getContent(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_USER_ADDED_SUBJECT));
String adminEmailUserAddedBody = ParamUtil.getString(request, "emailUserAddedBody", PrefsPropsUtil.getContent(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_USER_ADDED_BODY));

String adminEmailPasswordSentSubject = ParamUtil.getString(request, "emailPasswordSentSubject", PrefsPropsUtil.getContent(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_PASSWORD_SENT_SUBJECT));
String adminEmailPasswordSentBody = ParamUtil.getString(request, "emailPasswordSentBody", PrefsPropsUtil.getContent(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_PASSWORD_SENT_BODY));

String adminEmailPasswordResetSubject = ParamUtil.getString(request, "emailPasswordResetSubject", PrefsPropsUtil.getContent(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_PASSWORD_RESET_SUBJECT));
String adminEmailPasswordResetBody = ParamUtil.getString(request, "emailPasswordResetBody", PrefsPropsUtil.getContent(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_PASSWORD_RESET_BODY));
%>

<liferay-ui:error-marker key="errorSection" value="email_notifications" />

<liferay-ui:tabs
	names="sender,account-created-notification,password-changed-notification,password-reset-notification"
	refresh="<%= false %>"
>
	<liferay-ui:section>
		<aui:fieldset>
			<liferay-ui:error key="emailFromName" message="please-enter-a-valid-name" />

			<aui:input cssClass="lfr-input-text-container" label="name" name='<%= "settings--" + PropsKeys.ADMIN_EMAIL_FROM_NAME + "--" %>' type="text" value="<%= adminEmailFromName %>" />

			<liferay-ui:error key="emailFromAddress" message="please-enter-a-valid-email-address" />

			<aui:input cssClass="lfr-input-text-container" label="address" name='<%= "settings--" + PropsKeys.ADMIN_EMAIL_FROM_ADDRESS + "--" %>' type="text" value="<%= adminEmailFromAddress %>" />
		</aui:fieldset>
	</liferay-ui:section>
	<liferay-ui:section>
		<aui:fieldset>
			<aui:input inlineLabel="left" label="enabled" name='<%= "settings--" + PropsKeys.ADMIN_EMAIL_USER_ADDED_ENABLED + "--" %>' type="checkbox" value="<%= adminEmailUserAddedEnable %>" />

			<liferay-ui:error key="emailUserAddedSubject" message="please-enter-a-valid-subject" />

			<aui:input cssClass="lfr-input-text-container" label="subject" name='<%= "settings--" + PropsKeys.ADMIN_EMAIL_USER_ADDED_SUBJECT + "--" %>' type="text" value="<%= adminEmailUserAddedSubject %>" />

			<liferay-ui:error key="emailUserAddedBody" message="please-enter-a-valid-body" />

			<aui:field-wrapper label="body">
				<liferay-ui:input-editor editorImpl="<%= EDITOR_WYSIWYG_IMPL_KEY %>" initMethod='<%= renderResponse.getNamespace() + "initEmailUserAddedBodyEditor" %>' name="emailUserAddedBody" toolbarSet="email" width="470" />

				<aui:input name='<%= "settings--" + PropsKeys.ADMIN_EMAIL_USER_ADDED_BODY + "--" %>' type="hidden" value="<%= adminEmailUserAddedBody %>" />
			</aui:field-wrapper>

			<div class="terms email-user-add definition-of-terms">
				<%@ include file="/html/portlet/enterprise_admin/settings/definition_of_terms.jspf" %>
			</div>
		</aui:fieldset>
	</liferay-ui:section>
	<liferay-ui:section>
		<aui:fieldset>
			<liferay-ui:error key="emailPasswordSentSubject" message="please-enter-a-valid-subject" />

			<aui:input cssClass="lfr-input-text-container" label="subject" name='<%= "settings--" + PropsKeys.ADMIN_EMAIL_PASSWORD_SENT_SUBJECT + "--" %>' type="text" value="<%= adminEmailPasswordSentSubject %>" />

			<liferay-ui:error key="emailPasswordSentBody" message="please-enter-a-valid-body" />

			<aui:field-wrapper label="body">
				<liferay-ui:input-editor editorImpl="<%= EDITOR_WYSIWYG_IMPL_KEY %>" initMethod='<%= renderResponse.getNamespace() + "initEmailPasswordSentBodyEditor" %>' name="emailPasswordSentBody" toolbarSet="email" width="470" />

				<aui:input name='<%= "settings--" + PropsKeys.ADMIN_EMAIL_PASSWORD_SENT_BODY + "--" %>' type="hidden" value="<%= adminEmailPasswordSentBody %>" />
			</aui:field-wrapper>

			<div class="terms email-password-sent definition-of-terms">
				<%@ include file="/html/portlet/enterprise_admin/settings/definition_of_terms.jspf" %>
			</div>
		</aui:fieldset>
	</liferay-ui:section>
	<liferay-ui:section>
		<aui:fieldset>
			<liferay-ui:error key="emailPasswordResetSubject" message="please-enter-a-valid-subject" />

			<aui:input cssClass="lfr-input-text-container" label="subject" name='<%= "settings--" + PropsKeys.ADMIN_EMAIL_PASSWORD_RESET_SUBJECT + "--" %>' type="text" value="<%= adminEmailPasswordResetSubject %>" />

			<liferay-ui:error key="emailPasswordResetBody" message="please-enter-a-valid-body" />

			<aui:field-wrapper label="body">
				<liferay-ui:input-editor editorImpl="<%= EDITOR_WYSIWYG_IMPL_KEY %>" initMethod='<%= renderResponse.getNamespace() + "initEmailPasswordResetBodyEditor" %>' name="emailPasswordResetBody" toolbarSet="email" width="470" />

				<aui:input name='<%= "settings--" + PropsKeys.ADMIN_EMAIL_PASSWORD_RESET_BODY + "--" %>' type="hidden" value="<%= adminEmailPasswordResetBody %>" />
			</aui:field-wrapper>

			<div class="terms email-password-sent definition-of-terms">
				<%@ include file="/html/portlet/enterprise_admin/settings/definition_of_terms.jspf" %>
			</div>
		</aui:fieldset>
	</liferay-ui:section>
</liferay-ui:tabs>

<aui:script>
	function <portlet:namespace />initEmailUserAddedBodyEditor() {
		return "<%= UnicodeFormatter.toString(adminEmailUserAddedBody) %>";
	}

	function <portlet:namespace />initEmailPasswordSentBodyEditor() {
		return "<%= UnicodeFormatter.toString(adminEmailPasswordSentBody) %>";
	}

	function <portlet:namespace />initEmailPasswordResetBodyEditor() {
		return "<%= UnicodeFormatter.toString(adminEmailPasswordResetBody) %>";
	}

	function <portlet:namespace />saveEmails() {
		try {
			document.<portlet:namespace />fm['<portlet:namespace />settings--<%= PropsKeys.ADMIN_EMAIL_USER_ADDED_BODY %>--'].value = window.emailUserAddedBody.getHTML();
			document.<portlet:namespace />fm['<portlet:namespace />settings--<%= PropsKeys.ADMIN_EMAIL_PASSWORD_SENT_BODY %>--'].value = window.emailPasswordSentBody.getHTML();
			document.<portlet:namespace />fm['<portlet:namespace />settings--<%= PropsKeys.ADMIN_EMAIL_PASSWORD_RESET_BODY %>--'].value = window.emailPasswordResetBody.getHTML();
		}
		catch(error) {
		}
	}
</aui:script>

<%!
public static final String EDITOR_WYSIWYG_IMPL_KEY = "editor.wysiwyg.portal-web.docroot.html.portlet.enterprise_admin.view.jsp";
%>