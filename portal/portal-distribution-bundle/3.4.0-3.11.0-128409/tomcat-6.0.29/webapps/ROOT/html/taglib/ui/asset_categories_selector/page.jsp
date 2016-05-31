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

<%@ include file="/html/taglib/ui/asset_categories_selector/init.jsp" %>

<%
themeDisplay.setIncludeServiceJs(true);

String randomNamespace = PortalUtil.generateRandomKey(request, "taglib_ui_asset_categories_selector_page") + StringPool.UNDERLINE;

String className = (String)request.getAttribute("liferay-ui:asset-categories-selector:className");
long classPK = GetterUtil.getLong((String)request.getAttribute("liferay-ui:asset-categories-selector:classPK"));
String hiddenInput = (String)request.getAttribute("liferay-ui:asset-categories-selector:hiddenInput");
String curCategoryIds = GetterUtil.getString((String)request.getAttribute("liferay-ui:asset-categories-selector:curCategoryIds"), "");
String curCategoryNames = StringPool.BLANK;

if (Validator.isNotNull(className) && (classPK > 0)) {
	List<AssetCategory> categories = AssetCategoryServiceUtil.getCategories(className, classPK);

	curCategoryIds = ListUtil.toString(categories, "categoryId");
	curCategoryNames = ListUtil.toString(categories, "name");
}

String curCategoryIdsParam = request.getParameter(hiddenInput);

if (curCategoryIdsParam != null) {
	curCategoryIds = curCategoryIdsParam;
}

if (Validator.isNotNull(curCategoryIds)) {
	long[] curCategoryIdsArray = GetterUtil.getLongValues(StringUtil.split(curCategoryIds));

	if(curCategoryIdsArray.length == 0) {
		curCategoryNames = StringPool.BLANK;
	}
	else {
		StringBundler sb = new StringBundler(curCategoryIdsArray.length * 2);
		for (long curCategoryId : curCategoryIdsArray) {
			AssetCategory category = AssetCategoryServiceUtil.getCategory(curCategoryId);

			sb.append(category.getName());
			sb.append(StringPool.COMMA);
		}

		sb.setIndex(sb.index() - 1);

		curCategoryNames = sb.toString();
	}
}
%>

<div class="lfr-tags-selector-content" id="<%= namespace + randomNamespace %>assetCategoriesSelector">
	<aui:input name="<%= hiddenInput %>" type="hidden" />
</div>

<aui:script use="liferay-asset-categories-selector">
	new Liferay.AssetCategoriesSelector(
		{
			contentBox: '#<%= namespace + randomNamespace %>assetCategoriesSelector',
			curEntries: '<%= curCategoryNames %>',
			curEntryIds: '<%= curCategoryIds %>',
			hiddenInput: '#<%= namespace + hiddenInput %>',
			instanceVar: '<%= namespace + randomNamespace %>'
		}
	).render();
</aui:script>