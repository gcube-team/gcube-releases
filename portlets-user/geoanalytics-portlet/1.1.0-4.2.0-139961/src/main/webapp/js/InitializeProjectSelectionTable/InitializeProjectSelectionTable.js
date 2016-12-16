function initializeProjectSelectionTable(){
	var JSTREEToServerToken = {};
	JSTREEToServerToken.type = "LAYERTAXONOMY";
	JSTREEToServerToken.taxonomyID = null;

	$.fn.dataTable.ext.errMode = 'none';
	
	$('table#ProjectSelectionTable').on( 'init.dt', function(){

		$.fn.dataTable.ext.errMode = 'none';
		
		var totalCellsNumber = calculateNumberOfTotalCells();
		changeLengthMenuOptions(totalCellsNumber);
		
		if(initTableOnFirstTime)
			initialTableSorting();
		
		if(totalCellsNumber > 0){
			var firstCell = $table.DataTable().cells().nodes()[0];
			var $firstCell = $(firstCell);
			var projectName = $firstCell.find('.projectTitle').text();
			var projectDate = $firstCell.find('.projectTileDate').data('date');
			
			var extent = $firstCell.find('.projectTitle').data('extent');
			extent = extent.split(',');
			$.each(extent, function(index,value){
				extent[index] = Number(value);
			});
			extentForCenteringDSSMap = extent;
			userinfoObject.projectName = projectName;
			loadProjectObject.projectName = projectName;
			loadProjectObject.date = projectDate;
			loadProjectObject.extent = extent;
			loadProjectOnTableInit(loadProjectObject);
			
			if(!mapLayersLoaded){
				retrieveAvailableLayersAndPlaceThemOnTheLeft(userinfoObject);
			}else{
				removeLayersFromMap();
				$('#treeviewTaxonomiesLayers').jstree().deselect_all(true);
				$('#treeviewTaxonomiesLayers').jstree().refresh();
			}
		}
	}).on( 'draw.dt', function () {
		var pageLength = $table.DataTable().page.info().pages;
		if(pageLength > 1){
			$('#ProjectSelectionTable_paginate').removeClass('hidden');
		} else {
			$('#ProjectSelectionTable_paginate').addClass('hidden');
		}

		var cellNumbers = calculateNumberOfTotalCells();
		changeLengthMenuOptions(cellNumbers);
		projectDescripionFixHtml();
		hideSpinner();
	})
	.DataTable({
		columns: [{
		        	  data: 'one',
		        	  orderable: false
		          },{
		        	  data: 'two',
		        	  orderable: false
		          },{
		        	  data: 'three',
		        	  orderable: false
		          },{
		        	  data: 'four',
		        	  orderable: false
		          },{
		        	  data: 'helperColumn',
		        	  visible: false
		          }],
		pagingType : "full_numbers",
        dom: '<"toolbarContainer">frtlp',
        "lengthMenu": [ 2, 3, 4, 5, 6 ],
		language : {
			"processing" : "Processing...",
			"paginate": {
		        "next": "",
		        "previous": "",
		        "first": "",
		        "last": ""
		    },
		    "search": "_INPUT_",
	        "searchPlaceholder": "Search projects",
	        lengthMenu : "Show _MENU_ entries of _TOTAL_",
		},
		ajax : {
				dataType : 'json',
				contentType: 'application/json',
				url: projectsSummaryURL,
				type: "post",
				data:  function(d){
					return JSON.stringify( userinfoObject );
				},
				dataSrc: function(serverResponse){
		        	if(serverResponse.status === "Success"){
		        		var tileElements = [];
		        		if(serverResponse.response === null || serverResponse.response.length === 0){
		        			initTableOnFirstTime = false;
		        			return [];
		        		}
		        		$.each(serverResponse.response, function(index, value) {
		        			tileElements.push(buildProjectTile(value.name, value.startDate, value.description, value.creator, value.numberOfUsers, 0, value.numberOfLayers, value.client));//[0].outerHTML);
		        		});
		        		tileElementsGlobal = tileElements;
		        		
		        		if(tileElements.length > 0) {
		        			initTableOnFirstTime = false;
		        			projectDataForTheTable = tableData(sortTilesAlphabetically(tileElements, SORT_ASC));
		        			return projectDataForTheTable;
		        		}else{
		        			return [];
		        		}
		        	}
				}
		}
	});
	
	constructAndAppendToolbarElements();
}

