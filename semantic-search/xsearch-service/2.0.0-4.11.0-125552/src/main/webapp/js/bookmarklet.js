

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


function inspectEntity(category, element, element_id) {
    
    request = getXMLHTTPRequest();
    request.abort();
    
    var url = "InspectEntity";
    var params = "category="+category+"&element="+element;
    var asynchr = true;
        
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;


    var pos = $("#"+element_id).position();
    var width = $("#"+element_id).outerWidth();

    //show the menu directly over the placeholder
    $("#bubbleInfo").css({
        position: "absolute",
        top: pos.top + "px",
        left: (pos.left + width) + "px"
    }).show();
    
    //document.getElementById("bubbleInfo").style.display = "block";
    document.getElementById("popup").innerHTML = "<center><img border=\"0\" src=\"files/graphics/ajax-loader_transparent.gif\" /></center>";
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                document.getElementById("popup").innerHTML = response;
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else {   }
    }
    request.send(null);
    
    return false;
}

function inspectEntityInSidebar(category, element, element_id)  {
    request = getXMLHTTPRequest();
    request.abort();
    
    var url = "InspectEntity";
    var params = "category="+category+"&element="+element;
    var asynchr = true;
        
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;


    var pos = $("#"+element_id).position();
    var width = $("#"+element_id).outerWidth();

    //show the menu directly over the placeholder
    $("#bubbleInfo").css({
        position: "absolute",
        top: pos.top + "px",
        left: (pos.left + width) + "px"
    }).show();
    
    //document.getElementById("bubbleInfo").style.display = "block";
    document.getElementById("popup").innerHTML = "<center><img border=\"0\" src=\"files/graphics/ajax-loader_transparent.gif\" /></center>";
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                document.getElementById("popup").innerHTML = response;
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else {   }
    }
    request.send(null);
}

function closePopup() {
    
    var abrequest = getXMLHTTPRequest();
    
    var url = "StopSparqlRunner";
    var asynchr = true;
        
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?rand="+randomNumber;
     
    abrequest.open("GET", fullURL, asynchr);
    abrequest.onreadystatechange = function () {
        if (abrequest.readyState == 4) {
            if (abrequest.status == 200) {
                var response = abrequest.responseText;
            } else {
                alert("An error has occured: "+ abrequest.statusText);
            }
        } else {    }
    }
    abrequest.send(null);
    
    document.getElementById("bubbleInfo").style.display = "none";
}


function hoverText(category, entity, element_id) {
    
    closePopup();
    
    var pos = $("#"+element_id).position();
    var width = $("#"+element_id).outerWidth();

    //show the menu directly over the placeholder
    $("#bubbleInfo").css({
        position: "absolute",
        top: pos.top + "px",
        left: (pos.left + width) + "px"
    }).show();
    
    var shownEntity = entity.replace('^^^^^','%');
    var content = "&bull;&nbsp;Entity: " + shownEntity + "<br />" +
    "&bull;&nbsp;Category: " + category + "<br />" +
    "<a href=\"javascript:inspectElement('"+category+"','"+entity+"','"+element_id+"');\">get more information about this entity</a><br />";
    
    document.getElementById("popup").innerHTML = content;
    
    return false;
}

function showProperties(category, uri) {
    inspRequest = getXMLHTTPRequest();
    inspRequest.abort();
    
    var url = "ShowProperties";
    var params = "category="+category+"&uri="+uri;
    var asynchr = true;
        
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
     
    var pos = $("#bubbleInfo").position();
    scrollTo(pos.left,pos.top);
     
    document.getElementById("popup").innerHTML = "<center><img border=\"0\" src=\"files/graphics/ajax-loader_transparent.gif\" /><h3>please wait..retrieving properties...</h3></center>";
    inspRequest.open("GET", fullURL, asynchr);
    inspRequest.onreadystatechange = function () {
        if (inspRequest.readyState == 4) {
            if (inspRequest.status == 200) {
                var response = inspRequest.responseText;
                document.getElementById("popup").innerHTML = response;
            } else {
                //alert("An error has occured: "+ inspRequest.statusText);
            }
        } else {    }
    }
    inspRequest.send(null);
}