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
<%-- <script src="//code.jquery.com/ui/1.10.3/jquery-ui.js"></script> --%>
<script src="../resources/script/bootstrap-3.0.0.min.js" > </script>
<script src="../resources/script/bootstrap-datepicker.js" > </script>
<script src="../resources/script/utils.js" > </script>
<script src="../resources/script/adminMenu.js" > </script>
<script src="../resources/script/customerManagement.js" > </script>
<!--[if lt IE 9]>
		<script src="resources/script/css3-mediaqueries.js"></script>
		<script src="resources/script/eventListenerSupport.js"></script>
<![endif]-->
</head>
<body>
	  <!-- Add Customer Modal -->
	  <div class="modal fade" id="addCustomerModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	          <h4 class="modal-title">Add New Customer</h4>
	        </div>
	        <div class="modal-body" id="addCustomerModalBody">
				<form id="addFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="addFormTextBoxname" class="addFormElement col-md-2 control-label">Name</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addFormTextBoxname" class="addFormElement form-control" name="name">
						</div>
						<label for="addFormTextBoxeMail" class="addFormElement col-md-2 control-label">eMail</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addFormTextBoxeMail" class="addFormElement form-control" name="eMail">
						</div>
					</div>
					<div class="form-group">
						<label for="addFormTextBoxcode" class="addFormElement col-md-2 control-label">Code</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addFormTextBoxcode" class="addFormElement form-control" name="code">
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
	      </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	  </div><!-- /.modal -->
	  
	  <!-- Add Customer Activation Modal -->
	  <div class="modal fade" id="addActivationModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	          <h4 class="modal-title">Add New Customer Activation</h4>
	        </div>
	        <div class="modal-body" id="addActivationModalBody">
				<form id="addActivationFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="addActivationFormTextBoxstart" class="addActivationFormElement col-md-2 control-label">Start Date</label>
						<div class="addActivationFormElement col-md-4">
							<input type="text" id="addActivationFormTextBoxstart" class="addActivationFormElement form-control" name="startDate">
						</div>
						<label for="addActivationFormTextBoxend" class="addActivationFormElement col-md-2 control-label">End Date</label>
						<div class="addActivationFormElement col-md-4">
							<input type="text" id="addActivationFormTextBoxend" class="addActivationFormElement form-control" name="endDate">
						</div>
					</div>
					<div class="form-group">
						<label for="addActivationFormListBoxcustomer" class="addActivationFormElement col-md-2 control-label">Customer</label>
						<div class="addActivationFormElement col-md-4">
							<select id="addActivationFormListBoxcustomer" class="addActivationFormElement form-control" name="customer">
							</select>
						</div>
						<label for="addActivationFormTextBoxshape" class="addActivationFormElement col-md-2 control-label">Shape</label>
						<div class="addActivationFormElement col-md-4">
							<input type="text" id="addActivationFormTextBoxshape" class="addActivationFormElement form-control" name="shape">
						</div>
					</div>
					<div class="form-group">
						<label for="addActivationFormTextBoxconfig" class="addActivationFormElement col-md-2 control-label">Configuration</label>
						<div class="addActivationFormElement col-md-4">
							<input type="text" id="addActivationFormTextBoxconfig" class="addActivationFormElement form-control" name="config">
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-offset-8 col-md-2">
							<button type="button" id="addActivationFormCancelButton" class="addActivationFormButton btn btn-default" value="Cancel">Cancel</button>
						</div>
						<div class="col-md-1">
							<button type="button" id="addActivationFormSaveButton" class="addActivationFormButton btn btn-primary" value="Save">Save</button>
						</div>
					</div>
				</form>
	        </div>
	      </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	  </div><!-- /.modal -->
	  
	<div id="customerMan">
		
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
			<p class="title">Customer Management</p>
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
						<label for="textBoxCustomerNames" class="searchForm col-md-6">Customer Name(s)</label>
					</div>
					<div class="searchForm form-group">
						<div class="col-md-6">
							<input type="text" id="textBoxCustomerNames" class="searchForm form-control" name="customerNames" placeholder="All Customers">
						</div>
					</div>
					<div class="searchForm form-group">
						<div class="searchForm col-md-6">
							<div class="checkbox">
								<label class="searchForm">
									Only active customers
									<input type="checkbox" id="activeCustomers" class="searchForm" name="activeCustomers">
								</label>
							</div>
						</div>
					</div>
					<div class="searchForm form-group">
						<div class="searchForm col-md-offset-5 col-md-2">
							<button type="button" id="searchButton" class="searchForm btn btn-primary" value="Search">Search</button>
						</div>
					</div>
				</form>
				
				<div class="customerAddButtonCon col-md-offset-1 col-md-2 btn-group-vertical">
					<button type="button" id="addCustomerButton" class="btn btn-primary customerAddButton" value="Add New Customer">Add New Customer</button>
					<button type="button" id="addActivationButton" class="btn btn-primary customerAddButton" value="Add New Activation">Add New Activation</button>
				</div>
			</div>
		

			
			<div class="row">
				<div id="customerTable" class="customerTable col-md-12" style="">
					<p class="heading">Customer Results</p>
					<table id="customerTbl" class="table table-striped table-hover">
					</table>
				</div>
			</div>
			
			<div class="row">
				<div id="activationTable" class="activationTable col-md-12" style="">
					<p class="heading">Customer Activations</p>
					<table id="activationTbl" class="table table-striped table-hover">
					</table>
				</div>
			</div>
			
			<div id="editForm" class="editForm">
				<form id="editFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="textBoxname" class="editFormElement col-md-2 control-label">Name</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="textBoxname" class="editFormElement form-control" name="name">
						</div>
						<label for="textBoxeMail" class="editFormElement col-md-2 control-label">eMail</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="textBoxeMail" class="editFormElement form-control" name="eMail">
						</div>
					</div>
					<div class="form-group">
						<label for="textBoxcode" class="editFormElement col-md-2 control-label">Code</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="textBoxcode" class="editFormElement form-control" name="code">
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
			
			<div id="editActivationForm" class="editActivationForm">
				<form id="editActivationFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="editActivationFormTextBoxstart" class="editActivationFormElement col-md-2 control-label">Start</label>
						<div class="editActivationFormElement col-md-4">
							<input type="text" id="editActivationFormTextBoxstart" class="editFormElement form-control" name="startDate">
						</div>
						<label for="editActivationFormTextBoxend" class="editActivationFormElement col-md-2 control-label">End</label>
						<div class="editActivationFormElement col-md-4">
							<input type="text" id="editActivationFormTextBoxend" class="editActivationFormElement form-control" name="endDate">
						</div>
					</div>
					<div class="form-group">
						<label for="editActivationFormTextBoxshape" class="editActivationFormElement col-md-2 control-label">Shape</label>
						<div class="editActivationFormElement col-md-4">
							<input type="text" id="editActivationFormTextBoxshape" class="editFormElement form-control" name="shape">
						</div>
						<label for="editActivationFormTextBoxconfig" class="editActiavtionFormElement col-md-2 control-label">Configuration</label>
						<div class="editActivationFormElement col-md-4">
							<input type="text" id="editActivationFormTextBoxconfig" class="editActivationFormElement form-control" name="config">
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-offset-8 col-md-2">
							<button type="button" id="editActivationFormCancelButton" class="editActivationFormButton btn btn-default" value="Cancel">Cancel</button>
						</div>
						<div class="col-md-1">
							<button type="button" id="editActivationFormSaveButton" class="editActivationFormButton btn btn-primary" value="Save">Save</button>
						</div>
					</div>
				</form>
			</div>
			
		</div>
		</div>
			
	</div>
	
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
		
	  <script type="text/javascript">
	      var data = new Object();
		  data.systemOnline = "<c:out value='${SystemStatus}'/>";
		  if(isPresent(data.systemOnline))
		  {
			  data.systemOnline = (data.systemOnline === 'true');
		  }
		  showCustomerManagement(data);
	</script>
</body>
</html>