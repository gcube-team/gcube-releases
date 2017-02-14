<%
/* This jsp creates the tab in the authetication menu for the linkedIn  
 */%>


<%@ include file="/html/portlet/portal_settings/init.jsp" %>

<%
boolean isShibbolethAuthEnabled = PrefsPropsUtil.getBoolean(company.getCompanyId(), "shibboleth.auth.enabled", true);
String emailAttribute = PrefsPropsUtil.getString(company.getCompanyId(), "shibboleth.email.attribute");
String forenameAttribute = PrefsPropsUtil.getString(company.getCompanyId(), "shibboleth.givenName.attribute");
String surnameAttribute = PrefsPropsUtil.getString(company.getCompanyId(), "shibboleth.sn.attribute");
%>

<aui:fieldset>
	<aui:input helpMessage="shibboleth-enable" label="enabled" name='<%= "settings--shibboleth.auth.enabled--" %>' type="checkbox" value='<%= isShibbolethAuthEnabled %>' />
	
	<aui:input cssClass="input-xxlarge" helpMessage="shibboleth-email-attribute-helpMessage" label="shibboleth-email-attribute" name='<%= "settings--shibboleth.email.attribute--" %>' type="text" value="<%= emailAttribute %>" />
	
	<aui:input cssClass="input-xxlarge" helpMessage="shibboleth-givenName-attribute-helpMessage" label="shibboleth-givenName-attribute" name='<%= "settings--shibboleth.givenName.attribute--" %>' type="text" value="<%= forenameAttribute %>" />
	
	<aui:input cssClass="input-xxlarge" helpMessage="shibboleth-sn-attribute-helpMessage" label="shibboleth-sn-attribute" name='<%= "settings--shibboleth.sn.attribute--" %>' type="text" value="<%= surnameAttribute %>" />
</aui:fieldset>