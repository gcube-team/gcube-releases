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

<c:choose>
	<c:when test="<%= !PropsValues.SOCIAL_EQUITY_EQUITY_LOG_ENABLED %>">
		<div class="portlet-msg-error">
			<liferay-ui:message key="social-equity-is-not-enabled" />
		</div>
	</c:when>
	<c:otherwise>
		<%@ include file="/html/portlet/top_users/view_top_users.jspf" %>
	</c:otherwise>
</c:choose>