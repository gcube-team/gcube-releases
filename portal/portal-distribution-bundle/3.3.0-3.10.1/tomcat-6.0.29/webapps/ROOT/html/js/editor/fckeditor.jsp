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
<%@ page import="com.liferay.portal.kernel.util.HttpUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>

<%
long plid = ParamUtil.getLong(request, "p_l_id");
String mainPath = ParamUtil.getString(request, "p_main_path");
String doAsUserId = ParamUtil.getString(request, "doAsUserId");
String initMethod = ParamUtil.getString(request, "initMethod", DEFAULT_INIT_METHOD);
String onChangeMethod = ParamUtil.getString(request, "onChangeMethod");
String toolbarSet = ParamUtil.getString(request, "toolbarSet", "liferay");
String cssPath = ParamUtil.getString(request, "cssPath");
String cssClasses = ParamUtil.getString(request, "cssClasses");

// To upgrade FCKEditor, download the latest version and unzip it to fckeditor.
// Add custom configuration to fckeditor/fckconfig.jsp. Copy
// fckeditor/editor/filemanager/browser/default to
// fckeditor/editor/filemanager/browser/liferay. Modify browser.html,
// frmresourceslist.html, frmresourcetype.html, and frmupload.html.

%>

<html>

<head>
	<title>Editor</title>

	<script src="fckeditor/fckeditor.js" type="text/javascript"></script>

	<script type="text/javascript">
		function getHTML() {
			return FCKeditorAPI.GetInstance("FCKeditor1").GetXHTML();
		}

		function getText() {
			return FCKeditorAPI.GetInstance("FCKeditor1").GetXHTML();
		}

		function initFckArea() {

			// LEP-3563

			if (!document.all && window.frameElement.clientWidth == 0) {

				// This is hack since FCKEditor doesn't initialize properly in
				// Gecko if the editor is hidden.

				setTimeout('initFckArea();',250);
			}
			else {
				var textArea = document.getElementById("FCKeditor1");

				textArea.value = parent.<%= HtmlUtil.escape(initMethod) %>();

				var fckEditor = new FCKeditor("FCKeditor1");

				fckEditor.Config["CustomConfigurationsPath"] = "<%= request.getContextPath() %>/html/js/editor/fckeditor/fckconfig.jsp?p_l_id=<%= plid %>&p_main_path=<%= HttpUtil.encodeURL(mainPath) %>&doAsUserId=<%= HttpUtil.encodeURL(doAsUserId) %>&cssPath=<%= HttpUtil.encodeURL(cssPath) %>&cssClasses=<%= HttpUtil.encodeURL(cssClasses) %>";

				fckEditor.BasePath = "fckeditor/";
				fckEditor.Width = "100%";
				fckEditor.Height = "100%";
				fckEditor.ToolbarSet = '<%= HtmlUtil.escape(toolbarSet) %>';

				fckEditor.ReplaceTextarea();

				// LEP-5707

				var ua = navigator.userAgent, isFirefox2andBelow = false;
				var agent = /(Firefox)\/(.+)/.exec(ua);

				if (agent && agent.length && (agent.length == 3)) {
					if (parseInt(agent[2]) && parseInt(agent[2]) < 3) {
						isFirefox2andBelow = true;
					}
				}

				if (isFirefox2andBelow) {
					var fckInstanceName = fckEditor.InstanceName;
					var fckIframe = document.getElementById(fckInstanceName + '___Frame');

					var interval = setInterval(
						function() {
							var iframe = fckIframe.contentDocument.getElementsByTagName('iframe');

							if (iframe.length) {
								iframe = iframe[0];

								iframe.onload = function(event) {
									clearInterval(interval);
									parent.stop();
								};
							}
						},
						500);
				}
			}

			setInterval(
				function() {
					try {
						onChangeCallback();
					}
					catch(e) {
					}
				},
				300
			);
		}

		function setHTML(value) {
			FCKeditorAPI.GetInstance("FCKeditor1").SetHTML(value);
		}

		function onChangeCallback() {

			<%
			if (Validator.isNotNull(onChangeMethod)) {
			%>

				var dirty = FCKeditorAPI.GetInstance("FCKeditor1").IsDirty();

				if (dirty) {
					parent.<%= HtmlUtil.escape(onChangeMethod) %>(getText());

					FCKeditorAPI.GetInstance("FCKeditor1").ResetIsDirty();
				}

			<%
			}
			%>

		}

		window.onload = function() {
			initFckArea();
		}
	</script>
</head>

<body leftmargin="0" marginheight="0" marginwidth="0" rightmargin="0" topmargin="0">

<textarea id="FCKeditor1" name="FCKeditor1" style="display: none"></textarea>

</body>

</html>

<%!
public static final String DEFAULT_INIT_METHOD = "initEditor";
%>