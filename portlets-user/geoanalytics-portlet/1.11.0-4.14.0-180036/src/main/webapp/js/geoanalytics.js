function mapInit() {
	vectorSource = new ol.source.Vector();

	iconStyle = new ol.style.Style({
		image : new ol.style.Icon(({
			anchor : [ 0.5, 1 ],
			anchorXUnits : 'fraction',
			anchorYUnits : 'fraction',
			opacity : 0.75,
			src : iconSource,
			scale : 0.1
		}))
	});

	vectorLayer = new ol.layer.Vector({
		source : vectorSource,
		style : new ol.style.Style({
			fill : new ol.style.Fill({
				color : 'rgba(255, 255, 255, 0.2)'
			}),
			stroke : new ol.style.Stroke({
				color : '#ffcc33',
				width : 2
			}),
			image : new ol.style.Circle({
				radius : 7,
				fill : new ol.style.Fill({
					color : '#ffcc33'
				})
			})
		})
	});

	layer1 = new ol.layer.Tile({
		source : new ol.source.OSM()
	});

	var dragPanInteraction = new ol.interaction.DragPan();

	mousePositionControl = new ol.control.MousePosition({
		projection : 'EPSG:4326',
		coordinateFormat : ol.coordinate.createStringXY(2),
		target : $('#coord-info')[0],
		undefinedHTML : '&nbsp;'
	});

	var projection = new ol.proj.Projection({
		code : 'EPSG:900913',
		units : 'degrees',
		axisOrientation : 'neu'
	});

	var projection4326 = new ol.proj.Projection({
		code : 'EPSG:4326',
		units : 'degrees',
		axisOrientation : 'neu',
		global : true
	});

	// Menu for projections
    var selectList = document.createElement("select");
    selectList.id = "select-projection";
    selectList.onchange = function(e){
        console.log(e);
        alert(this.value);
    }

    var array = ["EPSG:4326","EPSG:28992","EPSG:3785","EPSG:4055","EPSG:27700"];
    for (var i = 0; i < array.length; i++) {
        var option = document.createElement("option");
        option.value = array[i];
        option.text = array[i];
        selectList.appendChild(option);
    }

    // A toggle control to show/hide the menu
//    var toggle = new ol.control.Toggle({
//    	html: '<i class="fa fa-bars" ></i>',
//        className: "menu",
//        title: "Menu",
//        onToggle: function() { menu.toggle(); }
//    });



	map = new ol.Map({
		target : 'map',
		controls : ol.control.defaults({
			zoom : true,
			attribution : false,
			rotate : false
		}),
		layers : [ layer1, vectorLayer // layer2,layer3
		],
		view : new ol.View({
			center : ol.proj.fromLonLat([ 22.00, 37.00 ]),
			// center: [22.00, 37.00],
			zoom : 7,
		// zoom: 6,
		// projection: projection4326
		})
	});

	map.set('mapId', 'map');

	var extent = [ 23.15436033424519, 36.417629409552816, 36.417629409552816,
			37.16194524232262 ];

	var extentEPSG4326 = ol.proj.transformExtent(map.getView().calculateExtent(
			map.getSize()), ol.proj.get('EPSG:3857'), ol.proj.get('EPSG:4326'));
	var extentEPSG900913 = ol.proj.transformExtent(map.getView()
			.calculateExtent(map.getSize()), ol.proj.get('EPSG:4326'), ol.proj
			.get('EPSG:900913'));

	map.on('pointerup', function(evt) {
		map.getViewport().style.cursor = "default";
	});

	map.on('pointerdrag', function(evt) {
		map.getViewport().style.cursor = "move";
	});

	map.on('moveend', moveEnd);

	map.on('singleclick', getInfoFromPixelCoordinates);

	map.getView();
	map.getView().on('change:resolution', function(evt) {
		var resolution = evt.target.get('resolution');
		var units = map.getView().getProjection().getUnits();
		var dpi = 25.4 / 0.28;
		var mpu = ol.proj.METERS_PER_UNIT[units];
		var scale = resolution * mpu * 39.37 * dpi;
		if (scale >= 9500 && scale <= 950000) {
			scale = Math.round(scale / 1000) + "K";
		} else if (scale >= 950000) {
			scale = Math.round(scale / 1000000) + "M";
		} else {
			scale = Math.round(scale);
		}
		$('#tools-info-scale').text("1 : " + scale);
	});

	map.addControl(new ol.control.ScaleLine({
		units : 'metric',
		target : $('#eye-section span')[0]
	}));

	map.addControl(mousePositionControl);
	map.addControl(new ol.control.FullScreen({
		source : 'DecisionSupportSystem'
	}));
	map.addControl(adjustMapToInitialExtent());
	map.addInteraction(dragPanInteraction);

    //Menu for projection
//    map.addControl(menu);
//    map.addControl(toggle);


	dockedGISToolsButtonControl(map);

	$('.ol-mouse-position').insertBefore('.dropup');
	$('div#tools-info-scale').text("1 : " + getCurrentScale());

	drawCoordinates();

	overlay = new ol.Overlay(
			{
				element : $('div#DecisionSupportSystem div#overlayForSingleClickingOnMapContainer')[0],
				positioning : 'bottom-center',
				autoPanMargin : 100
			});
	
	mapInsideViewMoreModalInit();

	// initialMapExtent = map.getView().calculateExtent(map.getSize());
	// initialMapExtent =
	// ol.proj.transformExtent(initialMapExtent,ol.proj.get('EPSG:3857'),ol.proj.get('EPSG:4326'));
}

