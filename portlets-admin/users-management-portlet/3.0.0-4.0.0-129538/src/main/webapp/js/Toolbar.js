function constructToolbarForCurrentUsersTable(){
	var toolbar = $('<div></div>', {
		id: 'toolbar',
		'class' : 'hiddenToolbar'
	});
	var dispSelected = $('<div></div>', {
		id : 'displaySelected',
		class: 'insideToolbar'
	});
	dispSelected.append($('<span></span>', {
		id : 'numOfSelectedRows'
	}));
	dispSelected.append($('<span></span>', {
		id : 'justSelectedText',
		text : ' selected'
	}));
	
	var notificationsWrapper = $('<div></div>', {
		id : 'userRequestsNotifications',
		'class' : 'notificationsShown visible-desktop',
		'data-toggle' : "tooltip",
		'data-placement':"left",
		'data-original-title':"Users' membership requests"
	});
	
	var notificationsNumberPlaceHolder = $('<div></div>', {
		id: 'notificationsNumberPlaceHolder',
		text: 0
	});
	
	var notificationsTextSpan = $('<span></span>', {
		id : 'notificationsTextDiv',
		text: ' pending requests',
		class : 'visible-desktop'
	});
	
	notificationsWrapper.append(notificationsNumberPlaceHolder);
	

	
	var notificationsWrapperTabletView = $('<div></div>', {
		id : 'userRequestsNotificationsTabletView',
		'class' : 'notificationsShown visible-tablet',
		'data-toggle' : "tooltip",
		'data-placement':"left",
		'data-original-title':"Users' membership requests"
	});
	
	var notificationsNumberPlaceHolderTabletView = $('<div></div>', {
		id: 'notificationsNumberPlaceHolderTabletView',
		text: 0
	});
	
	var notificationsTextSpanTabletView = $('<span></span>', {
		id : 'notificationsTextDivTabletView',
		text: ' pending requests',
		class : 'visible-tablet'
	});
	
	toolbar
	.append($('<div></div>', {
		id : 'deselectAll',
		class: 'insideToolbar',
		text : 'Deselect All'
	})).append($('<div></div>', {
		id : 'editSelected',
		class: 'insideToolbar',
		text : 'Edit Selected'
	})).append($('<div></div>', {
		id : 'assignRolesToUser',
		class: 'insideToolbar',
		text : 'Assign Roles'
	})).append($('<div></div>', {
		id : 'assignUsersToGroup',
		class: 'insideToolbar',
		text : 'Add to Group'
	})).append($('<div></div>', {
		id : 'deleteSelected',
		text : 'Delete Selected',
		class: 'insideToolbar'
	})).append(notificationsTextSpan).append(notificationsWrapper).append(dispSelected);
	
//	.append($('<div></div>', {
//		id : 'usersManagementDiv',
//		text : 'Pending Requests:'
//	}))//.append(currentUsersTableRefresh)
	
	$("#usersManagementPortletContainer div.toolbarContainer")
	.append($('<div></div>', {
		id : 'toolbarHr'
	})).append(toolbar);
	
	notificationsWrapperTabletView.append(notificationsNumberPlaceHolderTabletView);
	notificationsTextSpanTabletView.insertAfter(toolbar);
	notificationsWrapperTabletView.insertAfter(toolbar);
}

function searchInputFixForCurrentUsersTable(){
	var a = $('<div></div>', {
		'class': 'searchDiv',
		'data-toggle' : "tooltip",
		'data-placement': "top",
		'data-original-title':"Search"
	}).append($('<i></i>', {
		'class' : "icon-search"
	}).prop('outerHTML'));
	$('#CurrentUsersTable_filter').append(a);
	$('#CurrentUsersTable_filter label').toggleClass('hideMe');
	
	$('div#changeUsersRolesModal div.modal-body span#textAboveTagsInput div.bootstrap-tagsinput').addClass('span9');
}

function searchInputFixForMembershipRequestsTable(){
	var aa = $('<div></div>', {
		'class': 'searchDiv',
		'data-toggle' : "tooltip",
		'data-placement': "top",
		'data-original-title':"Search"
	}).append($('<i></i>', {
		'class' : "icon-search"
	}).prop('outerHTML'));
	$('#usersRequestsTable_filter').append(aa);
	$('#usersRequestsTable_filter label').toggleClass('hideMe');
}

function searchInputFixForSiteTeamsEditTable(){
	var aa = $('<div></div>', {
		'class': 'searchDiv',
		'data-toggle' : "tooltip",
		'data-placement': "top",
		'data-original-title':"Search"
	}).append($('<i></i>', {
		'class' : "icon-search"
	}).prop('outerHTML'));
	$('#GroupTeamsTable_filter').append(aa);
	$('#GroupTeamsTable_filter label').toggleClass('hideMe');
}

function searchInputFixForSiteTeamsUsersTable(){
	var aa = $('<div></div>', {
		'class': 'searchDiv',
		'data-toggle' : "tooltip",
		'data-placement': "top",
		'data-original-title':"Search"
	}).append($('<i></i>', {
		'class' : "icon-search"
	}).prop('outerHTML'));
	$('#GroupTeamsTableUsers_filter').append(aa);
	$('#GroupTeamsTableUsers_filter label').toggleClass('hideMe');
}

function constructToolbarForMembershipRequestsTable(){
	var dispSelectedUserReqs = $('<div></div>', {
		id : 'displaySelectedUserReqs'
	});
	dispSelectedUserReqs.append($('<span></span>', {
		id : 'numOfSelectedRowsUserReqs'
	}));
	dispSelectedUserReqs.append($('<span></span>', {
		id : 'justSelectedTextUserReqs',
		text : ' selected'
	}));
	
	$('#usersManagementPortletContainer .usersRequestsTableToolbarContainer')
		.addClass('hiddenToolbar')
		.prop('id', 'usersRequestsTableToolbarContainer')
		.append($('<div></div>', {
			id : 'acceptSeleced',
			text : 'Accept Selected'
		})).append($('<div></div>', {
			id : 'rejectSeleced',
			text : 'Reject Selected'
		})).append(dispSelectedUserReqs);

	var div = $('<div></div>', {
		id : 'borderFirstScreen'
	});
	div.insertBefore('#usersManagementPortletContainer .usersRequestsTableToolbarContainer');
}

function constructToolbarForSiteTeamsTable(){
	$('.groupTeamsTableToolbarContainer')
		.addClass('hiddenToolbar')
		.prop('id', 'groupTeamsTableToolbarContainer')
		.append($('<div></div>', {
			class : 'toolbarHr'
		})).append($('<div></div>', {
			id : 'deleteSiteTeam',
			text : 'Delete Group'
		})).append($('<div></div>', {
			id : 'editSiteTeam',
			text : 'Edit Group'
		}));
	
	var addSiteTeam = $('<div></div>', {
		id : 'addSiteTeam',
		text : 'New Group'
	});
	
	var newGroupPlusIcon = $('<i></i>',{
		class: "fa fa-plus-circle",
		'aria-hidden':"true",
		id : 'newGroupPlusIcon'
	});
	
	addSiteTeam.append(newGroupPlusIcon);
	
	addSiteTeam.insertAfter('.groupTeamsTableToolbarContainer');

	var div = $('<div></div>', {
		id : 'borderFirstScreen'
	});
	
	div.insertBefore('#groupTeamsTableToolbarContainer .groupTeamsTableToolbarContainer');
}