(function() {

	"use strict";

	var importMonitor = {
	    container : $("#geoadmin-import-monitor"),
	    init : function() {
		    this.container.load(window.config.contextPath + "modules/import/importMonitor.jsp", function() {
			    importMonitor.initBindings();
			    importMonitor.spinner = $("#geoadmin-import-monitor .spinner");
		    });
	    },
	    getImportMonitor : function() {
		    var url = window.config.createResourceURL('import/status');
		    $.ajax({
		        url : url,
		        type : 'GET',
		        cache : false,
		        dataType : "json",
		        beforeSend : function() {
			        importMonitor.spinner.show();
		        },
		        success : function(data) {
			        importMonitor.createDataTable(data);
			        window.notificator.setText($("#geoadmin-import-monitor-notificator"), "Last updated: " + importMonitor.getCurrentTime(), "success");
		        },
		        error : function(jqXHR, exception) {
			        window.notificator.errorHandling($("#geoadmin-import-monitor-notificator"), jqXHR, exception);
		        },
		        complete : function() {
			        importMonitor.spinner.hide();
			        importMonitor.disableButtons();
		        },
		        timeout : 20000
		    });
	    },
	    createDataTable : function(jsonData) {
		    var data = []
		    for (var i = 0; i < jsonData.length; i++) {
		    	var layerImport = jsonData[i];
			    var status;

			    switch (layerImport.status) {
				    case -1:
					    status = "<b class='portlet-color-error'>Failed </b>";
					    break;
				    case 0:
					    status = "<b class='portlet-color-neutral'>Running </b>";
					    break;
				    case 1:
					    status = "<b class='portlet-color-success'>Success </b>";
					    break;
				    default:
			    }

			    function splitDate(dateTime) {
				    var splitDate = dateTime.split(" ");
				    var date = splitDate[0].split("-"); 			// get dd-MM-yyyy
				    var time = splitDate[1]; 						// get time HH:mm
				    return date[2] + date[1] + date[0] + time;
			    }

			    var creationDate = "<span style='display:none'>" + splitDate(layerImport.creationDate) + "</span>" + layerImport.creationDate;

			    data.push([ "",
					    	layerImport.name, 
					    	layerImport.geocodeSystem, 
					    	layerImport.source,
					    	layerImport.importType, 
					    	layerImport.dataSource, 
							creationDate, 
							status,
							layerImport.id]);
		    }

		    this.dataTable = $('#geoadmin-import-monitor-datatable').DataTable({
		        data : data,
		        destroy : true,
		        autoWidth : false,
		        aaSorting : [ [ 6, 'desc' ] ],
		        columnDefs : [ {
		            targets : [ 0, 1, 2, 3, 4, 5, 6 ],
		            className : "geoadmin-import-monitor-datatable-cell",
		            render : function(data, type, full, meta) {
			            return data == null ? "" : data;
		            }
		        }, {
		            targets : 0,
		            orderable : false
		        }, {
		            targets : 7,
		            className : "geoadmin-import-monitor-datatable-cell white"
		        }, {
		            targets : 8,			// last column - id
		            visible : false,
		        } ],
		        oLanguage : {
			        sEmptyTable : "No layer imports have been submitted!"
		        },
		        select : {
			        style : 'multi'
		        }
		    });	 
		    
		    this.styling();
	    },
	    clear : function() {
		    var url = window.config.createResourceURL('import/status/clear');
		    
		    var rows = importMonitor.dataTable.rows(".selected").data();
		    var layerIds = [];		    

		    for (var i = 0; i < rows.length; i++) {
			    layerIds.push(rows[i][8]);
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
			        importMonitor.dataTable.rows(".selected").remove().draw();
			        window.notificator.setText($("#geoadmin-import-monitor-notificator"), "Last updated: " + importMonitor.getCurrentTime(), "success");
		        },
		        error : function(jqXHR, exception) {
			        window.notificator.errorHandling($("#geoadmin-import-monitor-notificator"), jqXHR, exception);
		        },
		        complete : function() {
			        importMonitor.spinner.hide();
		        },
		        timeout : 20000
		    });
	    },
	    styling : function(){
	    	if(this.getRowsCount() > 0){
	    		$("#geoadmin-import-monitor-datatable tr > td:first-child").addClass("checkbox");
	    	}	    	
	    },
	    initBindings : function() {
		    $("#geoadmin-import-monitor-button, #geoadmin-import-monitor-modal-refresh").on("click", function() {
			    importMonitor.getImportMonitor();
		    });

		    $("#geoadmin-import-monitor-modal-clear").on("click", function() {
			    importMonitor.clear();
		    });
		    
		    $("#geoadmin-import-monitor-datatable-select-all").on("click", function() {
			    var selectedRows = $(this).closest("table").find("tbody").find("tr");

		    	if(importMonitor.getSelectedRowsCount() == importMonitor.getRowsCount()){	
		    		importMonitor.dataTable.rows().deselect();
				    importMonitor.clearCheckBox(selectedRows);
		    	}else{
		    		$('#geoadmin-import-monitor-datatable-select-all-checkbox').prop('checked', true);
		    		importMonitor.dataTable.rows().select();
				    importMonitor.tickCheckBox(selectedRows);
		    	}
		    });		
		    
		    $(document.body).on("click", "td.geoadmin-import-monitor-datatable-cell", function() {
			    var selectedRow = $(this).closest("tr");

			    if (selectedRow.hasClass("selected")) {
				    importMonitor.tickCheckBox(selectedRow);
			    } else {
				    importMonitor.clearCheckBox(selectedRow);
			    }
		    });
	    },
	    tickCheckBox : function(rows) {
	    	importMonitor.enableButtons();	    		    	
	    	rows.find(".checkbox").html("&#10004;");
	    },
	    clearCheckBox : function(rows) {
    		$('#geoadmin-import-monitor-datatable-select-all-checkbox').prop('checked', false);

	    	if(this.getSelectedRowsCount() < 1 ){
	    		importMonitor.disableButtons();
	    	}

	    	rows.find(".checkbox").html("");
	    },
	    enableButtons : function() {
		    $("#geoadmin-import-monitor-modal-clear").attr("disabled", false);
	    },
	    disableButtons : function() {
		    $("#geoadmin-import-monitor-modal-clear").attr("disabled", true);
	    },
	    getSelectedRowsCount : function(){
	    	return importMonitor.dataTable.rows('.selected').count();
	    },
	    getRowsCount : function(){
	    	return importMonitor.dataTable.rows().count();
	    },
	    getCurrentTime : function(){
	        var date = new Date();
	        return date.toJSON().slice(0, 10).replace(new RegExp("-", 'g'), "/").split("/").reverse().join("/") + " " + date.toJSON().slice(11, 19);	
	    }
	};

	window.importMonitor = importMonitor;
})();