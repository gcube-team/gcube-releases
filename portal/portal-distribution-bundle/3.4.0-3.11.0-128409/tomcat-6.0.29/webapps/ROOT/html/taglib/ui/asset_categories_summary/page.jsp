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

<%@ page import="com.liferay.portlet.asset.model.AssetCategory" %>
<%@ page import="com.liferay.portlet.asset.model.AssetVocabulary" %>
<%@ page import="com.liferay.portlet.asset.service.AssetCategoryServiceUtil" %>
<%@ page import="com.liferay.portlet.asset.service.AssetVocabularyServiceUtil" %>

<%
String className = (String)request.getAttribute("liferay-ui:asset-categories-summary:className");
long classPK = GetterUtil.getLong((String)request.getAttribute("liferay-ui:asset-categories-summary:classPK"));
PortletURL portletURL = (PortletURL)request.getAttribute("liferay-ui:asset-categories-summary:portletURL");

List<AssetVocabulary> vocabularies = AssetVocabularyServiceUtil.getGroupsVocabularies(new long[] {themeDisplay.getParentGroupId(), themeDisplay.getCompanyGroupId()});
List<AssetCategory> categories = AssetCategoryServiceUtil.getCategories(className, classPK);
%>

<%
for (AssetVocabulary vocabulary : vocabularies) {
	String vocabularyName = vocabulary.getName();

	List<AssetCategory> curCategories = _filterCategories(categories, vocabulary);
%>

	<c:if test="<%= !curCategories.isEmpty() %>">
		<span class="taglib-asset-categories-summary">
			<%= vocabularyName %>:

			<c:choose>
				<c:when test="<%= portletURL != null %>">

					<%
					for (AssetCategory category : curCategories) {
						portletURL.setParameter("categoryId", String.valueOf(category.getCategoryId()));
					%>

						<a class="asset-category" href="<%= portletURL.toString() %>"><%= _buildCategoryPath(category) %></a>

					<%
					}
					%>

				</c:when>
				<c:otherwise>

					<%
					for (AssetCategory category : curCategories) {
					%>

						<span class="asset-category">
							<%= _buildCategoryPath(category) %>
						</span>

					<%
					}
					%>

				</c:otherwise>
			</c:choose>
		</span>
	</c:if>
<%
}
%>

<%!
private String _buildCategoryPath(AssetCategory category) throws PortalException, SystemException {
	List<AssetCategory> ancestorCategories = category.getAncestors();

	if (ancestorCategories.isEmpty()) {
		return category.getName();
	}

	Collections.reverse(ancestorCategories);

	StringBundler sb = new StringBundler(ancestorCategories.size() * 2 + 1);

	for (AssetCategory ancestorCategory : ancestorCategories) {
		sb.append(ancestorCategory.getName());
		sb.append(" &raquo; ");
	}

	sb.append(category.getName());

	return sb.toString();
}

private List<AssetCategory> _filterCategories(List<AssetCategory> categories, AssetVocabulary vocabulary) {
	List<AssetCategory> filteredCategories = new ArrayList<AssetCategory>();

	for (AssetCategory category : categories) {
		if (category.getVocabularyId() == vocabulary.getVocabularyId()) {
			filteredCategories.add(category);
		}
	}

	return filteredCategories;
}
%>