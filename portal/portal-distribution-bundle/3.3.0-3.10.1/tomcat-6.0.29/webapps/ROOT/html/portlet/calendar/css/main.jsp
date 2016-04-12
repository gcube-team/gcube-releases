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

<%@ include file="/html/portlet/css_init.jsp" %>

.portlet-calendar .calendar-container {
	background: #fff url(<%= themeImagesPath %>/calendar/calendar_day_drop_shadow.png) repeat-x 0 99%;
	border: 1px solid #D7D7D7;
	width: 400px;
}

.ie .portlet-calendar .calendar-container {
	background-position: 0 98%;
}

.ie6 .portlet-calendar .calendar-container {
	background: none;
}

.portlet-calendar .calendar {
	width: 100%;
}

.portlet-calendar .calendar td {
	border: 1px solid #ccc;
	padding: 5px;
}

.portlet-calendar .calendar td td {
	border-width: 0;
	padding: 0;
}

.portlet-calendar .calendar-day {
	float:left;
	text-align: center;
	width: 209px;
}

.portlet-calendar .calendar-day h2 {
	background: url(<%= themeImagesPath %>/calendar/day_heading.png) repeat-x 0 100%;
	font-size: 2em;
	margin: 0;
	padding: 0.5em 0;
}

.portlet-calendar .calendar-day h3 {
	font-size: 11em;
	line-height: 1.2;
	margin: 0;
	vertical-align: middle;
}

.portlet-calendar .calendar-day .day-text {
	background: #727C81;
	color: #fff;
	font-size: 1.6em;
}

.portlet-calendar .calendar-day .day-number {
	border: 1px solid #fff;
	border-bottom: none;
	font-size: 110px;
	font-weight: normal;
	padding-bottom: 5px;
}

.portlet-calendar .taglib-calendar {
	border-color: #999;
	margin-bottom: 1.5em;
	width: 190px;
}

.portlet-calendar .calendar-container .taglib-calendar {
	background: url(<%= themeImagesPath %>/calendar/calendar_drop_shadow.png) repeat-y 0 0;
	clear: none;
	float: right;
	margin-bottom: 0;
	margin-left: -8px;
	padding-left: 8px;
}

.ie6 .portlet-calendar .taglib-calendar {
	background: none;
	border-left: 1px solid;
	margin-left: 0;
	padding-left: 0;
	width: 189px;
}

.portlet-calendar .calendar-container .taglib-calendar {
	background: #fff;
	border-left: 1px solid #D7D7D7;
	margin-left: -1px;
	padding-left: 0;
}

.portlet-calendar .taglib-calendar table {
}

.portlet-calendar .calendar-container .taglib-calendar table {
	border: none;
}

.portlet-calendar .taglib-calendar table .first {
	border-left: none;
}

.portlet-calendar .taglib-calendar table .last {
	border-right: none;
}

.portlet-calendar .calendar-inactive {
	color: #999;
}

.portlet-calendar .calendar-current-day a {
	color: #fff;
	font-weight: bold;
	text-decoration: none;
}

.portlet-calendar .taglib-calendar tr td.calendar-current-day a:hover, .taglib-calendar tr td.calendar-current-day a:focus {
	background-color: #DFF4FF;
	border-color: #AEB8BC;
	color: #06c;
}

.portlet-calendar .taglib-calendar td.calendar-current-day a span {
	border: none;
}

.portlet-calendar .has-events a span {
	background: url(<%= themeImagesPath %>/calendar/event_indicator.png) no-repeat 50% 95%;
	padding-bottom: 5px;
}

.portlet-calendar .has-events.calendar-current-day a span {
	background-image: url(<%= themeImagesPath %>/calendar/event_indicator_current.png);
}

.portlet-calendar .day-grid {
	border-top: 2px solid #CCC;
	margin-left: 50px;
}

.portlet-calendar .day-grid .business-hour {
	background: #FEFEFE;
}

.portlet-calendar .day-grid .night-hour {
	background: #EFEFEF;
}

.portlet-calendar .day-grid .hour.all-day {
	border: none;
	height: auto;
	min-height: 24px;
}

.portlet-calendar .day-grid .hour {
	border-bottom: 1px solid #CCC;
	height: 24px;
}

.portlet-calendar .day-grid .hour span {
	color: #777;
	display: block;
	font-size: 0.8em;
	font-weight: bold;
	left: -50px;
	position: absolute;
	text-align: right;
	width: 45px;
}

.portlet-calendar .day-grid .half-hour {
	border-bottom: 2px solid #CCC;
	height: 23px;
}

.portlet-calendar .day-grid .event-box {
	background: #F0F5F7;
	border: 2px solid #828F95;
	padding: 5px;
}

.portlet-calendar .day-grid .event-description {
	border-top: 1px solid #AEB8BC;
	padding-top: 0.5em;
}

.portlet-calendar .detail-column-last {
	background-color: #D7F1FF;
	overflow: visible;
}

.portlet-calendar .detail-column-last .detail-column-content {
	border: 1px solid #88C5D9;
	padding: 0.7em;
}

.portlet-calendar .event-duration-hour {
	float: left;
}

.portlet-calendar .folder-icon {
	margin-bottom: 2em;
	overflow: hidden;
	text-align: center;
}

.portlet-calendar h3.event-title, .portlet-calendar h3.event-title {
	border-bottom: 1px solid #000;
	font-size: 14px;
	font-weight: bold;
	margin-top: 0;
}

.portlet-calendar .property-list {
	margin-left: 0;
	margin-top: 0;
	overflow: hidden;
	padding: 0;
}

.portlet-calendar .property-list dd, .portlet-calendar .property-list dd {
	padding-left: 5px;
}

.portlet-calendar .property-list dd img {
	vertical-align: middle;
}

.portlet-calendar .property-list dt {
	clear: left;
	font-weight: bold;
	min-width: 5em;
}

.portlet-calendar .property-list dt, .portlet-calendar .property-list dd {
	float: left;
	line-height: 1.5;
	margin: 0;
}

.portlet-calendar .reminders {
	clear: both;
}

.portlet-calendar .calendar-event-details .aui-field-wrapper-content {
	margin: 0;
}

.portlet-calendar .calendar-event-details .lfr-panel-content {
	padding: 10px 15px;
}

.portlet-calendar #commentsPanelContainer {
	border-width: 0;
}