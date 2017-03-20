var barcharts = [];

function barchart(divID, showValues, showTooltips, showControls, dataJSON ) {
	  removeBarChart(divID);
	var data = JSON.parse(dataJSON);
  nv.addGraph(function() {
    var chart = nv.models.multiBarHorizontalChart()
        .x(function(d) { return d.label })
        .y(function(d) { return d.value })
        .valueFormat(d3.format(",.0f"))
        .margin({top: 0, right: 0, bottom: 0, left: 150})
        .showValues(showValues)           //Show bar value next to each bar.
        .tooltips(showTooltips)             //Show tooltips on hover.
		.showLegend(false)
        .transitionDuration(700)
        .showControls(showControls);        //Allow user to switch between "Grouped" and "Stacked" mode.

    chart.yAxis
    	.tickFormat(d3.format('d'));
//        .tickFormat(d3.format(',.2f'));

    d3.select("#"+divID+" svg")
        .datum(data)
        .call(chart);

    
//    $("#"+divID).resize(function(){
//    	console.log("BARS: resizing width to: "+ $("#"+divID).width());
////    	chart.width = $("#"+divID).width();
//    	chart.update();
//    });
//    
//    $(window).resize( function(){
//    	console.log("BARS: set callbacks width to: "+ $("div.column.left").width());
//    	$("#"+divID).width($("div.column.left").width());
//    	$("#"+divID).resize();
////    	$(".statisticsField .vis").width($("div.column.left").width());
////    	$(".statisticsField .vis").resize();
//    });
    	
    
//    //$("#"+divID).parent().resize( function(){
////    nv.utils.windowResize( function(){	
////    $("div.column.left").resize(function(){
//    	console.log("resizing at: "+$("div.column.left").width());
//    	$("#"+divID).width($("div.column.left").width());
//    	chart.update();
//    });

    //TODO: Figure out a good way to do this automatically
//    nv.utils.windowResize(chart.update);
    nv.utils.windowResize(function(){chart.update(); console.log("updated barchart: "+divID);});
    
    
    chart.multibar.dispatch.on('elementClick', function(e){
		if(e.point.url != undefined)
	    	window.location.href = e.point.url;
		console.log("clicked on bar");
	});
    
    barcharts.push([divID, chart]);
    
    return chart;
  });
}

function barchart_update(divID, dataJSON){
	var data = JSON.parse(dataJSON);
	var chart = getLineChart(divID);
	
	chart.x(function(d) { return d.label });
	chart.y(function(d) { return d.value });

    d3.select("#"+divID+" svg")
        .datum(data)
		.transition().duration(500)
        .call(chart);

}


function barchart_refresh(divID){
	getBarChart(divID).update();
}



function removeBarChart(divID){
	for(var i=0;i<barcharts.length;i++)
		if(barcharts[i][0]==divID)
			barcharts.splice(i,1);
			
}

function getBarChart(divID){
	for(var i=0;i<barcharts.length;i++)
		if(barcharts[i][0]==divID)
			return barcharts[i][1];
}