function initializeAssignUsersToProjectTable(){
	
	var theData = {};
	theData[nameSpace + 'usersAndGroups'] = true;
	
	$('table#relateUsersToProjectsTable').on( 'init.dt', function(){
//		calculateNumberOfTotalCells();
//		changeLengthMenuOptions();
//		calculateNumberOfTotalRowsForRelateUserToProjectTable();
		if(!relateUsersToProjectsTableInitialized){
//			groupsManipulation();
			putFilterInsideToolbar();
			relateUsersToProjectsButtonsEvents();
			$relateUsersToProjectsTable = $('#relateUsersToProjectsTable').DataTable();
			$relateUsersToProjectsTable2 = $('#relateUsersToProjectsTable').dataTable();
			relateUsersToProjectsTableInitialized = true;
		}
	}).on( 'draw.dt', function () {
		if(setPage){
			setPage = false;
			var thePage = currentPage+1;
			$('#relateUsersToProjectsTable_paginate span:first a:nth-child('+ thePage +')').click();
		}
		var pageLength = $('#relateUsersToProjectsTable').DataTable().page.info().pages;
		if(pageLength > 1){
			$('#relateUsersToProjectsTable_paginate').removeClass('hidden');
		} else {
			$('#relateUsersToProjectsTable_paginate').addClass('hidden');
		}

		calculateNumberOfTotalRowsForRelateUserToProjectTable();
	})
	.dataTable({
		scrollY : "200px",
		columns: [{
					title: 'name',
		        	orderable: true
		          },{
		        	  title: 'email',
		        	  orderable: true
		          },{
		        	  title: 'groups',
		        	  orderable: true,
		        	  visible: false
		          },{
		        	  title: 'projects',
		        	  orderable: true
		          },{
		        	  title: 'actions',
		        	  orderable: false
		          },{
		        	  title: 'auxiliaryColumn',
		        	  visible: false,
		        	  orderable: true
		          }],
		pagingType : "full_numbers",
        dom: '<"usersToProjectToolbarContainer">frtlp<"groups">',
        "lengthMenu": [ 5 ],
		language : {
			"processing" : "Processing...",
			"paginate": {
		        "next": "",
		        "previous": "",
		        "first": "",
		        "last": ""
		    },
		    "search": "_INPUT_",
	        "searchPlaceholder": "Search members",
	        lengthMenu : "Show _MENU_ entries of _TOTAL_",
		},
//		retrieveAllUsers
		ajax : {
				url: retrieveAllUsers,
				type: "post",
				dataType : 'json',
		        contentType: 'application/json',
		        data: function(){
		        	return JSON.stringify(userinfoObject);
		        },
				dataSrc: function(data){
					if(data !== null && !$.isEmptyObject(data) && data.response !== null){
						var usersArray = data.response;
						var dtObjectsArray = [];
						$('.selectdUsersTagSection').addClass('hidden');
						$('.usersTagsinput').tagsinput('removeAll');
						$('#numOfSelectedUsers').text('');
						
						if(EDITMODE) {
							var url = participantsURL;
							var callback = function(data){
								$.each(usersArray, function(i, v) {
									$.each(data.response, function(index, value){
										if(usersArray[i].name === value.individualName){
											$('.selectdUsersTagSection').removeClass('hidden');
											$('.usersTagsinput').tagsinput('add', value.individualName);
											var trsData = $('table#relateUsersToProjectsTable').DataTable().rows().data();
											
											$.each(trsData, function(ind,val){
												if(val[0] === value.individualName){
													val[4] = val[4].replace("addUserGroupsToProject","removeUserGroupsFromProject").replace("icon-plus","icon-remove").replace("Add","Remove");
													$('table#relateUsersToProjectsTable').DataTable().row(ind).data(val).draw();
												}
											});
										}
									});
								});
							};
							var data = userinfoObject;
							
							AJAX_Call_POST(participantsURL, callback, data);
						}
						
						
						$.each(usersArray, function(index, value) {
							var dtObj = createDatatableArrayForRelateUsersToProject(value);
							dtObjectsArray.push(dtObj);
						});
						return dtObjectsArray;
					} else return [];
				}
		}
	});
	
//	constructAndAppendToolbarElements();
}

function initializeAssignUsersGroupsToProjectTable(){
	$('table#relateUsersGroupsToProjectsTable')
		.on('init.dt', function(){
			relateUsersGroupsToProjectsButtonsEvents();
			$relateUsersGroupsToProjectsTableD = $('#relateUsersGroupsToProjectsTable').DataTable();
			$relateUsersGroupsToProjectsTabled = $('#relateUsersGroupsToProjectsTable').dataTable();
		})
		.on('draw.dt', function(){
			var pageLength = $('#relateUsersGroupsToProjectsTable').DataTable().page.info().pages;
			if(pageLength > 1){
				$('#relateUsersGroupsToProjectsTable_paginate').removeClass('hidden');
			} else {
				$('#relateUsersGroupsToProjectsTable_paginate').addClass('hidden');
			}
			
			calculateNumberOfTotalRowsForTable('relateUsersGroupsToProjectsTable');
		})
		.dataTable({
			columns: [{
						title: 'Name',
						orderable: true
					},{
						title: 'Number of users',
						orderable: true
					}, {
						title: 'Creator',
						orderable: true
					}, {
						title: 'Actions',
						orderable: false
					}],
					pagingType : "full_numbers",
			        dom: '<"usersToProjectToolbarContainer">frtlp<"groups">',
			        "lengthMenu": [ 5 ],
					language : {
						"processing" : "Processing...",
						"paginate": {
					        "next": "",
					        "previous": "",
					        "first": "",
					        "last": ""
					    },
					    "search": "_INPUT_",
				        "searchPlaceholder": "Search members",
				        lengthMenu : "Show _MENU_ entries of _TOTAL_",
					},
					ajax : {
							url: retrieveGroupsAndNumOfUsersURL,
							type: "post",
							dataType : 'json',
					        contentType: 'application/json',
					        data: function(){
					        	return JSON.stringify(userinfoObject);
					        },
							dataSrc: function(data){
								if(data !== null && !$.isEmptyObject(data) && data.response !== null){
									$('.selectdUsersGroupsTagSection').addClass('hidden');
									$('.usersGroupsTagsinput').tagsinput('removeAll');
									$('#numOfSelectedUsersGroups').text('');
									var projectGroupNames = data.response;
									var dtObjectsArray = [];
									
									if(EDITMODE) {
										var url = participantsURL;
										var callback = function(data){
											$.each(projectGroupNames, function(i, v) {
												$.each(data.response, function(index, value){
													if(projectGroupNames[i].name === value.projectGroupName){
														$('.selectdUsersGroupsTagSection').removeClass('hidden');
														$('.usersGroupsTagsinput').tagsinput('add', value.projectGroupName);
														var trsData = $('table#relateUsersGroupsToProjectsTable').DataTable().rows().data();
														
														$.each(trsData, function(ind,val){
															if(val[0] === value.projectGroupName){
																val[3] = val[3].replace("addUserGroupsToProject","removeUserGroupsFromProject").replace("icon-plus","icon-remove").replace("Add","Remove");
																$('table#relateUsersGroupsToProjectsTable').DataTable().row(ind).data(val).draw();
															}
														});
													}
												});
											});
										};
										var data = userinfoObject;
										
										AJAX_Call_POST(participantsURL, callback, data);
									}
									
									$.each(projectGroupNames, function(index, value) {
										var dtObj = createDatatableArrayForAssignUsersGroupsToProjectTable(value);
										dtObjectsArray.push(dtObj);
									});
									return dtObjectsArray;
								} else return [];
							}
					}
		});
	
	putFilterInsideRelateUsersGroupsToProjectsTable();
}

