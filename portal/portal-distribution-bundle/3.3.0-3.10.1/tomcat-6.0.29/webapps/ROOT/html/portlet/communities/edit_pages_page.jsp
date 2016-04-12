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
Group group = (Group)request.getAttribute("edit_pages.jsp-group");
long groupId = ((Long)request.getAttribute("edit_pages.jsp-groupId")).longValue();
boolean privateLayout = ((Boolean)request.getAttribute("edit_pages.jsp-privateLayout")).booleanValue();
Layout selLayout = (Layout)request.getAttribute("edit_pages.jsp-selLayout");

String type = BeanParamUtil.getString(selLayout, request, "type");
String friendlyURL = BeanParamUtil.getString(selLayout, request, "friendlyURL");

String currentLanguageId = LanguageUtil.getLanguageId(request);
Locale currentLocale = LocaleUtil.fromLanguageId(currentLanguageId);
Locale defaultLocale = LocaleUtil.getDefault();
String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

Locale[] locales = LanguageUtil.getAvailableLocales();
%>

<liferay-ui:error exception="<%= ImageTypeException.class %>" message="please-enter-a-file-with-a-valid-file-type" />
<liferay-ui:error exception="<%= LayoutFriendlyURLException.class %>">

	<%
	LayoutFriendlyURLException lfurle = (LayoutFriendlyURLException)errorException;
	%>

	<c:if test="<%= lfurle.getType() == LayoutFriendlyURLException.ADJACENT_SLASHES %>">
		<liferay-ui:message key="please-enter-a-friendly-url-that-does-not-have-adjacent-slashes" />
	</c:if>

	<c:if test="<%= lfurle.getType() == LayoutFriendlyURLException.DOES_NOT_START_WITH_SLASH %>">
		<liferay-ui:message key="please-enter-a-friendly-url-that-begins-with-a-slash" />
	</c:if>

	<c:if test="<%= lfurle.getType() == LayoutFriendlyURLException.DUPLICATE %>">
		<liferay-ui:message key="please-enter-a-unique-friendly-url" />
	</c:if>

	<c:if test="<%= lfurle.getType() == LayoutFriendlyURLException.ENDS_WITH_SLASH %>">
		<liferay-ui:message key="please-enter-a-friendly-url-that-does-not-end-with-a-slash" />
	</c:if>

	<c:if test="<%= lfurle.getType() == LayoutFriendlyURLException.INVALID_CHARACTERS %>">
		<liferay-ui:message key="please-enter-a-friendly-url-with-valid-characters" />
	</c:if>

	<c:if test="<%= lfurle.getType() == LayoutFriendlyURLException.KEYWORD_CONFLICT %>">
		<%= LanguageUtil.format(pageContext, "please-enter-a-friendly-url-that-does-not-conflict-with-the-keyword-x", lfurle.getKeywordConflict()) %>
	</c:if>

	<c:if test="<%= lfurle.getType() == LayoutFriendlyURLException.POSSIBLE_DUPLICATE %>">
		<liferay-ui:message key="the-friendly-url-may-conflict-with-another-page" />
	</c:if>

	<c:if test="<%= lfurle.getType() == LayoutFriendlyURLException.TOO_DEEP %>">
		<liferay-ui:message key="the-friendly-url-has-too-many-slashes" />
	</c:if>

	<c:if test="<%= lfurle.getType() == LayoutFriendlyURLException.TOO_SHORT %>">
		<liferay-ui:message key="please-enter-a-friendly-url-that-is-at-least-two-characters-long" />
	</c:if>
