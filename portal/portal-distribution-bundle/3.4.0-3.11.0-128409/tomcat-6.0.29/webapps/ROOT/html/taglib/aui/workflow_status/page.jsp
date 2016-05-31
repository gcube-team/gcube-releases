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

<%@ page import="com.liferay.portal.NoSuchWorkflowInstanceLinkException" %>

<%
Object bean = request.getAttribute("aui:workflow-status:bean");
String id = GetterUtil.getString((String)request.getAttribute("aui:workflow-status:id"));
Class<?> model = (Class<?>)request.getAttribute("aui:workflow-status:model");
int status = GetterUtil.getInteger((String)request.getAttribute("aui:workflow-status:status"));
double version = GetterUtil.getDouble((String)request.getAttribute("aui:workflow-status:version"));
%>

<div class="taglib-workflow-status">
	<c:if test="<%= Validator.isNotNull(id) %>">
		<span class="workflow-id"><liferay-ui:message key="id" />: <%= HtmlUtil.escape(id) %></span>
	</c:if>

	<c:if test="<%= version > 0 %>">
		<span class="workflow-version"><liferay-ui:message key="version" />: <strong><%= version %></strong></span>
	</c:if>

	<%
	String statusMessage = StringPool.BLANK;
	String additionalText = StringPool.BLANK;

	if (status == WorkflowConstants.STATUS_APPROVED) {
		statusMessage = "approved";
	}
	else if (status == WorkflowConstants.STATUS_DRAFT) {
		statusMessage = "draft";
	}
	else if (status == WorkflowConstants.STATUS_EXPIRED) {
		statusMessage = "expired";
	}
	else if (status == WorkflowConstants.STATUS_PENDING) {
		statusMessage = "pending";

		long companyId = BeanPropertiesUtil.getLong(bean, "companyId");
		long groupId = BeanPropertiesUtil.getLong(bean, "groupId");
		long classPK = BeanPropertiesUtil.getLong(bean, "primaryKey");

		StringBundler sb = new StringBundler(4);

		try {
			String workflowStatus = WorkflowInstanceLinkLocalServiceUtil.getState(companyId, groupId, model.getName(), classPK);

			sb.append(StringPool.SPACE);
			sb.append(StringPool.OPEN_PARENTHESIS);
			sb.append(LanguageUtil.get(pageContext, workflowStatus));
			sb.append(StringPool.CLOSE_PARENTHESIS);

			additionalText = sb.toString();
		}
		catch (NoSuchWorkflowInstanceLinkException nswile) {
		}
	}
	%>

	<span class="workflow-status"><liferay-ui:message key="status" />: <strong class="workflow-status-<%= statusMessage %>"><liferay-ui:message key="<%= statusMessage %>" /><%= additionalText %></strong></span>

	<c:if test="<%= (status == WorkflowConstants.STATUS_APPROVED) && (version > 0) %>">
		<liferay-ui:icon-help message="a-new-version-will-be-created-automatically-if-this-content-is-modified" />
	</c:if>
</div>