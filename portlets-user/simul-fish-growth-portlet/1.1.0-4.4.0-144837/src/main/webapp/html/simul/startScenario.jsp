<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />


<%@page import="org.gcube.portlets.user.simulfishgrowth.model.util.ScenarioFullUtil"%>
<%@page import="gr.i2s.fishgrowth.model.ScenarioFull"%>
<%@page import="javax.portlet.PortletSession"%>
<%@page import="com.liferay.portal.kernel.log.LogFactoryUtil"%>
<%@page import="com.liferay.portal.kernel.log.Log"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>

<%
	Log logger = LogFactoryUtil
			.getLog("org.gcube.portlets.user.simulfishgrowth.portlet.jsp." + this.getClass().getSimpleName());
%>

<jsp:useBean id="addGCubeHeaders" type="org.gcube.portlets.user.simulfishgrowth.util.AddGCubeHeaders" scope="request" />

<%
	ScenarioFull entity = null;
	long id = ParamUtil.getLong(request, "id");
	if (logger.isTraceEnabled())
		logger.trace(String.format("my id is [%s]", id));
	if (id <= 0) {
		final PortletSession psession = renderRequest.getPortletSession();
		if (logger.isTraceEnabled())
			logger.trace(String.format("tried the session and found [%s]",
					psession.getAttribute("id", PortletSession.PORTLET_SCOPE)));
		Long sesid = (Long) psession.getAttribute("id", PortletSession.PORTLET_SCOPE);
		if (sesid != null) {
			id = sesid;
		}

		if (logger.isTraceEnabled())
			logger.trace(String.format("after the session my id is [%s]", id));
	}
	if (id > 0) {
		try {
			entity = new ScenarioFullUtil(addGCubeHeaders).getScenarioFull(id);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	if (logger.isDebugEnabled())
		logger.debug(String.format("loaded [%s]", entity));

	String graphs[] = entity.getResultsGraphData().split("gri2sbbridge");
	String tableWeight = graphs[0];
	String tableFCR = graphs[1];
	String tableFood = graphs[2];
%>

<div class="i2s-panel-container">
<liferay-ui:header title="<%=entity.getDesignation()%>"></liferay-ui:header>
<liferay-ui:panel-container accordion="true" extended="true">

<liferay-ui:panel title="Data" state="open">
		<table width="300">
		<tr>
			<td style="width:150px;font-weight:400;">Average Weight:</td>
			<td style="width:150px;text-align:right;font-weight:700;"><%=String.format("%.2f", entity.getResultsWeight())%></td>
		</tr>
		<tr>
			<td style="width:150px;font-weight:400;">LTD Growth:</td>
			<td style="width:150px;text-align:right;font-weight:700;"><%=String.format("%.2f", entity.getResultsGrowth())%></td>
		</tr>
		<tr>
			<td style="width:150px;font-weight:400;">LTD SGR:</td>
			<td style="width:150px;text-align:right;font-weight:700;"><%=String.format("%.2f", entity.getResultsSGR())%></td>
		</tr>
		<tr>
			<td style="width:150px;font-weight:400;">LTD Biological FCR:</td>
			<td style="width:150px;text-align:right;font-weight:700;"><%=String.format("%.2f", entity.getResultsBiolFCR())%></td>
		</tr>
		<tr>
			<td style="width:150px;font-weight:400;">LTD Economical FCR:</td>
			<td style="width:150px;text-align:right;font-weight:700;"><%=String.format("%.2f", entity.getResultsEconFCR())%></td>
		</tr>
		<tr>
			<td style="width:150px;font-weight:400;">LTD Mortality %:</td>
			<td style="width:150px;text-align:right;font-weight:700;"><%=String.format("%.2f", entity.getResultsMortality())%></td>
		</tr>
		</table>
	</liferay-ui:panel>
	
	<liferay-ui:panel title="Weight Graph" state="close">
		<div id="weight-graph-div"></div>
	</liferay-ui:panel>

	<liferay-ui:panel title="FCR Graph" state="close">
		<div id="fcr-graph-div" ></div>
	</liferay-ui:panel>

	<liferay-ui:panel title="Food Consumption Graph" state="close">
		<div id="food-cons-graph-div"></div>
	</liferay-ui:panel>
</liferay-ui:panel-container>
</div>
<portlet:renderURL var="cancel">
	<portlet:param name="mvcPath" value="/html/simul/view.jsp"></portlet:param>
</portlet:renderURL>
<portlet:renderURL var="edit">
	<portlet:param name="mvcPath" value="/html/simul/edit.jsp"></portlet:param>
	<portlet:param name="id"
		value="<%=String.valueOf(entity.getId())%>" />
</portlet:renderURL>


<fieldset style="padding-top:20px;">
	<a class="btn btn-sm i2s-btn i2s-info-btn" href="#" onclick="location.href = '<%=cancel.toString()%>';"><i class="icon-th-list"></i> View all analyses</a>
	<a class="btn btn-sm i2s-btn i2s-info-btn" href="#" onclick="location.href = '<%=edit.toString()%>';"><i class="icon-edit"></i> Edit this analysis</a>
</fieldset>

<script type="text/javascript">
google.charts.load("current", {packages:['corechart']})

// Set a callback to run when the Google Visualization API is loaded.
google.charts.setOnLoadCallback(drawWeightChart);
google.charts.setOnLoadCallback(drawFCRChart);
google.charts.setOnLoadCallback(drawFoodConsChart);

// Callback that creates and populates a data table,
// instantiates the pie chart, passes in the data and
// draws it.
function drawFoodConsChart() {

  // Create the data table.
	var data = new google.visualization.DataTable({
			cols: [{id: 'month', label: 'Month', type: 'string'},
		            {id: 'weight', label: 'kg', type: 'number'}],		
		            rows: [<%=tableFood%>]
	});

  // Set chart options
  var options = {
		  'title':'Monthly food consumption',
          'width':1200,
          'height':500,
          'vAxis': {minValue: 0} };

  // Instantiate and draw our chart, passing in some options.
  var chart = new google.visualization.ColumnChart(document.getElementById('food-cons-graph-div'));
  chart.draw(data, options);
}

function drawWeightChart() {
	// Create the data table.
	var data = new google.visualization.DataTable({
			cols: [{id: 'day', label: 'Day', type: 'string'},
		            {id: 'weight', label: 'Weight', type: 'number'}],		
		            rows: [<%=tableWeight%>]
	});
	
	// Set chart options
	var options = {
		title: 'Weight',
		curveType: 'function',
		legend: { position: 'bottom' },
		'width':1200,
		'height':500
	};
	
	
	// Instantiate and draw our chart, passing in some options.
	var chart = new google.visualization.LineChart(document.getElementById('weight-graph-div'));
	chart.draw(data, options);
}

function drawFCRChart() {
	// Create the data table.
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Day');
	data.addColumn('number', 'FCR');
	data.addColumn('number', 'Global FCR');

	var data = new google.visualization.DataTable({
		cols: [{id: 'day', label: 'Day', type: 'string'},
	            {id: 'fcr', label: 'FCR', type: 'number'},
	            {id: 'globalfcr', label: 'Global FCR', type: 'number'}],		
	            rows: [<%=tableFCR%>]
	});
	
	// Set chart options
	var options = {
		title: 'FCR (%)',
		legend: { position: 'top' },
		colors: ['green', 'red'],
		'width':1200,
		'height':500
	};
	
	
	// Instantiate and draw our chart, passing in some options.
	var chart = new google.visualization.LineChart(document.getElementById('fcr-graph-div'));
	chart.draw(data, options);
}
</script>

