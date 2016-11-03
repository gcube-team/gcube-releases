var tooltipDiv;
//var wordcloudDIVid = "wordcloud";
//var sunburstDIVid = "sunburst";
var wordCloudInitiated = 0;

var allWordCloudsDataObject;


function showToolTip(pMessage,pX,pY,pShow)
{
  if (typeof(tooltipDiv)=="undefined")
  {
             tooltipDiv = $('<div id="DocVisualisationToolTipDiv" style="position:absolute;display:block;z-index:10000;border:2px solid black;background-color:rgba(0,0,0,0.8);margin:auto;padding:3px 5px 3px 5px;color:white;font-size:12px;font-family:arial;border-radius: 5px;vertical-align: middle;text-align: center;min-width:50px;overflow:auto;"></div>');
		$('body').append(tooltipDiv);
  }
  if (!pShow) { tooltipDiv.hide(); return;}
  tooltipDiv.html(pMessage);
  tooltipDiv.css({top:pY,left:pX});
  tooltipDiv.show();
}



function align(divElem){
	if(divElem==undefined){
		console.log("divElem is undefined, skipping alignment");
		return;
	}
	
	var wordcloudObj = divElem.select("#wordcloud");
	var sunburstObj = divElem.select("#sunburst");

	if((wordcloudObj==undefined)||(sunburstObj==undefined)||(wordcloudObj.size()<1)||(sunburstObj.size()<1)){
		console.log("either wordcloud or sunburst container is undefined, skipping alignment");
		return;
	}
	
	wordcloudObj[0][0].style.zIndex = sunburstObj[0][0].style.zIndex + 1;
	var wordcloudHeight = wordcloudObj[0][0].clientHeight;
	var wordcloudWidth = wordcloudObj[0][0].clientWidth;
	var sunburstHeight = sunburstObj[0][0].clientHeight;
	var sunburstWidth = sunburstObj[0][0].clientWidth;
	wordcloudObj[0][0].style.top = ((sunburstHeight-wordcloudHeight)/2).toString()+"px";
	wordcloudObj[0][0].style.left = ((sunburstWidth-wordcloudWidth)/2).toString()+"px";
	
	console.log("aligning, wordcloudHeight:"+wordcloudHeight+" wordcloudWidth:"+wordcloudWidth+" sunburstHeight:"+sunburstHeight+" sunburstWidth:"+sunburstWidth+" wordcloudObj[0][0].style.top:"+wordcloudObj[0][0].style.top+" wordcloudObj[0][0].style.left:"+wordcloudObj[0][0].style.left);
	
	wordcloudObj[0][0].click( function(evt) { 
		evt.stopPropagation(); 
	});
	
}


function visualiseAllCollections(divElem, width, height, collectionsJSON, allWordCloudsJSON){
	allWordCloudsDataObject = JSON.parse(allWordCloudsJSON);
	visualiseCollections(divElem, width, height, collectionsJSON, true);
	visualiseWords(divElem, width, height, wordsJSON, false, createnew)
}


//divElem: the container of the whole visualisation, usually the parent of this 
//collectionsJSON: the list of the collections in json format
//createnew: boolean flag -> true for creating new one, false for updating existing one
function visualiseCollections(divElem, width, height, collectionsJSON, createnew){	
//	alert("createnew visualiseCollections: "+createnew);
	if(createnew)
		sunburst(divElem.select("#sunburst"), JSON.parse(collectionsJSON), width, height);
	else
		redrawSunburst(collectionsJSON);
//	alert("ended: visualiseCollections");
//	align(divElem);
}

//divElem: the container of the whole visualisation, usually the parent of this 
//wordsJSON: the list of the words in json format
//createnew: boolean flag -> true for creating new one, false for updating existing one
function visualiseWords(divElem, width, height, wordsJSON, useAllSpace, createnew){
	if(!useAllSpace){ //this is when we have the outer ring of collections (sunburst)
		width = width * 0.75;
		height = height * 0.75;
	}
		
//	alert("createnew visualiseWords: "+createnew);
	if(createnew)
		createWordCloud(divElem.select("#wordcloud"), width , height , JSON.parse(wordsJSON),150,20,-60,60,100);
	else
		updateWordCloud(wordsJSON,150,20,-60,60,100);
//	alert("ended: visualiseWords");
//	align(divElem);
}




