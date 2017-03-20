/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */

function getXMLHTTPRequest() {
    var req = false;
	
    try {
        req = new XMLHttpRequest(); //Firefox etc
    } catch (err1) {
        try {
            req = new ActiveXObject("Msxml2.XMLHTTP"); // some IE editions
        } catch (err2) {
            try {
                req = new ActiveXObject("Microsoft.XMLHTTP"); //other IE editions
            } catch (err3) {
                req = false;
            }
        }
    }
	
    return req;
}

function minePage(doc_id) {
    scroll(0,0);
    
    var request = getXMLHTTPRequest();
       
    var url = "Servlet_MinePage";
    var params = "doc="+doc_id;
    var asynchr = true;
    var id = "resultsFirstPage";
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
    
    document.getElementById(id).innerHTML = "<br />&nbsp;<br />&nbsp;<center><img border=\"0\" src=\"files/graphics/mine-loader.gif\" /></center>";
	
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                document.getElementById(id).innerHTML = response;
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else { }
    }
    request.send(null);
}


function getAllResults() {
    scroll(0,0);
    
    var maxNumOfCategories = 15;
    var maxNumOfEntities = 50000;
    
    for (var l=0; l<100; l++) {
        var clt_id ='clt_'+l;
        if (document.getElementById(clt_id) != null) {
            document.getElementById(clt_id).style.color = 'blue';
            if (document.getElementById('img_'+clt_id) != null) {
                document.getElementById('img_'+clt_id).style.display = 'none';
            }    
            if (document.getElementById('a_'+clt_id) != null) {
                document.getElementById('a_'+clt_id).href = document.getElementById('a_'+clt_id).href.replace('unloadEntityResults','loadEntityResults');
            } else {
                document.getElementById(clt_id).href = document.getElementById(clt_id).href.replace('unloadEntityResults','loadEntityResults');
            }
        }
    }
    
    for (var i=0; i<maxNumOfCategories; i++) {
        for (var j=0; j<maxNumOfEntities; j++) {
            var element_id = i+'_'+j;

            if (document.getElementById(element_id) != null) {
                document.getElementById(element_id).style.color = 'blue';
                
                if (document.getElementById('img_'+element_id) != null) {
                    document.getElementById('img_'+element_id).style.display = 'none';
                }
                
                if (document.getElementById('a_'+element_id) != null) {
                    document.getElementById('a_'+element_id).href = document.getElementById('a_'+element_id).href.replace('unloadEntityResults','loadEntityResults');
                } else {
                    document.getElementById(element_id).href = document.getElementById(element_id).href.replace('unloadEntityResults','loadEntityResults');
                }
            }
        }
    }
    
    var request = getXMLHTTPRequest();

    var url = "Servlet_GetInitialResults";
    var asynchr = true;
    var id = "resultsFirstPage";
    
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?rand="+randomNumber;
    
    document.getElementById(id).innerHTML = "<br />&nbsp;<br />&nbsp;<center><img border=\"0\" src=\"files/graphics/mine-loader.gif\" /></center>";
	
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                document.getElementById(id).innerHTML = response;
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else {   }
    }
    request.send(null);
}

function getResults() {
    
    var request = getXMLHTTPRequest();
    
    var url = "GetAllResults";
    var params = "";
    var asynchr = true;
    var id = "resultsFirstPage";
    
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;

    document.getElementById(id).innerHTML = "<br />&nbsp;<br />&nbsp;<center><img border=\"0\" src=\"files/graphics/mine-loader.gif\" /></center>";

    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                if (response != undefined) {
                    document.getElementById(id).innerHTML = response;
                } 
            } 
        } else { }
    }
    request.send(null);
      
}


