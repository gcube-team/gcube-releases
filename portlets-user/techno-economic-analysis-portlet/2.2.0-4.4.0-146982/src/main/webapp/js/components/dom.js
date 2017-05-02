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
			$(".techno-economic-analysis-portlet #tea-reset-button").hide();
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
			});
		},
		resetValidationHints : function () {			
			dom.analysisForm.validate().resetForm();
			$(".error").closest('.control-group').removeClass('error');
		},
		init : function(){
			this.uiBindings();
		},
		uiBindings : function(){
			dom.resetButton.on('click', function() {
				dom.resetAnalysis();
			});
			
			dom.models.on('change', function(){
				dom.fishSpecies.val($(this).find(":selected").data("fishSpecies"));
			});		
			
			$("form").submit(function() { return false; });		// disable refresh when pressing enter
		},
		moveWorkspaceButtonsToNoty(name, date){				
		    var table = $("<table class='row-fluid'> " +		    			
			    			"<tr>" +
			    				"<td class='span4'></td>"	+
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
		    			"</table>" ) ;
		    
			var workspaceButtons = $("<td class='span4' id='workspaceButtons' style='text-align:right;'></td>");
			var saveButton = this.saveButton;
			var loadButton = this.loadButton;
			
			loadButton.show();			    
		    saveButton.show();
		    saveButton.html("<i class='fa fa-fw fa-floppy-o' aria-hidden='true'></i>Save as");	
		    saveButton.css('position','relative');	
		    saveButton.css('display','inline');
		    
			workspaceButtons.append(loadButton);
			workspaceButtons.append(saveButton);
		    workspaceButtons.appendTo(table.find("tr"));
		    
		    return table;
		},
		moveWorkspaceButtonsToDefault(){		
			var saveButton = this.saveButton;
			var loadButton = this.loadButton;
						
			loadButton.show();		    		   
			loadButton.css('position','relative');		   
			loadButton.css('right','0');		    
			loadButton.appendTo("#tea-info-container");
		    
		    saveButton.show();
		    saveButton.css('position','absolute');
		    saveButton.css('display','inline-block');
		    saveButton.insertAfter( "#tea-results-container > hr" );
		    saveButton.html("<i class='fa fa-fw fa-floppy-o' aria-hidden='true'></i>Save in Workspace");	
		}
	};
	
	window.dom = dom;	
})();