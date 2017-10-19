	$.widget( "geoanalytics.seaBassIrrAnalysis", {
		// default options
		options: {
			
		},
		
		// The constructor
		_create: function(data) {
			this.serverResponse = data;
			
			this.bbox = [];
			
			var self = this;
			
			var template = '<div>' +
								'Sea Bass IRR Analysis' +
							'</div>';

			this.container = $(template);
			
//			this.element.append(this.container);
//			
//			//Binding
//			this.container.click(function(){
//				self.showModal();
//			});
		},
		
		showModal: function() {
			var self = this;
			
			if (this.modal === undefined || this.modal == null) {
				this.createModal();
			}
			this.modal.modal('show');
		},
		
		createModal: function() {
			var self = this;
			
			var ckbId = this.getUniqueControlId();
			
			var template = 
				'<div id="seaBassIrrAnalysisModal" class="modal fade in" hidden="hidden" tabindex="-1" role="dialog" aria-hidden="false" style="display: block; width: 50%; margin-left: 0; left: 25%;">' + 
					'<div class="modal-header">' + 
						'<div id="blueLineBottom">' + 
							'<span class="modalHeadermanipulateGroup">Analysis Options</span>' + 
							'<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>' + 
						'</div>' + 
					'</div>' + 
					'<div class="modal-body">' + 
						'<h4>Site Selection Criteria</h4>' +
						
						/* Costal Areas */
						'<div class="row">' + 
							'<h5 class="span12">Costal Areas</h5>' +
						'</div>' +
						'<div class="row">' + 
							'<div class="span12">Please select the base layer on which the analysis will be performed</div>' +
						'</div>' +
						'<div class="row">' + 
							'<div class="span2">Layer</div>' +
							'<div class="span4">' +
								'<select data-element="base-layer-selector">' +
									'<option value="0">Test Layer</option>' +
									'<option value="1">Test Layer 2</option>' +
								'</select>' +
							'</div>' +
						'</div>' +
						
						/* Marine Protected Areas */
						'<div class="row">' +
							'<h5 class="span12">Marine Protected Areas</h5>' +
						'</div>' +
						'<div class="row">' + 
							'<div class="span12">Please select a layer containing marine protected areas in order to exclude it from the analysis</div>' +
						'</div>' +
						'<div class="row">' +
							'<div class="span2">Layer</div>' +
							'<div class="span4">' +
								'<select data-element="protected-layer-selector">' +
									'<option value="0">Test Layer</option>' +
									'<option value="1">Test Layer 2</option>' +
								'</select>' +
							'</div>' +
						'</div>' +
						
						/* Currents Velocity */
						'<div class="row">' +
							'<h5 class="span12">Currents Velocity</h5>' +
						'</div>' +
						'<div class="row">' + 
							'<div class="span12">Please select a layer containing the sea currents velocity as well as a maximum acceptable velocity value</div>' +
						'</div>' +
						'<div class="row">' +
							'<div class="span2">Layer</div>' +
							'<div class="span4">' +
								'<select data-element="currents-layer-selector">' +
									'<option value="0">Test Layer</option>' +
									'<option value="1">Test Layer 2</option>' +
								'</select>' +
							'</div>' +
							'<div class="span2">Max Speed (km/s)</div>' +
							'<div class="span4">' +
								'<input data-element="currents-max-editor" type="text" value="2"></input>' +
							'</div>' +
						'</div>' +
						 
						/* Tourist attractions and beaches */
						'<div class="row">' +
							'<h5 class="span12">Tourist attractions and beaches</h5>' +
						'</div>' +
						'<div class="row">' + 
							'<div class="span12">Please select a layer containing tourist attractions and beaches and set the minimum acceptable distance</div>' +
						'</div>' +
						'<div class="row">' +
							'<div class="span2">Layer</div>' +
							'<div class="span4">' +
								'<select data-element="attractions-layer-selector">' +
									'<option value="0">Test Layer</option>' +
									'<option value="1">Test Layer 2</option>' +
								'</select>' +
							'</div>' +
							'<div class="span2">Distance (kms)</div>' +
							'<div class="span4">' +
								'<input data-element="attractions-max-editor" type="text" value="20"></input>' +
							'</div>' +
						'</div>' +
						
						/* References */
						'<div class="row">' + 
							'<h5 class="span12">References</h5>' +
						'</div>' +
						'<div class="row">' + 
							'<div class="span12">Additional information regarding site selection can be here <a href="http://www.fao.org/3/a-i6834e.pdf" target="_blank">Aquaculture zoning, site selection and area management under the ecosystem approach to aquaculture - A handbook</a></div>' +
						'</div>' +
					'</div>' +
					'<div class="modal-footer">' + 
						'<button data-element="run-button" class="btn btn-link btn-large cancelBtns" data-dismiss="modal" aria-hidden="true">Run</button>' + 
					'</div>' + 
				'</div>';
			
			this.modal = $(template);
			
			this.element.append(this.modal);
			
			this.baseLayerSelector = this.modal.find('[data-element="base-layer-selector"]');
			this.protectedLayerSelector = this.modal.find('[data-element="protected-layer-selector"]');
			this.currentsLayerSelector = this.modal.find('[data-element="currents-layer-selector"]');
			this.currentsMaxEditor = this.modal.find('[data-element="currents-max-editor"]');
			this.attractionsLayerSelector = this.modal.find('[data-element="attractions-layer-selector"]');
			this.attractionsMaxEditor = this.modal.find('[data-element="attractions-max-editor"]');
			this.runButton = this.modal.find('[data-element="run-button"]');
			
			this.runButton.click(function(){
				self.runFunction();
				
				self.animationCallback();
			});
		},
		
		getBbox : function() {
			return this.bbox;
		},
		
		animationCallback : null,
		
		BboxCallback : null,
		
		getBboxFromCallback : function() {
			if(typeof this.BboxCallback === 'function')
				return this.BboxCallback();
			else return null;
		},
		
		setBBOX : function(bboxArray){
			this.bbox = bboxArray;
		},
		
		setLayers :function(layersValues, data) {
			console.log('Setting layers');
			console.log(layersValues);
			console.log(data);
		},
		
		runFunction: function() {
			console.log(this.getBboxFromCallback());
			console.log(this.getParameters());
		},
		
		//ua pairnei to koumpi run function
		getParameters: function() {
			var params = {};
			
			params['base'] = this.baseLayerSelector.val();
			params['protected'] = this.protectedLayerSelector.val();
			params['currents'] = this.currentsLayerSelector.val();
			params['currentsMax'] = this.currentsMaxEditor.val();
			params['attractions'] = this.attractionsLayerSelector.val();
			params['attractionsMax'] = this.attractionsMaxEditor.val();
			
//			var operationDropdown = this.element.find('.' + this.options.dropdownOperationClass.trim()).find('option:selected').val();
//			params['samplingMeters'] = operationDropdown;
//			
//			var newLayerName = this.element.find('.' + this.options.textInputClass.trim()).val().trim();
//			params['resultingLayerName'] = newLayerName;
			
			params['bbox'] = this.getBbox();
			
			return params;
		},
		
		_getCallback: function() {
			var callback = function(data) {
				alert(data.response);
			};
			
			return callback;
		},
		
		getCallback : function() {
			var self = this;
			
			return function(BboxCallback, animationCallback) {
				self.showModal();
				
				self.BboxCallback = BboxCallback;
				
				self.animationCallback = animationCallback;
			};
		},
		
		_getLayerDescription : function() {
			return this.element.find('.' + this.options.descriptionCSSCLass).text();
		},
		
		setExecutionResult: function(functionResult) {
			this.element.find('.' + this.options.resultDivClass.trim()).text(functionResult);
		},
		
		setRunInBackgroundControl : function(){
			
		},
		
		getUniqueControlId: function () {
			if(!window.gfUniqueControlId) {
				window.gfUniqueControlId = 1;
			}
			return 'gf' + window.gfUniqueControlId++;
		},

		setStyles : function(styleNames) {
			console.log("setStyles functions needs to be implemented");
			return;
			
			$.each(styleNames, function(index, value) {
				var $styleOption = $('<option></option>', {
					value : value,
					text : value
				});

				$stylesDropdown.append($styleOption);
			});
		},
	});