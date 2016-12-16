<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
<html>
	<head>
		<meta charset="UTF-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

		<link href='//fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=latin,greek-ext' rel='stylesheet' type='text/css'>
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap-2.3.2.min.css">
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/datepicker.css">
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/admin.css" />
	</head>
	
	<body>
	
<!-- 		<div id="addTaxonomyModal" class="modal fade " tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> -->
<!-- 			<div> -->
<!-- 				<div class="header-top row"> -->
<!-- 					<div class="headerOfModal"> -->
<!-- 						<h1 class="titleOfModal">Add New Taxonomy</h1> -->
<!-- 					</div> -->
<!-- 				</div> -->
<!-- 				<div class="bodyOfModal"> -->
				
				
<!-- 				</div> -->
<!-- 			</div> -->
<!-- 		</div> -->
	
	
	
	
	
	
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
							<label for="addTaxonFormTextBoxname" class="addTaxonFormElement  control-label">Name</label>
							<div class="addTaxonFormElement ">
								<input type="text" id="addTaxonFormTextBoxname" class="addTaxonFormElement form-control" name="name">
							</div>
							<label for="addTaxonFormListBoxtaxonomyClass" class="addTaxonFormElement  control-label">Class</label>
							<div class="addTaxonFormElement">
								<select id="addTaxonFormListBoxtaxonomyClass" class="addTaxonFormElement form-control" name="taxonomyClass">
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="addTaxonCheckBoxuserTaxonomy" class="addTaxonFormELement  control-label">User Taxonomy </label>
							<div class="importForm checkbox">
								<div class = "row">
									<input type="checkbox" id="addTaxonFormCheckBoxuserTaxonomy" class="addTaxonFormElement " name="userTaxonomy">
								</div>
							</div>
						</div>
						<div class="form-group">
							<label for="addTaxonFormTextBoxextraData" class="addTaxonFormElement  control-label">Extra Data</label>
							<div class="addTaxonFormElement">
								<textarea rows="3" id="addTaxonFormTextBoxextraData" class="addTaxonFormElement form-control" name="extraData"></textarea>
							</div>
						</div>
						<div class="form-group scButtons">
							<div class="leftHalf">
								<button type="button" id="addTaxonFormCancelButton" class="addTaxonFormButton btn btn-default" value="Cancel">Cancel</button>
							</div>
							<div class="rightHalf">
								<button type="button" id="addTaxonFormSaveButton" class="addTaxonFormButton btn btn-primary" value="Save">Save</button>
							</div>
						</div>
					</form>
		        </div>
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
							<label for="addTermFormTextBoxname" class="addTermFormElement  control-label">Name</label>
							<div class="addTaxomFormElement ">
								<input type="text" id="addTermFormTextBoxname" class="addTermFormElement form-control" name="name">
							</div>
						</div>
						<div class="form-group">
							<label for="addTermFormTextorder" class="addTermFormElement control-label">Order</label>
							<div class="addTermFormElement ">
								<input type="text" id="addTermFormTextBoxorder" class="addTermFormElement form-control" name="order">
							</div>
						</div>
						<div class="form-group">
							<label for="addTermFormListBoxclassTaxonomy" class="addTermFormElement  control-label">Class: Taxonomy</label>
							<div class="addTermFormElement ">
								<select id="addTermFormListBoxclassTaxonomy" class="addTermFormElement form-control" name="classTaxonomy">
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="addTermFormListBoxclassTerm" class="addTermFormElement  control-label">Class: Term</label>
							<div class="addTermFormElement ">
								<select id="addTermFormListBoxclassTerm" class="addTermFormElement form-control" name="classTerm">
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="addTermFormListBoxparentTaxonomy" class="addTermFormElement control-label">Parent: Taxonomy</label>
							<div class="addTermFormElement ">
								<select id="addTermFormListBoxparentTaxonomy" class="addTermFormElement form-control" name="parentTaxonomy">
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="addTermFormListBoxparentTerm" class="addTermFormElement  control-label">Parent: Term</label>
							<div class="addTermFormElement ">
								<select id="addTermFormListBoxparentTerm" class="addTermFormElement form-control" name="parentTerm">
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="addTermFormListBoxtaxonomy" class="addTermFormElement  control-label">Taxonomy</label>
							<div class="addTermFormElement">
								<select id="addTermFormListBoxtaxonomy" class="addTermFormElement form-control" name="taxonomy">
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="addTermFormTextBoxconfig" class="addTermFormElement  control-label">Configuration</label>
							<div class="addTermFormElement ">
								<textarea rows="5" id="addTermFormTextBoxconfig" class="addTermFormElement form-control" name="configuration">
								</textarea>
							</div>
						</div>
						<div class="form-group scButtons">
							<div class="leftHalf">
								<button type="button" id="addTermFormCancelButton" class="addTermFormButton btn btn-default" value="Cancel">Cancel</button>
							</div>
							<div class="rightHalf">
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
		<div class="row adminContainer">
		
			<div class="mainScreen ">
				
				
				<div class="confs">
				
				
				<div id="searchCon" class="searchCon row">
					<form id="searchForm" class="searchForm form-horizontal ">
						<div class="searchForm form-group">
							<label for="listBoxTaxonomies" class="searchForm ">Select Taxonomy</label>
						</div>
						<div class="searchForm form-group">
							<div class="">
								<select id="listBoxTaxonomies" multiple="multiple" class="searchForm form-control" name="taxonomyNames">
									<option selected="selected" disabled="disabled">All Taxonomies</option>
								</select>
							</div>

						</div>
						<div class="searchForm form-group">
							<div class="searchForm ">
								<div class="checkbox">
									<label class="searchForm">
										Only active taxonomies
										<input type="checkbox" id="activeTaxonomies" class="searchForm" name="activeTaxonomies">
									</label>
								</div>
							</div>
						</div>
						
						
