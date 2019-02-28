(function() {
	'use strict';
    var editor;

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
		        href : window.config.contextPath + "/modules/styles/styles-management.css"
		    }).appendTo("head");
	    },
	    loadStyleTab : function() {
		    this.stylesManagement.load(window.config.contextPath + "modules/styles/styles-management.jsp", function() {
			    styles.notificator = $("#geoadmin-styles-notificator");
			    styles.spinner = $("#geoadmin-styles .spinner");
			    styles.initUIbindings();
			    styles.initValidation();
			    styles.initEditor();
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
	    createStyle : function(name, description, file, legend, files) {
		    var url = window.config.createResourceURL('styles/createStyle');
		    var files = $('#geoadmin-upload-icons-btn')[0].files;

		    var importFormData = new FormData();
			importFormData.append("styleImportFile", file);
			for (var i = 0; i < files.length; i++) {

                importFormData.append(files[i].name, files[i]);
            }
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
		        description : description,
		        content : editor.getValue()
		    };

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
	    getStyleXml : function(id) {
    	    var url = window.config.createResourceURL('styles/getStyle');

    	    $.ajax({
                url : url,
                type : 'GET',
   		        cache : false,
                data :{
                    id: id
                },
                contentType : "application/json",
                success : function(data) {
                    editor.getDoc().setValue(data.content);
                    var totalLines = editor.lineCount();
                    editor.autoFormatRange({line:0, ch:0}, {line:totalLines});
                    editor.on('change', function () {
                        $("#geoadmin-edit-style-modal-submit").prop("disabled", false);
                    });

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
			    var files = $('#geoadmin-upload-icons-btn')[0].files;

			    styles.createStyle(name, description, fileXml, files);
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

            $(document.body).on("change", "#geoadmin-upload-icons-btn", function() {
		    	var filename = this.value;
		    	var separators = ['/','\\\\'];
		    	filename = filename.split(new RegExp(separators.join('|'), ''))

		    	$("#geoadmin-upload-icons-filename").val(filename[filename.length -1]);
//		    	var valid = $('#geoadmin-import-legend-form').valid();
//		    	$("#geoadmin-create-style-modal-submit").attr("disabled", !valid);
		    });


		    $(document).ready(function () {

		        $("#geoadmin-create-style-modal-editor-submit").hide();
                $('#style-form-type-toggle').click(function() {
                    $("#geoadmin-create-style-editor-form").toggle(this.checked);
                    $("#geoadmin-create-style-form").toggle(!this.checked);
                    if ( this.checked ) {
                        $("#geoadmin-create-style-modal-editor-submit").show();
                        $("#geoadmin-create-style-modal-submit").hide();
                    }
                    else {
                        $("#geoadmin-create-style-modal-editor-submit").hide();
                        $("#geoadmin-create-style-modal-submit").show();
                    }
                });
		    });


            $(document.body).on("click", '#discard-legend-btn', function() {
               $('#import-legend-container').hide();
            });

            $(document.body).on("click", '#geoadmin-edit-style-button', function() {
                var selectedRowData = styles.dataTable.getSelectedRowData();
                var id = selectedRowData.id;

                styles.getStyleXml(id);
            });

            $(document).on('change', ':file', function() {
                var input = $(this),
                    numFiles = input.get(0).files ? input.get(0).files.length : 1,
                    files = input.get(0).files;
                    label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
                    input.trigger('fileselect', [numFiles, label, files]);
            });

              // We can watch for our custom `fileselect` event like this
            $('#geoadmin-upload-icons-btn').on('fileselect', function(event, numFiles, label,files) {

                $("#files-list").removeClass("hidden");
                var arrayOfFiles = [];
                  $("ol#files-list").find("li").remove();

                for (var i = 0; i < files.length; i++) {
                  var html = "<li data-name="+files[i].name+">"+files[i].name+" - "+ styles.bytesToSize(files[i].size)+"</li>";
                  $("#files-list").append(html);
                  //base64Converter(files[i]);
                }
                var input = $(this).parents('.input-group').find(':text'),
                  log = numFiles > 1 ? numFiles + ' αρχεία επιλέχθηκαν' : label;

                if( input.length ) {
                  input.val(log);
                }

            });


            //var myCodeMirror = CodeMirror(document.body);
            editor  = CodeMirror.fromTextArea(document.getElementById("geoadmin-raw-style-editor"), {
                lineNumbers: true,
                mode: "xml",
                lineWrapping: true,
                onChange: function () {
                }
            });

            $("#geoadmin-edit-style-name").on('change', function() {
                $("#geoadmin-edit-style-modal-submit").prop("disabled", false);
            });

            $("#geoadmin-edit-style-description").on('change', function() {
                $("#geoadmin-edit-style-modal-submit").prop("disabled", false);
            });


            $("#geoadmin-rule-property-fill-0").on('change', function() {
                if ($("#geoadmin-rule-property-fill-0").val()=="icon") {
                    $("#geoadmin-property-icon-0").show();
                }
                else{
                    $("#geoadmin-property-icon-0").hide();
                }
            });

            $(document.body).on("change", "#geoadmin-upload-icons-btn-0", function() {
                var str = (this.id).split("-");
                var filename = this.value;
                var separators = ['/','\\\\'];
                filename = filename.split(new RegExp(separators.join('|'), ''))

                $("#iconUpload-content-0").val(filename[filename.length -1]);

            });



	    },
	    showMessage : function(text, type) {
		    window.notificator.setText($("#geoadmin-styles-notificator"), text, type);
	    },
	    errorHandling : function(jqXHR, exception) {
		    window.notificator.errorHandling($("#geoadmin-styles-notificator"), jqXHR, exception);
	    },
	    initValidation : function() {
		    $.getScript(window.config.contextPath + "modules/styles/styles-validation.js");
	    },
	    initEditor : function (){
	    	$.getScript(window.config.contextPath + "modules/styles/styles-editor-functions.js");
	    },
	    resetForm : function(form) {
	    	form.closest(".modal").find(".modal-footer").find("button[type='button']").attr("disabled", true);
	    	form.find("*").removeClass("error");
	    	form.find('.help-inline > label').remove();
	    	form[0].reset();
	    },
	    bytesToSize : function (bytes) {
            var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
            if (bytes == 0) return 'n/a';
            var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
            if (i == 0) {
                return bytes + ' ' + sizes[i];
            }
            return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];
        }

	};

	window.styles = styles;
})();