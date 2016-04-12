/*
A sample keywordsArray has this form: 
keywordsArray = [
	{key:"cat", value: 100},
	{key:"mouse", value: 170},
	{key:"elephant", value: 63},
	{key:"girraffe", value: 92},
	{key:"dog", value: 123},
	{key:"crocodile", value: 19},
	{key:"fish", value: 176},
];

*/

var fill = d3.scale.category20b();

var w,
	h;

var words = [],
	max,
	scale = 1,
	complete = 0,
	tags,
	font = "Impact",
	fontSize,
	maxWords,
	statusText = d3.select("#status");

var layout;

var wordcloudSVG;

var background;


function generate() {
  layout.font(font).spiral("archimedean"); //alternative spiral: "rectangular"
  fontSize = d3.scale["sqrt"]().range([10, 100]); //alternatively scaling could be one of: {"log","linear"} 
  if (tags.length) fontSize.domain([+tags[tags.length - 1].value || 1, +tags[0].value]);
  complete = 0;
  //statusText.style("display", null);
  words = [];
  layout.stop().words(tags.slice(0, max = Math.min(tags.length, + maxWords))).start();
}


function progress(d) {
  statusText.text(++complete + "/" + max);
}


function draw(data, bounds) {
  statusText.style("display", "none");
  scale = bounds ? Math.min(
	  w / Math.abs(bounds[1].x - w / 2),
	  w / Math.abs(bounds[0].x - w / 2),
	  h / Math.abs(bounds[1].y - h / 2),
	  h / Math.abs(bounds[0].y - h / 2)) / 2 : 1;
  words = data;
  var text = vis.selectAll("text")
	  .data(words, function(d) { 
			return d.text.toLowerCase(); 
  });
  text.transition()
	  .duration(1000)
	  .attr("transform", function(d) { return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")"; })
	  .style("font-size", function(d) { return d.size + "px"; });
  text.enter().append("text")
	  .attr("text-anchor", "middle")
	  .attr("transform", function(d) { return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")"; })
	  .style("font-size", function(d) { return d.size + "px"; })
	  .on("click", function(d) {
//			updateWordCloud([{"key":d.text,"value":0}], 1, 1, 0, 0);
//			reformCollectionsByOccurrences(d.text);
	  })
	  .on("mouseleave", function(d,i){d3.select(this).transition().style("font-weight","normal");})
	  .on("mouseover", function(d,i){d3.select(this).transition().style("font-weight","bold");})
	  .style("opacity", 1e-6)
	.transition()
	  .duration(1000)
	  .style("opacity", 1);
  text.style("font-family", function(d) { return d.font; })
	  .style("fill", function(d) { return fill(d.text.toLowerCase()); })
	  .text(function(d) { return d.text; });
  var exitGroup = background.append("g")
	  .attr("transform", vis.attr("transform"));
  var exitGroupNode = exitGroup.node();
  text.exit().each(function() {
	exitGroupNode.appendChild(this);
  });
  exitGroup.transition()
	  .duration(1000)
	  .style("opacity", 1e-6)
	  .remove();
  vis.transition()
	  .delay(1000)
	  .duration(750)
	  .attr("transform", "translate(" + [w >> 1, h >> 1] + ")scale(" + scale + ")");
}


function load(){

	tags = d3.entries(tags).sort(function(a, b) { 
		return b.value.value - a.value.value;
	});
	for(var i=0;i<tags.length;i++){
		tags[i] = tags[i].value;
	}
	generate();
}

  var r = 40.5,
	  px = 35,
	  py = 20;


  var radians = Math.PI / 180,
	  numAngles,
	  from,
	  to,
	  scaleLinear = d3.scale.linear(),
	  arc = d3.svg.arc()
	        .innerRadius(0)
	        .outerRadius(r);

function getAngles() {
	numAngles = +numAngles;
	from = Math.max(-90, Math.min(90, +from));
	to = Math.max(-90, Math.min(90, +to));
	update();
}

function update() {
	scaleLinear.domain([0, numAngles - 1]).range([from, to]);
	var step = (to - from) / numAngles;

	layout.rotate(function() {
		return scaleLinear(~~(Math.random() * numAngles));
	});
}

  function cross(a, b) { return a[0] * b[1] - a[1] * b[0]; }
  function dot(a, b) { return a[0] * b[0] + a[1] * b[1]; }


function createWordCloud(divElem, width, height, keywordsArray, maxWordsVis, angleCount, angleFrom, angleTo){

//	d3.select("#"+divElemID).html("");
	divElem.html("");
//	divElem.empty();
	
	w = width;
	h = height;
	maxWords = maxWordsVis;
	numAngles = angleCount;
	from = angleFrom;
	to = angleTo;

 	layout = d3.layout.cloud()
		.timeInterval(10)
		.size([w, h])
		.fontSize(function(d) { return fontSize(+d.value); })
		.text(function(d) { return d.key; })
		.on("word", progress)
		.on("end", draw);

	wordcloudSVG = divElem
		.append("svg")
		.attr("width", w)
		.attr("height", h);

	background = wordcloudSVG.append("g"),
	vis = wordcloudSVG.append("g")
		.attr("transform", "translate(" + [w >> 1, h >> 1] + ")");

	d3.select("#random-palette").on("click", function() {
	  paletteJSON("http://www.colourlovers.com/api/palettes/random", {}, function(d) {
		fill.range(d[0].colors);
		vis.selectAll("text").style("fill", function(d) { return fill(d.text.toLowerCase()); });
	  });
	  d3.event.preventDefault();
	});

	tags = keywordsArray;
	getAngles();
	load();

}

function updateWordCloud(keywordsArray, maxWordsVis, angleCount, angleFrom, angleTo){
	tags = keywordsArray;
	maxWords = maxWordsVis;
	numAngles = angleCount;
	from = angleFrom;
	to = angleTo;
	getAngles();
	load();
}



