<%@ page contentType="text/html"%>

<link rel="stylesheet" href="<%=request.getContextPath()%>/js/base/reset.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/vendor.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/app.css">

<script src="<%=request.getContextPath()%>/js/i18n.js" data-baseDir="<%=request.getContextPath()%>/js/lang"
	data-langs="en"></script>
<script src="<%=request.getContextPath()%>/js/vendor.js"></script>
<script src="<%=request.getContextPath()%>/js/app.js"></script>
<script>
			window.onload = function() {
				if(location.href.contains("/_plugin/")) {
					var base_uri = location.href.replace(/_plugin\/.*/, '');
				}
				var args = location.search.substring(1).split("&").reduce(function(r, p) {
					r[decodeURIComponent(p.split("=")[0])] = decodeURIComponent(p.split("=")[1]); return r;
				}, {});
				new app.App("#elasticSearchPortletContainer", {
					id: "es",
					base_uri: args["base_uri"] || base_uri,
					auth_user : args["auth_user"] || "",
					auth_password : args["auth_password"],
					dashboard: args["dashboard"]
				});
			};
		</script>

<div id="elasticSearchPortletContainer"></div>