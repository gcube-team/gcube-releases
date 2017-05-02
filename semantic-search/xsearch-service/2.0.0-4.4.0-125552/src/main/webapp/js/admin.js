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

function allowInputEditing(id1, id2, id3, id4) {
    document.getElementById(id1).disabled = false;
    document.getElementById(id1).focus();
    document.getElementById(id1).select();
    document.getElementById(id2).style.display = "none";
    document.getElementById(id3).style.display = "inline";
    document.getElementById(id4).style.display = "none";
}

function allowTextAreaEditing(id1, id2, id3) {
    document.getElementById(id1).disabled = false;
    document.getElementById(id1).focus();
    document.getElementById(id1).select();
    document.getElementById(id2).style.display = "none";
    document.getElementById(id3).style.display = "inline";
}

function allowSelectEditing(id1, id2, id3) {   
    document.getElementById(id1).disabled = false;
    document.getElementById(id2).style.display = "none";
    document.getElementById(id3).style.display = "inline";
}


function changeDescriptionDocument(ses) {
    
    var request = getXMLHTTPRequest();
    
    var descriptionDocument = document.getElementById("descriptionDocument").value;
        
    var url = "ChangeDescriptionDocument";
    var params = "doc="+encodeURIComponent(descriptionDocument);
    if (ses=="y") {
        params =  "ses=y&"+params;
    } 
    
    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
    
    document.getElementById("changeDescriptionDocument").style.display = "none";
    document.getElementById("descriptionDocumentImg").style.display = "inline";
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                if (response != "") {
                    document.getElementById("errormessage").innerHTML = response;
                    document.getElementById("errormessage").style.display = "block";
                    document.getElementById("descriptionDocument").value = document.getElementById("initialDescrDoc").value;
                } else {
                    document.getElementById("errormessage").innerHTML = "";
                    document.getElementById("errormessage").style.display = "none";
                    document.getElementById("initialDescrDoc").value = descriptionDocument;
                }
                
                document.getElementById("descriptionDocumentImg").style.display = "none";
                document.getElementById("descriptionDocument").disabled = true;
                document.getElementById("editDescriptionDocument").style.display = "inline";
                document.getElementById("filecontents").style.display = "inline";
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else { }
    }
    request.send(null);
 
}


function changeEndpoint(category) {
    
    var request = getXMLHTTPRequest();
    var endpointInputId = "endpointValueOf"+category;
    var newEndpoint = document.getElementById(endpointInputId).value;
        
    var url = "ChangeEndpoint";
    var params = "category="+category+"&endpoint="+encodeURIComponent(newEndpoint);
    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
    
    document.getElementById("endpointChangeOf"+category).style.display = "none";
    document.getElementById("endpointImgOf"+category).style.display = "inline";
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                if (response != "") {
                    document.getElementById("endpointErrorMessageOf"+category).innerHTML = response;
                    document.getElementById("endpointErrorMessageOf"+category).style.display = "block";
                    document.getElementById(endpointInputId).value = document.getElementById("initialEndpointOf"+category).value;
                } else {
                    document.getElementById("endpointErrorMessageOf"+category).innerHTML = "";
                    document.getElementById("endpointErrorMessageOf"+category).style.display = "none";
                    document.getElementById("initialEndpointOf"+category).value = newEndpoint;
                }
                
                document.getElementById("endpointImgOf"+category).style.display = "none";
                document.getElementById(endpointInputId).disabled = true;
                document.getElementById("enpointEditOf"+category).style.display = "inline";
                document.getElementById("endpointLinkOf"+category).style.display = "inline";
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else { }
    }
    request.send(null);
          
    
}


