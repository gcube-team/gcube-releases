(function(){
	'use strict';
	
	var dom = window.dom;
	
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
			
			dom.enableDefaultToolTip($(element).attr("id"));

			label.remove();
		},
		errorPlacement: function(error, element) {			
			dom.createToolTip($(element).siblings(".label-tooltip"), error, "i");				
		}	
	});	
	
	function validateAnalysisForm(){
		var valid = $("#analysisForm").validate().checkForm();	
		dom.performButton.prop('disabled', !valid);		
	}	
	
	$('#tea_production_model').on('change', function () {	
		$(this).valid();
		
		validateAnalysisForm();
		
		dom.resetButton.show();
		
		if(window.currentAnalysis != null){
			window.noty.closeAllNoty();
			window.dom.moveWorkspaceButtonsToDefault();
		}
	});
	
 	$('.tea_input_percent, .tea_input_currency').on('input', function () {	
		$(this).valid();
		
		validateAnalysisForm();
		
		dom.resetButton.show();
		
		if(window.currentAnalysis != null){
			window.noty.closeAllNoty();
			window.dom.moveWorkspaceButtonsToDefault();
		}
	}); 	
})();	