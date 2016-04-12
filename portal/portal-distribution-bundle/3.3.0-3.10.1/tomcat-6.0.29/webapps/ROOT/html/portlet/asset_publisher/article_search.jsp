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
ArticleSearch searchContainer = (ArticleSearch)request.getAttribute("liferay-ui:search:searchContainer");

ArticleDisplayTerms displayTerms = (ArticleDisplayTerms)searchContainer.getDisplayTerms();

String redirect = ParamUtil.getString(request, "backURL");

redirect = ParamUtil.getString(request, "redirect");

PortletURL portletURL = renderResponse.createRenderURL();

portletURL.setParameter("struts_action", "/portlet_configuration/edit_configuration");
portletURL.setParameter("redirect", redirect);
portletURL.setParameter("backURL", redirect);
portletURL.setParameter("portletResource", portletResource);
portletURL.setParameter("typeSelection", JournalArticle.class.getName());
%>

<liferay-ui:search-toggle
	id="toggle_id_journal_article_search"
	displayTerms="<%= displayTerms %>"
	buttonLabel="search-web-content"
>
	<table class="lfr-table">
	<tr>
		<td>
			<liferay-ui:message key="id" />
		</td>
		<td>
			<liferay-ui:message key="version" />
		</td>
		<td>
			<liferay-ui:message key="name" />
		</td>
		<td>
			<liferay-ui:message key="description" />
		</td>
	</tr>
	<tr>
		<td>
			<input name="<portlet:namespace /><%= displayTerms.ARTICLE_ID %>" size="20" type="text" value="<%= displayTerms.getArticleId() %>" />
		</td>
		<td>
			<input name="<portlet:namespace /><%= displayTerms.VERSION %>" size="20" type="text" value="<%= displayTerms.getVersionString() %>" />
		</td>
		<td>
			<input name="<portlet:namespace /><%= displayTerms.TITLE %>" size="20" type="text" value="<%= displayTerms.getTitle() %>" />
		</td>
		<td>
			<input name="<portlet:namespace /><%= displayTerms.DESCRIPTION %>" size="20" type="text" value="<%= displayTerms.getDescription() %>" />
		</td>
	</tr>
	<tr>
		<td>
			<liferay-ui:message key="content" />
		</td>
		<td>
			<liferay-ui:message key="type" />
		</td>
		<td colspan="2">
			<c:choose>
				<c:when test="<%= portletName.equals(PortletKeys.JOURNAL) %>">
					<liferay-ui:message key="status" />
				</c:when>
				<c:otherwise>
					<liferay-ui:message key="community" />
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr>
		<td>
			<input name="<portlet:namespace /><%= displayTerms.CONTENT %>" size="20" type="text" value="<%= displayTerms.getContent() %>" />
		</td>
		<td>
			<select name="<portlet:namespace /><%= displayTerms.TYPE %>">
				<option value=""></option>

				<%
				for (int i = 0; i < JournalArticleConstants.TYPES.length; i++) {
				%>

					<option <%= displayTerms.getType().equals(JournalArticleConstants.TYPES[i]) ? "selected" : "" %> value="<%= JournalArticleConstants.TYPES[i] %>"><%= LanguageUtil.get(pageContext, JournalArticleConstants.TYPES[i]) %></option>

				<%
				}
				%>

			</select>
		</td>
		<td colspan="2">
			<c:choose>
				<c:when test="<%= portletName.equals(PortletKeys.JOURNAL) %>">
					<select name="<portlet:namespace /><%= displayTerms.STATUS %>">
						<option value=""></option>
						<option <%= displayTerms.getStatus().equals("approved") ? "selected" : "" %> value="approved"><liferay-ui:message key="approved" /></option>
						<option <%= displayTerms.getStatus().equals("not-approved") ? "selected" : "" %> value="not-approved"><liferay-ui:message key="not-approved" /></option>
						<option <%= displayTerms.getStatus().equals("expired") ? "selected" : "" %> value="expired"><liferay-ui:message key="expired" /></option>
						<option <%= displayTerms.getStatus().equals("review") ? "selected" : "" %> value="review"><liferay-ui:message key="review" /></option>
					</select>
				</c:when>
				<c:otherwise>
					<select name="<portlet:namespace /><%= displayTerms.GROUP_ID %>">

						<%
						List<Group> myPlaces = user.getMyPlaces();

						for (Group myPlace : myPlaces) {
							if (myPlace.hasStagingGroup()) {
								myPlace = myPlace.getStagingGroup();
							}
						%>

							<option <%= displayTerms.getGroupId() == myPlace.getGroupId() ? "selected" : "" %> value="<%= myPlace.getGroupId() %>">
								<c:choose>
									<c:when test="<%= myPlace.isUser() %>">
										<liferay-ui:message key="my-community" />
									</c:when>
									<c:otherwise>
										<%= HtmlUtil.escape(myPlace.getDescriptiveName()) %>
									</c:otherwise>
								</c:choose>
							</option>

						<%
						}
						%>

					</select>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	</table>
</liferay-ui:search-toggle>

<aui:script>
	var <portlet:namespace />configurationActionURL = "";

	Liferay.Util.actsAsAspect(window);

	Liferay.provide(
		window,
		'<portlet:namespace />setFormURL',
		function() {
			var A = AUI();

			var fm = A.one(document.<portlet:namespace />fm);

			if (fm) {
				fm.attr('action', <portlet:namespace />configurationActionURL);
			}
		},
		['aui-base']
	);

	window.before('<portlet:namespace />selectAsset', <portlet:namespace />setFormURL);

	<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace /><%= displayTerms.ARTICLE_ID %>);
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace /><%= displayTerms.KEYWORDS %>);
	</c:if>
</aui:script>

<aui:script use="aui-base">
	var fm = A.one(document.<portlet:namespace />fm);

	if (fm) {
		<portlet:namespace />configurationActionURL = fm.attr('action');

		fm.attr('action', '<%= portletURL.toString() %>');
	}
</aui:script>