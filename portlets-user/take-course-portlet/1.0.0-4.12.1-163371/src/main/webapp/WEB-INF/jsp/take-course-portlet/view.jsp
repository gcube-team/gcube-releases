<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@page import="com.liferay.portal.theme.*"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page
	import="org.gcube.portal.trainingmodule.shared.TrainingCourseDTO"%>
<%@ page import="java.util.List"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<portlet:actionURL name="opencourse" var="opencourse">
	<portlet:param name="action" value="courseSubmit"></portlet:param>
</portlet:actionURL>


<%
	int i = 1;
%>
<c:if test="${empty list}">
	<div class="well">
		<h2>Hello ${user.getFirstName()}</h2>
		<legend>The instructor hasn't activated any course for you at
			the moment.</legend>
		<p>If you think this is an error, you may contact the instructor(s) by clicking on the instructor's profile in <a href="${membersPage}"><span style="font-weight: bold;">this VRE member's page</span></a>.</p>	
	</div>

</c:if>
<c:forEach items="${list}" var="course">
	<div class="well">
		<c:if test="${not course.courseActive}">
			<div class="alert">
				<strong>Please note: </strong> this course (${course.title}) is not active, only you
				as instructor are able to access it in preview mode</strong>
			</div>
		</c:if>
		<ul class="nav nav-pills">
			<li class="active"><a href="#"
				onclick="$('#submit<%=i%>').click()">COURSE <%=i%></a></li>
		</ul>
		<div class="page-header">
			<h2>
				<c:out escapeXml="true" value="${course.title}" />
			</h2>
		</div>
		<p style="font-size: 1.2em; line-height: 1.2em;">
			<span style="font-weight: bold;">Commitment: </span>
			<c:out escapeXml="true" value="${course.commitment}" />
		</p>
		<legend>
			<span style="font-weight: bold;">About the Course</span>
		</legend>
		<p style="font-size: 1.2em; line-height: 1.2em;">
			<c:out escapeXml="true" value="${course.description}" />
		</p>
		<form action="${opencourse}" method="POST">
			<input type="hidden" name="course-id" value="${course.internalId}"></input>
			<% String labelAccessCourse = "Learn more"; %>
			<c:if test="${not course.courseActive}">
				<% labelAccessCourse = "See preview"; %>
			</c:if>
			<button class="btn btn-large btn-primary" type="submit"
				id="submit<%=i%>"><%= labelAccessCourse %></button>
		</form>


	</div>
	<%
		i++;
	%>
</c:forEach>

