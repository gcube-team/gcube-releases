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

<%@ include file="/html/taglib/ui/panel/init.jsp" %>

<div class="lfr-panel <%= cssClass %>" id="<%= id %>">
	<div class="lfr-panel-titlebar">
		<div class="lfr-panel-title">
			<span>
				<%= title %>
			</span>
		</div>

		<c:if test="<%= collapsible && extended %>">
			<a class="lfr-panel-button" href="javascript:;"></a>
		</c:if>
	</div>

	<div class="lfr-panel-content">