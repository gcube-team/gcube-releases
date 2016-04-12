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

<%@ include file="/html/portlet/communities/init.jsp" %>

<%
String tabs4 = (String)request.getAttribute("edit_pages.jsp-tab4");

String redirect = ParamUtil.getString(request, "redirect");

Group liveGroup = (Group)request.getAttribute("edit_pages.jsp-liveGroup");
long groupId = ((Long)request.getAttribute("edit_pages.jsp-groupId")).longValue();
long liveGroupId = ((Long)request.getAttribute("edit_pages.jsp-liveGroupId")).longValue();
boolean privateLayout = ((Boolean)request.getAttribute("edit_pages.jsp-privateLayout")).booleanValue();
UnicodeProperties liveGroupTypeSettings = (UnicodeProperties)request.getAttribute("edit_pages.jsp-liveGroupTypeSettings");

String rootNodeName = (String)request.getAttribute("edit_pages.jsp-rootNodeName");

PortletURL portletURL = (PortletURL)request.getAttribute("edit_pages.jsp-portletURL");
%>

<liferay-ui:error exception="<%= LayoutImportException.class %>" message="an-unexpected-error-occurred-while-importing-your-file" />

<%
List portletsList = new ArrayList();
Set portletIdsSet = new HashSet();

Iterator itr1 = LayoutLocalServiceUtil.getLayouts(liveGroupId, privateLayout).iterator();

while (itr1.hasNext()) {
	Layout curLayout = (Layout)itr1.next();

	if (curLayout.isTypePortlet()) {
		LayoutTypePortlet curLayoutTypePortlet = (LayoutTypePortlet)curLayout.getLayoutType();

		Iterator itr2 = curLayoutTypePortlet.getPortletIds().iterator();

		while (itr2.hasNext()) {
			Portlet curPortlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), (String)itr2.next());

			if (curPortlet != null) {
				PortletDataHandler portletDataHandler = curPortlet.getPortletDataHandlerInstance();

				if ((portletDataHandler != null) && !portletIdsSet.contains(curPortlet.getRootPortletId())) {
					portletIdsSet.add(curPortlet.getRootPortletId());

					portletsList.add(curPortlet);
				}
			}
		}
	}
}

List<Portlet> alwaysExportablePortlets = LayoutExporter.getAlwaysExportablePortlets(company.getCompanyId());

for (Portlet alwaysExportablePortlet : alwaysExportablePortlets) {
	if (!portletIdsSet.contains(alwaysExportablePortlet.getRootPortletId())) {
		portletIdsSet.add(alwaysExportablePortlet.getRootPortletId());

		portletsList.add(alwaysExportablePortlet);
	}
}

portletsList = ListUtil.sort(portletsList, new PortletTitleComparator(application, locale));

String tabs4Names = "export,import";

if (!StringUtil.contains(tabs4Names, tabs4)) {
	tabs4 = "export";
}
%>

<aui:fieldset>
	<liferay-ui:tabs
		names="<%= tabs4Names %>"
		param="tabs4"
		url="<%= portletURL.toString() %>"
	/>

	<liferay-ui:error exception="<%= LARFileException.class %>" message="please-specify-a-lar-file-to-import" />
	<liferay-ui:error exception="<%= LARTypeException.class %>" message="please-import-a-lar-file-of-the-correct-type" />
	<liferay-ui:error exception="<%= LayoutImportException.class %>" message="an-unexpected-error-occurred-while-importing-your-file" />

	<c:choose>
		<c:when test='<%= tabs4.equals("export") %>'>
			<aui:input label="export-the-selected-data-to-the-given-lar-file-name" name="exportFileName" size="50" value='<%= HtmlUtil.escape(StringUtil.replace(rootNodeName, " ", "_")) + "-" + Time.getShortTimestamp() + ".lar" %>' />

			<aui:field-wrapper label="what-would-you-like-to-export">
				<%@ include file="/html/portlet/communities/edit_pages_export_import_options.jspf" %>
			</aui:field-wrapper>

			<aui:button-row>
				<aui:button onClick='<%= renderResponse.getNamespace() + "exportPages();" %>' value="export" />

				<aui:button onClick="<%= redirect %>" type="cancel" />
			</aui:button-row>
		</c:when>
		<c:when test='<%= tabs4.equals("import") %>'>
			<c:choose>
				<c:when test="<%= (layout.getGroupId() != groupId) || (layout.isPrivateLayout() != privateLayout) %>">
					<aui:input label="import-a-lar-file-to-overwrite-the-selected-data" name="importFileName" size="50" type="file" />

					<aui:field-wrapper label="what-would-you-like-to-import">
						<%@ include file="/html/portlet/communities/edit_pages_export_import_options.jspf" %>
					</aui:field-wrapper>

					<aui:button-row>
						<aui:button onClick='<%= renderResponse.getNamespace() + "importPages();" %>' type="button" value="import" />

						<aui:button onClick="<%= redirect %>" type="cancel" />
					</aui:button-row>
				</c:when>
				<c:otherwise>
					<liferay-ui:message key="import-from-within-the-target-community-can-cause-conflicts" />
				</c:otherwise>
			</c:choose>
		</c:when>
	</c:choose>
</aui:fieldset>

<aui:script use="aui-base,selector-css3">
	var toggleHandlerControl = function(item, index, collection) {
		var container = item.ancestor('.<portlet:namespace />handler-control').one('ul');

		if (container) {
			var action = 'hide';

			if (item.get('checked')) {
				action = 'show';
			}

			container[action]();
		}
	};

	var checkboxes = A.all('.<portlet:namespace />handler-control input[type=checkbox]');

	if (checkboxes) {
		var uncheckedBoxes = checkboxes.filter(':not(:checked)');

		if (uncheckedBoxes) {
			uncheckedBoxes.each(toggleHandlerControl);
		}

		checkboxes.detach('click');

		checkboxes.on(
			'click',
			function(event) {
				toggleHandlerControl(event.currentTarget);
			}
		);
	}
</aui:script>