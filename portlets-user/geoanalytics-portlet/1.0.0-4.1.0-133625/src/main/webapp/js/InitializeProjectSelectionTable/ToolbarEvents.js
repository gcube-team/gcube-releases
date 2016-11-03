function toolbarEvents(){
//	Increase width of search input
	$('#ProjectSelectionTable_filter label input').addClass('input-xlarge');
//  Append search input to toolbar.	
	$('#toolbar').append($('#ProjectSelectionTable_filter'))
	
//	Change sorting icons on click
	$('.customTableToolbarButtons:not(#createNewProject)').on('click', function(){
		$('.customTableToolbarButtons:not(#createNewProject)').not(this)
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
		$('#BBOXModal').modal('show');
		$('.createHeader').removeClass('hidden');
		$('.editHeader').addClass('hidden');
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

