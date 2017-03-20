(function (){
	
	'use strict';
	
	var config = {
		contextPath : null,
		resourceURL : null,
		renderURL 	: null,
		init : function(args){
			this.contextPath = args.contextPath;
			this.resourceURL = args.resourceURL;
			this.renderURL = args.renderURL;
			
			window.tags.init();
		},
		createResourceURL : function(resourceId){
			return this.resourceURL.replace('%7Burl%7D', resourceId);
		}		
	}	

	window.config = config;	
})();