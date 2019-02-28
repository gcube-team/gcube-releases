$.widget('cite.geotiffImporter', {	
	notificator : $("#geotiff-importer-notificator"),
	options:{
		mode: "div"	,
		importGeotiffURL: "",
		stylesURL: "",
		notificator: null,
		headerDiv: {},
		content: {}
	},
	createImporter : function() {
		this.destroy();		
		this._createUI();
		this._initializeFunctionality();
	},
	_createShowButton : function() {
		var geotiffimporter = $('#geotiffimporter');	
		$(geotiffimporter).css("display", "inline-block");

		var showButton = 	'<button id="geotiff-importer-show-button"  '+
										'type="button"   '+
										'class="btn btn-large"   '+
										'data-toggle="modal"   '+
										'data-target="#geotiff-importer-container">	  '+									
								'<i class="fa fa-upload" ></i> geotiff Importer	  '+							 
							'</button>  ';
		showButton = $(showButton);
		geotiffimporter.append(showButton);
	},	
	_createNonModal : function() {
		var container = `<div id="geotiff-importer-container"> `;
		this._container = $(container);		
		this._container.appendTo(this.options.content);
	},	
	_createModal : function() {	
		var container = '<div id="geotiff-importer-container" ' +
							'class="modal fade in geotiff-importer-container-button"' +
							'tabindex="-1" '+
							'role="dialog"	'+
							'aria-labelledby="geotiffimporter"  '+
							'aria-hidden="true " '+
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
			closeButton = '<button id="geotiff-importer-close-button" type="button" data-dismiss="modal" aria-hidden="true"></button>';
			header = '<div id="geotiff-importer-modal-header" class="modal-header">' +
				closeButton +
				'<h5 id="geotiff-importer-label-modal">geotiff Importer</h5>' +
			'</div>';	
			body = '<div id="geotiff-importer-modal-body" class="modal-body scrollable" >';
			scrollableRow = " scrollable";
		}else{
			body = '<div id="geotiff-importer-modal-body" >';
		}

		var importerBody = header + body + 	'<input type="button" id="geotiff-importer-toggle-geonetwork-metadata" />' +
		'<div class="row">' +
			'<div class="span6" id="geotiff-importer-general-form-container">' +
				'<div class="spinner" style="display: none"></div>' +
				'<span class="headerDescription">GENERAL</span>	' +
				'<div id="geotiff-importer-notificator"></div>	' +													
				'<hr>	' +
				'<form class="form-horizontal" id="geotiff-importer-general-form">	' +
					'<div class="control-group row">		' +		
						'<div class="span4">' +
							'<label>Data Location<span class="makeMeOrange">*</span></label>' +
						'</div>		' +
																					
						'<div class="span6">		' +													
							'<input id="geotiff-importer-selected-file" class="span11"  type="text" placeholder="No file selected" readonly> 	' +
							'<br>' +
							'<span class="help-inline"></span>' +
						'</div>				' +			
						'<div class="controls span2" id="geotiff-importer-patch-browsefiles">	' +														
							'<button class="span12">Browse</button>' +
							'<input id="geotiff-importer-browsefiles-button" name="browseFiles" type="file" >	' +
						'</div>	' +
					'</div>		' +			
					'<div class="control-group row">' +
						'<div class="span4">' +
							'<label for="geotiff-importer-layername">Layer Name<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea  style="resize: none;" id="geotiff-importer-layername"  class="span12"  name="layerName"  placeholder="Please fill in your Layer Name" rows="1"></textarea>' +
							'<span class="help-inline"></span>' +
						'</div>' +
					'</div>' +	
					'<div class="control-group row">' +
						'<div class="span4">' +
							'<label for=""geotiff-importer-style"">Style<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<select id="geotiff-importer-style" class="span12"  name="style" disabled>' +
								'<option  value="raster" selected>raster</option>' +
							'</select>' +
							'<span class="help-inline"></span>' +
						'</div>' +
					'</div>	' +
										
					'<div class="control-group row" id="geotiff-importer-general-description">' +
						'<div class="span4">' +
							'<label for="geotiff-importer-abstract">Layer description<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea name="abstractDescription" id="geotiff-importer-abstract" class="span12" placeholder="Please give a brief description of the layer" rows="5"></textarea>' +
						'</div>' +
					'</div>' +
									
					'<div class="control-group row" id="generalKeywords">' +
						'<div class="span4">' +
							'<label for="geotiff-importer-tagsinput">Layer keywords<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea  style="resize: none;" id="geotiff-importer-tagsinput" name="tagsInput" class="span12" placeholder="Please fill in the layer keywords" rows="1"></textarea>' +
						'</div>' +
					'</div>' +
				'</form>' +
			'</div>	' +
					
			'<div class="span6">' +
				'<div id="geotiff-importer-geonetwork-metadata"></div>' +
			'</div>' +
		'</div>'	;
		
		this._container.append($(importerBody));

		
		this._container.append('<div class="modal-footer">' +
									'<div id="geotiff-importer-div-submit" class="control-group pull-right">' +
										'<button type="button" data-dismiss="modal" id="geotiff-importer-cancel" class="btn" aria-hidden="true">Cancel</button>' +
										'<button type="button" id="geotiff-importer-import-button"  class="btn" >Import</button>' +
									'</div>' +				
								'</div>');
	},
	_initializeFunctionality : function (){		
		var importGeotiffURL = this.options.importGeotiffURL;
		var stylesURL = this.options.stylesURL ;
		var notificator = this.options.notificator;
		var toggleGeoNetworkMetadata = $("#geotiff-importer-toggle-geonetwork-metadata");
		var geoNetworkModule = window.config.geoNetworkModule.createInstance("#geotiff-importer-geonetwork-metadata");
		geoNetworkModule.createGeoNetworkForm();
		
		$(document).ready(function(){				
			$('#geotiff-importer-tagsinput').tagsInput({
			   'defaultText':'Add a Tag',
			   'delimiter': [',',';',' '],   
			   'minChars' : 2,
			   'maxChars' : 40,
			   'placeholderColor' : 'rgba(153, 153, 153, 0.65)'
			});	
			
			$('#geotiff-importer-browsefiles-button').bind("change", function() {
				var fileName = $(this).val().split('\\').pop();
				$('#geotiff-importer-selected-file').val(fileName);
				var valid = $('#geotiff-importer-browsefiles-button').valid();
				
				if(valid){
					fileName += " has been selected";
					window.noty.showNoty($("#geotiff-importer-notificator"), fileName, "success", false);
				}else{
					window.noty.closeAllNotys();
				}		
			});
			
		    $.validator.addMethod("validRegex", function(value, element) {
		        return this.optional(element) || /^[a-zA-Z][a-zA-Z0-9\_\s]+$/i.test(value);
		    }, "Field must start with a letter and contain only letters, numbers, or underscores.");		    
		
			$('#geotiff-importer-general-form').validate({
				rules: {
					layerName: {
						minlength: 2,
					    required : true
					},
					geocodeSystem:{
						required: true
					},
					style:{
						required: true
					},
					abstractDescription: {
						required: true
					},
					browseFiles:{
						extension: "tif",
						required: true
					}
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('geotiff-importer-color-red');
					
					if($(element).attr('id') === 'geotiff-importer-browsefiles-button'){
						$(element).closest('.control-group').find('#geotiff-importer-patch-browsefiles').css("border-color","red");		
					}					
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					if($(element).attr('id') === 'geotiff-importer-browsefiles-button'){
						$(element).closest('.control-group').find('#geotiff-importer-patch-browsefiles').css("border-color","#DDD");		
					}
					label.remove();
				},		
				errorPlacement: function(error, element) {	
					if(element.attr('id') === 'geotiff-importer-browsefiles-button'){
						error.appendTo(element.closest('.control-group').find('.help-inline'));	
						element.closest('.control-group').find('#geotiff-importer-patch-browsefiles').addClass('error');			
					}else{
						error.appendTo($(element).siblings('.help-inline'));
					}
				}
			});	
			
			jQuery.extend(jQuery.validator.messages, {
				extension: "Please upload a file with .tif extension."
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
			$('#geotiff-importercancel , #geotiff-importerclose-button').on('click',function (e){ 
				$('#geotiff-importergeneral-form')[0].reset();
				geoNetworkModule.resetGeoNetworkForm();
				
				$("#geotiff-importercontainer").find("*").removeClass("geotiff-importercolor-red error success");
				$('.help-inline > label').remove();
				
				window.noty.closeAllNotys();
				
				$('#geotiff-importerpatch-browsefiles').css("border-color", "gray");
				$('#geotiff-importertagsinput_tagsinput').find(".tag").remove();
			});
		});	
				
		$(document).ready(function() {
			
			var notificator = $("#geotiff-importer-notificator");
			
			$('#geotiff-importer-import-button').on('click',function (e){
				var validData = $('#geotiff-importer-general-form').valid();
				var validMetadata = geoNetworkModule.validateForm();
				var publishOnGeoNetwork = toggleGeoNetworkMetadata.styledCheckbox("isChecked");				
				
				if(validData && ((publishOnGeoNetwork && validMetadata) || !publishOnGeoNetwork)){					
					var spans = $('#geotiff-importer-container .tagsinput').find(".tag > span");
					var tags = [];
					for(var i=0; i < spans.length; i++){
						spans[i] = spans[i].innerHTML.split("&nbsp;").join("");
//						tags.push(spans[i]);
						tags.push( encodeURI( spans[i]) );
					}
					
					var file = document.getElementById('geotiff-importer-browsefiles-button').files[0];					
				 
					var importFormData = new FormData();
					importFormData.append("geotiffImportFile", file);					
					importFormData.append("geotiffImportProperties", new Object([JSON.stringify({
						layerName		: 	encodeURI( $('#geotiff-importer-layername').val() ),
						description		: 	encodeURI( $('#geotiff-importer-abstract').val().trim() ),
						style			:   $('#geotiff-importer-style option:selected').text(),
						tags			: 	tags
					})], {
						type: "application/json"
					}));
					
					if(publishOnGeoNetwork){
						
						let author = {};
						let authorMetaData = geoNetworkModule.getAuthorMetadata();
						for( let i in authorMetaData ) {
							author[i] = encodeURI( authorMetaData[i] );
						}
						
						let distributor = {};
						let distributorMetaData = geoNetworkModule.getDistributorMetadata();
						for( let i in distributorMetaData ) {
							distributor[i] = encodeURI( distributorMetaData[i] );
						}
						
						let provider = {};
						let providerMetaData = geoNetworkModule.getProviderMetadata();
						for( let i in providerMetaData ) {
							provider[i] = encodeURI( providerMetaData[i] );
						}
						
						importFormData.append("geotiffImportMetadata", new Object([JSON.stringify({
							title			:	encodeURI( $('#geotiff-importer-layername').val() ),
							description		: 	encodeURI( $('#geotiff-importer-abstract').val().trim() ),
							keywords		: 	tags,
											
							purpose			:	encodeURI( geoNetworkModule.getPurpose() ),
							limitation		:	encodeURI( geoNetworkModule.getLimitation() ),
			
							author 			: 	author,
							distributor 	: 	distributor,
							provider 		: 	provider 
						})], {
							type: "application/json"
						}));	
					}	
					
 					$.ajax({
 					    type : "POST", 					    
 					    url : importGeotiffURL, 					    
 					    processData : false, 					   
 		                contentType : false, 
 					    data: importFormData, 	
 					    beforeSend : function() {
 					    	$('#geotiff-importer-container .spinner').show();
 					    },
 					    success: function(data){
 							window.noty.showNoty(notificator, data, "success");	
 					    }, 					    
 					    error: function(jqXHR, exception){ 	
 					    	window.noty.errorHandlingNoty(notificator, jqXHR, exception); 					    		
					    },
					    complete: function() {
					    	$('#geotiff-importer-container .spinner').hide();
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