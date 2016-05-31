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

<%@ include file="/html/portlet/navigation/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String[] bulletStyleOptions = StringUtil.split(themeDisplay.getTheme().getSetting("bullet-style-options"));
%>

<liferay-portlet:preview
	portletName="<%= portletResource %>"
	queryString="struts_action=/navigation/view"
/>

<div class="separator"><!-- --></div>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<aui:form action="<%= configurationURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<aui:fieldset>
		<aui:select name="displayStyle">

			<%
			for (int i = 1; i <= 6; i++) {
			%>

				<aui:option label="<%= i %>" selected="<%= displayStyle.equals(String.valueOf(i)) %>" />

			<%
			}
			%>

			<aui:option label="custom" selected='<%= displayStyle.equals("[custom]") %>' value="[custom]" />
		</aui:select>

		<aui:select name="bulletStyle">

			<%
			for (int i = 0; i < bulletStyleOptions.length; i++) {
			%>

				<aui:option label="<%= bulletStyleOptions[i] %>" selected="<%= bulletStyleOptions[i].equals(bulletStyle) %>" />

			<%
			}
			%>

			<c:if test="<%= bulletStyleOptions.length == 0 %>">
				<aui:option label="default" value="" />
			</c:if>
		</aui:select>
	</aui:fieldset>

	<aui:fieldset>
		<div id="<portlet:namespace />customDisplayOptions">
			<aui:select label="header" name="headerType">
				<aui:option label="none" selected='<%= headerType.equals("none") %>' />
				<aui:option label="portlet-title" selected='<%= headerType.equals("portlet-title") %>' />
				<aui:option label="root-layout" selected='<%= headerType.equals("root-layout") %>' />
				<aui:option label="breadcrumb" selected='<%= headerType.equals("breadcrumb") %>' />
			</aui:select>

			<aui:select label="root-layout" name="rootLayoutType">
				<aui:option label="parent-at-level" selected='<%= rootLayoutType.equals("absolute") %>' value="absolute" />
				<aui:option label="relative-parent-up-by" selected='<%= rootLayoutType.equals("relative") %>' value="relative" />
			</aui:select>

			<aui:select name="rootLayoutLevel">

				<%
				for (int i = 0; i <= 4; i++) {
				%>

					<aui:option label="<%= i %>" selected="<%= rootLayoutLevel == i %>" />

				<%
				}
				%>

			</aui:select>

			<aui:select name="includedLayouts">
				<aui:option label="auto" selected='<%= includedLayouts.equals("auto") %>' />
				<aui:option label="all" selected='<%= includedLayouts.equals("all") %>' />
			</aui:select>

			<aui:select name="nestedChildren">
				<aui:option label="yes" selected="<%= nestedChildren %>" value="1" />
				<aui:option label="no" selected="<%= !nestedChildren %>" value="0" />
			</aui:select>
		</div>
	</aui:fieldset>

	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>

<aui:script use="aui-base">
	var select = A.one('#<portlet:namespace />displayStyle');

	var toggleCustomFields = function() {
		var customDisplayStyle = A.one('#<portlet:namespace />customDisplayStyle');
		var displayStyle = select.val();

		if (customDisplayStyle) {
			var action = 'hide';

			if (displayStyle == '[custom]') {
				action = 'show';
			}

			customDisplayStyle[action]();
		}
	}

	if (select) {
		select.on('change', toggleCustomFields);

		toggleCustomFields();
	}
</aui:script>