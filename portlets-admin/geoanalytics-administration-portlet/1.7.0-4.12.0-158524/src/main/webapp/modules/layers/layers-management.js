(function() {

	"use strict";

	var layers = {
	    layersManagement : $("#geoadmin-layers"),
	    notificator : null,
	    spinner : null,
	    dataTable : null,
	    geoNetworkModule: null,
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
	    init : function() {
		    layers.loadCSS();
		    layers.loadTab();
	    },
	    loadCSS : function() {
		    $("<link/>", {
		        rel : "stylesheet",
		        type : "text/css",
		        href : window.config.contextPath + "modules/layers/layers-management.css"
		    }).appendTo("head");
	    },
	    loadTab : function() {
		    this.layersManagement.load(window.config.contextPath + "modules/layers/layers-management.jsp", function() {
			    layers.notificator = $("#geoadmin-layers-notificator");
			    layers.spinner = $("#geoadmin-layers .spinner");
			    layers.loadGeoNetworkModule(function(){
				    layers.initMapRenderer();
				    layers.initUIbindings();
				    layers.initValidation();
				    layers.getLayers();			    	
			    });
		    });
	    },
	    loadGeoNetworkModule : function(completeCallback){
	    	this.geoNetworkModule = window.config.geoNetworkModule.createInstance("#geoadmin-layers-geonetwork-publish-modal .modal-body");
	    	this.geoNetworkModule.createGeoNetworkForm(completeCallback);
	    },
	    initMapRenderer : function() {
		    $.getScript(window.config.contextPath + "modules/layers/map-renderer.js");
	    },
	    initValidation : function() {
		    $.getScript(window.config.contextPath + "modules/layers/layers-validation.js");	    	
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
		            var refreshCallback = function() {
                        layers.showMessage(message, "success");
                    };
                    layers.reloadDataTable(refreshCallback);			        
		        },
		        error : function(jqXHR, exception) {
			        layers.errorHandling(jqXHR, exception);
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
                    var refreshCallback = function() {
                        layers.showMessage(message, "success");
                    };
                    layers.reloadDataTable(refreshCallback);  
		        },
		        error : function(jqXHR, exception) {
			        layers.errorHandling(jqXHR, exception);
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
	        		var refreshCallback = function() {
				        layers.showMessage(message, "success");
			        };
			        layers.reloadDataTable(refreshCallback);			        
		        },
		        error : function(jqXHR, exception) {
			        layers.errorHandling(jqXHR, exception);
	                layers.spinner.hide();
		        },
		        timeout : 30000
		    });
	    },
	    renderLayer : function(layer) {
		    window.mapRenderer.renderLayer(layer);
	    },
	    downloadLayer : function(layer) {
			var url = window.config.createResourceURL("layers/downloadLayer?layerId=" + layer.id);
			var xhr = new XMLHttpRequest();
			xhr.open('GET', url, true);
			xhr.responseType = 'arraybuffer';
			xhr.onload = function() {
				if (this.status === 200) {
					var filename = xhr.getResponseHeader("filename");
					var type = xhr.getResponseHeader('Content-Type');
					var blob = new Blob([ this.response ], {
						type : type
					});
					var URL = window.URL || window.webkitURL;
					var downloadUrl = URL.createObjectURL(blob);

					if (typeof window.navigator.msSaveBlob !== 'undefined') {
						window.navigator.msSaveBlob(blob, filename);
					} else {
						var URL = window.URL || window.webkitURL;
						var downloadUrl = URL.createObjectURL(blob);

						if (filename) {
							var a = document.createElement("a");
							if (typeof a.download === 'undefined') {
								window.location = downloadUrl;
							} else {
								a.href = downloadUrl;
								a.download = filename;
								document.body.appendChild(a);
								a.click();
							}
						} else {
							window.location = downloadUrl;
						}

						setTimeout(function() {
							URL.revokeObjectURL(downloadUrl);
						}, 100); // cleanup
					}
				} else {
					layers.showMessage("Failed to download Layer " + layer.name, "error");
				}
				layers.spinner.hide();
			};
			xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
			layers.spinner.show();
			xhr.send();
	    },
	    initUIbindings : function() {
		    $(document.body).on("click", "#geoadmin-refresh-layers-button", function() {
			    layers.reloadDataTable();
		    });

		    $(document.body).on('shown', "#geoadmin-layers-render-modal", function() {
			    var layer = layers.dataTable.getSelectedRowData();
			    layers.renderLayer(layer);
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
			    	$("#geoadmin-layers-edit-name").closest(".control-group").hide();
				    $("#geoadmin-layers-edit-is-template").closest(".control-group").hide();
				    $("#geoadmin-layers-edit-geocode-system").closest(".control-group").hide();
				    $("#geoadmin-layers-edit-replication-factor").closest(".control-group").hide();
				    $("#geoadmin-layers-edit-style").closest(".control-group").hide();
			    } else {		    	
			    	$("#geoadmin-layers-edit-name").closest(".control-group").show();
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
			    var layer = layers.dataTable.getSelectedRowData();
			    var text = "Are you sure you want to delete \"" + layer.name + "\" ?";
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
				    var layer = layers.dataTable.getSelectedRowData();
				    layers.deleteLayer(layer);
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

				if (layer != null) {
					if (layer.isExternal === "Yes") {
						$("#geoadmin-layers-edit-layer-visualisation").attr("disabled", true);
						$("#geoadmin-layers-geonetwork-button").hide();
					} else {
						$("#geoadmin-layers-edit-layer-visualisation").attr("disabled", false);
						$("#geoadmin-layers-geonetwork-button").show();
					}

					if (layer.publishedOnGeoNetwork) {
						$("#geoadmin-layers-geonetwork-button .button-text").html("Unpublish from GeoNetwork");
						$('#geoadmin-layers-geonetwork-button').attr("data-target", "#geoadmin-layers-geonetwork-unpublish-modal");	
						
					    var layer = layers.dataTable.getSelectedRowData();
					    $("#geoadmin-layers-geonetwork-unpublish-modal-text").html(layer && "Are you sure you want to unpublish \"" + layer.name + "\" from GeoNetwork?");
					} else {
						$("#geoadmin-layers-geonetwork-button .button-text").html("Publish on GeoNetwork");
						$('#geoadmin-layers-geonetwork-button').attr("data-target", "#geoadmin-layers-geonetwork-publish-modal");	
					}
				}
		    });
		    
		    $(document.body).on("click", "#geoadmin-layers-download-button", function() {
			    if (layers.dataTable.getSelectedRow() != null) {
				    var layer = layers.dataTable.getSelectedRowData();
				    layers.downloadLayer(layer);
			    }
		    });
		    
		    layers.geoNetworkModule.toggleButtonOnValidation("#geoadmin-layers-geonetwork-publish-modal-submit");
		    
		    var successCallback = function(response){
		    	 layers.showMessage(response, "success");
		    };
		    
		    var errorCallback = function(jqXHR, exception){
		    	layers.errorHandling(jqXHR, exception);
		    };			    
		   
		    $(document.body).on("click", "#geoadmin-layers-geonetwork-publish-modal-submit", function() {
				if (layers.dataTable.getSelectedRow() != null) {
					var layer = layers.dataTable.getSelectedRowData();
					layers.geoNetworkModule.publishLayerOnGeoNetwork(layer, successCallback, errorCallback);					
				}
			});

			$(document.body).on("click", "#geoadmin-layers-geonetwork-unpublish-modal-submit", function() {
				if (layers.dataTable.getSelectedRow() != null) {
					var layer = layers.dataTable.getSelectedRowData();
					layers.geoNetworkModule.unpublishLayerFromGeoNetwork(layer, successCallback, errorCallback);
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
	    reloadDataTable : function(refreshCallback) {
		    this.clearMessage();
		    this.dataTable.refreshData(refreshCallback);
	    }
	};

	window.layers = layers;
})();