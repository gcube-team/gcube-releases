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

<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>

<%
String initMethod = ParamUtil.get(request, "initMethod", DEFAULT_INIT_METHOD);
String onChangeMethod = ParamUtil.getString(request, "onChangeMethod");
%>

<html>

<head>
	<title>Editor</title>

	<script src="tiny_mce/tiny_mce.js" type="text/javascript"></script>

	<script type="text/javascript">
		var onChangeCallbackCounter = 0;

		tinyMCE.init({
			mode : "textareas",
			theme : "advanced",
			extended_valid_elements : "a[name|href|target|title|onclick],img[class|src|border=0|alt|title|hspace|vspace|width|height|align|onmouseover|onmouseout|name|usemap],hr[class|width|size|noshade],font[face|size|color|style],span[class|align|style]",
			file_browser_callback : "fileBrowserCallback",
			init_instance_callback : "initInstanceCallback",
			invalid_elements: "script",
			onchange_callback : "onChangeCallback",
			plugins : "table,advhr,advimage,advlink,iespell,preview,media,searchreplace,print,contextmenu",
			theme_advanced_buttons1_add_before : "fontselect,fontsizeselect,forecolor,backcolor,separator",
			theme_advanced_buttons2_add : "separator,media,advhr,separator,preview,print",
			theme_advanced_buttons2_add_before: "cut,copy,paste,search,replace",
			theme_advanced_buttons3_add_before : "tablecontrols,separator",
			theme_advanced_disable : "formatselect,styleselect,help",
			theme_advanced_toolbar_align : "left",
			theme_advanced_toolbar_location : "top"
		});

		function fileBrowserCallback(field_name, url, type) {
		}

		function getHTML() {
			return tinyMCE.activeEditor.getContent();
		}

		function init(value) {
			setHTML(decodeURIComponent(value));
		}

		function initInstanceCallback() {
			init(parent.<%= initMethod %>());
		}

		function onChangeCallback(tinyMCE) {

			// This purposely ignores the first callback event because each call
			// to setContent triggers an undo level which fires the callback
			// when no changes have yet been made.

			// setContent is not really the correct way of initializing this
			// editor with content. The content should be placed statically
			// (from the editor's perspective) within the textarea. This is a
			// problem from the portal's perspective because it's passing the
			// content via a javascript method (initMethod).

			if (onChangeCallbackCounter > 0) {

				<%
				if (Validator.isNotNull(onChangeMethod)) {
				%>

					parent.<%= HtmlUtil.escape(onChangeMethod) %>(getHTML());

				<%
				}
				%>

			}

			onChangeCallbackCounter++;
		}

		function setHTML(value) {
			tinyMCE.activeEditor.setContent(value);
		}
	</script>
</head>

<body leftmargin="0" marginheight="0" marginwidth="0" rightmargin="0" topmargin="0">

<textarea id="textArea" name="textArea" style="height: 100%; width: 100%;"></textarea>

</body>

</html>

<%!
public static final String DEFAULT_INIT_METHOD = "initEditor";
%>