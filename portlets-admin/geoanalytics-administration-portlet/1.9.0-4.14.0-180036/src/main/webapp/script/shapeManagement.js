pageState = {};

function showShapeManagement(resourceURL, contextPath, data, notificator) {
	pageState.cPath = contextPath;
	pageState.rURL = resourceURL;
	pageState.notificator = notificator;
	
	enableZMaxIndex();
	
	retrieveGeoserverBridgeWorkspace();
}

function initialize(container)
{
	//var options = document.createElement('img');
	//options.id = container.id + '_showShapeOptions';
	//container.appendChild(options);
//	$('#toggleSelect').show();
	var toolbar = document.createElement('div');
	document.getElementById(container.id+"_showShapeToolbar");
	var featureInfo = document.createElement('div');
	featureInfo.id = container.id+'_showShapeFeatureInfo';
	container.appendChild(featureInfo);
}

function showVectorShapes(container, mode, data, bbox)
{
	
	var url, req;
	switch(mode)
	{
	case "search":
		url = "shapes/searchFast";
		req = JSON.stringify(data);
		break;
	case "shape":
		url = "shapes/retrieveShape";
		req = data;
		break;
	case "import":
		url = "shapes/retrieveShapesOfImport";
		req = data;
		break;
	case "bbox":
		url = "shapes/retrieveShapesWithinBBox";
		req = JSON.stringify(bbox);
		break;
	}
	
	url = createLink(pageState.rURL, url);
	
	console.log("URL:");
	console.log(url);

	
	var shapeSearchSelection = {};
	shapeSearchSelection.terms = [];
	shapeSearchSelection.terms = data.terms;
	shapeSearchSelection.id = data.id;
	
//	if (data.importInstances != undefined) {
//		shapeSearchSelection.importInstances = [];
//		shapeSearchSelection.importInstances.push()
//
//	} else
	shapeSearchSelection.importInstances = undefined;
	
	shapeSearchSelection.geometry = undefined;
	shapeSearchSelection.geoSearchType = undefined;
	
	$.ajax({ 
        url :  url, 
        type : "post", 
        dataType : "json",
        contentType : "application/json",
        data : JSON.stringify(shapeSearchSelection),
        success : function(shapes) 
		  { 
			 var allFeatures = [];
			 var bounds;
			 var projWGS84 = new OpenLayers.Projection("EPSG:4326");
			 var proj900913 = new OpenLayers.Projection("EPSG:900913");
			 if(shapes.constructor != Array)
				 shapes = [shapes];
			 for(var s=0; s<shapes.length; s++)
			 {
				 var features = new OpenLayers.Format.WKT().read(shapes[s].geometry);
				 if(features) {
			         if(features.constructor != Array)
			             features = [features];
			         for(var i=0; i<features.length; ++i) {
			             if (!bounds)
			                 bounds = features[i].geometry.getBounds();
			             else
			                 bounds.extend(features[i].geometry.getBounds());
			             features[i].geometry.transform(projWGS84, proj900913);
			             features[i].attributes.shapeId = shapes[s].id;
			             features[i].attributes.code = shapes[s].code;
			         	 features[i].attributes.shapeClass = shapes[s].shapeClass;
			         	 features[i].attributes.name = shapes[s].name;
			         	 features[i].attributes.importId = shapes[s].importId;
			         	 features[i].attributes.layerName = shapes[s].layerName;
			         	 features[i].attributes.layerGeocodeSystem = shapes[s].layerGeocodeSystem;
			         	 features[i].attributes.data = shapes[s].extraData;
			        }
			        allFeatures = allFeatures.concat(features);
				 }
			 }
			 if(!isPresent(bounds)) bounds = new OpenLayers.Bounds(19.2734, 34.2271, 28.8975, 42.0493); //if no shapes have been retrieved
			 showShapes(container, 'vector', allFeatures, null, bounds);
		  },
        error : function(jqXHR, textStatus, errorThrown) 
         		 {
 	   			   alert("The following error occured: " + textStatus, errorThrown);
         		 },
        complete : function(jqXHR, textStatus)
        		   {
        			 
        		   }
       }); 
}

