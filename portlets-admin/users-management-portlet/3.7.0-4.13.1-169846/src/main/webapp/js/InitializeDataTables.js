function initializeCurrentUsersTable(){
	$('table#CurrentUsersTable').on( 'draw.dt', function (e, settings, data) {
		var info = $('#CurrentUsersTable').DataTable().page.info();
		CurrentUsersTablePages = info.pages;
    }).dataTable({
		data : usersTableData,
		columns : [
			{
				data : "CheckBox"
			},
			{
				data : "UserName",
				orderable : true
			},
			{
				data : "Email",
				orderable : true
			},
			{
				data : "FullName",
				orderable : true
			},
			{
				data : "Roles",
				orderable : true
			} ,
			{
				data : "Teams",
				orderable : true
			} ,
			{
				data : "RequestDate",
				orderable : true,
				orderData: 8,
				targets : 6
			},
			{
				data : "ValidationDate",
				orderable : true,
				orderData: 9,
				targets : 7
			},
			{
				data : "RequestDateObject",
				visible : false,
				searchable : false,
				orderable : true,
				type : "date"
			},
			{
				data : "ValidationDateObject",
				visible : false,
				searchable : false,
				orderable : true,
				type : "date"
			}
		],
		pagingType : "full_numbers",
		language : {
		    	"info": "Showing _START_ - _END_ of _TOTAL_ | ",
				"processing" : "Processing...",
				"paginate": {
			        "next": "",
			        "previous": "",
			        "first": "",
			        "last": ""
			    },
			    "search": "_INPUT_",
		        "searchPlaceholder": "Search..."
		},
        dom: '<"toolbarContainer">frtilp',
        responsive: {
            details: {
            	display: $.fn.dataTable.Responsive.display.childRowImmediate,
            	type: ''
            }
        },
        columnDefs: [{"orderable" : false, "targets" : 0},
                     {responsivePriority: 1, targets: 0},
                     {responsivePriority: 2, targets: 1},
                     {responsivePriority: 3, targets: 2},
                     {responsivePriority: 4, targets: 3},
                     {responsivePriority: 5, targets: 5},
                     {responsivePriority: 6, targets: 6},
                     {responsivePriority: 7, targets: 7}]
	});
	
	addTagsInputFunctionalityToSearchInput();
}

function initializeMembershipRequestsTable(){
	$('table#usersRequestsTable').on( 'draw.dt', function (e, settings, data) {
		var info = $('#usersRequestsTable').DataTable().page.info();
		UsersRequestsTablePages = info.pages;
    }).DataTable({
		data : reqsTableData,
		columns : [
					{
						data : "CheckBox",
						orderable : false,
					},
					{
						data : "UserName",
						orderable : true,
					},
					{
						data : "Email",
						orderable : true,
					},
					{
						data : "FullName",
						orderable : true,
					},
					{
						data : "Message",
						orderable : true,
					},
					{
						data: "RequestDate",
						orderable : true,
						orderData : 6,
						targets : 5
					},{
						data : "RequestDateObject",
						orderable : true,
						visible : false,
						searchable : false,
						type : "date"
					}
		],
		pagingType : "full_numbers",
		language : {
	    		"info": "Showing _START_ - _END_ of _TOTAL_ | ",
				"processing" : "Processing...",
				"paginate": {
			        "next": "",
			        "previous": "",
			        "first": "",
			        "last": ""
			    },
			    "search": "_INPUT_",
		        "searchPlaceholder": "Search..."
		},
        dom: '<"usersRequestsTableToolbarContainer">frtilp',
        responsive: {
            details: {
            	display: $.fn.dataTable.Responsive.display.childRowImmediate,
            	type: ''
            }
        },
        columnDefs: [{responsivePriority: 1, targets: 0},
                     {responsivePriority: 2, targets: 1},
                     {responsivePriority: 3, targets: 2},
                     {responsivePriority: 4, targets: 3}]
	});
}

