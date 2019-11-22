<%@include file="/html/init.jsp" %>

<!-- Inherit Generic public web-app portlet configuration parameters -->
<%
String appURL_view = GetterUtil.getString(portletPreferences.getValue("appURL", StringPool.BLANK));
String appURLTokenParam_view = GetterUtil.getString(portletPreferences.getValue("appURLTokenParam", StringPool.BLANK));
%>
<span id="appURL" data-app-url="<%= appURL_view %>"></span>
<span id="appURLTokenParam" data-app-url-token-param="<%= appURLTokenParam_view %>"></span>

<!-- Retrieve VRE security user token -->
<% 
Object securityTokenObj = request.getAttribute("securityToken");
String securityToken = "";
if(securityToken != null){
	securityToken = securityTokenObj.toString();
}
%>
<span id="securityToken" data-securitytoken="<%= securityToken %>"></span>

<!-- Testing Proxy.jsp -->
<!-- 
<portlet:renderURL var="proxyURL">
    <portlet:param name="jsppage" value="/html/proxy.jsp" />
</portlet:renderURL>
<span id="proxyRef" data-proxyurl="<%= proxyURL %>"></span>
-->


<!-- Main app container -->
<div id="mainPortletContainer"></div>
