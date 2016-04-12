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
String strutsAction = ParamUtil.getString(request, "struts_action");

String tabs2 = ParamUtil.getString(request, "tabs2", "version-history");

String redirect = ParamUtil.getString(request, "redirect");

String referringPortletResource = ParamUtil.getString(request, "referringPortletResource");

String uploadProgressId = "dlFileEntryUploadProgress";

DLFileEntry fileEntry = (DLFileEntry)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY);

long fileEntryId = BeanParamUtil.getLong(fileEntry, request, "fileEntryId");

long folderId = BeanParamUtil.getLong(fileEntry, request, "folderId");
String name = BeanParamUtil.getString(fileEntry, request, "name");
String extension = BeanParamUtil.getString(fileEntry, request, "extension");
String title = BeanParamUtil.getString(fileEntry, request, "title");

String assetTagNames = ParamUtil.getString(request, "assetTagNames");

String[] conversions = new String[0];

if (PrefsPropsUtil.getBoolean(PropsKeys.OPENOFFICE_SERVER_ENABLED, PropsValues.OPENOFFICE_SERVER_ENABLED)) {
	conversions = (String[])DocumentConversionUtil.getConversions(extension);
}

DLFolder folder = null;

if (fileEntry != null) {
	folder = fileEntry.getFolder();
}

DLFileVersion fileVersion = null;

if (fileEntry != null) {
	fileVersion = fileEntry.getLatestFileVersion();
}

long assetClassPK = 0;

if ((fileVersion != null) && !fileVersion.isApproved() && (fileVersion.getVersion() != DLFileEntryConstants.DEFAULT_VERSION)) {
	assetClassPK = fileVersion.getFileVersionId();
}
else if (fileEntry != null) {
	assetClassPK = fileEntry.getFileEntryId();
}

Lock lock = null;
Boolean isLocked = Boolean.FALSE;
Boolean hasLock = Boolean.FALSE;

if (fileEntry != null) {
	try {
		lock = LockLocalServiceUtil.getLock(DLFileEntry.class.getName(), DLUtil.getLockId(fileEntry.getGroupId(), fileEntry.getFolderId(), fileEntry.getName()));

		isLocked = Boolean.TRUE;

		if (lock.getUserId() == user.getUserId()) {
			hasLock = Boolean.TRUE;
		}
	}
	catch (Exception e) {
	}
}

PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("struts_action", strutsAction);
portletURL.setParameter("tabs2", tabs2);
portletURL.setParameter("redirect", redirect);
portletURL.setParameter("folderId", String.valueOf(folderId));
portletURL.setParameter("name", name);
%>

<c:if test="<%= Validator.isNull(referringPortletResource) %>">
	<liferay-util:include page="/html/portlet/document_library/top_links.jsp" />
</c:if>

<c:if test="<%= isLocked %>">
	<c:choose>
		<c:when test="<%= hasLock %>">
			<div class="portlet-msg-success">
				<c:choose>
					<c:when test="<%= lock.isNeverExpires() %>">
						<liferay-ui:message key="you-now-have-an-indefinite-lock-on-this-document" />
					</c:when>
					<c:otherwise>

						<%
						String lockExpirationTime = LanguageUtil.getTimeDescription(pageContext, DLFileEntryImpl.LOCK_EXPIRATION_TIME).toLowerCase();
						%>

						<%= LanguageUtil.format(pageContext, "you-now-have-a-lock-on-this-document", lockExpirationTime, false) %>
					</c:otherwise>
				</c:choose>
			</div>
		</c:when>
		<c:otherwise>
			<div class="portlet-msg-error">
				<%= LanguageUtil.format(pageContext, "you-cannot-modify-this-document-because-it-was-locked-by-x-on-x", new Object[] {HtmlUtil.escape(PortalUtil.getUserName(lock.getUserId(), String.valueOf(lock.getUserId()))), dateFormatDateTime.format(lock.getCreateDate())}, false) %>
			</div>
		</c:otherwise>
	</c:choose>
</c:if>

<liferay-ui:header
	backURL="<%= redirect %>"
	title='<%= (fileEntry != null) ? fileEntry.getTitle() : "new-document" %>'
/>

