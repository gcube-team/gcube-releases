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

.portlet-asset-category-admin .vocabulary-container {
	width: 100%;
}

.portlet-asset-category-admin .vocabulary-container .results-header {
	background: #F0F5F7;
	font-weight: bold;
	margin: 2px 0;
	padding: 5px 10px;
}

.portlet-asset-category-admin .vocabulary-categories-container {
	min-width: 250px;
}

.ie6 .portlet-asset-category-admin .vocabulary-categories-container {
	width: 300px;
}

.portlet-asset-category-admin .vocabulary-list-container .results-header {
	background: #d3dadd;
}

.portlet-asset-category-admin .vocabulary-categories-container .results-header {
	background: #AEB9BE;
}

.portlet-asset-category-admin .vocabulary-edit-category .results-header {
	background: #6F7D83;
	color: #fff;
}

.portlet-asset-category-admin .vocabulary-content td {
	vertical-align: top;
}

.portlet-asset-category-admin .vocabulary-content li.vocabulary-category {
	padding: 1px 0;
}

.portlet-asset-category-admin .vocabulary-content li.vocabulary-category, .portlet-asset-category-admin li.vocabulary-item, .vocabulary-item.portlet-asset-category-admin-helper {
	border-bottom: 1px solid #D3D7DB;
	font-weight: bold;
	list-style: none;
}

.portlet-asset-category-admin .vocabulary-item.alt {
	background: #F0F2F4;
}

.portlet-asset-category-admin .vocabulary-container .results-row a, .vocabulary-item.results-row a {
	padding: 8px 0 8px 10px;
}

.portlet-asset-category-admin .vocabulary-list a {
	display: block;
	text-decoration: none;
}

.ie .portlet-asset-category-admin .vocabulary-list a {
	zoom: 1;
}

.portlet-asset-category-admin .vocabulary-item a, .vocabulary-item.portlet-asset-category-admin-helper a {
	display: block;
	padding-left: 20px;
}

.portlet-asset-category-admin .vocabulary-item.selected, .vocabulary-item.portlet-asset-category-admin-helper.selected {
	background: #aeb9be;
}

.ie6 .portlet-asset-category-admin .vocabulary-treeview-container .vocabulary-item.selected {
	background: none;
}

.portlet-asset-category-admin .vocabulary-item.selected a {
	color: #000;
	text-decoration: none;
}

.portlet-asset-category-admin .vocabulary-category-item.selected > span {
	font-weight: bold;
}

.portlet-asset-category-admin .vocabulary-list .selected a {
	background: #6F7D83;
	color: #fff;
}

.portlet-asset-category-admin .vocabulary-list .selected a:hover {
	background: #878F93;
}

.portlet-asset-category-admin .vocabulary-categories .active-area {
	background: #ffc;
}

.portlet-asset-category-admin .vocabulary-categories {
	border-right: 1px solid #D3D7DB;
	height: 300px;
	overflow: auto;
}

.portlet-asset-category-admin .vocabulary-list {
	border-left: 1px #D3D7DB solid;
	border-right: 1px #D3D7DB solid;
	height: 300px;
	overflow: auto;
	overflow-x: hidden;
}

.portlet-asset-category-admin .vocabulary-list a:hover, .portlet-asset-category-admin .vocabulary-categories a:hover {
	background: #D3DADD;
}

.portlet-asset-category-admin .vocabulary-search-bar {
	float: left;
}

.portlet-asset-category-admin .vocabulary-toolbar {
	background: #F6F8FB;
	border-bottom: 1px solid #dedede;
	overflow: hidden;
	padding: 5px 0;
}

.portlet-asset-category-admin .vocabulary-buttons {
	float: left;
	min-width: 220px;
	padding: 5px 0px 5px;
}

.portlet-asset-category-admin .vocabulary-actions {
	float: right;
}

.portlet-asset-category-admin .vocabulary-buttons .button {
	background: url(<%= themeImagesPath %>/common/page.png) no-repeat scroll 10px 50%;
	color: #9EA8AD;
	cursor: pointer;
	display: block;
	float: left;
	font-weight: bold;
	margin-right: 5px;
	min-width: 70px;
	padding: 5px 5px 5px 30px;
}

.portlet-asset-category-admin .vocabulary-buttons .selected {
	background-color: #CFD5D7;
	color: #0F0F0F;
}

.portlet-asset-category-admin .vocabulary-edit-category .vocabulary-edit {
	padding: 5px 5px 10px 10px;
}

.portlet-asset-category-admin .vocabulary-editing-tag .vocabulary-edit, .portlet-asset-category-admin .vocabulary-editing-tag .results-header {
	display: block;
}

.portlet-asset-category-admin div.vocabulary-close {
	text-align: right;
}

.portlet-asset-category-admin div.vocabulary-close span {
	cursor: pointer;
}

.portlet-asset-category-admin .vocabulary-property-row {
	white-space: nowrap;
}

.portlet-asset-category-admin .vocabulary-footer {
	border-top: 1px solid #dedede;
	margin-top: 5px;
	padding: 10px 0 0;
}

.asset-category-layer-wrapper {
	display: none;
}

.aui-widget-bd .asset-category-layer {
	padding: 10px;
	text-align: left;
}

.aui-widget-bd .asset-category-layer .aui-field-content {
	margin-bottom: 10px;
}

.aui-widget-bd .asset-category-layer label {
	display: block;
	font-weight: bold;
}

.aui-widget-bd .asset-category-layer .aui-field input, .aui-widget-bd .asset-category-layer .aui-field select {
	width: 200px;
}

.ie6 .aui-widget-bd .asset-category-layer .aui-field, .ie6 .aui-widget-bd .asset-category-layer .aui-field {
	width: 200px;
}

.aui-widget-bd .asset-category-layer .button-holder {
	margin-top: 10px;
}

.asset-category-layer .aui-overlay {
	overflow: visible;
	width: 230px;
}

.portlet-asset-category-admin #vocabulary-category-messages {
	margin: 10px;
}

.portlet-asset-category-admin .aui-tree-node-selected .aui-tree-label {
	cursor: move;
}

.portlet-asset-category-admin .vocabulary-treeview-container {
	padding: 5px;
}

.portlet-asset-category-admin .vocabulary-container .category-name {
	width: 300px;
}

.portlet-asset-category-admin #vocabulary-search-input {
	background-image: url(<%= themeImagesPath %>/common/search.png);
	background-repeat: no-repeat;
	background-position: 5px 50%;
	padding-left: 25px;
	width: 250px;
}