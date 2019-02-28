var layer;
function fetchLayerByLayerName(layerName){
	var layerAlreadyPlacedOnObject = false;
	for(var j in layerNamesObject){
		if(layerNamesObject[j] === geoserverWorkspaceName + ":"  + layerName){
			layerAlreadyPlacedOnObject = true;
			break;
		}
	}
	layer = layerName;
	if(!layerAlreadyPlacedOnObject){
		layerNamesObject.push(geoserverWorkspaceName + ":" +layerName);
		//so that layers array won't be object in URLParameters()
		var index = layerNamesObject.indexOf(geoserverWorkspaceName + ":" +layerName);
		console.log("the resource is:"+ resourceURL);
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
	    		},
		        projection: ol.proj.get('EPSG:4326')
	    	})

	    });

		var layerForViewMoreModal = new ol.layer.Tile({
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
	    		},
		        projection: ol.proj.get('EPSG:4326')	    		
	    	})
	    });
		
		layersByName[layerName] = layer;
		
		updateLayersForViewMoreModal();
		
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
	getLegend(layerNamesObject[index]);
	$("#legend").show();
	getMapObject={
			'BGCOLOR' : '0xcccccc',
			"Layers" : layers,
			"srs" : "EPSG:4326",
			'TILED' : true
	};
	
	return getMapObject;
}

function getLegend(layerId){
    var params = {
                'REQUEST': 'GetLegendGraphic',
                'VERSION' : '1.0.0',
                'FORMAT' : 'image/png',
                 'WIDTH':'20',
                 'HEIGHT':'20',
                'layerId': "'" + layerId + "'"
             };
    var callback = function(data){
    		document.getElementById("legend-image").src=data;
    		$('#legend-image').attr('src', `data:image/png;base64,${data}`);
    };
    var url = createLink(resourceURL,'wms') +"&REQUEST=GetLegendGraphic&VERSION=1.0.0&FORMAT=image/png&WIDTH=20&HEIGHT=20&layer="+layerId;

    AJAX_Call_GET( url, callback, null);
}