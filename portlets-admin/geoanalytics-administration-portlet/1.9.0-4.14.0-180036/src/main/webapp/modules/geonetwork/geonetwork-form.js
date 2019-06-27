var geoNetworkModule = (function() {

	var loadCSS = function() {
		$("<link/>", {
			rel : "stylesheet",
			type : "text/css",
			href : window.config.contextPath + "modules/geonetwork/geonetwork-form.css"
		}).appendTo("head");
	};

	var init = function() {
		loadCSS();
	}

	var loadGeoNetworkFormOnDiv = function(element, completeCallback) {
		$(element).load(window.config.contextPath + "modules/geonetwork/geonetwork-form.jsp", completeCallback);
	};

	var validateMetadata = function(element) {
		$(element).find('.geonetwork-publisher-metadata-form').validate({
			rules : {
				purpose : {
					required : true,
				},
				limitation : {
					required : true,
				},
				authorOrganisationName : {
					required : true,
				},
				distributorOrganisationName : {
					required : true,
				},
				providerOrganisationName : {
					required : true,
				},
				distributorIndividualName : {
					required : true,
				},
				providerIndividualName : {
					required : true,
				},
				distributorOnlineResource : {
					required : true,
					url : true
				},
				providerOnlineResource : {
					required : true,
					url : true
				}
			},
			highlight : function(element) {
				$(element).closest('.control-group').removeClass('success').addClass('error');
				$(element).closest('.control-group').find('.help-inline').addClass('validation-error-color-red');
			},
			success : function(label, element) {
				$(element).closest('.control-group').removeClass('error');
				label.remove();
			},
			errorPlacement : function(error, element) {
				error.appendTo($(element).siblings('.help-inline'));
			}
		});

		$(element).find(".geonetwork-publisher-metadata-form textarea").on('input', function() {
			$(this).valid();
		});
	};
	
	var validateForm = function(){
		return this.form.valid();
	};
	
	var toggleButtonOnValidation = function(button) {
		var form = this.form;
		form.find("textarea").on("input", function() {
			var valid = form.validate().checkForm();
			$(button).prop('disabled', !valid);
		});
	}

	var getPurpose = function() {
		return this.element.find("#geonetwork-publisher-general-purpose").val();
	}

	var getLimitation = function() {
		return this.element.find("#geonetwork-publisher-general-limitation").val();
	}

	var getAuthorMetadata = function() {
		var element = this.element;

		return {
			organisationName : element.find("#geonetwork-publisher-author-organisation-name").val()
		};
	}

	var getDistributorMetadata = function() {
		var element = this.element;

		return {
			organisationName : element.find("#geonetwork-publisher-distributor-organisation-name").val(),
			individualName : element.find("#geonetwork-publisher-distributor-individual-name").val(),
			onlineResource : element.find("#geonetwork-publisher-distributor-online-resource").val()
		};
	};

	var getProviderMetadata = function() {
		var element = this.element;

		return {
			organisationName : element.find("#geonetwork-publisher-provider-organisation-name").val(),
			individualName : element.find("#geonetwork-publisher-provider-individual-name").val(),
			onlineResource : element.find("#geonetwork-publisher-provider-online-resource").val()
		};
	};

	var getGeoNetworkMetadata = function() {
		return {
			purpose : this.getPurpose(),
			limitation : this.getLimitation(),
			author : this.getAuthorMetadata(),
			distributor : this.getDistributorMetadata(),
			provider : this.getProviderMetadata()
		};
	}

	var publishLayerOnGeoNetwork = function(layer, successCallback, errorCallback) {
		var geoNetworkMetadataDTO = this.getGeoNetworkMetadata();
		geoNetworkMetadataDTO.title = layer.name;
		geoNetworkMetadataDTO.keywords = layer.tags;
		geoNetworkMetadataDTO.description = layer.description;

		var data = JSON.stringify({
			geoNetworkMetadataDTO : geoNetworkMetadataDTO,
			layerId : layer.id
		});

		var url = window.config.createResourceURL("layers/publishLayerOnGeoNetwork");

		$.ajax({
			type : "POST",
			url : url,
			data : data,
			contentType : "application/json",
			beforeSend : function() {
				window.config.showSpinner();
			},
			success : function(response) {
				successCallback && successCallback(response);
			},
			error : function(jqXHR, exception) {
				errorCallback && errorCallback(jqXHR, exception);
			},
			complete : function() {
				window.config.hideSpinner();
			}
		});
	};

	var unpublishLayerFromGeoNetwork = function(layer, successCallback, errorCallback) {
		var data = layer.id;

		var url = window.config.createResourceURL("layers/unpublishLayerFromGeoNetwork");

		$.ajax({
			type : "POST",
			url : url,
			data : data,
			contentType : "application/json",
			beforeSend : function() {
				window.config.showSpinner();
			},
			success : function(response) {
				successCallback && successCallback(response);
			},
			error : function(jqXHR, exception) {
				errorCallback && errorCallback(jqXHR, exception);
			},
			complete : function() {
				window.config.hideSpinner();
			}
		});
	};

	var createGeoNetworkForm = function(completeCallback) {
		var instance = this;

		var wrapCompleteCallback = function() {
			validateMetadata(instance.element);
			instance.form = instance.element.find('.geonetwork-publisher-metadata-form');
			instance.fields = instance.form.find("textarea");
			completeCallback && completeCallback();			
		};

		loadGeoNetworkFormOnDiv(instance.element, wrapCompleteCallback);
	};
	
	var resetGeoNetworkForm = function(){
		this.form[0].reset();
		this.form.find("*").removeClass("shapefile-importer-color-red error success");
		this.form.find('.help-inline > label').remove();
	}
	
	var showGeoNetworkForm = function(){
		this.form.show();
	};
	
	var hideGeoNetworkForm = function(){
		this.form.hide();
	};
	
	var enableGeoNetworkForm = function(){
		this.fields.attr("disabled", false);
	};
	
	var disableGeoNetworkForm = function(){
		this.fields.attr("disabled", true);
		this.resetGeoNetworkForm();
	};
	
	var createInstance = function(element) {
		return {
			element : $(element),
			form : null,
			fields : null,

			getPurpose : getPurpose,
			getLimitation : getLimitation,
			getAuthorMetadata : getAuthorMetadata,
			getDistributorMetadata : getDistributorMetadata,
			getProviderMetadata : getProviderMetadata,
			getGeoNetworkMetadata : getGeoNetworkMetadata,

			publishLayerOnGeoNetwork : publishLayerOnGeoNetwork,
			unpublishLayerFromGeoNetwork : unpublishLayerFromGeoNetwork,

			createGeoNetworkForm : createGeoNetworkForm,
			resetGeoNetworkForm : resetGeoNetworkForm,
			validateForm : validateForm,

			showGeoNetworkForm : showGeoNetworkForm,
			hideGeoNetworkForm : hideGeoNetworkForm,

			enableGeoNetworkForm : enableGeoNetworkForm,
			disableGeoNetworkForm : disableGeoNetworkForm,

			toggleButtonOnValidation : toggleButtonOnValidation
		};
	};

	return {
		init : init,
		createInstance : createInstance
	};

})();