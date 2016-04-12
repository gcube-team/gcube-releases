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
<%@ include file="/html/portlet/wiki/init.jsp" %>

<%
WikiPage wikiPage = (WikiPage)request.getAttribute("edit_page.jsp-wikiPage");

String format = BeanParamUtil.getString(wikiPage, request, "format", WikiPageConstants.DEFAULT_FORMAT);
%>

<div align="right">
	<liferay-ui:toggle
		id="toggle_id_wiki_edit_wiki_syntax_help"
		showMessage='<%= "&laquo; " + LanguageUtil.get(pageContext, "show-syntax-help") %>'
		hideMessage='<%= LanguageUtil.get(pageContext, "hide-syntax-help") + " &raquo;" %>'
	/>
</div>

<table class="lfr-table" width="100%">
<tr>
	<td class="lfr-top" width="70%">
		<liferay-ui:input-field model="<%= WikiPage.class %>" bean="<%= wikiPage %>" field="content" />
	</td>
	<td class="syntax-help" id="toggle_id_wiki_edit_wiki_syntax_help" style="display: <liferay-ui:toggle-value id="toggle_id_wiki_edit_wiki_syntax_help" />" valign="top">
		<h3>
			<liferay-ui:message key="syntax-help" />
		</h3>

		<liferay-util:include page="<%= WikiUtil.getHelpPage(format) %>" />

		<aui:a href="<%= WikiUtil.getHelpURL(format) %>" target="_blank"><liferay-ui:message key="learn-more" /> &raquo;</aui:a>
	</td>
</tr>
</table>