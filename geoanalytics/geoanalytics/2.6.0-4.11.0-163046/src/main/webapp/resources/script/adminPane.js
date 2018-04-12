function showAdminInfo(id, data)
{
	var el = document.getElementById(id);
	
//	var outer = document.createElement("div");
//	outer.style.height = '10%';
//	el.appendChild(outer);
	
	createStatusTable(data);
	//status.className = status.className + ' systemStatus';
    
	createStatsTable(data);
	//stats.className = stats.className + ' systemStats';
    
    createActivityTable(data);
    //activity.className = activity.className + ' activity';
    
    createLoginTable(data);
    createLockTable(data);
    createIllegalRequestTable(data);
    createIllegalLayerAccessTable(data);
    createIllegalLayerZoomTable(data);
    
    createAdminMenu(data);
	
	//outer.appendChild(con);
	//outer.appendChild(menu);
	
	//el.appendChild(con);
	
	setPrimaryMenuButton("btnHome");
	
}

function toggleSystem(data)
{
	if(data.systemOnline == false)
	{
		$('#systemToggleBtn').prop('disabled', true);
		disableMenuButtons();
		$('#systemStatusCell').text("Activating");
		
		var request = $.ajax({ 
	         url : "systemOnline", 
	         type : "post", 
	         
	         success: function(resp)
	         		  {
	        	 		console.log("Response: status=" + resp.status + ", error=" + resp.error); 
	        	 		 if(resp.error == true)
	        	 	       	 alert("An error has occurred. System " + (resp.status ? "remains offline" : "is now online"));
	        	 		 else updateSystemToggleInfo(resp.status, data);
	         		  },
       
	
	         error: function(jqXHR, textStatus, errorThrown) 
	         		{
			   			alert("The following error occured: " + textStatus, errorThrown);
			   		},
	       	
	         complete: function(jqXHR, textStatus) 
	         {
	    	   $('#systemToggleBtn').prop('disabled', false);
	    	   enableMenuButtons();
	         }
		});
	}
	else 
	{
		$('#systemToggleBtn').prop('disabled', true);
		disableMenuButtons();
		
		$('#systemStatusCell').text("Decommissioning");
		
		var request = $.ajax({ 
	         url : "systemOffline", 
	         type : "post", 
	         success: function(resp, textStatus, jqXHR) 
	         		  { 
	        	 		console.log("Response: status=" + resp.status + ", error=" + resp.error); 
	        	 		if(resp.error == true)
	        	 			alert("An error has occurred. System " + (resp.status ? "remains online" : "is now offline"));
	        	 		else updateSystemToggleInfo(resp.status, data);
	         		  },
	         error: function(jqXHR, textStatus, errorThrown) 
	         		{
			   			alert("The following error occured: " + textStatus, errorThrown);
	         		},
	         complete: function(jqXHR, textStatus) 
	         		   {
		    	   		$('#systemToggleBtn').prop('disabled', false);
		    	   		enableMenuButtons();
	         		   }
	       }); 
	}
}



function createStatusTable(data)
{
	var tbl  = $('#statusTbl')[0];
	   
	var tr = tbl.insertRow(-1);
	var td;
	    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "System Status";
    td = tr.insertCell(-1);
    td.id = "systemStatusCell";
    console.log("systemOnline presence: " + isPresent(data.systemOnline) + " val: " + data.systemOnline + " val!=null" + (data.systemOnline!=null) + " val!=\"\"" + (data.systemOnline!=""));
    td.innerHTML = (isPresent(data.systemOnline)? (data.systemOnline ? "Online" : "Offline") : "N/A");
    if(isPresent(data.systemOnline))
    {
    	td = tr.insertCell(-1);
    	var inputElement = document.createElement('button');
    	inputElement.id = "systemToggleBtn";
    	inputElement.type = "button";
    	inputElement.className = "btn btn-default";
    	inputElement.value = data.systemOnline ? "Bring Offline" : "Bring Online";
    	inputElement.innerHTML = data.systemOnline ? "Bring Offline" : "Bring Online";
    	inputElement.addEventListener('click', function()
    										   {
    											toggleSystem(data);
    										   }
    								 );
        td.appendChild(inputElement);
    }
    return tbl;
}