function visualiseWordsCallback(divElem, width, height, collName){
	if(wordCloudInitiated == 0){
		createWordCloud(divElem.select("#wordcloud"), width * 0.75, height * 0.75, JSON.stringify(allWordCloudsDataObject[collName]), 150,20,-60,60,100);
		wordCloudInitiated = 1;
	}
	else{
		updateWordCloud(allWordCloudsDataObject[collName],150,20,-60,60,100);
	}
//	align();
}





/*
function visualiseCollection(collectionName){
	switch(collectionName){
		case 'Alice in Wonderland':
			visualiseWords("Collections/AliceWonderland_count");
			break;
		case 'A Christmas Carol':
			visualiseWords("Collections/ChristmasCarol_count");
			break;
		case 'Moby Dick':
			visualiseWords("Collections/MobyDick_count");
			break;
		case 'Peter Pan':
			visualiseWords("Collections/PeterPan_count");
			break;
		case 'Sherlock Holmes':
			visualiseWords("Collections/SherlockHolmes_count");
			break;
		case 'The Adventures of Tom Sawer':
			visualiseWords("Collections/TomSawer_count");
			break;
	}
}


function visualiseWords(filepath){
	d3.json(filepath, function( data ) {
		if(wordCloudInitiated == 0){
			createWordCloud("wordcloud", 600,600,data,150,20,-60,60,100);
			wordCloudInitiated = 1;
		}
		else{
			updateWordCloud(data,150,20,-60,60,100);
		}
		align();
	});
}

function visualiseCollections(filepath){	
	d3.json(filepath, function( data ) {
		sunburst("sunburst", data, 800,800);
		align();
	});
}


var completed;
function reformCollectionsByOccurrences(word){
	completed = 0;
	for(var i=0;i<collectionsArray.children.length;i++)
		collectionsArray.children[i].size = 0;
	getOccurrences("Collections/AliceWonderland_count","Alice in Wonderland");
	getOccurrences("Collections/ChristmasCarol_count","A Christmas Carol");
	getOccurrences("Collections/MobyDick_count","Moby Dick");
	getOccurrences("Collections/PeterPan_count","Peter Pan");
	getOccurrences("Collections/SherlockHolmes_count","Sherlock Holmes");
	getOccurrences("Collections/TomSawer_count","The Adventures of Tom Sawer");
	
	function getOccurrences(filepath,documentName){
		d3.json(filepath, function( data ) {
			var value = 0;
			for(var i=0;i<data.length;i++){
				if(data[i].key==word){
					value = data[i].value;
					break;
				}
			}
			for(var pos=0;pos<collectionsArray.children.length;pos++)
				if(collectionsArray.children[pos].name == documentName)
					collectionsArray.children[pos].value = value;
			completed = completed + 1;
			if(completed > 5) //if all have returned
				redrawSunburst(collectionsArray);
		});
	}
	
}

*/


function transformFilterStopwords (completeFacetsJSON) {
    
    var stopwords = 
    	["a", "an", "and", "are", "as", "at", "be", "but", "by",
    	"for", "if", "in", "into", "is", "it",
    	"no", "not", "of", "on", "or", "such",
    	"that", "the", "their", "then", "there", "these",
    	"they", "this", "to", "was", "will", "with"];

    	Array.prototype.remove = function(from, to) {
    	  var rest = this.slice((to || from) + 1 || this.length);
    	  this.length = from < 0 ? this.length + from : from;
    	  return this.push.apply(this, rest);
    	};

    	function isStopword(word){
    		for(var i=0;i<stopwords.length;i++)
    			if(stopwords[i]==word)
    				return true;
    		return false;
    	}

    	var termsObj = JSON.parse(completeFacetsJSON).facets.tag.terms;

    	//filter stopwords
    	for(var i=termsObj.length-1; i>-1; i--){
    		if(isStopword(termsObj[i].term))
    			termsObj.remove(i);
    	}

    	var termsJSON = JSON.stringify(termsObj);

    	return termsJSON.replace("\"term\":","\"key\":").replace("\"count\":","\"value\":");
	
	
}

