function requestCustomerInfo(postProcessingFuncs)
{
	var request = $.ajax({ 
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

function requestUserInfo(postProcessingFuncs)
{
	var req = {};
	var request = $.ajax({ 
        url : "users/list", 
        type : "post",
        data : req,
        success : function(userInfo) 
        		  { 
		        	applyFuncs(postProcessingFuncs, userInfo);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
}

function requestProjectInfo(postProcessingFuncs)
{
	var req = {};
	var request = $.ajax({ 
        url : "../projects/list", 
        type : "post",
        data : req,
        dataType : "json",
        contentType : "application/json",
        
        success : function(projectInfo) 
        		  { 
		        	applyFuncs(postProcessingFuncs, projectInfo);
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
	option.text = "All Customers";
	select.appendChild(option);
	
	for(var i=0; i<customerInfo.length; i++)
	{
		var option = document.createElement("option");
		option.value = customerInfo[i].name;
		option.text = customerInfo[i].name;
		select.appendChild(option);
	}
}

function populateUserList(userInfo, select)
{
	$(select).empty();
	var option = document.createElement("option");
	option.value = "None";
	option.text = "All Users";
	select.appendChild(option);
	
	var customerSel = $('#listBoxCustomer option:selected').val();
	var cus = Object.keys(userInfo);
	
	for(var i=0; i<cus.length; i++)
	{
		
		if(customerSel != 'None' && cus[i] != customerSel) continue;
		var info = userInfo[cus[i]];
		
		for(var j=0; j<info.length; j++)
		{
			var option = document.createElement("option");
			option.value = info[j].name;
			option.text = info[j].name;
			select.appendChild(option);
		}
	}
}

function populateProjectList(projectInfo, select)
{
	$(select).empty();
	var option = document.createElement("option");
	option.value = "None";
	option.text = "All Projects";
	select.appendChild(option);
	
	var customerSel = $('#listBoxCustomer option:selected').val();
	var userSel = $('#listBoxUser option:selected').val();
	
	for(var i=0; i<projectInfo.length; i++)
	{
		
		if(customerSel != 'None' && projectInfo[i].customer != customerSel) continue;
		if(userSel != 'None' && projectInfo[i].creator != usersel) continue;
		
		var option = document.createElement("option");
		option.value = projectInfo[i].name;
		option.text = projectInfo[i].name;
		select.appendChild(option);
	}
}

function showDocumentManagement(data)
{
	enableZMaxIndex();
	
	createSearchForm(data);
	createAddButton();
	var documentsCon = document.getElementById('documentTable');
	$(documentsCon).hide();
    
    var editCon = document.getElementById("editForm");
    $(editCon).hide();
    
    createEditForm();
    createModalAddForm();
    createAdminMenu(data);
	setPrimaryMenuButton("btnDocumentManagement");
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
	var documentTable = $('#documentTable');
	if(isPresent(documentTable)) documentTable.hide();
	
	input = document.getElementById("searchButton");
	
	input.addEventListener('click', function()
			   {
				createDocumentTable(data);
				$('body').addClass('documents-open');
				 $('#documentTable').show();
			   }
	 );
	
	createDocumentTable(data, true);
	requestCustomerInfo([{func: populateCustomerList, args: ["__funcRet",$('#listBoxCustomer')[0]]}]);
	
	requestUserInfo([{func: function(userInfo) {window.userInfo = userInfo;}, args: ["__funcRet"]},
		                 {func: populateUserList, args: ["__funcRet",$('#listBoxUser')[0]]}]);
		
	requestProjectInfo([{func: function(projectInfo) {window.projectInfo = projectInfo;}, args: ["__funcRet"]},
	                 {func: populateProjectList, args: ["__funcRet",$('#listBoxProject')[0]]}]);
	
	$('#listBoxCustomer')[0].addEventListener('change', function(ev) { 
															populateUserList(window.userInfo, $('#listBoxUser')[0]);
															populateProjectList(window.projectInfo, $('#listBoxProject')[0]);
														});
	$('#listBoxUser')[0].addEventListener('change', function(ev) { populateProjectList(window.projectInfo, $('#listBoxProject')[0]);});
}

function createAddButton()
{
	var input = document.getElementById('addDocumentButton');
	input.addEventListener('click', function()
			   {
				 $('#addDocumentModal').modal();
			   }
	 );
}

function createDocumentTable(data, empty) {
	
    tbl  = document.createElement('table');
    //tbl.className = "geopolis-admin-widget geopolis-admin-widget-content";
    
    tbl.id = 'documentTbl';
    tbl.className = "table table-striped table-hover";
    
//    var tr = tbl.insertRow(-1);
    var td;
//    var th = document.createElement("th");
//    th.colSpan = "6";
//    th.className = "heading";
//    th.innerHTML = "Document Results";
//    tr.appendChild(th);
    
    var tr = tbl.insertRow(-1);
    var ths = ['Name', 'Description', 'Creation Date', 'Type', 'Size', 'Customer', 'Creator', 'Project', 'Shapes'];
    var th;
    
    for(var i=0; i<ths.length; i++)
    {
    	th = document.createElement("th");
    	th.innerHTML = ths[i];
    	tr.appendChild(th);
    }
    
    th = document.createElement("th");
    
    button = document.createElement("button");
    button.className = "btn btn-default";
    button.type = "button";
    button.value = "Delete";
    button.innerHTML = "Delete";
    button.id = "DeleteButton";
    button.addEventListener('click', deleteDocuments);
    th.appendChild(button);
    tr.appendChild(th);
    
    th = document.createElement("th");
    var dummyDiv = document.createElement("div");
    dummyDiv.style.minWidth = '100px';
    th.appendChild(dummyDiv);
    tr.appendChild(th);
    
    if(isPresent(empty) && empty == true)
    {
    	var parent = document.getElementById('documentTable');
        var old = document.getElementById('documentTbl');
        parent.removeChild(old);
        parent.appendChild(tbl);
    	return tbl;
    }
    
    var form = document.getElementById('searchForm');
    var values = $(form).serializeArray();
    var paramsToConvert = [];
    if(formParameterPresent(values, 'terms')) paramsToConvert.push('terms');
 
    formConvertDelimitedToList(values, paramsToConvert);
    formRemoveIfValue(values, 'customer', 'None');
    formRemoveIfValue(values, 'creator', 'None');
    formRemoveIfValue(values, 'project', 'None');
    formRemoveIfValue(values, 'shape', 'None');
    formRemoveIfValue(values, 'terms', '');
    
    var req = {};
    for(var i=0; i<values.length; i++)
    	req[values[i].name] = values[i].value;
    req = JSON.stringify(req);
    //var req = jQuery.param(values);
    
    var request = $.ajax({ 
        url : "../documents/search", 
        type : "post", 
        data : req,
        dataType : "json",
        contentType : "application/json",
        
        success : function(info) 
        		  { 
        			if(info.status == "TermsExceedLimit") alert("Search terms exceed limit");
					if(info.status == "Unauthorized") alert("You are not authorized to perform this operation");
					if(info.status == "Failure") alert("An error has occurred:" + info.message);
		        	info = info.response;
					var count = 0;

		        	for(var i=0; i<info.length; i++)
		        	{
	        			tr = tbl.insertRow(-1);
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "documentName_"+count;
	        			td.className = "documentNames";
	        			td.innerHTML = info[i].name;
	        				
	        			td = tr.insertCell(-1);
	        			td.id = "documentDescription_"+count;
	        			td.className = "documentDescriptions";
	        			td.innerHTML = info[i].description;
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "documentCreationDate_"+count;
	        			td.className = "documentCreationDates";
	        			td.innerHTML = timestampToDateTimeString(info[i].creationDate);
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "documentType_"+count;
	        			td.className = "documentTypes";
	        			td.innerHTML = info[i].mimeType + "/" + info[i].mimeSubType;
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "documentSize_"+count;
	        			td.className = "documentSizes";
	        			td.innerHTML = fileSize(info[i].size);
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "documentCustomer_"+count;
	        			td.className = "documentCustomers";
	        			td.innerHTML = info[i].customer;
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "documentCreator_"+count;
	        			td.className = "documentCreators";
	        			td.innerHTML = info[i].creator;
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "documentProject_"+count;
	        			td.className = "documentProjects";
	        			td.innerHTML = info[i].projectName;
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "documentShape_"+count;
	        			td.className = "documentShapes";
	        			td.shapes = info[i].shapeNames;
	        			td.innerHTML = info[i].shapeNames.length;
	        			
	        			td = tr.insertCell(-1);
	        			var checkBoxCon = document.createElement("div");
	        			var input = document.createElement("input");
	        			input.type = "checkbox";
	        			input.id = "documentRecordDeleteCheckbox_"+count;
	        			input.name = "documentRecord_"+count;
	        			input.documentId = info[i].id;
	        			checkBoxCon.appendChild(input);
	        			td.appendChild(checkBoxCon);
	        			
	        			td = tr.insertCell(-1);
	        			var buttonCon = document.createElement("div");
	        			buttonCon.className = "btn-group";
	        			var innerButtonCon = document.createElement("div");
	        			innerButtonCon.className = "row";
	        			input = document.createElement("button");
	        			input.id = "documentRecordEditButton_"+count;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-default col-md-12";
	        			input.value = "Edit";
	        			input.innerHTML = "Edit";
	        			input.documentId = info[i].id;
	        			input.ordinal = count;
	        			input.addEventListener('click', editButtonListener);
	        			innerButtonCon.appendChild(input);
	        			buttonCon.appendChild(innerButtonCon);
	        			
	        			innerButtonCon = document.createElement("div");
	        			innerButtonCon.className = "row btn-group";
	        			input = document.createElement("button");
	        			input.id = "documentRecordViewButton_"+count;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-default col-md-6";
	        			input.value = "View";
	        			input.innerHTML = "View";
	        			input.documentId = info[i].id;
	        			input.ordinal = count;
	        			input.addEventListener('click', viewButtonListener);
	        			innerButtonCon.appendChild(input);
	        			
	        			input = document.createElement("button");
	        			input.id = "documentRecordDownloadButton_"+count;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-default col-md-6";
	        			input.value = "Download";
	        			input.innerHTML = "Download";
	        			input.documentId = info[i].id;
	        			input.ordinal = count;
	        			input.addEventListener('click', downloadButtonListener);
	        			innerButtonCon.appendChild(input);
	        			buttonCon.appendChild(innerButtonCon);
	        			
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
   
      var parent = document.getElementById('documentTable');
      var old = document.getElementById('documentTbl');
      parent.removeChild(old);
      parent.appendChild(tbl);
}

function createEditForm()
{
	input = document.getElementById("cancelButton");
	input.addEventListener('click', function(ev)
			   						{
									 unsplitResults();
									 enableActionControls();
								   }
	 					  );
	
	input = document.getElementById("saveButton");
	input.addEventListener('click', function()
								   {
									 unsplitResults();
									 updateDocument($('#editFormObj')[0], false);
									 enableActionControls();
								   }
						 );
	
	
	
	input = document.getElementById("editFormfile");
	input.addEventListener('change', function()
									 {
										$('#editFormTextBoxname').val("");
										if($('#editFormfile')[0].files.length > 0)
											$('#editFormTextBoxname').val($('#editFormfile')[0].files[0].name);
									 });
}

function createModalAddForm()
{
		input = document.getElementById("addFormCancelButton");
		input.addEventListener('click', function(ev)
				   {
					 $('#addDocumentModal').modal('hide');
				   }
		 );
		
		input = document.getElementById("addFormSaveButton");
		input.addEventListener('click', function()
				   {
					  if(document.getElementById('documentTable').split == true) unsplitResults();
					  updateDocument($('#addFormObj')[0], true);
					  //clear form
					    $('#addFormObj').find("input[type=text] , textarea").each(function(){
					                $(this).val('');            
					    });
					  $('#addDocumentModal').modal('hide');
				   }
		 );
		
		input = document.getElementById("addFormfile");
		input.addEventListener('change', function()
										 {
											$('#addFormTextBoxname').val("");
											if($('#addFormfile')[0].files.length > 0)
												$('#addFormTextBoxname').val($('#addFormfile')[0].files[0].name);
										 });
		var select = document.getElementById("addFormListBoxcustomer");
		requestCustomerInfo([{func: populateCustomerList, args: ["__funcRet",select]}]);
}

function showEditBetweenResults(ev)
{
	var offset = 1; //+1 for table header TODO depends on table heading
	var pos = ev.target.ordinal+offset; 
	
	var tableCon = $('#documentTable')[0];
	var table = $('#documentTbl')[0];
	var editCon = $('#editForm')[0];
	var mainScreen = $('.mainScreen')[0];
	
	
	trs = table.getElementsByTagName("tr");
	var removedTrs = [];
	var originalLength = trs.length;
	
	var splitTbl = document.createElement("table");
	splitTbl.id = 'splitDocumentTbl';
	splitTbl.className = 'table table-striped table-hover';
	
	var info = [];
	for(var i=pos; i<originalLength; i++)
	{
		var cl = trs[pos].cloneNode(true);
		var editBtn = document.getElementById("documentRecordEditButton_"+(i-offset));
		
		if(i>pos)
		{
			removedTrs.push(cl);
			info[i-pos-1] = {};
			info[i-pos-1].documentId = editBtn.documentId;
			info[i-pos-1].ordinal = editBtn.ordinal;
		}
		else
		{
			splitTbl.stash = cl;
			splitTbl.stash.documentId = editBtn.documentId;
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
		var editBtn = $(removedTrs[i]).find("#documentRecordEditButton_"+(pos-offset+i+1))[0];
		editBtn.documentId = info[i].documentId;
		editBtn.ordinal = info[i].ordinal;
		editBtn.addEventListener('click', editButtonListener);
		
		var viewBtn = $(removedTrs[i]).find("#documentRecordViewButton_"+(pos-offset+i+1))[0];
		viewBtn.documentId = info[i].documentId;
		viewBtn.ordinal = info[i].ordinal;
		viewBtn.addEventListener('click', viewButtonListener);
		
		var downloadBtn = $(removedTrs[i]).find("#documentRecordDownloadButton_"+(pos-offset+i+1))[0];
		downloadBtn.documentId = info[i].documentId;
		downloadBtn.ordinal = info[i].ordinal;
		downloadBtn.addEventListener('click', downloadButtonListener);
		
		var deleteCheckbox = $(removedTrs[i]).find("#documentRecordDeleteCheckbox_"+(pos-offset+i+1))[0];
		deleteCheckbox.documentId = info[i].documentId;
	}
	
	$(editCon).show();
	
	tableCon.split = true;
}

function unsplitResults()
{
	var tableCon = $('#documentTable')[0];
	var table = $('#documentTbl')[0];
	var splitTable = $('#splitDocumentTbl')[0];
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
	info[0].documentId = splitTable.stash.documentId;
	parent.appendChild(splitTable.stash);
	for(var i=0; i<splitLen; i++)
	{
		var editBtn = document.getElementById("documentRecordEditButton_"+(i+pos+1));
		info[i+1] = {};
		info[i+1].documentId = editBtn.documentId;
		info[i+1].ordinal = editBtn.ordinal;
		parent.appendChild(trs[0]);
	}
	
	tableCon.removeChild(editCon);
	tableCon.removeChild(splitTable);
	
	mainScreen.appendChild(editCon);
	
	var len = table.getElementsByTagName("tr").length-offset; 
	for(var i=pos; i<len; i++)
	{
		var editBtn = document.getElementById("documentRecordEditButton_"+(i));
		editBtn.documentId = info[i-pos].documentId;
		editBtn.ordinal = info[i-pos].ordinal;
		editBtn.addEventListener('click', editButtonListener);
		
		var viewBtn = document.getElementById("documentRecordViewButton_"+(i));
		viewBtn.documentId = info[i-pos].documentId;
		viewBtn.addEventListener('click', viewButtonListener);
		
		var downloadBtn = document.getElementById("documentRecordDownloadButton_"+(i));
		downloadBtn.documentId = info[i-pos].documentId;
		downloadBtn.addEventListener('click', downloadButtonListener);
		
		var deleteCheckbox = document.getElementById("documentRecordDeleteCheckbox_"+(i));
		deleteCheckbox.documentId = info[i-pos].documentId;
	}
	
	tableCon.split = false;
	
}

function editButtonListener(ev)
{ 
	disableActionControls();
	copyToEditForm(ev);
	$('#editForm')[0].documentId = ev.target.documentId;
	showEditBetweenResults(ev);
}

function viewButtonListener(ev)
{
	 $.ajax({ 
	        url : "../documents/retrievet",
	        type : "post", 
	        data : ev.target.documentId,
	        dataType : "json",
	        contentType : "application/json",
	        success : function(response) 
	        		  {
	        			var opened;
	        			if(response.status == "NotFound") alert(defs.i18n.project.error.notFound);
	        			if(response.status == "Unauthorized") alert(defs.i18n.project.error.unauthorized);
	        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
	        			var tok = Object.keys(response.response)[0];
	        			if(response.status == "Success")
	        				window.open('../documents/retrieveg?t='+tok, response.response[tok]);
			          }
	        		  ,
	        error : function(jqXHR, textStatus, errorThrown) 
	        		 {
		   			   alert(defs.i18n.error.failure + ":" + textStatus, errorThrown);
	        		 },
	        
	        statusCode: {
				 302: function(jqXHR) {
					 	window.location.href = jqXHR.getResponseHeader("Location");
				 	 }
				 }        		 
	      }); 	
}

function downloadButtonListener(ev)
{
	var id = ev.target.documentId;
	
	var params = {};
	params.id = id;
	
	postToUrl("../documents/retrieve", params);
}

function copyToEditForm(ev)
{
	var ordinal = ev.target.ordinal;
	$('#editFormObj')[0].reset();
	$('#editFormTextBoxname').prop('value', $('#documentName_'+(ordinal)).html());
	$('#editFormTextBoxdescription').prop('value', $('#documentDescription_'+(ordinal)).html());
	$('#editFormTextBoxproject').prop('value', $('#documentProject_'+(ordinal)).html());
	var shapes = $('#documentShape_'+(ordinal))[0].shapes;
	for(var i=0; i<shapes.length; i++)
		$('#editFormListshape').append('<li>'+shapes[i]+'</li>');
	//$('#editFormTextBoxshape').prop('value', ('#documentShape_'+(ordinal))[0].shapes);
}

function updateDocument(form, create, postProcessingFuncs)
{

	var values = $(form).serializeArray();
	
	values = formRemoveIfValue(values, 'customer', 'None');//[{name: 'customer', value: 'None'}], [{name: 'isActive', [{value: 'on', newValue: 'true'}, {value: 'off', newValue: 'false'}]
	
	var formData = new FormData();
	
	var inputFile = $(form).find("input[name='file']")[0].files;
	if(inputFile.length == 1) formData.append('file', inputFile[0]);
	if(!create) formData.append('id', $('#editForm')[0].documentId);
	var name = formFindValue(values, 'name');
	var description = formFindValue(values, 'description');
	if(name != 'None') formData.append('name', name);
	formData.append('description', description);
	
    var request = $.ajax({ 
        url : create ? "../documents/add" : "../documents/update",
        type : "post", 
        data : formData,
        contentType : false,
        processData : false,
        
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
					createDocumentTable(null, false);
					applyFuncs(postProcessingFuncs, response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function deleteDocuments(ev, postProcessingFuncs)
{
	var len = $('#documentTbl')[0].entryCount;
	var reqObject = [];
	var req = "documents=";
	
	for(var i=0; i<len; i++)
	{
		if($('#documentRecordDeleteCheckbox_'+i).is(':checked'))
		{
			var chkBox = $('#documentRecordDeleteCheckbox_'+i)[0];
			if(req != 'documents=') req += "%2C";
			req += chkBox.documentId;
		}
	}
	
	var request = $.ajax({ 
        url : "../documents/delete",
        type : "post", 
        data : req,
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
		        	var parent = $('#documentTable')[0];
					parent.removeChild(parent.childNodes[1]);
					parent.appendChild(createDocumentTable(null, false));
					
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
	$('#addDocumentButton').prop('disabled', true);
	$('#deleteButton').prop('disabled', true);
	var len = $('#documentTbl')[0].entryCount;
	for(var i=0; i<len; i++)
	{
		$('#documentRecordEditButton_'+i).prop('disabled', true);
		$('#documentRecordViewButton_'+i).prop('disabled', true);
		$('#documentRecordDownloadButton_'+i).prop('disabled', true);
		$('#documentRecordDeleteCheckbox_'+i).prop('disabled', true);
	}
}

function enableActionControls()
{
	$('#searchButton').prop('disabled', false);
	$('#addDocumentButton').prop('disabled', false);
	$('#deleteButton').prop('disabled', false);
	var len = $('#documentTbl')[0].entryCount;
	for(var i=0; i<len; i++)
	{
		$('#documentRecordEditButton_'+i).prop('disabled', false);
		$('#documentRecordViewButton_'+i).prop('disabled', false);
		$('#documentRecordDownloadButton_'+i).prop('disabled', false);
		$('#documentRecordDeleteCheckbox_'+i).prop('disabled', false);
	}
}