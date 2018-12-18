<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>
 <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/gxt/css/gxt-all.css" />
 <link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/Vredeployer.css">
 
 <script  type="text/javascript"  language='javascript' src='<%=request.getContextPath()%>/gxt/flash/swfobject.js'></script> 

<script type="text/javascript" language="javascript" src='<%=request.getContextPath()%>/vredeployer/vredeployer.nocache.js'></script>
<div id="DeployerDIV">
</div>