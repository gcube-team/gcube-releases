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
User user2 = company.getDefaultUser();

Locale[] locales = LanguageUtil.getAvailableLocales();
String[] languageIds = LocaleUtil.toLanguageIds(locales);

String timeZoneId = ParamUtil.getString(request, "timeZoneId", user2.getTimeZoneId());
String languageId = ParamUtil.getString(request, "languageId", user2.getLanguageId());
String availableLocales = ParamUtil.getString(request, "settings--" + PropsKeys.LOCALES + "--", StringUtil.merge(languageIds));

boolean companySecurityCommunityLogo = ParamUtil.getBoolean(request, "settings--" + PropsKeys.COMPANY_SECURITY_COMMUNITY_LOGO + "--", company.isCommunityLogo());
boolean deleteLogo = ParamUtil.getBoolean(request, "deleteLogo");

String defaultRegularThemeId = ParamUtil.getString(request, "settings--" + PropsKeys.DEFAULT_REGULAR_THEME_ID + "--", PrefsPropsUtil.getString(company.getCompanyId(), PropsKeys.DEFAULT_REGULAR_THEME_ID, PropsValues.DEFAULT_REGULAR_THEME_ID));
String defaultWapThemeId = ParamUtil.getString(request, "settings--" + PropsKeys.DEFAULT_WAP_THEME_ID + "--", PrefsPropsUtil.getString(company.getCompanyId(), PropsKeys.DEFAULT_WAP_THEME_ID, PropsValues.DEFAULT_WAP_THEME_ID));
String defaultControlPanelThemeId = ParamUtil.getString(request, "settings--" + PropsKeys.CONTROL_PANEL_LAYOUT_REGULAR_THEME_ID + "--", PrefsPropsUtil.getString(company.getCompanyId(), PropsKeys.CONTROL_PANEL_LAYOUT_REGULAR_THEME_ID, PropsValues.CONTROL_PANEL_LAYOUT_REGULAR_THEME_ID));
%>

<h3><liferay-ui:message key="language-and-time-zone" /></h3>

<aui:fieldset>
	<aui:select label="default-language" name="languageId">

		<%
		Locale locale2 = LocaleUtil.fromLanguageId(languageId);

		for (int i = 0; i < locales.length; i++) {
		%>

			<aui:option label="<%= locales[i].getDisplayName(locale) %>" lang="<%= LocaleUtil.toW3cLanguageId(locales[i]) %>" selected="<%= (locale2.getLanguage().equals(locales[i].getLanguage()) && locale2.getCountry().equals(locales[i].getCountry())) %>" value="<%= LocaleUtil.toLanguageId(locales[i]) %>" />

		<%
		}
		%>

	</aui:select>

	<aui:input cssClass="lfr-input-text-container" label="available-languages" name='<%= "settings--" + PropsKeys.LOCALES + "--" %>' type="text" value="<%= availableLocales %>" />

	<aui:input label="time-zone" name="timeZoneId" type="timeZone" value="<%= timeZoneId %>" />
</aui:fieldset>

<h3><liferay-ui:message key="logo" /></h3>

<aui:fieldset>
	<aui:input inlineLabel="left" label="allow-community-administrators-to-use-their-own-logo" name='<%= "settings--" + PropsKeys.COMPANY_SECURITY_COMMUNITY_LOGO + "--" %>' type="checkbox" value="<%= companySecurityCommunityLogo %>" />

	<portlet:renderURL windowState="<%= LiferayWindowState.POP_UP.toString() %>" var="editCompanyLogoURL">
		<portlet:param name="struts_action" value="/enterprise_admin/edit_company_logo" />
		<portlet:param name="redirect" value="<%= currentURL %>" />
	</portlet:renderURL>

	<%
	String taglibEditURL = "javascript:" + renderResponse.getNamespace() + "openEditCompanyLogoWindow('" + editCompanyLogoURL + "');";
	%>

	<aui:a cssClass="change-company-logo" href="<%= taglibEditURL %>">
		<img alt="<liferay-ui:message key="logo" />" class="avatar" id="<portlet:namespace />avatar" src="<%= themeDisplay.getPathImage() %>/company_logo?img_id=<%= deleteLogo ? 0 : company.getLogoId() %>&t=<%= ImageServletTokenUtil.getToken(company.getLogoId()) %>" />
	</aui:a>

	<div class="portrait-icons">
		<liferay-ui:icon
			image="edit"
			label="<%= true %>"
			message="change"
			url="<%= taglibEditURL %>"
		/>

		<c:if test="<%= company.getLogoId() != 0 %>">

			<%
			String taglibDeleteURL = "javascript:" + renderResponse.getNamespace() + "deleteLogo('" + themeDisplay.getPathImage() + "/company_logo?img_id=0');";
			%>

			<liferay-ui:icon
				cssClass="modify-link"
				image="delete"
				label="<%= true %>"
				url="<%= taglibDeleteURL %>"
			/>

			<aui:input name="deleteLogo" type="hidden" value="<%= deleteLogo %>" />
		</c:if>
	</div>
