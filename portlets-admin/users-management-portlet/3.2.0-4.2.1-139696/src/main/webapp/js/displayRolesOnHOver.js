function displaySiteRolesOnHover(){
	$('#usersManagementPortletContainer #CurrentUsersTable_wrapper').on('mouseover', function(e){
		var tagName = $(e.target).prop('tagName').toLowerCase();
		var parentTagName = $(e.target).parent().prop('tagName').toLowerCase();
		var className = $(e.target).attr('class');
		if(tagName === "td" || parentTagName === "td" || tagName === "span" || tagName === "ul" || tagName === "li"){
			return;
		}else {
			destroyBubble();
		}
	});
	
	$('#usersManagementPortletContainer table#CurrentUsersTable, table#usersRequestsTable')
	.off('mouseover').on('mouseover', 'tr td:not(:first) div', function(e){
		destroyBubble();
		var tagName = $(e.target).prop('tagName').toLowerCase();
		
		if(isEllipsisActive($(this))){
			$(this).closest('td').addClass('relative');
			
			var one = $('<span></span>', {
				id : 'rolesArrow'
			});
			var two = $('<span></span>', {
				id : 'onHoverUser',
				'class' : 'rolesBubbletittle'
			});
			var three = $('<span></span>', {
				id : 'elementBubbletittle'
//				text : 'Roles of'
			}); 
			
			var four = $('<span></span>', {
				id : 'roles'
			});
			
			var five = $('<span></span>', {
				id : 'rolesContainer'
			});
			
			four.append(three).append(two).append(one);
			five.append(four);
			$(this).closest('td').append(five);
			

			var text = $(this).text();
			var splitted = text.split(',');
			
			var theUl = $('<ul></ul>', {
				id : 'rolesList'
			});
			
			$('#roles').append(theUl);
			
			for(var string in splitted){
				$('#rolesList').append($('<li></li>', {
					text : splitted[string]
				}));
			}
			var thIndex = $(this).closest('td').index() + 1;
			var title = $('#CurrentUsersTable th:nth-child(' + thIndex + ')').text();
			$('#onHoverUser').text(title);
		}
	}).on('click', 'tr td:not(:first-of-type) div', function(){
		var table = $(this).closest('table');
		var tr = $(this).closest('tr');
		var index = $(this).closest('td').index() + 1;
		var data = table.dataTable().fnGetData(tr[0]);
		$('#userDetailsModal .modal-body').html('');
		$('span#userName').text('');
		var tableId = table.attr('id');
		if(tableId === 'CurrentUsersTable'){
			$('#openEditModal').removeClass('hidden');
			passUsersDetailsToModalFromCurrentUsersTable(tableId, data);
		}else if(tableId === 'usersRequestsTable'){
			$('#openEditModal').addClass('hidden');
			passUsersDetailsToModalFromUsersRequestsTable(tableId, data);
			
		}
		
		if($(this).closest('table').attr('id') === 'CurrentUsersTable'){
//			console.log('The index of the row is: ' + );
			var thisTrIndex = $('#CurrentUsersTable tbody tr').index($(this).closest('tr'));
			keepTrackOfUsersTableRow = thisTrIndex;
		}
	});
}

function destroyBubble(){
	$('#rolesList').remove();
	$('#usersManagementPortletContainer .relative').removeClass('relative');
	$('#rolesContainer').remove();
}

function isEllipsisActive(element) {
	  return_val = false;
	  var text = element.text();
	  var html = $('<span></span>',{
		  text : text,
		  id : "tmpsmp"
	  });
	  
	  $('body').append(html);
	  
	  if(element.width() < html.width()) {
	   return_val = true;
	  }
	  
	  $('#tmpsmp').remove();
	  
	  return return_val;
}

function removeDivTag(element){
	var text = $($.parseHTML(element)).text();
	return text;
}

function passUsersDetailsToModalFromCurrentUsersTable(tableId, data){
	var i=0;
	$('span#userName').text(removeDivTag(data.UserName));
	
	for(var field in data){
		if(i === 0 || i === 6 || i === 9 || i === 11) {
			i++;
			continue;
		} else {
			i++;
		}
		injectDetailsToModal(tableId, i, removeDivTag(data[field]));
	}
	
	usersRequestsDetailModaWasOpen = false;
	$('#userDetailsModal').modal('show');
}

function passUsersDetailsToModalFromUsersRequestsTable(tableId, data){
	var i=0;
	$('span#userName').text(removeDivTag(data.UserName));
	for(var field in data){
		if(i === 0 || i === 5 || i === 7) {
			i++;
			continue;
		} else {
			i++;
		}
		injectDetailsToModal(tableId, i, removeDivTag(data[field]));
	}
	
	$('#usersRequestsModal').modal('hide');
	usersRequestsDetailModaWasOpen = true;
	$('#userDetailsModal').modal('show');
}

function injectDetailsToModal(tableId, thNumber, text){
	if(thNumber === 8){
		thNumber = 7;
	}else if(thNumber === 9){
		thNumber = 8;
	}else if(thNumber === 11){
		thNumber = 9;
	}else if(thNumber === 7){
		thNumber = 6;
	}
	
	var properTitle = $('#' + tableId + ' th:nth-child(' + thNumber + ')').text();
	buildRowsForModal(properTitle, text);
}

function buildRowsForModal(properTitle, text){
	//Label for attribute must equal the div id
	var randomNumber = Math.floor(Math.random() * 2323614) + 1;
	var row = $('<div></div>', {
		'class' : 'row-fluid'
	});
	var theId = 'randomId'+randomNumber;
	var userField = $('<div></div>', {
		id : theId,
		text : text,
		'class' : 'span9'
	});
	var label = $('<label></label>', {
		'for' : theId,
		'class' : 'span3',
		text : properTitle + ':'
	});
	
	row.append(label).append(userField);
	
	$('#userDetailsModal .modal-body').append(row);
}