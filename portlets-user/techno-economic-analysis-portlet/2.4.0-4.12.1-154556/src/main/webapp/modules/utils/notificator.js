(function() {
	'use strict';

	Noty.setMaxVisible(1);

	var notificator = {
		timeoutEvent : null,
	    showText : function(target, text, type, timeout) {
			this.closeAllNoty();
			
			this.timeoutEvent != null && clearTimeout(this.timeoutEvent);
			
		    try {
			    var jsonText = JSON.parse(text);
			    text = jsonText;
		    } catch (e) {
		    	
		    }

		    switch (type) {
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
		    target.css('visibility', 'visible');
		    
		    if(type === "success"){
		    	this.timeoutEvent = setTimeout(function(){
				    target.html("");
			    }, 6000);
		    }
	    },
		showNoty : function(target, text, type, timeout){	
			this.clearNotification(target);
			
			if(timeout == null){
				timeout = (type === "success") ? 3000 : false;
			}
			
			target.removeClass().addClass(type);		 			
			
			if(text != null && text.length > 0){
		    	try {
			        var jsonText = JSON.parse(text);
			        text = jsonText;
			    } catch (e) {
			    	
			    }
				
				new Noty({
				    text: text,
				    type: type,
				    container : "#" + target.attr("id"),
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
		errorHandlingText : function (target, jqXHR, exception) {	
			var text = this.errorHandling(jqXHR, exception);
	    	this.showText(target, text, "error");
		},
		errorHandlingNoty : function (target, jqXHR, exception) {	
			var text = this.errorHandling(jqXHR, exception);
			this.showNoty(target, text, "error");
		},	
		errorHandling : function (jqXHR, exception) {				 
			var text = '';
			
		    if(jqXHR.responseText == null || 
		    			jqXHR.responseText.length == 0  || 
		    			jqXHR.responseText.indexOf("tomcat") > -1 || 
		    			jqXHR.responseText.indexOf("Tomcat") > -1){
		    	if (jqXHR.status == 400) {
			        text = 'Server understood the request, but request content was invalid.';
			    } else if (jqXHR.status == 401) {
			        text = 'Unauthorized access.';
			    } else if (jqXHR.status == 403) {
			        text = 'Forbidden resource can\'t be accessed.';
			    } else if (jqXHR.status == 404) {
			        text = 'Resource not found.';
			    } else if (jqXHR.status == 500) {
			        text = 'Internal server error.';		          
			    } else if (jqXHR.status == 503) {
			    	text = 'Service is currently unavailable';	
			    } else if (exception === 'timeout') {
			        text = 'Server did not respond in time';
			    } else {
			        text = 'Failed to contact server. Maybe server is offline';
			    }        
		    }else{		    	
		    	text = jqXHR.responseText;
		    }  
		    
		    try {
			    var jsonText = JSON.parse(text);
			    text = jsonText;
		    } catch (e) {
		    	
		    }
		    
		    return text;
		},
	    closeAllNoty : function() {
		    Noty.closeAll();
	    },
		createTooltip : function (target, content, items, tooltipClass){
			this.destroyTooltip(target);		// destroying existing tooltip to create the new one (if exists)			

		    target.tooltip({
		        items : items,
		        content : content,
		        tooltipClass: tooltipClass || ""
		    }).data("hasTooltip", true);			
		},
		destroyTooltip : function (target){
			if(target.data("hasTooltip")){
				target.removeData("hasTooltip");
				target.tooltip("destroy");
			}			
		},
		clearNotification : function(target){
			target.html("");
			this.closeAllNoty();			
		}
	}

	window.notificator = notificator;
})();