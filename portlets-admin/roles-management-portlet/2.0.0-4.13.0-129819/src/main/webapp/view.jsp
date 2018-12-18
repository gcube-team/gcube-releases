<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<theme:defineObjects/>
<portlet:defineObjects />
<portlet:resourceURL var="loginURL" />

<p id="portletInfo" data-namespace="<portlet:namespace/>" data-loginurl="<portlet:resourceURL />"></p>

<!-- font-awesome -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">

<script>window.jQuery || document.write('<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js" type="text/javascript"><\/script>')</script>
<script src="https://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
<!-- Bootstrap.min -->
<script src="<c:url value="/js/bootstrap.min.js" />"></script>
<!-- DataTables JS-->
<script src="<c:url value="/js/jquery_datatables/jquery.dataTables.js?01" />"></script>
<script src="<c:url value="/js/jquery_datatables/dataTables.responsive.min.js?01" />"></script>
<script type="text/javascript">
//	Global variables
	var rolesTableData = [];//Data for the table
	var nameSpace = $('#portletInfo').data('namespace');
	var loginURL = $('#portletInfo').data('loginurl');
	
	var RETRIEVE_ROLES = 0;
	var EDIT_ROLE = 1;
	var DELETE_ROLE = 2;
	var ADD_ROLE = 3;
	
	var theSelectedRow = -1;
	
</script>
<div id="RolesManagementContainer">

	<div id="alertError" class="alert alert-error" style="display:none;">
		<button type="button" class="close" data-dismiss="alert">&times;</button>
	</div>
	
	<div id="blanket" class="hiddenBlanket"></div>
	
	<p id="preloader" class="hiddenPreloader">
		<img src="<c:url value="/img/preloader.gif?01" />"/>
	</p>

	<table id="CurrentRolesTable" class="no-wrap " style="width: 100%;">
		<thead>
			<tr role="row">
				<th>
					<div style="padding-left:12px">all</div>
				</th>
				<th>
					Name
				</th>
				<th>
					Description
				</th>
			</tr>
		</thead>
	</table>


	<div id="EditRolesModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span id="acceptRequestHeader">Edit selected role</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<div id="rolesAttributes">
	  		<div class="row-fluid">
	  			<div class="span3">
	  				<label for="EditRoleName">Name:</label>
	  			</div>
	  			<div class="span9">
	  				<input type="text" id="EditRoleName" class="span12" placeholder="Name of role"/>
	  			</div>
	  		</div>
	  		<div class="row-fluid">
	  			<div class="span3">
	  				<label for="EditRoleDescription">Description:</label>
	  			</div>
	  			<div class="span9">
	  				<textarea id="EditRoleDescription" class="span12" rows="4" placeholder="Describe the role here"></textarea>
	  			</div>
	  		</div>
	  	</div>
	  </div>
	  <div class="modal-footer">
	    <button id="editRolesModalOK" class="btn btn-link">OK</button>
	    <button id="closeEditRolesModalModal" class="btn btn-link" data-dismiss="modal" aria-hidden="true">Cancel</button>
	  </div>
	</div>


	<div id="DeleteRoleModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span id="deleteRoleHeader">Delete selected role</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<p>Are you sure you want to delete the selected role?</p>
	  </div>
	  <div class="modal-footer">
	    <button id="deleteRoleModalOK" class="btn btn-link">OK</button>
	    <button id="closeDeleteRoleModalModal" class="btn btn-link" data-dismiss="modal" aria-hidden="true">Cancel</button>
	  </div>
	</div>


	<div id="AddRoleModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span id="addNewRoleHeader">Add new role</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<div id="rolesAttributes">
	  		<div class="row-fluid">
	  			<div class="span3">
	  				<label for="AddNewRoleName">Name:</label>
	  			</div>
	  			<div class="span9">
	  				<input type="text" id="AddNewRoleName" class="span12" placeholder="Name of role"/>
	  			</div>
	  		</div>
	  		<div class="row-fluid">
	  			<div class="span3">
	  				<label for="AddNewRoleDescription">Description:</label>
	  			</div>
	  			<div class="span9">
	  				<textarea id="AddNewRoleDescription" class="span12" rows="4" placeholder="Describe the new role here"></textarea>
	  			</div>
	  		</div>
	  	</div>
	  </div>
	  <div class="modal-footer">
	    <button id="addRoleModalOK" class="btn btn-link">OK</button>
	    <button id="closeAddRoleModalModal" class="btn btn-link" data-dismiss="modal" aria-hidden="true">Cancel</button>
	  </div>
	</div>

	<div id="InternalServerErrorModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span id="deleteRoleHeader">Internal server error</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<p>An internal server error has occured. Please try again later</p>
	  </div>
	  <div class="modal-footer">
	    <button id="closeInternalServerModal" class="btn btn-link" data-dismiss="modal" aria-hidden="true">OK</button>
	  </div>
	</div>

</div>

<!-- JS -->
<script src="<c:url value="/js/togglePreloader.js?01" />"></script>
<script src="<c:url value="/js/DataTableCSSArrowsIssueWorkArround.js?01" />"></script>
<script src="<c:url value="/js/RolesTablesInitialization.js?01" />"></script>
<script src="<c:url value="/js/navBar.js?01" />"></script>
<script src="<c:url value="/js/tableEvents.js?01" />"></script>
<script src="<c:url value="/js/modal.js?01" />"></script>
<!-- DataTables CSS-->
<link href="<c:url value="/css/jquery_datatables/jquery.dataTables.css?01" />" rel="stylesheet">
<!-- Custom CSS -->
<link href="<c:url value="/css/table.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/pagination.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/navbar.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/toolbar.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/modal.css?01" />" rel="stylesheet">
<link href="<c:url value="/css/preloader.css?01" />" rel="stylesheet">

<script type="text/javascript">
	$(function(){
		rolesTableInitialization();
		retrieveRoles();
		buildToolbarOverTable();
		navBar();
		tableEvents();
		modalFunctionality();
	});
</script>