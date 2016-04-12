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
String action = GetterUtil.getString((String)request.getAttribute("aui:form:action"));
String cssClass = GetterUtil.getString((String)request.getAttribute("aui:form:cssClass"));
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("aui:form:dynamicAttributes");
boolean inlineLabels = GetterUtil.getBoolean((String)request.getAttribute("aui:form:inlineLabels"));
String name = GetterUtil.getString((String)request.getAttribute("aui:form:name"));
%>

<form action="<%= action %>" class="aui-form <%= cssClass %> <%= inlineLabels ? "aui-field-labels-inline" : StringPool.BLANK %>" id="<%= namespace + name %>" name="<%= namespace + name %>" <%= _buildDynamicAttributes(dynamicAttributes) %>>