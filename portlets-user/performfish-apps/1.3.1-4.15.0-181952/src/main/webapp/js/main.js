function downloadItem(downloadFileURL) {
	var uri = downloadFileURL;
	window.open(uri, "_blank");
}
function downloadVersion(downloadFileURL) {
	var uri = downloadFileURL;
	window.open(uri, "_blank");
}
function getSelectedRadioFileId() {
	if (document.querySelector('input[name="fileItem"]:checked') == null)
		alert('Please select the file you wish to operate with first');
	return document.querySelector('input[name="fileItem"]:checked').id;
}

function getSelectedRadioFormWorkspaceItemId() {
	if (document.querySelector('input[name="fileItem"]:checked') == null)
		alert('Please select the file you wish to operate with first');
	return document.querySelector('input[name="fileItem"]:checked').value;
}


function getSelectedRadioFarmId() {
	if (document.querySelector('input[name="farmItem"]:checked') == null)
		alert('Please select the farm you want to manage');
	return document.querySelector('input[name="farmItem"]:checked').id;
}

function validateForm() {
	if (document.querySelector('input[name="fileItem"]:checked') == null) {
		alert('Please select the file first to see the versions');
		return false;
	}
	document.getElementById("form_versions").submit();// Form submission
}

function showFileNameWarningFromDOM(show) {
	if (show)
		document.getElementById('fileNamesExplain').style.display = "block";
	else
		document.getElementById('fileNamesExplain').style.display = "none";
}



function validateFile(companyId, farmId, endpoint, encodedURI, fileName, phase) {
	console.log("validateFile:" + endpoint);
	$('#validation-result-container').hide();
	$('#validation-portlet-container').show();
	$.ajax({
		url : endpoint,
		type : 'POST',
		datatype : 'json',
		data : {
			companyId : companyId,
			farmId : farmId,
			userId : Liferay.ThemeDisplay.getUserId(),
			encodedURI : encodedURI,
			groupId : Liferay.ThemeDisplay.getScopeGroupId(),
			fileName: fileName,
			phase: phase
		},
		success : function(data) {
			var content = JSON.parse(data);
			$('#validation-portlet-container').hide();
			$('#validation-result-container').show();
			if (content.success) {
				$('#resultFeedback').text('Congrats, the form is valid! '+ content.comment);
				$('#resultFeedback').css('color', 'green');
				$('#resultFeedbackButton').show();
			}
			else {
				$('#resultFeedback').text('Oops, the form is not valid! Motivation: ' + content.comment);
				$('#resultFeedback').css('color', 'red');
			}
		}
	});
}

/* in view_farm_repository */
function publishAndAnonymise(endpoint, farmId, fileId) {
	console.log("publishAndAnonymise:" + endpoint);
	$('#publishAndAnonymise-Button').html('<i class="icon-refresh icon-white">in progress ...please wait');
	
	$.ajax({
		url : endpoint,
		type : 'POST',
		datatype : 'json',
		data : {
			farmId : farmId,
			userId : Liferay.ThemeDisplay.getUserId(),
			fileToPublishId : fileId,
			groupId : Liferay.ThemeDisplay.getScopeGroupId()
		},
		success : function(data) {
			var content = JSON.parse(data);
			if (content.success == "OK") {
				$("#modalSuccess").show();
			}
			else {
				$("#modalFailed").show();
			}
			$('#publishAndAnonymise-Button').html('Anonymise and Publish');			
		}
	});
}

function showInsertFarm(show) {
	if (show) {
		$('#formNewFarmButton').hide();
		$('#formNewFarm').show();
	} else {
		$('#formNewFarmButton').show();
		$('#formDeleteFarm').hide();
		$('#formEditFarm').hide();
		$('#formNewFarm').hide();
	}
}



function showEditFarm(show) {
	if (show) {
		$('#formEditFarmButton').hide();
		$('#formEditFarm').show();
		$('#formDeleteFarm').hide();
		$('#formNewFarm').hide();
	} else {
		$('#formEditFarmButton').show();
		$('#formDeleteFarm').hide();
		$('#formEditFarm').hide();
		$('#formNewFarm').hide();
	}
}

function showDeleteFarm(show) {
	if (show) {
		$('#formDeleteFarmButton').hide();
		$('#formDeleteFarm').show();
		$('#formEditFarm').hide();
		$('#formNewFarm').hide();
	} else {
		$('#formDeleteFarmButton').show();
		$('#formDeleteFarm').hide();
		$('#formEditFarm').hide();
		$('#formNewFarm').hide();
	}
}

