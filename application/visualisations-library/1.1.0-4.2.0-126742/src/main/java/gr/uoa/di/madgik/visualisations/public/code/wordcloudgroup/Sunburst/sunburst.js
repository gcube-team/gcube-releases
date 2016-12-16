/*
SAMPLE DATA ARRAY INPUT:

dataArray = {
 "name": "flare",
 "children": [
	  {
		"name": "Collection 1",
		"size": 1
	  },
	  {
		"name": "Collection 2",
		"size": 1
	  },
	  {
		"name": "Collection 3",
		"size": 1
	  },
	  {
		"name": "Collection 4",
		"size": 1
	  },
	  {
		"name": "Collection 5",
		"size": 1
	  },
	  {
		"name": "Collection 6",
		"size": 1
	  }
  ]
};
*/


var path,
	partition,
	arc,
	radius,
	sunburstSVG,
	collectionsArray;

function sunburst(divObj, dataArray, width, height){

	collectionsArray = dataArray;

	radius = Math.min(width, height) / 2,
		color = d3.scale.category20c();

	sunburstSVG = divObj//d3.select("#"+divID)
		.append("svg")
		.attr("width", width)
		.attr("height", height)
	    .append("g")
		.attr("transform", "translate(" + width / 2 + "," + height * .52 + ")");

	partition = d3.layout.partition()
		.sort(null)
		.size([2 * Math.PI, radius * radius])
		.value(function(d) { return d.size; });

	arc = d3.svg.arc()
		.startAngle(function(d) { return d.x; })
		.endAngle(function(d) { return d.x + d.dx; })
		.innerRadius(function(d) { return Math.sqrt(d.y + d.dy/2); })
		.outerRadius(function(d) { return Math.sqrt(d.y + d.dy/1.1); });


	  path = sunburstSVG.datum(collectionsArray).selectAll("path")
		  .data(partition.nodes)
		  .enter().append("path")
		  .attr("display", function(d) { return d.depth ? null : "none"; }) // hide inner ring
		  .attr("d", arc)
		  .style("stroke", "#fff")
		  .style("fill", function(d) { return color((d.children ? d : d.parent).name); })
		  .style("fill-rule", "evenodd")
		  .style("opacity", 0.5)
      	  .on("mouseover", mouseover)
		  .on("mousemove", mousemove)
		  .on("mouseleave", mouseleave)
		  .on("click", function(d) { 
			  visualiseWordsCallback(d3.select(divObj.node().parentNode), $(divObj.node()).width(), $(divObj.node()).height(), d.name);
		   })
		  .each(stash);


	d3.select(self.frameElement).style("height", height + "px");

}


function mouseover(d,i){
	d3.selectAll("path").style("opacity", 0.5);
	d3.select(this).transition().style("opacity",1);
	showToolTip(" <b>Collection:</b> "+d.name+"<br> <b> # occurrences in collection:</b> "+d.value+" ",d3.mouse(d3.select('body')[0][0])[0]+6,d3.mouse(d3.select('body')[0][0])[1]+2,true);
}


function mousemove(){
	tooltipDiv.css({top:d3.mouse(d3.select('body')[0][0])[1]+2,left:d3.mouse(d3.select('body')[0][0])[0]+6});
}

function mouseleave(){
	d3.selectAll("path").transition().style("opacity", 0.5);
	showToolTip(" ",0,0,false);
}


function redrawSunburst(dataArray){
	collectionsArray = dataArray;

	partition = d3.layout.partition()
		.sort(null)
		.size([2 * Math.PI, radius * radius])
		.value(function(d) { 
			return d.value; 
		});

	path.data(partition.nodes)
	  	.transition()
	    .duration(1500)
	    .attrTween("d", arcTween);
}

// Stash the old values for transition.
function stash(d) {
  d.x0 = d.x;
  d.dx0 = d.dx;
}

// Interpolate the arcs in data space.
function arcTween(a) {
  var i = d3.interpolate({x: a.x0, dx: a.dx0}, a);
  return function(t) {
	var b = i(t);
	a.x0 = b.x;
	a.dx0 = b.dx;
	return arc(b);
  };
}
