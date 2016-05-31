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

<%@ include file="/html/portlet/journal/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

JournalFeed feed = (JournalFeed)request.getAttribute(WebKeys.JOURNAL_FEED);

long groupId = BeanParamUtil.getLong(feed, request, "groupId", scopeGroupId);

String feedId = BeanParamUtil.getString(feed, request, "feedId");
String newFeedId = ParamUtil.getString(request, "newFeedId");
String type = BeanParamUtil.getString(feed, request, "type");

String structureId = BeanParamUtil.getString(feed, request, "structureId");

JournalStructure structure = null;

String structureName = StringPool.BLANK;

if (Validator.isNotNull(structureId)) {
	try {
		structure = JournalStructureLocalServiceUtil.getStructure(groupId, structureId);

		structureName = structure.getName();
	}
	catch (NoSuchStructureException nsse) {
	}
}

List<JournalTemplate> templates = new ArrayList<JournalTemplate>();

if (structure != null) {
	templates = JournalTemplateLocalServiceUtil.getStructureTemplates(groupId, structureId);
}

String templateId = BeanParamUtil.getString(feed, request, "templateId");

if ((structure == null) && Validator.isNotNull(templateId)) {
	JournalTemplate template = null;

	try {
		template = JournalTemplateLocalServiceUtil.getTemplate(groupId, templateId);

		structureId = template.getStructureId();

		structure = JournalStructureLocalServiceUtil.getStructure(groupId, structureId);

		structureName = structure.getName();

		templates = JournalTemplateLocalServiceUtil.getStructureTemplates(groupId, structureId);
	}
	catch (NoSuchTemplateException nste) {
	}
}

String rendererTemplateId = BeanParamUtil.getString(feed, request, "rendererTemplateId");

String contentField = BeanParamUtil.getString(feed, request, "contentField");

if (Validator.isNull(contentField) || ((structure == null) && !contentField.equals(JournalFeedConstants.WEB_CONTENT_DESCRIPTION) && !contentField.equals(JournalFeedConstants.RENDERED_WEB_CONTENT))) {
	contentField = JournalFeedConstants.WEB_CONTENT_DESCRIPTION;
}

String feedType = BeanParamUtil.getString(feed, request, "feedType", RSSUtil.DEFAULT_TYPE);
double feedVersion = BeanParamUtil.getDouble(feed, request, "feedVersion", RSSUtil.DEFAULT_VERSION);

String orderByCol = BeanParamUtil.getString(feed, request, "orderByCol");
String orderByType = BeanParamUtil.getString(feed, request, "orderByType");

ResourceURL feedURL = null;

if (feed != null) {
	long targetLayoutPlid = PortalUtil.getPlidFromFriendlyURL(feed.getCompanyId(), feed.getTargetLayoutFriendlyUrl());

	feedURL = new PortletURLImpl(request, PortletKeys.JOURNAL, targetLayoutPlid, PortletRequest.RESOURCE_PHASE);

	feedURL.setCacheability(ResourceURL.FULL);

	feedURL.setParameter("struts_action", "/journal/rss");
	feedURL.setParameter("groupId", String.valueOf(groupId));
	feedURL.setParameter("feedId", String.valueOf(feedId));
}
%>

<portlet:actionURL var="editFeedURL">
	<portlet:param name="struts_action" value="/journal/edit_feed" />
</portlet:actionURL>

