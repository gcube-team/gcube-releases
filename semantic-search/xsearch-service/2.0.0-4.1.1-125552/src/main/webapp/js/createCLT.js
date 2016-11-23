
////////////////////
//Vars for Results Tables Operations

var INPUT_NAME_PREFIX = 'inputName'; // this is being set via script
var RADIO_NAME = 'totallyrad'; // this is being set via script
var TABLE_NAME = 'tblSample'; // this should be named in the HTML
var ROW_BASE = 1; // fi

//
//This table Holds the data for
//the docs key is the DocId
//
var docsData = new Array();

// Define the array that will contain the mapping table for ids to images.
var iconMap = new Array();
var iconList = new Array( iconMap );
var Node ;
var nnn = 0;

function Toggle(item)
{
    var idx = -1;
    for( i = 0; i < iconList.length; i++ )
    {
        if( iconList[i][0] == item )
        {
            idx = i;
            break;
        }
    }

    //	if( idx < 0 )
    //		alert( "Could not find key in Icon List." );

    var div=document.getElementById("D"+item);
    var visible=(div.style.display!="none");
    var key=document.getElementById("P"+item);


    // Check if the item clicked has any children. If it does not then remove the plus/minus icon
    // and replace it with a transaparent gif.
    var removeIcon = div.hasChildNodes() == false;

    if( key != null )
    {
        if( !removeIcon )
        {
            if (visible)
            {
                div.style.display="none";
                key.innerHTML="<img src='files/graphics/tree/plus.gif' hspace='0' vspace='0' border='0'>";
            }
            else
            {
                div.style.display="block";
                key.innerHTML="<img src='files/graphics/tree/minus.gif' hspace='0' vspace='0' border='0'>";
            }
        }
        else
            key.innerHTML="<img src='files/graphics/tree/leaf.png' hspace='0' vspace='0' border='0'>";
    }

    // Toggle the icon for the tree item
    key=document.getElementById("I"+item);
    if( key != null )
    {
        if (visible)
        {
            div.style.display="none";
            key.innerHTML="<img src='"+iconList[idx][1]+"' hspace='0' vspace='0' border='0'>";
        }
        else
        {
            div.style.display="block";
            key.innerHTML="<img src='"+iconList[idx][2]+"' hspace='0' vspace='0' border='0'>";
        }
    }
        
        
    request = getXMLHTTPRequest();
    
    var url = "Toggle";
    var params = "cluster="+item;
    var asynchr = true;
    var randomNumber = new Date().getTime()+parseInt(Math.random()*9999999);
    var fullURL = url+"?"+params+"&rand="+randomNumber;
	
    
    request.open("GET", fullURL, asynchr);
    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status == 200) {
                var response = request.responseText;
            } else {
                //alert("An error has occured: "+ request.statusText);
            }
        } else {    }
    }
    request.send(null);
}

function Expand() {
    divs=document.getElementsByTagName("DIV");
    for (i=0;i<divs.length;i++) {
        divs[i].style.display="block";
        key=document.getElementById("x" + divs[i].id);
        key.innerHTML="<img src='img/textfolder.gif' hspace='0' vspace='0' border='0'>";
    }
}

function Collapse() {
    divs=document.getElementsByTagName("DIV");
    for (i=0;i<divs.length;i++) {
        divs[i].style.display="none";
        key=document.getElementById("x" + divs[i].id);
        key.innerHTML="<img src='img/folder.gif' hspace='0' vspace='0' border='0'>";
    }
}

function AddImage( parent, imgFileName,values )
{
    img=document.createElement("IMG");
    img.setAttribute( "src", imgFileName );
    img.setAttribute( "hspace", 0 );
    img.setAttribute( "vspace", 0 );
    img.setAttribute( "border", 0 );
    parent.appendChild(img);
}

function AddImageRemove( parent, imgFileName,values )
{
    img=document.createElement("IMG");
    img.setAttribute( "src", imgFileName );
    img.setAttribute( "hspace", 0 );
    img.setAttribute( "vspace", 0 );
    img.setAttribute( "border", 0 );
    img.setStyle("display", "none");
    parent.appendChild(img);
}

function CreateUniqueTagName( seed )
{
    var tagName = seed;
    var attempt = 0;

    if( tagName == "" || tagName == null )
        tagName = "x";

    while( document.getElementById(tagName) != null )
    {
        tagName = "x" + tagName;
        if( attempt++ > 50 )
        {
            //alert( "Cannot create unique tag name. Giving up. \nTag = " + tagName );
            break;
        }
    }

    return tagName;
}