</liferay-ui:error>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
	<td>
		<table class="lfr-table">

		<c:choose>
			<c:when test="<%= !group.isLayoutPrototype() %>">
				<tr>
					<td></td>
					<td>
						<liferay-ui:message key="default-language" />: <%= defaultLocale.getDisplayName(defaultLocale) %>
					</td>
					<td>
						<liferay-ui:message key="localized-language" />:

						<select id="<portlet:namespace />languageId" onChange="<portlet:namespace />updateLanguage();">
							<option value="" />

							<%
							for (int i = 0; i < locales.length; i++) {
								if (locales[i].equals(defaultLocale)) {
									continue;
								}

								String optionStyle = StringPool.BLANK;

								if (Validator.isNotNull(selLayout.getName(locales[i], false)) ||
									Validator.isNotNull(selLayout.getTitle(locales[i], false))) {

									optionStyle = "style=\"font-weight: bold;\"";
								}
							%>

								<option <%= (currentLanguageId.equals(LocaleUtil.toLanguageId(locales[i]))) ? "selected" : "" %> <%= optionStyle %> value="<%= LocaleUtil.toLanguageId(locales[i]) %>"><%= locales[i].getDisplayName(locale) %></option>

							<%
							}
							%>

						</select>
					</td>
				</tr>
				<tr>
					<td colspan="3">
						<br />
					</td>
				</tr>
				<tr>
					<td>
						<liferay-ui:message key="name" />
					</td>
					<td>
						<input id="<portlet:namespace />name_<%= defaultLanguageId %>" name="<portlet:namespace />name_<%= defaultLanguageId %>" size="30" type="text" value="<%= selLayout.getName(defaultLocale) %>" />
					</td>
					<td>

						<%
						for (int i = 0; i < locales.length; i++) {
							if (locales[i].equals(defaultLocale)) {
								continue;
							}
						%>

							<input id="<portlet:namespace />name_<%= LocaleUtil.toLanguageId(locales[i]) %>" name="<portlet:namespace />name_<%= LocaleUtil.toLanguageId(locales[i]) %>" type="hidden" value="<%= selLayout.getName(locales[i], false) %>" />

						<%
						}
						%>

						<input class="<%= currentLocale.equals(defaultLocale) ? "aui-helper-hidden" : "" %>" id="<portlet:namespace />name_temp" size="30" type="text" onChange="<portlet:namespace />onNameChanged();" />
					</td>
				</tr>
				<tr>
					<td>
						<liferay-ui:message key="html-title" />
					</td>
					<td>
						<input id="<portlet:namespace />title_<%= defaultLanguageId %>" name="<portlet:namespace />title_<%= defaultLanguageId %>" size="30" type="text" value="<%= selLayout.getTitle(defaultLocale) %>" />
					</td>
					<td>

						<%
						for (int i = 0; i < locales.length; i++) {
							if (locales[i].equals(defaultLocale)) {
								continue;
							}
						%>

							<input id="<portlet:namespace />title_<%= LocaleUtil.toLanguageId(locales[i]) %>" name="<portlet:namespace />title_<%= LocaleUtil.toLanguageId(locales[i]) %>" type="hidden" value="<%= selLayout.getTitle(locales[i], false) %>" />

						<%
						}
						%>

						<input class="<%= currentLocale.equals(defaultLocale) ? "aui-helper-hidden" : "" %>" id="<portlet:namespace />title_temp" size="30" type="text" onChange="<portlet:namespace />onTitleChanged();" />
					</td>
				</tr>
				<tr>
					<td colspan="3">
						<br />
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<input id="<portlet:namespace />name_<%= defaultLanguageId %>" name="<portlet:namespace />name_<%= defaultLanguageId %>" type="hidden" value="<%= selLayout.getName(defaultLocale) %>" />
			</c:otherwise>
		</c:choose>

		<tr>
			<td>
				<liferay-ui:message key="type" />
			</td>
			<td colspan="2">
				<select id="<portlet:namespace />type" name="<portlet:namespace />type">

					<%
					for (int i = 0; i < PropsValues.LAYOUT_TYPES.length; i++) {
					%>

						<option <%= type.equals(PropsValues.LAYOUT_TYPES[i]) ? "selected" : "" %> value="<%= PropsValues.LAYOUT_TYPES[i] %>"><%= LanguageUtil.get(pageContext, "layout.types." + PropsValues.LAYOUT_TYPES[i]) %></option>

					<%
					}
					%>

				</select>
			</td>
		</tr>

		<c:if test="<%= !group.isLayoutPrototype() %>">
			<tr>
				<td>
					<liferay-ui:message key="hidden" />
				</td>
				<td colspan="2">
					<liferay-ui:input-checkbox param="hidden" defaultValue="<%= selLayout.isHidden() %>" />
				</td>
			</tr>
		</c:if>

		<c:choose>
			<c:when test="<%= PortalUtil.isLayoutFriendliable(selLayout) && !group.isLayoutPrototype() %>">
				<tr>
					<td>
						<liferay-ui:message key="friendly-url" />
					</td>
					<td colspan="2" nowrap>

						<%
						StringBuilder friendlyURLBase = new StringBuilder();

						friendlyURLBase.append(themeDisplay.getPortalURL());

						String virtualHost = selLayout.getLayoutSet().getVirtualHost();

						if (Validator.isNull(virtualHost) || (friendlyURLBase.indexOf(virtualHost) == -1)) {
							friendlyURLBase.append(group.getPathFriendlyURL(privateLayout, themeDisplay));
							friendlyURLBase.append(group.getFriendlyURL());
						}
						%>

						<%= friendlyURLBase.toString() %>

						<input name="<portlet:namespace />friendlyURL" size="30" type="text" value="<%= HtmlUtil.escape(friendlyURL) %>" />
					</td>
				</tr>
				<tr>
					<td>
						<br />
					</td>
					<td colspan="3">
						<%= LanguageUtil.format(pageContext, "for-example-x", "<em>/news</em>") %>
					</td>
				</tr>
				<tr>
					<td>
						<liferay-ui:message key="query-string" />
					</td>
					<td colspan="3">

						<%
						String queryString = selLayout.getTypeSettingsProperties().getProperty("query-string");

						if (queryString == null) {
							queryString = StringPool.BLANK;
						}
						%>

						<input name="TypeSettingsProperties--query-string--" size="30" type="text" value="<%= HtmlUtil.escape(queryString) %>" />

						<liferay-ui:icon-help
							message="query-string-help"
						/>
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<input name="<portlet:namespace />friendlyURL" type="hidden" value="<%= HtmlUtil.escape(friendlyURL) %>" />
			</c:otherwise>
		</c:choose>

		<tr>
			<td colspan="3">
				<br />
			</td>
		</tr>
		<tr>
			<td>
				<liferay-ui:message key="icon" />
			</td>
			<td colspan="2">
				<liferay-theme:layout-icon layout="<%= selLayout %>" />

				<input name="<portlet:namespace />iconFileName" size="30" type="file" onChange="document.<portlet:namespace />fm.<portlet:namespace />iconImage.value = true; document.<portlet:namespace />fm.<portlet:namespace />iconImageCheckbox.checked = true;" />
			</td>
		</tr>
		<tr>
			<td>
				<liferay-ui:message key="use-icon" />
			</td>
			<td colspan="2">
				<liferay-ui:input-checkbox param="iconImage" defaultValue="<%= selLayout.isIconImage() %>" />
			</td>
		</tr>
		<tr>
			<td>
				<liferay-ui:message key="target" />
			</td>
			<td>
				<%
				String curTarget = (String) selLayout.getTypeSettingsProperties().getProperty("target");

				if (curTarget == null) {
					curTarget = StringPool.BLANK;
				}
				%>
				<input name="TypeSettingsProperties--target--" size="15" type="text" value="<%= curTarget %>" />
			</td>
		</tr>

		<liferay-ui:custom-attributes-available className="<%= Layout.class.getName() %>">
			<tr>
				<td colspan="2">
					<br />
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<liferay-ui:custom-attribute-list
						className="<%= Layout.class.getName() %>"
						classPK="<%= (selLayout != null) ? selLayout.getPlid() : 0 %>"
						editable="<%= true %>"
						label="<%= true %>"
					/>
				</td>
			</tr>
		</liferay-ui:custom-attributes-available>

		</table>
	</td>
