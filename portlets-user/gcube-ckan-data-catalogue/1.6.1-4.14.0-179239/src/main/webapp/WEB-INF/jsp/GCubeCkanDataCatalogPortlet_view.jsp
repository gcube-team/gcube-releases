<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>
<script
	src='<%=request.getContextPath()%>/gCubeCkanDataCatalog/js/jquery-1.10.1.min.js'></script>

<script
	src='<%=request.getContextPath()%>/gCubeCkanDataCatalog/js/bootstrap.min.js'></script>

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/GCubeCkanDataCatalog.css"
	type="text/css">

<link type="text/css" rel="stylesheet"
	href="<%=request.getContextPath()%>/gCubeCkanDataCatalog/css/ol.css">

<script type="text/javascript"
	src="<%=request.getContextPath()%>/gCubeCkanDataCatalog/js/ol.js"></script>

<script type="text/javascript"
	src="<%=request.getContextPath()%>/gCubeCkanDataCatalog/gCubeCkanDataCatalog.nocache.js"></script>

<script type="text/javascript"
	src='<%=request.getContextPath()%>/js/jquery.min.js'></script>
	
<script type="text/javascript"
	src='<%=request.getContextPath()%>/js/jquery.autosize.js'></script>


<div id="gCubeCkanDataCatalog"></div>