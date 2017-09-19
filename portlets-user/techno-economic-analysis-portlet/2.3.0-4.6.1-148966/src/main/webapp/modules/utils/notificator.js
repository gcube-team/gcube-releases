(function () {
	'use strict';
	
	Noty.setMaxVisible(1);
	
	var notificator = {		
		showNoty : function(notificationDom, text, type, timeout, template){	
			this.closeAllNoty();
			
			if(timeout == null){
				timeout = (type === "success") ? 3000 : false;
			}
			
			notificationDom.removeClass().addClass(type);		 			
			
			if(text != null && text.length > 0){
		    	try {
			        var jsonText = JSON.parse(text);
			        text = jsonText;
			    } catch (e) {
			    	
			    }
				
				new Noty({
				    text: text,
				    type: type,
				    container : "#" + notificationDom.attr("id"),
				    theme: 'relax',
				    progressBar: false,
				    closeWith: ["button"],		// button is hidden using css
				    timeout: timeout,
				    maxVisible:1,
				    animation : {
			            open : null,
			            close : null
			        }
				}).show();	
			}			
		},	
		setDom : function (notificationDom, bodyDom, type){
			this.showNoty(notificationDom, bodyDom, type);
			//notificationDom.find(".noty_body").html(bodyDom);
		},
		errorHandlingNoty : function (notificationDom, jqXHR, exception) {				 
			var msg = '';
		    if(jqXHR.responseText == null || jqXHR.responseText.length == 0){
		    	if (jqXHR.status == 400) {
			        msg = 'Server understood the request, but request content was invalid.';
			    } else if (jqXHR.status == 401) {
			        msg = 'Unauthorized access.';
			    } else if (jqXHR.status == 403) {
			        msg = 'Forbidden resource can\'t be accessed.';
			    } else if (jqXHR.status == 404) {
			        msg = 'Resource not found.';
			    } else if (jqXHR.status == 500) {
			        msg = 'Internal Server Error.';		          
			    } else if (jqXHR.status == 503) {
			    	msg = 'Service unavailable.';	
			    } else if (exception === 'parsererror') {
			        msg = 'Requested parameters failed to be parsed as a valid JSON';
			    } else if (exception === 'timeout') {
			        msg = 'Request took longer than expected. Maybe server is offline.';
			    } else if (exception === 'abort') {
			        msg = 'Ajax request aborted.';
			    } else {
			        msg = 'Uncaught Error.';
			    }      
		    }else{
		    	msg = jqXHR.responseText;
		    }   

	    	this.showNoty(notificationDom, msg, "error");
		},
		errorHandlingText : function (notificationDom, jqXHR, exception) {				 
			var msg = '';
		    if(jqXHR.responseText == null || jqXHR.responseText.length == 0){
		    	if (jqXHR.status == 400) {
			        msg = 'Server understood the request, but request content was invalid.';
			    } else if (jqXHR.status == 401) {
			        msg = 'Unauthorized access.';
			    } else if (jqXHR.status == 403) {
			        msg = 'Forbidden resource can\'t be accessed.';
			    } else if (jqXHR.status == 404) {
			        msg = 'Resource not found.';
			    } else if (jqXHR.status == 500) {
			        msg = 'Internal Server Error.';		          
			    } else if (jqXHR.status == 503) {
			    	msg = 'Service unavailable.';	
			    } else if (exception === 'parsererror') {
			        msg = 'Requested parameters failed to be parsed as a valid JSON';
			    } else if (exception === 'timeout') {
			        msg = 'Request took longer than expected. Maybe server is offline.';
			    } else if (exception === 'abort') {
			        msg = 'Ajax request aborted.';
			    } else {
			        msg = 'Uncaught Error.';
			    }        
		    }else{
		    	msg = jqXHR.responseText;
		    }   

	    	this.showText(notificationDom, msg, "error");
		},
		showText : function(notificationDom, text, type){
	    	try {
		        var jsonText = JSON.parse(text);
		        text = jsonText;
		    } catch (e) {
		    	
		    }
		    notificationDom.hide();
		    if(type === "success"){
		    	notificationDom.html("<p style='color:#4F8A10' align='center'>" + text + "</p>");
		    }else{
		    	notificationDom.html("<p style='color:red' align='center'>" + text + "</p>");
		    }
			notificationDom.slideDown();
		},
		createTooltip : function(notificationDom){
			notificationDom.tooltip();
		},
		setTooltip : function(notificationDom, text, type){
	    	try {
		        var jsonText = JSON.parse(text);
		        text = jsonText;
		    } catch (e) {
		    	
		    }
			notificationDom.prop('title', text);
			if(type === "success"){
				notificationDom.removeClass().addClass("tooltip-success");
			}else{
				notificationDom.removeClass().addClass("tooltip-error");			
			}
		},
		closeAllNoty : function() {
			Noty.closeAll();			
		}
	}	
	
	window.noty = notificator;	
})();