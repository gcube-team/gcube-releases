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

<%@ include file="/html/portal/init.jsp" %>

<%
String articleId = layout.getTypeSettingsProperties().getProperty("article-id");
String languageId = LanguageUtil.getLanguageId(request);

String content = JournalContentUtil.getContent(scopeGroupId, articleId, null, languageId, themeDisplay);
%>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
	<td>
		<%= content %>
	</td>
</tr>
</table>

<%@ include file="/html/portal/layout/view/common.jspf" %>