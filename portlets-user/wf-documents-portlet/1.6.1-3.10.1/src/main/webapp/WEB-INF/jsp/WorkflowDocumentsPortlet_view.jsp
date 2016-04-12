<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>

 <link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/WorkflowDocuments.css">
  <link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/gxt/css/gxt-all.css">
 
<script type="text/javascript" language="javascript" src='<%=request.getContextPath()%>/workflowdocuments/workflowdocuments.nocache.js'></script>
<div id="workflowDocumentsDIV">
</div>