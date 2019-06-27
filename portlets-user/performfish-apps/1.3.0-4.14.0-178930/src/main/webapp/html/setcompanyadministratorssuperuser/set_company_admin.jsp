<%@include file="../init.jsp"%>
<%
	Boolean operationFinished = (Boolean) request.getAttribute("operationFinished");
	//
	
if (!operationFinished) {

	String portletURLString = PortalUtil.getCurrentURL(request);
	RowChecker rowChecker = new RowChecker(renderResponse);
	String currentUsername = Utils.getCurrentUser(request).getUsername();
	Team team = (Team) request.getAttribute("theTeam");
	Group group = (Group) GroupLocalServiceUtil.getGroup(PortalUtil.getScopeGroupId(request));
	long currentGroupId = group.getGroupId();
	String currentURL = PortalUtil.getCurrentURL(request);
	//this is the list to be working on manage
	List<GCubeUser> siteUsers = (List<GCubeUser>) request.getAttribute("availableUsers");
	pageContext.setAttribute("logoURL", renderRequest.getAttribute("companyLogoURL"));
	pageContext.setAttribute("companyName", team.getName());
%>


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
					style="font-size: 1.5em; padding-top: 12px; padding-left: 5px;">${companyName}
				</p></td>
		</tr>
	</table>
</div>
<p class="lead">
	Users currently associated to
	<%=group.getName()%>
	that can be set as Company Administrators (They do not belong to any
	other company yet):
</p>


<liferay-portlet:renderURL varImpl="iteratorURL">
</liferay-portlet:renderURL>

<%
	String orderByCol = ParamUtil.getString(request, "orderByCol");
	String orderByType = ParamUtil.getString(request, "orderByType");
	String sortingOrder = orderByType;

	//Logic for toggle asc and desc
	if (orderByType.equals("desc"))
		orderByType = "asc";
	else
		orderByType = "desc";
	if (Validator.isNull(orderByType)) {
		orderByType = "asc";
	}
%>

<aui:form action="<%=portletURLString%>" method="post" name="fm">
	<button name="associate" type="button" class="btn btn-primary"
		onClick='<%=renderResponse.getNamespace() + "promoteAdminUsers();"%>'>Promote
		selected</button>
	<liferay-ui:search-container var="searchContainer" delta="100"
		deltaConfigurable="true"
		emptyResultsMessage="Sorry. There are no users to display."
		iteratorURL="<%=iteratorURL%>" rowChecker="<%=rowChecker%>"
		orderByType="<%=orderByType%>">

		<liferay-ui:search-container-results>
			<%
				int totalUsers = siteUsers.size();
							List<GCubeUser> sortableUsers = ListUtil.subList(siteUsers, searchContainer.getStart(),
									searchContainer.getEnd());

							if (Validator.isNotNull(orderByCol)) {
								Collections.sort(sortableUsers, new org.gcube.portlets.user.performfish.util.comparators.UserComparator());
								if (sortingOrder.equalsIgnoreCase("desc"))
									Collections.reverse(sortableUsers);
							}
							pageContext.setAttribute("results", sortableUsers);
							pageContext.setAttribute("total", totalUsers);
			%>
		</liferay-ui:search-container-results>

		<liferay-ui:search-container-row
			className="org.gcube.vomanagement.usermanagement.model.GCubeUser"
			modelVar="item" keyProperty="userId">
			<liferay-ui:search-container-column-text orderable="true" name="Name">${item.fullname}</liferay-ui:search-container-column-text>
			<liferay-ui:search-container-column-text name="Email">${item.email}</liferay-ui:search-container-column-text>
			<liferay-ui:search-container-column-text name="Position">${item.jobTitle}</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>
		<liferay-ui:search-iterator />
	</liferay-ui:search-container>
	<button name="associate" type="button" class="btn btn-primary"
		onClick='<%=renderResponse.getNamespace() + "promoteAdminUsers();"%>'>Promote
		selected</button>
	<aui:input name="addUserIds" type="hidden" />
	<aui:input name="currentUsername" type="hidden"
		value="<%=currentUsername%>" />
	<aui:input name="currentGroupId" type="hidden"
		value="<%=currentGroupId%>" />
	<aui:input name="companyId" type="hidden"
		value="<%=team.getTeamId()%>" />

</aui:form>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />promoteAdminUsers',
		function() {
			var addUserIds = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, '<portlet:namespace />allRowIds');	
			if (addUserIds && confirm("Are you sure you want to promote ${companyName} Administrator the selected user(s)?")) {
				document.<portlet:namespace />fm.<portlet:namespace />addUserIds.value = addUserIds;

				submitForm(document.<portlet:namespace />fm, '<portlet:actionURL name="promoteAdminUsers"></portlet:actionURL>');
			} else {
				
			}
		},
		['liferay-util-list-fields']
	);
</aui:script>
<% } //enf if (operationOK != null  && operationOK.compareTo("") != 0) {
else {	
%>
<% if (operationFinished) {
	out.println("Company admin(s) set correctly.");
}%>
<% } %>