//(function() {
//    var global = {
//    	resources : {    		
//			'getInfo'		: null,
//		    'getFolders'	: null,
//		    'getWorkspace'	: null,
//		    'createFolder'	: null,
//			'removeFile' 	: null,
//			'renameFile' 	: null,
//		    'saveAnalysis' 	: null,
//		    'loadAnalysis'	: null			
//    	},
//    	init : function(){
//    		this.initResources();
//    	},
//    	initResources : function(){
//    		AUI().use('liferay-portlet-url', function(A) {  
//    			var resources = global.resources;
//    			for(var resourceId in resources){
//        			var resourceURL = Liferay.PortletURL.createResourceURL();
//        			resourceURL.setResourceId(resourceId);
//        			resourceURL.setPortletId('Techno-Economic-Analysis-Portlet');
//        			resources[resourceId] = resourceURL.toString();
//    			}  		
//    			global.initEnvironment();
//    		});
//    	},
//    	initEnvironment : function(){
//    		var resources = global.resources;
//    		
//	    	window.notificator = $("#tea-noty-container");
//	    	window.dom.init();
//	    	
//	    	window.analytics.init({
//	    		'PerformAnalysisUrl': resources['PerformAnalysis']
//	    	});
//	    	
//	    	window.models.init({
//			    'SimulFishGrowthDataModelUrl'	:	resources['SimulFishGrowthDataModel'],
//			    'modelsDOM' : $("#tea_production_model")
//	    	});	 
//	    	
//	    	window.workspace.init({
//	    		'getWorkspaceUrl'	: resources['getWorkspace'],
//				'getInfoUrl' 		: resources['getInfo'],
//			    'getFoldersUrl'		: resources['getFolders'],
//				'removeFileUrl' 	: resources['removeFile'],
//				'renameFileUrl' 	: resources['renameFile'],
//			    'saveAnalysisUrl' 	: resources['saveAnalysis'],
//			    'loadAnalysisUrl' 	: resources['loadAnalysis'],
//				'createFolderUrl' 	: resources['createFolder']
//	    	});	
//	    	
//	    	window.models.getModels();
//    	}
//    };
//    
//    window.global = global;
//})();