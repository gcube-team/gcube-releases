function siteTeamsTableEvents() {
	$('#usersManagementPortletContainerSiteTeamsEditMode table#GroupTeamsTable tbody')
			.on(
					'click',
					'tr:not(tr.control) td:first-of-type',
					function() {
						var countSelected = $('#usersManagementPortletContainerSiteTeamsEditMode table#GroupTeamsTable tr.selected').length;
						var clickedRow = $(this).closest('tr').index();
						var currentlySelectedRow = $('#usersManagementPortletContainerSiteTeamsEditMode table#GroupTeamsTable tr.selected').index();
//						if(countSelected === 1 && clickedRow !== currentlySelectedRow)return;
						var selectedRow = $(this).closest('tr')[0];
						var selectProperCheckbox = $(this).find('i.icon-ok')[0];
						theSelectedRow = currentlySelectedRow;
						$('#usersManagementPortletContainerSiteTeamsEditMode table#GroupTeamsTable tr.selected').not(selectedRow).removeClass('selected');
						$('#usersManagementPortletContainerSiteTeamsEditMode table#GroupTeamsTable i.icon-ok.whiteFont').not(selectProperCheckbox).removeClass('whiteFont');
						$(this).find('i.icon-ok').toggleClass('whiteFont');
						usersTableDataForEditing = [];
						userTableUUIDsForEditing = [];
						groupTeamsTableDataForEditing = [];
						$(this).closest('tr').toggleClass('selected');
						var theData = {};
							theData = $($(this)
									.closest('table')
									.dataTable()
									.fnGetData(
											$('#usersManagementPortletContainerSiteTeamsEditMode table#GroupTeamsTable tr.selected')[0]));
							groupTeamsTableDataForEditing.push(theData);

						var countSelectedRows = $('#usersManagementPortletContainerSiteTeamsEditMode table#GroupTeamsTable tr.selected').length;
						var countTableCells = $('table#GroupTeamsTable tbody tr td').length;
						if (countTableCells > 1 && countSelectedRows > 0 && !$('#groupTeamsTableToolbarContainer').hasClass('opened')) {
//							$('#usersManagementPortletContainerSiteTeamsEditMode #groupTeamsTableToolbarContainer')
//								.removeClass('hiddenToolbar').addClass('shownToolbar');
							$('#groupTeamsTableToolbarContainer').addClass('opened');
							$('#groupTeamsTableToolbarContainer').animate({height:'show'});
						} else if(countSelectedRows === 0) {
//							$('#usersManagementPortletContainerSiteTeamsEditMode #groupTeamsTableToolbarContainer').addClass('hiddenToolbar')
//									.removeClass('shownToolbar');
							$('#groupTeamsTableToolbarContainer').animate({height:'hide'});
							$('#groupTeamsTableToolbarContainer').removeClass('opened');
						}
					});
	
	$('#usersManagementPortletContainerSiteTeamsEditMode table#GroupTeamsTable tbody')
	.on(
		'click',
		'tr:not(tr.control) td:not(:first-of-type)', function(e){
			
			var table = $('table#GroupTeamsTable');
			var theTable = $('table#GroupTeamsTableUsers');
			theTable.DataTable().clear();
			theTable.DataTable().columns.adjust().draw();
			theTable.DataTable().columns.adjust().responsive.recalc();
			var selectedTr = $(e.target).closest('tr')[0];
			var data = table.dataTable().fnGetData(selectedTr);
			var usersHtml = data.siteTeamUsers;
			var usersJQuery = $($.parseHTML(usersHtml));
			var usersDetails = usersJQuery.find('p');
			if(usersDetails.length > 0){
				for(var i=0; i<usersDetails.length/2; i++){
					var fullName = $(usersDetails[2*i]).text();
					var screenName = $(usersDetails[2*i+1]).text();
					var dataTableObject = new siteTeamsUserObjectForDataTable(fullName, screenName);
					dataTableObject = surroundObjectPropWithDiv(dataTableObject);
					$('table#GroupTeamsTableUsers').dataTable().fnAddData(dataTableObject);
				}
			}
			$('#teamNameHeader').text($(data.Name).text());
			
			$('#displayGroupTeamUsersModal').modal('show');
		});

	$('#usersManagementPortletContainerSiteTeamsEditMode div.toolbarContainer').on(
			'click',
			'div#toolbar.shownToolbar div#editSelected',
			function() {
				$('#usersManagementPortletContainerSiteTeamsEditMode #EditRolesModal').modal('show');
				var rolesTable =  $('#usersManagementPortletContainerSiteTeamsEditMode #GroupTeamsTable');
				var selectedRow = rolesTable.find('tr.selected:first')[0];
				var selectedRowData = rolesTable.dataTable().fnGetData(selectedRow);
				
				var roleNameDiv = $($.parseHTML(selectedRowData.Name));
				var roleNameText = roleNameDiv.text();
				
				var roleDescriptionNameDiv = $($.parseHTML(selectedRowData.Description));
				var roleDescriptionText = roleDescriptionNameDiv.text();
				
				$('#usersManagementPortletContainerSiteTeamsEditMode #EditRoleName').val(roleNameText);
				$('#usersManagementPortletContainerSiteTeamsEditMode #EditRoleDescription').val(roleDescriptionText);
				
			}).on('click', 'div#toolbar.shownToolbar div#deleteSelected',
			function() {
				$('#usersManagementPortletContainerSiteTeamsEditMode #DeleteRoleModal').modal('show')
			}).on('click', 'div#toolbar div#addNew',
			function() {
				$('#usersManagementPortletContainerSiteTeamsEditMode #AddNewRoleName').val('');
				$('#usersManagementPortletContainerSiteTeamsEditMode #AddNewRoleDescription').val('');
				$('#usersManagementPortletContainerSiteTeamsEditMode #AddRoleModal').modal('show')
			});
}