<aui:form action="<%= editFeedURL %>" enctype="multipart/form-data" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveFeed();" %>' >
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (feed == null) ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="groupId" type="hidden" value="<%= groupId %>" />
	<aui:input name="feedId" type="hidden" value="<%= feedId %>" />
	<aui:input name="rendererTemplateId" type="hidden" value="<%= rendererTemplateId %>" />

	<liferay-ui:header
		backURL="<%= redirect %>"
		title='<%= (feed != null) ? feed.getName() : "new-feed" %>'
	/>

	<liferay-ui:error exception="<%= DuplicateFeedIdException.class %>" message="please-enter-a-unique-id" />
	<liferay-ui:error exception="<%= FeedContentFieldException.class %>" message="please-select-a-valid-feed-item-content" />
	<liferay-ui:error exception="<%= FeedDescriptionException.class %>" message="please-enter-a-valid-description" />
	<liferay-ui:error exception="<%= FeedIdException.class %>" message="please-enter-a-valid-id" />
	<liferay-ui:error exception="<%= FeedNameException.class %>" message="please-enter-a-valid-name" />
	<liferay-ui:error exception="<%= FeedTargetLayoutFriendlyUrlException.class %>" message="please-enter-a-valid-target-layout-friendly-url" />
	<liferay-ui:error exception="<%= FeedTargetPortletIdException.class %>" message="please-enter-a-valid-portlet-id" />

	<aui:model-context bean="<%= feed %>" model="<%= JournalFeed.class %>" />

	<aui:fieldset>
		<c:choose>
			<c:when test="<%= feed == null %>">
				<c:choose>
					<c:when test="<%= PropsValues.JOURNAL_FEED_FORCE_AUTOGENERATE_ID %>">
						<aui:input name="newFeedId" type="hidden" />
						<aui:input name="autoFeedId" type="hidden" value="<%= true %>" />
					</c:when>
					<c:otherwise>
						<aui:input cssClass="lfr-input-text-container" field="feedId" fieldParam="newFeedId" label="id" name="newFeedId" value="<%= newFeedId %>" />

						<aui:input label="autogenerate-id" name="autoFeedId" type="checkbox" />
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<aui:field-wrapper label="id">
					<%= feedId %>
				</aui:field-wrapper>
			</c:otherwise>
		</c:choose>

		<aui:input cssClass="lfr-input-text-container" name="name" />

		<aui:input cssClass="lfr-textarea-container" name="description" />

		<aui:input cssClass="lfr-input-text-container" helpMessage="journal-feed-target-layout-friendly-url-help" name="targetLayoutFriendlyUrl" />

		<aui:input cssClass="lfr-input-text-container" helpMessage="journal-feed-target-portlet-id-help" name="targetPortletId" />

		<c:choose>
			<c:when test="<%= feed == null %>">
				<aui:field-wrapper label="permissions">
					<liferay-ui:input-permissions modelName="<%= JournalFeed.class.getName() %>" />
				</aui:field-wrapper>
			</c:when>
			<c:otherwise>
				<aui:field-wrapper label="url">
					<liferay-ui:input-resource url="<%= feedURL.toString() %>" />
				</aui:field-wrapper>
			</c:otherwise>
		</c:choose>
	</aui:fieldset>

	<liferay-ui:panel-container extended="<%= true %>" id="journalFeedSettingsPanelContainer" persistState="<%= true %>">
		<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" id="journalFeedConstraintsPanel" persistState="<%= true %>" title='<%= LanguageUtil.get(pageContext, "web-content-contraints") %>' >
			<aui:fieldset>
				<aui:select label="web-content-type" name="type" showEmptyOption="<%= true %>">

					<%
					for (String curType : JournalArticleConstants.TYPES) {
					%>

						<aui:option label="<%= curType %>" selected="<%= type.equals(curType) %>" />

					<%
					}
					%>

				</aui:select>

				<aui:field-wrapper label="structure">
					<aui:input name="structureId" type="hidden" value="<%= structureId %>" />

					<portlet:renderURL var="structureURL">
						<portlet:param name="struts_action" value="/journal/edit_structure" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
						<portlet:param name="groupId" value="<%= String.valueOf(groupId) %>" />
						<portlet:param name="parentStructureId" value="<%= structureId %>" />
					</portlet:renderURL>

					<aui:a href="<%= structureURL %>" label="<%= structureName %>" id="structureName" />

					<aui:button name="selectStructureButton" onClick='<%= renderResponse.getNamespace() + "openStructureSelector();" %>' value="select" />

					<aui:button disabled="<%= Validator.isNull(structureId) %>" name="removeStructureButton" onClick='<%= renderResponse.getNamespace() + "removeStructure();" %>' value="remove" />
				</aui:field-wrapper>

				<aui:field-wrapper label="template">
					<c:choose>
						<c:when test="<%= templates.isEmpty() %>">
							<aui:input name="templateId" type="hidden" value="<%= templateId %>" />

							<aui:button name="selectTemplateButton" onClick='<%= renderResponse.getNamespace() + "openTemplateSelector();" %>' value="select" />
						</c:when>
						<c:otherwise>
							<liferay-ui:table-iterator
								list="<%= templates %>"
								listType="com.liferay.portlet.journal.model.JournalTemplate"
								rowLength="3"
								rowPadding="30"
							>

								<%
								boolean templateChecked = false;

								if (templateId.equals(tableIteratorObj.getTemplateId())) {
									templateChecked = true;
								}
								%>

								<aui:input checked="<%= templateChecked %>" name="templateId" type="radio" value="<%= tableIteratorObj.getTemplateId() %>" />

								<portlet:renderURL var="templateURL">
									<portlet:param name="struts_action" value="/journal/edit_template" />
									<portlet:param name="redirect" value="<%= currentURL %>" />
									<portlet:param name="groupId" value="<%= String.valueOf(tableIteratorObj.getGroupId()) %>" />
									<portlet:param name="templateId" value="<%= tableIteratorObj.getTemplateId() %>" />
								</portlet:renderURL>

								<aui:a href="<%= templateURL %>"><%= tableIteratorObj.getName() %></aui:a>

								<c:if test="<%= tableIteratorObj.isSmallImage() %>">
									<br />

									<img border="0" hspace="0" src="<%= Validator.isNotNull(tableIteratorObj.getSmallImageURL()) ? tableIteratorObj.getSmallImageURL() : themeDisplay.getPathImage() + "/journal/template?img_id=" + tableIteratorObj.getSmallImageId() + "&t=" + ImageServletTokenUtil.getToken(tableIteratorObj.getSmallImageId()) %>" vspace="0" />
								</c:if>
							</liferay-ui:table-iterator>
						</c:otherwise>
					</c:choose>
				</aui:field-wrapper>
			</aui:fieldset>
		</liferay-ui:panel>

		<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" id="journalPresentationSettingsPanel" persistState="<%= true %>" title='<%= LanguageUtil.get(pageContext, "presentation-settings") %>' >
			<aui:fieldset>
				<aui:select label="feed-item-content" name="contentField">

					<%
					String taglibSelectRendererTemplateOption = renderResponse.getNamespace() + "selectRendererTemplate('');";
					%>

					<aui:option label="<%= JournalFeedConstants.RENDERED_WEB_CONTENT %>" onClick="<%= taglibSelectRendererTemplateOption %>" selected="<%= contentField.equals(JournalFeedConstants.WEB_CONTENT_DESCRIPTION) %>" />
					<optgroup label='<liferay-ui:message key="<%= JournalFeedConstants.RENDERED_WEB_CONTENT %>" />'>
						<aui:option label="use-default-template" onClick="<%= taglibSelectRendererTemplateOption %>" selected="<%= contentField.equals(JournalFeedConstants.RENDERED_WEB_CONTENT) %>" value="<%= JournalFeedConstants.RENDERED_WEB_CONTENT %>" />

						<c:if test="<%= (structure != null) && (templates.size() > 1) %>">

							<%
							for (JournalTemplate currTemplate : templates) {
								taglibSelectRendererTemplateOption = renderResponse.getNamespace() + "selectRendererTemplate('" + currTemplate.getTemplateId() + "');";
							%>

								<aui:option label='<%= LanguageUtil.format(pageContext, "use-template-x", currTemplate.getName()) %>' onClick="<%= taglibSelectRendererTemplateOption %>" selected="<%= rendererTemplateId.equals(currTemplate.getTemplateId()) %>" value="<%= JournalFeedConstants.RENDERED_WEB_CONTENT %>" />

							<%
							}
							%>

						</c:if>
					</optgroup>

					<c:if test="<%= structure != null %>">
						<optgroup label="<liferay-ui:message key="structure-fields" />">

							<%
							Document doc = SAXReaderUtil.read(structure.getXsd());

							XPath xpathSelector = SAXReaderUtil.createXPath("//dynamic-element");

							List<Node> nodes = xpathSelector.selectNodes(doc);

							for (Node node : nodes) {
								Element el = (Element)node;

								String elName = el.attributeValue("name");
								String elType = StringUtil.replace(el.attributeValue("type"), StringPool.UNDERLINE, StringPool.DASH);

								if (!elType.equals("boolean") && !elType.equals("list") && !elType.equals("multi-list")) {
									taglibSelectRendererTemplateOption = renderResponse.getNamespace() + "selectRendererTemplate('');";
							%>

									<aui:option label='<%= TextFormatter.format(elName, TextFormatter.J) + "(" + LanguageUtil.get(pageContext, elType) + ")" %>' onClick="<%= taglibSelectRendererTemplateOption %>" selected="<%= contentField.equals(elName) %>" value="<%= elName %>" />

							<%
								}
							}
							%>

						</optgroup>
					</c:if>
				</aui:select>

				<aui:select label="feed-type" name="feedTypeAndVersion">

					<%
					StringBundler sb = new StringBundler();

					for (int i = 4; i < RSSUtil.RSS_VERSIONS.length; i++) {
						sb.append("<option ");

						if (feedType.equals(RSSUtil.RSS) && (feedVersion == RSSUtil.RSS_VERSIONS[i])) {
							sb.append("selected ");
						}

						sb.append("value=\"");
						sb.append(RSSUtil.RSS);
						sb.append(":");
						sb.append(RSSUtil.RSS_VERSIONS[i]);
						sb.append("\">");
						sb.append(LanguageUtil.get(pageContext, RSSUtil.RSS));
						sb.append(" ");
						sb.append(RSSUtil.RSS_VERSIONS[i]);
						sb.append("</option>");
					}

					for (int i = 1; i < RSSUtil.ATOM_VERSIONS.length; i++) {
						sb.append("<option ");

						if (feedType.equals(RSSUtil.ATOM) && (feedVersion == RSSUtil.ATOM_VERSIONS[i])) {
							sb.append("selected ");
						}

						sb.append("value=\"");
						sb.append(RSSUtil.ATOM);
						sb.append(":");
						sb.append(RSSUtil.ATOM_VERSIONS[i]);
						sb.append("\">");
						sb.append(LanguageUtil.get(pageContext, RSSUtil.ATOM));
						sb.append(" ");
						sb.append(RSSUtil.ATOM_VERSIONS[i]);
						sb.append("</option>");
					}
					%>

					<%= sb.toString() %>
				</aui:select>

				<aui:input label="maximum-items-to-display" name="delta" value="10" />

				<aui:select label="order-by-column" name="orderByCol">
					<aui:option label="modified-date" selected='<%=orderByCol.equals("modified-date") %>' />
					<aui:option label="display-date" selected='<%= orderByCol.equals("display-date") %>' />
				</aui:select>

				<aui:select name="orderByType">
					<aui:option label="ascending" selected='<%= orderByType.equals("asc") %>' value="asc" />
					<aui:option label="descending" selected='<%= orderByType.equals("desc") %>' value="desc" />
				</aui:select>
			</aui:fieldset>
		</liferay-ui:panel>
	</liferay-ui:panel-container>

	<aui:button-row>
		<aui:button type="submit" />

		<c:if test="<%= feed != null %>">

			<%
			String taglibPreviewButton = "window.open('" + feedURL + "', 'feed');";
			%>

			<aui:button type="button" value="preview" onClick="<%= taglibPreviewButton %>" />
		</c:if>

		<aui:button onClick="<%= redirect %>" type="cancel" />
	</aui:button-row>
