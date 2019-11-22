<%@include file="../init.jsp"%>

<%
	List<Farm> farms = (List<Farm>) renderRequest.getAttribute("farms");
	pageContext.setAttribute("farms", farms);
	GCubeTeam theCompany = (GCubeTeam) renderRequest.getAttribute("company");
	pageContext.setAttribute("theCompany", theCompany);
	RowChecker rowChecker = new RowChecker(renderResponse);
	String portletURLString = PortalUtil.getCurrentURL(request);
	String currentGroupId = PortalUtil.getScopeGroupId(request) + "";
	String fullName = PortalUtil.getUser(request).getFullName();
	pageContext.setAttribute("theCreator", fullName);
%>

<portlet:renderURL var="maximizedState"
	windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>" />
<portlet:renderURL var="normalState"
	windowState="<%=LiferayWindowState.NORMAL.toString()%>" />

<c:set var="maximised" scope="session"
	value="${renderRequest.getWindowState().toString().equalsIgnoreCase('maximized')}" />

<portlet:actionURL name="addFarm" var="addFarmURL" />
<portlet:actionURL name="editFarm" var="editFarmURL" />
<portlet:actionURL name="deleteFarm" var="deleteFarmURL" />

<liferay-portlet:renderURL
	portletName="<%=PFISHConstants.SET_FARM_ADMINISTRATOR_PORTLETID%>"
	var="manageFarmAdminsURL"
	windowState="<%=LiferayWindowState.POP_UP.toString()%>">
</liferay-portlet:renderURL>
<script>
//open the portlet to set CompanyAdmins, called by the getSelectedCompanyIds 
function openManageFarmAdminsPopup(companyId, farmId) {
	 Liferay.Util.openWindow({ dialog: { 
		 centered: true, 
		 height: 600, 
		 modal: true, 
		 width: 900 
		 }, 
		 id: '<portlet:namespace />dialog',
		 title: 'Assign Farm Administrator', 
		 uri: '<%=manageFarmAdminsURL.toString()%>&companyId=' + companyId + '&farmId=' + farmId
	 }); 
}
function populateEditFarm(farmId) {
	showEditFarm(true);
	var farmName = $("#"+farmId+"_name").text();
	var farmAddress = $("#"+farmId+"_address").text();
	$("#_managefarms_WAR_PerformFISHAppsportlet_editFarmIdHidden").val(farmId);
	$("#_managefarms_WAR_PerformFISHAppsportlet_editNameBox").val(farmName);
	$("#_managefarms_WAR_PerformFISHAppsportlet_editLocationBox").val(farmAddress);	
}
function populateDeleteFarm(farmId) {
	showDeleteFarm(true);
	var farmName = $("#"+farmId+"_name").text();
	var farmAddress = $("#"+farmId+"_address").text();
	$("#_managefarms_WAR_PerformFISHAppsportlet_deleteFarmIdHidden").val(farmId);
	$("#deleteNameBox").text(farmName);
	$("#deleteLocationBox").text(farmAddress);	
}
</script>
<aui:script use="liferay-util-window">
Liferay.provide(
	     window,
	     'closePopup',
	     function(dialogId) {
             var dialog = Liferay.Util.getWindow(dialogId);
	          Liferay.fire('closeWindow', {
	                    id:'<portlet:namespace/>dialog'
	                           });
	          var theURLToLoad = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname;
	          window.location.href = theURLToLoad;
              },['aui-base','liferay-util-window']
);        	
</aui:script>
<div style="width: 100%; text-align: left; color: #3B5998;">
	<table id="example" class="display">
		<thead>
			<th>Sel.</th>
			<th></th>
			<th>Name</th>
			<th><span style="margin-left: 10px; font-size: 1em;">Location
			</span></th>
			<th><span style="margin-left: 10px; font-size: 0.8em;">Created
					by </span></th>
			<th><span style="margin-left: 10px; font-size: 0.8em;">Created (UTC)</span></th>
			<th><span style="margin-left: 10px; font-size: 1em;">Administrator(s)</span></th>
		</thead>
		<tbody>
			<c:forEach var="farm" items="${farms}">
				<%
					Farm theFarm = (Farm) pageContext.getAttribute("farm");
						if (theFarm.getAdministrators() == null || theFarm.getAdministrators().isEmpty())
							pageContext.setAttribute("admins", "<span style='color: orange;'>Missing! Assign one</span>");
						else {
							String adminsToString = "";
							int i = 1;
							for (GCubeUser admin : theFarm.getAdministrators()) {
								adminsToString += admin.getFullname();
								if (i < theFarm.getAdministrators().size())
									adminsToString += ", ";
								i++;
							}
							pageContext.setAttribute("admins", adminsToString);
						}
				%>
				<tr>
					<td><input type="radio" name="farmItem" value="${farm.farmId}"
						id="${farm.farmId}" style="margin: 10px 5px 10px;"></td>
					<td><img src="${farm.imageUrl}"
						style="width: 24px; padding-right: 10px;"></td>
					<td><span id="${farm.farmId}_name" style="font-size: 1.1em;">${farm.name}</span></td>
					<td><span id="${farm.farmId}_address"
						style="margin-left: 10px; color: black;">${farm.location}</span></td>
					<td><span
						style="margin-left: 10px; color: black;">${farm.creatorFullname}</span></td>
					<td><span
						style="margin-left: 10px; color: #666;"><fmt:formatDate
								type="both" dateStyle="medium" timeStyle="short"
								value="${farm.dateCreated}" /></span></td>
					<td><span
						style="margin-left: 10px; color: black;">${admins}</span></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div style="padding: 15px 0;">
		<button class="btn btn-primary" id="formNewFarmButton"
			onClick="showInsertFarm(true);">Insert new Farm</button>
		<c:if test="${not empty farms}">
			<button id="assignFarmAdmin" type="button" class="btn btn-primary"
				onClick="openManageFarmAdminsPopup(${theCompany.teamId}, getSelectedRadioFarmId());">Assign and set
				Administrator</button>
