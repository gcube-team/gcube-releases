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

<%@ page import="com.liferay.portal.NoSuchGroupException" %>
<%@ page import="com.liferay.portlet.asset.service.AssetEntryServiceUtil" %>
<%@ page import="com.liferay.portlet.journal.NoSuchStructureException" %>
<%@ page import="com.liferay.portlet.journal.action.EditArticleAction" %>
<%@ page import="com.liferay.portlet.journal.model.JournalArticle" %>
<%@ page import="com.liferay.portlet.journal.model.JournalArticleConstants" %>
<%@ page import="com.liferay.portlet.journal.model.JournalArticleDisplay" %>
<%@ page import="com.liferay.portlet.journal.model.JournalStructure" %>
<%@ page import="com.liferay.portlet.journal.search.ArticleSearch" %>
<%@ page import="com.liferay.portlet.journal.search.ArticleSearchTerms" %>
<%@ page import="com.liferay.portlet.journal.search.StructureDisplayTerms" %>
<%@ page import="com.liferay.portlet.journal.search.StructureSearch" %>
<%@ page import="com.liferay.portlet.journal.search.StructureSearchTerms" %>
<%@ page import="com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.journal.service.JournalArticleServiceUtil" %>
<%@ page import="com.liferay.portlet.journal.service.JournalStructureLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.journal.service.permission.JournalArticlePermission" %>
<%@ page import="com.liferay.portlet.journal.util.JournalUtil" %>
<%@ page import="com.liferay.portlet.journalcontent.util.JournalContentUtil" %>
<%@ page import="com.liferay.portlet.layoutconfiguration.util.RuntimePortletUtil" %>
<%@ page import="com.liferay.portlet.layoutconfiguration.util.xml.ActionURLLogic" %>
<%@ page import="com.liferay.portlet.layoutconfiguration.util.xml.PortletLogic" %>
<%@ page import="com.liferay.portlet.layoutconfiguration.util.xml.RenderURLLogic" %>
<%@ page import="com.liferay.portlet.layoutconfiguration.util.xml.RuntimeLogic" %>

<%
PortletPreferences preferences = renderRequest.getPreferences();

String portletResource = ParamUtil.getString(request, "portletResource");

if (Validator.isNotNull(portletResource)) {
	preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletResource);
}

long groupId = GetterUtil.getLong(preferences.getValue("group-id", String.valueOf(themeDisplay.getScopeGroupId())));
String structureId = GetterUtil.getString(preferences.getValue("structure-id", StringPool.BLANK));
String type = preferences.getValue("type", StringPool.BLANK);
String pageURL = preferences.getValue("page-url", "maximized");
int pageDelta = GetterUtil.getInteger(preferences.getValue("page-delta", StringPool.BLANK));
String orderByCol = preferences.getValue("order-by-col", StringPool.BLANK);
String orderByType = preferences.getValue("order-by-type", StringPool.BLANK);

OrderByComparator orderByComparator = JournalUtil.getArticleOrderByComparator(orderByCol, orderByType);

Format dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
%>