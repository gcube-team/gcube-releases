<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<link href='//fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=latin,greek-ext' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="../resources/css/bootstrap-3.0.0.min.css">
<link rel="stylesheet" href="../resources/css/datepicker.css">
<link rel="stylesheet" href="../resources/css/geoanalytics-admin.css" />

<link rel="icon" type="image/png" href="../resources/img/logo3.png">

<script src="../resources/script/jquery-1.10.2.min.js"></script>
<script src="../resources/script/bootstrap-3.0.0.min.js" > </script>
<script src="../resources/script/bootstrap-datepicker.js" > </script>
<script src='//maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAjpkAC9ePGem0lIq5XcMiuhR_wWLPFku8Ix9i2SXYRVK3e45q1BQUd_beF8dtzKET_EteAjPdGDwqpQ'></script>
<script src="../resources/script/OpenLayers.js" > </script>
<script src="../resources/script/utils.js" > </script>
<script src="../resources/script/adminMenu.js" > </script>
<script src="../resources/script/shapeManagement.js" > </script>
<!--[if lt IE 9]>
		<script src="resources/script/css3-mediaqueries.js"></script>
		<script src="resources/script/eventListenerSupport.js"></script>
<![endif]-->
</head>
<body>
	  <!-- Modal -->
	  <div class="modal fade" id="addUserModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	          <h4 class="modal-title">Add New User</h4>
	        </div>
	        <div class="modal-body" id="addUserModalBody">
	        <form id="addFormObj">
					<div class="form-group">
						<label for="addFormListBoxcustomer" class="addFormElement col-md-2 control-label">Customer</label>
							<div class="addFormElement col-md-4">
								<select id="addFormListBoxcustomer" class="addFormElement form-control" name="customer">
									<option value="None">--None--</option>
								</select>
							</div>
						<div class="addFormElement col-md-4">
							<button id="addFormBtnisActive" name="isActive" type="button" value="Deactivate" class="addFormElement btn btn-default">Deactivate</button>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-offset-8 col-md-2">
							<button type="button" id="addFormCancelButton" class="addFormButton btn btn-default" value="Cancel">Cancel</button>
						</div>
						<div class="col-md-1">
							<button type="button" id="addFormSaveButton" class="addFormButton btn btn-primary" value="Save">Save</button>
						</div>
					</div>
				</form>
	        </div> 
	        <!-- <div class="modal-footer" id="addUserModalFooter">
	          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	          <button type="button" class="btn btn-primary">Save</button>
	        </div> -->
	      </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	  </div><!-- /.modal -->
	  
	  <!-- Modal -->
	  <div class="modal fade" id="dbOfflineModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	          <h4 class="modal-title">Bring System Offline?</h4>
	        </div>
	        <div class="modal-body" id="dbOfflineModalBody">
	        	<p><span style="float: left; margin: 0 7px 20px 0;"></span>
  					The system has to be brought offline before taking a backup of the database</p>
	        </div>
	         <div class="modal-footer" id="dbOfflineModalFooter">
	          <button type="button" id="dbOfflineModalNoButton" class="btn btn-default" data-dismiss="modal">No</button>
	          <button type="button" id = "dbOfflineModalYesButton" class="btn btn-primary">Yes</button>
	        </div>
	      </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	  </div><!-- /.modal -->
	<div id="shapeMan">
		
		<div class="top">
	 		<div class="top-links">
		 		<div class="top-link">
					<div id="logout">
	    				<c:url var="logoutUrl" value="/logout"/>
						<form action="${logoutUrl}" method="post">
							<input type="submit" value="Sign out" />
						</form>
	    			</div>
				</div>
				<div class="top-link">
					<a href="..">User Interface</a>
				</div>
			</div>
			<p class="title">Shape Management</p>
		</div>
		
		<div class="adminContainer row">
		<div class="menu col-md-2">
			<div class="logo" >
				<img alt="Geopolis" src="../resources/img/logo3(blue).png" >
			</div>
			<table id="menuTbl">
				<tbody>
					<tr><td><button type="button" id="btnHome" class="menuBtns btn btn-default" value="Home">Home</button></td></tr>
					<tr><td><button id="btnUserManagement" type="button" class="menuBtns btn btn-default" value="User Management">User Management</button></td></tr>
					<tr><td><button type="button" id="btnCustomerManagement" class="menuBtns btn btn-default" value="Customer Management">Customer Management</button></td></tr>
					<tr><td><button id="btnTaxonomyManagement" type="button" class="menuBtns btn btn-default" value="Taxonomy Management">Taxonomy Management</button></td></tr>
					<tr><td><button id="btnShapeManagement" type="button" class="menuBtns btn btn-default" value="Shape Management">Shape Management</button></td></tr>
					<tr><td><button id="btnDocumentManagement" type="button" class="menuBtns btn btn-default" value="Document Management">Document Management</button></td></tr>
					<tr><td><button id="btnDataImport" type="button" class="menuBtns btn btn-default" value="Data Import">Data Import</button></td></tr>
					<tr><td><button id="btnAccounting" type="button" class="menuBtns btn btn-default" value="Accounting">Accounting</button></td></tr>
					<tr><td><button id="btnPresentation" type="button" class="menuBtns btn btn-default" value="Presentation">Presentation</button></td></tr>
					<tr><td><button id="btnDatabaseBackup" type="button" class="menuBtns btn btn-default" value="Database Backup">Database Backup</button></td></tr>
				</tbody>
			</table>
		</div>
		
		<div class="mainScreen col-md-10">
			
			<div class="searchCon row">    
				<form id="searchForm" class="searchForm form-horizontal col-md-7">
					<div class = "control-group">
						<div class="controls">
							<div class="searchForm form-group">
								<label for="listBoxTerm" class="searchForm col-md-6">Term</label>
								<div class="col-md-6">
									<select id="listBoxTerm" class="searchForm form-control" name="term"> </select>
								</div>
								<label for="listBoxImportId" class="searchForm col-md-6">Import id</label>
								<div class="col-md-6">
									<select id="listBoxImportId" class="searchForm form-control" name="importId">
									</select>
								</div>
								<label for="textBoxShapeId" class="searchForm col-md-6">Shape id</label>
								<div class="col-md-6">
									<input type="text" id="textBoxShapeId" class="searchForm form-control" name="shapeId">
								</div>
							</div>
							<div class="searchForm form-group">
								<div class="btn-group" data-toggle="buttons" active="active">
								  <label id="noneLabel" class="btn btn-primary">
								    <input type="radio" name="geoSearchType" id="none" value="None" checked="checked"> None
								  </label>
								  <label id="boundingBoxLabel" class="btn btn-primary">
								    <input type="radio" name="geoSearchType" id="boundingBox" value="BoundingBox"> Bounding Box
								  </label>
								  <label id="proximityLabel" class="btn btn-primary">
								    <input type="radio" name="geoSearchType" id="proximity" value="Proximity"> Proximity to point
								  </label>
								</div>

							</div>
							<div class="searchForm form-group">
								<div class="searchForm col-md-offset-10 col-md-2">
									<button type="button" id="searchButton" class="searchForm btn btn-primary" value="Search">Search</button>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	
		<div class="col-md-6 col-md-offset-2">
			<div id="toggleSelectButtonCon">
	        	<button type="button" id="toggleSelect" class="btn btn-primary" value="Toggle Select Features">Toggle Select Features</button>
	        </div>
	        
			<div id="map" class="visualization-large">
			</div>
		</div>
		
		<div id="featureActions col-md-6">
        	<button type="button" id="layerActionDelete" class = "btn btn-primary" value="DeleteLayer">Delete Layer</button>
        	<button type="button" id="featureActionDelete" class="btn btn-primary" value="Delete">Delete</button>
        </div>
        	<div id="editForm" class="col-md-10 col-md-offset-2">
				<form id="editFormObj">
					<div class="form-group">
						<label for="editFormTextBoxid" class="editFormElement col-md-2 control-label">Id</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="editFormTextBoxid" class="editFormElement form-control input-sm" name="id">
						</div>
						<label for="editFormTextBoxname" class="editFormElement col-md-2 control-label">Name</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="editFormTextBoxname" class="editFormElement form-control input-sm" name="name">
						</div>
					</div>
					<div class="form-group">
						<label for="editFormTextimportId" class="editFormElement col-md-2 control-label">Import id</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="editFormTextBoximportId" class="editFormElement form-control input-sm" name="importId">
						</div>
						<label for="editFormTextBoxcode" class="editFormElement col-md-2 control-label">Code</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="editFormTextBoxcode" class="editFormElement form-control input-s" name="code"></div>
						</div>
					<div class="form-group">
						<label for="editFormTextBoxtermTaxonomy" class="editFormElement col-md-2 control-label">Term taxonomy</label>
							<div class="editFormElement col-md-4"><input type="text" id="editFormTextBoxtermTaxonomy" class="editFormElement form-control" name="termTaxonomy">
						</div>
						<label for="editFormTextBoxtermName" class="editFormElement col-md-2 control-label">Term</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="editFormTextBoxtermName" class="editFormElement form-control input-sm" name="termName">
						</div>
					</div>
					<div class="form-group">
						<label for="editFormTextBoxshapeClass" class="editFormElement col-md-2 control-label">Class</label>
							<div class="editFormElement col-md-4">
								<input type="text" id="editFormTextBoxshapeClass" class="editFormElement form-control input-sm" name="shapeClass">
							</div>
						<label for="editFormTextBoxdata" class="editFormElement col-md-2 control-label">Data</label>
							<div class="editFormElement col-md-4">
								<textarea rows="5" id="editFormTextBoxdata" class="editFormElement form-control" name="data">
								</textarea>
							</div>
					</div>
					<div class="form-group">
						<div class="col-md-offset-8 col-md-2">
							<button type="button" id="cancelButton" class="editFormButton btn btn-default" value="Cancel">Cancel</button>
						</div>
						<div class="col-md-1">
							<button type="button" id="saveButton" class="editFormButton btn btn-primary" value="Save">Save</button>
						</div>
					</div>
				</form>
			</div>
        
      </div>
        
	</div>
	
	<script type="text/javascript">
		var data = new Object();
		data.systemOnline = "<c:out value='${SystemStatus}'/>";
		if(isPresent(data.systemOnline))
		{
		 data.systemOnline = (data.systemOnline === 'true');
		}
		showShapeManagement(data);
	</script>
</body>
</html>