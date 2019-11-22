<%@include file="../init.jsp"%>
<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="org.gcube.portlets.user.performfish.EmptyFormsDocumentsDisplay"%>
<liferay-portlet:actionURL portletConfiguration="true"
	var="configurationURL" />


<p class="lead">Here you should enter the FolderId of the shared folder where the documents are located and whether it shows
	Hatchery, Pre-ongrowing or Grow out documents.</p>
<%
	String selectedPhase = GetterUtil.getString(portletPreferences.getValue(PFISHConstants.PHASE_PREFERENCE_ATTR_NAME, StringPool.BLANK));

	String folderId = GetterUtil.getString(portletPreferences.getValue(EmptyFormsDocumentsDisplay.FOLDERID_PREFERENCE_ATTR_NAME, StringPool.BLANK));

	pageContext.setAttribute("SHOW_HATCHERY", PFISHConstants.SHOW_HATCHERY);
	pageContext.setAttribute("SHOW_PRE_ONGROWING", PFISHConstants.SHOW_PRE_ONGROWING);
	pageContext.setAttribute("SHOW_GROW_OUT", PFISHConstants.SHOW_GROW_OUT);
%>

<aui:form action="<%=configurationURL%>" method="post" name="fm">
	<aui:input name="<%=Constants.CMD%>" type="hidden"
		value="<%=Constants.UPDATE%>" />

	<aui:input name="preferences--folderId--" type="text"
		cssClass="text long-field" showRequiredLabel="true" label="Folder Id"
		inlineField="true" inlineLabel="left"
		placeholder="The Id of the folder containing the files to display"
		helpMessage="The Id of the folder containing the files to display"
		value="<%=folderId%>" required="true">
	</aui:input>
	<aui:input
		checked="<%= selectedPhase.equalsIgnoreCase(PFISHConstants.SHOW_HATCHERY) %>"
		label="Hatchery" name="preferences--phase--" type="radio"
		value="${SHOW_HATCHERY}"
		helpMessage="By selecting this the portlet will show only files pertaining to Hatchery" />
	<aui:input
		checked="<%= selectedPhase.equalsIgnoreCase(PFISHConstants.SHOW_PRE_ONGROWING) %>"
		label="Pre-ongrowing" name="preferences--phase--" type="radio"
		value="${SHOW_PRE_ONGROWING}"
		helpMessage="By selecting this the portlet will show only files pertaining to Pre-ongrowing" />
	<aui:input
		checked="<%= selectedPhase.equalsIgnoreCase(PFISHConstants.SHOW_GROW_OUT) %>"
		label="Grow out" name="preferences--phase--" type="radio"
		value="${SHOW_GROW_OUT}"
		helpMessage="By selecting this the portlet will show only files pertaining to Grow out" />
	<aui:button type="submit" value="Submit Preference" />
</aui:form>