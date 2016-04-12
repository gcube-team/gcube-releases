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

<%@ include file="/html/portlet/portlet_configuration/init.jsp" %>

<%
String tabs2 = ParamUtil.getString(request, "tabs2", "regular-roles");

String redirect = ParamUtil.getString(request, "redirect");
String returnToFullPageURL = ParamUtil.getString(request, "returnToFullPageURL");

String modelResource = ParamUtil.getString(request, "modelResource");
String modelResourceDescription = ParamUtil.getString(request, "modelResourceDescription");
String modelResourceName = ResourceActionsUtil.getModelResource(pageContext, modelResource);

String resourcePrimKey = ParamUtil.getString(request, "resourcePrimKey");

if (Validator.isNull(resourcePrimKey)) {
	throw new ResourcePrimKeyException();
}

String selResource = modelResource;
String selResourceDescription = modelResourceDescription;
String selResourceName = modelResourceName;

if (Validator.isNull(modelResource)) {
	PortletURL portletURL = new PortletURLImpl(request, portletResource, plid, PortletRequest.ACTION_PHASE);

	portletURL.setWindowState(WindowState.NORMAL);
	portletURL.setPortletMode(PortletMode.VIEW);

	redirect = portletURL.toString();

	Portlet portlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), portletResource);

	selResource = portlet.getRootPortletId();
	selResourceDescription = PortalUtil.getPortletTitle(portlet, application, locale);
	selResourceName = LanguageUtil.get(pageContext, "portlet");
}
else {
	PortalUtil.addPortletBreadcrumbEntry(request, selResourceDescription, null);
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "permissions"), currentURL);
}

Group group = themeDisplay.getScopeGroup();
long groupId = group.getGroupId();

Layout selLayout = null;

if (modelResource.equals(Layout.class.getName())) {
	selLayout = LayoutLocalServiceUtil.getLayout(GetterUtil.getLong(resourcePrimKey));

	group = selLayout.getGroup();
	groupId = group.getGroupId();
}

Resource resource = null;

try {
	if (PropsValues.PERMISSIONS_USER_CHECK_ALGORITHM == 6) {
		if (ResourcePermissionLocalServiceUtil.getResourcePermissionsCount(company.getCompanyId(), selResource, ResourceConstants.SCOPE_INDIVIDUAL, resourcePrimKey) == 0) {
			throw new NoSuchResourceException();
		}
	}

	resource = ResourceLocalServiceUtil.getResource(company.getCompanyId(), selResource, ResourceConstants.SCOPE_INDIVIDUAL, resourcePrimKey);
}
catch (NoSuchResourceException nsre) {
	boolean portletActions = Validator.isNull(modelResource);

	ResourceLocalServiceUtil.addResources(company.getCompanyId(), groupId, 0, selResource, resourcePrimKey, portletActions, true, true);

	resource = ResourceLocalServiceUtil.getResource(company.getCompanyId(), selResource, ResourceConstants.SCOPE_INDIVIDUAL, resourcePrimKey);
}

PortletURL actionPortletURL = renderResponse.createActionURL();

actionPortletURL.setParameter("struts_action", "/portlet_configuration/edit_permissions");
actionPortletURL.setParameter("tabs2", tabs2);
actionPortletURL.setParameter("redirect", redirect);
actionPortletURL.setParameter("returnToFullPageURL", returnToFullPageURL);
actionPortletURL.setParameter("portletResource", portletResource);
actionPortletURL.setParameter("modelResource", modelResource);
actionPortletURL.setParameter("modelResourceDescription", modelResourceDescription);
actionPortletURL.setParameter("resourcePrimKey", resourcePrimKey);

PortletURL renderPortletURL = renderResponse.createRenderURL();

renderPortletURL.setParameter("struts_action", "/portlet_configuration/edit_permissions");
renderPortletURL.setParameter("tabs2", tabs2);
renderPortletURL.setParameter("redirect", redirect);
renderPortletURL.setParameter("returnToFullPageURL", returnToFullPageURL);
renderPortletURL.setParameter("portletResource", portletResource);
renderPortletURL.setParameter("modelResource", modelResource);
renderPortletURL.setParameter("modelResourceDescription", modelResourceDescription);
renderPortletURL.setParameter("resourcePrimKey", resourcePrimKey);

Group controlPanelGroup = GroupLocalServiceUtil.getGroup(company.getCompanyId(), GroupConstants.CONTROL_PANEL);

long controlPanelPlid = LayoutLocalServiceUtil.getDefaultPlid(controlPanelGroup.getGroupId(), true);

PortletURLImpl definePermissionsURL = new PortletURLImpl(request, PortletKeys.ENTERPRISE_ADMIN_ROLES, controlPanelPlid, PortletRequest.RENDER_PHASE);

definePermissionsURL.setPortletMode(PortletMode.VIEW);

definePermissionsURL.setRefererPlid(plid);

definePermissionsURL.setParameter("struts_action", "/enterprise_admin_roles/edit_role_permissions");
definePermissionsURL.setParameter(Constants.CMD, Constants.VIEW);
%>

