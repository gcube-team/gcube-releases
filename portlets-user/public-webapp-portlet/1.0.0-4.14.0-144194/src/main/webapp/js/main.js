/**
 * Javascript wrapper for the Public webapp Portlet
 * The Javascript will grab:
 * 	- the Liferay configuration params (appURL, appURLTokenParam property)
 *  - the VRE security token for invoking D4Science web-services
 *  
 * And configures a simple iframe to embedd the web-application
 *
 * @author Emmanuel Blondel (FAO)
 */

$(document).ready(function() { 

	//Inherit Liferay configuration portal
    var appURL = $("#appURL").attr("data-app-url");
    console.log("Application URL = "+appURL)
    
    var appURLTokenParam = $("#appURLTokenParam").attr("data-app-url-token-param");
	console.log("Application URL Token param ="+appURLTokenParam);
    
	//Inherit VRE security user token
    var securityToken = $("#securityToken").attr("data-securitytoken");
    console.log("VRE security token = "+securityToken);

    
    var vreApp = appURL;
    if(appURLTokenParam != "") vreApp += "?" + appURLTokenParam + "=" + securityToken;
    console.log("Public application URL running in VRE = "+vreApp);
    
    $("#mainPortletContainer").html('<iframe src ="'+vreApp+'" width="100%" height="1000" frameborder="0" marginheight="0"></iframe>');
    
});
