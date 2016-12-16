/* 
 * Author: Massimiliano Assante, CNR-ISTI 
 * */

/*The following function simply injects the Liferay object fields userId and scopeGroupId in the XMLHttpRequest header.
 * So that every ajax call performed in the page has those parameters set.*/
function injectClientContext() {
	if (Liferay != null) {
		var userId;
		var groupId;
		if (Liferay.ThemeDisplay.isSignedIn()) {
			userId = Liferay.ThemeDisplay.getUserId();
			groupId = Liferay.ThemeDisplay.getScopeGroupId();			
			console.log("groupid is = " + groupId);
		}
		else {
			groupId = Liferay.ThemeDisplay.getScopeGroupId();
			//console.log('Not logged in, injecting groupId only');
		}

		XMLHttpRequest.prototype.realSend = XMLHttpRequest.prototype.send;
		var newSend = function(vData) {
			this.setRequestHeader("gcube-userId", userId);
			this.setRequestHeader("gcube-vreid", groupId);
			this.setRequestHeader("gcube-request-url", location.href);
			this.realSend(vData);
		};
		XMLHttpRequest.prototype.send = newSend;
	}
}
/*
 * Override the expire function of the Liferay.Session javascript object. It makes the default behaviour and then open a modal
 * */
$(function () {    
	   AUI().use('liferay-session', function(A) {
	      if(Liferay.Session) {
	    	 console.log('Liferay session default overridden');
	         Liferay.Session.expire = function() {
	             var instance = this;
	                instance.set('sessionState', 'expired', {});
	            $("#expirationModal").css("display", "block");
	            $("#expirationModal").modal({backdrop: 'static', keyboard: false},'show');
	            $("body div.alert.alert-block.popup-alert-warning.alert-error").html("Due to inactivity your session has expired, please <a class='refresh' href=''>Refresh</a>");
	         };
	      }
	   });
	});
