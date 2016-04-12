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

<%@ page import="com.liferay.portal.DuplicateGroupException" %>
<%@ page import="com.liferay.portal.DuplicateTeamException" %>
<%@ page import="com.liferay.portal.GroupNameException" %>
<%@ page import="com.liferay.portal.ImageTypeException" %>
<%@ page import="com.liferay.portal.LARFileException" %>
<%@ page import="com.liferay.portal.LARTypeException" %>
<%@ page import="com.liferay.portal.LayoutFriendlyURLException" %>
<%@ page import="com.liferay.portal.LayoutHiddenException" %>
<%@ page import="com.liferay.portal.LayoutImportException" %>
<%@ page import="com.liferay.portal.LayoutNameException" %>
<%@ page import="com.liferay.portal.LayoutParentLayoutIdException" %>
<%@ page import="com.liferay.portal.LayoutSetVirtualHostException" %>
<%@ page import="com.liferay.portal.LayoutTypeException" %>
<%@ page import="com.liferay.portal.MembershipRequestCommentsException" %>
<%@ page import="com.liferay.portal.NoSuchGroupException" %>
<%@ page import="com.liferay.portal.NoSuchLayoutException" %>
<%@ page import="com.liferay.portal.NoSuchLayoutSetException" %>
<%@ page import="com.liferay.portal.NoSuchPortletException" %>
<%@ page import="com.liferay.portal.NoSuchRoleException" %>
<%@ page import="com.liferay.portal.RemoteExportException" %>
<%@ page import="com.liferay.portal.RemoteOptionsException" %>
<%@ page import="com.liferay.portal.RequiredGroupException" %>
<%@ page import="com.liferay.portal.RequiredLayoutException" %>
<%@ page import="com.liferay.portal.TeamNameException" %>
<%@ page import="com.liferay.portal.kernel.lar.PortletDataException" %>
<%@ page import="com.liferay.portal.kernel.lar.PortletDataHandler" %>
<%@ page import="com.liferay.portal.kernel.lar.PortletDataHandlerBoolean" %>
<%@ page import="com.liferay.portal.kernel.lar.PortletDataHandlerChoice" %>
<%@ page import="com.liferay.portal.kernel.lar.PortletDataHandlerControl" %>
<%@ page import="com.liferay.portal.kernel.lar.PortletDataHandlerKeys" %>
<%@ page import="com.liferay.portal.kernel.lar.UserIdStrategy" %>
<%@ page import="com.liferay.portal.kernel.plugin.PluginPackage" %>
<%@ page import="com.liferay.portal.kernel.scheduler.SchedulerEngineUtil" %>
<%@ page import="com.liferay.portal.kernel.scheduler.Trigger" %>
<%@ page import="com.liferay.portal.kernel.scheduler.messaging.SchedulerRequest" %>
<%@ page import="com.liferay.portal.kernel.staging.StagingConstants" %>
<%@ page import="com.liferay.portal.kernel.staging.StagingUtil" %>
<%@ page import="com.liferay.portal.lar.LayoutExporter" %>
<%@ page import="com.liferay.portal.liveusers.LiveUsers" %>
<%@ page import="com.liferay.portal.plugin.PluginUtil" %>
<%@ page import="com.liferay.portal.service.permission.GroupPermissionUtil" %>
<%@ page import="com.liferay.portal.service.permission.TeamPermissionUtil" %>
<%@ page import="com.liferay.portal.util.LayoutLister" %>
<%@ page import="com.liferay.portal.util.LayoutView" %>
<%@ page import="com.liferay.portal.security.permission.ResourceActionsUtil" %>
<%@ page import="com.liferay.portal.security.permission.comparator.ActionComparator" %>
<%@ page import="com.liferay.portal.security.permission.comparator.ModelResourceComparator" %>
<%@ page import="com.liferay.portal.service.permission.PortalPermissionUtil" %>
<%@ page import="com.liferay.portlet.communities.action.ActionUtil" %>
<%@ page import="com.liferay.portlet.communities.search.ExportPageChecker" %>
<%@ page import="com.liferay.portlet.communities.search.UserGroupRoleRoleChecker" %>
<%@ page import="com.liferay.portlet.communities.search.UserGroupRoleUserChecker" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.GroupSearch" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.GroupSearchTerms" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.OrganizationGroupChecker" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.OrganizationSearch" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.OrganizationSearchTerms" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.RoleSearch" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.RoleSearchTerms" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.TeamSearch" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.TeamSearchTerms" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.UserGroupChecker" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.UserGroupGroupChecker" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.UserGroupSearch" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.UserGroupSearchTerms" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.UserSearch" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.UserSearchTerms" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.UserTeamChecker" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.util.EnterpriseAdminUtil" %>
<%@ page import="com.liferay.portlet.tasks.DuplicateReviewUserIdException" %>
<%@ page import="com.liferay.portlet.tasks.NoSuchProposalException" %>
<%@ page import="com.liferay.portlet.tasks.NoSuchReviewException" %>
<%@ page import="com.liferay.portlet.tasks.model.TasksProposal" %>
<%@ page import="com.liferay.portlet.tasks.model.TasksReview" %>
<%@ page import="com.liferay.portlet.tasks.service.TasksProposalLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.tasks.service.TasksReviewLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.tasks.service.permission.TasksProposalPermission" %>
<%@ page import="com.liferay.portlet.tasks.util.comparator.ReviewUserNameComparator" %>

<%
Format dateFormatDate = FastDateFormatFactoryUtil.getDate(locale, timeZone);
Format dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
%>