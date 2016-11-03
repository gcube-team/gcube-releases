function addControls(linkName, theMap){
	var controlsMap = {};
	controlsMap.navcross = panNavToolbar;
	controlsMap.zoomSlider = addZoomSliderToMap;
	controlsMap.setInitialExten = adjustMapToInitialExtent;
	
	controlsMap[linkName](theMap);
}

function dockedGISToolsButtonControl(theMap){
	app.dockedGISToolsButtonControl = function(opt_options){
		var options = opt_options || {};
		
		var $dockedButtonContainer = $('<div></div>', {
			id: 'dockedGISContainer',
			class: 'ol-unselectable ol-control'
		})
		
		var $dockedButton = $('<button></button>', {
			id: 'dockedGIS'
		});
		
		$dockedButton.attr('title', 'Tools');
		
		var $caretForDockedButton = $('<i></i>', {
			class: 'fa fa-caret-down',
			'aria-hidden' : 'true'
		});
		
		$dockedButton.append($caretForDockedButton);
		$dockedButtonContainer.append($dockedButton);
		
		var css = {
			display : 'inline-block',
			cursor : 'pointer',
			color : 'black',
			'text-decoration' : 'none'
		};
		var onClickFunc = function(){
			var checkIconClass = "fa fa-check";
			var $icon = $(this).find('i');
			$icon.toggleClass(checkIconClass);
			$dockedButton.trigger('click');//close popover
			var className = $(this).attr('class');
			dockeGISControlButtonsPressed[className] = !dockeGISControlButtonsPressed[className];

			if(!$icon.hasClass(checkIconClass)){
				if(activeControls[className].hasOwnProperty('onDestroy')){
					activeControls[className].onDestroy();
				}
				theMap.removeControl(activeControls[className]);
				delete activeControls[className];
			}else{
				addControls(className, map);
			}
		};
		
		var popoverOptions = {
//				animation: '',
				html : true,
				placement : 'top',
//				selector : '',
				trigger : 'click',
				title : 'Tools',
				content : function(){
					var $checkIcon = $('<i></i>',{
						class: "",
						'aria-hidden' : true
					});
					
					var $wrapper = $('<a></a>', {
						class : 'controlsWrapper'
					});
					
					var $navCross = $('<a></a>', {
						class : 'navcross',
						text : 'navcross',
						css : css
					});
					
					var $zoomSlider = $('<a></a>',{
						text : 'zoomSlider',
						class : 'zoomSlider',
						css : css
					});
					
					$navCross.append($checkIcon);
					$zoomSlider.append($checkIcon.clone());
					$navCross.find('i').attr('class', findCheckedControlELement($navCross.attr('class')));
					$zoomSlider.find('i').attr('class', findCheckedControlELement($zoomSlider.attr('class')));
					
					$navCross.click(onClickFunc);
					$zoomSlider.click(onClickFunc);
					
					$wrapper.append($navCross);
					$wrapper.append($zoomSlider);
					
					return $wrapper;
				},
//				delay : ''
		};
		
		$dockedButton.popover(popoverOptions);
		
		ol.control.Control.call(this, {
          element: $dockedButtonContainer[0],
          target: options.target,
          map: options.map
        });
	};
	
	ol.inherits(app.dockedGISToolsButtonControl, ol.control.Control);

	if(map !== undefined)
		theMap.addControl(new app.dockedGISToolsButtonControl());
	
	dockeGISControlButtonsPressed.navCross = false;
	dockeGISControlButtonsPressed.zoomSlider = false;
}

function findCheckedControlELement(className){
	var checkIconClass = "fa fa-check";
	if(dockeGISControlButtonsPressed[className] === true){
		return checkIconClass;
	}else{
		return '';
	}
}

function panFunction(value,theMap){
	
	var mapButtonIDsToCoords = {
//			top : [0,3],
//			bottom : [0,1],
//			left : [0,1],
//			right : [2,1]
	};
	
	mapButtonIDsToCoords['top'+theMap.get('mapId')] = [0,3];
	mapButtonIDsToCoords['bottom'+theMap.get('mapId')] = [0,1];
	mapButtonIDsToCoords['left'+theMap.get('mapId')] = [0,1];
	mapButtonIDsToCoords['right'+theMap.get('mapId')] = [2,1];
	
	var current_extent=theMap.getView().calculateExtent(theMap.getSize()); 
    var current_center=theMap.getView().getCenter();
	
    var id = $(value).attr('id');
    var clicked_settings = mapButtonIDsToCoords[id];
    
    var pan = ol.animation.pan({
    	source: theMap.getView().getCenter()
    });
    
    theMap.beforeRender(pan);
    
    var cur_loc;
    var rightId = 'right'+theMap.get('mapId');
    var leftId = 'left'+theMap.get('mapId');
    if(id === rightId || id === leftId){
    	cur_loc = [
    	        current_extent[clicked_settings[0]],
    	        current_center[clicked_settings[1]]
    	];
    } else {
    	cur_loc = [
    	        current_center[clicked_settings[0]],
    	        current_extent[clicked_settings[1]]
    	];
    }
    
    theMap.getView().setCenter(cur_loc);
}