</tr>
<tr>
	<td>
		<div class="separator"><!-- --></div>
	</td>
</tr>

<%
for (int i = 0; i < PropsValues.LAYOUT_TYPES.length; i++) {
	String curLayoutType = PropsValues.LAYOUT_TYPES[i];
%>

	<tr class="layout-type-form layout-type-form-<%= curLayoutType %> <%= type.equals(PropsValues.LAYOUT_TYPES[i]) ? "" : "aui-helper-hidden" %>">
		<td>

			<%
			request.setAttribute(WebKeys.SEL_LAYOUT, selLayout);
			%>

			<liferay-util:include page="<%= StrutsUtil.TEXT_HTML_DIR + PortalUtil.getLayoutEditPage(curLayoutType) %>" />
		</td>
	</tr>

<%
}
%>

</table>

<%@ include file="/html/portal/layout/edit/common.jspf" %>

<br />

<input type="submit" value="<liferay-ui:message key="save" />" />

<liferay-security:permissionsURL
	modelResource="<%= Layout.class.getName() %>"
	modelResourceDescription="<%= selLayout.getName(locale) %>"
	resourcePrimKey="<%= String.valueOf(selLayout.getPlid()) %>"
	var="permissionURL"
/>

<c:if test="<%= !group.isLayoutPrototype() %>">
	<input type="button" value="<liferay-ui:message key="permissions" />" onClick="location.href = '<%= permissionURL %>';" />

	<input type="button" value="<liferay-ui:message key="delete" />" onClick="<portlet:namespace />deletePage();" />
