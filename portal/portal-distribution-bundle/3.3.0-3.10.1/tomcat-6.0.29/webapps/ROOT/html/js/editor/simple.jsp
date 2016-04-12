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

	<script type="text/javascript">
		function getHTML() {
			return document.getElementById("textArea").value;
		}

		function setHTML(value) {
			document.getElementById("textArea").value = value;
		}

		function initEditor() {
			setHTML(parent.<%= initMethod %>());
		}
	</script>
</head>

<body leftmargin="0" marginheight="0" marginwidth="0" rightmargin="0" topmargin="0" onLoad="initEditor();">

<table bgcolor="#FFFFFF" cellpadding="0" cellspacing="0" height="100%" width="100%">
<tr>
	<td bgcolor="#FFFFFF" height="100%">
		<textarea style="font-family: monospace; height: 100%; width: 100%;" id="textArea" name="textArea"

		<%
		if (Validator.isNotNull(onChangeMethod)) {
		%>

			onChange="parent.<%= HtmlUtil.escape(onChangeMethod) %>(this.value)"

		<%
		}
		%>

		></textarea>
	</td>
</tr>
</table>

</body>

</html>

<%!
public static final String DEFAULT_INIT_METHOD = "initEditor";
%>