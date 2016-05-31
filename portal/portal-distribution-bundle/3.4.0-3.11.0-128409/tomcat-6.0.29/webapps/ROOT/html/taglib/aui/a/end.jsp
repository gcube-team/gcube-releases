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

<%
String href = GetterUtil.getString((String)request.getAttribute("aui:a:href"));
String target = GetterUtil.getString((String)request.getAttribute("aui:a:target"));
%>

<c:choose>
	<c:when test="<%= Validator.isNotNull(href) %>">
		<c:if test='<%= target.equals("_blank") || target.equals("_new") %>'>
			<span class="opens-new-window-accessible"><liferay-ui:message key="opens-new-window" /></span>
		</c:if>

		</a>
	</c:when>
	<c:otherwise>
		</span>
	</c:otherwise>
</c:choose>