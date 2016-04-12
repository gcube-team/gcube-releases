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

<%@ include file="/html/portlet/document_library/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

long breadcrumbsFolderId = ParamUtil.getLong(request, "breadcrumbsFolderId");

long searchFolderId = ParamUtil.getLong(request, "searchFolderId");
long searchFolderIds = ParamUtil.getLong(request, "searchFolderIds");

long[] folderIdsArray = null;

if (searchFolderId > 0) {
	folderIdsArray = new long[] {searchFolderId};
}
else {
	List folderIds = new ArrayList();

	long folderId = DLFileEntryImpl.getFolderId(themeDisplay.getParentGroupId(), DLFileEntryImpl.getRepositoryId(scopeGroupId, searchFolderIds));

	folderIds.add(new Long(folderId));

	DLFolderServiceUtil.getSubfolderIds(folderIds, scopeGroupId, searchFolderIds);

	folderIdsArray = StringUtil.split(StringUtil.merge(folderIds), 0L);
}

String keywords = ParamUtil.getString(request, "keywords");
%>

<liferay-portlet:renderURL varImpl="searchURL"><portlet:param name="struts_action" value="/document_library/search" /></liferay-portlet:renderURL>

<aui:form action="<%= searchURL %>" method="get" name="fm">
	<liferay-portlet:renderURLParams varImpl="searchURL" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="breadcrumbsFolderId" type="hidden" value="<%= breadcrumbsFolderId %>" />
	<aui:input name="searchFolderId" type="hidden" value="<%= searchFolderId %>" />
	<aui:input name="searchFolderIds" type="hidden" value="<%= searchFolderIds %>" />

	<liferay-ui:header
		backURL="<%= redirect %>"
		title="search"
	/>

	<%
	PortletURL portletURL = renderResponse.createRenderURL();

	portletURL.setParameter("struts_action", "/document_library/search");
	portletURL.setParameter("redirect", redirect);
	portletURL.setParameter("breadcrumbsFolderId", String.valueOf(breadcrumbsFolderId));
	portletURL.setParameter("searchFolderId", String.valueOf(searchFolderId));
	portletURL.setParameter("searchFolderIds", String.valueOf(searchFolderIds));
	portletURL.setParameter("keywords", keywords);

	List<String> headerNames = new ArrayList<String>();

	headerNames.add("#");
	headerNames.add("folder");
	headerNames.add("document");
	headerNames.add("score");
	headerNames.add(StringPool.BLANK);

	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURL, headerNames, LanguageUtil.format(pageContext, "no-documents-were-found-that-matched-the-keywords-x", "<strong>" + HtmlUtil.escape(keywords) + "</strong>"));

	try {
		Indexer indexer = IndexerRegistryUtil.getIndexer(DLFileEntry.class);

		SearchContext searchContext = SearchContextFactory.getInstance(request);

		searchContext.setEnd(searchContainer.getEnd());
		searchContext.setFolderIds(folderIdsArray);
		searchContext.setKeywords(keywords);
		searchContext.setStart(searchContainer.getStart());

		Hits results = indexer.search(searchContext);

		int total = results.getLength();

		searchContainer.setTotal(total);

		List resultRows = searchContainer.getResultRows();

		for (int i = 0; i < results.getDocs().length; i++) {
			Document doc = results.doc(i);

			ResultRow row = new ResultRow(doc, i, i);

			// Position

			row.addText(searchContainer.getStart() + i + 1 + StringPool.PERIOD);

			// Folder and document

			long folderId = GetterUtil.getLong(doc.get("repositoryId"));

			if (folderId == scopeGroupId) {
				folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
			}

			String fileName = doc.get("path");

			DLFileEntry fileEntry = null;

			try {
				fileEntry = DLFileEntryLocalServiceUtil.getFileEntry(scopeGroupId, folderId, fileName);
			}
			catch (Exception e) {
				if (_log.isWarnEnabled()) {
					_log.warn("Document library search index is stale and contains file entry {" + folderId + ", " + fileName + "}");
				}

				continue;
			}

			row.setObject(fileEntry);

			DLFolder folder = fileEntry.getFolder();

			PortletURL rowURL = renderResponse.createRenderURL();

			rowURL.setParameter("struts_action", "/document_library/view_file_entry");
			rowURL.setParameter("redirect", currentURL);
			rowURL.setParameter("fileEntryId", String.valueOf(fileEntry.getFileEntryId()));

			String rowHREF = rowURL.toString();

			row.addText(folder.getName(), rowHREF);
			row.addText(fileEntry.getTitle(), rowHREF);

			// Score

			row.addScore(results.score(i));

			// Action

			row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/document_library/file_entry_action.jsp");

			// Add result row

			resultRows.add(row);
		}
	%>

		<span class="aui-search-bar">
			<aui:input inlineField="<%= true %>" label="" name="keywords" size="30" title="search-documents" type="text" value="<%= keywords %>" />

			<aui:button type="submit" value="search" />
		</span>

		<br /><br />

		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

	<%
	}
	catch (Exception e) {
		_log.error(e.getMessage());
	}
	%>

</aui:form>

<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
	<aui:script>
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />keywords);
	</aui:script>
</c:if>

<%
if (searchFolderId > 0) {
	DLUtil.addPortletBreadcrumbEntries(searchFolderId, request, renderResponse);
}

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "search") + ": " + keywords, currentURL);
%>

<%!
private static Log _log = LogFactoryUtil.getLog("portal-web.docroot.html.portlet.document_library.search.jsp");
%>