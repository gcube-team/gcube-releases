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
String folderName = LanguageUtil.get(pageContext, "documents-home");

if (folder != null) {
	folderName = folder.getName();

	DLUtil.addPortletBreadcrumbEntries(folder, request, renderResponse);
}
%>

<aui:form method="post" name="fm">
	<liferay-ui:header
		title="documents-home"
	/>

	<liferay-ui:breadcrumb showGuestGroup="<%= false %>" showParentGroups="<%= false %>" showLayout="<%= false %>" />

	<%
	PortletURL portletURL = renderResponse.createRenderURL();

	portletURL.setParameter("struts_action", "/document_library/select_folder");
	portletURL.setParameter("folderId", String.valueOf(folderId));

	List<String> headerNames = new ArrayList<String>();

	headerNames.add("folder");
	headerNames.add("num-of-folders");
	headerNames.add("num-of-documents");
	headerNames.add(StringPool.BLANK);

	SearchContainer searchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, portletURL, headerNames, null);

	int total = DLFolderServiceUtil.getFoldersCount(scopeGroupId, folderId);

	searchContainer.setTotal(total);

	List results = DLFolderServiceUtil.getFolders(scopeGroupId, folderId, searchContainer.getStart(), searchContainer.getEnd());

	searchContainer.setResults(results);

	List resultRows = searchContainer.getResultRows();

	for (int i = 0; i < results.size(); i++) {
		DLFolder curFolder = (DLFolder)results.get(i);

		curFolder = curFolder.toEscapedModel();

		ResultRow row = new ResultRow(curFolder, curFolder.getFolderId(), i);

		PortletURL rowURL = renderResponse.createRenderURL();

		rowURL.setParameter("struts_action", "/document_library/select_folder");
		rowURL.setParameter("folderId", String.valueOf(curFolder.getFolderId()));

		// Name

		StringBundler sb = new StringBundler(7);

		sb.append("<img align=\"left\" border=\"0\" src=\"");
		sb.append(themeDisplay.getPathThemeImages());
		sb.append("/common/folder.png\">");
		sb.append(curFolder.getName());

		row.addText(sb.toString(), rowURL);

		// Statistics

		List subfolderIds = new ArrayList();

		subfolderIds.add(new Long(curFolder.getFolderId()));

		DLFolderServiceUtil.getSubfolderIds(subfolderIds, scopeGroupId, curFolder.getFolderId());

		int foldersCount = subfolderIds.size() - 1;
		int fileEntriesCount = DLFileEntryServiceUtil.getFoldersFileEntriesCount(scopeGroupId, subfolderIds, WorkflowConstants.STATUS_APPROVED);

		row.addText(String.valueOf(foldersCount), rowURL);
		row.addText(String.valueOf(fileEntriesCount), rowURL);

		// Action

		sb.setIndex(0);

		sb.append("opener.");
		sb.append(renderResponse.getNamespace());
		sb.append("selectFolder('");
		sb.append(curFolder.getFolderId());
		sb.append("', '");
		sb.append(UnicodeFormatter.toString(curFolder.getName()));
		sb.append("'); window.close();");

		row.addButton("right", SearchEntry.DEFAULT_VALIGN, LanguageUtil.get(pageContext, "choose"), sb.toString());

		// Add result row

		resultRows.add(row);
	}

	showAddFolderButton = showAddFolderButton && DLFolderPermission.contains(permissionChecker, scopeGroupId, folderId, ActionKeys.ADD_FOLDER);
	%>

	<aui:button-row>
		<c:if test="<%= showAddFolderButton %>">
			<portlet:renderURL var="editFolderURL">
				<portlet:param name="struts_action" value="/document_library/edit_folder" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="parentFolderId" value="<%= String.valueOf(folderId) %>" />
			</portlet:renderURL>

			<aui:button onClick="<%= editFolderURL %>" value='<%= (folder == null) ? "add-folder" : "add-subfolder" %>' />
		</c:if>

		<%
		String taglibSelectOnClick = "opener." + renderResponse.getNamespace() + "selectFolder('" + folderId + "','" + folderName + "');window.close();";
		%>

		<aui:button onClick="<%= taglibSelectOnClick %>" value="choose-this-folder" />
	</aui:button-row>

	<c:if test="<%= !results.isEmpty() %>">
		<br />
	</c:if>

	<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
</aui:form>