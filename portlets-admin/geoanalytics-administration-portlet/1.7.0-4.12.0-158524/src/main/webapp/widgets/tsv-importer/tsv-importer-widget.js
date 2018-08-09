$.widget('cite.tsvImporter', {	
	notificator : $("#tsv-importer-notificator"),
	options:{
		mode: "div"	,
		geocodeSystemsURL: "",
		importTsvURL: "",
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
		var tsvimporter = $('#tsvimporter');	
		$(tsvimporter).css("display", "inline-block");

		var showButton = 	'<button 	id="tsv-importer-show-button"  '+
										'type="button"   '+
										'class="btn btn-large"   '+
										'data-toggle="modal"   '+
										'data-target="#tsv-importer-container">	  '+									
								'<i class="fa fa-upload" ></i> TSV Importer	  '+							 
							'</button>  ';
		showButton = $(showButton);
		tsvimporter.append(showButton);
	},	
	_createNonModal : function() {
		var container = `<div id="tsv-importer-container"> `;
		this._container = $(container);		
		this._container.appendTo(this.options.content);
	},	
	_createModal : function() {	
		var container = '<div id="tsv-importer-container" ' +
							'class="modal fade in tsv-importer-container-button"' +
							'tabindex="-1" '+
							'role="dialog"	'+
							'aria-labelledby="tsvimporter"  '+
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
			closeButton = '<button id="tsv-importer-close-button" type="button" data-dismiss="modal" aria-hidden="true"></button>';
			header = '<div id="tsv-importer-modal-header" class="modal-header">' +
				closeButton +
				'<h5 id="tsv-importer-label-modal">TSV Importer</h5>' +
			'</div>';	
			body = '<div id="tsv-importer-modal-body" class="modal-body scrollable" >';
			scrollableRow = " scrollable";
		}else{
			body = '<div id="tsv-importer-modal-body" >';
		}

		var importerBody = header + body + 	'<input type="button" id="tsv-importer-toggle-geonetwork-metadata" />' +
		'<div class="row">' +
			'<div class="span6" id="tsv-importer-general-form-container">' +
				'<div class="spinner" style="display: none"></div>' +
				'<span class="headerDescription">GENERAL</span>	' +
				'<div id="tsv-importer-notificator"></div>	' +													
				'<hr>	' +
				'<form class="form-horizontal" id="tsv-importer-general-form">	' +
					'<div class="control-group row">		' +		
						'<div class="span4">' +
							'<label>Data Location<span class="makeMeOrange">*</span></label>' +
						'</div>		' +
																					
						'<div class="span6">		' +													
							'<input id="tsv-importer-selected-file" class="span11"  type="text" placeholder="No file selected" readonly> 	' +
							'<br>' +
							'<span class="help-inline"></span>' +
						'</div>				' +			
						'<div class="controls span2" id="tsv-importer-patch-browsefiles">	' +														
							'<button class="span12">Browse</button>' +
							'<input id="tsv-importer-browsefiles-button" name="browseFiles" type="file" >	' +
						'</div>	' +
					'</div>		' +			
					'<div class="control-group row">' +
						'<div class="span4">' +
							'<label for="tsv-importer-layername">Layer Name<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea  style="resize: none;" id="tsv-importer-layername"  class="span12"  name="layerName"  placeholder="Please fill in your Layer Name" rows="1"></textarea>' +
							'<span class="help-inline"></span>' +
						'</div>' +
					'</div>' +	
					'<div class="control-group row">' +
						'<div class="span4">' +
							'<label for=""tsv-importer-geocodesystem"">Geocode System<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<select id="tsv-importer-geocodesystem" class="span12"  name="geocodeSystem">' +
								'<option  value="" disabled selected>Choose a Geocode System</option>' +
							'</select>' +
							'<span class="help-inline"></span>' +
						'</div>' +
					'</div>' +
					'<div class="control-group row">' +
						'<div class="span4">' +
							'<label for=""tsv-importer-style"">Style<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<select id="tsv-importer-style" class="span12"  name="style">' +
								'<option  value="" disabled selected>Choose a Style</option>' +
							'</select>' +
							'<span class="help-inline"></span>' +
						'</div>' +
					'</div>	' +
										
					'<div class="control-group row" id="tsv-importer-general-description">' +
						'<div class="span4">' +
							'<label for="tsv-importer-abstract">Layer description<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea name="abstractDescription" id="tsv-importer-abstract" class="span12" placeholder="Please give a brief description of the layer" rows="5"></textarea>' +
						'</div>' +
					'</div>' +
									
					'<div class="control-group row" id="generalKeywords">' +
						'<div class="span4">' +
							'<label for="tsv-importer-tagsinput">Layer keywords<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea  style="resize: none;" id="tsv-importer-tagsinput" name="tagsInput" class="span12" placeholder="Please fill in the layer keywords" rows="1"></textarea>' +
						'</div>' +
					'</div>' +
				'</form>' +
			'</div>	' +
					
			'<div class="span6">' +
				'<div id="tsv-importer-geonetwork-metadata"></div>' +
			'</div>' +
		'</div>'	;
		
		this._container.append($(importerBody));

		
		this._container.append('<div class="modal-footer">' +
									'<div id="tsv-importer-div-submit" class="control-group pull-right">' +
										'<button type="button" data-dismiss="modal" id="tsv-importer-cancel" class="btn" aria-hidden="true">Cancel</button>' +
										'<button type="button" id="tsv-importer-import-button"  class="btn" >Import</button>' +
									'</div>' +				
								'</div>');
	},
	_initializeFunctionality : function (){		
		var importTsvURL = this.options.importTsvURL;
		var geocodeSystemsURL = this.options.geocodeSystemsURL ;
		var stylesURL = this.options.stylesURL ;
		var notificator = this.options.notificator;
		var toggleGeoNetworkMetadata = $("#tsv-importer-toggle-geonetwork-metadata");
		var geoNetworkModule = window.config.geoNetworkModule.createInstance("#tsv-importer-geonetwork-metadata");
		geoNetworkModule.createGeoNetworkForm();		
		
		$(document).ready(function(){				
			$('#tsv-importer-tagsinput').tagsInput({
			   'defaultText':'Add a Tag',
			   'delimiter': [',',';',' '],   
			   'minChars' : 2,
			   'maxChars' : 40,
			   'placeholderColor' : 'rgba(153, 153, 153, 0.65)'
			});	
			
			$('#tsv-importer-browsefiles-button').bind("change", function() {
				var fileName = $(this).val().split('\\').pop();
				$('#tsv-importer-selected-file').val(fileName);
				var valid = $('#tsv-importer-browsefiles-button').valid();
				
				if(valid){
					fileName += " has been selected";
					window.noty.showNoty($("#tsv-importer-notificator"), fileName, "success", false);
				}else{
					window.noty.closeAllNotys();
				}		
			});
			
		    $.validator.addMethod("validRegex", function(value, element) {
		        return this.optional(element) || /^[a-zA-Z][a-zA-Z0-9\_\s]+$/i.test(value);
		    }, "Field must start with a letter and contain only letters, numbers, or underscores.");		    			
			
			$('#tsv-importer-general-form').validate({
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
						extension: "tsv",
						required: true
					}
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('tsv-importer-color-red');
					
					if($(element).attr('id') === 'tsv-importer-browsefiles-button'){
						$(element).closest('.control-group').find('#tsv-importer-patch-browsefiles').css("border-color","red");		
					}					
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					if($(element).attr('id') === 'tsv-importer-browsefiles-button'){
						$(element).closest('.control-group').find('#tsv-importer-patch-browsefiles').css("border-color","#DDD");		
					}
					label.remove();
				},		
				errorPlacement: function(error, element) {	
					if(element.attr('id') === 'tsv-importer-browsefiles-button'){
						error.appendTo(element.closest('.control-group').find('.help-inline'));	
						element.closest('.control-group').find('#tsv-importer-patch-browsefiles').addClass('error');			
					}else{
						error.appendTo($(element).siblings('.help-inline'));
					}
				}
			});				
			
			jQuery.extend(jQuery.validator.messages, {
				extension: "Please upload a file with .tsv extension."
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
			$('#tsv-importer-cancel , #tsv-importer-close-button').on('click',function (e){ 
				$('#tsv-importer-general-form')[0].reset();
				geoNetworkModule.resetGeoNetworkForm();
				
				$("#tsv-importer-container").find("*").removeClass("tsv-importer-color-red error success");
				$('.help-inline > label').remove();
				
				window.noty.closeAllNotys();
				
				$('#tsv-importer-patch-browsefiles').css("border-color", "gray");
				$('#tsv-importer-tagsinput_tagsinput').find(".tag").remove();
			});
		});	
				
		$(document).ready(function() {
			
			// Load Template Layers
			
			var notificator = $("#tsv-importer-notificator");
			
			$.ajax({
			    url : geocodeSystemsURL, 
				dataType:'json',
		        type:'GET',
			    success: function(response){
			    	if (response.status) {	
			    		var selectBox = $('#tsv-importer-geocodesystem');
			    		for(var i=0; i < response.data.length ; i++){	
			    			selectBox.append("<option>" + response.data[i] + "</option>");
			    		}
			    	} else{		
						window.noty.showNoty(notificator,  response.message, "error");			
			    	}
			    }, 					    
			    error: function(jqXHR, exception){	
			    	window.noty.errorHandlingNoty(notificator, jqXHR, exception);		    	 	
			    }
			});				
			
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
						$('#tsv-importer-style').append($option);
					});
				},
				error : function(jqXHR, exception) {
					window.noty.errorHandlingNoty(notificator, jqXHR, exception);
				}
			});	
			
			$('#tsv-importer-import-button').on('click',function (e){
				var validData = $('#tsv-importer-general-form').valid();
				var validMetadata = geoNetworkModule.validateForm();
				var publishOnGeoNetwork = toggleGeoNetworkMetadata.styledCheckbox("isChecked");				
				
				if(validData && ((publishOnGeoNetwork && validMetadata) || !publishOnGeoNetwork)){				
					var spans = $('#tsv-importer-container .tagsinput').find(".tag > span");
					var tags = [];
					for(var i=0; i < spans.length; i++){
						spans[i] = spans[i].innerHTML.split("&nbsp;").join("");
						tags.push(spans[i]);
					}
					
					var file = document.getElementById('tsv-importer-browsefiles-button').files[0];					
				 
					var importFormData = new FormData();
					importFormData.append("tsvImportFile", file);					
					importFormData.append("tsvImportProperties", new Object([JSON.stringify({
						layerName		: 	$('#tsv-importer-layername').val(),
						description		: 	$('#tsv-importer-abstract').val().trim(),
						geocodeSystem	:	$('#tsv-importer-geocodesystem').val(),
						style			:   $('#tsv-importer-style option:selected').text(),
						tags			: 	tags,
						fileName		:	file.name
					})], {
						type: "application/json"
					}));
					
					if(publishOnGeoNetwork){				
						importFormData.append("tsvImportMetadata", new Object([JSON.stringify({
							title			:	$('#tsv-importer-layername').val(),
							description		: 	$('#tsv-importer-abstract').val().trim(),
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
 					    url : importTsvURL, 					    
 					    processData : false, 					   
 		                contentType : false, 
 					    data: importFormData, 	
 					    beforeSend : function() {
 					    	$('#tsv-importer-container .spinner').show();
 					    },
 					    success: function(data){
 							window.noty.showNoty(notificator, data, "success");	
 					    }, 					    
 					    error: function(jqXHR, exception){ 	
 					    	window.noty.errorHandlingNoty(notificator, jqXHR, exception); 					    		
					    },
					    complete: function() {
					    	$('#tsv-importer-container .spinner').hide();
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