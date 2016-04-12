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

String viewOrganizationsRedirect = ParamUtil.getString(request, "viewOrganizationsRedirect");

OrganizationSearch searchContainer = (OrganizationSearch)request.getAttribute("liferay-ui:search:searchContainer");

OrganizationDisplayTerms displayTerms = (OrganizationDisplayTerms)searchContainer.getDisplayTerms();

String type = displayTerms.getType();

Organization organization = null;

if (displayTerms.getParentOrganizationId() > 0) {
	try {
		organization = OrganizationLocalServiceUtil.getOrganization(displayTerms.getParentOrganizationId());
	}
	catch (NoSuchOrganizationException nsoe) {
	}
}
%>

<c:if test="<%= organization != null %>">
	<aui:input name="<%= UserDisplayTerms.ORGANIZATION_ID %>" type="hidden" value="<%= organization.getOrganizationId() %>" />

	<liferay-ui:header
		title='<%= LanguageUtil.format(pageContext, "suborganizations-of-x", HtmlUtil.escape(organization.getName())) %>'
		backURL="<%= viewOrganizationsRedirect %>"
	/>

	<%
	EnterpriseAdminUtil.addPortletBreadcrumbEntries(organization, request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "suborganizations"), currentURL);
	%>

</c:if>

<liferay-ui:search-toggle
	id="toggle_id_enterprise_admin_organization_search"
	displayTerms="<%= displayTerms %>"
	buttonLabel="search"
>
	<aui:fieldset>
		<aui:column>
			<aui:input name="<%= displayTerms.NAME %>" size="20" value="<%= displayTerms.getName() %>" />

			<aui:select name="<%= displayTerms.TYPE %>">
				<aui:option value=""></aui:option>

				<%
				for (String curType : PropsValues.ORGANIZATIONS_TYPES) {
				%>

					<aui:option label="<%= curType %>" selected="<%= type.equals(curType) %>" />

				<%
				}
				%>

			</aui:select>
		</aui:column>

		<aui:column>
			<aui:input name="<%= displayTerms.STREET %>" size="20" value="<%= displayTerms.getStreet() %>" />

			<aui:select label="country" name="<%= displayTerms.COUNTRY_ID %>"></aui:select>
		</aui:column>

		<aui:column>
			<aui:input name="<%= displayTerms.CITY %>" size="20" value="<%= displayTerms.getCity() %>" />

			<aui:select label="region" name="<%= displayTerms.REGION_ID %>"></aui:select>
		</aui:column>

		<aui:column>
			<aui:input name="<%= displayTerms.ZIP %>" size="20" value="<%= displayTerms.getZip() %>" />
		</aui:column>
	</aui:fieldset>
</liferay-ui:search-toggle>

<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
	<aui:script>
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace /><%= displayTerms.NAME %>);
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace /><%= displayTerms.KEYWORDS %>);
	</aui:script>
</c:if>

<aui:script use="liferay-dynamic-select">
	new Liferay.DynamicSelect(
		[
			{
				select: '<portlet:namespace /><%= displayTerms.COUNTRY_ID %>',
				selectData: Liferay.Address.getCountries,
				selectDesc: 'name',
				selectId: 'countryId',
				selectVal: '<%= displayTerms.getCountryId() %>'
			},
			{
				select: '<portlet:namespace /><%= displayTerms.REGION_ID %>',
				selectData: Liferay.Address.getRegions,
				selectDesc: 'name',
				selectId: 'regionId',
				selectVal: '<%= displayTerms.getRegionId() %>'
			}
		]
	);
</aui:script>