function createStatsTable(data) {
    var tbl  = $('#statsTbl')[0];
    
    var tr = tbl.insertRow(-1);
    var td;
    var th = document.createElement("th");
    th.colSpan = "2";
    th.style.textAlign = "center";
    th.innerHTML = "System Statistics";
    tr.appendChild(th);
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Total Users";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.allUserCount) ? data.allUserCount : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Active Users";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.activeUserCount) ? data.activeUserCount : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Online Users";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.onlineUserCount) ? data.onlineUserCount : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Locked Users";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.lockedUserCount) ? data.lockedUserCount : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Total Customers";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.allCustomerCount) ? data.allCustomerCount : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Active Customers";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.activeCustomerCount) ? data.activeCustomerCount : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Total Customers";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.allCustomerCount) ? data.allCustomerCount : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Number of Projects";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.projectCount) ? data.projectCount : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Number of Documents";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.documentCount) ? data.documentCount : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Total Document Size";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.documentSize) ? fileSize(data.documentSize) : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Number of Workflows";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.workflowCount) ? data.workflowCount : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Number of Workflow Tasks";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.workflowTaskCount) ? data.workflowTaskCount : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Number of Shapes";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.shapeCount) ? data.shapeCount : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Number of Taxonomies";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.taxonomyCount) ? data.taxonomyCount : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Number of Taxonomy Terms";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.taxonomyTermCount) ? data.taxonomyTermCount : "N/A");
  
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Total Repository Size";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.repositorySize) ? fileSize(data.repositorySize) : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Last Repository Sweep";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.repositoryLastSweep) ? timestampToDateTimeString(data.repositoryLastSweep) : "N/A");
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Repository size reduction after last sweep";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.repositoryLastSweepSizeReduction) ? fileSize(data.repositoryLastSweepSizeReduction) : "N/A");
    
    return tbl;
}

function updateSystemToggleInfo(status, data)
{
	if(status == true && data.systemOnline == false)
	{
		data.systemOnline =  true;
		$('#systemStatusCell').text("Online");
		$('#systemToggleBtn').prop('value', "Bring Offline");
		$('#systemToggleBtn').html("Bring Offline");
	}else if(status == false && data.systemOnline == true)
	{
		data.systemOnline = false;
		$('#systemStatusCell').text("Offline");
		$('#systemToggleBtn').prop('value', "Bring Online");
		$('#systemToggleBtn').html("Bring Online");
	}
}

function createActivityTable(data) {
    
	if ( typeof createActivityTable.btnStatus == 'undefined' ) {
		createActivityTable.btnStatus = false;
    }
	
	var tbl  = document.createElement("table");
	tbl.id = "activityTbl";
	tbl.className = "table table-striped activity";
	
    $('#activityTbl').replaceWith(tbl);
	
    var tr = tbl.insertRow(-1);
    var td;
    var th = document.createElement("th");
    th.colSpan = createActivityTable.btnStatus ? "6" : "6";
    th.style.textAlign = "center";
    th.innerHTML = "Activity Overview";
    tr.appendChild(th);
    
    tr = tbl.insertRow(-1);
    th = document.createElement("th");
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "User";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Date";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Entity";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Action";
    tr.appendChild(th);
    td = tr.insertCell(-1);
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Last User Action";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.lastUserAction) && isPresent(data.lastUserAction.uId)) ? data.lastUserAction.uId : "N/A";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.lastUserAction) && isPresent(data.lastUserAction.timestamp)) ? timestampToDateTimeString(parseInt(data.lastUserAction.timestamp)) : "N/A";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.lastUserAction) && isPresent(data.lastUserAction.entityType)) ? data.lastUserAction.entityType : "N/A";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.lastUserAction) && isPresent(data.lastUserAction.action)) ? data.lastUserAction.action : "N/A";
    
    if(isPresent(data.lastUserActions))
    {
    	td = tr.insertCell(-1);
    	var inputElement = document.createElement('button');
    	//inputElement.align = "center";
    	inputElement.type = "button";
    	inputElement.className = "statsBtns btn btn-default";
    	inputElement.value = createActivityTable.btnStatus ? "Hide" : "All";
    	inputElement.innerHTML = createActivityTable.btnStatus ? "Hide" : "All";
    	inputElement.addEventListener('click', function()
    										   {
    											toggleShowUserActions(data);
    										   }
    								 );
        td.appendChild(inputElement);
        
        if(createActivityTable.btnStatus)
        { 
        	for(var i=1; i<data.lastUserActions.length; i++)
        	{
        		var action = data.lastUserActions[i];
        		tr = tbl.insertRow(-1);
        	    td = tr.insertCell(-1);
        	    td.innerHTML = "";
        	    td = tr.insertCell(-1);
        	    td.innerHTML = isPresent(action.uId) ? action.uId : "N/A";
        	    td = tr.insertCell(-1);
        	    td.innerHTML = isPresent(action.timestamp) ? timestampToDateTimeString(parseInt(action.timestamp)) : "N/A";
        	    td = tr.insertCell(-1);
        	    td.innerHTML = isPresent(action.entityType) ? action.entityType : "N/A";
        	    td = tr.insertCell(-1);
        	    td.innerHTML = isPresent(action.action) ? action.action : "N/A";
        	    td = tr.insertCell(-1);
        	}
        }
    }
    
    tr = tbl.insertRow(-1);
    td = tr.insertCell(-1);
    td.innerHTML = "Last Data Update";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.lastDataUpdate) && isPresent(data.lastDataUpdate.entityType)) ? data.lastDataUpdate.entityType : "N/A";
    td = tr.insertCell(-1);
    td.innerHTML = (isPresent(data.lastDataUpdate) && isPresent(data.lastDataUpdate.entityId)) ? data.lastDataUpdate.entityId : "N/A";
    td.innerHTML = (isPresent(data.lastDataUpdate) && isPresent(data.lastDataUpdate.timestamp)) ? new Date(parseInt(data.lastDataUpdate.timestamp)).toLocaleString() : "N/A";
    td = tr.insertCell(-1);
    
    return tbl;
  }

