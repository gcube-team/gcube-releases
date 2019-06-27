<%@include file="../init.jsp"%>
<%
Boolean operationFinished = (Boolean) request.getAttribute("operationFinished");
if (!operationFinished) {
	Team selectedAssociation = (Team) renderRequest.getAttribute("selectedAssociation");
	pageContext.setAttribute("selectedAssociation", selectedAssociation);
	List<Company> companies  = (List<Company> ) renderRequest.getAttribute("companies");
	pageContext.setAttribute("companies", companies);
	String vreName = (String) renderRequest.getAttribute("vreName");
	pageContext.setAttribute("vreName", vreName);
	RowChecker rowChecker = new RowChecker(renderResponse);
	String portletURLString = PortalUtil.getCurrentURL(request);
	String currentGroupId = PortalUtil.getScopeGroupId(request)+"";
%>

<p class="lead">
	Select the companies to associate to ${selectedAssociation.name} (${selectedAssociation.description}):
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

	<liferay-ui:search-container var="searchContainer" delta="100"
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
	<button name="delete" type="button" icon="icon-delete" class="btn btn-primary"
		onClick='<%=renderResponse.getNamespace() + "associateCompanies();"%>'>Associate
		selected</button>	
	<aui:input name="associateCompanyIds" type="hidden" />
	<aui:input name="currentGroupId" type="hidden"
		value="<%=currentGroupId%>" />
	<aui:input name="selectedAssociationId" type="hidden"
		value="${selectedAssociation.teamId}" />

</aui:form>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />associateCompanies',
		function() {
			var associateCompanyIds = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, '<portlet:namespace />allRowIds');	
			
			if (associateCompanyIds && confirm("Are you sure you want to associate the selected company(ies) to  ${selectedAssociation.name}?")) {
				document.<portlet:namespace />fm.<portlet:namespace />associateCompanyIds.value = associateCompanyIds;

				submitForm(document.<portlet:namespace />fm, '<portlet:actionURL name="associateCompanies"></portlet:actionURL>');
			} else {
				
			}
		},
		['liferay-util-list-fields']
	);
</aui:script>
<% } //end if (operationOK != null  && operationOK.compareTo("") != 0) {
else {	
%>
<% if (operationFinished) {
	out.println("Operation performed correctly.");
}%>
<% } %>