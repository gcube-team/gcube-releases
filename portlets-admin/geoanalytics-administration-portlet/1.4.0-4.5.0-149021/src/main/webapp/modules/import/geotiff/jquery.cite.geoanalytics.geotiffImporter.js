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
		var geotiffImporter = $('#geotiff-importer');	
		$(geotiffImporter).css("display", "inline-block");

		var showButton = 	`<button 	id="geotiff-importer-show-button"  
										type="button" 
										class="btn btn-large" 
										data-toggle="modal" 
										data-target="#geotiff-importer-container">										
								<i class="fa fa-upload" ></i> GeoTIFF Importer								 
							</button>`;
		showButton = $(showButton);
		geotiff-importer.append(showButton);
	},	
	_createNonModal : function() {
		var container = `<div id="geotiff-importer-container"> `;
		this._container = $(container);		
		this._container.appendTo(this.options.content);
	},	
	_createModal : function() {	
		var container = `<div id="geotiff-importer-container" 
							class="modal fade in geotiff-importer-container-button"   
							tabindex="-1" 
							role="dialog"	
							aria-labelledby="geotiff-importer"  
							aria-hidden="true " 
							style="display:none;"> `;			
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
			closeButton = `<button id="geotiff-importer-close-button" type="button" data-dismiss="modal" aria-hidden="true"></button>`;
			header = `<div id="geotiff-importer-modal-header" class="modal-header">` +
				closeButton +
				`<h5 id="geotiff-importer-label-modal">GeoTIFF Importer</h5> 
			</div>`;	
			body = `<div id="geotiff-importer-modal-body" class="modal-body scrollable" >`;
			scrollableRow = " scrollable";
		}else{
			body = `<div id="geotiff-importer-modal-body"" >`;
		}

		var importerBody = header + body + 	
		
		`<div class="row">
		<div class="span6">
			<div class="spinner" style="display: none"></div>
			<span class="headerDescription">GENERAL</span>	
			<div id="geotiff-importer-notificator"></div>														
			<hr>	
			<form class="form-horizontal" id="geotiff-importer-form-data">	
						
				<div class="control-group row">				
					<div class="span4">
						<label>Data Location<span class="makeMeOrange">*</span></label>
					</div>		
																				
					<div class="span6" id="selecGeotiffInput">															
						<input id="geotiff-importer-selected-file" class="span11"  type="text" placeholder="No file selected" readonly> 	
						<br>
						<span class="help-inline"></span>
					</div>	
					
					<div class="controls span2" id="geotiff-importer-patch-browsefiles">															
						<button class="span12">Browse</button>
						<input id="geotiff-importer-browsefiles-button" name="browseFiles" type="file" >	
					</div>	
				</div>
				
				<div class="control-group row">
					<div class="span4">
						<label for="geotiff-importer-layername">Layer Name<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="geotiff-importer-layername"  class="span12"  name="layerName"  placeholder="Please fill in your Layer Name" rows="1"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>

				<div class="control-group row">
					<div class="span4">
						<label for=""geotiff-importer-style"">Style<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<select id="geotiff-importer-style" class="span12" name="style" disabled>
							<option value="raster" selected>raster</option>
						</select>
						<span class="help-inline"></span>
					</div>
				</div>
			</form>
			
			<form class="form-horizontal" id="geotiff-importer-form-description">									
				<div class="control-group row" id="generalDescription">
					<div class="span4">
						<label for="geotiff-importer-abstract">Layer description<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea name="abstractDescription" id="geotiff-importer-abstract" class="span12" placeholder="Please give a brief description of the layer" rows="5"></textarea>
					</div>
				</div>
				
				<div class="control-group row" id="generalPurpose">
					<div class="span4">
						<label for="geotiff-importer-purpose">Layer purpose<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="geotiff-importer-purpose" name="purpose" class="span12" placeholder="Please fill in the layer purpose" rows="2"></textarea>
					</div>
				</div>
				
				<div class="control-group row" id="generalKeywords">
					<div class="span4">
						<label for="geotiff-importer-tagsinput">Layer keywords<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="geotiff-importer-tagsinput" name="tagsInput" class="span12" placeholder="Please fill in the layer keywords" rows="1"></textarea>
					</div>
				</div>
				
				<div class="control-group row" id="generalLimitations">
					<div class="span4">
						<label for="geotiff-importer-limitation">Layer limitations<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="geotiff-importer-limitation" name="limitation" class="span12" placeholder="Please fill in the layer limitations" rows="2"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
			</form>
		</div>
		
		
		<div class="span6">
			<span class="headerDescription">AUTHOR</span>																
			<hr>
			<form class="form-horizontal" id="geotiff-importer-author-metadata" >
				<div class="control-group row" id="authorOrganization">
					<div class="span4">
						<label for="geotiff-importer-author-organisationname">Auth. Organization<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="geotiff-importer-author-organisationname" name="organisationName" class="span12" rows="1" placeholder="Please fill in the organisation name"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
			</form>								
		
			<span class="headerDescription">DISTRIBUTOR</span>													
			<hr>
			<form class="form-horizontal" id="geotiff-importer-distributor-metadata" >									
				<div class="control-group row" id="distributorOrg">
					<div class="span4">
						<label for="geotiff-importer-distributor-organisationname">Distr. Organization<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="geotiff-importer-distributor-organisationname" name="organisationName" class="span12" rows="1" placeholder="Please fill in the organisation name"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
				
				<div class="control-group row" id="distributorDistributors">
					<div class="span4">
						<label for="geotiff-importer-distributor-individualname">Distributor/s<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="geotiff-importer-distributor-individualname" name="individualName" class="span12" rows="1" placeholder="Please fill in the distributor name(s)"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
				
				<div class="control-group row" id="distributorUrl">
					<div class="span4">
						<label for="geotiff-importer-distributor-onlineresource">URL of distribution<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea class="span12" 
						id="geotiff-importer-distributor-onlineresource" name="onlineResource" rows="1" placeholder="Please fill in the URL of distribution"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
			</form>

			<span class="headerDescription">PROVIDER</span>																
			<hr>
			<form class="form-horizontal" id="geotiff-importer-provider-metadata" >									
				<div class="control-group row" id="providerOrganization">
					<div class="span4">
						<label for="geotiff-importer-provider-organisationname">Prov. Organization<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea 	id="geotiff-importer-provider-organisationname" name="organisationName" class="span12" rows="1" placeholder="Please fill in the organisation name"></textarea>						
						<span class="help-inline"></span>
					</div>
				</div>
			
				<div class="control-group row" id="providerProviders">
					<div class="span4">
						<label for="geotiff-importer-provider-individualname">Providers<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="geotiff-importer-provider-individualname"  name="individualName" class="span12" rows="1" placeholder="Please fill in the provider name(s)"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
				
				<div class="control-group row" id="providerURL">
					<div class="span4">
						<label for="geotiff-importer-provider-onlineresource">URL of provision<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="geotiff-importer-provider-onlineresource" name="onlineResource" class="span12" rows="1" placeholder="Please fill in the URL of provision"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
			</form>								
		</div>
	</div>	`;
		

		importerBody = $(importerBody);
		this._container.append(importerBody);

		
		this._container.append(`<div class="modal-footer">
									<div id="geotiff-importer-div-submit" class="control-group pull-right">
										<button type="button" data-dismiss="modal" id="geotiff-importer-cancel" class="btn" aria-hidden="true">Cancel</button>
										<button type="button" id="geotiff-importer-import-button"  class="btn" >Import</button>
									</div>				
								</div>`);	
	},
	_initializeFunctionality : function (){		
		var importGeotiffURL = this.options.importGeotiffURL;
		var stylesURL = this.options.stylesURL ;
		var notificator = this.options.notificator;
		
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
		    
			$('#geotiff-importer-form-description').validate({
				rules: {
					abstractDescription: {
						required: true
					},
					purpose:{
						required: true
					},
					limitation: {
						required: true,
					},
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('geotiff-importer-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					label.remove();
				},
				errorPlacement: function(error, element) {	} 
			});		
			
			
			$('#geotiff-importer-author-metadata').validate({
				rules: {
					organisationName:{			
						required: true,
					},
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('geotiff-importer-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					label.remove();
				},
				errorPlacement: function(error, element) {			
					error.appendTo($(element).siblings('.help-inline'));
				}
			});		
			

			$('#geotiff-importer-distributor-metadata').validate({
				rules: {
					organisationName:{			
						required: true,
					},
					individualName:{			
						required: true,
					},
					onlineResource:{			
						required: true,
						url:true
					}
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('geotiff-importer-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					label.remove();
				},
				errorPlacement: function(error, element) {			
					error.appendTo($(element).siblings('.help-inline'));
				}
			});	
			
			$('#geotiff-importer-provider-metadata').validate({
				rules: {
					organisationName:{			
						required: true,
					},
					individualName:{			
						required: true,
					},
					onlineResource:{			
						required: true,
						url:true
					}
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('geotiff-importer-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					label.remove();
				},
				errorPlacement: function(error, element) {			
					error.appendTo($(element).siblings('.help-inline'));
				}
			});
			
			$('#geotiff-importer-form-data').validate({
				rules: {
					layerName: {
						minlength: 2,
					    required : true
					},
					style:{
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
				extension: "Please upload a file with .geotiff extension."
			});
			
		});		
		
		// Clear contents with cancel
		
		$(document).ready(function() {
			$('#geotiff-importer-cancel , #geotiff-importer-close-button').on('click',function (e){ 
				$('#geotiff-importer-author-metadata')[0].reset();
				$('#geotiff-importer-distributor-metadata')[0].reset();
				$('#geotiff-importer-provider-metadata')[0].reset();
				$('#geotiff-importer-form-data')[0].reset();
				$('#geotiff-importer-form-description')[0].reset();
				
				$("#geotiff-importer-container").find("*").removeClass("geotiff-importer-color-red error success");
				$('.help-inline > label').remove();
				
				window.noty.closeAllNotys();
				
				$('#geotiff-importer-patch-browsefiles').css("border-color", "gray");
				$('#geotiff-importer-tagsinput_tagsinput').find(".tag").remove();
			});
		});	
				
		$(document).ready(function() {
			
			// Load Template Layers
			
			var notificator = $("#geotiff-importer-notificator");
			
			$('#geotiff-importer-import-button').on('click',function (e){
				var validDescription = $('#geotiff-importer-form-description').valid();
				var validAuthorMetadata = $('#geotiff-importer-author-metadata').valid();
				var validDistributorMetadata = $('#geotiff-importer-distributor-metadata').valid();
				var validProviderMetadata = $('#geotiff-importer-provider-metadata').valid();
				var validData = $('#geotiff-importer-form-data').valid();
				
				if(validDescription && validAuthorMetadata && validDistributorMetadata && validProviderMetadata && validData){				
					var spans = $('#geotiff-importer-container .tagsinput').find(".tag > span");
					var tags = [];
					for(var i=0; i < spans.length; i++){
						spans[i] = spans[i].innerHTML.split("&nbsp;").join("");
						tags.push(spans[i]);
					}			
					
					var file = document.getElementById('geotiff-importer-browsefiles-button').files[0];					
				 
					var importFormData = new FormData();
					importFormData.append("geotiffImportFile", file);					
					importFormData.append("geotiffImportProperties", new Object([JSON.stringify({
						"layerName"		: 	$('#geotiff-importer-layername').val(),
						"style"			:   $('#geotiff-importer-style option:selected').text()
					})], {
						type: "application/json"
					}));
					
					importFormData.append("geotiffImportMetadata", new Object([JSON.stringify({
						"description"		: 	$('#geotiff-importer-abstract').val().trim(),
						"purpose"			:	$('#geotiff-importer-purpose').val(),
						"keywords"			: 	tags,
										
						"limitation"		:	$('#geotiff-importer-limitation').val(),
						
						"user"				:	$('#geotiff-importer-author-organisationname').val(),
						"title"				:	$('#geotiff-importer-layername').val(),
								
						"distributorOrganisationName":  $('#geotiff-importer-distributor-organisationname').val(),					
						"distributorIndividualName"	:	$('#geotiff-importer-distributor-individualname').val(),
						"distributorOnlineResource"	:	$('#geotiff-importer-distributor-onlineresource').val(),
						
						"providerOrganisationName"	:  	$('#geotiff-importer-provider-organisationname').val(),					
						"providerIndividualName"	:	$('#geotiff-importer-provider-individualname').val(),
						"providerOnlineResource"	:	$('#geotiff-importer-provider-onlineresource').val()
					})], {
						type: "application/json"
					}));				
				
					
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