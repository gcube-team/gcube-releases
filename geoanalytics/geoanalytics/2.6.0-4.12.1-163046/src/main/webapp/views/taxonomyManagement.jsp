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
<script src="../resources/script/utils.js" > </script>
<script src="../resources/script/adminMenu.js" > </script>
<script src="../resources/script/taxonomyManagement.js" > </script>
<!--[if lt IE 9]>
		<script src="resources/script/css3-mediaqueries.js"></script>
		<script src="resources/script/eventListenerSupport.js"></script>
<![endif]-->
</head>
<body>
	  <!-- Add Taxonomy Modal -->
	  <div class="modal fade" id="addTaxonomyModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	          <h4 class="modal-title">Add New Taxonomy</h4>
	        </div>
	        <div class="modal-body" id="addTaxonomyModalBody">
	        <form id="addTaxonFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="addTaxonFormTextBoxname" class="addTaxonFormElement col-md-2 control-label">Name</label>
						<div class="addTaxonFormElement col-md-4">
							<input type="text" id="addTaxonFormTextBoxname" class="addTaxonFormElement form-control" name="name">
						</div>
						<label for="addTaxonFormListBoxtaxonomyClass" class="addTaxonFormElement col-md-2 control-label">Class</label>
						<div class="addTaxonFormElement col-md-4">
							<select id="addTaxonFormListBoxtaxonomyClass" class="addTaxonFormElement form-control" name="taxonomyClass">
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="addTaxonCheckBoxuserTaxonomy" class="addTaxonFormELement col-md-2 control-label">User Taxonomy </label>
						<div class="importForm col-md-4 checkbox">
							<div class = "row">
								<input type="checkbox" id="addTaxonFormCheckBoxuserTaxonomy" class="addTaxonFormElement col-md-5" name="userTaxonomy">
							</div>
						</div>
					</div>
					<div class="form-group">
						<label for="addTaxonFormTextBoxextraData" class="addTaxonFormElement col-md-2 control-label">Extra Data</label>
						<div class="addTaxonFormElement col-md-10">
							<textarea rows="3" id="addTaxonFormTextBoxextraData" class="addTaxonFormElement form-control" name="extraData"></textarea>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-offset-8 col-md-2">
							<button type="button" id="addTaxonFormCancelButton" class="addTaxonFormButton btn btn-default" value="Cancel">Cancel</button>
						</div>
						<div class="col-md-1">
							<button type="button" id="addTaxonFormSaveButton" class="addTaxonFormButton btn btn-primary" value="Save">Save</button>
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
	  </div><!-- /.Add Taxonomy modal -->
	
	 <!-- Add Taxonomy Term Modal -->
	  <div class="modal fade" id="addTermModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	          <h4 class="modal-title">Add New Taxonomy Term</h4>
	        </div>
	        <div class="modal-body" id="addTermModalBody">
		        <form id="addTermFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="addTermFormTextBoxname" class="addTermFormElement col-md-2 control-label">Name</label>
						<div class="addTaxomFormElement col-md-4">
							<input type="text" id="addTermFormTextBoxname" class="addTermFormElement form-control" name="name">
						</div>
						<label for="addTermFormTextorder" class="addTermFormElement col-md-2 control-label">Order</label>
						<div class="addTermFormElement col-md-4">
							<input type="text" id="addTermFormTextBoxorder" class="addTermFormElement form-control" name="order">
						</div>
					</div>
					<div class="form-group">
						<label for="addTermFormListBoxclassTaxonomy" class="addTermFormElement col-md-2 control-label">Class: Taxonomy</label>
						<div class="addTermFormElement col-md-4">
							<select id="addTermFormListBoxclassTaxonomy" class="addTermFormElement form-control" name="classTaxonomy">
							</select>
						</div>
						<label for="addTermFormListBoxclassTerm" class="addTermFormElement col-md-2 control-label">Class: Term</label>
						<div class="addTermFormElement col-md-4">
							<select id="addTermFormListBoxclassTerm" class="addTermFormElement form-control" name="classTerm">
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="addTermFormListBoxparentTaxonomy" class="addTermFormElement col-md-2 control-label">Parent: Taxonomy</label>
						<div class="addTermFormElement col-md-4">
							<select id="addTermFormListBoxparentTaxonomy" class="addTermFormElement form-control" name="parentTaxonomy">
							</select>
						</div>
						<label for="addTermFormListBoxparentTerm" class="addTermFormElement col-md-2 control-label">Parent: Term</label>
						<div class="addTermFormElement col-md-4">
							<select id="addTermFormListBoxparentTerm" class="addTermFormElement form-control" name="parentTerm">
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="addTermFormListBoxtaxonomy" class="addTermFormElement col-md-2 control-label">Taxonomy</label>
						<div class="addTermFormElement col-md-4">
							<select id="addTermFormListBoxtaxonomy" class="addTermFormElement form-control" name="taxonomy">
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="addTermFormTextBoxconfig" class="addTermFormElement col-md-2 control-label">Configuration</label>
						<div class="addTermFormElement col-md-10">
							<textarea rows="5" id="addTermFormTextBoxconfig" class="addTermFormElement form-control" name="configuration">
							</textarea>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-offset-8 col-md-2">
							<button type="button" id="addTermFormCancelButton" class="addTermFormButton btn btn-default" value="Cancel">Cancel</button>
						</div>
						<div class="col-md-1">
							<button type="button" id="addTermFormSaveButton" class="addTermFormButton btn btn-primary" value="Save">Save</button>
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
	  </div><!-- /.Add Taxonomy Term modal -->
	  
	  <!-- DB Offline Modal -->
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
	  </div><!-- /.DB Offline modal -->
	  
	<div id="taxonomyMan">
		
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
			<p class="title">Taxonomy Management</p>
		</div>
		
		<div class="row adminContainer">
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
			<div id="searchCon" class="searchCon row">
				<form id="searchForm" class="searchForm form-horizontal col-md-7">
					<div class="searchForm form-group">
						<label for="listBoxTaxonomies" class="searchForm col-md-6">Taxonomies</label>
						<!-- <label for="textBoxTaxonomyTerms" class="searchForm col-md-6">Taxonomy Terms</label> -->
					</div>
					<div class="searchForm form-group">
						<div class="col-md-6">
							<select id="listBoxTaxonomies" multiple="multiple" class="searchForm form-control" name="taxonomyNames">
								<option selected="selected" disabled="disabled">All Taxonomies</option>
							</select>
						</div>
