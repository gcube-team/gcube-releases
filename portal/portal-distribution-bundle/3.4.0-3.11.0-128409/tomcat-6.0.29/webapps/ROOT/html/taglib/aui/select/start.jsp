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
Object bean = request.getAttribute("aui:select:bean");
boolean changesContext = GetterUtil.getBoolean((String)request.getAttribute("aui:select:changesContext"));
String cssClass = GetterUtil.getString((String)request.getAttribute("aui:select:cssClass"));
boolean disabled = GetterUtil.getBoolean((String)request.getAttribute("aui:select:disabled"));
Map<String, Object> data = (Map<String, Object>)request.getAttribute("aui:select:data");
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("aui:select:dynamicAttributes");
boolean first = GetterUtil.getBoolean((String)request.getAttribute("aui:select:first"));
String helpMessage = GetterUtil.getString((String)request.getAttribute("aui:select:helpMessage"));
String id = namespace + GetterUtil.getString((String)request.getAttribute("aui:select:id"));
boolean inlineField = GetterUtil.getBoolean((String)request.getAttribute("aui:select:inlineField"));
String inlineLabel = GetterUtil.getString((String)request.getAttribute("aui:select:inlineLabel"));
String inputCssClass = GetterUtil.getString((String)request.getAttribute("aui:select:inputCssClass"));
String label = GetterUtil.getString((String)request.getAttribute("aui:select:label"));
boolean last = GetterUtil.getBoolean((String)request.getAttribute("aui:select:last"));
String listType = GetterUtil.getString((String)request.getAttribute("aui:select:listType"));
String listTypeFieldName = GetterUtil.getString((String)request.getAttribute("aui:select:listTypeFieldName"));
boolean multiple = GetterUtil.getBoolean((String)request.getAttribute("aui:select:multiple"));
String name = GetterUtil.getString((String)request.getAttribute("aui:select:name"));
String onChange = GetterUtil.getString((String)request.getAttribute("aui:select:onChange"));
String onClick = GetterUtil.getString((String)request.getAttribute("aui:select:onClick"));
String prefix = GetterUtil.getString((String)request.getAttribute("aui:select:prefix"));
boolean showEmptyOption = GetterUtil.getBoolean((String)request.getAttribute("aui:select:showEmptyOption"));
String title = GetterUtil.getString((String)request.getAttribute("aui:select:title"));

if (Validator.isNull(label) && changesContext) {
	StringBundler sb = new StringBundler(5);

	sb.append(LanguageUtil.get(pageContext, title));
	sb.append(StringPool.SPACE);
	sb.append(StringPool.OPEN_PARENTHESIS);
	sb.append(LanguageUtil.get(pageContext, "changing-the-value-of-this-field-will-reload-the-page"));
	sb.append(StringPool.CLOSE_PARENTHESIS);

	title = sb.toString();
}
else if (Validator.isNotNull(title)) {
	title = LanguageUtil.get(pageContext, title);
}

String fieldCss = _buildCss(FIELD_PREFIX, "select", inlineField, disabled, false, first, last, cssClass);
String inputCss = _buildCss(INPUT_PREFIX, "select", false, false, false, false, false, inputCssClass);
%>

<span class="<%= fieldCss %>">
	<span class="aui-field-content">
		<c:if test='<%= Validator.isNotNull(label) && !inlineLabel.equals("right") %>'>
			<label <%= _buildLabel(inlineLabel, true, id) %>>
				<liferay-ui:message key="<%= label %>" />

				<c:if test="<%= Validator.isNotNull(helpMessage) %>">
					<liferay-ui:icon-help message="<%= helpMessage %>" />
				</c:if>

				<c:if test="<%= changesContext %>">
					<span class="aui-helper-hidden-accessible">(<liferay-ui:message key="changing-the-value-of-this-field-will-reload-the-page" />)</span>
				</c:if>
			</label>
		</c:if>

		<c:if test="<%= Validator.isNotNull(prefix) %>">
			<span class="aui-prefix">
				<liferay-ui:message key="<%= prefix %>" />
			</span>
		</c:if>

		<span class='aui-field-element <%= Validator.isNotNull(label) && inlineLabel.equals("right") ? "aui-field-label-right" : StringPool.BLANK %>'>
			<select class="<%= inputCss %>" <%= disabled ? "disabled" : StringPool.BLANK %> id="<%= id %>" <%= multiple ? "multiple" : StringPool.BLANK %> name="<%= namespace + name %>" <%= Validator.isNotNull(onChange) ? "onChange=\"" + onChange + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(onClick) ? "onClick=\"" + onClick + "\"" : StringPool.BLANK %> <%= Validator.isNotNull(title) ? "title=\"" + title + "\"" : StringPool.BLANK %> <%= _buildData(data) %> <%= _buildDynamicAttributes(dynamicAttributes) %>>
				<c:if test="<%= showEmptyOption %>">
					<aui:option />
				</c:if>

				<c:if test="<%= Validator.isNotNull(listType) %>">

					<%
					int listTypeId = ParamUtil.getInteger(request, (String)request.getAttribute("aui:select:name"), BeanParamUtil.getInteger(bean, request, listTypeFieldName));

					List<ListType> listTypeModels = ListTypeServiceUtil.getListTypes(listType);

					for (ListType listTypeModel : listTypeModels) {
					%>

						<aui:option selected="<%= listTypeId == listTypeModel.getListTypeId() %>" value="<%= listTypeModel.getListTypeId() %>"><liferay-ui:message key="<%= listTypeModel.getName() %>" /></aui:option>

					<%
					}
					%>

				</c:if>