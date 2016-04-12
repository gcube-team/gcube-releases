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

String redirect = ParamUtil.getString(request, "redirect");
String backURL = ParamUtil.getString(request, "backURL", redirect);

Organization organization = (Organization)request.getAttribute(WebKeys.ORGANIZATION);

long organizationId = BeanParamUtil.getLong(organization, request, "organizationId");

String[] mainSections = PropsValues.ORGANIZATIONS_FORM_ADD_MAIN;
String[] identificationSections = PropsValues.ORGANIZATIONS_FORM_ADD_IDENTIFICATION;
String[] miscellaneousSections = PropsValues.ORGANIZATIONS_FORM_ADD_MISCELLANEOUS;

if (organization != null) {
	mainSections = PropsValues.ORGANIZATIONS_FORM_UPDATE_MAIN;
	identificationSections = PropsValues.ORGANIZATIONS_FORM_UPDATE_IDENTIFICATION;
	miscellaneousSections = PropsValues.ORGANIZATIONS_FORM_UPDATE_MISCELLANEOUS;
}

String[] allSections = ArrayUtil.append(mainSections, ArrayUtil.append(identificationSections, miscellaneousSections));

String[][] categorySections = {mainSections, identificationSections, miscellaneousSections};

String curSection = mainSections[0];

String historyKey = ParamUtil.getString(request, "historyKey");

if (Validator.isNotNull(historyKey)) {
	curSection = historyKey;
}
%>

<liferay-util:include page="/html/portlet/enterprise_admin/organization/toolbar.jsp">
	<liferay-util:param name="toolbarItem" value='<%= (organization == null) ? "add" : "view-all" %>' />
</liferay-util:include>

<liferay-ui:header
	backURL="<%= backURL %>"
	title='<%= (organization == null) ? "new-organization" : organization.getName() %>'
/>

<portlet:actionURL var="editOrganizationURL">
	<portlet:param name="struts_action" value="/enterprise_admin/edit_organization" />
</portlet:actionURL>

<aui:form action="<%= editOrganizationURL %>" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveOrganization();" %>'>
	<aui:input name="<%= Constants.CMD %>" type="hidden" />
	<aui:input name="redirect" type="hidden" />
	<aui:input name="backURL" type="hidden" value="<%= backURL %>" />
	<aui:input name="organizationId" type="hidden" value="<%= organizationId %>" />

	<div id="<portlet:namespace />sectionsContainer">
		<table class="organization-table" width="100%">
		<tr>
			<td>

				<%
				request.setAttribute("addresses.className", Organization.class.getName());
				request.setAttribute("addresses.classPK", organizationId);
				request.setAttribute("emailAddresses.className", Organization.class.getName());
				request.setAttribute("emailAddresses.classPK", organizationId);
				request.setAttribute("phones.className", Organization.class.getName());
				request.setAttribute("phones.classPK", organizationId);
				request.setAttribute("websites.className", Organization.class.getName());
				request.setAttribute("websites.classPK", organizationId);

				for (String section : allSections) {
					String sectionId = _getSectionId(section);
					String sectionJsp = "/html/portlet/enterprise_admin/organization/" + _getSectionJsp(section) + ".jsp";
				%>

					<div class="form-section <%= (curSection.equals(section) || curSection.equals(sectionId)) ? "selected" : "aui-helper-hidden-accessible" %>" id="<%= sectionId %>">
						<liferay-util:include page="<%= sectionJsp %>" />
					</div>

				<%
				}
				%>

				<div class="lfr-component form-navigation">
					<div class="organization-info">
						<p class="float-container">
							<c:if test="<%= organization != null %>">

								<%
								long logoId = organization.getLogoId();
								%>

								<img alt="<%= HtmlUtil.escape(organization.getName()) %>" class="avatar" src="<%= themeDisplay.getPathImage() %>/organization_logo?img_id=<%= logoId %>&t=<%= ImageServletTokenUtil.getToken(logoId) %>" />

								<span><%= HtmlUtil.escape(organization.getName()) %></span>
							</c:if>
						</p>
					</div>

					<%
					String[] categoryNames = _CATEGORY_NAMES;
					%>

					<%@ include file="/html/portlet/enterprise_admin/categories_navigation.jspf" %>

					<aui:button-row>
						<aui:button type="submit" />

						<aui:button onClick="<%= backURL %>" type="cancel" />
					</aui:button-row>
				</div>
			</td>
		</tr>
		</table>
	</div>
</aui:form>

<aui:script>
	function <portlet:namespace />createURL(href, value, onclick) {
		return '<a href="' + href + '"' + (onclick ? ' onclick="' + onclick + '" ' : '') + '>' + value + '</a>';
	};

	function <portlet:namespace />saveOrganization() {
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= (organization == null) ? Constants.ADD : Constants.UPDATE %>";

		var redirect = "<portlet:renderURL><portlet:param name="struts_action" value="/enterprise_admin/edit_organization" /><portlet:param name="backURL" value="<%= backURL %>"></portlet:param></portlet:renderURL>";

		if (location.hash) {
			redirect += location.hash.replace('#_LFR_FN_', '&<portlet:namespace />historyKey=');
		}

		document.<portlet:namespace />fm.<portlet:namespace />redirect.value = redirect;

		submitForm(document.<portlet:namespace />fm);
	}

	<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />name);
	</c:if>
</aui:script>

<%
if (organization != null) {
	EnterpriseAdminUtil.addPortletBreadcrumbEntries(organization, request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "edit"), currentURL);
}
else {
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "add-organization"), currentURL);
}
%>

<%!
private static String[] _CATEGORY_NAMES = {"organization-information", "identification", "miscellaneous"};
%>