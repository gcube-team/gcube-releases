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

<%@ page import="com.liferay.portal.kernel.servlet.BrowserSnifferUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.struts.StrutsUtil" %>
<%@ page import="com.liferay.portal.util.PropsValues" %>

<%
String editorImpl = ParamUtil.getString(request, "editorImpl", PropsValues.EDITOR_WYSIWYG_DEFAULT);

if (!BrowserSnifferUtil.isRtf(request)) {
	if (BrowserSnifferUtil.isSafari(request) && BrowserSnifferUtil.isMobile(request)) {
		editorImpl = "simple";
	}
	else if (BrowserSnifferUtil.isSafari(request) && (editorImpl.indexOf("simple") == -1)) {
		editorImpl = "tinymcesimple";
	}
	else {
		editorImpl = "simple";
	}
}

//editorImpl = "fckeditor";
//editorImpl = "liferay";
//editorImpl = "simple";
//editorImpl = "tinymce";
//editorImpl = "tinymcesimple";

// Resin won't allow dynamic content inside the jsp:include tag

RequestDispatcher rd = application.getRequestDispatcher(StrutsUtil.TEXT_HTML_DIR + "/js/editor/" + editorImpl + ".jsp");

rd.include(request, response);
%>

<%--<jsp:include page='<%= StrutsUtil.TEXT_HTML_DIR + "/js/editor/" + editorImpl + ".jsp" %>' />--%>