<!-- 						<div class="selectTaxonomy"> -->
<!-- 		   					<div class="btn-group" id="taxonomiesDropdownDiv"> -->
<!-- 		   						<a class="btn dropdown-toggle" data-toggle="dropdown" id="taxonomiesDropdown"> -->
<!-- 		   							Select Taxonomy -->
<!-- 		   						    <span class="caret"></span> -->
<!-- 								</a> -->
<!-- 								<ul class="dropdown-menu"> -->
<!-- 								</ul> -->
<!-- 							</div> -->
<!-- 						</div> -->
						
						
						
						<div class="searchForm form-group">
							<div class="searchForm ">
								<button type="button" id="searchButton" class="searchForm btn btn-primary" value="Search">Search</button>
							</div>
						</div>
					</form>
					
<!-- 					<div class="addTaxonomyButtonCon"> -->
<!-- 						<div class="btn-group-vertical"> -->
<!-- 							<button type="button" id="addTaxonomyButton" class="btn btn-primary" value="Add New Taxonomy">Add New Taxonomy</button> -->
<!-- 							<button type="button" id="taxonomyConfigButton" class="btn btn-primary" value="Taxonomy Configuration">Taxonomy Configuration</button> -->
<!-- 							<button type="button" id="addTermButton" class="btn btn-primary" value="Add New Term">Add New Term</button> -->
<!-- 						</div> -->
<!-- 					</div> -->
				</div>
				
				<div class="addTaxonomyButtonCon">
						<div class="btn-group-vertical">
							<button type="button" id="addTaxonomyButton" class="btn btn-primary" value="Add New Taxonomy">Add New Taxonomy</button>
