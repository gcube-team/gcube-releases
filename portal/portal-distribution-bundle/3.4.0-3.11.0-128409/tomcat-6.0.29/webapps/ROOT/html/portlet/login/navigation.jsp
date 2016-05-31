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
String strutsAction = ParamUtil.getString(request, "struts_action");

boolean showCreateAccountIcon = false;

if (!strutsAction.equals("/login/create_account") && company.isStrangers()) {
	showCreateAccountIcon = true;
}

boolean showFacebookConnectIcon = false;

if (!strutsAction.startsWith("/login/facebook_connect") && FacebookConnectUtil.isEnabled(company.getCompanyId())) {
	showFacebookConnectIcon = true;
}

boolean showForgotPasswordIcon = false;

if (!strutsAction.equals("/login/forgot_password") && (company.isSendPassword() || company.isSendPasswordResetLink())) {
	showForgotPasswordIcon = true;
}

boolean showOpenIdIcon = false;

if (!strutsAction.equals("/login/open_id") && OpenIdUtil.isEnabled(company.getCompanyId())) {
	showOpenIdIcon = true;
}

boolean showSignInIcon = false;

if (Validator.isNotNull(strutsAction) && !strutsAction.equals("/login/login")) {
	showSignInIcon = true;
}
%>

<c:if test="<%= showCreateAccountIcon || showForgotPasswordIcon || showOpenIdIcon || showSignInIcon %>">
	<div class="navigation">
		<liferay-ui:icon-list>
			<c:if test="<%= showSignInIcon %>">
				<liferay-ui:icon
					image="status_online"
					message="sign-in"
					url="<%= themeDisplay.getURLSignIn() %>"
				/>
			</c:if>

			<c:if test="<%= showFacebookConnectIcon %>">
				<portlet:renderURL var="facebookRedirectURL">
					<portlet:param name="struts_action" value="/login/facebook_connect_add_user" />
				</portlet:renderURL>

				<portlet:actionURL var="facebookConnectURL">
					<portlet:param name="struts_action" value="/login/facebook_connect" />
					<portlet:param name="redirect" value="<%= facebookRedirectURL %>" />
				</portlet:actionURL>

				<liferay-ui:icon
					image="../social_bookmarks/facebook"
					message="facebook"
					url="<%= facebookConnectURL %>"
				/>
			</c:if>

			<c:if test="<%= showOpenIdIcon %>">
				<portlet:renderURL var="openIdURL">
					<portlet:param name="struts_action" value="/login/open_id" />
				</portlet:renderURL>

				<liferay-ui:icon
					message="open-id"
					src='<%= themeDisplay.getPathThemeImages() + "/common/openid.gif" %>'
					url="<%= openIdURL %>"
				/>
			</c:if>

			<c:if test="<%= showCreateAccountIcon %>">
				<liferay-ui:icon
					image="add_user"
					message="create-account"
					url="<%= themeDisplay.getURLCreateAccount().toString() %>"
				/>
			</c:if>

			<c:if test="<%= showForgotPasswordIcon %>">
				<portlet:renderURL var="forgotPasswordURL">
					<portlet:param name="struts_action" value="/login/forgot_password" />
				</portlet:renderURL>

				<liferay-ui:icon
					image="help"
					message="forgot-password"
					url="<%= forgotPasswordURL %>"
				/>
			</c:if>
		</liferay-ui:icon-list>
	</div>
</c:if>