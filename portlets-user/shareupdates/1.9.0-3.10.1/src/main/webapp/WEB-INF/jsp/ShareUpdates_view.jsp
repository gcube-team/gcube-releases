<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>

<script type="text/javascript">
     if(window.parent.PageBus) {
     window.PageBus = window.parent.PageBus;
   }
   </script>
<script type="text/javascript" src="<%=request.getContextPath()%>/shareupdates/js/jquery-1.10.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/shareupdates/js/bootstrap.min.js"></script>
<script type="text/javascript" 
	src='<%=request.getContextPath()%>/shareupdates/shareupdates.nocache.js'></script>

<script type="text/javascript" src='<%=request.getContextPath()%>/js/jquery.min.js'></script>	
<script type="text/javascript" src='<%=request.getContextPath()%>/js/jquery.autosize.js'></script>
<script>
		$(document).ready(function(){ 
		    setTimeout(function(){ 
		        $('.postTextArea').autosize();
		    }, 3000); 
		});
		</script>
<div id="shareUpdateDiv"></div>