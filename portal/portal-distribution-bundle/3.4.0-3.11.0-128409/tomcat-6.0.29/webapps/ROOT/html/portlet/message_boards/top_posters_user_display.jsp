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

<%@ include file="/html/portlet/message_boards/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

MBStatsUser statsUser = (MBStatsUser)row.getObject();

String[] ranks = MBUtil.getUserRank(preferences, themeDisplay.getLanguageId(), statsUser);
%>

<liferay-ui:user-display userId="<%= statsUser.getUserId() %>">
	<c:if test="<%= Validator.isNotNull(ranks[0]) %>">
		<liferay-ui:message key="rank" />: <%= ranks[0] %><br />
	</c:if>

	<liferay-ui:message key="posts" />: <%= statsUser.getMessageCount() %><br />
	<liferay-ui:message key="join-date" />: <%= dateFormatDate.format(userDisplay.getCreateDate()) %><br />

	<c:if test="<%= statsUser.getLastPostDate() != null %>">
		<liferay-ui:message key="last-post-date" />: <%= dateFormatDate.format(statsUser.getLastPostDate()) %>
	</c:if>
</liferay-ui:user-display>