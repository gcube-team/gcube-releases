function rolesTableInitialization(){
	$('#RolesManagementContainer table#CurrentRolesTable').dataTable({
		data : rolesTableData,
		columns : [
			{
				data : "All",
				orderable : false
			},
			{
				data : "Name",
				orderable : true
			},
			{
				data : "Description",
				orderable : true
			}
		],
		language : {
				"paginate": {
			        "next":       "",
			        "previous":   ""
			    }
		},
        dom: '<"toolbarContainer">ifrtlp',
	});
}

function retrieveRoles(){
	var theData = dataToBeSendViaAJAX("mode", RETRIEVE_ROLES);
	
	var callBack = function(data){
		var content= JSON.parse(data);
		
		currentRoles = [];
		currentRoles = content.groupRoles;

		if(!$.isEmptyObject(content) && !$.isEmptyObject(currentRoles)){
			for(var i = 0; i < currentRoles.length; i++){
				currentRoles[i].role = new roleObjectForDataTable(
								'<i class="icon-ok"></i>',
								currentRoles[i].role.name,
								currentRoles[i].role.description,
								currentRoles[i].role.roleID
				);
				currentRoles[i].role = surroundObjectPropWithDiv(currentRoles[i].role);
			}
			
			$('#RolesManagementContainer table#CurrentRolesTable').DataTable().clear();
			for(var i = 0; i < currentRoles.length; i++){
				$('table#CurrentRolesTable').dataTable().fnAddData(currentRoles[i].role);
			}

			$('#RolesManagementContainer div#toolbar').addClass('hiddenToolbar').removeClass('shownToolbar');
			
			hidePreloader();
		}else{
			hidePreloader();
			
			$('#InternalServerErrorModal').modal('show');
		}
		removeArrowFromFirstTableColumn();
	}
	
	AJAX_CALL_POST(theData, callBack);
}

function editRole(roleID, roleName ,roleDescription){
	var theData = dataToBeSendViaAJAX("mode", EDIT_ROLE);
	theData = dataToBeSendViaAJAX("roleID", roleID, theData);
	theData = dataToBeSendViaAJAX("roleName", roleName, theData);
	theData = dataToBeSendViaAJAX("roleDescription", roleDescription, theData);
	
	var callBack = function(data){
		var content= JSON.parse(data);
		if(!$.isEmptyObject(content) && content.editRole){
			$('#EditRolesModal').modal('hide');
			retrieveRoles();
		}else {
			$('div.modal.fade.in').modal('hide');
			hidePreloader();
			$('#InternalServerErrorModal').modal('show');
		}
		
	}
	
	AJAX_CALL_POST(theData, callBack);
}

function createRole(roleName, roleDescription){
	var theData = dataToBeSendViaAJAX("mode", ADD_ROLE);
	theData = dataToBeSendViaAJAX("roleName", roleName, theData);
	theData = dataToBeSendViaAJAX("roleDescription", roleDescription, theData);
	
	var callBack = function(data){
		var content= JSON.parse(data);
		if(!$.isEmptyObject(content) && content.addRole){
			$('#AddRoleModal').modal('hide');
			retrieveRoles();
		}else {
			$('div.modal.fade.in').modal('hide');
			hidePreloader();
			$('#InternalServerErrorModal').modal('show');			
		}
	};
	
	AJAX_CALL_POST(theData, callBack);
}

function deleteRole(roleID){
	var theData = dataToBeSendViaAJAX("mode", DELETE_ROLE);
	theData = dataToBeSendViaAJAX("roleID", roleID, theData);
	
	var callBack = function(data){
		var content= JSON.parse(data);
		if(!$.isEmptyObject(content) && content.deleteRole){
			$('#DeleteRoleModal').modal('hide');
			retrieveRoles();
		}else {
			$('div.modal.fade.in').modal('hide');
			hidePreloader();
			$('#InternalServerErrorModal').modal('show');			
		}
	};
	
	AJAX_CALL_POST(theData, callBack);
}

function dataToBeSendViaAJAX(fieldName, value, theObject){
	var returnObject;
	if(typeof theObject === "object"){	
		returnObject = theObject;
	}else {
		returnObject = {};	
	}
	
	returnObject[nameSpace + fieldName] = value;
	
	return returnObject;
}

function roleObjectForDataTable(All, Name, Description, RoleID){
	this.All = All;
	this.Name = Name;
	this.Description = Description;
	this.RoleID = RoleID;
}

function surroundObjectPropWithDiv(object){
	for(var prop in object){
		if(object[prop].length === 0)object[prop] = "-";
		if(object[prop] === '123')object[prop] = "";
		object[prop] = '<div>' + object[prop] + '</div>';
	}
	return object;
}

function buildToolbarOverTable(){
	var toolbar = $('<div></div>', {
		id: 'toolbar',
		'class' : 'hiddenToolbar'
	});
	
	toolbar.append($('<div></div>', {
		id : 'addNew',
		text : 'Add new'
	})).append($('<div></div>', {
		id : 'editSelected',
		text : 'Edit Selected'
	})).append($('<div></div>', {
		id : 'deleteSelected',
		text : 'Delete Selected'
	}));
	
	$("div.toolbarContainer")
		.append(
				$('<div></div>', {
					id : 'toolbarHr'
				})
		).append(toolbar);
	
	var a = $('<div></div>', {
		'class': 'searchDiv',
		'data-toggle' : "tooltip",
		'data-placement': "top",
		'data-original-title':"Search"
	}).append($('<i></i>', {
		'class' : "icon-search"
	}).prop('outerHTML'));
	$('#RolesManagementContainer #CurrentRolesTable_filter').append(a);
	$('#RolesManagementContainer #CurrentRolesTable_filter label').toggleClass('hideMe');
	
	$('#RolesManagementContainer div#changeUsersRolesModal div.modal-body span#textAboveTagsInput div.bootstrap-tagsinput').addClass('span9');
	
	$('#RolesManagementContainer .usersRequestsTableToolbarContainer')
		.addClass('hiddenToolbar')
		.prop('id', 'usersRequestsTableToolbarContainer')
		.append($('<div></div>', {
			id : 'acceptSeleced',
			text : 'Accept Seleced'
		})).append($('<div></div>', {
			id : 'rejectSeleced',
			text : 'Reject Seleced'
		}));

	var div = $('<div></div>', {
		id : 'borderFirstScreen'
	});
	div.insertBefore('.usersRequestsTableToolbarContainer');
}

function AJAX_CALL_POST(theData, callBack){
	$.ajax(
			{
				url: loginURL,
				type: 'post',
				datatype:'json',
				data: theData,
				success: function(data){
					callBack(data);
				},
				error: function (xhr, ajaxOptions, thrownError) {
					$('div.modal.fade.in').modal('hide');
					hidePreloader();
					$('#InternalServerErrorModal').modal('show');	
//					alert(xhr.status);
//					alert(thrownError);
				}
			}
		);
}