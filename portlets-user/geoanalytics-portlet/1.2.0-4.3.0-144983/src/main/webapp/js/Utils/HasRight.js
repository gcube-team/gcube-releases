function hasRight(projectName, right, callback, $element, projectId) {
	userinfoObject.projectName = projectName;
	userinfoObject.projectId = projectId;
	
	switch(right) {
		case 'read':
			url = authorizedToReadProjectURL;
			break;
		case 'edit':
			url = authorizedToEditProjectURL;
			break;
		case 'delete':
			url = authorizedToDeleteProjectURL;
			break;
	}
	
	var hasRightCallback = function(data){
		hideSpinner();
		if(data.status === "Success") {
			callback($element);
		} else if(data.status === 'Unauthorized') {
			$('#messageRights').text(data.message);
			$('#authorizationMessageModal').modal('show');
		} else {
			$('.wizard').modal('hide');
			$('#InternalServerErrorModal').modal('show');
		}
	};
	
	showSpinner();
	AJAX_Call_POST(url, hasRightCallback, userinfoObject);
}