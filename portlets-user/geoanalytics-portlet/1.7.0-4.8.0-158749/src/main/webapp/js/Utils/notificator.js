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
		    target.html(text);
		    target.slideDown();
		},
		errorHandling : function (target, jqXHR, exception) {				 
			var msg = '';
		    if(jqXHR.responseText == null || jqXHR.responseText.length == 0 || (jqXHR.responseText.indexOf('Tomcat') > -1) ){
		    	if (jqXHR.status == 400) {
			        msg = 'Error 400. Server understood the request, but request content was invalid.';
			    } else if (jqXHR.status == 401) {
			        msg = 'Error 401. Unauthorized access.';
			    } else if (jqXHR.status == 403) {
			        msg = 'Error 403. Forbidden resource can\'t be accessed.';
			    } else if (jqXHR.status == 404) {
			        msg = 'Error 404. Resource not found.';
			    } else if (jqXHR.status == 500) {
			        msg = 'Error 500. Internal Server Error.';		          
			    } else if (jqXHR.status == 503) {
			    	msg = 'Error 503. Service unavailable.';	
			    } else if (exception === 'parsererror') {
			        msg = 'Requested parameters failed to be parsed as a valid JSON';
			    } else if (exception === 'timeout') {
			        msg = 'Request took longer than expected. Maybe service is offline.';
			    } else if (exception === 'abort') {
			        msg = 'Request aborted.';
			    } else {
			        msg = 'Could not complete the request.';
			    }        
		    }else{
		    	msg = jqXHR.responseText;
		    }   

	    	this.setText(target, msg, "error");
		}
	};	
	
	window.notificator = notificator;	
})();