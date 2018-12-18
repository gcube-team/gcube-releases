<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
 

<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>

<portlet:defineObjects />

<%  
String displayName = GetterUtil.getString(portletPreferences.getValue("displayName", StringPool.BLANK));
%>

<div class="container">
	<script type="text/javascript" language="javascript" src='<%=request.getContextPath()%>/Survey/Survey.nocache.js'></script>
	<h2 id="#surveyAdminTitleHeading">Create or Manage <%= displayName %></h2>
	<div id="survey-div"></div>
</div>

