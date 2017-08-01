(function() {
	'use strict';

	var styles = {
	    stylesManagement : $("#geoadmin-styles"),
	    notificator : null,
	    spinner : null,
	    dataTable : null,
	    init : function() {
		    $('.stylesTab').one('click', function() {
		    	styles.loadCSS();
			    styles.loadStyleTab();
		    });
	    },
	    loadCSS : function() {
		    $("<link/>", {
		        rel : "stylesheet",
		        type : "text/css",
		        href : window.config.contextPath + "/modules/styles/stylesManagement.css"
		    }).appendTo("head");
	    },
	    loadStyleTab : function() {
		    this.stylesManagement.load(window.config.contextPath + "modules/styles/stylesManagement.jsp", function() {
			    styles.notificator = $("#geoadmin-styles-notificator");
			    styles.spinner = $("#geoadmin-styles .spinner");
			    styles.initUIbindings();
			    styles.initValidation();
 			    styles.getStyles();
		    });
	    },
	    getStyles : function() {
		    var url = window.config.createResourceURL('styles/listStyles');

		    // Create datatable
		    
		    $('#geoadmin-styles-datatable').PortletDataTable({
		    	ajax : {
			        url : url,
			        type : 'GET',
			        cache : false,
			        dataType : "json",
			        beforeSend : function() {
				        styles.spinner.show();
			        },
			        dataSrc : function(data) {				// success callback
				        $("#geoadmin-create-style-button").attr("disabled", false);
			        },
			        error : function(jqXHR, exception) {
				        styles.errorHandling(jqXHR, exception);
			        },
			        complete : function() {
				        styles.spinner.hide();
			        },
			        timeout : 20000		    		
		    	},
		    	columnDefs : [ 
		        {
		        	title : "Name",
		        	fieldName : "name",
		        },
		        {
		        	title : "Description",
		        	fieldName : "description"
		        },
		    	{
		        	title : "ID",
		            visible : false,
		            fieldName : "id"
		        }],
		        rowId : 'id',
		        order : [[0, "asc"]],
		    	toolbar : $('#geoadmin-styles-toolbar')
		    });

		    // Get Widget Instance
		    
		    this.dataTable = $('#geoadmin-styles-datatable').data("dt-PortletDataTable");
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
			        styles.dataTable.refreshData();			        
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
			        styles.dataTable.refreshData();		
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
			        var selectedRowData = styles.dataTable.getSelectedRowData();	
			        styles.dataTable.refreshData();	
			        styles.showMessage("Style \"" + selectedRowData.name + "\" has been updated successfully!", "success");
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
	    initUIbindings : function() {
		    $(document.body).on("click", '#geoadmin-refresh-style-button', function() {
		    	styles.dataTable.refreshData();
		    });
		    
		    $(document.body).on("click", '#geoadmin-create-style-button', function() {
		    	styles.resetForm($("#geoadmin-create-style-form"));
		    });

		    $(document.body).on("click", '#geoadmin-edit-style-button', function() {
		    	styles.resetForm($("#geoadmin-edit-style-form"));
			    var selectedRowData = styles.dataTable.getSelectedRowData();
			    $("#geoadmin-edit-style-name").val(selectedRowData.name);
			    $("#geoadmin-edit-style-description").val(selectedRowData.description);
		    });

		    $(document.body).on("click", '#geoadmin-delete-style-button', function() {
			    var selectedRowData = styles.dataTable.getSelectedRowData();
			    var text = "Are you sure you want to delete \"" + selectedRowData.name + "\" ?";
			    $("#geoadmin-delete-style-modal-text").html(text);
		    });

		    $(document.body).on("click", "#geoadmin-create-style-modal-submit", function() {
			    var name = $("#geoadmin-create-style-name").val();
			    var description = $("#geoadmin-create-style-description").val();
			    var fileXml = document.getElementById('geoadmin-create-style-content-browseButton').files[0];
			    styles.createStyle(name, description, fileXml);
		    });

		    $(document.body).on("click", "#geoadmin-edit-style-modal-submit", function() {
			    var selectedRowData = styles.dataTable.getSelectedRowData();
			    var id = selectedRowData.id;
			    var name = $("#geoadmin-edit-style-name").val();
			    var description = $("#geoadmin-edit-style-description").val();
			    styles.editStyle(id, name, description);
		    });

		    $(document.body).on("click", "#geoadmin-delete-style-modal-submit", function() {
			    if (styles.dataTable.getSelectedRow() != null) {
				    var selectedRowData = styles.dataTable.getSelectedRowData();
				    styles.deleteStyle(selectedRowData.id, selectedRowData.name);
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