<%@include file="../init.jsp"%>
<link href="https://fonts.googleapis.com/icon?family=Material+Icons"
	rel="stylesheet">
<%
	List<WorkspaceItem> companyFiles = (List<WorkspaceItem>) renderRequest.getAttribute("companyFiles");
	pageContext.setAttribute("companyFiles", companyFiles);
	pageContext.setAttribute("logoURL", renderRequest.getAttribute("companyLogoURL"));
	String portletURLString = PortalUtil.getCurrentURL(request);
	RowChecker rowChecker = new RowChecker(renderResponse);
	String currentUsername = Utils.getCurrentUser(request).getUsername();
	GCubeTeam theCompany = (GCubeTeam) request.getAttribute("theCompany");
	pageContext.setAttribute("theCompanyName", theCompany.getTeamName());
	GCubeTeam theFarm = (GCubeTeam) request.getAttribute("theFarm");
	if (theFarm != null) {
		pageContext.setAttribute("farmyName", theFarm.getTeamName());
		pageContext.setAttribute("farmId", theFarm.getTeamId());
	}
	Group group = (Group) GroupLocalServiceUtil.getGroup(PortalUtil.getScopeGroupId(request));
	long currentGroupId = group.getGroupId();
	String currentURL = PortalUtil.getCurrentURL(request);
%>

<portlet:renderURL var="maximizedState"
	windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>" />
<portlet:renderURL var="normalState"
	windowState="<%=LiferayWindowState.NORMAL.toString()%>" />
<c:set var="maximised" scope="session"
	value="${renderRequest.getWindowState().toString().equalsIgnoreCase('maximized')}" />
<c:if test="${not empty farmyName}">
	<div style="text-align: center; padding-bottom: 15px;">
		<table>
			<tr>
				<c:if test="${not empty logoURL}">
					<td>
						<div class="logo-circular"
							style="background-image: url('${logoURL}');
	 background-size: cover; "></div>
					</td>
				</c:if>
				<td><p
						style="font-size: 1.5em; padding-top: 12px; padding-left: 5px;">${theCompanyName} - ${farmyName} Private Repository Content</p></td>
			</tr>
		</table>
	</div>
	<div style="width: 100%; text-align: left;">
		<portlet:actionURL var="displayVersionsURL" name="displayVersions"></portlet:actionURL>
		<c:choose>
			<c:when test="${empty companyFiles}">
				<p style="font-size: 1.2em; padding-top: 12px; padding-left: 5px;">The
					repository is empty at the moment. You can submit files via the dedicated pages in the VRE (Hatchery, Pre-ongrowing and Grow out)</p>
			</c:when>
			<c:otherwise>
				<form action="${displayVersionsURL}" method="post" id="form_versions" name="fm">
					<table id="example" class="display">
						<tbody>
						<thead>
							<th>Sel.</th>
							<th></th>
							<th>Name</th>
							<th><span style="margin-left: 10px; font-size: 1.1em;">Uploaded
									by</span></th>
							<th><span style="margin-left: 10px; font-size: 1.1em;">Last
									Updated (UTC)</span></th>
						</thead>
						<c:forEach var="file" items="${companyFiles}">
							<tr>
								<portlet:resourceURL var="downloadFileURL">
											<portlet:param name="fileToDownloadId" value="${file.id}" />
								</portlet:resourceURL>
								<%
								ExternalFile item = (ExternalFile) pageContext.getAttribute("file");
								String iconHTML = IconsManager.getMDIconTextualName(item.getName()).getHtml();
								String fullName = Utils.getUserByUsername(item.getCurrentVersion().getUser()).getFullname();
								String userProfileLink = Utils.getUserProfileLink(item.getLastUpdatedBy());
								Date lastUpdated = new Date(item.getLastModificationTime().getTimeInMillis());
								pageContext.setAttribute("lastUpdated", lastUpdated);
							%>
								<td><input type="radio" name="fileItem" value="${file.id}" id="${downloadFileURL}"
									style="margin: 4px 5px 10px;"></td>
								<td><%=iconHTML%></td>
								<td><span style="font-size: 1.1em;"><a
										href="javascript:downloadItem('${downloadFileURL}');"
										title="Download latest version of ${file.name}">${file.name}</a></span></td>
								<td><span style="margin-left: 10px; font-size: 1.1em;"><a
										href="<%=userProfileLink%>" target="_blank"><%=fullName%></a></span></td>
								<td><span
									style="margin-left: 10px; font-size: 1.1em; color: #666;"><fmt:formatDate
											type="both" dateStyle="medium" timeStyle="short"
											value="${lastUpdated}" /></span></td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
					<div style="padding: 15px 0;">
						<aui:input name="farmId" type="hidden" value="${farmId} "/>
						<button id="downloadButton" type="button" 
							class="btn btn-primary"
							onClick="downloadItem(getSelectedRadioFileId());">Download
							latest version</button>
	
						<portlet:renderURL var="showVersionsURL">
							<portlet:param name="jspPage"
								value="/html/farmrepository/show_all_versions.jsp" />
						</portlet:renderURL>
						<button class="btn" type="button" onClick="validateForm()">See all versions</button>
					</div>
				</form>
			</c:otherwise>
		</c:choose>
	</div>
</c:if>


