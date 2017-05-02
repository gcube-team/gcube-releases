<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>

<link href='https://fonts.googleapis.com/css?family=Architects+Daughter' rel='stylesheet' type='text/css'>
<script type="text/javascript">
     if(window.parent.PageBus) {
     window.PageBus = window.parent.PageBus;
   }
   </script>

<script type="text/javascript" language="javascript"
	src='<%=request.getContextPath()%>/newsfeed/newsfeed.nocache.js'></script>
	
<script type="text/javascript" src='<%=request.getContextPath()%>/js/jquery.min.js'></script>	
<script type="text/javascript" src='<%=request.getContextPath()%>/js/jquery.autosize.js'></script>

<div id="newsfeedDIV"></div>
