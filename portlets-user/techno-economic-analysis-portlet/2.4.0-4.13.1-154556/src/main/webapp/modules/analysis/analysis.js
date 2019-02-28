(function() {
	'use strict';
	
	var dom = window.dom;
	var notificator = window.notificator;
	
	var models = {
		config : null,
		init : function(config) {		
			this.config = config;
			this.modelIds = {};
			this.modelsDOM = config.modelsDOM;
		},
		getModels : function(){
			var resourceUrl = this.config.getProductionModelsUrl;
			
			var onSuccessCallback = function (data){				
				for(var i=0; i<data.length; i++){
					models.addModel(data[i]);
				}				
			};
			
			var onErrorCallback = function (jqXHR, exception) {				
				models.modelsDOM.closest('.control-group').removeClass('success').addClass('error');
				
				var modelsTooltip = models.modelsDOM.siblings(".label-tooltip");
				modelsTooltip.removeClass("fa-info-circle");
				modelsTooltip.addClass("fa-times-circle-o");
				
				var msg = notificator.errorHandling(jqXHR, exception);
				
				notificator.createTooltip(modelsTooltip, msg, "i", "error");
			};
			
			window.ajax.get(null, resourceUrl, onSuccessCallback, onErrorCallback);
		},
		addModel : function(model){
			var option = $("<option>" + model.designation + "</option>");
			option.data("modelName", model.designation);
			option.data("modelId", model.id);	
			option.data("fishSpecies", model.speciesDesignation)
			this.modelsDOM.append(option);	
			this.modelIds[model.id] = model.designation;
		}
	};
	window.models = models;
})();
		
			
 (function() {
	'use strict';
	
	var dom = window.dom;
	
	var analytics = {
		config : null,
		init : function(config) {
			var self = analytics;			
			self.config = config;
			
			google.charts.load('current', {'packages':['corechart']});
						  
			$('.techno-economic-analysis-portlet [data-element="submit"]').bind('click', function() {									
				self.onCalculationStart();
				
				var nextYear = new Date().getFullYear() + 1;				
				var inflationRate = {};
				inflationRate[nextYear] = $('.techno-economic-analysis-portlet #tea_inflation_rate').val();
								
				var parameters = {
					modelId 			: $('#tea_production_model').find(':selected').data("modelId"),
					modelName 			: $('#tea_production_model').find(':selected').data("modelName"),
					fishSpecies 		: $('#tea-fish-species').val(),
					taxRate 			: $('#tea_tax_rate').val(),
					feedPrice 			: $('#tea_fish_feed_price').val(),
					fryPrice 			: $('#tea_fish_fry_price').val(),
					discountRate		: $('#tea_discount_rate').val(),
					maturity			: $('#tea_maturity_time').val(),
					inflationRate		: inflationRate,
					sellingPrice 		: $('#tea_fish_selling_price').val(),
					isOffShoreAquaFarm 	: $('#tea_is_off_shore_aqua_farm').is(':checked')
				}						
				
				self.callWS(self.config.performAnalysisUrl, 
				{
					'parameters' : JSON.stringify(parameters)
				},  
				self.showResult);
				return false;
			});
			
			
		},
		showParameters: function(data){
			if(window.models.modelIds[data.modelId] == null){
				window.models.addModel({id : data.modelId, designation : data.modelName});
				window.models.modelsDOM.val(data.modelName);
				window.models.modelsDOM.find(":selected").css("text-decoration", "line-through");			
				window.models.modelsDOM.find(":selected").css("color", "#b94a48");
			}	
			
			window.models.modelsDOM.val(data.modelName);
			
			var nextYear = new Date().getFullYear() + 1;
			var inflationRate = data.inflationRate[nextYear];

			$('#tea-fish-species').val(data.fishSpecies);
			$('#tea_tax_rate').val(data.taxRate),
			$('#tea_fish_feed_price').val(data.feedPrice),
			$('#tea_fish_fry_price').val(data.fryPrice),
			$('#tea_discount_rate').val(data.discountRate),
			$('#tea_maturity_time').val(data.maturity),
			$('#tea_inflation_rate').val(inflationRate),
			$('#tea_fish_selling_price').val(data.sellingPrice),
			$('#tea_is_off_shore_aqua_farm').prop('checked', data.isOffShoreAquaFarm);
		},
		showResult: function(data){
			var dep_npv = data.depreciatedValues.targetIndicators.npv;
			var dep_irr = data.depreciatedValues.targetIndicators.irr;
			
			$('#tea_dep_npv').text(analytics.formatTableNumberEntry(dep_npv));
			$('#tea_dep_irr').text(analytics.formatPercentageFromDecimal(dep_irr));
			
			var depChartArea = $('#tea_dep_net_profit_chart_area')[0];
			
			analytics.drawNetProfitChart('', data.depreciatedValues, depChartArea);
			
			var depDetailsChartArea = $('#tea_dep_details_chart_area')[0];
			
			analytics.drawDetailsChart('', data.depreciatedValues, depDetailsChartArea);
			
			var depDetailsTableView = $('#tea_dep_table_view')[0];
			
			analytics.drawTableView(data.depreciatedValues, data.undepreciatedValues, depDetailsTableView);
			
			var depProfitLossChartArea = $('#tea_dep_profit_loss_chart_area')[0];
			
			analytics.drawProfitLossChart('', data.depreciatedValues, depProfitLossChartArea);
			
			analytics.onCalculationEnd();
		},
		drawNetProfitChart: function(title, values, chartArea){
			
			var dataTable = new google.visualization.DataTable();
			dataTable.addColumn('string', 'Year');
			dataTable.addColumn('number', 'Margin');
			  
			var yearEntries = values.yearEntries;

			for (var entry in yearEntries) {
				if (!entry) continue;
				var yearEntry = yearEntries[entry];
				dataTable.addRow([yearEntry.year + '', 
				                  yearEntry.netProfitMargin]);
			}
		      
	        var options = {
//	          title: title,
	          curveType: 'function',
	          legend: { position: 'right' },
	          width: 900,
	          height: 300
	        };

	        var chart = new google.visualization.LineChart(chartArea);

	        chart.draw(dataTable, options);
		},
		drawProfitLossChart: function(title, values, chartArea){
			
			var dataTable = new google.visualization.DataTable();
			dataTable.addColumn('string', 'Year');
			dataTable.addColumn('number', 'Profit/Loss');
			  
			var yearEntries = values.yearEntries;

			for (var entry in yearEntries) {
				if (!entry) continue;
				var yearEntry = yearEntries[entry];
				dataTable.addRow([yearEntry.year + '', 
				                  yearEntry.afterTaxCummulativeGL]);
			}
		      
	        var options = {
//	          title: title,
	          curveType: 'function',
	          legend: { position: 'right' },
	          width: 900,
	          height: 300
	        };

	        var chart = new google.visualization.LineChart(chartArea);

	        chart.draw(dataTable, options);
		},
		drawDetailsChart: function(title, values, chartArea){
			
			var dataTable = new google.visualization.DataTable();
			dataTable.addColumn('string', 'Year');
			dataTable.addColumn('number', 'Expenses');
			dataTable.addColumn('number', 'Income');
			dataTable.addColumn('number', 'EBITDA');
			dataTable.addColumn('number', 'EBIAT');
			  
			var yearEntries = values.yearEntries;

			for (var entry in yearEntries) {
				if (!entry) continue;
				var yearEntry = yearEntries[entry];
				dataTable.addRow([yearEntry.year + '', 
				                  -yearEntry.expenses, 
				                  yearEntry.income, 
				                  yearEntry.preTaxBalance, 
				                  yearEntry.afterTaxBalance]);
			}
		      
	        var options = {
//	          title: title,
	          curveType: 'function',
	          legend: { position: 'right' },
	          width: 900,
	          height: 300,
	          series: {
	              0: { color: 'red' },
	              1: { color: 'green' },
	              2: { color: 'lightblue' },
	              3: { color: 'blue' }
	            }
	        };

	        var chart = new google.visualization.LineChart(chartArea);

	        chart.draw(dataTable, options);
		},
		drawTableView: function(values, underDepreciatedValues,  table){
			var yearEntries = values.yearEntries;
			var underDepreciatedYearEntries = underDepreciatedValues.yearEntries;
			$(table).empty();
			
			var row, col;
			{
				row = $('<tr style="border-bottom-style: ridge; border-bottom-color:rgb(250,250,210);"></tr>');
				
				col = $('<th></th>');
				row.append(col);
				
				for (var entry in yearEntries) {
					if (!entry) continue;
					var yearEntry = yearEntries[entry];
					
					col = $('<th></th>');
					col.text(yearEntry.year);
					row.append(col);
				}
				$(table).append(row);
			}
			{
				row = $('<tr style="background-color:rgb(245, 245, 245);"></tr>');
				
				col = $('<td class="tea_head_cols">OA Cost</td>');
				row.append(col);
				
				for (var entry in underDepreciatedYearEntries) {
					if (!entry) continue;
					var yearEntry = yearEntries[entry];
					
					col = $('<td></td>');
					col.text(analytics.formatTableNumberEntry(yearEntry.oacost));
					row.append(col);
				}
				$(table).append(row);
			}
			{
				row = $('<tr></tr>');
				
				col = $('<td class="tea_head_cols">Shopping Cost</td>');
				row.append(col);
				
				for (var entry in underDepreciatedYearEntries) {
					if (!entry) continue;
					var yearEntry = yearEntries[entry];
					
					col = $('<td></td>');
					col.text(analytics.formatTableNumberEntry(yearEntry.totalShoppingCost));
					row.append(col);
				}
				$(table).append(row);
			}
			{
				row = $('<tr style="background-color:rgb(245, 245, 245);' +
									'border-bottom-style: ridge;'  + 
									'border-bottom-color:rgb(250,250,210);"'  +  
						'></tr>');
				
				col = $('<td class="tea_head_cols">Expenses</td>');
				row.append(col);
				
				for (var entry in underDepreciatedYearEntries) {
					if (!entry) continue;
					var yearEntry = yearEntries[entry];
					
					col = $('<td></td>');
					col.text(analytics.formatTableNumberEntry(yearEntry.expenses));
					row.append(col);
				}
				$(table).append(row);
			}
			{
				row = $('<tr></tr>');
				
				col = $('<td class="tea_head_cols">Income</td>');
				row.append(col);
				
				for (var entry in underDepreciatedYearEntries) {
					if (!entry) continue;
					var yearEntry = yearEntries[entry];
					
					col = $('<td></td>');
					col.text(analytics.formatTableNumberEntry(yearEntry.income));
					row.append(col);
				}
				$(table).append(row);
			}
			{
				row = $('<tr style="background-color:rgb(245, 245, 245)"></tr>');
				
				col = $('<td class="tea_head_cols">EBITDA</td>');
				row.append(col);
				
				for (var entry in underDepreciatedYearEntries) {
					if (!entry) continue;
					var yearEntry = yearEntries[entry];
					
					col = $('<td></td>');
					col.text(analytics.formatTableNumberEntry(yearEntry.preTaxBalance));
					row.append(col);
				}
				$(table).append(row);
			}
			{
				row = $('<tr style="border-bottom-style: ridge; ' + 
						'border-bottom-color:rgb(250,250,210);"'  +
						'></tr>');
				
				col = $('<td class="tea_head_cols">EBIAT</td>');
				row.append(col);
				
				for (var entry in yearEntries) {
					if (!entry) continue;
					var yearEntry = yearEntries[entry];
					
					col = $('<td></td>');
					col.text(analytics.formatTableNumberEntry(yearEntry.afterTaxBalance));
					row.append(col);
				}
				$(table).append(row);
			}
			{
				row = $('<tr style="background-color:rgb(245, 245, 245)"></tr>');
				
				col = $('<td class="tea_head_cols">Cumulative Profit/Loss</td>');
				row.append(col);
				
				for (var entry in yearEntries) {
					if (!entry) continue;
					var yearEntry = yearEntries[entry];
					
					col = $('<td></td>');
					col.text(analytics.formatTableNumberEntry(yearEntry.afterTaxCummulativeGL));
					row.append(col);
				}
				$(table).append(row);
			}
		},
		
		formatTableNumberEntry: function(number){
			if ( number == 0 ) return '-';
			return parseFloat(number.toFixed(2)).toLocaleString();
		},
		
		formatPercentageFromDecimal: function(number){
			number = number * 100;
			return parseFloat(number.toFixed(2)).toLocaleString() + '%';
		},
		
		onCalculationStart: function() {
			window.currentAnalysis = null;
			$('.techno-economic-analysis-portlet #tea-info-container').hide();
			$('.techno-economic-analysis-portlet .save-analysis').hide();
			$('.techno-economic-analysis-portlet .tab-content').hide();
			$('.techno-economic-analysis-portlet .tea_loader').show();
			$('.techno-economic-analysis-portlet #tea-results-container ul > li').removeClass("techno-economic-analysis-portlet-success");
			$('.techno-economic-analysis-portlet #tea-results-container ul > li > a').prop( "disabled", true );
		},
		onCalculationEnd: function() {
			$('.techno-economic-analysis-portlet .tab-content').show();
			$('.techno-economic-analysis-portlet #tea-results-container ul > li').addClass("techno-economic-analysis-portlet-success");	
			$('.techno-economic-analysis-portlet #tea-results-container ul > li > a').prop("disabled", false);
			$('.techno-economic-analysis-portlet .tea_loader').hide();		
		},
		
		callWS : function(resourceUrl, data, onSuccess, onError) {							
			var onSuccessCallback = function (data){
				if (onSuccess){
					onSuccess(data);
				}
				window.currentAnalysis = data;
				notificator.showText(dom.notification, "Analysis has been successful!", "success");
				
				dom.saveButton.show();
				dom.loadButton.removeClass("center").addClass("right");
				dom.loadButton.insertAfter(".btn.save-analysis");
			};
			
			var onErrorCallback = function (jqXHR, exception) {
				notificator.errorHandlingText(dom.notification, jqXHR, exception);
				$('.techno-economic-analysis-portlet .tea_loader').hide();	
				$('.techno-economic-analysis-portlet #tea-info-container').show();
			};
			
			var beforeSendCallback = function(){
				dom.resetButton.hide();
				dom.saveButton.hide();
				dom.loadButton.hide();
				notificator.clearNotification(dom.notification);
			};
			
			var completeCallback = function(){
				dom.resetButton.show();
				dom.loadButton.show();			
				dom.saveButton.show();			
			};
			
			window.ajax.post(data, resourceUrl, onSuccessCallback, onErrorCallback, beforeSendCallback, completeCallback);
		}
	};

	window.analytics = analytics;
})();