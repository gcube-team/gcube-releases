$(document).ready(function() {
 	 function getFishSpecies(){
		$.ajax({
			url : SimulFishGrowthDataSpeciesUrl,
			type : "get",
			dataType : "json",
			beforeSend : function(xhr) {
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			},
			success : function(data) {	
				var models = $("#tea_fish_type");
				for(var i=0; i<data.length; i++){
					models.append("<option>" + data[i].designation + "</option>");
				}	
			},
			error : function(data) {}
		});			
	}
	
	function getModels(){
		$.ajax({
			url : SimulFishGrowthDataModelUrl,
			type : "get",
			dataType : "json",
			beforeSend : function(xhr) {
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			},
			success : function(data) {	
				var models = $("#tea_production_model");
				for(var i=0; i<data.length; i++){
					var option = $("<option>" + data[i].designation + "</option>");
					$(option).data( "modelId", data[i].id);		
					models.append(option);					
				}	
				
				$("#tea_production_model").on('change', function() {
					$("#tea_perform_button").prop('disabled', false);
				});					
				
				$('.techno-economic-analysis-portlet #tea_insert_text').text("Insert parameters and perform estimation analysis");
				$('.techno-economic-analysis-portlet #tea_insert_text').css("font-weight", "normal");
				$('.techno-economic-analysis-portlet #tea_insert_text').css("color","gray");	
				$('.techno-economic-analysis-portlet #tea_insert_text').show();
			},
			error : function(data) {
				$('.techno-economic-analysis-portlet #tea_insert_text').text("Could not retrieve Models");
				$('.techno-economic-analysis-portlet #tea_insert_text').css("font-weight", "bold");
				$('.techno-economic-analysis-portlet #tea_insert_text').css("color","red");	
				$('.techno-economic-analysis-portlet #tea_insert_text').show();
			}
		});			
	} 
	
	//getFishSpecies();
	getModels();	
});
		

 (function() {
	'use strict';

	var analytics = {
		config : null,
		init : function(config) {
			var self = analytics;
			
			self.config = config;
			
			google.charts.load('current', {'packages':['corechart']});
			
			$('.techno-economic-analysis-portlet [data-element="submit"]').bind('click',
					function() {					
						self.onCalculationStart();
						
						self.callWS(self.config.PerformAnalysisUrl, 
						{
							'modelId' : $('.techno-economic-analysis-portlet #tea_production_model').find(':selected').data("modelId"),
							'taxRate' : $('.techno-economic-analysis-portlet #tea_tax_rate').val(),
							'fishMix' : $('.techno-economic-analysis-portlet #tea_fish_mix').val(),
							'feedPrice' : $('.techno-economic-analysis-portlet #tea_fish_feed_price').val(),
							'fryPrice' : $('.techno-economic-analysis-portlet #tea_fish_fry_price').val(),
							'sellingPrice' : $('.techno-economic-analysis-portlet #tea_fish_selling_price').val(),
							'isOffShoreAquaFarm' : $('.techno-economic-analysis-portlet #tea_is_off_shore_aqua_farm').is(':checked')
						}, 
						self.showResult);
						return false;
					});
			
			
		},
		showResult: function(data){
			var dep_npv = data.depreciatedValues.targetIndicators.npv;
			var dep_irr = data.depreciatedValues.targetIndicators.irr;
			
			$('.techno-economic-analysis-portlet #tea_dep_npv').text(analytics.formatTableNumberEntry(dep_npv));
			$('.techno-economic-analysis-portlet #tea_dep_irr').text(analytics.formatPercentageFromDecimal(dep_irr));
			
			var depChartArea = $('.techno-economic-analysis-portlet #tea_dep_net_profit_chart_area')[0];
			
			analytics.drawNetProfitChart('', data.depreciatedValues, depChartArea);
			
			var depDetailsChartArea = $('.techno-economic-analysis-portlet #tea_dep_details_chart_area')[0];
			
			analytics.drawDetailsChart('', data.depreciatedValues, depDetailsChartArea);
			
			var depDetailsTableView = $('.techno-economic-analysis-portlet #tea_dep_table_view')[0];
			
			analytics.drawTableView(data.depreciatedValues, data.undepreciatedValues, depDetailsTableView);
			
			var depProfitLossChartArea = $('.techno-economic-analysis-portlet #tea_dep_profit_loss_chart_area')[0];
			
			analytics.drawProfitLossChart('', data.depreciatedValues, depProfitLossChartArea);
			
			analytics.onCalculationEnd();
		},
		drawNetProfitChart: function(title, values, chartArea){
			
			var dataTable = new google.visualization.DataTable();
			dataTable.addColumn('number', 'Year');
			dataTable.addColumn('number', 'Margin');
			  
			var yearEntries = values.yearEntries;

			for (var entry in yearEntries) {
				if (!entry) continue;
				var yearEntry = yearEntries[entry];
				dataTable.addRow([yearEntry.year, 
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
			dataTable.addColumn('number', 'Year');
			dataTable.addColumn('number', 'Profit/Loss');
			  
			var yearEntries = values.yearEntries;

			for (var entry in yearEntries) {
				if (!entry) continue;
				var yearEntry = yearEntries[entry];
				dataTable.addRow([yearEntry.year, 
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
			dataTable.addColumn('number', 'Year');
			dataTable.addColumn('number', 'Expenses');
			dataTable.addColumn('number', 'Income');
			dataTable.addColumn('number', 'EBITDA');
			dataTable.addColumn('number', 'EBIAT');
			  
			var yearEntries = values.yearEntries;

			for (var entry in yearEntries) {
				if (!entry) continue;
				var yearEntry = yearEntries[entry];
				dataTable.addRow([yearEntry.year, 
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
				row = $(`<tr style="border-bottom-style: ridge; border-bottom-color:rgb(250,250,210);"></tr>`);
				
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
				row = $(`<tr style="background-color:rgb(245, 245, 245);"></tr>`);
				
				col = $(`<td class="tea_head_cols">OA Cost</td>`);
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
				
				col = $(`<td class="tea_head_cols">Shopping Cost</td>`);
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
				row = $(`<tr style="background-color:rgb(245, 245, 245);
									border-bottom-style: ridge; 
									border-bottom-color:rgb(250,250,210);"  
						></tr>`);
				
				col = $(`<td class="tea_head_cols">Expenses</td>`);
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
				
				col = $(`<td class="tea_head_cols">Income</td>`);
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
				row = $(`<tr style="background-color:rgb(245, 245, 245)"></tr>`);
				
				col = $(`<td class="tea_head_cols">EBITDA</td>`);
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
				row = $(`<tr style="border-bottom-style: ridge; 
						border-bottom-color:rgb(250,250,210);"  
						></tr>`);
				
				col = $(`<td class="tea_head_cols">EBIAT</td>`);
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
				row = $(`<tr style="background-color:rgb(245, 245, 245)"></tr>`);
				
				col = $(`<td class="tea_head_cols">Cumulative Profit/Loss</td>`);
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
			$('.techno-economic-analysis-portlet #tea_insert_text').show();
			$('.techno-economic-analysis-portlet .tab-content').hide();
			$('.techno-economic-analysis-portlet .tea_loader').show();
		},
		onCalculationEnd: function() {
			$('.techno-economic-analysis-portlet #tea_insert_text').hide();
			$('.techno-economic-analysis-portlet .tab-content').show();
			$('.techno-economic-analysis-portlet ul > li').addClass("techno-economic-analysis-portlet-success");	
			$('.techno-economic-analysis-portlet .tea_loader').hide();		
		},
		
		callWS : function(url, data, onSuccess, onError) {
			$.ajax({
				url : url,
				type : "get",
				dataType : "json",
				data : data,
				beforeSend : function(xhr) {
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				success : function(data) {
					if (onSuccess)
						onSuccess(data);					
				},
				error : function(data) {
					$('.techno-economic-analysis-portlet .tab-content').hide();
					$('.techno-economic-analysis-portlet #tea_insert_text').text("Could not retrieve Analysis results");
					$('.techno-economic-analysis-portlet #tea_insert_text').css("font-weight", "bold");
					$('.techno-economic-analysis-portlet #tea_insert_text').css("color","red");	
					$('.techno-economic-analysis-portlet #tea_insert_text').show();
					$('.techno-economic-analysis-portlet .tea_loader').hide();	
				}
			});
		}
	};

	window.Analytics = analytics;
}());