function mapInsideViewMoreModalInit() {

	var layer1 = new ol.layer.Tile({
		source : new ol.source.OSM()
	});

	mapInsdideViewMoreModal = new ol.Map({
		target : 'mapAreaContainer',
		controls : ol.control.defaults({
			zoom : true,
			attribution : false,
			rotate : false
		}),
		layers : [ layer1 ],
		view : new ol.View({
			center : ol.proj.fromLonLat([ 22.00, 37.00 ]),
			zoom : 10
		})
	});
}

function getResolutionFromScale(scale) {
	var units = map.getView().getProjection().getUnits();
	var dpi = 25.4 / 0.28;
	var mpu = ol.proj.METERS_PER_UNIT[units];
	var resolution = scale / (mpu * 39.37 * dpi);
	return resolution;
}

function getCurrentScaleNotRounded() {
	var thisMap = map;
	var view = thisMap.getView();
	;
	var resolution = view.getResolution();
	var units = thisMap.getView().getProjection().getUnits();
	var dpi = 25.4 / 0.28;
	var mpu = ol.proj.METERS_PER_UNIT[units];
	var scale = resolution * mpu * 39.37 * dpi;

	return scale;
}

function getCurrentScale() {
	var thisMap = map;
	var view = thisMap.getView();
	
	var resolution = view.getResolution();
	var units = thisMap.getView().getProjection().getUnits();
	var dpi = 25.4 / 0.28;
	var mpu = ol.proj.METERS_PER_UNIT[units];
	var scale = resolution * mpu * 39.37 * dpi;
	if (scale >= 9500 && scale <= 950000) {
		scale = Math.round(scale / 1000) + "K";
	} else if (scale >= 950000) {
		scale = Math.round(scale / 1000000) + "M";
	} else {
		scale = Math.round(scale);
	}

	return scale;
}

function displayResolutionProperly(resolution) {
	return resolution;
}

function moveEnd() {
	var res = getResolutionFromScale(getCurrentScale());
	res = displayResolutionProperly(res);
	if (!popupSticksOutOfMapArea) {
		$('div#DecisionSupportSystem div.ol-overlay-container').addClass(
				'hidden');
	} else {
		popupSticksOutOfMapArea = false;
	}
	iconFeatureArray = [];
}

