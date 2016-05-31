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

<%@ include file="/html/portlet/journal_articles/init.jsp" %>

<%
String articleId = ParamUtil.getString(request, "articleId");
double version = ParamUtil.getDouble(request, "version", -1);
%>

<c:choose>
	<c:when test="<%= Validator.isNull(articleId) %>">

		<%
		if (Validator.isNull(type)) {
			type = null;
		}

		String status = "approved";

		PortletURL portletURL = renderResponse.createRenderURL();

		if (pageURL.equals("normal")) {
			portletURL.setWindowState(WindowState.NORMAL);
		}
		else {
			portletURL.setWindowState(WindowState.MAXIMIZED);
		}

		portletURL.setParameter("struts_action", "/journal_articles/view");

		PortletURL articleURL = PortletURLUtil.clone(portletURL, renderResponse);

		ArticleSearch searchContainer = new ArticleSearch(renderRequest, portletURL);

		searchContainer.setDelta(pageDelta);
		searchContainer.setDeltaConfigurable(false);
		searchContainer.setOrderByCol(orderByCol);
		searchContainer.setOrderByType(orderByType);
		searchContainer.setOrderByComparator(orderByComparator);

		List headerNames = searchContainer.getHeaderNames();

		headerNames.clear();

		headerNames.add("name");
		headerNames.add("display-date");
		headerNames.add("author");

		searchContainer.setOrderableHeaders(null);

		ArticleSearchTerms searchTerms = (ArticleSearchTerms)searchContainer.getSearchTerms();

		searchTerms.setGroupId(groupId);
		searchTerms.setType(type);

		if (Validator.isNotNull(structureId)) {
			searchTerms.setStructureId(structureId);
		}

		searchTerms.setDisplayDateLT(new Date());
		searchTerms.setStatus(status);
		searchTerms.setVersion(version);
		searchTerms.setAdvancedSearch(true);
		%>

		<%@ include file="/html/portlet/journal/article_search_results.jspf" %>

		<%
		List resultRows = searchContainer.getResultRows();

		for (int i = 0; i < results.size(); i++) {
			JournalArticle article = (JournalArticle)results.get(i);

			if (!JournalArticlePermission.contains(permissionChecker, article, ActionKeys.VIEW)) {
				if (searchContainer != null) {
					searchContainer.setTotal(searchContainer.getTotal() - 1);
				}

				continue;
			}

			article = article.toEscapedModel();

			ResultRow row = new ResultRow(article, article.getArticleId() + EditArticleAction.VERSION_SEPARATOR + article.getVersion(), i);

			String rowHREF = null;

			if (pageURL.equals("popUp")) {
				StringBundler sb = new StringBundler(7);

				sb.append(themeDisplay.getPathMain());
				sb.append("/journal_articles/view_article_content?groupId=");
				sb.append(article.getGroupId());
				sb.append("&articleId=");
				sb.append(article.getArticleId());
				sb.append("&version=");
				sb.append(article.getVersion());

				rowHREF = sb.toString();
			}
			else {
				articleURL.setParameter("groupId", String.valueOf(article.getGroupId()));
				articleURL.setParameter("articleId", article.getArticleId());
				articleURL.setParameter("version", String.valueOf(article.getVersion()));

				rowHREF = articleURL.toString();
			}

			String target = null;

			if (pageURL.equals("popUp")) {
				target = "_blank";
			}

			TextSearchEntry rowTextEntry = new TextSearchEntry(SearchEntry.DEFAULT_ALIGN, SearchEntry.DEFAULT_VALIGN, article.getArticleId(), rowHREF, target, null);

			/*// Article id

			row.addText(rowTextEntry);

			// Version

			rowTextEntry = (TextSearchEntry)rowTextEntry.clone();

			rowTextEntry.setName(String.valueOf(article.getVersion()));

			row.addText(rowTextEntry);*/

			// Title

			rowTextEntry = (TextSearchEntry)rowTextEntry.clone();

			rowTextEntry.setName(article.getTitle());

			row.addText(rowTextEntry);

			// Display date

			rowTextEntry = (TextSearchEntry)rowTextEntry.clone();

			rowTextEntry.setName(dateFormatDateTime.format(article.getDisplayDate()));

			row.addText(rowTextEntry);

			// Author

			rowTextEntry = (TextSearchEntry)rowTextEntry.clone();

			rowTextEntry.setName(HtmlUtil.escape(PortalUtil.getUserName(article.getUserId(), article.getUserName())));

			row.addText(rowTextEntry);

			// Add result row

			resultRows.add(row);
		}
		%>

		<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
	</c:when>
	<c:otherwise>

		<%
		String languageId = LanguageUtil.getLanguageId(request);
		int articlePage = ParamUtil.getInteger(renderRequest, "page", 1);
		String xmlRequest = PortletRequestUtil.toXML(renderRequest, renderResponse);

		JournalArticleDisplay articleDisplay = JournalContentUtil.getDisplay(groupId, articleId, null, null, languageId, themeDisplay, articlePage, xmlRequest);

		if (articleDisplay != null) {
			AssetEntryServiceUtil.incrementViewCounter(JournalArticle.class.getName(), articleDisplay.getResourcePrimKey());
		}
		%>

		<c:choose>
			<c:when test="<%= articleDisplay != null %>">
				<div class="journal-content-article">
					<%= articleDisplay.getContent() %>
				</div>

				<c:if test="<%= articleDisplay.isPaginate() %>">

					<%
					PortletURL portletURL = renderResponse.createRenderURL();

					portletURL.setParameter("articleId", articleId);
					portletURL.setParameter("version", String.valueOf(version));
					%>

					<br />

					<liferay-ui:page-iterator
						cur="<%= articleDisplay.getCurrentPage() %>"
						curParam='<%= "page" %>'
						delta="<%= 1 %>"
						maxPages="<%= 25 %>"
						total="<%= articleDisplay.getNumberOfPages() %>"
						type="article"
						url="<%= portletURL.toString() %>"
					/>

					<br />
				</c:if>
			</c:when>
			<c:otherwise>
				<div class="portlet-msg-error">
					<liferay-ui:message key="you-do-not-have-the-required-permissions" />
				</div>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>