<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>
<link rel="stylesheet" href="<%=request.getContextPath()%>/GCubeCkanDataCatalog.css" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/gCubeCkanDataCatalog/gCubeCkanDataCatalog.nocache.js"></script>
<script type="text/javascript" src='<%=request.getContextPath()%>/js/jquery.min.js'></script>	
<script type="text/javascript" src='<%=request.getContextPath()%>/js/jquery.autosize.js'></script>
<div id="gCubeCkanDataCatalog"></div>