function createLoginTable(data) {
    
	var first = false;
	if ( typeof createLoginTable.btnStatus == 'undefined' ) {
		createLoginTable.btnStatus = false;
		first = true;
    }
	
	if(!first && !createLoginTable.btnStatus)
		$('#loginTbl').dataTable().fnDestroy();
	
	var tbl  = document.createElement("table");
	tbl.id = "loginTbl";
	tbl.className = "table table-striped login";
	
    $('#loginTbl').replaceWith(tbl);
	
    var thead = tbl.createTHead();
    
    var tr = thead.insertRow(-1);
    var td;
    var th = document.createElement("th");
    th.colSpan = createLoginTable.btnStatus ? "3" : "4";
    th.style.textAlign = "center";
    th.innerHTML = "User login activity";
    tr.appendChild(th);
    
    tr = thead.insertRow(-1);
    
    if(!createLoginTable.btnStatus)
    {
    	th = document.createElement("th");
    	tr.appendChild(th);
    }
    
    th = document.createElement("th");
    th.innerHTML = "User";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Date";
    tr.appendChild(th);
    
    th = document.createElement("th");
    var inputElement = document.createElement('button');
	//inputElement.align = "center";
	inputElement.type = "button";
	inputElement.className = "statsBtns btn btn-default";
	inputElement.value = createLoginTable.btnStatus ? "Hide" : "All";
	inputElement.innerHTML = createLoginTable.btnStatus ? "Hide" : "All";
	inputElement.addEventListener('click', function()
										   {
											toggleShowLogins(data);
										   }
	
	);
	th.appendChild(inputElement);
	tr.appendChild(th);
	
	var tbody = document.createElement("tbody");
    tbl.appendChild(tbody);
    
	if(!createLoginTable.btnStatus)
	{
	    
	    tr = tbody.insertRow(-1);
	    td = tr.insertCell(-1);
	    td.innerHTML = "Last User Login";
	    td = tr.insertCell(-1);
	    td.innerHTML = (isPresent(data.lastUserLogin) && isPresent(data.lastUserLogin.uId)) ? data.lastUserLogin.uId : "N/A";
	    td = tr.insertCell(-1);
	    td.innerHTML = (isPresent(data.lastUserLogin) && isPresent(data.lastUserLogin.timestamp)) ? timestampToDateTimeString(parseInt(data.lastUserLogin.timestamp)) : "N/A";
	    
	    td = tr.insertCell(-1);
	}
	else
    { 
    	for(var i=0; i<data.lastUserLogins.length; i++)
    	{
    		var login = data.lastUserLogins[i];
    		tr = tbody.insertRow(-1);
    	    td = tr.insertCell(-1);
    	    td.innerHTML = isPresent(login.id) ? login.id : "N/A";
    	    td = tr.insertCell(-1);
    	    td.innerHTML = isPresent(login.timestamp) ? timestampToDateTimeString(parseInt(login.timestamp)) : "N/A";
    	    td = tr.insertCell(-1);
    	    
    	}
    	var opts = {"bLengthChange" : false,
    			   //  "sDom" : "<'row'<'col-xs-6'l><'col-sm-offset-2 col-xs-2'f>r>t",
    			     "bInfo" : false
    	};
    	if(data.lastUserLogins.length <= 10)
    	{
    		opts["bPaginate"] = false;
    		opts["bFilter"] = false;
  		}
    	$(tbl).dataTable(opts);
    }
    
    return tbl;
  }

