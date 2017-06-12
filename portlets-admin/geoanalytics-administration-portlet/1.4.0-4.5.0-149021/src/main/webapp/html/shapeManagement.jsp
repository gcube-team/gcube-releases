<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

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
		<div class="adminContainer row">		
			<div class="mainScreen ">
				<div class="searchCon row">   
					<form  id="searchForm"  class="form-horizontal hidden">
						<label for="geoadmin-layers" class="searchForm ">Layer</label>
						<div style="display:inline;">
						      <select id="geoadmin-layers" class="searchForm form-control"> </select>
						      <button type="button" id="searchButtonLayer" class="searchForm btn btn-primary" value="Search">Search</button>		
						</div>
					</form>
					
					<div class="layersDataTableContainer">
					
						<div class="spinner" style="display:none;"></div>
						
						<table id="layersDataTable" class="no-wrap " style="width: 100%;">
							<thead>
								<tr role="row">
									<th>
										<div class="layersDataTableAll"></div>
									</th>
									<th>
										Name
									</th>
									<th>
										Description
									</th>
									<th>
										Geocode System
									</th>
									<th>
										Status
									</th>
									<th>
										Replication Factor
									</th>
									<th>
										Description Tags
									</th>
									<th>
										Creator
									</th>
									<th>
										Created
									</th>
									<th>
										Style
									</th>
								</tr>
							</thead>
						</table>


					<div id="editLayerModal" class="layersModal modal fade in" hidden="true"
						tabindex="-1" role="dialog">
						<div class="modal-header">
							<div class="blueLineBottom">
								<div class="modalHeaderContainer">
									<h4 class="layerModalTitle">Edit</h4>
									<p class="layerModalSubTitle"><span class="layerToEditName"></span></p>
								</div>
								<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
							</div>
						</div>
						<div class="modal-body">
							<div class="editLayerSectionContainer">
								<div class="row-fluid ediLayerPropertiesContainer">
									<label for="editedLayerName" class="pull-left editLayerLabels">
										Name
									</label>
									<input id="editedLayerName" type="text" class="pull-right"/>
								</div>
								
								<div class="row-fluid ediLayerPropertiesContainer">
									<label for="editedLayerDescription" class="pull-left editLayerLabels">
										Description
									</label>
									<textarea rows="4" cols="50" class="pull-right" id="editedLayerDescription"></textarea>
								</div>
								
								<div class="row-fluid ediLayerPropertiesContainer">
									<label for="editedLayerCreator" class="pull-left editLayerLabels">
										Creator
									</label>
									<input id="editedLayerCreator" type="text" class="pull-right" readonly/>
								</div>
								
								
								<div class="row-fluid ediLayerPropertiesContainer">
									<label for="editedLayerCreatedDate" class="pull-left editLayerLabels">
										Created
									</label>
									<input id="editedLayerCreatedDate" type="text" class="pull-right" readonly/>
								</div>
								
								<div class="row-fluid ediLayerPropertiesContainer">
									<label for="editedLayerGeoCodeSystem" class="pull-left editLayerLabels">
										Geocode System
									</label>
									<input id="editedLayerGeoCodeSystem" type="text" class="pull-right" readonly/>	
								</div>
								
								<div class="row-fluid ediLayerPropertiesContainer">
									<label for="editedLayerStatus" class="pull-left editLayerLabels">
										Status
									</label>
									<div class="progressContainer pull-right">
											<span class="ieowjMessage">Not yet available</span>
										<div class="progress">
										  <div class="bar bar-success" style="width: 62%;"></div>
										  62%
										</div>
									</div>
								</div>
								
								<div class="row-fluid ediLayerPropertiesContainer">
									<label for="editedLayerIsTemplate" class="pull-left editLayerLabels">
										Is template
									</label>
									<select id="editedLayerIsTemplate" class="pull-right" disabled>
									  <option value="true">True</option>
									  <option value="false">False</option>
									</select>
								</div>
								
								<div class="row-fluid ediLayerPropertiesContainer">
									<label for="editedLayerStyle" class="pull-left editLayerLabels">
										Style
									</label>
									<select id="editedLayerStyle" class="pull-right">
									</select>
								</div>
								
								<div class="row-fluid ediLayerPropertiesContainer">
									<label for="editedLayerRepFactor" class="pull-left editLayerLabels">
										Replication factor
									</label>
									<select id="editedLayerRepFactor" class="pull-right">
									</select>
								</div>
								
								<div class="row-fluid ediLayerPropertiesContainer">
									<label for="editedLayerStyle" class="pull-left editLayerLabels">
										Tags
									</label>
									<div id="tagsContainer" class="">
										<input id="layersTagsInput" type="text" name="layersTagsInput" class="hidden">
									</div>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<button id="editLayerModalDeleteLayer"
								class="btn portlet-button small" data-dismiss="modal"
								aria-hidden="true">Delete</button>
							<button id="editLayerModalSave" data-dismiss="modal"
								class="btn portlet-button small">Save</button>
							<button id="editLayerModalCancel"
								class="btn portlet-button small" data-dismiss="modal"
								aria-hidden="true">Cancel</button>
						</div>
					</div>


					<div id="deleteLayerModal" class="layersModal modal fade in" hidden="true"
						tabindex="-1" role="dialog">
						<div class="modal-header">
							<div class="blueLineBottom">
								<div class="modalHeaderContainer">
									<h4 class="layerModalTitle">Delete</h4>
									<p class="layerModalSubTitle">Layer</p>
								</div>
								<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
							</div>
						</div>
						<div class="modal-body">
							<p class="modalBodyText">Are you sure you want to remove the selected layer?</p>
						</div>
						<div class="modal-footer">
							<button id="acceptDeleteLayerModal"
								class="btn portlet-button">OK</button>
							<button id="closeDeleteLayerModal"
								class="btn portlet-button" data-dismiss="modal"
								aria-hidden="true">Cancel</button>
						</div>
					</div>
					
				</div>
				</div>
		
				<div id="RenderLayerInMapModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
				  <div class="modal-header">
					<div id="blueLineBottom">
					    <!-- <span>Preview layer</span> -->
					    <span id="layerNameHeader"></span>
					    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
				    </div>
				  </div>
				  <div class="modal-body">
				  	<div id="map" class="map" tabindex="0"></div>
				  </div>
				  <div class="modal-footer">
				    <button id="closeInternalServerModal" class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Close</button>
				  </div>
				</div>

				<div id="featureActions">
		        	<button type="button" id="layerActionDelete" class = "btn btn-primary" value="DeleteLayer">Delete Layer</button>
		        </div>		        
	        </div>	        
      	</div>
	</div>