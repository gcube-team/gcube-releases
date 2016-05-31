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

<%@ page import="com.liferay.portal.kernel.search.Hits" %>
<%@ page import="com.liferay.portal.kernel.search.Indexer" %>
<%@ page import="com.liferay.portal.kernel.search.IndexerRegistryUtil" %>
<%@ page import="com.liferay.portal.kernel.search.SearchContext" %>
<%@ page import="com.liferay.portal.kernel.search.SearchContextFactory" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayPortletResponse" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayPortletRequest" %>
<%@ page import="com.liferay.portal.kernel.xml.Document" %>
<%@ page import="com.liferay.portal.kernel.xml.Element" %>
<%@ page import="com.liferay.portal.kernel.xml.SAXReaderUtil" %>
<%@ page import="com.liferay.portal.security.permission.ResourceActionsUtil" %>
<%@ page import="com.liferay.portal.security.permission.comparator.ModelResourceComparator" %>
<%@ page import="com.liferay.portlet.PortalPreferences" %>
<%@ page import="com.liferay.portlet.PortletContextImpl" %>
<%@ page import="com.liferay.portlet.asset.AssetRendererFactoryRegistryUtil" %>
<%@ page import="com.liferay.portlet.asset.NoSuchEntryException" %>
<%@ page import="com.liferay.portlet.asset.NoSuchTagException" %>
<%@ page import="com.liferay.portlet.asset.NoSuchTagPropertyException" %>
<%@ page import="com.liferay.portlet.asset.model.AssetCategory" %>
<%@ page import="com.liferay.portlet.asset.model.AssetEntry" %>
<%@ page import="com.liferay.portlet.asset.model.AssetRenderer" %>
<%@ page import="com.liferay.portlet.asset.model.AssetRendererFactory" %>
<%@ page import="com.liferay.portlet.asset.model.AssetTag" %>
<%@ page import="com.liferay.portlet.asset.model.AssetTagProperty" %>
<%@ page import="com.liferay.portlet.asset.model.AssetVocabulary" %>
<%@ page import="com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.asset.service.AssetEntryServiceUtil" %>
<%@ page import="com.liferay.portlet.asset.service.AssetTagLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.asset.service.AssetTagPropertyLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.asset.service.AssetVocabularyLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.asset.service.persistence.AssetEntryQuery" %>
<%@ page import="com.liferay.portlet.asset.util.AssetUtil" %>
<%@ page import="com.liferay.portlet.assetpublisher.search.AssetDisplayTerms" %>
<%@ page import="com.liferay.portlet.assetpublisher.search.AssetSearch" %>
<%@ page import="com.liferay.portlet.assetpublisher.search.AssetSearchTerms" %>
<%@ page import="com.liferay.portlet.assetpublisher.util.AssetPublisherUtil" %>
<%@ page import="com.liferay.portlet.blogs.model.BlogsEntry" %>
<%@ page import="com.liferay.portlet.blogs.service.BlogsEntryLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.blogs.service.BlogsEntryServiceUtil" %>
<%@ page import="com.liferay.portlet.blogs.service.permission.BlogsEntryPermission" %>
<%@ page import="com.liferay.portlet.blogs.service.permission.BlogsPermission" %>
<%@ page import="com.liferay.portlet.bookmarks.model.BookmarksEntry" %>
<%@ page import="com.liferay.portlet.bookmarks.model.BookmarksFolder" %>
<%@ page import="com.liferay.portlet.bookmarks.model.BookmarksFolderConstants" %>
<%@ page import="com.liferay.portlet.bookmarks.service.BookmarksEntryLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.bookmarks.service.BookmarksFolderLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.bookmarks.service.permission.BookmarksEntryPermission" %>
<%@ page import="com.liferay.portlet.bookmarks.service.permission.BookmarksFolderPermission" %>
<%@ page import="com.liferay.portlet.bookmarks.service.permission.BookmarksPermission" %>
<%@ page import="com.liferay.portlet.bookmarks.util.BookmarksUtil" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.DLFileEntry" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.DLFolder" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.DLFolderConstants" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.impl.DLFileEntryImpl" %>
<%@ page import="com.liferay.portlet.documentlibrary.model.impl.DLFolderImpl" %>
<%@ page import="com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.documentlibrary.service.DLFileEntryServiceUtil" %>
<%@ page import="com.liferay.portlet.documentlibrary.service.DLFolderServiceUtil" %>
<%@ page import="com.liferay.portlet.documentlibrary.service.permission.DLFileEntryPermission" %>
<%@ page import="com.liferay.portlet.documentlibrary.service.permission.DLPermission" %>
<%@ page import="com.liferay.portlet.documentlibrary.util.DLUtil" %>
<%@ page import="com.liferay.portlet.documentlibrary.util.DocumentConversionUtil" %>
<%@ page import="com.liferay.portlet.imagegallery.model.IGFolder" %>
<%@ page import="com.liferay.portlet.imagegallery.model.IGFolderConstants" %>
<%@ page import="com.liferay.portlet.imagegallery.model.IGImage" %>
<%@ page import="com.liferay.portlet.imagegallery.service.IGFolderLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.imagegallery.service.IGImageLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.imagegallery.service.permission.IGImagePermission" %>
<%@ page import="com.liferay.portlet.imagegallery.service.permission.IGPermission" %>
<%@ page import="com.liferay.portlet.journalcontent.util.JournalContentUtil" %>
<%@ page import="com.liferay.portlet.journal.NoSuchArticleException" %>
<%@ page import="com.liferay.portlet.journal.action.EditArticleAction" %>
<%@ page import="com.liferay.portlet.journal.model.JournalArticle" %>
<%@ page import="com.liferay.portlet.journal.model.JournalArticleConstants" %>
<%@ page import="com.liferay.portlet.journal.model.JournalArticleDisplay" %>
<%@ page import="com.liferay.portlet.journal.model.JournalArticleResource" %>
<%@ page import="com.liferay.portlet.journal.search.ArticleDisplayTerms" %>
<%@ page import="com.liferay.portlet.journal.search.ArticleSearch" %>
<%@ page import="com.liferay.portlet.journal.search.ArticleSearchTerms" %>
<%@ page import="com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.journal.service.JournalArticleServiceUtil" %>
<%@ page import="com.liferay.portlet.journal.service.JournalArticleResourceLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.journal.service.permission.JournalArticlePermission" %>
<%@ page import="com.liferay.portlet.journal.service.permission.JournalPermission" %>
<%@ page import="com.liferay.portlet.messageboards.model.MBMessage" %>
<%@ page import="com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.messageboards.service.permission.MBMessagePermission" %>
<%@ page import="com.liferay.portlet.wiki.model.WikiNode" %>
<%@ page import="com.liferay.portlet.wiki.model.WikiPage" %>
<%@ page import="com.liferay.portlet.wiki.model.WikiPageDisplay" %>
<%@ page import="com.liferay.portlet.wiki.model.WikiPageResource" %>
<%@ page import="com.liferay.portlet.wiki.model.impl.WikiPageImpl" %>
<%@ page import="com.liferay.portlet.wiki.service.WikiNodeLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.wiki.service.WikiPageLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.wiki.service.WikiPageResourceLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.wiki.service.permission.WikiPagePermission" %>
<%@ page import="com.liferay.portlet.wiki.util.WikiCacheUtil" %>
<%@ page import="com.liferay.portlet.wiki.util.WikiUtil" %>
<%@ page import="com.liferay.util.RSSUtil" %>
<%@ page import="com.liferay.util.xml.DocUtil" %>