// Creates a new package under a parent.
// Returns a TABLE tag to place child elements under.
function CreateTreeItem( parent, img1FileName, img2FileName, nodeName, url, target , rootChild, root, clusterId )
{
    var uniqueId = CreateUniqueTagName( nodeName );
    for( i=0; i < iconList.length; i++ )
        if( iconList[i][0] == uniqueId )
        {
            //alert( "Non unique ID in Element Map. '" + uniqueId + "'" );
        // return;
        }
    iconList[iconList.length] = new Array( uniqueId, img1FileName, img2FileName );

    table = document.createElement("TABLE");
    if( parent != null )
        parent.appendChild( table );

    table.setAttribute( "border", 0 );
    table.setAttribute( "cellpadding", 1 );
    table.setAttribute( "cellspacing", 1 );

    tablebody = document.createElement("TBODY");
    table.appendChild(tablebody);

    row=document.createElement("TR");
    tablebody.appendChild( row );

    // Create the cell for the plus and minus.
    cell=document.createElement("TD");
	
    row.appendChild(cell);

	
    if (root && !rootChild) {
        // Create the hyperlink for plus/minus the cell
        a=document.createElement("A");
        cell.appendChild( a );
        a.setAttribute( "id", "P"+uniqueId );
        a.setAttribute( "href", "javascript:Toggle(\""+uniqueId+"\");" );
        AddImage( a, "files/graphics/tree/minus.gif",16 );
    } else if(rootChild && !root) {
        a=document.createElement("A");
        cell.appendChild( a );
        a.setAttribute( "id", "P"+uniqueId );
        a.setAttribute( "href", "javascript:Toggle(\""+uniqueId+"\");" );
        AddImage( a, "files/graphics/tree/plus.gif",16 );
    } else {
        span=document.createElement("SPAN");
        cell.appendChild( span );
        span.setAttribute( "id", "P"+uniqueId );
        AddImage( span, "files/graphics/tree/leaf.png",10 );
    }
    // Create the cell for the image.
    cell=document.createElement("TD");
	
    row.appendChild(cell);

    // all the event to call when the icon is clicked.
    a=document.createElement("A");
    a.setAttribute( "id", "img_"+clusterId );
    a.setAttribute( "href", url );
    if (url.indexOf("loadEnt") != -1) {
        a.setAttribute( "class", "imgRemoveSt");
    }
    cell.appendChild(a);

    // Add the image to the cell
    AddImage( a, img1FileName,16 );

    // Create the cell for the text
    cell=document.createElement("TD");
    cell.noWrap = true;
    a=document.createElement("A");
    a.setAttribute( "id", clusterId );
    cell.appendChild( a );
    if( url != null )
    {
        a.setAttribute( "href", url );
        /*if( target != null )
			a.setAttribute( "target", target );
		else
			a.setAttribute( "target", "_blank" );
		*/
        text=document.createTextNode( nodeName );
        a.appendChild(text);
    }
    else
    {
        text=document.createTextNode( nodeName );
        cell.appendChild(text);
    }
    row.appendChild(cell);

    return CreateDiv( parent, uniqueId,rootChild );;
}

// Creates a new DIV tag and appends it to parent if parent is not null.
// Returns the new DIV tag.
function CreateDiv( parent, id,rootChild )
{
    div=document.createElement("DIV");
    if( parent != null )
        parent.appendChild( div );

    div.setAttribute( "id", "D"+id );

    div.style.display  = (rootChild ? "none" : "block");
    div.style.marginLeft = "2em";

    return div;
}

// This is the root of the tree. It must be supplied as the parent for anything at the top level of the tree.
var rootCell = null;

// This is the entry method into the Tree View. It builds an initial single row, single cell table tat will
// contain the tree. It initialises a global object "rootCell". This object must be used as the parent for all
// top-level tree elements.
// There are two methods for creating tree elements: CreatePackage() and CreateNode(). The images for the
// package are hard coded. CreateNode() allows you to supply your own image for each node element.
function Initialise()
{
    body = document.getElementById("clusterLabelTree2");
    body.setAttribute( "leftmargin", 2 );
    body.setAttribute( "topmargin", 0 );
    body.setAttribute( "marginwidth", 0 );
    body.setAttribute( "marginheight", 0 );

    table = document.createElement("TABLE");
    body.appendChild( table );

    table.setAttribute( "border", 0 );
    table.setAttribute( "cellpadding", 1 );
    table.setAttribute( "cellspacing", 1 );

    tablebody = document.createElement("TBODY");
    table.appendChild(tablebody);

    row=document.createElement("TR");
    tablebody.appendChild(row);

    cell=document.createElement("TD");
    row.appendChild(cell);

    rootCell = cell;	// Initialise the root of the tree view.
}


function escapeQuery ( str ){

    var array = new Array();
    var array = str.split("(");
    tempQuery = array[0];

    return tempQuery;
}

function escapePar ( str ){

    var array = new Array();
    var array = str.split("[");
    tempQuery = array[1];

    return tempQuery;
}



function CreateProjectExplorer(query){

    Initialise();

    var tempQuery = escapeQuery(query);


    //url ="?&query="+tempQuery+"&respp=10&ctype=t&rdfrs=off&start=0&model=v";
    url ="javascript:getAllResults();";
    d = CreateTreeItem( rootCell, "files/graphics/tree/project.gif", "files/graphics/tree/project.gif", query, url, null,false,true );
    return d;


}


