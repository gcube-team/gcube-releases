function circleclusters(divID, width, height, dataJSON){
	
	var data = JSON.parse(dataJSON);
	
	var format = d3.format(",d");
	
	var pack = d3.layout.pack()
	    .size([width - 4, height - 4])
	    .value(function(d) { return d.size; });
	
	var svg = d3.select("#"+divID).append("svg")
	    .attr("width", width)
	    .attr("height", height)
	    .append("g")
	    .attr("transform", "translate(2,2)");
	
    var node = svg.datum(data).selectAll(".node")
        .data(pack.nodes)
        .enter().append("g")
        .attr("class", function(d) { return d.children ? "node" : "leaf node"; })
        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

    node.append("title")
        .text(function(d) { return d.name + (d.children ? "" : ": " + format(d.size)); });

    node.append("circle")
        .attr("r", function(d) { return d.r; });

    node.filter(function(d) { return !d.children; }).append("text")
        .attr("dy", ".3em")
        .style("text-anchor", "middle")
        .text(function(d) { return d.name.substring(0, d.r / 3); });

	
	d3.select(self.frameElement).style("height", height + "px");

}