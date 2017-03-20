var linecharts = [];

function linechart(divID, dataJSON) {
  removeLineChart(divID);
  var data = JSON.parse(dataJSON);
  nv.addGraph(function() {
	  
    var chart = nv.models.cumulativeLineChart()
          .x(function(d) { return d[0] }) //mapping x-axis values to the first value of the json data (subarrays) 
          .y(function(d) { return d[1] }) //mapping y-axis values to the second value of the json data (subarrays) 
          .color(d3.scale.category10().range())
          .useInteractiveGuideline(true)
          ;
    
     chart.xAxis
//        .tickValues([1078030800000,1122782400000,1167541200000,1251691200000])
        .tickFormat(function(d) {
            return d3.time.format('%x')(new Date(d))
          });

    chart.yAxis
        .tickFormat(d3.format('d'));

    d3.select("#"+divID+" svg")
        .datum(data)
		.transition().duration(500)
        .call(chart);

    //TODO: Figure out a good way to do this automatically
    nv.utils.windowResize(chart.update);
	linecharts.push([divID, chart]);

    return chart;
  });
}


function linechart_update(divID, dataJSON){
	var data = JSON.parse(dataJSON);
	var chart = getLineChart(divID);
	
	chart.x(function(d) { return d[0] });
	chart.y(function(d) { return d[1] });

    d3.select("#"+divID+" svg")
        .datum(data)
		.transition().duration(500)
        .call(chart);

}

function linechart_refresh(divID){
	getLineChart(divID).update();
}



function removeLineChart(divID){
	for(var i=0;i<linecharts.length;i++)
		if(linecharts[i][0]==divID)
			linecharts.splice(i,1);
}

function getLineChart(divID){
	for(var i=0;i<linecharts.length;i++)
		if(linecharts[i][0]==divID)
			return linecharts[i][1];
}
