<%@page import="org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager"%>
<%@page import="java.util.Enumeration"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.gcube.vomanagement.usermanagement.model.GCubeUser"%>
<%@page import="org.gcube.common.portal.PortalContext"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="org.gcube.common.scope.api.ScopeProvider"%>

<!--                                           -->
<!-- The module reference below is the link    -->
<!-- between html and your Web Toolkit module  
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/DataMinerManagerDeployer.css"
	type="text/css">
<link rel="stylesheet"
	href="http://fonts.googleapis.com/css?family=Lato:300,400,700"
	type="text/css">

<script
	src='<%=request.getContextPath()%>/dataminermanagerdeployer/dataminermanagerdeployer.nocache.js'></script>
-->

<%   
Logger logger=LoggerFactory.getLogger(PortalContext.class);
logger.info("Request"+request);
Enumeration<String> names=request.getAttributeNames();
while(names.hasMoreElements()){
	String name=names.nextElement();
	logger.info("Request Header:"+name+"="+request.getAttribute(name));
}

long groupId = com.liferay.portal.util.PortalUtil.getScopeGroupId(request);
String username = com.liferay.portal.util.PortalUtil.getUser(request).getScreenName();
String scope =  new LiferayGroupManager().getInfrastructureScope(groupId);;
ScopeProvider.instance.set(scope);

PortalContext pContext = PortalContext.getConfiguration();

//GCubeUser user = pContext.getCurrentUser(request);
logger.info("User: "+username);
//String scope = pContext.getCurrentScope(request);
logger.info("Scope: "+scope);

String userToken = pContext.getCurrentUserToken(scope, username);
logger.debug("UserToken: "+userToken);
//Anche questa sotto non va
//String userToken = pContext.getCurrentUserToken(request);

 
 %>

<iframe src="<%=request.getContextPath()%>/DataMinerManagerDeployer.html?token=<%=userToken%>" width="100%" height="700" frameborder="0" marginheight="0" scrolling="yes"></iframe>
 
<!-- 
<div class="contentDiv" id="contentDiv"></div>
 -->