<c:if test="<%= fileEntry == null %>">
	<div class="lfr-dynamic-uploader">
		<div class="lfr-upload-container" id="<portlet:namespace />fileUpload"></div>
	</div>

	<div class="lfr-fallback aui-helper-hidden" id="<portlet:namespace />fallback">

	<aui:script use="liferay-upload">
		new Liferay.Upload(
			{
				allowedFileTypes: '<%= StringUtil.merge(PrefsPropsUtil.getStringArray(PropsKeys.DL_FILE_EXTENSIONS, StringPool.COMMA)) %>',
				container: '#<portlet:namespace />fileUpload',
				fileDescription: '<%= StringUtil.merge(PrefsPropsUtil.getStringArray(PropsKeys.DL_FILE_EXTENSIONS, StringPool.COMMA)) %>',
				fallbackContainer: '#<portlet:namespace />fallback',
				maxFileSize: <%= PrefsPropsUtil.getLong(PropsKeys.DL_FILE_MAX_SIZE) %> / 1024,
				namespace: '<portlet:namespace />',
				uploadFile: '<liferay-portlet:actionURL windowState="<%= LiferayWindowState.POP_UP.toString() %>" doAsUserId="<%= user.getUserId() %>"><portlet:param name="struts_action" value="/document_library/edit_file_entry" /><portlet:param name="<%= Constants.CMD %>" value="<%= Constants.ADD %>" /><portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" /></liferay-portlet:actionURL><liferay-ui:input-permissions-params modelName="<%= DLFileEntry.class.getName() %>" />'
			}
		);
	</aui:script>
</c:if>

<portlet:actionURL var="editFileEntryURL">
	<portlet:param name="struts_action" value="/document_library/edit_file_entry" />
	<portlet:param name="uploader" value="classic" />
</portlet:actionURL>

