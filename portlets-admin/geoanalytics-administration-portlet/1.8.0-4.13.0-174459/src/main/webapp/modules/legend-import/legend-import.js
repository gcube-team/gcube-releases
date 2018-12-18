(function() {

	"use strict";

	var importLegend = {
	    container : $("#geoadmin-import-legend"),
	    notificator : $("#geoadmin-import-legend-notificator"),
	    init : function() {
		    this.container.load(window.config.contextPath + "modules/legend-import/import-legend-modal.jsp", function() {
		    	importLegend.loadCSS();
			    importLegend.initBindings();
			    importLegend.spinner = $("#geoadmin-import-legend .spinner");
		    });
	    },
	    loadCSS : function() {
		    $("<link/>", {
		        rel : "stylesheet",
		        type : "text/css",
		        href : window.config.contextPath + "/modules/import/import-legend.css"
		    }).appendTo("head");
	    },
	    getImportLegend : function() {
	    	
		    var url = window.config.createResourceURL('import/status');
    
		    // Create datatable
		    
		    $('#geoadmin-import-legend-datatable').PortletDataTable({
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
				        importLegend.showMessage("Last updated: " + importLegend.getCurrentTime(), "success");
					    
					    return data;
			        },
			        beforeSend : function() {
				        importLegend.spinner.show();
			        },
			        error : function(jqXHR, exception) {
				        importLegend.notificator.errorHandling($("#geoadmin-import-legend-notificator"), jqXHR, exception);
			        },
			        complete : function() {
				        importLegend.spinner.hide();
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
		    	toolbar : $("#geoadmin-import-legend-toolbar"),
		    });

		    // Get Widget Instance
		    
		    this.dataTable = $('#geoadmin-import-legend-datatable').data("dt-PortletDataTable");
	    },
	    clear : function() {
		    var url = window.config.createResourceURL('import/status/clear');
		    
		    var rows = importLegend.dataTable.getSelectedRowsData();
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
			        importLegend.spinner.show();
		        },
		        success : function(data) {
			        importLegend.dataTable.refreshData();
		        },
		        error : function(jqXHR, exception) {
			        importLegend.notificator.errorHandling($("#geoadmin-import-legend-notificator"), jqXHR, exception);
		        },
		        complete : function() {
			        importLegend.spinner.hide();
		        },
		        timeout : 20000
		    });
	    },
	    initBindings : function() {
		    $("#geoadmin-import-legend-button").on("click", function() {
			    importLegend.getImportLegend();
		    });
		    
		    $("#geoadmin-import-legend-refresh-button").on("click", function() {
		    	importLegend.dataTable.refreshData();
		    });
		    
		    $("#geoadmin-import-legend-clear-button").on("click", function() {
			    importLegend.clear();
		    });	
	    },
	    showMessage : function(text, type) {
		    window.notificator.setText($("#geoadmin-import-legend-notificator"), text, type);
	    },
	    getCurrentTime : function(){
	        var date = new Date();
	        return date.toJSON().slice(0, 10).replace(new RegExp("-", 'g'), "/").split("/").reverse().join("/") + " " + date.toJSON().slice(11, 19);	
	    }
	};
	
	window.importLegend = importLegend;
	
})();