function panNavToolbar(theMap){

	/**
     * @constructor
     * @extends {ol.control.Control}
     * @param {Object=} opt_options Control options.
     */
	app.CustomControl = function(opt_options){
		var options = opt_options || {};
		
		var panFunc = function(){
			panFunction(this, theMap);
		}
		
		var mapId = theMap.get('mapId');
		var top = 'top' + mapId;
		var $topButton = $('<button></button>', {
			class : 'custom-navtoolbarcontrol-topbutton',
			id : top
		});
		
		var $up = $('<i></i>', {
			class : "fa fa-arrow-up",
			"aria-hidden" : true
		});
		
		$topButton.append($up);
		
		var bottom = 'bottom' + mapId;
		var $bottomButton = $('<button></button>', {
			class : 'custom-navtoolbarcontrol-bottomButton',
			id : bottom
		});
		
		var $down = $('<i></i>', {
			class : "fa fa-arrow-down",
			"aria-hidden" : true
		});
		
		$bottomButton.append($down);
		
		var left = 'left' + mapId;
		var $leftButton = $('<button></button>', {
			class : 'custom-navtoolbarcontrol-leftButton',
			id : left
		});
		
		var $left = $('<i></i>', {
			class : "fa fa-arrow-left",
			"aria-hidden" : true
		});
		
		$leftButton.append($left);

		var right = 'right' + mapId;
		var $rightButton = $('<button></button>', {
			class : 'custom-navtoolbarcontrol-rightButton',
			id : right
		});
		
		var $right = $('<i></i>', {
			class : "fa fa-arrow-right",
			"aria-hidden" : true
		});
		
		$rightButton.append($right);
		
		$topButton.click(panFunc);
		$bottomButton.click(panFunc);
		$leftButton.click(panFunc);
		$rightButton.click(panFunc);
		
		var $element = $('<div></div>', {
			class : 'custom-navtoolbarcontrol-container ol-unselectable ol-control'
		});
		
		$element.append($topButton);
		$element.append($bottomButton);
		$element.append($leftButton);
		$element.append($rightButton);
		
		ol.control.Control.call(this, {
          element: $element[0],
          target: options.target,
          map: options.map
        });

	};
	
	ol.inherits(app.CustomControl, ol.control.Control);

	if(theMap !== undefined){
		var navcross = new app.CustomControl();
		activeControls.navcross = navcross;
		theMap.addControl(navcross);
	}

}

function addZoomSliderToMap(theMap){
	var zoomSlider = new ol.control.ZoomSlider();
	theMap.addControl(zoomSlider);
	$('div#DecisionSupportSystem .ol-zoom-out').addClass('distant');
	
	zoomSlider.onDestroy = function(){
		$('div#DecisionSupportSystem .ol-zoom-out.distant').removeClass('distant');
	}
	
	activeControls.zoomSlider = zoomSlider;
}

function adjustMapToInitialExtent(){

		var initialExtent = function(opt_options) {

	        var options = opt_options || {};
	        
	        var $button = $('<button></button>',{
	        	id:'setToInitialExtent',
	        	title: 'Set extent'
	        });
	        var $icon = $("<i></i>",{
	        	class: 'fa fa-arrows-alt'
	        });
	        $button.append($icon);
	
	        var this_ = this;
	        var setExtent = function() {
	        	if(typeof extentForCenteringDSSMap !== "undefined"){
		        	map.getView().fit(extentForCenteringDSSMap, map.getSize());
	        	}
	        };
	        
	        $button.click(setExtent);
	
//	        button.addEventListener('click', handleRotateNorth, false);
//	        button.addEventListener('touchstart', handleRotateNorth, false);
	        
	        var $element = $('<div></div>',{
	        	class:'rotate-north ol-unselectable ol-control'
	        });
	        $element.append($button);
	
	        ol.control.Control.call(this, {
	          element: $element[0],
	          target: options.target
	        });

      };
      ol.inherits(initialExtent, ol.control.Control);
      
      return new initialExtent();

//  	if(theMap !== undefined){
//  		var initExtent = new initialExtent();
//  		theMap.addControl(initExtent);
//  	}
}