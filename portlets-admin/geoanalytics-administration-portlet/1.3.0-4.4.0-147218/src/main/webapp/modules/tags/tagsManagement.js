(function() {
	'use strict';

	var tags = {
	    tagsManagement : $("#geoadmin-tags"),
	    notificator : null,
	    spinner : null,
	    dataTable : null,
	    selectedRow : null,
	    init : function() {
		    $('.tagsTab').one('click', function() {
			    tags.loadTab();
		    });
	    },
	    loadTab : function() {
		    this.tagsManagement.load(window.config.contextPath + "modules/tags/tagsManagement.jsp", function() {
			    tags.notificator = $("#geoadmin-tags-notificator");
			    tags.spinner = $("#geoadmin-tags .spinner");
			    tags.disableButtons();
			    tags.initUIbindings();
			    tags.initValidation();
			    tags.getTags();
		    });
	    },
	    getTags : function() {
		    var url = window.config.createResourceURL('tags/listTags');
		    $.ajax({
		        url : url,
		        type : 'GET',
		        cache : false,
		        dataType : "json",
		        beforeSend : function() {
			        tags.spinner.show();
		        },
		        success : function(data) {
			        tags.createDataTable(data);
			        $("#geoadmin-create-tag-button").attr("disabled", false);
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
			        tags.dataTable.row.add([ null, name, description, id ]).draw();
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
			        tags.dataTable.row(tags.selectedRow).remove().draw();
			        tags.selectedRow = null;
			        tags.disableButtons();
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
			        var selectedRowData = tags.dataTable.row(tags.selectedRow).data();
			        var message = "";
			        if (selectedRowData[1] !== name) {
				        message += "Tag \"" + selectedRowData[1] + "\" has been renamed to \"" + name + "\"  successfully!<br>";
			        }

			        if (selectedRowData[2] !== description) {
				        message += "Tag \"" + selectedRowData[1] + "\" description has been updated successfully!";
			        }
			        tags.showMessage(message, "success");
			        tags.dataTable.row(tags.selectedRow).data([ null, name, description, id ]);
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
	    createDataTable : function(jsonData) {
		    var data = []
		    for (var i = 0; i < jsonData.length; i++) {
			    data.push([ "", jsonData[i].name, jsonData[i].description, jsonData[i].id ]);
		    }

		    this.dataTable = $('#geoadmin-tags-datatable').DataTable({
		        data : data,
		        columnDefs : [ {
		            targets : 0,
		            orderable : false,
		            searchable : false,
		            className : "geoadmin-tags-datatable-cell geoadmin-tags-datatable-checkbox"
		        }, {
		            targets : [ 1, 2 ],
		            className : "geoadmin-tags-datatable-cell"
		        }, {
		            targets : [ 3 ],
		            visible : false
		        }, {
		            targets : [ 0, 1, 2, 3 ],
		            render : function(data, type, full, meta) {
			            return data == null ? "" : data;
		            }
		        } ],
		        oLanguage: {
			        sEmptyTable: "No tags exist!",
		        },
		        select : {
			        style : 'os'
		        },
		        dom : '<"toolbar">frtip'
		    });

		    $("#geoadmin-tags-datatable_wrapper div.toolbar").append($("#geoadmin-tag-toolbar"));
	    },
	    initUIbindings : function() {
		    var previousCheckbox = null;

		    // Add tick when selecting a row

		    $(document.body).on("click", "td.geoadmin-tags-datatable-cell", function() {
			    var currentCheckbox = $(this).closest("tr").find(".geoadmin-tags-datatable-checkbox");

			    if (previousCheckbox != null) {
				    previousCheckbox.html("");
			    }

			    tags.selectedRow = $(this).closest("tr");

			    if (tags.selectedRow.hasClass("selected")) {
				    previousCheckbox = currentCheckbox;
				    currentCheckbox.html("&#10004;");
				    tags.enableButtons();
			    } else {
				    tags.selectedRow = null;
				    previousCheckbox = null;
				    currentCheckbox.html("");
				    tags.disableButtons();
			    }
		    });

		    $(document.body).on("click", '#geoadmin-create-tag-button', function() {
		    	tags.resetForm($("#geoadmin-create-tag-form"));
		    });

		    $(document.body).on("click", '#geoadmin-edit-tag-button', function() {
		    	tags.resetForm($("#geoadmin-edit-tag-form"));
			    var selectedRowData = tags.dataTable.row(tags.selectedRow).data();
			    $("#geoadmin-edit-tag-name").val(selectedRowData[1]);
			    $("#geoadmin-edit-tag-description").val(selectedRowData[2]);
		    });

		    $(document.body).on("click", '#geoadmin-delete-tag-button', function() {
			    var selectedRowData = tags.dataTable.row(tags.selectedRow).data();
			    var text = "Are you sure you want to delete \"" + selectedRowData[1] + "\" ?";
			    $("#geoadmin-delete-tag-modal-text").html(text);
		    });

		    $(document.body).on("click", "#geoadmin-create-tag-modal-submit", function() {
			    var name = $("#geoadmin-create-tag-name").val();
			    var description = $("#geoadmin-create-tag-description").val();
			    tags.createTag(name, description);
		    });

		    $(document.body).on("click", "#geoadmin-edit-tag-modal-submit", function() {
			    var selectedRowData = tags.dataTable.row(tags.selectedRow).data();
			    var id = selectedRowData[3];
			    var name = $("#geoadmin-edit-tag-name").val();
			    var description = $("#geoadmin-edit-tag-description").val();
			    tags.editTag(id, name, description);
		    });

		    $(document.body).on("click", "#geoadmin-delete-tag-modal-submit", function() {
			    if (tags.selectedRow != null) {
				    var selectedRowData = tags.dataTable.row(tags.selectedRow).data();
				    tags.deleteTag(selectedRowData[3], selectedRowData[1]);
			    }
		    });
	    },
	    enableButtons : function() {
		    $("#geoadmin-edit-tag-button").attr("disabled", false);
		    $("#geoadmin-delete-tag-button").attr("disabled", false);
	    },
	    disableButtons : function() {
		    $("#geoadmin-edit-tag-button").attr("disabled", true);
		    $("#geoadmin-delete-tag-button").attr("disabled", true);
	    },
	    showMessage : function(text, type) {
		    window.notificator.setText($("#geoadmin-tags-notificator"), text, type);
	    },
	    errorHandling : function(jqXHR, exception) {
		    window.notificator.errorHandling($("#geoadmin-tags-notificator"), jqXHR, exception);
	    },
	    initValidation : function() {
		    $.getScript(window.config.contextPath + "modules/tags/tagsValidation.js");
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