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

<%@ include file="/html/portlet/communities/init.jsp" %>

<%
Group liveGroup = (Group)request.getAttribute("edit_pages.jsp-liveGroup");
Group stagingGroup = (Group)request.getAttribute("edit_pages.jsp-stagingGroup");
long liveGroupId = ((Long)request.getAttribute("edit_pages.jsp-liveGroupId")).longValue();
UnicodeProperties liveGroupTypeSettings = (UnicodeProperties)request.getAttribute("edit_pages.jsp-liveGroupTypeSettings");

boolean workflowEnabled = ((Boolean)request.getAttribute("edit_pages.jsp-workflowEnabled")).booleanValue();
int workflowStages = ((Integer)request.getAttribute("edit_pages.jsp-workflowStages")).intValue();
String[] workflowRoleNames = (String[])request.getAttribute("edit_pages.jsp-workflowRoleNames");
%>

<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, liveGroupId, ActionKeys.MANAGE_STAGING) %>">
	<liferay-ui:error exception="<%= SystemException.class %>">

		<%
		SystemException se = (SystemException)errorException;
		%>

		<liferay-ui:message key="<%= se.getMessage() %>" />
	</liferay-ui:error>

	<div class="portlet-msg-info">
		<liferay-ui:message key="staging-type-help-1" />

		<ul>
			<li>
				<liferay-ui:message key="staging-type-help-2" />
			</li>
			<li>
				<liferay-ui:message key="staging-type-help-3" />
			</li>
		</ul>
	</div>

	<aui:select label="staging-type" name="stagingType">
		<aui:option selected="<%= !liveGroup.isStaged() %>" value="<%= StagingConstants.TYPE_NOT_STAGED %>"><liferay-ui:message key="none" /></aui:option>
		<aui:option selected="<%= liveGroup.isStaged() && !liveGroup.isStagedRemotely() %>" value="<%= StagingConstants.TYPE_LOCAL_STAGING %>"><liferay-ui:message key="local-live" /></aui:option>
		<aui:option selected="<%= liveGroup.isStaged() && liveGroup.isStagedRemotely() %>" value="<%= StagingConstants.TYPE_REMOTE_STAGING %>"><liferay-ui:message key="remote-live" /></aui:option>
	</aui:select>

	<div class='<%= (liveGroup.isStaged() && liveGroup.isStagedRemotely() ? StringPool.BLANK : "aui-helper-hidden") %>' id="<portlet:namespace />remoteStagingOptions">
		<br />

		<liferay-ui:error exception="<%= RemoteExportException.class %>">

			<%
			RemoteExportException ree = (RemoteExportException)errorException;
			%>

			<c:if test="<%= ree.getType() == RemoteExportException.BAD_CONNECTION %>">
				<liferay-ui:message arguments="<%= ree.getURL() %>" key="there-was-a-bad-connection-with-the-remote-server-at-x" />
			</c:if>

			<c:if test="<%= ree.getType() == RemoteExportException.NO_GROUP %>">

				<%
				String groupType = LanguageUtil.get(pageContext, (liveGroup.isOrganization()? "organization" : "community"));
				%>

				<liferay-ui:message arguments="<%= new Object[] {groupType, ree.getGroupId()} %>" key="no-group-exists-on-the-remote-server-with-group-id-x" />
			</c:if>
		</liferay-ui:error>

		<aui:fieldset label="remote-live-connection-settings">
			<liferay-ui:error exception="<%= RemoteOptionsException.class %>">

				<%
				RemoteOptionsException roe = (RemoteOptionsException)errorException;
				%>

				<c:if test="<%= roe.getType() == RemoteOptionsException.REMOTE_ADDRESS %>">
					<liferay-ui:message arguments="<%= roe.getRemoteAddress() %>" key="the-remote-address-x-is-not-valid" />
				</c:if>

				<c:if test="<%= roe.getType() == RemoteOptionsException.REMOTE_GROUP_ID %>">
					<liferay-ui:message arguments="<%= roe.getRemoteGroupId() %>" key="the-remote-group-id-x-is-not-valid" />
				</c:if>

				<c:if test="<%= roe.getType() == RemoteOptionsException.REMOTE_PORT %>">
					<liferay-ui:message arguments="<%= roe.getRemotePort() %>" key="the-remote-port-x-is-not-valid" />
				</c:if>
			</liferay-ui:error>

			<div class="portlet-msg-info">
				<liferay-ui:message key="remote-publish-help" />
			</div>

			<aui:input label="remote-host-ip" name="remoteAddress" size="20" type="text" value='<%= liveGroupTypeSettings.getProperty("remoteAddress") %>' />

			<aui:input label="port" name="remotePort" size="10" type="text" value='<%= liveGroupTypeSettings.getProperty("remotePort") %>' />

			<aui:input label='<%= LanguageUtil.get(pageContext, "remote-group-id" ) + " (" + LanguageUtil.get(pageContext, "organization-or-community") + ")" %>' name="remoteGroupId" size="10" type="text" value='<%= liveGroupTypeSettings.getProperty("remoteGroupId") %>' />

			<aui:input inlineLabel="left" label="use-a-secure-network-connection" name="secureConnection" type="checkbox" value='<%= liveGroupTypeSettings.getProperty("secureConnection") %>' />
		</aui:fieldset>
	</div>

	<div class='<%= (liveGroup.isStaged() ? StringPool.BLANK : "aui-helper-hidden") %>' id="<portlet:namespace />stagedPortlets">
		<br />

		<aui:fieldset label="staged-portlets">
			<div class="portlet-msg-alert">
				<liferay-ui:message key="staged-portlets-alert" />
			</div>

			<div class="portlet-msg-info">
				<liferay-ui:message key="staged-portlets-help" />
			</div>

			<%
			List<Portlet> portlets = PortletLocalServiceUtil.getPortlets(company.getCompanyId());

			portlets = ListUtil.sort(portlets, new PortletTitleComparator(application, locale));

			for (Portlet curPortlet : portlets) {
				if (!curPortlet.isActive()) {
					continue;
				}

				PortletDataHandler portletDataHandler = curPortlet.getPortletDataHandlerInstance();

				if (portletDataHandler == null) {
					continue;
				}

				boolean isStaged = GetterUtil.getBoolean(liveGroupTypeSettings.getProperty(StagingConstants.STAGED_PORTLET + curPortlet.getRootPortletId()), portletDataHandler.isPublishToLiveByDefault());
			%>

				<aui:input disabled="<%= portletDataHandler.isAlwaysExportable() %>" inlineLabel="right" label="<%= PortalUtil.getPortletTitle(curPortlet, application, locale) %>" name='<%= StagingConstants.STAGED_PORTLET + curPortlet.getRootPortletId() %>' type="checkbox" value="<%= isStaged %>" />

			<%
			}
			%>

		</aui:fieldset>
	</div>

	<br />

	<div class='<%= (workflowEnabled ? StringPool.BLANK : "aui-helper-hidden") %>' id="<portlet:namespace />advancedOptions">
		<aui:fieldset label="advanced-options">
			<aui:field-wrapper>
				<aui:select inlineField="<%= true %>" inlineLabel="left" label="number-of-editorial-stages" name="workflowStages">

					<aui:option selected="<%= (1 == workflowStages) %>" value="1">1</aui:option>

					<%
					for (int i = 3; i <= 6; i++) {
					%>

						<aui:option selected="<%= (i == (workflowStages + 1)) %>" value="<%= (i - 1) %>"><%= i %></aui:option>

					<%
					}
					%>

				</aui:select>
			</aui:field-wrapper>

			<div class='<%= ((workflowStages == 1) ? "aui-helper-hidden": StringPool.BLANK) %>' id="<portlet:namespace />workflowStage_0">
				<br />

				<div class="portlet-msg-info">
					<c:choose>
						<c:when test="<%= liveGroup.isOrganization() %>">
							<liferay-ui:message key="stage-organization-permissions-reference-help" />
						</c:when>
						<c:otherwise>
							<liferay-ui:message key="stage-community-permissions-reference-help" />
						</c:otherwise>
					</c:choose>
				</div>

				<aui:field-wrapper>
					<strong><%= LanguageUtil.get(pageContext, "creation-stage") %></strong> <liferay-ui:icon-help message="stage-1-role-help" />

					<br />

					<%= LanguageUtil.get(pageContext, "content-creators") %>
				</aui:field-wrapper>

				<%
				int roleType = liveGroup.isOrganization() ? RoleConstants.TYPE_ORGANIZATION : RoleConstants.TYPE_COMMUNITY;

				List<Role> workflowRoles = RoleLocalServiceUtil.search(company.getCompanyId(), null, null, new Integer[] {roleType}, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

				for (int i = 1; i <= 4; i++) {
					String helpMessage = "stage-review-role-help";
					String label = "review-stage-x";

					if (i == 1) {
						helpMessage = "stage-2-role-help";
						label = "task-leader-stage";
					}
				%>

					<div class='<%= ((i >= workflowStages) ? "aui-helper-hidden": StringPool.BLANK) %>' id="<portlet:namespace />workflowStage_<%= i %>">
						<aui:select helpMessage="<%= helpMessage %>" label='<%= LanguageUtil.format(pageContext, label, String.valueOf(i - 1)) %>' name='<%= "workflowRoleName_" + i %>'>

							<%
							for (Role workflowRole : workflowRoles) {
								String workflowRoleName = workflowRole.getName();

								if (!workflowRoleName.equals(RoleConstants.COMMUNITY_MEMBER) && !workflowRoleName.equals(RoleConstants.ORGANIZATION_MEMBER)) {
							%>

									<aui:option selected="<%= (((i - 1) < workflowRoleNames.length) && workflowRoleNames[i - 1].equals(workflowRoleName)) %>" value="<%= HtmlUtil.escape(workflowRoleName) %>"><%= HtmlUtil.escape(workflowRole.getTitle(locale)) %></aui:option>

							<%
								}
							}
							%>

						</aui:select>
					</div>

				<%
				}
				%>

				<aui:select helpMessage="stage-last-role-help" label='<%= LanguageUtil.get(pageContext, "approval-stage") %>' name="workflowRoleName_Last">

					<%
					for (Role workflowRole : workflowRoles) {
						String workflowRoleName = workflowRole.getName();

						if (!workflowRoleName.equals(RoleConstants.COMMUNITY_MEMBER) && !workflowRoleName.equals(RoleConstants.ORGANIZATION_MEMBER)) {
					%>

							<aui:option selected="<%= (workflowRoleNames[workflowRoleNames.length - 1].equals(workflowRoleName)) %>" value="<%= HtmlUtil.escape(workflowRoleName) %>"><%= HtmlUtil.escape(workflowRole.getTitle(locale)) %></aui:option>

					<%
						}
					}
					%>

				</aui:select>
			</div>
		</aui:fieldset>
	</div>

	<aui:button-row>
		<aui:button last="true" name="saveButton" onClick='<%= renderResponse.getNamespace() + "updateStaging();" %>' type="submit" />
	</aui:button-row>

	<aui:script>
		Liferay.provide(
			Liferay.Util,
			'toggleSelectBoxReverse',
			function(selectBoxId, value, toggleBoxId) {
				var A = AUI();

				var selectBox = A.one('#' + selectBoxId);
				var toggleBox = A.one('#' + toggleBoxId);

				if (selectBox && toggleBox) {
					var toggle = function() {
						var action = 'hide';

						if (selectBox.val() != value) {
							action = 'show';
						}

						toggleBox[action]();
					};

					toggle();

					selectBox.on('change', toggle);
				}
			},
			['aui-base']
		);

		Liferay.provide(
			Liferay.Util,
			'toggleSelectBoxCustom',
			function(selectBoxId, toggleBoxId) {
				var A = AUI();

				var selectBox = A.one('#' + selectBoxId);
				var toggleBox0 = A.one('#' + toggleBoxId + '0');
				var toggleBox1 = A.one('#' + toggleBoxId + '1');
				var toggleBox2 = A.one('#' + toggleBoxId + '2');
				var toggleBox3 = A.one('#' + toggleBoxId + '3');
				var toggleBox4 = A.one('#' + toggleBoxId + '4');

				if (selectBox) {
					var toggle = function() {
						if (selectBox.val() == '1') {
							toggleBox0['hide']();
							toggleBox1['hide']();
							toggleBox2['hide']();
							toggleBox3['hide']();
							toggleBox4['hide']();
						}
						else if (selectBox.val() == '2') {
							toggleBox0['show']();
							toggleBox1['show']();
							toggleBox2['hide']();
							toggleBox3['hide']();
							toggleBox4['hide']();
						}
						else if (selectBox.val() == '3') {
							toggleBox0['show']();
							toggleBox1['show']();
							toggleBox2['show']();
							toggleBox3['hide']();
							toggleBox4['hide']();
						}
						else if (selectBox.val() == '4') {
							toggleBox0['show']();
							toggleBox1['show']();
							toggleBox2['show']();
							toggleBox3['show']();
							toggleBox4['hide']();
						}
						else if (selectBox.val() == '5') {
							toggleBox0['show']();
							toggleBox1['show']();
							toggleBox2['show']();
							toggleBox3['show']();
							toggleBox4['show']();
						}
					};

					toggle();

					selectBox.on('change', toggle);
				}
			},
			['aui-base']
		);

		Liferay.Util.toggleSelectBoxCustom('<portlet:namespace />workflowStages','<portlet:namespace />workflowStage_');

		Liferay.Util.toggleSelectBoxReverse('<portlet:namespace />stagingType','<%= StagingConstants.TYPE_NOT_STAGED %>','<portlet:namespace />stagedPortlets');
		Liferay.Util.toggleSelectBoxReverse('<portlet:namespace />stagingType','<%= StagingConstants.TYPE_NOT_STAGED %>','<portlet:namespace />stagingOptions');
		Liferay.Util.toggleSelectBoxReverse('<portlet:namespace />stagingType','<%= StagingConstants.TYPE_NOT_STAGED %>','<portlet:namespace />advancedOptions');
		Liferay.Util.toggleSelectBox('<portlet:namespace />stagingType','<%= StagingConstants.TYPE_REMOTE_STAGING %>','<portlet:namespace />remoteStagingOptions');
	</aui:script>
</c:if>