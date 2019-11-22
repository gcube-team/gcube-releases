
<%@include file="init.jsp"%>
<link href="https://fonts.googleapis.com/icon?family=Material+Icons"
	rel="stylesheet">
	
<% pageContext.setAttribute("currentGroup", GroupLocalServiceUtil.getGroup(PortalUtil.getScopeGroupId(request)));
%>	
<script src="<%=renderRequest.getContextPath()%>/js/datatables.min.js"></script>

<portlet:resourceURL var="usersCustomDataSourceURL">
	<portlet:param name="cmd" value="itemId=" />
</portlet:resourceURL>
<portlet:resourceURL var="downloadFileURL">
	<portlet:param name="fileToDownloadId" value="itemId=" />
</portlet:resourceURL>
<portlet:renderURL var="maximizedState"
	windowState="<%=LiferayWindowState.MAXIMIZED.toString()%>" />
<portlet:renderURL var="normalState"
	windowState="<%=LiferayWindowState.NORMAL.toString()%>" />

<c:set var="maximised" scope="session"
	value="${renderRequest.getWindowState().toString().equalsIgnoreCase('maximized')}" />

<table width="100%" style="border: none; border-collapse: inherit;">
	<tr>
		<td><div class="ws-breadcrumb-container"></div></td>
		<c:choose>
			<c:when test="${currentGroup.getParentGroupId() > 0}">
				<td><div class="ws-recents">
						<a 	href="javascript:loadRecentItemsListIntoTable('recents', 'Recent documents');"><i
							style="font-size: 1.2em; color: #08c;" class="material-icons">access_time</i>
							<span style="color: #08c;" >Recent</span></a>
							<c:choose>
								<c:when test="${not maximised}">
									<a class="btn btn-link" href="${maximizedState}"><i class="material-icons">launch</i></a>
								</c:when>
								<c:otherwise>
									<a class="btn btn-link" href="${normalState}"><i class="material-icons">transit_enterexit</i> Return to full page</a>
								</c:otherwise>
							</c:choose>
					</div></td>
			</c:when>
			<c:otherwise>
				<td>
					<%pageContext.setAttribute("vreFoldersId", StorageHubServiceUtil.getVREFoldersId(request));%>
					<c:if test="${not empty vreFoldersId}">
						<div id="vreFoldersDiv" class="ws-recents">
							<a
								href="javascript:loadItemsListIntoTable('${vreFoldersId}', 'VRE Folders', true);"><i
								style="font-size: 1.5em; color: #08c;" class="material-icons">folder_special</i>&nbsp;
								<span style="color: #08c;" >VREs</span></a>
							<c:choose>
								<c:when test="${not maximised}">
									<a class="btn btn-link" href="${maximizedState}"><i class="material-icons">launch</i></a>
								</c:when>
								<c:otherwise>
									<a class="btn btn-link" href="${normalState}"><i class="material-icons">transit_enterexit</i> Return to full page</a>
								</c:otherwise>
							</c:choose>
						</div>
					</c:if>
				</td>
			</c:otherwise>
		</c:choose>
	</tr>
</table>

<table id="userTable" class="display" cellspacing="0" width="100%">
	<thead>
		<tr>
			<th>Name</th>
			<th>Owner</th>
			<th>Last modified</th>
		</tr>
	</thead>
</table>
<c:if test="${currentGroup.getParentGroupId() > 0}">
    <div class="ws-go">
    	<a href="<%= StorageHubServiceUtil.getWorkspaceFolderURL(request) %>" target="_blank">
    	<i class="icon-folder-open" style="font-size: 14px;"></i>&nbsp;
    	<span>Go to shared workspace</span>
    	</a>
    </div>
</c:if>
<script>
//avoid warning messages
$.fn.dataTable.ext.errMode = 'none';

function loadItemsListIntoTable(itemId, itemName, hideVreFolders) {
	var table = $('#userTable').DataTable();
	table.ajax.url('<%=usersCustomDataSourceURL%>'+itemId+'_selectedName='+itemName).load();
	if (hideVreFolders) {
		$('#vreFoldersDiv').hide();
	} else {
		$('#vreFoldersDiv').show();
	}
	$('#userTable_info').show();
	$('#userTable_length').show();
}

