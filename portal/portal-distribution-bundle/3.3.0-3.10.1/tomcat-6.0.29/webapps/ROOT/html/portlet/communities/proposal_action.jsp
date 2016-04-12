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
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

TasksProposal proposal = (TasksProposal)row.getObject();
%>

<liferay-ui:icon-menu>
	<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, proposal.getGroupId(), ActionKeys.APPROVE_PROPOSAL) || GroupPermissionUtil.contains(permissionChecker, proposal.getGroupId(), ActionKeys.MANAGE_LAYOUTS) %>">
		<portlet:renderURL var="editURL">
			<portlet:param name="struts_action" value="/communities/edit_proposal" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="groupId" value="<%= String.valueOf(proposal.getGroupId()) %>" />
			<portlet:param name="proposalId" value="<%= String.valueOf(proposal.getProposalId()) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			image="edit"
			url="<%= editURL %>"
		/>
	</c:if>

	<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, proposal.getGroupId(), ActionKeys.MANAGE_LAYOUTS) %>">
		<portlet:actionURL var="deleteURL">
			<portlet:param name="struts_action" value="/communities/edit_proposal" />
			<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE %>" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="groupId" value="<%= String.valueOf(proposal.getGroupId()) %>" />
			<portlet:param name="proposalId" value="<%= String.valueOf(proposal.getProposalId()) %>" />
		</portlet:actionURL>

		<liferay-ui:icon-delete
			url="<%= deleteURL %>"
		/>
	</c:if>
</liferay-ui:icon-menu>