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

.portlet-communities .lfr-theme-list h3,
.portlet-enterprise-admin .lfr-theme-list h3 {
	background: #D3DADD;
	padding: 0.5em;
}

.portlet-communities .lfr-theme-list li,
.portlet-enterprise-admin .lfr-theme-list li {
	float: left;
	margin: 0 1.3em 1.3em 0;
	text-align: center;
}

.portlet-communities .theme-title,
.portlet-enterprise-admin .theme-title {
	font-weight: bold;
	margin: 0;
	padding: 2px;
}

.portlet-communities .lfr-current-theme,
.portlet-enterprise-admin .lfr-current-theme {
	background: #F0F5F7;
	border: 1px solid #828F95;
	margin-bottom: 1em;
	padding: 3px 3px 1em;
}

.portlet-communities .lfr-current-theme h3,
.portlet-enterprise-admin .lfr-current-theme h3 {
	margin: 0 0 0.5em;
}

.portlet-communities .lfr-current-theme .theme-title,
.portlet-enterprise-admin .lfr-current-theme .theme-title {
	border-bottom: 1px solid #828F95;
	font-size: 1.4em;
	margin-bottom: 0.5em;
	padding-left: 0;
	padding-top: 0;
}

.portlet-communities .lfr-current-theme .theme-details,
.portlet-enterprise-admin .lfr-current-theme .theme-details {
	padding: 0 2px 0 170px;
}

.portlet-communities .theme-entry,
.portlet-enterprise-admin .theme-entry {
	height: 96px;
	text-decoration: none;
	width: 120px;
}

.portlet-communities .theme-entry, .portlet-communities .lfr-current-theme .theme-screenshot,
.portlet-enterprise-admin .theme-entry, .portlet-enterprise-admin .lfr-current-theme .theme-screenshot {
	border: 1px solid #AEB8BC;
	color: #fff;
	display: block;
	padding: 1px;
}

.portlet-communities .lfr-current-theme .theme-screenshot,
.portlet-enterprise-admin .lfr-current-theme .theme-screenshot {
	float: left;
	height: 120px;
	margin: 0 0.5em;
	width: 150px;
}

.portlet-communities .theme-entry .theme-title,
.portlet-enterprise-admin .theme-entry .theme-title {
	background: #878F93;
	display: block;
	font-size: 1em;
	margin-bottom: 3px;
}

.portlet-communities .lfr-theme-list .theme-entry:hover,
.portlet-enterprise-admin .lfr-theme-list .theme-entry:hover {
	background: #DFF4FF;
	border-color: #A7CEDF;
	color: #9ebac8;
	position: relative;
	text-decoration: underline;
}

.portlet-communities .theme-entry:hover .theme-thumbnail,
.portlet-enterprise-admin .theme-entry:hover .theme-thumbnail {
	border: 1px solid #A7CEDF;
	display: block;
	height: 120px;
	left: -17px;
	padding: 1px;
	position: absolute;
	top: 2em;
	width: 150px;
	z-index: 100;
}

.portlet-communities .theme-entry:hover .theme-title,
.portlet-enterprise-admin .theme-entry:hover .theme-title {
	background: #1E2529;
}

.portlet-communities .theme-thumbnail,
.portlet-enterprise-admin .theme-thumbnail {
	height: 68px;
	width: 85px;
}

.portlet-communities .lfr-available-themes h3,
.portlet-enterprise-admin .lfr-available-themes h3 {
	margin: 0;
	overflow: hidden;
}

.ie6 .lfr-available-themes h3 {
	height: 1%;
}

.portlet-communities .lfr-available-themes .lfr-theme-list,
.portlet-enterprise-admin .lfr-available-themes .lfr-theme-list {
	margin-top: 0.7em;
}

.portlet-communities .lfr-available-themes .header-title,
.portlet-enterprise-admin .lfr-available-themes .header-title {
	float: left;
}

.portlet-communities .lfr-available-themes .install-themes,
.portlet-enterprise-admin .lfr-available-themes .install-themes {
	float: right;
	font-size: 11px;
}

.portlet-communities .theme-details dl, .portlet-communities .lfr-theme-list .theme-details dd,
.portlet-enterprise-admin .theme-details dl, .portlet-enterprise-admin .lfr-theme-list .theme-details dd {
	margin: 0;
}

.portlet-communities .theme-details dl,
.portlet-enterprise-admin .theme-details dl {
	margin-bottom: 1em;
}

.portlet-communities .theme-details dt,
.portlet-enterprise-admin .theme-details dt {
	font-weight: bold;
}

.portlet-communities .selected-color-scheme .theme-entry,
.portlet-enterprise-admin .selected-color-scheme .theme-entry {
	border: 3px solid #369;
}

.portlet-communities .selected-color-scheme .theme-entry .theme-title,
.portlet-enterprise-admin .selected-color-scheme .theme-entry .theme-title {
	background: #1E2529;
}

.portlet-communities table .lfr-top .taglib-header .header-title,
.portlet-enterprise-admin table.lfr-top .taglib-header .header-title {
	margin: 0 0 0.5em 0;
}

.aui-tree-node-selected {
	background: #eee;
}

.aui-tree-drag-helper a {
	text-decoration: none;
}

.aui-tree-drag-helper-label {
	margin-top: -1px;
}

.aui-tree-pages .aui-tree-icon {
	background: transparent url() no-repeat 50% 50%;
	height: 18px;
	width: 18px;
}

.aui-tree-expanded .aui-tree-icon {
	background-image: url(<%= themeImagesPath %>/trees/page_copy.png);
}

.aui-tree-collapsed .aui-tree-icon {
	background-image: url(<%= themeImagesPath %>/trees/page.png);
}

.lfr-root-node .aui-tree-icon {
	background-image: url(<%= themeImagesPath %>/trees/root.png);
}

.lfr-tree-loading-icon {
	margin: 0 auto;
	padding-top: 5px;
}

.lfr-tree-controls {
	width: 165px;
}

.lfr-tree-controls div,
.lfr-tree-controls a {
	float: left;
}

.lfr-tree-controls-label {
	line-height: 17px;
	padding: 0 2px;
	text-decoration: none;
}

.lfr-tree-controls-item {
	padding: 0 0 6px 6px;
}

.lfr-tree-controls-expand {
	background-image: url(<%= themeImagesPath %>/trees/expand_all.png);
}

.lfr-tree-controls-collapse {
	background-image: url(<%= themeImagesPath %>/trees/collapse_all.png);
}

.ie6 .theme-thumbnail,
.ie6 .no-png-fix {
	behavior: none;
}