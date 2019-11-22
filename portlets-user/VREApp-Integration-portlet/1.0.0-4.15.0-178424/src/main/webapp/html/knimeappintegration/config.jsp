<%@include file="/html/init.jsp"%>

<liferay-portlet:actionURL portletConfiguration="true"
	var="configurationURL" />

<%
	String KNIMEAppURL_cfg = GetterUtil.getString(portletPreferences.getValue("KNIMEAppURL", StringPool.BLANK));
	String KNIMEAppURLTokenParam_cfg = GetterUtil
			.getString(portletPreferences.getValue("KNIMEAppURLTokenParam", StringPool.BLANK));
	Integer KNIMEiFrameHeightParam_cfg = GetterUtil
			.getInteger(portletPreferences.getValue("KNIMEiFrameHeightParam", "1000"));
	boolean KNIMEWindowPreference_cfg = GetterUtil
			.getBoolean(portletPreferences.getValue("KNIMEWindowPreference", StringPool.FALSE));
	String applicationNameParam_cfg = GetterUtil
			.getString(portletPreferences.getValue("applicationNameParam", "the Application"));
	if (applicationNameParam_cfg.equals(""))
		applicationNameParam_cfg = "the Application";
%>

<aui:form action="<%=configurationURL%>" method="post" name="fm">
	<aui:input name="<%=Constants.CMD%>" type="hidden"
		value="<%=Constants.UPDATE%>" />

	<!-- Application URL -->
	<aui:field-wrapper cssClass="field-group">
		<aui:input style="width: 100%;" name="preferences--KNIMEAppURL--"
			type="text" cssClass="text long-field" showRequiredLabel="true"
			label="KNIME Application URL" inlineField="true" inlineLabel="left"
			placeholder="KNIME Application URL"
			helpMessage="Actual endpoint of the external KNIME App (must start with https://)"
			value="<%=KNIMEAppURL_cfg%>" required="true" />
	</aui:field-wrapper>

	<!-- Application URL Parameter name -->
	<aui:field-wrapper cssClass="field-group">
		<aui:input type="text" name="preferences--KNIMEAppURLTokenParam--"
			cssClass="text medium-field" label="KNIME Application URL Security Token Parameter name"
			inlineField="true" inlineLabel="left" placeholder="Security Token Parameter name"
			helpMessage="Name of the Security Token Parameter (e.g gcube-token) expected by the web-application in the URL"
			value="<%=KNIMEAppURLTokenParam_cfg%>" />
	</aui:field-wrapper>
	<!-- Application URL Parameter name -->
	<aui:field-wrapper cssClass="field-group">
		<p class="lead">Display options (default iFrame):</p>
		<aui:input type="text" name="preferences--KNIMEiFrameHeightParam--"
			cssClass="text medium-field"
			label="iFrame height as integer in pixels" inlineField="true"
			inlineLabel="left" placeholder="in Pixels"
			helpMessage="Enter the height in pixels of the iFrame Height (default 1000)"
			value="<%=KNIMEiFrameHeightParam_cfg%>" />

	</aui:field-wrapper>
	<aui:field-wrapper cssClass="field-group">
		<aui:input name="preferences--KNIMEWindowPreference--" type="checkbox"
			label="Use a new tab instead of the iFrame"
			helpMessage="If checked will use a new tab instead of the iFrame (beware of the popup blocker)"
			value="<%=KNIMEWindowPreference_cfg%>" />
		<aui:input type="text" name="preferences--applicationNameParam--"
			cssClass="text medium-field"
			label="In case of new window enter the App name" inlineField="true"
			inlineLabel="left" placeholder="The name of the application (e.g. Galaxy, ShareLatex)"
			helpMessage="Enter the name of the application (e.g. Galaxy, ShareLatex etc.)"
			value="<%=applicationNameParam_cfg%>" />

	</aui:field-wrapper>
	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>

</aui:form>