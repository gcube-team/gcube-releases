<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
 
<!--                                           -->
<!-- The module reference below is the link    -->
<!-- between html and your Web Toolkit module  -->		
                                           
<script src='<%=request.getContextPath()%>/geoexplorerportlet/geoexplorerportlet.nocache.js'></script>
<script src='<%=request.getContextPath()%>/geoexplorerportlet/js/jquery-1.10.1.min.js'></script>
<script src='<%=request.getContextPath()%>/geoexplorerportlet/js/bootstrap.min.js'></script>
<link rel="stylesheet" href="<%= request.getContextPath()%>/ExtGWT/css/gxt-all.css" type="text/css">  
<link rel="stylesheet" href="<%= request.getContextPath()%>/GeoExplorerPortlet.css" type="text/css">

 
<div class="geoExplorerPortlet-content" id="geoExplorer-content"></div>
