<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="org.gcube.portal.trainingmodule.shared.*"%>
<%@ page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@ page
	import="org.gcube.portlets.user.takecourse.dto.TrainingCourseWithUnits"%>
<%@ page import="org.gcube.portlets.user.takecourse.Utils"%>

<%@ page import="java.util.List"%>

<portlet:renderURL var="renderjspURL">
	<portlet:param name="action" value="renderjsp"></portlet:param>
</portlet:renderURL>
<!-- Callback to get the unit files -->
<portlet:resourceURL id="getUnitContent" var="getUnitContent"></portlet:resourceURL>
<!-- Callback to get the unit questionnaires -->
<portlet:resourceURL id="getUnitQuestionnaires"
	var="getUnitQuestionnaires"></portlet:resourceURL>
<!-- Callback to get the unit videos -->
<portlet:resourceURL id="getUnitVideos" var="getUnitVideos"></portlet:resourceURL>
<!-- Callback to sget the unit files read -->
<portlet:resourceURL id="setFileRead" var="setFileRead"></portlet:resourceURL>

<script>
	$("h1.portlet-title").hide(); //hide the title of the portlet
</script>

<%
	long courseId = (Long) request.getAttribute("courseId");
	long userId = -1;
	if (request.getAttribute("userId") != null)
		userId = (Long) request.getAttribute("userId");
	pageContext.setAttribute("userId", userId);
%>
<c:if test="${userId > 0}">
	<div class="alert">
		<strong>Please note: </strong> you are viewing this page as student <strong><%=UserLocalServiceUtil.getUser(userId).getFullName()%></strong>
	</div>
</c:if>
<a class="btn btn-link  btn-large" href="${renderjspURL}"
	style="padding-left: 0;"><i class="icon icon-angle-left"></i>&nbsp;Back
	to your courses</a>
<div class="well">
	<div class="page-header">
		<h2>
			<c:out escapeXml="true" value="${course.title}" />
		</h2>
	</div>
	<p style="font-size: 1.2em; line-height: 1.2em;">
		<span style="font-weight: bold;">Created by: </span>
		<c:out escapeXml="true" value="${course.createdBy}" />
	</p>
	<div class="media">
		<a class="pull-left" href="#"> <img class="media-object"
			src="${course.trainer.avatarURL}" style="width: 100px;">
		</a>
		<div class="media-body">
			<h4 class="media-heading">Taught by:</h4>
			<legend>
				<a href="${course.trainer.accountURL}" target="_blank">${course.trainer.fullName}
					(open profile)</a>
				<p>${course.trainer.headline}</p>
			</legend>

		</div>
	</div>
	<legend>
		<span style="font-weight: bold;">About the Course</span>
	</legend>
	<p style="font-size: 1.2em; line-height: 1.2em;">
		<c:out escapeXml="true" value="${course.description}" />
	</p>

	<p style="font-size: 1.2em; line-height: 1.2em;">
		<span style="font-weight: bold;">Commitment: </span>
		<c:out escapeXml="true" value="${course.commitment}" />
	</p>
	<p style="font-size: 1.2em; line-height: 1.2em;">
		<span style="font-weight: bold;">Language: </span>
		<c:out escapeXml="true" value="${course.languages}" />
	</p>
</div>
<div style="padding: 0 15px;">
	<div class="page-header" style="margin-bottom: 0">
		<h3>Syllabus</h3>
	</div>
	<c:forEach items="${course.units}" var="unit">
		<div class="well" style="padding: 25px;">
			<ul class="nav nav-pills">
				<li class="active"><a href="#">${unit.workspaceFolderName}</a></li>
			</ul>
			<legend>
				<span style="font-weight: bold;">${unit.title}</span>
			</legend>
			<p style="font-size: 1.2em; line-height: 1.2em;">
				<c:out escapeXml="true" value="${unit.description}" />
				<script>
				<!-- Get the content of the folder	-->
					getUnitQuestionnaires('${unit.internalId}',
							'${getUnitQuestionnaires}', '${userId}');
					getUnitFolderContent('${unit.internalId}','${unit.workspaceFolderId}',
							'${getUnitContent}', '${setFileRead}', '${userId}');
					getUnitVideos('${unit.internalId}', '${getUnitVideos}');
				</script>
			</p>
			<p id="items-${unit.workspaceFolderId}"
				style="display: none; font-size: 1.2em;">
				<i class="icon-copy"></i> <span id="span-${unit.workspaceFolderId}"
					style="font-size: 1.2em; color: #555;"></span>
			</p>
			<div style="padding-left: 15px;"
				id="folder-${unit.workspaceFolderId}">
				<img alt="loading"
					src="<%=request.getContextPath()%>/images/spinner.gif">
			</div>
			<!-- video part -->
			<p id="pvideos-${unit.internalId}"
				style="display: none; margin-top: 10px; font-size: 1.2em;">
				<i class="icon-youtube-play"></i> <span
					id="span-videos-${unit.internalId}"
					style="font-size: 1.2em; color: #555;"></span>
			</p>
			<div style="padding-left: 15px; display: none;"
				id="videos-${unit.internalId}"></div>
			<!-- Grades/Questionnairs part -->
			<p id="pgrades-${unit.internalId}"
				style="display: none; margin-top: 10px;">
				<i class="icon-tasks"></i> <span id="graded-${unit.internalId}"
					style="font-size: 1.2em; color: #555;"></span>
			</p>
			<div style="padding-left: 15px; display: none;"
				id="quiz-${unit.internalId}"></div>
			<div style="margin-top: 15px; display: none;"
				id="percentage-${unit.internalId}">
				<span style="font-size: 1.2em; color: #555;">Progress:</span>
				<div class="progress">
					<input type="hidden" id="pnum-${unit.internalId}" value="0"/>
					<input type="hidden" id="ptotal-${unit.internalId}" value="0">
					<div id="bar-${unit.internalId}" class="bar"
						style="width: 0%"></div>
				</div>
			</div>

		</div>
	</c:forEach>
</div>

