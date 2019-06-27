(function() {
	'use strict';
	
	$('#geoadmin-create-plugin-form').validate({
	    rules : {
	    	createPluginName : {
			    required : true
		    },
		    createPluginDescription : {
			    required : true
		    },
	    	'geoadmin-create-plugin-pluginLibrary-JAR' : {
			    required : true,
			    extension : "jar"
		    },
		    'geoadmin-create-plugin-widget-name' : {
			    required : true
		    },
		    'geoadmin-create-plugin-className' : {
			    required : true
		    },
		    'geoadmin-create-plugin-methodName' : {
			    required : true
		    },
		    'geoadmin-create-plugin-jsFileName' : {
			    required : true
		    },
		    'geoadmin-create-plugin-configurationClass' : {
			    required : true
		    }
	    },
	    highlight : function(element) {
		    $(element).closest('.control-group').addClass('error');
	    },
	    success : function(label, element) {
		    $(element).closest('.control-group').removeClass('error');
		    label.remove();
	    },
	    errorPlacement : function(error, element) {
	    	if($(element).attr('id') === 'geoadmin-create-plugin-pluginLibrary-JAR'){
	    		error.appendTo($(element).closest('.control-group').find('.help-inline'));
	    	}else {
	    		error.appendTo($(element).siblings('.help-inline'));
	    	}
		    
	    }
	});

	$('#geoadmin-edit-plugin-form').validate({
	    rules : {
	    	editPluginName : {
			    required : true,
		    },
		    editPluginDescription : {
			    required : true,
		    },
	    	'geoadmin-edit-plugin-widget-name' : {
			    required : true,
		    },
	    	'geoadmin-edit-plugin-className' : {
			    required : true,
		    },
	    	'geoadmin-edit-plugin-methodName' : {
			    required : true,
		    },
	    	'geoadmin-edit-plugin-jsFileName' : {
			    required : true,
		    },
	    	'geoadmin-edit-plugin-configurationClass' : {
			    required : true,
		    }
	    },
	    highlight : function(element) {
		    $(element).closest('.control-group').addClass('error');
	    },
	    success : function(label, element) {
		    $(element).closest('.control-group').removeClass('error');
		    label.remove();
	    },
	    errorPlacement : function(error, element) {
		    error.appendTo($(element).siblings('.help-inline'));
	    }
	});

	$('#geoadmin-create-plugin-form input, #geoadmin-create-plugin-form textarea').bind('input', function() {
		var valid = $(this).closest("form").validate().checkForm();
		$('#geoadmin-create-plugin-modal-submit').attr("disabled", !valid);
	});

	$('#geoadmin-edit-plugin-form input, #geoadmin-edit-plugin-form textarea').bind('input', function() {
		var valid = $(this).closest("form").validate().checkForm();
		$("#geoadmin-edit-plugin-modal-submit").attr("disabled", !valid);
	});
	
	$('#geoadmin-create-plugin-button').one('click', function(){
		$('#geoadmin-create-plugin-modal-submit').attr("disabled", true);
	});
	
	$('#geoadmin-create-plugin-modal-clear-form').on('click', function(){
		var valid = $("#geoadmin-create-plugin-form").validate().checkForm();
		$('#geoadmin-create-plugin-modal-submit').attr("disabled", !valid);
	});
	
	$('#geoadmin-edit-plugin-modal-clear-form').on('click', function(){
		var valid = $("#geoadmin-edit-plugin-form").validate().checkForm();
		$('#geoadmin-create-plugin-modal-submit').attr("disabled", !valid);
	});
	
	$('#geoadmin-create-plugin-modal-submit').on('click', function(){
		var valid = $("#geoadmin-create-plugin-form").validate().checkForm();
		$('#geoadmin-create-plugin-modal-submit').attr("disabled", !valid);
	});
})();