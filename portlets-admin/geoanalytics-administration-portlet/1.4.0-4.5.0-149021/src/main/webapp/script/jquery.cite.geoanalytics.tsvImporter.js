$.widget('cite.tsvImporter', {	
	notificator : $("#tsvimporter-notificator"),
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

		var showButton = 	`<button 	id="tsvimporter-show-button"  
										type="button" 
										class="btn btn-large" 
										data-toggle="modal" 
										data-target="#tsvimporter-container">										
								<i class="fa fa-upload" ></i> TSV Importer								 
							</button>`;
		showButton = $(showButton);
		tsvimporter.append(showButton);
	},	
	_createNonModal : function() {
		var container = `<div id="tsvimporter-container"> `;
		this._container = $(container);		
		this._container.appendTo(this.options.content);
	},	
	_createModal : function() {	
		var container = `<div id="tsvimporter-container" 
							class="modal fade in tsvimporter-container-button"   
							tabindex="-1" 
							role="dialog"	
							aria-labelledby="tsvimporter"  
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
			closeButton = `<button id="tsvimporter-close-button" type="button" data-dismiss="modal" aria-hidden="true"></button>`;
			header = `<div id="tsvimporter-modal-header" class="modal-header">` +
				closeButton +
				`<h5 id="tsvimporter-label-modal">TSV Importer</h5> 
			</div>`;	
			body = `<div id="tsvimporter-modal-body" class="modal-body scrollable" >`;
			scrollableRow = " scrollable";
		}else{
			body = `<div id="tsvimporter-modal-body"" >`;
		}

		var importerBody = header + body + 	
		
		`<div class="row">
		<div class="span6">
			<div class="spinner" style="display: none"></div>
			<span class="headerDescription">GENERAL</span>	
			<div id="tsvimporter-notificator"></div>														
			<hr>	
			<form class="form-horizontal" id="tsvimporter-form-data">	
						
				<div class="control-group row">				
					<div class="span4">
						<label>Data Location<span class="makeMeOrange">*</span></label>
					</div>		
																				
					<div class="span6" id="selecTsvInput">															
						<input id="tsvimporter-selected-file" class="span11"  type="text" placeholder="No file selected" readonly> 	
						<br>
						<span class="help-inline"></span>
					</div>	
					
					<div class="controls span2" id="tsvimporter-patch-browsefiles">															
						<button class="span12">Browse</button>
						<input id="tsvimporter-browsefiles-button" name="browseFiles" type="file" >	
					</div>	
				</div>
				
				<div class="control-group row">
					<div class="span4">
						<label for="tsvimporter-layername">Layer Name<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="tsvimporter-layername"  class="span12"  name="layerName"  placeholder="Please fill in your Layer Name" rows="1"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>

				<div class="control-group row">
					<div class="span4">
						<label for=""tsvimporter-geocodesystem"">Geocode System<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<select id="tsvimporter-geocodesystem" class="span12"  name="geocodeSystem">
							<option  value="" disabled selected>Choose a Geocode System</option>
						</select>
						<span class="help-inline"></span>
					</div>
				</div>
				<div class="control-group row">
					<div class="span4">
						<label for=""tsvimporter-style"">Style<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<select id="tsvimporter-style" class="span12"  name="style">
							<option  value="" disabled selected>Choose a Style</option>
						</select>
						<span class="help-inline"></span>
					</div>
				</div>
			</form>
			
			<form class="form-horizontal" id="tsvimporter-form-description">									
				<div class="control-group row" id="generalDescription">
					<div class="span4">
						<label for="tsvimporter-abstract">Layer description<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea name="abstractDescription" id="tsvimporter-abstract" class="span12" placeholder="Please give a brief description of the layer" rows="5"></textarea>
					</div>
				</div>
				
				<div class="control-group row" id="generalPurpose">
					<div class="span4">
						<label for="tsvimporter-purpose">Layer purpose<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="tsvimporter-purpose" name="purpose" class="span12" placeholder="Please fill in the layer purpose" rows="2"></textarea>
					</div>
				</div>
				
				<div class="control-group row" id="generalKeywords">
					<div class="span4">
						<label for="tsvimporter-tagsinput">Layer keywords<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="tsvimporter-tagsinput" name="tagsInput" class="span12" placeholder="Please fill in the layer keywords" rows="1"></textarea>
					</div>
				</div>
				
				<div class="control-group row" id="generalLimitations">
					<div class="span4">
						<label for="tsvimporter-limitation">Layer limitations<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="tsvimporter-limitation" name="limitation" class="span12" placeholder="Please fill in the layer limitations" rows="2"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
			</form>
		</div>
		
		
		<div class="span6">
			<span class="headerDescription">AUTHOR</span>																
			<hr>
			<form class="form-horizontal" id="tsvimporter-author-metadata" >
				<div class="control-group row" id="authorOrganization">
					<div class="span4">
						<label for="tsvimporter-author-organisationname">Auth. Organization<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="tsvimporter-author-organisationname" name="organisationName" class="span12" rows="1" placeholder="Please fill in the organisation name"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
			</form>								
		
			<span class="headerDescription">DISTRIBUTOR</span>													
			<hr>
			<form class="form-horizontal" id="tsvimporter-distributor-metadata" >									
				<div class="control-group row" id="distributorOrg">
					<div class="span4">
						<label for="tsvimporter-distributor-organisationname">Distr. Organization<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="tsvimporter-distributor-organisationname" name="organisationName" class="span12" rows="1" placeholder="Please fill in the organisation name"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
				
				<div class="control-group row" id="distributorDistributors">
					<div class="span4">
						<label for="tsvimporter-distributor-individualname">Distributor/s<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="tsvimporter-distributor-individualname" name="individualName" class="span12" rows="1" placeholder="Please fill in the distributor name(s)"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
				
				<div class="control-group row" id="distributorUrl">
					<div class="span4">
						<label for="tsvimporter-distributor-onlineresource">URL of distribution<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea class="span12" 
						id="tsvimporter-distributor-onlineresource" name="onlineResource" rows="1" placeholder="Please fill in the URL of distribution"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
			</form>

			<span class="headerDescription">PROVIDER</span>																
			<hr>
			<form class="form-horizontal" id="tsvimporter-provider-metadata" >									
				<div class="control-group row" id="providerOrganization">
					<div class="span4">
						<label for="tsvimporter-provider-organisationname">Prov. Organization<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea 	id="tsvimporter-provider-organisationname" name="organisationName" class="span12" rows="1" placeholder="Please fill in the organisation name"></textarea>						
						<span class="help-inline"></span>
					</div>
				</div>
			
				<div class="control-group row" id="providerProviders">
					<div class="span4">
						<label for="tsvimporter-provider-individualname">Providers<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="tsvimporter-provider-individualname"  name="individualName" class="span12" rows="1" placeholder="Please fill in the provider name(s)"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
				
				<div class="control-group row" id="providerURL">
					<div class="span4">
						<label for="tsvimporter-provider-onlineresource">URL of provision<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="tsvimporter-provider-onlineresource" name="onlineResource" class="span12" rows="1" placeholder="Please fill in the URL of provision"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
			</form>								
		</div>
	</div>	`;
		

		importerBody = $(importerBody);
		this._container.append(importerBody);

		
		this._container.append(`<div class="modal-footer">
									<div id="tsvimporter-div-submit" class="control-group pull-right">
										<button type="button" data-dismiss="modal" id="tsvimporter-cancel" class="btn" aria-hidden="true">Cancel</button>
										<button type="button" id="tsvimporter-import-button"  class="btn" >Import</button>
									</div>				
								</div>`);	
	},
	_initializeFunctionality : function (){		
		var importTsvURL = this.options.importTsvURL;
		var geocodeSystemsURL = this.options.geocodeSystemsURL ;
		var stylesURL = this.options.stylesURL ;
		var notificator = this.options.notificator;
		
		$(document).ready(function(){				
			$('#tsvimporter-tagsinput').tagsInput({
			   'defaultText':'Add a Tag',
			   'delimiter': [',',';',' '],   
			   'minChars' : 2,
			   'maxChars' : 40,
			   'placeholderColor' : 'rgba(153, 153, 153, 0.65)'
			});	
			
			$('#tsvimporter-browsefiles-button').bind("change", function() {
				var fileName = $(this).val().split('\\').pop();
				$('#tsvimporter-selected-file').val(fileName);
				var valid = $('#tsvimporter-browsefiles-button').valid();
				
				if(valid){
					fileName += " has been selected";
					window.noty.showNoty($("#tsvimporter-notificator"), fileName, "success", false);
				}else{
					window.noty.closeAllNotys();
				}		
			});
			
		    $.validator.addMethod("validRegex", function(value, element) {
		        return this.optional(element) || /^[a-zA-Z][a-zA-Z0-9\_\s]+$/i.test(value);
		    }, "Field must start with a letter and contain only letters, numbers, or underscores.");
		    
			$('#tsvimporter-form-description').validate({
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
					$(element).closest('.control-group').find('.help-inline').addClass('tsvimporter-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					label.remove();
				},
				errorPlacement: function(error, element) {	} 
			});		
			
			
			$('#tsvimporter-author-metadata').validate({
				rules: {
					organisationName:{			
						required: true,
					},
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('tsvimporter-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					label.remove();
				},
				errorPlacement: function(error, element) {			
					error.appendTo($(element).siblings('.help-inline'));
				}
			});		
			

			$('#tsvimporter-distributor-metadata').validate({
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
					$(element).closest('.control-group').find('.help-inline').addClass('tsvimporter-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					label.remove();
				},
				errorPlacement: function(error, element) {			
					error.appendTo($(element).siblings('.help-inline'));
				}
			});	
			
			$('#tsvimporter-provider-metadata').validate({
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
					$(element).closest('.control-group').find('.help-inline').addClass('tsvimporter-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					label.remove();
				},
				errorPlacement: function(error, element) {			
					error.appendTo($(element).siblings('.help-inline'));
				}
			});
			
			$('#tsvimporter-form-data').validate({
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
					browseFiles:{
						extension: "tsv",
						required: true
					}
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('tsvimporter-color-red');
					
					if($(element).attr('id') === 'tsvimporter-browsefiles-button'){
						$(element).closest('.control-group').find('#tsvimporter-patch-browsefiles').css("border-color","red");		
					}					
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					if($(element).attr('id') === 'tsvimporter-browsefiles-button'){
						$(element).closest('.control-group').find('#tsvimporter-patch-browsefiles').css("border-color","#DDD");		
					}
					label.remove();
				},		
				errorPlacement: function(error, element) {	
					if(element.attr('id') === 'tsvimporter-browsefiles-button'){
						error.appendTo(element.closest('.control-group').find('.help-inline'));	
						element.closest('.control-group').find('#tsvimporter-patch-browsefiles').addClass('error');			
					}else{
						error.appendTo($(element).siblings('.help-inline'));
					}
				}
			});		
			
			
			jQuery.extend(jQuery.validator.messages, {
				extension: "Please upload a file with .tsv extension."
			});
			
		});		
		
		// Clear contents with cancel
		
		$(document).ready(function() {
			$('#tsvimporter-cancel , #tsvimporter-close-button').on('click',function (e){ 
				$('#tsvimporter-author-metadata')[0].reset();
				$('#tsvimporter-distributor-metadata')[0].reset();
				$('#tsvimporter-provider-metadata')[0].reset();
				$('#tsvimporter-form-data')[0].reset();
				$('#tsvimporter-form-description')[0].reset();
				
				$("#tsvimporter-container").find("*").removeClass("tsvimporter-color-red error success");
				$('.help-inline > label').remove();
				
				window.noty.closeAllNotys();
				
				$('#tsvimporter-patch-browsefiles').css("border-color", "gray");
				$('#tsvimporter-tagsinput_tagsinput').find(".tag").remove();
			});
		});	
				
		$(document).ready(function() {
			
			// Load Template Layers
			
			var notificator = $("#tsvimporter-notificator");
			
			$.ajax({
			    url : geocodeSystemsURL, 
				dataType:'json',
		        type:'GET',
			    success: function(response){
			    	if (response.status) {	
			    		var selectBox = $('#tsvimporter-geocodesystem');
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
						$('#tsvimporter-style').append($option);
					});

				},
				error : function(jqXHR, textStatus, errorThrown) {
					window.noty.errorHandlingNoty(notificator, jqXHR, exception);
				}
			});	
			
			$('#tsvimporter-import-button').on('click',function (e){
				var validDescription = $('#tsvimporter-form-description').valid();
				var validAuthorMetadata = $('#tsvimporter-author-metadata').valid();
				var validDistributorMetadata = $('#tsvimporter-distributor-metadata').valid();
				var validProviderMetadata = $('#tsvimporter-provider-metadata').valid();
				var validData = $('#tsvimporter-form-data').valid();
				
				if(validDescription && validAuthorMetadata && validDistributorMetadata && validProviderMetadata && validData){				
					var spans = $('#tsvimporter-container .tagsinput').find(".tag > span");
					var tags = [];
					for(var i=0; i < spans.length; i++){
						spans[i] = spans[i].innerHTML.split("&nbsp;").join("");
						tags.push(spans[i]);
					}
					
					var file = document.getElementById('tsvimporter-browsefiles-button').files[0];					
				 
					var importFormData = new FormData();
					importFormData.append("tsvImportFile", file);					
					importFormData.append("tsvImportProperties", new Object([JSON.stringify({
						"layerName"		: 	$('#tsvimporter-layername').val(),
						"geocodeSystem"	:	$('#tsvimporter-geocodesystem').val(),
						"fileName"		:	file.name,
						"style"			:   $('#tsvimporter-style option:selected').text()
					})], {
						type: "application/json"
					}));
					
					importFormData.append("tsvImportMetadata", new Object([JSON.stringify({
						"description"		: 	$('#tsvimporter-abstract').val().trim(),
						"purpose"			:	$('#tsvimporter-purpose').val(),
						"keywords"			: 	tags,
										
						"limitation"		:	$('#tsvimporter-limitation').val(),
						
						"user"				:	$('#tsvimporter-author-organisationname').val(),
						"title"				:	$('#tsvimporter-layername').val(),
								
						"distributorOrganisationName":  $('#tsvimporter-distributor-organisationname').val(),					
						"distributorIndividualName"	:	$('#tsvimporter-distributor-individualname').val(),
						"distributorOnlineResource"	:	$('#tsvimporter-distributor-onlineresource').val(),
						
						"providerOrganisationName"	:  	$('#tsvimporter-provider-organisationname').val(),					
						"providerIndividualName"	:	$('#tsvimporter-provider-individualname').val(),
						"providerOnlineResource"	:	$('#tsvimporter-provider-onlineresource').val()
					})], {
						type: "application/json"
					}));				
				
					
 					$.ajax({
 					    type : "POST", 					    
 					    url : importTsvURL, 					    
 					    processData : false, 					   
 		                contentType : false, 
 					    data: importFormData, 	
 					    beforeSend : function() {
 					    	$('#tsvimporter-container .spinner').show();
 					    },
 					    success: function(data){
 							window.noty.showNoty(notificator, data, "success");	
 					    }, 					    
 					    error: function(jqXHR, exception){ 	
 					    	window.noty.errorHandlingNoty(notificator, jqXHR, exception); 					    		
					    },
					    complete: function() {
					    	$('#tsvimporter-container .spinner').hide();
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