function retrieveAvailableLayersAndPlaceThemOnTheLeft(userinfoObject) {
	var retrieveTaxonomiesURL = listLayersOfType;
	var retrieveLayersByTaxonomyIDURL = listLayersByTaxonomyID;
	var retrieveListOfAllLayersURL = listOfAllLayers;
	
	mapLayersLoaded = true;
	
	$('#treeviewTaxonomiesLayers, #treeviewTaxonomiesLayersResponsive')
	.on('select_node.jstree', function(e, data) {
		if ($('#' + data.node.id).attr('aria-level') === '1') {
//			map.addLayer(fetchLayerByLayerName(data.node.text));
			map.addLayer(fetchLayerByLayerName(data.node.id));
		} else {
			$.each(data.node.children, function(index, value) {
				var theLayerName = $('#' + value).find('a').text();
				map.addLayer(fetchLayerByLayerName(theLayerName));
			});
		}
		$('#layersCount').text(layerNamesOnTheLeft.length);
//		$.each(layerNamesOnTheLeft, function(index, value) {
//			$('#layersTagsInput').tagit("createTag", value);
//		});
		$('#noLayersSelected, #tagsInputContainer').addClass('hidden');
		if (layerNamesOnTheLeft.length > 0) {
			$('#tagsInputContainer').removeClass('hidden');
		} else {
			$('#noLayersSelected').removeClass('hidden');
		}
	}).on('deselect_node.jstree', function(e, data) {
		if ($('#' + data.node.id).attr('aria-level') === '1') {
//			map.removeLayer(layersByName[data.node.text]);
			map.removeLayer(layersByName[data.node.id]);
			map.updateSize();
//			var indexOfItemToBeRemoved = layerNamesOnTheLeft.indexOf(geoserverWorkspaceName + ":"  + data.node.text);
			var indexOfItemToBeRemoved = layerNamesOnTheLeft.indexOf(geoserverWorkspaceName + ":"  + data.node.id);
			if (indexOfItemToBeRemoved > -1) {
				layerNamesOnTheLeft.splice(indexOfItemToBeRemoved, 1);
//				delete layersByName[data.node.text];
				delete layersByName[data.node.id];
			}
		} else {
			$.each(data.node.children, function(index, value) {
				var theLayerName = $('#' + value).find('a').text();
				map.removeLayer(layersByName[theLayerName]);
				var indexOfItemToBeRemoved = layerNamesOnTheLeft.indexOf(geoserverWorkspaceName + ":"  + theLayerName);
				if (indexOfItemToBeRemoved > -1) {
					layerNamesOnTheLeft.splice(indexOfItemToBeRemoved, 1);
				}
			});
			map.updateSize();
		}
		$('#layersCount').text(layerNamesOnTheLeft.length);
//		$.each(layerNamesOnTheLeft, function(index, value) {
//			$('#layersTagsInput').tagit("createTag", value);
//		});
		$('#noLayersSelected, #tagsInputContainer').addClass('hidden');
		if (layerNamesOnTheLeft.length > 0) {
			$('#tagsInputContainer').removeClass('hidden');
		} else {
			$('#noLayersSelected').removeClass('hidden');
		}
	}).jstree({
		plugins : [ 'checkbox', 'sort', 'wholerow', 'search' ],
		search : {
			'show_only_matches' : true
		},
		checkbox : {
			keep_selected_style : false
		},
		core : {
			themes : {
				'stripes' : true
			},
			"check_callback" : true,
			data : {
				url : function(node) {
					if (node.id === '#') {
						return listLayersByProjectUrl;
					} else {
					}
				},
				type : 'post',
				dataType : "json",
				contentType : 'application/json',
				data : function(node) {
					return JSON.stringify(userinfoObject);
				},
				success : function(serverResponse) {
//					$('.functionsAccordion .modelChooseALayer').html('');
					var layerNames = [];
					var theLayers = [];
					for (i = 0; i < serverResponse.length; i++) {
						layerNames.push(serverResponse[i].text);
						if($.inArray(serverResponse[i].text, featureInfoLayers) === -1){
//							featureInfoLayers.push(serverResponse[i].text);
							featureInfoLayers.push(serverResponse[i].id);
						}
						var layer = {};
						layer.id = serverResponse[i].id;
						layer.name = serverResponse[i].text;
						theLayers.push(layer);
//						var opt = $('<option></option>',{
//							text:serverResponse[i].text
//						})
//						$('.functionsAccordion .modelChooseALayer').append(opt);
					}
					
//					$('#layersPanel').data('layers', layerNames);
					$('#layersPanel').data('layers', theLayers);
					
					var auxiliaryArray = featureInfoLayers;
					featureInfoLayers = [];
					$.each(auxiliaryArray, function(index, value){
						var newValue = geoserverWorkspaceName + ":" + value;
						if($.inArray(newValue, featureInfoLayers) === -1 && value.indexOf(":") === -1){
							featureInfoLayers.push(newValue);
						}
					});
//					$('.functionsAccordion .modelChooseALayer').selectmenu('refresh');
					justLayerNames = layerNames;
					
					autocompletePluginInit(layerNames);
					populateAccordionWithHTMLEelemmentsBeforeInitializing2();
					
					var layers = $('#layersPanel').data('layers');
//					$.each(pluginObjects, function(index, value){
//						if(value.isPluginLoaded === true){
//							var name = getWidgetName(value);
//							value.pluginContainerElement[name]('setLayers', layers);
//						}
//					});
					
//					updateAvailablePluginsAccordion();
				},
				error : function(jqXHR, textStatus, errorThrown) {
					$('#errorModal').modal('show');
				},
				complete : function(data) {
				}
			}
		}
	});
	
}

