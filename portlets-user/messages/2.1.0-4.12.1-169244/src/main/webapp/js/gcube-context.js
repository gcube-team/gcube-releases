/* 
 * Author: Massimiliano Assante, CNR-ISTI 
 * */

/*The following function simply injects the Liferay object field scopeGroupId in the XMLHttpRequest header.
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
		//attach the 3 header params in all the XHR sends
		(function(send) {
			XMLHttpRequest.prototype.send = function(data) {
				this.setRequestHeader("gcube-userId", userId);
				this.setRequestHeader("gcube-vreid", groupId);
				this.setRequestHeader("gcube-request-url", location.href);
				send.call(this, data);
			};
		})(XMLHttpRequest.prototype.send);
	}
}

