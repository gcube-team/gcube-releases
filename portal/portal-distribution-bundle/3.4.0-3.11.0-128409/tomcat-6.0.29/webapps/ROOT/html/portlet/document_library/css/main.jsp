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

.portlet-document-library .file-entry-list-description {
	font-style: italic;
	margin-left: 10px;
}

.portlet-document-library .file-entry-tags {
	margin-top: 5px;
}

.portlet-document-library .folder-search {
	float: right;
	margin: 0 0 0.5em 0.5em;
}

.portlet-document-library img.shortcut-icon {
	display: inline;
	margin-left: 10px;
	margin-top: 75px;
	position: absolute;
	z-index: 10;
}

.portlet-document-library img.locked-icon {
	display: inline;
	margin: 95px 0 0 130px;
	position: absolute;
	z-index: 10;
}

.portlet-document-library .taglib-webdav {
	margin-top: 3em;
}

.portlet-document-library .taglib-workflow-status {
	margin-bottom: 5px;
}

.portlet-document-library .workflow-status-pending, .portlet-document-library .workflow-status-pending a {
	color: orange;
}