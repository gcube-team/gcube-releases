(function() {

	"use strict";

	var importMonitor = {
	    container : $("#geoadmin-import-monitor"),
	    notificator : $("#geoadmin-import-monitor-notificator"),
	    init : function() {
		    this.container.load(window.config.contextPath + "modules/import/import-monitor.jsp", function() {
		    	importMonitor.loadCSS();
			    importMonitor.initBindings();
			    importMonitor.spinner = $("#geoadmin-import-monitor .spinner");
		    });
	    },
	    loadCSS : function() {
		    $("<link/>", {
		        rel : "stylesheet",
		        type : "text/css",
		        href : window.config.contextPath + "/modules/import/import-monitor.css"
		    }).appendTo("head");
	    },
	    getImportMonitor : function() {
	    	
		    var url = window.config.createResourceURL('import/status');
    
		    // Create datatable
		    
		    $('#geoadmin-import-monitor-datatable').PortletDataTable({
		    	ajax : {
			        url : url,
			        type : 'GET',
			        cache : false,
			        dataType : "json",
            		dataSrc : function(data) { // success callback
					    for (var i = 0; i < data.length; i++) {
					    	var layerImport = data[i];

						    switch (layerImport.status) {
							    case -1:
							    	layerImport.status = "<b class='portlet-color-error'>Failed </b>";
								    break;
							    case 0:
							    	layerImport.status = "<b class='portlet-color-neutral'>Running </b>";
								    break;
							    case 1:
							    	layerImport.status = "<b class='portlet-color-success'>Success </b>";
								    break;
							    default:
						    }
					    }
				        importMonitor.showMessage("Last updated: " + importMonitor.getCurrentTime(), "success");
					    
					    return data;
			        },
			        beforeSend : function() {
				        importMonitor.spinner.show();
			        },
			        error : function(jqXHR, exception) {
				        importMonitor.notificator.errorHandling($("#geoadmin-import-monitor-notificator"), jqXHR, exception);
			        },
			        complete : function() {
				        importMonitor.spinner.hide();
			        },
			        timeout : 20000		    		
		    	},
		    	columnDefs : [ 
		        {
		        	title : "Name",
		        	fieldName : "name"
		        },
		        {
		        	title : "Geocode System",
		        	fieldName : "geocodeSystem"
		        },
		        {
		        	title : "Source",
		        	fieldName : "source"
		        },
		        {
		        	title : "Type",
		        	fieldName : "importType"
		        },
		        {
		        	title : "Data Source",
		        	fieldName : "dataSource"
		        },
		        {
		        	title : "Creation Date",
		        	fieldName : "creationDate"
		        },
		        {
		        	title : "Status",
		        	fieldName : "status",
		        	className : "white"
		        },
		    	{
		        	title : "ID",
		        	fieldName : "id",
		            visible : false
		        }],
		        rowId : 'id',
		        order: [[ 5, "desc" ]],
		        selectStyle : "multi",
		    	toolbar : $("#geoadmin-import-monitor-toolbar"),
		    });

		    // Get Widget Instance
		    
		    this.dataTable = $('#geoadmin-import-monitor-datatable').data("dt-PortletDataTable");
	    },
	    clear : function() {
		    var url = window.config.createResourceURL('import/status/clear');
		    
		    var rows = importMonitor.dataTable.getSelectedRowsData();
		    var layerIds = [];		    

		    for (var i = 0; i < rows.length; i++) {
			    layerIds.push(rows[i].id);
		    } 
		    
		    $.ajax({
		        url : url,
		        type : 'POST',
		        data : JSON.stringify(layerIds),
		        contentType : "application/json",
		        beforeSend : function() {
			        importMonitor.spinner.show();
		        },
		        success : function(data) {
			        importMonitor.dataTable.refreshData();
		        },
		        error : function(jqXHR, exception) {
			        importMonitor.notificator.errorHandling($("#geoadmin-import-monitor-notificator"), jqXHR, exception);
		        },
		        complete : function() {
			        importMonitor.spinner.hide();
		        },
		        timeout : 20000
		    });
	    },
	    initBindings : function() {
		    $("#geoadmin-import-monitor-button").on("click", function() {
			    importMonitor.getImportMonitor();
		    });
		    
		    $("#geoadmin-import-monitor-refresh-button").on("click", function() {
		    	importMonitor.dataTable.refreshData();
		    });
		    
		    $("#geoadmin-import-monitor-clear-button").on("click", function() {
			    importMonitor.clear();
		    });	
	    },
	    showMessage : function(text, type) {
		    window.notificator.setText($("#geoadmin-import-monitor-notificator"), text, type);
	    },
	    getCurrentTime : function(){
	        var date = new Date();
	        return date.toJSON().slice(0, 10).replace(new RegExp("-", 'g'), "/").split("/").reverse().join("/") + " " + date.toJSON().slice(11, 19);	
	    }
	};
	
	window.importMonitor = importMonitor;
	
})();