<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<div id="legend-import-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="legend-monitor-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">�</button>
		<h4 id="legend-import-modal-header" class="geoadmin-modal-header">Import Monitor</h4>
		<p>Monitor the status of submitted layer imports</p>
	</div>
	<div class="modal-body">
        <form class="form-horizontal legend-import-form">

            <div class="control-group row">
                <div class="span4">
                <textarea id="geonetwork-publisher-general-limitation" name="raw-xml" class="span11" rows="1" cols="50" placeholder="Please fill in the limitation of the Layer"></textarea>
                </div>
            </div>
            <div class="control-group row">
                <label>Add Legend file</label>
                <button>Add Legend file</button>
            </div>
            <div class="control-group row">
                <label>Width</label>
                 <div class="span8">
                    <textarea style="resize: none;"
                        id="legend-importer-template-layer-geocode-mapping"
                        class="span12"
                        rows="2"
                        placeholder="Please give the  attribute containing the Geocode"
                        name="geocodeMapping"></textarea>
                     <span class="help-inline"></span>
                </div>
            </div>
        </form>


		<div align='center' style='display: inline-block;' class="portlet-datatable-toolbar" id="geoadmin-import-monitor-toolbar">
			<div align='center' style='display: inline-block;' class="portlet-datatable-buttons" >
				<button type='button' id='geoadmin-import-monitor-refresh-button'>
					<i class="fa fa-refresh" aria-hidden="true"></i>
					Refresh
				</button>
				<button type='button' id='geoadmin-import-monitor-clear-button' class="toggle-on-row-selection" disabled>
					<i class="fa fa-minus-circle" aria-hidden="true"></i>
					Clear
				</button>
			</div>
		</div>

		<table id="geoadmin-import-monitor-datatable"></table>
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Close</button>
	</div>
</div>

<div class="spinner" style="display: none"></div>

