(function() {
	
	'use strict';

	var tags = {
	    tagsManagement : $("#geoadmin-tags"),
	    notificator : null,
	    spinner : null,
	    dataTable : null,
	    init : function() {
		    $('.tagsTab').one('click', function() {
		    	tags.loadCSS();
			    tags.loadTab();
		    });
	    },
	    loadCSS : function() {
		    $("<link/>", {
		        rel : "stylesheet",
		        type : "text/css",
		        href : window.config.contextPath + "/modules/tags/tags-management.css"
		    }).appendTo("head");
	    },
	    loadTab : function() {
		    this.tagsManagement.load(window.config.contextPath + "modules/tags/tags-management.jsp", function() {
			    tags.notificator = $("#geoadmin-tags-notificator");
			    tags.spinner = $("#geoadmin-tags .spinner");
			    tags.initUIbindings();
			    tags.initValidation();
			    tags.getTags();
		    });
	    },
	    getTags : function() {
		    var url = window.config.createResourceURL('tags/listTags');	   
	    	
		    // Create datatable
		    
		    $('#geoadmin-tags-datatable').PortletDataTable({
		    	ajax :	{
			        url : url,
			        type : 'GET',
			        cache : false,
			        dataType : "json",
			        beforeSend : function() {
				        tags.spinner.show();
			        },
			        dataSrc : function(data) {				// success callback
				        $("#geoadmin-create-tag-button").attr("disabled", false);
			        },
			        error : function(jqXHR, exception) {
				        tags.errorHandling(jqXHR, exception);
			        },
			        complete : function() {
				        tags.spinner.hide();
			        },
			        timeout : 20000
				},
		    	columnDefs : [ 
		        {
		        	title : "Name",
		        	fieldName : "name"
		        },
		        {
		        	title : "Description",
		        	fieldName : "description"
		        },
		    	{
		        	title : "ID",
		        	fieldName : "id",
		            visible : false
		        }],
		        rowId : 'id',
		        order : [[0, "asc"]],
		    	toolbar : $("#geoadmin-tags-toolbar")
		    });

		    // Get Widget Instance
		    
		    this.dataTable = $('#geoadmin-tags-datatable').data("dt-PortletDataTable");
	    },
	    createTag : function(name, description) {
		    var url = window.config.createResourceURL('tags/createTag');
		    var tag = {
		        name : name,
		        description : description
		    };

		    $.ajax({
		        url : url,
		        type : 'POST',
		        cache : false,
		        data : JSON.stringify(tag),
		        contentType : "application/json",
		        beforeSend : function() {
			        $("#geoadmin-create-tag-modal").modal('hide');
			        tags.spinner.show();
		        },
		        success : function(id) {
			        tags.dataTable.refreshData();
			        tags.showMessage("Tag \"" + name + "\" has been created successfully!", "success");
		        },
		        error : function(jqXHR, exception) {
			        tags.errorHandling(jqXHR, exception);
		        },
		        complete : function() {
			        tags.spinner.hide();
		        },
		        timeout : 20000
		    });
	    },
	    deleteTag : function(id, name) {
		    var url = window.config.createResourceURL('tags/deleteTag');

		    $.ajax({
		        url : url,
		        type : 'POST',
		        cache : false,
		        data : id,
		        contentType : "application/json",
		        beforeSend : function() {
			        $("#geoadmin-delete-tag-modal").modal('hide');
			        tags.spinner.show();
		        },
		        success : function(data) {
			        tags.dataTable.refreshData();
			        tags.showMessage("Tag \"" + name + "\" has been deleted successfully!", "success");
		        },
		        error : function(jqXHR, exception) {
			        tags.errorHandling(jqXHR, exception);
		        },
		        complete : function() {
			        tags.spinner.hide();
		        },
		        timeout : 20000
		    });
	    },
	    editTag : function(id, name, description) {
		    var url = window.config.createResourceURL('tags/editTag');
		    var tag = {
		        id : id,
		        name : name,
		        description : description
		    }

		    $.ajax({
		        url : url,
		        type : 'POST',
		        cache : false,
		        data : JSON.stringify(tag),
		        contentType : "application/json",
		        beforeSend : function() {
			        $("#geoadmin-edit-tag-modal").modal('hide');
			        tags.spinner.show();
		        },
		        success : function(data) {
			        var selectedRowData = tags.dataTable.getSelectedRowData();
			        tags.dataTable.refreshData();
			        tags.showMessage("Tag \"" + selectedRowData.name + "\" has been updated successfully!", "success");
		        },
		        error : function(jqXHR, exception) {
			        tags.errorHandling(jqXHR, exception);
		        },
		        complete : function() {
			        tags.spinner.hide();
		        },
		        timeout : 20000
		    });
	    },
	    initUIbindings : function() {
		    $(document.body).on("click", '#geoadmin-refresh-tag-button', function() {
			    tags.dataTable.refreshData();
		    });
		    
		    $(document.body).on("click", '#geoadmin-create-tag-button', function() {
			    tags.resetForm($("#geoadmin-create-tag-form"));
		    });

		    $(document.body).on("click", '#geoadmin-edit-tag-button', function() {
			    tags.resetForm($("#geoadmin-edit-tag-form"));
			    var selectedRowData = tags.dataTable.getSelectedRowData();
			    $("#geoadmin-edit-tag-name").val(selectedRowData.name);
			    $("#geoadmin-edit-tag-description").val(selectedRowData.description);
		    });

		    $(document.body).on("click", '#geoadmin-delete-tag-button', function() {
			    var selectedRowData = tags.dataTable.getSelectedRowData();
			    var text = "Are you sure you want to delete \"" + selectedRowData.name + "\" ?";
			    $("#geoadmin-delete-tag-modal-text").html(text);
		    });

		    $(document.body).on("click", "#geoadmin-create-tag-modal-submit", function() {
			    var name = $("#geoadmin-create-tag-name").val();
			    var description = $("#geoadmin-create-tag-description").val();
			    tags.createTag(name, description);
		    });

		    $(document.body).on("click", "#geoadmin-edit-tag-modal-submit", function() {
			    var selectedRowData = tags.dataTable.getSelectedRowData();
			    var name = $("#geoadmin-edit-tag-name").val();
			    var description = $("#geoadmin-edit-tag-description").val();
			    tags.editTag(selectedRowData.id, name, description);
		    });

		    $(document.body).on("click", "#geoadmin-delete-tag-modal-submit", function() {
			    if (tags.dataTable.getSelectedRow() != null) {
				    var selectedRowData = tags.dataTable.getSelectedRowData();
				    tags.deleteTag(selectedRowData.id, selectedRowData.name);
			    }
		    });
	    },
	    showMessage : function(text, type) {
		    window.notificator.setText($("#geoadmin-tags-notificator"), text, type);
	    },
	    errorHandling : function(jqXHR, exception) {
		    window.notificator.errorHandling($("#geoadmin-tags-notificator"), jqXHR, exception);
	    },
	    initValidation : function() {
		    $.getScript(window.config.contextPath + "modules/tags/tags-validation.js");
	    },
	    resetForm : function(form) {
		    form.closest(".modal").find(".modal-footer").find("button[type='button']").attr("disabled", true);
		    form.find("*").removeClass("error");
		    form.find('.help-inline > label').remove();
		    form[0].reset();
	    }
	};

	window.tags = tags;
})();