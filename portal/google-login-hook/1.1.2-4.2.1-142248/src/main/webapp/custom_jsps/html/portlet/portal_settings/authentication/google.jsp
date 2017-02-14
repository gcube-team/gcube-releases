<!-- 

A configuration page for client id and client secret, in case they need to be changedl.
Additionally there is a checkbox to disable google hook.

-->


<%@ include file="/html/portlet/portal_settings/init.jsp" %>

<%

boolean isGoogleAuthEnabled = PrefsPropsUtil.getBoolean(company.getCompanyId(), "google.auth.enabled", true);
String googleClientId = PrefsPropsUtil.getString(company.getCompanyId(), "google.client.id");
String googleClientSecret = PrefsPropsUtil.getString(company.getCompanyId(), "google.client.secret");

%>

<aui:fieldset>
	<aui:input helpMessage="google-enable" label="enabled" name='<%= "settings--google.auth.enabled--" %>' type="checkbox" value='<%= isGoogleAuthEnabled %>' />

	<aui:input cssClass="input-xxlarge" helpMessage="google-client-id-helpMessage" label="google-client-id" name='<%= "settings--google.client.id--" %>' type="text" value="<%= googleClientId %>" />

	<aui:input cssClass="input-xxlarge" helpMessage="google-client-secret-helpMessage" label="google-client-secret" name='<%= "settings--google.client.secret--" %>' type="text" value="<%= googleClientSecret %>" />
</aui:fieldset>