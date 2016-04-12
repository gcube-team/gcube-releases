<%@ page contentType="text/html" %>
<%@page import="java.util.*"%>

<!--                                           -->
<!-- The module reference below is the link    -->
<!-- between html and your Web Toolkit module  -->		
<!--                                           -->

<div style="text-align: center; position: relative; top: 100px;">
	<img style="vertical-align: middle" src="<%= request.getContextPath()%>/images/loading.gif"></img>
	<div style="vertical-align: middle; display: inline">Loading collections, please wait...</div>
</div>

<script language="javascript">
setTimeout('window.location.href=\"<%=request.getAttribute("actionURL")%>\";', 500);
</script>