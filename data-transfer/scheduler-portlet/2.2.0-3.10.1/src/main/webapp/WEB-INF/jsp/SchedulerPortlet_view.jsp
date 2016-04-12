<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>
<!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/gxt/css/gxt-all.css">
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/SchedulerPortlet.css">
    
    <script type="text/javascript" language="javascript" src='<%=request.getContextPath()%>/schedulerportletmodule/schedulerportletmodule.nocache.js'></script>
	
<div id="mainContainer"></div>