<div class="edit-permissions">
	<aui:form action="<%= actionPortletURL.toString() %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="role_permissions" />
		<aui:input name="resourceId" type="hidden" value="<%= resource.getResourceId() %>" />

		<c:choose>
			<c:when test="<%= Validator.isNull(modelResource) %>">
				<liferay-util:include page="/html/portlet/portlet_configuration/tabs1.jsp">
					<liferay-util:param name="tabs1" value="permissions" />
				</liferay-util:include>
			</c:when>
			<c:otherwise>
				<liferay-ui:header
					backURL="<%= redirect %>"
					title="<%= selResourceDescription %>"
			   />
			</c:otherwise>
		</c:choose>

		<%
		List<String> actions = ResourceActionsUtil.getResourceActions(portletResource, modelResource);

		List<Role> roles = ResourceActionsUtil.getRoles(company.getCompanyId(), group, modelResource);

		Role administrator = RoleLocalServiceUtil.getRole(company.getCompanyId(), RoleConstants.ADMINISTRATOR);

		roles.remove(administrator);

		if (group.isCommunity()) {
			Role communityAdministrator = RoleLocalServiceUtil.getRole(company.getCompanyId(), RoleConstants.COMMUNITY_ADMINISTRATOR);
			Role communityOwner = RoleLocalServiceUtil.getRole(company.getCompanyId(), RoleConstants.COMMUNITY_OWNER);

			roles.remove(communityAdministrator);
			roles.remove(communityOwner);
		}
		else if (group.isOrganization()) {
			Role organizationAdministrator = RoleLocalServiceUtil.getRole(company.getCompanyId(), RoleConstants.ORGANIZATION_ADMINISTRATOR);
			Role organizationOwner = RoleLocalServiceUtil.getRole(company.getCompanyId(), RoleConstants.ORGANIZATION_OWNER);

			roles.remove(organizationAdministrator);
			roles.remove(organizationOwner);
		}

		if (group.isCommunity() || group.isOrganization()) {
			List<Team> teams = TeamLocalServiceUtil.getGroupTeams(groupId);

			for (Team team : teams) {
				Role role = RoleLocalServiceUtil.getTeamRole(team.getCompanyId(), team.getTeamId());

				roles.add(role);
			}
		}
		%>

		<liferay-ui:search-container id="rolesSearchContainer">
			<liferay-ui:search-container-results
				results="<%= roles %>"
				total="<%= roles.size() %>"
			/>

			<liferay-ui:search-container-row
				className="com.liferay.portal.model.Role"
				escapedModel="<%= true %>"
				keyProperty="roleId"
				modelVar="role"
			>
				<liferay-util:param name="className" value="<%= EnterpriseAdminUtil.getCssClassName(role) %>" />
				<liferay-util:param name="classHoverName" value="<%= EnterpriseAdminUtil.getCssClassName(role) %>" />

				<%
				String definePermissionsHREF = null;

				String name = role.getName();

				if (!name.equals(RoleConstants.ADMINISTRATOR) && !name.equals(RoleConstants.COMMUNITY_ADMINISTRATOR) && !name.equals(RoleConstants.COMMUNITY_OWNER) && !name.equals(RoleConstants.ORGANIZATION_ADMINISTRATOR) && !name.equals(RoleConstants.ORGANIZATION_OWNER) && !name.equals(RoleConstants.OWNER) && RolePermissionUtil.contains(permissionChecker, role.getRoleId(), ActionKeys.DEFINE_PERMISSIONS)) {
					definePermissionsURL.setParameter("roleId", String.valueOf(role.getRoleId()));

					definePermissionsHREF = definePermissionsURL.toString();
				}
				%>

				<liferay-ui:search-container-column-text
					href="<%= definePermissionsHREF %>"
					name="role"
					value="<%= HtmlUtil.escape(role.getTitle(locale)) %>"
				/>

				<%

				// Actions

				List<String> currentIndividualActions = null;
				List<String> currentGroupActions = null;
				List<String> currentGroupTemplateActions = null;
				List<String> currentCompanyActions = null;

				if (PropsValues.PERMISSIONS_USER_CHECK_ALGORITHM == 6) {
					currentIndividualActions = ResourcePermissionLocalServiceUtil.getAvailableResourcePermissionActionIds(resource.getCompanyId(), resource.getName(), resource.getScope(), resource.getPrimKey(), role.getRoleId(), actions);
					currentGroupActions = ResourcePermissionLocalServiceUtil.getAvailableResourcePermissionActionIds(resource.getCompanyId(), resource.getName(), ResourceConstants.SCOPE_GROUP, String.valueOf(groupId), role.getRoleId(), actions);
					currentGroupTemplateActions = ResourcePermissionLocalServiceUtil.getAvailableResourcePermissionActionIds(resource.getCompanyId(), resource.getName(), ResourceConstants.SCOPE_GROUP_TEMPLATE, "0", role.getRoleId(), actions);
					currentCompanyActions = ResourcePermissionLocalServiceUtil.getAvailableResourcePermissionActionIds(resource.getCompanyId(), resource.getName(), ResourceConstants.SCOPE_COMPANY, String.valueOf(resource.getCompanyId()), role.getRoleId(), actions);
				}
				else {
					List<Permission> permissions = PermissionLocalServiceUtil.getRolePermissions(role.getRoleId(), resource.getResourceId());

					currentIndividualActions = ResourceActionsUtil.getActions(permissions);

					try {
						Resource groupResource = ResourceLocalServiceUtil.getResource(resource.getCompanyId(), resource.getName(), ResourceConstants.SCOPE_GROUP, String.valueOf(groupId));

						permissions = PermissionLocalServiceUtil.getRolePermissions(role.getRoleId(), groupResource.getResourceId());

						currentGroupActions = ResourceActionsUtil.getActions(permissions);
					}
					catch (NoSuchResourceException nsre) {
						currentGroupActions = new ArrayList<String>();
					}

					try {
						Resource groupTemplateResource = ResourceLocalServiceUtil.getResource(resource.getCompanyId(), resource.getName(), ResourceConstants.SCOPE_GROUP_TEMPLATE, "0");

						permissions = PermissionLocalServiceUtil.getRolePermissions(role.getRoleId(), groupTemplateResource.getResourceId());

						currentGroupTemplateActions = ResourceActionsUtil.getActions(permissions);
					}
					catch (NoSuchResourceException nsre) {
						currentGroupTemplateActions = new ArrayList();
					}

					try {
						Resource companyResource = ResourceLocalServiceUtil.getResource(resource.getCompanyId(), resource.getName(), ResourceConstants.SCOPE_COMPANY, String.valueOf(resource.getCompanyId()));

						permissions = PermissionLocalServiceUtil.getRolePermissions(role.getRoleId(), companyResource.getResourceId());

						currentCompanyActions = ResourceActionsUtil.getActions(permissions);
					}
					catch (NoSuchResourceException nsre) {
						currentCompanyActions = new ArrayList();
					}
				}

				List<String> currentActions = new ArrayList<String>();

				currentActions.addAll(currentIndividualActions);
				currentActions.addAll(currentGroupActions);
				currentActions.addAll(currentGroupTemplateActions);
				currentActions.addAll(currentCompanyActions);

				List<String> guestUnsupportedActions = ResourceActionsUtil.getResourceGuestUnsupportedActions(portletResource, modelResource);

				for (String action : actions) {
					boolean checked = false;
					boolean disabled = false;
					String preselectedMsg = StringPool.BLANK;

					if (currentIndividualActions.contains(action)) {
						checked = true;
					}

					if (currentGroupActions.contains(action) || currentGroupTemplateActions.contains(action)) {
						checked = true;
						preselectedMsg = "x-is-allowed-to-do-action-x-in-all-items-of-type-x-in-x";
					}

					if (currentCompanyActions.contains(action)) {
						checked = true;
						preselectedMsg = "x-is-allowed-to-do-action-x-in-all-items-of-type-x-in-this-portal-instance";
					}

					if (name.equals(RoleConstants.GUEST) && guestUnsupportedActions.contains(action)) {
						disabled = true;
					}
				%>

					<liferay-ui:search-container-column-text
						buffer="buffer"
						name="<%= ResourceActionsUtil.getAction(pageContext, action) %>"
					>

						<%
						buffer.append("<input ");

						if (checked) {
							buffer.append("checked ");
						}

						if (Validator.isNotNull(preselectedMsg)) {
							buffer.append("class=\"lfr-checkbox-preselected\" ");
						}

						if (disabled) {
							buffer.append("disabled ");
						}

						buffer.append("name=\"");
						buffer.append(role.getRoleId());

						if (Validator.isNotNull(preselectedMsg)) {
							buffer.append("_PRESELECTED_");
						}
						else {
							buffer.append("_ACTION_");
						}

						buffer.append(action);
						buffer.append("\" ");

						if (Validator.isNotNull(preselectedMsg)) {
							buffer.append("onclick=\"return false;\" onmouseover=\"Liferay.Portal.ToolTip.show(this, '");
							buffer.append(UnicodeLanguageUtil.format(pageContext, preselectedMsg, new Object[] {HtmlUtil.escape(role.getTitle(locale)), ResourceActionsUtil.getAction(pageContext, action), LanguageUtil.get(pageContext, ResourceActionsUtil.MODEL_RESOURCE_NAME_PREFIX + resource.getName()), HtmlUtil.escape(group.getDescriptiveName())}));
							buffer.append("'); return false;\" ");
						}

						buffer.append("type=\"checkbox\" />");
						%>

					</liferay-ui:search-container-column-text>
				<%
				}
				%>

			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" paginate="<%= false %>" />
		</liferay-ui:search-container>

		<br />

		<aui:button-row>
			<aui:button type="submit" />
	 	</aui:button-row>
	</aui:form>
</div>