<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/gxt/css/gxt-all.css" />
<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/ReportGenerator.css" />

<script src='<%=request.getContextPath()%>/reports/js/jquery-1.10.1.min.js'></script>
<script src='<%=request.getContextPath()%>/reports/js/bootstrap.min.js'></script>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/reports/reports.nocache.js"></script>
<div id="ReportGeneratorDIV"></div>