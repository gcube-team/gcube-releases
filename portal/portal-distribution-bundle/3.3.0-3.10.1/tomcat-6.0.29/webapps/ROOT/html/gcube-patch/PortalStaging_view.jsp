<%@page pageEncoding="UTF-8"%>

<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ include file="/html/portlet/login/init.jsp" %>
<%@ page import="com.liferay.portal.kernel.servlet.HttpHeaders" %>
<%@ page import="com.liferay.portal.kernel.util.ContentTypes" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<!--
@Author: Massimiliano Assante ISTI-CNR
@version 2.0 May 23rd 2014

This jsp creates the Default Community and redirects to it.
-->
<link type="text/css" rel="stylesheet" href="/html/gcube-patch/StagingStyle.css" /> 

<c:choose>
	<c:when test="<%= request.getAttribute(\"forward\") != null %>">
		<%
			String homeRedirectTo = themeDisplay.getPortalURL()+"/group/data-e-infrastructure-gateway";
		%>
		<script language="Javascript">
			location.href = "<%= homeRedirectTo %>";
		</script>
	</c:when>
</c:choose>
<c:choose>
	<c:when test="<%= themeDisplay.isSignedIn() %>">
		<div class="wizardTitle">
			Alright, your correctly signed in!
		</div>
		<div class="wizardText">Installing the needed components is a very simple process and takes less than five minutes to complete.</div>
		<div class="wizardText">Before you begin the install, we need you to configure the infrastructure this portal is going to run:
		<ul>
			<li>The property file in this bundle located in tomcat-6.0.29/conf/infrastructure.properties is already configured to run over the gCube Development Infrastructure, 
			if you intend to use another infrastructure please edit the file  (You will need to provide your infrastructure name and your starting scope) </li>
			<li>The gCube Libraries provided with this bundle, are kept in the folder gCube/lib, the java archive common-scope-maps contains several gCube infrastructures maps.
			If your infrastructure map is not there make sure you load it in the classpath (common level) before starting the portal.
			</li>
		</ul>
			<div class="wizardText">If you need help configuring your portal bundle you may want to have a look at this 
			<a href="http://gcube.wiki.gcube-system.org/gcube/index.php/GCube_Portal_Installation" target="_blank">gCube Portal wiki</a>
			</div>
		</div>
		<div class="wizardText">Begin your installation by clicking on the button below</div>
		<form name="begin" method="POST" action="<%= request.getAttribute("submitUrl") %>">
			<input type="hidden" name="install" value="true">
			<span class="aui-button-content">
				<input type="submit" value="Begin Install" class="aui-button-input aui-button-input-submit">
			</span>
		</form>
	</c:when>
	<c:otherwise> 
		<div id="welcomer">
			Welcome, this application will guide you to setup your portal to run over a gCube Framework powered infrastructure.
		</div>
		<div class="wizardText">Before you begin the install, please sign in as "test@<%=company.getWebId()%>", password "test" by using the login form on your left.</div>
	</c:otherwise>
</c:choose>

