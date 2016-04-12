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

.portlet-blogs-aggregator .abstract {
	clear: both;
	margin-top: 1.5em;
	margin-bottom: 1.5em;
}

.portlet-blogs-aggregator .comments {
	margin-top: 1.5em;
}

.portlet-blogs-aggregator .entry-info {
	line-height: 2em;
	overflow: hidden;
}

.ie .portlet-blogs-aggregator .entry-info {
	height: 1%;
}

.portlet-blogs-aggregator .entry-author {
	background: url(<%= themeImagesPath %>/portlet/edit_guest.png) no-repeat 0 50%;
	border-right: 1px solid #999;
	color: #999;
	float: left;
	font-weight: bold;
	margin-right: 10px;
	padding-left: 20px;
	padding-right: 10px;
}

.portlet-blogs-aggregator .entry-categories {
	border-left: 1px solid #999;
	float: left;
	padding-left: 10px;
}

.portlet-blogs-aggregator .entry-content {
}

.portlet-blogs-aggregator .entry-date {
	background: url(<%= themeImagesPath %>/common/date.png) no-repeat 0 50%;
	color: #999;
	float: left;
	overflow: hidden;
	padding-left: 25px;
	padding-right: 10px;
}

.portlet-blogs-aggregator .entry-tags {
	border-left: 1px solid #999;
	overflow: hidden;
	padding-left: 10px;
	padding-right: 10px;
}

.portlet-blogs-aggregator .entry-title {
	display: block;
	font-size: 1.2em;
	font-weight: bold;
	margin-bottom: 0.2em;
}

.portlet-blogs-aggregator .search-container {
	margin-top: 1.5em;
}