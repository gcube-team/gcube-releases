<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<p id="geoadmin-tags-notificator" style="display: none;"></p>
<div class="spinner" style="display: none"></div>

<div align='center' style='display: inline-block;' class="portlet-datatable-toolbar" id="geoadmin-tags-toolbar">
	<div align='center' style='display: inline-block;' class="portlet-datatable-buttons" >
		<button type='button' id='geoadmin-refresh-tag-button'>
			<i class="fa fa-refresh" aria-hidden="true"></i>
			Refresh
		</button>
		<button type='button' id='geoadmin-create-tag-button' data-toggle='modal' data-target='#geoadmin-create-tag-modal' disabled>
			<i class="fa fa-plus-circle" aria-hidden="true"></i>
			Create
		</button>
		<button type='button' id='geoadmin-edit-tag-button' data-toggle='modal' class="toggle-on-row-selection" data-target='#geoadmin-edit-tag-modal' disabled>
			<i class="fa fa-pencil-square" aria-hidden="true"></i>
			Edit
		</button>
		<button type='button' id='geoadmin-delete-tag-button' data-toggle='modal' class="toggle-on-row-selection" data-target='#geoadmin-delete-tag-modal' disabled>
			<i class="fa fa-minus-circle" aria-hidden="true"></i>	
			Delete
		</button>
	</div>
</div>

<table id="geoadmin-tags-datatable"></table>

<div id="geoadmin-create-tag-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-create-tag-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h4 id="geoadmin-create-tag-modal-header" class="geoadmin-modal-header">Create</h4>
		<p>Create a new tag</p>
	</div>
	<div class="modal-body">
		<form class="form-horizontal" id="geoadmin-create-tag-form" autocomplete="off">
			<div class="control-group">
				<label class="control-label" for="geoadmin-create-tag-name">Name</label>
				<div class="controls">
					<input type="text" id="geoadmin-create-tag-name" name="tagName" placeholder="Please give a name" class="span10"> <span class="help-inline"></span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-create-tag-description">Description</label>
				<div class="controls">
					<textarea id="geoadmin-create-tag-description" name="tagDescription" placeholder="Please give a brief description" class="span10" rows="6"></textarea>
					<span class="help-inline"></span>
				</div>
			</div>
		</form>
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button  pull-left" id="geoadmin-create-tag-modal-submit" type="button" value="">Create</button>	
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
	</div>
</div>

<div id="geoadmin-edit-tag-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-edit-tag-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h4 id="geoadmin-edit-tag-modal-header" class="geoadmin-modal-header">Edit</h4>
		<p>Edit details of tag</p>
	</div>
	<div class="modal-body">
		<form class="form-horizontal" id="geoadmin-edit-tag-form" autocomplete="off">
			<div class="control-group">
				<label class="control-label" for="geoadmin-edit-tag-name">Name</label>
				<div class="controls">
					<input type="text" id="geoadmin-edit-tag-name" name="tagName" placeholder="Please give a new name" class="span10"> <span class="help-inline"></span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-edit-tag-description">Description</label>
				<div class="controls">
					<textarea id="geoadmin-edit-tag-description" name="tagDescription" placeholder="Please give a new description" class="span10" rows="6"></textarea>
					<span class="help-inline"></span>
				</div>
			</div>
		</form>
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button pull-left" id="geoadmin-edit-tag-modal-submit" type="button">Update</button>	
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
	</div>
</div>

<div id="geoadmin-delete-tag-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-delete-tag-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h4 id="geoadmin-delete-tag-modal-header" class="geoadmin-modal-header">Delete</h4>
	</div>
	<div class="modal-body">
		<p id="geoadmin-delete-tag-modal-text" align="center"></p>
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button pull-left" id="geoadmin-delete-tag-modal-submit" type="button">Delete</button>	
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
	</div>
</div>
