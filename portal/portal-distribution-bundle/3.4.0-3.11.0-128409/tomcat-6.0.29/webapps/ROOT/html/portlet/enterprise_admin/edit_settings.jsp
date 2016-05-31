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
themeDisplay.setIncludeServiceJs(true);

String[] configurationSections = PropsValues.COMPANY_SETTINGS_FORM_CONFIGURATION;
String[] identificationSections = PropsValues.COMPANY_SETTINGS_FORM_IDENTIFICATION;
String[] miscellaneousSections = PropsValues.COMPANY_SETTINGS_FORM_MISCELLANEOUS;


String[] allSections = ArrayUtil.append(configurationSections, ArrayUtil.append(identificationSections, miscellaneousSections));
String[][] categorySections = {configurationSections, identificationSections, miscellaneousSections};

String curSection = configurationSections[0];

String historyKey = ParamUtil.getString(request, "historyKey");

if (Validator.isNotNull(historyKey)) {
	curSection = historyKey;
}
%>

<div id="<portlet:namespace />sectionsContainer">
	<table class="user-table" width="100%">
	<tr>
		<td>

			<%
			request.setAttribute("addresses.className", Account.class.getName());
			request.setAttribute("emailAddresses.className", Account.class.getName());
			request.setAttribute("phones.className", Account.class.getName());
			request.setAttribute("websites.className", Account.class.getName());

			request.setAttribute("addresses.classPK", company.getAccountId());
			request.setAttribute("emailAddresses.classPK", company.getAccountId());
			request.setAttribute("phones.classPK", company.getAccountId());
			request.setAttribute("websites.classPK", company.getAccountId());

			for (String section : allSections) {
				String sectionId = _getSectionId(section);
				String sectionJsp = "/html/portlet/enterprise_admin/settings/" + _getSectionJsp(section) + ".jsp";
			%>

				<div class="form-section <%= (curSection.equals(section) || curSection.equals(sectionId)) ? "selected" : "aui-helper-hidden-accessible" %>" id="<%= sectionId %>">
					<liferay-util:include page="<%= sectionJsp %>" />
				</div>

			<%
			}
			%>

			<div class="lfr-component form-navigation">
				<div class="user-info">
					<p class="float-container">
						<img alt="<liferay-ui:message key="logo" />" class="company-logo" src="<%= themeDisplay.getPathImage() %>/company_logo?img_id=<%= company.getLogoId() %>&t=<%= ImageServletTokenUtil.getToken(company.getLogoId()) %>" /><br />

						<span><%= company.getName() %></span>
					</p>
				</div>

				<%
				String[] categoryNames = _CATEGORY_NAMES;
				%>

				<%@ include file="/html/portlet/enterprise_admin/categories_navigation.jspf" %>

				<div class="aui-button-holder">
					<aui:button onClick='<%= renderResponse.getNamespace() + "saveCompany();" %>' value="save" />

					<%
					PortletURL portletURL = new PortletURLImpl(request, PortletKeys.ENTERPRISE_ADMIN_SETTINGS, plid, PortletRequest.RENDER_PHASE);
					%>

					<aui:button onClick="<%= portletURL.toString() %>" type="cancel" />
				</div>
			</div>
		</td>
	</tr>
	</table>
</div>

<%!
private static String[] _CATEGORY_NAMES = {"configuration", "identification", "miscellaneous"};
%>