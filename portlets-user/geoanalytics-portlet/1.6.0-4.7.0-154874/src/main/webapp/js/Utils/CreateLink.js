function createLink(url, resource, param) {
	var link;
	if (param === undefined) {
		link = url.replace('%7Burl%7D', resource).replace('%7Bparams%7D', '');
		if (link.charAt(link.length - 1) == "?")
			link = link.slice(0, -1);
	} else
		link = url.replace('%7Burl%7D', resource).replace('%7Bparams%7D', encodeURIComponent(param));
	
	return link;
}