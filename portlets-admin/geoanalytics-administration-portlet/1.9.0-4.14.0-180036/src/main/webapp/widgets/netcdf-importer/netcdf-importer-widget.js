$.widget('cite.netcdfImporter', {
    notificator : $("#netcdf-importer-notificator"),
	options:{
		mode: "div"	,
		importNetcdfURL: "",
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
		var netcdfimporter = $('#netcdfimporter');
		$(netcdfimporter).css("display", "inline-block");

		var showButton = 	'<button id="netcdf-importer-show-button"  '+
										'type="button"   '+
										'class="btn btn-large"   '+
										'data-toggle="modal"   '+
										'data-target="#netcdf-importer-container">	  '+
								'<i class="fa fa-upload" ></i> netcdf Importer	  '+
							'</button>  ';
		showButton = $(showButton);
		netcdfimporter.append(showButton);
	},
	_createNonModal : function() {
		var container = `<div id="netcdf-importer-container"> `;
		this._container = $(container);
		this._container.appendTo(this.options.content);
	},
	_createModal : function() {
		var container = '<div id="netcdf-importer-container" ' +
							'class="modal fade in netcdf-importer-container-button"' +
							'tabindex="-1" '+
							'role="dialog"	'+
							'aria-labelledby="netcdfimporter"  '+
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
			closeButton = '<button id="netcdf-importer-close-button" type="button" data-dismiss="modal" aria-hidden="true"></button>';
			header = '<div id="netcdf-importer-modal-header" class="modal-header">' +
				closeButton +
				'<h5 id="netcdf-importer-label-modal">netcdf Importer</h5>' +
			'</div>';
			body = '<div id="netcdf-importer-modal-body" class="modal-body scrollable" >';
			scrollableRow = " scrollable";
		}else{
			body = '<div id="netcdf-importer-modal-body" >';
		}

		var importerBody = header + body + 	'<input type="button" id="netcdf-importer-toggle-geonetwork-metadata" />' +
		'<div class="row">' +
			'<div class="span6" id="netcdf-importer-general-form-container">' +
				'<div class="spinner" style="display: none"></div>' +
				'<span class="headerDescription">GENERAL</span>	' +
				'<div id="netcdf-importer-notificator"></div>	' +
				'<hr>	' +
				'<form class="form-horizontal" id="netcdf-importer-general-form">	' +
					'<div class="control-group row">		' +
						'<div class="span4">' +
							'<label>Data Location<span class="makeMeOrange">*</span></label>' +
						'</div>		' +

						'<div class="span6">		' +
							'<input id="netcdf-importer-selected-file" class="span11"  type="text" placeholder="No file selected" readonly> 	' +
							'<br>' +
							'<span class="help-inline"></span>' +
						'</div>				' +
						'<div class="controls span2" id="netcdf-importer-patch-browsefiles">	' +
							'<button class="span12">Browse</button>' +
							'<input id="netcdf-importer-browsefiles-button" name="browseFiles" type="file" >	' +
						'</div>	' +
					'</div>		' +
					'<div class="control-group row">' +
						'<div class="span4">' +
							'<label for="netcdf-importer-layername">Layer Name<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea  style="resize: none;" id="netcdf-importer-layername"  class="span12"  name="layerName"  placeholder="Please fill in your Layer Name" rows="1"></textarea>' +
							'<span class="help-inline"></span>' +
						'</div>' +
					'</div>' +
					'<div class="control-group row">' +
						'<div class="span4">' +
							'<label for=""netcdf-importer-style"">Style<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<select id="netcdf-importer-style" class="span12"  name="style" disabled>' +
								'<option  value="raster" selected>raster</option>' +
							'</select>' +
							'<span class="help-inline"></span>' +
						'</div>' +
					'</div>	' +

					'<div class="control-group row" id="netcdf-importer-general-description">' +
						'<div class="span4">' +
							'<label for="netcdf-importer-abstract">Layer description<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea name="abstractDescription" id="netcdf-importer-abstract" class="span12" placeholder="Please give a brief description of the layer" rows="5"></textarea>' +
						'</div>' +
					'</div>' +

					'<div class="control-group row" id="generalKeywords">' +
						'<div class="span4">' +
							'<label for="netcdf-importer-tagsinput">Layer keywords<span class="makeMeOrange">*</span></label>' +
						'</div>' +
						'<div class="span8">' +
							'<textarea  style="resize: none;" id="netcdf-importer-tagsinput" name="tagsInput" class="span12" placeholder="Please fill in the layer keywords" rows="1"></textarea>' +
						'</div>' +
					'</div>' +
				'</form>' +
			'</div>	' +

			'<div class="span6">' +
				'<div id="netcdf-importer-geonetwork-metadata"></div>' +
			'</div>' +
		'</div>'	;

		this._container.append($(importerBody));


		this._container.append('<div class="modal-footer">' +
									'<div id="netcdf-importer-div-submit" class="control-group pull-right">' +
										'<button type="button" data-dismiss="modal" id="netcdf-importer-cancel" class="btn" aria-hidden="true">Cancel</button>' +
										'<button type="button" id="netcdf-importer-import-button"  class="btn" >Import</button>' +
									'</div>' +
								'</div>');
	},
	_initializeFunctionality : function (){
		var importNetcdfURL = this.options.importNetcdfURL;
		var stylesURL = this.options.stylesURL ;
		var notificator = this.options.notificator;
		var toggleGeoNetworkMetadata = $("#netcdf-importer-toggle-geonetwork-metadata");
		var geoNetworkModule = window.config.geoNetworkModule.createInstance("#netcdf-importer-geonetwork-metadata");
		geoNetworkModule.createGeoNetworkForm();

		$(document).ready(function(){
			$('#netcdf-importer-tagsinput').tagsInput({
			   'defaultText':'Add a Tag',
			   'delimiter': [',',';',' '],
			   'minChars' : 2,
			   'maxChars' : 40,
			   'placeholderColor' : 'rgba(153, 153, 153, 0.65)'
			});

			$('#netcdf-importer-browsefiles-button').bind("change", function() {
				var fileName = $(this).val().split('\\').pop();
				$('#netcdf-importer-selected-file').val(fileName);
				var valid = $('#netcdf-importer-browsefiles-button').valid();

				if(valid){
					fileName += " has been selected";
					window.noty.showNoty($("#netcdf-importer-notificator"), fileName, "success", false);
				}else{
					window.noty.closeAllNotys();
				}
			});

		    $.validator.addMethod("validRegex", function(value, element) {
		        return this.optional(element) || /^[a-zA-Z][a-zA-Z0-9\_\s]+$/i.test(value);
		    }, "Field must start with a letter and contain only letters, numbers, or underscores.");

			$('#netcdf-importer-general-form').validate({
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
						extension: "zip",
						required: true
					}
				},
				highlight: function (element) {
					$(element).closest('.control-group').removeClass('success').addClass('error');
					$(element).closest('.control-group').find('.help-inline').addClass('netcdf-importer-color-red');

					if($(element).attr('id') === 'netcdf-importer-browsefiles-button'){
						$(element).closest('.control-group').find('#netcdf-importer-patch-browsefiles').css("border-color","red");
					}
				},
				success: function (label, element) {
					$(element).closest('.control-group').removeClass('error');
					if($(element).attr('id') === 'netcdf-importer-browsefiles-button'){
						$(element).closest('.control-group').find('#netcdf-importer-patch-browsefiles').css("border-color","#DDD");
					}
					label.remove();
				},
				errorPlacement: function(error, element) {
					if(element.attr('id') === 'netcdf-importer-browsefiles-button'){
						error.appendTo(element.closest('.control-group').find('.help-inline'));
						element.closest('.control-group').find('#netcdf-importer-patch-browsefiles').addClass('error');
					}else{
						error.appendTo($(element).siblings('.help-inline'));
					}
				}
			});

			jQuery.extend(jQuery.validator.messages, {
				extension: "Please upload a file with .zip extension."
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
			$('#netcdf-importercancel , #netcdf-importerclose-button').on('click',function (e){
				$('#netcdf-importergeneral-form')[0].reset();
				geoNetworkModule.resetGeoNetworkForm();

				$("#netcdf-importercontainer").find("*").removeClass("netcdf-importercolor-red error success");
				$('.help-inline > label').remove();

				window.noty.closeAllNotys();

				$('#netcdf-importerpatch-browsefiles').css("border-color", "gray");
				$('#netcdf-importertagsinput_tagsinput').find(".tag").remove();
			});
		});

		$(document).ready(function() {

			var notificator = $("#netcdf-importer-notificator");

			$('#netcdf-importer-import-button').on('click',function (e){
				var validData = $('#netcdf-importer-general-form').valid();
				var validMetadata = geoNetworkModule.validateForm();
				var publishOnGeoNetwork = toggleGeoNetworkMetadata.styledCheckbox("isChecked");

				if(validData && ((publishOnGeoNetwork && validMetadata) || !publishOnGeoNetwork)){
					var spans = $('#netcdf-importer-container .tagsinput').find(".tag > span");
					var tags = [];
					for(var i=0; i < spans.length; i++){
						spans[i] = spans[i].innerHTML.split("&nbsp;").join("");
//						tags.push(spans[i]);
						tags.push( encodeURI( spans[i]) );
					}

					var file = document.getElementById('netcdf-importer-browsefiles-button').files[0];

					var importFormData = new FormData();
					importFormData.append("netcdfImportFile", file);
					importFormData.append("NetCDFImportProperties", new Object([JSON.stringify({
						layerName		: 	encodeURI( $('#netcdf-importer-layername').val() ),
						description		: 	encodeURI( $('#netcdf-importer-abstract').val().trim() ),
						style			:   $('#netcdf-importer-style option:selected').text(),
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

						importFormData.append("NetcdfImportMetadata", new Object([JSON.stringify({
							title			:	encodeURI( $('#netcdf-importer-layername').val() ),
							description		: 	encodeURI( $('#netcdf-importer-abstract').val().trim() ),
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
 					    url : importNetcdfURL,
 					    processData : false,
 		                contentType : false,
 					    data: importFormData,
 					    beforeSend : function() {
 					    	$('#netcdf-importer-container .spinner').show();
 					    },
 					    success: function(data){
 							window.noty.showNoty(notificator, data, "success");
 					    },
 					    error: function(jqXHR, exception){
 					    	window.noty.errorHandlingNoty(notificator, jqXHR, exception);
					    },
					    complete: function() {
					    	$('#netcdf-importer-container .spinner').hide();
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