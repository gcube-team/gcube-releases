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


<%@ page import="com.liferay.portlet.journal.action.EditArticleAction" %>
<%@ page import="com.liferay.portlet.journal.model.JournalArticle" %>
<%@ page import="com.liferay.portlet.journal.search.ArticleSearch" %>
<%@ page import="com.liferay.portlet.journal.search.ArticleSearchTerms" %>
<%@ page import="com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.rss.util.RSSUtil" %>

<%@ page import="com.sun.syndication.feed.synd.SyndContent" %>
<%@ page import="com.sun.syndication.feed.synd.SyndEntry" %>
<%@ page import="com.sun.syndication.feed.synd.SyndFeed" %>
<%@ page import="com.sun.syndication.feed.synd.SyndImage" %>

<%
PortletPreferences preferences = renderRequest.getPreferences();

String portletResource = ParamUtil.getString(request, "portletResource");

if (Validator.isNotNull(portletResource)) {
	preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletResource);
}

String[] urls = preferences.getValues("urls", new String[0]);
String[] titles = preferences.getValues("titles", new String[0]);
int entriesPerFeed = GetterUtil.getInteger(preferences.getValue("items-per-channel", "8"));
int expandedEntriesPerFeed = GetterUtil.getInteger(preferences.getValue("expanded-items-per-channel", "1"));
boolean showFeedTitle = GetterUtil.getBoolean(preferences.getValue("show-feed-title", Boolean.TRUE.toString()));
boolean showFeedPublishedDate = GetterUtil.getBoolean(preferences.getValue("show-feed-published-date", Boolean.TRUE.toString()));
boolean showFeedDescription = GetterUtil.getBoolean(preferences.getValue("show-feed-description", Boolean.TRUE.toString()));
boolean showFeedImage = GetterUtil.getBoolean(preferences.getValue("show-feed-image", Boolean.TRUE.toString()));
String feedImageAlignment = preferences.getValue("feed-image-alignment", "right");
boolean showFeedItemAuthor = GetterUtil.getBoolean(preferences.getValue("show-feed-item-author", Boolean.TRUE.toString()));

String[] headerArticleValues = preferences.getValues("header-article-values", new String[] {"0", ""});

long headerArticleGroupId = GetterUtil.getLong(headerArticleValues[0]);
String headerArticleId = headerArticleValues[1];

String[] footerArticleValues = preferences.getValues("footer-article-values", new String[] {"0", ""});

long footerArticleGroupId = GetterUtil.getLong(footerArticleValues[0]);
String footerArticleId = footerArticleValues[1];

Format dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
Format dateFormatDate = FastDateFormatFactoryUtil.getDate(locale, timeZone);
%>