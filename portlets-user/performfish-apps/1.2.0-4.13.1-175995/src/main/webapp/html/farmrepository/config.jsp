<%@include file="../init.jsp"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<liferay-portlet:actionURL portletConfiguration="true"
	var="configurationURL" />


<p class="lead">Here you can customise the documents (whether
	Hatchery, Pre-ongrowing, Grow out or all three) this portlet will have
	to show.</p>
<%
	String selectedPhase = GetterUtil.getString(portletPreferences.getValue("phase", StringPool.BLANK));

	pageContext.setAttribute("SHOW_ALL_PHASES", PFISHConstants.SHOW_ALL_PHASES);
	pageContext.setAttribute("SHOW_HATCHERY", PFISHConstants.SHOW_HATCHERY);
	pageContext.setAttribute("SHOW_PRE_ONGROWING", PFISHConstants.SHOW_PRE_ONGROWING);
	pageContext.setAttribute("SHOW_GROW_OUT", PFISHConstants.SHOW_GROW_OUT);
%>

<aui:form action="<%=configurationURL%>" method="post" name="fm">
	<aui:input name="<%=Constants.CMD%>" type="hidden"
		value="<%=Constants.UPDATE%>" />
	<aui:input checked="<%= selectedPhase.equalsIgnoreCase(PFISHConstants.SHOW_HATCHERY) %>" label="Hatchery" name="preferences--phase--" type="radio" value="${SHOW_HATCHERY}" helpMessage="By selecting this the portlet will show only files pertaining to Hatchery"/>
 	<aui:input checked="<%= selectedPhase.equalsIgnoreCase(PFISHConstants.SHOW_PRE_ONGROWING) %>" label="Pre-ongrowing" name="preferences--phase--" type="radio" value="${SHOW_PRE_ONGROWING}" helpMessage="By selecting this the portlet will show only files pertaining to Pre-ongrowing"/>
 	<aui:input checked="<%= selectedPhase.equalsIgnoreCase(PFISHConstants.SHOW_GROW_OUT) %>" label="Grow out" name="preferences--phase--" type="radio" value="${SHOW_GROW_OUT}" helpMessage="By selecting this the portlet will show only files pertaining to Grow out"/>
 	<aui:input checked="<%= selectedPhase.equalsIgnoreCase(PFISHConstants.SHOW_ALL_PHASES) %>" label="All of the 3 above" name="preferences--phase--" type="radio" value="${SHOW_ALL_PHASES}" helpMessage="By selecting this the portlet will show every file of the phases above"/>
	<aui:button type="submit" value="Submit Preference"/>
</aui:form>