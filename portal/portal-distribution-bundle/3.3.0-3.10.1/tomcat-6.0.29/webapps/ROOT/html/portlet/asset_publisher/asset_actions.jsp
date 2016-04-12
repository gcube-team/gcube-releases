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

<%@ include file="/html/portlet/asset_publisher/init.jsp" %>

<%
boolean showIconLabel = ((Boolean)request.getAttribute("view.jsp-showIconLabel")).booleanValue();

AssetRenderer assetRenderer = (AssetRenderer)request.getAttribute("view.jsp-assetRenderer");

PortletURL editPortletURL = assetRenderer.getURLEdit((LiferayPortletRequest)renderRequest, (LiferayPortletResponse)renderResponse);

if (editPortletURL != null) {
	editPortletURL.setWindowState(WindowState.MAXIMIZED);
	editPortletURL.setPortletMode(PortletMode.VIEW);

	editPortletURL.setParameter("redirect", currentURL);
}

Group stageableGroup = themeDisplay.getScopeGroup();

if (themeDisplay.getScopeGroup().isLayout()) {
	stageableGroup = layout.getGroup();
}
%>

<c:if test="<%= assetRenderer.hasEditPermission(permissionChecker) && (editPortletURL != null) && !stageableGroup.hasStagingGroup() %>">
	<div class="lfr-meta-actions asset-actions">
		<liferay-ui:icon
			image="edit"
			label="<%= showIconLabel %>"
			message='<%= showIconLabel ? LanguageUtil.format(pageContext, "edit-x-x", new Object[] {"aui-helper-hidden-accessible", HtmlUtil.escape(assetRenderer.getTitle())}) : LanguageUtil.format(pageContext, "edit-x", HtmlUtil.escape(assetRenderer.getTitle())) %>'
			url="<%= editPortletURL.toString() %>"
		/>
	</div>
</c:if>