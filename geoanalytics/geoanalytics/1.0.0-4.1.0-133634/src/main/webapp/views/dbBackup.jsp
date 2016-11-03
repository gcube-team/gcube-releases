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
<script src="../resources/script/userManagement.js" > </script>
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
	        <form id="addFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="addFormTextBoxsystemName" class="addFormElement col-md-2 control-label">System Name</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addFormTextBoxsystemName" class="addFormElement form-control" name="systemName">
						</div>
						<label for="addFormTextBoxfullName" class="addFormElement col-md-2 control-label">Full Name</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addFormTextBoxfullName" class="addFormElement form-control" name="fullName">
						</div>
					</div>
					<div class="form-group">
						<label for="addFormTextBoxinitials" class="addFormElement col-md-2 control-label">Initials</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addFormTextBoxinitials" class="addFormElement form-control" name="initials">
						</div>
						<label for="addFormTextBoxeMail" class="addFormElement col-md-2 control-label">Email</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addFormTextBoxeMail" class="addFormElement form-control" name="eMail"></div>
						</div>
					<div class="form-group">
						<label for="addFormTextBoxcredential" class="addFormElement col-md-2 control-label">Credential</label>
							<div class="addFormElement col-md-4">
								<input type="text" id="addFormTextBoxcredential" class="addFormElement form-control" name="credential">
						</div>
						<label for="addFormTextBoxexpirationDate" class="addFormElement col-md-2 control-label">Expiration Date</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addFormTextBoxexpirationDate" class="addFormElement form-control" name="expirationDate">
						</div>
					</div>
					<div class="form-group">
						<label for="addFormTextBoxrights" class="addFormElement col-md-2 control-label">Rights</label>
							<div class="addFormElement col-md-4">
								<input type="text" id="addFormTextBoxrights" class="addFormElement form-control" name="rights">
							</div>
						<label for="addFormTextBoxnotificationId" class="addFormElement col-md-2 control-label">Notification Id</label>
							<div class="addFormElement col-md-4">
								<input type="text" id="addFormTextBoxnotificationId" class="addFormElement form-control" name="notificationId">
							</div>
					</div>
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
			<p class="title">User Management</p>
		</div>
		
		<div class="menu">
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
		
		<div class="mainScreen">
			<div class="searchCon">
				<form id="searchForm" class="searchForm form-horizontal">
					<div class="searchForm form-group">
						<label for="textBoxUserNames" class="searchForm col-md-6">User Name(s)</label>
						<label for="textBoxCustomerNames" class="searchForm col-md-6">Customer Name(s)</label>
					</div>
					<div class="searchForm form-group">
						<div class="col-md-6">
							<input type="text" id="textBoxUserNames" class="searchForm form-control" name="userNames" placeholder="All Users">
						</div>
						<div class="col-md-6">
							<input type="text" id="textBoxCustomerNames" class="searchForm col-md-6 form-control" name="customerNames" placeholder="All Customers">
						</div>
					</div>
					<div class="searchForm form-group">
						<div class="searchForm col-md-6">
							<div class="checkbox">
								<label class="searchForm">
									Only active users
									<input type="checkbox" id="activeUsers" class="searchForm" name="activeUsers">
								</label>
							</div>
						</div>
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
						<div class="searchForm col-md-offset-10 col-md-2">
							<button type="button" id="searchButton" class="searchForm btn btn-primary" value="Search">Search</button>
						</div>
					</div>
				</form>
			</div>
		
			<div class="addUserButtonCon">
				<div>
					<button type="button" id="addUserButton" class="btn btn-primary" value="Add New User">Add New User</button>
				</div>
			</div>
			<div id="userTable" class="userTable" style="">
				<p class="heading">User Results</p>
				<table id="userTbl" class="table table-striped table-hover">
				</table>
			</div>
			
			<div id="editForm" class="editForm">
				<form id="editFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="textBoxsystemName" class="editFormElement col-md-2 control-label">System Name</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="textBoxsystemName" class="editFormElement form-control" name="systemName">
						</div>
						<label for="textBoxfullName" class="editFormElement col-md-2 control-label">Full Name</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="textBoxfullName" class="editFormElement form-control" name="fullName">
						</div>
					</div>
					<div class="form-group">
						<label for="textBoxinitials" class="editFormElement col-md-2 control-label">Initials</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="textBoxinitials" class="editFormElement form-control" name="initials">
						</div>
						<label for="textBoxeMail" class="editFormElement col-md-2 control-label">Email</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="textBoxeMail" class="editFormElement form-control" name="eMail"></div>
						</div>
					<div class="form-group">
						<label for="textBoxcredential" class="editFormElement col-md-2 control-label">Credential</label>
							<div class="editFormElement col-md-4"><input type="text" id="textBoxcredential" class="editFormElement form-control" name="credential">
						</div>
						<label for="textBoxexpirationDate" class="editFormElement col-md-2 control-label">Expiration Date</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="textBoxexpirationDate" class="editFormElement form-control" name="expirationDate">
						</div>
					</div>
					<div class="form-group">
						<label for="textBoxrights" class="editFormElement col-md-2 control-label">Rights</label>
							<div class="editFormElement col-md-4">
								<input type="text" id="textBoxrights" class="editFormElement form-control" name="rights">
							</div>
						<label for="textBoxnotificationId" class="editFormElement col-md-2 control-label">Notification Id</label>
							<div class="editFormElement col-md-4">
								<input type="text" id="textBoxnotificationId" class="editFormElement form-control" name="notificationId">
							</div>
					</div>
					<div class="form-group">
						<label for="listBoxcustomer" class="editFormElement col-md-2 control-label">Customer</label>
							<div class="editFormElement col-md-4">
								<select id="listBoxcustomer" class="editFormElement form-control" name="customer">
									<option value="None">--None--</option>
								</select>
							</div>
						<div class="editFormElement col-md-4">
							<button id="btnisActive" name="isActive" type="button" value="Deactivate" class="editFormElement btn btn-default">Deactivate</button>
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
		$(document).ready(
				function() {
					showUserManagement(data);
				});
	</script>
</body>
</html>