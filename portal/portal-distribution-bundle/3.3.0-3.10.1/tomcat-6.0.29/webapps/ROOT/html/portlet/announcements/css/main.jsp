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

.portlet-alerts {
}

.portlet-alerts .entry {
	border: 6px solid #c00;
	margin: 8px 0px;
	padding: 6px;
}

.portlet-alerts .entry-content {
	display: block;
	padding: 2px 2px 2px 30px;
}

.portlet-alerts .entry-scope {
	color: #ccc;
	display: block;
}

.portlet-alerts .entry-type-general {
	background: #fff url('<%= themeImagesPath %>/common/page.png') left top no-repeat;
}

.portlet-alerts .entry-type-news {
	background: #fff url('<%= themeImagesPath %>/common/page.png') left top no-repeat;
}

.portlet-alerts .entry-type-test {
	background: #fff url('<%= themeImagesPath %>/common/page.png') left top no-repeat;
}

.portlet-announcements {
}

.portlet-announcements .entry {
	margin: 4px 0px 1.2em;
	padding-bottom: 0.5em;
}

.portlet-announcements .important, .portlet-alerts .important {
	font-weight: normal;
}

.portlet-announcements .entry.last {
}

.portlet-announcements .entry-title, .portlet-alerts .entry-title {
	display: block;
	margin-bottom: 0;
	position: relative;
}

.portlet-announcements .read-entries .entry-title, .portlet-alerts .read-entries .entry-title{
	opacity: 0.5;
}

.portlet-announcements .read .entry-title, .portlet-alerts .read .entry-title {
}

.portlet-announcements .entry-content {
	display: block;
	margin-bottom: 0.5em;
	padding: 2px 2px 2px 30px;
}

.portlet-announcements .entry-scope {
	color: #555;
	display: block;
}

.portlet-announcements .edit-actions, .portlet-alerts .edit-actions {
	font-size: 0.7em;
	font-weight: normal;
	position: absolute;
	right: 0;
	top: 0;
}

.portlet-announcements .delete-entry {
	padding-right: 2em;
}

.portlet-announcements .entry-type-general {
	background: #fff url('<%= themeImagesPath %>/common/all_pages.png') 4px 4px no-repeat;
}

.portlet-announcements .entry-type-news {
	background: #fff url('<%= themeImagesPath %>/common/page.png') 4px 4px no-repeat;
}

.portlet-announcements .entry-type-test {
	background: #fff url('<%= themeImagesPath %>/common/page.png') 4px 4px no-repeat;
}