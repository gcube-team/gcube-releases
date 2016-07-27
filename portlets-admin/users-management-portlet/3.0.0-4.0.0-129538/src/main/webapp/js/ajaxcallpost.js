function surroundObjectPropWithDiv(object){
	for(var prop in object){
		if(object[prop].length === 0)object[prop] = "-";
		if(object[prop] === '123')object[prop] = "";
		if(Array.isArray(object[prop]) && object[prop].length > 0){
			var variable = "";
			for(var i = 0; i < object[prop].length; i++){
				if(i===object[prop].length-1){
					variable += object[prop][i];
				}else{
					variable += object[prop][i] + ", ";
				}
			}
			object[prop] = variable;
		}
		object[prop] = '<div>' + object[prop] + '</div>';
	}
	return object;
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
					removeArrowFromFirstTableColumn();
					hideTeamManagementToolbar();
					hidePreloader();
				},
				error: function (xhr, ajaxOptions, thrownError) {
					$('div.modal.fade').modal('hide');
					hidePreloader();
					$('#usersManagementPortletContainer #InternalServerErrorModal').modal('show');	
//					alert(xhr.status);
//					alert(thrownError);
				}
			}
		);
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

function fetchAllCurrentUsers(mode, deleteUsers, usersUUIDs, roles, teams, deleteRoles, reqIDs, sendEmail, typeOfChangesUpponUserMode){
	var theData = dataToBeSendViaAJAX("groupId", theGroupId);
	theData = dataToBeSendViaAJAX("currentUsersTable", true, theData);
	theData = dataToBeSendViaAJAX("deleteUsersFromCurrentUsersTable", deleteUsers, theData);
	theData = dataToBeSendViaAJAX("sendDismissalEmail", sendEmail, theData);
	theData = dataToBeSendViaAJAX("selectedUsers", usersUUIDs, theData);
	theData = dataToBeSendViaAJAX("usersRoles", roles, theData);
	theData = dataToBeSendViaAJAX("usersTeams", teams, theData);
	theData = dataToBeSendViaAJAX("modeCurrentUsersTable", mode, theData);
	theData = dataToBeSendViaAJAX("deletePreviousRoles", deleteRoles, theData);
	theData = dataToBeSendViaAJAX("membershipRequestsIDs", reqIDs, theData);
	theData = dataToBeSendViaAJAX("typeOfChangesUpponUserMode", typeOfChangesUpponUserMode, theData);
	
	var callBack = function(data){
		var content= JSON.parse(data);
		currentUsers = [];
		currentUsers = content.currentUsers;

		if(!$.isEmptyObject(content)){
			for(var i = 0; i < currentUsers.length; i++){
				currentUsers[i] = new currentUsersObjectForDataTable(
								'<i class="icon-ok"></i>',
								currentUsers[i].userName,
								currentUsers[i].userEmail,
								currentUsers[i].userFullName,
								currentUsers[i].userSiteRoles,
								currentUsers[i].userTeams,
								currentUsers[i].userId,
								currentUsers[i].requestDate,
								currentUsers[i].validationDate,
								currentUsers[i].reqID,
								currentUsers[i].acceptanceAdmin,
								currentUsers[i].isSelf
				);
				currentUsers[i] = surroundObjectPropWithDiv(currentUsers[i]);
			}
			
			$('table#CurrentUsersTable').DataTable().clear();
			for(var i = 0; i < currentUsers.length; i++){
				$('table#CurrentUsersTable').dataTable().fnAddData(currentUsers[i]);
			}
			
			var isDataTable = $.fn.DataTable.isDataTable( '#GroupTeamsTable' );
//			var siteTeamsTableDataLenght = $('#GroupTeamsTable').dataTable().fnGetData().length;
			if(mode !== 2 && isDataTable){//refresh
				fetchAllSiteTeamsForTheCurrentGroup();
			}else if(mode !== 2 && !isDataTable){
				initializeGroupTeamsTable();
				fetchAllSiteTeamsForTheCurrentGroup();
				searchInputFixForSiteTeamsEditTable();
				siteTeamsTableEvents();
				constructToolbarForSiteTeamsTable();
				initializeSiteTeamUsersTable();
				searchInputFixForSiteTeamsUsersTable();
				
				setTimeout(function(){//If you don't add some time interval, the table won't redraw when you press the tab
					$('table#GroupTeamsTable').DataTable().columns.adjust().draw();
					$('table#GroupTeamsTable').DataTable().columns.adjust().responsive.recalc();

					removeArrowFromFirstTableColumn();
				},200);
				$('.unhit').removeClass('unhit').addClass('redraw');
				$('li.redraw').off('click').on('click', function(){
					setTimeout(function(){//If you don't add some time interval, the table won't redraw when you press the tab
						$('table#GroupTeamsTable').DataTable().columns.adjust().draw();
						$('table#GroupTeamsTable').DataTable().columns.adjust().responsive.recalc();

						removeArrowFromFirstTableColumn();
					},200);
					$(this).removeClass('redraw');
				});
			}
			
		} else {
			hidePreloader();
			$('#usersManagementPortletContainer #InternalServerErrorModal').modal('show');
		}
	};
	
	AJAX_CALL_POST(theData, callBack);
}

