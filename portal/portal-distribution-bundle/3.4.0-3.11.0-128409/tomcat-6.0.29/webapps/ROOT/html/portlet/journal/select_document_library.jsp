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
DLFolder folder = (DLFolder)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FOLDER);

long folderId = BeanParamUtil.getLong(folder, request, "folderId", DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

long groupId = ParamUtil.getLong(request, "groupId");

PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("struts_action", "/journal/select_document_library");
portletURL.setParameter("folderId", String.valueOf(folderId));
portletURL.setParameter("groupId", String.valueOf(groupId));

if (folder != null) {
	DLUtil.addPortletBreadcrumbEntries(folder, request, renderResponse);
}
%>

<aui:form method="post" name="fm">
	<liferay-ui:header
		title="folders"
	/>

	<liferay-ui:breadcrumb showGuestGroup="<%= false %>" showParentGroups="<%= false %>" showLayout="<%= false %>" />

	<%
	List<String> headerNames = new ArrayList<String>();

	headerNames.add("folder");
	headerNames.add("num-of-folders");
	headerNames.add("num-of-documents");

	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null, "cur1", SearchContainer.DEFAULT_DELTA, portletURL, headerNames, null);

	int total = DLFolderServiceUtil.getFoldersCount(groupId, folderId);

	searchContainer.setTotal(total);

	List results = DLFolderServiceUtil.getFolders(groupId, folderId, searchContainer.getStart(), searchContainer.getEnd());

	searchContainer.setResults(results);

	List resultRows = searchContainer.getResultRows();

	for (int i = 0; i < results.size(); i++) {
		DLFolder curFolder = (DLFolder)results.get(i);

		ResultRow row = new ResultRow(curFolder, curFolder.getFolderId(), i);

		PortletURL rowURL = renderResponse.createRenderURL();

		rowURL.setParameter("struts_action", "/journal/select_document_library");
		rowURL.setParameter("folderId", String.valueOf(curFolder.getFolderId()));
		rowURL.setParameter("groupId", String.valueOf(groupId));

		// Name

		StringBundler sb = new StringBundler(4);

		sb.append("<img align=\"left\" border=\"0\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
		sb.append("/common/folder.png\">");
		sb.append(curFolder.getName());

		row.addText(sb.toString(), rowURL);

		// Statistics

		List subfolderIds = new ArrayList();

		subfolderIds.add(new Long(curFolder.getFolderId()));

		DLFolderServiceUtil.getSubfolderIds(subfolderIds, groupId, curFolder.getFolderId());

		int foldersCount = subfolderIds.size() - 1;
		int fileEntriesCount = DLFileEntryServiceUtil.getFoldersFileEntriesCount(groupId, subfolderIds, WorkflowConstants.STATUS_APPROVED);

		row.addText(String.valueOf(foldersCount), rowURL);
		row.addText(String.valueOf(fileEntriesCount), rowURL);

		// Add result row

		resultRows.add(row);
	}
	%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />

	<br />

	<liferay-ui:header title="documents" />

	<%
	headerNames.clear();

	headerNames.add("document");
	headerNames.add("size");

	if (PropsValues.DL_FILE_ENTRY_READ_COUNT_ENABLED) {
		headerNames.add("downloads");
	}

	headerNames.add("locked");
	headerNames.add(StringPool.BLANK);

	searchContainer = new SearchContainer(renderRequest, null, null, "cur2", SearchContainer.DEFAULT_DELTA, portletURL, headerNames, null);

	total = DLFileEntryServiceUtil.getFileEntriesCount(groupId, folderId);

	searchContainer.setTotal(total);

	results = DLFileEntryServiceUtil.getFileEntries(groupId, folderId, searchContainer.getStart(), searchContainer.getEnd());

	searchContainer.setResults(results);

	resultRows = searchContainer.getResultRows();

	for (int i = 0; i < results.size(); i++) {
		DLFileEntry fileEntry = (DLFileEntry)results.get(i);

		ResultRow row = new ResultRow(fileEntry, fileEntry.getFileEntryId(), i);

		String rowHREF = themeDisplay.getPortalURL() + themeDisplay.getPathContext() + "/documents/" + themeDisplay.getScopeGroupId() + StringPool.SLASH + folderId + StringPool.SLASH + HttpUtil.encodeURL(fileEntry.getTitle());

		// Title

		StringBundler sb = new StringBundler(10);

		sb.append("<img align=\"left\" border=\"0\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
		sb.append("/file_system/small/");
		sb.append(fileEntry.getIcon());
		sb.append(".png\">");
		sb.append(fileEntry.getTitle());

		row.addText(sb.toString(), rowHREF);

		// Statistics

		row.addText(TextFormatter.formatKB(fileEntry.getSize(), locale) + "k", rowHREF);

		if (PropsValues.DL_FILE_ENTRY_READ_COUNT_ENABLED) {
			row.addText(String.valueOf(fileEntry.getReadCount()), rowHREF);
		}

		// Locked

		boolean isLocked = LockLocalServiceUtil.isLocked(DLFileEntry.class.getName(), DLUtil.getLockId(fileEntry.getGroupId(), fileEntry.getFolderId(), fileEntry.getName()));

		row.addText(LanguageUtil.get(pageContext, isLocked ? "yes" : "no"), rowHREF);

		// Action

		sb.setIndex(0);

		sb.append("opener.");
		sb.append(renderResponse.getNamespace());
		sb.append("selectDocumentLibrary('");
		sb.append(themeDisplay.getPathContext());
		sb.append("/documents/");
		sb.append(groupId);
		sb.append(StringPool.SLASH);
		sb.append(fileEntry.getFolderId());
		sb.append(StringPool.SLASH);
		sb.append(HttpUtil.encodeURL(HtmlUtil.unescape(fileEntry.getTitle())));
		sb.append("'); window.close();");

		row.addButton("right", SearchEntry.DEFAULT_VALIGN, LanguageUtil.get(pageContext, "choose"), sb.toString());

		// Add result row

		resultRows.add(row);
	}
	%>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
</aui:form>