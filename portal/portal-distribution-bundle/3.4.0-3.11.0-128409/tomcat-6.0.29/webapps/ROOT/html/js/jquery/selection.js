jQuery.fn.disableSelection = function() {
	return this.attr('unselectable', 'on').css('MozUserSelect', 'none').bind('selectstart.ui',
		function() {
			return false;
		}
	);
};

jQuery.fn.enableSelection = function() {
	return this.attr('unselectable', 'off').css('MozUserSelect', '').unbind('selectstart.ui');
};