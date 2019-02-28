(function(){
	'use strict';
	
	var dom = window.dom;
	var notificator = window.notificator;
	
	$('#analysisForm').validate({
		rules: {
			model: {
				required: true
			},
			maturityTime : {
				step: false,
				required: true
			},
			discountRate:{
				step: false,
				required: true
			},
			taxRate:{
				step: false,
				required: true
			},
			inflationRate:{
				step: false,
				required: true
			},
			feedPrice: {
				step: false,
				required : true,
				onfocusout: false
			},
			fryPrice:{			
				step: false,
				required: true
			},
			fishPrice:{			
				step: false,
				required: true
			}
		},
		highlight: function (element) {
			$(element).closest('.control-group').removeClass('success').addClass('error');
			$(element).siblings(".label-tooltip").removeClass("fa-info-circle");
			$(element).siblings(".label-tooltip").addClass("fa-times-circle-o");
		},
		success: function (label, element) {
			$(element).closest('.control-group').removeClass('error');
			$(element).siblings(".label-tooltip").removeClass("fa-info-circle");
			$(element).siblings(".label-tooltip").addClass("fa-info-circle");
			$(element).siblings(".label-tooltip").removeClass("fa-times-circle-o");
			
			dom.enableDefaultTooltip($(element).attr("id"));

			label.remove();
		},
		errorPlacement: function(error, element) {			
			notificator.createTooltip($(element).siblings(".label-tooltip"), error, "i", "error");				
		}	
	});	
	
	function validateAnalysisForm(){
		var valid = $("#analysisForm").validate().checkForm();	
		dom.performButton.prop('disabled', !valid);		
	}	
	
	var validateAll = function(teaInputField){
		$(teaInputField).valid();
		
		validateAnalysisForm();
		
		dom.resetButton.show();
		
		if(window.currentAnalysis != null){
			notificator.closeAllNoty();
			dom.moveWorkspaceButtonsToSides();
		}		
	}
	
	$('#tea_production_model').on('change', function () {	
		validateAll(this);
	});
	
 	$('.tea_input_percent, .tea_input_currency').on('input', function () {	
		validateAll(this);
	}); 	
})();	