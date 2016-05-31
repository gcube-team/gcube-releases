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

<%@ include file="/html/portlet/journal/init.jsp" %>

<%
String editorType = ParamUtil.getString(request, "editorType");

if (Validator.isNotNull(editorType)) {
	portalPrefs.setValue(PortletKeys.JOURNAL, "editor-type", editorType);
}
else {
	editorType = portalPrefs.getValue(PortletKeys.JOURNAL, "editor-type", "html");
}

boolean useEditorCodepress = editorType.equals("codepress");
%>

<aui:form method="post" name="editorForm">
	<aui:fieldset>
		<aui:select name="editorType" onChange='<%= renderResponse.getNamespace() + "updateEditorType();" %>'>
			<aui:option label="plain" value="1" />
			<aui:option label="rich" selected="<%= useEditorCodepress %>" value="0" />
		</aui:select>

		<c:choose>
			<c:when test="<%= useEditorCodepress %>">
				<aui:input inputCssClass="codepress html" label="" name="xsdContent" type="textarea" wrap="off" />
			</c:when>
			<c:otherwise>
				<aui:input inputCssClass="lfr-textarea-container" label="" name="xsdContent" type="textarea" onKeyDown="Liferay.Util.checkTab(this); Liferay.Util.disableEsc();" wrap="off" />
			</c:otherwise>
		</c:choose>
	</aui:fieldset>

	<aui:button-row>
		<aui:button onClick='<%= renderResponse.getNamespace() + "updateStructureXsd();" %>' type="button" value="update" />

		<c:if test="<%= !useEditorCodepress %>">
			<aui:button onClick='<%= "Liferay.Util.selectAndCopy(document." + renderResponse.getNamespace() + "editorForm." + renderResponse.getNamespace() + "xsdContent);" %>' type="button" value="select-and-copy" />
		</c:if>

		<aui:button onClick="AUI().DialogManager.closeByChild(this);" type="button" value="cancel" />
	</aui:button-row>
</aui:form>

<c:if test="<%= useEditorCodepress %>">
	<script src="<%= themeDisplay.getPathContext() %>/html/js/editor/codepress/codepress.js" type="text/javascript"></script>
</c:if>

<aui:script>
	function getEditorContent() {
		return <portlet:namespace />getXsd();
	}

	Liferay.provide(
		window,
		'<portlet:namespace />updateEditorType',
		function() {
			var A = AUI();

			<%
			String newEditorType = "codepress";

			if (useEditorCodepress) {
				newEditorType = "html";
			}
			%>

			var editorForm = A.one(document.<portlet:namespace />editorForm);

			if (editorForm) {
				var popup = editorForm.ancestor('.aui-widget-bd');

				if (popup) {
					popup = popup.getDOM();
				}
			}

			Liferay.Util.switchEditor(
				{
					popup: popup,
					textarea: '<portlet:namespace />xsdContent',
					url: '<portlet:renderURL windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="struts_action" value="/journal/edit_structure_xsd" /><portlet:param name="editorType" value="<%= newEditorType %>" /></portlet:renderURL>'
				}
			);
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />updateStructureXsd',
		function() {
			var A = AUI();

			document.<portlet:namespace />fm1.scroll.value = "<portlet:namespace />xsd";

			var xsdContent = A.one('input[name=<portlet:namespace />xsd]');
			var content = '';

			content = document.<portlet:namespace />editorForm.<portlet:namespace />xsdContent.value;

			xsdContent.attr('value', content);

			A.DialogManager.closeByChild(document.<portlet:namespace />editorForm);

			submitForm(document.<portlet:namespace />fm1);
		},
		['aui-dialog']
	);

	if (<%= useEditorCodepress %>) {
		document.<portlet:namespace />editorForm.<portlet:namespace />xsdContent_cp.value = getEditorContent();
	}
	else {
		document.<portlet:namespace />editorForm.<portlet:namespace />xsdContent.value = getEditorContent();
	}

	Liferay.Util.resizeTextarea('<portlet:namespace />xsdContent', <%= useEditorCodepress %>, true);
</aui:script>