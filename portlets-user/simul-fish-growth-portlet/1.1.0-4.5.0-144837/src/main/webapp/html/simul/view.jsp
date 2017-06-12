<%@ include file="/html/commons/inc.jsp"%>
<%@page import="org.gcube.portlets.user.simulfishgrowth.model.util.ScenarioFullUtil"%>

<%
	Log logger = LogFactoryUtil
			.getLog("org.gcube.portlets.user.simulfishgrowth.portlet.jsp." + this.getClass().getSimpleName());

	logger.debug("starting");
%>

<%!com.liferay.portal.kernel.dao.search.SearchContainer<gr.i2s.fishgrowth.model.ScenarioFull> searchContainer = null;%>

<jsp:useBean id="ownerId" type="java.lang.String" scope="request" />
<jsp:useBean id="addGCubeHeaders" type="org.gcube.portlets.user.simulfishgrowth.util.AddGCubeHeaders" scope="request" />

<div id="s-con" > 
	<input type="text" id="searchBoxText" class="search-text-box" placeholder="Search.."/>
	<button id="searchBtn" class="search-btn"><i class="icon-search"></i></button>
</div>
<table class="table table-striped table-hover analysis-table">
  <thead>
    <tr>
      <th></th>
      <th>Name</th>
      <th>Comments</th>
      <th>Model</th>
      <th>Status</th>
    </tr>
  </thead>
  <tbody>

	<liferay-ui:search-container delta="<%=5%>"
		emptyResultsMessage="No Analysis found.">
		<liferay-ui:search-container-results
			results="<%=new ScenarioFullUtil(addGCubeHeaders).getScenarioFulls(ownerId, searchContainer.getStart(), searchContainer.getEnd())%>"
			total="<%=new ScenarioFullUtil(addGCubeHeaders).getScenarioFullCount(ownerId)%>" />
		<liferay-ui:search-container-row
			className="gr.i2s.fishgrowth.model.ScenarioFull" modelVar="item">
			<portlet:renderURL var="edit">
				<portlet:param name="mvcPath" value="/html/simul/edit.jsp"></portlet:param>
				<portlet:param name="id" value="<%=String.valueOf(item.getId())%>" />
			</portlet:renderURL>
			<portlet:actionURL name="delete" var="deleteURL">
				<portlet:param name="id" value="<%=String.valueOf(item.getId())%>" />
			</portlet:actionURL>
			<portlet:renderURL var="startScenarioURL">
				<portlet:param name="mvcPath" value="/html/simul/startScenario.jsp"></portlet:param>
				<portlet:param name="id" value="<%=String.valueOf(item.getId())%>" />
			</portlet:renderURL>
				
<tr>
		<th scope="row" style="width:280px;">
			<a class="btn btn-sm i2s-btn i2s-info-btn" href="<%=edit.toString()%>"> <i class="icon-edit"></i> Edit</a>
			<a class="btn btn-sm i2s-btn i2s-danger-btn" href="<%=deleteURL.toString()%>"
			   href="<%=deleteURL.toString()%>"  onclick="return confirm('Delete <%=StringEscapeUtils.escapeHtml(item.getDesignation())%>?');"><i class="icon-trash"></i> Delete</a>
			<% if (item.getResultsGraphData()!=null && !item.getResultsGraphData().isEmpty()) { %>			   
			<a class="btn btn-sm i2s-btn i2s-calm-btn" href="<%=startScenarioURL.toString()%>"> <i class="icon-th"></i> Results</a>
			<% } %>
		</th>
      <td><b><a href="<%= edit.toString() %>"><%=StringEscapeUtils.escapeHtml(item.getDesignation())%></a></b></td>
      <td><%=item.getComments()%></td>
      <td><%=item.getModelerDesignation()%></td>
      <td><% if(item.getStatusDesignation().indexOf("Calculation failed") != -1 ){ %>
      		<span style="color:red"><i style="padding-right:10px" class="icon-remove"></i> <%=item.getStatusDesignation() %></span>
      		<%} else if( item.getStatusDesignation().indexOf("Ready") != -1) {%>
      		<span style="color:green;"> <i style="padding-right:10px" class="icon-ok"></i> <%=item.getStatusDesignation() %></span>
      		<%} else {%>
      		<span style="color:blue;"><i style="padding-right:10px" class="icon-cloud"></i> <%=item.getStatusDesignation() %></span>
      		<%} %>
      		
      	</td>
    </tr>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator />
	</liferay-ui:search-container>

</tbody>
</table>
<portlet:renderURL var="add">
		<portlet:param name="mvcPath" value="/html/simul/edit.jsp"></portlet:param>
	</portlet:renderURL>
<fieldset style="padding-top:20px;">
<a class="btn btn-sm i2s-btn i2s-success-btn" href="<%=add.toString()%>"><i class="icon-plus"></i> Add</a>
</fieldset>
