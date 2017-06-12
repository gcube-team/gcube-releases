//$( function() {
	// the widget definition, where "custom" is the namespace,
	// "colorize" the widget name
	$.widget( "geoanalytics.analyticsFunctionTest", {
		// default options
		options: {
			btnClass : ' decisionSupportSystemFunctionButtons',
			inputClass : ' decisionSupportSystemFunctionInuts',
			divClass : ' decisionSupportSystemFunctionContainer',
			labelClass : ' decisionSupportSystemFunctionLabels',
			headerClass : ' decisionSupportSystemFunctionHeaders',
			dropdownClass : ' decisionSupportSystemFunctionDropdown',
			textInputClass : ' decisionSupportSystemFunctionTextInput',
			dropdownLayerClass : ' decisionSupportSystemFunctionLayerDropdown',
			dropdownOperationClass : ' decisionSupportSystemFunctionOperationDropdown',
			resultDivClass : ' decisionSupportSystemFunctionResult',
			collectDataClass : ' collectData',
			pluginId : null
		},
		
		response: {},
		
		serverResponse : [],
		
		namesToClassesObject : {},
		
		classesToValuesObject : {},
		
		namesToValuesObject : {},
		
		layersPlaceHolder : null,
		
		// The constructor
		_create: function(data) {
			this.serverResponse = data;
			
			this.bbox = [];
			
			var self = this;
			
			//jQuery element
			this.element
			// add a class for theming
			.addClass( "widgetLoaded" );
			
			var div = '<div></div>';
			var label = '<label></label>';
			var h4 = '<h4></h4>';
			var select = '<select></select>';
			var option = '<option></option>';
			var input = '<input></input>';
			
			var rf = " row-fluid";
			var opt = this.options;
			
			var $divBelowElement = $(div, {
				class: opt.divClass + rf
			});
			
			var $h4 = $(h4, {
				class : opt.headerClass,
				text: 'Select the layers the function will execute upon'
			});
			
			var $row1 = $(div, {
				class: opt.divClass + rf
			});
			
			this.layersPlaceHolder = $row1;

			var $label1_2 = $(label, {
				class: opt.labelClass + " span4",
				text: 'Layer 2:'
			});
			
//			var $dropDown2 = $dropDown1.clone();
//			
//			var $dropDown3 = $dropDown1.clone();
			
//			$row1.append($label1_1).append($dropDown1).append($label1_2).append($dropDown2).append($dropDown3);
			
			var $h4_2 = $(h4, {
				class : opt.headerClass,
				text: 'Select sampling meters distance'
			});

			var $row2 = $(div, {
				class: opt.divClass + rf
			});
			
			var $label2 = $(label, {
				class: opt.labelClass + " span4",
				text: 'Sampling in meters:'
			});
			
			var $dropDown4 = $(select, {
				class: opt.dropdownClass + opt.dropdownOperationClass
			});
			
			var $option2 = $(option, {
				text : 'Choose a distance',
				hidden: 'true',
				disabled:'',
				selected: ''
			});
			
			var $option3 = $(option, {
				text : '100',
				value : '100'
			});
			
			var $option4 = $(option, {
				text : '200',
				value : '200'
			});
			
			var $option5 = $(option, {
				text : '400',
				value : '400'
			});
			
			var $option6 = $(option, {
				text : '500',
				value : '500',
				selected: 'true'
			});

			var $row3 = $(div, {
				class: opt.divClass + rf
			});
			
			var $label3 = $(label, {
				class: opt.labelClass + " span4",
				text: 'Produced Layer name:'
			});
			
			var $layerTextInput = $(input, {
				class: opt.textInputClass
			});
			
			$row3.append($label3).append($layerTextInput);
			
			var $h4_3 = $(h4, {
				class: opt.headerClass,
				text: 'Function results'
			});
			
			var $resultDiv = $(div, {
				class: opt.resultDivClass + rf
			});
			
			$dropDown4.append($option2).append($option3).append($option4).append($option5).append($option6);
			
//			.append($arOperations1).append($arOperations2);
			$row2.append($label2).append($dropDown4);
			
			$divBelowElement
				.append($h4).append($row1).append($h4_2)
				.append($row2).append($row3);
//				.append($h4_3).append($resultDiv);
			
			this.element.append($divBelowElement);
			
		},
		
		_createElementsForLayers : function(data) {
			var count = 0;
			var label = '<label></label>';
			var select = '<select></select>';
			var option = '<option></option>';
			var opt = this.options;
			
			for(var field in data) {
				var $newLabel = $(label, {
					class: opt.labelClass + " span4",
					text: data[field] + ':'
				});
				
				var $dropDown1 = $(select, {
					class: opt.dropdownClass + opt.dropdownLayerClass + opt.collectDataClass + count
				});
				
				this.namesToClassesObject[data[field]] = opt.collectDataClass + count;
				
				this.classesToValuesObject[opt.collectDataClass + count] = "";
				
				this.namesToValuesObject[data[field]] = "";
				
				var $option1 = $(option, {
					text : 'Choose a Layer',
					hidden: 'true',
					disabled:'',
					selected: ''
				});
				
				$dropDown1.append($option1);
				
				this.layersPlaceHolder.append($newLabel);
				this.layersPlaceHolder.append($dropDown1);
				
				count++;
			}
			
		},

		// Called when created, and later when changing options
//		_refresh: function() {
//			this.element.css( "background-color", "rgb(" +
//					this.options.red +"," +
//					this.options.green + "," +
//					this.options.blue + ")"
//			);
//
//			// Trigger a callback/event
//			this._trigger( "change" );
//		},

		// Events bound via _on are removed automatically
		// revert other modifications here
		_destroy: function() {
			// remove generated elements
			this.changer.remove();

			this.element
			.removeClass( "custom-colorize" )
			.enableSelection()
			.css( "background-color", "transparent" );

		},

		// _setOptions is called with a hash of all options that are changing
		// always refresh when changing options
		_setOptions: function() {
			// _super and _superApply handle keeping the right this-context
			this._superApply( arguments );

//			this._refresh();
		},

		// _setOption is called for each individual option that is changing
		_setOption: function( key, value ) {
			// prevent invalid color values
			if ( /red|green|blue/.test(key) && (value < 0 || value > 255) ) {
				return;
			}
			this._super( key, value );

		},
		
		_setValuesForFunctionLayersDropdown: function(layersValues){
			
			var option = '<option></option>';
			
			var $layersDropdown = this.element.find("." + this.options.dropdownLayerClass.trim());
			
			$.each(layersValues, function(index, value){
				var $option = $(option, {
					text : value.name,
					value : value.id
				});
				
				$layersDropdown.append($option);
			});

		},
		
		setBBOX : function(bboxArray){
			this.bbox = bboxArray;
		},
		
		setLayers :function(layersValues, data) {
			this._createElementsForLayers(data);
			this._setValuesForFunctionLayersDropdown(layersValues);

		},
		
		_execute: function() {
//			var data = this._getParameters();
//			var callback = this._getCallback();
//			
//			pluginsCallBackendDSSUtilityFunction(data, callback);
		},
		
		getBbox : function() {
			return this.bbox;
		},
		
		//ua pairnei to koumpi run function
		getParameters: function() {
			var params = {};
			var layers = {};
			
//			var layerDropDowns = this.element.find('.' + this.options.dropdownLayerClass.trim());
//			$.each(layerDropDowns, function(index, value) {
////				params['layerName' + index.toString()] = $(value).find('option:selected').val();
//				layers["layer"+index] = $(value).find('option:selected').val();
//			});
			
			for(var className in this.classesToValuesObject){
				this.classesToValuesObject[className] = this.element.find('.' + className.trim()).val();
				
				for(var name in this.namesToClassesObject){
					if(className === this.namesToClassesObject[name]){
						this.namesToValuesObject[name] = this.classesToValuesObject[className]; 
					}
				}
			}
			
			params['layers'] = this.namesToValuesObject;
			
			var operationDropdown = this.element.find('.' + this.options.dropdownOperationClass.trim()).find('option:selected').val();
			params['samplingMeters'] = operationDropdown;
			
			var newLayerName = this.element.find('.' + this.options.textInputClass.trim()).val().trim();
			params['resultingLayerName'] = newLayerName;
			
			params['bbox'] = this.getBbox();
			

			
			return params;
		},
		
		_getCallback: function() {
			var callback = function(data) {
				alert(data.response);
			};
			
			return callback;
		},
		
		setExecutionResult: function(functionResult) {
			this.element.find('.' + this.options.resultDivClass.trim()).text(functionResult);
		}
	});
//});