function changeTemplate(category) {
    
    var request = getXMLHTTPRequest();
    var templateInputId = "templateValueOf"+category;
    var newTemplate = document.getElementById(templateInputId).value;
        
    var url = "ChangeTemplate";
    var params = "category="+category+"&template="+encodeURIComponent(newTemplate);
    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    params = params + "&rand="+randomNumber;
    document.getElementById("templateImgOf"+category).style.display = "inline";
    request.open("POST", url, asynchr);
    
    request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    request.setRequestHeader("Content-length", params.length);
    request.setRequestHeader("Connection", "close");
    
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                if (response != "") {
                    document.getElementById("templateErrorMessageOf"+category).innerHTML = response;
                    document.getElementById("templateErrorMessageOf"+category).style.display = "block";
                //document.getElementById(templateInputId).value = document.getElementById("initialTemplateOf"+category).value;
                } else {
                    document.getElementById("templateChangeOf"+category).style.display = "none";
                    document.getElementById(templateInputId).disabled = true;
                    document.getElementById("templateEditOf"+category).style.display = "inline";
                    document.getElementById("templateErrorMessageOf"+category).innerHTML = "";
                    document.getElementById("templateErrorMessageOf"+category).style.display = "none";
                    document.getElementById("initialTemplateOf"+category).value = newTemplate;  
                }           
                document.getElementById("templateImgOf"+category).style.display = "none";
                
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else { }
    }
    request.send(params);
          
    
}


function changeMinequery(ses) {
    
    var request = getXMLHTTPRequest();
    
    var minequery = document.getElementById("mineQuery").value;
        
    var url = "ChangeMineQuery";
    var params = "minequery="+minequery;
    if (ses=="y") {
        params =  "ses=y&"+params;
    } 
    
    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
    
    document.getElementById("changeMinequery").style.display = "none";
    document.getElementById("mineQueryImg").style.display = "inline";
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                
                document.getElementById("mineQueryImg").style.display = "none";
                document.getElementById("mineQuery").disabled = true;
                document.getElementById("editMinequery").style.display = "inline";
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else { }
    }
    request.send(null);
          
    
}


function changeClusteringAlgorithm(ses) {
    
    var request = getXMLHTTPRequest();
    
    var alg = document.getElementById("clusteringAlgorithm").value;
        
    var url = "ChangeClusteringAlgorithm";
    var params = "alg="+alg;
    if (ses=="y") {
        params =  "ses=y&"+params;
    } 
    
    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
    
    document.getElementById("changeClusteringAlgorithm").style.display = "none";
    document.getElementById("clusteringAlgorithmImg").style.display = "inline";
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                
                document.getElementById("clusteringAlgorithmImg").style.display = "none";
                document.getElementById("clusteringAlgorithm").disabled = true;
                document.getElementById("editClusteringAlgorithm").style.display = "inline";
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else { }
    }
    request.send(null);
          
}

function removeAcceptedCategory(category, ses) {
    
    var request = getXMLHTTPRequest();
        
    var url = "RemoveAcceptedCategory";
    var params = "category="+category;
    if (ses=="y") {
        params =  "ses=y&"+params;
    } 
    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
    
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                if (ses=="y") {
                    window.location = "sesadmin.jsp";
                } else {
                    window.location = "admin.jsp";
                }
                

            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else { }
    }
    request.send(null);
          
          
}

function removeEntityEnrichment(category, ses) {
    
    var request = getXMLHTTPRequest();
       
    var url = "RemoveEntityEnrichment";
    var params = "category="+category;
    if (ses=="y") {
        params =  "ses=y&"+params;
    } 
    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;

    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                if (ses=="y") {
                    window.location = "sesadmin.jsp";
                } else {
                    window.location = "admin.jsp";
                }

            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else { }
    }
    request.send(null);
          
          
}

function addNewCategory(){
    var request = getXMLHTTPRequest();   
    var url = "AddNewCategory";
    
    var categoryName = document.getElementById("newCategoryName").value;
    var list = document.getElementById("newCategoryList").value;
    
    var params = "categoryName="+categoryName+"&list="+encodeURIComponent(list);
    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    params = params + "&rand="+randomNumber;
    document.getElementById("newCategoryQueryImage").style.display = "inline";
    request.open("POST", url, asynchr);
    
    request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    request.setRequestHeader("Content-length", params.length);
    request.setRequestHeader("Connection", "close");
 
    
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                
                var response = request.responseText;
                if (response != "") {
                    document.getElementById("newCategoryQueryErrorMsg").innerHTML = response;
                    document.getElementById("newCategoryQueryErrorMsg").style.display = "block";
                } else {
                    
                    document.getElementsByTagName("body")[0].innerHTML = "";
                    alert('The new category was successfully added! If you want to detect entities of this new category, remember to add the category to the list of Accepted Categories.');
                    window.location = "admin.jsp";
                }                
            } else {
                document.getElementById("newCategoryQueryImage").style.display = "none";
                alert("An error has occured: "+ request.statusText + ". Please try another list of words/phrases!");
            }
        } else { }
    }
    request.send(params);
}

