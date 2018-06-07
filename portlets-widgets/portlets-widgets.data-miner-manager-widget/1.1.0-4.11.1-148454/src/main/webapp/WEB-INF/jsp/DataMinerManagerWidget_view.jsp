<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<!--                                           -->
<!-- The module reference below is the link    -->
<!-- between html and your Web Toolkit module  -->
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/dataminermanagerwidget/reset.css"
	type="text/css" />
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/DataMinerManagerWidget.css"
	type="text/css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/gxt/css/gxt-all.css"
	type="text/css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/dataminermanagerwidget/css/ol.css"
	type="text/css">

<script
	src='<%=request.getContextPath()%>/dataminermanagerwidget/js/jquery-1.11.0.min.js'></script>
<script src='<%=request.getContextPath()%>/dataminermanagerwidget/js/ol.js'></script>
<script
	src='<%=request.getContextPath()%>/dataminermanagerwidget/js/bootstrap.min.js'></script>
<script
	src='<%=request.getContextPath()%>/dataminermanagerwidget/dataminermanagerwidget.nocache.js'></script>


<div class="contentDiv" id="contentDiv"></div>
