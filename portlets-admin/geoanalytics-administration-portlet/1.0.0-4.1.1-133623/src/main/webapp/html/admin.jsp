<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
<html>
	<head>
		<title>Admin</title>
		<meta charset="UTF-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<link href='//fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=latin,greek-ext' rel='stylesheet' type='text/css'>
		
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/jquery.dataTables.min.css" />
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap-2.3.2.min.css" />
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/dataTables.bootstrap.css" />
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap-combined.min.css" />
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/admin.css" />
		<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/pretify.css" />
		
		
		<script src="<%=request.getContextPath()%>/script/jquery-1.12.0.min.js"></script>
		<script src="<%=request.getContextPath()%>/script/jquery-ui-1.10.3.min.js"></script>
		<script src="<%=request.getContextPath()%>/script/jquery.dataTables.min.js"></script>
		<script src="<%=request.getContextPath()%>/script/bootstrap-2.3.2.min.js"></script>
		<script src="<%=request.getContextPath()%>/script/dataTables.bootstrap.js"></script>
	
		<script src='<%=request.getContextPath()%>/script/admin.js'></script>
		<script src='<%=request.getContextPath()%>/script/jquery.modal.WFSImport.js'></script>
		<script src='<%=request.getContextPath()%>/script/pretify.js'></script>
		<script src='<%=request.getContextPath()%>/script/jquery.bootstrap.wizard.js'></script>
		
		
		<!-- TSV Importer -->	
		
		<portlet:resourceURL id="/importTsv" var="importTsvPath"/>		
		<portlet:resourceURL id="/shapes/listTemplateLayers" var="listTemplateLayers"/>
			
		<link rel="stylesheet" type="text/css" href='<%=request.getContextPath()%>/css/TsvImporter.css'>	
		<link rel="stylesheet" type="text/css" href='<%=request.getContextPath()%>/css/font-awesome.min.css'>
					
		<script src='<%=request.getContextPath()%>/script/jquery.tagsinput.js'></script>		
		<script src="<%=request.getContextPath()%>/script/jquery.validate.min.js"></script>
		<script src="<%=request.getContextPath()%>/script/jquery.validate.additional-methods.min.js"></script>
		<script src='<%=request.getContextPath()%>/script/jquery.cite.geoanalytics.tsvImporter.js'> </script>	
			
		<!---------------------------------------------------------------------------------------------------->
		
		<script>
			$(document).ready(function () {	
				$('#tsvimporter').tsvimporter({
					mode: 				"button" 	,					// or "div" 
					templateLayersURL: 	'<%=listTemplateLayers%>' ,
					importTsvURL:		'<%=importTsvPath %>' 	
				});
			});
		</script>
				
		<script defer="defer" type="text/javascript">
			(function() {
				$(document).ready(function () {
					var renderURL = '<portlet:renderURL><portlet:param name="jspPage" value="{url}.jsp" /><portlet:param name="getParams" value="{params}" /></portlet:renderURL>';
					var resourceURL = '<portlet:resourceURL id="{url}?{params}" />';
					var contextPath = '<%=request.getContextPath()%>/';
					
					window.Admin.init(contextPath, renderURL, resourceURL);	
				});
			}());
		</script>
			
	</head>
	<body>
		
		<div class="container-fluid adminContainer">
<!-- 			<div class="header-top row"> -->
<!-- 				<div id="headerTitleCon" class="adminHome"> -->
<!-- 					<h1 class="headerTitle">Admin Page</h1> -->
<!-- 				</div> -->
<!-- 			</div> -->
	
	
	
	
		    <div id="rootwizard" class="tabbable tabs-left">
    			<ul class="tabUl">
    	  			<li id="0"><a href="#tab0" data-toggle="tab">Import Data From tsv</a></li>
    				<li id="1"><a href="#tab1" data-toggle="tab">Import Data From wfs</a></li>
    			</ul>
    			<div class="tab-content">
	    		    <div class="tab-pane" id="tab0">
					
						<div id="tsvimporter"></div>
	    	   
	    		    </div>
	    		    <div class="tab-pane" id="tab1">
		    	   
		    		   <div id="wfsImporter"></div>
	    	   
	    	    	</div>
    			</div>	
    		</div>

		</div>
		
		
		<div class="loadContainer">
				<div class="loader">
					<img alt="" src="<%=request.getContextPath()%>/img/loading.gif" />
				</div>
			</div>
			
	</body>
</html>