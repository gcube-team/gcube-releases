<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<button type="button" class="btn portlet-button" id="geoadmin-import-monitor-button" data-toggle="modal" data-target="#geoadmin-import-monitor-modal">Import Monitor</button>

<div id="geoadmin-import-monitor-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="geoadmin-import-monitor-modal-header" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		<h4 id="geoadmin-import-monitor-modal-header" class="geoadmin-modal-header">Import Monitor</h4>
		<p>Monitor the status of submitted layer imports</p>
	</div>
	<div class="modal-body">
	
		<p id="geoadmin-import-monitor-notificator" style="display: none;"></p>
	
		<table id="geoadmin-import-monitor-datatable" class="dataTable" width="100%">
			<thead>
				<tr>
					<th width="5%" class="geoadmin-import-monitor-datatable-cell checkbox" id="geoadmin-import-monitor-datatable-select-all">						
						<input type="checkbox" id="geoadmin-import-monitor-datatable-select-all-checkbox">
						<label for="geoadmin-import-monitor-datatable-select-all-checkbox"></label>
					</th>
					<th width="20%" class="geoadmin-import-monitor-datatable-cell">Name</th>
					<th width="15%" class="geoadmin-import-monitor-datatable-cell">Geocode System</th>
					<th width="25%" class="geoadmin-import-monitor-datatable-cell">Source</th>
					<th width="10%" class="geoadmin-import-monitor-datatable-cell">Type</th>
					<th width="10%" class="geoadmin-import-monitor-datatable-cell">Data Source</th>	
					<th width="10%" class="geoadmin-import-monitor-datatable-cell">Creation Date</th>
					<th width="10%" class="geoadmin-import-monitor-datatable-cell">Status</th>
				</tr>
			</thead>
		</table>
	</div>
	<div class="modal-footer">
		<button class="btn portlet-button" aria-hidden="true" id="geoadmin-import-monitor-modal-refresh">Refresh</button>
		<button class="btn portlet-button" aria-hidden="true" id="geoadmin-import-monitor-modal-clear">Clear</button>
		<button class="btn portlet-button" data-dismiss="modal" aria-hidden="true">Close</button>
	</div>
</div>

<div class="spinner" style="display: none"></div>	