function showAccountingManagement(data)
{
	enableZMaxIndex();
	
	createSearchForm(data);
	createAddButton();
	var accountingCon = document.getElementById('accountingTable');
	$(accountingCon).hide();
    
    var editCon = document.getElementById("editForm");
    $(editCon).hide();
    
    createEditForm();
    createModalAddForm();
    createAdminMenu(data);
	setPrimaryMenuButton("btnAccounting");
	populateCustomerSelectors();
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
	var accountingTable = $('#accountingTable');
	if(isPresent(accountingTable)) accountingTable.hide();
	
	input = document.getElementById("searchButton");
	
	input.addEventListener('click', function()
			   {
				createAccountingTable(data);
				 $('#accountingTable').show();
			   }
	 );
	
	createAccountingTable(data, true);
	
}

function createAddButton()
{
	var input = document.getElementById('addAccountingButton');
	input.addEventListener('click', function()
			   {
				 $('#addAccountingModal').modal();
			   }
	 );
}

function createAccountingTable(data, empty) {
	
    tbl  = document.createElement('table');
    
    tbl.id = 'accountingTbl';
    tbl.className = "table table-striped table-hover";
    
    var tr = tbl.insertRow(-1);
    var td;
       
    th = document.createElement("th");
    th.innerHTML = "Customer";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "User";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Date";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Type";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Amount";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Data";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Validity";
    tr.appendChild(th);
    
    th = document.createElement("th");
    
    button = document.createElement("button");
    button.className = "btn btn-default";
    button.type = "button";
    button.value = "Delete";
    button.innerHTML = "Delete";
    button.id = "DeleteButton";
    button.addEventListener('click', deleteAccounting);
    th.appendChild(button);
    tr.appendChild(th);
    
    th = document.createElement("th");
    tr.appendChild(th);
    th = document.createElement("th");
    tr.appendChild(th);
    
    if(isPresent(empty) && empty == true)
    {
    	var parent = document.getElementById('accountingTable');
        var old = document.getElementById('accountingTbl');
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
    var req = jQuery.param(values);
    
    var request = $.ajax({ 
        url : "accounting/search", 
        type : "post", 
        data : req,
        success : function(accountingInfo) 
        		  { 
        			var count = 0;
	        		for(var i=0; i<accountingInfo.length; i++)
	        		{
	        			var info = accountingInfo[i];
	        			
	        			tr = tbl.insertRow(-1);
	        			
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "customerName_"+count;
	        			td.className = "customerNames";
	        			td.innerHTML = info.customer;
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "userName_"+count;
	        			td.className = "userNames";
	        			td.innerHTML = info.user;
	        		
	        			td = tr.insertCell(-1);
	        			td.id = "date_"+count;
	        			td.className = "dates";
	        			td.innerHTML = timestampToDateTimeString(info.date);
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "type_"+count;
	        			td.className = "types";
	        			td.innerHTML = info.type;
	        		
	        			td = tr.insertCell(-1);
	        			td.id = "amount_"+count;
	        			td.className = "amounts";
	        			td.innerHTML = info.units;
	        		
	        			td = tr.insertCell(-1);
	        			td.id = "data_"+count;
	        			td.className = "data";
	        			td.innerHTML = info.referenceData;
	        		
	        			td = tr.insertCell(-1);
	        			td.id = "valid_"+count;
	        			td.className = "validity";
	        			td.innerHTML = info.valid? "Valid" : "Invalid";
	        			
	        			td = tr.insertCell(-1);
	        			var checkBoxCon = document.createElement("div");
	        			var input = document.createElement("input");
	        			input.type = "checkbox";
	        			input.id = "accountingRecordDeleteCheckbox_"+count;
	        			input.name = "accountingRecord_"+count;
	        			input.accounting = info.id;
	        			checkBoxCon.appendChild(input);
	        			td.appendChild(checkBoxCon);
	        			
	        			td = tr.insertCell(-1);
	        			var buttonCon = document.createElement("div");
	        			input = document.createElement("button");
	        			input.id = "accountingRecordEditButton_"+count;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-default";
	        			input.value = "Edit";
	        			input.innerHTML = "Edit";
	        			input.accounting = info.id;
	        			input.ordinal = count;
	        			input.addEventListener('click', editButtonListener);
	        			buttonCon.appendChild(input);
	        			td.appendChild(buttonCon);
	        			
	        			count++;
		        	}
		        	tbl.entryCount = count;
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
   
      var parent = document.getElementById('accountingTable');
      var old = document.getElementById('accountingTbl');
      parent.removeChild(old);
      parent.appendChild(tbl);
}

function createEditForm()
{
	var input = document.getElementById("editFormTextBoxdate");
	input.addEventListener('click', function(ev)
			{
				$(ev.target).datepicker({format : "dd/mm/yyyy"})
					.on('show', function(ev) { $(ev.target).maxZIndex(); });
				$(ev.target).datepicker('show');
			});
	
	input = document.getElementById("editFormCancelButton");
	input.addEventListener('click', function(ev)
			   						{
									 unsplitResults();
									 enableActionControls();
								   }
	 					  );
	
	input = document.getElementById("editFormSaveButton");
	input.addEventListener('click', function()
								   {
									 unsplitResults();
									 updateAccounting($('#editFormObj')[0], false);
									 enableActionControls();
								   }
						 );
	
	//input = document.getElementById("editFormListBoxcustomer");
	//input.addEventListener('change', populateUsers);
}

function createModalAddForm()
{
		var input = document.getElementById("addFormTextBoxdate");
		input.addEventListener('click', function(ev)
										{
											$(ev.target).datepicker({format : "dd/mm/yyyy"})
												.on('show', function(ev) { //$(ev.target).css('z-index','999999');
												$('.datepicker').maxZIndex(); 
												});
											$(ev.target).datepicker('show');
										}
							  );
	
		input = document.getElementById("addFormCancelButton");
		input.addEventListener('click', function(ev)
				   {
					 $('#addAccountingModal').modal('hide');
				   }
		 );
		
		input = document.getElementById("addFormSaveButton");
		input.addEventListener('click', function()
				   {
					  if(document.getElementById('accountingTable').split == true) unsplitResults();
					  updateAccounting($('#addFormObj')[0], true);
					  //clear form
					    $('#addFormObj').find("input[type=text] , textarea").each(function(){
					                $(this).val('');            
					    });
					  $('#addAccountingModal').modal('hide');
				   }
		 );
		
		input = document.getElementById("addFormListBoxcustomer");
		input.addEventListener('change', populateUsers);
}

function populateCustomerSelectors()
{
	listCustomers([
	                
	                {func: populateSelector, args:[$('#addFormListBoxcustomer')[0],"__funcRet", "None", "--None--", false]}],
	                true);
}

function populateUsers(ev)
{
	var targetId;
	if(ev.target.id == 'editFormListBoxcustomer') targetId = 'editFormListBoxuser';
	else if(ev.target.id == 'addFormListBoxcustomer') targetId = 'addFormListBoxuser';
	
	$('#'+targetId).empty();
	var customer = $('#'+ev.target.id+' option:selected').val(); //get selected customer from dropdown
	var select = document.getElementById(targetId);
	option = document.createElement("option");
	option.value = "None";
	option.text = customer != 'None' ? "Select a user" : '--None--';
	option.selected = true;
	option.disabled = true;
	select.appendChild(option);
	
	//if(taxonomy == 'None') return;
	
	var req = 'customerNames='+customer;
	req += '&activeUsers=true';
	 $.ajax({ 
        url : "users/list",
        type : "post", 
        data : req,
        success : function(userInfo) 
        		  {
        			var users = userInfo[customer];
		        	for(var i=0; i<users.length; i++)
		        	{
		        		var option = document.createElement("option");
		        		option.value = users[i].name;
		        		option.text = users[i].name;
		        		select.appendChild(option);
		        	}
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function listCustomers(postProcessingFuncs, active)
{
	$.ajax({
        url : "customers/list",
        type : "post", 
        data : active,
        
        success : function(customers) {
        	var customerNames = [];
        	for(var i=0; i<customers.length; i++)
        		customerNames.push(customers[i].name);
        	applyFuncs(postProcessingFuncs, customerNames);
        },
        
        error : function(jqXHR, textStatus, errorThrown) 
		 {
			alert("The following error occured: " + textStatus, errorThrown);
		 }
	});
}

function showEditBetweenResults(ev)
{
	var offset = 1; //+1 for table header TODO depends on table heading
	var pos = ev.target.ordinal+offset; 
	
	var tableCon = $('#accountingTable')[0];
	var table = $('#accountingTbl')[0];
	var editCon = $('#editForm')[0];
	var mainScreen = $('.mainScreen')[0];
	
	
	trs = table.getElementsByTagName("tr");
	var removedTrs = [];
	var originalLength = trs.length;
	
	var splitTbl = document.createElement("table");
	splitTbl.id = 'splitAccountingTbl';
	splitTbl.className = 'table table-striped table-hover';
	
	var info = [];
	for(var i=pos; i<originalLength; i++)
	{
		var cl = trs[pos].cloneNode(true);
		var editBtn = document.getElementById("accountingRecordEditButton_"+(i-offset));
		
		if(i>pos)
		{
			removedTrs.push(cl);
			info[i-pos-1] = {};
			info[i-pos-1].accounting = editBtn.accounting;
			info[i-pos-1].ordinal = editBtn.ordinal;
		}
		else
		{
			splitTbl.stash = cl;
			splitTbl.stash.accounting = editBtn.accounting;
			splitTbl.stash.ordinal = editBtn.ordinal;
		}
		table.deleteRow(pos);
	}
	
	splitTbl.insertRow(-1);
	var parent = splitTbl.getElementsByTagName("tr")[0].parentNode;
	splitTbl.deleteRow(0);
	
	mainScreen.removeChild(editCon);
	tableCon.appendChild(editCon);
	tableCon.appendChild(splitTbl);
	
	for(var i=0; i<removedTrs.length; i++)
	{
		parent.appendChild(removedTrs[i]);
		var editBtn = $(removedTrs[i]).find("#accountingRecordEditButton_"+(pos-offset+i+1))[0];
		editBtn.accounting = info[i].accounting;
		editBtn.ordinal = info[i].ordinal;
		editBtn.addEventListener('click', editButtonListener);
		
		var deleteCheckbox = $(removedTrs[i]).find("#accountingRecordDeleteCheckbox_"+(pos-offset+i+1))[0];
		deleteCheckbox.accounting = info[i].accounting;
	}
	
	$(editCon).show();
	
	tableCon.split = true;
}

function unsplitResults()
{
	var tableCon = $('#accountingTable')[0];
	var table = $('#accountingTbl')[0];
	var splitTable = $('#splitAccountingTbl')[0];
	var editCon = $('#editForm')[0];
	var mainScreen = $('.mainScreen')[0];
	var offset = 1; //+1 for table header TODO depends on table heading
	$(editCon).hide();
	
	var parent = table.getElementsByTagName("tr")[0].parentNode;
	
	var info = [];
	var trs = splitTable.getElementsByTagName("tr");
	var pos = table.getElementsByTagName("tr").length-offset; //TODO depends on table heading
	var splitLen = trs.length;
	
	info[0] = {};
	info[0].accounting = splitTable.stash.accounting;
	info[0].ordinal = splitTable.stash.ordinal;
	parent.appendChild(splitTable.stash);
	for(var i=0; i<splitLen; i++)
	{
		var editBtn = document.getElementById("accountingRecordEditButton_"+(i+pos+1));
		info[i+1] = {};
		info[i+1].accounting = editBtn.accounting;
		info[i+1].ordinal = editBtn.ordinal;
		parent.appendChild(trs[0]);
	}
	
	tableCon.removeChild(editCon);
	tableCon.removeChild(splitTable);
	
	mainScreen.appendChild(editCon);
	
	var len = table.getElementsByTagName("tr").length-offset; 
	for(var i=pos; i<len; i++)
	{
		var editBtn = document.getElementById("accountingRecordEditButton_"+(i));
		editBtn.accounting = info[i-pos].accounting;
		editBtn.ordinal = info[i-pos].ordinal;
		editBtn.addEventListener('click', editButtonListener);
		
		var deleteCheckbox = document.getElementById("accountingRecordDeleteCheckbox_"+(i));
		deleteCheckbox.accounting = info[i-pos].accounting;
	}
	
	tableCon.split = false;
	
}

function editButtonListener(ev)
{ 
	disableActionControls();
	$('#editForm')[0].accountingId = ev.target.accounting;
	copyToEditForm(ev); 
	showEditBetweenResults(ev);
}

function copyToEditForm(ev)
{
	var ordinal = ev.target.ordinal;
	$('#editFormObj')[0].reset();

//	$('#editFormListBoxcustomer option').prop('selected', false);
//	$('#editFormListBoxcustomer option[value="'+$("#customer_"+ordinal).html()+'"]').prop('selected', true);
//	
//	$('#editFormListBoxuser option').prop('selected', false);
//	$('#editFormListBoxuser option[value="'+$("#user_"+ordinal).html()+'"]').prop('selected', true);
//	
	$('#editFormListBoxtype option').prop('selected', false);
	$('#editFormListBoxtype option[value="'+$("#type_"+ordinal).html()+'"]').prop('selected', true);
	
	$('#editFormTextBoxdate').prop('value', $('#date_'+(ordinal)).html());
	$('#editFormTextBoxamount').prop('value', $('#amount_'+(ordinal)).html());
	$('#editFormTextBoxdata').prop('value', $('#data_'+(ordinal)).html());
}

function updateAccounting(form, create, postProcessingFuncs)
{

	var values = $(form).serializeArray();
	//values = formRemoveIfValue(values, 'users', 'None');//[{name: 'customer', value: 'None'}], [{name: 'isActive', [{value: 'on', newValue: 'true'}, {value: 'off', newValue: 'false'}]
	var dateParts = formFindValue(values, 'date').split('/');
	// new Date(year, month [, date [, hours[, minutes[, seconds[, ms]]]]])
	  
	values = formReplaceValue(values, 'date',  new Date(dateParts[2], dateParts[1]-1, dateParts[0]).getTime());
	
	var userInfo = jQuery.param(values);
	var req = {};
	for(var i=0; i<values.length; i++)
	{
		req[values[i].name] = {}
		req[values[i].name] = values[i].value;
	}
	
	if(!create) req.id = $("#editForm")[0].accountingId;
	req = JSON.stringify(req);
	
    var request = $.ajax({ 
        url : create ? "accounting/add" : "accounting/update",
        type : "post", 
        data : req,
        dataType : "json",
        contentType : "application/json",
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
					createAccountingTable(data, $('#searchForm')[0]);
					applyFuncs(postProcessingFuncs, response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function deleteAccounting(ev, postProcessingFuncs)
{
	var len = $('#accountingTbl')[0].entryCount;
	var reqObject = [];
	var req = "accounting=";
	
	for(var i=0; i<len; i++)
	{
		if($('#accountingRecordDeleteCheckbox_'+i).is(':checked'))
		{
			var chkBox = $('#accountingRecordDeleteCheckbox_'+i)[0];
			//req += "&";
			if(req != 'accounting=') req += "%2C";
			
			req += chkBox.accounting;
		}
	}
	
	var request = $.ajax({ 
        url : "accounting/delete",
        type : "post", 
        data : req,
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
		        	var parent = $('#accountingTable')[0];
					parent.removeChild(parent.childNodes[1]);
					parent.appendChild(createAccountingTable(data, $('#searchForm')[0]));
					
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
	$('#addAccountingButton').prop('disabled', true);
	$('#deleteButton').prop('disabled', true);
	var len = $('#accountingTbl')[0].entryCount;
	for(var i=0; i<len; i++)
	{
		$('#accountingRecordEditButton_'+i).prop('disabled', true);
		$('#accountingRecordDeleteCheckbox_'+i).prop('disabled', true);
	}
}

function enableActionControls()
{
	$('#searchButton').prop('disabled', false);
	$('#addAccountingButton').prop('disabled', false);
	$('#deleteButton').prop('disabled', false);
	var len = $('#accountingTbl')[0].entryCount;
	for(var i=0; i<len; i++)
	{
		$('#accountingRecordEditButton_'+i).prop('disabled', false);
		$('#accountingRecordDeleteCheckbox_'+i).prop('disabled', false);
	}
}