<%
PortletPreferences preferences = renderRequest.getPreferences();

String portletResource = ParamUtil.getString(request, "portletResource");

if (Validator.isNotNull(portletResource)) {
	preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletResource);
}

String selectionStyle = preferences.getValue("selection-style", null);

if (Validator.isNull(selectionStyle)) {
	selectionStyle = "dynamic";
}

boolean defaultScope = GetterUtil.getBoolean(preferences.getValue("default-scope", null), true);

long[] groupIds = AssetPublisherUtil.getGroupIds(preferences, scopeGroupId, layout);

long[] availableClassNameIds = AssetRendererFactoryRegistryUtil.getClassNameIds();

boolean anyAssetType = GetterUtil.getBoolean(preferences.getValue("any-asset-type", Boolean.TRUE.toString()));

long[] classNameIds = AssetPublisherUtil.getClassNameIds(preferences, availableClassNameIds);

AssetEntryQuery	assetEntryQuery = new AssetEntryQuery();

String[] allAssetTagNames = new String[0];

if (selectionStyle.equals("dynamic")) {
	assetEntryQuery = AssetPublisherUtil.getAssetEntryQuery(preferences, scopeGroupId);

	allAssetTagNames = AssetPublisherUtil.getAssetTagNames(preferences, scopeGroupId);
}

long assetVocabularyId = GetterUtil.getLong(preferences.getValue("asset-vocabulary-id", StringPool.BLANK));

long assetCategoryId = ParamUtil.getLong(request, "categoryId");

String assetCategoryName = null;
String assetVocabularyName = null;

if (assetCategoryId > 0) {
	assetEntryQuery.setAllCategoryIds(new long[] {assetCategoryId});

	AssetCategory assetCategory = AssetCategoryLocalServiceUtil.getCategory(assetCategoryId);

	assetCategoryName = assetCategory.getName();

	AssetVocabulary assetVocabulary = AssetVocabularyLocalServiceUtil.getAssetVocabulary(assetCategory.getVocabularyId());

	assetVocabularyName = assetVocabulary.getName();

	PortalUtil.setPageKeywords(assetCategory.getName(), request);
}

