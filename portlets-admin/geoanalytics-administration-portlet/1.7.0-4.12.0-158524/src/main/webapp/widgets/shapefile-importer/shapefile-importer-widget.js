$.widget('cite.shapefileImporter', {	
	isActive : false,
	notificator : $("#shapefile-importer-notificator"),
	options:{
		mode: "div"	,
		importShapefileURL: "",
		stylesURL: "",
		notificator: null,
		headerDiv: {},
		content: {}
	},
	createImporter : function() {
		if (this.isActive) {
			this.destroy();
		}
		this._createUI();
		this._initializeFunctionality();
		this.isActive = true;
	},
	_createShowButton : function() {
		var shapefileimporter = $('#shapefileimporter');	
		$(shapefileimporter).css("display", "inline-block");

		var showButton = 	'<button id="shapefile-importer-show-button"  ' +
									'type="button" '+
									'class="btn btn-large" '+
									'data-toggle="modal" '+
									'data-target="#shapefile-importer-container">	'	+								
								'<i class="fa fa-upload" ></i> Shapefile Importer	'+							 
							'</button>';
		shapefileimporter.append($(showButton));
	},	
	_createNonModal : function() {
		var container = '<div id="shapefile-importer-container"> ';
		this._container = $(container);		
		this._container.appendTo(this.options.content);
	},	
	_createModal : function() {	
		var container = '<div id="shapefile-importer-container" ' +
							'class="modal fade in shapefile-importer-container-button"   ' +
							'tabindex="-1" ' +
							'role="dialog"	' +
							'aria-labelledby="shapefileimporter"  ' +
							'aria-hidden="true " ' +
							'style="display:none;"> ';			
		this._container = $(container);		
		this._container.appendTo(this.element);
	},
	_createUI : function() {
		var mode = this.options.mode;		
		
		switch (mode){
			case "button":
				this._createShowButton();
				this._createModal();
				break;			
			case "div" :
				this._createNonModal();		
				break;		
		}
		
		var closeButton = "";
		var header = "" ;
		var body = "";
		var scrollableRow = "";
		
		if(this.options.mode === "button"){
			closeButton = '<button id="shapefile-importer-close-button" type="button" data-dismiss="modal" aria-hidden="true"></button>';
			header = '<div id="shapefile-importer-modal-header" class="modal-header">' +
				closeButton +
				'<h5 id="shapefile-importer-label-modal">Shapefile Importer</h5> ' +
			'</div>';	
			body = '<div id="shapefile-importer-modal-body" class="modal-body scrollable" >';
			scrollableRow = " scrollable";
		}else{
			body = '<div id="shapefile-importer-modal-body" >';
		}

		var importerBody = header + body + 	'<input type="button" id="shapefile-importer-toggle-geonetwork-metadata" />' +
		'<div class="row">' +
			'<div class="span6" id="shapefile-importer-general-form-container">' +
				'<div class="spinner" style="display: none"></div>' +
				'<span class="headerDescription">GENERAL</span>	' +
				'<div id="shapefile-importer-notificator"></div>	' +													
				'<hr>	' +
				'<form class="form-horizontal" id="shapefile-importer-general-form">	' +
					'<div class="control-group row">		' +		
						'<div class="span4">' +
							'<label>Data Location<span class="makeMeOrange">*</span></label>' +
						'</div>		' +
																					
						'<div class="span6">		' +													
							'<input id="shapefile-importer-selected-file" class="span11"  type="text" placeholder="No shapefile selected with zip extension" readonly>' + 	
							'<br>' +
							'<span class="help-inline"></span>' +
						'</div>				' +			
						'<div class="controls span2" id="shapefile-importer-patch-browsefiles">	' +														
							'<button class="span12">Browse</button>' +
							'<input id="shapefile-importer-browsefiles-button" name="browseFiles" type="file" >	' +
						'</div>	' +
					'</div>		' +			
					'<div class="control-group row">' +
						'<div class="span4">' +
							'<label for="shapefile-importer-layername">Layer Name<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea  style="resize: none;" id="shapefile-importer-layername"  class="span12"  name="layerName"  placeholder="Please fill in your Layer Name" rows="1"></textarea>' +
							'<span class="help-inline"></span>' +
						'</div>' +
					'</div>' +	
					'<div class="control-group row">' +
						'<div class="span4">' +
							'<label for=""shapefile-importer-style"">Style<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<select id="shapefile-importer-style" class="span12"  name="style">' +
								'<option  value="" disabled selected>Choose a Style</option>' +
							'</select>' +
							'<span class="help-inline"></span>' +
						'</div>' +
					'</div>	' +
					
					'<div class="control-group row">' +
						'<div class="span4">' +
							'<label for=""shapefile-importer-template-layer"">Is Template Layer</label>' +
						'</div>' +
						'<div class="span8">			' +			
							'<input type="checkbox" id="shapefile-importer-template-layer">' +
							'<label for="shapefile-importer-template-layer"></label>' +
						'</div>' +
					'</div>' +		
					
					'<div class="control-group row" style="display:none; margin-top:20px;">' +
						'<div class="span4">' +
							'<label for="shapefile-importer-template-layer" style="padding-left:20px;">' +
								'Template Layer Geocode System<span class="makeMeOrange">*</span>' +
							'</label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea style="resize: none;" ' +
								'id="shapefile-importer-template-layer-geocode-system" ' +
								'class="span12" ' +
								'rows="1" ' +
								'placeholder="Please give the name of the Geocode System" 	' +						
								'name="geocodeSystem"></textarea>' +
							'<span class="help-inline"></span>' +
						'</div>' +
					'</div>' +
					
					'<div class="control-group row" style="display:none;">'	+
						'<div class="span4">'	+
							'<label for="shapefile-importer-template-layer" style="padding-left:20px;">'	+
								'Template Layer Geocode Mapping<span class="makeMeOrange">*</span>'	+
							'</label>'	+
						'</div>'	+
						'<div class="span8">'	+
							'<textarea style="resize: none;" '	+
								'id="shapefile-importer-template-layer-geocode-mapping" '	+
								'class="span12" '	+
								'rows="2" '	+
								'placeholder="Please give the shapefile attribute containing the Geocode" '	+							
								'name="geocodeMapping"></textarea>'	+
							'<span class="help-inline"></span>'	+
						'</div>'	+
					'</div>'	+	
					
					'<div class="control-group row" id="shapefile-importer-general-description">' +
						'<div class="span4">' +
							'<label for="shapefile-importer-abstract">Layer description<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea name="abstractDescription" id="shapefile-importer-abstract" class="span12" placeholder="Please give a brief description of the layer" rows="5"></textarea>' +
						'</div>' +
					'</div>' +
									
					'<div class="control-group row" id="generalKeywords">' +
						'<div class="span4">' +
							'<label for="shapefile-importer-tagsinput">Layer keywords<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea  style="resize: none;" id="shapefile-importer-tagsinput" name="tagsInput" class="span12" placeholder="Please fill in the layer keywords" rows="1"></textarea>' +
						'</div>' +
					'</div>' +
				'</form>' +
			'</div>	' +
					
			'<div class="span6">' +
				'<div id="shapefile-importer-geonetwork-metadata"></div>' +
			'</div>' +
		'</div>'	;
		
		this._container.append($(importerBody));

		
		this._container.append('<div class="modal-footer">' +
									'<div id="shapefile-importer-div-submit" class="control-group pull-right">' +
										'<button type="button" data-dismiss="modal" id="shapefile-importer-cancel" class="btn" aria-hidden="true">Cancel</button>' +
										'<button type="button" id="shapefile-importer-import-button"  class="btn" >Import</button>' +
									'</div>' +				
								'</div>');	
	},
	_initializeFunctionality : function (){		
		var importShapefileURL = this.options.importShapefileURL;
		var stylesURL = this.options.stylesURL;
		var notificator = this.options.notificator;
		var toggleGeoNetworkMetadata = $("#shapefile-importer-toggle-geonetwork-metadata");
		var geoNetworkModule = window.config.geoNetworkModule.createInstance("#shapefile-importer-geonetwork-metadata");
		geoNetworkModule.createGeoNetworkForm();
		
		$(document).ready(function(){
			$('#shapefile-importer-tagsinput').tagsInput({
			   'defaultText':'Add a Tag',
			   'delimiter': [',',';',' '],   
			   'minChars' : 2,
			   'maxChars' : 40,
			   'placeholderColor' : 'rgba(153, 153, 153, 0.65)'
			});	
			
			$('#shapefile-importer-browsefiles-button').bind("change", function() {
				var fileName = $(this).val().split('\\').pop();
				$('#shapefile-importer-selected-file').val(fileName);
				var valid = $('#shapefile-importer-browsefiles-button').valid();
				
				if(valid){
					fileName += " has been selected";
					window.noty.showNoty($("#shapefile-importer-notificator"), fileName, "success", false);
				}else{
					window.noty.closeAllNotys();
				}		
			});
			
		    $.validator.addMethod("validRegex", function(value, element) {
		        return this.optional(element) || /^[a-zA-Z][a-zA-Z0-9\_\s]+$/i.test(value);
		    }, "Field must start with a letter and contain only letters, numbers, or underscores.");		    
			
			$('#shapefile-importer-general-form').validate({
				rules: {
					layerName: {
						minlength: 2,
					    required : true
					},
					style:{
						required: true
					},
					abstractDescription: {
						required: true
					},
					browseFiles:{
						extension: "zip",
						required: true
					},
					geocodeSystem:{
						required: true
					},
					geocodeMapping:{
						required: true
					}
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('shapefile-importer-color-red');
					
					if($(element).attr('id') === 'shapefile-importer-browsefiles-button'){
						$(element).closest('.control-group').find('#shapefile-importer-patch-browsefiles').css("border-color","red");		
					}					
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					if($(element).attr('id') === 'shapefile-importer-browsefiles-button'){
						$(element).closest('.control-group').find('#shapefile-importer-patch-browsefiles').css("border-color","#DDD");		
					}
					label.remove();
				},		
				errorPlacement: function(error, element) {	
					if(element.attr('id') === 'shapefile-importer-browsefiles-button'){
						error.appendTo(element.closest('.control-group').find('.help-inline'));	
						element.closest('.control-group').find('#shapefile-importer-patch-browsefiles').addClass('error');			
					}else{
						error.appendTo($(element).siblings('.help-inline'));
					}
				}
			});	
			
			jQuery.extend(jQuery.validator.messages, {
				extension: "Please upload a file with .zip extension."
			});
			
			$("#shapefile-importer-template-layer").change(function() {
				$("#shapefile-importer-template-layer-geocode-system").closest(".control-group").toggle();
				$("#shapefile-importer-template-layer-geocode-mapping").closest(".control-group").toggle();
			});			
		});		
		
		toggleGeoNetworkMetadata.styledCheckbox({
			text : "Publish Layer on GeoNetwork",
			initiallyChecked : true
		});
		
		toggleGeoNetworkMetadata.on("click", function() {
			$(this).styledCheckbox("isChecked") ? geoNetworkModule.enableGeoNetworkForm() : geoNetworkModule.disableGeoNetworkForm();			
		});
		
		// Clear contents with cancel
		
		$(document).ready(function() {
			$('#shapefile-importer-cancel , #shapefile-importer-close-button').on('click',function (e){ 
				$('#shapefile-importer-general-form')[0].reset();
				geoNetworkModule.resetGeoNetworkForm();
				
				$("#shapefile-importer-container").find("*").removeClass("shapefile-importer-color-red error success");
				$('.help-inline > label').remove();
				
				window.noty.closeAllNotys();
				
				$('#shapefile-importer-patch-browsefiles').css("border-color", "gray");
				$('#shapefile-importer-tagsinput_tagsinput').find(".tag").remove();
			});
		});	
				
		$(document).ready(function() {			
			$.ajax({
				url: stylesURL,
				type: 'GET',
				cache : false,
				dataType: 'json',
				success: function(response) {
					$.each(response, function(i,v){
						var $option = $('<option></option>', {
							text : v,
							value : i
						});
						$('#shapefile-importer-style').append($option);
					});
				},
				error : function(jqXHR, exception) {
					window.noty.errorHandlingNoty(notificator, jqXHR, exception);
				}
			});	
			
			// Load Template Layers
			
			var notificator = $("#shapefile-importer-notificator");			
			
			$('#shapefile-importer-import-button').on('click',function (e){		
				var validData = $('#shapefile-importer-general-form').valid();
				var validMetadata = geoNetworkModule.validateForm();
				var publishOnGeoNetwork = toggleGeoNetworkMetadata.styledCheckbox("isChecked");				
				
				if(validData && ((publishOnGeoNetwork && validMetadata) || !publishOnGeoNetwork)){				
					var spans = $('#shapefile-importer-container .tagsinput').find(".tag > span");
					var tags = [];
					for(var i=0; i < spans.length; i++){
						spans[i] = spans[i].innerHTML.split("&nbsp;").join("");
						tags.push(spans[i]);
					}
					
					var file = document.getElementById('shapefile-importer-browsefiles-button').files[0];					

					var importFormData = new FormData();
					importFormData.append("shapefileImportFile", file);					
					importFormData.append("shapefileImportProperties", new Object([JSON.stringify({
						newLayerName	: 	$('#shapefile-importer-layername').val(),
						geocodeSystem	:	$("#shapefile-importer-template-layer-geocode-system").val(),
						geocodeMapping	:	$("#shapefile-importer-template-layer-geocode-mapping").val(),
						description		: 	$('#shapefile-importer-abstract').val().trim(),

						isTemplate		:	$("#shapefile-importer-template-layer").is(":checked"),
						style			:   $('#shapefile-importer-style option:selected').text(),
						tags			: 	tags,

					})], {
						type: "application/json"
					}));
					
					if(publishOnGeoNetwork){
						importFormData.append("shapefileImportMetadata", new Object([JSON.stringify({
							title			:	$('#shapefile-importer-layername').val(),
							description		: 	$('#shapefile-importer-abstract').val().trim(),
							purpose			:	$('#shapefile-importer-purpose').val(),
							keywords		: 	tags,
							
							purpose			:	geoNetworkModule.getPurpose(),
							limitation		:	geoNetworkModule.getLimitation(),
			
							author 			: 	geoNetworkModule.getAuthorMetadata(),
							distributor 	: 	geoNetworkModule.getDistributorMetadata(),
							provider 		: 	geoNetworkModule.getProviderMetadata() 
						})], {
							type: "application/json"
						}));	
					}
					
 					$.ajax({
 					    type : "POST", 					    
 					    url : importShapefileURL, 					    
 					    processData : false, 					   
 		                contentType : false, 
 					    data: importFormData, 	
 					    beforeSend : function() {
 					    	$('#shapefile-importer-container .spinner').show();
 					    },
 					    success: function(data){
 							window.noty.showNoty(notificator, data, "success");	
 					    }, 					    
 					    error: function(jqXHR, exception){ 	
 					    	window.noty.errorHandlingNoty(notificator, jqXHR, exception);
					    },
					    complete: function() {
					    	$('#shapefile-importer-container .spinner').hide();
					    }
 					});				    
				}				
			}); 			 
		});
	},
	destroy: function()	{
		$(this.options.content).children().remove();
	}
});