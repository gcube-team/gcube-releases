(function() {
	
	'use strict';
	
	var notificator = window.notificator;
	
	var dom = {	
		notification 		: $("#tea-notification"),
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
		tooltips			: {},
	
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

	    	notificator.clearNotification(dom.notification);
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
			this.enableAllDefaultTooltips();
		},
		
		init : function(){
			this.uiBindings();
			this.initTooltips();
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
		
		initTooltips : function () {			
			this.tooltips = {
				"#tea_production_model" 	: "The pre-built fish production model for the species to be cultivated",	
				"#tea-fish-species" 		: "The fish species represented by the production model",
				"#tea_fish_selling_price" 	: "The selling price per kg of the mature fish per generation",
				"#tea_tax_rate" 			: "The rate that applies to the yearly profits of the aqua farm",
				"#tea_discount_rate" 		: "The base year inflation rate that is used for the estimation of the project's key components' prices over the 10 year period",
				"#tea_inflation_rate" 		: "The inflation rate which will be applied on prices for the next 10 years.",
				"#tea_fish_feed_price" 		: "The feed price cost per kg representing the buy price of the feed for the cultivation of the targeted species",
				"#tea_fish_fry_price" 		: "The fry price cost per kg representing the buy price of the fry for the cultivation of the targeted species",
				"#tea_maturity_time" 		: "The necessary months for each generation of fish to reach a mature state. After that period, the fish are ready to be sold"
			};
			
			this.enableAllDefaultTooltips();
		},
		
		enableDefaultTooltip : function(tooltipId) {
			if(!tooltipId.startsWith("#")){
				tooltipId = "#" + tooltipId;
			}
			var helpIcon = $(tooltipId).siblings(".label-tooltip");
			var content = dom.tooltips[tooltipId];			
			notificator.createTooltip(helpIcon, content, "i");
		},
		
		enableAllDefaultTooltips : function() {
			for (var tooltipId in this.tooltips) {
			    if (this.tooltips.hasOwnProperty(tooltipId)) {
					dom.enableDefaultTooltip(tooltipId);				
			    }
			}
		},
		
		moveWorkspaceButtonsToNoty(name, date){				
		    var table = "<table class='row-fluid'> " +		    			
			    			"<tr>" +
			    				"<td class='span4' style='text-align:center;'>  " +
			    					  "<span class='font-14 blue-text'> " +
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
			this.saveButton.hide();

			this.loadButton.show();		    		   
			this.loadButton.addClass("center").removeClass("right");		    
			this.loadButton.appendTo("#tea-info-container");			
		},
		
		moveWorkspaceButtonsToSides(){
			this.saveButton.show();
			this.saveButton.css('position','absolute');
			this.saveButton.css('display','inline-block');
			this.saveButton.css('float','none');
			this.saveButton.insertBefore( "#tea-notification" );
			this.saveButton.html("<i class='fa fa-fw fa-floppy-o' aria-hidden='true'></i>Save in Workspace");	
			
			this.loadButton.show();		
			this.loadButton.removeClass("center").addClass("right");		    
			this.loadButton.appendTo("#tea-results-container-header");	
		}
	};
	
	window.dom = dom;	
})();