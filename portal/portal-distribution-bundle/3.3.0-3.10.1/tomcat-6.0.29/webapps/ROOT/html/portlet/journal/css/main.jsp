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

.journal-article-component-list {
	margin: 10px 0 0 0;
}

.portlet-journal .add-permission-button-row {
	float: left;
}

.portlet-journal .article-separator {
	clear: both;
}

.portlet-journal .subscribe-link {
	float: right;
	margin-bottom: 1em;
	margin-top: 2em;
}

.journal-template-error .scroll-pane {
	border: 1px solid #BFBFBF;
	max-height: 200px;
	min-height: 50px;
	overflow: auto;
	width: 100%;
}

.journal-template-error .scroll-pane .inner-scroll-pane {
	min-width: 104%;
}

.journal-template-error .scroll-pane .error-line {
	background: #fdd;
}

.journal-template-error .scroll-pane pre {
	margin: 0;
	white-space: pre;
}

.journal-template-error .scroll-pane pre span {
	background: #B5BFC4;
	border-right: 1px solid #BFBFBF;
	display: block;
	float: left;
	margin-right: 4px;
	padding-right: 4px;
	text-align: right;
	width: 40px;
}

.portlet-journal .lfr-panel-basic .lfr-panel-title {
	border-bottom: 1px solid #ccc;
	float: none;
	position: relative;
	top: -0.5em;
}

.portlet-journal .lfr-panel-basic .lfr-panel-title span {
	background: #fff;
	padding: 0 8px 0 4px;
	position: relative;
	top: 0.55em;
}

.portlet-journal .lfr-panel-basic .lfr-panel-content {
	background-color: #F0F5F7;
	padding: 10px;
}

.portlet-journal .lfr-panel-basic .lfr-tag-selector-input {
	width: 100%;
}

.portlet-journal .journal-extras {
	border-width: 0;
}

.portlet-journal .journal-extras .lfr-panel {
	margin-bottom: 1em;
}

.portlet-journal .journal-article-container ul {
	margin: 0;
}

.portlet-journal li {
	list-style: none;
}

.portlet-journal .structure-tree {
	position: relative;
}

.portlet-journal-edit-mode .structure-tree li.structure-field.repeated-field {
	background: #F7FAFB;
	border: 1px dashed #C6D9F0;
}

.portlet-journal .structure-tree li.structure-field.repeatable-border {
	background: #F7FAFB;
}

.portlet-journal-edit-mode .structure-tree li.structure-field.repeated-field.selected {
	border: 1px dashed #C3E7CC;
}

.portlet-journal-edit-mode .structure-tree span.folder, .portlet-journal-edit-mode .structure-tree span.file {
	display: block;
	padding: 10px;
	padding-top: 0;
}

.portlet-journal .structure-tree .placeholder {
	display: none;
}

.portlet-journal .component-group .aui-tree-placeholder {
	display: none;
}

.portlet-journal-edit-mode .structure-tree .aui-placeholder, .portlet-journal-edit-mode .structure-tree .aui-tree-placeholder, .portlet-journal-edit-mode .structure-tree .aui-tree-sub-placeholder {
	-ms-filter: alpha(opacity=75);
	background: #fff;
	border: 1px #cdcdcd dashed;
	filter: alpha(opacity=75);
	height: 100px;
	opacity: 0.75;
}

.portlet-journal .structure-tree .aui-tree-sub-placeholder {
	margin-top: 10px;
}

.portlet-journal .structure-tree li {
	border-top: 1px solid #CCC;
	margin: 10px;
	padding-top: 5px;
	position: relative;
}

.portlet-journal-edit-mode .structure-tree li {
	background: url(<%= themeImagesPath %>/journal/form_builder_bg.png);
	border: 1px #C6D9F0 solid;
	margin: 15px;
	padding: 10px 10px 10px 22px;
}

.portlet-journal-edit-mode .structure-tree li.parent-structure-field {
	background: none;
	background-color: #FFFFE6;
	border: 1px dotted #FFE67F;
	padding-bottom: 30px;
}

.ie .portlet-journal .structure-tree li {
	zoom: 1;
}

.portlet-journal .structure-tree .folder .field-container .journal-article-required-message {
	display: none;
}

.portlet-journal .structure-tree .folder .field-container.required-field .journal-article-required-message {
	display: block;
	margin: 0;
}

.portlet-journal .journal-article-localized-checkbox {
	display: block;
	margin-top: 10px;
}

.portlet-journal .localization-disabled .journal-article-language-options, .portlet-journal .localization-disabled .structure-field .journal-article-localized-checkbox, .portlet-journal-edit-mode .portlet-journal .structure-field .journal-article-localized-checkbox {
	display: none;
}

.portlet-journal .journal-article-header-edit .journal-article-localized-checkbox {
	margin-bottom: 10px;
}

.portlet-journal-edit-mode .structure-tree li.structure-field.selected {
	background: #EBFFEE;
	border: 1px #C3E7CC solid;
}

.portlet-journal .structure-tree .journal-article-field-label {
	display: block;
	font-weight: bold;
	margin-left: 3px;
	padding-bottom: 5px;
}

