<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<input type="hidden" value="${catalogueURL}" id="catalogueURL">
<div id="catalogueDiv">
	<div class="input-append input-catalogue">
		<input type="text" class="input-large" name="q" value=""
			autocomplete="off" placeholder="Insert keywords here" id="inputQueryCatalogue">
		<button class="btn btn-primary" type="button" title="Search" id="searchCatalogueButton">
			<i class="icon-search"></i> <span>Search</span>
		</button>
	</div>
	<div class="catstats">
		<ul>
			<li><a href="${catalogueURL}?path=/dataset/"> <b><span>${itemsNo}</span></b> items
			</a></li>
			<li><a href="${catalogueURL}?path=/organization/"> <b><span>${organisationsNo}</span></b>
					organisation
			</a></li>
			<li><a href="${catalogueURL}?path=/group/"> <b><span>${groupsNo}</span></b> groups
			</a></li>
			<li><a href="${catalogueURL}?path=/type/"> <b><span>${typesNo}</span></b> types
			</a></li>
		</ul>
	</div>
</div>