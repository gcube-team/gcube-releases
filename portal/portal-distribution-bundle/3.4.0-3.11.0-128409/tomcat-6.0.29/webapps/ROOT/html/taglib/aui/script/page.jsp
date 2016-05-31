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

<%@ page import="com.liferay.portal.kernel.servlet.taglib.aui.ScriptData" %>
<%@ page import="com.liferay.taglib.aui.ScriptTag" %>

<%
ScriptData scriptData = (ScriptData)request.getAttribute(ScriptTag.class.getName());

if (scriptData == null) {
	scriptData = (ScriptData)request.getAttribute(WebKeys.AUI_SCRIPT_DATA);

	if (scriptData != null) {
		request.removeAttribute(WebKeys.AUI_SCRIPT_DATA);
	}
}
%>

<c:if test="<%= scriptData != null %>">
	<script type="text/javascript">
		// <![CDATA[

			<%
			StringBundler rawSB = scriptData.getRawSB();

			rawSB.writeTo(out);

			StringBundler callbackSB = scriptData.getCallbackSB();
			%>

			<c:if test="<%= callbackSB.index() > 0 %>">

				<%
				Set<String> useSet = scriptData.getUseSet();

				StringBundler useSB = new StringBundler(useSet.size() * 4);

				for (String use : useSet) {
					useSB.append(StringPool.APOSTROPHE);
					useSB.append(use);
					useSB.append(StringPool.APOSTROPHE);
					useSB.append(StringPool.COMMA_AND_SPACE);
				}
				%>

				AUI().use(

					<%
					useSB.writeTo(out);
					%>

					function(A) {

						<%
						callbackSB.writeTo(out);
						%>

					}
				);
			</c:if>
		// ]]>
	</script>
</c:if>