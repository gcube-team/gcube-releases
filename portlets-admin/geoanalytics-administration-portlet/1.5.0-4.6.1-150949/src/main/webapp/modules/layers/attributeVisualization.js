(function(){
	
	"use strict";
	
	var attributes = {
			attributeLabelCSSClass : "geoadmin-attribute-label",
			
			attributeOrderCSSClass : "geoadmin-attribute-order",
			
			attributesVisualization : $('#geoadmin-layers-attributes'),
			
			dataTable : null,
			
			dataTableID : null,
			
			errorHandling : function(jqXHR, exception) {
				window.notificator.errorHandling(attributes.notificator, jqXHR, exception);
			},
			
			gatherAllAttributesLabelsAndOrders : function() {
				var editedCellsInputs = $('#' + attributes.dataTableID + " ." + attributes.attributeLabelCSSClass);
				var layerAttrs = [];
				var editedAttrs = {}
				
				var allRows = attributes.dataTable.getRows();
				
				$.each(allRows, function(index, value) {
					var $tr = $(this);
					var rowData = attributes.dataTable.getDataTable().row($tr[0]).data();
					
					var attrName = rowData.attributeName;
					var attrLabel = $tr.find('.' + attributes.attributeLabelCSSClass).length > 0 ? $.trim($tr.find('.' + attributes.attributeLabelCSSClass).val()) : rowData.attributeLabel;
					var attrOrder = $tr.find('.' + attributes.attributeOrderCSSClass).val();
					attrOrder = attrOrder === '' ? -1 : attrOrder;
					
					if(attrOrder !== null || typeof attrOrder !== 'undefined') {
						attrOrder = Number(attrOrder);
					} else {
						attrOrder = -1;
					}
					if(rowData.visible === false) {
						attrOrder = 0;
						attrLabel = null;
					}
					var nameToLabelOrder = {};
					nameToLabelOrder.attributeLabel = attrLabel;
					nameToLabelOrder.attributeAppearanceOrder = attrOrder;
					nameToLabelOrder.attributeName = attrName;
					
					layerAttrs.push(nameToLabelOrder);
				});
				
				editedAttrs.layerID = attributes.layerID;
				editedAttrs.layerAttrs = layerAttrs;
				
				return editedAttrs;
			},
			
			getAttributes : function() {
				
				var theURL = window.config.createResourceURL('layers/listLayerAttributesByLayerID');

				// Create datatable
				$('#layer-attributes-datatable').PortletDataTable({
					onDrawDtCallback : function() {
						var $trs = $('#layer-attributes-datatable tbody tr');
						$.each($trs, function(index, value) {
							if(attributes.dataTable.getDataTable().row($(this)[0]).data().visible === false) {
								$(this).addClass(attributes.trVisibilityUtilClass);
							}
						});
					},
					ajax :	{
						url : theURL,
						type : 'POST',
						cache : false,
						dataType : "json",
						beforeSend : function(xhr) {
							xhr.setRequestHeader("Accept", "application/json");
							xhr.setRequestHeader("Content-Type", "application/json");
							layers.spinner.show();
						},
						data : function() {
							var rowData = attributes.layersDataTable.row('.selected').data();
							var layerID =  rowData.id;
							
							attributes.layerID = layerID;
							
							$('#layerVisualizationHeader').text(rowData.name);
							
							return JSON.stringify(layerID);
						},
						dataSrc : function(data) {
							$.each(data, function(index, value) {
								var order = value.attributeAppearanceOrder;
								value.attributeAppearanceOrder = (order === -1 || order === 0) ? "" : order; 
								
								var $orderElement = $('<input />', {
									class : attributes.attributeOrderCSSClass,
									min : 1,
									max : data.length,
									type : 'number',
									text : value.attributeAppearanceOrder,
									value : value.attributeAppearanceOrder
								}); 

								data[index].attributeAppearanceOrder = $orderElement[0].outerHTML;
								
								var $visibilityElement = $('<div></div>', {
									class : attributes.visibilityCheckboxContainerCSSClass,
									'aria-hidden': "true"
								});
								
								var className = "";
								if(order === 0) {
									className = attributes.visibilityCheckboxInvisibleCSSClass;
									data[index].visible = false;
									
								} else {
									className = attributes.visibilityCheckboxVisibleCSSClass;
									data[index].visible = true;
								}
								
								var $icon = $('<i></i>', {
									class : attributes.visibilityCheckboxCSSClass  + " " + className
								});
								
								$visibilityElement.append($icon);
								
								data[index].visibilityCheckbox = $visibilityElement[0].outerHTML;
							});

							return data;
						},
						error : function(jqXHR, exception) {
							attributes.errorHandling(jqXHR, exception);
						},
						complete : function() {
							layers.spinner.hide();
						},
						timeout : 20000
					},
					checkBox : false,
					columnDefs : [{
						title : "Visibility",
						fieldName : "visibilityCheckbox",
						targets : 0,
						width: '10%',
				        orderable : false,
				        searchable : false,
					},{
						title : "Name",
						fieldName : "attributeName",
						targets : 1,
						width: '45%'
					}, {
						title : "Label",
						fieldName : "attributeLabel",
						targets : 2,
						width: '45%'
					}, {
						title : "Order",
						fieldName : "attributeAppearanceOrder",
						targets : 3,
						width: '10%'
					}, {
						title : "Visibility",
						fieldName : "visible",
				        orderable : false,
				        searchable : false,
				        visible : false
					}],
					order : [[1, "asc"]],
					pageLength : 10000,
					toolbar : $('#geoadmin-layers-attributes-toolbar')
				});

				// Get Widget Instance
				this.dataTable = $('#layer-attributes-datatable').data("dt-PortletDataTable");
				this.dataTableID = $(attributes.dataTable.element).attr('id')
			},
			
			init : function(dataTable) {
				this.layersDataTable = dataTable;
				
				attributes.loadCSS();
				attributes.getAttributes();
				attributes.notificator = $("#geoadmin-attributes-visualization-notificator");
				
				attributes.initUIbindings();
			},
			
			initUIbindings : function() {
				$(document.body).on("click", '#' + attributes.dataTableID + ' tr:not(.' + attributes.trVisibilityUtilClass + ') td:nth-of-type(3)', function(event) {
					if($(this).find('input').length === 0){
						var text = $(this).text();
						$(this).html('');
						var $input = $('<input />', {
							text : text,
							value : text,
							type : 'text',
							class : attributes.attributeLabelCSSClass,
							css : {
								margin : 0,
								padding : 0,
								width : '100%'
							}
						});
						
						$(this).append($input);
						
						$input.focus();
					}
					
				});
				
				$(document.body).on("click", '#geoadmin-layer-visualization-submit', function(event) {
					var theData = attributes.gatherAllAttributesLabelsAndOrders();
					
					var url= window.config.createResourceURL('layers/editLayerAttributes');
					
					$.ajax({
				        url : url,
				        type : 'POST',
				        cache : false,
				        data : JSON.stringify(theData),
				        dataType : "json",
				        beforeSend : function(xhr) {
				        	xhr.setRequestHeader("Accept", "application/json");
				        	xhr.setRequestHeader("Content-Type", "application/json");
				        	
				        	layers.spinner.show();
				        },
				        success : function(data) {
				        	attributes.reloadDataTable();
				        },
				        error : function(jqXHR, exception) {
				        	attributes.errorHandling(jqXHR, exception);
				        },
				        complete : function() {
				        	layers.spinner.hide();
				        },
				        timeout : 20000
				    });
				});
				
				$(document.body).on("click", '.' + attributes.visibilityCheckboxContainerCSSClass, function(event) {
					$(this).find('.' + attributes.visibilityCheckboxCSSClass)
						.toggleClass(attributes.visibilityCheckboxVisibleCSSClass);
				
					$(this).find('.' + attributes.visibilityCheckboxCSSClass)
						.toggleClass(attributes.visibilityCheckboxInvisibleCSSClass);
					
					var $tr = $(this).closest('tr');
					var rowData = attributes.dataTable.getDataTable().row($tr[0]).data();
					rowData.visible = !rowData.visible;
					attributes.dataTable.getDataTable().rows($tr[0]).data(rowData);
					
					var $cells= $(this).closest('tr').find('td:not(:first-child)');
					if(rowData.visible) {
						$tr.removeClass(attributes.trVisibilityUtilClass);
						$tr.find("." + attributes.attributeOrderCSSClass).attr('readonly', false);
					} else {
						$tr.addClass(attributes.trVisibilityUtilClass);
						$tr.find('td:nth-of-type(3) input').remove();
						rowData.visibilityCheckbox = $(this)[0].outerHTML;
						attributes.dataTable.getDataTable().row($tr[0]).data(rowData).draw();
						$tr.find("." + attributes.attributeOrderCSSClass).attr('readonly', true);
					}
				});
			},
			
			layersDataTable : null,
			layerID : null,
			
			loadCSS : function() {
				$("<link/>", {
					rel : "stylesheet",
					type : "text/css",
					href : window.config.contextPath + "modules/layers/attributeVisualization.css"
				}).appendTo("head");
			},
			
			loadTab : function() {
				this.attributesVisualization.load(window.config.contextPath + "modules/layers/layersManagement.jsp", function() {
					layers.spinner = $("#geoadmin-layers-attributes .spinner");
					layers.initUIbindings();
					layers.getAttributes();
				});
			},
			
			reloadDataTable : function() {
				//check layerID to decide if you want to reload or not
				this.dataTable.refreshData();
			},
			
			showMessage : function(text, type) {
				window.notificator.setText(attributes.notificator, text, type);
			},
			
			spinner : null,
			
			trVisibilityUtilClass : 'notVisible',
			
			visibilityCheckboxContainerCSSClass : "visibilityCheckboxContainer",
			
			visibilityCheckboxCSSClass : "visibilityCheckbox",
			
			visibilityCheckboxVisibleCSSClass : "fa fa-eye",
			
			visibilityCheckboxInvisibleCSSClass : "fa fa-eye-slash"
	};
	
	window.attributes = attributes;
})();