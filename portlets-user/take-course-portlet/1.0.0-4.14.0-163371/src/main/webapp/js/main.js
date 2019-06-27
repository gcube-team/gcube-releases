//retrieve the videos associated to the unit, if any
function getUnitVideos(unitId, endpoint) {
	$.ajax({
		url : endpoint,
		type : 'POST',
		datatype : 'json',
		data : {
			unitId : unitId,
			userId : Liferay.ThemeDisplay.getUserId(),
			groupId : Liferay.ThemeDisplay.getScopeGroupId(),
		},
		success : function(data) {
			var content = JSON.parse(data);
			var j = 0;
			var table = $('<table></table>');
			$.each(content,	function(i, item) {
				var videoURL = item.url;
				//src="https://www.youtube.com/embed/ZgxJO-0wXAk"
				if (! (videoURL.indexOf("/embed/") >=0) && (videoURL.indexOf("youtube") >= 0 || videoURL.indexOf("youtu.be") >=0 )) {
					console.log("Not valid:"+videoURL);
					var n = videoURL.lastIndexOf("v=");
					if (n >= 0) {
						var result = videoURL.substring(n + 2);
						videoURL = "https://www.youtube.com/embed/"+result;
					} 
					else {	//https://youtu.be/901kiwhjUE8
						n = videoURL.lastIndexOf("be/");
						var result = videoURL.substring(n + 3); 
						videoURL = "https://www.youtube.com/embed/"+result;
					}
				}
				console.log("video:"+videoURL);
				var videoEmbedded = '<iframe width="400" height="260" src="'+videoURL+'" frameborder="0" allowfullscreen></iframe>';
				var row = '<tr style="width: 100%">';
				row+= '<td>'+videoEmbedded+'</td></tr>';				
				table.append(row);
				j++;
			});
			if (j > 0) {
				$('#span-videos-' + unitId).text(" " + (j) + " Video(s) ");
				$('#pvideos-' + unitId).append(table);
				$('#pvideos-' + unitId).show();
			}
		}
	});
}

//retrieve the questionnaires associated to the unit, if any
function getUnitQuestionnaires(unitId, endpoint, userId) {
	if (userId < 0)
		userId = Liferay.ThemeDisplay.getUserId();
	$.ajax({
		url : endpoint,
		type : 'POST',
		datatype : 'json',
		data : {
			unitId : unitId,
			userId : userId,
			groupId : Liferay.ThemeDisplay.getScopeGroupId(),
		},
		success : function(data) {
			var content = JSON.parse(data);
			var j = 0;
			var table = $('<table></table>');
			var answeredNo = 0;
			$.each(content,	function(i, item) {
				var row = '<tr style="width: 100%">';
				if (item.answered) {
					row += '<td><span class="label label-success">answered</span></td>';
					answeredNo++;
				}
				else
					row += '<td><span class="label label-warning"> to answer</span></td>';
				row += '<td><i class="icon-file-text-alt"></i></td>'
					+ '<td valign="middle"><a href="'+item.url+'" target="_blank">'
					+ (j+1) + '. ' + item.name
					+ '</a></td></tr>';
				table.append(row);
				j++;
			});
			if (j > 0) {
				$('#graded-' + unitId).text("Graded: " + j + " Assessment questionnaires");
				$('#quiz-' + unitId).append(table);
				$('#quiz-' + unitId).show();
				$('#pgrades-' + unitId).show();
				updatePercentage(answeredNo,j, unitId);
			}
			else	
				$('#graded-' + unitId).hide();
		}
	});
}

//retrieve the files in the unit workspace folder, if any
function getUnitFolderContent(unitId, folderid, endpoint, setFileReadEndpoint, userId) {
	if (userId < 0)
		userId = Liferay.ThemeDisplay.getUserId();
	$.ajax({
		url : endpoint,
		type : 'POST',
		datatype : 'json',
		data : {
			folderId : folderid,
			userId : userId,
			groupId : Liferay.ThemeDisplay.getScopeGroupId(),
		},
		success : function(data) {
			var content = JSON.parse(data);
			$('#folder-' + folderid).html('');// to clear the previous content
			var table = $('<table></table>');
			var j = 1;
			var readNo = 0;
			$.each(content,	function(i, item) {
				var row = '<tr style="width: 100%">';
				if (item.read) {
					row += '<td><span class="label label-success">read</span></td>';
					readNo++;
				}
				else
					row += '<td><span id="spanread-'+item.id+'" class="label label-warning">to read</span></td>';
				row += '<td class="photo-mini">'
					+ '<a href="'+item.uri+'"><img src="'+ item.image +'"><a/></td>'
					+ '<td valign="middle"><a href="javascript:doSetRead(\''+ unitId + '\', \''+ folderid + '\', \''+ item.id + '\',\'' + setFileReadEndpoint + '\',\''+ item.uri + '\');">'
					+ j + '. ' + item.name
					+ '</a></td></tr>';

				table.append(row);
				j++;
			});

			$('#folder-' + folderid).append(table);
			$('#items-' + folderid).show();
			updatePercentage(readNo,j-1, unitId);
			$('#span-' + folderid).text(
					"Content: " + (j - 1) + " files");
		}
	});
}

//download the file and set it read in the workspace
function doSetRead(unitId, folderId, workspaceItemId, endpoint, uri) {
	if (navigator.userAgent.indexOf('Safari') != -1 && navigator.userAgent.indexOf('Chrome') == -1) 
		window.open(uri);
	else
		window.open(uri, "_self");
	$.ajax({
		url : endpoint,
		type : 'POST',
		datatype : 'json',
		data : {
			unitId : unitId,
			folderId : folderId,
			workspaceItemId : workspaceItemId,
			userId : Liferay.ThemeDisplay.getUserId(),
			groupId : Liferay.ThemeDisplay.getScopeGroupId(),
		},
		success : function(data) {
			$('#spanread-' + workspaceItemId).removeClass("label-warning");
			$('#spanread-' + workspaceItemId).addClass("label-success");
			if ($('#spanread-' + workspaceItemId).text() == "to read") {
				$('#spanread-' + workspaceItemId).text("read");
				increasePercentagebyOne(unitId);
			}		
		}
	});
}

function updatePercentage(num, total, unitId){
	var currentCompleted = Number($('#pnum-' + unitId).val());
	var totalToComplete = Number($('#ptotal-' + unitId).val());	
	currentCompleted = currentCompleted + Number(num);
	totalToComplete = totalToComplete + Number(total);
	$('#pnum-' + unitId).val(currentCompleted);
	$('#ptotal-' + unitId).val(totalToComplete);
	//update the progress bar
	var per =  Math.floor(currentCompleted * 100 / totalToComplete);
	$('#bar-' + unitId).width(per+'%');
	$('#bar-' + unitId).text(per+'%');
	$('#percentage-' + unitId).show();
}

function increasePercentagebyOne(unitId) {
	var currentCompleted = Number($('#pnum-' + unitId).val());
	var totalToComplete = Number($('#ptotal-' + unitId).val());	
	currentCompleted++;
	$('#pnum-' + unitId).val(currentCompleted);
	var per =  Math.floor(currentCompleted * 100 / totalToComplete);
	$('#bar-' + unitId).width(per+'%');
	$('#bar-' + unitId).text(per+'%');
}

