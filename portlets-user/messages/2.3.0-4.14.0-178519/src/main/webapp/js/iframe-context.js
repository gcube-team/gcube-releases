/* 
 * Author: Massimiliano Assante, CNR-ISTI 
 * */

function getParameterByName(name, url) {
		if (!url) url = window.location.href;
		name = name.replace(/[\[\]]/g, "\\$&");
		var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
		results = regex.exec(url);
		if (!results) return null;
		if (!results[2]) return '';
		return decodeURIComponent(results[2].replace(/\+/g, " "));
}
	

	var ThemeDisplayObject = { 
			userId : getParameterByName('uid'),
			groupId : getParameterByName('gid'),
			getUserId : function () {
				return this.userId
			},
			getScopeGroupId : function () {
				return this.groupId
			},
			isSignedIn : function () {
				return true
			} 
	} 

	var Liferay = new Object();
	Liferay.ThemeDisplay = ThemeDisplayObject;


