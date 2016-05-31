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

.portlet-directory .details .avatar {
	float: left;
	width: 100px;
}

.portlet-directory .details dd {
	margin-bottom: 0.8em;
}

.portlet-directory .details dl {
	margin-left: 115px;
}

.portlet-directory .details dt, .portlet-directory .details dd {
	clear: both;
}

.portlet-directory .details dt {
	font-weight: bold;
	line-height: 1.1;
	margin-bottom: 0;
}

.portlet-directory .details {
	overflow: hidden;
}

.portlet-directory table.org-labor-table td {
	background-color: #EFEFEF;
	padding: 5px;
	text-align: center;
}

.portlet-directory table.org-labor-table td.no-color {
	background-color: #FFF;
}

.portlet-directory table.org-labor-table th {
	background-color: #999;
	color: white;
	padding: 1px 5px 1px 5px;
}

.portlet-directory table.org-labor-table {
	border: 1px solid white;
	margin-bottom: 30px;
	margin-top: 10px;
}

.portlet-directory .primary {
	background: #EEE url(<%= themeImagesPath %>/dock/my_place_current.png) no-repeat 3px 50%;
	color: #020509;
	font-weight: bold;
	margin-bottom: 10px;
	padding: 5px;
	padding-left: 10px;
}

.portlet-directory dl.property-list {
	margin-top: 0;
	overflow: hidden;
	padding: 0;
}

.portlet-directory .property-list dd img {
	vertical-align: middle;
}

.portlet-directory .property-list dd, .portlet-directory .property-list dd {
	padding-left: 5px;
}

.portlet-directory ul.property-list li {
	margin-bottom: 5px;
}

.portlet-directory .property-list dt, .portlet-directory .property-list dd {
	float: left;
	line-height: 1.5;
	margin: 0;
}

.portlet-directory .property-list dt {
	clear: left;
	font-weight: bold;
	min-width: 5em;
}

.portlet-directory .section {
	float: left;
	margin-left: 10px;
	width: 47%;
}

.portlet-directory .section h3 {
	background: url() no-repeat 2px 50%;
	border-bottom: 1px solid #CCC;
	line-height: 1.5;
	margin-bottom: 0.5em;
	padding-left: 25px;
}

.portlet-directory .section li {
	list-style: none;
	margin: 0;
	padding-left: 25px;
}

.portlet-directory .section ul {
	margin: 0;
}

.portlet-directory .entity-addresses .mailing-name {
	display: block;
	font-style: italic;
}

.portlet-directory .entity-addresses h3 {
	background-image: url(<%= themeImagesPath %>/dock/home.png);
}

.portlet-directory .entity-addresses .primary {
	background-position: 3px 5px;
}

.portlet-directory .entity-comments h3 {
	background-image: url(<%= themeImagesPath %>/dock/welcome_message.png);
}

.portlet-directory .entity-details {
	clear: both;
}

.portlet-directory .entity-email-addresses h3 {
	background-image: url(<%= themeImagesPath %>/mail/unread.png);
}

.portlet-directory .user-information, .portlet-directory .organization-information {
	overflow: hidden;
}

.portlet-directory .entity-instant-messenger h3 {
	background-image: url(<%= themeImagesPath %>/common/conversation.png);
}

.portlet-directory .entity-phones h3 {
	background-image: url(<%= themeImagesPath %>/common/telephone.png);
}

.portlet-directory .entity-sms h3 {
	background-image: url(<%= themeImagesPath %>/common/telephone_mobile.png);
}

.portlet-directory .entity-social-network h3 {
	background-image: url(<%= themeImagesPath %>/common/group.png);
}

.portlet-directory .entity-websites h3 {
	background-image: url(<%= themeImagesPath %>/common/history.png);
}

.portlet-directory .entity-services h3 {
	background-image: url(<%= themeImagesPath %>/common/services.png);
}

.ie6 .portlet-directory {
	height: 1%;
}

.ie6 .portlet-directory dl.property-list {
	height: 1%;
}

.ie6 .portlet-directory .user-information, .ie6 .portlet-directory .organization-information {
	height: 1%;
}