function showShapes(container,mode, features, layer, bounds) {
    initialize(container);
    
	format = 'image/png';
    
	if(mode == 'raster')
	{
	        bounds = new OpenLayers.Bounds(
	        bounds.minx, bounds.miny,
	        bounds.maxx, bounds.maxy
	    );
	} 

    var layers = [];
    var tiled, untiled, vector;
    
    if(mode == 'vector')
    {
    	vector = new OpenLayers.Layer.Vector("Vector Layer", {isBaseLayer: false});
    	vector.addFeatures(features);
    	layers.push(vector);
    	
   	 	function onPopupClose(evt) {
   	 		//selectControl.unselect(selectedFeature);
   	 	}
   	 	
    	vector.events.on({
            'featureselected': function(feature) {
	            	selectedFeature = feature;
	                if(this.selectedFeatures.length == 1)
	                {
//		            	popup = new OpenLayers.Popup.Anchored("featurePopup", 
//		                                         feature.feature.geometry.getBounds().getCenterLonLat(),
//		                                         null, $('#featureActions')[0],
//		                                         //"<div style='font-size:.8em'>Feature: " + feature.feature.id +"<br>Area: " + feature.feature.geometry.getArea()+"</div>",
//		                                         null, false, onPopupClose);
//		                window.featurePopup = popup;
//		                map.addPopup(popup);
	                	$('#featureActions').show();
	                	//$('#featureActionEdit').show();
	                	$('#editForm').show();
	                	if(!isPresent($('#saveButton')[0].wired))
	                	{
	                		$('#saveButton')[0].addEventListener('click', saveShape);
	                		$('#saveButton')[0].wired = true;
	                		window.selectedFeature = feature.feature;
	                		window.selectedFeatures = this.selectedFeatures;
	                	}
	                	if(!isPresent($('#featureActionDelete')[0].wired))
	                	{
	                		$('#featureActionDelete')[0].addEventListener('click', deleteShapes);
	                		$('#featureActionDelete')[0].wired = true;
	                	}
	                	copyToEditForm(feature.feature);
	                }else
	                {
	                	$('#editForm').hide();
	                	window.selectedFeatures = this.selectedFeatures;
	                }
            },
            'featureunselected': function(feature) {
            	if(this.selectedFeatures.length == 1)
            	{
            		//map.removePopup(window.featurePopup);
            		//window.featurePopup.destroy();
            		//$('#featureActionEdit').show();
            		copyToEditForm(this.selectedFeatures[0]);
            		$('#editForm').show();
            	}else if(this.selectedFeatures.length == 0)
            		$('#featureActions').hide();
            }
        });

    }
    
	var projWGS84 = new OpenLayers.Projection("EPSG:4326");
	var proj900913 = new OpenLayers.Projection("EPSG:900913");
	bounds.transform(projWGS84, proj900913);
	
    var options = {
        controls: [],
        maxExtent: bounds,
      //  resolutions: [0.087890625, 0.0439453125, 0.02197265625, 0.010986328125, 0.0038268482997141],
      //  maxResolution: 0.0038268482997141,
        projection: new OpenLayers.Projection("EPSG:900913"),
        'tileSize' : new OpenLayers.Size(256,256)
    };
    
    var map = new OpenLayers.Map(container, options);
    window.featureMap = map;
    
    if(isPresent(layer))
    {
	    // setup tiled layer
	    tiled = new OpenLayers.Layer.WMS(
	        "geoanalytics:"+layer+" - Tiled", "wms",
	        {
	            LAYERS: 'geoanalytics:'+layer,
	            STYLES: '',
	            format: format,
	            tiled: true,
	            tilesOrigin : map.maxExtent.left + ',' + map.maxExtent.bottom
	        },
	        {
	            buffer: 0,
	            displayOutsideMaxExtent: true,
	            isBaseLayer: true,
	            yx : {'EPSG:4326' : true}
	        } 
	    );
	
	    // setup single tiled layer
	    untiled = new OpenLayers.Layer.WMS(
	        "geoanalytics:"+layer+" - Untiled", "wms",
	        {
	            LAYERS: 'geoanalytics:'+layer,
	            STYLES: '',
	            format: format
	        },
	        {
	           singleTile: true, 
	           ratio: 1, 
	           isBaseLayer: true,
	           yx : {'EPSG:4326' : true}
	        } 
	    );
	    layers.push(tiled);
	    layers.push(untiled);
    }
    
//    var wms = new OpenLayers.Layer.WMS( "OpenLayers WMS",
//            "http://vmap0.tiles.osgeo.org/wms/vmap0", {layers: 'basic', projection: new OpenLayers.Projection("EPSG:4326"), displayProjection: new OpenLayers.Projection("EPSG:4326")} );
      
    var osm = new OpenLayers.Layer.OSM();
		
//	var gphy = new OpenLayers.Layer.Google(
//		"Google Physical",
//		{type: G_PHYSICAL_MAP, sphericalMercator : true}
//	);
//	var gmap = new OpenLayers.Layer.Google(
//		"Google Streets", // the default
//		{numZoomLevels: 20, sphericalMercator : true}
//	);
//	var ghyb = new OpenLayers.Layer.Google(
//		"Google Hybrid",
//		{type: G_HYBRID_MAP, numZoomLevels: 20, sphericalMercator : true}
//	);
//	var gsat = new OpenLayers.Layer.Google(
//		"Google Satellite",
//		{type: G_SATELLITE_MAP, numZoomLevels: 22, sphericalMercator : true}
//	);
    
	//layers = layers.concat([wms, gphy, gmap, ghyb, gsat]);
    layers = layers.concat([osm]);
    map.addLayers(layers);

    // build up all controls
    map.addControl(new OpenLayers.Control.PanZoomBar({
        position: new OpenLayers.Pixel(2, 15)
    }));

    
    map.addControl(new OpenLayers.Control.Navigation({dragPanOptions: {enableKinetic: true}}));
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    map.addControl(new OpenLayers.Control.Scale($('scale')));
    map.addControl(new OpenLayers.Control.MousePosition({element: $('location')}));
    if(mode != 'bbox')
    {
	    var editingToolbar = new OpenLayers.Control.EditingToolbar(vector);
	    map.addControl(editingToolbar);
	    var select = new OpenLayers.Control.SelectFeature(
				            vector,
				            {
				                clickout: false, toggle: true,
				                multiple: true, hover: false,
				                toggleKey: "ctrlKey", // ctrl key removes from selection
				                multipleKey: "shiftKey", // shift key adds to selection
				                box: true
				            }
				        );
	    map.addControl(select);
    }
    
    if(mode == 'bbox')
    {
	    //Bounding box control
	    var boundingBoxControl = new OpenLayers.Control();
	    OpenLayers.Util.extend(boundingBoxControl, {
	        draw: function () {
	            // this Handler.Box will intercept the shift-mousedown
	            // before Control.MouseDefault gets to see it
	            this.box = new OpenLayers.Handler.Box( boundingBoxControl,
	                {"done": this.notice},
	                {keyMask: OpenLayers.Handler.MOD_SHIFT});
	            this.box.activate();
	        },
	
	        notice: function (bounds) {
	            var ll = map.getLonLatFromPixel(new OpenLayers.Pixel(bounds.left, bounds.bottom)); 
	            var ur = map.getLonLatFromPixel(new OpenLayers.Pixel(bounds.right, bounds.top)); 
	            var bbox = new OpenLayers.Bounds(ll.lon, ll.lat, ur.lon, ur.lat);
	            bbox.transform(proj900913,projWGS84);
	            var values = $('#searchForm').serializeArray();
	        	
	        	var selection = {};
	        	for(var i=0; i<values.length; i++)
	        	{
	        		switch(values[i].name)
	        		{
	        		case 'term':
	        			if(values[i].value != 'None')
	        			{
	        				selection.terms = values[i].value;
	        				if(selection.terms.constructor != Array) selection.terms = [selection.terms];
	        			}
	        			break;
	        		case 'importId':
	        			if(values[i].value != 'None')
	        			{
	        				selection.importInstances = values[i].value;
	        				if(selection.importInstances.constructor != Array) selection.importInstances = [selection.importInstances];
	        			}
	        			break;
	        		}
	        	}
	        	if(window.featureMap)
	        	{
	        		window.featureMap.destroy();
	        		window.featureMap = null;
	        	}
	        	selection.geoSearchType = "BoundingBox";
	        	selection.geometry = bbox.toGeometry().toString();
	        	showVectorShapes($('#map')[0], 'search', selection, null);
	        }
	    });
	    map.addControl(boundingBoxControl);
    }
    
    if(mode == 'bbox')
    {
    	boundingBoxControl.activate();
    }else
    {
    	select.deactivate();
    	editingToolbar.activate();
    }
    
//    $('#toggleSelect')[0].status = false;
//    $('#toggleSelect')[0].addEventListener('click', function(ev)
//    												  {
//    													if(ev.target.status == false)
//    													{
//    														editingToolbar.deactivate(), select.activate();
//    														ev.target.status = true;
//    													}else
//    													{
//    														editingToolbar.activate(), select.deactivate();
//    														ev.target.status = false;
//    													}
//    													
//    												  });
   // var options = {
   //     hover: true,
   //    onSelect: function() { }
   // };
   // var select = new OpenLayers.Control.SelectFeature(vector, options);
    //map.addControl(select);
    map.zoomToExtent(bounds);
    
    // wire up the option button
    //var options = document.getElementById("options");
//    options.addEventListener('click', function(ev)
//    								  {
//    									toggleControlPanel(ev, container);
//    								  });
    
    // support GetFeatureInfo
    if(mode == 'raster')
    {
	    map.events.register('click', map, function (e) {
	        document.getElementById(container.id+'_showShapeFeatureInfo').innerHTML = "Loading... please wait...";
	        var params = {
	            REQUEST: "GetFeatureInfo",
	            EXCEPTIONS: "application/vnd.ogc.se_xml",
	            BBOX: map.getExtent().toBBOX(),
	            SERVICE: "WMS",
	            INFO_FORMAT: 'text/html',
	            QUERY_LAYERS: map.layers[0].params.LAYERS,
	            FEATURE_COUNT: 50,
	            Layers: 'geopolis:'+layer,
	            WIDTH: map.size.w,
	            HEIGHT: map.size.h,
	            format: format,
	            styles: map.layers[0].params.STYLES,
	            srs: map.layers[0].params.SRS};
	        
	        // handle the wms 1.3 vs wms 1.1 madness
	        if(map.layers[0].params.VERSION == "1.3.0") {
	            params.version = "1.3.0";
	            params.j = parseInt(e.xy.x);
	            params.i = parseInt(e.xy.y);
	        } else {
	            params.version = "1.1.1";
	            params.x = parseInt(e.xy.x);
	            params.y = parseInt(e.xy.y);
	        }
            
	        // merge filters
	        if(map.layers[0].params.CQL_FILTER != null) {
	            params.cql_filter = map.layers[0].params.CQL_FILTER;
	        } 
	        if(map.layers[0].params.FILTER != null) {
	            params.filter = map.layers[0].params.FILTER;
	        }
	        if(map.layers[0].params.FEATUREID) {
	            params.featureid = map.layers[0].params.FEATUREID;
	        }
	        setHTML.container = container;
	        OpenLayers.Request.GET({
	        						url: "wms", 
	        						params: params, 
	        						callback: setHTML});
        
	        OpenLayers.Event.stop(e);
	    });
    }
    
    $('#featureActions').css('display', 'block');
    return map;
}

