<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<p id="geoadmin-styles-notificator" style="height:20px;"></p>
<div class="spinner" style="display: none"></div>

<div align='center' style='display: inline-block;' class="portlet-datatable-toolbar" id="geoadmin-styles-toolbar">
	<div align='center' style='display: inline-block;' class="portlet-datatable-buttons" >
		<button type='button' id='geoadmin-refresh-style-button'>
			<i class="fa fa-refresh" aria-hidden="true"></i>
			Refresh
		</button>
		<button type='button' id='geoadmin-create-style-button' data-toggle='modal' data-target='#geoadmin-create-style-modal' >
			<i class="fa fa-plus-circle" aria-hidden="true"></i>
			Create
		</button>
		<button type='button' id='geoadmin-edit-style-button' data-toggle='modal' class="toggle-on-row-selection" data-target='#geoadmin-edit-style-modal' >
			<i class="fa fa-pencil-square" aria-hidden="true"></i>
			Edit
		</button>
		<button type='button' id='geoadmin-delete-style-button' data-toggle='modal' class="toggle-on-row-selection" data-target='#geoadmin-delete-style-modal' >
			<i class="fa fa-minus-circle" aria-hidden="true"></i>	
			Delete
		</button>
	</div>
</div>

<table id="geoadmin-styles-datatable"></table>

