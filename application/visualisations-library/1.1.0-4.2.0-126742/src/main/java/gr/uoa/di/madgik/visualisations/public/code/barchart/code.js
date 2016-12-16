function horizontalbarchart(divID, width, height, dataJSON){
	
	var margin = {top: 30, right: 10, bottom: 10, left: 10},
	    width = width - margin.left - margin.right,
	    height = height - margin.top - margin.bottom;
	
	var x = d3.scale.linear()
	    .range([0, width]);
	
	var y = d3.scale.ordinal()
	    .rangeRoundBands([0, height], .2);
	
	var xAxis = d3.svg.axis()
	    .scale(x)
	    .orient("top");
	
	var svg = d3.select("#"+divID).append("svg")
	    .attr("width", width + margin.left + margin.right)
	    .attr("height", height + margin.top + margin.bottom)
	  .append("g")
	    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	

	var data = [{"name":"Νικόλας","value":15},{"name":"Πλάτωνας","value":20},{"name":"Σωκράτης","value":23},{"name":"Κανένας","value":26}];
	
	  x.domain(d3.extent(data, function(d) { return d.value; })).nice();
	  y.domain(data.map(function(d) { return d.name; }));
	
	  svg.selectAll(".bar")
	      .data(data)
	    .enter().append("rect")
	    .attr("width", function(d) { return 0;})
	     .transition().duration(1500)
	      .attr("class", function(d) { return d.value < 0 ? "bar negative" : "bar positive"; })
	      .attr("x", function(d) { return x(Math.min(0, d.value)); })
	      .attr("y", function(d) { return y(d.name); })
	      .attr("width", function(d) { return Math.abs(x(d.value) - x(0)); })
	      .attr("height", y.rangeBand());
	
	  svg.append("g")
	      .attr("class", "x axis")
	      .call(xAxis);
	
	  svg.append("g")
	      .attr("class", "y axis")
	    .append("line")
	      .attr("x1", x(0))
	      .attr("x2", x(0))
	      .attr("y2", height);
	
	function type(d) {
	  d.value = +d.value;
	  return d;
	}

}