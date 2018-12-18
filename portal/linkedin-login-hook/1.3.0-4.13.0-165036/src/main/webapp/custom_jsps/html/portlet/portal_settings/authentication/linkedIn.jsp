<!-- 

A configuration page for client id and client secret, in case they need to be changed.
Additionally there is a checkbox to disable linkedin hook.

-->


<%@ include file="/html/portlet/portal_settings/init.jsp" %>

<%

boolean isLinkedInAuthEnabled = PrefsPropsUtil.getBoolean(company.getCompanyId(), "linkedIn.auth.enabled", true);
String linkedInClientId = PrefsPropsUtil.getString(company.getCompanyId(), "linkedIn.client.id");
String linkedInClientSecret = PrefsPropsUtil.getString(company.getCompanyId(), "linkedIn.client.secret");

%>

<aui:fieldset>
	<aui:input label="enabled" name='<%= "settings--linkedIn.auth.enabled--" %>' type="checkbox" value="<%= isLinkedInAuthEnabled %>" />

	<aui:input cssClass="lfr-input-text-container" label="linkedIn-client-id" name='<%= "settings--linkedIn.client.id--" %>' type="text" value="<%= linkedInClientId %>" />

	<aui:input cssClass="lfr-input-text-container" label="linkedIn-client-secret" name='<%= "settings--linkedIn.client.secret--" %>' type="text" value="<%= linkedInClientSecret %>" />
</aui:fieldset>