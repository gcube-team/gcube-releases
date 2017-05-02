(function() {
	'use strict';

	var styles = {
	    stylesManagement : $("#stylesManagement"),
	    notificator : null,
	    spinner : null,
	    dataTable : null,
	    selectedRow : null,
	    init : function() {
		    $('.stylesTab').one('click', function() {
			    styles.loadStyleTab();
		    });
	    },
	    loadStyleTab : function() {
		    this.stylesManagement.load(window.config.contextPath + "modules/styles/stylesManagement.jsp", function() {
			    styles.notificator = $("#geoadmin-styles-notificator");
			    styles.spinner = $("#stylesManagement .spinner");
			    styles.disableButtons();
			    styles.initUIbindings();
			    styles.initValidation();
 			    styles.getStyles();
		    });
	    },
	    getStyles : function() {
		    var url = window.config.createResourceURL('styles/listStyles');
		    $.ajax({
		        url : url,
		        type : 'GET',
		        cache : false,
		        dataType : "json",
		        beforeSend : function() {
			        styles.spinner.show();
		        },
		        success : function(data) {
			        styles.createDataTable(data);
			        $("#geoadmin-create-style-button").attr("disabled", false);
		        },
		        error : function(jqXHR, exception) {
			        styles.errorHandling(jqXHR, exception);
		        },
		        complete : function() {
			        styles.spinner.hide();
		        },
		        timeout : 20000
		    });
	    },
	    createStyle : function(name, description, file) {
		    var url = window.config.createResourceURL('styles/createStyle');
		    
		    var importFormData = new FormData();
			importFormData.append("styleImportFile", file);					
			importFormData.append("styleImportProperties", new Object([JSON.stringify({
				"name"	: 	name,
				"description" : description,
			})], {
				type: "application/json"
			}));
		    
		    var style = {
		        name : name,
		        description : description,
		        content : file
		    };

		    $.ajax({
		        url : url,
		        type : 'POST',
		        data : importFormData,
		        contentType : false,
		        processData : false,
		        beforeSend : function() {
			        $("#geoadmin-create-style-modal").modal('hide');
			        styles.spinner.show();
		        },
		        success : function(id) {
			        styles.dataTable.row.add([ null, name, description, id ]).draw();
			        styles.showMessage("Style \"" + name + "\" has been created successfully!", "success");
		        },
		        error : function(jqXHR, exception) {
			        styles.errorHandling(jqXHR, exception);
		        },
		        complete : function() {
			        styles.spinner.hide();
		        },
		        timeout : 20000
		    });
	    },
	    deleteStyle : function(id, name) {
		    var url = window.config.createResourceURL('styles/deleteStyle');

		    $.ajax({
		        url : url,
		        type : 'POST',
		        cache : false,
		        data : id,
		        contentType : "application/json",
		        beforeSend : function() {
			        $("#geoadmin-delete-style-modal").modal('hide');
			        styles.spinner.show();
		        },
		        success : function(data) {
			        styles.dataTable.row(styles.selectedRow).remove().draw();
			        styles.selectedRow = null;
			        styles.disableButtons();
			        styles.showMessage("Style \"" + name + "\" has been deleted successfully!", "success");
		        },
		        error : function(jqXHR, exception) {
			        styles.errorHandling(jqXHR, exception);
		        },
		        complete : function() {
			        styles.spinner.hide();
		        },
		        timeout : 20000
		    });
	    },
	    editStyle : function(id, name, description) {
		    var url = window.config.createResourceURL('styles/editStyle');
		    var style = {
		        id : id,
		        name : name,
		        description : description
		        
		    }

		    $.ajax({
		        url : url,
		        type : 'POST',
		        cache : false,
		        data : JSON.stringify(style),
		        contentType : "application/json",
		        beforeSend : function() {
			        $("#geoadmin-edit-style-modal").modal('hide');
			        styles.spinner.show();
		        },
		        success : function(data) {
			        var selectedRowData = styles.dataTable.row(styles.selectedRow).data();
			        var message = "";
			        if (selectedRowData[1] !== name) {
				        message += "Style \"" + selectedRowData[1] + "\" has been renamed to \"" + name + "\"  successfully!<br>";
			        }

			        if (selectedRowData[2] !== description) {
				        message += "Style \"" + selectedRowData[1] + "\" description has been updated successfully!";
			        }
			        styles.showMessage(message, "success");
			        styles.dataTable.row(styles.selectedRow).data([ null, name, description, id ]);
		        },
		        error : function(jqXHR, exception) {
			        styles.errorHandling(jqXHR, exception);
		        },
		        complete : function() {
			        styles.spinner.hide();
		        },
		        timeout : 20000
		    });
	    },
	    createDataTable : function(jsonData) {
		    var data = []
		    for (var i = 0; i < jsonData.length; i++) {
			    data.push([ "", jsonData[i].name, jsonData[i].description, jsonData[i].id ]);
		    }

		    this.dataTable = $('#geoadmin-styles-datatable').DataTable({
		        data : data,
		        columnDefs : [ {
		            targets : 0,
		            orderable : false,
		            searchable : false,
		            className : "geoadmin-style-datatable-cell geoadmin-style-datatable-checkbox"
		        }, {
		            targets : [ 1, 2 ],
		            className : "geoadmin-style-datatable-cell"
		        }, {
		            targets : [ 3 ],
		            visible : false
		        }, {
		            targets : [ 0, 1, 2, 3 ],
		            render : function(data, type, full, meta) {
			            return data == null ? "" : data;
		            }
		        } ],
		        select : {
			        style : 'os'
		        },
		        dom : '<"toolbar">frtip'
		    });

		    $("#geoadmin-styles-datatable_wrapper div.toolbar").append($("#geoadmin-style-toolbar"));
	    },
	    initUIbindings : function() {
		    var previousCheckbox = null;

		    // Add tick when selecting a row

		    $(document.body).on("click", "td.geoadmin-style-datatable-cell", function() {
			    var currentCheckbox = $(this).closest("tr").find(".geoadmin-style-datatable-checkbox");

			    if (previousCheckbox != null) {
				    previousCheckbox.html("");
			    }

			    styles.selectedRow = $(this).closest("tr");

			    if (styles.selectedRow.hasClass("selected")) {
				    previousCheckbox = currentCheckbox;
				    currentCheckbox.html("&#10004;");
				    styles.enableButtons();
			    } else {
				    styles.selectedRow = null;
				    previousCheckbox = null;
				    currentCheckbox.html("");
				    styles.disableButtons();
			    }
		    });

		    $(document.body).on("click", '#geoadmin-create-style-button', function() {
		    	styles.resetForm($("#geoadmin-create-style-form"));
		    });

		    $(document.body).on("click", '#geoadmin-edit-style-button', function() {
		    	styles.resetForm($("#geoadmin-edit-style-form"));
			    var selectedRowData = styles.dataTable.row(styles.selectedRow).data();
			    $("#geoadmin-edit-style-name").val(selectedRowData[1]);
			    $("#geoadmin-edit-style-description").val(selectedRowData[2]);
		    });

		    $(document.body).on("click", '#geoadmin-delete-style-button', function() {
			    var selectedRowData = styles.dataTable.row(styles.selectedRow).data();
			    var text = "Are you sure you want to delete \"" + selectedRowData[1] + "\" ?";
			    $("#geoadmin-delete-style-modal-text").html(text);
		    });

		    $(document.body).on("click", "#geoadmin-create-style-modal-submit", function() {
			    var name = $("#geoadmin-create-style-name").val();
			    var description = $("#geoadmin-create-style-description").val();
			    var fileXml = document.getElementById('geoadmin-create-style-content-browseButton').files[0];
			    styles.createStyle(name, description, fileXml);
		    });

		    $(document.body).on("click", "#geoadmin-edit-style-modal-submit", function() {
			    var selectedRowData = styles.dataTable.row(styles.selectedRow).data();
			    var id = selectedRowData[3];
			    var name = $("#geoadmin-edit-style-name").val();
			    var description = $("#geoadmin-edit-style-description").val();
			    styles.editStyle(id, name, description);
		    });

		    $(document.body).on("click", "#geoadmin-delete-style-modal-submit", function() {
			    if (styles.selectedRow != null) {
				    var selectedRowData = styles.dataTable.row(styles.selectedRow).data();
				    styles.deleteStyle(selectedRowData[3], selectedRowData[1]);
			    }
		    });

		    $(document.body).on("change", "#geoadmin-create-style-content-browseButton", function() {
		    	var filename = this.value;
		    	var separators = ['/','\\\\'];
		    	filename = filename.split(new RegExp(separators.join('|'), ''))
		 
		    	$("#geoadmin-create-style-content").val(filename[filename.length -1]);
		    	var valid = $('#geoadmin-create-style-form').valid();
		    	$("#geoadmin-create-style-modal-submit").attr("disabled", !valid);
		    });
	    },
	    enableButtons : function() {
		    $("#geoadmin-edit-style-button").attr("disabled", false);
		    $("#geoadmin-delete-style-button").attr("disabled", false);
	    },
	    disableButtons : function() {
		    $("#geoadmin-edit-style-button").attr("disabled", true);
		    $("#geoadmin-delete-style-button").attr("disabled", true);
	    },
	    showMessage : function(text, type) {
		    window.notificator.setText($("#geoadmin-styles-notificator"), text, type);
	    },
	    errorHandling : function(jqXHR, exception) {
		    window.notificator.errorHandling($("#geoadmin-styles-notificator"), jqXHR, exception);
	    },
	    initValidation : function() {
		    $.getScript(window.config.contextPath + "modules/styles/stylesValidation.js");
	    },
	    resetForm : function(form) {
	    	form.closest(".modal").find(".modal-footer").find("button[type='button']").attr("disabled", true);
	    	form.find("*").removeClass("error");
	    	form.find('.help-inline > label').remove();
	    	form[0].reset();
	    }
	};

	window.styles = styles;
})();