<!-- 						<div class="col-md-6">
							<input type="text" id="textBoxTaxonomyTerms" class="searchForm col-md-6 form-control" name="termNames" placeholder="All Terms">
						</div> -->
					</div>
					<div class="searchForm form-group">
						<div class="searchForm col-md-6">
							<div class="checkbox">
								<label class="searchForm">
									Only active taxonomies
									<input type="checkbox" id="activeTaxonomies" class="searchForm" name="activeTaxonomies">
								</label>
							</div>
						</div>
<!-- 						<div class="searchForm col-md-6">
							<div class="checkbox">
								<label class="searchForm">
									Only active terms
									<input type="checkbox" id="activeTerms" class="searchForm" name="activeTerms">
								</label>
							</div>
						</div> -->
					</div>
					<div class="searchForm form-group">
						<div class="searchForm col-md-offset-5 col-md-2">
							<button type="button" id="searchButton" class="searchForm btn btn-primary" value="Search">Search</button>
						</div>
					</div>
				</form>
				
				<div class="addTaxonomyButtonCon col-sm-offset-1 col-sm-2">
					<div class="btn-group-vertical">
						<button type="button" id="addTaxonomyButton" class="btn btn-primary" value="Add New Taxonomy">Add New Taxonomy</button>
						<button type="button" id="taxonomyConfigButton" class="btn btn-primary" value="Taxonomy Configuration">Taxonomy Configuration</button>
						<button type="button" id="addTermButton" class="btn btn-primary" value="Add New Term">Add New Term</button>
					</div>
				</div>
			</div>
			
			<div id="taxonomyTable" class="taxonomyTable" style="">
				<p id="taxonomyTableHeading" class="heading">Taxonomy Results</p>
				<table id="taxonomyTbl" class="table table-striped table-hover">
				</table>
			</div>
			
			<div id="termTable" class="taxonomyTable" style="">
				<p id="termTableHeading" class="heading">Taxonomy Terms</p>
				<table id="termTbl" class="table table-striped table-hover">
				</table>
			</div>
			
			<div id="taxonomyConfig" class="taxonomyConfig" style="">
				<p id="taxonomyConfigHeading" class="heading">Global Taxonomy Configuration</p>
				<form id="taxonomyConfigForm">
					<div id="taxonomyConfigFormFooter" class="form-group">
						<div class="col-md-offset-8 col-md-2">
							<button type="button" id="taxonomyConfigFormCancelButton" class="taxonomyConfigFormButton btn btn-default" value="Cancel">Cancel</button>
						</div>
						<div class="col-md-1">
							<button type="button" id="taxonomyConfigFormSaveButton" class="taxonomyConfigFormButton btn btn-primary" value="Save">Save</button>
						</div>
					</div>
				</form>
			</div>
			
			<!-- Edit Taxonomy Form -->
			<div id="editTaxonomyForm" class="editTaxonomyForm">
				<form id="editTaxonFormObj" class="form-horizontal">
						<div class="form-group">
							<label for="editTaxonFormTextBoxname" class="editTaxonFormElement col-md-2 control-label">Name</label>
							<div class="addTaxomFormElement col-md-4">
								<input type="text" id="editTaxonFormTextBoxname" class="editTaxonFormElement form-control" name="name">
							</div>
							<label for="editTaxonFormListBoxtaxonomyClass" class="editTaxonFormElement col-md-2 control-label">Class</label>
							<div class="editTaxonFormElement col-md-4">
								<select id="editTaxonFormListBoxtaxonomyClass" class="editTaxonFormElement form-control" name="taxonomyClass">
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="editTaxonFormCheckBoxUserTaxonomy" class="editTaxonFormELement col-md-2 control-label">User Taxonomy </label>
							<div class="importForm col-md-4 checkbox">
								<div class = "row">
									<input type="checkbox" id="editTaxonFormCheckBoxuserTaxonomy" class="editTaxonFormElement col-md-5" name="userTaxonomy">
								</div>
							</div>
							<div class="editFormElement col-md-4">
								<button id="editTaxonFormBtnisActive" name="isActive" type="button" value="Deactivate" class="editTermFormElement btn btn-default">Deactivate</button>
							</div>
						</div>
						<div class="form-group">
							<label for="editTaxonFormTextBoxextraData" class="editTaxonFormElement col-md-2 control-label">Extra Data</label>
							<div class="editTaxonFormElement col-md-10">
								<textarea rows="3" id="editTaxonFormTextBoxextraData" class="editTaxonFormElement form-control" name="extraData"></textarea>
							</div>
					</div>
						<div class="form-group">
							<div class="col-md-offset-8 col-md-2">
								<button type="button" id="editTaxonFormCancelButton" class="editTaxonFormButton btn btn-default" value="Cancel">Cancel</button>
							</div>
							<div class="col-md-1">
								<button type="button" id="editTaxonFormSaveButton" class="editTaxonFormButton btn btn-primary" value="Save">Save</button>
							</div>
						</div>
					</form>
			</div>
			<!-- ./edit Taxonomy Form -->
			
			<!-- Edit Taxonomy Term Form -->
			<div id="editTermForm" class="editTermForm">
				<form id="editTermFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="editTermFormTextBoxname" class="editTermFormElement col-md-2 control-label">Name</label>
						<div class="editTaxomFormElement col-md-4">
							<input type="text" id="editTermFormTextBoxname" class="editTermFormElement form-control" name="name">
						</div>
						<label for="editTermFormTextorder" class="editTermFormElement col-md-2 control-label">Order</label>
						<div class="editTermFormElement col-md-4">
							<input type="text" id="editTermFormTextBoxorder" class="editTermFormElement form-control" name="order">
						</div>
					</div>
					<div class="form-group">
						<label for="editTermFormListBoxtaxonomy" class="editTermFormElement col-md-2 control-label">Taxonomy</label>
						<div class="editTermFormElement col-md-4">
							<select id="editTermFormListBoxtaxonomy" class="editTermFormElement form-control" name="taxonomy">
							</select>
						</div>
						<div class="editFormElement col-md-4">
							<button id="editTermFormBtnisActive" name="isActive" type="button" value="Deactivate" class="editTermFormElement btn btn-default">Deactivate</button>
						</div>
					</div>
					<div class="form-group">
						<label for="editTermFormListBoxclassTaxonomy" class="editTermFormElement col-md-2 control-label">Class: Taxonomy</label>
						<div class="editTermFormElement col-md-4">
							<select id="editTermFormListBoxclassTaxonomy" class="editTermFormElement form-control" name="classTaxonomy">
							</select>
						</div>
						<label for="editTermFormListBoxclassTerm" class="editTermFormElement col-md-2 control-label">Class: Term</label>
						<div class="editTermFormElement col-md-4">
							<select id="editTermFormListBoxclassTerm" class="editTermFormElement form-control" name="classTerm">
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="editTermFormListBoxparentTaxonomy" class="editTermFormElement col-md-2 control-label">Parent: Taxonomy</label>
						<div class="editTermFormElement col-md-4">
							<select id="editTermFormListBoxparentTaxonomy" class="editTermFormElement form-control" name="parentTaxonomy">
							</select>
						</div>
						<label for="editTermFormListBoxparentTerm" class="editTermFormElement col-md-2 control-label">Parent: Term</label>
						<div class="editTermFormElement col-md-4">
							<select id="editTermFormListBoxparentTerm" class="editTermFormElement form-control" name="parentTerm">
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="editTermFormTextBoxconfig" class="editTermFormElement col-md-2 control-label">Configuration</label>
						<div class="editTermFormElement col-md-10">
							<textarea rows="5" id="editTermFormTextBoxconfig" class="editTermFormElement form-control" name="configuration">
							</textarea>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-offset-8 col-md-2">
							<button type="button" id="editTermFormCancelButton" class="editTermFormButton btn btn-default" value="Cancel">Cancel</button>
						</div>
						<div class="col-md-1">
							<button type="button" id="editTermFormSaveButton" class="editTermFormButton btn btn-primary" value="Save">Save</button>
						</div>
					</div>
				</form>
			</div>
		</div>
		<!--./Edit Taxonomy Term Form -->
		
	</div>
	</div>
	
	<script type="text/javascript">
		var data = new Object();
		data.systemOnline = "<c:out value='${SystemStatus}'/>";
		if(isPresent(data.systemOnline))
		{
		 data.systemOnline = (data.systemOnline === 'true');
		}
		showTaxonomyManagement(data);
	</script>
</body>
</html>