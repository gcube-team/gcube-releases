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
String cssClass = GetterUtil.getString((String)request.getAttribute("aui:button:cssClass"));
boolean disabled = GetterUtil.getBoolean((String)request.getAttribute("aui:button:disabled"));
Map<String, Object> data = (Map<String, Object>)request.getAttribute("aui:button:data");
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("aui:button:dynamicAttributes");
String inputCssClass = GetterUtil.getString((String)request.getAttribute("aui:button:inputCssClass"));
String name = GetterUtil.getString((String)request.getAttribute("aui:button:name"));
String onClick = GetterUtil.getString((String)request.getAttribute("aui:button:onClick"));
String type = GetterUtil.getString((String)request.getAttribute("aui:button:type"));
String value = (String)request.getAttribute("aui:button:value");

if (onClick.startsWith(Http.HTTP_WITH_SLASH) || onClick.startsWith(Http.HTTPS_WITH_SLASH) || onClick.startsWith(StringPool.SLASH) || onClick.startsWith("wsrp_rewrite?")) {
	onClick = "location.href = '" + HtmlUtil.escape(PortalUtil.escapeRedirect(onClick)) + "';";
}
%>

<span class="<%= _buildCss(BUTTON_PREFIX, type, false, disabled, false, false, false, cssClass) %>">
	<span class="aui-button-content">
		<input class="<%= _buildCss(BUTTON_INPUT_PREFIX, type, false, false, false, false, false, inputCssClass) %>" <%= disabled ? "disabled" : StringPool.BLANK %> <%= Validator.isNotNull(name) ? "id=\"" + namespace + name + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(onClick) ? "onClick=\"" + onClick + "\"" : StringPool.BLANK %> type='<%= type.equals("cancel") ? "button" : type %>' value="<%= LanguageUtil.get(pageContext, value) %>" <%= _buildData(data) %> <%= _buildDynamicAttributes(dynamicAttributes) %> />
	</span>
</span>