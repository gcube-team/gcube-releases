<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="theme"%>
<theme:defineObjects />
<portlet:defineObjects />


<p id="geoadmin-layers-notificator" style="height: 20px;"></p>
<div class="spinner" style="display: none"></div>

<div align='center' style='display: inline-block;' class="portlet-datatable-toolbar" id="geoadmin-layers-toolbar">
	<div align='center' style='display: inline-block;' class="portlet-datatable-buttons">
		<button type='button' id='geoadmin-refresh-layers-button'>
			<i class="fa fa-refresh" aria-hidden="true"></i> Refresh
		</button>
		
		<button type='button' id='geoadmin-layers-add-external-button' data-toggle='modal' data-target='#geoadmin-layers-add-external-modal'>
			<i class="fa fa-plus-circle" aria-hidden="true"></i> Add External Layer
		</button>
		
		<button type='button' id='geoadmin-layers-render-button' class="toggle-on-row-selection" data-toggle='modal' data-target='#geoadmin-layers-render-modal' disabled>
			<i class="fa fa-map-o" aria-hidden="true"></i> Render in Map
		</button>
		
		<button type='button' id='geoadmin-layers-edit-button' class="toggle-on-row-selection" data-toggle='modal' data-target='#geoadmin-layers-edit-modal' disabled>
			<i class="fa fa-pencil-square" aria-hidden="true"></i> Edit
		</button>
		
		<button type='button' id='geoadmin-layers-delete-button' class="toggle-on-row-selection" data-toggle='modal' data-target='#geoadmin-layers-delete-modal' disabled>
			<i class="fa fa-minus-circle" aria-hidden="true"></i> Delete
		</button>
		
		<button type='button' id='geoadmin-layers-edit-layer-visualisation' class="toggle-on-row-selection" data-toggle='modal' data-target='#edit-layer-visualization-modal' disabled>
			<i class="fa fa-filter" aria-hidden="true"></i> Edit attribute presentation
		</button>
		
		<button type='button' id='geoadmin-layers-download-button' class="toggle-on-row-selection" data-toggle='modal'>
			<i class="fa fa-download" aria-hidden="true"></i> Download Layer
		</button>
		
		<button type='button' id='geoadmin-layers-geonetwork-button' class="hide-on-row-deselection" data-toggle='modal' data-target='#geoadmin-layers-geonetwork-publish-modal'>			
			<i class="fa fa-globe" aria-hidden="true"></i> <span class="button-text"></span>
		</button>
	</div>
</div>

<table id="geoadmin-layers-datatable"></table>

<div id="geoadmin-layers-edit-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-layers-edit-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h4 id="geoadmin-layers-edit-modal-header" class="geoadmin-modal-header">Edit Layer</h4>
		<p>Edit details of Layer</p>
	</div>
	<div class="modal-body">
		<form class="form-horizontal" id="geoadmin-layers-edit-form" autocomplete="off">

			<div class="control-group">
				<label class="control-label" for="geoadmin-layers-edit-name">Name</label>
				<div class="controls">
					<input type="text" id="geoadmin-layers-edit-name" name="layersName" placeholder="Please give a name" class="span11"> <span class="help-inline"></span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-layers-edit-description">Description</label>
				<div class="controls">
					<textarea rows="4" cols="50" id="geoadmin-layers-edit-description" name="layersDescription" placeholder="Please give a description" class="span11"></textarea>
					<span class="help-inline"></span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-layers-edit-creator">Creator</label>
				<div class="controls">
					<input type="text" id="geoadmin-layers-edit-creator" name="layersCreator" class="span11" readonly> <span class="help-inline"></span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-layers-edit-created">Created</label>
				<div class="controls">
					<input type="text" id="geoadmin-layers-edit-created" name="layersCreated" class="span11" readonly> <span class="help-inline"></span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-layers-edit-geocode-system">Geocode System</label>
				<div class="controls">
					<input type="text" id="geoadmin-layers-edit-geocode-system" name="layersGeocodeSystem" class="span11" readonly> <span class="help-inline"></span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-layers-is-template">Is template </label>
				<div class="controls">
					<input type="text" id="geoadmin-layers-edit-is-template" name="layersIsTemplate" class="span11" readonly> <span class="help-inline"></span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-layers-edit-style">Style</label>
				<div class="controls">
					<select id="geoadmin-layers-edit-style" name="layerstyle" class="span11"></select> <span class="help-inline"></span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-layers-edit-replication-factor" class="span4">Replication factor</label>
				<div class="controls">
					<select id="geoadmin-layers-edit-replication-factor" name="layersReplicationFactor" class="span11">
						<option>1</option>
						<option>2</option>
					</select> <span class="help-inline"></span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-layers-edit-tags">Tags</label>
				<div class="controls">
					<input id="geoadmin-layers-edit-tags" type="text" name="layersTags" class="hidden span11">
				</div>
			</div>

		</form>
	</div>

	<div class="modal-footer">
		<button id="geoadmin-layers-edit-modal-delete" class="btn portlet-button small" data-dismiss="modal" aria-hidden="true">Delete</button>
		<button id="geoadmin-layers-edit-modal-save" data-dismiss="modal" class="btn portlet-button small">Save</button>
		<button id="geoadmin-layers-edit-modal-cancel" class="btn portlet-button small" data-dismiss="modal" aria-hidden="true">Cancel</button>
	</div>
