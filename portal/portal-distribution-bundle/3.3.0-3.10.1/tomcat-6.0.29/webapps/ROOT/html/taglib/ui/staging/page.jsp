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

<%@ include file="/html/taglib/init.jsp" %>

<%@ page import="com.liferay.portlet.tasks.NoSuchProposalException" %>
<%@ page import="com.liferay.portlet.tasks.model.TasksProposal" %>
<%@ page import="com.liferay.portlet.tasks.service.TasksProposalLocalServiceUtil" %>

<c:if test="<%= themeDisplay.isShowStagingIcon() %>">

	<%
	Group group = themeDisplay.getScopeGroup();

	if (themeDisplay.getScopeGroup().isLayout()) {
		group = layout.getGroup();
	}

	String publishNowDialogTitle = "publish-to-live-now";
	String publishScheduleDialogTitle = "schedule-publication-to-live";

	Group liveGroup = null;
	Group stagingGroup = null;

	if (group.isStagingGroup()) {
		liveGroup = group.getLiveGroup();
		stagingGroup = group;
	}
	else if (group.isStaged()) {
		if (group.isStagedRemotely()) {
			liveGroup = group;
			stagingGroup = null;

			publishNowDialogTitle = "publish-to-remote-live-now";
			publishScheduleDialogTitle = "schedule-publication-to-remote-live";
		}
		else {
			liveGroup = group;
			stagingGroup = group.getStagingGroup();
		}
	}

	boolean workflowEnabled = liveGroup.isWorkflowEnabled();
	%>

	<ul>
		<c:if test="<%= liveGroup.isStaged() %>">
			<c:choose>
				<c:when test="<%= !liveGroup.isStagedRemotely() && group.isStagingGroup() %>">

					<%
					String friendlyURL = null;

					try {
						Layout liveLayout = LayoutLocalServiceUtil.getLayoutByUuidAndGroupId(layout.getUuid(), liveGroup.getGroupId());

						friendlyURL = PortalUtil.getLayoutFriendlyURL(liveLayout, themeDisplay);
						friendlyURL = PortalUtil.addPreservedParameters(themeDisplay, friendlyURL);
					}
					catch (Exception e) {
					}
					%>

					<c:if test="<%= Validator.isNotNull(friendlyURL) %>">
						<li class="page-settings">
							<a href="<%= friendlyURL %>"><liferay-ui:message key="view-live-page" /></a>
						</li>
					</c:if>
				</c:when>
				<c:when test="<%= !liveGroup.isStagedRemotely() && !group.isStagingGroup() %>">

					<%
					String friendlyURL = null;

					try {
						Layout stagedLayout = LayoutLocalServiceUtil.getLayoutByUuidAndGroupId(layout.getUuid(), stagingGroup.getGroupId());

						friendlyURL = PortalUtil.getLayoutFriendlyURL(stagedLayout, themeDisplay);
						friendlyURL = PortalUtil.addPreservedParameters(themeDisplay, friendlyURL);
					}
					catch (Exception e) {
					}
					%>

					<c:if test="<%= Validator.isNotNull(friendlyURL) %>">
						<li class="page-settings">
							<a href="<%= friendlyURL %>"><liferay-ui:message key="view-staged-page" /></a>
						</li>
					</c:if>
				</c:when>
			</c:choose>

			<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, liveGroup.getGroupId(), ActionKeys.MANAGE_LAYOUTS) || GroupPermissionUtil.contains(permissionChecker, liveGroup.getGroupId(), ActionKeys.PUBLISH_STAGING) || LayoutPermissionUtil.contains(permissionChecker, layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(), ActionKeys.UPDATE) %>">

				<%
				TasksProposal proposal = null;

				if (workflowEnabled) {
					try {
						proposal = TasksProposalLocalServiceUtil.getProposal(Layout.class.getName(), String.valueOf(layout.getPlid()));
					}
					catch (NoSuchProposalException nspe) {
					}
				}
				%>

				<c:if test="<%= liveGroup.isStagedRemotely() || group.isStagingGroup() %>">
					<c:choose>
						<c:when test="<%= workflowEnabled %>">
							<c:if test="<%= proposal == null %>">

								<%
								PortletURL proposePublicationURL = new PortletURLImpl(request, PortletKeys.LAYOUT_MANAGEMENT, layout.getPlid(), PortletRequest.ACTION_PHASE);

								proposePublicationURL.setWindowState(WindowState.MAXIMIZED);
								proposePublicationURL.setPortletMode(PortletMode.VIEW);

								proposePublicationURL.setParameter("struts_action", "/layout_management/edit_proposal");
								proposePublicationURL.setParameter(Constants.CMD, Constants.ADD);
								proposePublicationURL.setParameter("redirect", currentURL);
								proposePublicationURL.setParameter("groupId", String.valueOf(liveGroup.getGroupId()));
								proposePublicationURL.setParameter("className", Layout.class.getName());
								proposePublicationURL.setParameter("classPK", String.valueOf(layout.getPlid()));

								String[] workflowRoleNames = StringUtil.split(liveGroup.getWorkflowRoleNames());

								JSONArray jsonReviewers = JSONFactoryUtil.createJSONArray();

								Role role = RoleLocalServiceUtil.getRole(company.getCompanyId(), workflowRoleNames[0]);

								LinkedHashMap userParams = new LinkedHashMap();

								if (liveGroup.isOrganization()) {
									userParams.put("usersOrgs", new Long(liveGroup.getOrganizationId()));
								}
								else {
									userParams.put("usersGroups", new Long(liveGroup.getGroupId()));
								}

								userParams.put("userGroupRole", new Long[] {new Long(liveGroup.getGroupId()), new Long(role.getRoleId())});

								List<User> reviewers = UserLocalServiceUtil.search(company.getCompanyId(), null, null, userParams, QueryUtil.ALL_POS, QueryUtil.ALL_POS, (OrderByComparator)null);

								if (reviewers.isEmpty()) {
									if (liveGroup.isCommunity()) {
										role = RoleLocalServiceUtil.getRole(company.getCompanyId(), RoleConstants.COMMUNITY_OWNER);
									}
									else {
										role = RoleLocalServiceUtil.getRole(company.getCompanyId(), RoleConstants.ORGANIZATION_OWNER);
									}

									userParams.put("userGroupRole", new Long[] {new Long(liveGroup.getGroupId()), new Long(role.getRoleId())});

									reviewers = UserLocalServiceUtil.search(company.getCompanyId(), null, null, userParams, QueryUtil.ALL_POS, QueryUtil.ALL_POS, (OrderByComparator)null);
								}

								for (User reviewer : reviewers) {
									JSONObject jsonReviewer = JSONFactoryUtil.createJSONObject();

									jsonReviewer.put("userId", reviewer.getUserId());
									jsonReviewer.put("fullName", HtmlUtil.escape(reviewer.getFullName()));

									jsonReviewers.put(jsonReviewer);
								}
								%>

								<li class="page-settings">
									<a href="javascript:Liferay.LayoutExporter.proposeLayout({namespace: '<%= PortalUtil.getPortletNamespace(PortletKeys.LAYOUT_MANAGEMENT) %>', reviewers: <%= StringUtil.replace(jsonReviewers.toString(), '"', '\'') %>, title: '<liferay-ui:message key="proposal-description" />', url: '<%= proposePublicationURL.toString() %>'});"><liferay-ui:message key="propose-publication" /></a>
								</li>
							</c:if>
						</c:when>
						<c:when test="<%= themeDisplay.getURLPublishToLive() != null %>">

							<%
							PortletURL publishToLiveURL = themeDisplay.getURLPublishToLive();
							%>

							<li class="page-settings">
								<a href="javascript:Liferay.LayoutExporter.publishToLive({title: '<%= UnicodeLanguageUtil.get(pageContext, publishNowDialogTitle) %>', url: '<%= publishToLiveURL.toString() %>'});"><liferay-ui:message key="<%= publishNowDialogTitle %>" /></a>
							</li>

							<%
							publishToLiveURL.setParameter("schedule", String.valueOf(true));
							%>

							<li class="page-settings">
								<a href="javascript:Liferay.LayoutExporter.publishToLive({title: '<%= UnicodeLanguageUtil.get(pageContext, publishScheduleDialogTitle) %>', url: '<%= publishToLiveURL.toString() %>'});"><liferay-ui:message key="<%= publishScheduleDialogTitle %>" /></a>
							</li>
						</c:when>
					</c:choose>
				</c:if>
			</c:if>
		</c:if>

		<c:if test="<%= workflowEnabled %>">
			<li class="page-settings">

				<%
				Layout stagedLayout = null;

				if (stagingGroup != null) {
					stagedLayout = LayoutLocalServiceUtil.getLayoutByUuidAndGroupId(layout.getUuid(), stagingGroup.getGroupId());
				}
				else {
					stagedLayout = LayoutLocalServiceUtil.getLayoutByUuidAndGroupId(layout.getUuid(), liveGroup.getGroupId());
				}

				PortletURL viewProposalsURL = new PortletURLImpl(request, PortletKeys.LAYOUT_MANAGEMENT, stagedLayout.getPlid(), PortletRequest.RENDER_PHASE);

				viewProposalsURL.setWindowState(WindowState.MAXIMIZED);
				viewProposalsURL.setPortletMode(PortletMode.VIEW);

				viewProposalsURL.setParameter("struts_action", "/layout_management/edit_pages");
				viewProposalsURL.setParameter("tabs2", "proposals");
				viewProposalsURL.setParameter("redirect", currentURL);
				viewProposalsURL.setParameter("groupId", String.valueOf(liveGroup.getGroupId()));
				%>

				<a href="<%= viewProposalsURL.toString() %>"><liferay-ui:message key="view-proposals" /></a>
			</li>
		</c:if>
	</ul>
</c:if>