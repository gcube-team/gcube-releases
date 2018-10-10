function screenToTheLeft() {
	var width = $('div#usersRequestsModal div.modal-body').width();
	width = '' + width + 'px';
	$('div#requestsAcceptanceContainer').animate({
		left : width
	}, '2500', function() {
		$('div#requestsAcceptanceContainer').toggleClass('hideSection');
		$('div#usersRequestsTableContainer').toggleClass('hideSection').css({
			right : '0px'
		});
		$('table#usersRequestsTable').DataTable().columns.adjust().draw();
		$('table#usersRequestsTable').DataTable().columns.adjust().responsive.recalc();
		$('#usersRequestsTable th:first').removeClass('sorting_asc');
	});
	$('#userRequestsHeader').contents().first()[0].textContent = "Users' requests";
	if (!$('button#sendAcceptance').hasClass('hideButton'))
		$('button#sendAcceptance').toggleClass('hideButton');
	if (!$('button#sendRejection').hasClass('hideButton'))
		$('button#sendRejection').toggleClass('hideButton');
	$('button#acceptAll').toggleClass('hideButton');
	$('button#rejectAll').toggleClass('hideButton');
}

$('#usersRequestsModal #closeUsersRolesModal').off().on(
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

//TODO
$('#singleTag').off('click').on('click', function(){
	deletePreviousRoles = $('#singleTag').prop('checked');
	
	if(deletePreviousRoles){
		$('#changeUsersRolesModal .row:not(:first) .text-tag').remove();
	}
});

$('#singleTagInAssignUsersToGroupsModal').off('click').on('click', function(){
	deletePreviousRoles = $('#singleTagInAssignUsersToGroupsModal').prop('checked');
	
	if(deletePreviousRoles){
		$('#assignUsersToGroupsModal .row:not(:first) .text-tag').remove();
	}
});

$('#singleTagInAssignRolesModal').off('click').on('click', function(){
	deletePreviousRoles = $('#singleTagInAssignRolesModal').prop('checked');
	
	if(deletePreviousRoles){
		$('#assignUsersRolesModal .row:not(:first) .text-tag').remove();
	}
});

function tableEvents() {
	//Press all column-title, select all, deselect all
	$('table:not(#GroupTeamsTableUsers):not(#GroupTeamsTable):not(#rejectedUsersRequestsTable) thead th:first-of-type').off('click').on('click', function(){
		var $table = $(this).closest('table');
		var $rows = $table.find('tbody tr');
		var rowsCount = $rows.length;
		var rowsCellsCount = $rows.find('td').length;
		//If 0 rows do nothing
		if(rowsCellsCount > 1){
			$(this).toggleClass('none');
			
			if($(this).hasClass('none')){
				
//				$(this).find('div').text('none');
//				$(this).find('div').css('padding-left','0px');

				$rows.addClass('selected');
				$rows.find('.icon-ok').addClass('whiteFont');
				
				usersTableDataForEditing = [];
				userTableUUIDsForEditing = [];
				var countSelected = $('table#CurrentUsersTable tr.selected').length;
				var selectedTrs = $('table#CurrentUsersTable tr.selected');
				var theData = {};
				for (var i = 0; i < countSelected; i++) {
					theData = $($table
							.closest('table')
							.dataTable()
							.fnGetData(
									$('table#CurrentUsersTable tr.selected')[i]));
					theData.rowIndex = $(selectedTrs[i]).index();
					usersTableDataForEditing.push(theData);
				}
				
				//If currentusres table show toolbar
				if($table.attr('id')==='CurrentUsersTable'){
//					$('div#toolbar').removeClass('hiddenToolbar').addClass('shownToolbar');
					$('span#numOfSelectedRusersRequestsDataForEditingows').text(rowsCount);
					$('span#numOfSelectedRows').text(rowsCount);
					$('div#toolbar').animate({height:'show'});
				}else if($table.attr('id')==='usersRequestsTable'){
//					$('div#usersRequestsTableToolbarContainer').removeClass('hiddenToolbar').addClass('shownToolbar');
					$('span#numOfSelectedRowsUserReqs').text(rowsCount);
					$('div#usersRequestsTableToolbarContainer').animate({height:'show'});
					
					usersRequestsDataForEditing = [];
					$.each($('#usersRequestsTable tr.selected td:nth-child(3)'), function(){
						
						var theData = $($table
								.dataTable()
								.fnGetData(
										$table.find('tr.selected')[i]));
						usersRequestsDataForEditing.push(theData);
					});
					
				}else if($table.attr('id')==='GroupTeamsTable'){
					$('#usersManagementPortletContainerSiteTeamsEditMode #groupTeamsTableToolbarContainer').removeClass('hiddenToolbar').addClass('shownToolbar');
				}
			}else{
//				$(this).find('div').text('all')
//				$(this).find('div').css('padding-left','8px');

				$rows.removeClass('selected');
				$rows.find('.icon-ok').removeClass('whiteFont');

				//If currentusres table hide toolbar
				if($table.attr('id')==='CurrentUsersTable'){
//					$('div#toolbar').addClass('hiddenToolbar').removeClass('shownToolbar');
					$('span#numOfSelLectedRows').text('');
					$('div#toolbar').animate({height:'hide'});
				}else if($table.attr('id')==='usersRequestsTable'){
//					$('div#usersRequestsTableToolbarContainer').addClass('hiddenToolbar').removeClass('shownToolbar');
					$('span#numOfSelectedRowsUserReqs').text('');
					$('div#usersRequestsTableToolbarContainer').animate({height:'hide'});
				}else if($table.attr('id')==='GroupTeamsTable'){
					$('#usersManagementPortletContainerSiteTeamsEditMode #groupTeamsTableToolbarContainer').addClass('hiddenToolbar').removeClass('shownToolbar');
				}
			}
		}
	});
	
	$('#userNamesList').textext({
		plugins : ' tags'
	});
	$('#userNamesListInAssignRolesModal').textext({
		plugins : ' tags'
	});
	$('#userNamesListInAssignUsersToGroupsModal').textext({
		plugins : ' tags'
	});
	
	$('span#textAboveTagsInput div.row div.text-core:first').addClass('span9');
	$('span#textAboveTagsInputInAssignRolesModal div.row div.text-core').addClass('span9');
	$('span#textAboveTagsInputInAssignUsersToGroupsModal div.row div.text-core').addClass('span9');
	
	$('#userNamesListInAssignUsersToGroupsModal').parent().find('div.text-tags').unbind().bind(
			'DOMNodeInserted',
			function(event) {
				var element = event.target;
				var tagName = $(element).prop("tagName");
				if (tagName !== 'DIV')
					return;
				// $('#userNamesList').parent().find('div.text-tag').addClass('span4');
				$('#userNamesListInAssignUsersToGroupsModal').parent().find('div.text-button').addClass(
						'span12');
				$('#userNamesListInAssignUsersToGroupsModal').parent().find('a.text-remove').html('<i class="fa fa-times"></i>')
						.removeClass('text-remove').addClass('tag-remove');
			});
	
	$('#userNamesListInAssignRolesModal').parent().find('div.text-tags').unbind().bind(
			'DOMNodeInserted',
			function(event) {
				var element = event.target;
				var tagName = $(element).prop("tagName");
				if (tagName !== 'DIV')
					return;
				// $('#userNamesList').parent().find('div.text-tag').addClass('span4');
				$('#userNamesListInAssignRolesModal').parent().find('div.text-button').addClass(
						'span12');
				$('#userNamesListInAssignRolesModal').parent().find('a.text-remove').html('<i class="fa fa-times"></i>')
						.removeClass('text-remove').addClass('tag-remove');
			});
	$('#userNamesList').parent().find('div.text-tags').unbind().bind(
			'DOMNodeInserted',
			function(event) {
				var element = event.target;
				var tagName = $(element).prop("tagName");
				if (tagName !== 'DIV')
					return;
				// $('#userNamesList').parent().find('div.text-tag').addClass('span4');
				$('#userNamesList').parent().find('div.text-button').addClass(
						'span12');
				$('#userNamesList').parent().find('a.text-remove').html('<i class="fa fa-times"></i>')
						.removeClass('text-remove').addClass('tag-remove');
			});
	
	$('#teamsList').parent().find('div.text-tags').off().bind(
			'DOMNodeInserted',
			function(event) {
				var element = event.target;
				var tagName = $(element).prop("tagName");
				if (tagName !== 'DIV')
					return;
				// $('#roleList').parent().find('div.text-tag').addClass('span5');
				$('#teamsList').parent().find('div.text-button').addClass(
						'span12');
				$('#teamsList').parent().find('a.text-remove').html('<i class="fa fa-times"></i>')
						.removeClass('text-remove').addClass('tag-remove');
				$('textarea#teamsList').parent().find('a.tag-remove').off().on(
						'click', function() {
							$(this).closest('.text-tag').remove();
						});
				var matched = false;
				var tagsTextt = $('#teamsList').parent().find(
						'div.text-tag.span5');
				for (var i = 0; i < tagsTextt.length; i++) {
					for (var j = i + 1; j < tagsTextt.length; j++) {
						if ($(tagsTextt[i]).text() === $(tagsTextt[j]).text()) {
							tagsTextt[j].remove();
						}
					}
				}
			});
	
	$('#teamsListInAssignUsersToGroupsModal').parent().find('div.text-tags').off().bind(
			'DOMNodeInserted',
			function(event) {
				var element = event.target;
				var tagName = $(element).prop("tagName");
				if (tagName !== 'DIV')
					return;
				// $('#roleList').parent().find('div.text-tag').addClass('span5');
				$('#teamsListInAssignUsersToGroupsModal').parent().find('div.text-button').addClass(
						'span12');
				$('#teamsListInAssignUsersToGroupsModal').parent().find('a.text-remove').html('<i class="fa fa-times"></i>')
						.removeClass('text-remove').addClass('tag-remove');
				$('textarea#teamsListInAssignUsersToGroupsModal').parent().find('a.tag-remove').off().on(
						'click', function() {
							$(this).closest('.text-tag').remove();
						});
				var matched = false;
				var tagsTextt = $('#teamsListInAssignUsersToGroupsModal').parent().find(
						'div.text-tag.span5');
				for (var i = 0; i < tagsTextt.length; i++) {
					for (var j = i + 1; j < tagsTextt.length; j++) {
						if ($(tagsTextt[i]).text() === $(tagsTextt[j]).text()) {
							tagsTextt[j].remove();
						}
					}
				}
			});
	
	$('#roleList').parent().find('div.text-tags').off().bind(
			'DOMNodeInserted',
			function(event) {
				var element = event.target;
				var tagName = $(element).prop("tagName");
				if (tagName !== 'DIV')
					return;
				// $('#roleList').parent().find('div.text-tag').addClass('span5');
				$('#roleList').parent().find('div.text-button').addClass(
						'span12');
				$('#roleList').parent().find('a.text-remove').html('<i class="fa fa-times"></i>')
						.removeClass('text-remove').addClass('tag-remove');
				$('textarea#roleList').parent().find('a.tag-remove').off().on(
						'click', function() {
							$(this).closest('.text-tag').remove();
						});
				var matched = false;
				var tagsTextt = $('#roleList').parent().find(
						'div.text-tag.span5');
				for (var i = 0; i < tagsTextt.length; i++) {
					for (var j = i + 1; j < tagsTextt.length; j++) {
						if ($(tagsTextt[i]).text() === $(tagsTextt[j]).text()) {
							tagsTextt[j].remove();
						}
					}
				}
			});
	
	$('#roleListInAssignRolesModal').parent().find('div.text-tags').off().bind(
		'DOMNodeInserted',
		function(event) {
			var element = event.target;
			var tagName = $(element).prop("tagName");
			if (tagName !== 'DIV')
				return;
			// $('#roleList').parent().find('div.text-tag').addClass('span5');
			$('#roleListInAssignRolesModal').parent().find('div.text-button').addClass(
					'span12');
			$('#roleListInAssignRolesModal').parent().find('a.text-remove').html('<i class="fa fa-times"></i>')
					.removeClass('text-remove').addClass('tag-remove');
			$('textarea#roleListInAssignRolesModal').parent().find('a.tag-remove').off().on(
					'click', function() {
						$(this).closest('.text-tag').remove();
					});
			var matched = false;
			var tagsTextt = $('#roleListInAssignRolesModal').parent().find(
					'div.text-tag.span5');
			for (var i = 0; i < tagsTextt.length; i++) {
				for (var j = i + 1; j < tagsTextt.length; j++) {
					if ($(tagsTextt[i]).text() === $(tagsTextt[j]).text()) {
						tagsTextt[j].remove();
					}
				}
			}
		});

	displaySiteRolesOnHover();

	$('table#CurrentUsersTable tbody').on(
		'click',
		'tr:not(tr.control) td:first-of-type',
		function() {
			$(this).find('i.icon-ok').toggleClass('whiteFont');
			usersTableDataForEditing = [];
			userTableUUIDsForEditing = [];
			$(this).closest('tr').toggleClass('selected');
			var countSelected = $('table#CurrentUsersTable tr.selected').length;
			var selectedTrs = $('table#CurrentUsersTable tr.selected');
			var theData = {};
			for (var i = 0; i < countSelected; i++) {
				theData = $($(this)
						.closest('table')
						.dataTable()
						.fnGetData(
								$('table#CurrentUsersTable tr.selected')[i]));
				theData.rowIndex = $(selectedTrs[i]).index();
				usersTableDataForEditing.push(theData);
			}

			var countSelectedRows = $('table#CurrentUsersTable tr.selected').length;
			var countTableCells = $('table#CurrentUsersTable tbody tr td').length;
			if (countTableCells > 1 && countSelectedRows > 0/* && !$('div#toolbar').hasClass('openToolbar')*/) {
//				$('div#toolbar').removeClass('hiddenToolbar')
//						.addClass('shownToolbar');//initially it was just hiding the toolbar, not displaying none
				$('div#toolbar').animate({height:'show'});
				$('div#toolbar').addClass('openToolbar');
				$('span#numOfSelectedRows').text(countSelectedRows);
			} else if(countSelectedRows === 0){
//				$('div#toolbar').addClass('hiddenToolbar')
//						.removeClass('shownToolbar');//initially it was just hiding the toolbar, not displaying none
				$('div#toolbar').removeClass('openToolbar');
				$('span#numOfSelLectedRows').text('');
				$('div#toolbar').animate({height:'hide'});
			}
			//now it toggles it toggles it up and down
			
			var countTableRows = $('table#CurrentUsersTable tbody tr').length;
			if(countTableRows === countSelectedRows){
				$('#CurrentUsersTable th:first').addClass('none');
			}else {
				$('#CurrentUsersTable th:first').removeClass('none');
			}
		}
	);

	$('table#usersRequestsTable tbody')
			.off()
			.on(
				'click',
				'tr td:first-of-type',
				function() {
					$(this).find('i.icon-ok').toggleClass('whiteFont');
					usersRequestsDataForEditing = [];
					usersRequestsMembershipRequestsIdsForEditing = [];
					$(this).closest('tr').toggleClass('selected');
					$('#openEditModal').addClass('hidden');

					var countSelected = $('table#usersRequestsTable tr.selected').length;
					var theData = {};
					for (var i = 0; i < countSelected; i++) {
						theData = $($(this)
								.closest('table')
								.dataTable()
								.fnGetData(
										$('table#usersRequestsTable tr.selected')[i]));
						usersRequestsDataForEditing.push(theData);
					}

					var countSelectedRows = $('table#usersRequestsTable tr.selected').length;
					var countTableCells = $('table#usersRequestsTable tbody tr td').length;
					if (countTableCells > 1 && countSelectedRows > 0/* && !$('div#usersRequestsTableToolbarContainer').hasClass('openToolbar')*/) {
//						$('div#usersRequestsTableToolbarContainer')
//								.removeClass('hiddenToolbar').addClass(
//										'shownToolbar');
						$('div#usersRequestsTableToolbarContainer').animate({height:'show'});
						$('div#usersRequestsTableToolbarContainer').addClass('openToolbar');
						$('span#numOfSelectedRowsUserReqs').text(
								countSelectedRows);
					} else if(countSelectedRows === 0){
//						$('div#usersRequestsTableToolbarContainer')
//								.addClass('hiddenToolbar').removeClass(
//										'shownToolbar');
						$('div#usersRequestsTableToolbarContainer').animate({height:'hide'});
						$('div#usersRequestsTableToolbarContainer').removeClass('openToolbar');
						$('span#numOfSelectedRowsUserReqs').text('');
					}
					
					var countTableRows = $('table#usersRequestsTable tbody tr').length;
					if(countTableRows === countSelectedRows){
						$('#usersRequestsTable th:first').addClass('none');
					}else {
						$('#usersRequestsTable th:first').removeClass('none');
					}
				});

	$('div#deselectAll').off().on('click', function() {
		$('#usersManagementPortletContainer .selected').removeClass('selected');
		$('#usersManagementPortletContainer .whiteFont').removeClass('whiteFont');
		$('#CurrentUsersTable th:first').removeClass('none');
//		$('div#toolbar').addClass('hiddenToolbar').removeClass('shownToolbar');
		$('div#toolbar').removeClass('openToolbar');
		$('div#toolbar').animate({height:'hide'});
	});

	$('#usersManagementPortletContainer div.toolbarContainer').off().on(
			'click',
			'div#toolbar.shownToolbar div#editSelected',
			function() {
				$('#changeUsersRolesModal').modal('show');
				$('#roleList').parent().find('.text-tag').remove();
				$('#teamsList').parent().find('.text-tag').remove();
				// if(!$('span#textAboveTagsInput div.row
				// div.text-core:first').hasClass('span9')){
				//			
				// }
				currentUsersTableRows = [];
				var usersEmails = [];
				for (var i = 0; i < usersTableDataForEditing.length; i++) {
					var email = usersTableDataForEditing[i][0].Email;
					currentUsersTableRows
							.push(usersTableDataForEditing[i].rowIndex);
					usersEmails.push(email.substring(5, email.length - 6));
					var userUuid = usersTableDataForEditing[i][0].UserId;
					userTableUUIDsForEditing.push(userUuid);
				}
				var tags = $('textarea#userNamesList').parent().find(
						'div.text-tags div.text-tag');
				if (tags.length > 0)
					tags.remove();// Remove previous tags
				$('textarea#userNamesList').textext()[0].tags().addTags(
						usersEmails);
				for (var j = 0; j < userTableUUIDsForEditing.length; j++) {
					var value = userTableUUIDsForEditing[j].toString();
					$($('#userNamesList').parent().find('.text-tag')[j]).data(
							'userUUID', value.substring(5, value.length - 6));
				}
				showCheckBoxForSingleTag();
				$('textarea#userNamesList').parent().find('a.tag-remove').off()
						.on('click', function() {
							$(this).closest('.text-tag').remove();
							showCheckBoxForSingleTag();
						});
			}).on('click', 'div#toolbar.shownToolbar div#editTeams',
			function() {
				
				$('#changeUsersTeamsModal').modal('show');
				
			}).on('click', 'div#toolbar.shownToolbar div#deleteSelected',
			function() {
				
				$('#deleteUsersFromCurrentSiteModal').modal('show');
				
				
			}).on('click', 'div#toolbar.shownToolbar div#assignRolesToUser',
			function() {
				
				$('#assignUsersRolesModal').modal('show');
				//remove previous tags
				$('#roleListInAssignRolesModal').parent().find('.text-tag').remove();
				currentUsersTableRows = [];
				var usersEmails = [];
				for (var i = 0; i < usersTableDataForEditing.length; i++) {
					var email = usersTableDataForEditing[i][0].Email;
					currentUsersTableRows
							.push(usersTableDataForEditing[i].rowIndex);
					usersEmails.push(email.substring(5, email.length - 6));
					var userUuid = usersTableDataForEditing[i][0].UserId;
					userTableUUIDsForEditing.push(userUuid);
				}
				var tags = $('textarea#userNamesListInAssignRolesModal').parent().find(
				'div.text-tags div.text-tag');
				if (tags.length > 0)
					tags.remove();// Remove previous tags
				$('textarea#userNamesListInAssignRolesModal').textext()[0].tags().addTags(
						usersEmails);
				
				for (var j = 0; j < userTableUUIDsForEditing.length; j++) {
					var value = userTableUUIDsForEditing[j].toString();
					$($('#userNamesListInAssignRolesModal').parent().find('.text-tag')[j]).data(
							'userUUID', value.substring(5, value.length - 6));
				}
				showCheckBoxForSingleTagInAssignRolesModal();
				$('textarea#userNamesListInAssignRolesModal').parent().find('a.tag-remove').off()
						.on('click', function() {
							$(this).closest('.text-tag').remove();
							showCheckBoxForSingleTagInAssignRolesModal();
						});
				
			}).on('click', 'div#toolbar.shownToolbar div#assignUsersToGroup',
			function() {
				
				$('#assignUsersToGroupsModal').modal('show');
				//remove previous tags
				$('#teamsListInAssignUsersToGroupsModal').parent().find('.text-tag').remove();
				currentUsersTableRows = [];
				var usersEmails = [];
				for (var i = 0; i < usersTableDataForEditing.length; i++) {
					var email = usersTableDataForEditing[i][0].Email;
					currentUsersTableRows
							.push(usersTableDataForEditing[i].rowIndex);
					usersEmails.push(email.substring(5, email.length - 6));
					var userUuid = usersTableDataForEditing[i][0].UserId;
					userTableUUIDsForEditing.push(userUuid);
				}
				var tags = $('textarea#userNamesListInAssignUsersToGroupsModal').parent().find(
				'div.text-tags div.text-tag');
				if (tags.length > 0 && !filterUserTableByUsersThatDontBelongInAGroup){
					tags.remove();// Remove previous tags					
				}else if(filterUserTableByUsersThatDontBelongInAGroup){
					tags.remove();
//					If the user presses add to group from the toolbar the groupname by which 
//					he searched must be available in the modal
					var tags = [];
					tags.push($('#teamNameHeader').text());
					$('#teamsListInAssignUsersToGroupsModal').textext()[0].tags().addTags(tags);
				}

				$('textarea#userNamesListInAssignUsersToGroupsModal').textext()[0].tags().addTags(
						usersEmails);
				
				for (var j = 0; j < userTableUUIDsForEditing.length; j++) {
					var value = userTableUUIDsForEditing[j].toString();
					$($('#userNamesListInAssignUsersToGroupsModal').parent().find('.text-tag')[j]).data(
							'userUUID', value.substring(5, value.length - 6));
				}
				showCheckBoxForSingleTagInAssignUsersToGroupsModal();
				$('textarea#userNamesListInAssignUsersToGroupsModal').parent().find('a.tag-remove').off()
						.on('click', function() {
							$(this).closest('.text-tag').remove();
							showCheckBoxForSingleTagInAssignUsersToGroupsModal();
						});
				
			});

	$('button#acceptDeleteUsersFromCurrentSiteModal')
			.off()
			.on(
					'click',
					function() {

						var groupId = theGroupId;
						var doRefresh = true;
						var selectedRows = $('table#CurrentUsersTable tbody tr.selected');
						var deletePreviousRoles = false;
						var deleteUsers = true;
						var userIDs = [];
						var roles = [];
						var reqIDs = [];
						for (var i = 0; i < selectedRows.length; i++) {
							var isSelf = $('table#CurrentUsersTable').dataTable().fnGetData(selectedRows[i]).isSelf;
							var isSelfText = $($.parseHTML(isSelf)).text();
							if(isSelfText === "true") {
								$('#deleteUsersFromCurrentSiteModal').modal('hide');
								$('#cannotRemoveSelfModal').modal('show');
								return;
							}
							var value = $('table#CurrentUsersTable').dataTable().fnGetData(selectedRows[i]).UserId;
							if (isNaN(value))
								value = value.substring(5, value.length - 6);
							userIDs.push(value);
							var reqID = $('table#CurrentUsersTable')
									.dataTable().fnGetData(selectedRows[i]).reqID;
							reqIDs.push(reqID.substring(5, reqID.length - 6));
						}
						var sendDismissalEmail = true;//$('#sendAutomaticRejectionEmail').prop('checked');
						fetchAllCurrentUsers(deleteMode, deleteUsers, userIDs,
								roles, [], deletePreviousRoles, reqIDs,
								sendDismissalEmail);
//						$('div#toolbar').addClass('hiddenToolbar').removeClass(
//								'shownToolbar');
						$('#deleteUsersFromCurrentSiteModal').modal('hide');
//						$('#sendAutomaticRejectionEmail').prop('checked', false);
						$('div#toolbar').removeClass('openToolbar');
						$('div#toolbar').animate({height:'hide'});
						$('#CurrentUsersTable th:first').removeClass('none');
					});

	function showCheckBoxForSingleTag() {
		var currentTags = $('textarea#userNamesList').parent().find(
				'div.text-tags div.text-tag');
		// appendCheckboxHere.append($('<br>')).append(labelForSingleTag).append(checkBoxForSingleTag);
		if (currentTags.length == 1) {
			$('div#singleTagSection').removeClass('hiddenSection');
			$('div#multipleTagsSection').addClass('hiddenSection');
			displayRolesForSingleUser();
		} else {
			$('div#singleTagSection').addClass('hiddenSection');
			$('div#multipleTagsSection').removeClass('hiddenSection');
		}
		if (!$('#singleTagSection').hasClass('hiddenSection')) {
			deletePreviousRoles = $('#singleTag').prop('checked');
			$('#singleTag').prop('checked', false);
		} else {
			deletePreviousRoles = false;
		}
	}

	function displayRolesForSingleUser() {

		var singleRow = $('table#CurrentUsersTable tbody tr.selected')[0];
		var table = $('table#CurrentUsersTable');
		var roles = table.dataTable().fnGetData(singleRow).Roles;
		var teams = table.dataTable().fnGetData(singleRow).Teams;
		var rolesText = $(roles).text();//roles is an HTML element, not a jquery one
		var teamsText = $(teams).text();
		if(rolesText !== "-"){//"-" means no role
			var rolesArray = rolesText.split(",");
			$('textarea#roleList').textext()[0].tags().addTags(rolesArray);
		}
		if(teamsText !== "-"){//"-" means no team
			var teamsArray = teamsText.split(",");
			$('textarea#teamsList').textext()[0].tags().addTags(teamsArray);
		}
	}

	function showCheckBoxForSingleTagInAssignRolesModal() {
		var currentTags = $('textarea#userNamesListInAssignRolesModal').parent().find(
				'div.text-tags div.text-tag');
		// appendCheckboxHere.append($('<br>')).append(labelForSingleTag).append(checkBoxForSingleTag);
		if (currentTags.length == 1) {
			$('div#singleTagSectionInAssignRolesModal').removeClass('hiddenSection');
			$('div#multipleTagsSectionInAssignRolesModal').addClass('hiddenSection');
			displayRolesForSingleUserInAssignRolesModal();
		} else {
			$('div#singleTagSectionInAssignRolesModal').addClass('hiddenSection');
			$('div#multipleTagsSectionInAssignRolesModal').removeClass('hiddenSection');
		}
		if (!$('#singleTagSectionInAssignRolesModal').hasClass('hiddenSection')) {
			deletePreviousRoles = $('#singleTagInAssignRolesModal').prop('checked');
			$('#singleTagInAssignRolesModal').prop('checked', false);
		} else {
			deletePreviousRoles = false;
		}
	}

	function displayRolesForSingleUserInAssignRolesModal() {

		var singleRow = $('table#CurrentUsersTable tbody tr.selected')[0];
		var table = $('table#CurrentUsersTable');
		var roles = table.dataTable().fnGetData(singleRow).Roles;
		var teams = table.dataTable().fnGetData(singleRow).Teams;
		var teamsText = $(teams).text();
		var rolesText = $(roles).text();//roles is an HTML element, not a jquery one
		if(rolesText !== "-"){//"-" means no role
			var rolesArray = rolesText.split(",");
			$('textarea#roleListInAssignRolesModal').textext()[0].tags().addTags(rolesArray);
		}
	}

	function showCheckBoxForSingleTagInAssignUsersToGroupsModal() {
		var currentTags = $('textarea#userNamesListInAssignUsersToGroupsModal').parent().find(
				'div.text-tags div.text-tag');
		// appendCheckboxHere.append($('<br>')).append(labelForSingleTag).append(checkBoxForSingleTag);
		if (currentTags.length == 1) {
			$('div#singleTagSectionInAssignUsersToGroupsModal').removeClass('hiddenSection');
			$('div#multipleTagsSectionInAssignUsersToGroupsModal').addClass('hiddenSection');
			displayRolesForSingleUserInAssignUsersToGroupsModal();
		} else {
			$('div#singleTagSectionInAssignUsersToGroupsModal').addClass('hiddenSection');
			$('div#multipleTagsSectionInAssignUsersToGroupsModal').removeClass('hiddenSection');
		}
		if (!$('#singleTagSectionInAssignUsersToGroupsModal').hasClass('hiddenSection')) {
			deletePreviousRoles = $('#singleTagInAssignUsersToGroupsModal').prop('checked');
			$('#singleTagInAssignUsersToGroupsModal').prop('checked', false);
		} else {
			deletePreviousRoles = false;
		}
	}

	function displayRolesForSingleUserInAssignUsersToGroupsModal() {

		var singleRow = $('table#CurrentUsersTable tbody tr.selected')[0];
		var table = $('table#CurrentUsersTable');
		var roles = table.dataTable().fnGetData(singleRow).Roles;
		var teams = table.dataTable().fnGetData(singleRow).Teams;
		var rolesText = $(roles).text();//roles is an HTML element, not a jquery one
		var teamsText = $(teams).text();
		if(teamsText !== "-"){//"-" means no team
			var teamsArray = teamsText.split(",");
			$('textarea#teamsListInAssignUsersToGroupsModal').textext()[0].tags().addTags(teamsArray);
		}
	}
	
	$('div#userRequestsNotifications.notificationsShown')
	.off('click').on(
			'click', function() {
				fetchAllUsersRequests(refreshMode, []);
				$('div#usersRequestsModal').modal('show');
				usersRequestsModalIsOpen = true;
			});
	
	$('#usersRequestsModal').on('shown', function () {
		$('.denyClass').addClass('hidden');
		$('.grantDenyClass').removeClass('hidden');
		
		$('table#usersRequestsTable').DataTable().columns.adjust().draw();
		$('table#usersRequestsTable').DataTable().columns.adjust().responsive.recalc();
		removeArrowFromFirstTableColumn();
	});
	
	$('div#userRequestsNotificationsTabletView.notificationsShown')
	.off('click').on(
			'click', function() {
				fetchAllUsersRequests(refreshMode, []);
				$('div#usersRequestsModal').modal('show');
				usersRequestsModalIsOpen = true;
			});
	
	$('#usersRequestsModal').on('shown', function () {
		$('table#usersRequestsTable').DataTable().columns.adjust().draw();
		$('table#usersRequestsTable').DataTable().columns.adjust().responsive.recalc();
		removeArrowFromFirstTableColumn();
	});

	$('a#reloadUsersRequestsTable').off().on(
			'click',
			function() {
				var reqIds = [];
				reqIds.push(theGroupId);
				var organizationId = $('#organizationId').text();
				// ajaxCallUsersRequests(reqIds, refreshMode, organizationId);
				$('table#usersRequestsTable').DataTable().clear();
				fetchAllUsersRequests(refreshMode, []);
//				$('div#usersRequestsTableToolbarContainer').addClass(
//						'hiddenToolbar').removeClass('shownToolbar');
				$('div#usersRequestsTableToolbarContainer').animate({height: 'hide'});
				$('#usersRequestsTable th.none').removeClass('none');
				// startPreloader();
			});
	
	$('#acceptUsersRequestsModal, #rejectUsersRequestsModal').on('hidden', function(){
		$('#usersRequestsModal').modal('show');
	});

	$('div#usersRequestsTableContainer').on('click',
					'div.usersRequestsTableToolbarContainer.shownToolbar div#acceptSeleced',
					function() {

						var usersEmails = [];
						usersRequestsMembershipRequestsIdsForEditing = [];
						// tagsForEmails
						for (var i = 0; i < usersRequestsDataForEditing.length; i++) {
							var email = usersRequestsDataForEditing[i][0].Email;
							usersEmails.push(email.substring(5,
									email.length - 6));
							var reqId = usersRequestsDataForEditing[i][0].RequestId;
							reqId = reqId.toString().substring(5,
									reqId.length - 6);
							usersRequestsMembershipRequestsIdsForEditing
									.push(reqId);
						}

						$('#acceptUsersRequestsOk').data('reqIDs',
								usersRequestsMembershipRequestsIdsForEditing);

						$('#acceptUsersRequestsModal').modal('show');

						$('div.modal-backdrop.fade.in').addClass(
								'hideFirstModal');
						
						$('#usersRequestsModal').modal('hide');
					});

	$('#acceptUsersRequestsModal').on('hidden', function() {
		$('div.modal-backdrop.fade.in').removeClass('hideFirstModal');
	});

	$('#usersRequestsModal').on('hidden', function() {
		sendCustomMailForMembershipRequestRejection = false;
		customMailForMembershipRequestRejectionBody = "";
		$('#emailForRejection').html(automaticRejectionEmailTemplate.html());
	});
	
	$('#userDetailsModal').on('hidden',function(){
		if(usersRequestsDetailModaWasOpen){
			$('#usersRequestsModal').modal('show');
		}
	});

	$('#acceptUsersRequestsOk').off().on('click', function() {
		var mode = acceptMode;
		var managerId = $('#userID').text();
		var reqIds = [];
		reqIds = $(this).data('reqIDs');
		// $('textarea#tagsForEmails').parent().find('.text-tag').each(function(){
		// reqIds.push($(this).data('reqId'));
		// });
		var organizationId = $('#organizationId').text();
		$('div#usersRequestsModal').modal('hide');
		$('textarea#tagsForEmails').parent().find('.text-tag').remove();
		if (reqIds.length === 0)
			return;
		
		$('table#usersRequestsTable').DataTable().clear();
		fetchAllUsersRequests(mode, reqIds, managerId, false);
		
		$('div#usersRequestsTableToolbarContainer').animate({height:'hide'});
		$('table#usersRequestsTable thead th:first-of-type').removeClass('none');
		
		$('#acceptUsersRequestsModal').modal('hide');

	});

	$('#rejectUsersRequestsOk').off().on('click', function() {
		var mode = deleteMode;
		var managerId = $('#userID').text();
		var reqIds = [];
		reqIds = $(this).data('reqIDs');
		// $('textarea#tagsForEmails').parent().find('.text-tag').each(function(){
		// reqIds.push($(this).data('reqId'));
		// });
		var organizationId = $('#organizationId').text();
		$('div#usersRequestsModal').modal('hide');
		$('textarea#tagsForEmails').parent().find('.text-tag').remove();
		if (reqIds.length === 0){
			$('#rejectUsersRequestsModal').modal('hide');
			return;
		}
			
		
		$('table#usersRequestsTable').DataTable().clear();
		fetchAllUsersRequests(mode, reqIds, managerId,
				false,"");
		
		$('div#usersRequestsTableToolbarContainer').animate({height:'hide'});
		$('table#usersRequestsTable thead th:first-of-type').removeClass('none');
		
		$('#rejectUsersRequestsModal').modal('hide');

	});

	$('div#usersRequestsTableContainer')
			.on(
					'click',
					'div.usersRequestsTableToolbarContainer.shownToolbar div#rejectSeleced',
					function() {
//						$('.grantDenyClass').addClass('hidden');
//						$('.denyClass').removeClass('hidden');
//						$('#emailForRejection').html(automaticRejectionEmailTemplate.html());
//						$('#emailForRejection').html(
//							$('#emailForRejection').html()
//							.replace('%site%', $('#groupName').text())
//							.replace('%portalName%', portalName)
//							.replace('%adminName%', $('#adminName').text())
//						);
//						$('#editEmailTemplate').tooltip();
//						
//						// allButtons hide
//						$('#closeUsersRolesModal').data('btnData', 1);
//						if (!$('div#emailForAcceptance').parent().hasClass(
//								'hideSection'))
//							$('div#emailForAcceptance').parent().addClass(
//									'hideSection');
//						$('div#emailForRejection').parent().removeClass(
//								'hideSection');
//						var width = $('div#usersRequestsModal div.modal-body')
//								.width();
//						width = '' + width + 'px';
//						$('div#usersRequestsTableContainer').animate(
//								{
//									right : width
//								},
//								'2500',
//								function() {
//									$('div#usersRequestsTableContainer')
//											.toggleClass('hideSection');
//									$('div#requestsAcceptanceContainer')
//											.toggleClass('hideSection').css({
//												left : '0px'
//											});
//								});
//						$('#userRequestsHeader').contents().first()[0].textContent = 'Requests rejection';
//						$('button#sendRejection').toggleClass('hideButton');
//						$('button#acceptAll').toggleClass('hideButton');
//						$('button#rejectAll').toggleClass('hideButton');
//
//						var usersEmails = [];
//						usersRequestsMembershipRequestsIdsForEditing = [];
//						// tagsForEmails
//						for (var i = 0; i < usersRequestsDataForEditing.length; i++) {
//							var email = usersRequestsDataForEditing[i][0].Email;
//							usersEmails.push(email.substring(5,
//									email.length - 6));
//							var reqId = usersRequestsDataForEditing[i][0].RequestId;
//							usersRequestsMembershipRequestsIdsForEditing
//									.push(reqId);
//						}
//
//						var tags = $('textarea#tagsForEmails').parent().find(
//								'div.text-tags div.text-tag');
//						if (tags.length > 0)
//							tags.remove();
//
//						$('textarea#tagsForEmails').textext()[0].tags().addTags(usersEmails);
//						for (var j = 0; j < usersRequestsMembershipRequestsIdsForEditing.length; j++) {
//							var theReqId = usersRequestsMembershipRequestsIdsForEditing[j]
//									.toString();
//							$($('#tagsForEmails').parent().find('.text-tag')[j])
//									.data(
//											'reqId',
//											theReqId.substring(5,
//													theReqId.length - 6));
//						}
//
//						$('textarea#tagsForEmails').parent().find(
//								'a.tag-remove').off().on('click', function() {
//							$(this).closest('.text-tag').remove();
//						});
//
//						$('#reloadUsersRequestsTable').addClass('hide');

						
						
						
						
						
						
//						var mode = deleteMode;
						var reqIds = [];
						for (var i = 0; i < usersRequestsDataForEditing.length; i++) {
							var reqId = usersRequestsDataForEditing[i][0].RequestId;
							reqIds.push($(reqId).text());
						}

						// startPreloader();
//						fetchAllUsersRequests(mode, reqIds, managerId,
//								false,"");
						
						$('#rejectUsersRequestsOk').data('reqIDs',
								reqIds);
						
						$('#usersRequestsModal').modal('hide');
						$('#rejectUsersRequestsModal').modal('show');
						
					});

	$('div#clickToGoBack').off().on(
			'click',
			function() {
				$('.denyClass').addClass('hidden');
				$('.grantDenyClass').removeClass('hidden');
				screenToTheLeft();
				$('#reloadUsersRequestsTable').removeClass('hide');

				$('#userEditedMailTemplate').closest('div').replaceWith('');
				if ($('#emailForRejection').length !== 1) {
					$('div#requestsAcceptanceBody .row:last').append(
							automaticRejectionEmailTemplate);
					$('#editEmailTemplate').tooltip();
				}
			});

	$('button#saveUsersRolesModal').off().on(
		'click',
		function() {
			var groupId = theGroupId;
			var trueFalse = true;
			var mode = acceptMode;
			var deleteUsers = false;
			var ajaxData = [];
			for (var i = 0; i < $($('#userNamesList').parent().find(
					'.text-tag')).length; i++) {
				ajaxData.push($(
						$('#userNamesList').parent().find('.text-tag')[i])
						.data('userUUID'));
			}
			var roles = [];
			$('#roleList').parent().find('div.text-tag span.text-label')
					.each(function() {
						roles.push($(this).text());
			});
			
			var teams = [];
			$('#teamsList').parent().find('div.text-tag span.text-label')
			.each(function() {
				teams.push($(this).text().trim());
			});

//			if (!deletePreviousRoles && (roles.length === 0 || teams.length)){
//				
//				return;
//			}
			var deletePreviousRoles2 = true;//If you remove a role, the roles should be updated even though the checkbox might be unchecked 
			
			fetchAllCurrentUsers(mode, deleteUsers, ajaxData, roles, teams, deletePreviousRoles2, [], false, MASS_EDIT_USERS);
			$('table#CurrentUsersTable tr.selected').removeClass('selected');
			$('#changeUsersRolesModal').modal('hide');
//			$('div#toolbar').addClass('hiddenToolbar').removeClass('shownToolbar');
			$('div#toolbar').removeClass('openToolbar');
			$('div#toolbar').animate({height:'hide'});
			$('#CurrentUsersTable th:first').removeClass('none');
			$('span#numOfSelLectedRows').text('');
		}
	);

	$('button#saveUsersRolesModalInAssignRolesModal').off().on(
		'click',
		function() {
			var groupId = theGroupId;
			var trueFalse = true;
			var mode = acceptMode;
			var deleteUsers = false;
			var ajaxData = [];
			for (var i = 0; i < $($('#userNamesListInAssignRolesModal').parent().find(
					'.text-tag')).length; i++) {
				ajaxData.push($(
						$('#userNamesListInAssignRolesModal').parent().find('.text-tag')[i])
						.data('userUUID'));
			}
			var roles = [];
			$('#roleListInAssignRolesModal').parent().find('div.text-tag span.text-label')
					.each(function() {
						roles.push($(this).text());
			});
			
			var teams = [];
			
			var deletePreviousRoles2 = true;//If you remove a role, the roles should be updated even though the checkbox might be unchecked 
			
			fetchAllCurrentUsers(mode, deleteUsers, ajaxData, roles, teams, deletePreviousRoles2, [], false, ASSIGN_ROLES_TO_USERS);
			$('table#CurrentUsersTable tr.selected').removeClass('selected');
			$('#assignUsersRolesModal').modal('hide');
//			$('div#toolbar').addClass('hiddenToolbar').removeClass('shownToolbar');
			$('span#numOfSelLectedRows').text('');
			$('#CurrentUsersTable th:first').removeClass('none');
//			$('div#toolbar').addClass('hiddenToolbar').removeClass('shownToolbar');
			$('div#toolbar').removeClass('openToolbar');
			$('div#toolbar').animate({height:'hide'});
			$('#CurrentUsersTable th:first').removeClass('none');
		}
	);

	$('button#saveUsersTeamsInAssignUsersToGroupsModal').off().on(
		'click',
		function() {
			var groupId = theGroupId;
			var trueFalse = true;
			var mode = acceptMode;
			var deleteUsers = false;
			var ajaxData = [];
			for (var i = 0; i < $($('#userNamesListInAssignUsersToGroupsModal').parent().find(
					'.text-tag')).length; i++) {
				ajaxData.push($(
						$('#userNamesListInAssignUsersToGroupsModal').parent().find('.text-tag')[i])
						.data('userUUID'));
			}
			var roles = [];
			var teams = [];
			$('#teamsListInAssignUsersToGroupsModal').parent().find('div.text-tag span.text-label')
			.each(function() {
				teams.push($(this).text().trim());
			});

			var deletePreviousRoles2 = true;//If you remove a role, the roles should be updated even though the checkbox might be unchecked 
			
			fetchAllCurrentUsers(mode, deleteUsers, ajaxData, roles, teams, deletePreviousRoles2, [], false, ASSIGN_TEAMS_TO_USERS);
			$('table#CurrentUsersTable tr.selected').removeClass('selected');
			$('#assignUsersToGroupsModal').modal('hide');
//			$('div#toolbar').addClass('hiddenToolbar').removeClass('shownToolbar');
			$('span#numOfSelLectedRows').text('');
			$('div#toolbar').removeClass('openToolbar');
			$('div#toolbar').animate({height:'hide'});
			$('#CurrentUsersTable th:first').removeClass('none');
		}
	);

	$('button#sendAcceptance').off().on('click', function() {
		var mode = acceptMode;
		var managerId = $('#userID').text();
		var reqIds = [];
		$('textarea#tagsForEmails').parent().find('.text-tag').each(function() {
			reqIds.push($(this).data('reqId'));
		});
		var organizationId = $('#organizationId').text();
		$('div#usersRequestsModal').modal('hide');
		$('textarea#tagsForEmails').parent().find('.text-tag').remove();
		if (reqIds.length === 0)
			return;
		screenToTheLeft();
		// startPreloader();
		fetchAllUsersRequests(mode, reqIds, managerId);
		// ajaxCallUsersRequests(reqIds, acceptMode, organizationId);
	});

	$('button#sendRejection').off()
			.on(
					'click',
					function() {
						var mode = deleteMode;
						var managerId = $('#userID').text();
						var reqIds = [];
						$('textarea#tagsForEmails').parent().find('.text-tag')
								.each(function() {
									reqIds.push($(this).data('reqId'));
								});
						var organizationId = $('#organizationId').text();
						$('div#usersRequestsModal').modal('hide');
						$('textarea#tagsForEmails').parent().find('.text-tag')
								.remove();
						if (reqIds.length === 0)
							return;
						screenToTheLeft();

						customMailForMembershipRequestRejectionBody = $('#userEditedMailTemplate').val();
						
						if(customMailForMembershipRequestRejectionBody !== undefined){
							customMailForMembershipRequestRejectionBody = customMailForMembershipRequestRejectionBody.replace(/\n/g,"<br>");
						}

						// startPreloader();
						fetchAllUsersRequests(mode, reqIds, managerId,
								sendCustomMailForMembershipRequestRejection,
								customMailForMembershipRequestRejectionBody);
						// ajaxCallUsersRequests(reqIds, deleteMode,
						// organizationId);
						
//						fetchAllRejectedUsersRequests();
					});

	$('#userRequestsNotifications #notificationsNumberPlaceHolder, #userRequestsNotificationsTabletView #notificationsNumberPlaceHolderTabletView').off('DOMNodeInserted').bind(
			'DOMNodeInserted',
			function(event) {
				$('#closeUsersRolesModal').data('btnData', 0);
				if ($(this).text() === '0' || $(this).text() === '') {
					$(this).parent().removeClass('notificationsShown').addClass(
							'notificationsHidden');
					if($(this).text() === '0'){
						return;
					}else{
						$(this).text('0');
					}
//					$('#usersManagementDiv').text('No Pending Requests');
				} else {
					$(this).parent().removeClass('notificationsHidden').addClass(
							'notificationsShown');
//					$('#usersManagementDiv').text('Pending Requests:');
				}
			});
	
	if ($('#userRequestsNotifications #notificationsNumberPlaceHolder').text() === '0') {
		$('#userRequestsNotifications').removeClass('notificationsShown')
				.addClass('notificationsHidden');
	}
	if ($('#userRequestsNotificationsTabletView #notificationsNumberPlaceHolderTabletView').text() === '0') {
		$('#userRequestsNotificationsTabletView').removeClass('notificationsShown')
				.addClass('notificationsHidden');
	}

	$('button#acceptAll').off().on(
			'click',
			function() {
				var mode = acceptMode;
				var managerId = $('#userID').text();
				var existingTrs = $('table#usersRequestsTable tbody tr');
				var reqIDs = [];
				for (var i = 0; i < existingTrs.length; i++) {
					var data = $($('table#usersRequestsTable').dataTable()
							.fnGetData(existingTrs[i]));
					var reqID = data[0].RequestId;
					reqIDs.push(reqID.substring(5, reqID.length - 6));
				}
				var organizationId = $('#organizationId').text();
				fetchAllUsersRequests(mode, reqIDs, managerId);
				$('div#usersRequestsModal').modal('hide');
			});

	$('button#rejectAll').off().on(
			'click',
			function() {
//				var mode = deleteMode;
//				var managerId = $('#userID').text();
//				var existingTrs = $('table#usersRequestsTable tbody tr');
//				var reqIDs = [];
//				for (var i = 0; i < existingTrs.length; i++) {
//					var data = $($('table#usersRequestsTable').dataTable()
//							.fnGetData(existingTrs[i]));
//					var reqID = data[0].RequestId;
//					reqIDs.push(reqID.substring(5, reqID.length - 6));
//				}
//				fetchAllUsersRequests(mode, reqIDs, managerId, false, "");
//				$('div#usersRequestsModal').modal('hide');
				if($('#usersRequestsTable th:first').hasClass('none')){
					var trs = $('#usersRequestsTable tbody tr');
					$.each(trs, function(){
						$(this).find('td:first-of-type').click();
					});
					$.each(trs, function(){
						$(this).find('td:first-of-type').click();
					});
				}else {
					var trs = $('#usersRequestsTable tbody tr:not(.selected)');
					$.each(trs, function(){
						$(this).find('td:first-of-type').click();
					});
				}
				$('#rejectSeleced').click();
			});

	$('a#currentUsersTableRefresh').off().on(
			'click',
			function() {
				var groupId = theGroupId;
				var doRefresh = true;
				var mode = refreshMode;
				var selectedUsers = [];
				var roles = [];
				// startPreloader();
				fetchAllCurrentUsers(2, false, [], [], false, [], false);
				// ajaxCallCurrentUsers(groupId, doRefresh, mode, selectedUsers,
				// roles, false);
//				if ($('div#toolbar').hasClass('shownToolbar'))
//					$('div#toolbar').addClass('hiddenToolbar').removeClass(
//							'shownToolbar');
			});

	$(document).on('click', 'button#editEmailTemplate', function() {
		var automaticTemplate = $('#emailForRejection');
		automaticTemplate.find('div.tooltip.fade.top.in').remove();
		var text = automaticTemplate.text();
		var div = $('<div></div>', {
			'class' : 'span11'
		});
		var textarea = $('<textarea></textarea>', {
			id : 'userEditedMailTemplate',
			text : text.trim(),
			'class' : 'span12',
			rows : 7
		});
		div.append(textarea);
		automaticTemplate.replaceWith(div);

		// Setting this parameter to true means the admin wants to send a custom
		// mail to the user whose request is being rejected.
		sendCustomMailForMembershipRequestRejection = true;
	});
	
	$('#openEditModal').off('click').on('click', function(){
		$('#userDetailsModal').modal('hide');
		if(keepTrackOfUsersTableRow !== -1){
			var htmlRow = $('#CurrentUsersTable tbody tr')[keepTrackOfUsersTableRow];
			var $Row = $(htmlRow);
			$Row.find('td:first').trigger('click');
			$('div#toolbar.shownToolbar div#editSelected').trigger('click');
		}
		keepTrackOfUsersTableRow = -1;
	});
}