<%-- 			<liferay-ui:icon-menu message="Submit forms"> --%>
<%-- 				<liferay-ui:icon image="edit" message="Edit" url="#" /> --%>
<%-- 				<liferay-ui:icon image="permissions" message="permissions" url="#" /> --%>
<%-- 			</liferay-ui:icon-menu> --%>
			<button id="editFarm" type="button" class="btn" id="formEditFarmButton"
			onClick="populateEditFarm(getSelectedRadioFarmId());">Edit
				selected</button>
			<button id="deleteFarm" type="button" class="btn" id="formDeleteFarmButton"
			onClick="populateDeleteFarm(getSelectedRadioFarmId());">Delete
				selected</button>
		</c:if>
	</div>
	<div id="formDeleteFarm" style="display: none;">
		<aui:form action="<%=deleteFarmURL%>" method="post">
			<aui:fieldset label="Please confim the delete operation">
				<aui:layout>
					<aui:column>
					<div class="alert alert-error">
					 <strong>Warning!</strong> This operation is undoable and deletes also all the forms associated to this farm in ${theCompany.teamName} Private Repository.
					</div>
						<label><strong>Farm name: </strong><span id="deleteNameBox"></span></label> 
						<label><strong>Location: </strong><span id="deleteLocationBox"></span></label> 
						<aui:input type="hidden" name="CompanyId"
							value="${theCompany.teamId}" />
						<aui:input type="hidden" name="FarmId" id="deleteFarmIdHidden" 
							value="" />
						<aui:input name="currentGroupId" type="hidden"
							value="<%=currentGroupId%>" />
					</aui:column>
					<aui:button-row>
						<aui:button type="submit" value="Confirm Delete Farm" />
						<aui:button type="button" value="Cancel" last="true"
							onClick="showDeleteFarm(false);" />
					</aui:button-row>
				</aui:layout>
			</aui:fieldset>
		</aui:form>
	</div>
	<div id="formNewFarm" style="display: none;">
		<aui:form action="<%=addFarmURL%>" method="post">
			<aui:fieldset label="Insert new ${theCompany.teamName} Farm">
				<aui:layout>
					<aui:column>
						<aui:input type="text" name="farmName" required="true"
							label="Name:" inlineLabel="true" />
						<aui:input type="text" name="Location" label="Location / Address:"
							inlineLabel="true" />
						<aui:input type="hidden" name="CompanyId"
							value="${theCompany.teamId}" />
						<aui:input name="currentGroupId" type="hidden"
							value="<%=currentGroupId%>" />
						<aui:input type="hidden" name="theCreatorFullName"
							value="${theCreator}" />
					</aui:column>
					<aui:button-row>
						<aui:button type="submit" value="Confirm" />
						<aui:button type="button" value="Cancel" last="true"
							onClick="showInsertFarm(false);" />
					</aui:button-row>
				</aui:layout>
			</aui:fieldset>
		</aui:form>
	</div>
	<div id="formEditFarm" style="display: none;">
		<aui:form action="<%=editFarmURL%>" method="post">
			<aui:fieldset label="Edit Farm">
				<aui:layout>
					<aui:column>
						<aui:input type="text" id="editNameBox" name="farmName" required="true"
							label="Name:" inlineLabel="true" />
						<aui:input type="text" id="editLocationBox" name="Location" label="Location / Address:"
							inlineLabel="true" />
						<aui:input type="hidden" name="CompanyId"
							value="${theCompany.teamId}" />
						<aui:input type="hidden" name="FarmId" id="editFarmIdHidden" 
							value="" />
						<aui:input name="currentGroupId" type="hidden"
							value="<%=currentGroupId%>" />
					</aui:column>
					<aui:button-row>
						<aui:button type="submit" value="Confirm" />
						<aui:button type="button" value="Cancel" last="true"
							onClick="showEditFarm(false);" />
					</aui:button-row>
				</aui:layout>
			</aui:fieldset>
		</aui:form>
	</div>
</div>



