<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />

<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

<link href='//fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=latin,greek-ext' rel='stylesheet' type='text/css'>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/jquery.dataTables.min.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/dataTables.bootstrap.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/modals.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/navbar_top.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/layers_table.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/mapStuff.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/noty-animate.css" />
<link rel="stylesheet" type="text/css" href='<%=request.getContextPath()%>/css/font-awesome.min.css'>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/util-classes.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/spinner.css" />

<link rel="stylesheet" type="text/css" href='<%=request.getContextPath()%>/css/TsvImporter.css'>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/wfsImporter.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/ShapefileImporter.css" />

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/layersDataTable.css" />

<link rel="stylesheet" type="text/css" href='<%=request.getContextPath()%>/css/dataTables/select.dataTables.min.css'>
<link rel="stylesheet" type="text/css" href='<%=request.getContextPath()%>/css/dataTables/buttons.dataTables.min.css'>

<link rel="stylesheet" type="text/css" href='<%=request.getContextPath()%>/modules/datatable/datatable-widget.css'>
<link rel="stylesheet" type="text/css" href='<%=request.getContextPath()%>/modules/import/geotiff/geotiffImporter.css'>

<script src="<%=request.getContextPath()%>/script/jquery-1.12.0.min.js"></script>
<script src="<%=request.getContextPath()%>/script/jquery-ui-1.10.3.min.js"></script>

<script src="<%=request.getContextPath()%>/script/bootstrap-2.3.2.min.js"></script>

<script src="<%=request.getContextPath()%>/script/dataTables/jquery.dataTables.min.js"></script>
<script src="<%=request.getContextPath()%>/script/dataTables/dataTables.tableTools.min.js"></script>
<script src="<%=request.getContextPath()%>/script/dataTables/dataTables.responsive.min.js"></script>
<script src="<%=request.getContextPath()%>/script/dataTables/dataTables.select.min.js"></script>
<script src="<%=request.getContextPath()%>/script/dataTables/dataTables.buttons.min.js"></script>

<script src='<%=request.getContextPath()%>/script/admin.js'></script>
<script src='<%=request.getContextPath()%>/script/admin_commons.js'></script>
<script src='<%=request.getContextPath()%>/script/adminControl.widget.notification.js'></script>
<script src='<%=request.getContextPath()%>/script/jquery.bootstrap.wizard.js'></script>		

<script src='<%=request.getContextPath()%>/script/jquery.tagsinput.js'></script>		
<script src="<%=request.getContextPath()%>/script/jquery.validate.min.js"></script>
<script src="<%=request.getContextPath()%>/script/jquery.validate.additional-methods.min.js"></script>
<script src='<%=request.getContextPath()%>/script/jquery.noty.packaged.min.js'></script>		
<script src='<%=request.getContextPath()%>/script/noty.js'></script>
<script src='<%=request.getContextPath()%>/script/common/notificator.js'></script>

<script src='<%=request.getContextPath()%>/script/jquery.cite.WFSImport.js'></script>
<script src='<%=request.getContextPath()%>/script/jquery.cite.geoanalytics.tsvImporter.js'> </script>
<script src='<%=request.getContextPath()%>/script/jquery.cite.geoanalytics.shapefileImporter.js'> </script>
   	
<%-- <script src='<%=request.getContextPath()%>/script/shapeManagement.js'></script> --%>
<script src='<%=request.getContextPath()%>/script/utils.js'></script>

<%-- <script src="<%=request.getContextPath()%>/script/OpenLayers.js" > </script> --%>
<!-- The line below is only needed for old environments like Internet Explorer and Android 4.x -->
<script src="https://cdn.polyfill.io/v2/polyfill.min.js?features=requestAnimationFrame,Element.prototype.classList,URL"></script>

<script defer="defer" src='<%=request.getContextPath()%>/modules/datatable/datatable-widget.js'></script>
<script defer="defer" src='<%=request.getContextPath()%>/modules/layers/layersManagement.js'></script>
<script defer="defer" src='<%=request.getContextPath()%>/modules/layers/attributeVisualization.js'></script>
<script defer="defer" src='<%=request.getContextPath()%>/modules/tags/tagsManagement.js'></script>
<script defer="defer" src='<%=request.getContextPath()%>/modules/import/importMonitor.js'></script>
<script defer="defer" src='<%=request.getContextPath()%>/modules/config/config.js'></script>
<script defer="defer" src='<%=request.getContextPath()%>/modules/styles/stylesManagement.js'></script>
<script defer="defer" src='<%=request.getContextPath()%>/modules/plugins/pluginsManagement.js'></script>
<script defer="defer" src='<%=request.getContextPath()%>/modules/users/usersListing.js'></script>
<script defer="defer" src='<%=request.getContextPath()%>/modules/import/geotiff/jquery.cite.geoanalytics.geotiffImporter.js'></script>
		
