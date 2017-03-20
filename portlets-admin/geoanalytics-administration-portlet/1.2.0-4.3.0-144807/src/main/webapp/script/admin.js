(function() {
'use strict';

var pageState = {};

function init(contextPath, renderURL, resourceURL) {
	pageState.contextPath = contextPath;
	pageState.renderURL = renderURL;
	pageState.resourceURL = resourceURL;

	$('#importOptions .dropDownSelection').append('<option id="tsvImport">TSV Import</option>');
	$('#importOptions .dropDownSelection').append('<option id="wfsImport">WFS Import</option>');
	$('#importOptions .dropDownSelection').append('<option id="shapefileImport">Shapefile Import</option>');
	
	// click listener for import dropdown
	
	$('#importOptions .dropDownSelection').change(function(){
		if ($(this).children(":selected").attr('id') == 'wfsImport') {
			$('#tab3').WFSImport({
				headerDiv: "#importOptions",
				content: "#contentOfImporter"
			})
			.WFSImport("cleanMe")
			.WFSImport("createAsDiv", pageState);
		} else if ($(this).children(":selected").attr('id') == 'tsvImport') {			
			var geocodeSystems = createLink(resourceURL, "shapes/listTemplateGeocodeSystems");
			var importTsvPath = createLink(resourceURL, "importTsv");

			$('#tab3').tsvImporter({
				mode				: 	"div" 	,					// or "button" 
				geocodeSystemsURL	: 	geocodeSystems,
				importTsvURL		:	importTsvPath,			
				headerDiv			: 	"#importOptions",
				content				: 	"#contentOfImporter"
			}).tsvImporter("createImporter");
		}else if ($(this).children(":selected").attr('id') == 'shapefileImport') {
			var importShapefileURLPath = createLink(resourceURL, "importShapefileToGeoanalytics");

			$('#tab3').shapefileImporter({
				mode				: 	"div" 	,					// or "button" 
				importShapefileURL	:	importShapefileURLPath,			
				headerDiv			: 	"#importOptions",
				content				: 	"#contentOfImporter"
			})
			.shapefileImporter("destroy")
			.shapefileImporter("createImporter");
		}
	});
	$('#importOptions .dropDownSelection #tsvImport').prop('selected', true);	
	$('#importOptions .dropDownSelection').change();
	
	layersManagement();
//	usersListing();
	tabsEvents();

	$('.getLayers').on('click', function() {
		if ($(this).hasClass('idleMe')) return;


		pageState.featureTypesToSave = [];
		var wfsRequestMessenger = {
		    	url: $('#url').val(),
//		    	version: $('#version').val(),
		    	version: "1.0.0",
		    	featureTypes: pageState.featureTypesToSave
		};
		
		postDataToServer(wfsRequestMessenger,  createLink(pageState.resourceURL, 'admin/import/getCapabilities'), function(response) {
			if(response.status) {
			
				
				$('.getLayers').addClass('idleMe');
				
				$('#url').attr('readonly', true);
				$('#version').attr('readonly', true);
				
				$('.listContainer').removeClass('hide');
				$('.clearAll').removeClass('hide');
				
				for (var i=0; i<response.data.length; i++) {
					var li = '<li>' +
     							'<label class="checkbox">' +
									'<input type="checkbox" class="checkMe" id="'+ response.data[i] +'">' + response.data[i].split(":")[1]
								'</label>' +
							 '</li>';
					
					$('.listContainer ul').append(li);
				}
				$('.saveChecked').removeClass('hide');
			
			} else {

				return;
			}
			
			$("#containerOfActions").removeClass("hide");
			
			var taxonomyTransfer = {
				active: true
			};
			postDataToServer(taxonomyTransfer,  createLink(pageState.resourceURL, 'admin/tags/listTags'), function(response) {
				$("#taxonomiesDropdown li").remove();
				$("#termsDropdown li").remove();
				$('.saveChecked').addClass('idleMe');
				
				if(response.status) {
					
					for (var i=0; i<response.data.length; i++) {
						var li = '<li id="'+response.data[i]+'">'+response.data[i]+'</li>';
						$('#taxonomiesDropdownDiv ul').append(li);
					}
					
					$('#taxonomiesDropdownDiv ul li').click(function(event){
						$('#taxonomiesDropdownDiv a').html(event.target.textContent+'<span class="caret"></span>');
						pageState.taxonomyValue = event.target.textContent;
						
						var taxonomyNameTrasnfer = {
								taxonomyName: pageState.taxonomyValue,
						};
						postDataToServer(taxonomyNameTrasnfer,  createLink(pageState.resourceURL, 'admin/taxonomies/listTerms'), function(response) {
							if(response.status) {
								
								for (var i=0; i<response.data.length; i++) {
									var li = '<li id="'+response.data[i]+'">'+response.data[i]+'</li>';
									$('#termsDropdownDiv ul').append(li);
								}
								
								$('#termsDropdownDiv ul li').click(function(event){
									$('#termsDropdownDiv a').html(event.target.textContent+'<span class="caret"></span>');
									pageState.termValue = event.target.textContent;
									
									if (pageState.featureTypesToSave.length!=0 && !(pageState.taxonomyValue==undefined) && !(pageState.newTerm==undefined && pageState.termValue==undefined))
										$('.saveChecked').removeClass('idleMe');
								});
								
							} else {

								return;
							}
						});
						
						
						if (pageState.featureTypesToSave.length!=0 && !(pageState.taxonomyValue==undefined) && !(pageState.newTerm==undefined && pageState.termValue==undefined))
							$('.saveChecked').removeClass('idleMe');
						
					});
					
					$('#newTerm').on('input', function() {
						if ($(this).val().replace(/\s/g, '').length) {
						
							pageState.newTerm = $(this).val();
							
							
							if (pageState.featureTypesToSave.length!=0 && !(pageState.taxonomyValue==undefined))
								$('.saveChecked').removeClass('idleMe');
							else 
								$('.saveChecked').addClass('idleMe');
							
						} else {
							if (pageState.featureTypesToSave.length!=0 && !(pageState.termValue==undefined))
								$('.saveChecked').removeClass('idleMe');
							else 
								$('.saveChecked').addClass('idleMe');
							
						}
					});
					
					
				} else {

					return;
				}
				
			});
				
			
			
			$('.checkMe').on('click', function() {
				console.log($(this));
				
				if (pageState.taxonomyValue!=undefined && (!(pageState.termValue==undefined && pageState.newTermValue==undefined)) && pageState.featureTypesToSave.length!=0)
					$('.saveChecked').removeClass('idleMe');
	
				var found = false;
				for(i=0; i < pageState.featureTypesToSave.length; i++){
			        if(pageState.featureTypesToSave[i].match($(this).attr('id'))){
			        	pageState.featureTypesToSave.splice( pageState.featureTypesToSave.indexOf($(this).attr('id')), 1 );
			        	found = true;
			        	return;
			        }
			    } 
				
				if (!found) {
					pageState.featureTypesToSave.push($(this).attr('id'));
					if (pageState.taxonomyValue!=undefined && (!(pageState.termValue==undefined && pageState.newTermValue==undefined)))
						$('.saveChecked').removeClass('idleMe');
				} else {
					pageState.featureTypesToSave.splice( pageState.featureTypesToSave.indexOf($(this).attr('id')), 1 );
					
					if (pageState.featureTypesToSave.length==0) 
						$('.saveChecked').addClass('idleMe');
					else if (pageState.taxonomyValue==undefined || 
							((pageState.termValue==undefined && pageState.newTermValue==undefined)))
						$('.saveChecked').addClass('idleMe');
				}
				
			});
				
				
			$('.saveChecked').on('click', function() {
				if ($(this).hasClass('idleMe')) return;
				
				
				var tt;
				if ((pageState.newTerm!=undefined))
					tt = pageState.newTerm;
				else tt = pageState.termValue;
				
				var wfsRequestMessenger = {
				    	url: $('#url').val(),
//				    	version: $('#version').val(),
				    	version: "1.0.0",
				    	featureTypes: pageState.featureTypesToSave,
				    	taxonomyName: pageState.taxonomyValue,
				    	termName: tt
				};
				
				postDataToServer(wfsRequestMessenger,  createLink(pageState.resourceURL, 'admin/import/storeShapeFilesForFeatureType'), function(response){
					if (response.status) {

					} else {

					}
				});
			});
				
			$('.clearAll').on('click', function() {
				
				$('.getLayers').removeClass('idleMe');
				$('#url').attr('readonly', false);
//				$('#version').attr('readonly', false);
				
				$('.listContainer ul').empty();
				$('.listContainer').addClass('hide');
				$('.clearAll').addClass('hide');
				
				$('#url').val('');
//				$('#version').val('');
				
				$('.saveChecked').addClass('hide');
				pageState.featureTypesToSave = [];
				
				$("#containerOfActions").addClass("hide");
								
			});
			
		});
		
	});
	
	$("#version").keyup(function() {
		if (!$('.error.onVersion').hasClass("hide")) {
			$('.error.onVersion').addClass('hide');
			$('#version').removeClass("errorOnInput");
		}
	}).keydown(function() {
		if (!$('.error.onVersion').hasClass("hide")) {
			$('.error.onVersion').addClass('hide');
			$('#version').removeClass("errorOnInput");
		}
	});
	
	
}

function layersManagement() {
	$('#layersManagement').load(pageState.contextPath+"html/shapeManagement.jsp", function(){
		  var data = new Object();
		  data.systemOnline = true;
		  showShapeManagement(pageState.resourceURL, pageState.contextPath, data, pageState.notificator);
		  initializeLayersDataTableAndLayersModalEvents();
	});
}

function usersListing() {
	$('#usersListing').load(pageState.contextPath+"html/usersListing.jsp", function(){
		  var data = new Object();
		  data.systemOnline = true;
		  showUserManagement(pageState.resourceURL, pageState.contextPath, data, pageState.notificator);
	});
}

function initializeLayersDataTableAndLayersModalEvents() {
	var listLayersByTenantURL = createLink(pageState.resourceURL, 'layers/listLayersByTenant');
	
	$('table#layersDataTable')
	.off()
	.on('init.dt', function(){
		searchInputFixForLayersDataTable();
		constructToolbarForLayersDataTable();
		
		layersDataTableEvents();
	})
	.on( 'draw.dt', function (e, settings, data) {
		$('#layersDataTable thead th:first').attr('class', '').off();
		$('.layersDataTableContainer table.no-wrap thead th:first').attr('class', '').off();
		
		var info = $('#layersDataTable').DataTable().page.info();
		var pages = info.pages;
		
		if(pages <= 1){
			$('#layersDataTable_paginate').addClass('hidden');
		} else {
			$('#layersDataTable_paginate').removeClass('hidden');
		}
	})
    .dataTable({
		columns : [
			{
				data : "CheckBox",
				orderable : false
			},
			{
				data : "Name",
				orderable : true
			},
			{
				data : "Description",
				orderable : true
			} ,
			{
				data : "GeocodeSystem",
				orderable : true
			} ,
			{
				data : "Status",
				orderable : true
			},
			{
				data : "ReplicationFactor",
				orderable : true
			},
			{
				data : "DescriptionTags",
				orderable : true
			},
			{
				data : "Creator",
				orderable : true
			},
			{
				data : "Created",
				orderable : true
			},
			{
				data : "Id",
				visible : false
			}
		],
		ajax : {
			dataType : 'json',
			contentType: 'application/json',
			url: listLayersByTenantURL,
			type: "post",
			data:  function(d){
			},
			dataSrc: function(serverResponse){
	        	if(serverResponse !== null || serverResponse.length === 0){
	        		
	        		var layerRows = serverResponse;
	    			for(var i = 0; i < layerRows.length; i++){
	    				layerRows[i] = new layersObjectForDataTable(
	    								'<i class="icon-ok"></i>',
	    								layerRows[i].name,
	    								layerRows[i].description,
	    								layerRows[i].geocodeSystem,
	    								layerRows[i].status,
	    								layerRows[i].replicationFactor,
	    								layerRows[i].tags,
	    								layerRows[i].creator,
	    								layerRows[i].created,
	    								layerRows[i].id
	    				);
	    				layerRows[i] = surroundObjectPropWithDiv(layerRows[i]);
	    			}
	    			
	    			return layerRows;
	    		} else {
	    			return [];
	    		}
			},
			error: function(){
				onConnectionLostFunction();
			}
		},
		pagingType : "full_numbers",
		language : {
		    	"info": "Showing _START_ - _END_ of _TOTAL_ | ",
				"processing" : "Processing...",
				"paginate": {
			        "next": "",
			        "previous": "",
			        "first": "",
			        "last": ""
			    },
			    "search": "_INPUT_",
		        "searchPlaceholder": "Search..."
		},
        dom: '<"layersDataTableToolbarContainer">frtilp',
        scrollX : true
//        responsive: {
//            details: {
//            	display: $.fn.dataTable.Responsive.display.childRowImmediate,
//            	type: ''
//            }
//        },
//        columnDefs: [{"orderable" : false, "targets" : 0},
//                     {responsivePriority: 1, targets: 0},
//                     {responsivePriority: 2, targets: 1},
//                     {responsivePriority: 3, targets: 2},
//                     {responsivePriority: 4, targets: 3},
//                     {responsivePriority: 5, targets: 5},
//                     {responsivePriority: 6, targets: 6},
//                     {responsivePriority: 7, targets: 7},
//                     {responsivePriority: 8, targets: 8}]
	});
	
	layersModalEvents();
}

function searchInputFixForLayersDataTable(){
	var $searchDiv =  $('<div></div>', {
		'class': 'searchDiv',
		'data-toggle' : "tooltip",
		'data-placement': "top",
		'data-original-title':"Search"
	});
	var $icon_search = $('<i></i>', {
		'class' : "icon-search"
	});
	$searchDiv.append($icon_search);
	$('#layersDataTable_filter').append($searchDiv.prop('outerHTML'));
	
	$('#layersDataTable_filter .searchDiv').off('click').on('click', function(){
		$(this).closest('#layersDataTable_filter').find('label input:first').animate({width:'toggle'});
		$(this).toggleClass('active');
		$(this).closest('#layersDataTable_filter').find('label input:first').focus();
	});
	
	$('#layersDataTable_filter label').toggleClass('hideMe');
}

function constructToolbarForLayersDataTable(){
	var toolbar = $('<div></div>', {
		id: 'toolbar',
		class: 'shownToolbar',
		css: {
			display: 'none'
		}
	});
	
	var $editSelectedBtn = $('<div></div>', {
		id : 'editSelected',
		class: 'insideToolbar',
		text : 'Edit Selected',
		css : {
			display : 'none'
		}
	}).click(function(event){
		$('#editLayerModal').modal('show');
	});
	
	var $deleteSelectedBtn = $('<div></div>', {
		id : 'deleteSelected',
		text : 'Delete Selected',
		class: 'insideToolbar'
	}).click(function(event){
		$('#deleteLayerModal').modal('show');
	});;
	
	$(".layersDataTableContainer div.layersDataTableToolbarContainer")
	.addClass('shownToolbar')
	.css('display', 'none')
	.append($editSelectedBtn)
	.append($deleteSelectedBtn);
}

function layersObjectForDataTable(
		CheckBox, name,
		description, geocodeSystem,
		status, replicationFactor,
		descriptionTags,
		creator, created,
		id){
	this.CheckBox = CheckBox;
	this.Name = name;
	this.Description = description;
	this.GeocodeSystem = geocodeSystem;
	this.Status = status;
	this.ReplicationFactor = replicationFactor;
	this.DescriptionTags = descriptionTags;
	this.Creator = creator;
	this.Created = created;
	this.Id = id;
}

function surroundObjectPropWithDiv(object){
	for(var prop in object){
		if(object[prop] === null)object[prop] = "-";
		if(object[prop].length === 0)object[prop] = "-";
		if(object[prop] === '123')object[prop] = "";
		if(Array.isArray(object[prop]) && object[prop].length > 0){
			var variable = "";
			for(var i = 0; i < object[prop].length; i++){
				if(i===object[prop].length-1){
					variable += object[prop][i];
				}else{
					variable += object[prop][i] + ", ";
				}
			}
			object[prop] = variable;
		}
		object[prop] = '<div>' + object[prop] + '</div>';
	}
	return object;
}

function layersDataTableEvents(){
	$('#layersDataTable tbody')
	.on(
			'click',
			'tr:not(tr.control) td:first-of-type',
			function() {
				if($(this).hasClass('dataTables_empty')){
					return;
				}
				var countSelected = $('#layersDataTable tr.selected').length;
				var clickedRow = $(this).closest('tr').index();
				var currentlySelectedRow = $('#layersDataTable tr.selected').index();
//				if(countSelected === 1 && clickedRow !== currentlySelectedRow)return;
				var selectedRow = $(this).closest('tr')[0];
				var selectProperCheckbox = $(this).find('i.icon-ok')[0];
				$('#layersDataTable tr.selected').not(selectedRow).removeClass('selected');
				$('#layersDataTable i.icon-ok.whiteFont').not(selectProperCheckbox).removeClass('whiteFont');
				$(this).find('i.icon-ok').toggleClass('whiteFont');
				var groupTeamsTableDataForEditing = [];
				$(this).closest('tr').toggleClass('selected');
				var theData = {};
					theData = $($(this)
							.closest('table')
							.dataTable()
							.fnGetData(
									$('#layersDataTable tr.selected')[0]));
					groupTeamsTableDataForEditing.push(theData);

				var countSelectedRows = $('#layersDataTable tr.selected').length;
				var countTableCells = $('#layersDataTable tbody tr td').length;
				if (countTableCells > 1 && countSelectedRows > 0 && !$('.layersDataTableToolbarContainer').hasClass('opened')) {
					$('.layersDataTableToolbarContainer').addClass('opened');
					$('.layersDataTableToolbarContainer').animate({height:'show'});
				} else if(countSelectedRows === 0) {
					$('.layersDataTableToolbarContainer').animate({height:'hide'});
					$('.layersDataTableToolbarContainer').removeClass('opened');
				}
			});
}

function layersModalEvents() {
	$('#acceptDeleteLayerModal')
		.off('click')
		.on('click', function(event){
			deleteLayer();
		});
}

function deleteLayer()
{
	var layerId;
	var theData = {};
	theData = $('#layersDataTable').DataTable().row('.selected').data();
	
	layerId = $(theData.Id).text();
	
	if(layerId == null) return;
	
	var url = createLink(pageState.resourceURL, 'admin/shapes/deleteLayer');
	
	$.ajax({ 
        url :  url, 
        type : "post", 
        dataType : "json",
        contentType : "application/json",
        data : layerId,
        success : function(response) {
        	$('#deleteLayerModal').modal('hide');
        	$('.layersDataTableToolbarContainer').animate({height:'hide'});
			$('.layersDataTableToolbarContainer').removeClass('opened');
        	$('#layersDataTable tr.selected');
        	$( '#layersDataTable' ).DataTable().ajax.reload();
        },
        error : function(jqXHR, textStatus, errorThrown) 
         		 {
 	   			   alert("The following error occured: " + textStatus, errorThrown);
         		 }
       }); 
}

function tabsEvents() {
	$('.usersTab').one('click', function(){
		usersListing();
	});
}

function populateLayersTable() {
	$('#layersTable').dataTable().fnDestroy();
	$('html').off('click');
	$('#layersTable tbody').off('click');
	$('#layersTable tbody').empty();	
	
	$.ajax({
		url: createLink(resourceURL, 'registerurl/getInfoForRegisteredUris'),
		type: 'get',
		dataType: 'json',
		contentType: 'application/json',
		success: function(response) {
			if (!response.status) {

				return;
			}
				
			var toPopulateList = response.data;
			pageState.dataTable = $('#endPointsTable');																
			pageState.toPopulateList = toPopulateList;
			pageState.listOfContents = [];
			pageState.listOfContentsChild = [];
			for (var row=0; row<toPopulateList.info.length; row++) {
				var tr;
				tr =  '<tr id='+row+' class="basicT">' +
							'<td class="name">'+ toPopulateList.info[row].name +'</td>' +
							'<td  title="'+toPopulateList.info[row].statusMessage+'"  class="status statusTooltip" id="statusOfEndPoint" data-container="body" data-placement="top"><div class="'+ 
							(toPopulateList.info[row].status? greenCircle: redCircle) +'"></div></td>' +
							'<td class="numberOfRecords">'+ (toPopulateList.info[row].numberOfRecords  == 0 ? " - ": toPopulateList.info[row].numberOfRecords) +'</td>' +
							'<td class="interval">'+ toPopulateList.info[row].intervalTime + 
							(toPopulateList.info[row].timeUnit.toUpperCase() == "MINUTES"? "  m": 
								(toPopulateList.info[row].timeUnit.toUpperCase()=="HOURS"? "  h": 
										(toPopulateList.info[row].timeUnit.toUpperCase()=="SECONDS"? "  s": " d"))
							) +'</td>' +
							'<td class="lastHarvest">' + toPopulateList.info[row].lastHarvestingTime +'</td>' +
							'<td class="actions"></td>'+
							
							'<td class="arrowH hideMe" id="hideArrow">'+
								'<div class="triangleContainerI" id="triangleContainer'+row+'">'+
									'<div class="triangleVertical">'+
										'<div class="empty"></div>'+
									'</div>'+
									'<div class="arrowLineVertical"></div>'+
								'</div>'+
								'<div class="arrowLineVertical" id="verticalLine'+row+'"></div>'+
							'</td>'+
							
						  '</tr><hr>';
				
				$('#endPointsTable tbody').append(tr);
				
				var contentForInfo = createContentForInfo(toPopulateList.info[row].url, toPopulateList.info[row].schema, toPopulateList.info[row].name);
				pageState.listOfContents.push(contentForInfo);
				
				var contentForInfo = createContentForInfoChild(toPopulateList.info[row].url, toPopulateList.info[row].schema, toPopulateList.info[row].name);
				pageState.listOfContentsChild.push(contentForInfo);
			}
			
			if (toPopulateList.info.length!=0)
				if (toPopulateList.info.length<7) {
					var emptyLines = 7 - toPopulateList.info.length;
				
					for (var row=0; row<emptyLines; row++) {
						var tr;
						tr =  '<tr id="emptyRow" class="hideMe basicT">' +
									'<td class="emptyName"></td>' +
									'<td  class="emptyStatus"></td>' +
									'<td class="emptyNumberOfRecords"></td>' +
									'<td class="emptyInterval"></td>' +
									'<td class="emptyLastHarvest"></td>' +
									'<td class="emptyActions"></td>'+
									'<td class="arrowH">'+
										'<div class="arrowLineVertical"></div>'+
									'</td>'+
							  '</tr><hr>';
						
						$('#endPointsTable tbody').append(tr);
					}
				}
		
//			/** create dataTable **/
			pageState.endPointsTable = $('#endPointsTable').DataTable({
				"aaSorting": [],
				"bDestroy": true,
				destroy: true,
				"language": {
					"info": "Items _START_ to _END_ of _TOTAL_ entries",
					"loadingRecords": "Loading...",
					 "emptyTable": "No data available in table"
				},
		        "pageLength": 7,
				"columnDefs": [
				{
					className: "borderOfTableHeads",
					"targets": [0]
				},
				{
					"orderable": false,
					"targets": [5, 6]
				},
				{
					"targets": 5,
					"render": function(data, type, full, meta) {
						if (full.DT_RowId == "emptyRow") return "";
						
						return '<div class="actionButtons">'+
									'<button title="Harvest" type="button" class="reharvestTooltip" id="reHarvestAction'+full.DT_RowId+'" data-placement="top" data-target="#reharvestEndPointModal" data-toggle="modal">' +
										'<span class="reharvestIcon"></span>' +
									'</button>' +	
									'<button title="Edit" type="button" class="editTooltip" id="editHarvestAction'+full.DT_RowId+'" data-placement="top" data-target="#editEndPointModal"  data-toggle="modal" >' +
										'<span class="editIcon"></span>' +
									'</button>' +
									'<button title="Remove" type="button" class="removeTooltip" id="removeHarvestAction'+full.DT_RowId+'" data-placement="top" data-target="#removeEndPointModal"  data-toggle="modal">' +
										'<span class="removeIcon"></span>' +
									'</button>' +
								'</div>';
					}
				}
				]
			});
			
			$('.reharvestTooltip').tooltip();
			$('.editTooltip').tooltip();
			$('.removeTooltip').tooltip();
			$('.statusTooltip').tooltip();
			
			$('.dataTables_paginate li').on('click', function() { 
				$('.reharvestTooltip').tooltip();
				$('.editTooltip').tooltip();
				$('.removeTooltip').tooltip();
				$('.statusTooltip').tooltip();
			});

			proceed();
			
		}, error : function(jqXHR, textStatus, errorThrown) {

		}
	});	
}

	window.Admin = {};
	window.Admin.init = init;
}());