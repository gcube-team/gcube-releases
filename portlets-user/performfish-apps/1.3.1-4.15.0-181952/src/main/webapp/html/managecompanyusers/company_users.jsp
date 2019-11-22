<%@include file="../init.jsp"%>

<%
	List<CompanyMember> members = (List<CompanyMember>) renderRequest.getAttribute("companyMembers");
	pageContext.setAttribute("companyMembers", members);
	pageContext.setAttribute("logoURL", renderRequest.getAttribute("companyLogoURL"));
	String portletURLString = PortalUtil.getCurrentURL(request);
	RowChecker rowChecker = new RowChecker(renderResponse);
	String currentUsername = Utils.getCurrentUser(request).getUsername();
	Team team = (Team) request.getAttribute("theTeam");
	pageContext.setAttribute("companyName", team.getName());
	Group group = (Group) GroupLocalServiceUtil.getGroup(PortalUtil.getScopeGroupId(request));
	long currentGroupId = group.getGroupId();
	String currentURL = PortalUtil.getCurrentURL(request);
	//this is the list to be working on manage
	List<CompanyMember> teamUsers = members;
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
					<img class="logo-circular" src="${logoURL}" />
				</td>
			</c:if>
			<td><p
					style="font-size: 1.5em; padding-top: 12px; padding-left: 5px;">${companyName}
					Users</p></td>
		</tr>
	</table>
</div>
<c:choose>
	<c:when test="${not maximised}">
		<div style="width: 100%; text-align: left; color: #3B5998;">
			<table id="example" class="display">
				<tbody>
					<c:forEach var="member" items="${companyMembers}">
						<%
							CompanyMember theUser = (CompanyMember) pageContext.getAttribute("member");
										String userProfileLink = Utils.getUserProfileLink(theUser.getUsername());
						%>
						<tr>
							<td><img src="${member.userAvatarURL}"
								style="width: 24px; padding-right: 10px;"></td>
							<td><span style="font-size: 1.3em;"><a
									href="<%=userProfileLink%>">${member.fullname}</a></span></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div style="padding: 15px 0;">
				<a class="btn btn-primary" href="${maximizedState}">Manage
					regular users</a>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<a class="btn btn-link btn-large no-padding" href="${normalState}"><i
			class="icon icon-angle-left"></i>&nbsp;Back to Dashboard</a>
		<a class="btn btn-link btn-large" href="manage-company-users"><i
			class="icon icon-angle-right"></i>&nbsp;Associate new users to the
			Company</a>

		<%@include file="/html/editcompanyusers/edit_company_users.jsp"%>
	</c:otherwise>
</c:choose>


