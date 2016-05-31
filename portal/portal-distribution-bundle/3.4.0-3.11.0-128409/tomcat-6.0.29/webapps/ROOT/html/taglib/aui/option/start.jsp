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
String cssClass = GetterUtil.getString((String)request.getAttribute("aui:option:cssClass"));
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("aui:option:dynamicAttributes");
String label = GetterUtil.getString((String)request.getAttribute("aui:option:label"));
boolean selected = GetterUtil.getBoolean((String)request.getAttribute("aui:option:selected"));
String style = GetterUtil.getString((String)request.getAttribute("aui:option:style"));
String value = GetterUtil.getString((String)request.getAttribute("aui:option:value"));
%>

<option <%= Validator.isNotNull(cssClass) ? "class=\"" + cssClass + "\"" : StringPool.BLANK %> <%= selected ? "selected" : StringPool.BLANK %> <%= Validator.isNotNull(style) ? "style=\"" + style + "\"" : StringPool.BLANK %> value="<%= value %>" <%= _buildDynamicAttributes(dynamicAttributes) %>>

<c:if test="<%= Validator.isNotNull(label) %>">
	<liferay-ui:message key="<%= label %>" />
</c:if>