function checkElement(element, id_to_show, doc) {
    
    var request = getXMLHTTPRequest();
    request = getXMLHTTPRequest();
    
    var url = "ShowPage";
    var params = "element="+element+"&category="+id_to_show+"&doc="+doc;
    var asynchr = true;
    var id = id_to_show;
    
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
    
    document.getElementById(id).style.visibility = 'visible';
    document.getElementById(id).innerHTML = "&nbsp;&nbsp;&nbsp;<img border=\"0\" src=\"files/graphics/ajax-loader_special.gif\" />";
	
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                document.getElementById(id).style.visibility = 'visible';
                var response = request.responseText;
                document.getElementById(id).innerHTML = response;
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else {   }
    }
    request.send(null);
    
}


function unloadEntityResults(category, element, docids, element_id) {
    scroll(0,0);
    
    document.getElementById(element_id).style.color = 'blue';

    if (document.getElementById('img_'+element_id) != null) {
        document.getElementById('img_'+element_id).style.display = 'none';
    }
    
    if (document.getElementById('a_'+element_id) != null) {
        document.getElementById('a_'+element_id).href = "javascript:loadEntityResults('"+category+"','"+element+"', '"+docids+"', '"+element_id+"')";
    }else {
        document.getElementById(element_id).href = document.getElementById(element_id).href.replace('unloadEntityResults','loadEntityResults');
    }
    
    var request = getXMLHTTPRequest();
    
    var url = "Servlet_UnLoadEntityResults";
    var params = "category="+category+"&element="+element+"&docids="+docids;
    var asynchr = true;
    var id = "resultsFirstPage";
    
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
	
    document.getElementById(id).innerHTML = "<br />&nbsp;<br />&nbsp;<center><img border=\"0\" src=\"files/graphics/mine-loader.gif\" /></center>";
    
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;  
                document.getElementById(id).innerHTML = response;
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else {   }
    }
    request.send(null);
}

function loadEntityResults(category, element, docids, element_id) {
    scroll(0,0);
    
    
    document.getElementById(element_id).style.color = 'red';

    
    if (document.getElementById('img_'+element_id) != null) {
        document.getElementById('img_'+element_id).style.display = 'inline';
    }
    
    if (document.getElementById('a_'+element_id) != null) {
        document.getElementById('a_'+element_id).href = "javascript:unloadEntityResults('"+category+"','"+element+"', '"+docids+"', '"+element_id+"')";
    } else {
        document.getElementById(element_id).href = document.getElementById(element_id).href.replace('loadEntityResults','unloadEntityResults');
        document.getElementById('img_'+element_id).href = "javascript:unloadEntityResults('"+category+"','"+element+"', '"+docids+"', '"+element_id+"')";
    }
    
    var request = getXMLHTTPRequest();
    
    var url = "Servlet_LoadEntityResults";
    var params = "category="+category+"&element="+element+"&docids="+docids;
    var asynchr = true;
    var id = "resultsFirstPage";
    
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
	
    document.getElementById(id).innerHTML = "<br />&nbsp;<br />&nbsp;<center><img border=\"0\" src=\"files/graphics/mine-loader.gif\" /></center>";
    
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;  
                document.getElementById(id).innerHTML = response;
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else {   }
    }
    request.send(null);
}
function showAll(id, show) {
    
    var request = getXMLHTTPRequest();
    
    document.getElementById(id).style.display = 'block';
    document.getElementById(show).style.display = 'none';
    
    var data = document.getElementById(id).innerHTML;
  
    request = getXMLHTTPRequest();
    
    var url = "Servlet_LoadHiddenEntities";
    var params = "category="+id;
    if (data == "") {
        params += "&data=no";
    } else {
        params += "&data=yes";
    }
    var asynchr = true;
    
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
	
    if (data == "") {
        document.getElementById(id).innerHTML = "<img border=\"0\" src=\"files/graphics/ajax-loader_special.gif\" />";
    }   
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                if (data == "") {
                    document.getElementById(id).innerHTML = response;
                }
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else {    }
    }
    request.send(null);
   
}

function showAllText(id, show) {

    var request = getXMLHTTPRequest();
    
    document.getElementById(id).style.display = 'inline';
    document.getElementById(show).style.display = 'none';
      
    request = getXMLHTTPRequest();
    
    var url = "Servlet_ShowAll";
    var params = "id="+id;

    var asynchr = true;
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
    
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else {    }
    }
    request.send(null);

}



