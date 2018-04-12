function requestCustomerInfo(postProcessingFuncs)
{
	$.ajax({ 
        url : "customers/list", 
        type : "post", 
        success : function(customerInfo) 
        		  { 
		        	applyFuncs(postProcessingFuncs, customerInfo);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
}

function populateCustomerList(customerInfo, select)
{
	$(select).empty();
	var option = document.createElement("option");
	option.value = "None";
	option.text = "--None--";
	select.appendChild(option);
	
	for(var i=0; i<customerInfo.length; i++)
	{
		var option = document.createElement("option");
		option.value = customerInfo[i].name;
		option.text = customerInfo[i].name;
		select.appendChild(option);
	}
}

function showUserManagement(data)
{
	enableZMaxIndex();
	
	createSearchForm(data);
	createAddButton();
	var usersCon = document.getElementById('userTable');
	$(usersCon).hide();
    
    var editCon = document.getElementById("editForm");
    $(editCon).hide();
    
    createEditForm();
    createModalAddForm();
    createAdminMenu(data);
	setPrimaryMenuButton("btnUserManagement");
	enableActionControls();
}

function showToolTip(event)
{
	event = event || window.event;
	var x = event.pageX;
    var y = event.pageY;
    $(".tooltip").css({"left":x+"px","top":y+"px"});
    $(".tooltip").show();
}

function hideToolTip(event)
{
	event = event || window.event;
	$(".tooltip").hide();
}

function createSearchForm(data, id)
{
	var userTable = $('#userTable');
	if(isPresent(userTable)) userTable.hide();
	
	input = document.getElementById("searchButton");
	
	input.addEventListener('click', function()
			   {
				createUserTable(data);
				 $('#userTable').show();
			   }
	 );
	
	createUserTable(data, true);
	
}

function createAddButton()
{
	var input = document.getElementById('addUserButton');
	input.addEventListener('click', function()
			   {
				 $('#addUserModal').modal();
			   }
	 );
}

function createUserTable(data, empty) {
	
    tbl  = document.createElement('table');
    //tbl.className = "geopolis-admin-widget geopolis-admin-widget-content";
    
    tbl.id = 'userTbl';
    tbl.className = "table table-striped table-hover";
    
    var thead = tbl.createTHead();
 
    var td;
    
    var tr = thead.insertRow(-1);
    var th = document.createElement("th");
    th.innerHTML = "Name";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Customer";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Status";
    tr.appendChild(th);
    
    th = document.createElement("th");
    
    button = document.createElement("button");
    button.className = "btn btn-default";
    button.type = "button";
    button.value = "Delete";
    button.innerHTML = "Delete";
    button.id = "deleteButton";
    button.addEventListener('click', deleteUsers);
    th.appendChild(button);
    tr.appendChild(th);
    
    th = document.createElement("th");
    tr.appendChild(th);
    th = document.createElement("th");
    tr.appendChild(th);
    
    var tbody = document.createElement("tbody");
    tbl.appendChild(tbody);
    
    if(isPresent(empty) && empty == true)
    {
    	var parent = document.getElementById('userTable');
        var old = document.getElementById('userTbl');
        parent.removeChild(old);
        parent.appendChild(tbl);
    	return tbl;
    }
    
    var form = document.getElementById('searchForm');
    var values = $(form).serializeArray();
    var paramsToConvert = [];
    if(formParameterPresent(values, 'userNames')) paramsToConvert.push('userNames');
 
    if(formParameterPresent(values, 'customerNames')) paramsToConvert.push('customerNames');
   
    formConvertDelimitedToList(values, paramsToConvert);
    var userInfo = jQuery.param(values);
    
    $.ajax({ 
        url : "users/search", 
        type : "post", 
        data : userInfo,
        success : function(userInfo) 
        		  { 
		        	var cus = Object.keys(userInfo);
		        	
		        	var count = 0;
		        	for(var i=0; i<cus.length; i++)
		        	{
		        		var info = userInfo[cus[i]];
		        		for(var j=0; j<info.length; j++)
		        		{
		        			tr = tbody.insertRow(-1);
		        			
		        			td = tr.insertCell(-1);
		        			td.id = "userName_"+count;
		        			td.className = "userNames";
		        			td.innerHTML = info[j].name;
		        			td.user = info[j].name;
		        			td.customer = cus[i];
		        				
		        			td = tr.insertCell(-1);
		        			td.id = "customerName_"+count;
		        			td.className = "customerNames";
		        			td.innerHTML = cus[i];
		        			
		        			td = tr.insertCell(-1);
		        			td.className = "activeStatus";
		        			td.innerHTML = info[j].active ? "Active" : "Inactive";
		        			
		        			td = tr.insertCell(-1);
		        			var checkBoxCon = document.createElement("div");
		        			var input = document.createElement("input");
		        			input.type = "checkbox";
		        			input.id = "userRecordDeleteCheckbox_"+count;
		        			input.name = "userRecord_"+count;
		        			input.className = "userRecordDeleteCheckboxes";
		        			input.user = info[j].name;
		        			input.customer = cus[i];
		        			checkBoxCon.appendChild(input);
		        			td.appendChild(checkBoxCon);
		        			
		        			td = tr.insertCell(-1);
		        			var buttonCon = document.createElement("div");
		        			input = document.createElement("button");
		        			input.id = "userRecordEditButton_"+count;
		        			input.type = "button";
		        			input.className = "btn btn-sm btn-default userRecordEditButtons";
		        			input.value = "Edit";
		        			input.innerHTML = "Edit";
		        			input.user = info[j].name;
		        			input.customer = cus[i];
		        			input.ordinal = count;
		        			input.addEventListener('click', editButtonListener);
		        			buttonCon.appendChild(input);
		        			td.appendChild(buttonCon);
		        			
		        			td = tr.insertCell(-1);
		        			buttonCon = document.createElement("div");
		        			input = document.createElement("button");
		        			input.id = "userRecordActivateButton_"+count;
		        			input.type = "button";
		        			input.className = "btn btn-sm btn-default userRecordActivateButtons";
		        			input.value = info[j].active ? "Deactivate" : "Activate";
		        			input.innerHTML = info[j].active ? "Deactivate" : "Activate";
		        			input.user = info[j].name;
		        			input.customer = cus[i];
		        			input.addEventListener('click', activateButtonToggleListener);
		        			buttonCon.appendChild(input);
		        			td.appendChild(buttonCon);
		        			
		        			count++;
		        			
		        		}
		        	}
		        	tbl.entryCount = count;
		        	var parent = document.getElementById('userTable');
		        	$('#userTbl').dataTable().fnDestroy();
		            var old = document.getElementById('userTbl');
		            parent.removeChild(old);
		            parent.appendChild(tbl);
		           //$(document).ready(function(){$('#userTbl').dataTable({"bSort" : false});});
		            $('#userTbl').dataTable();
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
}

function createEditForm()
{
	var input = document.getElementById("textBoxexpirationDate");
	input.addEventListener('click', function(ev)
			{
				$(ev.target).datepicker({format : "dd/mm/yyyy"})
					.on('show', function(ev) { $(ev.target).maxZIndex(); });
				$(ev.target).datepicker('show');
			});
	
	input = document.getElementById('btnisActive');
	input.addEventListener('click', function(ev)
									{  if(ev.target.value == 'Activate')
									   {
										ev.target.value = 'Deactivate';
										ev.target.innerHTML = 'Deactivate';
									   }else if(ev.target.value == "Deactivate")
									   {
										ev.target.value = 'Activate';
										ev.target.innerHTML = 'Activate';
									   }  
								    });
	
	
	input = document.getElementById("cancelButton");
	input.addEventListener('click', function(ev)
			   						{
									 unsplitResults();
									 $('#userTable')[0].removeChild($('#userTbl')[0]);
									 $('#userTable')[0].appendChild(window.savedTable);
									 $('#userTbl').dataTable();
									 $('#userTbl').show();
									 enableActionControls();
								   }
	 					  );
	
	input = document.getElementById("saveButton");
	input.addEventListener('click', function()
								   {
									 unsplitResults();
									//$(table).dataTable({"bDestroy" : true});
									 $('#userTbl').dataTable().fnDestroy();
									 updateUser($('#editFormObj')[0], false);
									 enableActionControls();
								   }
						 );
	
	var select = document.getElementById("listBoxcustomer");
	requestCustomerInfo([{func: populateCustomerList, args: ["__funcRet",select]}]);
}

function createModalAddForm()
{
		var input = document.getElementById("addFormTextBoxexpirationDate");
		input.addEventListener('click', function(ev)
										{
											$(ev.target).datepicker({format : "dd/mm/yyyy"})
												.on('show', function(ev) { //$(ev.target).css('z-index','999999');
												$('.datepicker').maxZIndex(); 
												});
											$(ev.target).datepicker('show');
										}
							  );
		
		input = document.getElementById("addFormBtnisActive");
		input.addEventListener('click', function(ev)
										{  if(ev.target.value == 'Activate')
										   {
											ev.target.value = 'Deactivate';
											ev.target.innerHTML = 'Deactivate';
										   }else if(ev.target.value == "Deactivate")
										   {
											ev.target.value = 'Activate';
											ev.target.innerHTML = 'Activate';
										   }  
									    });
		
	
		input = document.getElementById("addFormCancelButton");
		input.addEventListener('click', function(ev)
				   {
					 $('#addUserModal').modal('hide');
				   }
		 );
		
		input = document.getElementById("addFormSaveButton");
		input.addEventListener('click', function()
				   {
					  if(document.getElementById('userTable').split == true) unsplitResults();
					  updateUser($('#addFormObj')[0], true);
					  //clear form
					    $('#addFormObj').find("input[type=text] , textarea").each(function(){
					                $(this).val('');            
					    });
					  $('#addUserModal').modal('hide');
				   }
		 );
		
		var select = document.getElementById("addFormListBoxcustomer");
		requestCustomerInfo([{func: populateCustomerList, args: ["__funcRet",select]}]);
}

function showEditBetweenResults(ev)
{
	var offset = 1; //+1 for table header TODO depends on table heading
	//var pos = ev.target.ordinal+offset; 
	
	var tableCon = $('#userTable')[0];
	var table = $('#userTbl')[0];
	var editCon = $('#editForm')[0];
	var mainScreen = $('.mainScreen')[0];
	var pos = $('#userTbl tr').index($(ev.target).closest('tr'));

	/*$(table).dataTable(
			{"bFilter" : false,
			  "bInfo" : false,
			  "bLengthChange" : false,
			  "aaSorting" : [[]],
			  "sDom" : "<'row'<'col-xs-6'l><'col-sm-offset-4 col-xs-2'f>r>t",
			  "bDestroy" : true}
			);
	*/
	
	trs = table.getElementsByTagName("tr");
	var removedTrs = [];
	var originalLength = trs.length;
	
	var splitTbl = document.createElement("table");
	splitTbl.id = 'splitUserTbl';
	splitTbl.className = 'table table-striped table-hover';
	
	var info = [];
	for(var i=pos; i<originalLength; i++)
	{
		var cl = trs[i].cloneNode(true);
		//var editBtn = document.getElementById("userRecordEditButton_"+(i-offset));
		var editBtn = $(trs[i]).find("button")[0];
		
		if(i>pos)
		{
			removedTrs.push(cl);
			info[i-pos-1] = {};
			info[i-pos-1].user = editBtn.user;
			info[i-pos-1].customer = editBtn.customer;
			info[i-pos-1].ordinal = editBtn.ordinal;
		}
		else
		{
			splitTbl.stash = cl;
			splitTbl.stash.user = editBtn.user;
			splitTbl.stash.customer = editBtn.customer;
			splitTbl.stash.ordinal = editBtn.ordinal;
		}
	}
	
	var clonedTable = table.cloneNode();
	$(table).hide();
	$(table).dataTable().fnDestroy();
	$(tableCon)[0].removeChild(table);
	$(tableCon)[0].appendChild(clonedTable);
	window.savedTable = table;
	table = clonedTable;
	
	for(var i=pos; i<originalLength; i++)
		table.deleteRow(pos);
	
	splitTbl.insertRow(-1);
	var parent = splitTbl.getElementsByTagName("tr")[0].parentNode;
	splitTbl.deleteRow(0);
	
	mainScreen.removeChild(editCon);
	tableCon.appendChild(editCon);
	tableCon.appendChild(splitTbl);
	
	for(var i=0; i<removedTrs.length; i++)
	{
		parent.appendChild(removedTrs[i]);
		//var editBtn = $(removedTrs[i]).find("#userRecordEditButton_"+(pos-offset+i+1))[0];
		var editBtn = $(removedTrs[i]).find("button")[0];
		editBtn.user = info[i].user;
		editBtn.customer = info[i].customer;
		editBtn.ordinal = info[i].ordinal;
		editBtn.addEventListener('click', editButtonListener);
		
		//var activateBtn = $(removedTrs[i]).find("#userRecordActivateButton_"+(pos-offset+i+1))[0];
		var activateBtn = $(removedTrs[i]).find("button")[1];
		activateBtn.user = info[i].user;
		activateBtn.customer = info[i].customer;
		activateBtn.addEventListener('click', activateButtonToggleListener);
		
		//var deleteCheckbox = $(removedTrs[i]).find("#userRecordDeleteCheckbox_"+(pos-offset+i+1))[0];
		var deleteCheckbox = $(removedTrs[i]).find("input")[0];
		deleteCheckbox.user = info[i].user;
		deleteCheckbox.customer = info[i].customer;
	}
	
	$(editCon).show();
	
	tableCon.split = true;
}

function unsplitResults()
{
	var tableCon = $('#userTable')[0];
	var table = $('#userTbl')[0];
	var splitTable = $('#splitUserTbl')[0];
	var editCon = $('#editForm')[0];
	var mainScreen = $('.mainScreen')[0];
	var offset = 1; //+1 for table header TODO depends on table heading
	$(editCon).hide();
	
	var parent = table.getElementsByTagName("tbody")[0];
	
	var info = [];
	var trs = splitTable.getElementsByTagName("tr");
	var pos = table.getElementsByTagName("tr").length-offset; //TODO depends on table heading
	var splitLen = trs.length;
	
	info[0] = {};
	info[0].user = splitTable.stash.user;
	info[0].customer = splitTable.stash.customer;
	info[0].ordinal = splitTable.stash.ordinal;
	parent.appendChild(splitTable.stash);
	
	//var firstBtnId = $(trs[0]).find("button")[0].id;
	//var absolutePos = parseInt(firstBtnId.substring(firstBtnId.indexOf("_")+1));
	
	for(var i=0; i<splitLen; i++)
	{
		//var editBtn = document.getElementById("userRecordEditButton_"+(i+pos+1));
		var editBtn = $(trs[0]).find("button")[0];
		info[i+1] = {};
		info[i+1].user = editBtn.user;
		info[i+1].customer = editBtn.customer;
		info[i+1].ordinal = editBtn.ordinal;
		parent.appendChild(trs[0]);
	}
	
	tableCon.removeChild(editCon);
	tableCon.removeChild(splitTable);
	
	mainScreen.appendChild(editCon);
	
	var len = table.getElementsByTagName("tr").length-offset; 
	for(var i=0; i<splitLen+1; i++)
	{
		var editBtn = document.getElementById("userRecordEditButton_"+info[i].ordinal);
		editBtn.user = info[i].user;
		editBtn.customer = info[i].customer;
		editBtn.ordinal = info[i].ordinal;
		editBtn.addEventListener('click', editButtonListener);
		
		var activateBtn = document.getElementById("userRecordActivateButton_"+info[i].ordinal);
		activateBtn.user = info[i].user;
		activateBtn.customer = info[i].customer;
		activateBtn.addEventListener('click', activateButtonToggleListener);
		
		var deleteCheckbox = document.getElementById("userRecordDeleteCheckbox_"+info[i].ordinal);
		deleteCheckbox.user = info[i].user;
		deleteCheckbox.customer = info[i].customer;
	}
	
	tableCon.split = false;
	
}

function editButtonListener(ev)
{ 
	disableActionControls();
	retrieveUser(ev, [{func: showEditBetweenResults, args: [ev]}]); 
}

function activateButtonToggleListener(ev) 
{
	var updateEditFormActiveBtn =  function(val) 
								   { 
										$('#btnisActive').prop('value', val ? 'Deactivate' : 'Activate');
										$('#btnisActive').html(val ? 'Deactivate' : 'Activate');
								   };
	var updateActivateBtnOnSuccess = function(ev)
								  {
										if(ev.target.value == 'Deactivate')
										{
											ev.target.value = 'Activate';
											ev.target.innerHTML = 'Activate';
										}else if(ev.target.value == 'Activate')
										{
											updateEditFormActiveBtnArg = true;
											ev.target.value = 'Deactivate';
											ev.target.innerHTML = 'Deactivate';
										}
								  };
	var updateEditFormActiveBtnArg;
	if(ev.target.value == 'Deactivate')
		updateEditFormActiveBtnArg = false;
	else if(ev.target.value == 'Activate')
		updateEditFormActiveBtnArg = true;
	retrieveUser(ev, [{func: updateEditFormActiveBtn, args: [updateEditFormActiveBtnArg]},
	                  {func: updateUser, args: [$('#editFormObj')[0], false, {func: updateActivateBtnOnSuccess, args: [ev]}]},
	                  ]);
}

function retrieveUser(ev, postProcessingFuncs)
{
	name = ev.target.user;
	customer = ev.target.customer;
	req = "name="+name+((customer != 'None') ? ("&customer="+customer) : "");
	$.ajax({ 
        url : "users/show", 
        type : "post", 
        data : req,
        success : function(userInfo) 
        		  { 
        			if(!isPresent(userInfo))
        			{
        				alert("User " + name + " not found!");
        				return;
        			}
		        	var attrs = Object.keys(userInfo);
		        	
		        	for(var i=0; i<attrs.length; i++)
		        	{
		        		if(attrs[i] != 'customer' && attrs[i] != 'isActive')
		        			$('#textBox'+attrs[i]).prop('value', userInfo[attrs[i]]);
		        		if(attrs[i] == 'customer')
		        		{
		        			$('#listBox'+attrs[i] + ' option').prop("selected", false);
		        			$('#listBox'+attrs[i]+ ' option[value="'+customer+'"]').prop('selected', true); //make the current customer selected in list selector
		        		}
		        		if(attrs[i] == 'isActive' && userInfo.isActive == true)
		        		{
		        			$('#btn'+attrs[i]).prop('value', 'Deactivate');
		        			$('#btn'+attrs[i]).html('Deactivate');
		        			
		        		}else
		        		{
		        			$('#btn'+attrs[i]).prop('value', 'Activate');
		        			$('#btn'+attrs[i]).html('Activate');
		        		}
		        	    //$('#editButton').prop('value', 'Edit');
		        		//$('#editButton').html('Edit');
		        	}
		        	var expDate = new Date(userInfo['expirationDate']);
		      
		        	function addZero(num) { return (num >= 0 && num < 10) ? "0" + num : num + ""; }
		        	var expDateStr = addZero(expDate.getDate()) + '/' + addZero(expDate.getMonth()+1) + "/" + expDate.getFullYear();
		        	$('#textBoxexpirationDate').prop('value', expDateStr);
		        	
		        	applyFuncs(postProcessingFuncs, userInfo);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
}

function updateUser(form, create, postProcessingFuncs)
{

	var values = $(form).serializeArray();
	var usr = formFindValue(values, 'systemName');
	values = formRemoveIfValue(values, 'customer', 'None');//[{name: 'customer', value: 'None'}], [{name: 'isActive', [{value: 'on', newValue: 'true'}, {value: 'off', newValue: 'false'}]
	var dateParts = formFindValue(values, 'expirationDate').split('/');
	// new Date(year, month [, date [, hours[, minutes[, seconds[, ms]]]]])
	  
	values = formReplaceValue(values, 'expirationDate',  new Date(dateParts[2], dateParts[1]-1, dateParts[0], 23, 59, 59).getTime());
	var isActive = $('#btnisActive').val() == 'Deactivate' ? true : false;
	var toPush = {};
	toPush.name = 'isActive';
	toPush.value = isActive;
	values.push(toPush);
	
	if(!create)
	{
		var userRecords = $('#userTbl > tbody > tr');
		var rec = null;
		for(var i=0; i<userRecords.length; i++)
		{
			rec = $(userRecords[i]).find(".userNames");
			if(rec && rec[0])
			{
				if(rec[0].innerHTML == usr)
					break;
			}
			rec = null;
		}
		if(rec == null) { alert("Could not find user in table"); return;}
		toPush = {};
		toPush.name = 'originalCustomer';
		toPush.value = $(userRecords[i]).find(".customerNames")[0].innerHTML;
		if(toPush.value != 'None') values.push(toPush);
	}
	
	var userInfo = jQuery.param(values);
	
    $.ajax({ 
        url : create ? "users/add" : "users/update",
        type : "post", 
        data : userInfo,
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
					createUserTable(data, $('#searchForm')[0]);
					applyFuncs(postProcessingFuncs, response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function deleteUsers(ev, postProcessingFuncs)
{
	var len = $('#userTbl')[0].entryCount;
	var req = "";
	var reqUser = 'users=';
	var reqCustomer = 'customers=';
	
	for(var i=0; i<len; i++)
	{
		if($('#userRecordDeleteCheckbox_'+i).is(':checked'))
		{
			//req += "&";
			if(reqUser != 'users=') reqUser += "%2C";
			if(reqCustomer != 'customers=') reqCustomer += "%2C";
			
			reqUser += $('#userName_'+i)[0].innerHTML;
			reqCustomer += $('#customerName_'+i)[0].innerHTML;
			//req += "user=" + chkBox.user + "&customer" + chkBox.customer;
			//reqObject.push({user: chkBox.user, customer: chkBox.customer});
		}
	}
	req = reqUser + "&" + reqCustomer;
	$.ajax({ 
        url : "users/delete",
        type : "post", 
        data : req,
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
		        	var parent = $('#userTable')[0];
					parent.removeChild(parent.childNodes[1]);
					parent.appendChild(createUserTable(data, $('#searchForm')[0]));
					
					applyFuncs(postProcessingFuncs, response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function disableActionControls()
{
	$('#searchButton').prop('disabled', true);
	$('#addUserButton').prop('disabled', true);
	$('#deleteButton').prop('disabled', true);
	//var len = $('#userTbl')[0].entryCount;
	$(".userRecordDeleteCheckboxes").prop('disabled', true);
	$(".userRecordEditButtons").prop('disabled', true);
	$(".userRecordActivateButtons").prop('disabled', true);
//	for(var i=0; i<len; i++)
//	{
//		$('#userRecordEditButton_'+i).prop('disabled', true);
//		$('#userRecordActivateButton_'+i).prop('disabled', true);
//		$('#userRecordDeleteCheckbox_'+i).prop('disabled', true);
//	}
}

function enableActionControls()
{
	$('#searchButton').prop('disabled', false);
	$('#addUserButton').prop('disabled', false);
	$('#deleteButton').prop('disabled', false);
	//var len = $('#userTbl')[0].entryCount;
	$(".userRecordDeleteCheckboxes").prop('disabled', false);
	$(".userRecordEditButtons").prop('disabled', false);
	$(".userRecordActivateButtons").prop('disabled', false);
	
/*	for(var i=0; i<len; i++)
	{
		$('#userRecordEditButton_'+i).prop('disabled', false);
		$('#userRecordActivateButton_'+i).prop('disabled', false);
		$('#userRecordDeleteCheckbox_'+i).prop('disabled', false);
	}*/
}