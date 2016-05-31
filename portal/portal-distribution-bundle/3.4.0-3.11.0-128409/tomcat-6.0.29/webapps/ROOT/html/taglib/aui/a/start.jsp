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
String cssClass = GetterUtil.getString((String)request.getAttribute("aui:a:cssClass"));
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("aui:a:dynamicAttributes");
String href = GetterUtil.getString((String)request.getAttribute("aui:a:href"));
String id = GetterUtil.getString((String)request.getAttribute("aui:a:id"));
String label = GetterUtil.getString((String)request.getAttribute("aui:a:label"));
String lang = GetterUtil.getString((String)request.getAttribute("aui:a:lang"));
String target = GetterUtil.getString((String)request.getAttribute("aui:a:target"));
%>

<c:choose>
	<c:when test="<%= Validator.isNotNull(href) %>">
		<a <%= Validator.isNotNull(cssClass) ? "class=\"" + cssClass + "\"" : StringPool.BLANK %> href="<%= HtmlUtil.escape(href) %>" <%= Validator.isNotNull(id) ? "id=\"" + namespace + id + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(lang) ? "lang=\"" + lang + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(target) ? "target=\"" + target + "\"" : StringPool.BLANK %> <%= _buildDynamicAttributes(dynamicAttributes) %>>

		<c:if test="<%= Validator.isNotNull(label) %>">
			<liferay-ui:message key="<%= label %>" />
		</c:if>
	</c:when>
	<c:otherwise>
		<span <%= Validator.isNotNull(cssClass) ? "class=\"" + cssClass + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(id) ? "id=\"" + namespace + id + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(lang) ? "lang=\"" + lang + "\"" : StringPool.BLANK %> <%= _buildDynamicAttributes(dynamicAttributes) %>>
	</c:otherwise>
</c:choose>