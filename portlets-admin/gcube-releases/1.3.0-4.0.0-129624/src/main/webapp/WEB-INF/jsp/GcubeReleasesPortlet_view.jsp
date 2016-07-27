<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>

<!-- <link rel="stylesheet" -->
<%-- 	href="<%=request.getContextPath()%>/resources/css/gxt-all.css" --%>
<!-- 	type="text/css"> -->

<link rel="stylesheet" href="<%=request.getContextPath()%>/GcubeReleasesApp.css" type="text/css">

<script type="text/javascript" src="<%=request.getContextPath()%>/GcubeReleasesApp/js/jquery-1.10.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/GcubeReleasesApp/js/bootstrap.min.js"></script>


<script type="text/javascript" src="<%=request.getContextPath()%>/GcubeReleasesApp/GcubeReleasesApp.nocache.js"></script>
<div id="buildreportmanager"></div>