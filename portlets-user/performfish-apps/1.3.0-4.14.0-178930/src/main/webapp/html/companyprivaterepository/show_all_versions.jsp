<%@include file="../init.jsp"%>
<link href="https://fonts.googleapis.com/icon?family=Material+Icons"
	rel="stylesheet">
<%
	String selectedItemId = (String) request.getAttribute("itemId");
	String selectedItemName = (String) request.getAttribute("itemName");
	pageContext.setAttribute("selectedItemName",selectedItemName);
	pageContext.setAttribute("selectedItemId",selectedItemId);
	Group group = (Group) GroupLocalServiceUtil.getGroup(PortalUtil.getScopeGroupId(request));
	long currentGroupId = group.getGroupId();
	String currentURL = PortalUtil.getCurrentURL(request);
	pageContext.setAttribute("currentURL",currentURL);
	List<WorkspaceVersion> fileVersions = (List<WorkspaceVersion>) renderRequest.getAttribute("versions");
	if (fileVersions != null)
		Collections.reverse(fileVersions);
	pageContext.setAttribute("fileVersions", fileVersions);
	String portletURLString = PortalUtil.getCurrentURL(request);
	RowChecker rowChecker = new RowChecker(renderResponse);
	String currentUsername = Utils.getCurrentUser(request).getUsername();
	PortletURL portletURL = renderResponse.createRenderURL();
	String iconHTML = IconsManager.getMDIconTextualName(selectedItemName).getHtml();
%>
<a class="btn btn-link btn-large no-padding" href="javascript: history.back();"><i
	class="icon icon-angle-left"></i>&nbsp;Back to Private Repository Content</a>
<p class="lead">Available versions for: <strong>${selectedItemName}</strong></p>

<table id="example" class="display">
			<tbody>
			<thead>
				<th colspan="2">
				Version Number</th>
				<th><span style="margin-left: 10px; font-size: 1.1em;">Created by
						</span></th>
				<th><span style="margin-left: 10px; font-size: 1.1em;">Created date (UTC)</span></th>
				<th><span style="margin-left: 10px; font-size: 1.1em;">Current</span></th>
			</thead>
			<c:forEach var="file" items="${fileVersions}">
		<tr>
			<%
			WorkspaceVersion version = (WorkspaceVersion) pageContext.getAttribute("file");
			Date created = new Date(version.getCreated().getTimeInMillis());
			pageContext.setAttribute("created", created);
			String fullName = "";
			String userProfileLink = "";
			if (version.getUser() != null) {
				 fullName = Utils.getUserByUsername(version.getUser()).getFullname();
				userProfileLink = Utils.getUserProfileLink(version.getUser());
			}
			pageContext.setAttribute("fullName", fullName);			 
			pageContext.setAttribute("userProfileLink", userProfileLink);
			%>
			<portlet:resourceURL var="downloadVersionURL">
				<portlet:param name="versionDownloadItemId" value="${selectedItemId}" />
				<portlet:param name="versionDownloadName" value="${file.name}" />
			</portlet:resourceURL>
			<td><%=iconHTML%></td>
			<td><span style="font-size: 1.1em;"><a
					href="javascript:downloadVersion('${downloadVersionURL}');"
					title="Download version ${file.name}">${file.name}</a></span></td>
			<td><span style="margin-left: 10px; font-size: 1.1em;"><a
					href="<%=userProfileLink%>" target="_blank"><%=fullName%></a></span></td>
			<td><span
				style="margin-left: 10px; font-size: 1.1em; color: #666;"><fmt:formatDate
						type="both" dateStyle="medium" timeStyle="short"
						value="${created}" /></span></td>
			<td><span style="margin-left: 10px; font-size: 1.1em;">${file.currentVersion}</span></td>
		</tr>
	</c:forEach>
			</tbody>
</table>


	

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />associateCompanyUsers',
		function() {
			var addUserIds = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, '<portlet:namespace />allRowIds');	
			
			if (addUserIds && confirm("Are you sure you want to associate the selected users to this company?")) {
				document.<portlet:namespace />fm.<portlet:namespace />addUserIds.value = addUserIds;

				submitForm(document.<portlet:namespace />fm, '<portlet:actionURL name="associateCompanyUsers"></portlet:actionURL>');
			} else {
				
			}
		},
		['liferay-util-list-fields']
	);
</aui:script>