function initializeRejectedMembershipRequestsTable(){
	$('table#rejectedUsersRequestsTable')
	.on('init.dt', function() {
		constructToolbarForRejectedUsersRequestsTable();
	})
	.on( 'draw.dt', function (e, settings, data) {
//		var info = $('#usersRequestsTable').DataTable().page.info();
//		UsersRequestsTablePages = info.pages;
    }).DataTable({
		data : reqsTableData,
		columns : [
					{
						data : "CheckBox",
						orderable : false,
					},
					{
						data : "UserName",
						orderable : true,
					},
					{
						data : "Email",
						orderable : true,
					},
					{
						data : "FullName",
						orderable : true,
					},
					{
						data : "Message",
						orderable : true,
					},
					{
						data: "RequestDate",
						orderable : true,
						orderData : 7,
						targets : 5
					},
					{
						data: "RejectionDate",
						orderable : true,
						orderData : 8,
						targets : 6
					},
					{
						data: "RequestDateObject",
						orderable : true,
						searchable : false,
						visible : false
					},
					{
						data: "RejectionDateObject",
						orderable : true,
						searchable : false,
						visible : false
					}
		],
		pagingType : "full_numbers",
		language : {
	    		"info": "Showing _START_ - _END_ of _TOTAL_ | ",
				"processing" : "Processing...",
				"paginate": {
			        "next": "",
			        "previous": "",
			        "first": "",
			        "last": ""
			    },
			    "search": "_INPUT_",
		        "searchPlaceholder": "Search..."
		},
        dom: '<"rejectedMembershipRequestsTableToolbarContainer">frtilp',
        responsive: {
            details: {
            	display: $.fn.dataTable.Responsive.display.childRowImmediate,
            	type: ''
            }
        },
        columnDefs: [{responsivePriority: 1, targets: 0},
                     {responsivePriority: 2, targets: 1},
                     {responsivePriority: 3, targets: 2},
                     {responsivePriority: 4, targets: 3},
                     {responsivePriority: 5, targets: 4}]
	});
}

function initializeGroupTeamsTable(){
	$('table#GroupTeamsTable').on( 'draw.dt', function (e, settings, data) {
		var info = $('#GroupTeamsTable').DataTable().page.info();
		GroupTeamsTablePages = info.pages;
    }).DataTable({
		data : groupTeamsTableData,
		columns : [
					{
						data : "CheckBox",
						orderable : false,
					},
					{
						data : "Name",
						orderable : true,
					},
					{
						data : "Description",
						orderable : true,
					},
					{
						data : "NumberOfUsers",
						orderable : true,
					},
					{
						data : "CreationDate",
						orderable : true,
						orderData: 7,
						targets : 4
					},
					{
						data: "LastModificationDate",
						orderable : true,
						orderData: 8,
						targets : 5
					},
					{
						data: "CreatorName",
						orderable : true
					},
					{
						data : "CreationDateObject",
						visible : false,
						searchable : false,
						orderable : true,
						type : "date"
					},
					{
						data : "LastModificationDateObject",
						visible : false,
						searchable : false,
						orderable : true,
						type : "date"
					}
		],
		language : {
    			"info": "Showing _START_ - _END_ of _TOTAL_ | ",
				"processing" : "Processing...",
				"paginate": {
			        "next": "",
			        "previous": "",
			        "first": "",
			        "last": ""
			    },
			    "search": "_INPUT_",
		        "searchPlaceholder": "Search..."
		},
        dom: '<"groupTeamsTableToolbarContainer">frtilp',
		pagingType : "full_numbers",
        responsive: {
            details: {
            	display: $.fn.dataTable.Responsive.display.childRowImmediate,
            	type: ''
            }
        },
        columnDefs: [{responsivePriority: 1, targets: 0},
                     {responsivePriority: 2, targets: 1},
                     {responsivePriority: 3, targets: 2},
                     {responsivePriority: 4, targets: 3}]
	});
}

function initializeSiteTeamUsersTable(){
	$('table#GroupTeamsTableUsers').on( 'draw.dt', function (e, settings, data) {
		var info = $('#GroupTeamsTableUsers').DataTable().page.info();
		GroupTeamsTableUsersTablePages = info.pages;
		if(GroupTeamsTableUsersTablePages <= 1){
			$('#GroupTeamsTableUsers_paginate').addClass('hidden');
		}else{
			$('#GroupTeamsTableUsers_paginate').removeClass('hidden');
		}
    }).dataTable({
		data : [],
		columns : [
			{
				data : "FullName",
				orderable : true
			},
			{
				data : "UserName",
				orderable : true
			}
		],
		pagingType : "full_numbers",
        dom: 'frtilp',
		language : {
	    		"info": "Showing _START_ - _END_ of _TOTAL_ | ",
				"processing" : "Processing...",
				"paginate": {
			        "next": "",
			        "previous": "",
			        "first": "",
			        "last": ""
			    },
			    "search": "_INPUT_",
		        "searchPlaceholder": "Search..."
		}
	});
}

function setTooltips(){
//	$('#userRequestsNotifications').tooltip();
	$('#currentUsersTableRefresh').tooltip();
	$('#reloadUsersRequestsTable').tooltip();
	$('#editEmailTemplate').tooltip();
//	$('#usersManagementPortletContainer .searchDiv').tooltip();
//	$('#usersManagementPortletContainerSiteTeamsEditMode .searchDiv').tooltip();
}

function eraseTextOfSearchInputLabels(){
	var array = $('#CurrentUsersTable_filter label, #usersRequestsTable_filter label, #GroupTeamsTable_filter label, #GroupTeamsTableUsers_filter label');
	
	$.each(array, function(){
		$($(this).contents()[0]).remove();
	});//remove Search: text from label
	
	array.find('input').attr('placeholder', 'Search:');
}