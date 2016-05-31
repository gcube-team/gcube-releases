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

<%@ include file="/html/portlet/top_users/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

SocialEquityUser socialEquityUser = (SocialEquityUser)row.getObject();
%>

<liferay-ui:user-display
	userId="<%= socialEquityUser.getUserId() %>"
	userName=""
>
	<c:if test="<%= userDisplay != null %>">
		<liferay-ui:message key="rank" />: <%= socialEquityUser.getRank() %><br />

		<liferay-ui:message key="contribution-score" />: <%= Math.round(socialEquityUser.getContributionEquity()) %><br />

		<liferay-ui:message key="participation-score" />: <%= Math.round(socialEquityUser.getParticipationEquity()) %>
	</c:if>
</liferay-ui:user-display>