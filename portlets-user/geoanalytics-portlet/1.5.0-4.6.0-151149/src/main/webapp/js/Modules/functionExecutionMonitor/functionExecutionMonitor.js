(function(){
	'use string';
	
	var functionExecutionMonitor = {
			addExecutionID : function(executionID) {
				this.getExecutionIDs().push(executionID);
			},
			
			clear : function() {
			
			},
			
			container : $('#geoanalyitcs-functions-execution-monitoring-container'),
			
			dataTable : null,
			
			errorHandling : function(jqXHR, exception) {
			    window.notificator.errorHandling($("#geoanalytics-functions-execution-monitor-notificator"), jqXHR, exception);
		    },
			
			executionID : null,
			
			executionIDs : [],
			
			getExecutionIDs : function() {
				return this.executionIDs;
			},
			
			getFunctionExecutionMonitor : function() {
				var url = window.config.createResourceURL('plugin/executionStatus');
				
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
					        		} else if(data[index].status === 'RUNNING' || data[index].status === 'INPROGRESS' || data[index].status === 'FINISHED') {
					        			className = 'portlet-color-neutral';
					        		} else if(data[index].status === 'SUCCESS') {
					        			className = 'portlet-color-success';
					        		}
					        		
					        		$b = $('<b></b>', {
					        			class : className,
					        			text : data[index].status
					        		});
					        		data[index].status = $b[0].outerHTML;
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
			            targets :  4
			        }
			        , {
			        	title : 'Stopped',
			        	fieldName : 'stopTimestamp',
			            targets :  5
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
			        }],
			        order : [[0, "asc"]],
			        selectStyle : "multi",
			    	toolbar : $('#geoanalytics-functions-execution-monitor-toolbar')
			    });
			    
			    // Get Widget Instance
			    this.dataTable = $('#geoanalytics-functions-execution-monitor-datatable').data("dt-PortletDataTable");
				
			},
			
			init : function(spinnerHideCallback, spinnerShowCallback) {
				this.spinnerHideFunction = spinnerHideCallback;
				this.spinnerShowFunction = spinnerShowCallback;
				
				$('#settingsResponsive, #adminSettingsButtonContainer').one('click', function(){
					$('#adminSettingsButtonContainer').attr('data-toggle','modal');
					$('#adminSettingsButtonContainer').attr('data-target','#geoanalytics-functions-execution-monitor-modal');
					
					functionExecutionMonitor.loadContent($(this));
				});
			},
			
			initUIBindings : function() {
//			    $("#settingsResponsive, #adminSettingsButtonContainer").on("click", function() {
//			    	functionExecutionMonitor.getFunctionExecutionMonitor();
//			    });
			    
			    $("#geoanalytics-functions-execution-monitor-refresh-execution-status-button").on("click", function() {
			    	functionExecutionMonitor.reload();
			    });
			    
			    $("#geoanalytics-functions-execution-monitor-clear-button").on("click", function() {
			    	functionExecutionMonitor.clear();
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
		    
		    spinnerShowFunction : null
	};
	
	window.functionExecutionMonitor = functionExecutionMonitor;
})();