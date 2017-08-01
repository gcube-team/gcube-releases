<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<div id="geoanalytics-functions-execution-monitor-modal"
	class="modal fade in" tabindex="-1" role="dialog" aria-hidden="true"
	style="display: none">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">×</button>
		<h4 id="geoanalytics-functions-execution-monitor-modal-header"
			class="geoanalytics-functions-execution-monitor-modal-header">Import Monitor</h4>
		<p>Monitor the execution status of submitted funtions</p>
	</div>
	<div class="modal-body">

		<p id="geoanalytics-functions-execution-monitor-notificator"
			style="display: none;"></p>

		<div align='center' style='display: inline-block;'
			class="portlet-datatable-toolbar"
			id="geoanalytics-functions-execution-monitor-toolbar">
			<div align='center' style='display: inline-block;'
				class="portlet-datatable-buttons">
				<button type='button'
					id='geoanalytics-functions-execution-monitor-refresh-execution-status-button'>
					<i class="fa fa-refresh" aria-hidden="true"></i> Refresh
				</button>
				<!-- <button type='button'
					id='geoanalytics-functions-execution-monitor-clear-button'
					class="toggle-on-row-selection" data-toggle='modal' disabled>
					<i class="fa fa-minus-circle" aria-hidden="true"></i> Delete
				</button> -->
			</div>
		</div>

		<table id="geoanalytics-functions-execution-monitor-datatable"></table>
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button" data-dismiss="modal"
			aria-hidden="true">Close</button>
	</div>
</div>