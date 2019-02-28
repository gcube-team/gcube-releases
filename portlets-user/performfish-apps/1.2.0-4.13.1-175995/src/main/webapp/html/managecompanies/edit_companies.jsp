<%@include file="../init.jsp"%>

<p class="lead">
	Registered companies in
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
		emptyResultsMessage="Sorry. There are no companies to display."
		iteratorURL="<%=iteratorURL%>" rowChecker="<%=rowChecker%>"
		orderByType="<%=orderByType%>">

		<liferay-ui:search-container-results>
			<%
				int totalCompanies = companies.size();
							List<Company> sortableCompanies = ListUtil.subList(companies, searchContainer.getStart(),
									searchContainer.getEnd());

							if (Validator.isNotNull(orderByCol)) {
								Collections.sort(sortableCompanies, new CompanyComparator());
								if (sortingOrder.equalsIgnoreCase("desc"))
									Collections.reverse(sortableCompanies);
							}
							pageContext.setAttribute("results", sortableCompanies);
							pageContext.setAttribute("total", totalCompanies);
			%>
		</liferay-ui:search-container-results>

		<liferay-ui:search-container-row
			className="org.gcube.portlets.user.performfish.bean.Company"
			modelVar="item" keyProperty="companyId">
			<liferay-ui:search-container-column-text orderable="true" name="Name">${item.name}</liferay-ui:search-container-column-text>
			<liferay-ui:search-container-column-text name="Id">${item.companyId}</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>
		<liferay-ui:search-iterator />
	</liferay-ui:search-container>
	<button name="delete" type="button" icon="icon-delete" class="btn btn-warning"
		onClick='<%=renderResponse.getNamespace() + "removeCompanies();"%>'>Remove
		selected</button>	
	<aui:input name="removeCompanyIds" type="hidden" />
	<aui:input name="currentGroupId" type="hidden"
		value="<%=currentGroupId%>" />

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