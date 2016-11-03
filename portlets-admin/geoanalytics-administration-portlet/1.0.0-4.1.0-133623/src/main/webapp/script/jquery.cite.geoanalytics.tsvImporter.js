$.widget('cite.tsvimporter', {	
	options:{
		mode: "div"	,
		templateLayersURL: "",
		importTsvURL: ""
	},

	_create : function () {		
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
		var importTsvURL = this.options.importTsvURL;
		var templateLayersURL = this.options.templateLayersURL ;
		this._initializeFunctionality(importTsvURL, templateLayersURL);
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
		var container = `<div id="tsvimporter-container" class="tsvimporter-container-div"> `;
		this._container = $(container);		
		this._container.appendTo(this.element);
		this._createImporter();		
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
		this._createImporter();
	},
	
	_createImporter : function() {
		var closeButton = "";
		var header = "" ;
		if(this.options.mode === "button"){
			closeButton = `<button id="tsvimporter-close-button" type="button" data-dismiss="modal" aria-hidden="true"></button>`;
			header = `<div id="tsvimporter-modal-header" class="modal-header">` +
				closeButton +
				`<h5 id="tsvimporter-label-modal">TSV Importer</h5> 
			</div>`;	
		}
			
		var importerBody = header + 			
			`<div id="tsvimporter-modal-body" class="modal-body" >
			
				<div class="row-fluid tsvimporter-row">
					
					<h5>
						<span class="numberCircle"><span>1</span></span>
						Layer Description
					</h5>
					
					<form class="form-horizontal" id="tsvimporter-form-description">
					
						<div class="control-group" id="tsvimporter-div-abstract">
							<label class="control-label">Abstract</label>
							<div class="controls">
								<textarea  class="span11" 
											name="abstractDescription" 
											rows="4" 
											id="tsvimporter-abstract" 
											placeholder="Please give a brief description of the layer"
								></textarea>
							</div>							
						</div>
						
						<div class="control-group" id="tsvimporter-div-purpose">
							<label class="control-label">Purpose</label>
							<div class="controls" >
								<textarea 	class="span11" 
											name="purpose" 
											rows="4" 
											id="tsvimporter-purpose" 
											placeholder="Please give a brief description of the layer's purpose"
								></textarea>
							</div>
						</div>
						
						<div class="control-group ">
							<label class="control-label" for="tsvimporter-tagsinput" >Descriptive Keywords</label>
							<div class="controls ">
								<div class="span11">
									<input class="tsvimporter-container-input"  name="tagsInput"  id="tsvimporter-tagsinput"  placeholder="Please add descriptive tags">
								</div>
							</div>
						</div>							
						
					</form>
				</div>
				
				<hr>	
			
				<div class="row-fluid tsvimporter-row">
					<h5>
						<span class="numberCircle"><span>2</span></span>						
						Layer Metadata
					</h5>
		
					<form class="form-horizontal" id="tsvimporter-form-metadata" >
						
						<div class="control-group">
							<label class="control-label">Limitation</label>
							<div class="controls">
								<div class="span11">
									<input  type="text" 
											class="tsvimporter-container-input" 
											name="limitation" 
											id="tsvimporter-limitation" 
											placeholder="Please fill in your Limitation">
									<span class="help-inline"></span>
								</div>
																
							</div>
						</div>
					
						<div class="control-group">
							<label class="control-label" >Graphic Overview</label>
							<div class="controls">
								<div class="span11">
									<input 	type="text" 
											id="tsvimporter-graphicoverview"
											class="tsvimporter-container-input"  
											name="graphicOverview" 
											value="http://" 
											placeholder="Please fill in your desired URL">
									<span class="help-inline"></span>
								</div>
								
							</div>
						</div>
				
					</form>
				</div>
						
				<hr>		

				<div class="row-fluid tsvimporter-row">
					<h5>
						<span class="numberCircle"><span>3</span></span>						
						Author's Metadata
					</h5>
		
					<form class="form-horizontal" id="tsvimporter-author-metadata" >						
						<div class="control-group">
							<label class="control-label">Organisation Name</label>
							<div class="controls">
								<div class="span11">
									<input  type="text" 
											class="tsvimporter-container-input" 
											name="organisationName" 
											id="tsvimporter-author-organisationname" 
											placeholder="Please fill in your Limitation">
									<span class="help-inline"></span>
								</div>																
							</div>
						</div>						
					</form>
				</div>
						
				<hr>		
		
				<div class="row-fluid tsvimporter-row">
					<h5>
						<span class="numberCircle"><span>4</span></span>						
						Distributor Metadata
					</h5>
		
					<form class="form-horizontal" id="tsvimporter-distributor-metadata" >						
						<div class="control-group">
							<label class="control-label">Organisation Name</label>
							<div class="controls">
								<div class="span11">
									<input  type="text" 
											class="tsvimporter-container-input" 
											name="organisationName" 
											id="tsvimporter-distributor-organisationname" 
											placeholder="Please fill in your Limitation">
									<span class="help-inline"></span>
								</div>																
							</div>
						</div>	
						<div class="control-group">
							<label class="control-label">Individual Name</label>
							<div class="controls">
								<div class="span11">
									<input  type="text" 
											class="tsvimporter-container-input" 
											name="individualName" 
											id="tsvimporter-distributor-individualname" 
											placeholder="Fill in your Individual Name">
									<span class="help-inline"></span>
								</div>
								
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">Online Resource</label>
							<div class="controls">
								<div class="span11">
									<input  type="text" 
											class="tsvimporter-container-input"
											name="onlineResource" 
											id="tsvimporter-distributor-onlineresource" 
											value="http://" placeholder="Fill in your Online Resource URL">
									<span class="help-inline"></span>
								</div>
								
							</div>
						</div>
						
					</form>
				</div>
						
				<hr>	
				
				<div class="row-fluid tsvimporter-row">
					<h5>
						<span class="numberCircle"><span>5</span></span>						
						Resource Provider Metadata
					</h5>
		
					<form class="form-horizontal" id="tsvimporter-provider-metadata" >
						<div class="control-group">
							<label class="control-label">Organisation Name</label>
							<div class="controls">
								<div class="span11">
									<input  type="text" 
											class="tsvimporter-container-input" 
											name="organisationName" 
											id="tsvimporter-provider-organisationname" 
											placeholder="Please fill in your Limitation">
									<span class="help-inline"></span>
								</div>																
							</div>
						</div>	
						<div class="control-group">
							<label class="control-label">Individual Name</label>
							<div class="controls">
								<div class="span11">
									<input  type="text" 
											class="tsvimporter-container-input" 
											name="individualName" 
											id="tsvimporter-provider-individualname" 
											placeholder="Fill in your Individual Name">
									<span class="help-inline"></span>
								</div>
								
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">Online Resource</label>
							<div class="controls">
								<div class="span11">
									<input  type="text" 
											class="tsvimporter-container-input"
											name="onlineResource" 
											id="tsvimporter-provider-onlineresource" 
											value="http://" placeholder="Fill in your Online Resource URL">
									<span class="help-inline"></span>
								</div>
								
							</div>
						</div>
						
					</form>
				</div>
						
				<hr>
				
				<div class="row-fluid tsvimporter-row">			
				
					<h5>
						<span class="numberCircle"><span>6</span></span>	
						Layer Data
					</h5>						
						
					<form class="form-horizontal" id="tsvimporter-form-data">
					
						<div class="control-group">
							<label class="control-label">Layer Name</label>
							<div class="controls">
								<div class="span11">
									<input 	id="tsvimporter-layername" 
											class="tsvimporter-container-input" 
											name="layerName" 
											type="text" placeholder="Please fill in your Layer Name"
											autocomplete="off">
									<span class="help-inline"></span>
								</div>
																
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label">Template Layer</label>
							<div class="controls">
								<div class="span11">
									<select id="tsvimporter-layertemplate" class="tsvimporter-container-input"  name="templateLayer">
										  <option  value="" disabled selected>Choose a Template Layer</option>										  
									</select>
									<span class="help-inline"></span>
								</div>
																
							</div>
						</div>		
					  
						<div class="control-group">	
							<label class="control-label" >TSV Data</label>							
							<div class="controls ">								
								<div class="span8 input-append">
									<input 	id="tsvimporter-selected-file" 
											class="tsvimporter-container-input" 
											type="text" 
											placeholder="Upload a TSV file" readonly>		
								
									
										<div id="tsvimporter-patch-browsefiles" class="btn btn-default">
											<span>Browse File</span>
											<input id="tsvimporter-browsefiles-button" name="browseFiles" type="file" >
										</div>
									
									<br>
									<span class="help-inline"></span>
								</div>

							</div>
						</div> 
						
					</form>
				</div>					
			</div> 	
			
			<div class="modal-footer">
				<div id="tsvimporter-div-submit" class="control-group pull-right">
					<a href="#" data-dismiss="modal" id="tsvimporter-cancel" aria-hidden="true">cancel</a>
					<button type="button" id="tsvimporter-import-button"  class="btn btn-large btn-warning" >Import</button>
				</div>				
			</div>	
		`;	
		
		importerBody = $(importerBody);
		this._container.append(importerBody);
	},
	_initializeFunctionality : function (importTsvURL, templateLayersURL){
		$(document).ready(function(){				
			$('#tsvimporter-tagsinput').tagsInput({
			   'defaultText':'Add a Tag',
			   'delimiter': [',',';',' '],   
			   'minChars' : 2,
			   'maxChars' : 40,
			   'placeholderColor' : 'gray'
			});	
			$('#tsvimporter-browsefiles-button').bind("change", function() {
				var fileName = $(this).val().split('\\').pop();
				$('#tsvimporter-selected-file').val(fileName);
			});
				
			$('#tsvimporter-form-description').validate({
				rules: {
					abstractDescription: {
						minlength: 5,
						required: true
					},
					purpose:{
						minlength: 5,
						required: true
					}
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('tsvimporter-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error').addClass('success');
					label.remove();
				},
				errorPlacement: function(error, element) {	} 
			});		
			
			
			$('#tsvimporter-form-metadata ').validate({
				rules: {
					limitation: {
						minlength: 2,
						required: true
					},
					graphicOverview:{			
						required: false,
						url:true
					}
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('tsvimporter-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error').addClass('success');
					label.remove();
				},

				errorPlacement: function(error, element) {			
					error.appendTo($(element).siblings('.help-inline'));
				}
			});
			
			$('#tsvimporter-author-metadata').validate({
				rules: {
					organisationName:{			
						required: true,
						minlength: 2
					}
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('tsvimporter-color-red');
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error').addClass('success');
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
						minlength: 2
					},
					individualName:{			
						required: true,
						minlength: 2
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
					$(element).closest('.control-group').removeClass('error').addClass('success');
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
						minlength: 2
					},
					individualName:{			
						required: true,
						minlength: 2
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
					$(element).closest('.control-group').removeClass('error').addClass('success');
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
					    required: {
					        depends:function(){
					            $(this).val($(this).val().replace(/ /g,"_"));
					            return true;
					        }
					    }
					},
					templateLayer:{
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
						$(element).closest('.controls').find('#tsvimporter-patch-browsefiles').css("border-color","red");		
					}
					
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error').addClass('success');
					if($(element).attr('id') === 'tsvimporter-browsefiles-button'){
						$(element).closest('.controls').find('#tsvimporter-patch-browsefiles').css("border-color","green");		
					}
					label.remove();
				},		
				errorPlacement: function(error, element) {	
					if(element.attr('id') === 'tsvimporter-browsefiles-button'){
						error.appendTo(element.closest('.controls').find('.help-inline'));	
						element.closest('.controls').find('#tsvimporter-patch-browsefiles').addClass('error');			
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
				$('#tsvimporter-form-metadata')[0].reset();
				$('#tsvimporter-author-metadata')[0].reset();
				$('#tsvimporter-distributor-metadata')[0].reset();
				$('#tsvimporter-provider-metadata')[0].reset();
				$('#tsvimporter-form-data')[0].reset();
				$('#tsvimporter-form-description')[0].reset();				
				$('#tsvimporter-container').find('*').removeClass('tsvimporter-color-red error success');
				$('#tsvimporter-container .help-inline > label').remove();
				$('#tsvimporter-patch-browsefiles').css("border-color", "gray");
				$('#tsvimporter-tagsinput_tagsinput').find(".tag").remove();
			});
		});
		
		// Load Template Layers
		
		$(document).ready(function() {	
			$.ajax({			    
			    url : templateLayersURL, 					    
		        dataType:'json',
		        type:'GET',
			    success: function(data){
			    	var selectBox = $('#tsvimporter-layertemplate');
			        for(var i=0; i < data.length ; i++){
			        	selectBox.append("<option>" + data[i] + "</option>");
			        }
			    }, 					    
			    error: function(data){
			        alert("Error. Cannot load Template Layers");
			    }
			});			
		});
		
		$(document).ready(function() {			
			$('#tsvimporter-import-button').on('click',function (e){				
				$('#tsvimporter-abstract').val("Test Abstract");
				$('#tsvimporter-purpose').val("Test purpose");				
									
				$('#tsvimporter-limitation').val("Test limitation");
				$('#tsvimporter-graphicoverview').val("http://TestGraphicOverview.com");
				
				$('#tsvimporter-author-organisationname').val("Test User");					

				$('#tsvimporter-distributor-organisationname').val("Test Organisation Name");					
				$('#tsvimporter-distributor-individualname').val("Test Individual Name");
				$('#tsvimporter-distributor-onlineresource').val("http://TestOnlineResource.com");
				
				$('#tsvimporter-provider-organisationname').val("Test OrganisationName");					
				$('#tsvimporter-provider-individualname').val("Test Individual Name");
				$('#tsvimporter-provider-onlineresource').val("http://TestOnlineResource.com");
				
				//$('#tsvimporter-layername').val("TSV_Test_Layer_" + new Date());				
				$('#tsvimporter-layertemplate').val("NUTS_Layer_Test");
				
				var testtags = $('#tsvimporter-tagsinput_addTag');
				for(var i=1; i < 3; i++){
					var tagName = "tag" + i;
					var tag = $('<span class="tag"><span>'+tagName + '&nbsp;&nbsp;</span><a href="#" title="Removing tag">x</a></span>');
					testtags.append(tag);
				}
				
				var validDescription = $('#tsvimporter-form-description').valid();
				var validMetadata = $('#tsvimporter-form-metadata').valid();
				var validAuthorMetadata = $('#tsvimporter-author-metadata').valid();
				var validDistributorMetadata = $('#tsvimporter-distributor-metadata').valid();
				var validProviderMetadata = $('#tsvimporter-provider-metadata').valid();
				var validData = $('#tsvimporter-form-data').valid();
				
				if(validDescription && validMetadata && validAuthorMetadata && validDistributorMetadata && validProviderMetadata && validData){				
					var spans = $('#tsvimporter-tagsinput_tagsinput').find(".tag > span");
					var tags = []
					for(var i=0; i < spans.length; i++){
						spans[i] = spans[i].innerHTML.split("&nbsp;").join("");
						tags.push(spans[i]);
					}
					
					var file = document.getElementById('tsvimporter-browsefiles-button').files[0];
					
					//$('#tsvimporter-layername').val($('#tsvimporter-layername').val().replace(/ /g,"_"));
					 
					var importFormData = new FormData();
					importFormData.append("tsvImportFile", file);					
					importFormData.append("tsvImportProperties", new Object([JSON.stringify({
						"newLayerName"		: 	$('#tsvimporter-layername').val(),
						"templateLayerName"	:	$('#tsvimporter-layertemplate').val()
					})], {
						type: "application/json"
					}));
					
					importFormData.append("tsvImportMetadata", new Object([JSON.stringify({
						"abstractField"		: 	$('#tsvimporter-abstract').val().trim(),
						"purpose"			:	$('#tsvimporter-purpose').val(),
						"keywords"			: 	tags,
										
						"limitation"		:	$('#tsvimporter-limitation').val(),
						"graphicOverview"	:	$('#tsvimporter-graphicoverview').val(),
						
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
 					    dataType: 'text', 					    
 					    success: function(data){
 					        alert("Layer has been Imported Successfully!");
 					        $("#tsvimporter-container").modal('toggle');
 					    }, 					    
 					    error: function(data){
					        alert("Layer failed to import.");
					        $("#tsvimporter-container").modal('toggle');
					    }
 					});				    
				}				
			}); 			 
		})
	}
});