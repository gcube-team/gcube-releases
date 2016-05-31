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
String topLink = ParamUtil.getString(request, "topLink", "documents-home");

DLFolder folder = (DLFolder)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FOLDER);

long defaultFolderId = GetterUtil.getLong(preferences.getValue("rootFolderId", StringPool.BLANK), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

long folderId = BeanParamUtil.getLong(folder, request, "folderId", defaultFolderId);

if ((folder == null) && (defaultFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {
	try {
		folder = DLFolderLocalServiceUtil.getFolder(folderId);
	}
	catch (NoSuchFolderException nsfe) {
		folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
	}
}

int status = WorkflowConstants.STATUS_APPROVED;

if (permissionChecker.isCompanyAdmin() || permissionChecker.isCommunityAdmin(scopeGroupId)) {
	status = WorkflowConstants.STATUS_ANY;
}

int foldersCount = DLFolderServiceUtil.getFoldersCount(scopeGroupId, folderId);
int fileEntriesCount = DLFolderServiceUtil.getFileEntriesAndFileShortcutsCount(scopeGroupId, folderId, status);

long categoryId = ParamUtil.getLong(request, "categoryId");
String tagName = ParamUtil.getString(request, "tag");

String categoryName = null;
String vocabularyName = null;

if (categoryId != 0) {
	AssetCategory assetCategory = AssetCategoryLocalServiceUtil.getAssetCategory(categoryId);

	categoryName = assetCategory.getName();

	AssetVocabulary assetVocabulary = AssetVocabularyLocalServiceUtil.getAssetVocabulary(assetCategory.getVocabularyId());

	vocabularyName = assetVocabulary.getName();
}

boolean useAssetEntryQuery = (categoryId > 0) || Validator.isNotNull(tagName);

PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("struts_action", "/document_library/view");
portletURL.setParameter("topLink", topLink);
portletURL.setParameter("folderId", String.valueOf(folderId));

request.setAttribute("view.jsp-folder", folder);

request.setAttribute("view.jsp-folderId", String.valueOf(folderId));

request.setAttribute("view.jsp-viewFolder", Boolean.TRUE.toString());

request.setAttribute("view.jsp-useAssetEntryQuery", String.valueOf(useAssetEntryQuery));
%>

<liferay-util:include page="/html/portlet/document_library/top_links.jsp" />

<c:choose>
	<c:when test="<%= useAssetEntryQuery %>">
		<c:if test="<%= Validator.isNotNull(categoryName) %>">
			<h1 class="entry-title">
				<%= LanguageUtil.format(pageContext, "documents-with-x-x", new String[] {vocabularyName, categoryName}) %>
			</h1>
		</c:if>

		<c:if test="<%= Validator.isNotNull(tagName) %>">
			<h1 class="entry-title">
				<%= LanguageUtil.format(pageContext, "documents-with-tag-x", tagName) %>
			</h1>
		</c:if>

		<%@ include file="/html/portlet/document_library/view_file_entries.jspf" %>

		<%
		if (portletName.equals(PortletKeys.DOCUMENT_LIBRARY) || portletName.equals(PortletKeys.DOCUMENT_LIBRARY_DISPLAY)) {
			PortalUtil.addPageKeywords(tagName, request);
			PortalUtil.addPageKeywords(categoryName, request);
		}
		%>

	</c:when>
	<c:when test='<%= topLink.equals("documents-home") %>'>
		<aui:layout>
			<c:if test="<%= folder != null %>">

				<%
				long parentFolderId = defaultFolderId;
				String parentFolderName = LanguageUtil.get(pageContext, "documents-home");

				if (!folder.isRoot()) {
					DLFolder parentFolder = folder.getParentFolder();

					parentFolderId = parentFolder.getFolderId();
					parentFolderName = parentFolder.getName();
				}
				%>

				<portlet:renderURL var="backURL">
					<portlet:param name="struts_action" value="/document_library/view" />
					<portlet:param name="folderId" value="<%= String.valueOf(parentFolderId) %>" />
				</portlet:renderURL>

				<liferay-ui:header
					title="<%= folder.getName() %>"
					backLabel='<%= "&laquo; " + LanguageUtil.format(pageContext, "back-to-x", HtmlUtil.escape(parentFolderName)) %>'
					backURL="<%= backURL.toString() %>"
				/>
			</c:if>

			<aui:column columnWidth="<%= showFolderMenu ? 75 : 100 %>" cssClass="lfr-asset-column lfr-asset-column-details" first="<%= true %>">
				<liferay-ui:panel-container extended="<%= false %>" persistState="<%= true %>">
					<c:if test="<%= folder != null %>">
						<c:if test="<%= Validator.isNotNull(folder.getDescription()) %>">
							<div class="lfr-asset-description">
								<%= HtmlUtil.escape(folder.getDescription()) %>
							</div>
						</c:if>

						<div class="lfr-asset-metadata">
							<div class="lfr-asset-icon lfr-asset-date">
								<%= LanguageUtil.format(pageContext, "last-updated-x", dateFormatDateTime.format(folder.getModifiedDate())) %>
							</div>

							<div class="lfr-asset-icon lfr-asset-subfolders">
								<%= foldersCount %> <liferay-ui:message key='<%= (foldersCount == 1) ? "subfolder" : "subfolders" %>' />
							</div>

							<div class="lfr-asset-icon lfr-asset-items last">
								<%= fileEntriesCount %> <liferay-ui:message key='<%= (fileEntriesCount == 1) ? "document" : "documents" %>' />
							</div>
						</div>

						<liferay-ui:custom-attributes-available className="<%= DLFolder.class.getName() %>">
							<liferay-ui:custom-attribute-list
								className="<%= DLFolder.class.getName() %>"
								classPK="<%= (folder != null) ? folder.getFolderId() : 0 %>"
								editable="<%= false %>"
								label="<%= true %>"
							/>
						</liferay-ui:custom-attributes-available>
					</c:if>

					<c:if test="<%= foldersCount > 0 %>">
						<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" persistState="<%= true %>" title='<%= LanguageUtil.get(pageContext, (folder != null) ? "subfolders" : "folders") %>'>
							<liferay-ui:search-container
								curParam="cur1"
								delta="<%= foldersPerPage %>"
								deltaConfigurable="<%= false %>"
								headerNames="<%= StringUtil.merge(folderColumns) %>"
								iteratorURL="<%= portletURL %>"
							>
								<liferay-ui:search-container-results
									results="<%= DLFolderServiceUtil.getFolders(scopeGroupId, folderId, searchContainer.getStart(), searchContainer.getEnd()) %>"
									total="<%= foldersCount %>"
								/>

								<liferay-ui:search-container-row
									className="com.liferay.portlet.documentlibrary.model.DLFolder"
									escapedModel="<%= true %>"
									keyProperty="folderId"
									modelVar="curFolder"
								>
									<liferay-portlet:renderURL varImpl="rowURL">
										<portlet:param name="struts_action" value="/document_library/view" />
										<portlet:param name="folderId" value="<%= String.valueOf(curFolder.getFolderId()) %>" />
									</liferay-portlet:renderURL>

									<%@ include file="/html/portlet/document_library/folder_columns.jspf" %>
								</liferay-ui:search-container-row>

								<liferay-ui:search-iterator />
							</liferay-ui:search-container>
						</liferay-ui:panel>
					</c:if>

					<c:choose>
						<c:when test="<%= showTabs %>">
							<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" persistState="<%= true %>" title='<%= LanguageUtil.get(pageContext, "documents") %>'>
								<%@ include file="/html/portlet/document_library/view_file_entries.jspf" %>
							</liferay-ui:panel>
						</c:when>
						<c:otherwise>
							<%@ include file="/html/portlet/document_library/view_file_entries.jspf" %>
						</c:otherwise>
					</c:choose>
				</liferay-ui:panel-container>
			</aui:column>

			<c:if test="<%= showFolderMenu %>">
				<aui:column columnWidth="<%= 25 %>" cssClass="lfr-asset-column lfr-asset-column-actions" last="<%= true %>">
					<div class="lfr-asset-summary">
						<liferay-ui:icon
							cssClass="lfr-asset-avatar"
							image='<%= "../file_system/large/" + (((foldersCount + fileEntriesCount) > 0) ? "folder_full_document" : "folder_empty") %>'
							message='<%= (folder != null) ? folder.getName() : LanguageUtil.get(pageContext, "documents-home") %>'
						/>

						<div class="lfr-asset-name">
							<h4><%= (folder != null) ? folder.getName() : LanguageUtil.get(pageContext, "documents-home") %></h4>
						</div>
					</div>

					<%
					request.removeAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
					%>

					<liferay-util:include page="/html/portlet/document_library/folder_action.jsp" />
				</aui:column>
			</c:if>
		</aui:layout>

		<%
		if (folder != null) {
			DLUtil.addPortletBreadcrumbEntries(folder, request, renderResponse);

			if (portletName.equals(PortletKeys.DOCUMENT_LIBRARY)) {
				PortalUtil.setPageSubtitle(folder.getName(), request);
				PortalUtil.setPageDescription(folder.getDescription(), request);
			}
		}
		%>

	</c:when>
	<c:when test='<%= topLink.equals("my-documents") || topLink.equals("recent-documents") %>'>
		<aui:layout>
			<liferay-ui:header
				title="<%= topLink %>"
			/>

			<liferay-ui:search-container
				delta="<%= fileEntriesPerPage %>"
				deltaConfigurable="<%= false %>"
				emptyResultsMessage="there-are-no-documents"
				iteratorURL="<%= portletURL %>"
			>

				<%
				long groupFileEntriesUserId = 0;

				if (topLink.equals("my-documents") && themeDisplay.isSignedIn()) {
					groupFileEntriesUserId = user.getUserId();
				}
				%>

				<liferay-ui:search-container-results
					results="<%= DLFileEntryServiceUtil.getGroupFileEntries(scopeGroupId, groupFileEntriesUserId, searchContainer.getStart(), searchContainer.getEnd()) %>"
					total="<%= DLFileEntryServiceUtil.getGroupFileEntriesCount(scopeGroupId, groupFileEntriesUserId) %>"
				/>

				<liferay-ui:search-container-row
					className="com.liferay.portlet.documentlibrary.model.DLFileEntry"
					escapedModel="<%= true %>"
					keyProperty="fileEntryId"
					modelVar="fileEntry"
				>

					<%
					String rowHREF = null;

					if (DLFileEntryPermission.contains(permissionChecker, fileEntry, ActionKeys.VIEW)) {
						PortletURL viewFileEntryURL = renderResponse.createRenderURL();

						viewFileEntryURL.setParameter("struts_action", "/document_library/view_file_entry");
						viewFileEntryURL.setParameter("redirect", currentURL);
						viewFileEntryURL.setParameter("fileEntryId", String.valueOf(fileEntry.getFileEntryId()));

						rowHREF = viewFileEntryURL.toString();
					}
					%>

					<%@ include file="/html/portlet/document_library/file_entry_columns.jspf" %>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator />
			</liferay-ui:search-container>
		</aui:layout>

		<%
		PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, topLink), currentURL);

		PortalUtil.setPageSubtitle(LanguageUtil.get(pageContext, topLink), request);
		%>

	</c:when>
</c:choose>