function updateAvailablePluginsAccordion(){
	var theUrl = listProjectPluginsUrl;
	var callback = function(data){
		hideSpinner();
		if(data.status === "Success"){
			
			//global
			pluginObjects = [];
			$.each(data.response, function(index, value) {
				pluginObjects.push(value);
			});
			
			if(pluginObjects.length > 0){
				buildAccordion(RIGHTSIDE_ACCORDION, pluginObjects);
				
				loadPlugin(pluginObjects[0]);
				
				loadPluginOnAccordionHeaderClick();
			}
			
		} else {
			$('.wizard').modal('hide');
			$('#InternalServerErrorModal').modal('show');
		}
	};
	var data = userinfoObject;
	AJAX_Call_POST(theUrl, callback, userinfoObject);
}

function extractCoordinates(coordinatesArray){
	var coordinates = [];
	coordinates = coordinatesArray.split(",");
	map.getView().fit(coordinates, map.getSize());
	map.updateSize();
}


//jQuery autocomplete plugin

function autocompletePluginInit(layerNames) {
	var termTemplate = "<span class='ui-autocomplete-term'>%s</span>";
	layerNames.sort();
	
	$('#DSSSearchbar').autocomplete({
		source : layerNames,
		autoFocus : true,
		minLength: 0,
		
		create : function() {
			$(this).data('ui-autocomplete')._renderItem = function(ul, item) {
				return $("<li>").addClass("autocomplete-item").attr("data-value", item.value)
				.append(item.label).appendTo(ul);
			};
		},

		response : function(e, ui) {
			autocompleteHintInit(ui);			
		},
		
		select: function (e, ui) {
			$('#autocomplete-hint').val('');
			$('#treeviewTaxonomiesLayers').jstree(true).search(ui.item.value);
		},
		
		open: function(){
			var acData = $(this).data('ui-autocomplete');
			acData.menu.element.find('li').each(function() {
				var me = $(this);
				var keywords = acData.term.split(' ').join('|');
				me.html(me.text().replace(new RegExp("(" + keywords + ")", "gi"), '<b>$1</b>'));
			});
			$('ul.ui-autocomplete').addClass('opened');
		},

		close : function() {
			$('ul.ui-autocomplete').removeClass('opened').css('display', 'block');
		}
	});
	
	// clear hint when search input gets cleared
	
	$('#DSSSearchbar').on('input', function() {
		if (!$('#DSSSearchbar').val().length) {
			$('#autocomplete-hint').val('');
		}
	});

	// attach jstree search plugin to search bar

	var to = false;

	$('#DSSSearchbar').keyup(function(){
	    $('#treeviewTaxonomiesLayers').jstree(true).show_all();
	    $('#treeviewTaxonomiesLayers').jstree('search', $(this).val());
	});
	
	$('#treeviewTaxonomiesLayers').on('search.jstree', function (nodes, str, res) {
	    if (str.nodes.length===0) {
	    	$('#treeviewTaxonomiesLayers').jstree(true).hide_all();
	    }
	})
}

// autocomplete hint while typing	

