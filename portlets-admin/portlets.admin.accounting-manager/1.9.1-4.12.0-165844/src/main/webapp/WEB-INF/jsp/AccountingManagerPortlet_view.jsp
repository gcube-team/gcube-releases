<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
 
<!--                                           -->
<!-- The module reference below is the link    -->
<!-- between html and your Web Toolkit module  -->		                                      
<link rel="stylesheet" href="<%= request.getContextPath()%>/accountingman/reset.css" type="text/css"/>
<!-- <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/gxt/css/gxt-all.css" /> -->
<link rel="stylesheet" href="<%= request.getContextPath()%>/AccountingManager.css" type="text/css">
<script src='<%=request.getContextPath()%>/accountingman/js/jquery-1.11.0.min.js'></script>
<script src='<%=request.getContextPath()%>/accountingman/js/highcharts.js'></script>
<script src='<%=request.getContextPath()%>/accountingman/js/highcharts-3d.js'></script>
<script src='<%=request.getContextPath()%>/accountingman/js/highcharts-more.js'></script>
<script src='<%=request.getContextPath()%>/accountingman/js/modules/exporting.js'></script>

<script src='<%=request.getContextPath()%>/accountingman/accountingman.nocache.js'></script>


<div id="AccountingManagerPortlet" style="width: 100%; height: 100%">
</div>

 