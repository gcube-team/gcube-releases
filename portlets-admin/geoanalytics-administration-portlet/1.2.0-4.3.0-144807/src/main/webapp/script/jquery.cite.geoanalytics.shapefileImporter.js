$.widget('cite.shapefileImporter', {	
	isActive : false,
	notificator : $("#shapefileimporter-notificator"),
	options:{
		mode: "div"	,
		importShapefileURL: "",
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

		var showButton = 	`<button 	id="shapefileimporter-show-button"  
										type="button" 
										class="btn btn-large" 
										data-toggle="modal" 
										data-target="#shapefileimporter-container">										
								<i class="fa fa-upload" ></i> Shapefile Importer								 
							</button>`;
		showButton = $(showButton);
		shapefileimporter.append(showButton);
	},	
	_createNonModal : function() {
		var container = `<div id="shapefileimporter-container"> `;
		this._container = $(container);		
		this._container.appendTo(this.options.content);
	},	
	_createModal : function() {	
		var container = `<div id="shapefileimporter-container" 
							class="modal fade in shapefileimporter-container-button"   
							tabindex="-1" 
							role="dialog"	
							aria-labelledby="shapefileimporter"  
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
			closeButton = `<button id="shapefileimporter-close-button" type="button" data-dismiss="modal" aria-hidden="true"></button>`;
			header = `<div id="shapefileimporter-modal-header" class="modal-header">` +
				closeButton +
				`<h5 id="shapefileimporter-label-modal">Shapefile Importer</h5> 
			</div>`;	
			body = `<div id="shapefileimporter-modal-body" class="modal-body scrollable" >`;
			scrollableRow = " scrollable";
		}else{
			body = `<div id="shapefileimporter-modal-body"" >`;
		}

		var importerBody = header + body + 	
		
		`<div class="row">
		<div class="span6">
			<div class="spinner" style="display: none"></div>
			<span class="headerDescription">GENERAL</span>	
			<div id="shapefileimporter-notificator"></div>														
			<hr>	
			<form class="form-horizontal" id="shapefileimporter-form-data">	
						
				<div class="control-group row">				
					<div class="span4">
						<label>Data Location<span class="makeMeOrange">*</span></label>
					</div>		
																				
					<div class="span6" id="selectShapefileInput">															
						<input id="shapefileimporter-selected-file" class="span11"  type="text" placeholder="No shapefile selected with zip extension" readonly> 	
						<br>
						<span class="help-inline"></span>
					</div>	
					
					<div class="controls span2" id="shapefileimporter-patch-browsefiles">															
						<button class="span12">Browse</button>
						<input id="shapefileimporter-browsefiles-button" name="browseFiles" type="file" >	
					</div>	
				</div>
				
				<div class="control-group row">
					<div class="span4">
						<label for="shapefileimporter-layername">Layer Name<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="shapefileimporter-layername"  class="span12"  name="layerName"  placeholder="Please fill in your layer name" rows="1"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>

			</form>
			
			<form class="form-horizontal" id="shapefileimporter-form-description">									
				<div class="control-group row" id="generalDescription">
					<div class="span4">
						<label for="shapefileimporter-abstract">Layer description<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea name="abstractDescription" id="shapefileimporter-abstract" class="span12" placeholder="Please give a brief description of the layer" rows="5"></textarea>
					</div>
				</div>
				
				<div class="control-group row" id="generalPurpose">
					<div class="span4">
						<label for="shapefileimporter-purpose">Layer purpose<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="shapefileimporter-purpose" name="purpose" class="span12" placeholder="Please fill in the layer purpose" rows="2"></textarea>
					</div>
				</div>
				
				<div class="control-group row" id="generalKeywords">
					<div class="span4">
						<label for="shapefileimporter-tagsinput">Layer keywords<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="shapefileimporter-tagsinput" name="tagsInput" class="span12" placeholder="Please fill in the layer keywords" rows="1"></textarea>
					</div>
				</div>
				
				<div class="control-group row" id="generalLimitations">
					<div class="span4">
						<label for="shapefileimporter-limitation">Layer limitations<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="shapefileimporter-limitation" name="limitation" class="span12" placeholder="Please fill in the layer limitations" rows="2"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
			</form>
		</div>
		
		
		<div class="span6">
			<span class="headerDescription">AUTHOR</span>																
			<hr>
			<form class="form-horizontal" id="shapefileimporter-author-metadata" >
				<div class="control-group row" id="authorOrganization">
					<div class="span4">
						<label for="shapefileimporter-author-organisationname">Auth. Organization<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="shapefileimporter-author-organisationname" name="organisationName" class="span12" rows="1"  placeholder="Please fill in the organisation's name"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
			</form>								
		
			<span class="headerDescription">DISTRIBUTOR</span>													
			<hr>
			<form class="form-horizontal" id="shapefileimporter-distributor-metadata" >									
				<div class="control-group row" id="distributorOrg">
					<div class="span4">
						<label for="shapefileimporter-distributor-organisationname">Distr. Organization<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="shapefileimporter-distributor-organisationname" name="organisationName" class="span12" rows="1"  placeholder="Please fill in the organisation's name"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
				
				<div class="control-group row" id="distributorDistributors">
					<div class="span4">
						<label for="shapefileimporter-distributor-individualname">Distributor/s<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="shapefileimporter-distributor-individualname" name="individualName" class="span12" rows="1" placeholder="Please fill in the distributor name(s)"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
				
				<div class="control-group row" id="distributorUrl">
					<div class="span4">
						<label for="shapefileimporter-distributor-onlineresource">URL of distribution<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea class="span12" 
						id="shapefileimporter-distributor-onlineresource" name="onlineResource" rows="1" placeholder="Please fill in the URL of distribution"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
			</form>

			<span class="headerDescription">PROVIDER</span>																
			<hr>
			<form class="form-horizontal" id="shapefileimporter-provider-metadata" >									
				<div class="control-group row" id="providerOrganization">
					<div class="span4">
						<label for="shapefileimporter-provider-organisationname">Prov. Organization<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea 	id="shapefileimporter-provider-organisationname" name="organisationName" class="span12" rows="1" placeholder="Please fill in the organisation's name"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
			
				<div class="control-group row" id="providerProviders">
					<div class="span4">
						<label for="shapefileimporter-provider-individualname">Providers<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="shapefileimporter-provider-individualname"  name="individualName" class="span12" rows="1" placeholder="Please fill in the provider name(s)"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
				
				<div class="control-group row" id="providerURL">
					<div class="span4">
						<label for="shapefileimporter-provider-onlineresource">URL of provision<span class="makeMeOrange">*</span></label>
					</div>
					<div class="span8">
						<textarea id="shapefileimporter-provider-onlineresource" name="onlineResource" class="span12" rows="1" placeholder="Please fill in the URL of provision"></textarea>
						<span class="help-inline"></span>
					</div>
				</div>
			</form>								
		</div>
	</div>	`;
		

		importerBody = $(importerBody);
		this._container.append(importerBody);

		
		this._container.append(`<div class="modal-footer">
									<div id="shapefileimporter-div-submit" class="control-group pull-right">
										<button type="button" data-dismiss="modal" id="shapefileimporter-cancel" class="btn" aria-hidden="true">Cancel</button>
										<button type="button" id="shapefileimporter-import-button"  class="btn" >Import</button>
									</div>				
								</div>`);	
	},
	_initializeFunctionality : function (){		
		var importShapefileURL = this.options.importShapefileURL;
		var notificator = this.options.notificator;
		
		$(document).ready(function(){				
			$('#shapefileimporter-tagsinput').tagsInput({
			   'defaultText':'Add a Tag',
			   'delimiter': [',',';',' '],   
			   'minChars' : 2,
			   'maxChars' : 40,
			   'placeholderColor' : 'rgba(153, 153, 153, 0.65)'
			});	
			
			$('#shapefileimporter-browsefiles-button').bind("change", function() {
				var fileName = $(this).val().split('\\').pop();
				$('#shapefileimporter-selected-file').val(fileName);
				var valid = $('#shapefileimporter-browsefiles-button').valid();
				
				if(valid){
					fileName += " has been selected";
					window.noty.showNoty($("#shapefileimporter-notificator"), fileName, "success", false);
				}else{
					window.noty.closeAllNotys();
				}		
			});
			
		    $.validator.addMethod("validRegex", function(value, element) {
		        return this.optional(element) || /^[a-zA-Z][a-zA-Z0-9\_\s]+$/i.test(value);
		    }, "Field must start with a letter and contain only letters, numbers, or underscores.");
		    
			$('#shapefileimporter-form-description').validate({
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
					$(element).closest('.control-group').find('.help-inline').addClass('shapefileimporter-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					label.remove();
				},
				errorPlacement: function(error, element) {	} 
			});		
			
			
			$('#shapefileimporter-author-metadata').validate({
				rules: {
					organisationName:{			
						required: true,
					},
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('shapefileimporter-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					label.remove();
				},
				errorPlacement: function(error, element) {			
					error.appendTo($(element).siblings('.help-inline'));
				}
			});		
			

			$('#shapefileimporter-distributor-metadata').validate({
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
					$(element).closest('.control-group').find('.help-inline').addClass('shapefileimporter-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					label.remove();
				},
				errorPlacement: function(error, element) {			
					error.appendTo($(element).siblings('.help-inline'));
				}
			});	
			
			$('#shapefileimporter-provider-metadata').validate({
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
					$(element).closest('.control-group').find('.help-inline').addClass('shapefileimporter-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					label.remove();
				},
				errorPlacement: function(error, element) {			
					error.appendTo($(element).siblings('.help-inline'));
				}
			});
			
			$('#shapefileimporter-form-data').validate({
				rules: {
					layerName: {
						minlength: 2,
					    required : true
					},
					geocodeSystem:{
						required: true
					},
					browseFiles:{
						extension: "zip",
						required: true
					}
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('shapefileimporter-color-red');
					
					if($(element).attr('id') === 'shapefileimporter-browsefiles-button'){
						$(element).closest('.control-group').find('#shapefileimporter-patch-browsefiles').css("border-color","red");		
					}					
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					if($(element).attr('id') === 'shapefileimporter-browsefiles-button'){
						$(element).closest('.control-group').find('#shapefileimporter-patch-browsefiles').css("border-color","#DDD");		
					}
					label.remove();
				},		
				errorPlacement: function(error, element) {	
					if(element.attr('id') === 'shapefileimporter-browsefiles-button'){
						error.appendTo(element.closest('.control-group').find('.help-inline'));	
						element.closest('.control-group').find('#shapefileimporter-patch-browsefiles').addClass('error');			
					}else{
						error.appendTo($(element).siblings('.help-inline'));
					}
				}
			});		
			
			
			jQuery.extend(jQuery.validator.messages, {
				extension: "Please upload a file with .zip extension."
			});
			
		});		
		
		// Clear contents with cancel
		
		$(document).ready(function() {
			$('#shapefileimporter-cancel , #shapefileimporter-close-button').on('click',function (e){ 
				$('#shapefileimporter-author-metadata')[0].reset();
				$('#shapefileimporter-distributor-metadata')[0].reset();
				$('#shapefileimporter-provider-metadata')[0].reset();
				$('#shapefileimporter-form-data')[0].reset();
				$('#shapefileimporter-form-description')[0].reset();
				
				$("#shapefileimporter-container").find("*").removeClass("shapefileimporter-color-red error success");
				$('.help-inline > label').remove();
				
				window.noty.closeAllNotys();
				
				$('#shapefileimporter-patch-browsefiles').css("border-color", "gray");
				$('#shapefileimporter-tagsinput_tagsinput').find(".tag").remove();
			});
		});	
				
		$(document).ready(function() {
			
			// Load Template Layers
			
			var notificator = $("#shapefileimporter-notificator");
			
			
			$('#shapefileimporter-import-button').on('click',function (e){				
				
				var validDescription = $('#shapefileimporter-form-description').valid();
				var validAuthorMetadata = $('#shapefileimporter-author-metadata').valid();
				var validDistributorMetadata = $('#shapefileimporter-distributor-metadata').valid();
				var validProviderMetadata = $('#shapefileimporter-provider-metadata').valid();
				var validData = $('#shapefileimporter-form-data').valid();
				
				if(validDescription && validAuthorMetadata && validDistributorMetadata && validProviderMetadata && validData){				
					var spans = $('#shapefileimporter-tagsinput_tagsinput').find(".tag > span");
					var tags = []
					for(var i=0; i < spans.length; i++){
						spans[i] = spans[i].innerHTML.split("&nbsp;").join("");
						tags.push(spans[i]);
					}
					
					var file = document.getElementById('shapefileimporter-browsefiles-button').files[0];
					
					$('#shapefileimporter-layername').val($('#shapefileimporter-layername').val().replace(/ /g,"_"));
					 
					var importFormData = new FormData();
					importFormData.append("shapefileImportFile", file);					
					importFormData.append("shapefileImportProperties", new Object([JSON.stringify({
						"newLayerName"		: 	$('#shapefileimporter-layername').val()
					})], {
						type: "application/json"
					}));
					
					importFormData.append("shapefileImportMetadata", new Object([JSON.stringify({
						"abstractField"		: 	$('#shapefileimporter-abstract').val().trim(),
						"purpose"			:	$('#shapefileimporter-purpose').val(),
						"keywords"			: 	tags,
										
						"limitation"		:	$('#shapefileimporter-limitation').val(),
						
						"user"				:	$('#shapefileimporter-author-organisationname').val(),
						"title"				:	$('#shapefileimporter-layername').val(),
								
						"distributorOrganisationName":  $('#shapefileimporter-distributor-organisationname').val(),					
						"distributorIndividualName"	:	$('#shapefileimporter-distributor-individualname').val(),
						"distributorOnlineResource"	:	$('#shapefileimporter-distributor-onlineresource').val(),
						
						"providerOrganisationName"	:  	$('#shapefileimporter-provider-organisationname').val(),					
						"providerIndividualName"	:	$('#shapefileimporter-provider-individualname').val(),
						"providerOnlineResource"	:	$('#shapefileimporter-provider-onlineresource').val()
					})], {
						type: "application/json"
					}));				
				
					
					
 					$.ajax({
 					    type : "POST", 					    
 					    url : importShapefileURL, 					    
 					    processData : false, 					   
 		                contentType : false, 
 					    data: importFormData, 	
 					    beforeSend : function() {
 					    	$('#shapefileimporter-container .spinner').show();
 					    },
 					    success: function(data){
 							window.noty.showNoty(notificator, data, "success");	
 					    }, 					    
 					    error: function(jqXHR, exception){ 	
 					    	var responseText = jQuery.parseJSON(jqXHR.responseText);
 					        console.log(responseText);
 					    	window.noty.errorHandlingNoty(notificator, jqXHR, exception); 
 					    	
					    },
					    complete: function() {
					    	$('#shapefileimporter-container .spinner').hide();
					    }
 					});				    
				}				
			}); 			 
		});
	},
	destroy: function()	{
		$(this.options.content).children().remove();
		$(this.options.headerDiv + ' div:not(.dropDownSelection').remove();
	}
});