function initializeGroupsTable(){
	$('table#groupsTable').on( 'draw.dt', function () {
		var pageLength = $('table#groupsTable').DataTable().page.info().pages;
		if(pageLength > 1){
			$('#groupsTable_paginate').removeClass('hidden');
		} else {
			$('#groupsTable_paginate').addClass('hidden');
		}
		
		groupsTableEvents();
		calculateNumberOfTotalRowsForTable('groupsTable');
	})
	.dataTable({
//		scrollY : "200px",
		columns: [{
					title: 'Name',
		        	orderable: true
		          },{
		        	  title: 'Creator',
		        	  orderable: true,
		        	  visible: false
		          },{
		        	  title: 'Number of users',
		        	  orderable: true
		          },{
		        	  title: 'Actions',
		        	  orderable: false
		          }],
		pagingType : "full_numbers",
        dom: '<"usersToProjectToolbarContainer">frtlp<"groups">',
        "lengthMenu": [ 5 ],
		language : {
			"processing" : "Processing...",
			"paginate": {
		        "next": "",
		        "previous": "",
		        "first": "",
		        "last": ""
		    },
		    "search": "_INPUT_",
	        "searchPlaceholder": "Search members",
	        lengthMenu : "Show _MENU_ entries of _TOTAL_",
		},
		ajax : {
				url: retrieveGroupsURL,
				type: "post",
				dataType : 'json',
		        contentType: 'application/json',
		        data: function(){
		        	return JSON.stringify(userinfoObject);
		        },
				dataSrc: function(data){
					if(data !== null && !$.isEmptyObject(data) && data.response !== null){
						var projectGroupNames = data.response;
						var dtObjectsArray = [];
						
						$.each(projectGroupNames, function(index, value) {
							var dtObj = createDatatableArrayForGroupsTable(value);
							dtObjectsArray.push(dtObj);
						});
						return dtObjectsArray;
					} else return [];
				}
		}
	});
	
	putFilterInsideToolbarOfGroupTable();
}

function assignUsersToExistingProjectGroup(){
	
	var theData = {};
	theData[nameSpace + 'usersAndGroups'] = true;
	
	$('table#assignUsersToProjectGroupssTable').on( 'init.dt', function(){
		putFilterInsideAssignUsersToExistingGroupsTable();
	}).on( 'draw.dt', function () {
		assignUsersToProjectsButtonsEvents();
		calculateNumberOfTotalRowsForTable('assignUsersToProjectGroupssTable');
	}).dataTable({
		scrollY : "200px",
		columns: [{
					title: 'name',
		        	orderable: true
		          },{
		        	  title: 'email',
		        	  orderable: true
		          },{
		        	  title: 'groups',
		        	  orderable: true,
		        	  visible: true
		          },{
		        	  title: 'projects',
		        	  orderable: true
		          },{
		        	  title: 'actions',
		        	  orderable: false
		          },{
		        	  title: 'auxiliaryColumn',
		        	  visible: false,
		        	  orderable: true
		          }],
		pagingType : "full_numbers",
        dom: '<"usersToProjectToolbarContainer">frtlp<"groups">',
        "lengthMenu": [ 5 ],
		language : {
			"processing" : "Processing...",
			"paginate": {
		        "next": "",
		        "previous": "",
		        "first": "",
		        "last": ""
		    },
		    "search": "_INPUT_",
	        "searchPlaceholder": "Search members",
	        lengthMenu : "Show _MENU_ entries of _TOTAL_",
		},
		ajax : {
				url: retrieveAllUsers,
				type: "post",
				dataType : 'json',
		        contentType: 'application/json',
		        data: function(){
		        	return JSON.stringify(userinfoObject);
		        },
				dataSrc: function(data){
					if(data !== null && !$.isEmptyObject(data) && data.response !== null){
						var usersArray = data.response;
						
						var dtObjectsArray = [];
						$('.assignedUsersTagSection').addClass('hidden');
						$('.assignedUsersTagsinput').tagsinput('removeAll');
						$('#numOfAssignedUsers').text('');
						
						var url = groupMembersURL;
						var callback = function(data){
							$.each(usersArray, function(i, v) {
								$.each(data.response, function(index, value){
									if(usersArray[i].name === value){
										$('.assignedUsersTagSection').removeClass('hidden');
										$('.assignedUsersTagsinput').tagsinput('add', value);
										var trsData = $('table#assignUsersToProjectGroupssTable').DataTable().rows().data();
										
										$.each(trsData, function(ind,val){
											if(val[0] === value) {
												val[4] = val[4].replace("addUserGroupsToProject","removeUserGroupsFromProject").replace("icon-plus","icon-remove").replace("Add","Remove");
												$('table#assignUsersToProjectGroupssTable').DataTable().row(ind).data(val).draw();
											}
										});
									}
								});
							});
						};
						var data = userinfoObject;
						data.groupName = projectGroupNameAssignUsersToProjectGroupsModal;
						
						AJAX_Call_POST(url, callback, data);
						
						$.each(usersArray, function(index, value) {
							var dtObj = createDatatableArrayForRelateUsersToProject(value);
							dtObjectsArray.push(dtObj);
						});
						return dtObjectsArray;
					} else return [];
				}
		}
	});
}

