<%@ include file="/html/commons/inc.jsp"%>
<%@ page import="org.gcube.portlets.user.simulfishgrowth.model.util.SiteFullUtil"%>

<%
	Log logger = LogFactoryUtil
			.getLog("org.gcube.portlets.user.simulfishgrowth.portlet.jsp." + this.getClass().getSimpleName());

	logger.debug("starting");
%>

<%!com.liferay.portal.kernel.dao.search.SearchContainer<gr.i2s.fishgrowth.model.SiteFull> searchContainer = null;%>


<jsp:useBean id="ownerId" type="java.lang.String" scope="request" />
<jsp:useBean id="addGCubeHeaders" type="org.gcube.portlets.user.simulfishgrowth.util.AddGCubeHeaders" scope="request" />
<jsp:useBean id="usages" type="java.util.Map<?, ?>" scope="request" />

<div id="s-con" > 
	<input type="text" id="searchBoxText" class="search-text-box" placeholder="Search.."/>
	<button id="searchBtn" class="search-btn"><i class="icon-search"></i></button>
</div>
<table class="table table-striped table-hover analysis-table">
  <thead>
    <tr>
      <th></th>
      <th>Name</th>
      <th>Current Rating</th>
      <th>Oxygen Rating</th>
    </tr>
  </thead>
  <tbody>

	<liferay-ui:search-container delta="<%=5%>"
		emptyResultsMessage="No Sites found.">
		<liferay-ui:search-container-results
			results="<%=new SiteFullUtil(addGCubeHeaders).getSiteFulls(ownerId, searchContainer.getStart(), searchContainer.getEnd())%>"
			total="<%=new SiteFullUtil(addGCubeHeaders).getSiteFullCount(ownerId)%>" />
		<liferay-ui:search-container-row
			className="gr.i2s.fishgrowth.model.SiteFull" modelVar="item">
			<portlet:renderURL var="edit">
				<portlet:param name="mvcPath" value="/html/site/edit.jsp"></portlet:param>
				<portlet:param name="id" value="<%=String.valueOf(item.getId())%>" />
			</portlet:renderURL>
			<portlet:actionURL name="delete" var="deleteURL">
				<portlet:param name="id" value="<%=String.valueOf(item.getId())%>" />
			</portlet:actionURL>
				
<tr>
		<th scope="row" style="width:165px;">
			<a class="btn btn-sm i2s-btn i2s-info-btn" href="<%=edit.toString()%>"> <i class="icon-edit"></i> Edit</a>
			<%if  ((Integer)usages.get(item.getId()) <= 0) { %>
			<a class="btn btn-sm i2s-btn i2s-danger-btn" href="<%=deleteURL.toString()%>"
			   href="<%=deleteURL.toString()%>"  onclick="return confirm('Delete <%=StringEscapeUtils.escapeHtml(item.getDesignation())%>?');"><i class="icon-trash"></i> Delete</a>
			<% } %>
		</th>
	  <td><b><a href="<%= edit.toString() %>"><%=StringEscapeUtils.escapeHtml(item.getDesignation())%></a></b></td>
	  <td><%=item.getCurrentRatingDesignation() %></td>
	  <td><%=item.getOxygenRatingDesignation() %></td>
    </tr>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator />
	</liferay-ui:search-container>

</tbody>
</table>
<portlet:renderURL var="add">
		<portlet:param name="mvcPath" value="/html/site/edit.jsp"></portlet:param>
	</portlet:renderURL>
<fieldset style="padding-top:20px;">
<a class="btn btn-sm i2s-btn i2s-success-btn" href="<%=add.toString()%>"><i class="icon-plus"></i> Add</a>
</fieldset>