function fetchAllUsersRequests(mode, reqIDs, replyUserId, sendCustomRejectionEmail, CustomRejectionEmailBody){
	var theData = dataToBeSendViaAJAX("groupId", theGroupId);
	theData = dataToBeSendViaAJAX("fetchUsersRequests", true, theData);
	theData = dataToBeSendViaAJAX("modeMembershipRequestsTable", mode, theData);
	theData = dataToBeSendViaAJAX("membershipRequestsIds", reqIDs, theData);
	theData = dataToBeSendViaAJAX("managerId", replyUserId, theData);
	theData = dataToBeSendViaAJAX("CustomRejectionEmailFromAdmin", sendCustomRejectionEmail, theData);
	theData = dataToBeSendViaAJAX("CustomRejectionEmailBodyFromAdmin", CustomRejectionEmailBody, theData);
	
	var callBack =  function(data){
		var content= JSON.parse(data);
		currentUsersRequests = [];
		currentUsersRequests = content.currentUsersRequests;
		
		if(!$.isEmptyObject(content) && content !== null && content.currentUsersRequests.length!== 0){
			for(var i = 0; i < currentUsersRequests.length; i++){
				currentUsersRequests[i] = new usersRequestObjectForDataTable(
								'<i class="icon-ok"></i>',
								currentUsersRequests[i].userName,
								currentUsersRequests[i].userEmail,
								currentUsersRequests[i].userFullName,
								currentUsersRequests[i].requestComments,
								currentUsersRequests[i].userId,
								currentUsersRequests[i].requestId,
								currentUsersRequests[i].requestDate
				);
				currentUsersRequests[i] = surroundObjectPropWithDiv(currentUsersRequests[i]);
			}
			$('table#usersRequestsTable').DataTable().clear();
			for(var i = 0; i < currentUsersRequests.length; i++){
				$('table#usersRequestsTable').dataTable().fnAddData(currentUsersRequests[i]);
			}
			
			countUsersMembershipRequests();
			$('div#usersRequestsTableToolbarContainer').addClass('hiddenToolbar').removeClass('shownToolbar');
			$('span#numOfSelectedRowsUserReqs').text('');
			
		} else if(content.currentUsersRequests.length === 0) {
			$('#notificationsNumberPlaceHolder').text(0);
			$('#notificationsNumberPlaceHolderTabletView').text(0);
		} else if(content === null) {
			hidePreloader();
			$('#usersManagementPortletContainer #InternalServerErrorModal').modal('show');
		}
		
		fetchAllCurrentUsers(2, false, [], [], [], false, [], false);
	}
	
	AJAX_CALL_POST(theData, callBack);
}


function countUsersMembershipRequests(){
	var theData = dataToBeSendViaAJAX("groupId", theGroupId);
	theData = dataToBeSendViaAJAX("countUsersMembershipRequests", true, theData);
	
	var callBack = function(data){
		var content= JSON.parse(data);
		
		if(!$.isEmptyObject(content) && content !== null){
			$('#notificationsNumberPlaceHolder').text(content.countUsersMembershipRequests[0]);
			$('#notificationsNumberPlaceHolderTabletView').text(content.countUsersMembershipRequests[0]);
			portalName = content.countUsersMembershipRequests[1];//retrieving the portal name from the backend via ajax
		}else if(content === null) {
			hidePreloader();
			$('#usersManagementPortletContainer #InternalServerErrorModal').modal('show');
		}
	};
	
	AJAX_CALL_POST(theData, callBack);
}

