function sortTilesAlphabetically(tiles, orderOfSorting) {
	var mapProjectsToPositionInArray = {};
	var mapProjectNamesToPositionInArray = {};
	var projectNames = [];
	var retTiles = [];
	
	$.each(tiles, function(index, value) {
		var projectName = $(this).find('h4.projectTitle').text();
		mapProjectsToPositionInArray[projectName] = index;
		projectNames.push(projectName);
	});
	
	projectNames.sort();
	
	$.each(projectNames, function(index, value){
		mapProjectNamesToPositionInArray[value] = index;
	});
	
	var value;
	var value2;
	for(var key in mapProjectsToPositionInArray){
		value = mapProjectsToPositionInArray[key];
		value2 = mapProjectNamesToPositionInArray[key];
		
		retTiles[value2] = tiles[value];
	}
	if(orderOfSorting === SORT_DESC){
		retTiles.reverse();
	}
	$.each(retTiles, function(index, value){
		retTiles[index] = jqueryTileToHML(retTiles[index]);
	});
	
	return retTiles;
}

function sortTilesByDate(tiles, orderOfSorting) {
	var mapProjectsToPositionInArray = {};
	var mapProjectDatesToPositionInArray = {};
	var projectDates = [];
	var retTiles = [];
	
	$.each(tiles, function(index, value) {
		var projectDate = $(this).find('.projectTileDate').data('date');
		var dateID = Math.floor(Math.random() * 1000000) + 1;//Added this because you might have identical dates,
//		which will try to create identical object properites
//		and when you add it a property that already exists to the object it will update the existing property,
//		it won't add a new one.
		if(projectDate !== undefined){
			projectDate = projectDate.toString();
		} else {
			return true;
		}
		
		projectDate += dateID.toString();
		mapProjectsToPositionInArray[projectDate] = index;
		projectDates.push(projectDate);
	});
	
	projectDates.sort();
	
	$.each(projectDates, function(index, value){
		mapProjectDatesToPositionInArray[value] = index;
	});
	
	var value;
	var value2;
	for(var key in mapProjectsToPositionInArray){
		value = mapProjectsToPositionInArray[key];
		value2 = mapProjectDatesToPositionInArray[key];
		
		tiles[value].find('.hiddenColumn.hidden').text(value2);
		retTiles[value2] = tiles[value];
	}
	if(orderOfSorting === SORT_DESC){
		retTiles.reverse();
	}
	$.each(retTiles, function(index, value){
		retTiles[index] = jqueryTileToHML(retTiles[index]);
	});
	
	return retTiles;
}

function jqueryTileToHML(jqueryTile){
	if(jqueryTile == undefined){
		return '';
	}else if(jqueryTile[0] !== undefined){
		return jqueryTile[0].outerHTML;
	}else{
		return '';
	}
}

function filterColumnsByDateDesc() {
	var tr = $table.DataTable().rows().data();
	var dates = [];
	var mapFirstDateToRowObject = {};
	
	$.each(tr, function(index, value) {
		var firstDate = value.one;
		var dateID = Math.floor(Math.random() * 1000000) + 1;
		firstDate += dateID;
		dates.push(firstDate);
		mapFirstDateToRowObject[firstDate] = value;
	});
	
	dates.sort();
	var data = [];
	$.each(dates, function(index, value){
		data.push(mapFirstDateToRowObject[value]);
	});
	
	return data;
}

function filterColumnsByDate(typeOfOrdering) {
	var cells = $table.DataTable().cells().nodes();
	if(cells.length > 1){
		var cellData;
		var dates = [];
		var dateToCellDataObject = {};
		
		$.each(cells, function(index, value){
			cellData = $table.DataTable().cell(this).data();
			var date;
			if(cellData === ''){ 
				date='';
			}else{
				date = $(value).find('.projectTileDate').data('date').toString();
			}
	    	var dateID = Math.floor(Math.random() * 1000000) + 1;
	    	date += dateID.toString();
	    	dates.push(date);
	    	dateToCellDataObject[date] = cellData;
		});
		
		dates.sort();
		
		if(typeOfOrdering === SORT_ASC && dates.length > 0){
			
		} else if(typeOfOrdering === SORT_DESC && dates.length > 0) {
			var data = [];
			if(cells.length === dates.length){
				$.each(cells, function(index, value){
					$table.DataTable().cells().data(dateToCellDataObject[index]).draw();
				});
			}
			if(!checkIfSorted()){
				reorderCellsByDate();
			}
		}
	}
	
	return;
}

function reorderCellsByDate(){
	var firstCellsDates = [];
	var mapFirstDateToRowData = {};
	var flag = false;
	var filteredData = $table.DataTable()
    .column( 0 )
    .data()
    .filter( function ( value, index ) {
    	var $Cell = $($(this)[index]);
    	var date = $Cell.find('.projectTileDate').data('date').toString();
    	var dateID = Math.floor(Math.random() * 1000000) + 1;
    	date += dateID.toString();
    	firstCellsDates.push(date);
    	var data = $table.DataTable().row(index).data();
    	mapFirstDateToRowData[date] = data;
    });
	
	firstCellsDates.sort();
	$table.DataTable().clear();
	$.each(firstCellsDates, function(index, value){
		$table.DataTable().rows(index).data(mapFirstDateToRowData[value]);
	});
}

function checkIfSorted() {
	var ret = true;
	var trs = $table.DataTable().rows().nodes();
	var allData = $table.DataTable().rows().data();
	
	if(allData.length>2){
		var trFirstDates = [];
		
		$.each(trs, function(index, value){
			var dateID = Math.floor(Math.random() * 1000000) + 1;
			trFirstDates.push($(this).find('td:first .projectTileDate').data('date').toString() + dateID.toString());
			if(index > 0) {
				if(trFirstDates[index] > trFirstDates[index-1]){
					ret = false;
					return false;
				}
			}
		});
	}
	
	return ret;
}