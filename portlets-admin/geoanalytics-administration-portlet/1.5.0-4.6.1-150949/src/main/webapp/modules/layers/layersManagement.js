(function() {

	"use strict";

	var layers = {
	    checkIfLayerAttributesDataTableExists : function() {
		    if ($.fn.DataTable.isDataTable('table#layer-attributes-datatable'))
			    return true;
		    else
			    return false;
	    },
	    createLayerAttributesDataTable : function() {
		    if (typeof attributes !== 'undefined') {
			    attributes.init(layers.dataTable.dataTable);
		    } else {
			    alert("Cannot initialize attributes datatable. Widget is not defined");
		    }
	    },
	    layersManagement : $("#geoadmin-layers"),
	    notificator : null,
	    spinner : null,
	    dataTable : null,
	    init : function() {
		    layers.loadCSS();
		    layers.loadTab();
	    },
	    loadCSS : function() {
		    $("<link/>", {
		        rel : "stylesheet",
		        type : "text/css",
		        href : window.config.contextPath + "modules/layers/layersManagement.css"
		    }).appendTo("head");
	    },
	    loadTab : function() {
		    this.layersManagement.load(window.config.contextPath + "modules/layers/layersManagement.jsp", function() {
			    layers.notificator = $("#geoadmin-layers-notificator");
			    layers.spinner = $("#geoadmin-layers .spinner");
			    layers.initMapRenderer();
			    layers.initUIbindings();
			    layers.initValidation();
			    layers.getLayers();
		    });
	    },
	    initMapRenderer : function() {
		    $.getScript(window.config.contextPath + "modules/layers/mapRenderer.js");
	    },
	    initValidation : function() {
		    $.getScript(window.config.contextPath + "modules/layers/layersValidation.js");	    	
	    },
	    getLayers : function() {
		    var url = window.config.createResourceURL("layers/listLayersByTenant");

		    // Create datatable

		    $("#geoadmin-layers-datatable").PortletDataTable({
		        ajax : {
		            url : url,
		            type : "GET",
		            cache : false,
		            dataType : "json",
		            beforeSend : function() {
			            layers.spinner.show();
		            },
		            error : function(jqXHR, exception) {
			            layers.errorHandling(jqXHR, exception);
		            },
		            dataSrc : function(data) {

		            },
		            complete : function() {
			            layers.spinner.hide();
		            },
		            timeout : 30000
		        },
		        columnDefs : [ {
		            title : "Name",
		            fieldName : "name",
		            width : "15%"
		        }, {
		            title : "Description",
		            fieldName : "description",
		            width : "15%"
		        }, {
		            title : "Tags",
		            fieldName : "tags",
		            width : "10%"
		        }, {
		            title : "Geocode System",
		            fieldName : "geocodeSystem",
		            width : '15%'
		        }, {
		            title : "Style",
		            fieldName : "style",
		            width : "10%"
		        }, {
		            title : "Template",
		            fieldName : "isTemplate",
		            width : "10%"
		        }, {
		            title : "External",
		            fieldName : "isExternal",
		            width : "10%"
		        }, {
		            title : "Replication Factor",
		            fieldName : "replicationFactor",
		            width : "15%"
		        }, {
		            title : "ID",
		            fieldName : "id",
		            visible : false
		        } ],
		        rowId : 'id',
		        order : [ [ 0, "asc" ] ],
		        selectStyle : "single",
		        toolbar : $("#geoadmin-layers-toolbar")
		        //, scrollX : true
		    });

		    // Get Widget Instance
		    
		    this.dataTable = $("#geoadmin-layers-datatable").data("dt-PortletDataTable");
	    },
	    getMaxReplicationFactor : function(replicationFactor) {
		    var url = window.config.createResourceURL("layers/getMaxReplicationFactor");

		    $.ajax({
		        url : url,
		        type : "GET",
		        cache : false,
		        dataType : "json",
		        success : function(response) {
			        $("#geoadmin-layers-edit-replication-factor").find('option').remove();

			        for (var i = 1; i <= response; i++) {
				        var $option = $("<option></option>", {
				            text : i,
				            value : i
				        });

				        $("#geoadmin-layers-edit-replication-factor").append($option);
			        }

			        $("#geoadmin-layers-edit-replication-factor").val(replicationFactor);
		        },
		        error : function(jqXHR, exception) {
			        layers.errorHandling(jqXHR, exception);
		        },
		        beforeSend : function() {
			        $("#geoadmin-layers-edit-modal").modal("hide");
			        layers.spinner.show();
		        },
		        complete : function() {
			        layers.spinner.hide();
		        }
		    });
	    },
	    getStyles : function(selectedStyle) {
		    var url = window.config.createResourceURL("styles/getAllStyles");

		    $.ajax({
		        url : url,
		        type : "GET",
		        cache : false,
		        dataType : "json",
		        success : function(response) {
			        $("#geoadmin-layers-edit-style").find('option').remove();

			        $.each(response, function(i, v) {
				        var $option = $("<option></option>", {
				            text : v,
				            value : v
				        });
				        $("#geoadmin-layers-edit-style").append($option);
			        });

			        $("#geoadmin-layers-edit-style").val(selectedStyle);
		        },
		        error : function(jqXHR, exception) {
			        layers.errorHandling(jqXHR, exception);
		        },
		        beforeSend : function() {
			        $("#geoadmin-layers-edit-modal").modal("hide");
			        layers.spinner.show();
		        },
		        complete : function() {
			        layers.spinner.hide();
		        }
		    });
	    },
	    getTags : function(tags) {
		    $("#geoadmin-layers-edit-tags").tagsInput({
		        "defaultText" : "Add a Tag",
		        "delimiter" : [ ",", ";", " " ],
		        "minChars" : 2,
		        "maxChars" : 40,
		        "placeholderColor" : "rgba(153, 153, 153, 0.65)"
		    });

		    $("#geoadmin-layers-edit-tags").siblings(".tagsinput").css("width", "");
		    $("#geoadmin-layers-edit-tags").siblings(".tagsinput").addClass("span11");

		    $("#geoadmin-layers-edit-tags").importTags(""); // Reset tags

		    $.each(tags, function(index, value) {
			    $("#geoadmin-layers-edit-tags").addTag(value);
		    });
	    },
	    addExternalLayer : function(externalLayer) {
		    var url = window.config.createResourceURL("layers/addExternalLayer");

		    $.ajax({
		        url : url,
		        type : "POST",
		        cache : false,
		        data : JSON.stringify(externalLayer),
		        contentType : "application/json",
		        beforeSend : function() {
			        $("#geoadmin-layers-add-external-modal").modal("hide");
			        layers.spinner.show();
		        },
		        success : function(message) {
			        layers.reloadDataTable();
			        layers.showMessage(message, "success");
		        },
		        error : function(jqXHR, exception) {
			        layers.errorHandling(jqXHR, exception);
		        },
		        complete : function() {
			        layers.spinner.hide();
		        },
		        timeout : 30000
		    });
	    },
	    deleteLayer : function(layer) {
		    var url = window.config.createResourceURL("layers/deleteLayer");

		    $.ajax({
		        url : url,
		        type : "POST",
		        cache : false,
		        data : JSON.stringify(layer),
		        contentType : "application/json",
		        beforeSend : function() {
			        $("#geoadmin-layers-delete-modal").modal("hide");
			        layers.spinner.show();
		        },
		        success : function(message) {
			        layers.reloadDataTable();
			        layers.showMessage(message, "success");
		        },
		        error : function(jqXHR, exception) {
			        layers.errorHandling(jqXHR, exception);
		        },
		        complete : function() {
			        layers.spinner.hide();
		        },
		        timeout : 30000
		    });
	    },
	    editLayer : function(layer) {
		    var url = window.config.createResourceURL("layers/updateLayer");

		    $.ajax({
		        url : url,
		        type : "POST",
		        cache : false,
		        data : JSON.stringify(layer),
		        contentType : "application/json",
		        beforeSend : function() {
			        $("#geoadmin-layers-edit-modal").modal("hide");
			        layers.spinner.show();
		        },
		        success : function(message) {
			        layers.reloadDataTable();
			        layers.showMessage(message, "success");
		        },
		        error : function(jqXHR, exception) {
			        layers.errorHandling(jqXHR, exception);
		        },
		        complete : function() {
			        layers.spinner.hide();
		        },
		        timeout : 30000
		    });
	    },
	    renderLayer : function(layerId) {
		    window.mapRenderer.renderLayer(layerId);
	    },
	    initUIbindings : function() {
		    $(document.body).on("click", "#geoadmin-refresh-layers-button", function() {
			    layers.reloadDataTable();
		    });

		    $(document.body).on('shown', "#geoadmin-layers-render-modal", function() {
			    var layer = layers.dataTable.getSelectedRowData();
			    layers.renderLayer(layer.id);
			    $("#geoadmin-layers-render-modal-header").text("Previewing Layer " + layer.name);
		    });

		    $(document.body).on("click", "#geoadmin-layers-edit-button", function() {
			    var layer = layers.dataTable.getSelectedRowData();
			    $("#geoadmin-layers-edit-name").val(layer.name);
			    $("#geoadmin-layers-edit-geocode-system").val(layer.geocodeSystem);
			    $("#geoadmin-layers-edit-description").val(layer.description);
			    $("#geoadmin-layers-edit-creator").val(layer.creator);
			    $("#geoadmin-layers-edit-created").val(layer.created);
			    $("#geoadmin-layers-edit-is-template").val(layer.isTemplate);

			    if (layer.isExternal === "Yes") {
				    $("#geoadmin-layers-edit-is-template").closest(".control-group").hide();
				    $("#geoadmin-layers-edit-geocode-system").closest(".control-group").hide();
				    $("#geoadmin-layers-edit-replication-factor").closest(".control-group").hide();
				    $("#geoadmin-layers-edit-style").closest(".control-group").hide();
			    } else {
				    $("#geoadmin-layers-edit-is-template").closest(".control-group").show();
				    $("#geoadmin-layers-edit-geocode-system").closest(".control-group").show();
				    $("#geoadmin-layers-edit-replication-factor").closest(".control-group").show();
				    $("#geoadmin-layers-edit-style").closest(".control-group").show();
			    }

			    layers.getStyles(layer.style);
			    layers.getTags(layer.tags);
			    layers.getMaxReplicationFactor(layer.replicationFactor);

			    $("#geoadmin-layers-edit-modal-header").text("Edit Layer " + layer.name);
		    });

		    $(document.body).on("click", "#geoadmin-layers-delete-button", function() {
			    var selectedRowData = layers.dataTable.getSelectedRowData();
			    var text = "Are you sure you want to delete \"" + selectedRowData.name + "\" ?";
			    $("#geoadmin-layers-delete-modal-text").html(text);
		    });

		    $(document.body).on("click", "#geoadmin-layers-edit-modal-save", function() {
			    var selectedRowData = layers.dataTable.getSelectedRowData();

			    var spans = $("#geoadmin-layers .tagsinput").find(".tag > span");
			    var tags = [];
			    for (var i = 0; i < spans.length; i++) {
				    spans[i] = spans[i].innerHTML.split("&nbsp;").join("");
				    tags.push(spans[i]);
			    }

			    var layer = {
			        id : selectedRowData.id,
			        name : $("#geoadmin-layers-edit-name").val(),
			        description : $("#geoadmin-layers-edit-description").val(),
			        style : $("#geoadmin-layers-edit-style").val(),
			        replicationFactor : $("#geoadmin-layers-edit-replication-factor").val(),
			        tags : tags
			    };

			    layers.editLayer(layer);
		    });

		    $(document.body).on("click", "#geoadmin-layers-add-external-modal-submit", function() {
			    var externalLayer = {
			        geoserverUrl : $("#geoadmin-layers-add-external-geoserver-url").val(),
			        workspace : $("#geoadmin-layers-add-external-workspace").val(),
			        name : $("#geoadmin-layers-add-external-name").val()
			    };

			    layers.addExternalLayer(externalLayer);
		    });

		    $(document.body).on("click", "#geoadmin-layers-delete-modal-submit", function() {
			    if (layers.dataTable.getSelectedRow() != null) {
				    var selectedRowData = layers.dataTable.getSelectedRowData();
				    layers.deleteLayer(selectedRowData);
			    }
		    });

		    $(document.body).on("click", "#geoadmin-layers-edit-layer-visualisation", function() {
			    if (!layers.checkIfLayerAttributesDataTableExists()) {
				    layers.createLayerAttributesDataTable();
			    } else {
				    attributes.reloadDataTable();
			    }
		    });
		    
		    $(document.body).on("click", "#geoadmin-layers-datatable tbody tr", function() {
		    	var layer = layers.dataTable.getSelectedRowData();		
		    	if(layer != null){
			    	if(layer.isExternal === "Yes"){
					    $("#geoadmin-layers-edit-layer-visualisation").attr("disabled", true);		    		
			    	} else {
					    $("#geoadmin-layers-edit-layer-visualisation").attr("disabled", false);		    		
			    	}		    		
		    	}
		    });

	    },
	    showMessage : function(text, type) {
		    window.notificator.setText($("#geoadmin-layers-notificator"), text, type);
	    },
	    clearMessage : function() {
		    this.showMessage("", "success");
	    },
	    errorHandling : function(jqXHR, exception) {
		    window.notificator.errorHandling($("#geoadmin-layers-notificator"), jqXHR, exception);
	    },
	    reloadDataTable : function() {
		    this.clearMessage();
		    this.dataTable.refreshData();
	    }
	};

	window.layers = layers;
})();