function fetchAllSiteTeamsForTheCurrentGroup(){
	var theData = dataToBeSendViaAJAX("groupId", theGroupId);
	theData = dataToBeSendViaAJAX("fetchAllSiteTeamsForTheCurrentGroup", true, theData);
	theData = dataToBeSendViaAJAX("modeSiteTeams", SITE_TEAMS_TABLE_REFRESH, theData);
	
	var callBack = function(data){
		var content= JSON.parse(data);
		var siteTeams = [];
		siteTeams = content.siteTeams;
		
		if(!$.isEmptyObject(content) && content !== null && siteTeams.length!== 0){
			var teams = [];
			for(var i = 0; i < siteTeams.length; i++){
				siteTeams[i] = new siteTeamsObjectForDataTable(
						'<i class="icon-ok"></i>',
						siteTeams[i].Name,
						siteTeams[i].TeamID,
						siteTeams[i].Description,
						siteTeams[i].NumberOfUsers,
						siteTeams[i].CreationDate,
						siteTeams[i].LastModificationDate,
						siteTeams[i].CreatorName,
						siteTeams[i].siteTeamUsers
				);
				teams.push(siteTeams[i].Name);
				siteTeams[i].siteTeamUsers = formatSiteTeamUsers(siteTeams[i].siteTeamUsers);
				siteTeams[i] = surroundObjectPropWithDiv(siteTeams[i]);
			}
			teamEditedOrDeleted(teams);
			$('table#GroupTeamsTable').DataTable().clear();
			for(var i = 0; i < siteTeams.length; i++){
				$('table#GroupTeamsTable').dataTable().fnAddData(siteTeams[i]);
			}
		}else if(content === null) {
			hidePreloader();
			$('#usersManagementPortletContainer #InternalServerErrorModal').modal('show');
		}
		if(!handlersAppliedToToolbarForFirstTime){
			siteTeamsToolbarEvents();
		}
	};
	
	AJAX_CALL_POST(theData, callBack);
}

function EditSiteTeamsForTheCurrentGroup(siteTeamName, siteTeamDescription, TeamID){
	var theData = dataToBeSendViaAJAX("groupId", theGroupId);
	theData = dataToBeSendViaAJAX("fetchAllSiteTeamsForTheCurrentGroup", true, theData);
	theData = dataToBeSendViaAJAX("modeSiteTeams", SITE_TEAMS_TABLE_EDIT_GROUP, theData);
	theData = dataToBeSendViaAJAX("siteTeamName", siteTeamName, theData);
	theData = dataToBeSendViaAJAX("siteTeamDescription", siteTeamDescription, theData);
	theData = dataToBeSendViaAJAX("siteTeamID", TeamID, theData);
	
	var callBack = function(data){
		var content= JSON.parse(data);
		var siteTeams = [];
		siteTeams = content.siteTeams;
		
		if(!$.isEmptyObject(content) && content !== null && siteTeams.length!== 0){
			var teams = [];
			for(var i = 0; i < siteTeams.length; i++){
				siteTeams[i] = new siteTeamsObjectForDataTable(
						'<i class="icon-ok"></i>',
						siteTeams[i].Name,
						siteTeams[i].TeamID,
						siteTeams[i].Description,
						siteTeams[i].NumberOfUsers,
						siteTeams[i].CreationDate,
						siteTeams[i].LastModificationDate,
						siteTeams[i].CreatorName,
						siteTeams[i].siteTeamUsers
				);
				teams.push(siteTeams[i].Name);
				siteTeams[i].siteTeamUsers = formatSiteTeamUsers(siteTeams[i].siteTeamUsers);
				siteTeams[i] = surroundObjectPropWithDiv(siteTeams[i]);
			}
			teamEditedOrDeleted(teams);
			$('table#GroupTeamsTable').DataTable().clear();
			for(var i = 0; i < siteTeams.length; i++){
				$('table#GroupTeamsTable').dataTable().fnAddData(siteTeams[i]);
			}
			fetchAllCurrentUsers(2, false, [], [], false, [], false);
		}else if(content === null) {
			$('div.modal.fade').modal('hide');
			hidePreloader();
			$('#usersManagementPortletContainer #InternalServerErrorModal').modal('show');
		}
		$('#editGroupTeamModal').modal('hide');
	};

	showPreloader();
	AJAX_CALL_POST(theData, callBack);
}

