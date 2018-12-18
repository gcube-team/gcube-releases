<%@page import="org.apache.tika.sax.ToTextContentHandler"%>
<%@include file="../init.jsp"%>

<p class="lead">Administrators currently associated to <%= theFarm.getName() %>:</p>


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

	<liferay-ui:search-container var="searchContainer" delta="5"
		deltaConfigurable="true"
		emptyResultsMessage="Sorry. There are no users to display."
		iteratorURL="<%=iteratorURL%>" rowChecker="<%=rowChecker%>"
		orderByType="<%=orderByType%>">

		<liferay-ui:search-container-results>
			<%
				int totalUsers = admins.size();
				List<GCubeUser> sortableUsers = ListUtil.subList(admins, searchContainer.getStart(),
									searchContainer.getEnd());

				if (Validator.isNotNull(orderByCol)) {
					Collections.sort(sortableUsers, new UserComparator());
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
	<button name="delete" type="button" icon="icon-delete"
		class="btn btn-warning"
		onClick='<%=renderResponse.getNamespace() + "removeAdministratorRoleFromUsers();"%>'>Remove Administrator role from selected</button>
	<aui:input name="removeUserIds" type="hidden" />
	<aui:input name="teamId" type="hidden" value="<%= theFarm.getTeamId() %>"/>
	<aui:input name="currentUsername" type="hidden" value="<%= currentUsername %>"/>
	<aui:input name="currentGroupId" type="hidden" value="<%= currentGroupId %>"/>
	<input id="totalUsers" name="no" type="hidden" value="<%= admins.size() %>"/>
</aui:form>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />removeAdministratorRoleFromUsers',
		function() {
			var removeUserIds = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, '<portlet:namespace />allRowIds');	
			var array = removeUserIds.split(',');
			var totalUsers = document.getElementById('totalUsers');

			if (array.length == totalUsers.value) {
				alert("Forbidden: You are  trying removing the role from all the users of the company, at least one must have it.")
				return;
			}
			if (removeUserIds && confirm("Are you sure you want to remove the Administrator role from the selected users?")) {
				document.<portlet:namespace />fm.<portlet:namespace />removeUserIds.value = removeUserIds;

				submitForm(document.<portlet:namespace />fm, '<portlet:actionURL name="removeAdministratorRoleFromUsers"></portlet:actionURL>');
			} else {
				
			}
		},
		['liferay-util-list-fields']
	);
</aui:script>