<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

	 	<!-- Modal -->
	  <div class="modal fade" id="dbOfflineModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	    <div class="modal-dialog">
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	          <h4 class="modal-title">Bring System Offline?</h4>
	        </div>
	        <div class="modal-body" id="dbOfflineModalBody">
	        	<p><span style="float: left; margin: 0 7px 20px 0;"></span>
  					The system has to be brought offline before taking a backup of the database</p>
	        </div>
	         <div class="modal-footer" id="dbOfflineModalFooter">
	          <button type="button" id="dbOfflineModalNoButton" class="btn btn-default" data-dismiss="modal">No</button>
	          <button type="button" id = "dbOfflineModalYesButton" class="btn btn-primary">Yes</button>
	        </div>
	      </div><!-- /.modal-content -->
	    </div><!-- /.modal-dialog -->
	  </div><!-- /.modal -->
	
	<div id="userMan">
		<div class="adminContainer row">
		
		<div class="mainScreen">
			
			<div class="listUsers row">
				
				    <table class="table table-striped" id="users">
     					<thead><th>Name<th/></thead>
     					<tbody></tbody>
    				</table>
			
			</div>
			
		</div>
		</div>
		
	</div>
	
<!-- 	<script type="text/javascript"> 
	 		var data = new Object();
	 		data.systemOnline = "<c:out value='${SystemStatus}'/>";
	 		if(isPresent(data.systemOnline))
	 		{
	 		 data.systemOnline = (data.systemOnline === 'true');
	 		}
	 		showUserManagement(data);
 		</script> -->