// move map if popup sticks out of map area:
function moveMapIfPopupSticksOutOfMapArea(coordinate) {
	var extent = map.getView().calculateExtent(map.getSize());
	var center = map.getView().getCenter();
	var pixelPosition = map.getPixelFromCoordinate([ coordinate[0],
			coordinate[1] ]);
	var mapWidth = $("#map").width();
	var mapHeight = $("#map").height() - 2 * $('#searchBarCenter').height();
	var popoverHeight = $(".ol-overlay-container").height();
	var popoverWidth = $("#overlayForSingleClickingOnMapContainer").width();
	var thresholdTop = popoverHeight + 115;
	var thresholdBottom = $("#map").height() - 25;
	var thresholdLeft = popoverWidth / 2 - 20;
	var thresholdRight = mapWidth - popoverWidth / 2 - 105;

	if (pixelPosition[0] < thresholdLeft || pixelPosition[0] > thresholdRight
			|| pixelPosition[1] < thresholdTop
			|| pixelPosition[1] > thresholdBottom) {
		popupSticksOutOfMapArea = true;
		if (pixelPosition[0] < thresholdLeft) {
			var newX = pixelPosition[0] + (thresholdLeft - pixelPosition[0]);
		} else if (pixelPosition[0] > thresholdRight) {
			var newX = pixelPosition[0] - (pixelPosition[0] - thresholdRight);
		} else {
			var newX = pixelPosition[0];
		}

		if (pixelPosition[1] < thresholdTop) {
			var newY = pixelPosition[1] + (thresholdTop - pixelPosition[1]);
		} else if (pixelPosition[1] > thresholdBottom) {
			var newY = pixelPosition[1] - (pixelPosition[1] - thresholdBottom);
		} else {
			var newY = pixelPosition[1];
		}
		newCoordinate = map.getCoordinateFromPixel([ newX, newY ]);
		newCenter = [ (center[0] - (newCoordinate[0] - coordinate[0])),
				(center[1] - (newCoordinate[1] - coordinate[1])) ]
		map.getView().setCenter(newCenter);
	} else {
		popupSticksOutOfMapArea = false;
	}

	map.updateSize();
}

function getInfoFromPixelCoordinates(event) {

	if (drawInteractionIsAdded) {
		return;
	}
	
	var coord = event.coordinate;
	coordsDSSTab = coord;
	var degrees = ol.proj.transform(coord, 'EPSG:3857', 'EPSG:4326')
	var hdms = ol.coordinate.toStringHDMS(degrees);

	if (overlay === null) {
		overlay = new ol.Overlay(
				{
					element : $('div#DecisionSupportSystem div#overlayForSingleClickingOnMapContainer')[0],
					positioning : 'bottom-center',
					autoPanMargin : 100
				});
	}

	var element = overlay.getElement();
	var jqueryElement = $(element).find('div#coordsOnPopoverTitle');
	jqueryElement.text(hdms);
	overlay.setPosition(coord);
	map.addOverlay(overlay);
	$('div#DecisionSupportSystem div.ol-overlay-container').removeClass(
			'hidden');

	// var iconFeatureNew = new ol.Feature({
	// geometry: new ol.geom.Point(coord),
	// name : 'Null island'
	// });
	// vectorLayer.setSource(vectorSource);
	// iconFeatureArray.push(iconFeatureNew);
	$('#regionInPathForm').text('Searching...');
	var breadcrumb = retrieveBreadcrumbInfo(degrees);

	mapExportEvents();
	popoverInfo(event);
	moveMapIfPopupSticksOutOfMapArea(coord);
	var afterBreadcrumbIsFinishedCallback = function() {
		moveMapIfPopupSticksOutOfMapArea(coord);
	}

	breadcrumb.ajaxObject.done(breadcrumb.callback).done(
			afterBreadcrumbIsFinishedCallback);

	// pinInfo(event);
}

