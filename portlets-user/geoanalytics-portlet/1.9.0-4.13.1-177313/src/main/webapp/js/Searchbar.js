//div#DecisionSupportSystem, portlet specific jquery
function searchBar(){
	$('div#DecisionSupportSystem button#DSSSearchbarDropdown').off('click').on('click', function(){
		$(this).toggleClass('clicked');
		$('div#DecisionSupportSystem  ul#searchBarDropdown-menu').toggleClass('shown');
	});
	
	$('div.pickViewport').off('click').on('click', function(){
		if(!$(this).hasClass('clicked')){
			$.each($('div.pickViewport.clicked'),function(){
				$(this).find('i').removeClass('fa-dot-circle-o').addClass('fa-circle-o');
			});
			$('div.pickViewport.clicked').removeClass('clicked');
			$(this).addClass('clicked');
			$(this).find('i').removeClass('fa-circle-o').addClass('fa-dot-circle-o');
		}
		
		map.removeInteraction(drawDSS);
		
		if($(this).attr('id') === 'selectedAreaViewport') {
			drawInteractionIsAdded = true;
			
			drawDSS = new ol.interaction.Draw({
				source: vectorSource,
				type: /** @type {ol.geom.GeometryType} */ ('LineString'),//LineString-->Box
				geometryFunction: geometryFunctionDSS,
		        maxPoints: 2
			});
			
			map.addInteraction(drawDSS);
			
			var control = addClearMapControl();
			
			map.addControl(control);
			
			activeControls['clearMap'] = control;
		} else {
			drawInteractionIsAdded = false;
			
			clearDDSMapFunction();
			
			map.removeControl(activeControls['clearMap']);
			delete activeControls['clearMap'];
		}
	});
}

function radioButtonToggle(){
	$.each($('div.pickViewport'), function() {
		if($(this).hasClass('clicked')) {
			$(this).find('i').removeClass('fa-circle-o').addClass('fa-dot-circle-o');
		}else {
			$(this).find('i').removeClass('fa-dot-circle-o').addClass('fa-circle-o');
		}
	});
}

var geometryFunctionDSS = function(coordinates, geometry) {
	clearDDSMapFunction();
	
    if (!geometry) {
    	geometry = new ol.geom.Polygon(null);
    }
    
    var start = coordinates[0];
    var end = coordinates[1];
    geometry.setCoordinates([
                             [start, [start[0], end[1]], end, [end[0], start[1]], start]
                             ]);
    var extent = geometry.getExtent();
    
    drawnGeometryExtent = extent;
    
    return geometry;
}

function clearDDSMapFunction() {
	
	vectorSource.clear();
	
}