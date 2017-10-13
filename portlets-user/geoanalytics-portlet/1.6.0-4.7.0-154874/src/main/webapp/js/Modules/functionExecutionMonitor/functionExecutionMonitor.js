(function(){
	'use strict';
	
	var functionExecutionMonitor = {
			addExecutionID : function(executionID) {
				this.getExecutionIDs().push(executionID);
			},
			
			clear : function() {
				var IDs = this.getAllExecutionIDs();
				
				this.deleteExcecutionInfo(IDs);
			},
			
			container : $('#geoanalyitcs-functions-execution-monitoring-container'),
			
			dataTable : null,
			
			dateOptions : { 
				year: 'numeric',
				month: 'short',
				day: 'numeric',
				hour: 'numeric',
				minute: 'numeric',
				hour12: false
			},
			
			deleteExcecutionInfo: function(ExecutionInfoIDs) {
				 $.ajax({
				        url : window.config.createResourceURL('plugin/deleteExecutionDetailsInfo'),
				        type : 'POST',
				        data : JSON.stringify(ExecutionInfoIDs),
				        contentType : "application/json",
				        beforeSend : function(xhr) {
				        	xhr.setRequestHeader("Accept", "application/json");
				        	xhr.setRequestHeader("Content-Type", "application/json");

				        	functionExecutionMonitor.spinnerShowFunction();
				        },
				        success : function(data) {
				        	functionExecutionMonitor.reload();
				        },
				        error : function(jqXHR, exception) {
				        	functionExecutionMonitor.errorHandling(jqXHR, exception);
				        },
				        complete : function() {
				        	functionExecutionMonitor.spinnerHideFunction();
				        },
				        timeout : 20000
				    });
			},
			
			errorHandling : function(jqXHR, exception) {
			    window.notificator.errorHandling($("#geoanalytics-functions-execution-monitor-notificator"), jqXHR, exception);
		    },
			
			executionID : null,
			
			executionIDs : [],
			
			formatDate : function(date) {
				var firstHalf = date.substring(0, date.indexOf(','));
		    	
		    	var secondHalf = date.substring(date.indexOf(','), date.length);
		    	
		    	firstHalf = firstHalf.replace(/ /g,'-');
		    	
		    	return firstHalf + secondHalf;
			},
			
			getAllData : function(){
				return this.dataTable.dataTable.data();
			},
			
			getAllExecutionIDs : function() {
				var IDs = [];
				
				$.each(functionExecutionMonitor.getAllData(), function(index, value) {
					IDs.push(value.executionDetailsID);
				});
				
				return IDs;
			},
			
			getExecutionIDs : function() {
				return this.executionIDs;
			},
			
			getFunctionExecutionMonitor : function() {
				var url = window.config.createResourceURL('plugin/executionStatus');
				
		    	var self = this;
				
				// Create datatable
			    $('#geoanalytics-functions-execution-monitor-datatable').PortletDataTable({
			    	ajax :	{
				        url : url,
				        type : 'POST',
				        cache : false,
				        dataType : "json",
				        beforeSend : function(xhr) {
				        	xhr.setRequestHeader("Accept", "application/json");
				        	xhr.setRequestHeader("Content-Type", "application/json");

				        	functionExecutionMonitor.spinnerShowFunction();
				        },
				        data : function (d){
				        	return JSON.stringify(functionExecutionMonitor.getExecutionIDs());
				        },
				        dataSrc : function(data) {
					        if(data !== '' || data !== null || typeof data === 'undefined') {
					        	$.each(data, function(index, value) {
					        		data[index].progress = data[index].progress + '%';
					        		
					        		var className = '';
					        		if(data[index].status === 'KILLED' || data[index].status === 'FAILED') {
					        			className = 'portlet-color-error';
					        		} else if(data[index].status === 'RUNNING' || data[index].status === 'INPROGRESS' || data[index].status === 'QUEUED') {
					        			className = 'portlet-color-neutral';
					        		} else if(data[index].status === 'SUCCEEDED') {
					        			className = 'portlet-color-success';
					        		}
					        		
					        		var $b = $('<b></b>', {
					        			class : className,
					        			text : data[index].status
					        		});
					        		data[index].status = $b[0].outerHTML;
					        		
					        		if(data[index].startTimestamp !== null) {
					        			data[index]["startTimestampDateObject"] = new Date(data[index].startTimestamp);
					        			
						        		var startTimestampString = new Date(data[index].startTimestamp).toLocaleString(self.userLocale, self.dateOptions);
						        		
						        		data[index].startTimestamp = self.formatDate(startTimestampString);
					        		}
					        		
					        		if(data[index].stopTimestamp !== null) {
					        			data[index]["stopTimestampDateObject"] = new Date(data[index].stopTimestamp);
					        			
						        		var stopTimestampString = new Date(data[index].stopTimestamp).toLocaleString(self.userLocale, self.dateOptions);
						        		
						        		data[index].stopTimestamp = self.formatDate(stopTimestampString);
					        		}
					        	});
					        	
					        	return data;
					        }
				        },
				        error : function(jqXHR, exception) {
				        	functionExecutionMonitor.errorHandling(jqXHR, exception);
				        },
				        complete : function() {
				        	functionExecutionMonitor.spinnerHideFunction();
				        },
				        timeout : 20000
					},
					columnDefs : [{
			        	title : "Scope",
			        	fieldName : "submissionOrigin",
			            targets : 0,
			        }, {
			        	title : "Layer",
			            fieldName : "layerName",
			            targets :  1
			        }, {
			        	title : 'Plugin',
			        	fieldName : "pluginName",
			            targets :  2
			        }, {
			        	title : 'Project',
			        	fieldName : 'projectName',
			            targets :  3
			        }, {
			        	title : 'Started',
			        	fieldName : 'startTimestamp',
			            targets :  4,
						orderData: 10
			        }
			        , {
			        	title : 'Stopped',
			        	fieldName : 'stopTimestamp',
			            targets :  5,
						orderData: 11
			        }
			        , {
			        	title : "Progress",
			        	fieldName : "progress",
			        	targets : 6,
			        }, {
			        	title : 'Status',
			        	fieldName : 'status',
			        	className : "white",
			            targets :  7
			        }, {
			        	title : 'ID',
			            fieldName : "id",
			            visible : false
			        },
					{
						data : "startTimestampDateObject",
						visible : false,
						searchable : false,
						orderable : true,
						type : "date"
					},
					{
						data : "stopTimestampDateObject",
						visible : false,
						searchable : false,
						orderable : true,
						type : "date"
					}],
			        order : [[0, "asc"]],
			        selectStyle : "multi",
			    	toolbar : $('#geoanalytics-functions-execution-monitor-toolbar')
			    });
			    
			    // Get Widget Instance
			    this.dataTable = $('#geoanalytics-functions-execution-monitor-datatable').data("dt-PortletDataTable");
				
			},
			
			getSelectRowsExecutionIDs : function() {
				var selectedRowsData = this.dataTable.getSelectedRowsData();

				var IDs = [];
				
				$.each(selectedRowsData, function(index, value) {
					IDs.push(value.executionDetailsID);
				});
				
				return IDs;
			},
			
			init : function(spinnerHideCallback, spinnerShowCallback) {
				this.spinnerHideFunction = spinnerHideCallback;
				this.spinnerShowFunction = spinnerShowCallback;
				
				var self = this;
				
				$('#settingsResponsive, #adminSettingsButtonContainer').one('click', function(){
					$('#adminSettingsButtonContainer').attr('data-toggle','modal');
					$('#adminSettingsButtonContainer').attr('data-target','#geoanalytics-functions-execution-monitor-modal');
					
					functionExecutionMonitor.loadContent($(this));
					
					self.userLocale = window.config.userLocale;
				});
			},
			
			initUIBindings : function() {
//			    $("#settingsResponsive, #adminSettingsButtonContainer").on("click", function() {
//			    	functionExecutionMonitor.getFunctionExecutionMonitor();
//			    });
			    
			    $("#geoanalytics-functions-execution-monitor-refresh-execution-status-button").on("click", function() {
			    	functionExecutionMonitor.reload();
			    });
			    
			    $("#geoanalytics-functions-execution-monitor-clear-all-info-button").on("click", function() {
			    	functionExecutionMonitor.clear();
			    });
			    
			    $("#geoanalytics-functions-execution-monitor-delete-info-by-id-button").on('click', function() {
			    	functionExecutionMonitor.deleteExcecutionInfo(functionExecutionMonitor.getSelectRowsExecutionIDs());
			    });
		    },
			
			loadContent : function($elem) {
				this.container.load(window.config.contextPath + "js/Modules/functionExecutionMonitor/functionExecutionMonitor.jsp", function() {
					functionExecutionMonitor.loadCSS();
					functionExecutionMonitor.initUIBindings();

					functionExecutionMonitor.getFunctionExecutionMonitor();
					$elem.click();
					$elem.off().on('click', function() {
						functionExecutionMonitor.reload();
					});
			    });
			},
			
		    loadCSS : function() {
			    $("<link/>", {
			        rel : "stylesheet",
			        type : "text/css",
			        href : window.config.contextPath + "js/Modules/functionExecutionMonitor/functionExecutionMonitor.css"
			    }).appendTo("head");
		    },
		    
		    notificator : $("#geoanalytics-functions-execution-monitor-notificator"),
		    
		    reload : function() {
		    	functionExecutionMonitor.dataTable.refreshData();
		    },
		    
		    setExecutionID : function(executionID) {
		    	this.executionID = executionID;
		    },

		    showMessage : function(text, type) {
			    window.notificator.setText($("#geoadmin-import-monitor-notificator"), text, type);
		    },
		    
		    spinnerHideFunction : null,
		    
		    spinnerShowFunction : null,
		    
		    userLocale : null
	};
	
	window.functionExecutionMonitor = functionExecutionMonitor;
})();