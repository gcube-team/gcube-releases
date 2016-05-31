<%/**
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
String redirect = ParamUtil.getString(request, "redirect");

Group liveGroup = (Group)request.getAttribute(WebKeys.GROUP);
Group stagingGroup = null;

if (liveGroup.hasStagingGroup()) {
	stagingGroup = liveGroup.getStagingGroup();
}
else if (liveGroup.isStagingGroup()) {
	stagingGroup = liveGroup;
	liveGroup = liveGroup.getLiveGroup();
}

long liveGroupId = liveGroup.getGroupId();

long stagingGroupId = 0;

if (stagingGroup != null) {
	stagingGroupId = stagingGroup.getGroupId();
}

int workflowStages = liveGroup.getWorkflowStages();
String[] workflowRoleNames = StringUtil.split(liveGroup.getWorkflowRoleNames());

TasksProposal proposal = (TasksProposal)request.getAttribute(WebKeys.TASKS_PROPOSAL);

long proposalId = proposal.getProposalId();

String className = PortalUtil.getClassName(proposal.getClassNameId());
String classPK = proposal.getClassPK();

Calendar dueDate = CalendarFactoryUtil.getCalendar(timeZone, locale);

dueDate.add(Calendar.MONTH, 9);

if (proposal.getDueDate() != null) {
	dueDate.setTime(proposal.getDueDate());
}

TasksReview review = null;

try {
	review = TasksReviewLocalServiceUtil.getReview(user.getUserId(), proposalId);
}
catch (NoSuchReviewException nsre) {
}

PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("struts_action", "/communities/edit_proposal");
portletURL.setParameter("redirect", redirect);
portletURL.setParameter("groupId", String.valueOf(liveGroupId));
portletURL.setParameter("proposalId", String.valueOf(proposalId));
%>

<portlet:actionURL var="editProposalURL">
	<portlet:param name="struts_action" value="/communities/edit_proposal" />
</portlet:actionURL>

<aui:form action="<%= editProposalURL %>" method="post" name="fm1" onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveProposal();" %>'>
	<aui:input name="<%= Constants.CMD %>" type="hidden" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="groupId" type="hidden" value="<%= liveGroupId %>" />
	<aui:input name="proposalId" type="hidden" value="<%= proposalId %>" />

	<%
	for (int i = 2; i <= workflowStages; i++) {
		String workflowRoleName = workflowRoleNames[i - 1];
	%>

		<aui:input name='<%= "reviewUserIds_" + i %>' type="hidden" />

	<%
	}
	%>

	<liferay-ui:header
		backURL="<%= redirect %>"
		title="<%= proposal.getName() %>"
	/>

	<liferay-ui:error exception="<%= DuplicateReviewUserIdException.class %>" message="users-cannot-be-assigned-to-more-than-one-stage" />

	<aui:model-context bean="<%= proposal %>" model="<%= TasksProposal.class %>" />

	<aui:fieldset>
		<aui:field-wrapper label="user">
			<%= HtmlUtil.escape(PortalUtil.getUserName(proposal.getUserId(), proposal.getUserName())) %>
		</aui:field-wrapper>

		<aui:field-wrapper label="name">
			<%= proposal.getName() %>
		</aui:field-wrapper>

		<aui:field-wrapper label="type">
			<%= LanguageUtil.get(pageContext, "model.resource." + className) %>
		</aui:field-wrapper>

		<aui:field-wrapper label="id">
			<%= classPK %>
		</aui:field-wrapper>

		<aui:field-wrapper label="status">
			<%= proposal.getStatus(locale) %>
		</aui:field-wrapper>

		<aui:input name="description" />

		<aui:input formName="fm1" name="dueDate" value="<%= dueDate %>" />
	</aui:fieldset>

	<c:if test="<%= (review != null) && (review.getStage() == 1) && GroupPermissionUtil.contains(permissionChecker, liveGroupId, ActionKeys.ASSIGN_REVIEWER) %>">
		<br />

		<liferay-ui:toggle-area
			id="toggle_id_communities_edit_proposal_reviewers"
			showMessage='<%= LanguageUtil.get(pageContext, "show-assign-reviewers") + " &raquo;" %>'
			hideMessage='<%= "&laquo; " + LanguageUtil.get(pageContext, "hide-assign-reviewers") %>'
		>
			<table class="lfr-table">

			<%
			for (int i = 2; i <= workflowStages; i++) {
				String workflowRoleName = workflowRoleNames[i - 1];
			%>

				<tr>
					<td colspan="3">
						<br />
					</td>
				</tr>
				<tr>
					<td>
						<liferay-ui:message key="stage" /> <%= i + 1 %>:

						<%= workflowRoleName %>
					</td>
					<td>

						<%

						// Left list

						List leftList = new ArrayList();

						String reviewUserIdsParam = request.getParameter("reviewUserIds_" + i);

						if (reviewUserIdsParam == null) {
							List<TasksReview> reviews = TasksReviewLocalServiceUtil.getReviews(proposal.getProposalId(), i);

							for (TasksReview curReview : reviews) {
								leftList.add(new KeyValuePair(String.valueOf(curReview.getUserId()), HtmlUtil.escape(PortalUtil.getUserName(curReview.getUserId(), curReview.getUserName()))));
							}
						}
						else {
							long[] reviewUserIds = StringUtil.split(reviewUserIdsParam, 0L);

							for (long reviewUserId : reviewUserIds) {
								User reviewUser = UserLocalServiceUtil.getUserById(reviewUserId);

								leftList.add(new KeyValuePair(String.valueOf(reviewUser.getUserId()), HtmlUtil.escape(PortalUtil.getUserName(reviewUser.getUserId(), reviewUser.getFullName()))));
							}
						}

						leftList = ListUtil.sort(leftList, new KeyValuePairComparator(false, true));

						// Right list

						List rightList = new ArrayList();

						Role role = RoleLocalServiceUtil.getRole(company.getCompanyId(), workflowRoleName);

						LinkedHashMap userParams = new LinkedHashMap();

						if (liveGroup.isOrganization()) {
							userParams.put("usersOrgs", new Long(liveGroup.getOrganizationId()));
						}
						else {
							userParams.put("usersGroups", new Long(liveGroupId));
						}

						userParams.put("userGroupRole", new Long[] {new Long(liveGroupId), new Long(role.getRoleId())});

						List<User> reviewers = UserLocalServiceUtil.search(company.getCompanyId(), null, null, userParams, QueryUtil.ALL_POS, QueryUtil.ALL_POS, (OrderByComparator)null);

						for (User reviewer : reviewers) {
							if (reviewer.getUserId() == review.getUserId()) {
								continue;
							}

							KeyValuePair kvp = new KeyValuePair(String.valueOf(reviewer.getUserId()), HtmlUtil.escape(reviewer.getFullName()));

							if (!leftList.contains(kvp)) {
								rightList.add(kvp);
							}
						}

						rightList = ListUtil.sort(rightList, new KeyValuePairComparator(false, true));
						%>

						<liferay-ui:input-move-boxes
							leftTitle="current"
							rightTitle="available"
							leftBoxName='<%= "current_reviewers_" + i %>'
							rightBoxName='<%= "available_reviewers_" + i %>'
							leftList="<%= leftList %>"
							rightList="<%= rightList %>"
						/>
					</td>
				</tr>

			<%
			}
			%>

			</table>
		</liferay-ui:toggle-area>
	</c:if>

	<br />

	<liferay-ui:toggle-area
		id="toggle_id_communities_edit_proposal_activities"
		showMessage='<%= LanguageUtil.get(pageContext, "show-activities") + " &raquo;" %>'
		hideMessage='<%= "&laquo; " + LanguageUtil.get(pageContext, "hide-activities") %>'
		defaultShowContent="<%= false %>"
	>
		<br />

		<liferay-ui:social-activities
			className="<%= TasksProposal.class.getName() %>"
			classPK="<%= proposalId %>"
		/>
	</liferay-ui:toggle-area>

	<br />

	<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, liveGroupId, ActionKeys.ASSIGN_REVIEWER) || GroupPermissionUtil.contains(permissionChecker, liveGroupId, ActionKeys.MANAGE_STAGING) || GroupPermissionUtil.contains(permissionChecker, liveGroupId, ActionKeys.PUBLISH_STAGING) || TasksProposalPermission.contains(permissionChecker, proposalId, ActionKeys.UPDATE) %>">
		<aui:button type="submit" />
	</c:if>

	<%
	PortletURL publishToLiveURL = renderResponse.createRenderURL();

	publishToLiveURL.setWindowState(LiferayWindowState.EXCLUSIVE);
	publishToLiveURL.setPortletMode(PortletMode.VIEW);

	publishToLiveURL.setParameter("pagesRedirect", redirect);
	publishToLiveURL.setParameter("groupId", String.valueOf(liveGroupId));
	publishToLiveURL.setParameter("stagingGroupId", String.valueOf(stagingGroupId));
	publishToLiveURL.setParameter("proposalId", String.valueOf(proposal.getProposalId()));

	long proposedLayoutPlid = LayoutConstants.DEFAULT_PLID;

	Layout proposedLayout = null;

	if (className.equals(Layout.class.getName())) {
		publishToLiveURL.setParameter("struts_action", "/communities/export_pages");
		publishToLiveURL.setParameter(Constants.CMD, "publish-to-live");

		proposedLayoutPlid = GetterUtil.getLong(proposal.getClassPK());

		proposedLayout = LayoutLocalServiceUtil.getLayout(proposedLayoutPlid);

		publishToLiveURL.setParameter("tabs2", proposedLayout.isPrivateLayout() ? "private-pages" : "public-pages");
		publishToLiveURL.setParameter("selPlid", String.valueOf(proposedLayoutPlid));
	}
	else if (className.equals(Portlet.class.getName())) {
		publishToLiveURL.setParameter("struts_action", "/communities/publish_portlet");
		publishToLiveURL.setParameter(Constants.CMD, "publish-to-live");

		proposedLayoutPlid = GetterUtil.getLong(classPK.substring(0, classPK.indexOf(PortletConstants.LAYOUT_SEPARATOR)));

		proposedLayout = LayoutLocalServiceUtil.getLayout(proposedLayoutPlid);
	}

	String proposedLayoutURL = PortalUtil.getLayoutFriendlyURL(proposedLayout, themeDisplay);

	proposedLayoutURL = PortalUtil.addPreservedParameters(themeDisplay, proposedLayoutURL);

	String tagligPreviewURL = "window.open('" + proposedLayoutURL + "');";
	%>

	<aui:button onClick="<%= tagligPreviewURL %>" value="preview" />

	<c:choose>
		<c:when test="<%= review != null %>">
			<c:if test="<%= ((review.getStage() == workflowStages) && GroupPermissionUtil.contains(permissionChecker, liveGroupId, ActionKeys.PUBLISH_STAGING)) || GroupPermissionUtil.contains(permissionChecker, liveGroupId, ActionKeys.MANAGE_STAGING) %>">

				<%
				String taglibPublishToLiveURL = "Liferay.LayoutExporter.publishToLive({title: '" + UnicodeLanguageUtil.get(pageContext, "publish-to-live") + "', url: '" + publishToLiveURL.toString() + "'});";
				%>

				<aui:button onClick="<%= taglibPublishToLiveURL %>" value="publish-to-live" />
			</c:if>

			<c:choose>
				<c:when test="<%= review.isCompleted() %>">
					<c:if test="<%= review.isRejected() %>">
						<aui:button onClick='<%= renderResponse.getNamespace() + "approveProposal();" %>' value="approve" />
					</c:if>

					<c:if test="<%= !review.isRejected() %>">
						<aui:button onClick='<%= renderResponse.getNamespace() + "rejectProposal();" %>' value="reject" />
					</c:if>
				</c:when>
				<c:otherwise>
					<aui:button onClick='<%= renderResponse.getNamespace() + "approveProposal();" %>' value="approve" />

					<aui:button onClick='<%= renderResponse.getNamespace() + "rejectProposal();" %>' value="reject" />
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:when test="<%= (review == null) && GroupPermissionUtil.contains(permissionChecker, liveGroupId, ActionKeys.MANAGE_STAGING) %>">

			<%
			String taglibPublishToLiveURL = "Liferay.LayoutExporter.publishToLive({title: '" + UnicodeLanguageUtil.get(pageContext, "publish-to-live") + "', url: '" + publishToLiveURL.toString() + "'});";
			%>

			<aui:button onClick="<%= taglibPublishToLiveURL %>" value="publish-to-live" />
		</c:when>
	</c:choose>

	<aui:button onClick="<%= redirect %>" type="cancel" />
</aui:form>

<br />

<liferay-ui:header
	title="reviewers"
/>

<%
List<String> headerNames = new ArrayList<String>();

headerNames.add("user");
headerNames.add("stage");
headerNames.add("status");
headerNames.add("review-date");

SearchContainer searchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURL, headerNames, "no-reviewers-were-found");

List<TasksReview> results = TasksReviewLocalServiceUtil.getReviews(proposal.getProposalId());

results = ListUtil.sort(results, new ReviewUserNameComparator(true));

int total = results.size();

searchContainer.setTotal(total);
searchContainer.setResults(results);

List resultRows = searchContainer.getResultRows();

for (int i = 0; i < results.size(); i++) {
	TasksReview curReview = (TasksReview)results.get(i);

	curReview = curReview.toEscapedModel();

	ResultRow row = new ResultRow(curReview, curReview.getReviewId(), i);

	// User

	row.addText(HtmlUtil.escape(PortalUtil.getUserName(curReview.getUserId(), curReview.getUserName())));

	// Stage

	row.addText(String.valueOf(curReview.getStage() + 1));

	// Status

	String status = "not-reviewed";

	if (curReview.isCompleted()) {
		status = curReview.isRejected() ? "rejected" : "approved";
	}

	row.addText(LanguageUtil.get(pageContext, status));

	// Review date

	if (curReview.isCompleted()) {
		row.addText(dateFormatDateTime.format(curReview.getModifiedDate()));
	}
	else {
		row.addText(StringPool.BLANK);
	}

	// Add result row

	resultRows.add(row);
}
%>

<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

<br />

<liferay-ui:header title="comments" />

<portlet:actionURL var="discussionURL">
	<portlet:param name="struts_action" value="/communities/edit_proposal_discussion" />
</portlet:actionURL>

<liferay-ui:discussion
	className="<%= TasksProposal.class.getName() %>"
	classPK="<%= proposal.getProposalId() %>"
	formAction="<%= discussionURL %>"
	formName="fm2"
	redirect="<%= currentURL %>"
	subject="<%= proposal.getName() %>"
	userId="<%= proposal.getUserId() %>"
/>

<aui:script>
	function <portlet:namespace />approveProposal() {
		if (confirm('<%= UnicodeLanguageUtil.get(pageContext, "are-you-sure-you-want-to-approve-this-proposal") %>')) {
			document.<portlet:namespace />fm1.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.APPROVE %>";
			submitForm(document.<portlet:namespace />fm1);
		}
	}

	function <portlet:namespace />rejectProposal() {
		if (confirm('<%= UnicodeLanguageUtil.get(pageContext, "are-you-sure-you-want-to-reject-this-proposal") %>')) {
			document.<portlet:namespace />fm1.<portlet:namespace /><%= Constants.CMD %>.value = "<%= Constants.REJECT %>";
			submitForm(document.<portlet:namespace />fm1);
		}
	}

	Liferay.provide(
		window,
		'<portlet:namespace />saveProposal',
		function() {
			document.<portlet:namespace />fm1.<portlet:namespace /><%= Constants.CMD %>.value = '<%= Constants.UPDATE %>';

			<%
			for (int i = 2; i <= workflowStages; i++) {
				String workflowRoleName = workflowRoleNames[i - 1];
			%>

				document.<portlet:namespace />fm1.<portlet:namespace />reviewUserIds_<%= i %>.value = Liferay.Util.listSelect(document.<portlet:namespace />fm1.<portlet:namespace />current_reviewers_<%= i %>);

			<%
			}
			%>

			submitForm(document.<portlet:namespace />fm1);
		},
		['liferay-util-list-fields']
	);
</aui:script>