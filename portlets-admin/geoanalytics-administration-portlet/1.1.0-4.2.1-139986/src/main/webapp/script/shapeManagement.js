pageState = {};

function showShapeManagement(resourceURL, contextPath, data, notificator) {
	pageState.cPath = contextPath;
	pageState.rURL = resourceURL;
	pageState.notificator = notificator;
	
	enableZMaxIndex();
	
	//createSearchForm(data);
	//createAddButton();
	//var usersCon = document.getElementById('userTable');
	//$(usersCon).hide();
    
	populateTermSelector();
	populateImportInstanceSelector();
	
    $('#featureActions').hide();
    $('#toggleSelect').hide();
    $('#editForm').hide();
    $('#editFormTextBoxid').prop('disabled', true);
    $('#editFormTextBoximportId').prop('disabled', true);
    $('div.btn-group .btn').click(function(){
    	  $('div.btn-group .btn').find('input:radio').attr('checked', false);
    	  $(this).find('input:radio').attr('checked', true);
    	});
    var container = document.getElementById("map");
    $('#searchButtonLayer')[0].addEventListener('click', function(ev) {searchButtonListener(ev, container);});
    //createEditForm();
    //createModalAddForm();
	
	$('#layerActionDelete').off('click').click(deleteLayer);
	//enableActionControls();
	
	//showVectorShapes(container,"shape", "68527bd9-dbc6-49bc-9cd4-1cfe3fd36152", null);


	$.getScript(pageState.cPath+'script/OpenLayers.js', function() {
		// pink tile avoidance
	    OpenLayers.IMAGE_RELOAD_ATTEMPTS = 5;
	    // make OL compute scale according to WMS spec
	    OpenLayers.DOTS_PER_INCH = 25.4 / 0.28;
	});

}

function populateTermSelector()
{
	var select = $('#listBoxTerm')[0];
	$(select).empty();
	var option = document.createElement("option");
	option.value = "None";
	option.text = "None";
	option.selected = true;
	select.appendChild(option);
	
	var req = "active=true";
	
	var url = createLink(pageState.rURL, 'admin/shapes/layerTerms');
	
	$.ajax({ 
        url : url,
        type : "get",
        cache: false,
        dataType : "json",
        success : function(terms) 
        		  {
        			for(var i=0; i<terms.length; i++)
        			{
        				option = document.createElement("option");
        				option.value = terms[i].taxonomy+":"+terms[i].term;
        				option.text = terms[i].taxonomy+":"+terms[i].term;
        				select.appendChild(option);
        			}
        			
        			
        			$('#listBoxTerm').on('change', function() {
        				pageState.taxonomyTermSelected = this.value;
        			});
        			
        			
        			
        		  },
	  error: 	function(jqXHR, textStatus, errorThrown) 
			    {
			      alert("The following error occured: " + textStatus, errorThrown);
			    }});
}

function populateImportInstanceSelector()
{return;
	var select = $('#listBoxImportId')[0];
	$(select).empty();
	var option = document.createElement("option");
	option.value = "None";
	option.text = "None";
	option.selected = true;
	select.appendChild(option);
	
	var req = "active=true";
	
	var url = createLink(pageState.rURL, 'admin/shapes/importInstances');
	
	$.ajax({ 
        url : url,
        type : "post", 
        dataType : "json",
        contentType : "application/json",
        success : function(instances) 
        		  {
        			for(var i=0; i<instances.data.length; i++)
        			{
        				option = document.createElement("option");
        				option.value = instances.data[i].importId;
        				option.text = instances.data[i].importId + (isPresent(instances.data[i].termTaxonomy) ? " for " + instances.data[i].termTaxonomy + ":" + instances.data[i].term : "") +
						" on " + timestampToDateTimeString(instances.data[i].timestamp);
        				select.appendChild(option);
        			}
        		  },
	  error: 	function(jqXHR, textStatus, errorThrown) 
			    {
			      alert("The following error occured: " + textStatus, errorThrown);
			    }});
}

function searchButtonListener(ev, container) {
	if (pageState.taxonomyTermSelected == undefined) {
		pageState.notificator.notification("error", "Not a selected taxonomy term");
		$("#notification").addClass("in");
		return;
	}
	
	var values = $('#searchForm').serializeArray();
	
	var selection = {};
//	for(var i=0; i<values.length; i++)
//	{
//		switch(values[i].name) {
//		case 'term':
//			if(values[i].value != 'None') {
//				selection.terms = values[i].value;
//				if(selection.terms.constructor != Array) selection.terms = [selection.terms];
//			}
//			break;
//		case 'importId':
//			if(values[i].value != 'None') {
//				selection.importInstances = values[i].value;
//				if(selection.importInstances.constructor != Array) selection.importInstances = [selection.importInstances];
//			}
//			break;
//		case 'shapeId' :
//			if($.trim(values[i].value) != '') {
//				selection.id = values[i].value;
//			}
//			break;
//		}
//	}
	
	for(var i=0; i<values.length; i++) {
		if((values[i].value == 'None' && pageState.taxonomyTermSelected!=undefined) ||
		   (values[i].value != 'None' && pageState.taxonomyTermSelected!=undefined)) {
			selection.terms = pageState.taxonomyTermSelected;
			if(selection.terms.constructor != Array) selection.terms = [selection.terms];
		}
	}
	
	
	if($('#none').prop('checked'))
		selection.geoSearchType = "None";
	if($('#boundingBox').prop('checked'))
	{
		selection.geoSearchType = "BoundingBox";
		if(window.featureMap)
		{
			window.featureMap.destroy();
			window.featureMap = null;
		}
		showShapes(container, 'bbox', null, null, new OpenLayers.Bounds(19.2734, 34.2271, 28.8975, 42.0493));
		//showVectorShapes will be called from within the bounding box control handler
		return;
	}
	else if($('#proximity').prop('checked'))
		selection.geoSearchType = "Proximity"; //TODO
	else
		selection.geoSearchType = "None";
	
	if(window.featureMap)
	{
		window.featureMap.destroy();
		window.featureMap = null;
	}
	showVectorShapes(container, 'search', selection, null);
		
	//if(isPresent(values['']))
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
		url = "admin/shapes/search";
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
	
	

	
	var shapeSearchSelection = {};
	shapeSearchSelection.terms = [];
	shapeSearchSelection.terms = data.terms;
	
//	if (data.importInstances != undefined) {
//		shapeSearchSelection.importInstances = [];
//		shapeSearchSelection.importInstances.push()
//
//	} else
	shapeSearchSelection.importInstances = undefined;
	
	shapeSearchSelection.id = undefined;
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
			                 	 features[i].attributes.termName = shapes[s].termName;
			                 	 features[i].attributes.termTaxonomy = shapes[s].termTaxonomy;
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

function deleteLayer()
{
	var term = $('#listBoxTerm :selected').val();
	if(term == 'None') return;
	
	var url = createLink(pageState.rURL, 'admin/shapes/deleteLayer');

	
	$.ajax({ 
        url :  url, 
        type : "post", 
        dataType : "json",
        contentType : "application/json",
        data : term,
        success : function(response) 
        		  { 
        			if(response.status == false) {
        				pageState.notificator.notification("error", "An error has occurred while deleting shapes: " + response.message);
        	        	$("#notification").addClass("in");
        	        	return;
        			}
        			pageState.notificator.notification("success", "Layer deleted");
    	        	$("#notification").addClass("in");
        			populateTermSelector();
        			populateImportInstanceSelector();
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
         		 {
 	   			   alert("The following error occured: " + textStatus, errorThrown);
         		 }
       }); 
}

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