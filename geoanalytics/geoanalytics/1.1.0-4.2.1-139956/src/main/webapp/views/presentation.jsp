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
<script src="../resources/script/jquery.dataTables.js"></script>
<script src="../resources/script/TableTools.min.js"></script>
<script src="../resources/script/bootstrap-3.0.0.min.js" > </script>
<script src="../resources/script/dataTables.bootstrap.js"></script>
<script src="../resources/script/bootstrap-datepicker.js" > </script>
<script src="../resources/script/utils.js" > </script>
<script src="../resources/script/adminMenu.js" > </script>
<script src="../resources/script/presentation.js" > </script>
<!--[if lt IE 9]>
		<script src="resources/script/css3-mediaqueries.js"></script>
		<script src="resources/script/eventListenerSupport.js"></script>
<![endif]-->
</head>
<body>
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
	
	<!-- Modal -->
	  <div class="modal fade" id="addStyleModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	          <h4 class="modal-title">Add New Style</h4>
	        </div>
	        <div class="modal-body" id="addStyleModalBody">
	        <form id="addStyleFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="addStyleFormListBoxexisting" class="addFormElement col-md-2 control-label">Copy from existing style</label>
						<div class="addFormElement col-md-4">
							<select id="addStyleFormListBoxexisting" class="addFormElement form-control" name="existing">
								<option value="None">--None--</option>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="addStyleFormTextBoxname" class="addFormElement col-md-2 control-label">Name</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addStyleFormTextBoxname" class="addFormElement form-control" name="name" >
						</div>
					</div>
					<div class="form-group">
						<label for="addStyleFormTextBoxstyle" class="addFormElement col-md-2 control-label">Style definition</label>
						<div class="addFormElement col-md-10">
							<textarea rows="10" id="addStyleFormTextBoxstyle" class="addFormElement form-control" name="style">
							</textarea>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-offset-8 col-md-2">
							<button type="button" id="addStyleFormCancelButton" class="addFormButton btn btn-default" value="Cancel">Cancel</button>
						</div>
						<div class="col-md-1">
							<button type="button" id="addStyleFormSaveButton" class="addFormButton btn btn-primary" value="Save">Save</button>
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
	  <div class="modal fade" id="editStyleModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	          <h4 class="modal-title">Edit Style</h4>
	        </div>
	        <div class="modal-body" id="editStyleModalBody">
	        <form id="editStyleFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="editStyleFormListBoxexisting" class="editFormElement col-md-2 control-label">Copy from existing style</label>
						<div class="editFormElement col-md-4">
							<select id="editStyleFormListBoxexisting" class="editFormElement form-control" name="existing">
								<option value="None">--None--</option>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="editStyleFormTextBoxname" class="editFormElement col-md-2 control-label">Name</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="editStyleFormTextBoxname" class="editFormElement form-control" name="name" >
						</div>
					</div>
					<div class="form-group">
						<label for="editStyleFormTextBoxstyle" class="editFormElement col-md-2 control-label">Style definition</label>
						<div class="editFormElement col-md-10">
							<textarea rows="10" id="editStyleFormTextBoxstyle" class="editFormElement form-control" name="style">
							</textarea>
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-offset-8 col-md-2">
							<button type="button" id="editStyleFormCancelButton" class="editFormButton btn btn-default" value="Cancel">Cancel</button>
						</div>
						<div class="col-md-1">
							<button type="button" id="editStyleFormSaveButton" class="editFormButton btn btn-primary" value="Save">Save</button>
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
	  <div class="modal fade" id="addThemeModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	          <h4 class="modal-title">Add New Theme</h4>
	        </div>
	        <div class="modal-body" id="addThemeModalBody">
	        <form id="addThemeFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="addThemeFormListBoxtemplate" class="addFormElement col-md-2 control-label">Template theme</label>
						<div class="addFormElement col-md-4">
							<select id="addThemeFormListBoxtemplate" class="addFormElement form-control" name="template">
								<option value="None">--None--</option>
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="addThemeFormTextBoxname" class="addFormElement col-md-2 control-label">Name</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addThemeFormTextBoxname" class="addFormElement form-control" name="name" >
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-offset-8 col-md-2">
							<button type="button" id="addThemeFormCancelButton" class="addFormButton btn btn-default" value="Cancel">Cancel</button>
						</div>
						<div class="col-md-1">
							<button type="button" id="addThemeFormSaveButton" class="addFormButton btn btn-primary" value="Save">Save</button>
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
	  
	<div id="presentation">
		
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
			<p class="title">Presentation</p>
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
			<div id="presentation-nav">
				<!-- Nav tabs -->
				<ul class="nav nav-tabs">
				  <li><a href="#presentation-styles" data-toggle="tab">Styles</a></li>
				  <li><a href="#presentation-themes" data-toggle="tab">Themes</a></li>
				  <li><a href="#presentation-layers" data-toggle="tab">Layer Configuration</a></li>
				</ul>
				
				<!-- Tab panes -->
				<div id="rpoc-project-tabs" class="tab-content">
					  <div class="tab-pane active" id="presentation-styles">
					  	 <div class="row">
						  	 <div class="addStyleButtonCon col-md-offset-8 col-md-2">
								<div>
									<button type="button" id="addStyleButton" class="btn btn-primary" value="Add New Style">Add New Style</button>
								</div>
							</div>
						 </div>
						<div class="row">
							<div id="styleTable" class="styleTable col-md-12">
								<p class="heading">Available Styles</p>
								<table id="styleTbl" class="table table-striped table-hover">
								</table>
							</div>
						</div>
					 </div>
					<div class="tab-pane" id="presentation-themes">
						<div class="row">
							<div class="addThemeButtonCon col-md-offset-8 col-md-2">
								<div>
									<button type="button" id="addThemeButton" class="btn btn-primary" value="Add New Theme">Add New Theme</button>
								</div>
							</div>
						</div>
						<div class="row">
							<div id="themeTable" class="themeTable col-md-12">
								<p class="heading">Available Themes</p>
								<table id="themeTbl" class="table table-striped table-hover">
								</table>
							</div>
						</div>
					</div>
				   	<div class="tab-pane" id="presentation-layers">
				   		<div class="row">
							<div class="addLayerConfigButtonCon col-md-offset-8 col-md-2">
								<div>
									<button type="button" id="addLayerConfigButton" class="btn btn-primary" value="Add Layer Style">Add Layer Style</button>
								</div>
							</div>
						</div>
				   		<div class="row">
							<div id="layerConfigTable" class="layerConfigTable col-md-12">
								<p class="heading">Layer Configuration</p>
								<table id="layerConfigTbl" class="table table-striped table-hover">
								</table>
							</div>
						</div>
				   	</div>
			   </div>
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
		 $(document).ready(function () {
			 showPresentation(data);
		});
		
	</script>
</body>
</html>