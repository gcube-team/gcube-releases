<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<link href='//fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=latin,greek-ext' rel='stylesheet' type='text/css'>
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap-2.3.2.min.css">
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/datepicker.css">
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/admin.css" />	
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
						<label for="addFormListBoxcustomer" class="addFormElement  control-label">Customer</label>
							<div class="addFormElement ">
								<select id="addFormListBoxcustomer" class="addFormElement form-control" name="customer">
									<option value="None">--None--</option>
								</select>
							</div>
						<div class="addFormElement ">
							<button id="addFormBtnisActive" name="isActive" type="button" value="Deactivate" class="addFormElement btn btn-default">Deactivate</button>
						</div>
					</div>
					<div class="form-group">
						<div class="">
							<button type="button" id="addFormCancelButton" class="addFormButton btn btn-default" value="Cancel">Cancel</button>
						</div>
						<div class="">
							<button type="button" id="addFormSaveButton" class="addFormButton btn btn-primary" value="Save">Save</button>
						</div>
					</div>
				</form>
	        </div>
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
	 		<p class="title">Shape Management</p>
		</div>
		
		<div class="adminContainer row">
		
			<div class="mainScreen ">
				<div class="searchCon row">    
					<form id="searchForm" class="searchForm form-horizontal ">
						<div class = "control-group">

							<div class="searchForm form-group">
								<label for="listBoxTerm" class="searchForm ">Term</label>
								<div class="">
									<select id="listBoxTerm" class="searchForm form-control" name="term"> </select>
								</div>
							</div>
						
							<div class="controls">
								<div class="searchForm form-group">
									<div class="searchForm ">
										<button type="button" id="searchButtonLayer" class="searchForm btn btn-primary" value="Search">Search</button>
									</div>
								</div>
							</div>
							
						</div>
					</form>
				</div>
			
		
				<div class="row">
					<div id="map" class="visualization-large"></div>

					<div id="featureActions">
			        	<button type="button" id="layerActionDelete" class = "btn btn-primary" value="DeleteLayer">Delete Layer</button>
			        </div>
				</div>
		        
	        </div>
	        
      </div>
	</div>
</body>
</html>