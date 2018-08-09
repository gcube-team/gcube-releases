<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>
    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
	<link href='http://fonts.googleapis.com/css?family=Reenie+Beanie' rel='stylesheet' type='text/css'>
    <link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/xmlverbatim.css">
    <link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/gxt/css/gxt-all.css" />
    <link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ResourceManagementPortlet.css" />
    

<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/resourcemanagementportlet/resourcemanagementportlet.nocache.js"></script>
<div id="MyUniqueDIV" class="portlet-wrapper"></div>
