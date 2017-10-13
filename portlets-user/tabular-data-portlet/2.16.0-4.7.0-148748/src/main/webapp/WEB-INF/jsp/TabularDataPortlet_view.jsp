<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
 

<!--                                           -->
<!-- The module reference below is the link    -->
<!-- between html and your Web Toolkit module  -->		                                      
<link rel="stylesheet" href="<%= request.getContextPath()%>/tabulardataportlet/reset.css" type="text/css"/>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/gxt/css/gxt-all.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/gxt2/css/gxt-all.css" />
<link rel="stylesheet" href="<%= request.getContextPath()%>/tabulardataportlet.css" type="text/css">
<script src='<%=request.getContextPath()%>/tabulardataportlet/js/jquery-1.10.1.min.js'></script>
<script src='<%=request.getContextPath()%>/tabulardataportlet/tabulardataportlet.nocache.js'></script>

<div id="tdp" style="width: 100%; height: 100%">
</div>

 