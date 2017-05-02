<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>
<script type="text/javascript" src="<%=request.getContextPath()%>/notifications/js/jquery-1.10.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/notifications/js/bootstrap.min.js"></script>

<script type="text/javascript" language="javascript" src='<%=request.getContextPath()%>/notifications/notifications.nocache.js'></script>
<div id="notificationsDIV"></div>



