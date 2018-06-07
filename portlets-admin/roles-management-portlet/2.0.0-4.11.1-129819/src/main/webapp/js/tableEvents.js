$('#RolesManagementContainer #usersRequestsModal #closeUsersRolesModal').off().on(
		'click',
		function() {
			if ($('#closeUsersRolesModal').data('btnData') !== 0) {
				screenToTheLeft();
			}

			$('div#usersRequestsModal').modal('hide');

			$('#userEditedMailTemplate').closest('div').replaceWith('');
			if ($('#emailForRejection').length !== 1) {
				$('div#requestsAcceptanceBody .row:last').append(
						automaticRejectionEmailTemplate);
				$('#editEmailTemplate').tooltip();
			}
		});

function tableEvents() {
	$('#RolesManagementContainer table#CurrentRolesTable tbody')
			.on(
					'click',
					'tr:not(tr.control) td:first-of-type',
					function() {
//						var countSelected = $('#RolesManagementContainer table#CurrentRolesTable tr.selected').length;
//						var clickedRow = $(this).closest('tr').index();
//						var currentlySelectedRow = $('#RolesManagementContainer table#CurrentRolesTable tr.selected').index();
//						if(countSelected === 1 && clickedRow !== currentlySelectedRow)return;
//						theSelectedRow = currentlySelectedRow;
						$(this).find('i.icon-ok').toggleClass('whiteFont');
						usersTableDataForEditing = [];
						userTableUUIDsForEditing = [];
						$(this).closest('tr').toggleClass('selected');
						var countSelected = $('#RolesManagementContainer table#CurrentRolesTable tr.selected').length;
						var selectedTrs = $('#RolesManagementContainer table#CurrentRolesTable tr.selected');
						var theData = {};
						for (var i = 0; i < countSelected; i++) {
							theData = $($(this)
									.closest('table')
									.dataTable()
									.fnGetData(
											$('#RolesManagementContainer table#CurrentRolesTable tr.selected')[i]));
							theData.rowIndex = $(selectedTrs[i]).index();
							usersTableDataForEditing.push(theData);
						}

						var countSelectedRows = $('#RolesManagementContainer table#CurrentRolesTable tr.selected').length;
						var countTableCells = $('#RolesManagementContainer table#CurrentRolesTable tbody tr td').length;
						if (countSelectedRows > 0 && countTableCells > 1) {
							if(countSelectedRows > 1){
								$('#editSelected').addClass('hidden');
							} else {
								$('#editSelected').removeClass('hidden');
							}
							$('#RolesManagementContainer div#toolbar').removeClass('hiddenToolbar')
									.addClass('shownToolbar');
							$('#RolesManagementContainer span#numOfSelectedRows').text(countSelectedRows);
						} else {
							$('#RolesManagementContainer div#toolbar').addClass('hiddenToolbar')
									.removeClass('shownToolbar');
							$('#RolesManagementContainer span#numOfSelLectedRows').text('');
						}
					});

	$('#RolesManagementContainer div.toolbarContainer').off().on(
			'click',
			'div#toolbar.shownToolbar div#editSelected',
			function() {
				$('#RolesManagementContainer #EditRolesModal').modal('show');
				var rolesTable =  $('#RolesManagementContainer #CurrentRolesTable');
				var selectedRow = rolesTable.find('tr.selected:first')[0];
				var selectedRowData = rolesTable.dataTable().fnGetData(selectedRow);
				
				var roleNameDiv = $($.parseHTML(selectedRowData.Name));
				var roleNameText = roleNameDiv.text();
				
				var roleDescriptionNameDiv = $($.parseHTML(selectedRowData.Description));
				var roleDescriptionText = roleDescriptionNameDiv.text();
				
				$('#RolesManagementContainer #EditRoleName').val(roleNameText);
				$('#RolesManagementContainer #EditRoleDescription').val(roleDescriptionText);
				
			}).on('click', 'div#toolbar.shownToolbar div#deleteSelected',
			function() {
				$('#RolesManagementContainer #DeleteRoleModal').modal('show')
			}).on('click', 'div#toolbar div#addNew',
			function() {
				$('#RolesManagementContainer #AddNewRoleName').val('');
				$('#RolesManagementContainer #AddNewRoleDescription').val('');
				$('#RolesManagementContainer #AddRoleModal').modal('show')
			});
}