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

<%@ include file="/html/portlet/recent_documents/init.jsp" %>

<%
List fileRanks = DLFileRankLocalServiceUtil.getFileRanks(scopeGroupId, user.getUserId(), 0, SearchContainer.DEFAULT_DELTA);
%>

<c:choose>
	<c:when test="<%= fileRanks.isEmpty() %>">
		<liferay-ui:message key="there-are-no-recent-documents" />
	</c:when>
	<c:otherwise>
		<table class="lfr-table">

		<%
		for (int i = 0; i < fileRanks.size(); i++) {
			DLFileRank fileRank = (DLFileRank)fileRanks.get(i);

			try {
				DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getFileEntry(scopeGroupId, fileRank.getFolderId(), fileRank.getName());

				fileEntry = fileEntry.toEscapedModel();

				PortletURL rowURL = renderResponse.createActionURL();

				rowURL.setWindowState(LiferayWindowState.EXCLUSIVE);

				rowURL.setParameter("struts_action", "/recent_documents/get_file");
				rowURL.setParameter("folderId", String.valueOf(fileRank.getFolderId()));
				rowURL.setParameter("name", HtmlUtil.unescape(fileRank.getName()));
		%>

				<tr>
					<td>
						<a href="<%= rowURL.toString() %>"><img align="left" border="0" src="<%= themeDisplay.getPathThemeImages() %>/file_system/small/<%= fileEntry.getIcon() %>.png" /><%= fileEntry.getTitle() %></a>
					</td>
				</tr>

		<%
			}
			catch (Exception e) {
			}
		}
		%>

		</table>
	</c:otherwise>
</c:choose>