function loadRecentItemsListIntoTable(itemId, itemName) {
	var table = $('#userTable').DataTable();
	table.ajax.url('<%=usersCustomDataSourceURL%>'+itemId+'_selectedName='+itemName).load();
	$('#userTable_info').hide();
	$('#userTable_length').hide();
}

function downloadItem(itemId) {
	var uri = '<%=downloadFileURL%>'+itemId;
	window.open(uri, "_blank");
}
//instance of DataTables framework, see https://datatables.net/manual/
function mainTable() {
	var table = $('#userTable').DataTable( {
		retrieve: true, // tell DataTables that you are aware that the initialisation options can't be changed after initialisation, and that should that occur, that you just want the DataTable instance to be returned.
		"lengthMenu": [ [5, 12, 25, 50, -1], [5, 12, 25, 50, "All"] ],
		"dom": "<'row'<'small-6 columns'><'small-6 columns'>r>t<'row'<'mydt-pagination'p><'#mydtwrap'<'mydt-block'l><i>>'>",
		select: {
	         style: 'single'
	    },
		processing: true,
		serverSide: true, 
		searching: false,
		ordering:  false,
		"language": {
		      "emptyTable": "This folder is empty",
		      "info": "_START_ to _END_ of _TOTAL_ items"
		},
		"stripeClasses": [ 'strip1', 'strip2'],
	    "ajax": {
	      "url":"<%=usersCustomDataSourceURL%>",
	      "dataSrc": function ( json ) { //here is the json return by the ajax call
	    	  $( "div.ws-breadcrumb-container" ).html(json.breadcrumb);
	          return json.mytabledata;
	        }
	     },
	    "columns": [
	        { "data": "Name",
	          "render": function ( data, type, row, meta ) {
	        	var obj = JSON.parse(data);
	        	var truncatedName = truncateText(obj.Name);
	        	var anchorURL = '<a  title="'+obj.Name+'" target="_blank" href="<%=downloadFileURL%>'+obj.Id+'">'+truncatedName+'<a/>';
	         	if(obj.isFolder) {
	         		anchorURL = '<a title="'+obj.Name+'" href="javascript:loadItemsListIntoTable(\''+obj.Id+'\',\''+obj.Name+'\', true);">'+truncatedName+'<a/>';
	         	}
	          	return '<div class="noselect" style="display: table;">'+
	          	'<span style="padding: 5px; background-color: white; border-radius: 20px; color: '+ obj.IconColor+ '; font-size: 1.5em; vertical-align: middle; display: table-cell;">'+
	          	'<i class="material-icons " title="'+obj.Name+'">'+obj.Icon+'</i></span>'+
	          	'<span style="padding-left: 10px; vertical-align: middle; display: table-cell;">'+
	        	anchorURL+
	        	'</span></div>';
	        	}
	        },
	        { "data": "Owner",
	          "render": function ( data, type, row, meta ) {
	        	  	if (data != "me") {
	        			return getInitials(data);
	        	  	}
					return data;
	           }
	        },
	        { "data": "LastModified" ,
		      "render": function ( data, type, row, meta ) {
		    	  	var date = $.format.date(new Date(data), 'dd MMM HH:mm yy');
		     		return '<span style="font-size: 10px;">'+date+'</span>';
		      }
	        }
	    ]
		} 
	);
	//	SimpleDateFormat sdf = new SimpleDateFormat("dd MMM HH:mm yy"); 
	table
// 	.on( 'select', function ( e, dt, type, indexes ) {
// 		var selectedId = table.rows( indexes ).data().pluck( 'Id' )[0];
// 	} )
	.on( 'dblclick', 'tr', function (e, dt, type, indexes ) {    	
		var selectedRowIndex = table.row( this ).index();
	 	//console.log("->"+selectedRowIndex);
		var selectedId = table.rows( indexes ).data().pluck( 'Id' )[selectedRowIndex];
		var selectedNameData = table.rows( indexes ).data().pluck( 'Name' )[selectedRowIndex];
		var obj = JSON.parse(selectedNameData);
		var selectedName = obj.Name;
		if(obj.isFolder) 
	    	loadItemsListIntoTable(selectedId, selectedName, true);
		else
			downloadItem(selectedId);
	} )
	.on( 'error.dt', function ( e, settings, techNote, message ) {
		$('div.ws-breadcrumb-container').text('Ops, cannot reach the server. Please try to reload the page or check your internet connection');
		console.log( 'An error has happened in the server: ', message );
	} );
}

$(document).ready(mainTable);

</script>