function autocompleteHintInit(ui){
	var currentInput = $('#DSSSearchbar').val();			
	var hint = true;

	if(ui.content.length && currentInput.length){
		var firstSuggestion = ui.content[0].label;

		for (i = 0; i < currentInput.length; i++) {
			if (currentInput[i].toLowerCase() !== firstSuggestion[i].toLowerCase()) {
				hint = false;
				break;
			}
		}				
	} else {
		hint = false;
		$('#DSSSearchbar').autocomplete('close');
	}

	if (hint) {
		currentInput = firstSuggestion.substr(0, currentInput.length);
		$('#autocomplete-hint').val(firstSuggestion);
		$('#DSSSearchbar').val(currentInput);
	} else {
		$('#autocomplete-hint').val('');		
	}
}

function retrieveAvailableLayersAndPlaceThemOnTheLeftOld(){
	var url = listLayersOfType;//createLink('shapes/listLayersOfType', resourceURL);
	var context = $('div#DecisionSupportSystem div#layersPanel')[0];
	var callback = function(data, domElement){
		if(data.status === "Success"){
			if(typeof(data.response) ==='object' && !$.isEmptyObject(data.response)){
				var layersAccordion = $('<div></div>', {
					class : 'accordion',
					id : 'layersAccordion'
				});
				for(var taxonomyName in data.response){
					if(Array.isArray(data.response[taxonomyName]) && data.response[taxonomyName].length !== 0){
						var accordionGroup = $('<div></div>', {
							class : 'accordion-group'
						});
						var accordionHeading = $('<div></div>', {
							class: 'accordion-heading'
						});
						var accordiongToggle = $('<a></a>',{
							class: 'accordion-toggle',
							'data-toggle':"collapse",
							'data-parent':"#layersAccordion",
							href: "#"+taxonomyName,
							text: taxonomyName
						});
						
						var accordionBody = $('<div></div>',{
							class: 'accordion-body collapse',
							id: taxonomyName
						});
						
						var accordionInner = $('<div></div>',{
							class: 'accordion-inner'
						});
						var layers = data.response[taxonomyName];
						for(var i=0; i< layers.length; i++){
							var layerAlreadyPlacedOnTheLeft = false;
							for(var j in layerNamesOnTheLeft){
								if(layerNamesOnTheLeft[j] === layers[j]/*data.response[taxonomyName]*/){
									layerAlreadyPlacedOnTheLeft = true;
									break;
								}
							}
							if(layerAlreadyPlacedOnTheLeft === true){
								continue;
							}
							
							layerNamesOnTheLeft.push(geoserverWorkspaceName + ":"  + layers[i]/*data.response[taxonomyName]*/);
							
							var layerContainer = $('<div></div>',{
								'class' : 'layerContainer row-fluid'
							});
							
							var inputContainer = $('<div></div>',{
								'class':'inputContainer span1 row-fluid'
							});
							
							var divThatLooksLikeCheckbox = $('<div></div>',{
								'class': 'divThatLooksLikeCheckbox span10 offset2'
							});
							
							var coloredCheckbox = $('<div></div>',{
								'class': 'coloredCheckbox'
							});
							
							divThatLooksLikeCheckbox.append(coloredCheckbox);
							inputContainer.append(divThatLooksLikeCheckbox);
							layerContainer.append(inputContainer);
							
							var layerTextContainer = $('<div></div>',{
								'class':'layerTextContainer span9 offset1 row-fluid'
							});
							
							var layerText = $('<div></div>',{
								'class': 'layerText span12',
								text : layers[i]//data.response[taxonomyName]
							});
							
							var layerSubtext = $('<div></div>',{
								'class': 'layerSubtext span12',
								text : taxonomyName//layers[i]/*data.response[taxonomyName]*/ + " subtext"
							});
							
							
							layerTextContainer.append(layerText).append(layerSubtext);
							layerContainer.append(layerTextContainer);
							
							
							accordionInner.append(layerContainer);
							accordionBody.append(accordionInner);
							
							accordionHeading.append(accordiongToggle);
							accordionGroup.append(accordionHeading);
							accordionGroup.append(accordionBody);
						}
						layersAccordion.append(accordionGroup);
					}
				}

				$(domElement).append(layersAccordion);
			}
			$('div#DecisionSupportSystem div.divThatLooksLikeCheckbox ').off('click').on('click', function(){
				$(this).toggleClass('clicked');
				var layerName = $(this).closest('.layerContainer').find('.layerText').text();
				addLayers(layerName, $(this).hasClass('clicked'));
			});
		}
	};
	
	var data = 'LAYERTAXONOMY';
	AJAX_Call_POST_Single_String(url, callback, data, context);
}

