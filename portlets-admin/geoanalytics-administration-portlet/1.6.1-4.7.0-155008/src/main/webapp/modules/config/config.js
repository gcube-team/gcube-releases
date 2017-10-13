(function (){
	
	'use strict';
	
	var config = {
		contextPath : null,
		locale : null,
		nameSpace : null,
		resourceURL : null,
		renderURL 	: null,
		init : function(args) {
			this.contextPath = args.contextPath;
			this.nameSpace = args.nameSpace;
			this.resourceURL = args.resourceURL;
			this.resourceURLNoParams = args.resourceURLNoParams;
			this.renderURL = args.renderURL;
			
			var theData = {};
			theData[this.nameSpace + 'getLocale'] = true;
			
			$.ajax(
				{
					url: config.resourceURL,
					type: 'post',
					datatype:'json',
					data: theData,
					success: function(data) {
						var jsonResponse = JSON.parse(data);
						config.locale = jsonResponse.locale;
						
						window.layers.init();
						window.tags.init();
						window.importMonitor.init();
						window.styles.init();
						window.plugins.init();
						window.usersListing.init();						
					},
					error: function (xhr, ajaxOptions, thrownError) {
						$('#InternalServerErrorModal').modal('show');
					}
				}
			);
			window.config.geoNetworkModule = geoNetworkModule;
			window.config.geoNetworkModule.init();
		},
		createResourceURL : function(resourceId, parameters){
			if(parameters == undefined || parameters == null){
				return this.resourceURLNoParams.replace('%7Burl%7D', resourceId);
			} else {
				var resourceURL = this.resourceURL.replace('%7Burl%7D', resourceId);
				return resourceURL.replace('%7Bparameters%7D', parameters);
			}
		},
        createResourceUrlWithParameters : function(resourceId, parameters) {
            var resourceURL = this.resourceURL.replace('%7Burl%7D', resourceId);
            var parametersString = "";

            var firstParameter = true;

            for ( var property in parameters) {
                if (parameters.hasOwnProperty(property)) {
                    if (firstParameter) {
                        firstParameter = false;
                        parametersString += "?"
                    } else {
                        parametersString += "&"
                    }
                    parametersString += property + "=" + parameters[property];
                }
            }

            return resourceURL.replace('%3F%7Bparameters%7D', parametersString);		    
		},
		showSpinner : function(){
			$(".spinner").show();
		},
		hideSpinner : function(){
			$(".spinner").hide();
		}
	}	

	window.config = config;	
})();