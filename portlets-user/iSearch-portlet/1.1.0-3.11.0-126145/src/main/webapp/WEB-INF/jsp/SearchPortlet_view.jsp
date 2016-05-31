<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>

<script type="text/javascript" language="javascript" src='<%=request.getContextPath()%>/searchportlet/searchportlet.nocache.js'></script>

<div id="SearchDIV"></div>
<div style="display:none;" id="sessionID"><%= session.getId() %></div>
<div style="display:none;" id="searchActionURL"><portlet:actionURL></portlet:actionURL></div>