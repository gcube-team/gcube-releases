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

<%@ include file="/html/portlet/layout_configuration/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");
%>

<c:if test="<%= themeDisplay.isSignedIn() && (layout != null) && layout.isTypePortlet() %>">
	<aui:form action='<%= themeDisplay.getPathMain() + "/portal/update_layout?p_l_id=" + plid %>' method="post" name="layoutTemplates" useNamespace="<%= false %>">
		<aui:input name="doAsUserId" type="hidden" value="<%= themeDisplay.getDoAsUserId() %>" />
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="template" />
		<aui:input name="<%= WebKeys.REFERER %>" type="hidden" value="<%= redirect %>" />
		<aui:input name="refresh" type="hidden" value="<%= true %>" />

		<aui:layout cssClass="lfr-page-layouts">

			<%
			List layoutTemplates = LayoutTemplateLocalServiceUtil.getLayoutTemplates(theme.getThemeId());

			layoutTemplates = PluginUtil.restrictPlugins(layoutTemplates, user);

			Group group = layout.getGroup();

			String selector1 = StringPool.BLANK;

			if (group.isUser()) {
				selector1 = "desktop";
			}
			else if (group.isCommunity()) {
				selector1 = "community";
			}
			else if (group.isOrganization()) {
				selector1 = "organization";
			}

			String selector2 = StringPool.BLANK;

			if ((layout.getPriority() == 0) && (layout.getParentLayoutId() == LayoutConstants.DEFAULT_PARENT_LAYOUT_ID)) {
				selector2 = "firstLayout";
			}

			int i = 0;

			for (int j = 0; j < _COLUMNS_COUNT; j++) {
				int columnLayoutTemplatesCount = layoutTemplates.size() / _COLUMNS_COUNT;

				if (j < layoutTemplates.size() % _COLUMNS_COUNT) {
					columnLayoutTemplatesCount++;
				}
			%>

				<aui:column cssClass="lfr-layout-template-column">

					<%
					for (int k = 0; k < columnLayoutTemplatesCount; k++) {
						LayoutTemplate layoutTemplate = (LayoutTemplate)layoutTemplates.get(i);
					%>

						<div class="lfr-layout-template">
							<img onclick="document.getElementById('layoutTemplateId<%= i %>').checked = true;" src="<%= layoutTemplate.getContextPath() %><%= layoutTemplate.getThumbnailPath() %>" />

							<aui:input checked="<%= layoutTypePortlet.getLayoutTemplateId().equals(layoutTemplate.getLayoutTemplateId()) %>" id='<%= "layoutTemplateId" + i %>' label="<%= layoutTemplate.getName() %>" name="layoutTemplateId" type="radio" value="<%= layoutTemplate.getLayoutTemplateId() %>" />
						</div>

					<%
						i++;
					}
					%>

				</aui:column>

			<%
			}
			%>

			<aui:button-row>
				<aui:button type="submit" />
			</aui:button-row>
		</aui:layout>
	</aui:form>
</c:if>

<%!
private static final int _COLUMNS_COUNT = 4;
%>