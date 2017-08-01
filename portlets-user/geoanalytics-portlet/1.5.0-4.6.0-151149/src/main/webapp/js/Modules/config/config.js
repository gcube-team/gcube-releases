(function (){
	
	'use strict';
	
	var config = {
		contextPath : null,
		resourceURL : null,
		renderURL 	: null,
		resourceURLNoParams : null,
		init : function(args) {
			this.contextPath = args.contextPath;
			this.resourceURL = args.resourceURL;
			this.resourceURLNoParams = args.resourceURLNoParams;
			this.renderURL = args.renderURL;
			
			window.functionExecutionMonitor.init(hideSpinner, showSpinner);
		},
		createResourceURL : function(resourceId, parameters){
			if(parameters == undefined || parameters == null){
				return this.resourceURLNoParams.replace('%7Burl%7D', resourceId);
			} else {
				var resourceURL = this.resourceURL.replace('%7Burl%7D', resourceId);
				return resourceURL.replace('%7Bparameters%7D', parameters);
			}
		}
	}	

	window.config = config;	
})();