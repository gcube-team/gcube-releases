<%@include file="../init.jsp"%>
<%
	String portletURLString = PortalUtil.getCurrentURL(request);
	RowChecker rowChecker = new RowChecker(renderResponse);
	String currentUsername = Utils.getCurrentUser(request).getUsername();
	String vreName = (String) renderRequest.getAttribute("vreName");
	pageContext.setAttribute("vreName", vreName);
	Group group = (Group) GroupLocalServiceUtil.getGroup(PortalUtil.getScopeGroupId(request));
	long currentGroupId = group.getGroupId();
	String currentURL = PortalUtil.getCurrentURL(request);
	//this is the list to be working on manage
	List<GCubeTeam> availableTeams = (List<GCubeTeam>) request.getAttribute("availableTeams");
%>

<a class="btn btn-link btn-large no-padding" href="control-centre"><i
	class="icon icon-angle-left"></i>&nbsp;Back to manage companies</a>

<p class="lead">Available teams to promote as Company in ${vreName }:</p>


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
		emptyResultsMessage="Sorry. There are no available teams to display."
		iteratorURL="<%=iteratorURL%>" rowChecker="<%=rowChecker%>"
		orderByType="<%=orderByType%>">

		<liferay-ui:search-container-results>
			<%
				int totalteams = availableTeams.size();
							List<GCubeTeam> sortableTeams = ListUtil.subList(availableTeams, searchContainer.getStart(),
									searchContainer.getEnd());

							if (Validator.isNotNull(orderByCol)) {
								Collections.sort(sortableTeams, new TeamComparator());
								if (sortingOrder.equalsIgnoreCase("desc"))
									Collections.reverse(sortableTeams);
							}
							pageContext.setAttribute("results", sortableTeams);
							pageContext.setAttribute("total", totalteams);
			%>
		</liferay-ui:search-container-results>

		<liferay-ui:search-container-row
			className="org.gcube.vomanagement.usermanagement.model.GCubeTeam"
			modelVar="item" keyProperty="teamId">
			<liferay-ui:search-container-column-text orderable="true" name="Name">${item.teamName}</liferay-ui:search-container-column-text>
			<liferay-ui:search-container-column-text name="Id">${item.teamId}</liferay-ui:search-container-column-text>
			<liferay-ui:search-container-column-text name="Other info">${item.description}</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>
		<liferay-ui:search-iterator />
	</liferay-ui:search-container>
	<button name="associate" type="button" class="btn btn-primary"
		onClick='<%=renderResponse.getNamespace() + "associateCompanies();"%>'>Associate
		selected</button>
	<aui:input name="addCompanyIds" type="hidden" />
	<aui:input name="currentUsername" type="hidden"
		value="<%=currentUsername%>" />
		<aui:input name="groupId" type="hidden"
		value="<%=currentGroupId%>" />
</aui:form>
<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />associateCompanies',
		function() {
			var addCompanyIds = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, '<portlet:namespace />allRowIds');	
			
			if (addCompanyIds && confirm("Are you sure you want to add the selected company?")) {
				document.<portlet:namespace />fm.<portlet:namespace />addCompanyIds.value = addCompanyIds;

				submitForm(document.<portlet:namespace />fm, '<portlet:actionURL name="associateCompanies"></portlet:actionURL>');
			} else {
				
			}
		},
		['liferay-util-list-fields']
	);
</aui:script>