<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
 
<!--                                           -->
<!-- The module reference below is the link    -->
<!-- between html and your Web Toolkit module  -->		                                      
<link rel="stylesheet" href="<%= request.getContextPath()%>/statalgoimporter/reset.css" type="text/css"/>
<!-- <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/gxt/css/gxt-all.css" /> -->
<link rel="stylesheet" href="<%= request.getContextPath()%>/StatAlgoImporter.css" type="text/css">

<script src='<%=request.getContextPath()%>/statalgoimporter/js/jquery-1.11.3.min.js'></script>
<script src='<%=request.getContextPath()%>/statalgoimporter/ace/ace.js' type='text/javascript' charset='utf-8'></script>
<script src='<%=request.getContextPath()%>/statalgoimporter/ace/theme-eclipse.js' type='text/javascript' charset='utf-8'></script>
<script src='<%=request.getContextPath()%>/statalgoimporter/ace/mode-r.js' type='text/javascript' charset='utf-8'></script>
<script src='<%=request.getContextPath()%>/statalgoimporter/ace/mode-sh.js' type='text/javascript' charset='utf-8'></script>
<script src='<%=request.getContextPath()%>/statalgoimporter/js/bootstrap.min.js'></script>


<script src='<%=request.getContextPath()%>/statalgoimporter/statalgoimporter.nocache.js'></script>


<div id="StatAlgoImporterPortlet" style="width: 100%; height: 100%">
</div>

 