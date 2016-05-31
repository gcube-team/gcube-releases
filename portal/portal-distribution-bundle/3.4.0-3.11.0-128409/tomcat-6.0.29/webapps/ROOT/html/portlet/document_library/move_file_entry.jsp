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

DLFileEntry fileEntry = (DLFileEntry)request.getAttribute(WebKeys.DOCUMENT_LIBRARY_FILE_ENTRY);

long folderId = BeanParamUtil.getLong(fileEntry, request, "folderId");
String name = BeanParamUtil.getString(fileEntry, request, "name");

Lock lock = null;
Boolean isLocked = Boolean.FALSE;
Boolean hasLock = Boolean.FALSE;

try {
	lock = LockLocalServiceUtil.getLock(DLFileEntry.class.getName(), DLUtil.getLockId(fileEntry.getGroupId(), fileEntry.getFolderId(), fileEntry.getName()));

	isLocked = Boolean.TRUE;

	if (lock.getUserId() == user.getUserId()) {
		hasLock = Boolean.TRUE;
	}
}
catch (Exception e) {
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

<portlet:actionURL var="moveFileEntryURL">
	<portlet:param name="struts_action" value="/document_library/move_file_entry" />
</portlet:actionURL>

<aui:form action="<%= moveFileEntryURL %>" enctype="multipart/form-data" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveFileEntry(false);" %>'>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.MOVE %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="folderId" type="hidden" value="<%= folderId %>" />
	<aui:input name="newFolderId" type="hidden" value="<%= folderId %>" />
	<aui:input name="name" type="hidden" value="<%= name %>" />

	<liferay-ui:header
		backURL="<%= redirect %>"
		title='<%= LanguageUtil.get(pageContext, "move") + StringPool.SPACE + fileEntry.getTitle() %>'
	/>

	<liferay-ui:error exception="<%= NoSuchFolderException.class %>" message="please-enter-a-valid-folder" />

	<aui:model-context bean="<%= fileEntry %>" model="<%= DLFileEntry.class %>" />

	<aui:fieldset>

		<%
		String folderName = StringPool.BLANK;

		if (folderId > 0) {
			DLFolder folder = DLFolderLocalServiceUtil.getFolder(folderId);

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

		<aui:field-wrapper label="current-folder">
			<liferay-ui:icon
				image="folder"
				label="true"
				message="<%= folderName %>"
				url="<%= viewFolderURL %>"
			/>
		</aui:field-wrapper>

		<aui:field-wrapper label="new-folder">
			<aui:a href="<%= viewFolderURL %>" id="folderName"><%= folderName %></aui:a>

			<portlet:renderURL windowState="<%= LiferayWindowState.POP_UP.toString() %>" var="selectFolderURL">
				<portlet:param name="struts_action" value="/document_library/select_folder" />
				<portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" />
			</portlet:renderURL>

			<%
			String taglibOpenFolderWindow = "var folderWindow = window.open('" + selectFolderURL + "','folder', 'directories=no,height=640,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=680'); void(''); folderWindow.focus();";
			%>

			<aui:button onClick='<%= taglibOpenFolderWindow %>' value="select" />
		</aui:field-wrapper>

		<aui:button-row>
			<aui:button disabled="<%= isLocked && !hasLock %>" type="submit" value="move" />

			<aui:button onClick="<%= redirect %>" type="cancel" />
		</aui:button-row>
	</aui:fieldset>
</aui:form>

<aui:script>
	function <portlet:namespace />saveFileEntry() {
		submitForm(document.<portlet:namespace />fm);
	}

	function <portlet:namespace />selectFolder(folderId, folderName) {
		document.<portlet:namespace />fm.<portlet:namespace />newFolderId.value = folderId;

		var nameEl = document.getElementById("<portlet:namespace />folderName");

		nameEl.href = "javascript:location = '<portlet:renderURL><portlet:param name="struts_action" value="/document_library/view" /></portlet:renderURL>&<portlet:namespace />folderId=" + folderId + "'; void('');";
		nameEl.innerHTML = folderName + "&nbsp;";
	}

	<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />file);
	</c:if>
</aui:script>

<%
DLUtil.addPortletBreadcrumbEntries(fileEntry, request, renderResponse);

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "move"), currentURL);
%>