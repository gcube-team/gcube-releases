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
<script src="../resources/script/documentManagement.js" > </script>
<!--[if lt IE 9]>
		<script src="resources/script/css3-mediaqueries.js"></script>
		<script src="resources/script/eventListenerSupport.js"></script>
<![endif]-->
</head>
<body>
	  <!-- Modal -->
	  <div class="modal fade" id="addDocumentModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	          <h4 class="modal-title">Add New Document</h4>
	        </div>
	        <div class="modal-body" id="addDocumentModalBody">
	        <form id="addFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="addFormTextBoxname" class="addFormElement col-md-2 control-label">Name</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addFormTextBoxname" class="addFormElement form-control" name="name">
						</div>
						<label for="addFormTextBoxdescription" class="addFormElement col-md-2 control-label">Description</label>
						<div class="addFormElement col-md-4">
							<textarea rows="1" id="addFormTextBoxdescription" class="addFormElement form-control" name="description">
							</textarea>
						</div>
					</div>
					<div class="form-group">
						<label for="addFormTextBoxProject" class="addFormElement col-md-2 control-label">Project</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addFormTextBoxProject" class="addFormElement form-control" name="project">
						</div>
						<label for="addFormTextBoxshape" class="addFormElement col-md-2 control-label">Shape</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addFormTextBoxshape" class="addFormElement form-control" name="shape">
						</div>
					</div>
					<div class="form-group">
						<label for="addFormfile" class="addFormElement col-md-2 control-label">File</label>
						<div class="addFormElement col-md-4">
							<input type="file" id="addFormfile" class="addFormElement form-control" name="file">
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
	<div id="userMan">
		
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
			<p class="title">Document Management</p>
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
					<div class="searchForm form-group">
						<label for="textBoxTerms" class="searchForm col-md-6">Term(s)</label>
						<label for="listBoxCustomer" class="searchForm col-md-6">Customer</label>
					</div>
					<div class="searchForm form-group">
						<div class="col-md-6">
							<input type="text" id="textBoxTerms" class="searchForm form-control" name="terms" placeholder="Search terms">
						</div>
						<div class="col-md-6">
							<select id="listBoxCustomer" class="searchForm col-md-6 form-control" name="customer" >
								<option value="None">All Customers</option>
							</select>
						</div>
					</div>
					<div class="searchForm form-group">
						<label for="listBoxUser" class="searchForm col-md-6">Creator</label>
						<label for="listBoxProject" class="searchForm col-md-6">Project</label>
					</div>
					<div class="searchForm form-group">
						<div class="col-md-6">
							<select id="listBoxUser" class="searchForm form-control" name="creator">
								<option value="None">All Users</option>
							</select>
						</div>
						<div class="col-md-6">
							<select id="listBoxProject" class="searchForm col-md-6 form-control" name="project">
								<option value="None">All Projects</option>
							</select>
						</div>
					</div>
					
					
					<div class="searchForm form-group">
						<div class="searchForm col-md-offset-10 col-md-2">
							<button type="button" id="searchButton" class="searchForm btn btn-primary" value="Search">Search</button>
						</div>
					</div>
				</form>
				
			<div class="addDocumentButtonCon col-md-offset-1 col-md-2">
				<div>
					<button type="button" id="addDocumentButton" class="btn btn-primary" value="Add New Document">Add New Document</button>
				</div>
			</div>
			</div>
			
			<div class="row">
			<div id="documentTable" class="documentTable col-md-12" style="">
				<p class="heading">Document Results</p>
				<table id="documentTbl" class="table table-striped table-hover">
				</table>
			</div>
			</div>
			
			<div id="editForm" class="editForm">
				<form id="editFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="editFormTextBoxname" class="editFormElement col-md-2 control-label">Name</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="editFormTextBoxname" class="editFormElement form-control" name="name">
						</div>
						<label for="editFormTextBoxdescription" class="editFormElement col-md-2 control-label">Description</label>
						<div class="editFormElement col-md-4">
							<textarea rows="1" id="editFormTextBoxdescription" class="editFormElement form-control" name="description">
							</textarea>
						</div>
					</div>
					<div class="form-group">
						<label for="editFormTextBoxProject" class="editFormElement col-md-2 control-label">Project</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="editFormTextBoxProject" class="editFormElement form-control" name="project">
						</div>
						<label for="editFormTextBoxshape" class="editFormElement col-md-2 control-label">Shapes</label>
						<div class="editFormElement col-md-4">
							<ul id="editFormListshape" class="editFormElement" name="shape">
						</div>
					</div>
					<div class="form-group">
						<label for="editFormfile" class="editFormElement col-md-2 control-label">File</label>
						<div class="editFormElement col-md-4">
							<input type="file" id="editFormfile" class="editFormElement form-control" name="file">
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
		
	</div>
	
	<script type="text/javascript">
		var data = new Object();
		data.systemOnline = "<c:out value='${SystemStatus}'/>";
		if(isPresent(data.systemOnline))
		{
		 data.systemOnline = (data.systemOnline === 'true');
		}
		showDocumentManagement(data);
	</script>
</body>
</html>