function groupsTableEvents(){
	$('.groupNamesEditButton')
		.off('click')
		.on('click', function(){
//			Create modal if not exists and assign users to modal
			$('#GroupsManipulationModal').modal('hide');
			$('#AssignUsersToProjectGroupsModal').modal('show');
			if(!$.fn.DataTable.isDataTable( '#assignUsersToProjectGroupssTable' )){
				assignUsersToExistingProjectGroup();
			} else {
				$( '#assignUsersToProjectGroupssTable' ).DataTable().ajax.reload();
			}
			
			projectGroupNameAssignUsersToProjectGroupsModal = $('#groupsTable').DataTable().row($(this).closest('tr')).data()[0];
		});
	
	$('.groupNamesDeleteButton')
		.off('click')
		.on('click', function(){
			var url = deleteProjectGroupURL;
			var callback = function(data){
				$( '#groupsTable' ).DataTable().ajax.reload();
			};
			var data = userinfoObject;
			userinfoObject.groupName = $('#groupsTable').DataTable().row($(this).closest('tr')).data()[0];
			AJAX_Call_POST(url, callback, userinfoObject);
		});
	
	$('.groupNamesDeleteButton, .groupNamesEditButton')
		.on('click', function(){
			$(this).addClass('clicked');
		});
	
}

function putFilterInsideToolbarOfGroupTable(){
//	Increase width of search input
	$('#groupsTable_filter label input').addClass('input-xlarge');
//  Append search input to toolbar.	
	$('#GroupsManipulationModal .usersToProjectToolbarContainer').text('Groups in this VRE');
	$('#GroupsManipulationModal .usersToProjectToolbarContainer').append($('#groupsTable_filter'));
}

function putFilterInsideRelateUsersGroupsToProjectsTable(){
//	Increase width of search input
	$('#relateUsersGroupsToProjectsTable_filter label input').addClass('input-xlarge');
//  Append search input to toolbar.	
	$('#RelateUsersGroupsToProjectModal .usersToProjectToolbarContainer').text('Project groups in this VRE');
	$('#RelateUsersGroupsToProjectModal .usersToProjectToolbarContainer').append($('#relateUsersGroupsToProjectsTable_filter'));	
}

function putFilterInsideAssignUsersToExistingGroupsTable(){
//	Increase width of search input
	
	$('#assignUsersToProjectGroupssTable_filter label input').addClass('input-xlarge');
//  Append search input to toolbar.	
	$('#AssignUsersToProjectGroupsModal .usersToProjectToolbarContainer').html('Users available for the group: <span id="projectGroupName"></span>');
	$('#AssignUsersToProjectGroupsModal .usersToProjectToolbarContainer').append($('#assignUsersToProjectGroupssTable_filter'));	
}

function constructAndAppendToolbarElements(){
	var $toolbarContainer = $('.toolbarContainer');
	var divTagAsText = '<div></div>';
	var $toolbar = $(divTagAsText, {
		id : 'toolbar',
	});
	var $newProject = $(divTagAsText, {
		id : 'createNewProject',
		class : 'customTableToolbarButtons',
		text : 'Create new project'
	});
	var $plusIcon = $('<i></i>',{
		class: 'icon-plus pull-left'
	});
	var $mostRecent = $(divTagAsText, {
		id: 'mostRecent',
		class : 'customTableToolbarButtons',
		text: 'Most recent'
	});
	var $alphabOrder = $(divTagAsText, {
		id: 'alphabOrder',
		class : 'customTableToolbarButtons',
		text: 'Alphabetical order'
	});
	var $moreRecords = $(divTagAsText, {
		id: 'moreRecords',
		class : 'customTableToolbarButtons hidden',
		text: 'More records'
	});
	
	var $projectGroups = $(divTagAsText,{
		id: 'manipulateProjectGroups',
		class : 'customTableToolbarButtons',
		text : 'Manipulate groups'
	});
	var $groupsIcon = $('<i></i>',{
		class: 'fa fa-users pull-left',
		'aria-hidden': true
	});
	
	$newProject.append($plusIcon);
	$projectGroups.append($groupsIcon);
	
	$toolbar.append($mostRecent).append($alphabOrder).append($moreRecords);
	$toolbarContainer.append($newProject);
	$toolbarContainer.append($projectGroups);
	$toolbarContainer.append($toolbar);
	
	toolbarEvents();
	tableEvents();
}

