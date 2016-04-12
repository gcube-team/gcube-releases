<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>

<script src="<%=request.getContextPath()%>/searchportletg/pagebus.js" language="JavaScript1.2"></script>

<script type="text/javascript">
	if(window.parent.PageBus) {
		window.PageBus = window.parent.PageBus;
	}
</script>

<script type="text/javascript" language="javascript" src='<%=request.getContextPath()%>/searchportletg/searchportletg.nocache.js'></script>

<div id="SearchDIV"></div>
<div style="display:none;" id="sessionID"><%= session.getId() %></div>
<div style="display:none;" id="searchActionURL"><portlet:actionURL></portlet:actionURL></div>