function createLockTable(data) {
    
	var first = false;
	if ( typeof createLockTable.btnStatus == 'undefined' ) {
		createLockTable.btnStatus = false;
		first = true;
    }
	
	if(!first && !createLockTable.btnStatus)
		$('#lockTbl').dataTable().fnDestroy();
	
	var tbl  = document.createElement("table");
	tbl.id = "lockTbl";
	tbl.className = "table table-striped alert";
	
    $('#lockTbl').replaceWith(tbl);
	
    var thead = tbl.createTHead();
    
    var tr = thead.insertRow(-1);
    var td;
    var th = document.createElement("th");
    th.colSpan = createLockTable.btnStatus ? "4" : "2";
    th.style.textAlign = "center";
    th.innerHTML = "Locked user accounts";
    tr.appendChild(th);
    
    tr = thead.insertRow(-1);
    if(!createLockTable.btnStatus)
    {
    	
    	th = document.createElement("th");
    	th.innerHTML = "Total: " + data.lockedUserCount;
    	tr.appendChild(th);
    	
    	th = document.createElement("th");
	    if(data.lockedUserCount > 0)
	    {
	    	var inputElement = document.createElement('button');
			//inputElement.align = "center";
			inputElement.type = "button";
			inputElement.className = "statsBtns btn btn-default";
			inputElement.value = createLockTable.btnStatus ? "Hide" : "All";
			inputElement.innerHTML = createLockTable.btnStatus ? "Hide" : "All";
			inputElement.addEventListener('click', function()
												   {
													toggleShowAccountLocks(data);
												   }
			
			);
			th.appendChild(inputElement);
	    }
		tr.appendChild(th);
    }
    else
    { 
	    th = document.createElement("th");
	    th.innerHTML = "User";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
	    th.innerHTML = "Date";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
	    th.innerHTML = "Addresses";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
	    th.innerHTML = "Time to unlock";
	    tr.appendChild(th);
    }
    
	var tbody = document.createElement("tbody");
    tbl.appendChild(tbody);
    
	if(createLockTable.btnStatus)
    { 
    	for(var i=0; i<data.accountLocks.length; i++)
    	{
    		var lock = data.accountLocks[i];
    		tr = tbody.insertRow(-1);
    	    td = tr.insertCell(-1);
    	    td.innerHTML = isPresent(lock.id) ? lock.id : "N/A";
    	    td = tr.insertCell(-1);
    	    td.innerHTML = isPresent(lock.timestamp) ? timestampToDateTimeString(parseInt(lock.timestamp)) : "N/A";
    	    td = tr.insertCell(-1);
    	    var addresses = "";
    	    if(isPresent(lock.address))
    	    {
    	    	for(var i=0; i<lock.address.length; i++)
    	    	{
    	    		addresses += lock.address[i];
    	    		if(i != lock.address.length-1) addresses += ", ";
    	    	}
    	    	td.innerHTML = addresses;
    	    }
    	    else
    	    	td.innerHTML = "N/A";
    	    td = tr.insertCell(-1);
    	    td.innerHTML = isPresent(lock.timeToUnlock) ? millisToDurationString(lock.timeToUnlock) : "N/A";
    	}
    	var opts = {"bLengthChange" : false,
    			   //  "sDom" : "<'row'<'col-xs-6'l><'col-sm-offset-2 col-xs-2'f>r>t",
    			     "bInfo" : false
    	};
    	if(data.accountLocks.length <= 10)
    	{
    		opts["bPaginate"] = false;
    		opts["bFilter"] = false;
  		}
    	$(tbl).dataTable(opts);
    }
    
    return tbl;
  }

