<%@include file="../init.jsp"%>

<%
	Boolean isCompanyAdministrator = false;	
	isCompanyAdministrator = (Boolean) renderRequest.getAttribute("isCompanyAdministrator");
	pageContext.setAttribute("isCompanyAdministrator", isCompanyAdministrator);
	List<GCubeUser> admins = (List<GCubeUser>) renderRequest.getAttribute("farmAdmins");
	pageContext.setAttribute("adminMembers", admins);
	pageContext.setAttribute("logoURL", renderRequest.getAttribute("companyLogoURL"));
	
	String portletURLString = PortalUtil.getCurrentURL(request);
	RowChecker rowChecker = new RowChecker(renderResponse);
	String currentUsername = Utils.getCurrentUser(request).getUsername();
	GCubeTeam theCompany = (GCubeTeam) request.getAttribute("theCompany");
	pageContext.setAttribute("theCompanyName", theCompany.getTeamName());
	
	Team theFarm = (Team) request.getAttribute("theFarm");
	pageContext.setAttribute("farmName", theFarm.getName());
	pageContext.setAttribute("theFarmId", theFarm.getTeamId());
	
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
					style="font-size: 1.5em; padding-top: 12px; padding-left: 5px;">${theCompanyName} - ${farmName}
					Administrators</p></td>
		</tr>
	</table>
</div>
<c:choose>
	<c:when test="${not maximised}">
		<div style="width: 100%; text-align: left; color: #3B5998;">

			<table id="example" class="display">
				<tbody>
					<c:forEach var="member" items="${adminMembers}">
						<tr>
							<%
							GCubeUser theUser = (GCubeUser) pageContext.getAttribute("member");
							String userProfileLink = Utils.getUserProfileLink(theUser.getUsername());
							%>
							<td><img src="${member.userAvatarURL}"
								style="width: 24px; padding-right: 10px;"></td>
							<td><span style="font-size: 1.3em;"><a
									href="<%= userProfileLink %>">${member.fullname}</a></span></td>

						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div style="padding: 15px 0; ">
				<c:choose>
					<c:when test="${isCompanyAdministrator}">
						<a class="btn btn-primary" href="${maximizedState}&farmId=${theFarmId}">Manage Farm Administrators</a>
					</c:when>
					<c:otherwise>
					<a class="btn disabled" onClick="alert('Enabled to Company Administrators only');">Manage Farm Administrators</a>
					</c:otherwise>
				</c:choose>
				
			</div>
	</c:when>
	<c:otherwise>
		<a class="btn btn-link btn-large no-padding" href="${normalState}"><i
			class="icon icon-angle-left"></i>&nbsp;Back to Dashboard</a>
		
		<a class="btn btn-link btn-large" href="promote-farm-administrators?farmId=${theFarmId}"><i
			class="icon icon-angle-right"></i>&nbsp;Promote New Farm Administrators</a>

		<%@include file="/html/editfarmusers/edit_farmadmin_users.jsp"%>
	</c:otherwise>
</c:choose>


