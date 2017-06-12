<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
 
<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>


<div class="container">
	<script type="text/javascript" language="javascript" src='<%=request.getContextPath()%>/Survey/Survey.nocache.js'></script>
	<h2 id="#surveyAdminTitleHeading">Survey Admin</h2>
	<div id="survey-div"></div>
</div>

