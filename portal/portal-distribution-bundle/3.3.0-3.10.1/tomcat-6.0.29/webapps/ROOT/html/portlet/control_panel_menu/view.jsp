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

<%@ include file="/html/portlet/init.jsp" %>

<h1 class="user-greeting">
	<liferay-ui:message key="control-panel" />
</h1>

<div class="portal-add-content">
	<liferay-ui:panel-container extended="<%= true %>" id="addContentPanelContainer" persistState="<%= true %>">

		<%
		String ppid = GetterUtil.getString((String)request.getAttribute("control_panel.jsp-ppid"), layoutTypePortlet.getStateMaxPortletId());

		String category = PortalUtil.getControlPanelCategory(ppid, themeDisplay);

		for (String curCategory : PortletCategoryKeys.ALL) {
			List<Portlet> portlets = PortalUtil.getControlPanelPortlets(curCategory, themeDisplay);

			if (portlets.isEmpty()) {
				continue;
			}

			List<Layout> scopeLayouts = new ArrayList<Layout>();

			String curGroupLabel = null;
			Group curGroup = null;

			String title = null;

			if (curCategory.equals(PortletCategoryKeys.MY)) {
				title = HtmlUtil.escape(StringUtil.shorten(user.getFullName(), 25));
			}
			else if (curCategory.equals(PortletCategoryKeys.CONTENT)) {
				Layout scopeLayout = null;

				curGroup = themeDisplay.getScopeGroup();

				if (curGroup.isLayout()) {
					scopeLayout = LayoutLocalServiceUtil.getLayout(curGroup.getClassPK());

					curGroup = scopeLayout.getGroup();
				}

				String curGroupName = null;

				if (curGroup.isCompany()) {
					curGroupName = LanguageUtil.get(pageContext, "global");
				}
				else if (curGroup.isUser() && (curGroup.getClassPK() == user.getUserId())) {
					curGroupName = LanguageUtil.get(pageContext, "my-community");
				}
				else {
					curGroupName = curGroup.getDescriptiveName();
				}

				if (category.equals(PortletCategoryKeys.CONTENT)) {
					PortalUtil.addPortletBreadcrumbEntry(request, curGroupName, null);
				}

				if (scopeLayout == null) {
					curGroupLabel = LanguageUtil.get(pageContext, "default");
				}
				else {
					curGroupLabel = scopeLayout.getName(locale);

					if (category.equals(PortletCategoryKeys.CONTENT)) {
						PortalUtil.addPortletBreadcrumbEntry(request, curGroupLabel, null);
					}
				}

				List<Layout> curGroupLayouts = new ArrayList<Layout>();

				curGroupLayouts.addAll(LayoutLocalServiceUtil.getLayouts(curGroup.getGroupId(), false));
				curGroupLayouts.addAll(LayoutLocalServiceUtil.getLayouts(curGroup.getGroupId(), true));

				for (Layout curGroupLayout : curGroupLayouts) {
					if (curGroupLayout.hasScopeGroup()) {
						scopeLayouts.add(curGroupLayout);
					}
				}

				title = "<a href=\"javascript:;\" class=\"lfr-floating-trigger lfr-group-selector\">" + HtmlUtil.escape(StringUtil.shorten(curGroupName, 25)) + "</a>";
			}
			else if (curCategory.equals(PortletCategoryKeys.PORTAL) && (CompanyLocalServiceUtil.getCompaniesCount(false) > 1)) {
				title = HtmlUtil.escape(company.getName());
			}
			else {
				title = LanguageUtil.get(pageContext, "category." + curCategory);
			}
		%>

			<c:if test="<%= curCategory.equals(PortletCategoryKeys.CONTENT) %>">
				<liferay-ui:panel-floating-container id="groupSelectorPanel" paging="<%= true %>" trigger=".lfr-group-selector">

					<%
					List<Group> manageableGroups = GroupServiceUtil.getManageableGroups(ActionKeys.MANAGE_LAYOUTS, PropsValues.CONTROL_PANEL_NAVIGATION_MAX_COMMUNITIES);
					List<Organization> manageableOrganizations = OrganizationServiceUtil.getManageableOrganizations(ActionKeys.MANAGE_LAYOUTS, PropsValues.CONTROL_PANEL_NAVIGATION_MAX_ORGANIZATIONS);
					%>

					<c:if test="<%= !manageableGroups.isEmpty() %>">
						<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" id="communitiesPanel" persistState="<%= true %>" title='<%= LanguageUtil.get(pageContext, "communities") %>'>
							<ul>

								<%
								for (int i = 0; i < manageableGroups.size(); i++) {
									Group group = manageableGroups.get(i);
								%>

									<c:if test="<%= (i != 0) && (i % 7 == 0 ) %>">
										</ul>
										<ul>
									</c:if>

									<li>
										<a href="<%= HttpUtil.setParameter(PortalUtil.getCurrentURL(request), "doAsGroupId", group.getGroupId()) %>"><%= (group.isUser() && (group.getClassPK() == user.getUserId())) ? LanguageUtil.get(pageContext, "my-community") : HtmlUtil.escape(group.getDescriptiveName()) %></a>
									</li>

								<%
								}
								%>

							</ul>
						</liferay-ui:panel>
					</c:if>

					<c:if test="<%= !manageableOrganizations.isEmpty() %>">
						<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" id="communitiesPanel" persistState="<%= true %>" title='<%= LanguageUtil.get(pageContext, "organizations") %>'>
							<ul>

								<%
								for (int i = 0; i < manageableOrganizations.size(); i++) {
									Organization organization = manageableOrganizations.get(i);
								%>

									<c:if test="<%= (i != 0) && (i % 7 == 0 ) %>">
										</ul>
										<ul>
									</c:if>

									<li>
										<a href="<%= HttpUtil.setParameter(PortalUtil.getCurrentURL(request), "doAsGroupId", organization.getGroup().getGroupId()) %>"><%= HtmlUtil.escape(organization.getName()) %></a>
									</li>

								<%
								}
								%>

							</ul>
						</liferay-ui:panel>
					</c:if>

					<%
					boolean showGlobal = permissionChecker.isCompanyAdmin();
					boolean showMyCommunity = user.getGroup().hasPrivateLayouts() || user.getGroup().hasPublicLayouts();
					%>

					<c:if test="<%= showGlobal || showMyCommunity %>">
						<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" id="sharedPanel" persistState="<%= true %>" title='<%= LanguageUtil.get(pageContext, "other[plural]") %>'>
							<ul>
								<c:if test="<%= showGlobal %>">
									<li>
										<a href="<%= HttpUtil.setParameter(PortalUtil.getCurrentURL(request), "doAsGroupId", themeDisplay.getCompanyGroupId()) %>"><liferay-ui:message key="global" /></a>
									</li>
								</c:if>
								<c:if test="<%= showMyCommunity %>">
									<li>
										<a href="<%= HttpUtil.setParameter(PortalUtil.getCurrentURL(request), "doAsGroupId", user.getGroup().getGroupId()) %>"><liferay-ui:message key="my-community" /></a>
									</li>
								</c:if>
							</ul>
						</liferay-ui:panel>
					</c:if>
				</liferay-ui:panel-floating-container>

				<c:if test="<%= !scopeLayouts.isEmpty() %>">
					<liferay-ui:panel-floating-container trigger=".lfr-scope-selector">
						<liferay-ui:panel title="">
							<ul>
								<li>
									<a href="<%= HttpUtil.setParameter(PortalUtil.getCurrentURL(request), "doAsGroupId", curGroup.getGroupId()) %>"><liferay-ui:message key="default" /></a>
								</li>

								<%
								for (Layout curScopeLayout : scopeLayouts) {
								%>

									<li>
										<a href="<%= HttpUtil.setParameter(PortalUtil.getCurrentURL(request), "doAsGroupId", curScopeLayout.getScopeGroup().getGroupId()) %>"><%= HtmlUtil.escape(curScopeLayout.getName(locale)) %></a>
									</li>

								<%
								}
								%>

							</ul>
						</liferay-ui:panel>
					</liferay-ui:panel-floating-container>
				</c:if>
			</c:if>

			<liferay-ui:panel collapsible="<%= true %>" cssClass="lfr-component panel-page-category" extended="<%= true %>" id='<%= "panel-manage-" + curCategory %>' persistState="<%= true %>" title="<%= title %>">
				<c:if test="<%= !scopeLayouts.isEmpty() && curCategory.equals(PortletCategoryKeys.CONTENT) %>">
					<span class="nobr lfr-title-scope-selector">
						<liferay-ui:message key="scope" /> <a href="javascript:;" class="lfr-scope-selector"><%= curGroupLabel %></a>
					</span>
				</c:if>

				<ul class="category-portlets">

					<%
					for (Portlet portlet : portlets) {
						if (portlet.isActive() && !portlet.isInstanceable()) {
					%>

							<li class="<%= ppid.equals(portlet.getPortletId()) ? "selected-portlet" : "" %>">
								<a href="<liferay-portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" portletName="<%= portlet.getRootPortletId() %>" />">
									<c:choose>
										<c:when test="<%= Validator.isNull(portlet.getIcon()) %>">
											<liferay-ui:icon src='<%= themeDisplay.getPathContext() + "/html/icons/default.png" %>' />
										</c:when>
										<c:otherwise>
											<liferay-portlet:icon-portlet portlet="<%= portlet %>" />
										</c:otherwise>
									</c:choose>

									<%= PortalUtil.getPortletTitle(portlet, application, locale) %>
								</a>
							</li>

					<%
						}
					}
					%>

				</ul>
			</liferay-ui:panel>

		<%
		}
		%>

	</liferay-ui:panel-container>
</div>

<aui:script use="lfr-panel-floating">
	var groupSelectorPanel = Liferay.Panel.get('groupSelectorPanel');

	if (groupSelectorPanel) {
		groupSelectorPanel.get('trigger').swallowEvent('mousedown');
	}
</aui:script>