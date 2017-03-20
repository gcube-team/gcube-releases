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
	   complete: function(){
		   
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

function parseResponse(response){
	var features = response.features;//array
	
	var $viewMoreButton = '<div class="row-fluid popoverViewAllRow"><div class="viewAllContainer row-fluid span12"><button id="popoverInfoViewAll" class="span5 offset6">Viewmore</button></div></div>';
	var $modalButtonsRow = '<div id="functionRunAndExportButtons" class="row-fluid btn-group"><button id="exportAsButtonModalBottom" class="span4 offset5">	Export as<i class="fa fa-caret-down"></i></button><button id="closeButtonModalBottom" class="span3">Close</button></div>';
	
	var $placeToAppendRows = $('#popoverBodyContainingInfo');
	$placeToAppendRows.html('');
	var $modalRowsPlaceHolder = $('#modalAttributesContainer');
	$modalRowsPlaceHolder.html('');
	
	var rowsForPopover = [];
	
	if(features && features.length !== 0){
		var counter = 0;//present only 5 attributes
		var rowsForModal = [];
		
		for(var i=0; i<features.length; i++){
			var propertiesObject = features[i].properties;
			for(var name in propertiesObject){
				if(propertiesObject[name] !== null && name !== "shp_id" && counter !== 5){
					var row = buildPopoverRows(name, propertiesObject[name]);
					rowsForPopover.push(row);
					counter++;
				}
				var modalRow = buildModalRows(name, propertiesObject[name]);
				rowsForModal.push(modalRow);
			}
		}
		
		for(var j=0; j<rowsForPopover.length;j++){
			$placeToAppendRows.append(rowsForPopover[j]);
		}
		
		for(var k=0; k<rowsForModal.length;k++){
			$modalRowsPlaceHolder.append(rowsForModal[k]);
		}
//		$modalRowsPlaceHolder.append($modalButtonsRow);
		$placeToAppendRows.append($viewMoreButton);
		mapExportEvents();
	}else{
		var row = buildPopoverRows("Data", "Not found");
		$placeToAppendRows.append(row);
		$placeToAppendRows.append($viewMoreButton);
		var modalRow = buildModalRows("Data", "Not found");
		$modalRowsPlaceHolder.append(modalRow);
		mapExportEvents();
	}
}