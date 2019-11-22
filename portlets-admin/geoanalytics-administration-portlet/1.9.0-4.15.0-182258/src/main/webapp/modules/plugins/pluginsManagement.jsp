<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<link rel="stylesheet" type="text/css" href='<%=request.getContextPath()%>/modules/plugins/pluginsManagement.css'>

<p id="geoadmin-plugins-notificator" style="display: none;"></p>
<div class="spinner" style="display: none"></div>

<div align='center' style='display: inline-block;' class="portlet-datatable-toolbar" id="geoadmin-plugins-toolbar">
	<div align='center' style='display: inline-block;' class="portlet-datatable-buttons" >
		<button type='button' id='geoadmin-refresh-plugins-button'>
			<i class="fa fa-refresh" aria-hidden="true"></i>
			Refresh
		</button>
		<button type='button' id='geoadmin-create-plugin-button' data-toggle='modal' data-target='#geoadmin-create-plugin-modal'>
			<i class="fa fa-plus-circle" aria-hidden="true"></i>
			Create
		</button>
		<button type='button' id='geoadmin-edit-plugin-button' class="toggle-on-row-selection" data-toggle='modal' data-target='#geoadmin-edit-plugin-modal' disabled>
			<i class="fa fa-pencil-square" aria-hidden="true"></i>
			Edit info
		</button>
		<button type='button' id='geoadmin-delete-plugin-button' class="toggle-on-row-selection" data-toggle='modal' data-target='#geoadmin-delete-plugin-modal' disabled>
			<i class="fa fa-minus-circle" aria-hidden="true"></i>	
			Delete
		</button>
	</div>
</div>

<table id="geoadmin-plugins-datatable" class="dataTable portlet-datatable" width="100%"></table>

<div id="geoadmin-create-plugin-modal" class="modal fade geoadmin-plugins-modals" tabindex="-1" role="dialog"
	data-backdrop="static" data-keyboard="false"
 	aria-labelledby="geoadmin-create-plugin-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h3 id="geoadmin-create-plugin-modal-header" class="geoadmin-modal-header">Create</h3>
		<p>Create a new plugin</p>
	</div>
	<div class="modal-body">
		<form class="form-horizontal geoadmin-plugin-form" id="geoadmin-create-plugin-form" autocomplete="off">
			<div class="control-group">
				<label class="control-label" for="geoadmin-create-plugin-name">Plugin Name	<span class="makeMeOrange">*</span></label>
				<div class="controls">
					<input type="text" id="geoadmin-create-plugin-name" name="createPluginName"
					 placeholder="Name of your plugin" class="span10" required>
					<span class="help-inline"></span>					
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-create-plugin-description">Plugin Description</label>
				<div class="controls">
					<textarea id="geoadmin-create-plugin-description" name="createPluginDescription"
					 placeholder="Brief description" class="span10" rows="6"></textarea>
					<span class="help-inline"></span>					
				</div>
			</div>
			<div class="control-group">
				<label for="geoadmin-create-plugin-pluginLibrary-JAR" class="control-label"> Choose a
					plugin from your disk <span class="pull-right makeMeOrange">*</span>
				</label>
				<div class="controls">
					
					<label class="span3" id="geoadmin-create-plugin-go-to-pluginLibrary-JAR">
						<input class="span6" type="file" name="geoadmin-create-plugin-pluginLibrary-JAR" id="geoadmin-create-plugin-pluginLibrary-JAR"/>
						Browse
					</label>
					
					<div id="pluginFileName" class="span8" title="Choose file">Choose
						file
					</div>
					
					<span class="help-inline"></span>
				</div>
				
			</div>

			<div class="control-group">
				<label class="control-label" for="geoadmin-create-plugin-widget-name">
					 Widget name: <span	class="pull-right makeMeOrange">*</span>
				</label>
				<div class="controls">
					<input class="span10" id="geoadmin-create-plugin-widget-name" name="geoadmin-create-plugin-widget-name" type="text"
					placeholder="Widget name" required />
					<span class="help-inline"></span>
				</div>
			</div>

			<div class="control-group">
				<label class="control-label" for="geoadmin-create-plugin-className">
				 Qualified name of JAVA	class: 
				 <span class="pull-right makeMeOrange">*</span>
				</label>
				<div class="controls">
					<input class="span10" id="geoadmin-create-plugin-className" name="geoadmin-create-plugin-className" type="text" required 
					placeholder="JAVA class" />
					<span class="help-inline"></span>
				</div>
				 
			</div>

			<div class="control-group">
				<label class="control-label" for="geoadmin-create-plugin-methodName">
				 JAVA-Method name: 
					<span class="pull-right makeMeOrange">*</span>
				</label>
				<div class="controls">
					<input class="span10" id="geoadmin-create-plugin-methodName" name="geoadmin-create-plugin-methodName" type="text" required 
					placeholder="Method name"/>
					<span class="help-inline"></span>
				</div>
			</div>

			<div class="control-group">
				<label class="control-label" for="geoadmin-create-plugin-jsFileName"> 
					Script file name: 
					<span class="pull-right makeMeOrange">*</span>
				</label> 
				<div class="controls">
					<input class="span10" id="geoadmin-create-plugin-jsFileName" name="geoadmin-create-plugin-jsFileName" type="text"	required 
					placeholder="Name of the script"/>
					<span class="help-inline"></span>
				</div>
			</div>

			<div class="control-group">
				<label class="control-label" for="geoadmin-create-plugin-configurationClass"> 
					Configuration class:
					<span class="pull-right makeMeOrange">*</span>
				</label> 
				<div class="controls">
					<input class="span10" id="geoadmin-create-plugin-configurationClass" name="geoadmin-create-plugin-configurationClass" type="text" required
					placeholder="Qualified name of configuration class"/>
					<span class="help-inline"></span>
				</div>
			</div>

		</form>
		
		<!-- <button class="btn portlet-button" id="geoadmin-create-plugin-modal-clear-form" type="button" value="">Clear</button> -->
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
		<button class="btn portlet-button" id="geoadmin-create-plugin-modal-submit" type="button" value="">Create</button>
	</div>
