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

<%@ include file="/html/portlet/recent_bloggers/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

Object[] objArray = (Object[])row.getObject();

BlogsStatsUser statsUser = (BlogsStatsUser)objArray[0];
String rowHREF = (String)objArray[1];
%>

<liferay-ui:user-display userId="<%= statsUser.getUserId() %>" url="<%= rowHREF %>">
	<liferay-ui:message key="posts" />: <%= statsUser.getEntryCount() %><br />
	<liferay-ui:message key="stars" />: <%= statsUser.getRatingsTotalEntries() %><br />
	<liferay-ui:message key="date" />: <%= dateFormatDate.format(statsUser.getLastPostDate()) %>
</liferay-ui:user-display>