function sure() {
    if (document.getElementById('suggestion').value == "" || document.getElementById('suggestion').value == " " || document.getElementById('suggestion').value == "  " || document.getElementById('suggestion').value == "   " || document.getElementById('suggestion').value ==  "    ") {
        document.getElementById('resultsFirstPage').innerHTML = " ";
        document.getElementById('clusterLabelTree').innerHTML = " ";
        document.getElementById('clusterLabelTree2').innerHTML = " ";
        alert("What to search???");
        return false;
    }

    document.getElementById('resultsFirstPage').innerHTML = "<center>loading...</center>";
    document.getElementById('clusterLabelTree').innerHTML = " ";
    document.getElementById('clusterLabelTree2').innerHTML = " ";
    if (document.getElementById('mining_checkbox').checked == true) {

        if (document.getElementById('onlySnippets').checked != true) {
            var close = confirm("You chose to mine/cluster the content of the results. \nThis is very time consuming, specially for large number of top results. \nAre you sure you want to continue;");
            if (close) {
                document.getElementById('resultsFirstPage').innerHTML = "<center>loading...</center>";
                document.getElementById('clusterLabelTree').innerHTML = " ";
                document.getElementById('clusterLabelTree2').innerHTML = " ";
                return true;
            }
            else {
                document.getElementById('resultsFirstPage').innerHTML = " ";
                document.getElementById('clusterLabelTree').innerHTML = " ";
                document.getElementById('clusterLabelTree2').innerHTML = " ";
                return false;
            }
        } else {
            document.getElementById('resultsFirstPage').innerHTML = "<center>loading...</center>";
            document.getElementById('clusterLabelTree').innerHTML = " ";
            document.getElementById('clusterLabelTree2').innerHTML = " ";
            return true;
        }
    } else {
        document.getElementById('resultsFirstPage').innerHTML = "<center>loading...</center>";
        document.getElementById('clusterLabelTree').innerHTML = " ";
        document.getElementById('clusterLabelTree2').innerHTML = " ";
        return true;
    }
    
}

function hide_data() {
    document.getElementById('resultsFirstPage').innerHTML = " ";
    document.getElementById('clusterLabelTree').innerHTML = " ";
}


function display_options() {
    document.getElementById('image_advance').style.display = "none";
    document.getElementById('advance_options').style.display = "block";
    
}

function hide_options() {
    document.getElementById('image_advance').style.display = "block";
    document.getElementById('advance_options').style.display = "none";   
}

function change_clustering() {
    if (document.getElementById('clustering_checkbox').checked != true) {
        document.getElementById('clustering_num_option2').disabled = true;
        document.getElementById('clusterLabelTree2').style.visibility = "hidden";
     
        
    } else {
        document.getElementById('clustering_num_option2').disabled = false;
        document.getElementById('clusterLabelTree2').style.visibility = "visible";

    }
}

function change_mining() {
    if (document.getElementById('mining_checkbox').checked != true) {
        document.getElementById('clusterLabelTree').style.visibility = "hidden";
    } else {
        document.getElementById('clusterLabelTree').style.visibility = "visible";
    }
}


function inspectElement(category, element, element_id) {
    
    inspRequest = getXMLHTTPRequest();
    inspRequest.abort();
    
    var url = "InspectEntity";
    var params = "category="+category+"&element="+element;
    var asynchr = true;
        
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;

    var pos = $("#"+element_id).position();
    var width = $("#"+element_id).outerWidth();
    
    var newbubbleid = "bubble"+randomNumber;
    var newbubble = createNewPopup(newbubbleid);
    document.getElementById('clusterLabelTree').appendChild(newbubble);           
    //show the menu directly over the placeholder
    $("#"+newbubbleid).css({
        position: "absolute",
        top: (pos.top+15) + "px",
        left: (pos.left-50) + "px"
    }).show();
    
    var newpos = $("#"+newbubbleid).position();
    scrollTo(newpos.left,newpos.top);
                
    document.getElementById(newbubbleid).style.display = "block";
    document.getElementById(newbubbleid+"Data").innerHTML = "<center><img border=\"0\" src=\"files/graphics/ajax-loader_transparent.gif\" /><h3>please wait..retrieving information...</h3></center>";
    inspRequest.open("GET", fullURL, asynchr);
    inspRequest.onreadystatechange = function () {
        if (inspRequest.readyState == 4) {
            if (inspRequest.status == 200) {
                var response = inspRequest.responseText;
                document.getElementById(newbubbleid+"Data").innerHTML = response;
            } else {
        //alert("An error has occured: "+ inspRequest.statusText);
        }
        } else {   }
    }
    inspRequest.send(null);
    
}

