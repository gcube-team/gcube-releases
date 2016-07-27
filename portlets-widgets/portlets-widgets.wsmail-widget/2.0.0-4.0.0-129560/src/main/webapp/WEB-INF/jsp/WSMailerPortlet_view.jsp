<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>
<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/WsMail_Widget.css" />
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/wsmail_widget/wsmail_widget.nocache.js"></script>
<div id="wsmailer"></div>