function tableEvents(){
	$(document).on('click', '.projectTitle', function(){
		var date = $(this).closest('.projectTileContainer').find('.projectTileDate').data('date');
		var extent = $(this).data('extent');
//		extent = extent.toString().replace(/\./g,"d");
//		extent = extent.toString().replace(new RegExp(",",'g'),"c");
//		window.location.href = createLink(renderURL, "dss", "&projectName=" + $(this).text() + "~~" + "&projectDate="+date + "~~" + "&projectExtent="+extent + "~~");
		
		
		
		extent = extent.split(',');
		$.each(extent, function(index,value){
			extent[index] = Number(value);
		});
		extentForCenteringDSSMap = extent;
		var projectName = $(this).text();
		userinfoObject.projectName = projectName;
		loadProjectObject.projectName = projectName;
		loadProjectObject.date = date;
		loadProjectObject.extent = extent;
		loadProject(loadProjectObject);
		$('#appTabs .tabLink:last').tab('show');
		
		if(!mapLayersLoaded){
			retrieveAvailableLayersAndPlaceThemOnTheLeft(userinfoObject);
		}else{
			removeLayersFromMap();
			$('#treeviewTaxonomiesLayers').jstree().deselect_all(true);
			$('#treeviewTaxonomiesLayers').jstree().refresh();
		}
		
	});
	
	$(document).on('click', '.deleteProject', function(){
		userinfoObject.projectName = $(this).closest('.projectTileTitleContainer').find('.projectTitle').text();
		deleteProjectCallback = function(data){
			if(data.status === "Success") {
				$table.DataTable().ajax.reload();
				$('#onDeleteSure').modal('hide');
			}else {
				$('#projectTableErrorModal').modal('show');
				$('#projectError').text(data.message);
			}
		};
//		AJAX_Call_POST(deleteProjectURL, deleteProjectCallback, userinfoObject);
		$('#onDeleteSure').modal('show');
	});
	
	$(document).on('click', '.viewDetailsOfProject', function(){
		userinfoObject.projectName = $(this).closest('.projectTileTitleContainer').find('.projectTitle').text();
		viewDetailsProjectCallback = function(data){
			if(data.status === "Success") {
				viewDetailsResponseParse(data.response);
				hideSpinner();
				$('#projectViewModal').modal('show');
			}else {
				$('#projectTableErrorModal').modal('show');
				$('#projectError').text(data.message);
			}
		};

		showSpinner();
		AJAX_Call_POST(viewDetailsProjectURL, viewDetailsProjectCallback, userinfoObject);
	});
	
	function viewDetailsResponseParse(response){
		$('.viewModalProjectAttributes').html('');
		
		$('#viewModalProjectName').text(response.name);
		$('#viewModalProjectDescription').text(response.description);
		$('#viewModalProjectCreator').text(response.creator);
		var creationDate = timestampToDateString(response.startDate);
		$('#viewModalProjectDate').text(creationDate);
		var usersList = response.usersNames;
		if(usersList.length > 0){
			var $ul = $('<ul></ul>', {
				id: 'usersListViewDetails'
			});

			$.each(usersList, function(index, value){
				var $li = $('<li></li>',{
					class: 'usersListElementsViewDetails',
					text: value
				});
				$ul.append($li);
			});
			$('#viewModalProjectParticipants').append($ul);
		}else{
			$('#viewModalProjectParticipants').text("-");
		}
		
		var layersList = response.layerNames;
		if(layersList.length > 0){
			var $ul = $('<ul></ul>', {
				id: 'layersListViewDetails'
			});

			$.each(layersList, function(index, value){
				var $li = $('<li></li>',{
					class: 'layersListElementsViewDetails',
					text: value
				});
				$ul.append($li);
			});
			$('#viewModalProjectLayerNames').append($ul);
		}else{
			$('#viewModalProjectLayerNames').text("-");
		}
	}
	
	$('#OKOnProjectViewModal').off('click').on('click', function(){
		
	});
	
	$('#OKOnDeleteSureModal').off('click').on('click', function(){
		showSpinner();
		AJAX_Call_POST(deleteProjectURL, deleteProjectCallback, userinfoObject);
	});
	
	$(document).on('click', '.editProject', function(){
		clearModals();
		$('.editHeader').removeClass('hidden');
		$('.createHeader').addClass('hidden');
		EDITMODE = true;
		var projectName = $(this).closest('.projectTileTitleContainer').find('.projectTitle').text();
		var projectDescription = $(this).closest('.projectTileContainer').find('.projectDescription').text();
		projectNameToBeEdited = projectName;
		projectDateToBeEdited = $(this).closest('.projectTileContainer').find('.projectTileDate').data('date');
		userinfoObject.projectName = projectName
		projectNameAndDescriptionObject.name = projectName;
		projectNameAndDescriptionObject.description = projectDescription;
		$('#BBOXModal').modal('show');
	});
	
	CustomSearch();
}

