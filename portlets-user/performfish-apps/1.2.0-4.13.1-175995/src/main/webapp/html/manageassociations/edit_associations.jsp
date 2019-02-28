<%@include file="../init.jsp"%>

<p class="lead">
	Current associations in
	<%=vreName%>:
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

	<liferay-ui:search-container var="searchContainer" delta="5"
		deltaConfigurable="true"
		emptyResultsMessage="Sorry. There are no associations to display."
		iteratorURL="<%=iteratorURL%>" rowChecker="<%=rowChecker%>"
		orderByType="<%=orderByType%>">

		<liferay-ui:search-container-results>
			<%
				int totalAssociations = associations.size();
							List<Association> sortableAssociations = ListUtil.subList(associations, searchContainer.getStart(),
									searchContainer.getEnd());

							if (Validator.isNotNull(orderByCol)) {
								Collections.sort(sortableAssociations, new AssociationComparator());
								if (sortingOrder.equalsIgnoreCase("desc"))
									Collections.reverse(sortableAssociations);
							}
							pageContext.setAttribute("results", sortableAssociations);
							pageContext.setAttribute("total", totalAssociations);
			%>
		</liferay-ui:search-container-results>

		<liferay-ui:search-container-row
			className="org.gcube.portlets.user.performfish.bean.Association"
			modelVar="item" keyProperty="associationId">
			<liferay-ui:search-container-column-text orderable="true" name="Name">${item.shortName}</liferay-ui:search-container-column-text>
			<liferay-ui:search-container-column-text name="Id">${item.associationId}</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>
		<liferay-ui:search-iterator />
	</liferay-ui:search-container>
	<aui:input name="currentGroupId" type="hidden"	value="<%=currentGroupId%>" />

</aui:form>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />removeCompanies',
		function() {
			var removeCompanyIds = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, '<portlet:namespace />allRowIds');	
			
			if (removeCompanyIds && confirm("Are you sure you want to remove the selected company(ies)?" +
					"This action will remove also all the Farms and the Members associated to the company")) {
				document.<portlet:namespace />fm.<portlet:namespace />removeCompanyIds.value = removeCompanyIds;

				submitForm(document.<portlet:namespace />fm, '<portlet:actionURL name="removeCompanies"></portlet:actionURL>');
			} else {
				
			}
		},
		['liferay-util-list-fields']
	);
</aui:script>