function retrieveLayers(){
	var context = $('div#DecisionSupportSystem div#layersPanel')[0];
	var callback = function(data, domElement){
//		console.log(data.length);
	};
	var lNames = [];
	lNames.push(geoserverWorkspaceName + ":" + "Taxon1Lakonia");
	var params = {
  	  REQUEST: "GetFeatureInfo",
      EXCEPTIONS: "application/vnd.ogc.se_xml",
      BBOX: map.getView().calculateExtent(map.getSize()),//map.getExtent().toBBOX(),
      SERVICE: "WMS",
      INFO_FORMAT: 'application/json',
      QUERY_LAYERS: lNames,
      FEATURE_COUNT: 50,
      Layers: lNames,
      WIDTH: map.getSize()[0],
      HEIGHT: map.getSize()[1],
      format: "application/json",
      styles: "",
      srs: "EPSG:900913"
	};
	var parameters = "REQUEST=" + "GetFeatureInfo" +
	"&SERVICE=" + "WMS" +
	'&INFO_FORMAT=' + 'application/json'+
//	"&QUERY_LAYERS=" + lNames +
	'&FEATURE_COUNT=' + 50 +
	"&Layers=" + lNames +
	'&WIDTH=' +  map.getSize()[0] +
	"&HEIGHT=" +  map.getSize()[1] +
	'&format=' + "application/json" +
	"&styles=" + ""  +
	"&srs=" + "EPSG:900913"; 
	
	var url = createLink(resourceURL,'wms', parameters);
	
	AJAX_Call_GET(url, callback, context);
	
}



function addLayers(layerName, isClicked){
	if(isClicked){
		map.addLayer(fetchLayerByLayerName(layerName));
	}else{
		map.removeLayer(layersByName[layerName]);
		map.updateSize();
	}
}

function fetchLayerByLayerName(layerName){
	var layerAlreadyPlacedOnTheLeft = false;
	for(var j in layerNamesOnTheLeft){
		if(layerNamesOnTheLeft[j] === geoserverWorkspaceName + ":"  + layerName){
			layerAlreadyPlacedOnTheLeft = true;
			break;
		}
	}
	
	if(!layerAlreadyPlacedOnTheLeft){
		layerNamesOnTheLeft.push(geoserverWorkspaceName + ":" +layerName);//so that layers array won't be object in URLParameters()
		var index = layerNamesOnTheLeft.indexOf(geoserverWorkspaceName + ":" +layerName);
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

function fetchLayerByLayerNameModal(layerName){
	var layerAlreadyPlacedOnTheLeft = false;
	for(var j in layerNamesObjectModal){
		if(layerNamesObjectModal[j] === geoserverWorkspaceName + ":"  + layerName){
			layerAlreadyPlacedOnTheLeft = true;
			break;
		}
	}
	
	if(!layerAlreadyPlacedOnTheLeft){
		layerNamesObjectModal.push(geoserverWorkspaceName + ":" +layerName);//so that layers array won't be object in URLParameters()
		var index = layerNamesObjectModal.indexOf(geoserverWorkspaceName + ":" +layerName);
		var layer = new ol.layer.Tile({
	    	source: new ol.source.TileWMS({
	    		url : createLink(resourceURL,'wms'),
	    		params : paramsModal(index),
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
		
		layersByNameModal[layerName] = layer;
		return layer;
		
	}else{
		return layersByNameModal[layerName];
	}
}

function paramsModal(index){
	var layers = [];
	layers.push(layerNamesObjectModal[index]);
	var theBbox = layersMap.getView().calculateExtent(layersMap.getSize());
	var getMapObject = {};
	
	getMapObject={
			'BGCOLOR' : '0xcccccc',
			"Layers" : layers,
			"srs" : "EPSG:4326", 
	};
	
	return getMapObject;
}

function nodesToBeAppended(nodes){
	if(Array.isArray(nodes) && nodes.length > 0){
		var nodesToBeReturned = [];
		$.each(nodes, function(index, value){
			nodesToBeReturned.push({
				icon: false,
				children:true,
				id: nodes[index].taxonomyID,
            	state:{
            		disabled:false,
                	opened:false,
                	selected:false
            	},
            	text:nodes[index].taxonomyName,
            	type:"parent"
			});
		});
		return nodesToBeReturned;
	}else {
		return [];
	}
}