function siteTeamsToolbarEvents(){
	$('div#usersManagementPortletContainerSiteTeamsEditMode').on('click', 
			'#groupTeamsTableToolbarContainer.shownToolbar #editSiteTeam', function(){
		
		$('div#editGroupTeamModal').modal('show');
		
		$('#EditGroupTeamName').val('');
		$('#EditGroupTeamDescription').val('');
		
		var teamName = groupTeamsTableDataForEditing[0][0].Name;
		var teamDescription = groupTeamsTableDataForEditing[0][0].Description;
		var name = $.parseHTML(teamName);
		var description = $.parseHTML(teamDescription);
		var nameText = $(name).text();
		var descriptionText = $(description).text();
		
		$('#EditGroupTeamName').val(nameText);
		$('#EditGroupTeamDescription').val(descriptionText);
	});

	$('#acceptEditGroupTeamOk').on('click', function(){
		var siteTeamName = $('#EditGroupTeamName').val();
		var siteTeamDescription = $('#EditGroupTeamDescription').val();
		var siteTeamID = $('table#GroupTeamsTable').dataTable().fnGetData($('table#GroupTeamsTable tbody tr.selected')[0]).TeamID;
		var teamID = $(siteTeamID).text();
		EditSiteTeamsForTheCurrentGroup(siteTeamName, siteTeamDescription, teamID);
	});
	
	$('div#usersManagementPortletContainerSiteTeamsEditMode').on('click', 
			'#groupTeamsTableToolbarContainer.shownToolbar #deleteSiteTeam', function(){
		$('#deleteGroupTeamModal').modal('show');
	});
	
	$('#usersManagementPortletContainerSiteTeamsEditMode').on('click',
			'#GroupTeamsTable_wrapper #addSiteTeam', function(){
		
		$('#addGroupTeamName').val('');
		$('#addGroupTeamDescription').val('');
		
		$('#newGroupTeamModal').modal('show');
	});
	
	$('#acceptCreateGroupTeamOk').on('click', function(){
		var teamName = $('#addGroupTeamName').val();
		var teamDescription = $('#addGroupTeamDescription').val();
		
		CreateSiteTeamsForTheCurrentGroup(teamName, teamDescription);
	});
	
	$('#acceptDeleteGroupTeamOk').on('click', function(){
		var siteTeamID = $('table#GroupTeamsTable').dataTable().fnGetData($('table#GroupTeamsTable tbody tr.selected')[0]).TeamID;
		var teamID = $(siteTeamID).text();
		DeleteSiteTeamsForTheCurrentGroup(teamID);
	});
}

function hideTeamManagementToolbar() {
	$('#groupTeamsTableToolbarContainer').removeClass('shownToolbar').addClass('hiddenToolbar');
}