</div>

<div id="geoadmin-edit-plugin-modal" class="modal fade geoadmin-plugins-modals" tabindex="-1" role="dialog"
 aria-labelledby="geoadmin-edit-plugin-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h3 id="geoadmin-edit-plugin-modal-header" class="geoadmin-modal-header">Edit</h3>
		<p>Edit details of plugin</p>
	</div>
	<div class="modal-body">
		<form class="form-horizontal geoadmin-plugin-form" id="geoadmin-edit-plugin-form" autocomplete="off">
			<div class="control-group">
				<label class="control-label" for="geoadmin-edit-plugin-name">Plugin Name	<span class="makeMeOrange">*</span></label>
				<div class="controls">
					<input type="text" id="geoadmin-edit-plugin-name" name="editPluginName"
					 placeholder="Name of your plugin" class="span10" required>
					<span class="help-inline"></span>					
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-edit-plugin-description">Plugin Description</label>
				<div class="controls">
					<textarea id="geoadmin-edit-plugin-description" name="editPluginDescription"
					 placeholder="Brief description" class="span10" rows="6"></textarea>
					<span class="help-inline"></span>					
				</div>
			</div>
			<div class="control-group hidden">
				<label for="geoadmin-edit-plugin-pluginLibrary-JAR" class="control-label"> Choose a
					plugin from your disk <span class="pull-right makeMeOrange">*</span>
				</label>
				<div class="controls">
					<label class="" id="geoadmin-edit-plugin-go-to-pluginLibrary-JAR">
						<input class="span6" type="file" name="geoadmin-edit-plugin-pluginLibrary-JAR" id="geoadmin-edit-plugin-pluginLibrary-JAR"/>
						Browse
					</label>
					<div id="pluginFileName" class="span5" title="Choose file">Choose
						file
					</div>
					<span class="help-inline"></span>
				</div>
				
			</div>

			<div class="control-group">
				<label class="control-label" for="geoadmin-edit-plugin-widget-name">
					 Widget name: <span	class="pull-right makeMeOrange">*</span>
				</label>
				<div class="controls">
					<input class="span10" id="geoadmin-edit-plugin-widget-name" name="geoadmin-edit-plugin-widget-name" type="text"
					placeholder="Widget name" required />
					<span class="help-inline"></span>
				</div>
			</div>

			<div class="control-group">
				<label class="control-label" for="geoadmin-edit-plugin-className">
				 Qualified name of JAVA	class: 
				 <span class="pull-right makeMeOrange">*</span>
				</label>
				<div class="controls">
					<input class="span10" id="geoadmin-edit-plugin-className" name="geoadmin-edit-plugin-className" type="text" required 
					placeholder="JAVA class" />
					<span class="help-inline"></span>
				</div>
				 
			</div>

			<div class="control-group">
				<label class="control-label" for="geoadmin-edit-plugin-methodName">
				 JAVA-Method name: 
					<span class="pull-right makeMeOrange">*</span>
				</label>
				<div class="controls">
					<input class="span10" id="geoadmin-edit-plugin-methodName" name="geoadmin-edit-plugin-methodName" type="text" required 
					placeholder="Method name"/>
					<span class="help-inline"></span>
				</div>
			</div>

			<div class="control-group">
				<label class="control-label" for="geoadmin-edit-plugin-jsFileName"> 
					Script file name: 
					<span class="pull-right makeMeOrange">*</span>
				</label> 
				<div class="controls">
					<input class="span10" id="geoadmin-edit-plugin-jsFileName" name="geoadmin-edit-plugin-jsFileName" type="text"	required 
					placeholder="Name of the script"/>
					<span class="help-inline"></span>
				</div>
			</div>

			<div class="control-group">
				<label class="control-label" for="geoadmin-edit-plugin-configurationClass"> 
					Configuration class:
					<span class="pull-right makeMeOrange">*</span>
				</label> 
				<div class="controls">
					<input class="span10" id="geoadmin-edit-plugin-configurationClass" name="geoadmin-edit-plugin-configurationClass" type="text" required
					placeholder="Qualified name of configuration class"/>
					<span class="help-inline"></span>
				</div>
			</div>

		</form>
		<!-- <button class="btn portlet-button" id="geoadmin-edit-plugin-modal-clear-form" type="button">Clear</button> -->
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
		<button class="btn portlet-button" id="geoadmin-edit-plugin-modal-submit" type="button">Update</button>
	</div>
</div>

<div id="geoadmin-delete-plugin-modal" class="modal fade geoadmin-plugins-modals" tabindex="-1" role="dialog" aria-labelledby="geoadmin-delete-plugin-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h3 id="geoadmin-delete-plugin-modal-header" class="geoadmin-modal-header">Delete</h3>
	</div>
	<div class="modal-body">
		<p id="geoadmin-delete-plugin-modal-text" align="center">Are you sure you want to delete <span id="plugiNameToDelete" style="font-weight:bold"></span>?</p>
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
		<button class="btn portlet-button" id="geoadmin-delete-plugin-modal-submit" type="button">Delete</button>
	</div>
</div>