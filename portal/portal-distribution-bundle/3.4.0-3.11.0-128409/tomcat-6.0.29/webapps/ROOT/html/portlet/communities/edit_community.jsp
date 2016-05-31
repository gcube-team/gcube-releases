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
String redirect = ParamUtil.getString(request, "redirect");

Group group = (Group)request.getAttribute(WebKeys.GROUP);

long groupId = BeanParamUtil.getLong(group, request, "groupId");

int type = BeanParamUtil.getInteger(group, request, "type");
String friendlyURL = BeanParamUtil.getString(group, request, "friendlyURL");
%>

<c:if test="<%= !portletName.equals(PortletKeys.COMMUNITIES) %>">
	<liferay-util:include page="/html/portlet/communities/toolbar.jsp">
		<liferay-util:param name="toolbarItem" value='<%= (group == null) ? "add" : "view-all" %>' />
	</liferay-util:include>
</c:if>

<liferay-ui:header
	backURL="<%= redirect %>"
	title='<%= (group == null) ? "new-community" : group.getDescriptiveName() %>'
/>

<portlet:actionURL var="editCommunityURL">
	<portlet:param name="struts_action" value="/communities/edit_community" />
</portlet:actionURL>

<aui:form action="<%= editCommunityURL %>" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveGroup();" %>'>
	<aui:input name="<%= Constants.CMD %>" type="hidden" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="groupId" type="hidden" value="<%= groupId %>" />
	<aui:input name="friendlyURL" type="hidden" value="<%= friendlyURL %>" />

	<liferay-ui:error exception="<%= DuplicateGroupException.class %>" message="please-enter-a-unique-name" />
	<liferay-ui:error exception="<%= GroupNameException.class %>" message="please-enter-a-valid-name" />
	<liferay-ui:error exception="<%= RequiredGroupException.class %>" message="old-group-name-is-a-required-system-group" />

	<aui:model-context bean="<%= group %>" model="<%= Group.class %>" />

	<aui:fieldset>
		<c:if test="<%= group != null %>">
			<aui:field-wrapper label="group-id">
				<%= groupId %>
			</aui:field-wrapper>
		</c:if>

		<c:choose>
			<c:when test="<%= (group != null) && PortalUtil.isSystemGroup(group.getName()) %>">
				<aui:input name="name" type="hidden" />
			</c:when>
			<c:otherwise>
				<aui:input name="name" />
			</c:otherwise>
		</c:choose>

		<aui:input name="description" />

		<aui:select name="type">
			<aui:option label="open" selected="<%= (type == GroupConstants.TYPE_COMMUNITY_OPEN) %>" value="<%= GroupConstants.TYPE_COMMUNITY_OPEN %>" />
			<aui:option label="restricted" selected="<%= (type == GroupConstants.TYPE_COMMUNITY_RESTRICTED) %>" value="<%= GroupConstants.TYPE_COMMUNITY_RESTRICTED %>" />
			<aui:option label="private" selected="<%= (type == GroupConstants.TYPE_COMMUNITY_PRIVATE) %>" value="<%= GroupConstants.TYPE_COMMUNITY_PRIVATE %>" />
		</aui:select>

		<aui:input inlineLabel="left" name="active" value="<%= true %>" />

		<aui:input name="categories" type="assetCategories" />

		<aui:input name="tags" type="assetTags" />

		<%
		List<LayoutSetPrototype> layoutSetPrototypes = LayoutSetPrototypeServiceUtil.search(company.getCompanyId(), Boolean.TRUE, null);
		%>

		<c:if test="<%= (group != null) || !layoutSetPrototypes.isEmpty() %>">
			<c:choose>
				<c:when test="<%= ((group == null) || (group.getPublicLayoutsPageCount() == 0)) && !layoutSetPrototypes.isEmpty() %>">
					<aui:select label="public-pages" name="publicLayoutSetPrototypeId">
						<aui:option label="none" selected="<%= true %>" value="" />

						<%
						for (LayoutSetPrototype layoutSetPrototype : layoutSetPrototypes) {
						%>

							<aui:option value="<%= layoutSetPrototype.getLayoutSetPrototypeId() %>"><%= layoutSetPrototype.getName(user.getLanguageId()) %></aui:option>

						<%
						}
						%>

					</aui:select>
				</c:when>
				<c:otherwise>
					<aui:field-wrapper label="public-pages">
						<c:choose>
							<c:when test="<%= (group != null) && (group.getPublicLayoutsPageCount() > 0) %>">
								<liferay-portlet:actionURL var="publicPagesURL" portletName="<%= PortletKeys.MY_PLACES %>">
									<portlet:param name="struts_action" value="/my_places/view" />
									<portlet:param name="groupId" value="<%= String.valueOf(group.getGroupId()) %>" />
									<portlet:param name="privateLayout" value="<%= Boolean.FALSE.toString() %>" />
								</liferay-portlet:actionURL>

								<liferay-ui:icon
									image="view"
									label="<%= true %>"
									message="open-public-pages"
									method="get"
									target="_blank"
									url="<%= publicPagesURL.toString() %>"
								/>
							</c:when>
							<c:otherwise>
								<liferay-ui:message key="this-community-does-not-have-any-public-pages" />
							</c:otherwise>
						</c:choose>
					</aui:field-wrapper>
				</c:otherwise>
			</c:choose>

			<c:choose>
				<c:when test="<%= ((group == null) || (group.getPrivateLayoutsPageCount() == 0)) && !layoutSetPrototypes.isEmpty() %>">
					<aui:select label="private-pages" name="privateLayoutSetPrototypeId">
						<aui:option label="none" selected="<%= true %>" value="" />

						<%
						for (LayoutSetPrototype layoutSetPrototype : layoutSetPrototypes) {
						%>

							<aui:option value="<%= layoutSetPrototype.getLayoutSetPrototypeId() %>"><%= layoutSetPrototype.getName(user.getLanguageId()) %></aui:option>

						<%
						}
						%>

					</aui:select>
				</c:when>
				<c:otherwise>
					<aui:field-wrapper label="private-pages">
						<c:choose>
							<c:when test="<%= (group != null) && (group.getPrivateLayoutsPageCount() > 0) %>">
								<liferay-portlet:actionURL var="privatePagesURL" portletName="<%= PortletKeys.MY_PLACES %>">
									<portlet:param name="struts_action" value="/my_places/view" />
									<portlet:param name="groupId" value="<%= String.valueOf(group.getGroupId()) %>" />
									<portlet:param name="privateLayout" value="<%= Boolean.TRUE.toString() %>" />
								</liferay-portlet:actionURL>

								<liferay-ui:icon
									image="view"
									label="<%= true %>"
									message="open-private-pages"
									method="get"
									target="_blank"
									url="<%= privatePagesURL.toString() %>"
								/>
							</c:when>
							<c:otherwise>
								<liferay-ui:message key="this-community-does-not-have-any-private-pages" />
							</c:otherwise>
						</c:choose>
					</aui:field-wrapper>
				</c:otherwise>
			</c:choose>
		</c:if>
	</aui:fieldset>

	<aui:button-row>
		<aui:button type="submit" />

		<aui:button onClick="<%= redirect %>" type="cancel" />
	</aui:button-row>
</aui:form>

<aui:script>
	function <portlet:namespace />saveGroup() {
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= (group == null) ? Constants.ADD : Constants.UPDATE %>";
		submitForm(document.<portlet:namespace />fm);
	}

	<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />name);
	</c:if>
</aui:script>

<%
if (group != null) {
	PortalUtil.addPortletBreadcrumbEntry(request, HtmlUtil.escape(group.getDescriptiveName()), null);
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "edit"), currentURL);
}
else {
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "add-community"), currentURL);
}
%>