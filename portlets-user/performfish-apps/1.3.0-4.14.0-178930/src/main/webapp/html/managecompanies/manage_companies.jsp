<%@include file="../init.jsp"%>

<%
	List<Company> companies  = (List<Company> ) renderRequest.getAttribute("companies");
	pageContext.setAttribute("companies", companies);
	String vreName = (String) renderRequest.getAttribute("vreName");
	pageContext.setAttribute("vreName", vreName);
	RowChecker rowChecker = new RowChecker(renderResponse);
	String portletURLString = PortalUtil.getCurrentURL(request);
	String currentGroupId = PortalUtil.getScopeGroupId(request)+"";
%>

<portlet:renderURL var="maximizedState"
	windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>" />
<portlet:renderURL var="normalState"
	windowState="<%=LiferayWindowState.NORMAL.toString()%>" />
	
<liferay-portlet:renderURL portletName="<%=PFISHConstants.SET_COMPANY_ADMINISTRATOR_PORTLETID%>" var="manageCompanyAdminsURL" windowState="<%=LiferayWindowState.POP_UP.toString()%>">
</liferay-portlet:renderURL>
<script>
//open the portlet to set CompanyAdmins, called by the getSelectedCompanyIds 
function openManageCompanyAdminsPopup(companyIds) {
	 Liferay.Util.openWindow({ dialog: { 
		 centered: true, 
		 height: 600, 
		 modal: true, 
		 width: 900 
		 }, 
		 id: '<portlet:namespace />dialog',
		 title: 'Set Company Admins', 
		 uri: '<%=manageCompanyAdminsURL.toString()%>&companyId=' + companyIds
	 }); 
}
//define the js function getSelectedCompanyIds returning the id of the checkboxed selected
Liferay.provide(
		window,
		'getSelectedCompanyIds',
		function() {
			var companyIds = Liferay.Util.listCheckedExcept(document.<portlet:namespace />fm, '<portlet:namespace />allRowIds');
			if (companyIds == null || companyIds == '')
				alert('Select one company first');
			else if (companyIds.indexOf(',') > -1)
				alert('Select one company only');			
			else
				openManageCompanyAdminsPopup(companyIds);
		},
		['liferay-util-list-fields']
		);

</script>
<c:set var="maximised" scope="session"
	value="${renderRequest.getWindowState().toString().equalsIgnoreCase('maximized')}" />
<c:choose>
	<c:when test="${not maximised}">
		<div style="width: 100%; text-align: left; color: #3B5998;">
			<table id="example" class="display">
				<tbody>
					<c:forEach var="company" items="${companies}">
							<tr>
							<td><img src="${company.imageUrl}"
								style="width: 24px; padding-right: 10px;"></td>
							<td><span style="font-size: 1.3em; color: black;">${company.name}</span></td>
							<td><span style="font-size: 1.3em; color: black;">(${company.companyId})</span></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div style="padding: 15px 0;">
				<a class="btn btn-primary" href="${maximizedState}">Manage
					Companies</a>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<a class="btn btn-link btn-large no-padding" href="${normalState}"><i
			class="icon icon-angle-left"></i>&nbsp;Back to Control Centre</a>
		<button class="btn btn-link btn-large" onClick="getSelectedCompanyIds();"><i
			class="icon icon-angle-right"></i>&nbsp;Set company Administrators</button>

		<%@include file="/html/managecompanies/edit_companies.jsp"%>
	</c:otherwise>
</c:choose>