function CreateSiteTeamsForTheCurrentGroup(siteTeamName, siteTeamDescription){
	var theData = dataToBeSendViaAJAX("groupId", theGroupId);
	theData = dataToBeSendViaAJAX("fetchAllSiteTeamsForTheCurrentGroup", true, theData);
	theData = dataToBeSendViaAJAX("modeSiteTeams", SITE_TEAMS_TABLE_CREATE_GROUP, theData);
	theData = dataToBeSendViaAJAX("siteTeamName", siteTeamName, theData);
	theData = dataToBeSendViaAJAX("siteTeamDescription", siteTeamDescription, theData);
	
	var callBack = function(data){
		var content= JSON.parse(data);
		var siteTeams = [];
		siteTeams = content.siteTeams;
		
		if(!$.isEmptyObject(content) && content !== null && siteTeams.length!== 0){
			var teams = [];
			for(var i = 0; i < siteTeams.length; i++){
				siteTeams[i] = new siteTeamsObjectForDataTable(
						'<i class="icon-ok"></i>',
						siteTeams[i].Name,
						siteTeams[i].TeamID,
						siteTeams[i].Description,
						siteTeams[i].NumberOfUsers,
						siteTeams[i].CreationDate,
						siteTeams[i].LastModificationDate,
						siteTeams[i].CreatorName,
						siteTeams[i].siteTeamUsers
				);
				teams.push(siteTeams[i].Name);
				siteTeams[i].siteTeamUsers = formatSiteTeamUsers(siteTeams[i].siteTeamUsers);
				siteTeams[i] = surroundObjectPropWithDiv(siteTeams[i]);
			}
			teamEditedOrDeleted(teams);
			$('table#GroupTeamsTable').DataTable().clear();
			for(var i = 0; i < siteTeams.length; i++){
				$('table#GroupTeamsTable').dataTable().fnAddData(siteTeams[i]);
			}
			fetchAllCurrentUsers(2, false, [], [], false, [], false);
		}else if(content === null) {
			$('div.modal.fade').modal('hide');
			hidePreloader();
			$('#usersManagementPortletContainer #InternalServerErrorModal').modal('show');
		}
		$('#newGroupTeamModal').modal('hide');
	};
	
	$('div.modal.fade').modal('hide');
	showPreloader();
	AJAX_CALL_POST(theData, callBack);
}

function DeleteSiteTeamsForTheCurrentGroup(teamID){
	var theData = dataToBeSendViaAJAX("groupId", theGroupId);
	theData = dataToBeSendViaAJAX("fetchAllSiteTeamsForTheCurrentGroup", true, theData);
	theData = dataToBeSendViaAJAX("modeSiteTeams", SITE_TEAMS_TABLE_DELETE_GROUP, theData);
	theData = dataToBeSendViaAJAX("siteTeamID", teamID, theData);
	
	var callBack = function(data){
		var content= JSON.parse(data);
		var siteTeams = [];
		siteTeams = content.siteTeams;
		
		if(!$.isEmptyObject(content) && content !== null && siteTeams.length!== 0){
			var teams = [];
			for(var i = 0; i < siteTeams.length; i++){
				siteTeams[i] = new siteTeamsObjectForDataTable(
						'<i class="icon-ok"></i>',
						siteTeams[i].Name,
						siteTeams[i].TeamID,
						siteTeams[i].Description,
						siteTeams[i].NumberOfUsers,
						siteTeams[i].CreationDate,
						siteTeams[i].LastModificationDate,
						siteTeams[i].CreatorName,
						siteTeams[i].siteTeamUsers
				);
				teams.push(siteTeams[i].Name);
				siteTeams[i].siteTeamUsers = formatSiteTeamUsers(siteTeams[i].siteTeamUsers);
				siteTeams[i] = surroundObjectPropWithDiv(siteTeams[i]);
			}
			teamEditedOrDeleted(teams);
			$('table#GroupTeamsTable').DataTable().clear();
			for(var i = 0; i < siteTeams.length; i++){
				$('table#GroupTeamsTable').dataTable().fnAddData(siteTeams[i]);
			}
			fetchAllCurrentUsers(2, false, [], [], false, [], false);
		}else if(content === null) {
			$('div.modal.fade').modal('hide');
			hidePreloader();
			$('#usersManagementPortletContainer #InternalServerErrorModal').modal('show');
		}
		$('#deleteGroupTeamModal').modal('hide');
	};

	showPreloader();
	AJAX_CALL_POST(theData, callBack);
}