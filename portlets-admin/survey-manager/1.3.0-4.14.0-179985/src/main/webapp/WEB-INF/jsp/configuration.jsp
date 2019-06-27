<%@include file="init.jsp" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

Here you can customise whether this portlet should manage surveys
(default) or questionairre (quiz). It changes the label

<%
	String displayName_cfg = GetterUtil.getString(portletPreferences.getValue("displayName", StringPool.BLANK));
%>

<aui:form action="<%= configurationURL %>" method="post" name="fm">
    <aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />

	<!-- Application URL -->
	<aui:field-wrapper cssClass="field-group">
		<aui:input name="preferences--displayName--" type="text"
			cssClass="text long-field" showRequiredLabel="true"
			label="Display Name" inlineField="true" inlineLabel="left"
			placeholder="Survey"
			helpMessage="Name of the questionaire type, e.g. Survey, Quiz, Questionairre"
			value="<%= displayName_cfg %>" required="false" />
	</aui:field-wrapper>

	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>