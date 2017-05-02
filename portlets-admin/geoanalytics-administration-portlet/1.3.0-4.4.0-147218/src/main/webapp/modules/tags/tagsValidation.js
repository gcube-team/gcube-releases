(function() {
	'use strict';

	$('#geoadmin-create-tag-form').validate({
	    rules : {
		    tagName : {
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
		    error.appendTo($(element).siblings('.help-inline'));
	    }
	});

	$('#geoadmin-edit-tag-form').validate({
	    rules : {
		    tagName : {
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

	$('#geoadmin-create-tag-form input, #geoadmin-edit-tag-form input').bind('input', function() {
		var valid = $(this).closest("form").validate().checkForm();
		$(this).closest(".modal").find(".modal-footer").find("button[type='button']").attr("disabled", !valid);
	});

	$('#geoadmin-edit-tag-form input, #geoadmin-edit-tag-form textarea').bind('input', function() {
		var selectedRowData = window.tags.dataTable.row(window.tags.selectedRow).data();
		var name = $("#geoadmin-edit-tag-name").val();
		var description = $("#geoadmin-edit-tag-description").val();
		var valid = (selectedRowData[1] !== name || selectedRowData[2] !== description) && $(this).closest("form").validate().checkForm();
		$(this).closest(".modal").find(".modal-footer").find("button[type='button']").attr("disabled", !valid);
	});

})();