function navBar(){
//	$('div.searchDiv').off().on('click', function(){
//		if($(this).parent('div').attr('id') === 'CurrentUsersTable_filter'){
////			$('#CurrentUsersTable_filter label').toggleClass('hideMe');
////			$('#CurrentUsersTable_filter label input').focus();
//		}
//		if($(this).parent('div').attr('id') === 'usersRequestsTable_filter'){
//			$('#usersRequestsTable_filter label').toggleClass('hideMe');
//			$('#usersRequestsTable_filter label input').focus();
//		}
//		$(this).closest('div.dataTables_filter').find('label input:first').animate({height:'toggle'})
//			
//	});
	$(document).off('click').on('click', 'div.searchDiv', function(){
//		if($(this).parent('div').attr('id') === 'GroupTeamsTable_filter'){
//			$('#GroupTeamsTable_filter label').toggleClass('hideMe');
//			$('#GroupTeamsTable_filter label input').focus();
//		} else if($(this).parent('div').attr('id') === 'GroupTeamsTableUsers_filter'){
//			$('#GroupTeamsTableUsers_filter label').toggleClass('hideMe');
//			$('#GroupTeamsTableUsers_filter label input').focus();
//		}
		$(this).closest('div.dataTables_filter').find('label input:first').animate({width:'toggle'});
		$(this).toggleClass('active');
	});
}