function postDataToServer(json, postUrl, callback) {
	if (json != null) {
		$.ajax({
			url: postUrl,
			type: 'post',
			cache: false,
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify(json),
			success: function(response) {
				if(callback) callback(response);
			},
			error : function(jqXHR, textStatus, errorThrown) {
				if(callback) {
					var response = {};
					response.state = false;
					response.data = undefined;
					response.message = "An unenxpected error occured";
					
					callback(response);
				}
			}
		});
	} else {
		$.ajax({
			url: postUrl,
			type: 'post',
			cache: false,
			contentType: "application/json",
			dataType: "json",
			success: function(response) {
				if(callback) callback(response);
			},
			error : function(jqXHR, textStatus, errorThrown) {
				if(callback) {
					var response = {};
					response.state = false;
					response.data = undefined;
					response.message = "An unenxpected error occured";
					
					callback(response);
				}
			}
		});
	}
	
}

function createLink(url, resource, param) {
	var link;
	if (param === undefined) {
		link = url.replace('%7Burl%7D', resource).replace('%7Bparams%7D', '');
		if (link.charAt(link.length - 1) == "?")
			link = link.slice(0, -1);		
	} else link = url.replace('%7Burl%7D', resource).replace('%7Bparams%7D', param);
	return link;
}