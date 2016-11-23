var piecharts = [];

function addPieChart(divID, dataJSON, showLabels, showLegend, type){
	  removePieChart(divID);
	var data = JSON.parse(dataJSON);
	if(type=="simple"){//Regular pie chart
		nv.addGraph(function() {
		  var chart = nv.models.pieChart()
		      .x(function(d) { return d.label })
		      .y(function(d) { return d.value })
		      .valueFormat(d3.format(",.0f"))
		      .showLabels(showLabels).showLegend(showLegend);
		
		    d3.select("#"+divID+" svg")
		        .datum(data)
		        .transition().duration(700)
		        .call(chart);
		
		    chart.pie.dispatch.on('elementClick', function(e){
				if(e.point.url != undefined)
			    	window.location.href = e.point.url;
			});
		    
//		    $("#"+divID).parent().resize(function(){
//		    	$("#"+divID).width($("#"+divID).parent().width());
//		    	chart.update();
//		    });
		    
//		    nv.utils.windowResize( function(){
//		    	$("#"+divID).width($(".column.left").width());
////		    	console.log("setting width of "+divID+" to: "+$(".column.left").width());
//		    	chart.update();
//
//		    });
		    
//		    $("#"+divID).resize(function(){
//		    	console.log("PIE: resizing width to: "+ $("#"+divID).width());
////		    	chart.width = $("#"+divID).width();
////		    	chart.height = $("#"+divID).height();
//		    	chart.update();
//		    });
//		    
//		    $(window).resize( function(){
//		    	console.log("PIE: set callbacks width to: "+ $("div.column.left").width());
//		    	$("#"+divID).width($("div.column.left").width());
//		    	$("#"+divID).resize();
////		    	$(".statisticsField .vis").width($("div.column.left").width());
////		    	$(".statisticsField .vis").resize();
//		    });
		    
		    //TODO: Figure out a good way to do this automatically
		    nv.utils.windowResize(function(){chart.update(); console.log("updated piechart: "+divID);});
		    
		    piecharts.push([divID, chart]);
		    
		  return chart;
		});
	}
	else{
		nv.addGraph(function() {
			  var chart = nv.models.pieChart()
			      .x(function(d) { return d.label })
			      .y(function(d) { return d.value })
			      .valueFormat(d3.format(",.0f"))
			      .showLabels(showLabels)     //Display pie labels
			      .showLegend(showLegend)
			      .labelThreshold(.05)  //Configure the minimum slice size for labels to show up
			      .labelType("value") //Configure what type of data to show in the label. Can be "key", "value" or "percent"
			      .donut(true)          //Turn on Donut mode. Makes pie chart look tasty!
			      .donutRatio(0.35);     //Configure how big you want the donut hole size to be.

			    d3.select("#"+divID+" svg")
			        .datum(data)
			        .transition().duration(700)
			        .call(chart);

			    chart.pie.dispatch.on('elementClick', function(e){
					if(e.point.url != undefined)
				    	window.location.href = e.point.url;
				});
			    
			    
//			    nv.utils.windowResize( function(){
//			    	$("#"+divID).width($(".column.left").width());
////			    	$("#"+divID).height($(".column.left").height());
//			    	chart.update();
//			    });
			    
			    //TODO: Figure out a good way to do this automatically
			    nv.utils.windowResize(chart.update);
			    
			    piecharts.push([divID, chart]);
			    
			  return chart;
			});
	}
}

function piechart_update(divID, dataJSON){
	var data = JSON.parse(dataJSON);
	var chart = getPieChart(divID);
	
	chart.x(function(d) { return d.label });
	chart.y(function(d) { return d.value });

    d3.select("#"+divID+" svg")
        .datum(data)
		.transition().duration(500)
        .call(chart);

}

function piechart_refresh(divID){
	getPieChart(divID).update();
}

function removePieChart(divID){
	for(var i=0;i<piecharts.length;i++)
		if(piecharts[i][0]==divID)
			piecharts.splice(i,1);	
}

function getPieChart(divID){
	for(var i=0;i<piecharts.length;i++)
		if(piecharts[i][0]==divID)
			return piecharts[i][1];
}



//Pie chart example data. Note how there is only a single array of key-value pairs.
//function exampleData() {
//  return  [
//      { 
//        "label": "One",
//        "value" : 29.765957771107
//      } , 
//      { 
//        "label": "Two",
//        "value" : 0
//      } , 
//      { 
//        "label": "Three",
//        "value" : 32.807804682612
//      } , 
//      { 
//        "label": "Four",
//        "value" : 196.45946739256
//      } , 
//      { 
//        "label": "Five",
//        "value" : 0.19434030906893
//      } , 
//      { 
//        "label": "Six",
//        "value" : 98.079782601442
//      } , 
//      { 
//        "label": "Seven",
//        "value" : 13.925743130903
//      } , 
//      { 
//        "label": "Eight",
//        "value" : 5.1387322875705
//      }
//    ];
//}