<aui:form action="<%= editFileEntryURL %>" enctype="multipart/form-data" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveFileEntry(false);" %>'>
	<aui:input name="<%= Constants.CMD %>" type="hidden" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="referringPortletResource" type="hidden" value="<%= referringPortletResource %>" />
	<aui:input name="uploadProgressId" type="hidden" value="<%= uploadProgressId %>" />
	<aui:input name="folderId" type="hidden" value="<%= folderId %>" />
	<aui:input name="name" type="hidden" value="<%= name %>" />

	<liferay-ui:error exception="<%= DuplicateFileException.class %>" message="please-enter-a-unique-document-name" />
	<liferay-ui:error exception="<%= DuplicateFolderNameException.class %>" message="please-enter-a-unique-document-name" />

	<liferay-ui:error exception="<%= FileNameException.class %>">
		<liferay-ui:message key="document-names-must-end-with-one-of-the-following-extensions" /> <%= StringUtil.merge(PrefsPropsUtil.getStringArray(PropsKeys.DL_FILE_EXTENSIONS, StringPool.COMMA), StringPool.COMMA_AND_SPACE) %>.
	</liferay-ui:error>

	<liferay-ui:error exception="<%= NoSuchFolderException.class %>" message="please-enter-a-valid-folder" />

	<liferay-ui:error exception="<%= SourceFileNameException.class %>">
		<liferay-ui:message key="the-source-file-does-not-have-the-same-extension-as-the-original-file" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= FileSizeException.class %>" message="please-enter-a-file-with-a-valid-file-size" />

	<liferay-ui:asset-tags-error />

	<aui:model-context bean="<%= fileVersion %>" model="<%= DLFileVersion.class %>" />

	<c:if test="<%= (fileVersion != null) && (!fileVersion.isNew()) %>">
		<aui:workflow-status status="<%= fileVersion.getStatus() %>" version="<%= GetterUtil.getDouble(fileVersion.getVersion()) %>" />
	</c:if>

	<aui:fieldset>
		<aui:field-wrapper>

			<%
			long fileMaxSize = PrefsPropsUtil.getLong(PropsKeys.DL_FILE_MAX_SIZE) / 1024;
			%>

			<c:if test="<%= fileMaxSize != 0 %>">
				<div class="portlet-msg-info">
					<%= LanguageUtil.format(pageContext, "upload-documents-no-larger-than-x-k", String.valueOf(fileMaxSize), false) %>
				</div>
			</c:if>
		</aui:field-wrapper>

		<%
		String folderName = StringPool.BLANK;

		if (folderId > 0) {
			folder = DLFolderLocalServiceUtil.getFolder(folderId);

			folder = folder.toEscapedModel();

			folderId = folder.getFolderId();
			folderName = folder.getName();
		}
		else {
			folderName = LanguageUtil.get(pageContext, "documents-home");
		}

		%>

		<portlet:renderURL var="viewFolderURL">
			<portlet:param name="struts_action" value="/document_library/view" />
			<portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" />
		</portlet:renderURL>

		<c:choose>
			<c:when test="<%= (folderId <= 0) %>">
				<aui:field-wrapper label="folder">
					<aui:a href="<%= viewFolderURL %>" id="folderName"><%= folderName %></aui:a>

					<portlet:renderURL windowState="<%= LiferayWindowState.POP_UP.toString() %>" var="selectFolderURL">
						<portlet:param name="struts_action" value="/document_library/select_folder" />
						<portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" />
					</portlet:renderURL>

					<%
					String taglibOpenFolderWindow = "var folderWindow = window.open('" + selectFolderURL + "','folder', 'directories=no,height=640,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=680'); void(''); folderWindow.focus();";
					%>

					<aui:button onClick='<%= taglibOpenFolderWindow %>' value="select" />

					<aui:button name="removeFolderButton" onClick='<%= renderResponse.getNamespace() + "removeFolder();" %>' value="remove" />
				</aui:field-wrapper>
			</c:when>
			<c:otherwise>
				<aui:field-wrapper label="folder">
					<aui:a href="<%= viewFolderURL %>"><%= folderName %></aui:a>
				</aui:field-wrapper>
			</c:otherwise>
		</c:choose>

		<aui:input name="file" type="file" />

		<aui:input name="title" />

		<aui:input name="description" />

		<liferay-ui:custom-attributes-available className="<%= DLFileEntry.class.getName() %>">
			<liferay-ui:custom-attribute-list
				className="<%= DLFileEntry.class.getName() %>"
				classPK="<%= (fileVersion != null) ? fileVersion.getFileVersionId() : 0 %>"
				editable="<%= true %>"
				label="<%= true %>"
			/>
		</liferay-ui:custom-attributes-available>

		<aui:input classPK="<%= assetClassPK %>" model="<%= DLFileEntry.class %>" name="categories" type="assetCategories" />

		<aui:input classPK="<%= assetClassPK %>" model="<%= DLFileEntry.class %>" name="tags" type="assetTags" />

		<%
		if (fileEntry == null) {
			request.setAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY, new DLFileEntryImpl());
		}
		%>

		<%@ include file="/html/portlet/document_library/edit_file_entry_form_extra_fields.jsp" %>

		<%
		if (fileEntry == null) {
			request.removeAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY);
		}
		%>

		<c:if test="<%= fileEntry == null %>">
			<aui:field-wrapper label="permissions">
				<liferay-ui:input-permissions
					modelName="<%= DLFileEntry.class.getName() %>"
				/>
			</aui:field-wrapper>
		</c:if>

		<%
		boolean approved = false;
		boolean pending = false;

		if (fileVersion != null) {
			approved = fileVersion.isApproved();
			pending = fileVersion.isPending();
		}
		%>

		<c:if test="<%= approved %>">
			<div class="portlet-msg-info">
				<liferay-ui:message key="a-new-version-will-be-created-automatically-if-this-content-is-modified" />
			</div>
		</c:if>

		<c:if test="<%= pending %>">
			<div class="portlet-msg-info">
				<liferay-ui:message key="there-is-a-publication-workflow-in-process" />
			</div>
		</c:if>

		<aui:button-row>

			<%
			String publishButtonLabel = "publish";

			if (WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(themeDisplay.getCompanyId(), scopeGroupId, DLFileEntry.class.getName())) {
				publishButtonLabel = "submit-for-publication";
			}

			if (pending) {
				publishButtonLabel = "save";
			}
			%>

			<aui:button disabled="<%= isLocked && !hasLock %>" name="publishButton" type="submit" value="<%= publishButtonLabel %>" />

			<c:if test="<%= (fileEntry != null) && ((isLocked && hasLock) || !isLocked) %>">
				<c:choose>
					<c:when test="<%= !hasLock %>">
						<aui:button onClick='<%= renderResponse.getNamespace() + "lock();" %>' value="lock" />
					</c:when>
					<c:otherwise>
						<aui:button onClick='<%= renderResponse.getNamespace() + "unlock();" %>' value="unlock" />
					</c:otherwise>
				</c:choose>
			</c:if>

			<aui:button onClick="<%= redirect %>" type="cancel" />
		</aui:button-row>
	</aui:fieldset>