function loadList() {
    document.getElementById("loadCategoryQueryErrorMsg").innerHTML = "";
    document.getElementById("loadCategoryQueryErrorMsg").style.display = "none";
                    
    var request = getXMLHTTPRequest();   
    var url = "LoadList";

    var endpoint = document.getElementById("newCategoryEndpoint").value;
    var query = document.getElementById("newCategoryQuery").value;
    
    
    var params = "endpoint="+encodeURIComponent(endpoint)+"&query="+encodeURIComponent(query);
    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
        
    document.getElementById("loadCategoryImage").style.display = "inline";
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                
                var response = request.responseText;
                if (response.indexOf("ATTENTION!!") == 0) {
                    document.getElementById("loadCategoryQueryErrorMsg").innerHTML = response;
                    document.getElementById("loadCategoryQueryErrorMsg").style.display = "block";
                } else {
                    document.getElementById("newCategoryList").value = response;
                }           
                document.getElementById("loadCategoryImage").style.display = "none";
                
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else { }
    }
    request.send(null);
}


function addEntityEnrichment(ses) {
    
    var request = getXMLHTTPRequest();   
    var url = "AddEntityEnrichment";
    
    var category = document.getElementById("addLODenrichmentCategory").value;
    var endpoint = document.getElementById("addLODenrichmentEndpoint").value;
    var template = document.getElementById("addLODenrichmentTemplate").value;
    
    var params = "category="+category+"&endpoint="+encodeURIComponent(endpoint)+"&template="+encodeURIComponent(template);
    if (ses=="y") {
        params =  "ses=y&"+params;
    } 
    
    var asynchr = true;	
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    params = params + "&rand="+randomNumber;  
    document.getElementById("addEEimg").style.display = "inline";
    request.open("POST", url, asynchr);
    
    request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    request.setRequestHeader("Content-length", params.length);
    request.setRequestHeader("Connection", "close");
    
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                
                var response = request.responseText;
                if (response != "") {
                    document.getElementById("addEEerrorMessage").innerHTML = response;
                    document.getElementById("addEEerrorMessage").style.display = "block";
                } else {
                    if (ses=="y") {
                        window.location = "sesadmin.jsp";
                    } else {
                        window.location = "admin.jsp";
                    }
                }           
                document.getElementById("addEEimg").style.display = "none";
                
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else { }
    }
    request.send(params);
          
          
}


function addAcceptedCategory(ses) {
    
    var request = getXMLHTTPRequest();
    
    var category = document.getElementById("addCategory").value;
    var id = "pcategory"+category;
    
    var url = "AddAcceptedCategory";
    var params = "category="+category;
    if (ses=="y") {
        params =  "ses=y&"+params;
    } 
    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
    
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                if (ses=="y") {
                    window.location = "sesadmin.jsp";
                } else {
                    window.location = "admin.jsp";
                }
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else { }
    }
    request.send(null);
          
          
}

function removeSure() {
    
    var close = confirm("Are you sure that you want to remove the LOD enrichment for that category of entites?\nIf so, the SPARQL endpoint and the SPARQL template query for that category will be removed!");
    if (close) {
        return true;
    }
    else {
        return false;
    }
}

function contEntityEnrichment() {
    document.getElementById("addEEendpoint").style.display = "block";
    document.getElementById("addEEtemplate").style.display = "block";
    document.getElementById("addCategoryButton").style.display = "block";
    document.getElementById("addContinueButton").style.display = "none";
}

function sureToChangeNamedEntities() {
    var close = confirm("Attention, after this action the category cannot be restored! Are you sure that you want to update the named entities?");
    if (close) {
        changeNamedEntities();
    }
    else {
        return false;
    }
}

