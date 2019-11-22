function initializeProjectSelectionTable(userinfoObject){
	var JSTREEToServerToken = {};
	JSTREEToServerToken.type = "LAYERTAXONOMY";
	JSTREEToServerToken.taxonomyID = null;

	$.fn.dataTable.ext.errMode = 'none';
	
	$('table#ProjectSelectionTable').off()
	.on('error.dt', function(){
		$('#InternalServerErrorModal').modal('show');
	})
	.on( 'init.dt', function(){

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
//			extentForCenteringDSSMap = extent;
//			userinfoObject.projectName = projectName;
//			loadProjectObject.projectName = projectName;
//			loadProjectObject.date = projectDate;
//			loadProjectObject.extent = extent;
//			loadProjectOnTableInit(loadProjectObject);
			
			if(!mapLayersLoaded){
//				retrieveAvailableLayersAndPlaceThemOnTheLeft(userinfoObject);
			}else{
				removeLayersFromMap();
				$('#treeviewTaxonomiesLayers').jstree().deselect_all(true);
				$('#treeviewTaxonomiesLayers').jstree().refresh();
			}
			
			$('#pickProjectContainer #show-public-projects').on('change', function() {
				$table.DataTable().clear().draw();
				$('#ProjectSelectionTable_wrapper').find('.dataTables_empty').hide();
				$('#ProjectSelectionTable_wrapper').find('.dataTables_processing').show();
				$table.DataTable().ajax.reload();
			});
		}
	}).on( 'draw.dt', function () {
		var pageLength = $table.DataTable().page.info().pages;
		
		
		$('#ProjectSelectionTable_wrapper .dataTables_scroll').remove();
		
		if(pageLength > 1){
			var $scrollDivForCSSPagination = $('<div></div>',{class:'dataTables_scroll'})
			$('#ProjectSelectionTable_wrapper').prepend($scrollDivForCSSPagination);
			
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
        processing : true,
		language : {
			emptyTable : "No projects available",
//			"processing" : "Processing...",
			processing : '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i>',
			"paginate": {
                next : '<i class="fa fa-angle-right" aria-hidden="true"></i>',
                previous : '<i class="fa fa-angle-left" aria-hidden="true"></i>',
                first : '<i class="fa fa-angle-double-left" aria-hidden="true"></i>',
                last : '<i class="fa fa-angle-double-right" aria-hidden="true"></i>'
            },
		    "search": "_INPUT_",
	        "searchPlaceholder": "Search "+ userinfoObject.layerProjectSubtitle + "s",//"Search projects",
	        lengthMenu : "Show _MENU_ entries of _TOTAL_",
		},
		ajax : {
				dataType : 'json',
				contentType: 'application/json',
				url: projectsSummaryURL,
				type: "post",
				data:  function(d){
					if($('#show-public-projects').length === 0)
						return JSON.stringify( 'true' );
					else 
						return JSON.stringify( $('#show-public-projects').prop('checked') );
				},
				dataSrc: function(serverResponse){
					createdOrEditedOrDeletedProject = false;
					
		        	if(serverResponse.status === "Success"){
		        		var tileElements = [];
		        		if(serverResponse.response === null || serverResponse.response.length === 0){
		        			initTableOnFirstTime = false;
		        			return [];
		        		}
		        		$.each(serverResponse.response, function(index, value) {
		        			tileElements.push(buildProjectTile(value.name, value.startDate, value.description, value.id,
		        					value.creator, value.numberOfUsers, 0, value.numberOfLayers, value.extent, value.rights, value.publicProject));//[0].outerHTML);
		        		});
		        		tileElementsGlobal = tileElements;
		        		
		        		if(tileElements.length > 0) {
		        			initTableOnFirstTime = false;
		        			projectDataForTheTable = tableData(sortTilesAlphabetically(tileElements, SORT_ASC));
		        			return projectDataForTheTable;
		        		}else{
		        			return [];
		        		}
		        	} else if(serverResponse.status === "Failure"){
		        		$('#InternalServerErrorModal').modal('show');
		        	}
				},
				error: function(){
					$('#InternalServerErrorModal').modal('show');
				},
		        complete : function() {
		        	$('#ProjectSelectionTable_wrapper').find('.dataTables_processing').hide();
		        }
		}
	});
	
	constructAndAppendToolbarElements(userinfoObject);
}

