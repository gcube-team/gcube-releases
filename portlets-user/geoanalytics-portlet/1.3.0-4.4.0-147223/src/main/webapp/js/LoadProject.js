function loadProject(object){
	$('#appTabs a:last').tab('show')
	$('#dateOfProject').text(timestampToDateStringDots(object.date));
	$('#nameOfProject').text(object.projectName);
	$('#nameOfProject').data("projectID",loadProjectObject.projectID);
	map.getView().fit(object.extent, map.getSize());
	map.updateSize();
}

function loadProjectOnTableInit(object){
	$('#dateOfProject').text(timestampToDateStringDots(object.date));
	$('#nameOfProject').text(object.projectName);
	map.getView().fit(object.extent, map.getSize());
	map.updateSize();
}

function extractCoordinates(coordinatesArray){
	var coordinates = [];
	coordinates = coordinatesArray.split(",");
	
	map.getView().fit(coordinates, map.getSize());
	map.updateSize();
}

function setUserInfoObject(){
	
	var theURl = $('#portletInfo').data('loginurl');
	var theData = {};
	theData[nameSpace + 'fetchTenantAndUsername'] = true;
	var callback = function(data){
		userinfoObject = JSON.parse(data);
		userinfoObject.projectName = projectName;
	};

	$.ajax(
			{
				url: theURl,
				type: 'post',
				datatype:'json',
				data: theData,
				success: function(data){
					callback(data);
					retrieveAvailableLayersAndPlaceThemOnTheLeft(userinfoObject);
				},
				error: function (xhr, ajaxOptions, thrownError) {
					$('#errorModal').modal('show');
				}
			}
		);

}

function getProjectBBOX(userinfoObject){
	var url = getProjectBBOXURL;
	var callback = function(data){
		if(data.status === "Success"){
			extractCoordinates(data.response);
		} else {
			$('#errorModal').modal('show');
		}
	};
	var data = userinfoObject;
	
	
	$.ajax(
			{
				url: url,
				type: 'post',
				datatype:'json',
				contentType : 'application/json',
				data: data,
				success: function(data){
					if(data.status === "Success"){
						extractCoordinates(data.response);
					} else {
						$('#errorModal').modal('show');
					}
				},
				error: function (xhr, ajaxOptions, thrownError) {
					$('#errorModal').modal('show');
				}
			}
		);
//	AJAX_Call_POST(url, callback, data);
}

function setProjectBBOX(userinfoObject){
	var callback = function(data){
//		console.log(data);
	};
	
	AJAX_Call_POST(getProjectBBOXURL, callback, userinfoObject);
}

function timestampToDateStringDots(timestamp)
{
	timestamp = Number(timestamp );
	var date = new Date(timestamp);
    
	function addZero(num) { return (num >= 0 && num < 10) ? "0" + num : num + ""; }
	var dateStr = addZero(date.getMonth()+1) + '.' + addZero(date.getDate()) + "." + date.getFullYear();
	return dateStr;
}

function retrieveGeoserverBridgeWorkspace(userinfoObject){
	var url = geoserverWorkspaceURL;
	var callback = function(data){
		geoserverWorkspaceName = data.response;
		if(data === null){
			$('.wizard').modal('hide');
			$('#InternalServerErrorModal').modal('show');
		} else {
			initializeProjectSelectionTable();
		}
	};
	
	AJAX_Call_POST(url, callback, userinfoObject);
}

function removeLayersFromMap(){
	for(var i in layersByName){
		map.removeLayer(layersByName[i]);
	}
	layersByName = {};
	layerNamesOnTheLeft =[];
	layerNamesObject = [];
}

function removeLayersFromMapModal(){
	for(var i in layersByNameModal){
		layersMap.removeLayer(layersByNameModal[i]);
	}
	layersByNameModal = {};
	layerNamesOnTheLeftModal =[];
	layerNamesObjectModal = [];
}