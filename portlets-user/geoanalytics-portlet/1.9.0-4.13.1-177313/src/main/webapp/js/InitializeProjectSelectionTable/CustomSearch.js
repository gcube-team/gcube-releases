function CustomSearch(){
	$('#ProjectSelectionTable_filter input[type=search]').off().on('keyup',function(){
		var pattern = $('#ProjectSelectionTable_filter input[type=search]').val();
		searchVal = pattern;
		var regex = new RegExp(pattern, 'gi');
		var matchingCells = [];
		$.each(tileElementsGlobal, function(index, value){
			if(value !== null && value !== undefined){
				var matches = value.text().match(regex);
				if(matches !== null){
					matchingCells.push(value);
				}
			}
		});
		
		$table.DataTable().clear();
		
		$.each(matchingCells, function(index, value){
			matchingCells[index] = jqueryTileToHML(value);
		});
		
		if(matchingCells.length > 0){
			var dataForTheTable = tableData(matchingCells);
			$table.dataTable().fnAddData(dataForTheTable);
		} else {
			$table.DataTable().draw();
		}
	});
}

function tableData(someData){
	var dataLength = someData.length;
	var retArray = [];
	var retObject = {};
	if(dataLength <= numOfCols){
		retObject = initializeTableDataObject();
		
		for(var i=0;i<dataLength;i++){
			retObject[mapNumbersInWordFormWihtOthers[i+1]] = someData[i];
		}
		retArray.push(retObject);
		return retArray;
	} else {
		$.each(someData, function(index, value){
			if(index%numOfCols === 0) {
				retObject = initializeTableDataObject();
			}
			retObject[mapNumbersInWordFormWihtOthers[index%4+1]] = someData[index];
			
			if($(someData[index]).find('.hiddenColumn.hidden').text() !== ''){
				retObject['helperColumn'] = $(someData[index]).find('.hiddenColumn.hidden').text();
			}
			if(index%numOfCols === numOfCols-1 || index === someData.length-1){
				retArray.push(retObject);
			}
		});
		return retArray;
	}
}

function initializeTableDataObject(){
	var ob = {};
	$.each(mapNumbersInWordFormWihtOthers, function(index, value){
		ob[mapNumbersInWordFormWihtOthers[index+1]] = '';
	});
	return ob;
}

function tableDataToJQueryElements(tableData) {
	var jqueryElements = [];
	$.each(tableData, function(index,value){
		for(var key in tableData[index]) {
			if(key !== 'helperColumn' && key !== 'undefined' && tableData[index][key] !== ''){
				jqueryElements.push($(tableData[index][key]));
			}
		}
	});
	return jqueryElements;
}