.portlet-journal .structure-tree .journal-subfield input {
	float: left;
}

.portlet-journal .structure-tree .journal-subfield .journal-article-field-label {
	float: left;
	font-weight: normal;
	padding: 0 0 0 3px;
}

.portlet-journal .structure-tree .journal-article-move-handler {
	display: none;
}

.portlet-journal-edit-mode .structure-tree li.structure-field.yui3-dd-draggable .journal-article-move-handler {
	background: transparent url(<%= themeImagesPath %>/application/handle_sort_vertical.png) no-repeat scroll right 50%;
	cursor: move;
	display: block;
	height: 20px;
	left: 7px;
	position: absolute;
	top: 8px;
	width: 16px;
	z-index: 420;
}

.portlet-journal .structure-tree .journal-article-localized {
	font-size: 13px;
	padding-top: 5px;
}

.portlet-journal-edit-mode .structure-tree li.structure-field .journal-article-buttons {
	display: block;
	height: 27px;
	margin-top: 18px;
	text-align: right;
}

.portlet-journal .journal-article-buttons, .portlet-journal .structure-tree li.structure-field.repeated-field .journal-article-variable-name {
	display: none;
}

.portlet-journal .journal-article-buttons .edit-button, .portlet-journal .journal-article-buttons .repeatable-button {
	float: left;
	margin-left: 3px;
}

.portlet-journal .journal-article-variable-name .aui-field-label {
	font-weight: normal;
	margin-right: 5px;
}

.portlet-journal .component-group-title {
	font-size: 12px;
	font-weight: bold;
	text-decoration: underline;
	padding: 4px 0 0;
}

.portlet-journal .journal-article-component-container {
	margin: 3px;
	overflow: hidden;
}

.portlet-journal .journal-component {
	color: #0E3F6F;
	cursor: move;
	line-height: 25px;
	padding-left: 30px;
}

.portlet-journal .journal-component.dragging {
	font-weight: bold;
}

.portlet-journal .journal-article-instructions-container {
	display: normal;
}

.journal-component {
	background: transparent url() no-repeat scroll 3px 3px;
}

.journal-component-text {
	background-image: url(<%= themeImagesPath %>/journal/text_field.png);
	background-position: 3px 9px;
}

.journal-component-textbox {
	background-image: url(<%= themeImagesPath %>/journal/textbox.png);
	background-position: 3px 6px;
}

.journal-component-textarea {
	background-image: url(<%= themeImagesPath %>/journal/textarea.png);
	background-position: 3px 4px;
}

.journal-component-image {
	background-image: url(<%= themeImagesPath %>/journal/image_uploader.png);
	background-position: 3px 7px;
}

.journal-component-imagegallery {
	background-image: url(<%= themeImagesPath %>/journal/image_gallery.png);
	background-position: 3px 5px;
}

.journal-component-documentlibrary {
	background-image: url(<%= themeImagesPath %>/journal/document_library.png);
}

.journal-component-boolean {
	background-image: url(<%= themeImagesPath %>/journal/checkbox.png);
	background-position: 3px 7px;
}

.journal-component-options {
	background-image: url(<%= themeImagesPath %>/journal/options.png);
	background-position: 3px 5px;
}

.journal-component-list {
	background-image: url(<%= themeImagesPath %>/journal/selectbox.png);
	background-position: 3px 9px;
}

.journal-component-multilist {
	background-image: url(<%= themeImagesPath %>/journal/multiselection_list.png);
	background-position: 3px 4px;
}

.journal-component-linktolayout {
	background-image: url(<%= themeImagesPath %>/journal/link_to_page.png);
	background-position: 3px 9px;
}

.journal-component-formgroup {
	background-image: url(<%= themeImagesPath %>/journal/form_group.png);
	background-position: 3px 5px;
}

.journal-component-selectionbreak {
	background-image: url(<%= themeImagesPath %>/journal/selection_break.png);
	background-position: 3px 12px;
}

.journal-article-helper .journal-component {
	height: 25px;
	line-height: 25px;
	margin-left: 5px;
	padding-left: 25px;
}

.component-group .component-dragging {
	background-color: #fff !important;
}

.component-group.form-controls {
	border-top: 1px solid #E0ECFF;
}

.portlet-journal .journal-field-template {
	display: none;
}

.portlet-journal .journal-fieldmodel-container {
	display: none;
}

.portlet-journal .journal-icon-button {
	cursor: pointer;
}

.portlet-journal-edit-mode .structure-tree li.structure-field .journal-article-close {
	background: url(<%= themeImagesPath %>/journal/form_builder_close.png);
	cursor: pointer;
	display: block;
	height: 29px;
	position: absolute;
	right: -10px;
	top: -9px;
	width: 29px;
	z-index: 420;
}

.journal-article-helper {
	background: #dedede;
	border: 1px #555 dashed;
	cursor: move;
	opacity: 0.8;
	position: absolute;
	visibility: hidden;
	width: 100px;
}

.ie .journal-article-helper {
	-ms-filter: alpha(opacity=80);
}