function retrieveBreadcrumbInfo(degrees) {
	var callback = function(data) {
		$('#regionInPathForm').text('');
		$.each(data, function() {
			$('#regionInPathForm').text(
					$('#regionInPathForm').text() + this + " / ");
		});
		$('#regionInPathFormInModal').text($('#regionInPathForm').text());
	};
	var coords = {};
	coords.lon = degrees[0];
	coords.lat = degrees[1];

	var breadcrumb = {};
	breadcrumb.ajaxObject = $.ajax({
		url : breadcrumbsByCoordinatesMostSpecific,
		type : 'post',
		cache : false,
		contentType : false,
		processData : false,
		data : JSON.stringify(coords),
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		}
	});
	breadcrumb.callback = callback;

	return breadcrumb;
}

function pinInfo(event) {
	var feature = map.forEachFeatureAtPixel(event.pixel, function(feature,
			layer) {
		return feature;
	});
	if (feature) {
	} else {
	}
}

function popoverInfo(evt) {
	destroyPopoverData();

	var layer = null;
	for ( var name in layersByName) {
		layer = layersByName[name];
		break;
	}
	
	if (layer === null || $('#treeviewTaxonomiesLayers').jstree('get_selected').length === 0) {
		showNullMessageOnPopover();
		return;
	}

	var coords = evt.coordinate;	

	var featureInofRequestLayers = [];
	var selectedLayers = $('#treeviewTaxonomiesLayers').jstree('get_selected');// array
	$.each(featureInfoLayers, function(i, v) {
		$.each(selectedLayers, function(index, value) {
			if (v.indexOf(value) !== -1) {
				featureInofRequestLayers.push(v);
			}
		});
	});

	var url = layer.getSource().getGetFeatureInfoUrl(coords, map.getView().getResolution(), map.getView().getProjection(), {
		'QUERY_LAYERS' : featureInofRequestLayers,
		'INFO_FORMAT' : 'application/json',
		'FEATURE_COUNT' : 50
	});

	var resourceURLNoParams = window.config.resourceURLNoParams;

	var wmsResourceUrl = createLink(resourceURL, "wms");
	
	var getFeatureInfoRequest = url.replace(wmsResourceUrl, "");	
	getFeatureInfoRequest = decodeURIComponent(getFeatureInfoRequest);
	getFeatureInfoRequest = getFeatureInfoRequest.replace("I=", "X=");
	getFeatureInfoRequest = getFeatureInfoRequest.replace("J=", "Y=");
	getFeatureInfoRequest = getFeatureInfoRequest.replace("1.3.0", "1.1.0");
	getFeatureInfoRequest = getFeatureInfoRequest.replace("&srs=EPSG:4326", "&srs=EPSG:3857");
	
	var urlParameters = getFeatureInfoRequest.split("&");
	
	var parameters = {};
	
	for(var i=0; i<urlParameters.length; i++){
		var entry = urlParameters[i].split("=");
		var key = entry[0];
		var value = entry[1];
		parameters[key] = value;
	}	
	
	var successCallback = function(response) {
		parseResponse(response);
		moveMapIfPopupSticksOutOfMapArea(coordsDSSTab);
	};	
	
	$.ajax({
		url : wmsResourceUrl,
		data : parameters,
		type : "GET",
		success : function(data) {
			successCallback(data);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			$('.wizard').modal('hide');
			$('#InternalServerErrorModal').modal('show');
		},
		complete : function() {

		}
	});
}

