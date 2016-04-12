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

<%@ page import="com.liferay.portlet.messageboards.model.MBCategory" %>
<%@ page import="com.liferay.portlet.messageboards.model.MBDiscussion" %>
<%@ page import="com.liferay.portlet.messageboards.model.MBMessage" %>
<%@ page import="com.liferay.portlet.messageboards.model.MBMessageDisplay" %>
<%@ page import="com.liferay.portlet.messageboards.model.MBThread" %>
<%@ page import="com.liferay.portlet.messageboards.model.MBThreadConstants" %>
<%@ page import="com.liferay.portlet.messageboards.model.MBTreeWalker" %>
<%@ page import="com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.messageboards.service.permission.MBDiscussionPermission" %>
<%@ page import="com.liferay.portlet.messageboards.util.BBCodeUtil" %>
<%@ page import="com.liferay.portlet.messageboards.util.comparator.MessageCreateDateComparator" %>
<%@ page import="com.liferay.portlet.ratings.model.RatingsEntry" %>
<%@ page import="com.liferay.portlet.ratings.model.RatingsStats" %>
<%@ page import="com.liferay.portlet.ratings.service.RatingsEntryLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.ratings.service.RatingsStatsLocalServiceUtil" %>
<%@ page import="com.liferay.portlet.ratings.service.persistence.RatingsEntryUtil" %>
<%@ page import="com.liferay.portlet.ratings.service.persistence.RatingsStatsUtil" %>

<portlet:defineObjects />

<%
String randomNamespace = PortalUtil.generateRandomKey(request, "taglib_ui_discussion_page") + StringPool.UNDERLINE;

String className = (String)request.getAttribute("liferay-ui:discussion:className");
long classPK = GetterUtil.getLong((String)request.getAttribute("liferay-ui:discussion:classPK"));
String formAction = (String)request.getAttribute("liferay-ui:discussion:formAction");
String formName = namespace + request.getAttribute("liferay-ui:discussion:formName");
String permissionClassName = (String)request.getAttribute("liferay-ui:discussion:permissionClassName");
long permissionClassPK = GetterUtil.getLong((String)request.getAttribute("liferay-ui:discussion:permissionClassPK"));
boolean ratingsEnabled = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:discussion:ratingsEnabled"));
String redirect = (String)request.getAttribute("liferay-ui:discussion:redirect");
long userId = GetterUtil.getLong((String)request.getAttribute("liferay-ui:discussion:userId"));

String threadView = PropsValues.DISCUSSION_THREAD_VIEW;

MBMessageDisplay messageDisplay = MBMessageLocalServiceUtil.getDiscussionMessageDisplay(userId, scopeGroupId, className, classPK, WorkflowConstants.STATUS_ANY, threadView);

MBCategory category = messageDisplay.getCategory();
MBThread thread = messageDisplay.getThread();
MBTreeWalker treeWalker = messageDisplay.getTreeWalker();
MBMessage rootMessage = null;
List<MBMessage> messages = null;
int messagesCount = 0;

if (treeWalker != null) {
	rootMessage = treeWalker.getRoot();
	messages = treeWalker.getMessages();
	messagesCount = messages.size();
}
else {
	rootMessage = MBMessageLocalServiceUtil.getMessage(thread.getRootMessageId());
	messagesCount = MBMessageLocalServiceUtil.getThreadMessagesCount(rootMessage.getThreadId(), WorkflowConstants.STATUS_ANY);
}

Format dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
%>

