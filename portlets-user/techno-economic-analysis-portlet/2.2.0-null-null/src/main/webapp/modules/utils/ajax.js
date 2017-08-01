(function () {
	'use strict';
	
	var ajax = {
		post : function(data, url, successCallback, errorCallback, beforeSendCallback, completeCallback){			
			$.ajax({
				url : url,
				type : "post",
				dataType : "json",
				data : data,
				beforeSend: function(){
					if(beforeSendCallback){
						beforeSendCallback();
					}					
				},
			    complete: function () {
					if(completeCallback){
						completeCallback();
					}	
			    },
				success : function(data) {
					if(successCallback){
						successCallback(data);
					}					
				},
				error : function(jqXHR, exception) {
					if(errorCallback){
						errorCallback(jqXHR, exception);
					}	
				},
				timeout: 20000
			});
		},
		get : function(data, url, successCallback, errorCallback, beforeSendCallback, completeCallback){
			$.ajax({
				url : url,
				type : "get",
				dataType : "json",
				contentType: "application/json; charset=utf-8",
				data : data,
				beforeSend: function(){
					if(beforeSendCallback){
						beforeSendCallback();
					}					
				},
			    complete: function () {
					if(completeCallback){
						completeCallback();
					}	
			    },
				success : function(data) {
					if(successCallback){
						successCallback(data);
					}					
				},
				error : function(jqXHR, exception) {
					if(errorCallback){
						errorCallback(jqXHR, exception);
					}	
				},
				timeout: 20000
			});
		}
	}
	window.ajax = ajax;
})();