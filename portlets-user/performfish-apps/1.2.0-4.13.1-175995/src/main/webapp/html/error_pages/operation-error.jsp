<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="com.liferay.portal.kernel.servlet.SessionErrors"%>
<%@page import="com.liferay.portal.kernel.servlet.SessionMessages"%>
<%@page import="com.liferay.portal.model.User"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<portlet:defineObjects />
<liferay-theme:defineObjects />

<%
User currentUser = (User) request.getAttribute(WebKeys.USER);
%>

<portlet:renderURL var="normalState"
	windowState="<%=LiferayWindowState.NORMAL.toString()%>" />

<p class="lead">
	We're sorry 
	<%=currentUser.getFirstName()%>. There was an error performing this operation. Try again, if the error occurs again please report this issue to <a href="https://support.d4science.org" target="_blank">https://support.d4science.org</a>
</p>


<a class="btn btn-large  btn-primary" href="${normalState}"><i
	class="icon icon-angle-left"></i>&nbsp;Close</a>
<div