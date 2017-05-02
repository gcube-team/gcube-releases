(function() {

	"use strict";

	var importMonitor = {
	    importMonitor : $("#geoadmin-import-monitor"),
	    init : function() {
		    this.importMonitor.load(window.config.contextPath + "modules/import/importMonitor.jsp", function() {
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
			        importMonitor.spinner.show()
		        },
		        success : function(data) {
			        importMonitor.createDataTable(data);	
			        var date = new Date();
			        var dateTime = date.toJSON().slice(0,10).replace(new RegExp("-", 'g'),"/" ).split("/").reverse().join("/")+" "+date.toJSON().slice(11,19);			        
			        window.notificator.setText($("#geoadmin-import-monitor-notificator"), "Last updated: " + dateTime, "success");
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
	    createDataTable : function(jsonData) {
		    var data = []
		    for (var i = 0; i < jsonData.length; i++) {
			    var status;

			    switch (jsonData[i].status) {
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

			    data.push([ jsonData[i].type, jsonData[i].name, jsonData[i].geocodeSystem, jsonData[i].fileName, jsonData[i].creationDate, jsonData[i].lastUpdate, status ]);
		    }

		    this.dataTable = $('#geoadmin-import-monitor-datatable').DataTable({
		        data : data,
		        destroy : true,
		        columnDefs : [ {
		            targets : '_all',
		            className : "geoadmin-import-monitor-datatable-cell",
		            render : function(data, type, full, meta) {
			            return data == null ? "" : data;
		            }
		        } ],
		        aaSorting: [[5, 'desc']],		        
		        oLanguage: {
			        sEmptyTable: "No layer imports have been submitted!",
		        },
		        select : {
			        style : 'os'
		        }
		    });
	    },
	    initBindings : function() {
		    $("#geoadmin-import-monitor-button, #geoadmin-import-monitor-modal-refresh").on("click", function() {
			    importMonitor.getImportMonitor();
		    });
	    }
	};

	window.importMonitor = importMonitor;
})();