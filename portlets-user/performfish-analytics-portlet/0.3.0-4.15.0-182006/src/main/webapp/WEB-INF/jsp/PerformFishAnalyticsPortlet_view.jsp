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

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/PerformFishAnalytics.css"
	type="text/css">

<script type="text/javascript"
	src="<%=request.getContextPath()%>/PerformFishAnalytics/js/jquery-1.10.1.min.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/PerformFishAnalytics/js/bootstrap.min.js"></script>

<script type="text/javascript"
	src="<%=request.getContextPath()%>/PerformFishAnalytics/PerformFishAnalytics.nocache.js"></script>
<div id="perform-fish-analytics" style="width: 100%; height: 100%;"></div>