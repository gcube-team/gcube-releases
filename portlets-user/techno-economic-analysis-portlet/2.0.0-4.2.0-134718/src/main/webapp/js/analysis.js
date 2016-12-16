var Analysis = {};

$(document).ready(function() {
	Analysis.notificator = $("#tea-noty-container");
	
	Analysis.showNoty = function showNoty(text, type){	
		
		if(typeof Analysis.notificator.close === 'function'){
			Analysis.notificator.close();
		}		
		
		var timeout = null;
		
		if(type === "success"){
			timeout = 3000;
		}else{
			timeout = false;
		}
		
		if(text != null && text.length > 0){
			text = text.split("\"").join("");
			Analysis.notificator = $("#tea-noty-container").noty({
			    text: text,
			    type: type,
			    template: '<div class="noty_message"><span class="noty_text"></span><div class="noty_close"></div></div>',
			    theme: 'relax',
			    closeWith: ['button'],
			    timeout: timeout,
			    maxVisible:1,
			    animation: {
			        open: 'animated bounceInLeft', 
			        close: 'animated bounceOutLeft',
			        easing: 'swing',
			        speed: 500
			    }
			});	
		}
	}
	
	Analysis.setNoty = function setNoty(text, type){	
		text = text.split("\"").join("");
		Analysis.notificator.setText(text);
		Analysis.notificator.setType(type);
	}
	
	Analysis.errorHandling = function errorHandling(jqXHR, exception) {				 
		var msg = '';
	    if(jqXHR.responseText != null && jqXHR.responseText > 0){
		    if (jqXHR.status === 0) {
		        msg = 'Could not connect.\n Verify Network.';
		    } else if (jqXHR.status == 400) {
		        msg = 'Error 400. Server understood the request, but request content was invalid.';
		    } else if (jqXHR.status == 401) {
		        msg = 'Error 401. Unauthorized access.';
		    } else if (jqXHR.status == 403) {
		        msg = 'Error 403. Forbidden resource can\'t be accessed.';
		    } else if (jqXHR.status == 404) {
		        msg = 'Error 404. Resource not found.';
		    } else if (jqXHR.status == 500) {
		        msg = 'Error 500. Internal Server Error.';		          
		    } else if (jqXHR.status == 503) {
		    	msg = 'Error 503. Service unavailable.';	
		    } else if (exception === 'parsererror') {
		        msg = 'Requested JSON parse failed.';
		    } else if (exception === 'timeout') {
		        msg = 'Time out error.';
		    } else if (exception === 'abort') {
		        msg = 'Ajax request aborted.';
		    } else {
		        msg = 'Uncaught Error.\n' + jqXHR.responseText;
		    }        
	    }
	    
		//Analysis.setNoty(msg + " " + jqXHR.responseText, "error");
	    Analysis.showNoty(msg + " " + jqXHR.responseText, "error");
	}	
	Analysis.showNoty("Insert parameters and perform analysis", "alert");
});


$(document).ready(function(){		
	$('#tea_left_form').validate({
		rules: {
			model: {
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
	
	
	$('#tea_mid_form').validate({
		rules: {	
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
	
	$('#tea_right_form').validate({
		rules: {
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
});	


$(document).ready(function() {
	function validateAllForms(){
		var allValid = true;
		$('form').each(function() {  
			var valid = $(this).validate().checkForm();		
			if(!valid){	
				allValid = false;
				return;
			}
		});		
		
		$("#tea_perform_button").prop('disabled', !allValid);
		
		if(allValid){
			$('form').each(function() {  
				$(this).valid();	
			});	
		}
	}	
	
 	$('	#tea_production_model, ' + 
 		'#tea_fish_feed_price, ' +  
		'#tea_fish_fry_price,  ' + 
		'#tea_fish_selling_price, ' + 
		'#tea_tax_rate, #tea_discount_rate, #tea_maturity, #tax_inflation_rate').bind('input', function () {
			$(this).valid();
			validateAllForms();
	});
});

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
			success : function(response) {					
				var modelSelectBox = $("#tea_fish_type");
				for(var i=0; i<response.length; i++){
					modelSelectBox.append("<option>" + response[i].designation + "</option>");
				}	
			},
			error : function (jqXHR, exception) {				 
				Analysis.errorHandling(jqXHR, exception);
			}
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
			},
			error : function(jqXHR, exception) {
				Analysis.errorHandling(jqXHR, exception);
			}
		});			
	}

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
						var inflationRate = {
								'2018' :  $('.techno-economic-analysis-portlet #tea_inflation_rate').val()
						}

						self.callWS(self.config.PerformAnalysisUrl, 
						{
							'modelId' : $('.techno-economic-analysis-portlet #tea_production_model').find(':selected').data("modelId"),
							'taxRate' : $('.techno-economic-analysis-portlet #tea_tax_rate').val(),
							'fishMix' : $('.techno-economic-analysis-portlet #tea_fish_mix').val(),
							'feedPrice' : $('.techno-economic-analysis-portlet #tea_fish_feed_price').val(),
							'fryPrice' : $('.techno-economic-analysis-portlet #tea_fish_fry_price').val(),
							'discountRate': $('.techno-economic-analysis-portlet #tea_discount_rate').val(),
							'maturity': $('.techno-economic-analysis-portlet #tea_maturity_time').val(),
							'inflationRate': JSON.stringify(inflationRate),
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
			$('.techno-economic-analysis-portlet #tea_insert_text').show();
			$('.techno-economic-analysis-portlet .tab-content').hide();
			$('.techno-economic-analysis-portlet .tea_loader').show();
			$('.techno-economic-analysis-portlet #tea_results_container ul > li').removeClass("techno-economic-analysis-portlet-success");
			$('.techno-economic-analysis-portlet #tea_results_container ul > li > a').prop( "disabled", true );
		},
		onCalculationEnd: function() {
			$('.techno-economic-analysis-portlet #tea_insert_text').hide();
			$('.techno-economic-analysis-portlet .tab-content').show();
			$('.techno-economic-analysis-portlet #tea_results_container ul > li').addClass("techno-economic-analysis-portlet-success");	
			$('.techno-economic-analysis-portlet #tea_results_container ul > li > a').prop( "disabled", false );
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
					if (onSuccess){
						onSuccess(data);
					}
					Analysis.showNoty("Analysis has been successful!", "success");
				},
				error : function(jqXHR, exception) {
					Analysis.errorHandling(jqXHR, exception);
					$('.techno-economic-analysis-portlet .tea_loader').hide();		
				}
			});
		}
	};

	window.Analytics = analytics;
}());