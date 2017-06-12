function toolbarEvents(){
//	Increase width of search input
	$('#ProjectSelectionTable_filter label input').addClass('input-xlarge');
//  Append search input to toolbar.	
	$('#toolbar').append($('#ProjectSelectionTable_filter'))
	
//	Change sorting icons on click
	$('.customTableToolbarButtons:not(#createNewProject):not(#manipulateProjectGroups)').on('click', function(){
		$('.customTableToolbarButtons:not(#createNewProject):not(#manipulateProjectGroups)').not(this)
		.removeClass('clicked').removeClass('sortDesc').removeClass('sortAsc');
		if(!$(this).hasClass('clicked')){
			$(this).addClass('sortDesc');
			$(this).addClass('clicked');
		}else if($(this).hasClass('clicked')){
			$(this).toggleClass('sortDesc');
			$(this).toggleClass('sortAsc');
		}
	});
	
//	Change styling of button on click
	$('#createNewProject').off().on('click', function(){
		clearModals();
		$(this).addClass('clicked');
		EDITMODE = false;
		mapBBOX.getView().fit(initialMapExtent, mapBBOX.getSize());
//		map.getView().setCenter(ol.proj.transform([22.00, 37.00], 'EPSG:4326', 'EPSG:3857'));
		mapBBOX.getView().setZoom(4);
		mapBBOX.updateSize();
		$('#BBOXModal').modal('show');
		$('.createHeader').removeClass('hidden');
		$('.editHeader').addClass('hidden');
	});
	
	$('#manipulateProjectGroups').off().on('click', function(){
		clearModals();
		$(this).addClass('clicked');
		
		var div = '<div></div>';
		var $outerDiv = $(div);
		
		var $tableGroupsContainer = $(div, {
			class: 'tableGroupsContainer'
		});
		var $theTable = $('<table></table>',{
			id:'groupsTable',
			class: 'no-wrap',
			style: 'width:100%'
		});
		var $tBody = $('<tbody></tbody>',{});
		$theTable.append($tBody);
		$tableGroupsContainer.append($theTable);
		
		$createNewGroupContainer = $(div, {
			class: 'createNewGroupContainer'
		});
		$rowFluid = $(div,{
			class: 'row-fluid'
		});
		$newGroupContainer = $(div,{
			class: 'span2 newGroupContainer hidden'
		});
		$label = $('<label></label>',{
			'for': 'groupName',
			text: 'Name:'
		});
		$newGroupContainer2 = $(div,{
			class: 'span8 newGroupContainer hidden'
		});
		var $input= $('<input/>',{
			type: 'text',
			name: 'groupName',
			id: 'groupName',
			placeholder: 'Group Name...',
			class: 'input-xlarge'
		});
		var $div = $(div,{
			class:''
		});
		var $newGroupCreateGroup = $('<button></button>',{
			id: 'newGroupCreateGroup',
			class: '',
			text: 'New group'
		});
		
		$div.append($newGroupCreateGroup);
		$newGroupContainer2.append($input);
		$newGroupContainer.append($label);
		$rowFluid.append($newGroupContainer).append($newGroupContainer2).append($div);
		$createNewGroupContainer.append($rowFluid);
		$outerDiv.append($tableGroupsContainer).append($createNewGroupContainer);
		
		if($('#GroupsManipulationModal').length === 0){
			initializeGroupNamesTableForTheFirstTime = true;
			createBootstrapModal(
					'GroupsManipulationModal', "",
					"manipulateGroup", "Manipulate your groups",
					$outerDiv,
					'ef', "", 'Cancel');
			GroupsManipulationModalEvents();
			initializeGroupsTable();
		}
		
		$('#GroupsManipulationModal').modal('show');
	});
	
//	Add behavior to each individual button on click
	$('#alphabOrder').on('click', function() {
		if($(this).hasClass('sortAsc')){
			if($table.find('tbody tr td').hasClass('dataTables_empty')){
				return;
			}
			var theTableData = $table.DataTable().rows().data();
			var elementsToSort = tableDataToJQueryElements(theTableData);
			var descProjectDataForTheTable = tableData(sortTilesAlphabetically(elementsToSort, SORT_ASC));
			$table.DataTable().clear();
			$table.dataTable().fnAddData(descProjectDataForTheTable);
			$table.DataTable().order([0, 'asc']).draw();
			$('#ProjectSelectionTable_filter input').val(searchVal);
		} else if($(this).hasClass('sortDesc')) {
			if($table.find('tbody tr td').hasClass('dataTables_empty')){
				return;
			}
			var theTableData = $table.DataTable().rows().data();
			var elementsToSort = tableDataToJQueryElements(theTableData);
			var descProjectDataForTheTable = tableData(sortTilesAlphabetically(elementsToSort, SORT_DESC));
			$table.DataTable().clear();
			$table.dataTable().fnAddData(descProjectDataForTheTable);
			$table.DataTable().order([numOfCols-1, 'desc']).draw();
			$('#ProjectSelectionTable_filter input').val(searchVal);
		}
	});
	
	$('#mostRecent').on('click', function() {
		if($(this).hasClass('sortAsc')){
			if($table.find('tbody tr td').hasClass('dataTables_empty')){
				return;
			}
			var theTableData = $table.DataTable().rows().data();
			var elementsToSort = tableDataToJQueryElements(theTableData);
			var data = tableData(sortTilesByDate(elementsToSort, SORT_ASC));
			$table.DataTable().clear();
			$table.dataTable().fnAddData(data);
			$table.DataTable().order([numOfCols, 'asc']).draw();
			$('#ProjectSelectionTable_filter input').val(searchVal);
		} else if($(this).hasClass('sortDesc')) {
			if($table.find('tbody tr td').hasClass('dataTables_empty')){
				return;
			}
			var theTableData = $table.DataTable().rows().data();
			var elementsToSort = tableDataToJQueryElements(theTableData);
			var descProjectDataForTheTable = tableData(sortTilesByDate(elementsToSort, SORT_DESC));
			$table.DataTable().clear();
			$table.dataTable().fnAddData(descProjectDataForTheTable);
			$table.DataTable().order([numOfCols, 'desc']).draw();
			$('#ProjectSelectionTable_filter input').val(searchVal);
		}
	});
}

function putFilterInsideToolbar(){
//	Increase width of search input
	$('#relateUsersToProjectsTable_filter label input').addClass('input-xlarge');
//  Append search input to toolbar.	
	$('.usersToProjectToolbarContainer').text('Users in this VRE');
	$('.usersToProjectToolbarContainer').append($('#relateUsersToProjectsTable_filter'));
}

function groupsManipulation(){
	var $button = $('<button></button>',{
		text: 'Groups manipulation',
		id: 'groupsManipulation'
	});
	$button.click(function(){
		$('#RelateUsersToProjectModal').modal('hide');
		$('#GroupsManipulationModal').modal('show');
		if(!$.fn.DataTable.isDataTable( '#groupsTable' )){
			initializeGroupsTable();
		}
	});
	$('#RelateUsersToProjectModal .groups').append($button);
	
}