function requestProjects(){
	var JSTREEToServerToken = {};
	JSTREEToServerToken.type = "LAYERTAXONOMY";
	JSTREEToServerToken.taxonomyID = null;
	projectsSummaryURL;
	listLayersOfType;
	$.ajax({ 
		url: projectsSummaryURL,
        type: 'post',
        dataType : 'json',
        contentType: 'application/json',
        data: JSON.stringify(JSTREEToServerToken),
        success: function(serverResponse){
        	if(serverResponse.status === "Success"){
        		var tileElements = [];
        		var deleteThis = 0;
        		$.each(serverResponse.response, function(index, value) {
        			tileElements.push(buildProjectTile(value.name, value.startDate, value.description, value.creator, deleteThis, deleteThis, deleteThis));//[0].outerHTML);
        			deleteThis++;
        		});
        		tileElementsGlobal = tileElements;
        		if(tileElements.length > 0) {
        			projectDataForTheTable = tableData(sortTilesAlphabetically(tileElements, SORT_ASC));
            		$table.dataTable().fnAddData(projectDataForTheTable);
        		}
        	}
        },error: function(jqXHR, textStatus, errorThrown) {
        	$('#InternalServerErrorModal').modal('show');
        }
      });
}

function buildProjectTile(projectName, projectDate, projDescription,
		projCreatorName, numOfUsers, numOfFunctions, numOfLayers, extent){
	var div = '<div></div>';
	var span = '<span></span>';
	var projectTileContainer = $(div,{
		class : 'projectTileContainer'
	});
	
	var projectTileTitleContainer = $(div,{
		class : 'projectTileTitleContainer'
	});
	var projectTitle = $('<h4></h4>',{
		class : 'projectTitle',
		text : projectName,
		'data-extent' : extent
	});
	projectTileTitleContainer.append(projectTitle);
	
	var projectTileDateContainer = $(div,{
		class : 'projectTileDateContainer'
	});
	var projectTileDate = $(span,{
		class : 'projectTileDate',
		text : timestampToDateString(projectDate),
		'data-date' : projectDate
	});
	var projectToolbar = $(span, {
		class: 'projectToolbar'
	});
	var editProject = $(span, {
		class: 'editProject',
		title: 'Edit Project'
	});
	var editIcon = $('<i></i>',{
		class: 'fa fa-pencil-square-o'
	});
	var deleteProject = $(span,{
		class: 'deleteProject',
		title: 'Delete Project'
	});
	var deleteIcon = $('<i></i>', {
		class: 'fa fa-trash'
	});
	var viewDetailsOfProject = $(span,{
		class: 'viewDetailsOfProject',
		title: 'Details'
	});
	var viewDetailsIcon = $('<i></i>', {
		class: 'fa fa-list'
	});
	viewDetailsOfProject.append(viewDetailsIcon);
	editProject.append(editIcon);
	deleteProject.append(deleteIcon);
	projectToolbar.append(viewDetailsOfProject).append(editProject).append(deleteProject);
	projectTileDateContainer.append(projectTileDate);
	projectTileTitleContainer.append(projectToolbar);
	
	var projectDescriptionContainer = $(div, {
		class : 'projectDescriptionContainer'
	});
//	projDescription = projDescription.replace(/\n/g, "<br />");
	var  projectDescription = $('<p></p>', {
		class : 'projectDescription',
		text : projDescription
	});
	projectDescriptionContainer.append(projectDescription);
	
	var projectCreatorNameContainer = $(div, {
		class : 'projectCreatorNameContainer'
	});
	var projectCreatorName = $(span, {
		class : 'projectCreatorName',
		text : 'By: ' + projCreatorName
	});
	projectCreatorNameContainer.append(projectCreatorName);
	
	var projectTileIcons = $(div, {
		class : 'projectTileIcons'
	});
	
	var iconContainer = $(span, {
		class : 'iconContainer'
	});
	var iconUser = $(span,{
		class : 'iconUser projectDetailsIcons',
		title : 'Users'
	});
	var iconText = $(span,{
		class : 'iconText',
		text : numOfUsers
	});
	iconContainer.append(iconUser).append(iconText);
	
	var iconContainer2 = $(span, {
		class : 'iconContainer pull-right hidden'
	});
	var iconFunction = $(span,{
		class : 'iconFunction projectDetailsIcons',
		title : 'Functions'
	});
	var iconText2 = $(span,{
		class : 'iconText',
		text : numOfFunctions
	});
	iconContainer2.append(iconFunction).append(iconText2);
	
	var iconContainer3 = $(span, {
		class : 'iconContainer pull-right'
	});
	var iconLayers = $(span,{
		class : 'iconLayers projectDetailsIcons',
		title : 'Layers'
	});
	var iconText3 = $(span,{
		class : 'iconText',
		text : numOfLayers
	});
	iconContainer3.append(iconLayers).append(iconText3);
	projectTileIcons.append(iconContainer).append(iconContainer2).append(iconContainer3);
	
	var hiddenColumn = $(div, {
		class : 'hiddenColumn hidden'
	});
	
	projectTileContainer
	.append(projectTileTitleContainer)
	.append(projectTileDateContainer)
	.append(projectDescriptionContainer)
	.append(projectCreatorNameContainer)
	.append(projectTileIcons)
	.append(hiddenColumn);
	
	return projectTileContainer;
}

function calculateNumberOfTotalCells(){
	var cellsNumber = $table.DataTable().cells().nodes();
	var totalCellsNumber = 0;
	
	$.each(cellsNumber, function(index, value) {
		var classLength = $(value).find('.projectTileContainer').length;
		if(classLength !== 0){
			totalCellsNumber++;
		}
	});
	
	$('#ProjectSelectionTable_length label').contents().last().replaceWith(' of ' +totalCellsNumber);
	return totalCellsNumber;
}