function closePopup() {
    
    var request = getXMLHTTPRequest();
    
    var url = "StopSparqlRunner";
    var asynchr = true;
        
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?rand="+randomNumber;
     
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else {    }
    }
    request.send(null);
    
    inspRequest.abort();
    document.getElementById("bubbleFirst").style.display = "none";
}

function closeBubble(id) {
    
    var request = getXMLHTTPRequest();
    
    var url = "StopSparqlRunner";
    var asynchr = true;
        
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?rand="+randomNumber;
     
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else {    }
    }
    request.send(null);
    
    inspRequest.abort();
    document.getElementById(id).style.display = "none";
}


function showProperties(category, uri, bubbleid) {
    inspRequest = getXMLHTTPRequest();
    inspRequest.abort();
   
    var url = "ShowProperties";
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var params = "rand="+randomNumber+"&category="+category+"&uri="+uri;
    var asynchr = true;

    var fullURL = url+"?"+params;
    var pos = $("#"+bubbleid).position();
    //scrollTo(pos.left,pos.top);
    
    var newbubbleid = "bubble"+randomNumber;
    var newbubble = createNewPopup(newbubbleid);
    document.getElementById('clusterLabelTree').appendChild(newbubble);           
    //show the menu directly over the placeholder
    $("#"+newbubbleid).css({
        position: "absolute",
        top: (pos.top+45) + "px",
        left: (pos.left+20) + "px"
    }).show();
    
    var newpos = $("#"+newbubbleid).position();
    scrollTo(newpos.left,newpos.top);
                
    document.getElementById(newbubbleid).style.display = "block";
    document.getElementById(newbubbleid+"Data").innerHTML = "<center><img border=\"0\" src=\"files/graphics/ajax-loader_transparent.gif\" /><h3>please wait..retrieving information...</h3></center>";
    inspRequest.open("GET", fullURL, asynchr);
    inspRequest.onreadystatechange = function () {
        if (inspRequest.readyState == 4) {
            if (inspRequest.status == 200) {
                var response = inspRequest.responseText;
                document.getElementById(newbubbleid+"Data").innerHTML = response;
            } else {
        //alert("An error has occured: "+ inspRequest.statusText);
        }
        } else {    }
    }
    inspRequest.send(null);
}


function createNewPopup(id) {
    var maindiv = document.createElement('div');
    maindiv.setAttribute('id', id);
    maindiv.setAttribute('class', "bubble");
    
    var headerdiv = document.createElement('div');
    headerdiv.setAttribute('class', "bubbleHeader");
    headerdiv.innerHTML = '<font class="popup_title">Entity Exploration</font>&nbsp;'+"<a class=\"closePopup\" href=\"javascript:closeBubble('"+id+"')\">(close)</a>";
    
    var datadiv = document.createElement('div');
    datadiv.setAttribute("id", id+'Data');
    datadiv.setAttribute('class', "bubbleData");
    
    var footerdiv = document.createElement('div');
    footerdiv.setAttribute('class', "bubbleFooter");
    footerdiv.innerHTML = "<a class=\"closePopup\" href=\"javascript:closeBubble('"+id+"')\">(close)</a>";
    
    maindiv.appendChild(headerdiv);
    maindiv.appendChild(datadiv);
    maindiv.appendChild(footerdiv);
    
    return maindiv;
}