(function() {
	"use strict";

	$("#geoadmin-layers-add-external-form").validate({
	    rules : {
	        geoserverUrl : {
	            required : true,
	            url : true
	        },
	        workspace : {
		        required : true
	        },
	        name : {
		        required : true
	        }
	    },
	    highlight : function(element) {
		    $(element).closest(".control-group").addClass("error");
	    },
	    success : function(label, element) {
		    $(element).closest(".control-group").removeClass("error");
		    label.remove();
	    },
	    errorPlacement : function(error, element) {
		    error.appendTo($(element).siblings(".help-inline"));
	    }
	});

	$("#geoadmin-layers-add-external-modal-submit").attr("disabled", true);

	$("#geoadmin-layers-add-external-form input").bind("input", function() {
		var valid = $(this).closest("form").validate().checkForm();
		$(this).closest(".modal").find(".modal-footer").find("button[type='button']").attr("disabled", !valid);
	});
})();