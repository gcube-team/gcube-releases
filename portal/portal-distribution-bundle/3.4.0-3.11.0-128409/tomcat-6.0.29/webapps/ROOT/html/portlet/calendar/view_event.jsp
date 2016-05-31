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

<%@ include file="/html/portlet/calendar/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

CalEvent event = (CalEvent)request.getAttribute(WebKeys.CALENDAR_EVENT);

event = event.toEscapedModel();

Recurrence recurrence = null;

int recurrenceType = ParamUtil.getInteger(request, "recurrenceType", Recurrence.NO_RECURRENCE);
if (event.getRepeating()) {
	recurrence = event.getRecurrenceObj();
	recurrenceType = recurrence.getFrequency();
}

int endDateType = ParamUtil.getInteger(request, "endDateType");
if ((event.getRepeating()) && (recurrence != null)) {
	if (recurrence.getUntil() != null) {
		endDateType = 2;
	}
	else if (recurrence.getOccurrence() > 0) {
		endDateType = 1;
	}
}

request.setAttribute("view_event.jsp-event", event);
%>

<liferay-ui:header
	backURL="<%= redirect %>"
	title='<%= event.getTitle() %>'
/>

<aui:layout cssClass="event">
	<aui:column columnWidth="<%= 75 %>" cssClass="folder-column folder-column-first" first="<%= true %>">
		<dl class="property-list">
			<dt>
				<liferay-ui:icon
					image="../common/calendar"
				/>

				<liferay-ui:message key="start-date" />:
			</dt>
			<dd>
				<c:choose>
					<c:when test="<%= event.isTimeZoneSensitive() %>">
						<%= dateFormatDate.format(Time.getDate(event.getStartDate(), timeZone)) %>
					</c:when>
					<c:otherwise>
						<%= dateFormatDate.format(event.getStartDate()) %>
					</c:otherwise>
				</c:choose>
			</dd>
			<dt>
				<c:choose>
					<c:when test="<%= (endDateType == 0) || (endDateType == 2) %>">
						<liferay-ui:icon
							image="../common/calendar"
						/>

						<liferay-ui:message key="end-date" />:
					</c:when>
					<c:otherwise>
						<liferay-ui:message key="ocurrence-s" />:
					</c:otherwise>
				</c:choose>
			</dt>
			<dd>
				<c:if test="<%= (endDateType == 0) %>">
					<liferay-ui:message key="none" />
				</c:if>

				<c:if test="<%= (endDateType == 1) %>">
					<%= recurrence.getOccurrence() %>
				</c:if>

				<c:if test="<%= (endDateType == 2) %>">
					<%= event.isTimeZoneSensitive() ? dateFormatDate.format(Time.getDate(event.getEndDate(), timeZone)) : dateFormatDate.format(event.getEndDate()) %>
				</c:if>
			</dd>
			<dt>
				<liferay-ui:icon
					image="../common/time"
				/>

				<liferay-ui:message key="duration" />:
			</dt>
			<dd>

				<%
				boolean allDay = CalUtil.isAllDay(event, timeZone, locale);
				%>

				<c:choose>
					<c:when test="<%= allDay %>">
						<abbr class="duration" title="<liferay-ui:message key="all-day" />">
							<liferay-ui:message key="all-day" />:
						</abbr>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="<%= event.isTimeZoneSensitive() %>">
								<abbr class="dtstart" title="<%= dateFormatISO8601.format(Time.getDate(event.getStartDate(), timeZone)) %>">
									<%= dateFormatTime.format(Time.getDate(event.getStartDate(), timeZone)) %>
								</abbr>
							</c:when>
							<c:otherwise>
								<abbr class="dtstart" title="<%= dateFormatISO8601.format(event.getStartDate()) %>">
									<%= dateFormatTime.format(event.getStartDate()) %>
								</abbr>
							</c:otherwise>
						</c:choose>
						&#150;
						<c:choose>
							<c:when test="<%= event.isTimeZoneSensitive() %>">
								<%= dateFormatTime.format(Time.getDate(CalUtil.getEndTime(event), timeZone)) %>
							</c:when>
							<c:otherwise>
								<%= dateFormatTime.format(CalUtil.getEndTime(event)) %>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>

				<c:if test="<%= allDay %>">
					<liferay-ui:message key="all-day" />
				</c:if>

				<c:if test="<%= event.isTimeZoneSensitive() %>">
					(<liferay-ui:message key="time-zone-sensitive" />)
				</c:if>
			</dd>
			<dt>
				<liferay-ui:icon
					image="../common/attributes"
				/>

				<liferay-ui:message key="type" />:
			</dt>
			<dd>
				<span class="categories"><%= LanguageUtil.get(pageContext, event.getType()) %></span>
			</dd>
		</dl>

		<c:if test="<% recurrence.getOccurrence() != null %>">
			<liferay-ui:icon
				image="../common/undo"
			/>
			<liferay-util:include page="/html/portlet/calendar/view_event_recurrence.jsp" />
		</c:if>

		<liferay-ui:custom-attributes-available className="<%= CalEvent.class.getName() %>">
			<liferay-ui:custom-attribute-list
				className="<%= CalEvent.class.getName() %>"
				classPK="<%= (event != null) ? event.getEventId() : 0 %>"
				editable="<%= false %>"
				label="<%= true %>"
			/>
		</liferay-ui:custom-attributes-available>

		<p>
			<%= event.getDescription() %>
		</p>

		<span class="entry-categories">
			<liferay-ui:asset-categories-summary
				className="<%= CalEvent.class.getName() %>"
				classPK="<%= event.getEventId() %>"
			/>
		</span>

		<span class="entry-tags">
			<liferay-ui:asset-tags-summary
				className="<%= CalEvent.class.getName() %>"
				classPK="<%= event.getEventId() %>"
				message="tags"
			/>
		</span>
	</aui:column>

	<aui:column columnWidth="<%= 25 %>" cssClass="detail-column detail-column-last" last="<%= true %>">
		<div class="folder-icon">
			<liferay-ui:icon
				cssClass="folder-avatar"
				image="../file_system/large/calendar"
			/>

			<div class="event-name">
				<h4><%= event.getTitle() %></h4>
			</div>
		</div>

		<%
		request.removeAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
		%>

		<liferay-util:include page="/html/portlet/calendar/event_action.jsp" />
	</aui:column>
</aui:layout>

<c:if test="<%= enableComments %>">
	<liferay-ui:panel-container extended="<%= false %>" id="commentsPanelContainer" persistState="<%= true %>">
		<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" id="commentsPanel" persistState="<%= true %>" title='<%= LanguageUtil.get(pageContext, "comments") %>'>
			<portlet:actionURL var="discussionURL">
				<portlet:param name="struts_action" value="/calendar/edit_event_discussion" />
			</portlet:actionURL>

			<liferay-ui:discussion
				className="<%= CalEvent.class.getName() %>"
				classPK="<%= event.getEventId() %>"
				formAction="<%= discussionURL %>"
				formName="fm2"
				ratingsEnabled="true"
				redirect="<%= currentURL %>"
				subject="<%= event.getTitle() %>"
				userId="<%= event.getUserId() %>"
			/>
		</liferay-ui:panel>
	</liferay-ui:panel-container>
</c:if>

<%
PortalUtil.setPageSubtitle(event.getTitle(), request);
PortalUtil.setPageDescription(event.getDescription(), request);

PortalUtil.addPortletBreadcrumbEntry(request, event.getTitle(), currentURL);
%>