function createIllegalRequestTable(data) {
    
	var first = false;
	if ( typeof createIllegalRequestTable.btnStatus == 'undefined' ) {
		createIllegalRequestTable.btnStatus = false;
		first = true;
    }
	
	if(!first && !createIllegalRequestTable.btnStatus)
		$('#illegalRequestTbl').dataTable().fnDestroy();
	
	var tbl  = document.createElement("table");
	tbl.id = "illegalRequestTbl";
	tbl.className = "table table-striped alert";
	
    $('#illegalRequestTbl').replaceWith(tbl);
	
    var thead = tbl.createTHead();
    
    var tr = thead.insertRow(-1);
    var td;
    var th = document.createElement("th");
    th.colSpan = createIllegalRequestTable.btnStatus ? "5" : "2";
    th.style.textAlign = "center";
    th.innerHTML = "Illegal request attempst";
    tr.appendChild(th);
    
    tr = thead.insertRow(-1);
    if(!createIllegalRequestTable.btnStatus)
    {
    	
    	th = document.createElement("th");
    	th.innerHTML = "Total: " + data.illegalRequestAttemptCount;
    	tr.appendChild(th);
    	
    	th = document.createElement("th");
	    if(data.illegalRequestAttemptCount > 0)
	    {
	    	var inputElement = document.createElement('button');
			//inputElement.align = "center";
			inputElement.type = "button";
			inputElement.className = "statsBtns btn btn-default";
			inputElement.value = createIllegalRequestTable.btnStatus ? "Hide" : "All";
			inputElement.innerHTML = createIllegalRequestTable.btnStatus ? "Hide" : "All";
			inputElement.addEventListener('click', function()
												   {
													toggleShowIllegalRequests(data);
												   }
			
			);
			th.appendChild(inputElement);
	    }
		tr.appendChild(th);
    }
    else
    { 
	    th = document.createElement("th");
	    th.innerHTML = "User";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
	    th.innerHTML = "Date";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
	    th.innerHTML = "Address";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
		tr.appendChild(th);
    }
    
	var tbody = document.createElement("tbody");
    tbl.appendChild(tbody);
    
	if(createIllegalRequestTable.btnStatus)
    { 
    	for(var i=0; i<data.illegalRequests.length; i++)
    	{
    		var request = data.illegalRequests[i];
    		tr = tbody.insertRow(-1);
    	    td = tr.insertCell(-1);
    	    td.innerHTML = isPresent(request.id) ? request.id : "N/A";
    	    td = tr.insertCell(-1);
    	    td.innerHTML = isPresent(request.timestamp) ? timestampToDateTimeString(parseInt(request.timestamp)) : "N/A";
    	    td = tr.insertCell(-1);
    	    td.innerHTML = isPresent(request.address) ? request.address : "N/A";
    	}
    	var opts = {"bLengthChange" : false,
    			   //  "sDom" : "<'row'<'col-xs-6'l><'col-sm-offset-2 col-xs-2'f>r>t",
    			     "bInfo" : false
    	};
    	if(data.accountLocks.length <= 10)
    	{
    		opts["bPaginate"] = false;
    		opts["bFilter"] = false;
  		}
    	$(tbl).dataTable(opts);
    }
    
    return tbl;
}

