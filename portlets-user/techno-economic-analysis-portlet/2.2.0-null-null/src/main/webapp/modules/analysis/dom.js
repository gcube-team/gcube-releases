(function() {
	
	'use strict';

	var dom = {	
		analysisForm		: $("#analysisForm"),
		models 				: $('#tea_production_model'),
		fishSpecies			: $("#tea-fish-species"),
		taxRate 			: $('#tea_tax_rate'),
		feedPrice 			: $('#tea_fish_feed_price'),
		fryPrice 			: $('#tea_fish_fry_price'),
		discountRate 		: $('#tea_discount_rate'),
		maturity 			: $('#tea_maturity_time'),
		inflationRate 		: $('#tea_inflation_rate'),
		sellingPrice 		: $('#tea_fish_selling_price'),
		isOffShoreAquaFarm 	: $('#tea_is_off_shore_aqua_farm'),
		
		folderName 			: $("#folderName"),
		folderDescription 	: $("#folderDescription"),
		analysisName 		: $("#analysisName"),
		analysisDescription	: $("#analysisDescription"),
		fileNewName 		: $("#fileNewName"),
		
		performButton		: $("#tea_perform_button"),
		resetButton 		: $("#tea-reset-button"),
		saveButton 			: $(".save-analysis"),
		loadButton 			: $(".load-analysis"),
		toolTips			: {},
	
		resetAnalysis : function() {
			dom.models.val('Choose a Model');
			dom.fishSpecies.val('');
			dom.taxRate.val('');
			dom.feedPrice.val('');
			dom.fryPrice.val('');
			dom.discountRate.val('');
			dom.maturity.val('');
			dom.inflationRate.val('');
			dom.sellingPrice.val('');
			dom.isOffShoreAquaFarm.removeAttr('checked');
			
			dom.moveWorkspaceButtonsToDefault();			
			dom.resetValidationHints();
			dom.performButton.prop('disabled', true);
			
			$(".techno-economic-analysis-portlet .tab-content").hide();
			$(".techno-economic-analysis-portlet .save-analysis").hide();
			$(".techno-economic-analysis-portlet #tea-info-container").show();

			window.noty.closeAllNoty();
			window.currentAnalysis = null;	
			
			dom.models.one("change", function(){
				dom.taxRate.val(dom.taxRate.prop("defaultValue"));
				dom.feedPrice.val(dom.feedPrice.prop("defaultValue"));
				dom.fryPrice.val(dom.fryPrice.prop("defaultValue"));
				dom.discountRate.val(dom.discountRate.prop("defaultValue"));
				dom.maturity.val(dom.maturity.prop("defaultValue"));
				dom.inflationRate.val(dom.inflationRate.prop("defaultValue"));
				dom.sellingPrice.val(dom.sellingPrice.prop("defaultValue"));
				dom.performButton.prop('disabled', false);
				dom.resetValidationHints();
			});
		},
		resetValidationHints : function () {			
			dom.analysisForm.validate().resetForm();
			$(".control-group.error").find(".label-tooltip").removeClass("fa-info-circle");			
			$(".control-group.error").find(".label-tooltip").removeClass("fa-times-circle-o");
			$(".control-group.error").find(".label-tooltip").addClass("fa-info-circle");
			$(".control-group.error").removeClass('error');
			this.enableAllDefaultToolTips();
		},
		init : function(){
			this.uiBindings();
			this.initToolTips();
		},
		uiBindings : function(){
			dom.resetButton.on('click', function() {
				dom.resetAnalysis();
			});
			
			dom.models.on('change', function(){
				dom.fishSpecies.val($(this).find(":selected").data("fishSpecies"));
			});		
			
			$('[data-toggle="tooltip"]').click(function() {		// remove tooltips when clicking them
			    $('.tooltip').fadeOut('fast', function() {
			        $('.tooltip').remove();
			    });
			});
			
			$("form").submit(function() { return false; });		// disable refresh when pressing enter
		},
		initToolTips : function () {			
			this.toolTips = {
				"tea_production_model" 		: "The pre-built fish production model for the species to be cultivated",	
				"tea-fish-species" 			: "The fish species represented by the production model",
				"tea_fish_selling_price" 	: "The selling price per kg of the mature fish per generation",
				"tea_tax_rate" 				: "The rate that applies to the yearly profits of the aqua farm",
				"tea_discount_rate" 		: "The base year inflation rate that is used for the estimation of the project's key components' prices over the 10 year period",
				"tea_inflation_rate" 		: "The inflation rate which will be applied on prices for the next 10 years.",
				"tea_fish_feed_price" 		: "The feed price cost per kg representing the buy price of the feed for the cultivation of the targeted species",
				"tea_fish_fry_price" 		: "The fry price cost per kg representing the buy price of the fry for the cultivation of the targeted species",
				"tea_maturity_time" 		: "The necessary months for each generation of fish to reach a mature state. After that period, the fish are ready to be sold"
			}
			this.enableAllDefaultToolTips();
		},
		createToolTip : function (helpIcon, content, items){
			if(helpIcon.data("hasToolTip")){
				helpIcon.removeData("hasToolTip");
				helpIcon.tooltip("destroy");
			}
			
			helpIcon.tooltip({
				items : items,
			    content : content,
		    }).data("hasToolTip", true);			
		},	
		enableDefaultToolTip : function(toolTipId) {
			var helpIcon = $("#" + toolTipId).siblings(".label-tooltip");
			var content = dom.toolTips[toolTipId];			
			this.createToolTip(helpIcon, content, "i");
		},
		enableAllDefaultToolTips : function() {
			$(".tea_input_percent, .tea_input_currency, #tea_production_model").each(function () {
				var id = $(this).attr("id");
				dom.enableDefaultToolTip(id);				
			});
		},
		destroyAllDefaultToolTips : function () {
			$(".tea_input_percent, .tea_input_currency, #tea_production_model").each(function () {
				var id = $(this).attr("id");
				$("#" + id).siblings(".label-tooltip").tooltip("destroy");				
			});			
		},
		moveWorkspaceButtonsToNoty(name, date){				
		    var table = "<table class='row-fluid'> " +		    			
			    			"<tr>" +
			    				"<td class='span4' style='text-align:center;'>  " +
			    					  "<span class='font-16 blue-text'> " +
					    					name +
					    			  "</span> " +
					    			  "<br>" +
					    			  "<span class='font-12 gray-text'> " +
					    			  		"Created: " + date +
					    			  "</span>" +
					    		"</td>" +
			    			"</tr>" +
		    			"</table>" ;		    
			
			this.saveButton.show();	
			
			this.loadButton.show();			
			this.loadButton.removeClass("center").addClass("right");
			this.loadButton.insertAfter(".btn.save-analysis");
   
		    return table;
		},
		moveWorkspaceButtonsToDefault(){						
			this.loadButton.show();		    		   
			this.loadButton.addClass("center").removeClass("right");		    
			this.loadButton.appendTo("#tea-info-container");
		    
			this.saveButton.show();
			this.saveButton.css('position','absolute');
			this.saveButton.css('display','inline-block');
			this.saveButton.css('float','none');
			this.saveButton.insertBefore( "#tea-noty-container" );
			this.saveButton.html("<i class='fa fa-fw fa-floppy-o' aria-hidden='true'></i>Save in Workspace");	
		}
	};
	
	window.dom = dom;	
})();