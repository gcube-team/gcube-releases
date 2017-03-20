<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>		
		
<p id="geoadmin-tags-notificator" style="display:none;"></p>	
<div class="spinner" style="display:none"></div>	

<div align='center' style='display:inline-block;' id="geoadmin-tag-toolbar">	 
  	<button type='button' id='geoadmin-create-tag-button' 	class='btn portlet-button' data-toggle='modal' data-target='#geoadmin-create-tag-modal'	disabled>Create</button> 
	<button type='button' id='geoadmin-edit-tag-button'		class='btn portlet-button' data-toggle='modal' data-target='#geoadmin-edit-tag-modal' 	disabled>Edit info</button>
	<button type='button' id='geoadmin-delete-tag-button' 	class='btn portlet-button' data-toggle='modal' data-target='#geoadmin-delete-tag-modal' disabled>Delete</button>	 	
</div>
		
<table id="geoadmin-tags-datatable" class="dataTable" width="100%">
	<thead>
	    <tr>
	        <th width="24px"class="geoadmin-tag-datatable-cell geoadmin-tag-datatable-checkbox">&#10004;</th>
	        <th width="40%" class="geoadmin-tag-datatable-cell">Tag Name</th>
	        <th width="40%" class="geoadmin-tag-datatable-cell">Tag Description</th>
	        <th class="geoadmin-tag-datatable-cell">Tag ID</th>
	    </tr>
	</thead>
</table>

<div id="geoadmin-create-tag-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-create-tag-modal-header" aria-hidden="true">		
  	<div class="modal-header">
    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    	<h3 id="geoadmin-create-tag-modal-header" class="geoadmin-modal-header">Create a new Tag</h3>
  	</div>	  	
  	<div class="modal-body">
    	<form class="form-horizontal" id="geoadmin-create-tag-form" autocomplete="off">
			<div class="control-group">
			    <label class="control-label" for="geoadmin-create-tag-name">Name</label>
			    <div class="controls">
			      <input type="text" id="geoadmin-create-tag-name" placeholder="Please give a name" class="span10">
			    </div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-create-tag-description">Description</label>
		    	<div class="controls">
		      		<textarea  id="geoadmin-create-tag-description" placeholder="Please give a brief description" class="span10"  rows="6"></textarea>
		    	</div>
			</div>
		</form>
  	</div>	  	
  	<div class="modal-footer">
    	<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
    	<button class="btn portlet-button" id="geoadmin-create-tag-modal-submit"  type="button"  value="" >Create</button>
  	</div>
</div>

<div id="geoadmin-edit-tag-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-edit-tag-modal-header" aria-hidden="true">		
  	<div class="modal-header">
    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    	<h3 id="geoadmin-edit-tag-modal-header" class="geoadmin-modal-header">Edit Tag</h3>
  	</div>	  	
  	<div class="modal-body">
    	<form class="form-horizontal" autocomplete="off">
			<div class="control-group">
			    <label class="control-label" for="geoadmin-edit-tag-name">Name</label>
			    <div class="controls">
			      <input type="text" id="geoadmin-edit-tag-name" placeholder="Please give a new name" class="span10">
			    </div>
			</div>
			<div class="control-group">
				<label class="control-label" for="geoadmin-edit-tag-description">Description</label>
		    	<div class="controls">
		      		<textarea id="geoadmin-edit-tag-description" placeholder="Please give a new description" class="span10" rows="6"></textarea>
		    	</div>
			</div>
		</form>
  	</div>	  	
  	<div class="modal-footer">
    	<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
    	<button class="btn portlet-button" id="geoadmin-edit-tag-modal-submit" type="button" >Update</button>
  	</div>
</div>

<div id="geoadmin-delete-tag-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-delete-tag-modal-header" aria-hidden="true">		
  	<div class="modal-header">
    	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
    	<h3 id="geoadmin-delete-tag-modal-header" class="geoadmin-modal-header">Delete Tag</h3>
  	</div>	  	
  	<div class="modal-body">
		<p id="geoadmin-delete-tag-modal-text" align="center"></p>
  	</div>	  	
  	<div class="modal-footer">
    	<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
    	<button class="btn portlet-button" id="geoadmin-delete-tag-modal-submit" type="button">Delete</button>
  	</div>
</div>	