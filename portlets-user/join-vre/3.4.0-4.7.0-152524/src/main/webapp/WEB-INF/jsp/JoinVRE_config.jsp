<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portlet.PortletPreferencesFactoryUtil"%>

<%@page import="javax.portlet.PortletPreferences"%>

<%@page import="org.gcube.portlets.user.joinvre.server.portlet.JoinVREConfigurationActionImpl"%>

<%
String portletResource = ParamUtil.getString(request, JoinVREConfigurationActionImpl.PORTLET_RESOURCE);
PortletPreferences preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletResource);
String properties = preferences.getValue(JoinVREConfigurationActionImpl.PROPERTIES, StringPool.BLANK);
%>

<portlet:defineObjects />

<form action="<liferay-portlet:actionURL portletConfiguration="true" />" method="post" name="<portlet:namespace />fm"> 
	
	<textarea rows="10" name="<portlet:namespace /><%= JoinVREConfigurationActionImpl.PROPERTIES %>">
		<%= properties %>
	</textarea>
	
	<input type="button" value="Save" onClick="submitForm(document.<portlet:namespace />fm);" />
	
</form>