<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<!--                                           -->
<!-- The module reference below is the link    -->
<!-- between html and your Web Toolkit module  -->

<script
	src='<%=request.getContextPath()%>/TrainingCourse/js/jquery-1.10.1.min.js'></script>
<script
	src='<%=request.getContextPath()%>/TrainingCourse/js/bootstrap.min.js'></script>
<script
	src='<%=request.getContextPath()%>/TrainingCourse/TrainingCourse.nocache.js'></script>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/gxt-all.css"
	type="text/css">
<link rel="stylesheet"
	href="<%= request.getContextPath()%>/TrainingCourse.css"
	type="text/css">

<div id="training-course"></div>
