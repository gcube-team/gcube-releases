function retrieveLayers(){
	
}

function fetchLayerByLayerName(layerName){
	var layerAlreadyPlacedOnObject = false;
	for(var j in layerNamesObject){
		if(layerNamesObject[j] === geoserverWorkspaceName + ":"  + layerName){
			layerAlreadyPlacedOnObject = true;
			break;
		}
	}
	
	if(!layerAlreadyPlacedOnObject){
		layerNamesObject.push(geoserverWorkspaceName + ":" +layerName);
		//so that layers array won't be object in URLParameters()
		var index = layerNamesObject.indexOf(geoserverWorkspaceName + ":" +layerName);
		var layer = new ol.layer.Tile({
	    	source: new ol.source.TileWMS({
	    		url : createLink(resourceURL,'wms'),
	    		params : params(index),
	    		tileLoadFunction : function (imageTile, src){
	    			var client = new XMLHttpRequest();
	    			client.open('GET', src);
	    			client.responseType = "arraybuffer";

	    			client.onload = function () {
	    			    var arrayBufferView = new Uint8Array(this.response);
	    			    var blob = new Blob([arrayBufferView], { type: 'image/png' });
	    			    var urlCreator = window.URL || window.webkitURL;
	    			    var imageUrl = urlCreator.createObjectURL(blob);
	    			    imageTile.getImage().src = imageUrl;
	    			};
	    			client.send();
	    		}
	    	})
	    });
		
		layersByName[layerName] = layer;
		return layer;
		
	}else{
		return layersByName[layerName];
	}
}

function params(index){
	var layers = [];
	layers.push(layerNamesObject[index]);
	var theBbox = map.getView().calculateExtent(map.getSize());
	var getMapObject = {};
	
	getMapObject={
			'BGCOLOR' : '0xcccccc',
			"Layers" : layers,
			"srs" : "EPSG:4326", 
	};
	
	return getMapObject;
}