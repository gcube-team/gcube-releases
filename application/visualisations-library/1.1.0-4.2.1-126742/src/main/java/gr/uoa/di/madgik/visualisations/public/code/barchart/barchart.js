function barchart(divID, showValues, showTooltips, showControls, dataJSON ) {
	var data = JSON.parse(dataJSON);
  nv.addGraph(function() {
    var chart = nv.models.multiBarHorizontalChart()
        .x(function(d) { return d.label })
        .y(function(d) { return d.value })
		//.valueFormat(d3.format('d'))
  //      .staggerLabels(true)
        //.margin({top: 0, right: 0, bottom: 0, left: 100})
        .showValues(showValues)           //Show bar value next to each bar.
        .tooltips(showTooltips)             //Show tooltips on hover.
		.showLegend(false)
        .transitionDuration(350)
        .showControls(showControls);        //Allow user to switch between "Grouped" and "Stacked" mode.

    chart.yAxis
    	.tickFormat(d3.format('d'));
//        .tickFormat(d3.format(',.2f'));

    d3.select("#"+divID+" svg")
        .datum(data)
        .call(chart);

    nv.utils.windowResize(chart.update);

    return chart;
  });
}