<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<link href='https://fonts.googleapis.com/css?family=Architects+Daughter'
	rel='stylesheet' type='text/css'>
<script type="text/javascript" language="javascript"
	src="<%=request.getContextPath()%>/statistics/statistics.nocache.js"></script>
<script type="text/javascript">
	if (window.parent.PageBus) {
		window.PageBus = window.parent.PageBus;
	}
</script>
<div id="statistics-container"></div>