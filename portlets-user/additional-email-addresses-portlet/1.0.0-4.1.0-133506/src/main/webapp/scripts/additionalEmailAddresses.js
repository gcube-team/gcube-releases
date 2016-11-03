var additionalEmailAddressesNS = {};

$(document).ready(function (){
    	
	var saveEmail = ".AdditionalEmailAddressesPortlet #save-email";
	var cancelEmail = ".AdditionalEmailAddressesPortlet #cancel-email";
	var addEmail = ".AdditionalEmailAddressesPortlet #add-email";
	
	var notificationEmail = ".AdditionalEmailAddressesPortlet .max-emails-notification";
	var addEmailBlock = ".AdditionalEmailAddressesPortlet .add-email-buttons";
	var inputEmail = ".AdditionalEmailAddressesPortlet .add-email-input";
		
	var maxNumEmails = 0;
	var numEmails = 0;
	
	var enterNewEmail = "Enter new email";
	var emailIsNotVerified = "(not verified)";
	var reverifyYourEmailAddress = "Re-Verify your email";
	var cancelButtonLabel = "Cancel";
	var saveButtonLabel = "Save";
	
	/**
	 * Initialize the javascript logic.
	 * 
	 */
	additionalEmailAddressesNS.init = function(sendEmailVerification, listAdditionalEmailAddresses, removeEmailVerification, selectPrimaryEmailAddress, resendVerificationEmail, maxEmails, isEmailAddressAlreadyUsed){
	
		maxNumEmails = parseInt(maxEmails);
		
		fetchAdditionalEmailAddresses(listAdditionalEmailAddresses);
		setAdditionalEmailAddressesEvents(sendEmailVerification, removeEmailVerification, selectPrimaryEmailAddress, resendVerificationEmail, isEmailAddressAlreadyUsed);
	}
	
	additionalEmailAddressesNS.language = function(enterNewEmailNative, emailIsNotVerifiedNative, reverifyYourEmailAddressNative, cancelButtonLabelNative, saveButtonLabelNative){
		
		enterNewEmail = enterNewEmailNative, emailIsNotVerified = emailIsNotVerifiedNative, reverifyYourEmailAddress = reverifyYourEmailAddressNative;
		cancelButtonLabel = cancelButtonLabelNative, saveButtonLabel = saveButtonLabelNative;
	}

	/**
	 * 
	 * Initialize the jquery events.
	 * 
	 */
	function setAdditionalEmailAddressesEvents(sendEmailVerification, removeEmailVerification, selectPrimaryEmailAddress, resendVerificationEmail, isEmailAddressAlreadyUsed){
		$(document).on('click', saveEmail, function(){
			registerSendEmailVertificationEvent(sendEmailVerification, isEmailAddressAlreadyUsed);
		});
		$(document).on('change', '.AdditionalEmailAddressesPortlet .radio-button-for-primary', function(){
			registerSelectPrimaryEmailAddressEvent.call(this, selectPrimaryEmailAddress);
		});
		$(document).on('click', '.AdditionalEmailAddressesPortlet .remove-email', function(){
			registerRemoveEmailVerificationEvent.call(this, removeEmailVerification);
		});
		$(document).on('click', '.AdditionalEmailAddressesPortlet .verify-email', function(){
			registerResendVerificationEmailEvent.call(this, resendVerificationEmail);
		});
		
		$(document).on('click', addEmail, createDomInput);
		$(document).on('click', cancelEmail, refreshDom);
	}
	
   /**
    * 
    * Fetch additional Email Addresses using Ajax call.
    * 
    */
   function fetchAdditionalEmailAddresses(listAdditionalEmailAddresses){

	   var promiseOfFetchAdditionalEmailAddresses = callToBackEnd(listAdditionalEmailAddresses, "get");
	   
	   successFetchAdditionalEmailAddresses = promiseOfFetchAdditionalEmailAddresses.done(createGridOfAdditionalEmailAddresses).done(refreshDom);
	   FailFetchAdditionalEmailAddresses = promiseOfFetchAdditionalEmailAddresses.done(showError);
   }
   
   /**
    * 
    * Fetch additional Email Addresses using Ajax call.
    * 
    */
   function registerResendVerificationEmailEvent(resendVerificationEmail){

	   var data = { emailAddressId : $(this).attr('id')};
	   var promiseOfFetchAdditionalEmailAddresses = callToBackEnd(resendVerificationEmail, "get", data);
	   
	   successFetchAdditionalEmailAddresses = promiseOfFetchAdditionalEmailAddresses.done(createGridOfAdditionalEmailAddresses).done(refreshDom);
	   FailFetchAdditionalEmailAddresses = promiseOfFetchAdditionalEmailAddresses.done(showError);
   }
   
   /**
    * 
    * Set Verification Email Event
    * 
    */
   function registerSendEmailVertificationEvent(sendEmailVerification, isEmailAddressAlreadyUsed){

	   var data = { emailAddress : $('.AdditionalEmailAddressesPortlet .add-email-input input').val() };
	   var promiseOfIsEmailAddressAlreadyUsed = callToBackEnd(isEmailAddressAlreadyUsed, "get", data);
	   promiseOfIsEmailAddressAlreadyUsed.done(function(response) {
		   if (!response.entity.isUsed) {
			   var promiseOfFetchAdditionalEmailAddresses = callToBackEnd(sendEmailVerification, "get", data);
			   
			   successFetchAdditionalEmailAddresses = promiseOfFetchAdditionalEmailAddresses.done(createGridOfAdditionalEmailAddresses).done(refreshDom);
			   FailFetchAdditionalEmailAddresses = promiseOfFetchAdditionalEmailAddresses.done(showError); 
		   }
		   else {
			   alert("The given e-mail address cannot be used as it is already present in the system");
		   }
	   });
   }
   
   /**
    * 
    * Set Remove Email verification Event
    * 
    */
   function registerRemoveEmailVerificationEvent(removeEmailVerification){

	   var data = { emailAddressId : $(this).attr('id')};
	   var promiseOfFetchAdditionalEmailAddresses = callToBackEnd(removeEmailVerification, "get", data);
	   
	   successFetchAdditionalEmailAddresses = promiseOfFetchAdditionalEmailAddresses.done(createGridOfAdditionalEmailAddresses).done(refreshDom);
	   FailFetchAdditionalEmailAddresses = promiseOfFetchAdditionalEmailAddresses.done(showError);
   }
   
   /**
    * 
    * Set Select Primary Email Event
    * 
    */
   function registerSelectPrimaryEmailAddressEvent(selectPrimaryEmailAddress){
	   
	   var data = { emailAddressId : $(this).val() };
	   var promiseOfFetchAdditionalEmailAddresses = callToBackEnd(selectPrimaryEmailAddress, "get", data);
	   
	   successFetchAdditionalEmailAddresses = promiseOfFetchAdditionalEmailAddresses.done(createGridOfAdditionalEmailAddresses).done(refreshDom);
	   FailFetchAdditionalEmailAddresses = promiseOfFetchAdditionalEmailAddresses.done(showError);
   }
   
   function callToBackEnd(url, method, data){
		  var ajaxCall =  $.ajax({
					       url: url,
					       type: method,
					       dataType: "json",
					       data: (typeof data == "undefined") ? {} : data,
		   			  });

		  return ajaxCall;
   }
   
   function createDomInput(){
	   
		   var input = '<div class="add-email-input">' + 
		   				 '<div class="row-fluid">' +
			   				 '<div class="span1"></div>' +
			   				 '<div class="span9">' +
			   					 '<input class="span12" type="text" placeholder="'+ enterNewEmail +'">' +
			   				 '</div>' +
			   				 '<div class="span2"></div>' +
			   			 '</div>' +
			   			 '<div class="row-fluid">' +
			   				 '<div class="span1"></div>' +
			   				 '<div class="span9">' +
			   					 '<button id="cancel-email" type="button" class="btn">'+ cancelButtonLabel +'</button>' +
			   					 '<button id="save-email" type="button" class="btn">'+ saveButtonLabel +'</button>' +
			   				 '</div>' +
			   				 '<div class="span2"></div>' +
			   			 '</div>'
			   		   '</div>';
		   
		   $(input).insertBefore('.AdditionalEmailAddressesPortlet .add-email-buttons');
		   $(".AdditionalEmailAddressesPortlet .add-email-buttons").hide();
   }
   
   //TODO
   function createGridOfAdditionalEmailAddresses(data){
	  
	  var input = "";
	  var $formDom = $('.AdditionalEmailAddressesPortlet #addtional-email-addresses-form');
	  numEmails = data.entity.emailAddresses.length;
	  $.each(data.entity.emailAddresses, function(index, value){
		   if (maxNumEmails < index + 1){
			   return;
		   }
		   input = input + '<div class="row-fluid email-row ' + ((index%2==0) ? "even-row" : "odd-row") + '">' +
		   						'<div class="span1">' +
		   							'<input class="radio-button radio-button-for-primary" name="primary-email" type="radio" value="'+ value.id +'"' + ((value.isPrimary) ? "checked" : "") + '>' +
		   						'</div>' +
		   						'<div class="span9 email-address ' + ((value.status == "ACTIVE") ? "active" : "inactive") + '">' +
	   								'<p>'+ value.email +'</p>' +
	   								((value.status == "ACTIVE") ? '<span class="active">(verified)</span><span class="no-need-verify-email">no need to Re-Verify your email</span>' : 
	   															  '<span class="inactive">'+ emailIsNotVerified +'</span><span id="'+ value.id +'" class="verify-email">'+ reverifyYourEmailAddress +'</span>') +
	   							'</div>' +
	   							'<div class="span2">' +
	   								'<i id="'+ value.id +'" class="icon-remove remove-email"></i>' +
	   							'</div>' +
		   					'</div>';
	  });
	  
	  $('.AdditionalEmailAddressesPortlet .email-row').remove();
	  $formDom.prepend(input);
   }
   
   function refreshDom(){
	   if(numEmails >= maxNumEmails){
		   $(addEmailBlock).hide();
		   $(notificationEmail).show();
		   $(inputEmail).remove();
	   }else{
		   $(addEmailBlock).show();
		   $(notificationEmail).hide();
		   $(inputEmail).remove();
	   }
   }
   
   //TODO
   function showError(data){
	   console.log("ERROR -> Back-End message", data);
   }   
});
