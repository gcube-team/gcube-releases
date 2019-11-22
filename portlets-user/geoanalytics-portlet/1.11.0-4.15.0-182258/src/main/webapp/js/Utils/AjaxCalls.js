function AJAX_Call_POST(theURl, callback, json, theContext){
	$.ajax({
	   url: theURl,
	   context: theContext,
	   type: "post",
	   beforeSend: function(xhr) {
	       xhr.setRequestHeader("Accept", "application/json");
	       xhr.setRequestHeader("Content-Type", "application/json");
	   },
	   data : JSON.stringify(json),
	   success: function(data) {
		   callback(data);
	   },
	   error : function(jqXHR, textStatus, errorThrown) {
		   $('.wizard').modal('hide');
		   $('#InternalServerErrorModal').modal('show');
		   $('#manipulateProjectGroups').off();
		   $('#createNewProject').off();
	   },
	   complete: function() {
		   
	   },
	});
}

function AJAX_Call_POST_Single_String(theURl, callback, json, theContext){
	$.ajax({
	   url: theURl,
	   context: theContext,
	   type: "post",
	   beforeSend: function(xhr) {
	       xhr.setRequestHeader("Accept", "application/json");
	       xhr.setRequestHeader("Content-Type", "application/json");
	   },
	   data : json,
	   success: function(data) {
		   callback(data, theContext);
	   },
	   error : function(jqXHR, textStatus, errorThrown) {
		   $('.wizard').modal('hide');
		   $('#InternalServerErrorModal').modal('show');
	   },
	   complete: function(){
		   
	   },
	});
}

function AJAX_Call_POST_Form(theURl, callback, json, theContext, beforeSendCallback, onErrorThrownCallback, onCompleteCallback){
	$.ajax({
	   url: theURl,
	   context: theContext,
	   type: "post",
	   cache: false,
	   processData: false,
	   contentType: false,
	   beforeSend: function(xhr) {
		   beforeSendCallback();
	   },
	   data : json,
	   success: function(data) {
		   callback(data, theContext);
	   },
	   error : function(jqXHR, textStatus, errorThrown) {
		   onErrorThrownCallback();
	   },
	   complete: function(){
		   onCompleteCallback();
	   },
	});
}

function AJAX_Call_GET(theURl, callback, theContext){
	$.ajax({
	   url: theURl,
	   context: theContext,
	   type: "get",
	   contentType: 'application/json',
	   beforeSend: function(xhr) {
	       xhr.setRequestHeader("Accept", "application/json");
	       xhr.setRequestHeader("Content-Type", "application/json");
	   },
	   success: function(data) {
		   callback(data);
	   },
	   error : function(jqXHR, textStatus, errorThrown) {
		   $('.wizard').modal('hide');
		   $('#InternalServerErrorModal').modal('show');
	   },
	   complete: function(){
		   
	   },
	});
}

function AJAX_Call_GET_JSONP(theURl, callback, theContext){
	$.ajax({
	   url: theURl,
	   dataType: 'jsonp',
	   jsonpCallback: 'callback',
	   contentType: 'application/json',
	    jsonp: 'jsonp',
	   success: function(data) {
		   callback(data);
	   },
	   error : function(jqXHR, textStatus, errorThrown) {
		   $('.wizard').modal('hide');
		   $('#InternalServerErrorModal').modal('show');
	   },
	   complete: function(){
		   
	   },
	   type: "get",
	});
}