<script defer="defer" type="text/javascript">
	(function() {
		$(document).ready(function () {
			var renderURL = '<portlet:renderURL><portlet:param name="jspPage" value="{url}.jsp" /><portlet:param name="getParams" value="{params}" /></portlet:renderURL>';
			var resourceURL = '<portlet:resourceURL id="{url}?{parameters}" />';
			var resourceURLNoParams = '<portlet:resourceURL id="{url}" />';
			var contextPath = '<%=request.getContextPath()%>/';
			
			window.Admin.init(contextPath, renderURL, resourceURL);		

			window.config.init({
				contextPath : contextPath, 
				resourceURL : resourceURL,
				resourceURLNoParams : resourceURLNoParams,
				renderURL : renderURL
			});
		});
	})();
</script>
		
<div class="container-fluid adminContainer">
	<div id="notificationContainer"></div>	
	
	<div class="tabbable navbarTop">   				
		<div class="row">  				
 			<ul class="nav nav-tabs">

   				<li class="layersTab active">
   					<a href="#tab1" data-toggle="tab">
   						<span class="title row">Layers</span>
   						<span class="description row">Manage uploaded layers</span>
   					</a>
   				</li>

   				<li class="tagsTab">
   					<a href="#tab2" data-toggle="tab">
   						<span class="title row">Tags</span>
   						<span class="description row">Manage tags of layers</span>
   					</a>
   				</li>
   				<li class="stylesTab">
   					<a href="#tab5" data-toggle="tab">
   						<span class="title row">Styles</span>
   						<span class="description row">Manage styles of layers</span>
   					</a>
   				</li>
   				<li class="importTab">
   					<a href="#tab3" data-toggle="tab">
   						<span class="title row">Import Data</span>
   						<span class="description row">Upload geospatial layers</span>
   					</a>
   				</li>
   				<li class="usersTab">
   					<a href="#tab4" data-toggle="tab">
   						<span class="title row">Users</span>
   						<span class="description row">Manage users</span>
   					</a>
   				</li>
   				<li class="pluginsTab">
   					<a href="#tab6" data-toggle="tab">
   						<span class="title row">Plugins</span>
   						<span class="description row">Manage uploaded function plugins</span>
   					</a>
   				</li>	
 			</ul>	
		</div>

		<div class="tab-content">
			<div class="tab-pane active" id="tab1">
				<div id="geoadmin-layers">
					
					<div id="geoadmin-layers-attributes"></div>
				</div>
			</div>

			<div class="tab-pane" id="tab2">
				<div id="geoadmin-tags"></div>
			</div>
			
			<div class="tab-pane" id="tab5">
				<div id="geoadmin-styles"></div>
			</div>
			
			<div class="tab-pane" id="tab3">
				<div class="row" id="importOptions">
					<select class="dropDownSelection portlet-selectbox"></select>
					<div id="geoadmin-import-monitor"></div>
				</div>

				<div class="row" id="contentOfImporter"></div>
			</div>

			<div class="tab-pane" id="tab4">
				<div id="usersListing"></div>
			</div>

			<div class="tab-pane" id="tab6">
				<div id="pluginsManagement"></div>
			</div>
		</div>
	</div>
	
	<div id="InternalServerErrorModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span id="deleteRoleHeader">Internal server error</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<p>An internal server error has occured. Please try again later</p>
	  </div>
	  <div class="modal-footer">
	    <button id="closeInternalServerModal" class="btn portlet-button" data-dismiss="modal" aria-hidden="true">OK</button>
	  </div>
	</div>
</div>

	<!-- Tags input -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/bootstrap.tagsinput/0.4.2/bootstrap-tagsinput.css?01" />
<script src="https://cdn.jsdelivr.net/bootstrap.tagsinput/0.4.2/bootstrap-tagsinput.min.js?01"></script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/script/openlayers3/ol.css" />
<script src="<%=request.getContextPath()%>/script/openlayers3/ol-debug.js" > </script>