function createIllegalLayerAccessTable(data) {
    
	var first = false;
	if ( typeof createIllegalLayerAccessTable.btnStatus == 'undefined' ) {
		createIllegalLayerAccessTable.btnStatus = false;
		first = true;
    }
	
	if(!first && !createIllegalLayerAccessTable.btnStatus)
		$('#illegalLayerAccessTbl').dataTable().fnDestroy();
	
	var tbl  = document.createElement("table");
	tbl.id = "illegalLayerAccessTbl";
	tbl.className = "table table-striped alert";
	
    $('#illegalLayerAccessTbl').replaceWith(tbl);
	
    var thead = tbl.createTHead();
    
    var tr = thead.insertRow(-1);
    var td;
    var th = document.createElement("th");
    th.colSpan = createIllegalLayerAccessTable.btnStatus ? "5" : "2";
    th.style.textAlign = "center";
    th.innerHTML = "Illegal layer access attempts";
    tr.appendChild(th);
    
    tr = thead.insertRow(-1);
    if(!createIllegalLayerAccessTable.btnStatus)
    {
    	
    	th = document.createElement("th");
    	th.innerHTML = "Total: " + data.illegalLayerAccessAttemptCount;
    	tr.appendChild(th);
    	
    	th = document.createElement("th");
	    if(data.illegalLayerAccessCount > 0)
	    {
	    	var inputElement = document.createElement('button');
			//inputElement.align = "center";
			inputElement.type = "button";
			inputElement.className = "statsBtns btn btn-default";
			inputElement.value = createIllegalLayerAccess.btnStatus ? "Hide" : "All";
			inputElement.innerHTML = createIllegalLayerAccessTable.btnStatus ? "Hide" : "All";
			inputElement.addEventListener('click', function()
												   {
													toggleShowIllegalLayerAccess(data);
												   }
			
			);
			th.appendChild(inputElement);
	    }
		tr.appendChild(th);
    }
    else
    { 
	    th = document.createElement("th");
	    th.innerHTML = "User";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
	    th.innerHTML = "Date";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
	    th.innerHTML = "Address";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
		tr.appendChild(th);
    }
    
	var tbody = document.createElement("tbody");
    tbl.appendChild(tbody);
    
	if(createIllegalLayerAccessTable.btnStatus)
    { 
    	for(var i=0; i<data.illegalLayerAccess.length; i++)
    	{
    		var access = data.illegalLayerAccess[i];
    		tr = tbody.insertRow(-1);
    	    td = tr.insertCell(-1);
    	    td.innerHTML = isPresent(access.id) ? access.id : "N/A";
    	    td = tr.insertCell(-1);
    	    td.innerHTML = isPresent(access.timestamp) ? timestampToDateTimeString(parseInt(access.timestamp)) : "N/A";
    	    td = tr.insertCell(-1);
    	    td.innerHTML = isPresent(access.address) ? access.address : "N/A";
    	}
    	var opts = {"bLengthChange" : false,
    			   //  "sDom" : "<'row'<'col-xs-6'l><'col-sm-offset-2 col-xs-2'f>r>t",
    			     "bInfo" : false
    	};
    	if(data.accountLocks.length <= 10)
    	{
    		opts["bPaginate"] = false;
    		opts["bFilter"] = false;
  		}
    	$(tbl).dataTable(opts);
    }
    
    return tbl;
}

function createIllegalLayerZoomTable(data) {
    
	var first = false;
	if ( typeof createIllegalLayerZoomTable.btnStatus == 'undefined' ) {
		createIllegalLayerZoomTable.btnStatus = false;
		first = true;
    }
	
	if(!first && !createIllegalLayerZoomTable.btnStatus)
		$('#illegalLayerZoomTbl').dataTable().fnDestroy();
	
	var tbl  = document.createElement("table");
	tbl.id = "illegalLayerZoomTbl";
	tbl.className = "table table-striped alert";
	
    $('#illegalLayerZoomTbl').replaceWith(tbl);
	
    var thead = tbl.createTHead();
    
    var tr = thead.insertRow(-1);
    var td;
    var th = document.createElement("th");
    th.colSpan = createIllegalLayerZoomTable.btnStatus ? "5" : "2";
    th.style.textAlign = "center";
    th.innerHTML = "Illegal layer zoom attempts";
    tr.appendChild(th);
    
    tr = thead.insertRow(-1);
    if(!createIllegalLayerZoomTable.btnStatus)
    {
    	
    	th = document.createElement("th");
    	th.innerHTML = "Total: " + data.illegalLayerZoomAttemptCount;
    	tr.appendChild(th);
    	
    	th = document.createElement("th");
	    if(data.illegalLayerZoomCount > 0)
	    {
	    	var inputElement = document.createElement('button');
			//inputElement.align = "center";
			inputElement.type = "button";
			inputElement.className = "statsBtns btn btn-default";
			inputElement.value = createIllegalLayerZoom.btnStatus ? "Hide" : "All";
			inputElement.innerHTML = createIllegalLayerZoomTable.btnStatus ? "Hide" : "All";
			inputElement.addEventListener('click', function()
												   {
													toggleShowIllegalLayerZoom(data);
												   }
			
			);
			th.appendChild(inputElement);
	    }
		tr.appendChild(th);
    }
    else
    { 
	    th = document.createElement("th");
	    th.innerHTML = "User";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
	    th.innerHTML = "Date";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
	    th.innerHTML = "Address";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
		tr.appendChild(th);
    }
    
	var tbody = document.createElement("tbody");
    tbl.appendChild(tbody);
    
	if(createIllegalLayerZoomTable.btnStatus)
    { 
    	for(var i=0; i<data.illegalLayerZoom.length; i++)
    	{
    		var access = data.illegalLayerZoom[i];
    		tr = tbody.insertRow(-1);
    	    td = tr.insertCell(-1);
    	    td.innerHTML = isPresent(access.id) ? access.id : "N/A";
    	    td = tr.insertCell(-1);
    	    td.innerHTML = isPresent(access.timestamp) ? timestampToDateTimeString(parseInt(access.timestamp)) : "N/A";
    	    td = tr.insertCell(-1);
    	    td.innerHTML = isPresent(access.address) ? access.address : "N/A";
    	}
    	var opts = {"bLengthChange" : false,
    			   //  "sDom" : "<'row'<'col-xs-6'l><'col-sm-offset-2 col-xs-2'f>r>t",
    			     "bInfo" : false
    	};
    	if(data.accountLocks.length <= 10)
    	{
    		opts["bPaginate"] = false;
    		opts["bFilter"] = false;
  		}
    	$(tbl).dataTable(opts);
    }
    
    return tbl;
}