</aui:form>

<liferay-ui:upload-progress
	id="<%= uploadProgressId %>"
	message="uploading"
	redirect="<%= redirect %>"
/>

<c:if test="<%= fileEntry == null %>">
	</div>
</c:if>

<aui:script>
	function <portlet:namespace />lock() {
		submitForm(document.hrefFm, "<portlet:actionURL><portlet:param name="struts_action" value="/document_library/edit_file_entry" /><portlet:param name="<%= Constants.CMD %>" value="<%= Constants.LOCK %>" /><portlet:param name="redirect" value="<%= redirect %>" /><portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" /><portlet:param name="name" value="<%= name %>" /></portlet:actionURL>");
	}

	function <portlet:namespace />removeFolder() {
		document.<portlet:namespace />fm.<portlet:namespace />folderId.value = "<%= rootFolderId %>";

		var nameEl = document.getElementById("<portlet:namespace />folderName");

		nameEl.href = "";
		nameEl.innerHTML = "";
	}

	function <portlet:namespace />saveFileEntry() {
		<%= HtmlUtil.escape(uploadProgressId) %>.startProgress();

		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= (fileEntry == null) ? Constants.ADD : Constants.UPDATE %>";
		submitForm(document.<portlet:namespace />fm);
	}

	function <portlet:namespace />selectFolder(folderId, folderName) {
		document.<portlet:namespace />fm.<portlet:namespace />folderId.value = folderId;

		var nameEl = document.getElementById("<portlet:namespace />folderName");

		nameEl.href = "javascript:location = '<portlet:renderURL><portlet:param name="struts_action" value="/document_library/view" /></portlet:renderURL>&<portlet:namespace />folderId=" + folderId + "'; void('');";
		nameEl.innerHTML = folderName + "&nbsp;";
	}

	function <portlet:namespace />unlock() {
		submitForm(document.hrefFm, "<portlet:actionURL><portlet:param name="struts_action" value="/document_library/edit_file_entry" /><portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UNLOCK %>" /><portlet:param name="redirect" value="<%= redirect %>" /><portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" /><portlet:param name="name" value="<%= name %>" /></portlet:actionURL>");
	}

	<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />file);
	</c:if>
</aui:script>

<aui:script use="aui-base">
	var validateFile = function(fileField) {
		var value = fileField.val();

		if (value) {
			var extension = value.substring(value.lastIndexOf('.')).toLowerCase();
			var validExtensions = ['<%= StringUtil.merge(PrefsPropsUtil.getStringArray(PropsKeys.DL_FILE_EXTENSIONS, StringPool.COMMA), "', '") %>'];

			if ((A.Array.indexOf(validExtensions, '*') == -1) &&
				(A.Array.indexOf(validExtensions, extension) == -1)) {

				alert('<%= UnicodeLanguageUtil.get(pageContext, "document-names-must-end-with-one-of-the-following-extensions") %> <%= StringUtil.merge(PrefsPropsUtil.getStringArray(PropsKeys.DL_FILE_EXTENSIONS, StringPool.COMMA), StringPool.COMMA_AND_SPACE) %>');

				fileField.val('');
			}
		}
	};

	var onFileChange = function(event) {
		validateFile(event.currentTarget);
	};

	var fileField = A.one('#<portlet:namespace />file')

	if (fileField) {
		fileField.on('change', onFileChange);

		validateFile(fileField);
	}
</aui:script>

<%
if (fileEntry != null) {
	DLUtil.addPortletBreadcrumbEntries(fileEntry, request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "edit"), currentURL);
}
else {
	DLUtil.addPortletBreadcrumbEntries(folderId, request, renderResponse);

	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "add-file-entry"), currentURL);
}
%>