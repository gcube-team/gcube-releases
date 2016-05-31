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

<%@ include file="/html/common/init.jsp" %>

<c:choose>
	<c:when test="<%= themeDisplay.isFacebook() || themeDisplay.isStateExclusive() %>">

		<%
		StringBundler sb = (StringBundler)request.getAttribute(WebKeys.LAYOUT_CONTENT);

		sb.writeTo(out);

		request.removeAttribute(WebKeys.LAYOUT_CONTENT);
		%>

	</c:when>
	<c:when test="<%= themeDisplay.isStatePopUp() || themeDisplay.isWidget() %>">
		<liferay-theme:include page="portal_pop_up.jsp" />
	</c:when>
	<c:otherwise>
		<liferay-theme:include page="portal_normal.jsp" />
	</c:otherwise>
</c:choose>

<%
SessionMessages.clear(request);
SessionErrors.clear(request);
%>