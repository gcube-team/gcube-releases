<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<p id="geoadmin-styles-notificator" style="display: none;"></p>
<div class="spinner" style="display: none"></div>

<div align='center' style='display: inline-block;' id="geoadmin-style-toolbar">
	<button type='button' id='geoadmin-create-style-button' class='btn portlet-button' data-toggle='modal' data-target='#geoadmin-create-style-modal' disabled>Create</button>
	<button type='button' id='geoadmin-edit-style-button' class='btn portlet-button' data-toggle='modal' data-target='#geoadmin-edit-style-modal' disabled>Edit info</button>
	<button type='button' id='geoadmin-delete-style-button' class='btn portlet-button' data-toggle='modal' data-target='#geoadmin-delete-style-modal' disabled>Delete</button>
</div>

<table id="geoadmin-styles-datatable" class="dataTable" width="100%">
	<thead>
		<tr>
			<th width="24px" class="geoadmin-style-datatable-cell geoadmin-style-datatable-checkbox">&#10004;</th>
			<th width="40%" class="geoadmin-style-datatable-cell">Style Name</th>
			<th width="40%" class="geoadmin-style-datatable-cell">Style Description</th>
			<th class="geoadmin-style-datatable-cell">Style ID</th>
		</tr>
	</thead>
</table>

<div id="geoadmin-create-style-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-create-style-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h3 id="geoadmin-create-style-modal-header" class="geoadmin-modal-header">Create</h3>
		<p>Create a new style</p>		
	</div>
	<div class="modal-body">
		<form class="form-horizontal" id="geoadmin-create-style-form" autocomplete="off">
			<div class="control-group">
				<label class="control-label" for="geoadmin-create-style-name">Name	<span class="makeMeOrange">*</span></label>
				<div class="controls">
					<input type="text" id="geoadmin-create-style-name" name="styleName" placeholder="Please give a name" class="span10">
					<span class="help-inline"></span>					
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-create-style-description">Description</label>
				<div class="controls">
					<textarea id="geoadmin-create-style-description" name="styleDescription" placeholder="Please give a brief description" class="span10" rows="6"></textarea>
					<span class="help-inline"></span>					
				</div>
			</div>
			<div class="control-group">
  				<div class="span4">  
					<label class="control-label" for="geoadmin-create-style-content">Style Editor	<span class="makeMeOrange">*</span></label>
 				</div>		 												
				<div class="span5" id="selectContentInput">															
					<input id="geoadmin-create-style-content" class="span11"  type="text" placeholder="No style selected with xml extension" readonly> 	
 					<span class="help-inline"></span>
				</div>	
				
				<div id="styleUpload" class="fileUpload span2">
				    <span>Upload</span>
				    <input id="geoadmin-create-style-content-browseButton" name="browseStyleFiles" type="file" class="upload" />
				</div>
			</div>
		</form>
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
		<button class="btn portlet-button" id="geoadmin-create-style-modal-submit" type="button" value="">Create</button>
	</div>
</div>

<div id="geoadmin-edit-style-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-edit-style-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h3 id="geoadmin-edit-style-modal-header" class="geoadmin-modal-header">Edit</h3>
		<p>Edit details of style</p>
	</div>
	<div class="modal-body">
		<form class="form-horizontal" id="geoadmin-edit-style-form" autocomplete="off">
			<div class="control-group">
				<label class="control-label" for="geoadmin-edit-style-name">Name</label>
				<div class="controls">
					<input type="text" id="geoadmin-edit-style-name" name="styleName" placeholder="Please give a new name" class="span10">
					<span class="help-inline"></span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-edit-style-description">Description</label>
				<div class="controls">
					<textarea id="geoadmin-edit-style-description" name="styleDescription" placeholder="Please give a new description" class="span10" rows="6"></textarea>
					<span class="help-inline"></span>
				</div>
			</div>
		</form>
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
		<button class="btn portlet-button" id="geoadmin-edit-style-modal-submit" type="button">Update</button>
	</div>
</div>

<div id="geoadmin-delete-style-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-delete-style-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h3 id="geoadmin-delete-style-modal-header" class="geoadmin-modal-header">Delete</h3>
	</div>
	<div class="modal-body">
		<p id="geoadmin-delete-style-modal-text" align="center"></p>
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
		<button class="btn portlet-button" id="geoadmin-delete-style-modal-submit" type="button">Delete</button>
	</div>
</div>
