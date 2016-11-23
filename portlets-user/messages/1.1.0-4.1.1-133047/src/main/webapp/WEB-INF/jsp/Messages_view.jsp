<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<!--                                           -->
<!-- The module reference below is the link    -->
<!-- between html and your Web Toolkit module  -->
<script src='<%=request.getContextPath()%>/messages/js/jquery-1.10.1.min.js'></script>
<script src='<%=request.getContextPath()%>/messages/js/bootstrap.min.js'></script>

<script src='<%=request.getContextPath()%>/messages/messages.nocache.js'></script>

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/Messages.css" type="text/css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/gxt/css/gxt-all.css"
	type="text/css">


<div id="MESSAGES_DIV" style="width: 100%; height: 100%"></div>
