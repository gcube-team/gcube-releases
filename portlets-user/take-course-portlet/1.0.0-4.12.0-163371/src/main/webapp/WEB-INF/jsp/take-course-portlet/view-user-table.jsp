<%@page import="java.util.Collections"%>
<%@page import="org.apache.commons.beanutils.BeanComparator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.liferay.portal.kernel.util.ListUtil"%>
<%@page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>
<%@page import="com.liferay.portal.service.UserLocalServiceUtil"%>
<%@page import="com.liferay.portal.model.User"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="com.liferay.portal.util.PortalUtil"%>
<%@page import="org.gcube.portlets.user.takecourse.ProgressManager"%>
<%@page
	import="org.gcube.portlets.user.takecourse.dto.StudentProgressDTO"%>
<%@page import="org.gcube.portlets.user.takecourse.dto.UnitProgress"%>

<%@page import="java.util.List"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="theme"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>

<portlet:defineObjects />
<theme:defineObjects />


<%
	pageContext.setAttribute("currentURL", PortalUtil.getCurrentURL(request));

	long groupId = (Long) request.getAttribute("groupId");
	String courseId = (String) request.getAttribute("courseId");
	long courseIdLong = Long.parseLong(courseId);
	pageContext.setAttribute("courseId", courseId);

	//orderByCol is the column name passed in the request while sorting
	String orderByCol = ParamUtil.getString(request, "orderByCol");

	//orderByType is passed in the request while sorting. It can be either asc or desc
	String orderByType = ParamUtil.getString(request, "orderByType");
	String sortingOrder = orderByType;
	//Logic for toggle asc and desc
	if (orderByType.equals("desc")) {
		orderByType = "asc";
	} else {
		orderByType = "desc";
	}
%>
<div class="alert">
	<strong>Please note: </strong> Click on the student names to see their
	individual progress</strong>
</div>

<liferay-ui:search-container var="searchContainer" delta="20"
	deltaConfigurable="true"
	emptyResultsMessage="There are no users to show, the list is empty."
	orderByType="<%=orderByType%>">

	<liferay-ui:search-container-results>
		<%
			//Get all the users from User_ table
					List<StudentProgressDTO> allUsers = ProgressManager.getAllStudentProgress(courseIdLong, groupId);
					//usersPerPage is unmodifyable list. It can not be sorted.
					List<StudentProgressDTO> usersPerPage = ListUtil.subList(allUsers, searchContainer.getStart(),
							searchContainer.getEnd());
					int totalUsers = allUsers.size();

					//From usersPerPage a new list sortableUsers is created. For sorting we will use this list
					List<StudentProgressDTO> sortableUsers = new ArrayList<StudentProgressDTO>(usersPerPage);
					if (Validator.isNotNull(orderByCol)) {
						//Pass the column name to BeanComparator to get comparator object
						BeanComparator comparator = new BeanComparator(orderByCol);
						Collections.sort(sortableUsers, comparator);
						if (sortingOrder.equalsIgnoreCase("desc"))
							Collections.reverse(sortableUsers);
					}
					//sortableUsers list is sorted on the basis of condition. When page load it wont be sorted
					//It will be sorted only when a header of coulmn is clicked for sorting
					pageContext.setAttribute("results", sortableUsers);
					pageContext.setAttribute("total", totalUsers);
		%>
	</liferay-ui:search-container-results>

	<liferay-ui:search-container-row
		className="org.gcube.portlets.user.takecourse.dto.StudentProgressDTO"
		modelVar="aUser">

		<portlet:renderURL var="rowURL">
			<portlet:param name="userId" value="${aUser.userId}" />
			<portlet:param name="courseId" value="${courseId}" />
		</portlet:renderURL>
		<liferay-ui:search-container-column-text property="fullName"
			orderable="true" name="Full Name" orderableProperty="fullName"
			href="${rowURL}" />
		<% List<UnitProgress> theList = aUser.getUnits();
				for (UnitProgress progress: theList) {
		%>
		<liferay-ui:search-container-column-text
			name="<%= progress.getName() %>">
			<div class="progress">
					<div class="bar" style="width: <%= progress.getProgressPercentage()%>%"><%= progress.getProgressPercentage()%>%</div>
			</div>
		</liferay-ui:search-container-column-text>
		<%} %>

	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>

