function toggleSingle(obj) {
	var el = document.getElementById(obj);
	el.style.display = el.style.display != 'none' ? 'none' : '' ;
}


function getElementsByClass(searchClass,node,tag) {

	var classElements = new Array();

	if ( node == null ) node = document;

	if ( tag == null ) tag = '*';

	var els = node.getElementsByTagName(tag);

	var elsLen = els.length;

	var pattern = new RegExp('(^|\\\\s)'+searchClass+'(\\\\s|$)');

	for (i = 0, j = 0; i < elsLen; i++) {
		if ( pattern.test(els[i].className) ) {
			classElements[j] = els[i];
			j++;
		}
	}
	return classElements;
}

function toggle(searchClass,obj,tag) {
	var el = document.getElementById(obj);
	var classElements = getElementsByClass(searchClass,el,tag);
	
	for ( var i=0; i < classElements.length; i++ ) {
		classElements[i].style.display = classElements[i].style.display != 'none' ? 'none' : '' ;
	}
}