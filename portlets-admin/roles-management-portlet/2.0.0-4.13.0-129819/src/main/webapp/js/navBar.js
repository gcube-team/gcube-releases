function navBar(){
	$('#RolesManagementContainer div.searchDiv').off().on('click', function(){
		if($(this).parent('div').attr('id') === 'CurrentRolesTable_filter'){
			$('#RolesManagementContainer #CurrentRolesTable_filter label').toggleClass('hideMe');
			$('#RolesManagementContainer #CurrentRolesTable_filter label input').focus();
		}
	});
}