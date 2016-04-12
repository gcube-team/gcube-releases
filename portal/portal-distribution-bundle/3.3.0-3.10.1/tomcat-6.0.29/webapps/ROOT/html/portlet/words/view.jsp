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

<%@ include file="/html/portlet/words/init.jsp" %>

<%
String word = ParamUtil.getString(request, "word");
boolean scramble = ParamUtil.getBoolean(request, "scramble", true);

String[] words = (String[])request.getAttribute(WebKeys.WORDS_LIST);
%>

<portlet:renderURL var="portletURL">
	<portlet:param name="struts_action" value="/words/view" />
</portlet:renderURL>

<aui:form action="<%= portletURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.SEARCH %>" />

	<liferay-ui:error exception="<%= ScramblerException.class %>" message="please-enter-a-word-that-is-at-least-3-characters-long" />

	<aui:fieldset>
		<aui:column>
			<aui:input label="" name="word" value="<%= word %>" />
		</aui:column>

		<aui:column>
			<aui:select label="" name="scramble">
				<aui:option label="scramble" selected="<%= scramble %>" value="1" />
				<aui:option label="unscramble" selected="<%= !scramble %>" value="0" />
			</aui:select>
		</aui:column>
	</aui:fieldset>

	<aui:button-row>
		<aui:button type="submit" value="search" />
	</aui:button-row>

	<c:if test="<%= (words != null) && (words.length > 0) %>">

		<%
		for (int i = 0; i < words.length; i++) {
		%>

			<%= HtmlUtil.escape(words[i]) %><br />

		<%
		}
		%>

	</c:if>
</aui:form>

<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
	<aui:script>
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />word);
	</aui:script>
</c:if>