<!-- 							<button type="button" id="taxonomyConfigButton" class="btn btn-primary" value="Taxonomy Configuration">Taxonomy Configuration</button> -->
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
							<div class="">
								<button type="button" id="taxonomyConfigFormCancelButton" class="taxonomyConfigFormButton btn btn-default" value="Cancel">Cancel</button>
							</div>
							<div class="">
								<button type="button" id="taxonomyConfigFormSaveButton" class="taxonomyConfigFormButton btn btn-primary" value="Save">Save</button>
							</div>
						</div>
					</form>
				</div>
				
				<!-- Edit Taxonomy Form -->
				<div id="editTaxonomyForm" class="editTaxonomyForm">
					<form id="editTaxonFormObj" class="form-horizontal">
							<div class="form-group">
								<label for="editTaxonFormTextBoxname" class="editTaxonFormElement control-label">Name</label>
								<div class="addTaxomFormElement ">
									<input type="text" id="editTaxonFormTextBoxname" class="editTaxonFormElement form-control" name="name">
								</div>
								<label for="editTaxonFormListBoxtaxonomyClass" class="editTaxonFormElement control-label">Class</label>
								<div class="editTaxonFormElement ">
									<select id="editTaxonFormListBoxtaxonomyClass" class="editTaxonFormElement form-control" name="taxonomyClass">
									</select>
								</div>
							</div>
							<div class="form-group">
								<label for="editTaxonFormCheckBoxUserTaxonomy" class="editTaxonFormELement  control-label">User Taxonomy </label>
								<div class="importForm  checkbox">
									<div class = "row">
										<input type="checkbox" id="editTaxonFormCheckBoxuserTaxonomy" class="editTaxonFormElement " name="userTaxonomy">
									</div>
								</div>
								<div class="editFormElement ">
									<button id="editTaxonFormBtnisActive" name="isActive" type="button" value="Deactivate" class="editTermFormElement btn btn-default">Deactivate</button>
								</div>
							</div>
							<div class="form-group">
								<label for="editTaxonFormTextBoxextraData" class="editTaxonFormElement control-label">Extra Data</label>
								<div class="editTaxonFormElement ">
									<textarea rows="3" id="editTaxonFormTextBoxextraData" class="editTaxonFormElement form-control" name="extraData"></textarea>
								</div>
						</div>
							<div class="form-group scButtons">
							<div class="leftHalf">
									<button type="button" id="editTaxonFormCancelButton" class="editTaxonFormButton btn btn-default" value="Cancel">Cancel</button>
								</div>
								<div class="rightHalf">
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
							<label for="editTermFormTextBoxname" class="editTermFormElement control-label">Name</label>
							<div class="editTaxomFormElement ">
								<input type="text" id="editTermFormTextBoxname" class="editTermFormElement form-control" name="name">
							</div>
						</div>
						<div class="form-group">
							<label for="editTermFormTextorder" class="editTermFormElement  control-label">Order</label>
							<div class="editTermFormElement">
								<input type="text" id="editTermFormTextBoxorder" class="editTermFormElement form-control" name="order">
							</div>
						</div>
						<div class="form-group">
							<label for="editTermFormListBoxtaxonomy" class="editTermFormElement  control-label">Taxonomy</label>
							<div class="editTermFormElement ">
								<select id="editTermFormListBoxtaxonomy" class="editTermFormElement form-control" name="taxonomy">
								</select>
							</div>
						</div>
						<div class="form-group">
							<div class="editFormElement ">
								<button id="editTermFormBtnisActive" name="isActive" type="button" value="Deactivate" class="editTermFormElement btn btn-default">Deactivate</button>
							</div>
						</div>
						<div class="form-group">
							<label for="editTermFormListBoxclassTaxonomy" class="editTermFormElement  control-label">Class: Taxonomy</label>
							<div class="editTermFormElement ">
								<select id="editTermFormListBoxclassTaxonomy" class="editTermFormElement form-control" name="classTaxonomy">
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="editTermFormListBoxclassTerm" class="editTermFormElement control-label">Class: Term</label>
							<div class="editTermFormElement ">
								<select id="editTermFormListBoxclassTerm" class="editTermFormElement form-control" name="classTerm">
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="editTermFormListBoxparentTaxonomy" class="editTermFormElement  control-label">Parent: Taxonomy</label>
							<div class="editTermFormElement ">
								<select id="editTermFormListBoxparentTaxonomy" class="editTermFormElement form-control" name="parentTaxonomy">
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="editTermFormListBoxparentTerm" class="editTermFormElement  control-label">Parent: Term</label>
							<div class="editTermFormElement 4">
								<select id="editTermFormListBoxparentTerm" class="editTermFormElement form-control" name="parentTerm">
								</select>
							</div>
						</div>
						<div class="form-group">
							<label for="editTermFormTextBoxconfig" class="editTermFormElement  control-label">Configuration</label>
							<div class="editTermFormElement ">
								<textarea rows="5" id="editTermFormTextBoxconfig" class="editTermFormElement form-control" name="configuration">
								</textarea>
							</div>
						</div>
						<div class="form-group scButtons">
							<div class="leftHalf">
								<button type="button" id="editTermFormCancelButton" class="editTermFormButton btn btn-default" value="Cancel">Cancel</button>
							</div>
							<div class="rightHalf">
								<button type="button" id="editTermFormSaveButton" class="editTermFormButton btn btn-primary" value="Save">Save</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<!--./Edit Taxonomy Term Form -->
			
		</div>
	</div>
</body>
</html>