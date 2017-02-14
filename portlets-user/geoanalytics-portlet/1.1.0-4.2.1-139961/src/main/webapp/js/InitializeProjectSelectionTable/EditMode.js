function extractCoordinates(coordinatesArray){
	var coordinates = [];
	coordinates = coordinatesArray.split(",");
	var editeModeCoordinatesString = coordinates;
	$.each(editeModeCoordinatesString, function(index, value){
		editeModeCoordinates[index] = Number(value);
	});
	
	mapBBOX.getView().fit(editeModeCoordinates, mapBBOX.getSize());
	mapBBOX.updateSize();
}

function transformProj(extent) {
	return ol.proj.transformExtent(extent, 'EPSG:3857', 'EPSG:4326');
}