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
<script src="../resources/script/accountingManagement.js" > </script>
<!--[if lt IE 9]>
		<script src="resources/script/css3-mediaqueries.js"></script>
		<script src="resources/script/eventListenerSupport.js"></script>
<![endif]-->
</head>
<body>
	  <!-- Modal -->
	  <div class="modal fade" id="addAccountingModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	          <h4 class="modal-title">Add Accounting Entry</h4>
	        </div>
	        <div class="modal-body" id="addAccountingModalBody">
	        <form id="addFormObj" class="form-horizontal">
					<div class="form-group">
						<label for="addFormListBoxcustomer" class="addFormElement col-md-2 control-label">Customer</label>
						<div class="addFormElement col-md-4">
							<select id="addFormListBoxcustomer" class="addFormElement form-control" name="customer">
							</select>
						</div>
						<label for="addFormListBoxuser" class="addFormElement col-md-2 control-label">User</label>
						<div class="addFormElement col-md-4">
							<select id="addFormListBoxuser" class="addFormElement form-control" name="user">
							</select>
						</div>
					</div>
					<div class="form-group">
						<label for="addFormTextBoxtype" class="addFormElement col-md-2 control-label">Type</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addFormTextBoxtype" class="addFormElement form-control" name="type">
						</div>
						<label for="addFormTextBoxamount" class="addFormElement col-md-2 control-label">Amount</label>
						<div class="addFormElement col-md-4">
							<input type="text" id="addFormTextBoxamount" class="addFormElement form-control" name="units"></div>
						</div>
					<div class="form-group">
						<label for="addFormTextBoxdate" class="addFormElement col-md-2 control-label">Date</label>
							<div class="addFormElement col-md-4">
								<input type="text" id="addFormTextBoxdate" class="addFormElement form-control" name="date">
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
	        <!-- <div class="modal-footer" id="addAccountingModalFooter">
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
	<div id="accountingMan">
		
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
			<p class="title">User &amp; Customer Accounting</p>
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
						<div class="searchForm form-group">
							<label for="textBoxUserNames" class="searchForm col-md-6">User Name(s)</label>
							<label for="textBoxCustomerNames" class="searchForm col-md-6">Customer Name(s)</label>
						</div>
						<div class="controls">
							<div class="searchForm form-group">
								<div class="col-md-6">
									<input type="text" id="textBoxUserNames" class="searchForm form-control" name="userNames" placeholder="All Users">
								</div>
								<div class="col-md-6">
									<input type="text" id="textBoxCustomerNames" class="searchForm col-md-6 form-control" name="customerNames" placeholder="All Customers">
								</div>
							</div>
							<div class="searchForm form-group">
								<div class="col-md-6">
									<input type="text" id="textBoxFrom" class="searchForm form-control" name="from" placeholder="All dates">
								</div>
								<div class="col-md-6">
									<input type="text" id="textBoxTo" class="searchForm col-md-6 form-control" name="to" placeholder="All Dates">
								</div>
							</div>
							<div class="searchForm form-group">
								<div class="col-md-6">
									<select  id="listBoxType" class="searchForm form-control" name="type">
									</select>
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
				
				<div class="addAccountingButtonCon col-md-offset-1 col-md-2">
					<div>
						<button type="button" id="addAccountingButton" class="btn btn-primary" value="Add Accounting Entry">Add Accounting Entry</button>
					</div>
				</div>
			</div>
		
			<div class="row">
				<div id="accountingTable" class="accountingTable col-md-12" style="">
					<p class="heading">Accounting Results</p>
					<table id="accountingTbl" class="table table-striped table-hover">
					</table>
				</div>
			</div>
			
			<div id="editForm" class="editForm">
				<form id="editFormObj" class="form-horizontal">
<%-- 					<div class="form-group">
						<label for="editFormListBoxcustomer" class="editFormElement col-md-2 control-label">Customer</label>
						<div class="editFormElement col-md-4">
							<select id="editFormListBoxcustomer" class="editFormElement form-control" name="customer">
							</select>
						</div>
						<label for="editFormListBoxuser" class="editFormElement col-md-2 control-label">User</label>
						<div class="editFormElement col-md-4">
							<select id="addFormListBoxuser" class="addFormElement form-control" name="user">
							</select>
						</div>
					</div> --%>
					<div class="form-group">
						<label for="editFormTextBoxtype" class="editFormElement col-md-2 control-label">Type</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="editFormTextBoxtype" class="editFormElement form-control" name="type">
						</div>
						<label for="editFormTextBoxamount" class="editFormElement col-md-2 control-label">Amount</label>
						<div class="editFormElement col-md-4">
							<input type="text" id="editFormTextBoxamount" class="editFormElement form-control" name="units"></div>
						</div>
					<div class="form-group">
						<label for="editFormTextBoxdate" class="editFormElement col-md-2 control-label">Date</label>
							<div class="editFormElement col-md-4">
								<input type="text" id="editFormTextBoxdate" class="editFormElement form-control" name="date">
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-offset-8 col-md-2">
							<button type="button" id="editFormCancelButton" class="editFormButton btn btn-default" value="Cancel">Cancel</button>
						</div>
						<div class="col-md-1">
							<button type="button" id="editFormSaveButton" class="editFormButton btn btn-primary" value="Save">Save</button>
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
		showAccountingManagement(data);
	</script>
</body>
</html>