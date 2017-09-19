<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/font-awesome.min.css" />	
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/jquery-ui.min.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/spinner.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap-responsive.min.css">

<script src="<%=request.getContextPath()%>/js/charts-loader.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery-1.12.4.min.js"></script>
<script src="<%=request.getContextPath()%>/js/bootstrap-2.3.2.min.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery-ui-1.12.1.min.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery.validate.min.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery.validate.additional-methods.min.js"></script>
<script src='<%=request.getContextPath()%>/js/jquery.dialogextend.min.js'></script>

									<!-- Libs -->
									
<script src='<%=request.getContextPath()%>/libs/noty/noty.min.js'></script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/libs/noty/noty.css" />

<script src='<%=request.getContextPath()%>/libs/jstree.min.js'></script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/libs/jstree-themes/default/style.min.css" />

									<!-- Custom imports -->

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/modules/utils/responsive.css" />		
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/modules/utils/util-classes.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/modules/utils/notificator.css" />
						
<script src='<%=request.getContextPath()%>/modules/utils/notificator.js'></script>
<script src='<%=request.getContextPath()%>/modules/utils/ajax.js'></script>

									<!-- Analysis -->

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/modules/analysis/analysis.css" />

<script defer="defer" src="<%=request.getContextPath()%>/modules/analysis/dom.js"></script>
<script defer="defer" src='<%=request.getContextPath()%>/modules/analysis/validation.js'></script>	
<script defer="defer" src="<%=request.getContextPath()%>/modules/analysis/analysis.js"></script>

									<!-- Workspace -->

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/modules/workspace/workspace.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/modules/workspace/context-menu.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/modules/workspace/dialogs.css" />
	
<script defer="defer" src="<%=request.getContextPath()%>/modules/workspace/dialogs.js"></script>
<script defer="defer" src="<%=request.getContextPath()%>/modules/workspace/workspace.js"></script>

									<!-- Views -->
									
<jsp:include page="../modules/workspace/dialogs.jsp" />
<jsp:include page="../modules/analysis/analysis.jsp" />

									<!-- Portlet Resource Methods -->	
									
<portlet:defineObjects />

<portlet:resourceURL id="PerformAnalysis" var="PerformAnalysis"/>
<portlet:resourceURL id="SimulFishGrowthDataAPI" var="SimulFishGrowthDataAPI"/>
<portlet:resourceURL id="SimulFishGrowthDataModel" var="SimulFishGrowthDataModel"/>
<portlet:resourceURL id="getWorkspace" var="getWorkspace"/>
<portlet:resourceURL id="getFolders" var="getFolders"/>
<portlet:resourceURL id="saveAnalysis" var="saveAnalysis"/>
<portlet:resourceURL id="createFolder" var="createFolder"/>
<portlet:resourceURL id="folderExists" var="folderExists"/>
<portlet:resourceURL id="loadAnalysis" var="loadAnalysis"/>
<portlet:resourceURL id="renameFile" var="renameFile"/>
<portlet:resourceURL id="removeFile" var="removeFile"/>
<portlet:resourceURL id="getInfo" var="getInfo"/>

<script defer="defer" type="text/javascript">
	(function() {
	    $(document).ready(function () {
	    	window.notificator = $("#tea-noty-container");
	    	
	    	window.dom.init();
	    	
	    	window.analytics.init({
	    		'ContextPath': '<%=request.getContextPath()%>/',
	    		'ResourceURL': '<portlet:resourceURL id="{url}?{params}" />',
	    		'NameSpaceNative': 'portlet:namespace',
	    		'PerformAnalysisUrl': '<%= PerformAnalysis %>'
	    	});
	    	
	    	window.models.init({
	    		'ContextPath': '<%=request.getContextPath()%>/',
	    		'ResourceURL': '<portlet:resourceURL id="{url}?{params}" />',
	    		'NameSpaceNative': 'portlet:namespace',
			    'SimulFishGrowthDataModelUrl'	:	'<%= SimulFishGrowthDataModel %>',
			    'modelsDOM' : $("#tea_production_model")
	    	});	 
	    	
	    	window.workspace.init({
	    		'ContextPath': '<%=request.getContextPath()%>/',
	    		'ResourceURL': '<portlet:resourceURL id="{url}?{params}" />',
	    		'NameSpaceNative'	: 'portlet:namespace',
	    		'getWorkspaceUrl'	: '<%= getWorkspace	%>',
				'getInfoUrl' 		: '<%= getInfo 		%>',
			    'getFoldersUrl'		: '<%= getFolders 	%>',
				'removeFileUrl' 	: '<%= removeFile 	%>',
				'renameFileUrl' 	: '<%= renameFile 	%>',
			    'saveAnalysisUrl' 	: '<%= saveAnalysis %>',
			    'loadAnalysisUrl' 	: '<%= loadAnalysis %>',
				'createFolderUrl' 	: '<%= createFolder %>'
	    	});	
	    	
	    	window.models.getModels();
		});
	})();
</script>