</c:if>

<aui:script>
	<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />name_<%= defaultLanguageId %>);
	</c:if>

	var nameChanged = false;
	var titleChanged = false;
	var lastLanguageId = "<%= currentLanguageId %>";

	function <portlet:namespace />onNameChanged() {
		nameChanged = true;
	}

	function <portlet:namespace />onTitleChanged() {
		titleChanged = true;
	}

	Liferay.provide(
		window,
		'<portlet:namespace />toggleLayoutTypeFields',
		function(type) {
			var A = AUI();

			var layoutTypeForms = A.all('.layout-type-form');
			var currentType = 'layout-type-form-' + type;

			layoutTypeForms.each(
				function(item, index, collection) {
					var action = 'hide';
					var disabled = true;

					if (item.hasClass(currentType)) {
						action = 'show';
						disabled = false;
					}

					item[action]();

					item.all('input, select, textarea').set('disabled', disabled);
				}
			);
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />updateLanguage',
		function(type) {
			var A = AUI();

			var nameNode = A.one('#<portlet:namespace />name_temp');
			var titleNode = A.one('#<portlet:namespace />title_temp');

			if (lastLanguageId != "<%= defaultLanguageId %>") {
				if (nameChanged) {
					var nameValue = (nameNode && nameNode.val()) || '';

					var lastLanguageNameNode = A.one('#<portlet:namespace />name_' + lastLanguageId);

					if (lastLanguageNameNode) {
						lastLanguageNameNode.val(nameValue);
					}

					nameChanged = false;
				}

				if (titleChanged) {
					var titleValue = (titleNode && titleNode.val()) || '';

					var lastLanguageTitleNode = A.one('#<portlet:namespace />title_' + lastLanguageId);

					if (lastLanguageTitleNode) {
						lastLanguageTitleNode.val(titleValue);
					}

					titleChanged = false;
				}
			}

			var selLanguageId = "";

			for (var i = 0; i < document.<portlet:namespace />fm.<portlet:namespace />languageId.length; i++) {
				if (document.<portlet:namespace />fm.<portlet:namespace />languageId.options[i].selected) {
					selLanguageId = document.<portlet:namespace />fm.<portlet:namespace />languageId.options[i].value;

					break;
				}
			}

			var action = 'hide';

			if (selLanguageId != "") {
				<portlet:namespace />updateLanguageTemps(selLanguageId);

				action = 'show';
			}

			if (nameNode) {
				nameNode[action]();
			}

			if (titleNode) {
				titleNode[action]();
			}

			lastLanguageId = selLanguageId;
		},
		['aui-base']
	);

	Liferay.provide(
		window,
		'<portlet:namespace />updateLanguageTemps',
		function(lang) {
			var A = AUI();

			if (lang != "<%= defaultLanguageId %>") {
				var nameNode = A.one('#<portlet:namespace />name_' + lang);
				var titleNode = A.one('#<portlet:namespace />title_' + lang);
				var defaultName = A.one('#<portlet:namespace />name_<%= defaultLanguageId %>');
				var defaultTitle = A.one('#<portlet:namespace />title_<%= defaultLanguageId %>');

				var nameValue = (nameNode && nameNode.val()) || '';
				var titleValue = (titleNode && titleNode.val()) || '';
				var defaultNameValue = (defaultName && defaultName.val()) || '';
				var defaultTitleValue = (defaultTitle && defaultTitle.val()) || '';

				var nameTempNode = A.one('#<portlet:namespace />name_temp');
				var titleTempNode = A.one('#<portlet:namespace />title_temp');

				if (nameTempNode) {
					nameTempNode.val(nameValue || defaultNameValue);
				}

				if (titleTempNode) {
					titleTempNode.val(titleValue || defaultTitleValue);
				}
			}
		},
		['aui-base']
	);
</aui:script>

<aui:script use="aui-base">
	<portlet:namespace />toggleLayoutTypeFields('<%= selLayout.getType() %>');

	<portlet:namespace />updateLanguageTemps(lastLanguageId);

	A.one("#<portlet:namespace />type").on(
		'change',
		function(event) {
			<portlet:namespace />toggleLayoutTypeFields(event.currentTarget.val());
		}
	);
</aui:script>