function changeNamedEntities() {
    
    var request = getXMLHTTPRequest();  
    var categoryName = document.getElementById("updateNamedEntities").value;
    var list = document.getElementById("namedEntitiesList").value;
    var url = "UpdateCategoryNamedEntities";
    
    var params = "categoryName="+categoryName+"&list="+encodeURIComponent(list);
    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    params = params + "&rand="+randomNumber;
    
    document.getElementById("continueUpdateImage2").style.display = "inline";
    request.open("POST", url, asynchr);
    
    request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    request.setRequestHeader("Content-length", params.length);
    request.setRequestHeader("Connection", "close");
    
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                if (response != "") {
                    document.getElementById("continueUpdateErrMessage2").innerHTML = response;
                    document.getElementById("continueUpdateErrMessage2").style.display = "block";
                    document.getElementById("continueUpdateImage2").style.display = "none";
                } else {
                    document.getElementsByTagName("body")[0].innerHTML = "";
                    alert('The category was successfully updated! ');
                    window.location = "admin.jsp";
                }   
            } else {
                document.getElementById("continueUpdateImage2").style.display = "none";
                alert("An error has occured: "+ request.statusText + ". Please try another list of words/phrases!");
            }
        } else { }
    }
    request.send(params);
    
}

function contNamedEntitiesUpdate() {
    
    var request = getXMLHTTPRequest();   
    
    var category = document.getElementById("updateNamedEntities").value;
    var url = "LoadNamedEntitiesList";  
    
    var params = "category="+category;
    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
        
    document.getElementById("continueUpdateImage").style.display = "inline";
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
                if (response.indexOf("Attention!") == 0) {
                    document.getElementById("continueUpdateErrMessage").innerHTML = response;
                    document.getElementById("continueUpdateErrMessage").style.display = "block";
                } else {
                    document.getElementById("namedEntitiesList").style.display = "block";
                    document.getElementById("changeNamedEntitiesButton").style.display = "block";
                    document.getElementById("namedEntitiesList").value = response;
                }           
                document.getElementById("continueUpdateImage").style.display = "none";
                
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else { }
    }
    request.send(null);

    
}

function contAddNewCategory() {
    document.getElementById("addNewCategoryInputs").style.display = "block";
    document.getElementById("addNewCategoryButton").style.display = "none";
}

function runAquery() {
    document.getElementById("runaqueryinputs").style.display = "block";
}


function saveConfiguration(ses) {
    var request = getXMLHTTPRequest();   
    var url = "SaveConfiguration";

    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?rand="+randomNumber;
    if (ses=="y") {
        fullURL =  fullURL + "&ses=y";
    } 
        
    document.getElementById("storeImg").style.display = "inline";
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {      
                var response = request.responseText;
                document.getElementById("storingSpanText").innerHTML = response;
                document.getElementById("storingSpanText").style.display = "block";         
                document.getElementById("storeImg").style.display = "none";
                
            } else {
                alert("An error has occured: "+ request.statusText);
            }
        } else { }
    }
    request.send(null);
}

function loadConfiguration(ses) {
    var request = getXMLHTTPRequest();   
    var url = "LoadConfiguration";
    var id = document.getElementById("configurationID").value;
      
    var params = "id="+id;
    if (ses=="y") {
        params =  "ses=y&"+params;
    } 
    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
        
    document.getElementById("loadImg").style.display = "inline";
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {      
                var response = request.responseText;
                if (response != "") {
                    document.getElementById("errorLoadingMyConf").innerHTML = response;
                    document.getElementById("errorLoadingMyConf").style.display = "block";         
                    document.getElementById("loadImg").style.display = "none";
                } else {
                    document.getElementsByTagName("body")[0].innerHTML = "";
                    alert('Your configuration was successfully loaded!');
                    if (ses=="y") {
                        window.location = "sesadmin.jsp";
                    } else {
                        window.location = "admin.jsp";
                    }
                }
            } else {
                document.getElementById("loadImg").style.display = "none";
                alert("No stored configuration with ID = "+ id);
            }
        } else { }
    }
    request.send(null);
}


function resetConfiguration() {
    var request = getXMLHTTPRequest();   
    var url = "ResetConfiguration";
    var asynchr = true;
		
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?rand="+randomNumber;
        
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {      
                var response = request.responseText;
                document.getElementsByTagName("body")[0].innerHTML = "";
                alert('The configuration was reseted!');
                window.location = "sesadmin.jsp";

            } 
        } else { }
    }
    request.send(null);
}