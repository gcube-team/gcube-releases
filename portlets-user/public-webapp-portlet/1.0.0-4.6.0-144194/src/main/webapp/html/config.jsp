<%@include file="/html/init.jsp" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<%  
String appURL_cfg = GetterUtil.getString(portletPreferences.getValue("appURL", StringPool.BLANK));
String appURLTokenParam_cfg = GetterUtil.getString(portletPreferences.getValue("appURLTokenParam", StringPool.BLANK));
%>

<link rel="stylesheet" href="https://docs.atlassian.com/aui/5.5.1/sandbox/aui/css/aui-all.css" media="all">

<aui:form action="<%= configurationURL %>" method="post" name="fm">
    <aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />

	<!-- Application URL -->
	<aui:field-wrapper cssClass="field-group">
		<aui:input
			name="preferences--appURL--"
			type="text"
			cssClass="text long-field"
			showRequiredLabel="true"
			label="Application URL"
			inlineField="true"
			inlineLabel="left"
			placeholder="Application URL"
			helpMessage="URL of the web-application to embedd in the portlet"
			value="<%= appURL_cfg %>"
			required="true" />
	</aui:field-wrapper>
	
	<!-- Application URL Parameter name -->
	<aui:field-wrapper cssClass="field-group">
		<aui:input
			type="text"
			name="preferences--appURLTokenParam--"
			cssClass="text medium-field"
			label="Application URL Parameter name"
			inlineField="true"
			inlineLabel="left"
			placeholder="Parameter name"
			helpMessage="Name of the web-application URL Parameter used to inherit the VRE security token"
			value="<%= appURLTokenParam_cfg %>"/>
	</aui:field-wrapper>

	<aui:button-row>
        <aui:button type="submit" />
    </aui:button-row>

</aui:form>