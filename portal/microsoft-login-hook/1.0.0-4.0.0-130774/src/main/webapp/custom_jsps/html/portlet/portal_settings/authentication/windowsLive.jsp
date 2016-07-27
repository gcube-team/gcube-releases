<%
/** 
*A configuration page for client id and client secret, in case they need to be changed.
*Additionally there is a checkbox to disable windows live hook.
*/
%>


<%@ include file="/html/portlet/portal_settings/init.jsp" %>

<%

boolean windowsLiveAuthEnabled = PrefsPropsUtil.getBoolean(company.getCompanyId(), "windowsLive.auth.enabled", true);
String windowsLiveClientId = PrefsPropsUtil.getString(company.getCompanyId(), "windowsLive.client.id");
String windowsLiveClientSecret = PrefsPropsUtil.getString(company.getCompanyId(), "windowsLive.client.secret");

%>

<aui:fieldset>
	<aui:input  helpMessage="windowsLive-enable" label="enabled" name='<%= "settings--windowsLive.auth.enabled--" %>' type="checkbox" value="<%= windowsLiveAuthEnabled %>" />
	<aui:input cssClass="input-xxlarge" helpMessage="windowsLive-client-id-helpMessage" label="windowsLive-client-id" name='<%= "settings--windowsLive.client.id--" %>' type="text" value="<%= windowsLiveClientId %>" />
	<aui:input cssClass="input-xxlarge" helpMessage="windowsLive-client-secret-helpMessage" label="windowsLive-client-secret" name='<%= "settings--windowsLive.client.secret--" %>' type="text" value="<%= windowsLiveClientSecret %>" />
</aui:fieldset>