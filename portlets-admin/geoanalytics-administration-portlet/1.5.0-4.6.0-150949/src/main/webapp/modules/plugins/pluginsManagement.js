(function(){
	'use strict';
	
	var plugins = {
			$clearCreatePluginFormButton : null,
			
			$clearEditPluginFormButton : null,
			
			$createPluginButton : null,
			
			$createPluginForm : null,
		    
		    $createPluginModal : null,
		    
		    $editPluginForm : null,
		    
		    $editPluginModal : null,
		    
			$pluginsManagement : $('#pluginsManagement'),
			
		    $spinner : null,

			ajaxPOST : function(theURl, beforeSendCallback, successCallback, completeCallback, theData) {
				$.ajax({
			        url : theURl,
			        type : 'POST',
			        cache : false,
			        data : JSON.stringify(theData),
			        dataType : "json",
			        beforeSend : function(xhr) {
			        	xhr.setRequestHeader("Accept", "application/json");
			        	xhr.setRequestHeader("Content-Type", "application/json");
			        	plugins.hideNotificators();
			        	beforeSendCallback();
			        },
			        success : function(data) {
			        	successCallback(data);
			        },
			        error : function(jqXHR, exception) {
			        	plugins.errorHandling(jqXHR, exception);
			        },
			        complete : function() {
			        	completeCallback();
			        },
			        timeout : 20000
			    });
			},

			ajaxPOSTForm : function(theURl, beforeSendCallback, successCallback, completeCallback, theData) {
				$.ajax({
			        url : theURl,
			        type : 'POST',
			        cache : false,
			        processData: false,
			        contentType: false,
			        data : theData,
			        beforeSend : function(xhr) {
			        	plugins.hideNotificators();
			        	beforeSendCallback();
			        },
			        success : function(data) {
			        	successCallback(data);
			        },
			        error : function(jqXHR, exception) {
			        	plugins.errorHandling(jqXHR, exception);
			        },
			        complete : function() {
			        	completeCallback();
			        },
//			        timeout : 600000
			    });
			},
			
			collectPluginCreateFormData : function() {
				var formData = new FormData();
				
				var pluginLibrary = {};
				pluginLibrary.pluginLibraryName = $('#geoadmin-create-plugin-name').val();
				var jarFile = $('#geoadmin-create-plugin-pluginLibrary-JAR').prop('files')[0];
//				$('#pluginLibraryJAR')[0].files[0];
				
				formData.append('file', jarFile);
				
				pluginLibrary.pluginMessengers = [];
				
				var pluginMessenger = {};

				pluginMessenger.name = $.trim($('#geoadmin-create-plugin-name').val());
				pluginMessenger.description = $.trim($('#geoadmin-create-plugin-description').val());
				pluginMessenger.widgetName = $.trim($('#geoadmin-create-plugin-widget-name').val());
				pluginMessenger.className = $.trim($('#geoadmin-create-plugin-className').val());
				pluginMessenger.methodName = $.trim($('#geoadmin-create-plugin-methodName').val());
				pluginMessenger.jsFileName = $.trim($('#geoadmin-create-plugin-jsFileName').val());
				pluginMessenger.configurationClass = $.trim($('#geoadmin-create-plugin-configurationClass').val());
				pluginMessenger.type = 0;

				pluginLibrary.pluginMessengers.push(pluginMessenger);
				
				formData.append('pluginLibrary', new Blob([JSON.stringify(pluginLibrary)], {type: "application/json"}));
				
				return formData;
			},
			
			collectPluginEditFormData : function() {
				var pluginLibrary = {};
				pluginLibrary.pluginLibraryName = $('#geoadmin-create-plugin-name').val();
				
				pluginLibrary.pluginMessengers = [];
				
				var pluginMessenger = {};

				pluginMessenger.name = $.trim($('#geoadmin-edit-plugin-name').val());
				pluginMessenger.description = $.trim($('#geoadmin-edit-plugin-description').val());
				pluginMessenger.widgetName = $.trim($('#geoadmin-edit-plugin-widget-name').val());
				pluginMessenger.className = $.trim($('#geoadmin-edit-plugin-className').val());
				pluginMessenger.methodName = $.trim($('#geoadmin-edit-plugin-methodName').val());
				pluginMessenger.jsFileName = $.trim($('#geoadmin-edit-plugin-jsFileName').val());
				pluginMessenger.configurationClass = $.trim($('#geoadmin-edit-plugin-configurationClass').val());
				pluginMessenger.type = 0;
				pluginMessenger.id = plugins.dataTable.getSelectedRowData().pluginId;

				pluginLibrary.pluginMessengers.push(pluginMessenger);
				
				return pluginLibrary;
			},
			
			clearCreatePluginForm : function() {
				this.$createPluginForm.find('input, textarea').val('');
				$('#pluginFileName').text('Choose file');
			},
			
			clearEditPluginForm : function() {
				this.$editPluginForm.find('input, textarea').val('');
			},
			
			createDataTable : function(jsonData) {
				var theURL = window.config.createResourceURL('plugin/listPluginsByTenantOrNullTenant');
			    
			    // Create datatable
			    $('#geoadmin-plugins-datatable').PortletDataTable({
			    	ajax :	{
				        url : theURL,
				        type : 'POST',
				        cache : false,
				        dataType : "json",
				        beforeSend : function(xhr) {
				        	xhr.setRequestHeader("Accept", "application/json");
				        	xhr.setRequestHeader("Content-Type", "application/json");
				        	
					        plugins.globalBeforeSendCallback();
				        },
				        dataSrc : function(data) {
					        
				        },
				        error : function(jqXHR, exception) {
					        plugins.errorHandling(jqXHR, exception);
				        },
				        complete : function() {
					        plugins.globalCompleteCallback();
				        },
				        timeout : 20000
					},
					columnDefs : [{
			        	title : "Name",
			        	fieldName : "pluginName",
			            targets : 0,
						width: '25%'
			        }, {
			        	title : "Description",
			        	fieldName : "pluginDescription",
			        	targets : 1,
						width: '55%'
			        }, {
			        	title : "Last Update",
			            fieldName : "updateDate",
			            targets :  2
			        },  {
			        	title : "Creation Date",
			            fieldName : "creationDate",
			            targets :  3
			        }, {
			        	title : 'Type',
			        	fieldName : "pluginType",
			        	visible : false
			        }, {
			        	title : 'Widget Name',
			        	fieldName : 'widgetName',
			        	visible : false
			        }, {
			        	title : 'Qualified Name Of Class',
			        	fieldName : 'qualifiedNameOfClass',
			        	visible : false
			        }, {
			        	title : 'Script File Name',
			        	fieldName : 'jsFileName',
			        	visible : false
			        }, {
			        	title : 'JAVA-Method Name',
			        	fieldName : 'methodName',
			        	visible : false
			        }, {
			        	title : 'Configuration Class',
			            fieldName : "configurationClass",
			            visible : false
			        }, {
			        	title : "Plugin ID",
			            visible : false,
			            fieldName : "pluginId"
			        }],
			        order : [[0, "asc"]],
//			    	data : data,
			    	toolbar : $('#geoadmin-plugins-toolbar')
			    });
			    
			    // Get Widget Instance
			    this.dataTable = $('#geoadmin-plugins-datatable').data("dt-PortletDataTable");
		    },
		    
		    createPlugin : function(pluginData) {

//				hidePluginModalMessages();
//				
//				if(!validatePluginUploadForm()) {
//					showValidationMessage();
//					return;
//				}
				
				var beforeSendCallback = plugins.globalBeforeSendCallback;
				var onCompleteCallback = function() {
					plugins.globalCompleteCallback();
					plugins.$createPluginModal.modal('hide');
				};
				
				var url = window.config.createResourceURL('plugin/upload');
				var successCallback = this.globalSuccessCallback;
				
				var pluginFormData = plugins.collectPluginCreateFormData();
				
				plugins.ajaxPOSTForm(url, beforeSendCallback, successCallback, onCompleteCallback, pluginData);
		    },
		    
		    dataToDatableRows : function(jsonData) {
		    	var dtData = [];

			    $.each(jsonData, function(index, value) {
			    	var dtDataObject = {
				    	pluginId : value.pluginId,
			    		pluginName : value.pluginName,
			    		pluginDescription : value.pluginDescription,
			    		updateDate : value.updateDate,
			    		creationDate : value.creationDate,
			    		pluginType : value.pluginType,
			    		widgetName : value.widgetName,
			    		qualifiedNameOfClass : value.qualifiedNameOfClass,
			    		jsFileName : value.jsFileName,
			    		methodName : value.methodName,
			    		configurationClass : value.configurationClass
			    	}
			    	
			    	for( var i in dtDataObject) {
			    		if(typeof dtDataObject[i] === 'undefined' || dtDataObject[i] === null) {
			    			dtDataObject[i] = '-';
			    		}
			    	}
			    
			    	dtData.push(dtDataObject);
			    });
			    
			    return dtData;
		    },
		    
		    dataTable : null,
		    
		    deletePlugin : function(pluginID) {
		    	var deletePluginURL = window.config.createResourceURL('plugin/deletePluginAndPluginLibrary');
		    	
		    	var beforeSendCallbackForDelete = this.globalBeforeSendCallback;
		    	
		    	var successCallbackForDelete = this.globalSuccessCallback;
		    	
		    	var completeCallbackForDelete = function(){
		    		plugins.globalCompleteCallback();
		    		$('#geoadmin-delete-plugin-modal').modal('hide');
		    		plugins.disableButtons();
		    	}
		    	
		    	this.ajaxPOST(deletePluginURL, beforeSendCallbackForDelete, successCallbackForDelete, completeCallbackForDelete, pluginID);
		    },
		    
		    disableButtons : function(){
		    	
		    },
		    			
			disableCreatePluginsButton : function() {
				$("#geoadmin-create-plugin-button").attr("disabled", false);
			},
			
			editModalFill : function(data) {
				$('#geoadmin-edit-plugin-name').val(data.pluginName);
				$('#geoadmin-edit-plugin-description').val(data.pluginDescription);
				$('#geoadmin-edit-plugin-widget-name').val(data.widgetName);
				$('#geoadmin-edit-plugin-className').val(data.qualifiedNameOfClass);
				$('#geoadmin-edit-plugin-methodName').val(data.methodName);
				$('#geoadmin-edit-plugin-jsFileName').val(data.jsFileName);
				$('#geoadmin-edit-plugin-configurationClass').val(data.configurationClass);
			},
			
			editPlugin : function(data) {
				var editPluginURL = window.config.createResourceURL('plugin/updatePlugin');
				
				var beforeSendCallback = this.globalBeforeSendCallback;
				
				var successCallback = this.globalSuccessCallback;
			    
			    var completeCallback = function(){
			    	plugins.globalCompleteCallback();
			    	plugins.$editPluginModal.modal('hide');
			    	plugins.disableButtons();
			    };
			    
			    this.ajaxPOST(editPluginURL, beforeSendCallback, successCallback, completeCallback, data);
			},
						
		    errorHandling : function(jqXHR, exception) {
			    window.notificator.errorHandling($("#geoadmin-plugins-notificator"), jqXHR, exception);
		    },
		    
		    internalServerError : function(){
		    	$('#InternalServerErrorModal').modal('show');
		    },
			
		    getPlugins : function() {
			    plugins.createDataTable();
		    },
		    
		    globalBeforeSendCallback : function() {
		    	plugins.$spinner.show();
		    },
		    
		    globalSuccessCallback : function(responseData) {
				if(responseData.status === 'Success') {
					plugins.reloadDataTable();
				} else if(responseData.status === 'Failure') {
					plugins.internalServerError();
				}
			},
		    
		    globalCompleteCallback : function() {
		    	plugins.$spinner.hide();
		    	plugins.disableButtons();
		    },
		    
		    hideNotificators : function() {
		    	$("#geoadmin-plugins-notificator").hide();
		    	$('#InternalServerErrorModal').modal('hide');
		    },
		    
			init : function(){
				$('.pluginsTab').one('click', function() {
					plugins.loadTab();
				});
			},
			
			initUIbindings : function() {
				this.$createPluginButton = $('#geoadmin-create-plugin-form');
				
				this.$createPluginForm = $('#geoadmin-create-plugin-form');
				
				this.$editPluginForm = $('#geoadmin-edit-plugin-form');
				
				this.$clearCreatePluginFormButton = $('#geoadmin-edit-plugin-modal-clear-form');
				this.$clearCreatePluginFormButton.click(plugins.clearCreatePluginForm);
				
				this.$clearEditPluginFormButton = $('#geoadmin-edit-plugin-modal-clear-form');
				this.$clearEditPluginFormButton.click(plugins.clearEditPluginForm);
				
				this.$createPluginModal = $('#geoadmin-create-plugin-modal');
				
			    this.$editPluginModal = $('#geoadmin-edit-plugin-modal');
			    
			    $(document.body).on('click', '#geoadmin-create-plugin-modal-clear-form', function(){
			    	$('#geoadmin-create-plugin-form').find('input, textarea').val('');
					$('#pluginFileName').text('Choose file');
					//disable submit button
					$('#geoadmin-create-plugin-modal-submit').attr('disabled', true);
			    });
			    
			    $(document.body).on('click', '#geoadmin-edit-plugin-modal-clear-form', function(){
			    	$('#geoadmin-edit-plugin-form').find('input, textarea').val('');
			    	//disable submit button
			    	$('#geoadmin-edit-plugin-modal-submit').attr('disabled', true);
			    });
				
				$(document.body).on("click", '#geoadmin-refresh-plugins-button', function() {
					plugins.reloadDataTable();
			    });
				
				$(document.body).on("click", '#geoadmin-delete-plugin-modal-submit', function() {
					if (plugins.dataTable.getSelectedRow() != null) {
					    var selectedRowData = plugins.dataTable.getSelectedRowData();
					    if(typeof selectedRowData !== 'undefined' && selectedRowData !== null) {
					    	plugins.deletePlugin(selectedRowData.pluginId);
					    }
				    }
			    });

			    $(document.body).on("click", '#geoadmin-delete-plugin-button', function() {
				    $("#plugiNameToDelete").text(plugins.dataTable.getSelectedRowData().pluginName);
			    });
			    
//			    $(document.body).on('click', '#geoadmin-create-plugin-go-to-pluginLibrary-JAR', function(event){
//			    	event.preventDefault();
//			    	$('#geoadmin-create-plugin-pluginLibrary-JAR').click();
//			    });
			    
			    $(document.body).on('click', '#geoadmin-create-plugin-modal-submit', function(event) {
					plugins.createPlugin(plugins.collectPluginCreateFormData());
			    });
			    
			    $(document.body).on('click', '#geoadmin-edit-plugin-button', function(event) {
			    	plugins.editModalFill(plugins.dataTable.getSelectedRowData());
			    });
			    
			    $(document.body).on('click', '#geoadmin-edit-plugin-modal-submit', function(event) {
			    	//enabel plugin button
			    	$('#geoadmin-edit-plugin-modal-submit').attr('disabled', false);
			    	plugins.editPlugin(plugins.collectPluginEditFormData());
			    });
			    
			    $('#geoadmin-create-plugin-pluginLibrary-JAR').on('change', function(){
			    	var fileName = $('#geoadmin-create-plugin-pluginLibrary-JAR').val().split('\\').pop();
					if(fileName === '')
						fileName ='Choose file';
					$('#pluginFileName').text(fileName);
					$('#pluginFileName').attr('title',fileName);
			    });
			    
			    this.$createPluginButton.on('click', function() {
			    	
			    });
			},
			
			initValidation : function() {
				$.getScript(window.config.contextPath + "modules/plugins/pluginsManagementValidation.js");
			},
			
		    loadTab : function() {
			    this.$pluginsManagement.load(window.config.contextPath + "modules/plugins/pluginsManagement.jsp", function() {
			    	plugins.notificator = $("#geoadmin-plugins-notificator");
			    	plugins.$spinner = $("#pluginsManagement .spinner");
			    	plugins.disableButtons();
			    	plugins.initUIbindings();
			    	plugins.initValidation();
			    	plugins.getPlugins();
			    });
		    },
		    
		    reloadDataTable : function() {
		    	this.dataTable.refreshData();
		    }
	};
	
	window.plugins = plugins;
})();