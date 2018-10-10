$.widget( "geoanalytics.aquacultureCostal", {
	// default options
	options: {
		btnClass : ' decisionSupportSystemFunctionButtons',
		inputClass : ' decisionSupportSystemFunctionInuts',
		divClass : ' decisionSupportSystemFunctionContainer',
		labelClass : ' decisionSupportSystemFunctionLabels',
		headerClass : ' decisionSupportSystemFunctionHeaders',
		dropdownClass : ' decisionSupportSystemFunctionDropdown',
		textInputClass : ' decisionSupportSystemFunctionTextInput',
		textInputLayerStyleClass : ' decisionSupportSystemFunctionTextInputLayerStyle',
		dropdownLayerClass : ' decisionSupportSystemFunctionLayerDropdown',
		dropdownStyleClass : ' decisionSupportSystemFunctionStyleDropdown',
		textForStyleDropdown : 'Layer style',
		dropdownOperationClass : ' decisionSupportSystemFunctionOperationDropdown',
		resultDivClass : ' decisionSupportSystemFunctionResult',
		collectDataClass : ' collectData',
		pluginId : null,
		descriptionCSSCLass : 'functionDescription',
		spinnerForLoadingStyles : null,
		sampleMetersDropdown : null,
		styleDropdown : null,
		resultingLayerNameElement : null,
		bboxValidationElement : null,
		arrayThatStoresValidationElements : []
	},

	facade : null,

	response: {},

	serverResponse : [],

	namesToClassesObject : {},

	classesToValuesObject : {},

	namesToValuesObject : {},

	layersPlaceHolder : null,

	objectIDToFacade : {},

	objectIDToElement : {},

	functionDescription : null,

	_generateValidationElement : function(text) {
		var $validationDiv = $('<div></div>', {
			class : 'row-fluid',
			css : {
				'color' : 'red',
				'text-align' : 'center'
			},
			text : text
		});

		this.options.arrayThatStoresValidationElements.push($validationDiv);

		return $validationDiv;
	},

	// The constructor
	_create: function(data) {
		this.serverResponse = data;

		this.bbox = [];

		var self = this;

		// jQuery element
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

		var $option7 = $(option, {
			text : '1000',
			value : '1000'
		});

		var $option8 = $(option, {
			text : '1500',
			value : '1500'
		});

		var $option9 = $(option, {
			text : '2000',
			value : '2000'
		});

		var $option10 = $(option, {
			text : '5000',
			value : '5000'
		});

		var $option11 = $(option, {
			text : '10000',
			value : '10000'
		});

		var $row3 = $(div, {
			class: opt.divClass + rf
		});

		var $label3 = $(label, {
			class: opt.labelClass + " span4",
			text: 'Produced Layer name:'
		});

		var $labelStyleDropdown = $(label, {
			class: opt.labelClass + " span4",
			text: 'Produced Layer style:'
		});

		var $styleDropdown = $(select, {
			class : opt.dropdownStyleClass
		});

		this.options.styleDropdown = $styleDropdown;

		var $option1StyleDropdown = $(option, {
			text : 'Choose a style',
			hidden: 'true',
			disabled:'',
			selected: ''
		});

		var $layerTextInput = $(input, {
			class: opt.textInputClass
		});

		this.options.resultingLayerNameElement = $layerTextInput;

		var $rowStyleDropdown = $(div, {
			class: opt.divClass + rf
		});

		$styleDropdown.append($option1StyleDropdown);

		var $spinnerForLoadingStyles = $('<i></i>', {
			class : 'fa fa-spinner fa-spin'
		});

		$rowStyleDropdown.append($labelStyleDropdown).append($styleDropdown).append($spinnerForLoadingStyles);
		$row3.append($label3).append($layerTextInput);

		this.options.spinnerForLoadingStyles = $spinnerForLoadingStyles;

		var $h4_3 = $(h4, {
			class: opt.headerClass,
			text: 'Function results'
		});

		var $resultDiv = $(div, {
			class: opt.resultDivClass + rf
		});

		$dropDown4.append($option2).append($option3).append($option4).append($option5).append($option6)
		.append($option7).append($option8).append($option9).append($option10).append($option11);

		this.options.sampleMetersDropdown = $dropDown4;

// .append($arOperations1).append($arOperations2);
		
		$row2.append($label2).append($dropDown4);

		$divBelowElement
		.append($h4).append($row1).append($h4_2)
		.append($row2).append($row3).append($rowStyleDropdown);
// .append($h4_3).append($resultDiv);

		this.element.append($divBelowElement);

	},

	_createElementsForLayers : function(data) {
		var count = 0;
		var label = '<label></label>';
		var select = '<select></select>';
		var option = '<option></option>';
		var div = '<div></div>';
		var opt = this.options;
		this.facade = data;

		for(var field in data) {

			if(data[field].objectID !== "0") continue;

			var $div = $(div, {
				class : 'row-fluid'
			});

			var $divForStyleDropdown = $(div, {
				class : 'row-fluid'
			});

			var $newLabel = $(label, {
				class: opt.labelClass + " span4",
				text: data[field].captionForUser + ':'
			});

			var $dropDown1 = $(select, {
				class: opt.dropdownClass + opt.dropdownLayerClass + opt.collectDataClass + count
			});

			this.namesToClassesObject[data[field].captionForUser] = opt.collectDataClass + count;

			this.classesToValuesObject[opt.collectDataClass + count] = "";

			this.namesToValuesObject[data[field].captionForUser] = "";

			var $option1 = $(option, {
				text : 'Choose a layer',
				hidden: 'true',
				disabled:'',
				selected: ''
			});

			$dropDown1.append($option1);
			
			$div.append($newLabel).append($dropDown1);

// this.layersPlaceHolder.append($newLabel);
// this.layersPlaceHolder.append($dropDown1);
			this.layersPlaceHolder.append($div);

			count++;

			this.objectIDToElement[data[field].objectID] = $dropDown1;
			this.objectIDToFacade[data[field].objectID] = data[field];
		}

	},

	// Called when created, and later when changing options
// _refresh: function() {
// this.element.css( "background-color", "rgb(" +
// this.options.red +"," +
// this.options.green + "," +
// this.options.blue + ")"
// );

// // Trigger a callback/event
// this._trigger( "change" );
// },

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

// this._refresh();
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

	setStyles : function(styleNames) {
		var $stylesDropdown = this.element.find('.' + this.options.dropdownStyleClass.trim());

		$.each(styleNames, function(index, value) {
			var $styleOption = $('<option></option>', {
				value : value,
				text : value
			});

			$stylesDropdown.append($styleOption);
		});

		this.hideSpinnerForLoadingStyles();
	},

	_execute: function() {},

	getBbox : function() {
		return this.bbox;
	},

	// ua pairnei to koumpi run function
	getParameters: function($bboxValidationElement) {

		if(this.options.bboxValidationElement === null)
			this.options.bboxValidationElement = $bboxValidationElement;

		var params = {};
		var layers = {};

		for(var className in this.classesToValuesObject){
			this.classesToValuesObject[className] = this.element.find('.' + className.trim()).val();

			for(var name in this.namesToClassesObject){
				if(className === this.namesToClassesObject[name]){
					this.namesToValuesObject[name] = this.classesToValuesObject[className]; 
				}
			}
		}

		var layersArray = [];

		for(var index in this.objectIDToElement) {
			this.objectIDToFacade[index].layerID = this.objectIDToElement[index].val();
			this.objectIDToFacade[index].layerDescription = this._getLayerDescription();

			layersArray.push(this.objectIDToFacade[index]);
		}

		params['layers'] = layersArray;// this.objectIDToFacade;

		var samplingMeters = this.element.find('.' + this.options.dropdownOperationClass.trim()).find('option:selected').val();
		params['samplingMeters'] = samplingMeters;

// var newLayerName = this.element.find('.' +
// this.options.textInputClass.trim()).val().trim();
		params['resultingLayerName'] = this._getResultingLayerName();

// var newLayerStyleID = this.element.find('.' +
// this.options.dropdownStyleClass.trim()).val().trim();
		params['resultingLayerStyleName'] = this._getResultingLayerStyleName();

		params['bbox'] = this.getBbox();

		var formValidation = this._validateForm(params);
		
		if(formValidation)
			return params;
		else return null;
	},

	_getCallback: function() {
		var callback = function(data) {
			alert(data.response);
		};

		return callback;
	},
	
	getCallback: function() {
		return null;
	},

	hideSpinnerForLoadingStyles : function() {
		this.options.spinnerForLoadingStyles.addClass('hidden');
	},

	_getResultingLayerName : function() {
		return this.element.find('.' + this.options.textInputClass.trim()).val().trim();
	},

	_getLayerDescription : function() {
		return this.element.find('.' + this.options.descriptionCSSCLass).text();
	},

	_getResultingLayerStyleName : function() {
		var resultingLayerStyleName = this.element.find('.' + this.options.dropdownStyleClass.trim()).val();
		
		if(resultingLayerStyleName !== null)
			return resultingLayerStyleName.trim();
		else
			return resultingLayerStyleName;
	},

	setExecutionResult: function(functionResult) {
		this.element.find('.' + this.options.resultDivClass.trim()).text(functionResult);
	},

	setRunInBackgroundControl : function(){

	},

	_validateBBOX : function(params) {
		var response = true;

		if(typeof params["bbox"] === "undefined" || !Array.isArray(params.bbox)) {
			response = false

// this._generateValidationElement('No bounding box
// found').insertAfter(this.sampleMetersDropdown);
			this.options.bboxValidationElement.text('No bounding box selected');
		}

		$.each(params.bbox, function(index, value) {
			if(typeof value !== 'number') {
// response = this._validationResponse(false, 'No bounding box found');
				response = false;

				this.options.bboxValidationElement.text('No bounding box selected');
			}
		});

		return response;
	},

	_validateStyleName : function(params) {
		var response = true;

		if(typeof params["resultingLayerStyleName"] === "undefined"
			|| typeof params["resultingLayerStyleName"] !== "string" || params["resultingLayerStyleName"] === null) {
// response = this._validationResponse(false, 'No style found');
			response = false;

			this._generateValidationElement('Invalid style').insertAfter(this.options.styleDropdown.parent());
		}

		return response;
	},

	_validateLayerName : function(params) {
		var response = true;

		if(typeof params["resultingLayerName"] === "undefined" || typeof params["resultingLayerName"] !== "string"
			|| params["resultingLayerName"] === null || params["resultingLayerName"] === '') {
// response = this._validationResponse(false, 'No layer name found');

			response = false;

			this._generateValidationElement('Invalid layer name').insertAfter(this.options.resultingLayerNameElement.parent());
		}

		return response;
	},

	_validateSamplingMeters : function(params) {
		var response = true;

		if(typeof params["samplingMeters"] === "undefined" || params["samplingMeters"] === null
			|| typeof parseInt(params["samplingMeters"]) !== "number") {
// response = this._validationResponse(false, 'No layer name found');

			this._generateValidationElement('Invalid value').insertAfter(this.options.sampleMetersDropdown.parent());

			reponse = false;
		}

		return response;
	},

	_validateLayers : function(params) {
		var response = true;
		
		for(var className in this.classesToValuesObject) {
			var $layerDropdown = this.element.find('.' + className.trim());
			
			if(typeof $layerDropdown.val() === "undefined" || $layerDropdown.val() === null || $layerDropdown.val() === '') {
				response = false;
				
				this._generateValidationElement('Invalid layer value').insertAfter($layerDropdown.parent());
			}
		}

		return response;
	},

	_validateForm : function(params) {
		$.each(this.options.arrayThatStoresValidationElements, function(index, value) {
			$(this).remove();
		});
		
		this.options.arrayThatStoresValidationElements = [];

		var formValidationReponse = true;

		var bboxValidation = this._validateBBOX(params);

		var styleNameValidation = this._validateStyleName(params);

		var layerNameValidation = this._validateLayerName(params);

		var samplingMetersValidation = this._validateSamplingMeters(params);

		var layersValidation = this._validateLayers(params);
		
		formValidationReponse = bboxValidation && styleNameValidation && layerNameValidation && samplingMetersValidation && layersValidation;

		return formValidationReponse;
	}
});