<%@include file="/html/init.jsp"%>

<!-- Inherit Generic public web-app portlet configuration parameters -->
<%
	String KNIMEAppURL_view = GetterUtil.getString(portletPreferences.getValue("KNIMEAppURL", StringPool.BLANK));
	String KNIMEAppURLTokenParam_view = GetterUtil
			.getString(portletPreferences.getValue("KNIMEAppURLTokenParam", StringPool.BLANK));
	Integer iFrameHeight = GetterUtil.getInteger(portletPreferences.getValue("KNIMEiFrameHeightParam", "1000"));
	pageContext.setAttribute("iFrameHeight", iFrameHeight);
	
	boolean KNIMEWindowPreference = GetterUtil.getBoolean(portletPreferences.getValue("KNIMEWindowPreference", StringPool.FALSE));
	pageContext.setAttribute("KNIMEWindowPreference", KNIMEWindowPreference);
	
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

	/* handle the case where the KNIMEAppURL provided has GET parameters needing to be forwarded*/
	if (KNIMEAppURL_view.indexOf("&") > 0) {
		if (queryString != null && !queryString.equals("")) { //not empty
			queryString += "&" + KNIMEAppURL_view.substring(KNIMEAppURL_view.indexOf("&") + 1);
		} else {
			queryString = KNIMEAppURL_view.substring(KNIMEAppURL_view.indexOf("&") + 1);
		}
		KNIMEAppURL_view = KNIMEAppURL_view.substring(0, KNIMEAppURL_view.indexOf("&"));
		queryString = queryString.trim();
	}

	String applicationURL = KNIMEAppURL_view;
	if (!KNIMEAppURLTokenParam_view.equals("")) {
		applicationURL += "&" + KNIMEAppURLTokenParam_view + "=" + securityToken;
		if (queryString != null && !queryString.equals("")) {
			applicationURL += "&" + queryString;
		}
	} else {
		if (queryString != null && !queryString.equals("")) {
			applicationURL += "&" + queryString;
		}
	}
	System.out.println("applicationURL: "+applicationURL);
	pageContext.setAttribute("applicationURL", applicationURL);
	pageContext.setAttribute("newWindow", true);
%>
<c:choose>
	<c:when test="${not KNIMEWindowPreference}">
		<iframe id="iFrameProxy" src="${applicationURL}" width="100%"
			marginwidth="0" marginheight="0" frameborder="0"
			height="${iFrameHeight}" style="overflow-x: hidden;"> </iframe>
	</c:when>
	<c:otherwise>
		<p class="lead">If no new window appears, please click <a href="${applicationURL}" target="_blank">here</a> to open <a href="${applicationURL}" target="_blank">${applicationNameParam_view}</a></p>
		<script>
			//window.open('${applicationURL}');
		</script>
	</c:otherwise>
</c:choose>