<c:if test="<%= (messagesCount > 1) || MBDiscussionPermission.contains(permissionChecker, company.getCompanyId(), scopeGroupId, permissionClassName, permissionClassPK, ActionKeys.VIEW) %>">
	<div class="taglib-discussion">
		<form action="<%= HtmlUtil.escape(formAction) %>" method="post" name="<%= formName %>">
		<input name="<%= namespace %><%= Constants.CMD %>" type="hidden" value="" />
		<input name="<%= namespace %>redirect" type="hidden" value="<%= HtmlUtil.escapeAttribute(redirect) %>" />
		<input name="<%= namespace %>className" type="hidden" value="<%= className %>" />
		<input name="<%= namespace %>classPK" type="hidden" value="<%= classPK %>" />
		<input name="<%= namespace %>permissionClassName" type="hidden" value="<%= permissionClassName %>" />
		<input name="<%= namespace %>permissionClassPK" type="hidden" value="<%= permissionClassPK %>" />
		<input name="<%= namespace %>messageId" type="hidden" value="" />
		<input name="<%= namespace %>threadId" type="hidden" value="<%= thread.getThreadId() %>" />
		<input name="<%= namespace %>parentMessageId" type="hidden" value="" />
		<input name="<%= namespace %>body" type="hidden" value="" />

		<%
		int i = 0;

		MBMessage message = rootMessage;
		%>

		<c:if test="<%= MBDiscussionPermission.contains(permissionChecker, company.getCompanyId(), scopeGroupId, permissionClassName, permissionClassPK, ActionKeys.ADD_DISCUSSION) %>">
			<table class="add-comment" id="<%= randomNamespace %>messageScroll0">
			<tr>
				<td id="<%= randomNamespace %>messageScroll<%= message.getMessageId() %>">
					<input name="<%= namespace %>messageId<%= i %>" type="hidden" value="<%= message.getMessageId() %>" />
					<input name="<%= namespace %>parentMessageId<%= i %>" type="hidden" value="<%= message.getMessageId() %>" />
				</td>
			</tr>
			<tr>
				<td>

					<%
					String taglibPostReplyURL = "javascript:" + randomNamespace + "showForm('" + randomNamespace + "postReplyForm" + i + "', '" + randomNamespace + "postReplyBody" + i + "');";
					%>

					<c:choose>
						<c:when test="<%= messagesCount == 1 %>">
							<liferay-ui:message key="no-comments-yet" /> <a href="<%= taglibPostReplyURL %>"><liferay-ui:message key="be-the-first" /></a>
						</c:when>
						<c:otherwise>
							<liferay-ui:icon
								image="reply"
								label="<%= true %>"
								message="add-comment"
								url="<%= taglibPostReplyURL %>"
							/>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr id="<%= randomNamespace %>postReplyForm<%= i %>" style="display: none;">
				<td>
					<br />

					<div>
						<textarea id="<%= randomNamespace %>postReplyBody<%= i %>" name="<%= namespace %>postReplyBody<%= i %>" style="height: <%= ModelHintsConstants.TEXTAREA_DISPLAY_HEIGHT %>px; width: <%= ModelHintsConstants.TEXTAREA_DISPLAY_WIDTH %>px;" wrap="soft"></textarea>
					</div>

					<br />

					<%
					String postReplyButtonLabel = LanguageUtil.get(pageContext, "reply");

					if (WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(themeDisplay.getCompanyId(), scopeGroupId, MBDiscussion.class.getName())) {
						postReplyButtonLabel = LanguageUtil.get(pageContext, "submit-for-publication");
					}
					%>

					<input disabled="disabled" id="<%= randomNamespace %>postReplyButton<%= i %>" type="button" value="<%= postReplyButtonLabel %>" onClick="<%= randomNamespace %>postReply(<%= i %>);" />

					<input type="button" value="<liferay-ui:message key="cancel" />" onClick="document.getElementById('<%= randomNamespace %>postReplyForm<%= i %>').style.display = 'none'; void('');" />
				</td>
			</tr>
			</table>
		</c:if>

		<c:if test="<%= messagesCount > 1 %>">
			<c:if test="<%= MBDiscussionPermission.contains(permissionChecker, company.getCompanyId(), scopeGroupId, permissionClassName, permissionClassPK, ActionKeys.ADD_DISCUSSION) %>">
				<br />
			</c:if>

			<a name="<%= randomNamespace %>messages_top"></a>

			<c:if test="<%= treeWalker != null %>">
				<table class="tree-walker">
				<tr class="portlet-section-header results-header" style="font-size: x-small; font-weight: bold;">
					<td colspan="2">
						<liferay-ui:message key="threaded-replies" />
					</td>
					<td colspan="2">
						<liferay-ui:message key="author" />
					</td>
					<td>
						<liferay-ui:message key="date" />
					</td>
				</tr>

				<%
				int[] range = treeWalker.getChildrenRange(rootMessage);

				for (i = range[0]; i < range[1]; i++) {
					message = (MBMessage)messages.get(i);

					boolean lastChildNode = false;

					if ((i + 1) == range[1]) {
						lastChildNode = true;
					}

					request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER, treeWalker);
					request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_SEL_MESSAGE, rootMessage);
					request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_CUR_MESSAGE, message);
					request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_CATEGORY, category);
					request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_THREAD, thread);
					request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_LAST_NODE, Boolean.valueOf(lastChildNode));
					request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_DEPTH, new Integer(0));
				%>

					<liferay-util:include page="/html/taglib/ui/discussion/view_message_thread.jsp" />

				<%
				}
				%>

				</table>

				<br />
			</c:if>

			<table class="lfr-grid lfr-table">

			<%
			SearchContainer searchContainer = null;

			if (messages != null) {
				messages = ListUtil.sort(messages, new MessageCreateDateComparator(true));

				messages = ListUtil.copy(messages);

				messages.remove(0);
			}
			else {
				PortletURL currentURLObj = PortletURLUtil.getCurrent(renderRequest, renderResponse);

				searchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, currentURLObj, null, null);

				searchContainer.setTotal(messagesCount - 1);

				messages = MBMessageLocalServiceUtil.getThreadRepliesMessages(message.getThreadId(), WorkflowConstants.STATUS_ANY, searchContainer.getStart(), searchContainer.getEnd());

				searchContainer.setResults(messages);
			}

			List<Long> classPKs = new ArrayList<Long>();

			for (MBMessage curMessage : messages) {
				classPKs.add(curMessage.getMessageId());
			}

			List<RatingsEntry> ratingsEntries = RatingsEntryLocalServiceUtil.getEntries(themeDisplay.getUserId(), MBMessage.class.getName(), classPKs);
			List<RatingsStats> ratingsStatsList = RatingsStatsLocalServiceUtil.getStats(MBMessage.class.getName(), classPKs);

			for (i = 1; i <= messages.size(); i++) {
				message = messages.get(i - 1);

				if ((!message.isApproved() && (message.getUserId() != user.getUserId()) && !permissionChecker.isCommunityAdmin(scopeGroupId)) || !MBDiscussionPermission.contains(permissionChecker, company.getCompanyId(), scopeGroupId, permissionClassName, permissionClassPK, ActionKeys.VIEW)) {
					continue;
				}
			%>

				<tr>
					<td colspan="2" id="<%= randomNamespace %>messageScroll<%= message.getMessageId() %>">
						<a name="<%= randomNamespace %>message_<%= message.getMessageId() %>"></a>

						<input name="<%= namespace %>messageId<%= i %>" type="hidden" value="<%= message.getMessageId() %>" />
						<input name="<%= namespace %>parentMessageId<%= i %>" type="hidden" value="<%= message.getMessageId() %>" />
					</td>
				</tr>
				<tr>
					<td class="lfr-center lfr-top">
						<liferay-ui:user-display
							userId="<%= message.getUserId() %>"
							userName="<%= HtmlUtil.escape(message.getUserName()) %>"
							displayStyle="<%= 2 %>"
						/>
					</td>
					<td class="lfr-top stretch">
						<c:if test="<%= (message != null) && !message.isApproved() %>">
							<aui:model-context bean="<%= message %>" model="<%= MBMessage.class %>" />

							<div>
								<aui:workflow-status model="<%= MBDiscussion.class %>" status="<%= message.getStatus() %>" />
							</div>
						</c:if>

						<div>

							<%
							String msgBody = BBCodeUtil.getHTML(message);

							msgBody = StringUtil.replace(msgBody, "@theme_images_path@/emoticons", themeDisplay.getPathThemeImages() + "/emoticons");
							msgBody = HtmlUtil.wordBreak(msgBody, 80);
							%>

							<%= msgBody %>
						</div>

						<br />

						<div>
							<c:choose>
								<c:when test="<%= message.getParentMessageId() == rootMessage.getMessageId() %>">
									<%= LanguageUtil.format(pageContext, "posted-on-x", dateFormatDateTime.format(message.getModifiedDate())) %>
								</c:when>
								<c:otherwise>

									<%
									MBMessage parentMessage = MBMessageLocalServiceUtil.getMessage(message.getParentMessageId());

									StringBundler sb = new StringBundler(7);

									sb.append("<a href=\"#");
									sb.append(randomNamespace);
									sb.append("message_");
									sb.append(parentMessage.getMessageId());
									sb.append("\">");
									sb.append(HtmlUtil.escape(parentMessage.getUserName()));
									sb.append("</a>");
									%>

									<%= LanguageUtil.format(pageContext, "posted-on-x-in-reply-to-x", new Object[] {dateFormatDateTime.format(message.getModifiedDate()), sb.toString()}) %>
								</c:otherwise>
							</c:choose>
						</div>

						<br />

						<table class="lfr-table">
						<tr>
							<c:if test="<%= ratingsEnabled %>">

								<%
								RatingsEntry ratingsEntry = getRatingsEntry(ratingsEntries, message.getMessageId());
								RatingsStats ratingStats = getRatingsStats(ratingsStatsList, message.getMessageId());
								%>

								<td>
									<liferay-ui:ratings
										className="<%= MBMessage.class.getName() %>"
										classPK="<%= message.getMessageId() %>"
										ratingsEntry="<%= ratingsEntry %>"
										ratingsStats="<%= ratingStats %>"
										type="thumbs"
									/>
								</td>
							</c:if>

							<c:if test="<%= MBDiscussionPermission.contains(permissionChecker, company.getCompanyId(), scopeGroupId, permissionClassName, permissionClassPK, ActionKeys.ADD_DISCUSSION) %>">
								<td>

									<%
									String taglibPostReplyURL = "javascript:" + randomNamespace + "showForm('" + randomNamespace + "postReplyForm" + i + "', '" + randomNamespace + "postReplyBody" + i + "');";
									%>

									<liferay-ui:icon
										image="reply"
										label="<%= true %>"
										message="post-reply"
										url="<%= taglibPostReplyURL %>"
									/>
								</td>
							</c:if>

							<c:if test="<%= i > 0 %>">

								<%
								String taglibTopURL = "#" + randomNamespace + "messages_top";
								%>

								<td>
									<liferay-ui:icon
										image="top"
										label="<%= true %>"
										url="<%= taglibTopURL %>"
										/>
								</td>

								<c:if test="<%= MBDiscussionPermission.contains(permissionChecker, company.getCompanyId(), scopeGroupId, permissionClassName, permissionClassPK, message.getMessageId(), ActionKeys.UPDATE_DISCUSSION) %>">

									<%
									String taglibEditURL = "javascript:" + randomNamespace + "showForm('" + randomNamespace + "editForm" + i + "', '" + randomNamespace + "editReplyBody" + i + "');";
									%>

									<td>
										<liferay-ui:icon
											image="edit"
											label="<%= true %>"
											url="<%= taglibEditURL %>"
										/>
									</td>
								</c:if>

								<c:if test="<%= MBDiscussionPermission.contains(permissionChecker, company.getCompanyId(), scopeGroupId, permissionClassName, permissionClassPK, message.getMessageId(), ActionKeys.DELETE_DISCUSSION) %>">

									<%
									String taglibDeleteURL = "javascript:" + randomNamespace + "deleteMessage(" + i + ");";
									%>

									<td>
										<liferay-ui:icon-delete
											label="<%= true %>"
											url="<%= taglibDeleteURL %>"
										/>
									</td>
								</c:if>
							</c:if>
						</tr>
						</table>

						<table class="lfr-table">
						<tr id="<%= randomNamespace %>postReplyForm<%= i %>" style="display: none;">
							<td>
								<br />

								<div>
									<textarea id="<%= randomNamespace %>postReplyBody<%= i %>" name="<%= namespace %>postReplyBody<%= i %>" style="height: <%= ModelHintsConstants.TEXTAREA_DISPLAY_HEIGHT %>px; width: <%= ModelHintsConstants.TEXTAREA_DISPLAY_WIDTH %>px;" wrap="soft"></textarea>
								</div>

								<br />

								<input disabled="disabled" id="<%= randomNamespace %>postReplyButton<%= i %>" type="button" value="<liferay-ui:message key="reply" />" onClick="<%= randomNamespace %>postReply(<%= i %>);" />

								<input type="button" value="<liferay-ui:message key="cancel" />" onClick="document.getElementById('<%= randomNamespace %>postReplyForm<%= i %>').style.display = 'none'; void('');" />
							</td>
						</tr>

						<c:if test="<%= MBDiscussionPermission.contains(permissionChecker, company.getCompanyId(), scopeGroupId, permissionClassName, permissionClassPK, message.getMessageId(), ActionKeys.UPDATE_DISCUSSION) %>">
							<tr id="<%= randomNamespace %>editForm<%= i %>" style="display: none;">
								<td>
									<br />

									<div>
										<textarea id="<%= randomNamespace %>editReplyBody<%= i %>" name="<%= namespace %>editReplyBody<%= i %>" style="height: <%= ModelHintsConstants.TEXTAREA_DISPLAY_HEIGHT %>px; width: <%= ModelHintsConstants.TEXTAREA_DISPLAY_WIDTH %>px;" wrap="soft"><%= HtmlUtil.escape(message.getBody()) %></textarea>
									</div>

									<br />

									<%
									String publishButtonLabel = LanguageUtil.get(pageContext, "publish");

									if (WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(themeDisplay.getCompanyId(), scopeGroupId, MBDiscussion.class.getName())) {
										publishButtonLabel = LanguageUtil.get(pageContext, "submit-for-publication");
									}
									%>

									<input id="<%= randomNamespace %>editReplyButton<%= i %>" onClick="<%= randomNamespace %>updateMessage(<%= i %>);" type="button" value="<liferay-ui:message key="<%= publishButtonLabel %>" />" />

									<input type="button" value="<liferay-ui:message key="cancel" />" onClick="document.getElementById('<%= randomNamespace %>editForm<%= i %>').style.display = 'none'; void('');" />
								</td>
							</tr>
						</c:if>

						</table>
					</td>
				</tr>

				<c:if test="<%= i < messages.size() %>">
					<tr>
						<td colspan="2">
							<div class="separator"><!-- --></div>
						</td>
					</tr>
				</c:if>

			<%
			}
			%>

			</table>

			<c:if test="<%= (searchContainer != null) && (searchContainer.getTotal() > searchContainer.getDelta()) %>">
				<liferay-ui:search-paginator searchContainer="<%= searchContainer %>" />
			</c:if>
		</c:if>

		</form>
	</div>

	<aui:script>
		function <%= randomNamespace %>deleteMessage(i) {
			eval("var messageId = document.<%= formName %>.<%= namespace %>messageId" + i + ".value;");

			document.<%= formName %>.<%= namespace %><%= Constants.CMD %>.value = "<%= Constants.DELETE %>";
			document.<%= formName %>.<%= namespace %>messageId.value = messageId;
			submitForm(document.<%= formName %>);
		}

		function <%= randomNamespace %>postReply(i) {
			eval("var parentMessageId = document.<%= formName %>.<%= namespace %>parentMessageId" + i + ".value;");
			eval("var body = document.<%= formName %>.<%= namespace %>postReplyBody" + i + ".value;");

			document.<%= formName %>.<%= namespace %><%= Constants.CMD %>.value = "<%= Constants.ADD %>";
			document.<%= formName %>.<%= namespace %>parentMessageId.value = parentMessageId;
			document.<%= formName %>.<%= namespace %>body.value = body;
			submitForm(document.<%= formName %>);
		}

		function <%= randomNamespace %>scrollIntoView(messageId) {
			document.getElementById("<%= namespace %>messageScroll" + messageId).scrollIntoView();
		}

		function <%= randomNamespace %>showForm(rowId, textAreaId) {
			document.getElementById(rowId).style.display = "";
			document.getElementById(textAreaId).focus();
		}

		function <%= randomNamespace %>updateMessage(i) {
			eval("var messageId = document.<%= formName %>.<%= namespace %>messageId" + i + ".value;");
			eval("var body = document.<%= formName %>.<%= namespace %>editReplyBody" + i + ".value;");

			document.<%= formName %>.<%= namespace %><%= Constants.CMD %>.value = "<%= Constants.UPDATE %>";
			document.<%= formName %>.<%= namespace %>messageId.value = messageId;
			document.<%= formName %>.<%= namespace %>body.value = body;
			submitForm(document.<%= formName %>);
		}
	</aui:script>

	<aui:script use="aui-event-input">
		var form = A.one(document.<%= formName %>);

		if (form) {
			var textareas = form.all('textarea');

			if (textareas) {
				textareas.on(
					'input',
					function(event) {
						var textarea = event.currentTarget;
						var currentValue = A.Lang.trim(textarea.val());

						var id = textarea.get('id');
						var buttonId = id.replace(/Body/, 'Button');
						var button = A.one('#' + buttonId);

						if (button) {
							button.set('disabled', !currentValue.length);
						}
					}
				);
			}
		}
	</aui:script>
</c:if>

<%!
private RatingsEntry getRatingsEntry(List<RatingsEntry> ratingEntries, long classPK) {
	for (RatingsEntry ratingsEntry : ratingEntries) {
		if (ratingsEntry.getClassPK() == classPK) {
			return ratingsEntry;
		}
	}

	return RatingsEntryUtil.create(0);
}

private RatingsStats getRatingsStats(List<RatingsStats> ratingsStatsList, long classPK) {
	for (RatingsStats ratingsStats : ratingsStatsList) {
		if (ratingsStats.getClassPK() == classPK) {
			return ratingsStats;
		}
	}

	return RatingsStatsUtil.create(0);
}
%>