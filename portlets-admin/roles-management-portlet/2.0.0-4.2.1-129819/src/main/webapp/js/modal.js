function modalFunctionality(){
	$('#RolesManagementContainer #editRolesModalOK').off('click').on('click', function(){
		var rolesTable =  $('#CurrentRolesTable');
		var selectedRow = rolesTable.find('tr.selected:first')[0];
		var selectedRowData = rolesTable.dataTable().fnGetData(selectedRow);
		
		var roleName = $('#EditRoleName').val();
		if(roleName === '')return;
		var roleDescription = $('#EditRoleDescription').val();
		
		var roleIDDiv = $($.parseHTML(selectedRowData.RoleID));
		var roleID = roleIDDiv.text();
		
		showPreloader();
		
		editRole(roleID, roleName ,roleDescription);
		
	});
	
	$('#RolesManagementContainer #deleteRoleModalOK').off('click').on('click', function(){
		var rolesTable =  $('#CurrentRolesTable');
		var selectedRow = rolesTable.find('tr.selected:first')[0];
		var selectedRowData = rolesTable.dataTable().fnGetData(selectedRow);
		
		var roleIDDiv = $($.parseHTML(selectedRowData.RoleID));
		var roleID = roleIDDiv.text();
		
		showPreloader();
		
		deleteRole(roleID);
	});
	
	$('#RolesManagementContainer #addRoleModalOK').off('click').on('click', function(){
		var roleName = $('#AddNewRoleName').val();
		var roleDescription = $('#AddNewRoleDescription').val();
		if(roleName === '')return;
		showPreloader();
		
		createRole(roleName, roleDescription);
	});
}