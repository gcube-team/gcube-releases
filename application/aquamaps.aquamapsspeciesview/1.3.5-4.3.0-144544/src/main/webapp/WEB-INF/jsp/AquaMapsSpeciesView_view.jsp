<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/gxt-all.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/css/resources.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/AquaMapsSpeciesView.css" />
<script type="text/javascript" language="javascript" src='<%=request.getContextPath()%>/aquamapsspeciesview/aquamapsspeciesview.nocache.js'></script>
<script src='<%=request.getContextPath()%>/aquamapsspeciesview/js/jquery-1.10.1.min.js'></script>
<script src='<%=request.getContextPath()%>/aquamapsspeciesview/js/bootstrap.min.js'></script>
<div id="SPECIES_VIEW">
</div>
