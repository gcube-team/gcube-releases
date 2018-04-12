function addTagsInputFunctionalityToSearchInput(){
	var input = $('<input>',{
		id: 'tagsForWhenYouWantToAssignUsersToGroups',
		'data-role': 'tagsinput',
		css : {
			display: 'none'
		},
		type: 'text'
	});
	$('#CurrentUsersTable_filter label').append(input);
	
	$('#tagsForWhenYouWantToAssignUsersToGroups').tagsinput({
		trimValue: true
	});
	$('#tagsForWhenYouWantToAssignUsersToGroups').on('itemAdded',function(){
		var tagsTexts = $('div.bootstrap-tagsinput span.label-info');
		$.each(tagsTexts, function(index,value){
			var val = $(this).html();
			if(!(val.indexOf('#') > -1)){
				$(this).html('#' + val);
			}
		});
	}).on('itemRemoved',function(){
		$('table#CurrentUsersTable').DataTable().columns( 5 ).search('', true, false).draw();
		$('table#CurrentUsersTable th:first').removeClass('sorting_asc');
		filterUserTableByUsersThatDontBelongInAGroup = false;
		var countTableRows = $('table#CurrentUsersTable tbody tr').length;
		var countSelectedRows = $('table#CurrentUsersTable tr.selected').length;
		if(countTableRows === countSelectedRows){
			$('#CurrentUsersTable th:first').addClass('none');
		}else {
			$('#CurrentUsersTable th:first').removeClass('none');
		}
	});
}