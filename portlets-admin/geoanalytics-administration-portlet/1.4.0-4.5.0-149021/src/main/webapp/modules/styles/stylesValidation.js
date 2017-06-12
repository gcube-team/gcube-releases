(function() {
	'use strict';

	$('#geoadmin-create-style-form').validate({
	    rules : {
		    styleName : {
		    	minlength: 2,
			    required : true
		    },
		    browseStyleFiles : {
		    	extension: "xml",
			    required : true
		    }
	    },
	    messages: {
	    	browseStyleFiles: "Please upload a file with .xml extension",
	    },
	    highlight : function(element) {
		    $(element).closest('.control-group').addClass('error');		
	    },
	    success : function(label, element) {
		    $(element).closest('.control-group').removeClass('error');
		    label.remove();
	    },
	    errorPlacement : function(error, element) {
	    	if(element.attr('id') === 'geoadmin-create-style-content-browseButton'){
				error.appendTo(element.closest('.control-group').find('.help-inline'));	
			}else{
				error.appendTo($(element).siblings('.help-inline'));
			}
	    }
	});

	$('#geoadmin-edit-style-form').validate({
	    rules : {
		    styleName : {
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

	$('#geoadmin-create-style-form input, #geoadmin-edit-style-form input').bind('input', function() {
		var valid = $(this).closest("form").validate().checkForm();
		$(this).closest(".modal").find(".modal-footer").find("button[type='button']").attr("disabled", !valid);
	});

	$('#geoadmin-edit-style-form input, #geoadmin-edit-style-form textarea').bind('input', function() {
		var selectedRowData = window.styles.dataTable.row(window.styles.selectedRow).data();
		var name = $("#geoadmin-edit-style-name").val();
		var description = $("#geoadmin-edit-style-description").val();
		var valid = (selectedRowData[1] !== name || selectedRowData[2] !== description) && $(this).closest("form").validate().checkForm();
		$(this).closest(".modal").find(".modal-footer").find("button[type='button']").attr("disabled", !valid);
	});

})();