<div id="geoadmin-create-style-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-create-style-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h3 id="geoadmin-create-style-modal-header" class="geoadmin-modal-header">Create</h3>
		<p>Create a new style</p>
		<div class="control-group">
		    <div class="span6">
		        <label id="switch-label" class="control-label" for="style-form-type-toggle">Change to style editor</label>
		    </div>
		    <div class="span6">
                <label class="switch">
                  <input id="style-form-type-toggle" type="checkbox">
                  <span class="slider round"></span>
                </label>
            </div>
        </div>
	</div>

	<div class="modal-body">
	<!-------------------------------------- file form ----------------------------------------------->
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
			<br/>


            <div class="control-group row" id="filesForm" enctype="multipart/form-data">
                <div class="span4">
                    <label>Add Legend Icons</label>
                </div>
                <div class="files-input-group input-group">
                    <div id="styleUpload" class="fileUpload span2">
                        <span>Upload</span>
                        <input id="geoadmin-upload-icons-btn" name="browseIconFiles" type="file" class="upload" multiple/>
                    </div>
                </div>
                <ol id="files-list" class="hidden"></ol>
            </div>
		</form>

        <!---------------------------------------------------------------------------------------------------->
        <!---------------------------------------- STYLE  EDITOR --------------------------------------------->


        <form class="form-horizontal display-hide" id="geoadmin-create-style-editor-form" autocomplete="off" style="display:none">
            <div class="control-group">
                <label class="control-label" for="geoadmin-create-style-editor-name">Name	<span class="makeMeOrange">*</span></label>
                <div class="controls">
                    <input type="text" id="geoadmin-create-style-editor-name" name="styleNameEditor" placeholder="Please give a name" class="span10">
                    <span class="help-inline"></span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="geoadmin-create-style-editor-description">Description</label>
                <div class="controls">
                    <textarea id="geoadmin-create-style-editor-description" name="styleDescription" placeholder="Please give a brief description" class="span10" rows="6"></textarea>
                    <span class="help-inline"></span>
                </div>
            </div>


            <div class="control-group row">
                <h3>Rules</h3>
            </div>
            <div id="rule-container-0">
             <div class="control-group row">
                    <span class="span6">
                        <label>Rule 1</label>
                    </span>

                  </div>
                <div class="control-group row">
                     <label class="control-label" for="geoadmin-create-rule-title-0">Rule Title</label>
                      <div class="controls">
                          <input id="geoadmin-create-rule-title-0" name="ruleTitle" placeholder="Rule Title" class="span10" type="text"></textarea>
                          <span class="help-inline"></span>
                      </div>
                </div>
                <div class="control-group row">
                    <label class="control-label" for="geoadmin-rule-symbol-0">Rule Symbol</label>
                    <div class="controls">
                        <select id="geoadmin-rule-symbol-0" name="ruleSymbol" >
                            <option value="polygon">Polygon</option>
                            <option value="point">Point</option>
                            <option value="line">Line</option>
                        </select>
                    </div>
                </div>
                <div class="control-group row">
                     <label class="control-label" for="geoadmin-rule-property-name-0">Property Name</label>
                      <div class="controls">
                          <input id="geoadmin-rule-property-name-0" name="propertyName" placeholder="Property Name" class="span10" type="text"/>
                          <span class="help-inline"></span>
                      </div>
                </div>
                <div class="control-group row">
                    <label class="control-label" for="geoadmin-rule-property-range-0">Property Range</label>
                    <div class="controls row flex" id="geoadmin-rule-property-range-0">
                        <span class="span1"></span>
                        <input id="geoadmin-rule-property-range-from-0" name="rangeFrom" placeholder="From" class="span3" type="number"/>
                         -
                        <input id="geoadmin-rule-property-range-to-0" name="rangeTo" placeholder="To" class="span3" type="number"/>
                    </div>
                </div>

                <div class="control-group row">
                    <label class="control-label" for="geoadmin-rule-property-fill-0">Fill Color</label>
                    <div class="controls">
                        <select id="geoadmin-rule-property-fill-0" name="fillSelector" >
                            <option value="-">-</option>
                            <option value="red"><span class="color-box" style="background-color: #d80015;"></span>Red</option>
                            <option value="blue"><span class="color-box" style="background-color: #1a4ce0;"></span>Blue</option>
                            <option value="yellow"><span class="color-box" style="background-color: #f2ff00;"></span>Yellow</option>
                            <option value="green"><span class="color-box" style="background-color: #00b226;"></span>Green</option>
                            <option value="icon">Icon</option>
                        </select>
                    </div>
                    <span class="help-inline"></span>
                </div>
                <div id="geoadmin-property-icon-0" class="control-group">
                    <div class="span4">
                        <label class="control-label" for="iconUpload-content-0">Add property icon</label>
                    </div>
                    <div class="span5" id="selectIconInput-0">
                        <input id="iconUpload-content-0" class="span11"  type="text" placeholder="No icon selected" readonly/>
                        <span class="help-inline"></span>
                    </div>
                    <div id="iconUpload-0" class="fileUpload file-icon span2">
                        <span>Upload</span>
                        <input id="geoadmin-upload-icons-btn-0" name="browseIconFiles" type="file" class="upload" />
                    </div>
                </div>
            </div>
            <div class="control-group row">
                <div class="span8">
                    <button id="add-rule-btn" class="btn portlet-button" type="button" >Add Rule</button>
                    <span class="help-inline"></span>
                </div>
            </div>
        </form>

    <!--------------------------------------- END OF style Editor ------------------------------------------------->
	</div>


	<div class="modal-footer">
		<button class="btn portlet-button  pull-left" id="geoadmin-create-style-modal-submit" type="button" value="">Create</button>	
    	<button class="btn portlet-button  pull-left" id="geoadmin-create-style-modal-editor-submit" type="button" value="">Create</button>

		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
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
            <div class="control-group row">
                <div class="span4">
                    <label>Style Editor<label>
                </div>
                <div class="span8">
                    <textarea id="geoadmin-raw-style-editor" name="file-editor" class="span11" rows="5" cols="50" placeholder="Please edit style xml">
                    </textarea>
                </div>
            </div>
		</form>
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button pull-left" id="geoadmin-edit-style-modal-submit" type="button">Update</button>
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
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
		<button class="btn portlet-button pull-left" id="geoadmin-delete-style-modal-submit" type="button">Delete</button>	
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Cancel</button>
	</div>
</div>