function createProjectNode(node,str,rootChild,docsData){
    //docsData = escape(docsData);
        
    clusterId = 'clt_'+nnn;
    url ="javascript:loadEntityResults('Cluster','"+str+"','"+docsData+"','"+clusterId+"')";
    d2 = CreateTreeItem( node, "files/graphics/remove.gif", "files/graphics/tree/remove.gif", str, url, null,rootChild,false, clusterId );
    nnn++;
    return d2;
}


//////////////////////////////////////////// --- DoM STYLE --- /////////////////////////////////////////
//
////////////////////////////// DELETES ELEMENTS AT RESULTS TABLE///////////////////
//
//
//

function deleteCells(){
    var checkedObjArray = new Array();
    var cCount = 0;

    var tbl = document.getElementById("results-table");

    for (var i=0; i<tbl.tBodies[0].rows.length; i++) {
        if (tbl.tBodies[0].rows[i] ) {
            checkedObjArray[cCount] = tbl.tBodies[0].rows[i];
            cCount++;
        }
    }

    if (checkedObjArray.length > 0) {
        var rIndex = checkedObjArray[0].sectionRowIndex;
        deleteRows(checkedObjArray);
    }

    var checkedObjArray1 = new Array();
    var cCount1 = 0;

    var tbl = document.getElementById("results-table");

    for (var i=0; i<tbl.tBodies[0].rows.length; i++) {
        if (tbl.tBodies[0].rows[i] ) {
            checkedObjArray1[cCount1] = tbl.tBodies[0].rows[i];
            cCount1++;
        }
    }

    if (checkedObjArray1.length > 0) {
        var rIndex = checkedObjArray1[0].sectionRowIndex;
        deleteRows(checkedObjArray1);
    }

    if (document.getElementById("thepages")!= null) {
        document.getElementById("thepages").style.visibility = "hidden";
    }
                
                
}

function deleteRows(rowObjArray)
{
    for (var i=0; i<rowObjArray.length; i++) {
        var rIndex = rowObjArray[i].sectionRowIndex;
        rowObjArray[i].parentNode.deleteRow(rIndex);
    }
}
//
//
/////////////////////////////////////// Add Elements AT RESULTS TABLE /////////////
//
//



function addRowToTable(docsData){

    //delete the Rows tha alreeady exist
    document.getElementById("resultsFirstPage").innerHTML = "<table id=\"results-table\"><tbody><tr><td></td></tr></tbody></table>";

    var temp1 = new Array();

    temp1 = docsData.split('^^');
	
    var tbl = document.getElementById('results-table');
    var nextRow = tbl.tBodies[0].rows.length;
    var iteration = nextRow + ROW_BASE;
    var num = 0;

    var ii=temp1.length-1;
    while( ii >= 0 ){

        //var fileLength 	 = temp1[ii--];
        var docId	 = temp1[ii--];
        var textFile	 = temp1[ii--];
        var pageHyperlink= temp1[ii--];
        var filePath     = temp1[ii--];
        var pageTitle 	 = temp1[ii--];


        //Insert new Row at results Table

        //		if (!pageTitle || !filePath)
        //			continue;


        var row = tbl.tBodies[0].insertRow(num);
        var row = row.insertCell(num);


        var resTbl     = document.createElement("table");
        var resTblBody = document.createElement("tbody");


        var rowRes 	= document.createElement("tr");

        var cell 	= document.createElement("td");

        var span 	= document.createElement('span')

        var a 		= document.createElement('A');
        span.setAttribute("class","F14");
        a.setAttribute("href",pageHyperlink);
        var textNode = document.createTextNode(pageTitle);
        a.appendChild(textNode);
        span.appendChild(a);
        cell.appendChild(span);

        //		var span 	= document.createElement('span')
        //		span.setAttribute("class","F11");
        //		textNode = document.createTextNode("- "+pageRank);
        //		span.appendChild(textNode);
        //		cell.appendChild(span);

        rowRes.appendChild(cell);


        var rowRes1 = document.createElement("tr");
        var cell1 	= document.createElement("td");


        var div = document.createElement("div");
        div.setAttribute("class","F12");
        textNode = document.createTextNode(textFile);
        div.appendChild(textNode);
        cell1.appendChild(div);
        rowRes1.appendChild(cell1);


        var rowRes2 	= document.createElement("tr");
        cell 		= document.createElement("td");

        div = document.createElement("div");
        div.setAttribute("class","F10");
        span = document.createElement('span');
        span.setAttribute("class","gray");
        textNode = document.createTextNode(pageHyperlink);
        span.appendChild(textNode);
        cell.appendChild(span);

        cell.appendChild(div);
        rowRes2.appendChild(cell);



        var rowRes4     = document.createElement("tr");
        cell            = document.createElement("td");
        var p 		= document.createElement("p");

        cell.appendChild(p);
        rowRes4.appendChild(cell);


        resTblBody.appendChild(rowRes);
        resTblBody.appendChild(rowRes1);
        resTblBody.appendChild(rowRes2);
        //		resTblBody.appendChild(rowRes3);
        resTblBody.appendChild(rowRes4);
        resTbl.appendChild(resTblBody);


        row.appendChild(resTbl);
    }

}
