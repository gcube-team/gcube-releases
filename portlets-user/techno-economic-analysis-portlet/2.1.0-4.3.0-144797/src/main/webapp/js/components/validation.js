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
			$(element).closest('.control-group').find('.help-inline').addClass('tea-color-red');
		},
		success: function (label, element) {
			$(element).closest('.control-group').removeClass('error');
			label.remove();
		},
		errorPlacement: function(error, element) {			
			error.appendTo($(element).siblings('.help-inline'));
		}	
	});	
	
	function validateAnalysisForm(){
		var valid = $("#analysisForm").validate().checkForm();	
		dom.performButton.prop('disabled', !valid);		
	}	
	
 	$(  '#tea_production_model, '	+ 
 		'#tea_fish_feed_price, ' 	+  
		'#tea_fish_fry_price,  ' 	+ 
		'#tea_fish_selling_price, ' + 
		'#tea_tax_rate, #tea_discount_rate, #tea_maturity_time, #tea_inflation_rate').bind('input', function () {
			$(this).valid();
			validateAnalysisForm();
			dom.resetButton.show();
			if(window.currentAnalysis != null){
				window.noty.closeAllNoty();
				window.dom.moveWorkspaceButtonsToDefault();
			}
	});
})();	