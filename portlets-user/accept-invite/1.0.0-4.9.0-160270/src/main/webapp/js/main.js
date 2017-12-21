$(document).ready(function(){	
	$( "#createAccountButton" ).click(function() {
		var firstnameBox =  $.trim( $('#firstname').val() )
		if (firstnameBox == "") {
			$('#labelFirstName').css("color","red");
			$('#labelFirstName').text("First Name (This field is required)");
		} else {
			$('#labelFirstName').css("color","#555");
			$('#labelFirstName').text("First Name (Required)");
		}
		var lastnameBox =  $.trim( $('#lastname').val() )
		if (lastnameBox == "") {
			$('#labelLastName').css("color","red");
			$('#labelLastName').text("Last Name (This field is required)");
		} else {
			$('#labelLastName').css("color","#555");
			$('#labelLastName').text("Last Name (Required)");
		}

		var passwd1 = $('#password');
		var labelPwd1 =  $('#labelPwd1');
		var passwd2 = $('#repassword');
		var labelPwd2 =  $('#labelPwd2');

		labelPwd1.addClass( passwd1.val().length === 0 ? 'has-error' : 'has-success' );
		labelPwd2.addClass( passwd2.val().length === 0 ? 'has-error' : 'has-success' );

		var nomatch =  $('#labelPasswordDontMatch');

		if (passwd1.val() !== passwd2.val()) {
			nomatch.css("display","block");
			nomatch.css("color","red");
			labelPwd1.addClass( passwd1.val() !== passwd2.val() ? 'has-error' : 'has-success' )
			.removeClass( passwd1.val() === passwd2.val() ? 'has-error' : 'has-success' );
			labelPwd2.addClass( passwd1.val() !== passwd2.val() ? 'has-error' : 'has-success' )
			.removeClass( passwd1.val() === passwd2.val() ? 'has-error' : 'has-success' );
		} 
		else {
			nomatch.css("display","none");
		}

		var shortpwd =  $('#labelPasswordTooShort');

		if (passwd1.val().length > 0 && passwd1.val().length < 8) {
			shortpwd.css("display","block");
			shortpwd.css("color","red");
			labelPwd1.addClass(passwd1.val().length < 8 ? 'has-error' : 'has-success' )
			.removeClass(passwd1.val().length < 8  ? 'has-error' : 'has-success' );
			labelPwd2.addClass(passwd2.val().length < 8  ? 'has-error' : 'has-success' )
			.removeClass(passwd2.val().length < 8  ? 'has-error' : 'has-success' );
		} 
		else {
			shortpwd.css("display","none");
		}	
		
		if (passwd1.val() == passwd2.val() && passwd1.val().length >= 8 && lastnameBox != "" && firstnameBox != "") {
			doCallback();
		}
	
	});
});