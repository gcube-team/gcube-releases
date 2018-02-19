
var registeredCharts = {}

/*
 * useParent (bool) if the resize is applied from the main widget or from
 * the container div.
 */
function registerChart(param, useParent) {
	var paramid = String(param.id);
	registeredCharts[paramid] = param;
	setUseParent(param.id, useParent);
}

/*
 * From the id of a chart retrieves the container from which
 * the size must be taken.
 */
function getParentContainer(chartId) {
	var chart = getChartById(chartId);
	try {
		if (document.getElementById(chart.id.substring(5) + '-frame').parentNode != null) {
			return document.getElementById(chart.id.substring(5) + '-frame').parentNode;
		}
		return document.getElementById(chart.id.substring(5) + '-frame');
	} catch (e) {
		return chart.container;
	}
}

/*
 * chartId: the id of the chart
 * useParent: boolean - if the resize is applied from the main widget or from
 * the container div.
 */
function setUseParent(chartId, useParent) {
	var param = getChartById(chartId);
	if (useParent) {
		param.parentContainer = getParentContainer(chartId);
		param.widthOffset = 0;
		param.heightOffset = 0;
	} else {
		param.parentContainer = document.documentElement;
		param.widthOffset = 10;
		param.heightOffset = 90;
	}
}

/*
 * from the id (used at registration phase) retrieves the JS instance
 * if the chart.
 */
function getChartById(variable){
	return registeredCharts[variable];
}

/*
 * The parent can be either param.container or document.documentElement
 */
function autoResizeChart(chartID, widthOffset, heightOffset) {
	var param = getChartById(chartID);
	if (param != null) {
		var swidth = param.parentContainer.clientWidth;
		var sheight =  param.parentContainer.clientHeight;
		
		// The first time the size of the chart must be explicitly expressed
		if (widthOffset == null || widthOffset == -1) {
			swidth = param.parentContainer.clientWidth - param.widthOffset;
		} else {
			swidth = param.parentContainer.clientWidth - widthOffset;
		}
		
		if (heightOffset == null || heightOffset == -1) {
			sheight =  param.parentContainer.clientHeight - param.heightOffset;
		} else {
			sheight =  param.parentContainer.clientHeight - heightOffset;
		}
		param.setSize(swidth, sheight, 1);
		if (param.legend != null && param.legend.renderLegend != null) {
			param.legend.renderLegend();
		}
	}
}

function resizeChart(chartID, width, height, widthOffset, heightOffset) {
	var param = getChartById(chartID);
	if (param != null) {
		param.setSize(width - widthOffset, height - heightOffset, 1);
	}
}