
function bubbleplot(divID, width, height, dataJSON){
	
	var data = JSON.parse(dataJSON);
	
	var format = d3.format(",d"),
    color = d3.scale.category20c();


	d3.select("#"+divID).style("width", width + "px");
	d3.select("#"+divID).style("height", height + "px");
	
	
	var bubble = d3.layout.pack()
	    .sort(null)
	    .size([width, height])
	    .padding(1.5);
	
	var svg = d3.select("#"+divID)
		.append("svg");
	
	  var node = svg.selectAll(".node")
	      .data(bubble.nodes(classes(data))
	      .filter(function(d) { return !d.children; }))
	      .enter().append("g")
	      .attr("class", "node")
	      .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
	
	  node.append("title")
	      .text(function(d) { return d.className + ": " + format(d.value); });
	
	  node.append("circle")
	      .attr("r", function(d) { return d.r; })
	      .style("fill", function(d) { return color(d.packageName); });
	
	  node.append("text")
	      .attr("dy", ".3em")
	      .style("text-anchor", "middle")
	      .text(function(d) { return d.className.substring(0, d.r / 3); });

	
	// Returns a flattened hierarchy containing all leaf nodes under the root.
	function classes(root) {
	  var classes = [];
	
	  function recurse(name, node) {
	    if (node.children) node.children.forEach(function(child) { recurse(node.name, child); });
	    else classes.push({packageName: name, className: node.name, value: node.size});
	  }
	
	  recurse(null, root);
	  return {children: classes};
	}

}