.ie6 .journal-article-helper, .ie7 .journal-article-helper {
	filter: alpha(opacity=80);
}

.journal-article-helper.aui-draggable-dragging {
	font-size: 15px;
}

.journal-article-helper.not-intersecting .forbidden-action {
	background: url(<%= themeImagesPath %>/application/forbidden_action.png) no-repeat;
	height: 32px;
	position: absolute;
	right: -15px;
	top: -15px;
	width: 32px;
}

.ie .journal-article-helper.not-intersecting .forbidden-action {
	right: 2px;
	top: 2px;
}

.portlet-journal .portlet-section-header td {
	background: #CFE5FF;
}

.portlet-journal .journal-form-presentation-label {
	color: #0E3F6F;
	padding-top: 3px;
}

.portlet-journal .journal-edit-field-control, .portlet-journal .journal-list-subfield .journal-icon-button {
	display: none;
}

.portlet-journal-edit-mode .structure-tree li.structure-field .journal-edit-field-control {
	display: block;
}

.portlet-journal-edit-mode .journal-list-subfield .journal-icon-button {
	display: inline;
}

.portlet-journal .journal-icon-button span img {
	margin-bottom: 3px;
}

.portlet-journal .journal-article-instructions-message {
	margin: 5px 0 0 0;
}

.ie .journal-article-edit-field-wrapper form {
	width: auto;
}

.journal-article-edit-field-wrapper.aui-overlaycontextpanel-container,
.journal-article-edit-field-wrapper .aui-overlaycontextpanel-container {

	background-color: #EBFFEE;
	border-color: #C3E7CC;
}

.journal-article-edit-field-wrapper.aui-overlaycontextpanel {
	margin: 0 13px 0 0;
	padding: 0;
	position: relative;
}

.journal-article-edit-field-wrapper.aui-overlaycontextpanel-arrow-tl .aui-overlaycontextpanel-pointer-inner {
	border-bottom: 10px solid #EBFFEE;
}

.journal-article-edit-field-wrapper .container-close {
	display: none;
}

.journal-article-edit-field {
	padding: 5px;
	position: relative;
	width: 180px;
	z-index: 420;
}

.journal-article-edit-field-wrapper .cancel-button, .journal-article-edit-field-wrapper .save-button {
	display: none;
}

.journal-article-edit-field-wrapper.save-mode .close-button {
	display: none;
}

.journal-article-edit-field-wrapper.save-mode .save-button, .journal-article-edit-field-wrapper.save-mode .cancel-button {
	display: inline;
}

.journal-article-edit-field .aui-field {
	padding: 0;
}

.journal-article-edit-field strong {
	font-size: 14px;
	text-decoration: underline;
}

.journal-article-edit-field .journal-edit-label {
	margin-top: 10px;
}

.journal-article-edit-field .aui-field .textarea {
	height: 6em;
}

.structure-message {
	margin-top: 5px;
}

.structure-name-label {
	font-weight: bold;
}

.save-structure-template-dialog textarea {
	height: 150px;
	width: 450px;
}

.save-structure-name {
	width: 470px;
}

.save-structure-description {
	height: 150px;
	width: 470px;
}

.portlet-journal .structure-controls {
	margin-top: 5px;
}

.portlet-journal .structure-links {
	display: block;
	margin-top: 5px;
}

.portlet-journal .structure-links a {
	margin-right: 10px;
}

.portlet-journal .default-link {
	font-size: 0.9em;
	font-weight: normal;
}

.portlet-journal .journal-image-preview {
	border: 1px dotted;
	margin-top: 2px;
	overflow: scroll;
	padding: 4px;
	width: 500px;
}

.journal-article-edit-field-wrapper .user-instructions {
	border-width: 0;
	padding: 0;
	margin-bottom: 1em;
}

.journal-article-edit-field-wrapper .button-holder {
	margin-top: 1.5em;
}

.portlet-journal .repeatable-field-image {
	cursor: pointer;
	position: absolute;
	right: 0;
	top: 0;
}

.portlet-journal .lfr-textarea {
	width: 350px;
}

.portlet-journal-edit-mode .journal-article-header-edit, .portlet-journal-edit-mode #journalAbstractPanel, .portlet-journal-edit-mode #journalCategorizationPanel, .portlet-journal-edit-mode #journalSchedulePanel, .portlet-journal-edit-mode .journal-article-button-row, .portlet-journal-edit-mode .panel-page-menu, .portlet-journal-edit-mode .journal-article-permissions, .portlet-journal-edit-mode .repeatable-field-image, .portlet-journal-edit-mode .structure-tree li.structure-field .journal-article-instructions-container, .portlet-journal-edit-mode .structure-tree li.parent-structure-field .journal-article-close, .portlet-journal-edit-mode .structure-tree li.parent-structure-field .journal-delete-field, .portlet-journal-edit-mode .structure-tree li.repeated-field .journal-edit-field-control, .portlet-journal-edit-mode .structure-tree li.repeated-field .journal-delete-field {
	display: none;
}

.portlet-journal-edit-mode .panel-page-application {
	float: none;
	width: 100%;
}