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
String cssClass = GetterUtil.getString((String)request.getAttribute("aui:layout:cssClass"));
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("aui:layout:dynamicAttributes");

String cssClasses = StringPool.BLANK;

if (Validator.isNotNull(cssClass)) {
	for (String curCssClass : StringUtil.split(cssClass, StringPool.SPACE)) {
		cssClasses += curCssClass + "-content ";
	}
}
%>

<div class="aui-layout <%= cssClass %>" <%= _buildDynamicAttributes(dynamicAttributes) %>>
	<div class="aui-layout-content <%= cssClasses %>">