function saveShape()
{
	//var feature = window.selectedFeature;
	var updated = {};
	//updated.geometry = OpenLayers.Format.WKT().write(window.selectedFeature.geometry); TODO Geometry not updated at this point to avoid piling up conversion errors
    updated.id = $('#editFormTextBoxid').val();
    updated.code = $('#editFormTextBoxcode').val();
	updated.shapeClass = $('#editFormTextBoxshapeClass').val();
	updated.name = $('#editFormTextBoxname').val();
	updated.termName = $('#editFormTextBoxtermName').val();
	updated.termTaxonomy = $('#editFormTextBoxtermTaxonomy').val();
	updated.extraData = $('#editFormTextBoxdata').val();
	
	var req = JSON.stringify(updated);
	
	$.ajax({ 
        url :  "shapes/update", 
        type : "post", 
        dataType : "json",
        contentType : "application/json",
        data : req,
        success : function(response) 
        		  { 
        			if(response.status == false) alert('An error has occurred during shape update: ' + response.message);
        			searchButtonListener(null, $('#map')[0]);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
         		 {
 	   			   alert("The following error occured: " + textStatus, errorThrown);
         		 }
       }); 
}

function deleteShapes()
{
	var shapes = [];
	var features = window.selectedFeatures;
	for(var i=0; i<features.length; i++)
		shapes.push(features[i].attributes.shapeId);
	var req = JSON.stringify(shapes);
	
	$.ajax({ 
        url :  "shapes/delete", 
        type : "post", 
        dataType : "json",
        contentType : "application/json",
        data : req,
        success : function(response) 
        		  { 
        			if(response.status == false) alert('An error has occurred while deleting shapes: ' + response.message);
        			searchButtonListener(null, $('#map')[0]);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
         		 {
 	   			   alert("The following error occured: " + textStatus, errorThrown);
         		 }
       }); 
}

//function deleteLayer()
//{
//	var term = $('#geoadmin-layers :selected').val();
//	if(term == 'None') return;
//	
//	var url = createLink(pageState.rURL, 'shapes/deleteLayer');
//
//	
//	$.ajax({ 
//        url :  url, 
//        type : "post", 
//        dataType : "json",
//        contentType : "application/json",
//        data : term,
//        success : function(response) 
//        		  { 
//        			if(response.status == false) {
//        				pageState.notificator.notification("error", "An error has occurred while deleting shapes: " + response.message);
//        	        	$("#notification").addClass("in");
//        	        	return;
//        			}
//        			pageState.notificator.notification("success", "Layer deleted");
//    	        	$("#notification").addClass("in");
//        			populateTermSelector();
//        			populateImportInstanceSelector();
//        		  },
//        error : function(jqXHR, textStatus, errorThrown) 
//         		 {
// 	   			   alert("The following error occured: " + textStatus, errorThrown);
//         		 }
//       }); 
//}

function copyToEditForm(feature)
{
	var attrs = ['id', 'name', 'importId', 'code', 'termTaxonomy', 'termName', 'shapeClass', 'data'];
	
	for(var i=0; i<attrs.length; i++)
		$('#editFormTextBox'+attrs[i]).val(feature.attributes[attrs[i] != 'id' ? attrs[i] : 'shapeId']);
}

// sets the HTML provided into the featureInfo element
function setHTML(response){
    document.getElementById(setHTML.container.id+'_showShapeFeatureInfo').innerHTML = response.responseText;
};

// shows/hide the control panel
function toggleControlPanel(event, container){
    var toolbar = document.getElementById(container.id+"_showShapeToolbar");
    if (toolbar.style.display == "none") {
        toolbar.style.display = "block";
    }
    else {
        toolbar.style.display = "none";
    }
    event.stopPropagation();
    map.updateSize();
}

// Tiling mode, can be 'tiled' or 'untiled'
function setTileMode(tilingMode){
    if (tilingMode == 'tiled') {
        untiled.setVisibility(false);
        tiled.setVisibility(true);
        map.setBaseLayer(tiled);
    }
    else {
        untiled.setVisibility(true);
        tiled.setVisibility(false);
        map.setBaseLayer(untiled);
    }
}

// Transition effect, can be null or 'resize'
function setTransitionMode(transitionEffect){
    if (transitionEffect === 'resize') {
        tiled.transitionEffect = transitionEffect;
        untiled.transitionEffect = transitionEffect;
    }
    else {
        tiled.transitionEffect = null;
        untiled.transitionEffect = null;
    }
}

// changes the current tile format
function setImageFormat(mime){
    // we may be switching format on setup
    if(tiled == null)
      return;
      
    tiled.mergeNewParams({
        format: mime
    });
    untiled.mergeNewParams({
        format: mime
    });
    /*
    var paletteSelector = document.getElementById('paletteSelector')
    if (mime == 'image/jpeg') {
        paletteSelector.selectedIndex = 0;
        setPalette('');
        paletteSelector.disabled = true;
    }
    else {
        paletteSelector.disabled = false;
    }
    */
}

// sets the chosen style
function setStyle(style){
    // we may be switching style on setup
    if(tiled == null)
      return;
      
    tiled.mergeNewParams({
        styles: style
    });
    untiled.mergeNewParams({
        styles: style
    });
}

// sets the chosen WMS version
function setWMSVersion(wmsVersion){
    // we may be switching style on setup
    if(wmsVersion == null)
      return;
      
    if(wmsVersion == "1.3.0") {
       origin = map.maxExtent.bottom + ',' + map.maxExtent.left;
    } else {
       origin = map.maxExtent.left + ',' + map.maxExtent.bottom;
    }
      
    tiled.mergeNewParams({
        version: wmsVersion,
        tilesOrigin : origin
    });
    untiled.mergeNewParams({
        version: wmsVersion
    });
}

function setAntialiasMode(mode){
    tiled.mergeNewParams({
        format_options: 'antialias:' + mode
    });
    untiled.mergeNewParams({
        format_options: 'antialias:' + mode
    });
}

function setPalette(mode){
    if (mode == '') {
        tiled.mergeNewParams({
            palette: null
        });
        untiled.mergeNewParams({
            palette: null
        });
    }
    else {
        tiled.mergeNewParams({
            palette: mode
        });
        untiled.mergeNewParams({
            palette: mode
        });
    }
}

function setWidth(size){
    var mapDiv = document.getElementById('map');
    var wrapper = document.getElementById('wrapper');
    
    if (size == "auto") {
        // reset back to the default value
        mapDiv.style.width = null;
        wrapper.style.width = null;
    }
    else {
        mapDiv.style.width = size + "px";
        wrapper.style.width = size + "px";
    }
    // notify OL that we changed the size of the map div
    map.updateSize();
}

function setHeight(size){
    var mapDiv = document.getElementById('map');
    
    if (size == "auto") {
        // reset back to the default value
        mapDiv.style.height = null;
    }
    else {
        mapDiv.style.height = size + "px";
    }
    // notify OL that we changed the size of the map div
    map.updateSize();
}

function updateFilter(){
	
    var filterType = document.getElementById('filterType').value;
    var filter = document.getElementById('filter').value;
    
    // by default, reset all filters
    var filterParams = {
        filter: null,
        cql_filter: null,
        featureId: null
    };
    if (OpenLayers.String.trim(filter) != "") {
        if (filterType == "cql") 
            filterParams["cql_filter"] = filter;
        if (filterType == "ogc") 
            filterParams["filter"] = filter;
        if (filterType == "fid") 
            filterParams["featureId"] = filter;
    }
    // merge the new filter definitions
    mergeNewParams(filterParams);
}

function resetFilter() {

    document.getElementById('filter').value = "";
    updateFilter();
}

function mergeNewParams(params){
    tiled.mergeNewParams(params);
    untiled.mergeNewParams(params);
}

function initializeLayersDataTableAndLayersModalEvents(geoserverWorkspaceName) {
	var map = initializeMap(geoserverWorkspaceName);
	
	var listLayersByTenantURL = createLink(pageState.rURL, 'layers/listLayersByTenant');
	
	$('table#layersDataTable')
	.off()
	.on('init.dt', function(){
		searchInputFixForLayersDataTable();
		constructToolbarForLayersDataTable(map);
		
		layersDataTableEvents();
		
		$('.layersTab').off('click').on('click', function(){
		    setTimeout(function(){ $('#layersDataTable').DataTable().columns.adjust().draw(); }, 10);
		});
	})
	.on( 'draw.dt', function (e, settings, data) {
		$('#layersDataTable thead th:first').attr('class', '').off();
		$('.layersDataTableContainer table.no-wrap thead th:first').attr('class', '').off();
		
		var info = $('#layersDataTable').DataTable().page.info();
		var pages = info.pages;
		
		if(pages <= 1){
			$('#layersDataTable_paginate').addClass('hidden');
		} else {
			$('#layersDataTable_paginate').removeClass('hidden');
		}
	})
    .dataTable({
		columns : [
			{
				data : "CheckBox",
				orderable : false
			},
			{
				data : "Name",
				orderable : true,
				width: '10%'
			},
			{
				data : "Description",
				orderable : true,
				width: '20%'
			} ,
			{
				data : "GeocodeSystem",
				orderable : true,
				width: '5%'
			} ,
			{
				data : "Status",
				orderable : true,
				width: '5%'
			},
			{
				data : "ReplicationFactor",
				orderable : true,
				width: '5%'
			},
			{
				data : "DescriptionTags",
				orderable : true
			},
			{
				data : "Creator",
				orderable : true
			},
			{
				data : "Created",
				orderable : true,
				width: '7%'
			},
			{
				data : "Style",
				visible : true
			},
			{
				data : "Id",
				visible : false
			},
			{
				data : "TagsObject",
				visible : false
			}
			
		],
		ajax : {
			dataType : 'json',
			contentType: 'application/json',
			url: listLayersByTenantURL,
			type: "post",
			data:  function(d){
			},
			dataSrc: function(serverResponse){
	        	if(serverResponse !== null || serverResponse.length === 0){
	        		
	        		var layerRows = serverResponse;
	        		
	    			for(var i = 0; i < layerRows.length; i++){
	    				layerRows[i] = new layersObjectForDataTable(
	    								'<i class="icon-ok"></i>',
	    								layerRows[i].name,
	    								layerRows[i].description,
	    								layerRows[i].geocodeSystem,
	    								layerRows[i].status,
	    								layerRows[i].replicationFactor,
	    								layerRows[i].tags,
	    								layerRows[i].creator,
	    								layerRows[i].created,
	    								layerRows[i].id,
	    								layerRows[i].style
	    				);
	    				layerRows[i] = surroundObjectPropWithDiv(layerRows[i]);
	    			}
	    			
	    			return layerRows;
	    		} else {
	    			return [];
	    		}
			},
			error: function(){
				onConnectionLostFunction();
			}
		},
		aaSorting: [[1, 'asc']],
		pagingType : "full_numbers",
		language : {
		    	"info": "Showing _START_ - _END_ of _TOTAL_ | ",
				"processing" : "Processing...",
				"paginate": {
			        "next": "",
			        "previous": "",
			        "first": "",
			        "last": ""
			    },
			    "search": "_INPUT_",
		        "searchPlaceholder": "Search..."
		},
        dom: '<"layersDataTableToolbarContainer">frtilp',
        scrollX : true
	});
	
	layersModalEvents();
}

function searchInputFixForLayersDataTable(){
	var $refreshDiv = $('<div></div>', {
		'class': 'refreshDiv',
		'title':"Refresh"
	});
	var $icon_refresh = $('<i></i>', {
		'class' : "icon-refresh"
	});
	
	var $searchDiv =  $('<div></div>', {
		'class': 'searchDiv',
		'data-toggle' : "tooltip",
		'data-placement': "top",
		'title':"Search"
	});
	var $icon_search = $('<i></i>', {
		'class' : "icon-search"
	});
	
	$refreshDiv.append($icon_refresh);
	$searchDiv.append($icon_search);
	$('#layersDataTable_filter').append($searchDiv.prop('outerHTML'));
	$('#layersDataTable_filter').append($refreshDiv.prop('outerHTML'));
	
	$('#layersDataTable_filter .searchDiv').off('click').on('click', function(){
		$(this).closest('#layersDataTable_filter').find('label input:first').animate({width:'toggle'});
		$(this).toggleClass('active');
		$(this).closest('#layersDataTable_filter').find('label input:first').focus();
	});
	
	$('#layersDataTable_filter .refreshDiv').off('click').on('click', function(){
		if ( $.fn.DataTable.isDataTable( 'table#layersDataTable' ) ) {
			$('table#layersDataTable').DataTable().ajax.reload();
		}
	});
	
	$('#layersDataTable_filter label').toggleClass('hideMe');
}

function constructToolbarForLayersDataTable(map){
	var toolbar = $('<div></div>', {
		id: 'toolbar',
		class: 'shownToolbar',
		css: {
			display: 'none'
		}
	});
	
	var $editSelectedBtn = $('<div></div>', {
		id : 'editSelected',
		class: 'insideToolbar',
		text : 'Edit selected'
	}).click(function(event){
		fillEditLayerModal();
		$('#editLayerModal').modal('show');
	});
	
	var $deleteSelectedBtn = $('<div></div>', {
		id : 'deleteSelected',
		text : 'Delete selected',
		class: 'insideToolbar'
	}).click(function(event){		
		$('#deleteLayerModal').modal('show');
	});
	
	var $renderSelectedBtn = $('<div></div>', {
		id : 'renderSelected',
		class: 'insideToolbar',
		text : 'Render selected in map'
	}).click(function(event){
		var datastore = map.get('geoserverWorkspaceName');
		var layerNamesArray = map.get('layerNamesArray');
		var layersByName = map.get('layersByName');
		var layerName = $($('#layersDataTable').DataTable().row('.selected').data().Id).text();
		var layerTitle = $($('#layersDataTable').DataTable().row('.selected').data().Name).text();
		
		for(var i in layersByName){
			map.removeLayer(layersByName[i]);
		}
//		layerNamesArray = [];
//		layerName = {};
		
		var layer = fetchLayerByLayerName(layerName, datastore, layerNamesArray, layersByName);
		
		map.addLayer(layer);
		
		$('#layerNameHeader').text(layerTitle);
		
		$('#RenderLayerInMapModal').modal('show');
		
	});
	
	var $editLayerVisualizationButton = $('<div></div>', {
		id : 'editLayerVisualisation',
		class : 'insideToolbar',
		text : 'Edit layer visualization'
	}).click(function(event) {
		$('#edit-layer-visualization-modal').modal('show');
		
		if( !checkIfLayerAttributesDataTableExists() ) {
			createLayerAttributesDataTable();
		} else {
			reloadLayerAttributesDataTable();
		}
	});
	
//	var $deselectSelectedBtn = $('<div></div>', {
//		id : 'deselectSelected',
//		class: 'insideToolbar',
//		text : 'Deselect selected'
//	}).click(function(event){
//		deselectSelectedRow();
//	});
	
	$(".layersDataTableContainer div.layersDataTableToolbarContainer")
	.addClass('shownToolbar')
	.css('display', 'none')
	.append($editSelectedBtn)
	.append($deleteSelectedBtn)
	.append($renderSelectedBtn)
	.append($editLayerVisualizationButton);
	
	$('#RenderLayerInMapModal').on('shown', function(){
		map.updateSize();
	});
}

function layersObjectForDataTable(
		CheckBox, name,
		description, geocodeSystem,
		status, replicationFactor,
		descriptionTags,
		creator, created,
		id, style){
	this.CheckBox = CheckBox;
	this.Name = name;
	this.Description = description;
	this.GeocodeSystem = geocodeSystem;
	this.Status = status;
	this.ReplicationFactor = replicationFactor;
	this.DescriptionTags = descriptionTags;
	this.Creator = creator;
	this.Created = created;
	this.Id = id;
	this.TagsObject = descriptionTags;
	this.Style = style;
}

function surroundObjectPropWithDiv(object){
	for(var prop in object){
		if(object[prop] === null)object[prop] = "-";
		if(object[prop].length === 0)object[prop] = "-";
		if(object[prop] === '123')object[prop] = "";
		if(Array.isArray(object[prop]) && object[prop].length > 0){
			var variable = "";
			for(var i = 0; i < object[prop].length; i++){
				if(i===object[prop].length-1){
					variable += object[prop][i];
				}else{
					variable += object[prop][i] + ", ";
				}
			}
			object[prop] = variable;
		}
		object[prop] = '<div>' + object[prop] + '</div>';
	}
	return object;
}

function layersDataTableEvents(){
	$('#layersDataTable tbody')
	.on(
			'click',
//			'tr:not(tr.control) td:first-of-type',
			'tr:not(tr.control) td',
			function() {
				if($(this).hasClass('dataTables_empty')){
					return;
				}
				
//				var countSelected = $('#layersDataTable tr.selected').length;
				var countSelected = 0;
				$.each($('#layersDataTable').DataTable().rows().nodes(), function(index){
					if($(this).hasClass('selected'))
						countSelected++;
				});
				
				var clickedRowIndex = $(this).closest('tr').index();
				var currentlySelectedRow = $('#layersDataTable tr.selected').index();
//				if(countSelected === 1 && clickedRow !== currentlySelectedRow)return;
				var selectedRow = $(this).closest('tr')[0];
				var selectProperCheckbox = $(this).closest('tr').find('i.icon-ok')[0];
				$('#layersDataTable tr.selected').not(selectedRow).removeClass('selected');
				$('#layersDataTable i.icon-ok.whiteFont').not(selectProperCheckbox).removeClass('whiteFont');
				
				$.each($('#layersDataTable').DataTable().rows().nodes(), function(index){
					if($(this).hasClass('selected') && $(this).index() !== clickedRowIndex){
						$(this).removeClass('selected');
						$(this).find('i.icon-ok').removeClass('whitefont');
                    }
				});
				
//				$(this).find('i.icon-ok').toggleClass('whiteFont');
				$(this).closest('tr').find('i.icon-ok').toggleClass('whiteFont');
				var groupTeamsTableDataForEditing = [];
				$(this).closest('tr').toggleClass('selected');
				var theData = {};
					theData = $($(this)
							.closest('table')
							.dataTable()
							.fnGetData(
									$('#layersDataTable tr.selected')[0]));
					groupTeamsTableDataForEditing.push(theData);

				var countSelectedRows = $('#layersDataTable tr.selected').length;
				var countTableCells = $('#layersDataTable tbody tr td').length;
				if (countTableCells > 1 && countSelectedRows > 0 && !$('.layersDataTableToolbarContainer').hasClass('opened')) {
					$('.layersDataTableToolbarContainer').addClass('opened');
					$('.layersDataTableToolbarContainer').animate({height:'show'});
				} else if(countSelectedRows === 0) {
					$('.layersDataTableToolbarContainer').animate({height:'hide'});
					$('.layersDataTableToolbarContainer').removeClass('opened');
				}
			});
	
//	$('#layersTagsInput').tagsinput({
//		tagClass: 'tagColor',
//		allowDuplicates: false,
//		maxChars: 40,
//		itemText : 'text',
//		itemValue : 'value'
//	});
}


function hideSpinner(){
	$('.layersDataTableToolbarContainer').removeClass('hideSpinner');
	$('.spinner').hide();
}

function showSpinner(){
	$('.layersDataTableToolbarContainer').addClass('hideSpinner');
	$('.spinner').show();
}

function layersModalEvents() {
//	Delete modal
	$('#acceptDeleteLayerModal')
		.off('click')
		.on('click', function(event){
			deleteLayer();
		});
	
//	Edit modal
	$('#editLayerModalSave').off().on('click', function(){
		saveEditedLayer();
	});
	$('#editLayerModalDeleteLayer').off().on('click', function(){
		deleteLayer();
	});
}

function deleteLayer()
{
	var layerId;
	var theData = {};
	theData = $('#layersDataTable').DataTable().row('.selected').data();
	
	layerId = $(theData.Id).text();
	
	if(layerId == null) return;
	
	var url = createLink(pageState.rURL, 'shapes/deleteLayer');
	
	$.ajax({ 
        url :  url, 
        type : "post", 
        dataType : "json",
        contentType : "application/json",
        data : layerId,
        success : function(response) {
        	$('#deleteLayerModal').modal('hide');
        	$('.layersDataTableToolbarContainer').animate({height:'hide'});
			$('.layersDataTableToolbarContainer').removeClass('opened');
        	$('#layersDataTable tr.selected');
        	$( '#layersDataTable' ).DataTable().ajax.reload();
        },
        error : function(jqXHR, textStatus, errorThrown) {
        	$('#InternalServerErrorModal').modal('show');
        }
       }); 
}

function saveEditedLayer()
{
	var layerId;
	var theData = {};
	theData = $('#layersDataTable').DataTable().row('.selected').data();
	
	layerId = $(theData.Id).text();
	
	if(layerId == null) return;
	
	var LayerMessengerForAdminPortlet = {};
	LayerMessengerForAdminPortlet.id = layerId;
	LayerMessengerForAdminPortlet.name = $('#editedLayerName').val();
	LayerMessengerForAdminPortlet.description = $('#editedLayerDescription').val();
	LayerMessengerForAdminPortlet.creator = "";
	LayerMessengerForAdminPortlet.created = "";
	LayerMessengerForAdminPortlet.geocodeSystem = "";
	LayerMessengerForAdminPortlet.Status = "";
	LayerMessengerForAdminPortlet.isTemplate = 0;
	LayerMessengerForAdminPortlet.style = $('#editedLayerStyle').val();
	LayerMessengerForAdminPortlet.replicationFactor = Number($('#editedLayerRepFactor').find(":selected").text());
	var tags = [];
	tags = $('#layersTagsInput').tagsinput('items');
	$.each(tags, function(index, value){
		tags[index] = value.trim();
	});
	LayerMessengerForAdminPortlet.tags = tags;
	
	var url = createLink(pageState.rURL, 'shapes/updateLayer');
//	var url = createLink(pageState.rURL, 'layers/updateLayer');
	
	$.ajax({ 
        url :  url, 
        type : "post", 
        dataType : "json",
        contentType : "application/json",
        data : JSON.stringify(LayerMessengerForAdminPortlet),
        success : function(response) {
        	$('#deleteLayerModal').modal('hide');
        	$('.layersDataTableToolbarContainer').animate({height:'hide'});
			$('.layersDataTableToolbarContainer').removeClass('opened');
        	$('#layersDataTable tr.selected');
        	$( '#layersDataTable' ).DataTable().ajax.reload();
        },
        error : function(jqXHR, textStatus, errorThrown) {
        	$('#InternalServerErrorModal').modal('show');
        }
       }); 
}

function fillEditLayerModal() {
	var layerName
	var theData = {};
	theData = $('#layersDataTable').DataTable().row('.selected').data();
	
	layerName = $(theData.Name).text();
	
	$('.layerToEditName').text(layerName);
	
	//Name
	$('#editedLayerName').val(layerName);
	
	//Description
	$('#editedLayerDescription').val($(theData.Description).text());
	
	//Creator
	$('#editedLayerCreator').val($(theData.Creator).text());
	
	//Created
	$('#editedLayerCreatedDate').val($(theData.Created).text());
	
	//Geocode System
	$('#editedLayerGeoCodeSystem').val($(theData.GeocodeSystem).text());

	//Tags
	$('#layersTagsInput').tagsinput('removeAll');	
	
	var a = $(theData.DescriptionTags).text().split(',');
	$.each(a, function(i, v) {
		if(v === '-') {
			return true;
		}
		$('#layersTagsInput').tagsinput('add', v);
	});
	
	//Geocode Systmem
//	if($('#editedLayerGeoCodeSystem option').length === 0){
		showSpinner();
		var $optionNull = $('<option></option>', {
			text : "-",
			value : null
		});
		$('#editedLayerGeoCodeSystem').append($optionNull);
		$.ajax({
			url: createLink(pageState.rURL, 'layers/listGeocodeSystems'),
			type: 'post',
			dataType: 'json',
			contentType: 'application/json',
			success: function(response) {
				if(response.status === "Success"){
					$.each(response.response, function(i,v){
						var $option = $('<option></option>', {
							text : v,
							value : i
						});
						$('#editedLayerGeoCodeSystem').append($option);
					});
				} else if(response.status === "Failure") {
					$('#InternalServerErrorModal').modal('show');
				}

				$('#editedLayerGeoCodeSystem option').filter(function(i, e){
					return $(e).text() === $(theData.GeocodeSystem).text();
				}).prop('selected', true);
			},
			error : function(jqXHR, textStatus, errorThrown) {
				$('#InternalServerErrorModal').modal('show');
			},
			complete : function(){
				hideSpinner();
			}
		});	
//	} else {
//		$('#editedLayerGeoCodeSystem option').filter(function(i, e){
//			return $(e).text() === $(theData.GeocodeSystem).text();
//		}).prop('selected', true);
//	}
	
	//Replication Factor
	if($('#editedLayerRepFactor option').length === 0){
		for(var i=1; i < 101; i++){ 
			$('#editedLayerRepFactor').append($('<option></option>').attr('value', i).text(i));
		}
		
		$('#editedLayerRepFactor option').filter(function(i, e){
			return $(e).text() === $(theData.ReplicationFactor).text();
		}).prop('selected', true);
	} else {
		$('#editedLayerRepFactor option').filter(function(i, e){
			return $(e).text() === $(theData.ReplicationFactor).text();
		}).prop('selected', true);
	}
	
	//Styles
	$('#editedLayerStyle option').remove();
//	if($('#editedLayerStyle option').length === 0){
		showSpinner();
		
		$.ajax({
			url: createLink(pageState.rURL, 'styles/getAllStyles'),
			type: 'GET',
			cache : false,
			dataType: 'json',
			success: function(response) {
				var $optionNull = $('<option></option>', {
					text : '-',
					value : null
				});
				$('#editedLayerStyle').append($optionNull);
				
				$.each(response, function(i,v){
					var $option = $('<option></option>', {
						text : v,
						value : v
					});
					$('#editedLayerStyle').append($option);
				});

				$('#editedLayerStyle option').filter(function(i, e){
					return $(e).text() === $(theData.Style).text();
				}).prop('selected', true);
			},
			error : function(jqXHR, textStatus, errorThrown) {
				$('#InternalServerErrorModal').modal('show');
			},
			complete : function(){
				hideSpinner();
			}
		});	
//	} else {
//		$('#editedLayerStyle option').filter(function(i, e){
//			return $(e).text() === $(theData.Style).text();
//		}).prop('selected', true);
//	}
	
	//Is template
//	$('#editedLayerIsTemplate option').filter(function(i, e){
//		return $(e).text() === theData.
//	});
	
//	this.CheckBox = CheckBox;
//	this.Name = name;
//	this.Status = status;
//	this.ReplicationFactor = replicationFactor;
//	this.DescriptionTags = descriptionTags;
	
	var tags = $(theData.DescriptionTags).text();
	if(tags === '-') {
//		alert('oeuo');
	} else {
//		$.each()
	}
}

function tagsForLayersAreInitialized() {
	if($('#tagsContainer .bootstrap-tagsinput').length === 0) {
		return false;
	} else {
		return true;
	}
}

function initializeTagsForLayers() {
	$('#layersTagsInput')
	.on('beforeItemAdd', function(e) {
		if(e.item.value === '' || e.item.text === ''){
			//prevents '' from being added as tag
			e.cancel = true;
		}
		$.each($('#layersTagsInput').tagsinput('items'), function(i,v){
			if(v.trim() === e.item.text){
				e.cancel = true;
				return false;
			}
		});
	})
	.tagsinput({
		allowDuplicates: false,
		maxChars: 40,
		trimValue: true
	});
	
	$('#tagsContainer .bootstrap-tagsinput')
		.addClass('pull-right')
		.addClass('layersTagsInputTagsContainer');
}

function deselectSelectedRow() {
	$('#layersDataTable tbody tr.selected td:first-of-type').click();
}

function initializeMap(geoserverWorkspaceName) {
	var layer1 = new ol.layer.Tile({
    	source : new ol.source.OSM()
    });
	
	var map = new ol.Map({
    	target: 'map',
        controls: ol.control.defaults({
            zoom: true,
            attribution: false,
            rotate: false
          }),
        layers: [
          layer1
        ],
        view: new ol.View({
        	center: ol.proj.fromLonLat([22.00, 37.00]),
        	zoom: 7,
        })
    });
	
	var layersByName = {};
	var layerNamesArray = [];
	
	map.set('geoserverWorkspaceName', geoserverWorkspaceName);
	map.set('layerNamesArray', layerNamesArray);
	map.set('layersByName', layersByName);
	
	return map;
}

function fetchLayerByLayerName(layerName, geoserverWorkspaceName, layerNamesArray, layersByName) {
	var layerAlreadyPlacedOnObject = false;
	for(var j in layerNamesArray){
		if(layerNamesArray[j] === geoserverWorkspaceName + ":"  + layerName){
			layerAlreadyPlacedOnObject = true;
			break;
		}
	}
	
	if(!layerAlreadyPlacedOnObject){
		layerNamesArray.push(geoserverWorkspaceName + ":" +layerName);
//		var index = layerNamesArray.indexOf(geoserverWorkspaceName + ":" +layerName);
		var layer = new ol.layer.Tile({
	    	source: new ol.source.TileWMS({
	    		url : createLink(pageState.rURL, 'wms'),
	    		params : params(/*index*/geoserverWorkspaceName + ":" +layerName),
	    		tileLoadFunction : function (imageTile, src){
	    			src = fixURLIfNotCorrect(src);
	    			
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

function retrieveGeoserverBridgeWorkspace(){
	var geoserverWorkspaceName;
	var url = createLink(pageState.rURL, 'shapes/geoServerBridgeWorkspace');
	
	var callback = function(data){
		geoserverWorkspaceName = data.response;
		if(data === null){
			$('#InternalServerErrorModal').modal('show')
		} else {
			if(!tagsForLayersAreInitialized()) {
				initializeTagsForLayers();
			}
			initializeLayersDataTableAndLayersModalEvents(geoserverWorkspaceName);
		}
	};
	
	$.ajax({
		   url: url,
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
			   
		   },
		   complete: function(){
			   
		   },
		});
}

function params(/*index*/ layername) {
	var layers = [];
//	layers.push(layerNamesOnTheLeft[index]);
	layers.push(layername);
	var getMapObject = {};
	
	getMapObject={
			'BGCOLOR' : '0xcccccc',
			"Layers" : layers,
			"srs" : "EPSG:4326", 
	};
	
	return getMapObject;
}

function fixURLIfNotCorrect(src) {
	var string = 'p_p_resource_id=wms';
	var indexOfString = src.indexOf(string);
	
	var problematicField = src.substring(indexOfString + string.length, indexOfString + string.length + 3);
	var missingString = '%3F';
	if(problematicField !== missingString) {
		var firstHalf = src.substring(0, indexOfString + string.length);
		var secondHalf = src.substring(indexOfString + string.length, src.length);
		src = firstHalf + missingString + secondHalf;
	}
	
	return src;
}
//Layer visualization modal
function createLayerAttributesDataTable() {
	var theURL = window.config.createResourceURL('layers/listLayerAttributesByLayerID');
    
    // Create datatable
    $('#layer-attributes-datatable').PortletDataTable({
    	ajax :	{
	        url : theURL,
	        type : 'POST',
	        cache : false,
	        dataType : "json",
	        beforeSend : function(xhr) {
	        	xhr.setRequestHeader("Accept", "application/json");
	        	xhr.setRequestHeader("Content-Type", "application/json");
	        },
	        data : function() {
	        	var rowData = $('table#layersDataTable').DataTable().row('.selected').data();
	        	var layerID =  $(rowData.Id).text();
	        	return JSON.stringify(layerID);
	        },
	        dataSrc : function(data) {
		        $.each(data, function(index, value) {
		        	
		        	
		        	var $spinnerOrder = $('<select></select>', {
		        		id : 'someID',
		        		class : 'probablySomeClass'
		        	}); 
		        	
		        	for(var i = 0; i < data.length; i++) {
		        		var $option = $('<option></option>', {
			        		text : i,
			        		value : i
			        	});
		        		
		        		$spinnerOrder.append($option);
		        	}
		        	
		        	data[index].attributeAppearanceOrder = $spinnerOrder;
		        });
		        
		        return data;
	        },
	        error : function(jqXHR, exception) {
		        alert('error')
	        },
	        complete : function() {
		        
	        },
	        timeout : 20000
		},
		columnDefs : [{
        	title : "Name",
        	fieldName : "attributeName",
            targets : 0,
			width: '45%'
        }, {
        	title : "Label",
        	fieldName : "attributeLabel",
        	targets : 1,
			width: '45%'
        }, {
        	title : "Order of appearance",
        	fieldName : "attributeAppearanceOrder",
        	targets : 2,
			width: '10%'
        }],
        order : [[0, "asc"]],
    	toolbar : $('#geoadmin-layers-toolbar'),
    	clickRowCallBack : 	doNothing,
    	unClickRowCallBack : doNothing
    });
    
    // Get Widget Instance
    this.dataTable = $('#geoadmin-plugins-datatable').data("dt-PortletDataTable");
}

function reloadLayerAttributesDataTable() {
	$('#layer-attributes-datatable').DataTable().ajax.reload();
}

function doNothing(){
	
}

function checkIfLayerAttributesDataTableExists() {
	if ( $.fn.DataTable.isDataTable( 'table#layer-attributes-datatable' ) )
		return true;
	else
		return false;
}