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

<%@ include file="/html/portlet/communities/init.jsp" %>

<%
PortletDataHandlerControl[] controls = (PortletDataHandlerControl[])request.getAttribute("render_controls.jsp-controls");
boolean portletDisabled = (Boolean)request.getAttribute("render_controls.jsp-portletDisabled");

for (int i = 0; i < controls.length; i++) {
%>

	<li class="<portlet:namespace />handler-control">
		<c:choose>
			<c:when test="<%= controls[i] instanceof PortletDataHandlerBoolean %>">

				<%
				PortletDataHandlerBoolean control = (PortletDataHandlerBoolean)controls[i];
				PortletDataHandlerControl[] children = control.getChildren();

				String taglibOnChange = children != null ? renderResponse.getNamespace() + "toggleChildren(this, '" + renderResponse.getNamespace() + control.getNamespacedControlName() + "Controls');" : null;
				%>

				<aui:input disabled="<%= controls[i].isDisabled() || portletDisabled %>" inlineLabel="right" label="<%= controls[i].getControlName() %>" name="<%= control.getNamespacedControlName() %>" onChange="<%= taglibOnChange %>" type="checkbox" value="<%= control.getDefaultState() %>" />

				<c:if test="<%= children != null %>">
					<ul id="<portlet:namespace /><%= control.getNamespacedControlName() %>Controls">

						<%
						request.setAttribute("render_controls.jsp-controls", children);
						%>

						<liferay-util:include page="/html/portlet/communities/render_controls.jsp" />
					</ul>
				</c:if>
			</c:when>
			<c:when test="<%= controls[i] instanceof PortletDataHandlerChoice %>">
				<aui:field-wrapper label='<%= "&#9632" + LanguageUtil.get(pageContext, controls[i].getControlName()) %>'>

					<%
					PortletDataHandlerChoice control = (PortletDataHandlerChoice)controls[i];
					String[] choices = control.getChoices();

					for (int j = 0; j < choices.length; j++) {
						String choice = choices[j];
					%>
						<aui:input checked="<%= control.getDefaultChoiceIndex() == j %>" inlineLabel="right" label="<%= choice %>" name="<%= control.getNamespacedControlName() %>" type="radio" value="<%= choices[j] %>" />

					<%
					}
					%>

				</aui:field-wrapper>
			</c:when>
		</c:choose>
	</li>

<%
}
%>