function calculateNumberOfTotalRowsForRelateUserToProjectTable() {
	var rowsCount = $('#relateUsersToProjectsTable').DataTable().rows().nodes().length;
	$('#relateUsersToProjectsTable_length label').contents().last().replaceWith(' of ' + rowsCount);
	
	var currentPageRowsCount = $('#relateUsersToProjectsTable tbody tr').length;
	var emptyDataTable = $('#relateUsersToProjectsTable tbody td.dataTables_empty').length > 0;
	if(!emptyDataTable){
		$('#relateUsersToProjectsTable_length select option').text(currentPageRowsCount);
	} else {
		$('#relateUsersToProjectsTable_length select option').text(0);
	}
}

function calculateNumberOfTotalRowsForTable(dtId) {
	var rowsCount = $('#'+dtId.toString()).DataTable().rows().nodes().length;
	$('#'+dtId.toString() + '_length label').contents().last().replaceWith(' of ' + rowsCount);
	
	var currentPageRowsCount = $('#'+ dtId.toString() +' tbody tr').length;
	var emptyDataTable = $('#'+ dtId.toString() +' tbody td.dataTables_empty').length > 0;
	if(!emptyDataTable){
		$('#'+ dtId.toString() +'_length select option').text(currentPageRowsCount);
	} else {
		$('#'+ dtId.toString() +'_length select option').text(0);
	}
}

function changeLengthMenuOptions(totalCellsNumber){
	var options = $('#ProjectSelectionTable_length select option');
	$.each(options, function(){
		$(this).text($(this).val()*4);
	});
	
	var $firstOption = $(options[0]);
	if($firstOption.text() > totalCellsNumber){
		$firstOption.text(totalCellsNumber);
	}
}

function createDatatableArrayForRelateUsersToProject(userInfo) {
	var dtObject = {};
	
	dtObject.name = userInfo.name;
	dtObject.email = userInfo.email;
	dtObject.groups = "";
	dtObject.projects = userInfo.numOfProjects;
	dtObject.actions = createAddUserToProjecButton();
	dtObject.auxiliaryColumn = '';
	dtObject.userId = '';
	
	var dtArray = [];
	for(var dtData in dtObject){
		dtArray.push(dtObject[dtData]);
	}
	
	return dtArray;
}

function createDatatableArrayForGroupsTable(response) {
	var dtObject = {};
	var button = '<button></button>';
	var i = '<i></i>';
	var $buttonEdit = $(button, {
		class: 'groupNamesEditButton projectManagementButtons',
		text: 'Edit'
	});
	var $editIcon = $(i,{
		class:'fa fa-pencil-square-o pull-left',
		'aria-hidden': true
	});
	$buttonEdit.append($editIcon);
	
	var $buttonDelete = $(button, {
		class: 'groupNamesDeleteButton projectManagementButtons',
		text: 'Delete'
	});
	var $deleteIcon = $(i,{
		class:'fa fa-times pull-left',
		'aria-hidden': true
	});
	$buttonDelete.append($deleteIcon);
	
	dtObject.name = response.projectGroupName;
	dtObject.creatorName = response.creatorName;
	dtObject.numOfMembers = response.numOfMembers;
	dtObject.button = $buttonEdit[0].outerHTML.toString() + $buttonDelete[0].outerHTML.toString();
	
	var dtArray = [];
	for(var dtData in dtObject){
		dtArray.push(dtObject[dtData]);
	}
	
	return dtArray;
}

function createDatatableArrayForAssignUsersGroupsToProjectTable(responseObject){
	var dtObject = {};
	
	dtObject.name = responseObject.name;
	dtObject.numOfUsers = responseObject.numOfProjects;
	dtObject.principalname = responseObject.principalname;
	dtObject.buttons = createAddUserGroupsToProjecButton();
	
	var dtArray = [];
	for(var dtData in dtObject){
		dtArray.push(dtObject[dtData]);
	}
	
	return dtArray;
}

function createAddUserToProjecButton(){
	var $button = $('<button></button>',{
		class: 'relateUsersToProjectsButtons addUserToProject projectManagementButtons',
		text : 'Add'
	});
	var $plus = $('<i></i>',{
		class: 'icon-plus pull-left'
	});
	$button.append($plus);
	
	return $button[0].outerHTML;
}

function createAddUserGroupsToProjecButton(){
	var $button = $('<button></button>',{
		class: 'relateUsersGroupsToProjectsButtons addUserGroupsToProject projectManagementButtons',
		text : 'Add'
	});
	var $plus = $('<i></i>',{
		class: 'icon-plus pull-left'
	});
	$button.append($plus);
	
	return $button[0].outerHTML;
}

function relateUsersToProjectsButtonsEvents(){
	$('#relateUsersToProjectsTable').on('click', '.addUserToProject', function(){
		$(this).removeClass('addUserToProject').addClass('removeUserFromProject');
		$(this).find('.icon-plus').removeClass('icon-plus').addClass('icon-remove');
		$(this).contents().first().replaceWith('Remove');
		
		var htmlRow = $(this).closest('tr')[0];
		var rowData = $relateUsersToProjectsTable.rows(htmlRow).data();
		var rowIndex = $relateUsersToProjectsTable.row(htmlRow).index();
		if(rowData !== undefined && rowData !== null) {
			var userName = rowData[0][0];
			var userId = rowData[0][6];//there might be duplicate values
			$('.usersTagsinput').tagsinput('add', userName);
			
			usersToRowsMap[userName] = rowIndex;
		}
	});
	
	$('#relateUsersToProjectsTable').on('click', '.removeUserFromProject', function(){
		currentPage = $('#relateUsersToProjectsTable').DataTable().page.info().page;
		setPage = true;
		
		$(this).removeClass('removeUserFromProject').addClass('addUserToProject');
		$(this).find('.icon-remove').addClass('icon-plus').removeClass('icon-remove');
		$(this).contents().first().replaceWith('Add');
		
		var htmlRow = $(this).closest('tr')[0];
		var rowData = $relateUsersToProjectsTable.rows(htmlRow).data();
		var userName;
		if(rowData !== undefined && rowData !== null) {
			userName = rowData[0][0];
			$('.usersTagsinput').tagsinput('remove', userName);
		}
	});
}

