$.widget( "adminControl.notification", {
	options: {
		text: "",
		defaultText: {
			success: '<strong>Success!</strong> ',
			danger: '<strong>Error!</strong> '
		},
//		timeout: 19000000
		timeout: 3000
	},
	_create: function() {},
	success: function(text) {
		this._msg('success', text);
	},
	error: function(text) {
		this._msg('danger', text, 'redmine');
	},
	_msg: function(type, text, addClass) {
		if ($("#notification").length != 0)
			$("#notification").remove();
		var alert = $('<div id="notification" class="alert fade" data-alert="alert">' +
							'<button type="button" class="close" aria-label="Close">' +
								'<span aria-hidden="true">×</span>' +
							'</button>' +
       			  	   '</div>').appendTo(this.element);
     
		alert.find('span').off().click({w: this}, function(e) {
			e.data.w._remove($($(this).parents('div')[0]), true);
		});
     
		alert.append( text? text : this.options.defaultText[type]);
		if (addClass)
			$("#notification").addClass(addClass);
		
		this._setTimeout(alert, this.options.timeout)
    },
	_setTimeout: function(e, timeout){
		setTimeout(function(w, e) {
			w._remove(e);
		}, timeout, this, e);
    },
    _remove: function(e, force) {
    	if (e.is(':hover') && !force)
    		e.mouseleave({w: this}, function(e){
    			e.data.w._setTimeout($(this), e.data.w.options.timeout);
    		});
    	else if(e.hasClass('alert')) {
    		$("#notification").removeClass("in");
    	}
    }
});