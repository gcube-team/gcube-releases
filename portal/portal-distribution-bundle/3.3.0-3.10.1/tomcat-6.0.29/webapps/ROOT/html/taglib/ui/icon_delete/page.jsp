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

<%@ include file="/html/taglib/init.jsp" %>

<%
String url = (String)request.getAttribute("liferay-ui:icon:url");
boolean label = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:icon:label"));

if (url.startsWith("javascript:")) {
	url = url.substring(11);
}

if (url.startsWith(Http.HTTP_WITH_SLASH) || url.startsWith(Http.HTTPS_WITH_SLASH)) {
	url = "submitForm(document.hrefFm, '" + HttpUtil.encodeURL(url) + "');";
}

if (url.startsWith("wsrp_rewrite?")) {
	url = StringUtil.replace(url, "/wsrp_rewrite", "&wsrp-extensions=encodeURL/wsrp_rewrite");
	url = "submitForm(document.hrefFm, '" + url + "');";
}

url = "javascript:if (confirm('" + UnicodeLanguageUtil.get(pageContext, "are-you-sure-you-want-to-delete-this") + "')) { " + url + " } else { self.focus(); }";
%>

<liferay-ui:icon
	image="delete"
	label="<%= label %>"
	url="<%= url %>"
/>