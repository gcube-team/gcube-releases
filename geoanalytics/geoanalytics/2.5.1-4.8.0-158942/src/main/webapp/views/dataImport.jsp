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
<script src="../resources/script/OpenLayers.js" > </script>
<script src="../resources/script/utils.js" > </script>
<script src="../resources/script/showShape.js" > </script>
<script src="../resources/script/adminMenu.js" > </script>
<script src="../resources/script/dataImport.js" > </script>
<!--[if lt IE 9]>
		<script src="resources/script/css3-mediaqueries.js"></script>
		<script src="resources/script/eventListenerSupport.js"></script>
<![endif]-->
</head>
<body>
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
	  </div><!-- /.modal -->
	  
	    <!-- Remove value mappings modal -->
	  <div class="modal" id="removeValueMappingsModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	          <h4 class="modal-title">Remove Value Mappings</h4>
	        </div>
	        <div class="modal-body" id="removeValueMappingsModalBody">
	        	<p><span style="float: left; margin: 0 7px 20px 0;"></span>
  					All defined value mappings for this attribute will be removed. Proceed?</p>
	        </div>
	         <div class="modal-footer" id="removeValueMappingsModalFooter">
	          <button type="button" id="removeValueMappingsModalNoButton" class="btn btn-default" data-dismiss="modal">No</button>
	          <button type="button" id = "removeValueMappingsModalYesButton" class="btn btn-primary">Yes</button>
	        </div>
	      </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	  </div><!-- /.modal -->
	  
	   <!-- Remove document mappings modal -->
	  <div class="modal" id="removeDocumentMappingsModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	          <h4 class="modal-title">Remove Document Mappings</h4>
	        </div>
	        <div class="modal-body" id="removeDocumentMappingsModalBody">
	        	<p><span style="float: left; margin: 0 7px 20px 0;"></span>
  					All defined document mappings for this attribute will be removed. Proceed?</p>
	        </div>
	         <div class="modal-footer" id="removeDocumentMappingsModalFooter">
	          <button type="button" id="removeDocumentModalNoButton" class="btn btn-default" data-dismiss="modal">No</button>
	          <button type="button" id = "removeDocumentMappingsModalYesButton" class="btn btn-primary">Yes</button>
	        </div>
	      </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	  </div><!-- /.modal -->
	  
	   <!-- Replace/Merge Modal -->
	  <div class="modal fade" id="replaceMergeModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <!-- <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button> -->
	          <h4 class="modal-title">Shapes associated with this term already exist</h4>
	        </div>
	        <div class="modal-body" id="replaceMergeModalBody">
	        	<p><span style="float: left; margin: 0 7px 20px 0;"></span>
  					Shapes associated with the selected term already exist in the system. Do you wish to replace the existing shapes 
  					or merge with the existing dataset?</p>
  					<form id="viewReplaceMergeForm" class="form-horizontal">
	  					<div class="form-group">
							<div class="col-md-2">
								<button type="button" id="replaceMergeModalViewButton" class="btn btn-default" value="View">View Existing</button>
							</div>
						</div>
					</form>
					<div id="replaceMergeModalVisualization" class="visualization">
					</div>
	        </div>
	         <div class="modal-footer" id="replaceMergeModalFooter">
	         <button type="button" id="replaceMergeModalCancelButton" class="btn btn-default">Cancel</button>
	          <button type="button" id="replaceMergeModalReplaceButton" class="btn btn-primary">Replace</button>
	          <button type="button" id = "replaceMergeModalMergeButton" class="btn btn-primary">Merge</button>
	        </div>
	      </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	  </div><!-- /.modal -->
	  
	  <!-- Loading Modal -->
	  <div class="modal fade" id="loadingModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <!-- <div class="modal-header">
	          <h4 class="modal-title">Shapes associated with this term already exist</h4>
	        </div> -->
	        <div class="modal-body" id="replaceMergeModalBody">
	        	<p><span style="float: left; margin: 0 7px 20px 0;"></span>
  					Importing data, please wait...</p>
  			</div>
	      </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	  </div><!-- /.modal -->
	  
	<div id="dataImport">
		
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
			<p class="title">Data Import</p>
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
			<div id="importCon" class="importCon row">
				<form id="importForm" class="importForm form-horizontal col-md-7">
					<div class="importForm form-group">
						<label for="inputBoxFiles" class="importForm col-md-2 control-label">Files</label>
						<div class="col-md-4">
							<input type="file" id="inputBoxFiles" multiple="multiple" class="importForm importFormInput input-large form-control" name="inputFiles" placeholder="Select a group of files to upload">
						</div>
						<label for="listBoxCRS" class="importForm col-md-2 control-label">CRS</label>
						<div class="col-md-4">
							<select id="listBoxCRS" class="importForm importFormInput form-control" name="CRS">
							</select>
						</div>
					</div>
					<div class="importForm form-group">
						<label for="listBoxCharset" class="importForm col-md-2 control-label">DBF file charset</label>
						<div class="col-md-4">
							<select id="listBoxCharset" class="importForm importFormInput form-control" name="Charset">
							</select>
						</div>
							<label for="checkBoxForceLonLat" class="importForm col-md-2 control-label">Force Lon-Lat order </label>
							<div class="importForm col-md-4 checkbox">
								<div class = "row">
								<input type="checkbox" id="checkBoxForceLonLat" class="importForm col-md-5" name="forceLonLat">
								</div>
							</div>
	<!-- 					<label class="checkbox">
							Force Lon-Lat order
							<div class="importForm checkbox">
								<input type="checkbox" id="checkBoxForceLonLat" class="importForm" name="forceLonLat">
							</div>
						</label> -->
					</div>
					<div class="importForm form-group">
						<label for="listBoxTaxonomy" class="importForm col-md-2 control-label">Taxonomy</label>
						<div class="col-md-4">
							<select id="listBoxTaxonomy" class="importForm importFormInput form-control" name="taxonomy">
							</select>
						</div>
						<label for="listBoxTerm" class="importForm col-md-2 control-label">Taxonomy term</label>
						<div class="col-md-4">
							<select id="listBoxTerm" class="importForm importFormInput form-control" name="term">
							</select>
						</div>
					</div>
					<div class="boundary form-group">
							<label for="checkBoxBoundary" class="importForm col-md-2 control-label">Compute boundary</label>
							<div class="importForm col-md-2 checkbox">
								<div class = "row">
								<input type="checkbox" id="checkBoxBoundary" class="boundaryForm col-md-10" name="boundary">
								</div>
							</div>
							
					</div>
					<div id="importFormBoundaryGroup" class="importForm form-group">
							<label for="listBoxBoundaryTaxonomy" class="importForm col-md-2 control-label">Boundary taxonomy</label>
							<div class="col-md-4">
								<select id="listBoxBoundaryTaxonomy" class="importForm importFormInput form-control" name="boundaryTaxonomy" disabled="disabled">
								</select>
							</div>
							<label for="listBoxBoundaryTerm" class="boundaryForm col-md-2 control-label">Boundary term</label>
							<div class="col-md-4">
								<select id="listBoxBoundaryTerm" class="boundaryForm importFormInput form-control" name="boundaryTerm" disabled="disabled">
								</select>
							</div>
					</div>
					<div class="importForm form-group">
						<div class="importForm col-md-offset-10 col-md-2">
							<button type="button" id="analyzeButton" class="importForm btn btn-primary" value="Analyze">Analyze</button>
						</div>
					</div>
				</form>
				<div class="col-md-2 col-md-offset-3">
					<button id="updateInfoButton" type="button" class="btn btn-primary">Update Info</button>
				</div>
			</div>
			<div class="row">
					<div class="col-md-offset-10 col-md-2">
						<button type="button" id="importButton" class="importForm btn btn-primary" value="Import">Import</button>
					</div>
				</div>
			<div class="row">
			<div id="attributeTable" class="attributeTable col-md-12" style="">
				<p class="heading">Attributes</p>
				<table id="attrTbl" class="table table-striped table-hover">
				</table>
			</div>
			</div>
			
			<div class="row">
			<div id="successfulImport" class="col-md-6">
				<p id="successfulImportText"></p>
				<form id="successfulImportForm" class="form-horizontal">
					<div class="form-group">
						<button type="button" id="successfulImportFormButtonView" class="btn btn-primary" value="View">View</button>
						<button type="button" id="successfulImportFormButtonDone" class="btn btn-primary" value="Done">Done</button>
					</div>
				</form>
				<div id="successfulImportVisualization" class="visualization" >
				</div>
				<div id="featureInfo">
			
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
		showDataImport(data);
	</script>
</body>
</html>