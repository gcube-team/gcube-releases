$('.modal-footer button#assignUsersToGroupModalBtn').off('click').on('click',function(){
	assignUsersToGroupModalBtnPressed = true;
	
	$('#CurrentUsersTable_filter label input:first').animate({height:'show'});
	
	$('#displayGroupTeamUsersModal').modal('hide');
	//view $('#displayGroupTeamUsersModal').on('hidden', function(){
	
//	var modalHide = function(){
//		$('#displayGroupTeamUsersModal').modal('hide');
//	};
//	
//	var showAfterModalHides = function(){
//		setTimeout(function(){
//			$('#CurrentUsersTable_filter label').removeClass('hideMe');
//			$('#CurrentUsersTable_filter label input').focus();
//			$('li#userManagement a.tabTitle').tab('show');//tab('show') applies on data-toggle="tab" element, only
//		},700);
//	};
//	
//	var aMhI = afterModalHidesItself(modalHide);
//	
//	aMhI.done(showAfterModalHides);
});

function afterModalHidesItself(fn, time){
	var dfd = $.Deferred();
	
	setTimeout(function(){
		dfd.resolve(fn());
	}, time || 0);
	
	return dfd.promise();
}

$('#displayGroupTeamUsersModal').on('hidden', function(){
	if(assignUsersToGroupModalBtnPressed){
		$('#CurrentUsersTable_filter label').removeClass('hideMe');
		$('#tagsForWhenYouWantToAssignUsersToGroups').tagsinput('removeAll');
		$('#tagsForWhenYouWantToAssignUsersToGroups').tagsinput('add', $.trim($('#teamNameHeader').text()));
		var regex = '^((?!' + $.trim($('#teamNameHeader').text()) + ').)*$';
		
		$('table#CurrentUsersTable').DataTable().columns( 5 ).search(regex, true, false).draw();
		$('table#CurrentUsersTable th:first').removeClass('sorting_asc');
		$('li#userManagement a.tabTitle').tab('show');//tab('show') applies on data-toggle="tab" element, only
		assignUsersToGroupModalBtnPressed = false;
	}
});