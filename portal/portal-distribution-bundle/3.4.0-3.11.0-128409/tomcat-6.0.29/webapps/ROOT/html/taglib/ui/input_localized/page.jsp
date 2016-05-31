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

<%
String randomNamespace = PortalUtil.generateRandomKey(request, "taglib_ui_input_localized_page");

String cssClass = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-localized:cssClass"));
boolean disabled = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:input-localized:disabled"));
Map<String, Object> dynamicAttributes = (Map<String, Object>)request.getAttribute("liferay-ui:input-localized:dynamicAttributes");
String name = (String)request.getAttribute("liferay-ui:input-localized:name");
String xml = (String)request.getAttribute("liferay-ui:input-localized:xml");
String type = (String)request.getAttribute("liferay-ui:input-localized:type");

Locale defaultLocale = LocaleUtil.getDefault();
String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);
Locale[] locales = LanguageUtil.getAvailableLocales();

String defaultLanguageValue = ParamUtil.getString(request, name + StringPool.UNDERLINE + defaultLanguageId, LocalizationUtil.getLocalization(xml, defaultLanguageId));
%>

<span class="taglib-input-localized">
	<c:choose>
		<c:when test='<%= type.equals("input") %>'>
			<input class="language-value <%= cssClass %>" <%= disabled ? "disabled=\"disabled\"" : "" %> id="<portlet:namespace /><%= name + StringPool.UNDERLINE + defaultLanguageId %>" name="<portlet:namespace /><%= name + StringPool.UNDERLINE + defaultLanguageId %>" type="text" value="<%= HtmlUtil.escape(defaultLanguageValue) %>" <%= _buildDynamicAttributes(dynamicAttributes) %> />
		</c:when>
		<c:when test='<%= type.equals("textarea") %>'>
			<textarea class="language-value <%= cssClass %>" <%= disabled ? "disabled=\"disabled\"" : "" %> id="<portlet:namespace /><%= name + StringPool.UNDERLINE + defaultLanguageId %>" name="<portlet:namespace /><%= name + StringPool.UNDERLINE + defaultLanguageId %>" <%= _buildDynamicAttributes(dynamicAttributes) %>><%= HtmlUtil.escape(defaultLanguageValue) %></textarea>
		</c:when>
	</c:choose>

	<img alt="<%= defaultLocale.getDisplayName() %>" class="default-language" src="<%= themeDisplay.getPathThemeImages() %>/language/<%= defaultLanguageId %>.png" />

	<%
	List<String> languageIds = new ArrayList<String>();

	if (Validator.isNotNull(xml)) {
		for (int i = 0; i < locales.length; i++) {
			if (locales[i].equals(defaultLocale)) {
				continue;
			}

			String languageId = LocaleUtil.toLanguageId(locales[i]);
			String languageValue = LocalizationUtil.getLocalization(xml, languageId, false);

			if (Validator.isNotNull(languageValue) || (request.getParameter(name + StringPool.UNDERLINE + languageId) != null)) {
				languageIds.add(languageId);
			}
		}
	}
	%>

	<a class="lfr-floating-trigger" href="javascript:;" id="<%= randomNamespace %>languageSelectorTrigger">
		<liferay-ui:message key="other-languages" /> (<%= languageIds.size() %>)
	</a>

	<%
	if (languageIds.isEmpty()) {
		languageIds.add(StringPool.BLANK);
	}
	%>

	<div class="lfr-floating-container lfr-language-selector aui-helper-hidden" id="<%= randomNamespace %>languageSelector">
		<div class="lfr-panel aui-form">
			<div class="lfr-panel-titlebar">
				<h3 class="lfr-panel-title"><span><liferay-ui:message key="other-languages" /></span></h3>
			</div>

			<div class="lfr-panel-content">

				<%
				for (int i = 0; i < languageIds.size(); i++) {
					String curLanguageId = languageIds.get(i);
				%>

					<div class="lfr-form-row">
						<div class="row-names">
							<img alt="<%= Validator.isNotNull(curLanguageId) ? LocaleUtil.fromLanguageId(curLanguageId).getDisplayName() : StringPool.BLANK %>" class="language-flag" src="<%= themeDisplay.getPathThemeImages() %>/language/<%= Validator.isNotNull(curLanguageId) ? curLanguageId : "../spacer" %>.png" />

							<select <%= disabled ? "disabled=\"disabled\"" : "" %> id="<portlet:namespace />languageId<%= i %>">
								<option value="" />

								<%
								for (Locale curLocale : locales) {
									if (curLocale.equals(defaultLocale)) {
										continue;
									}

									String optionStyle = StringPool.BLANK;

									String languageId = LocaleUtil.toLanguageId(curLocale);
									String languageValue = LocalizationUtil.getLocalization(xml, languageId, false);

									if ((Validator.isNotNull(xml)) && Validator.isNotNull(languageValue)) {
										optionStyle = "style=\"font-weight: bold\"";
									}
								%>

									<option <%= (curLanguageId.equals(languageId)) ? "selected" : "" %> <%= optionStyle %> value="<%= languageId %>"><%= curLocale.getDisplayName(locale) %></option>

								<%
								}
								%>

							</select>

							<%
							String languageValue = ParamUtil.getString(request, name + StringPool.UNDERLINE + curLanguageId);

							if (Validator.isNotNull(xml) && Validator.isNull(languageValue)) {
								languageValue = LocalizationUtil.getLocalization(xml, curLanguageId, false);
							}
							%>

							<c:choose>
								<c:when test='<%= type.equals("input") %>'>
									<input class="language-value" <%= disabled ? "disabled=\"disabled\"" : "" %> name="<portlet:namespace /><%= name + StringPool.UNDERLINE + curLanguageId %>" type="text" value="<%= HtmlUtil.escape(languageValue) %>" />
								</c:when>
								<c:when test='<%= type.equals("textarea") %>'>
									<textarea class="language-value" <%= disabled ? "disabled=\"disabled\"" : "" %> name="<portlet:namespace /><%= name + StringPool.UNDERLINE + curLanguageId %>"><%= HtmlUtil.escape(languageValue) %></textarea>
								</c:when>
							</c:choose>
						</div>
					</div>

				<%
				}
				%>

			</div>
		</div>
	</div>
