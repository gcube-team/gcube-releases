pageState = {};

function showUserManagement(resourceURL, contextPath, data, notificator) {
	pageState.cPath = contextPath;
	pageState.rURL = resourceURL;
	pageState.notificator = notificator;
	
	enableZMaxIndex();
	
	var url = createLink(pageState.rURL, 'users/listUsers');

    $.ajax({
        url : url,
        type : "post",
        success : function(response) {
        	if (response.data.length == 0) {
        		alert("No data for principals");
        		return;
        	}
        	for (var i=0; i<response.data.length; i++) {
        		$('#users tbody').append('<tr><td>'+response.data[i]+'<td/></tr>');
        	}
        },
        error : function(jqXHR, textStatus, errorThrown) {
        	alert("The following error occured: " + textStatus, errorThrown);
        }
    }); 
}