String assetTagName = ParamUtil.getString(request, "tag");

if (Validator.isNotNull(assetTagName)) {
	allAssetTagNames = new String[] {assetTagName};

	long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, allAssetTagNames);

	assetEntryQuery.setAllTagIds(assetTagIds);

	PortalUtil.setPageKeywords(assetTagName, request);
}

boolean mergeUrlTags = GetterUtil.getBoolean(preferences.getValue("merge-url-tags", null), true);

String displayStyle = GetterUtil.getString(preferences.getValue("display-style", "abstracts"));

if (Validator.isNull(displayStyle)) {
	displayStyle = "abstracts";
}

boolean showAssetTitle = GetterUtil.getBoolean(preferences.getValue("show-asset-title", null), true);
boolean showContextLink = GetterUtil.getBoolean(preferences.getValue("show-context-link", null), true);
int abstractLength = GetterUtil.getInteger(preferences.getValue("abstract-length", StringPool.BLANK), 200);
String assetLinkBehaviour = GetterUtil.getString(preferences.getValue("asset-link-behaviour", "showFullContent"));
String orderByColumn1 = GetterUtil.getString(preferences.getValue("order-by-column-1", "modifiedDate"));
String orderByColumn2 = GetterUtil.getString(preferences.getValue("order-by-column-2", "title"));
String orderByType1 = GetterUtil.getString(preferences.getValue("order-by-type-1", "DESC"));
String orderByType2 = GetterUtil.getString(preferences.getValue("order-by-type-2", "ASC"));
boolean excludeZeroViewCount = GetterUtil.getBoolean(preferences.getValue("exclude-zero-view-count", "0"));
int delta = GetterUtil.getInteger(preferences.getValue("delta", StringPool.BLANK), SearchContainer.DEFAULT_DELTA);
String paginationType = GetterUtil.getString(preferences.getValue("pagination-type", "none"));
boolean showAvailableLocales = GetterUtil.getBoolean(preferences.getValue("show-available-locales", StringPool.BLANK));
boolean enableRatings = GetterUtil.getBoolean(preferences.getValue("enable-ratings", null));
boolean enableComments = GetterUtil.getBoolean(preferences.getValue("enable-comments", null));
boolean enableCommentRatings = GetterUtil.getBoolean(preferences.getValue("enable-comment-ratings", null));
boolean enableTagBasedNavigation = GetterUtil.getBoolean(preferences.getValue("enable-tag-based-navigation", null));

String[] conversions = DocumentConversionUtil.getConversions("html");
String[] extensions = preferences.getValues("extensions", new String[0]);
boolean openOfficeServerEnabled = PrefsPropsUtil.getBoolean(PropsKeys.OPENOFFICE_SERVER_ENABLED, PropsValues.OPENOFFICE_SERVER_ENABLED);
boolean enableConversions = openOfficeServerEnabled && (extensions != null) && (extensions.length > 0);
boolean enablePrint = GetterUtil.getBoolean(preferences.getValue("enable-print", null));
boolean enableFlags = GetterUtil.getBoolean(preferences.getValue("enable-flags", null));

String defaultMetadataFields = StringPool.BLANK;
String allMetadataFields = "create-date,modified-date,publish-date,expiration-date,priority,author,view-count,categories,tags";

String[] metadataFields = StringUtil.split(preferences.getValue("metadata-fields", defaultMetadataFields));

boolean enableRSS = GetterUtil.getBoolean(preferences.getValue("enable-rss", null));
int rssDelta = GetterUtil.getInteger(preferences.getValue("rss-delta", "20"));
String rssDisplayStyle = preferences.getValue("rss-display-style", RSSUtil.DISPLAY_STYLE_ABSTRACT);
String rssFormat = preferences.getValue("rss-format", "atom10");
String rssName = preferences.getValue("rss-name", portletDisplay.getTitle());

String[] assetEntryXmls = preferences.getValues("asset-entry-xml", new String[0]);

boolean viewInContext = assetLinkBehaviour.equals("viewInPortlet");

boolean showPortletWithNoResults = false;
boolean groupByClass = (assetVocabularyId == -1);
boolean allowEmptyResults = false;

Format dateFormatDate = FastDateFormatFactoryUtil.getDate(locale, timeZone);
%>

<%@ include file="/html/portlet/asset_publisher/init-ext.jsp" %>

<%!
private String _checkViewURL(String viewURL, String currentURL, ThemeDisplay themeDisplay) {
	if (Validator.isNotNull(viewURL) && viewURL.startsWith(themeDisplay.getURLPortal())) {
		viewURL = HttpUtil.setParameter(viewURL, "redirect", currentURL);
	}

	return viewURL;
}
%>