(function() {
'use strict';

var pageState = {};

function init(contextPath, renderURL, resourceURL) {
	pageState.contextPath = contextPath;
	pageState.renderURL = renderURL;
	pageState.resourceURL = resourceURL;

//	$('#importOptions .dropDownSelection').append('<option id="tsvImport">TSV Import</option>');
//	$('#importOptions .dropDownSelection').append('<option id="wfsImport">WFS Import</option>');
//	$('#importOptions .dropDownSelection').append('<option id="shapefileImport">Shapefile Import</option>');
	
	// click listener for import dropdown
	
//	$('#importOptions .dropDownSelection').change(function(){
//		if ($(this).children(":selected").attr('id') == 'wfsImport') {
//			$('#tab3').WFSImport({
//				headerDiv: "#importOptions",
//				content: "#contentOfImporter"
//			})
//			.WFSImport("cleanMe")
//			.WFSImport("createAsDiv", pageState);
//		} else if ($(this).children(":selected").attr('id') == 'tsvImport') {			
//			var geocodeSystems = createLink(resourceURL, "shapes/listTemplateGeocodeSystems");
//			var importTsvPath = createLink(resourceURL, "import/tsv");
//
//			$('#tab3').tsvImporter({
//				mode				: 	"div" 	,					// or "button" 
//				geocodeSystemsURL	: 	geocodeSystems,
//				importTsvURL		:	importTsvPath,			
//				headerDiv			: 	"#importOptions",
//				content				: 	"#contentOfImporter"
//			}).tsvImporter("createImporter");
//		}else if ($(this).children(":selected").attr('id') == 'shapefileImport') {
//			var importShapefileURLPath = createLink(resourceURL, "importShapefileToGeoanalytics");
//
//			$('#tab3').shapefileImporter({
//				mode				: 	"div" 	,					// or "button" 
//				importShapefileURL	:	importShapefileURLPath,			
//				headerDiv			: 	"#importOptions",
//				content				: 	"#contentOfImporter"
//			})
//			.shapefileImporter("destroy")
//			.shapefileImporter("createImporter");
//		}
//	});
//	$('#importOptions .dropDownSelection #tsvImport').prop('selected', true);	
//	$('#importOptions .dropDownSelection').change();
	
	layersManagement();
//	usersListing();
	tabsEvents(resourceURL);

	$('.getLayers').on('click', function() {
		if ($(this).hasClass('idleMe')) return;


		pageState.featureTypesToSave = [];
		var wfsRequestMessenger = {
		    	url: $('#url').val(),
//		    	version: $('#version').val(),
		    	version: "1.0.0",
		    	featureTypes: pageState.featureTypesToSave
		};
		
		postDataToServer(wfsRequestMessenger,  createLink(pageState.resourceURL, 'import/getCapabilities'), function(response) {
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
			postDataToServer(taxonomyTransfer,  createLink(pageState.resourceURL, 'tags/listTags'), function(response) {
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
						postDataToServer(taxonomyNameTrasnfer,  createLink(pageState.resourceURL, 'taxonomies/listTerms'), function(response) {
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
				
				postDataToServer(wfsRequestMessenger,  createLink(pageState.resourceURL, 'import/storeShapeFilesForFeatureType'), function(response){
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
	});
}

function usersListing() {
	$('#usersListing').load(pageState.contextPath+"html/usersListing.jsp", function(){
		  var data = new Object();
		  data.systemOnline = true;
		  showUserManagement(pageState.resourceURL, pageState.contextPath, data, pageState.notificator);
	});
}

function tabsEvents(resourceURL) {
	$('.usersTab').one('click', function(){
		usersListing();
	});
	
//	$('.layersTab').on('click', function(){
//		$('#layersDataTable').DataTable().columns.adjust().draw();
//	});
	
	$('.importTab').one('click', function() {
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
				var importTsvPath = createLink(resourceURL, "import/tsv");

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
			$('#InternalServerErrorModal').modal('show');
		}
	});	
}

	window.Admin = {};
	window.Admin.init = init;
}());