function parseResponse(response) {
	var layerFeatures = response.layerFeatures;

	var features = [];// response.features;//array

	$.each(layerFeatures, function(index, value) {
		if (value.features !== null || value.features.length !== 0) {
			features = features.concat(value.features);
		}
	});

	var $viewMoreButton = '<div class="row-fluid popoverViewAllRow"><div class="viewAllContainer row-fluid span12"><button id="popoverInfoViewAll" class="span5 offset6">Viewmore</button></div></div>';
	var $modalButtonsRow = '<div id="functionRunAndExportButtons" class="row-fluid btn-group"><button id="exportAsButtonModalBottom" class="span1 pull-right">	Export as<i class="fa fa-caret-down"></i></button><button id="closeButtonModalBottom" class="span1 pull-right">Close</button></div>';

	destroyPopoverData();
	var $placeToAppendRows = $('#popoverBodyContainingInfo');

	var $modalRowsPlaceHolder = $('#modalAttributesContainer');
	$modalRowsPlaceHolder.html('');

	var rowsForPopover = [];

	if (features && features.length !== 0) {
		var counter = 0;// present only 5 attributes
		var rowsForModal = [];

		for (var i = 0; i < features.length; i++) {
			var propertiesObject = features[i].properties;
			for ( var name in propertiesObject) {
				let nameToBeDisplayed = null;
				if (propertiesObject[name] !== null && name !== "shp_id"
						&& counter !== 5) {
					
					if(name.substring(0,2) === '_#') {
						nameToBeDisplayed = name.substring( 2, name.length );
					} else nameToBeDisplayed = name;
					
					var row = buildPopoverRows(nameToBeDisplayed, propertiesObject[name]);
					rowsForPopover.push(row);
					counter++;
				} else if(counter >= 5){
				    if(name.substring(0,2) === '_#') {
                        nameToBeDisplayed = name.substring( 2, name.length );
                    } else nameToBeDisplayed = name;
				}
				var modalRow = buildModalRows(nameToBeDisplayed, propertiesObject[name]);
				rowsForModal.push(modalRow);
			}
		}

		for (var j = 0; j < rowsForPopover.length; j++) {
			$placeToAppendRows.append(rowsForPopover[j]);
		}

		for (var k = 0; k < rowsForModal.length; k++) {
			$modalRowsPlaceHolder.append(rowsForModal[k]);
		}
		$placeToAppendRows.append($viewMoreButton);
		mapExportEvents();
	} else {
		var row = buildPopoverRows("Data", "Not found");
		$placeToAppendRows.append(row);
		var modalRow = buildModalRows("Data", "Not found");
		$modalRowsPlaceHolder.append(modalRow);
	}
}

function buildPopoverRows(labelText, attributeText) {
	var topDiv = $('<div></div>', {
		"class" : 'row-fluid popoverInfoRow'
	});
	var labelDiv = $('<div></div>', {
		"class" : 'popoverInfoRowLabel span6',
		text : labelText
	});
	var attributeDiv = $('<div></div>', {
		"class" : 'popoverInfoRowData span5 offset1',
		text : attributeText
	});
	topDiv.append(labelDiv).append(attributeDiv);

	return topDiv;
}

function buildModalRows(labelText, attributeText) {
	var topDiv = $('<div></div>', {
		"class" : 'row-fluid modalInfoRow'
	});
	var labelDiv = $('<div></div>', {
		"class" : 'modalInfoRowLabel span6',
		text : labelText
	});
	var attributeDiv = $('<div></div>', {
		"class" : 'modalInfoRowData span5',
		text : attributeText
	});
	topDiv.append(labelDiv).append(attributeDiv);

	return topDiv;
}

function URLParameters() {
	var layers = [];
	layers = layerNamesOnTheLeft;
	var theBbox = map.getView().calculateExtent(map.getSize());

	var parameters = "REQUEST=" + "GetMap" + "&SERVICE=" + "WMS" + '&BGCOLOR='
			+ '0xcccccc' + "&Layers=" + layers + '&WIDTH=' + map.getSize()[0]
			+ "&HEIGHT=" + map.getSize()[1] + '&TRANSPARENT=' + "true"
			+ "&styles=" + "" + "&srs=" + "EPSG:4326" + "&Format="
			+ "image/png" + "&BBOX=" + theBbox + "&VERSION=" + "1.1.1";

	return parameters;
}

function destroyPopoverData() {
	var $placeToAppendRows = $('#popoverBodyContainingInfo');
	$placeToAppendRows.html('');
}

function showNullMessageOnPopover() {
	destroyPopoverData();

	var $placeToAppendRows = $('#popoverBodyContainingInfo');
	var row = buildPopoverRows("No layer loaded", "");
	$placeToAppendRows.append(row);
}