</aui:form>

<aui:script>
	function <portlet:namespace />openStructureSelector() {
		if (confirm('<%= UnicodeLanguageUtil.get(pageContext, "selecting-a-new-structure-will-change-the-available-templates-and-available-feed-item-content") %>')) {
			var structureWindow = window.open('<portlet:renderURL windowState="<%= LiferayWindowState.POP_UP.toString() %>"><portlet:param name="struts_action" value="/journal/select_structure" /><portlet:param name="groupId" value="<%= String.valueOf(groupId) %>" /></portlet:renderURL>', 'structure', 'directories=no,height=640,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=680');
			void('');
			structureWindow.focus();
		}
	}

	function <portlet:namespace />openTemplateSelector() {
		if (confirm('<%= UnicodeLanguageUtil.get(pageContext, "selecting-a-template-will-change-the-structure,-available-templates,-and-available-feed-item-content") %>')) {
			var templateWindow = window.open('<portlet:renderURL windowState="<%= LiferayWindowState.POP_UP.toString() %>"><portlet:param name="struts_action" value="/journal/select_template" /><portlet:param name="groupId" value="<%= String.valueOf(groupId) %>" /></portlet:renderURL>', 'template', 'directories=no,height=640,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=680');
			void('');
			templateWindow.focus();
		}
	}

	function <portlet:namespace />removeStructure() {
		document.<portlet:namespace />fm.<portlet:namespace />structureId.value = "";
		document.<portlet:namespace />fm.<portlet:namespace />templateId.value = "";
		document.<portlet:namespace />fm.<portlet:namespace />rendererTemplateId.value = "";
		document.<portlet:namespace />fm.<portlet:namespace />contentField.value = "<%= JournalFeedConstants.WEB_CONTENT_DESCRIPTION %>";
		submitForm(document.<portlet:namespace />fm);
	}

	function <portlet:namespace />saveFeed() {
		document.<portlet:namespace />fm.<portlet:namespace /><%= Constants.CMD %>.value = "<%= feed == null ? Constants.ADD : Constants.UPDATE %>";

		<c:if test="<%= feed == null %>">
			document.<portlet:namespace />fm.<portlet:namespace />feedId.value = document.<portlet:namespace />fm.<portlet:namespace />newFeedId.value;
		</c:if>

		submitForm(document.<portlet:namespace />fm);
	}

	function <portlet:namespace />selectRendererTemplate(rendererTemplateId) {
		document.<portlet:namespace />fm.<portlet:namespace />rendererTemplateId.value = rendererTemplateId;
	}

	function <portlet:namespace />selectStructure(structureId) {
		if (document.<portlet:namespace />fm.<portlet:namespace />structureId.value != structureId) {
			document.<portlet:namespace />fm.<portlet:namespace />structureId.value = structureId;
			document.<portlet:namespace />fm.<portlet:namespace />templateId.value = "";
			document.<portlet:namespace />fm.<portlet:namespace />rendererTemplateId.value = "";
			document.<portlet:namespace />fm.<portlet:namespace />contentField.value = "<%= JournalFeedConstants.WEB_CONTENT_DESCRIPTION %>";
			<portlet:namespace />saveFeed();
		}
	}

	function <portlet:namespace />selectTemplate(structureId, templateId) {
		document.<portlet:namespace />fm.<portlet:namespace />structureId.value = structureId;
		document.<portlet:namespace />fm.<portlet:namespace />templateId.value = templateId;
		<portlet:namespace />saveFeed();
	}

	Liferay.Util.disableToggleBoxes('<portlet:namespace />autoFeedIdCheckbox','<portlet:namespace />newFeedId', true);

	<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
		<c:choose>
			<c:when test="<%= PropsValues.JOURNAL_FEED_FORCE_AUTOGENERATE_ID %>">
				Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace />name);
			</c:when>
			<c:otherwise>
				Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace /><%= (feed == null) ? "newFeedId" : "name" %>);
			</c:otherwise>
		</c:choose>
	</c:if>
</aui:script>