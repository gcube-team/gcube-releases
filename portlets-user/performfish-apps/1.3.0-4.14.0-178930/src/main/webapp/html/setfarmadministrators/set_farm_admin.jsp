<%@include file="../init.jsp"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%
	Boolean operationFinished = (Boolean) request.getAttribute("operationFinished");
	//
	
if (!operationFinished) {

	String portletURLString = PortalUtil.getCurrentURL(request);
	RowChecker rowChecker = new RowChecker(renderResponse);
	String currentUsername = Utils.getCurrentUser(request).getUsername();
	Team theCompany = (Team) request.getAttribute("theCompany");
	Team theFarm = (Team) request.getAttribute("theFarm");
	Group group = (Group) GroupLocalServiceUtil.getGroup(PortalUtil.getScopeGroupId(request));
	long currentGroupId = group.getGroupId();
	String currentURL = PortalUtil.getCurrentURL(request);
	//this is the list to be working on manage
	List<GCubeUser> siteUsers = (List<GCubeUser>) request.getAttribute("availableUsers");
	pageContext.setAttribute("logoURL", renderRequest.getAttribute("companyLogoURL"));
	pageContext.setAttribute("companyName", theCompany.getName());
	pageContext.setAttribute("farmName", theFarm.getName());
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
<p class="lead">List of ${companyName} users not associated already to ${farmName}</p>


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
		emptyResultsMessage="Sorry. There are no users to display."
		iteratorURL="<%=iteratorURL%>" rowChecker="<%=rowChecker%>"
		orderByType="<%=orderByType%>">

		<liferay-ui:search-container-results>
			<%
				int totalUsers = siteUsers.size();
							List<GCubeUser> sortableUsers = ListUtil.subList(siteUsers, searchContainer.getStart(),
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
	<button name="associate" type="button" class="btn btn-primary"
		onClick='<%=renderResponse.getNamespace() + "promoteAdminUsers();"%>'>Assign and Set Farm
		Administrator selected</button>
	<div class="alert alert-info" style="margin-top: 10px;">
	<h4>Please note, Farm Administrator role is company global:</h4>
	<strong>The Farm Administrator role applies to all the farms a user is assigned to.</strong>
	</div>
		
	<aui:input name="addUserIds" type="hidden" />
	<aui:input name="currentUsername" type="hidden"
		value="<%=currentUsername%>" />
	<aui:input name="currentGroupId" type="hidden"
		value="<%=currentGroupId%>" />
	<aui:input name="farmId" type="hidden" value="<%=theFarm.getTeamId()%>" />

</aui:form>

<aui:script>
	Liferay.provide(
		window,
		'<portlet:namespace />promoteAdminUsers',
		function() {
			var addUserIds = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, '<portlet:namespace />allRowIds');	
			if (addUserIds && confirm("Are you sure you want to promote ${farmName} Administrator the selected user(s)?")) {
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
<% if (operationFinished) { %>
<p class="lead">Farm admin(s) set correctly</p>

<aui:button name="closeDialog" type="button" value="close" />

<aui:script use="aui-base">
	A.one('#<portlet:namespace/>closeDialog').on('click', function(event) {
                     Liferay.Util.getOpener().closePopup('<portlet:namespace/>dialog');
                 });
</aui:script>
<%}%>
<% } %>