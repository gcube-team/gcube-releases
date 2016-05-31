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
String randomNamespace = PortalUtil.generateRandomKey(request, "portlet_document_library_folder_action") + StringPool.UNDERLINE;

String redirect = currentURL;

ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

DLFolder folder = null;

long folderId = 0;

if (row != null) {
	folder = (DLFolder)row.getObject();

	folderId = folder.getFolderId();
}
else {
	folder = (DLFolder)request.getAttribute("view.jsp-folder");

	folderId = GetterUtil.getLong((String)request.getAttribute("view.jsp-folderId"));
}

String modelResource = null;
String modelResourceDescription = null;
String resourcePrimKey = null;

boolean showPermissionsURL = false;

if (folder != null) {
	modelResource = DLFolder.class.getName();
	modelResourceDescription = folder.getName();
	resourcePrimKey = String.valueOf(folderId);

	showPermissionsURL = DLFolderPermission.contains(permissionChecker, folder, ActionKeys.PERMISSIONS);
}
else {
	modelResource = "com.liferay.portlet.documentlibrary";
	modelResourceDescription = themeDisplay.getScopeGroupName();
	resourcePrimKey = String.valueOf(scopeGroupId);

	showPermissionsURL = GroupPermissionUtil.contains(permissionChecker, scopeGroupId, ActionKeys.PERMISSIONS);
}

boolean view = false;

if (row == null) {
	view = true;
}
%>

<liferay-ui:icon-menu showExpanded="<%= view %>" showWhenSingleIcon="<%= view %>">
	<c:if test="<%= (folder != null) && DLFolderPermission.contains(permissionChecker, scopeGroupId, folderId, ActionKeys.UPDATE) %>">
		<portlet:renderURL var="editURL">
			<portlet:param name="struts_action" value="/document_library/edit_folder" />
			<portlet:param name="redirect" value="<%= redirect %>" />
			<portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" />
		</portlet:renderURL>

		<liferay-ui:icon
			image="edit"
			url="<%= editURL %>"
		/>
	</c:if>

	<c:if test="<%= showPermissionsURL %>">
		<liferay-security:permissionsURL
			modelResource="<%= modelResource %>"
			modelResourceDescription="<%= HtmlUtil.escape(modelResourceDescription) %>"
			resourcePrimKey="<%= resourcePrimKey %>"
			var="permissionsURL"
		/>

		<liferay-ui:icon image="permissions" url="<%= permissionsURL %>" />
	</c:if>

	<c:if test="<%= (folder != null) && DLFolderPermission.contains(permissionChecker, scopeGroupId, folderId, ActionKeys.DELETE) %>">
		<portlet:renderURL var="redirectURL">
			<portlet:param name="struts_action" value="/document_library/view" />
			<portlet:param name="folderId" value="<%= String.valueOf(folder.getParentFolderId()) %>" />
		</portlet:renderURL>

		<portlet:actionURL var="deleteURL">
			<portlet:param name="struts_action" value="/document_library/edit_folder" />
			<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE %>" />
			<portlet:param name="redirect" value="<%= view ? redirectURL : redirect %>" />
			<portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" />
		</portlet:actionURL>

		<liferay-ui:icon-delete url="<%= deleteURL %>" />
	</c:if>

	<c:if test="<%= DLFolderPermission.contains(permissionChecker, scopeGroupId, folderId, ActionKeys.ADD_FOLDER) %>">
		<portlet:renderURL var="addFolderURL">
			<portlet:param name="struts_action" value="/document_library/edit_folder" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="parentFolderId" value="<%= String.valueOf(folderId) %>" />
		</portlet:renderURL>

		<liferay-ui:icon image="add_folder" message='<%= (folder != null) ? "add-subfolder" : "add-folder" %>' url="<%= addFolderURL %>" />
	</c:if>

	<c:if test="<%= DLFolderPermission.contains(permissionChecker, scopeGroupId, folderId, ActionKeys.ADD_DOCUMENT) %>">
		<portlet:renderURL var="editFileEntryURL">
			<portlet:param name="struts_action" value="/document_library/edit_file_entry" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" />
		</portlet:renderURL>

		<liferay-ui:icon image="../document_library/add_document" message="add-document" url="<%= editFileEntryURL %>" />
	</c:if>

	<c:if test="<%= DLFolderPermission.contains(permissionChecker, scopeGroupId, folderId, ActionKeys.ADD_SHORTCUT) %>">
		<portlet:renderURL var="editFileShortcutURL">
			<portlet:param name="struts_action" value="/document_library/edit_file_shortcut" />
			<portlet:param name="redirect" value="<%= currentURL %>" />
			<portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" />
		</portlet:renderURL>

		<liferay-ui:icon image="add_instance" message="add-shortcut" url="<%= editFileShortcutURL %>" />
	</c:if>

	<c:if test="<%= portletDisplay.isWebDAVEnabled() && DLFolderPermission.contains(permissionChecker, scopeGroupId, folderId, ActionKeys.VIEW) %>">
		<liferay-ui:icon cssClass='<%= randomNamespace + "-webdav-action" %>' image="desktop" message="access-from-desktop" url="javascript:;" />
	</c:if>
</liferay-ui:icon-menu>

<div id="<%= randomNamespace %>webDav" style="display: none;">
	<div class="portlet-document-library">

		<%
		String webDavHelpMessage = null;

		if (BrowserSnifferUtil.isWindows(request)) {
			webDavHelpMessage = LanguageUtil.format(pageContext, "webdav-windows-help", new Object[] {"http://www.microsoft.com/downloads/details.aspx?FamilyId=17C36612-632E-4C04-9382-987622ED1D64", "http://www.liferay.com/web/guest/community/wiki/-/wiki/Main/WebDAV"});
		}
		else {
			webDavHelpMessage = LanguageUtil.format(pageContext, "webdav-help", "http://www.liferay.com/web/guest/community/wiki/-/wiki/Main/WebDAV");
		}
		%>

		<liferay-ui:message key="<%= webDavHelpMessage %>" />

		<br /><br />

		<div class="file-entry-field">
			<label><liferay-ui:message key="webdav-url" /></label>

			<%
			StringBuilder sb = new StringBuilder();

			if (folder != null) {
				DLFolder curFolder = folder;

				while (true) {
					sb.insert(0, HttpUtil.encodeURL(curFolder.getName(), true));
					sb.insert(0, StringPool.SLASH);

					if (curFolder.getParentFolderId() == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
						break;
					}
					else {
						curFolder = DLFolderLocalServiceUtil.getFolder(curFolder.getParentFolderId());
					}
				}
			}

			Group group = themeDisplay.getScopeGroup();
			%>

			<liferay-ui:input-resource
				url='<%= themeDisplay.getPortalURL() + "/tunnel-web/secure/webdav" + group.getFriendlyURL() + "/document_library" + sb.toString() %>'
			/>
		</div>
	</div>
</div>

<aui:script use="aui-dialog">
	var webdavAction = A.one('.<%= randomNamespace %>-webdav-action');

	if (webdavAction) {
		webdavAction.on(
			'click',
			function(event) {
				var popup = new A.Dialog(
					{
						bodyContent: A.one('#<%= randomNamespace %>webDav').html(),
						centered: true,
						destroyOnClose: true,
						modal: true,
						title: '<liferay-ui:message key="access-from-desktop" />',
						width: 500
					}
				).render();

				event.preventDefault();
			}
		);
	}
</aui:script>