</span>

<aui:script use="liferay-auto-fields,liferay-panel-floating">
	var updateLanguageFlag = function(event) {
		var target = event.target;

		var selectedValue = target.val();

		var newName = '<portlet:namespace /><%= name %>_';

		var currentRow = target.ancestor('.lfr-form-row');

		var img = currentRow.all('img.language-flag');
		var imgSrc = 'spacer';

		if (selectedValue) {
			newName ='<portlet:namespace /><%= name %>_' + selectedValue;

			imgSrc = 'language/' + selectedValue;
		}

		var inputField = currentRow.one('.language-value');

		if (inputField) {
			inputField.attr('name', newName);
			inputField.attr('id', newName);
		}

		if (img) {
			img.attr('src', '<%= themeDisplay.getPathThemeImages() %>/' + imgSrc + '.png');
		}
	};

	<c:if test="<%= !disabled %>">
		new Liferay.AutoFields(
			{
				contentBox: '#<%= randomNamespace %>languageSelector .lfr-panel-content',
				on: {
					'clone': function(event) {
						var instance = this;

						var row = event.row;

						var select = row.one('select');
						var img = row.one('img.language-flag');

						if (select) {
							select.on('change', updateLanguageFlag);
						}

						if (img) {
							img.attr('src', '<%= themeDisplay.getPathThemeImages() %>/spacer.png');
						}
					}
				}
			}
		).render();
	</c:if>

	var panel = new Liferay.PanelFloating(
		{
			collapsible: false,
			container: '#<%= randomNamespace %>languageSelector',
			on: {
				hide: function(event) {
					var instance = this;

					instance._positionHelper.appendTo(document.<portlet:namespace />fm);
				},
				show: function(event) {
					var instance = this;

					instance._positionHelper.appendTo(document.body);
				}
			},
			trigger: '#<%= randomNamespace %>languageSelectorTrigger',
			width: 500
		}
	);

	panel._positionHelper.appendTo(document.<portlet:namespace />fm);

	A.all('#<%= randomNamespace %>languageSelector select').each(
		function(item) {
			if (item) {
				item.on('change', updateLanguageFlag);
			}
		}
	);
</aui:script>