function initializeAssignUsersToProjectTable(){
	
	var theData = {};
	theData[nameSpace + 'usersAndGroups'] = true;
	
	$('table#relateUsersToProjectsTable')
	.off()
	.on( 'init.dt', function(){
		hideSpinner();
//		calculateNumberOfTotalCells();
//		changeLengthMenuOptions();
//		calculateNumberOfTotalRowsForRelateUserToProjectTable();
		if(!relateUsersToProjectsTableInitialized){
//			groupsManipulation();
			putFilterInsideToolbar();
			relateUsersToProjectsButtonsEvents();
			relateUsersToProjectsCheckboxesEvents();
			$relateUsersToProjectsTable = $('#relateUsersToProjectsTable').DataTable();
			$relateUsersToProjectsTable2 = $('#relateUsersToProjectsTable').dataTable();
			relateUsersToProjectsTableInitialized = true;
		}
		
		if(EDITMODE) {
			showSpinner();
			
			var url = participantsURL;
			var callback = function(data){
				hideSpinner();
				
				if(data.status === "Success"){
					var usersArray = [];
					var trsData = $('table#relateUsersToProjectsTable').DataTable().rows().data();
					$.each(trsData, function(tri, trv){
						usersArray.push(trv[0]);
					});
					
					var trsCount = $('table#relateUsersToProjectsTable').DataTable().rows().count();
					
					for(var j=0; j < trsCount; j++){
						var rowData = $('#relateUsersToProjectsTable').DataTable().row(j).data();
						var $row = $('#relateUsersToProjectsTable').DataTable().row(j);
						$.each(data.response, function(index, value){
							if(rowData[7] === value.id){
								$('.selectdUsersTagSection').removeClass('hidden');
								
								var tagObject = {};
								var rightsObject = Object.assign({},value.rights);
								
								for(var prop in value.rights){
									rightsObject[prop] = rightsObject[prop] > 0 ? true : false;
								}
								
								tagObject.text = value.individualName;
								tagObject.value = value.id;
								tagObject.data = value.rights;
//								$('.usersTagsinput').tagsinput('add', tagObject);
								
								if(rowData[7] === value.id) {
									rowData[4] = createAssignRightsToUserCheckboxes(j, rightsObject);
//									rowData[5] = rowData[5].replace("addUserToProject","removeUserFromProject").replace("icon-plus","icon-remove").replace("Add","Remove");
									$row.data(rowData).draw();
								}
							}
						});
					}
				}else if(data.status === "Failure"){
					$('.wizard').modal('hide');
					$('#InternalServerErrorModal').modal('show');
				}
				
			}
			var data = userinfoObject;
			
			AJAX_Call_POST(participantsURL, callback, data);
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
		        	  title: 'rights',
		        	  orderable: false
		          },{
		        	  title: 'auxiliaryColumn',
		        	  visible: false,
		        	  orderable: true
		          }, {
		        	  title: 'id',
		        	  visible: false
		          }],
		pagingType : "full_numbers",
        dom: '<"usersToProjectToolbarContainer">frtlp<"groups">',
        "lengthMenu": [ 5 ],
		language : {
			"processing" : "Processing...",
			"paginate": {
                next : '<i class="fa fa-angle-right" aria-hidden="true"></i>',
                previous : '<i class="fa fa-angle-left" aria-hidden="true"></i>',
                first : '<i class="fa fa-angle-double-left" aria-hidden="true"></i>',
                last : '<i class="fa fa-angle-double-right" aria-hidden="true"></i>'
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
//						$('.usersTagsinput').tagsinput('removeAll');
						$('#numOfSelectedUsers').text('');
						
						$.each(usersArray, function(index, value) {
							var rightsObject = {};
							var dtObj = createDatatableArrayForRelateUsersToProject(value, true, index, rightsObject);
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
		.off()
		.on('init.dt', function(){
			relateUsersGroupsToProjectsButtonsEvents();
			$relateUsersGroupsToProjectsTableD = $('#relateUsersGroupsToProjectsTable').DataTable();
			$relateUsersGroupsToProjectsTabled = $('#relateUsersGroupsToProjectsTable').dataTable();
			relateUsersGroupsToProjectsCheckboxesEvents();
			
			if(EDITMODE) {
				var url = participantsURL;
				var callback = function(data){
					hideSpinner();
					
					if(data.status === "Success"){
						var trsData = $('table#relateUsersGroupsToProjectsTable').DataTable().rows().data();
						var projectGroupNames = [];
						$.each(trsData, function(inn, vall) {
							var ob = {};
							ob.id = vall[5];//id
							projectGroupNames.push(ob);
						});
						$.each(projectGroupNames, function(i, v) {
							$.each(data.response, function(index, value){
								if(projectGroupNames[i].id === value.id){
									$('.selectdUsersGroupsTagSection').removeClass('hidden');
									
//									var tagObject = {};
									var rightsObject = Object.assign({},value.rights);
									
									for(var prop in value.rights){
										rightsObject[prop] = rightsObject[prop] > 0 ? true : false;
									}
									
//									tagObject.text = value.projectGroupName; 
//									tagObject.value = value.id;
//									tagObject.data = value.rights;
//									$('.usersGroupsTagsinput').tagsinput('add', tagObject);
									
									$.each(trsData, function(ind,val){
										if(val[5] === value.id){
											var currentIndex = $(val[3]).find('label:first').attr('for').slice(-1);
											val[3] = createAssignRightsToUserCheckboxes(currentIndex, rightsObject);
//											val[4] = val[4].replace("addUserGroupsToProject","removeUserGroupsFromProject").replace("icon-plus","icon-remove").replace("Add","Remove");
											trsData[ind] = val;
										}
									});
								}
							});
						});
						$('table#relateUsersGroupsToProjectsTable').DataTable().clear();
						$.each(trsData, function(index, value){
							$('table#relateUsersGroupsToProjectsTable').dataTable().fnAddData(trsData[index]);	
						});
					}else if(data.status === "Failure"){
						$('.wizard').modal('hide');
						$('#InternalServerErrorModal').modal('show');
					}
				};
				var data = userinfoObject;
				
				showSpinner();
				AJAX_Call_POST(participantsURL, callback, data);
			}
		})
		.on('draw.dt', function(){
			$('#relateUsersGroupsToProjectsTable_wrapper .dataTables_scroll').remove();
			
			var pageLength = $('#relateUsersGroupsToProjectsTable').DataTable().page.info().pages;
			if(pageLength > 1){
				var $scrollDivForCSSPagination = $('<div></div>',{class:'dataTables_scroll'})
				$('#relateUsersGroupsToProjectsTable_wrapper').prepend($scrollDivForCSSPagination);
				
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
						title: 'Rights',
						orderable: false
					}, {
						title: 'ID',
						visible: false
					}],
					pagingType : "full_numbers",
			        dom: '<"usersToProjectToolbarContainer">frtlp<"groups">',
			        "lengthMenu": [ 5 ],
					language : {
						"processing" : "Processing...",
						"paginate": {
			                next : '<i class="fa fa-angle-right" aria-hidden="true"></i>',
			                previous : '<i class="fa fa-angle-left" aria-hidden="true"></i>',
			                first : '<i class="fa fa-angle-double-left" aria-hidden="true"></i>',
			                last : '<i class="fa fa-angle-double-right" aria-hidden="true"></i>'
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
//									$('.usersGroupsTagsinput').tagsinput('removeAll');
									$('#numOfSelectedUsersGroups').text('');
									var projectGroupNames = data.response;
									var dtObjectsArray = [];
									
									$.each(projectGroupNames, function(index, value) {
										var dtObj = createDatatableArrayForAssignUsersGroupsToProjectTable(value, index);
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
		$('#groupsTable_wrapper .dataTables_scroll').remove();
		
		var pageLength = $('table#groupsTable').DataTable().page.info().pages;
		if(pageLength > 1){
			var $scrollDivForCSSPagination = $('<div></div>',{class:'dataTables_scroll'})
			$('#groupsTable_wrapper').prepend($scrollDivForCSSPagination);
			
			$('#groupsTable_paginate').removeClass('hidden');
		} else {
			$('#groupsTable_paginate').addClass('hidden');
		}
		
		groupsTableEvents();
		calculateNumberOfTotalRowsForTable('groupsTable');
	})
	.dataTable({
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
                next : '<i class="fa fa-angle-right" aria-hidden="true"></i>',
                previous : '<i class="fa fa-angle-left" aria-hidden="true"></i>',
                first : '<i class="fa fa-angle-double-left" aria-hidden="true"></i>',
                last : '<i class="fa fa-angle-double-right" aria-hidden="true"></i>'
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
                next : '<i class="fa fa-angle-right" aria-hidden="true"></i>',
                previous : '<i class="fa fa-angle-left" aria-hidden="true"></i>',
                first : '<i class="fa fa-angle-double-left" aria-hidden="true"></i>',
                last : '<i class="fa fa-angle-double-right" aria-hidden="true"></i>'
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
									if(usersArray[i].id === value){
										
										var tagObject = {};
										tagObject.text = usersArray[i].name;
										tagObject.value = usersArray[i].id;
										
										$('.assignedUsersTagSection').removeClass('hidden');
										$('.assignedUsersTagsinput').tagsinput('add', tagObject);
										
										var trsCount = $('table#assignUsersToProjectGroupssTable').DataTable().rows().count();
										
										for(var j=0; j < trsCount; j++){
											var rowData = $('table#assignUsersToProjectGroupssTable').DataTable().row(j).data();
											var $row = $('table#assignUsersToProjectGroupssTable').DataTable().row(j);
											if(rowData[6] === value) {
												rowData[4] = rowData[4].replace("addUserToProject","removeUserFromProject").replace("icon-plus","icon-remove").replace("Add","Remove");
												$row.data(rowData).draw();
											}
										}
									}
								});
							});
							
							hideSpinner();
							
						};
						var trOfSelectedProject = $('.groupNamesEditButton.clicked').closest('tr');
						var rowData = $('table#groupsTable').DataTable().row(trOfSelectedProject).data();
						var groupID = rowData[4];
						
						var data = userinfoObject;
						data.groupName = projectGroupNameAssignUsersToProjectGroupsModal;
						
						ProjectGroupMessenger = {};
						ProjectGroupMessenger.uio = userinfoObject;
						ProjectGroupMessenger.users = [];
						ProjectGroupMessenger.projectGroupID = groupID;
						
						showSpinner();
						AJAX_Call_POST(url, callback, ProjectGroupMessenger);
						
						$.each(usersArray, function(index, value) {
							var rightsObject = {};
							var dtObj = createDatatableArrayForRelateUsersToProject(value, false, null, rightsObject);
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
			$('.groupNamesDeleteButton').removeClass('.active');
			$(this).addClass('active');
			$('#GroupsManipulationModal').modal('hide');
			$('#onDeleteGroupSure').modal('show');
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

function constructAndAppendToolbarElements(userinfoObject){
	var $toolbarContainer = $('.toolbarContainer');
	var divTagAsText = '<div></div>';
	var label = '<label></label>';
	var $toolbar = $(divTagAsText, {
		id : 'toolbar',
	});
	var $newProject = $(divTagAsText, {
		id : 'createNewProject',
		class : 'customTableToolbarButtons',
		text : 'Create new ' + userinfoObject.layerProjectSubtitle
	});
//	$('<span></span>').appendTo($newProject);
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
		text : 'Edit groups'
	});
	var $groupsIcon = $('<i></i>',{
		class: 'fa fa-users pull-left',
		'aria-hidden': true
	});
	
	var $publicProjectsContainer = $(divTagAsText, {
		id : 'show-public-projects-container',
		class : 'btn-group',
		'data-toggle' : 'buttons-checkbox'
	});
	
//	var $showPublicProjectsButton = $('<button></button>', {
//		type : 'button',
//		class : 'btn btn-grp',
//		id : 'show-public-projects',
//		text : 'Show public projects',
//		css : {
//			'border-color' : '#0271be'
//		}
//	});
	
	var $showPublicProjectsLabel = $('<label>Show public <span>'+userinfoObject.layerProjectSubtitle+'</span>s</label>', {
//		text : 'Show public <span></span>s',
		for : 'show-public-projects',
		css : {
			display : 'inline-block'
		}
	}).appendTo($publicProjectsContainer);
	
	var $showPublicProjectsCheckbox = $('<input />', {
		type : 'checkbox',
		id : 'show-public-projects',
		checked : true
	}).appendTo($showPublicProjectsLabel);
	
	var $showPublicProjectsLabelForCSSStyling = $('<label></label>', {
		id : 'show-public-projects-css-label',
		for : 'show-public-projects',
		css : {
			display : 'inline-block',
			'margin-left' : '0.5em'
		}
	}).appendTo($showPublicProjectsLabel);
	
	$newProject.append($plusIcon);
	$projectGroups.append($groupsIcon);
	
	$toolbar.append($mostRecent).append($alphabOrder).append($moreRecords);//.append($publicProjectsContainer);
	$toolbarContainer.append($newProject);
	$toolbarContainer.append($projectGroups);
	$toolbarContainer.append($toolbar);
	
	toolbarEvents($publicProjectsContainer);
	tableEvents();
}

function tableEvents(){
	$(document).on('click', '.projectTitle', function(){
		var projectName = $(this).text();
		var projectId = $(this).data('projectid');
		var right = 'read';
		var url = authorizedToReadProjectURL;
		
		var $element = $(this);
		
		var callback = function($element){
		    $('.appTabItem').removeClass('disabled');

			var date = $element.closest('.projectTileContainer').find('.projectTileDate').data('date');
			var extent = $element.data('extent');
			extent = extent.split(',');
			$.each(extent, function(index,value){
				extent[index] = Number(value);
			});
			extentForCenteringDSSMap = extent;
			userinfoObject.projectName = projectName;
			userinfoObject.projectId = projectId;
			loadProjectObject.projectName = projectName;
			loadProjectObject.projectID = projectId;
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
			
			removePlugins();
			
			$('#layersPanel').data('layers', []);

			//updateAvailablePluginsAccordion();
			
//			retrieveAvailablePluginsForProject(principalName, tenantName, pluginName, projectName, pluginId);
		}
		
		hasRight(projectName, right, callback, $element, projectId);
	});
	
	$(document).on('click', '.deleteProject', function(){
		userinfoObject.projectName = $(this).closest('.projectTileTitleContainer').find('.projectTitle').text();
		userinfoObject.projectId = $(this).closest('.projectTileTitleContainer').find('.projectTitle').data('projectid');
		deleteProjectCallback = function(data){
			hideSpinner();
			if(data.status === "Success") {
				$('#onDeleteSure').modal('hide');
				createdOrEditedOrDeletedProject = true;
				$table.DataTable().ajax.reload();
				if(mapLayersLoaded && (userinfoObject.projectId === $('#nameOfProject').data("projectID"))) {
					$('#treeviewTaxonomiesLayers').jstree().destroy();
					mapLayersLoaded = false;
					$('#nameOfProject').text(projectNameWhenNotAvailable);
					$('#dateOfProject').text(projectDateWhenNotAvailable);
				}
			} else if(data.status === 'Unauthorized') {
				$('#messageRights').text(data.message);
				$('#authorizationMessageModal').modal('show');
			} else {
				$('.wizard').modal('hide');
				$('#InternalServerErrorModal').modal('show');
			}
		};
//		AJAX_Call_POST(deleteProjectURL, deleteProjectCallback, userinfoObject);
		$('#onDeleteSure').modal('show');
	});
	
	$(document).on('click', '.viewDetailsOfProject', function(){
		userinfoObject.projectName = $(this).closest('.projectTileTitleContainer').find('.projectTitle').text();
		userinfoObject.projectId = $(this).closest('.projectTileTitleContainer').find('.projectTitle').data('projectid');
		viewDetailsProjectCallback = function(data){
			hideSpinner();
			if(data.status === "Success") {
				viewDetailsResponseParse(data.response);
				$('#projectViewModal').modal('show');
			}else {
				$('#InternalServerErrorModal').modal('show');
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
		var extent = $(this).closest('.projectTileTitleContainer').find('.projectTitle').data('extent');
		editModeCallBack = function() {
			extractCoordinates(extent);
		};
		var projectName = $(this).closest('.projectTileTitleContainer').find('.projectTitle').text();
		var projectDescription = $(this).closest('.projectTileContainer').find('.projectDescription').text();
		var projectId = $(this).closest('.projectTileContainer').find('.projectTitle').data('projectid');
		projectNameToBeEdited = projectName;
		projectDateToBeEdited = $(this).closest('.projectTileContainer').find('.projectTileDate').data('date');
		userinfoObject.projectId = projectId;
		userinfoObject.projectName = projectName;
		projectNameAndDescriptionObject.name = projectName;
		projectNameAndDescriptionObject.description = projectDescription;
		projectNameAndDescriptionObject.publicProject = $(this).closest('.projectTileContainer').find('.projectTitle').data('ispublic');
		
		var right = 'edit';
		var callback = function($element){
			$('#BBOXModal').modal('show');
		}
		$element = $(this);
		hasRight(projectName, right, callback, $element, projectId);
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
        			tileElements.push(buildProjectTile(value.name, value.startDate, value.description, value.creator, deleteThis, deleteThis, deleteThis, value.rights, value.publicProject));//[0].outerHTML);
        			deleteThis++;
        		});
        		tileElementsGlobal = tileElements;
        		if(tileElements.length > 0) {
        			projectDataForTheTable = tableData(sortTilesAlphabetically(tileElements, SORT_ASC));
            		$table.dataTable().fnAddData(projectDataForTheTable);
        		}
        	}
        },error: function(jqXHR, textStatus, errorThrown) {
        	$('.wizard').modal('hide');
        	$('#InternalServerErrorModal').modal('show');
        }
      });
}

function buildProjectTile(projectName, projectDate, projDescription, projectId,
		projCreatorName, numOfUsers, numOfFunctions, numOfLayers, extent, rights, publicProject){
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
		'data-extent' : extent,
		'data-projectId' : projectId,
		'data-isPublic': publicProject
	});
	if(rights["read"] === 0){
		projectTitle.addClass('lineThrough');
	}
	projectTileTitleContainer.append(projectTitle);
	
	var projectTileDateContainer = $(div,{
		class : 'projectTileDateContainer'
	});
	var projectTileDate = $(span,{
		class : 'projectTileDate',
		text : timestampToLocale(projectDate),
		'data-date' : projectDate
	});
	var projectToolbar = $(span, {
		class: 'projectToolbar'
	});
	var editProject = $(span, {
		class: 'editProject',
		title: 'Edit Project'
	});
	if(rights["edit"] === 0){
		editProject.addClass('hidden');
	}
	var editIcon = $('<i></i>',{
		class: 'fa fa-pencil-square-o'
	});
	var deleteProject = $(span,{
		class: 'deleteProject',
		title: 'Delete Project'
	});
	if(rights["delete"] === 0){
		deleteProject.addClass('hidden');
	}
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
	
	var iconUser;
	if(publicProject) {
		iconUser = $(span,{
			class : 'fa fa-users projectDetailsIcons',
			title : 'Public project'
		});
	} else {
		iconUser = $(span,{
			class : 'iconUser projectDetailsIcons',
			title : 'Users'
		});
	}
	
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

function createDatatableArrayForRelateUsersToProject(userInfo, hasRightsColumn, index, rightsObject) {
	var dtObject = {};
	
	dtObject.name = userInfo.name;
	dtObject.email = userInfo.email;
	dtObject.groups = "";
	dtObject.projects = userInfo.numOfProjects;
	if(hasRightsColumn){
		dtObject.rights = createAssignRightsToUserCheckboxes(index, rightsObject);
	}
	dtObject.actions = createAddUserToProjecButton();
	dtObject.auxiliaryColumn = '';
	dtObject.id = userInfo.id;
	
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
		class: 'groupNamesEditButton projectManagementButtons span4',
		text: 'Edit'
	});
	var $editIcon = $(i,{
		class:'fa fa-pencil-square-o pull-left',
		'aria-hidden': true
	});
	$buttonEdit.append($editIcon);
	
	var $buttonDelete = $(button, {
		class: 'groupNamesDeleteButton projectManagementButtons span5',
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
	dtObject.projectGroupID = response.projectGroupID;
	
	var dtArray = [];
	for(var dtData in dtObject){
		dtArray.push(dtObject[dtData]);
	}
	
	return dtArray;
}

function createDatatableArrayForAssignUsersGroupsToProjectTable(responseObject, index){
	var dtObject = {};
	
	dtObject.name = responseObject.name;
	dtObject.numOfUsers = responseObject.numOfProjects;
	dtObject.principalname = responseObject.principalname;
	var rightsObject = {};
	rightsObject['read'] = responseObject['read'];
	rightsObject['edit'] = responseObject['edit'];
	rightsObject['delete'] = responseObject['delete'];
	var rights = Object.assign({}, rightsObject);
	for(var prop in rightsObject){
		rights[prop] = rightsObject[prop] > 0 ? true : false;
	}
	dtObject.rights = createAssignRightsToUserCheckboxes(index, rights);
	dtObject.buttons = createAddUserGroupsToProjecButton();
	dtObject.id = responseObject.id;
	
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

function createAssignRightsToUserCheckboxes(index, rightsObject){
	var div = '<div></div>';
	var input = '<input />';
	var label = '<label></label>';
	var $container = $(div, {
		class : 'allRightsContainerInTableCell'
	});

	var $readRightContainer = $(div, {
		class: 'rightsContainerInTableCell readRightCheckbox span4',
		text: 'R.',
		title: 'Read'
	});
	var $editRightContainer = $(div, {
		class: 'rightsContainerInTableCell editRightCheckbox span4',
		text: 'E.',
		title: 'Edit'
	});
	var $deleteRightContainer = $(div, {
		class: 'rightsContainerInTableCell deleteRightCheckbox span4',
		text: 'D.',
		title: 'Delete'
	});

	var readRightCheckboxId = 'readRightCheckboxId' + index.toString();
	var $readRightCheckbox =  $(input, {
		type : "checkbox",
		class : "RightsCheckboxes",
		name : "read",
		value : "read",
		id : readRightCheckboxId
	});
	var $readRightCheckboxLabel = $(label, {
		'for' : readRightCheckboxId
	});

	var editRightCheckboxId = 'editRightCheckboxId' + index.toString();
	var $editRightCheckbox =  $(input, {
		type : "checkbox",
		class : "RightsCheckboxes",
		name : "edit",
		value : "edit",
		id : editRightCheckboxId
	});
	var $editRightCheckboxLabel = $(label, {
		'for' : editRightCheckboxId
	});

	var deleteRightCheckboxId = 'deleteRightCheckboxId' + index.toString();
	var $deleteRightCheckbox =  $(input, {
		type : "checkbox",
		class : "RightsCheckboxes",
		name : "delete",
		value : "delete",
		id : deleteRightCheckboxId
	});
	var $deleteRightCheckboxLabel = $(label, {
		'for' : deleteRightCheckboxId
	});
	
	if(!$.isEmptyObject(rightsObject)){
		$readRightCheckbox.attr('checked',rightsObject['read']);
		$editRightCheckbox.attr('checked',rightsObject['edit']);
		$deleteRightCheckbox.attr('checked',rightsObject['delete']);
	}

	$readRightContainer.append($readRightCheckbox).append($readRightCheckboxLabel);
	$editRightContainer.append($editRightCheckbox).append($editRightCheckboxLabel);
	$deleteRightContainer.append($deleteRightCheckbox).append($deleteRightCheckboxLabel);
	$container
	.append($readRightContainer)
	.append($editRightContainer)
	.append($deleteRightContainer);

	return $container[0].outerHTML;
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
//	$('#relateUsersToProjectsTable').on('click', '.addUserToProject', function(){
//		$(this).removeClass('addUserToProject').addClass('removeUserFromProject');
//		$(this).find('.icon-plus').removeClass('icon-plus').addClass('icon-remove');
//		$(this).contents().first().replaceWith('Remove');
//		
//		var htmlRow = $(this).closest('tr')[0];
//		var rowData = $relateUsersToProjectsTable.rows(htmlRow).data();
//		var rowIndex = $relateUsersToProjectsTable.row(htmlRow).index();
//		if(rowData !== undefined && rowData !== null) {
//			var userName = rowData[0][0];
//			var userId = rowData[0][7];//there might be duplicate values
//			var $checkBoxesContainer = $(htmlRow).find('.allRightsContainerInTableCell');
//
//			var tagObject = {};
//			var rightsObject = {};
//			rightsObject['read'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'read');
//			rightsObject['edit'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'edit');
//			rightsObject['delete'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'delete');//delete is a reserved word
//
//			tagObject.text = userName;
//			tagObject.value = userId;
//			tagObject.data = rightsObject;
//			$('.usersTagsinput').tagsinput('add', tagObject);
//			
//			usersToRowsMap[userName] = rowIndex;
//		}
//	});
	
	$('#relateUsersToProjectsTable').on('click', '.removeUserFromProject', function(){
		currentPage = $('#relateUsersToProjectsTable').DataTable().page.info().page;
		setPage = true;
		
		$(this).removeClass('removeUserFromProject').addClass('addUserToProject');
		$(this).find('.icon-remove').addClass('icon-plus').removeClass('icon-remove');
		$(this).contents().first().replaceWith('Add');
		
		var htmlRow = $(this).closest('tr')[0];
		var rowData = $relateUsersToProjectsTable.rows(htmlRow).data();
		var userUUID;
		if(rowData !== undefined && rowData !== null) {
			userUUID = rowData[0][7];
			$('.usersTagsinput').tagsinput('remove', userUUID);
		}

		//deselect checboxes
		$(htmlRow).find('.RightsCheckboxes').attr('checked', false);
	});
}

function relateUsersToProjectsCheckboxesEvents(){
//	$('#relateUsersToProjectsTable').on('change', '.RightsCheckboxes', function(){
//		var htmlRow = $(this).closest('tr')[0];
//		var rowData = $relateUsersToProjectsTable.rows(htmlRow).data();
//		var id = rowData[0][7];
//		var $checkBoxesContainer = $(htmlRow).find('.allRightsContainerInTableCell');
//		
//		var tags = $('.usersTagsinput').tagsinput('items');
//		$.each(tags, function(index, value){
//			if(value.value === id){
//				var rightsObject = {};
//				rightsObject['read'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'read');
//				rightsObject['edit'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'edit');
//				rightsObject['delete'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'delete');//delete is a reserved word
//				
//				value.data = rightsObject;
//				$('.usersTagsinput').tagsinput('refresh');
//				return false;
//			}
//		});
//
//	});
}

function relateUsersGroupsToProjectsCheckboxesEvents(){
//	$('#relateUsersGroupsToProjectsTable').on('change', '.RightsCheckboxes', function(){
//		var htmlRow = $(this).closest('tr')[0];
//		var rowData = $relateUsersGroupsToProjectsTableD.rows(htmlRow).data();
//		var id = rowData[0][5];
//		var $checkBoxesContainer = $(htmlRow).find('.allRightsContainerInTableCell');
//		
//		var tags = $('.usersGroupsTagsinput').tagsinput('items');
//		$.each(tags, function(index, value){
//			if(value.value === id){
//				var rightsObject = {};
//				rightsObject['read'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'read');
//				rightsObject['edit'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'edit');
//				rightsObject['delete'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'delete');//delete is a reserved word
//				
//				value.data = rightsObject;
//				$('.usersGroupsTagsinput').tagsinput('refresh');
//				return false;
//			}
//		});
//
//	});
}

function checkIfRightsCheckboxIsCheckedByRightName(checkBoxesContainer, rightName){
	var $checkbox = $(checkBoxesContainer.find('.RightsCheckboxes[name=' + rightName + ']'));
	var isChecked = $checkbox.is(':checked');
	var val = isChecked ? 1 : 0; 
	return val;
}

function assignUsersToProjectsButtonsEvents(){
	$('#assignUsersToProjectGroupssTable').off('click').on('click', '.addUserToProject', function(){
		$(this).removeClass('addUserToProject').addClass('removeUserFromProject');
		$(this).find('.icon-plus').removeClass('icon-plus').addClass('icon-remove');
		$(this).contents().first().replaceWith('Remove');

		var htmlRow = $(this).closest('tr')[0];
		var rowData = $('#assignUsersToProjectGroupssTable').DataTable().rows(htmlRow).data();
		var rowIndex = $('#assignUsersToProjectGroupssTable').DataTable().row(htmlRow).index();
		if(rowData !== undefined && rowData !== null) {
			var userName = rowData[0][0];
			var userId = rowData[0][6];//there might be duplicate values
			var tagObject = {};
			tagObject.text = userName;
			tagObject.value = userId;
			$('.assignedUsersTagsinput').tagsinput('add', tagObject);

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
		var userID;
		if(rowData !== undefined && rowData !== null) {
			userName = rowData[0][0];
			userID = rowData[0][6];
			$('.assignedUsersTagsinput').tagsinput('remove', userID);
		}
	});
}

function relateUsersGroupsToProjectsButtonsEvents(){
//	$('#relateUsersGroupsToProjectsTable').off('click').on('click', '.addUserGroupsToProject', function(){
//		$(this).removeClass('addUserGroupsToProject').addClass('removeUserGroupsFromProject');
//		$(this).find('.icon-plus').removeClass('icon-plus').addClass('icon-remove');
//		$(this).contents().first().replaceWith('Remove');
//		
//		var htmlRow = $(this).closest('tr')[0];
//		var rowData = $relateUsersGroupsToProjectsTableD.rows(htmlRow).data();
//		var rowIndex = $relateUsersGroupsToProjectsTableD.row(htmlRow).index();
//		if(rowData !== undefined && rowData !== null) {
//
//			var userName = rowData[0][0];
//			var userId = rowData[0][5];//there might be duplicate values
//			var $checkBoxesContainer = $(htmlRow).find('.allRightsContainerInTableCell');
//
//			var tagObject = {};
//			var rightsObject = {};
//			rightsObject['read'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'read');
//			rightsObject['edit'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'edit');
//			rightsObject['delete'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'delete');//delete is a reserved word
//
//			tagObject.text = userName;
//			tagObject.value = userId;
//			tagObject.data = rightsObject;
//			$('.usersGroupsTagsinput').tagsinput('add', tagObject);
//			
//			usersGroupsToRowsMap[userName] = rowIndex;
//		}
//	});
	
//	$('#relateUsersGroupsToProjectsTable').on('click', '.removeUserGroupsFromProject', function(){
//		$(this).removeClass('removeUserGroupsFromProject').addClass('addUserGroupsToProject');
//		$(this).find('.icon-remove').addClass('icon-plus').removeClass('icon-remove');
//		$(this).contents().first().replaceWith('Add');
//		
//		var htmlRow = $(this).closest('tr')[0];
//		var rowData = $relateUsersGroupsToProjectsTableD.rows(htmlRow).data();
//		var userName;
//		var userGroupID;
//		if(rowData !== undefined && rowData !== null) {
//			userName = rowData[0][0];
//			userGroupID = rowData[0][5];
//			$('.usersGroupsTagsinput').tagsinput('remove', userGroupID);
//		}
//	});
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
		
		window.config.userLocale = userinfoObject.locale.replace("_","-");
		window.config.dataVisualizationTabName = userinfoObject.tabName;
		window.config.layerProjectTabName = userinfoObject.layerProjectTabName;
		window.config.layerProjectTitle = userinfoObject.layerProjectTitle;
		window.config.layerProjectSubtitle = userinfoObject.layerProjectSubtitle;

        $('#visualization-tab').text(userinfoObject.tabName);
        $('#project-layer-organization').text(userinfoObject.layerProjectTabName);
        $('#projects').text(userinfoObject.layerProjectTitle);
        $('#projectsSubheading span').text(userinfoObject.layerProjectSubtitle);

        $(".modalHeader.createHeader").find("span").text(userinfoObject.layerProjectSubtitle);
        $(".modalHeader.editHeader").find("span").text(userinfoObject.layerProjectSubtitle);
	};

	$.ajax(
			{
				url: theURl,
				type: 'post',
				datatype:'json',
				data: theData,
				success: function(data){
					callback(data);
//					initializeProjectSelectionTable();
				},
				error: function (xhr, ajaxOptions, thrownError) {
					$('#InternalServerErrorModal').modal('show');
				}
			}
		);
}

function removePlugins() {
	$('.functionsAccordion').remove();
	
	var $container = $('<div></div>', {
		class : 'functionsAccordion new'
	});
	
	$container.insertAfter('#available-functions-element');
//	$('#functionsPanel').prepend($container);
	
//	$('.functionsAccordion').html('');
}

function retrieveAvailablePluginsForProject(principalName, tenantName, pluginName, projectName, pluginId){
	
	pluginIdOfLatestLoadedPlugin = pluginId;
	
	var parameters = "principalName=" + encodeURIComponent(principalName)
					+ "&tenantName=" + encodeURIComponent(tenantName)
					+ "&pluginName=" + encodeURIComponent(pluginName)
					+ "&projectName=" + encodeURIComponent(projectName)
					+ "&pluginId=" + encodeURIComponent(pluginId);
	
	var callback = function(data){
		hideSpinner();
	};
	
	var urlMVC = encodeURIComponent("plugin/loadPluginByNameAndTenant");
	var url = createLink(resourceURL, urlMVC, parameters);
	var context = null;
	
	showSpinner();
//	appendFunctionContainerToDom(pluginName);
	
	$.getScript(url)
		.done(function( script, textStatus ) {
			hideSpinner();
//			removeNewPluginHelperClasses();
		})
		.fail(function( jqxhr, settings, exception ) {
			$('.wizard').modal('hide');
			$('#InternalServerErrorModal').modal('show');
//			destroyNewPluginContainer();
			hideSpinner();
		});
}