function toggleShowUserActions(data)
{
	createActivityTable.btnStatus = !createActivityTable.btnStatus;
	activity = createActivityTable(data);
   // activity.style.width='100%';
   // activity.style.border = "1px; solid; black;";
}

function toggleShowLogins(data)
{
	createLoginTable.btnStatus = !createLoginTable.btnStatus;
	
	if(createLoginTable.btnStatus)
	{
	$.ajax({ 
        url : "home/allLogins", 
        type : "post", 
        success : function(logins) 
        		  { 
        			var list = [];
        			for(var i=0; i<Object.keys(logins).length; i++)
        				list.push(logins[Object.keys(logins)[i]]);
		        	data.lastUserLogins = list;
		        	createLoginTable(data);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
	}else
		createLoginTable(data);
}


function toggleShowAccountLocks(data)
{
	createLockTable.btnStatus = !createLockTable.btnStatus;
	
	if(createLockTable.btnStatus)
	{
	$.ajax({ 
        url : "home/alerts/accountLock", 
        type : "post", 
        success : function(locks) 
        		  { 
		        	var list = [];
		        	for(var i=0; i<Object.keys(locks).length; i++)
						list.push(locks[Object.keys(locks)[i]]);
		        	data.accountLocks = list;
		        	createLockTable(data);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
	}else
		createLockTable(data);
}


function toggleShowIllegalRequests(data)
{
	createIllegalRequestTable.btnStatus = !createIllegalRequestTable.btnStatus;
	
	if(createIllegalRequestTable.btnStatus)
	{
	$.ajax({ 
        url : "home/alerts/illegalRequest", 
        type : "post", 
        success : function(requests) 
        		  { 
		        	var list = [];
		        	for(var i=0; i<Object.keys(requests).length; i++)
						list.push(requests[Object.keys(requests)[i]]);
		        	data.illegalRequests = list;
		        	createIllegalRequestTable(data);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
	}else
		createIllegalRequestTable(data);
}


function toggleShowIllegalLayerAccess(data)
{
	createIllegalLayerAccessTable.btnStatus = !createIllegalLayerAccessTable.btnStatus;
	
	if(createIllegalLayerAccessTable.btnStatus)
	{
	$.ajax({ 
        url : "home/alerts/illegalLayerAccess", 
        type : "post", 
        success : function(accesses) 
        		  { 
		        	var list = [];
		        	for(var i=0; i<Object.keys(accesses).length; i++)
						list.push(accesses[Object.keys(accesses)[i]]);
		        	data.illegalLayerAccess = list;
		        	createIllegalLayerAccessTable(data);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
	}else
		createIllegalLayerAccessTable(data);
}

function toggleIllegalLayerZoom(data)
{
	createIllegalLayerZoomTable.btnStatus = !createIllegalLayerZoomTable.btnStatus;
	
	if(createIllegalLayerZoomTable.btnStatus)
	{
	$.ajax({ 
        url : "home/alerts/illegalLayerZoom", 
        type : "post", 
        success : function(accesses) 
        		  { 
		        	var list = [];
		        	for(var i=0; i<Object.keys(accesses).length; i++)
						list.push(accesses[Object.keys(accesses)[i]]);
		        	data.illegalLayerZoom = list;
		        	createIllegalLayerZoomTable(data);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
	}else
		createIllegalLayerZoomTable(data);
}

