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

<%@ include file="/html/portlet/asset_tags_admin/init.jsp" %>

<form id="<portlet:namespace />fm">

<table class="tags-admin-container">
<tr>
	<td colspan="3">
		<div class="tags-admin-toolbar">
			<span class="tags-admin-search-bar">
				<input id="tags-admin-search-input" type="text" value="" />
			</span>

			<span class="tags-admin-actions">
				<c:if test="<%= AssetPermission.contains(permissionChecker, themeDisplay.getParentGroupId(), ActionKeys.ADD_TAG) %>">
					<input class="add-tag-button" id="add-tag-button" name="add-tag-button" type="button" value="<liferay-ui:message key="add-tag" />">
				</c:if>

				<c:if test="<%= GroupPermissionUtil.contains(permissionChecker, themeDisplay.getParentGroupId(), ActionKeys.PERMISSIONS) %>">
					<liferay-security:permissionsURL
						modelResource="com.liferay.portlet.asset"
						modelResourceDescription="<%= HtmlUtil.escape(themeDisplay.getParentGroupName()) %>"
						resourcePrimKey="<%= String.valueOf(themeDisplay.getParentGroupId()) %>"
						var="permissionsURL"
					/>

					<input type="button" value="<liferay-ui:message key="permissions" />" onClick="location.href = '<%= permissionsURL %>';" />
				</c:if>

				<div class="add-tag-layer-wrapper">
					<div class="add-tag-layer">
						<span class="aui-field">
							<span class="aui-field-content">
								<label class="tag-label" for="new-tag-name">
									<liferay-ui:message key="name" />
								</label>

								<input class="new-tag-name" id="new-tag-name" name="new-tag-name" type="text" value="" />
							</span>
						</span>

						<div class="tag-permissions-actions">
							<liferay-ui:input-permissions
								modelName="<%= AssetTag.class.getName() %>"
							/>
						</div>

						<div class="aui-button-holder">
							<input class="tag-save-button" type="button" value="<liferay-ui:message key="save" />" />

							<input class="close-panel" type="button" value="<liferay-ui:message key="close" />" />
						</div>
					</div>
				</div>
			</span>
		</div>
	</td>
</tr>
<tr class="tags-admin-content">
	<td class="tag-container">
		<div class="results-header">
			<liferay-ui:message key="tags" />
		</div>

		<div class="lfr-message-response" id="tag-messages" style="display: none;"></div>

		<div class="tags lfr-component"></div>
	</td>
	<td class="aui-helper-hidden tag-edit-container">
		<div class="results-header"><liferay-ui:message key="edit-tag" /></div>
		<div class="tag-edit">
			<div class="tag-close">
				<span>
					<liferay-ui:icon
						image="close"
					/>
				</span>
			</div>

			<div class="tag-label">
				<liferay-ui:message key="name" />:
			</div>

			<input class="tag-name" name="tag-name" type="text" />

			<br /><br />

			<div class="tag-properties">
				<liferay-ui:message key="properties" />:

				<liferay-ui:icon-help
					message="properties-are-a-way-to-add-more-detailed-information-to-a-specific-tag"
				/>

				<div class="aui-helper-hidden tag-property-row">
					<input class="property-key" type="text" />

					<input class="property-value" type="text" />

					<span class="add-property">
						<liferay-ui:icon
							image="add"
						/>
					</span>

					<span class="delete-property">
						<liferay-ui:icon
						image="delete"
					/>
					</span>
				</div>

				<br />

				<input class="tag-save-properties" type="button" value="<liferay-ui:message key="save" />" />

				<input class="tag-close" type="button" value="<liferay-ui:message key="close" />" />

				<input class="tag-delete-button" type="button" value="<liferay-ui:message key="delete" />" />

				<input class="tag-permissions-button" type="button" value="<liferay-ui:message key="edit-tag-permissions" />" />
			</div>
		</div>
	</td>
</tr>
</table>

</form>

<aui:script use="liferay-tags-admin">
	new Liferay.Portlet.AssetTagsAdmin('<%= portletDisplay.getId() %>');
</aui:script>