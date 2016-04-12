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

<%@ include file="/html/portlet/enterprise_admin/init.jsp" %>

<%
User selUser = PortalUtil.getSelectedUser(request);
%>

<c:choose>
	<c:when test='<%= SessionMessages.contains(renderRequest, "request_processed") %>'>
		<aui:script>
			window.close();
			opener.<portlet:namespace />changePortrait('<%= themeDisplay.getPathImage() %>/user_<%= selUser.isFemale() ? "female" : "male" %>_portrait?img_id=<%= selUser.getPortraitId() %>&t=<%= ImageServletTokenUtil.getToken(selUser.getPortraitId()) %>');
		</aui:script>
	</c:when>
	<c:otherwise>
		<portlet:actionURL var="editUserPortraitURL">
			<portlet:param name="struts_action" value="/enterprise_admin/edit_user_portrait" />
		</portlet:actionURL>

		<aui:form action="<%= editUserPortraitURL %>" enctype="multipart/form-data" method="post" name="fm">
			<aui:input name="p_u_i_d" type="hidden" value="<%= selUser.getUserId() %>" />

			<liferay-ui:error exception="<%= UploadException.class %>" message="an-unexpected-error-occurred-while-uploading-your-file" />
			<liferay-ui:error exception="<%= UserPortraitSizeException.class %>" message="please-enter-a-file-with-a-valid-file-size" />
			<liferay-ui:error exception="<%= UserPortraitTypeException.class %>" message="please-enter-a-file-with-a-valid-file-type" />

			<aui:fieldset>
				<aui:input label='<%= LanguageUtil.format(pageContext, "upload-a-gif-or-jpeg-that-is-x-pixels-tall-and-x-pixels-wide", new Object[] {"120", "100"}, false) %>' name="fileName" size="50" type="file" />

				<aui:button-row>
					<aui:button type="submit" />

					<aui:button onClick="window.close();" type="cancel" value="close" />
				</aui:button-row>
			</aui:fieldset>
		</aui:form>

		<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
			<aui:script>
				Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />fileName);
			</aui:script>
		</c:if>
	</c:otherwise>
</c:choose>