function requestCustomerInfo(postProcessingFuncs)
{
	var request = $.ajax({ 
        url : "customers/show", 
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
	option.text = "--None--"
	select.appendChild(option);
	
	for(var i=0; i<customerInfo.length; i++)
	{
		var option = document.createElement("option");
		option.value = customerInfo[i].name;
		option.text = customerInfo[i].name;
		select.appendChild(option);
	}
}

function showCustomerManagement(data)
{
	enableZMaxIndex();
	
	createSearchForm(data);
	createAddButton();
	createAddActivationButton();
   
    $("#customerTable").hide();
    $("#activationTable").hide();
    
    $("#editForm").hide();
    $("#editActivationForm").hide();
    
    createEditForm();
    createEditActivationForm();
    createModalAddForm();
    createModalAddActivationForm();
    
    createAdminMenu(data);
	setPrimaryMenuButton("btnCustomerManagement");
	enableActionControls();
}

function createSearchForm(data, id)
{
	var customerTable = $('#customerTable');
	if(isPresent(customerTable)) customerTable.hide();
	
	input = document.getElementById('searchButton');
	input.addEventListener('click', function()
			   {
				 $('#activationTable').hide();
				 $('#addCustomerButton').show();
				 createCustomerTable(data, false);
				 $('#customerTable').show();
			   }
	 );
	
	createCustomerTable(data, true);
}

function createAddButton()
{
	input = document.getElementById('addCustomerButton');
	input.addEventListener('click', function()
			   {
				 $('#addCustomerModal').modal();
			   }
	 );
}

function createAddActivationButton()
{
	input = document.getElementById('addActivationButton');
	input.addEventListener('click', function()
			   {
				 $('#addActivationModal').modal();
			   }
	 );
}

function createCustomerTable(data, empty) {
	
    tbl  = document.createElement('table');
    //tbl.className = "geopolis-admin-widget geopolis-admin-widget-content";
    
    tbl.id = 'customerTbl';
    tbl.className = "table table-striped table-hover";
    
    var tr = tbl.insertRow(-1);
    var td;
//    var th = document.createElement("th");
//    th.colSpan = "3";
//    th.style.textAlign = "center";
//    th.innerHTML = "Results";
//    tr.appendChild(th);
    
    tr = tbl.insertRow(-1);
    var th = document.createElement("th");
    th.innerHTML = "Name";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "eMail";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Code";
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
    button.id = "DeleteButton";
    button.addEventListener('click', deleteCustomers);
    th.appendChild(button);
    //th.className = "geopolis-admin-widget-header";
    tr.appendChild(th);
    
    th = document.createElement("th");
    tr.appendChild(th);
    
    th = document.createElement("th");
    tr.appendChild(th);
    
    if(isPresent(empty) && empty == true)
    {
    	var parent = document.getElementById('customerTable');
        var old = document.getElementById('customerTbl');
        parent.removeChild(old);
        parent.appendChild(tbl);
    	return tbl;
    }
    
    var form = document.getElementById('searchForm');
    var values = $(form).serializeArray();
    var paramsToConvert = [];
 
    if(formParameterPresent(values, 'customerNames')) paramsToConvert.push('customerNames');
   
    formConvertDelimitedToList(values, paramsToConvert);
    var customerInfo = jQuery.param(values);
   
    var tbl;
    var request = $.ajax({ 
        url : "customers/search", 
        type : "post", 
        data : customerInfo,
        success : function(customerInfo) 
        		  { 
		        	var count = 0;
		        	for(var i=0; i<customerInfo.length; i++)
		        	{
		        		var info = customerInfo[i];
	        			tr = tbl.insertRow(-1);
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "customerName_"+i;
	        			td.className = "customerNames";
	        			td.innerHTML = info.name;
	        			td.customer = info.name;
	        				
	        			td = tr.insertCell(-1);
	        			td.id = "customerEmail_"+i;
	        			td.className = "customerEmails";
	        			td.innerHTML = info.eMail;
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "customerCode_"+i;
	        			td.className = "customerCodes";
	        			td.innerHTML = info.code;
	        			
	        			td = tr.insertCell(-1);
	        			td.className = "activeStatus";
	        			td.innerHTML = info.active ? "Active" : "Inactive";
	        			
	        			td = tr.insertCell(-1);
	        			var checkBoxCon = document.createElement("div");
	        			var input = document.createElement("input");
	        			input.type = "checkbox";
	        			input.id = "customerRecordDeleteCheckbox_"+i;
	        			input.name = "customerRecord_"+i;
	        			input.customer = info.name;
	        			checkBoxCon.appendChild(input);
	        			td.appendChild(checkBoxCon);
	        			
	        			td = tr.insertCell(-1);
	        			var buttonCon = document.createElement("div");
	        			input = document.createElement("button");
	        			input.id = "customerRecordEditButton_"+i;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-default";
	        			input.value = "Edit";
	        			input.innerHTML = "Edit";
	        			input.customer = info.name;
	        			input.ordinal = i;
	        			input.addEventListener('click', editButtonListener);
	        			buttonCon.appendChild(input);
	        			td.appendChild(buttonCon);
	        			
	        			td = tr.insertCell(-1);
	        			var buttonCon = document.createElement("div");
	        			input = document.createElement("button");
	        			input.id = "customerRecordManageButton_"+i;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-primary";
	        			input.value = "Manage Activation";
	        			input.innerHTML = "Manage Activation";
	        			input.customer = info.name;
	        			input.ordinal = i;
	        			input.addEventListener('click', manageButtonListener);
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
   
    var parent = document.getElementById('customerTable');
    var old = document.getElementById('customerTbl');
    parent.removeChild(old);
    parent.appendChild(tbl);
}

function createActivationTable(data, empty) {
	
    tbl  = document.createElement('table');
    //tbl.className = "geopolis-admin-widget geopolis-admin-widget-content";
    
    tbl.id = 'activationTbl';
    tbl.className = "table table-striped table-hover";
    
    var tr = tbl.insertRow(-1);
    var td;
//    var th = document.createElement("th");
//    th.colSpan = "3";
//    th.style.textAlign = "center";
//    th.innerHTML = "Results";
//    tr.appendChild(th);
    
    tr = tbl.insertRow(-1);
    var th = document.createElement("th");
    th.innerHTML = "Start Date";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "End Date";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Customer";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Shape";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Status";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Configuration";
    tr.appendChild(th);
    
    th = document.createElement("th");
    
    button = document.createElement("button");
    button.className = "btn btn-default";
    button.type = "button";
    button.value = "Delete";
    button.innerHTML = "Delete";
    button.id = "DeleteActivationButton";
    button.addEventListener('click', deleteActivations);
    th.appendChild(button);
    //th.className = "geopolis-admin-widget-header";
    tr.appendChild(th);
    
    th = document.createElement("th");
    tr.appendChild(th);
    
    if(isPresent(empty) && empty == true)
    {
    	var parent = document.getElementById('activationTable');
        var old = document.getElementById('activationTbl');
        parent.removeChild(old);
        parent.appendChild(tbl);
    	return tbl;
    }
    
    var req = "customer=" + $('#activationTable')[0].customer; 
    var tbl;
    var request = $.ajax({ 
        url : "customers/retrieveActivation", 
        type : "post", 
        data : req,
        success : function(activationInfo) 
        		  { 
		        	var count = 0;
		        	for(var i=0; i<activationInfo.length; i++)
		        	{
		        		var info = activationInfo[i];
	        			tr = tbl.insertRow(-1);
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "activationStart_"+i;
	        			td.className = "activationStarts";
	        			td.innerHTML = timestampToDateString(info.startDate);
	        				
	        			td = tr.insertCell(-1);
	        			td.id = "activationEnd_"+i;
	        			td.className = "activationEnds";
	        			td.innerHTML = timestampToDateString(info.endDate);
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "activationCustomer_"+i;
	        			td.className = "activationCustomers";
	        			td.innerHTML = info.customer;
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "activationShape_"+i;
	        			td.className = "activationShapes";
	        			td.innerHTML = info.shape;
	        			
	        			td = tr.insertCell(-1);
	        			td.className = "activeStatus";
	        			td.innerHTML = info.active ? "Active" : "Inactive";
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "activationConfiguration_"+i;
	        			td.className = "activationConfigurations";
	        			td.innerHTML = info.activationConfig;
	        			
	        			td = tr.insertCell(-1);
	        			var checkBoxCon = document.createElement("div");
	        			var input = document.createElement("input");
	        			input.type = "checkbox";
	        			input.id = "activationRecordDeleteCheckbox_"+i;
	        			input.name = "activationRecord_"+i;
	        			input.activation = info.id;
	        			checkBoxCon.appendChild(input);
	        			td.appendChild(checkBoxCon);
	        			
	        			td = tr.insertCell(-1);
	        			var buttonCon = document.createElement("div");
	        			input = document.createElement("button");
	        			input.id = "activationRecordEditButton_"+i;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-default";
	        			input.value = "Edit";
	        			input.innerHTML = "Edit";
	        			input.activation = info.id;
	        			input.ordinal = i;
	        			input.addEventListener('click', editActivationButtonListener);
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
   
    var parent = document.getElementById('activationTable');
    var old = document.getElementById('activationTbl');
    parent.removeChild(old);
    parent.appendChild(tbl);
}

function createEditForm()
{
	var customer = {
		name : "Name",
		eMail : "E-Mail",
		code : "Code",
	}
	
	var gridLayout = [['name',  'eMail'        ],
					  ['code'                  ]
					 ];
	
	var input = document.getElementById('cancelButton');
	input.addEventListener('click', function(ev)
								    {
									  unsplitResults();
									  enableActionControls();
								    }
						  );
	
	input = document.getElementById('saveButton');
	input.addEventListener('click', function(ev)
			   {
				 var btnVal = $('#saveButton').val();
				 if(btnVal == 'Add') updateCustomer(ev, $('#editFormObj')[0], true);
				 else if(btnVal == 'Save')
				 {
					 unsplitResults();
					 updateCustomer(ev, $('#editFormObj')[0], false);
					 enableActionControls();
				 }
				 else alert("Unrecognized Action!");
			   }
	 );
}

function createEditActivationForm()
{
	var activation = {
		start : "Start date",
		end : "End date",
		config : "Configuration",
		shape : "Shape"
	}
	
	var gridLayout = [['start',    'end'    ],
					  ['shape',    'config' ],
					 ];
					  
	var input = document.getElementById("editActivationFormTextBoxstart");
	input.addEventListener('click', function(ev)
			{
				$(ev.target).datepicker({format : "dd/mm/yyyy"})
					.on('show', function(ev) { $(ev.target).maxZIndex(); });
				$(ev.target).datepicker('show');
			});
	
	input = document.getElementById("editActivationFormTextBoxend");
	input.addEventListener('click', function(ev)
			{
				$(ev.target).datepicker({format : "dd/mm/yyyy"})
					.on('show', function(ev) { $(ev.target).maxZIndex(); });
				$(ev.target).datepicker('show');
			});
	
	input = document.getElementById('editActivationFormCancelButton');
	input.addEventListener('click', function(ev)
								    {
									  unsplitActivationResults();
									  enableActionControls();
								    }
						  );
	
	input = document.getElementById('editActivationFormSaveButton');
	input.addEventListener('click', function(ev)
								    {
										unsplitActivationResults();
										updateActivation(ev, $('#editActivationFormObj')[0], false, [{func: createActivationTable, args:[null, false]}]);
										enableActionControls();
								    });
}

function createModalAddForm()
{
	var customer = {
			name : "Name",
			eMail : "E-Mail",
			code : "Code",
		}
		
//	var input = document.getElementById('addFormTextBoxexpirationDate');
//			
//	input.addEventListener('click', function(ev)
//									{
//										$(ev.target).datepicker({format : "dd/mm/yyyy"})
//											.on('show', function(ev) { //$(ev.target).css('z-index','999999');
//											$('.datepicker').maxZIndex(); 
//											});
//										$(ev.target).datepicker('show');
//									});

		
	var input = document.getElementById('addFormCancelButton');
	input.addEventListener('click', function(ev)
			   {
				 $('#addCustomerModal').modal('hide');
			   }
	 );
	
	input = document.getElementById('addFormSaveButton');
	input.addEventListener('click', function(ev)
			   {
				  if(document.getElementById('customerTable').split == true) unsplitResults();
				  updateCustomer(ev, $('#addFormObj')[0], true);
				    $('#addForm').find("input[type=text] , textarea ").each(function(){
				                $(this).val('');            
				    });
				  $('#addCustomerModal').modal('hide');
			   }
	 );
}

function createModalAddActivationForm()
{
	var activation = {
			start : "Start date",
			end : "End date",
			customer : "Customer",
			config : "Configuration",
			shape : "Shape"
		}
		
		var gridLayout = [['start',    'end'    ],
						  ['customer', 'config' ],
						  ['shape'              ]
						 ];
		
//	var input = document.getElementById('addFormTextBoxexpirationDate');
//			
//	input.addEventListener('click', function(ev)
//									{
//										$(ev.target).datepicker({format : "dd/mm/yyyy"})
//											.on('show', function(ev) { //$(ev.target).css('z-index','999999');
//											$('.datepicker').maxZIndex(); 
//											});
//										$(ev.target).datepicker('show');
//									});

	var input = document.getElementById("addActivationFormTextBoxstart");
	input.addEventListener('click', function(ev)
			{
				$(ev.target).datepicker({format : "dd/mm/yyyy"})
					.on('show', function(ev) { $(ev.target).maxZIndex(); });
				$(ev.target).datepicker('show');
			});
	
	input = document.getElementById("addActivationFormTextBoxend");
	input.addEventListener('click', function(ev)
			{
				$(ev.target).datepicker({format : "dd/mm/yyyy"})
					.on('show', function(ev) { $(ev.target).maxZIndex(); });
				$(ev.target).datepicker('show');
			});
	
	input = document.getElementById('addActivationFormCancelButton');
	input.addEventListener('click', function(ev)
			   {
				 $('#addActivationModal').modal('hide');
			   }
	 );
	
	input = document.getElementById('addActivationFormSaveButton');
	input.addEventListener('click', function(ev)
			   {
				  if(document.getElementById('activationTable').split == true) unsplitActivationResults();
				  var postProcessingFuncs = [];
				  if($('#activityTable').is(":visible")) postProcessingFuncs.push({func: createActivationTable, args: [null, false]});
				  updateActivation(ev, $('#addActivationFormObj')[0], true, postProcessingFuncs);
				    $('#addActivationForm').find("input[type=text] , textarea ").each(function(){
				                $(this).val('');            
				    });
				  $('#addActivationModal').modal('hide');
			   }
	 );
	
	requestCustomerInfo([{func: populateCustomerList, args: ["__funcRet",$('#addActivationFormListBoxcustomer')[0]]}]);
}

function showEditBetweenResults(ev)
{
	var pos = ev.target.ordinal+2; //+2 for table header and empty heading row (TODO why empty heading?)
	
	var tableCon = $('#customerTable')[0];
	var table = $('#customerTbl')[0];
	var editCon = $('#editForm')[0];
	var mainScreen = $('.mainScreen')[0];
	
	
	trs = table.getElementsByTagName("tr");
	var removedTrs = [];
	var originalLength = trs.length;
	
	var splitTbl = document.createElement("table");
	splitTbl.id = 'splitCustomerTbl';
	splitTbl.className = 'table table-striped table-hover';
	
	var info = [];
	for(var i=pos; i<originalLength; i++)
	{
		var cl = trs[pos].cloneNode(true);
		var editBtn = document.getElementById("customerRecordEditButton_"+(i-2));
		
		if(i>pos)
		{
			removedTrs.push(cl);
			info[i-pos-1] = {};
			info[i-pos-1].customer = editBtn.customer;
			info[i-pos-1].ordinal = editBtn.ordinal;
		}
		else
		{
			splitTbl.stash = cl;
			splitTbl.stash.customer = editBtn.customer;
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
		var editBtn = $(removedTrs[i]).find("#customerRecordEditButton_"+(pos-2+i+1))[0];
		editBtn.customer = info[i].customer;
		editBtn.ordinal = info[i].ordinal;
		editBtn.addEventListener('click', editButtonListener);
		
		var manageBtn = $(removedTrs[i]).find("#customerRecordManageButton_"+(pos-2+i+1))[0];
		manageBtn.customer = info[i].customer;
		manageBtn.ordinal = info[i].ordinal;
		manageBtn.addEventListener('click', manageButtonListener);
		
		var deleteCheckbox = $(removedTrs[i]).find("#customerRecordDeleteCheckbox_"+(pos-2+i+1))[0];
		deleteCheckbox.customer = info[i].customer;
	}
	
	$(editCon).show();
	
	tableCon.split = true;
	
	
}

function showActivationEditBetweenResults(ev)
{
	var pos = ev.target.ordinal+2; //+2 for table header and empty heading row (TODO why empty heading?)
	
	var tableCon = $('#activationTable')[0];
	var table = $('#activationTbl')[0];
	var editCon = $('#editActivationForm')[0];
	var mainScreen = $('.mainScreen')[0];
	
	
	trs = table.getElementsByTagName("tr");
	var removedTrs = [];
	var originalLength = trs.length;
	
	var splitTbl = document.createElement("table");
	splitTbl.id = 'splitActivationTbl';
	splitTbl.className = 'table table-striped table-hover';
	
	var info = [];
	for(var i=pos; i<originalLength; i++)
	{
		var cl = trs[pos].cloneNode(true);
		var editBtn = document.getElementById("activationRecordEditButton_"+(i-2));
		
		if(i>pos)
		{
			removedTrs.push(cl);
			info[i-pos-1] = {};
			info[i-pos-1].activation = editBtn.activation;
			info[i-pos-1].ordinal = editBtn.ordinal;
		}
		else
		{
			splitTbl.stash = cl;
			splitTbl.stash.activation = editBtn.activation;
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
		var editBtn = $(removedTrs[i]).find("#activationRecordEditButton_"+(pos-2+i+1))[0];
		editBtn.activation = info[i].activation;
		editBtn.ordinal = info[i].ordinal;
		editBtn.addEventListener('click', editActivationButtonListener);
		
		var deleteCheckbox = $(removedTrs[i]).find("#activationRecordDeleteCheckbox_"+(pos-2+i+1))[0];
		deleteCheckbox.activation = info[i].activation;
	}
	
	$(editCon).show();
	
	tableCon.split = true;
}

function unsplitResults()
{
	var tableCon = $('#customerTable')[0];
	var table = $('#customerTbl')[0];
	var splitTable = $('#splitCustomerTbl')[0];
	var editCon = $('#editForm')[0];
	var mainScreen = $('.mainScreen')[0];
	
	$(editCon).hide();
	
	var parent = table.getElementsByTagName("tr")[0].parentNode;
	
	var info = [];
	var trs = splitTable.getElementsByTagName("tr");
	var pos = table.getElementsByTagName("tr").length-2;
	var splitLen = trs.length;
	
	info[0] = {};
	info[0].customer = splitTable.stash.customer;
	info[0].ordinal = splitTable.stash.ordinal;
	parent.appendChild(splitTable.stash);
	for(var i=0; i<splitLen; i++)
	{
		var editBtn = document.getElementById("customerRecordEditButton_"+(i+pos+1));
		info[i+1] = {};
		info[i+1].customer = editBtn.customer;
		info[i+1].ordinal = editBtn.ordinal;
		parent.appendChild(trs[0]);
	}
	
	tableCon.removeChild(editCon);
	tableCon.removeChild(splitTable);
	
	mainScreen.appendChild(editCon);
	
	var len = table.getElementsByTagName("tr").length-2; //-2 for heading and empty row TODO browser incompatibility?
	for(var i=pos; i<len; i++)
	{
		var editBtn = document.getElementById("customerRecordEditButton_"+(i));
		editBtn.customer = info[i-pos].customer;
		editBtn.ordinal = info[i-pos].ordinal;
		editBtn.addEventListener('click', editButtonListener);
		
		var manageBtn = document.getElementById("customerRecordManageButton_"+(i));
		manageBtn.customer = info[i-pos].customer;
		manageBtn.ordinal = info[i-pos].ordinal;
		manageBtn.addEventListener('click', manageButtonListener);
		
		var deleteCheckbox = document.getElementById("customerRecordDeleteCheckbox_"+(i));
		deleteCheckbox.customer = info[i-pos].customer;
	}
	
	tableCon.split = false;
	
}

function unsplitActivationResults()
{
	var tableCon = $('#activationTable')[0];
	var table = $('#activationTbl')[0];
	var splitTable = $('#splitActivationTbl')[0];
	var editCon = $('#editActivationForm')[0];
	var mainScreen = $('.mainScreen')[0];
	
	$(editCon).hide();
	
	var parent = table.getElementsByTagName("tr")[0].parentNode;
	
	var info = [];
	var trs = splitTable.getElementsByTagName("tr");
	var pos = table.getElementsByTagName("tr").length-2;
	var splitLen = trs.length;
	
	info[0] = {};
	info[0].activation = splitTable.stash.activation;
	info[0].ordinal = splitTable.stash.ordinal;
	parent.appendChild(splitTable.stash);
	for(var i=0; i<splitLen; i++)
	{
		var editBtn = document.getElementById("activationRecordEditButton_"+(i+pos+1));
		info[i+1] = {};
		info[i+1].activation= editBtn.activation;
		info[i+1].ordinal = editBtn.ordinal;
		parent.appendChild(trs[0]);
	}
	
	tableCon.removeChild(editCon);
	tableCon.removeChild(splitTable);
	
	mainScreen.appendChild(editCon);
	
	var len = table.getElementsByTagName("tr").length-2; //-2 for heading and empty row TODO browser incompatibility?
	for(var i=pos; i<len; i++)
	{
		var editBtn = document.getElementById("activationRecordEditButton_"+(i));
		editBtn.activation = info[i-pos].activation;
		editBtn.ordinal = info[i-pos].ordinal;
		editBtn.addEventListener('click', editActivationButtonListener);
		
		var deleteCheckbox = document.getElementById("activationRecordDeleteCheckbox_"+(i));
		deleteCheckbox.activation = info[i-pos].activation;
	}
	
	tableCon.split = false;
	
}

function editButtonListener(ev)
{ 
	disableActionControls();
	copyToEditForm(ev);
	$("#saveButton")[0].ordinal = ev.target.ordinal;
	showEditBetweenResults(ev)
}

function editActivationButtonListener(ev)
{ 
	disableActionControls();
	copyToActivationEditForm(ev);
	$('#editActivationFormSaveButton')[0].activation = ev.target.activation;
	$("#editActivationFormSaveButton")[0].ordinal = ev.target.ordinal;
	showActivationEditBetweenResults(ev)
}

function manageButtonListener(ev)
{
	$('#customerTable').hide();
	$('#addCustomerButton').hide();
	$('#activationTable')[0].customer = ev.target.customer;
	createActivationTable(null, false);
	$('#activationTable').show();
}

function activationDoneButtonListener(ev)
{
	$('#activationTable').hide();
	createCustomerTable(null, false);
	$('#customerTable').show();
}

function copyToEditForm(ev)
{
	var ordinal = ev.target.ordinal;
	$('#textBoxname').prop('value', $('#customerName_'+(ordinal)).html());
	$('#textBoxeMail').prop('value', $('#customerEmail_'+(ordinal)).html());
	$('#textBoxcode').prop('value', $('#customerCode_'+(ordinal)).html());
}

function copyToActivationEditForm(ev)
{
	var ordinal = ev.target.ordinal;
	
	$('#editActivationFormTextBoxstart').prop('value', $('#activationStart_'+(ordinal)).html());
	$('#editActivationFormTextBoxend').prop('value', $('#activationEnd_'+(ordinal)).html());
	
	//$('#editActivationFormListBoxcustomer option').prop("selected", false);
	//$('#editActivationFormListBoxcustomer option[value="'+$("#activationCustomer"+ordinal).html()+"'").prop('selected', true);
	
	$('#editActivationFormTextBoxconfig').prop('value', $('#activationConfig_'+(ordinal)).html());
	$('#editActivationFormTextBoxshape').prop('value', $('#activationShape_'+(ordinal)).html());
}

function retrieveCustomer(ev, postProcessingFuncs)
{
	customer = ev.target.customer;
	req = "name="+name+((customer != 'None') ? ("&customer="+customer) : "");
	var request = $.ajax({ 
        url : "customers/show", 
        type : "post", 
        data : req,
        success : function(customerInfo) 
        		  { 
        			if(!isPresent(customerInfo))
        			{
        				alert("Customer " + name + " not found!");
        				return;
        			}
		        	var attrs = Object.keys(customerInfo);
		        	
		        	for(var i=0; i<attrs.length; i++)
		        	{
		        		if(attrs[i] != 'customer' && attrs[i] != 'isActive')
		        			$('#textBox'+attrs[i]).prop('value', customerInfo[attrs[i]]);
		        		//if(attrs[i] == 'customer') 
		        		//	$('#listBox'+attrs[i]+ 'option[value="'+customer+'"]').prop('selected', 'selected'); //make the current customer selected in list selector
		        		//if(attrs[i] == 'isActive' && customerInfo.isActive == true)
		        		//	$('#checkBox'+attrs[i]).prop('checked', true);
		        	    //$('#editButton').prop('value', 'Edit');
		        		//$('#editButton').html('Edit');
		        	}
		        	//var expDate = new Date(userInfo['expirationDate']);
		      
		        	//function addZero(num) { return (num >= 0 && num < 10) ? "0" + num : num + ""; }
		        	//var expDateStr = addZero(expDate.getDate()) + '/' + addZero(expDate.getMonth()+1) + "/" + expDate.getFullYear();
		        	//$('#textBoxexpirationDate').prop('value', expDateStr);
		        	applyFuncs(postProcessingFuncs, customerInfo);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
}

function updateCustomer(ev, form, create, postProcessingFuncs)
{

	var values = $(form).serializeArray();
	values = formRemoveIfValue(values, 'customer', 'None');//[{name: 'customer', value: 'None'}], [{name: 'isActive', [{value: 'on', newValue: 'true'}, {value: 'off', newValue: 'false'}]
	var originalCustomerName = $('#customerName_'+ev.target.ordinal).html();
	//var dateParts = formFindValue(values, 'expirationDate').split('/');
	// new Date(year, month [, date [, hours[, minutes[, seconds[, ms]]]]])
	  
	//values = formReplaceValue(values, 'expirationDate',  new Date(dateParts[2], dateParts[1]-1, dateParts[0]).getTime());
	//var isActive = $('#btnisActive').val() == 'Deactivate' ? true : false;
	var toPush = {};
	toPush.name = 'originalName';
	toPush.value = originalCustomerName;
	values.push(toPush);
	var customerInfo = jQuery.param(values);
	
    var request = $.ajax({ 
        url : create ? "customers/add" : "customers/update",
        type : "post", 
        data : customerInfo,
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
		        	createCustomerTable(data, false);
					
					applyFuncs(postProcessingFuncs, response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function deleteCustomers(ev, postProcessingFuncs)
{
	var len = $('#customerTbl')[0].entryCount;
	var reqObject = [];
	var req = "";
	var reqCustomer = 'customers=';
	
	for(var i=0; i<len; i++)
	{
		if($('#customerRecordDeleteCheckbox_'+i).is(':checked'))
		{
			var chkBox = $('#customerRecordDeleteCheckbox_'+i)[0];
			if(reqCustomer != 'customers=') reqCustomer += "%2C";
			req += chkBox.customer;
		}
	}
	
	var request = $.ajax({ 
        url : "customers/delete",
        type : "post", 
        data : req,
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
		        	var parent = $('#customerTable')[0];
					parent.removeChild(parent.childNodes[1]);
					parent.appendChild(createCustomerTable(data, false));
					
					applyFuncs(postProcessingFuncs, response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function updateActivation(ev, form, create, postProcessingFuncs)
{

	var values = $(form).serializeArray();
	values = formRemoveIfValue(values, 'shape', '');//[{name: 'customer', value: 'None'}], [{name: 'isActive', [{value: 'on', newValue: 'true'}, {value: 'off', newValue: 'false'}]
	values = formRemoveIfValue(values, 'config', '');
	
	var dateParts = formFindValue(values, 'startDate').split('/');
	// new Date(year, month [, date [, hours[, minutes[, seconds[, ms]]]]])
	values = formReplaceValue(values, 'startDate',  new Date(dateParts[2], dateParts[1]-1, dateParts[0], 23, 59, 59).getTime());
	dateParts = formFindValue(values, 'endDate').split('/');
	values = formReplaceValue(values, 'endDate',  new Date(dateParts[2], dateParts[1]-1, dateParts[0], 23, 59, 59).getTime());
	
	var toPush = {};
	toPush.name = 'id';
	toPush.value = ev.target.activation;
	values.push(toPush);
	
	var req = {};
	for(var i=0; i<values.length; i++)
		req[values[i].name] = values[i].value;
	req = JSON.stringify(req);
	
    var request = $.ajax({ 
        url : create ? "customers/addActivation" : "customers/updateActivation",
        type : "post", 
        data : req,
        dataType : "json",
        contentType : "application/json",
        
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
					applyFuncs(postProcessingFuncs, response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function deleteActivations(ev, postProcessingFuncs)
{
	var len = $('#activationTbl')[0].entryCount;
	var reqObject = [];
	var req = 'activations=';
	
	for(var i=0; i<len; i++)
	{
		if($('#activationRecordDeleteCheckbox_'+i).is(':checked'))
		{
			var chkBox = $('#activationRecordDeleteCheckbox_'+i)[0];
			if(req != 'activations=') req+= "%2C";
			req += chkBox.activation;
		}
	}
	
	var request = $.ajax({ 
        url : "customers/deleteActivations",
        type : "post", 
        data : req,
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
					createActivationTable(data, false);
					
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
	$('#addCustomerButton').prop('disabled', true);
	$('#addActivationButton').prop('disabled', true);
	$('#deleteButton').prop('disabled', true);
	$('#deleteActivationButton').prop('disabled', true);
	
	var len = $('#customerTbl')[0].entryCount;
	for(var i=0; i<len; i++)
	{
		$('#customerRecordEditButton_'+i).prop('disabled', true);
		$('#customerRecordActivateButton_'+i).prop('disabled', true);
		$('#customerRecordManageButton_'+i).prop('disabled', true);
		$('#customerRecordDeleteCheckbox_'+i).prop('disabled', true);
	}
	
	len = $('#activationTbl')[0].entryCount;
	for(var i=0; i<len; i++)
	{
		$('#activationRecordEditButton_'+i).prop('disabled', true);
		$('#activationRecordDeleteCheckbox_'+i).prop('disabled', true);
	}
}

function enableActionControls()
{
	$('#searchButton').prop('disabled', false);
	$('#addCustomerButton').prop('disabled', false);
	$('#addActivationButton').prop('disabled', false);
	$('#deleteButton').prop('disabled', false);
	$('#deleteActivationButton').prop('disabled', false);
	
	var len = $('#customerTbl')[0].entryCount;
	for(var i=0; i<len; i++)
	{
		$('#customerRecordEditButton_'+i).prop('disabled', false);
		$('#customerRecordActivateButton_'+i).prop('disabled', false);
		$('#customerRecordManageButton_'+i).prop('disabled', false);
		$('#customerRecordDeleteCheckbox_'+i).prop('disabled', false);
	}
	
	len = $('#activationTbl')[0].entryCount;
	for(var i=0; i<len; i++)
	{
		$('#activationRecordEditButton_'+i).prop('disabled', false);
		$('#activationRecordDeleteCheckbox_'+i).prop('disabled', false);
	}
}