</div>

<div id="geoadmin-layers-add-external-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-layers-add-external-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h4 id="geoadmin-layers-add-external-modal-header" class="geoadmin-modal-header">Add Layer from external Geoserver</h4>
	</div>
	<div class="modal-body">
		<form class="form-horizontal" id="geoadmin-layers-add-external-form" autocomplete="off">
			<div class="control-group">
				<label class="control-label" for="geoadmin-layers-add-external-geoserver-url">Geoserver URL</label>
				<div class="controls">
					<input type="text" id="geoadmin-layers-add-external-geoserver-url" name="geoserverUrl" placeholder="Please give a Geoserver URL" class="span11"> <span
						class="help-inline"></span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-layers-add-external-workspace">Workspace</label>
				<div class="controls">
					<input type="text" id="geoadmin-layers-add-external-workspace" name="workspace" placeholder="Please give a workspace" class="span11"> <span class="help-inline"></span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-layers-add-external-name">Name</label>
				<div class="controls">
					<input type="text" id="geoadmin-layers-add-external-name" name="name" placeholder="Please give a name" class="span11"> <span class="help-inline"></span>
				</div>
			</div>
		</form>
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button pull-left" id="geoadmin-layers-add-external-modal-submit" type="button">Add</button>
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
	</div>
</div>

<div id="geoadmin-layers-delete-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-layers-delete-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h4 id="geoadmin-layers-delete-modal-header" class="geoadmin-modal-header">Delete</h4>
	</div>
	<div class="modal-body">
		<p id="geoadmin-layers-delete-modal-text" align="center"></p>
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button pull-left" id="geoadmin-layers-delete-modal-submit" type="button">Delete</button>
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
	</div>
</div>


<div id="geoadmin-layers-render-modal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h4 id="geoadmin-layers-render-modal-header" class="geoadmin-modal-header">Previewing Layer:</h4>
	</div>
	<div class="modal-body">
		<div id="render-map" class="map" tabindex="0"></div>
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Close</button>
	</div>
</div>


<div id="edit-layer-visualization-modal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	<div class="modal-header">
		<div id="blueLineBottom">
			<span>Preview attributes for layer </span> <span id="layerVisualizationHeader"></span>
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		</div>
	</div>
	<div class="modal-body">
		<div id="geoadmin-layers-attributes">
			<p id="geoadmin-attributes-visualization-notificator" style="display: none;"></p>
			<div align='center' style='display: inline-block;' class="portlet-datatable-toolbar" id="geoadmin-layers-attributes-toolbar"></div>

			<table id="layer-attributes-datatable" class="dataTable portlet-datatable" width="100%"></table>
		</div>
	</div>
	<div class="modal-footer">
		<button id="geoadmin-layer-visualization-submit" class="btn portlet-button pull-left" data-dismiss="modal" aria-hidden="true">OK</button>
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Close</button>
	</div>
</div>

<div align='center' style='display: inline-block;' class="portlet-datatable-toolbar" id="geoadmin-layers-toolbar"></div>

<div id="geoadmin-layers-geonetwork-publish-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-layers-geonetwork-publish-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h4 id="geoadmin-layers-geonetwork-publish-modal-header" >GeoNetwork</h4>
		<p>Publish Layer in GeoNetwork</p>
	</div>
	<div class="modal-body"></div>
	<div class="modal-footer">
		<button id="geoadmin-layers-geonetwork-publish-modal-submit" data-dismiss="modal" class="btn portlet-button pull-left" disabled>Publish</button>
		<button id="geoadmin-layers-geonetwork-publish-modal-cancel" data-dismiss="modal" class="btn portlet-button pull-right">Cancel</button>
	</div>
</div>

<div id="geoadmin-layers-geonetwork-unpublish-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-layers-geonetwork-unpublish-modal" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h4 id="geoadmin-layers-geonetwork-unpublish-modal">Unpublish Layer from GeoNetwork</h4>
	</div>
	<div class="modal-body">
		<p id="geoadmin-layers-geonetwork-unpublish-modal-text" align="center"></p>
	</div>
	<div class="modal-footer">
		<button id="geoadmin-layers-geonetwork-unpublish-modal-submit" class="btn portlet-button pull-left" data-dismiss="modal" type="button" >Delete</button>
		<button class="btn portlet-button pull-right" data-dismiss="modal" aria-hidden="true">Cancel</button>
	</div>
</div>