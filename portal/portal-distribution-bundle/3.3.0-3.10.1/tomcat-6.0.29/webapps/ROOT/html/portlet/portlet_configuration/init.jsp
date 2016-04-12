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

<%@ page import="com.liferay.portal.LARFileException" %>
<%@ page import="com.liferay.portal.LARTypeException" %>
<%@ page import="com.liferay.portal.LayoutImportException" %>
<%@ page import="com.liferay.portal.NoSuchLayoutException" %>
<%@ page import="com.liferay.portal.NoSuchPortletItemException" %>
<%@ page import="com.liferay.portal.NoSuchResourceException" %>
<%@ page import="com.liferay.portal.PortletIdException" %>
<%@ page import="com.liferay.portal.PortletItemNameException" %>
<%@ page import="com.liferay.portal.ResourcePrimKeyException" %>
<%@ page import="com.liferay.portal.kernel.lar.PortletDataException" %>
<%@ page import="com.liferay.portal.kernel.lar.PortletDataHandler" %>
<%@ page import="com.liferay.portal.kernel.lar.PortletDataHandlerBoolean" %>
<%@ page import="com.liferay.portal.kernel.lar.PortletDataHandlerChoice" %>
<%@ page import="com.liferay.portal.kernel.lar.PortletDataHandlerControl" %>
<%@ page import="com.liferay.portal.kernel.lar.PortletDataHandlerKeys" %>
<%@ page import="com.liferay.portal.kernel.lar.UserIdStrategy" %>
<%@ page import="com.liferay.portal.kernel.portlet.FriendlyURLMapper" %>
<%@ page import="com.liferay.portal.kernel.portlet.PortletModeFactory" %>
<%@ page import="com.liferay.portal.kernel.servlet.PortletServlet" %>
<%@ page import="com.liferay.portal.security.permission.ResourceActionsUtil" %>
<%@ page import="com.liferay.portal.security.permission.comparator.ActionComparator" %>
<%@ page import="com.liferay.portal.service.permission.RolePermissionUtil" %>
<%@ page import="com.liferay.portlet.PortletContextImpl" %>
<%@ page import="com.liferay.portlet.PortletQNameUtil" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.OrganizationSearch" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.OrganizationSearchTerms" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.RoleSearch" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.RoleSearchTerms" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.UserGroupSearch" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.UserGroupSearchTerms" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.UserSearch" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.search.UserSearchTerms" %>
<%@ page import="com.liferay.portlet.enterpriseadmin.util.EnterpriseAdminUtil" %>
<%@ page import="com.liferay.portlet.portletconfiguration.util.PublicRenderParameterConfiguration" %>
<%@ page import="com.liferay.portlet.social.util.FacebookUtil" %>
<%@ page import="com.liferay.portlet.tasks.NoSuchProposalException" %>
<%@ page import="com.liferay.portlet.tasks.model.TasksProposal" %>
<%@ page import="com.liferay.portlet.tasks.service.TasksProposalLocalServiceUtil" %>

<%
String portletResource = ParamUtil.getString(request, "portletResource");

Portlet selPortlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), portletResource);

Format dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
%>

<c:if test='<%= themeDisplay.isStatePopUp() && SessionMessages.contains(renderRequest, portletName + ".doConfigure") %>'>
	<aui:script use="aui-base">
		if (window.parent) {
			<c:choose>
				<c:when test="<%= !selPortlet.isAjaxable() %>">
					window.parent.location.reload();
				</c:when>
				<c:otherwise>
					var curPortletBoundaryId = '#p_p_id_<%= portletResource %>_';

					window.parent.Liferay.Portlet.refresh(curPortletBoundaryId);
				</c:otherwise>
			</c:choose>
		}
	</aui:script>
</c:if>