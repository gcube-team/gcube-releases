(function () {
	'use strict';
	
	var notifier = {		
		showNoty : function(notificator, text, type, timeout){	
			
			$.noty.closeAll();
			
			if(timeout == null){
				timeout = (type === "success") ? 3000 : false;
			}
			
			if(text != null && text.length > 0){
		    	try {
			        var jsonText = JSON.parse(text);
			        text = jsonText;
			    } catch (e) {
			    	
			    }
				
				notificator.noty({
				    text: text,
				    type: type,
				    template: '<div class="noty_message"><span class="noty_text"></span><div class="noty_close"></div></div>',
				    theme: 'relax',
				    closeWith: ['button'],
				    timeout: timeout,
				    maxVisible:1,
				    animation: {
				        open: 'animated flipInX', 
				        close: 'animated flipOutX',
				        easing: 'swing',
				        speed: 400
				    }
				});	
			}			
		},
		setNoty : function (notificator, text, type){	
			notificator.setText(text);
			notificator.setType(type);
		},
		errorHandlingNoty : function (notificator, jqXHR, exception) {				 
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

	    	this.showNoty(notificator, msg, "error");
		},
		errorHandlingText : function (notificator, jqXHR, exception) {				 
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

	    	this.showText(notificator, msg, "error");
		},
		closeNoty : function(notificator){
			if(notificator != null && typeof notificator.close === 'function'){
				notificator.close();				
			}	
		},
		showText : function(notificator, text, type){
	    	try {
		        var jsonText = JSON.parse(text);
		        text = jsonText;
		    } catch (e) {
		    	
		    }
		    notificator.hide();
		    if(type === "success"){
		    	notificator.html("<p style='color:#4F8A10' align='center'>" + text + "</p>");
		    }else{
		    	notificator.html("<p style='color:red' align='center'>" + text + "</p>");
		    }
			notificator.slideDown();
		},
		createTooltip : function(notificator){
			notificator.tooltip();
		},
		setTooltip : function(notificator, text, type){
	    	try {
		        var jsonText = JSON.parse(text);
		        text = jsonText;
		    } catch (e) {
		    	
		    }
			notificator.prop('title', text);
			if(type === "success"){
				notificator.removeClass().addClass("tooltip-success");
			}else{
				notificator.removeClass().addClass("tooltip-error");			
			}
		},
		closeAllNotys : function(){
			$.noty.closeAll();
		}
	}	
	
	window.noty = notifier;	
})();