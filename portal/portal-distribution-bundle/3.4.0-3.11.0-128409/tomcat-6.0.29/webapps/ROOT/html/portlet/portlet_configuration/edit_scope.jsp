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

<%@ include file="/html/portlet/portlet_configuration/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");
String returnToFullPageURL = ParamUtil.getString(request, "returnToFullPageURL");

PortletPreferences preferences = PortletPreferencesFactoryUtil.getLayoutPortletSetup(layout, portletResource);

String scopeLayoutUuid = GetterUtil.getString(preferences.getValue("lfr-scope-layout-uuid", null));

Group group = layout.getGroup();
%>

<liferay-util:include page="/html/portlet/portlet_configuration/tabs1.jsp">
	<liferay-util:param name="tabs1" value="scope" />
</liferay-util:include>

<portlet:actionURL var="editScopeURL">
	<portlet:param name="struts_action" value="/portlet_configuration/edit_scope" />
	<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.SAVE %>" />
</portlet:actionURL>

<aui:form action="<%= editScopeURL %>" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="returnToFullPageURL" type="hidden" value="<%= returnToFullPageURL %>" />
	<aui:input name="portletResource" type="hidden" value="<%= portletResource %>" />

	<aui:fieldset>
		<aui:select label="scope" name="scopeLayoutUuid">
			<aui:option label="default" selected="<%= Validator.isNull(scopeLayoutUuid) %>" value="" />
			<aui:option label='<%= LanguageUtil.get(pageContext,"current-page") + " (" + HtmlUtil.escape(layout.getName(locale)) + ")" %>' selected="<%= scopeLayoutUuid.equals(layout.getUuid()) %>" value="<%= layout.getUuid() %>" />

			<%
			for (Layout curLayout : LayoutLocalServiceUtil.getLayouts(layout.getGroupId(), layout.isPrivateLayout())) {
				if (curLayout.getPlid() == layout.getPlid()) {
					continue;
				}

				if (curLayout.hasScopeGroup()) {
			%>

					<aui:option label="<%= HtmlUtil.escape(curLayout.getName(locale)) %>" selected="<%= scopeLayoutUuid.equals(curLayout.getUuid()) %>" value="<%= curLayout.getUuid() %>" />

			<%
				}
			}
			%>

		</aui:select>
	</aui:fieldset>

	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>