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