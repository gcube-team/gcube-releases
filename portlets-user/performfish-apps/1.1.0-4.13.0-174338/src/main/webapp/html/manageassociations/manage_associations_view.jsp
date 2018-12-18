<%@include file="../init.jsp"%>

<%
	LinkedHashMap<Association, List<Company>> associationCompanies = (LinkedHashMap<Association, List<Company>>) renderRequest
			.getAttribute("associationCompanies");
	pageContext.setAttribute("associationCompanies", associationCompanies);
	List<Association> associations = (List<Association>) renderRequest.getAttribute("associations");
	pageContext.setAttribute("associations", associations);
	String vreName = (String) renderRequest.getAttribute("vreName");
	pageContext.setAttribute("vreName", vreName);
	RowChecker rowChecker = new RowChecker(renderResponse);
	String portletURLString = PortalUtil.getCurrentURL(request);
	String currentGroupId = PortalUtil.getScopeGroupId(request) + "";
%>

<portlet:renderURL var="maximizedState"
	windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>" />
<portlet:renderURL var="normalState"
	windowState="<%=LiferayWindowState.NORMAL.toString()%>" />

<liferay-portlet:renderURL
	portletName="<%=PFISHConstants.ASSOCIATE_COMPANIES_TO_ASSOC_PORTLETID%>"
	var="associateCompaniesURL"
	windowState="<%=LiferayWindowState.POP_UP.toString()%>">
</liferay-portlet:renderURL>
<script>
//open the portlet to set CompanyAdmins, called by the getSelectedCompanyIds 
function openManageCompanyAdminsPopup(associationIds) {
	 Liferay.Util.openWindow({ dialog: { 
		 centered: true, 
		 height: 600, 
		 modal: true, 
		 width: 900 
		 }, 
		 id: '<portlet:namespace />dialog',
		 title: 'Associate companies', 
		 uri: '<%=associateCompaniesURL.toString()%>&associationId='+ associationIds
		});
	}
	//define the js function getSelectedCompanyIds returning the id of the checkboxed selected
	Liferay.provide(window, 'getSelectedAssocationIds', function() {
		var associationIds = Liferay.Util.listCheckedExcept(
				document.<portlet:namespace />fm,
				'<portlet:namespace />allRowIds');
		if (associationIds == null || associationIds == '')
			alert('Select one association first');
		else if (associationIds.indexOf(',') > -1)
			alert('Select one association only');
		else
			openManageCompanyAdminsPopup(associationIds);
	}, [ 'liferay-util-list-fields' ]);
</script>

<c:set var="maximised" scope="session"
	value="${renderRequest.getWindowState().toString().equalsIgnoreCase('maximized')}" />
<c:choose>
	<c:when test="${not maximised}">
		<div style="width: 100%; text-align: left;">
			<c:forEach var="association" items="${associationCompanies}">
				<div style="font-size: 1.2em; margin-top: 10px; padding: 5px; border-bottom: 1px solid #CCC;">
				<table id="theTable" class="display">
					<tbody>
						<tr>
							<td><span style="font-size: 1.3em; color: black;">Association:</span></td>
							<td><span style="font-size: 1.3em; color: black;">${association.key.shortName}</span></td>
							<td><span style="font-size: 1.3em; color: black;">(${association.key.associationId})</span></td>
							<td><img src="${association.key.imageUrl}"
								style="width: 24px; padding-left: 10px;"></td>
						</tr>

					</tbody>
				</table>
				</div>
				<c:if test="${not empty association.value}">
					<c:forEach var="company" items="${association.value}">
						<table id="theCompanyTable" class="display" style="margin-left: 20px;">
							<tbody>
								<tr>
									<td><img src="${company.imageUrl}"
										style="width: 24px; padding-right: 10px;"></td>
									<td><span style="font-size: 1.3em; color: black;">${company.name}</span></td>
									<td><span style="font-size: 1.3em; color: black;">(${company.companyId})</span></td>
								</tr>
							</tbody>
						</table>
					</c:forEach>
				</c:if>
			</c:forEach>
			<div style="padding: 15px 0;">
				<a class="btn btn-primary" href="${maximizedState}">Manage
					Associations</a>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<a class="btn btn-link btn-large no-padding" href="${normalState}"><i
			class="icon icon-angle-left"></i>&nbsp;Back to Control Centre</a>
		<button class="btn btn-link btn-large"
			onClick="getSelectedAssocationIds();">
			<i class="icon icon-angle-right"></i>&nbsp;Associate companies
		</button>

		<%@include file="/html/manageassociations/edit_associations.jsp"%>
	</c:otherwise>
</c:choose>


