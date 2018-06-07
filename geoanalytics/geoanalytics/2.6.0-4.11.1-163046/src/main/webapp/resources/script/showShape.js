var map;
var untiled;
var tiled;

// pink tile avoidance
OpenLayers.IMAGE_RELOAD_ATTEMPTS = 5;
// make OL compute scale according to WMS spec
OpenLayers.DOTS_PER_INCH = 25.4 / 0.28;

function initialize(container)
{
	//var options = document.createElement('img');
	//options.id = container.id + '_showShapeOptions';
	//container.appendChild(options);
	var toolbar = document.createElement('div');
	document.getElementById(container.id+"_showShapeToolbar");
	var featureInfo = document.createElement('div');
	featureInfo.id = container.id+'_showShapeFeatureInfo';
	container.appendChild(featureInfo);
}

function showShape(container, layer, bounds, boundaryTermTaxonomy, boundaryTerm){
    initialize(container);
    
    if(isPresent(boundaryTermTaxonomy) && isPresent(boundaryTerm))
    {
    	var req = {};
    	req.taxonomy = boundaryTermTaxonomy;
    	req.term = boundaryTerm;
    	req = JSON.stringify(req);
    	
    	$.ajax({ 
            url :  "../shapes/retrieveByTerm", 
            type : "post", 
            dataType : "json",
            contentType : "application/json",
            data : req,
            success : function(shapes) 
            		  { 
            			 var allFeatures = [];
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
    			                     //features[i].geometry.transform(projWGS84, proj900913);
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
            		    return doShowShape(container, layer, bounds, allFeatures);
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
    else
    	return doShowShape(container, layer, bounds);
}

function doShowShape(container, layer, bounds, features)
{
	format = 'image/png';
    
    var bounds = new OpenLayers.Bounds(
        bounds.minx, bounds.miny,
        bounds.maxx, bounds.maxy
    );
    var options = {
        controls: [],
        maxExtent: bounds,
        maxResolution: 0.0038268482997141,
        projection: "EPSG:4326",
        units: 'degrees'
    };
    var map = new OpenLayers.Map(container, options);

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
    var layers = [];
    layers.push(untiled);
    layers.push(tiled);
    if(features)
    {
    	vector = new OpenLayers.Layer.Vector("Boundary", {isBaseLayer: false});
    	vector.addFeatures(features);
    	layers.push(vector);
    }

    map.addLayers(layers);

    // build up all controls
    map.addControl(new OpenLayers.Control.PanZoomBar({
        position: new OpenLayers.Pixel(2, 15)
    }));
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    map.addControl(new OpenLayers.Control.Navigation());
    map.addControl(new OpenLayers.Control.Scale($('scale')));
    map.addControl(new OpenLayers.Control.MousePosition({element: $('location')}));
    map.zoomToExtent(bounds);
    
    // wire up the option button
    //var options = document.getElementById("options");
//    options.addEventListener('click', function(ev)
//    								  {
//    									toggleControlPanel(ev, container);
//    								  });
    
    // support GetFeatureInfo
    map.events.register('click', map, function (e) {
        document.getElementById(container.id+'_showShapeFeatureInfo').innerHTML = "Loading... please wait...";
        var params = {
            REQUEST: "GetFeatureInfo",
            EXCEPTIONS: "application/vnd.ogc.se_xml",
            BBOX: map.getExtent().toBBOX(),
            SERVICE: "WMS",
            INFO_FORMAT: 'application/json',
            QUERY_LAYERS: map.layers[0].params.LAYERS,
            FEATURE_COUNT: 50,
            Layers: 'geopolis:'+layer,
            WIDTH: map.size.w,
            HEIGHT: map.size.h,
            format: format,
            styles: map.layers[0].params.STYLES,
            srs: map.layers[0].params.SRS};
        
     /*   var loc = map.getLonLatFromPixel(xy);
        var getTileUrl = layer.getURL(
            new OpenLayers.Bounds(loc.lon, loc.lat, loc.lon, loc.lat)
        );
        var params = OpenLayers.Util.getParameters(getTileUrl);
        var tileInfo = layer.getTileInfo(loc);
      */
        
        // handle the wms 1.3 vs wms 1.1 madness
        if(map.layers[0].params.VERSION == "1.3.0") {
            params.version = "1.3.0";
            params.j = parseInt(e.layerX);
            params.i = parseInt(e.layerY);
        } else {
            params.version = "1.1.1";
            alert("e.xy.x=" + e.xy.x + "\n"+ "e.xy.y=" + e.xy.y+"\n" + "e.layerX=" + e.layerX + "\n e.layerY=" + e.layerY);
            params.x = parseInt(e.layerX);
            params.y = parseInt(e.layerY);
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
    return map;
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