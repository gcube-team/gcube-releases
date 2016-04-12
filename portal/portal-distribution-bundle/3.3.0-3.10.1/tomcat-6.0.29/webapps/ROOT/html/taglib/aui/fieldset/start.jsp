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
boolean column = GetterUtil.getBoolean((String)request.getAttribute("aui:fieldset:column"));
String cssClass = GetterUtil.getString((String)request.getAttribute("aui:fieldset:cssClass"));
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("aui:fieldset:dynamicAttributes");
String label = GetterUtil.getString((String)request.getAttribute("aui:fieldset:label"));
%>

<fieldset class="aui-fieldset <%= cssClass %> <%= column ? "aui-column aui-form-column" : StringPool.BLANK %>" <%= _buildDynamicAttributes(dynamicAttributes) %>>
	<c:if test="<%= Validator.isNotNull(label) %>">
		<aui:legend label="<%= label %>" />
	</c:if>

	<div class="aui-fieldset-content <%= column ? "aui-column-content" : StringPool.BLANK %>">