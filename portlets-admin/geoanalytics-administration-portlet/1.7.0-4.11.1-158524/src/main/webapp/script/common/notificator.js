(function () {
	'use strict';
	
	var notificator = {		
		setText : function(target, text, type){
	    	try {
		        var jsonText = JSON.parse(text);
		        text = jsonText;
		    } catch (e) {
		    	
		    }
		    
		    target.hide();
		    
		    switch(type) {
			    case "success":
			    	target.css("color", "#4F8A10");
			        break;
			    case "error":
			    	target.css("color", "red");
			        break;
			    default:
			    	target.css("color", "blue");
			}
		    
		    target.css("text-align", "center");
		    target.html(text != null ? text : "");
		    target.show();
		},
		errorHandling : function (target, jqXHR, exception) {				 
			var msg = '';
		    if(jqXHR.responseText == null || 
		    			jqXHR.responseText.length == 0  || 
		    			jqXHR.responseText.indexOf("tomcat") > -1 || 
		    			jqXHR.responseText.indexOf("Tomcat") > -1){
		    	if (jqXHR.status == 400) {
			        msg = 'Server understood the request, but request content was invalid.';
			    } else if (jqXHR.status == 401) {
			        msg = 'Unauthorized access.';
			    } else if (jqXHR.status == 403) {
			        msg = 'Forbidden resource can\'t be accessed.';
			    } else if (jqXHR.status == 404) {
			        msg = 'Resource not found.';
			    } else if (jqXHR.status == 500) {
			        msg = 'Internal server error.';		          
			    } else if (jqXHR.status == 503) {
			    	msg = 'Service is currently unavailable';	
			    } else if (exception === 'timeout') {
			        msg = 'Server did not respond in time';
			    } else {
			        msg = 'Failed to contact server. Maybe server is offline';
			    }        
		    }else{		    	
		    	msg = jqXHR.responseText;
		    }  
		    
	    	this.setText(target, msg, "error");
		}
	};	
	
	window.notificator = notificator;	
})();