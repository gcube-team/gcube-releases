<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%-- Uncomment below lines to add portlet taglibs to jsp
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />
--%>

<!-- <link rel="stylesheet" -->
<%-- 	href="<%=request.getContextPath()%>/resources/css/gxt-all.css" --%>
<!-- 	type="text/css"> -->

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/GcubeReleasesApp.css"
	type="text/css">

<script type="text/javascript"
	src="<%=request.getContextPath()%>/GcubeReleasesApp/js/jquery-1.10.1.min.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/GcubeReleasesApp/js/bootstrap.min.js"></script>
<script type="text/javascript">
	changeVisibility = function(ext_href_id, inner_div, label_to_show) {
		var e = document.getElementById(inner_div);
		var vis = e.style.display;

		if (vis == 'block') {
			e.style.display = 'none';
			document.getElementById(ext_href_id).innerHTML = "Show "
					+ label_to_show;
		} else {
			e.style.display = 'block';
			document.getElementById(ext_href_id).innerHTML = "Hide "
					+ label_to_show;
		}
	}
</script>

<script type="text/javascript"
	src="<%=request.getContextPath()%>/GcubeReleasesApp/GcubeReleasesApp.nocache.js"></script>
<div id="buildreportmanager"></div>