</aui:fieldset>

<h3><liferay-ui:message key="look-and-feel" /></h3>

<%
List<Theme> themes = null;
boolean deployed = false;
%>

<aui:fieldset>
	<aui:select label="default-regular-theme" name='<%= "settings--" + PropsKeys.DEFAULT_REGULAR_THEME_ID + "--" %>'>

		<%
		themes = ThemeLocalServiceUtil.getThemes(company.getCompanyId(), 0, user.getUserId(), false);
		deployed = false;

		for (Theme curTheme: themes) {
			if (Validator.equals(defaultRegularThemeId, curTheme.getThemeId())) {
				deployed = true;
			}
		%>

			<aui:option label="<%= curTheme.getName() %>" selected="<%= (Validator.equals(defaultRegularThemeId, curTheme.getThemeId())) %>" value="<%= curTheme.getThemeId() %>" />

		<%
		}
		%>

		<c:if test="<%= !deployed %>">
			<aui:option label='<%= defaultRegularThemeId + "(" + LanguageUtil.get(pageContext, "undeployed") + ")" %>' selected="<%= true %>" value="<%= defaultRegularThemeId %>" />
		</c:if>
	</aui:select>

	<aui:select label="default-mobile-theme" name='<%= "settings--" + PropsKeys.DEFAULT_REGULAR_THEME_ID + "--" %>'>

		<%
		themes = ThemeLocalServiceUtil.getThemes(company.getCompanyId(), 0, user.getUserId(), true);
		deployed = false;

		for (Theme curTheme: themes) {
			if (Validator.equals(defaultWapThemeId, curTheme.getThemeId())) {
				deployed = true;
			}
		%>

			<aui:option label="<%= curTheme.getName() %>" selected="<%= (Validator.equals(defaultWapThemeId, curTheme.getThemeId())) %>" value="<%= curTheme.getThemeId() %>" />

		<%
		}
		%>

		<c:if test="<%= !deployed %>">
			<aui:option label='<%= defaultWapThemeId + "(" + LanguageUtil.get(pageContext, "undeployed") + ")" %>' selected="<%= true %>" value="<%= defaultWapThemeId %>" />
		</c:if>
	</aui:select>

	<aui:select label="default-control-panel-theme" name='<%= "settings--" + PropsKeys.CONTROL_PANEL_LAYOUT_REGULAR_THEME_ID + "--" %>'>

		<%
		themes = ThemeLocalServiceUtil.getThemes(company.getCompanyId(), 0, user.getUserId(), false);
		deployed = false;

		Theme controlPanelTheme = ThemeLocalServiceUtil.getTheme(company.getCompanyId(), "controlpanel", false);

		if (controlPanelTheme != null) {
			themes.add(controlPanelTheme);
		}

		for (Theme curTheme: themes) {
			if (Validator.equals(defaultControlPanelThemeId, curTheme.getThemeId())) {
				deployed = true;
			}
		%>

			<aui:option label="<%= curTheme.getName() %>" selected="<%= (Validator.equals(defaultControlPanelThemeId, curTheme.getThemeId())) %>" value="<%= curTheme.getThemeId() %>" />

		<%
		}
		%>

		<c:if test="<%= !deployed %>">
			<aui:option label='<%= defaultControlPanelThemeId + "(" + LanguageUtil.get(pageContext, "undeployed") + ")" %>' selected="<%= true %>" value="<%= defaultControlPanelThemeId %>" />
		</c:if>
	</aui:select>
</aui:fieldset>

<aui:script>
	function <portlet:namespace />openEditCompanyLogoWindow(editCompanyLogoURL) {
		var editCompanyLogoWindow = window.open(editCompanyLogoURL, 'changeLogo', 'directories=no,height=400,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=500');

		editCompanyLogoWindow.focus();
	}

	Liferay.provide(
		window,
		'<portlet:namespace />changeLogo',
		function(newLogoURL) {
			var A = AUI();

			A.one('#<portlet:namespace />avatar').attr('src', newLogoURL);
			A.one('.company-logo').attr('src', newLogoURL);

			A.one('#<portlet:namespace />deleteLogo').val(false);
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />deleteLogo',
		function(defaultLogoURL) {
			var A = AUI();

			A.one('#<portlet:namespace />deleteLogo').val(true);

			A.one('#<portlet:namespace />avatar').attr('src', defaultLogoURL);
			A.one('.company-logo').attr('src', defaultLogoURL);
		},
		['aui-base']
	);
</aui:script>

<aui:script use="aui-base">
	var modifyLinks = A.all('span.modify-link');

	if (modifyLinks) {
		modifyLinks.on(
			'click',
			function() {
				A.fire(
					'enterpriseAdmin:trackChanges',
					A.one('.selected .modify-link')
				);
			}
		);
	}
</aui:script>