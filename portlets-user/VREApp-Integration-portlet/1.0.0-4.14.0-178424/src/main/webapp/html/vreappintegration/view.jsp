<%@include file="/html/init.jsp"%>

<!-- Inherit Generic public web-app portlet configuration parameters -->
<%
	String appURL_view = GetterUtil.getString(portletPreferences.getValue("appURL", StringPool.BLANK));
	String appURLTokenParam_view = GetterUtil
			.getString(portletPreferences.getValue("appURLTokenParam", StringPool.BLANK));
	Integer iFrameHeight = GetterUtil.getInteger(portletPreferences.getValue("iFrameHeightParam", "1000"));
	pageContext.setAttribute("iFrameHeight", iFrameHeight);
	
	boolean newWindowPreference = GetterUtil.getBoolean(portletPreferences.getValue("newWindowPreference", StringPool.FALSE));
	pageContext.setAttribute("newWindowPreference", newWindowPreference);
	
	String applicationNameParam_view = GetterUtil.getString(portletPreferences.getValue("applicationNameParam", "the Application"));
	pageContext.setAttribute("applicationNameParam_view", applicationNameParam_view);

	Object securityTokenObj = request.getAttribute("securityToken");
	String securityToken = "";
	if (securityToken != null) {
		securityToken = securityTokenObj.toString();
	}

	/* handle the case where the page is called with GET parameters needing to be forwarded*/
	String completeURL = PortalUtil.getCurrentCompleteURL(request);
	String queryString = "";
	if (completeURL.indexOf("?") > 0) {
		queryString = completeURL.substring(completeURL.indexOf("?") + 1);
		queryString = queryString.trim();
	}

	/* handle the case where the appURL provided has GET parameters needing to be forwarded*/
	if (appURL_view.indexOf("?") > 0) {
		if (queryString != null && !queryString.equals("")) { //not empty
			queryString += "&" + appURL_view.substring(appURL_view.indexOf("?") + 1);
		} else {
			queryString = appURL_view.substring(appURL_view.indexOf("?") + 1);
		}
		appURL_view = appURL_view.substring(0, appURL_view.indexOf("?"));
		queryString = queryString.trim();
	}

	String applicationURL = appURL_view;
	if (!appURLTokenParam_view.equals("")) {
		applicationURL += "?" + appURLTokenParam_view + "=" + securityToken;
		if (queryString != null && !queryString.equals("")) {
			applicationURL += "&" + queryString;
		}
	} else {
		if (queryString != null && !queryString.equals("")) {
			applicationURL += "?" + queryString;
		}
	}
	pageContext.setAttribute("applicationURL", applicationURL);
	pageContext.setAttribute("newWindow", true);
%>
<c:choose>
	<c:when test="${not newWindowPreference}">
		<iframe id="iFrameProxy" src="${applicationURL}" width="100%"
			marginwidth="0" marginheight="0" frameborder="0"
			height="${iFrameHeight}" style="overflow-x: hidden;"> </iframe>
	</c:when>
	<c:otherwise>
		<p class="lead">If no new window appears, please click <a href="${applicationURL}" target="_blank">here</a> to open <a href="${applicationURL}" target="_blank">${applicationNameParam_view}</a></p>
		<script>
			window.open('${applicationURL}');
		</script>
	</c:otherwise>
</c:choose>
