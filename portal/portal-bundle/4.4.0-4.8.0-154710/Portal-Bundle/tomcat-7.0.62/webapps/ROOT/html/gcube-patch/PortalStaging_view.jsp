<%@page pageEncoding="UTF-8"%>

<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ include file="/html/portlet/login/init.jsp" %>
<%@ page import="com.liferay.portal.kernel.servlet.HttpHeaders" %>
<%@ page import="com.liferay.portal.kernel.util.ContentTypes" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<!--
@Author: Massimiliano Assante ISTI-CNR

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
			gCube Portal Staging
		</div>
		<div class="wizardText">Installing the needed components is a very simple process and takes less than five minutes to complete.</div>
		<div class="wizardText">Before you begin the install, there are few things you need to have and do:
		<ul>
			<li>A running <a href="http://wiki.gcube-system.org" target="_blank">gCube</a> infrastructure</li>
			<li>The gCube wHN Distribution (<a href="https://wiki.gcube-system.org/gcube/SmartGears" target="_blank">SmartGears</a>), provided with this bundle, 
			correctly configured for your gCube infrastructure (You will need to provide your infrastructure name and your starting scope) 
			<a href="https://wiki.gcube-system.org/gcube/GCube/index.php/Scope_Management#Modelling_Scope" target="_blank"> what's this?</a>
			</li>
		</ul>
			<div class="wizardText">If you need help configuring your wHN you may want to have a look at this 
			<a href="http://wiki.gcube-system.org/gcube/SmartGears_Web_Hosting_Node_(wHN)_Installation" target="_blank">wHN Configuration wiki</a>
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