function assignUsersToProjectsButtonsEvents(){
	$('#assignUsersToProjectGroupssTable').on('click', '.addUserToProject', function(){
		$(this).removeClass('addUserToProject').addClass('removeUserFromProject');
		$(this).find('.icon-plus').removeClass('icon-plus').addClass('icon-remove');
		$(this).contents().first().replaceWith('Remove');
		
		var htmlRow = $(this).closest('tr')[0];
		var rowData = $('#assignUsersToProjectGroupssTable').DataTable().rows(htmlRow).data();
		var rowIndex = $('#assignUsersToProjectGroupssTable').DataTable().row(htmlRow).index();
		if(rowData !== undefined && rowData !== null) {
			var userName = rowData[0][0];
			var userId = rowData[0][6];//there might be duplicate values
			$('.assignedUsersTagsinput').tagsinput('add', userName);
			
			assignedUsersToProjectGroupToRowsMap[userName] = rowIndex;
		}
	});
	
	$('#assignUsersToProjectGroupssTable').on('click', '.removeUserFromProject', function(){
		currentPage = $('#assignUsersToProjectGroupssTable').DataTable().page.info().page;
		setPage = true;
		
		$(this).removeClass('removeUserFromProject').addClass('addUserToProject');
		$(this).find('.icon-remove').addClass('icon-plus').removeClass('icon-remove');
		$(this).contents().first().replaceWith('Add');
		
		var htmlRow = $(this).closest('tr')[0];
		var rowData = $('#assignUsersToProjectGroupssTable').DataTable().rows(htmlRow).data();
		var userName;
		if(rowData !== undefined && rowData !== null) {
			userName = rowData[0][0];
			$('.assignedUsersTagsinput').tagsinput('remove', userName);
		}
	});
}

function relateUsersGroupsToProjectsButtonsEvents(){
	$('#relateUsersGroupsToProjectsTable').on('click', '.addUserGroupsToProject', function(){
		$(this).removeClass('addUserGroupsToProject').addClass('removeUserGroupsFromProject');
		$(this).find('.icon-plus').removeClass('icon-plus').addClass('icon-remove');
		$(this).contents().first().replaceWith('Remove');
		
		var htmlRow = $(this).closest('tr')[0];
		var rowData = $relateUsersGroupsToProjectsTableD.rows(htmlRow).data();
		var rowIndex = $relateUsersGroupsToProjectsTableD.row(htmlRow).index();
		if(rowData !== undefined && rowData !== null) {
			var userName = rowData[0][0];
			var userId = rowData[0][6];//there might be duplicate values
			$('.usersGroupsTagsinput').tagsinput('add', userName);
			
			usersGroupsToRowsMap[userName] = rowIndex;
		}
	});
	
	$('#relateUsersGroupsToProjectsTable').on('click', '.removeUserGroupsFromProject', function(){
//		currentPage = $('#relateUsersToProjectsTable').DataTable().page.info().page;
//		setPage = true;
		
		$(this).removeClass('removeUserGroupsFromProject').addClass('addUserGroupsToProject');
		$(this).find('.icon-remove').addClass('icon-plus').removeClass('icon-remove');
		$(this).contents().first().replaceWith('Add');
		
		var htmlRow = $(this).closest('tr')[0];
		var rowData = $relateUsersGroupsToProjectsTableD.rows(htmlRow).data();
		var userName;
		if(rowData !== undefined && rowData !== null) {
			userName = rowData[0][0];
			$('.usersGroupsTagsinput').tagsinput('remove', userName);
		}
	});
}

function projectDescripionFixHtml(){
	$.each($('.projectDescription'),function(indxe, value){
		var $element = $(this);
		var text = $element.text();
		text = text.replace(/\n/,'<br>');
		$element.html(text);
	});
}

function initialTableSorting(){
	var theTableData = $table.DataTable().rows().data();
	var elementsToSort = tableDataToJQueryElements(theTableData);
	var data = tableData(sortTilesByDate(elementsToSort, SORT_ASC));
	$table.DataTable().clear();
	$table.dataTable().fnAddData(data);
	$table.DataTable().order([numOfCols, 'asc']).draw();
}

function setUserInfoObject(){
	
	var theURl = $('#portletInfo').data('loginurl');
	var theData = {};
	theData[nameSpace + 'fetchTenantAndUsername'] = true;
	var callback = function(data){
		userinfoObject = JSON.parse(data);
		retrieveGeoserverBridgeWorkspace(userinfoObject);
	};

	$.ajax(
			{
				url: theURl,
				type: 'post',
				datatype:'json',
				data: theData,
				success: function(data){
					callback(data);
					initializeProjectSelectionTable();
				},
				error: function (xhr, ajaxOptions, thrownError) {
					
				}
			}
		);
}