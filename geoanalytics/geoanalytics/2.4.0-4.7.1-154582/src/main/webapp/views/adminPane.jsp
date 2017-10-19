<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<link href='//fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=latin,greek-ext' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="../resources/css/bootstrap-3.0.0.min.css">
<link rel="stylesheet" href="../resources/css/geoanalytics-admin.css" />

<link rel="icon" type="image/png" href="../resources/img/logo3.png">

<script src="../resources/script/jquery-1.10.2.min.js"></script>
<script src="../resources/script/jquery.dataTables.js"></script>
<script src="../resources/script/TableTools.min.js"></script>
<script src="../resources/script/bootstrap-3.0.0.min.js" > </script>
<script src="../resources/script/dataTables.bootstrap.js"></script>
<script src="../resources/script/utils.js" > </script>
<script src="../resources/script/adminPane.js" > </script>
<script src="../resources/script/adminMenu.js" > </script>
<!--[if lt IE 9]>
		<script src="resources/script/css3-mediaqueries.js"></script>
		<script src="resources/script/eventListenerSupport.js"></script>
<![endif]-->
</head>
<body>
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
	 
	 	<div class="top">
	 		<div class="top-links">
		 		<div class="top-link">
					<div id="logout">
	    				<c:url var="logoutUrl" value="/logout"/>
						<form action="${logoutUrl}" method="post">
							<input type="submit" value="Sign out" />
						</form>
	    			</div>
				</div>
				<div class="top-link">
					<a href="..">User Interface</a>
				</div>
			</div>
			<p class="title">Admin Home</p>
		</div>
	<div class="adminContainer row">
  	<div class="menu col-md-2">
		<table id="menuTbl">
			<div class="logo" >
				<img alt="Geopolis" src="../resources/img/logo3(blue).png" >
			</div>
			<tbody>
				<tr><td><button type="button" id="btnHome" class="menuBtns btn btn-default" value="Home">Home</button></td></tr>
				<tr><td><button id="btnUserManagement" type="button" class="menuBtns btn btn-default" value="User Management">User Management</button></td></tr>
				<tr><td><button type="button" id="btnCustomerManagement" class="menuBtns btn btn-default" value="Customer Management">Customer Management</button></td></tr>
				<tr><td><button id="btnTaxonomyManagement" type="button" class="menuBtns btn btn-default" value="Taxonomy Management">Taxonomy Management</button></td></tr>
				<tr><td><button id="btnShapeManagement" type="button" class="menuBtns btn btn-default" value="Shape Management">Shape Management</button></td></tr>
				<tr><td><button id="btnDocumentManagement" type="button" class="menuBtns btn btn-default" value="Document Management">Document Management</button></td></tr>
				<tr><td><button id="btnDataImport" type="button" class="menuBtns btn btn-default" value="Data Import">Data Import</button></td></tr>
				<tr><td><button id="btnAccounting" type="button" class="menuBtns btn btn-default" value="Accounting">Accounting</button></td></tr>
				<tr><td><button id="btnPresentation" type="button" class="menuBtns btn btn-default" value="Presentation">Presentation</button></td></tr>
				<tr><td><button id="btnDatabaseBackup" type="button" class="menuBtns btn btn-default" value="Database Backup">Database Backup</button></td></tr>
			</tbody>
		</table>
	</div>
	
	<div class="mainScreen col-md-10">
		<table id="statusTbl" class="table table-striped systemStatus">
		</table>
		<table id="statsTbl" class="table table-striped systemStats">
		</table>
		<table id="activityTbl" class="table table-striped activity">
		</table>
		<table id="loginTbl" class="table table-striped login">
		</table>
		<table id="lockTbl" class="table table-striped alert">
		</table>
		<table id="illegalRequestTbl" class="table table-striped alert">
		</table>
		<table id="illegalLayerAccessTbl" class="table table-striped alert">
		</table>
		<table id="illegalLayerZoomTbl" class="table table-striped alert">
		</table>
	</div>
	</div>
	
	<script type="text/javascript">
		var data = new Object();
	    data.systemOnline = "<c:out value='${SystemStatus}'/>";
		data.activeUserCount  = "<c:out value='${ActiveUserCount}'/>";
		data.allUserCount = "<c:out value='${AllUserCount}'/>";
		data.onlineUserCount = "<c:out value='${OnlineUserCount}'/>";
		data.lockedUserCount = "<c:out value='${LockedUserCount}'/>";
		data.allCustomerCount = "<c:out value='${AllCustomerCount}'/>";
		data.activeCustomerCount = "<c:out value='${ActiveCustomerCount}'/>";
		data.shapeCount = "<c:out value='${ShapeCount}'/>";
		data.documentCount = "<c:out value='${DocumentCount}'/>";
		data.documentSize = "<c:out value='${DocumentSize}'/>";
		data.projectCount = "<c:out value='${ProjectCount}'/>";
		data.taxonomyCount = "<c:out value='${TaxonomyCount}'/>";
		data.taxonomyTermCount = "<c:out value='${TaxonomyTermCount}'/>";
		data.workflowCount = "<c:out value='${WorkflowCount}'/>";
		data.workflowTaskCount = "<c:out value='${WorkflowTaskCount}'/>";
		data.illegalRequestAttemptCount = "<c:out value='${IllegalRequestAttemptCount}'/>";
		data.illegalLayerAccessAttemptCount =  "<c:out value='${IllegalLayerAccessAttemptCount}'/>";
		data.illegalLayerZoomAttemptCount =  "<c:out value='${IllegalLayerZoomAttemptCount}'/>";
		
		data.repositorySize = "<c:out value='${RepositorySize}'/>";
		data.repositoryLastSweep = "<c:out value='${RepositoryLastSweep}'/>";
		data.repositoryLastSweepSizeReduction = "<c:out value='${RepositoryLastSweepSizeReduction}'/>";
		
		data.lastUserAction = { uId: "<c:out value = '${LastUserAction.id}' />",
								timestamp : "<c:out value='${LastUserAction.timestamp}' />",
								entityType : "<c:out value='${LastUserAction.entityType}' />",
								action : "<c:out value='${LastUserAction.action}' />"
							  };
		data.lastUserActions = [
		             	      <c:forEach var="lua" items="${LastUserActions}" varStatus="status">  
		             		    {
		             		        uId: "<c:out value ='${lua.getValue().id}'/>",
		             		        timestamp : "<c:out value='${lua.getValue().timestamp}'/>",
		             		        entityType : "<c:out value='${lua.getValue().entityType}'/>",
		             		        action : "<c:out value='${lua.getValue().action}'/>"
		             		    }
		             		    <c:if test="${!status.last}">    
		             		      ,    
		             		    </c:if>    
		                 	</c:forEach> 
		                 ];
	    data.lastDataUpdate = { entityId: "<c:out value='${LastDataUpdate.entityId}' />",
	    	    				entityType: "<c:out value='${LastDataUpdate.entityType}' />",
	    	    				timestamp: "<c:out value='${LastDataUpdate.timestamp}' />"
	    					  };
	    data.lastUserLogin = { uId: "<c:out value = '${LastUserLogin.id}' />",
				timestamp : "<c:out value='${LastUserLogin.timestamp}' />",
			  };
	    
	    data.lastUserLogins = [
     	      <c:forEach var="lul" items="${LastUserLogins}" varStatus="status">  
     		    {
     		        uId: "<c:out value ='${lua.getValue().id}'/>",
     		        timestamp : "<c:out value='${lua.getValue().timestamp}'/>",
     		    }
     		    <c:if test="${!status.last}">    
     		      ,    
     		    </c:if>    
         	</c:forEach> 
         ];
	     if(isPresent(data.systemOnline))
		 {
			 data.systemOnline = (data.systemOnline === 'true');
		 }
		 showAdminInfo("adInfo",data);
	</script>
</body>
</html>