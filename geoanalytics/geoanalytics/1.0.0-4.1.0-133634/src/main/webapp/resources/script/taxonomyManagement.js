function showTaxonomyManagement(data)
{
	enableZMaxIndex();
	
	createSearchForm(data);
	createAddTaxonomyButton();
	createAddTermButton();
	createTaxonomyConfigButton();
	
	$('#taxonomyTable').hide();
	$('#termTable').hide();
	
	$('#taxonomyConfig').hide();
    $('#editTaxonomyForm').hide();
    $('#editTermForm').hide();
    
    createTaxonomyEditForm();
    createTaxonomyModalAddForm();
    createTaxonomyConfigForm([{func: populateTaxonomySelectors, args: []},
                              {func: function()
    								 {
                            	  		$('#taxonomyConfigForm select').click(function(ev)
                            	  												{
                            	  													$(ev.target).find('option[value="None"]').text('None');
                            	  													$(ev.target).off('click');
                            	  												});
    								 }, args: []}
    						  ]);
    
    createTermEditForm();
    createTermModalAddForm();
    
    createAdminMenu(data);
    setPrimaryMenuButton("btnTaxonomyManagement");
    
    enableActionControls();
}

function createSearchForm(data, id)
{
	var taxonomyTable = $('#taxonomyTable');
	if(isPresent(taxonomyTable)) taxonomyTable.hide();
	
	input = document.getElementById("searchButton");
	
	input.addEventListener('click', function()
			   {
				$('#termTable').hide();
				$('#addTaxonomyButton').prop('disabled', false);
				$('#taxonomyConfigButton').prop('disabled', false);
				createTaxonomyTable(data);
				 $('#taxonomyTable').show();
			   }
	 );
	
	createTaxonomyTable(data, true);
	
}

function createAddTaxonomyButton()
{
	var input = document.getElementById('addTaxonomyButton');
	input.addEventListener('click', function()
			   {
				 $('#addTaxonomyModal').modal();
			   }
	 );
}

function createAddTermButton()
{
	var input = document.getElementById('addTermButton');
	input.addEventListener('click', function()
			   {
				 $('#addTermModal').modal();
			   }
	 );
}

function createTaxonomyConfigButton()
{
	var input = document.getElementById('taxonomyConfigButton');
	input.addEventListener('click', taxonomyConfigButtonListener);
}

function createTaxonomyTable(data, empty) {
	
    tbl  = document.createElement('table');
    //tbl.className = "geopolis-admin-widget geopolis-admin-widget-content";
    
    tbl.id = 'taxonomyTbl';
    tbl.className = "table table-striped table-hover";
    
    var tr = tbl.insertRow(-1);
    var td;
//    var th = document.createElement("th");
//    th.colSpan = "6";
//    th.className = "heading";
//    th.innerHTML = "User Results";
//    tr.appendChild(th);
    
    tr = tbl.insertRow(-1);
    var th = document.createElement("th");
    th.innerHTML = "Name";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Class";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "User Taxonomy";
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
    button.id = "taxonomyDeleteButton";
    button.addEventListener('click', function(ev)
    								 {
    									deleteTaxonomies(ev, [{func: populateTaxonomySelectors, args: []}]);
    								 });
    th.appendChild(button);
    tr.appendChild(th);
    
    th = document.createElement("th");
    tr.appendChild(th);
    th = document.createElement("th");
    tr.appendChild(th);
    th = document.createElement("th");
    tr.appendChild(th);
    
    var parent = document.getElementById('taxonomyTable');
    var old = document.getElementById('taxonomyTbl');
    parent.removeChild(old);
    parent.appendChild(tbl);
    
    if(isPresent(empty) && empty == true)
    	return tbl;
    
    var form = document.getElementById('searchForm');
    var values = $(form).serializeArray();
    var paramsToConvert = [];
    if(formParameterPresent(values, 'termNames')) paramsToConvert.push('termNames');
    formConvertDelimitedToList(values, paramsToConvert);
    
    formRemoveIfValue(values, 'taxonomyNames', 'None');
    
    var taxonomyInfo = jQuery.param(values);
    
    $.ajax({ 
        url : "taxonomies/search", 
        type : "post", 
        data : taxonomyInfo,
        success : function(info) 
        		  { 
		        	var count = 0;
	
	        		for(var i=0; i<info.length; i++)
	        		{
	        			tr = tbl.insertRow(-1);
	        			if(isPresent(info[i].extraData))
	        				tr.extraData = info[i].extraData;
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "taxonomyName_"+count;
	        			td.className = "taxonomyNames";
	        			td.innerHTML = info[i].name;
	        			td.taxonomy = info[i].name;
	        				
	        			td = tr.insertCell(-1);
	        			td.id = "taxonomyClass_"+count;
	        			td.className = "taxonomyClasses";
	        			td.innerHTML = info[i].taxonomyClass;
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "taxonomyIsUserTaxonomy_"+count;
	        			td.className = "taxonomyIsUserTaxonomy";
	        			td.innerHTML = info[i].userTaxonomy == true ? 'Yes' :
	        						   info[i].userTaxonomy == false ? 'No' :
	        						   "Error";
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "taxonomyIsActive_"+count;
	        			td.className = "activeStatus";
	        			td.innerHTML = info[i].active ? "Active" : "Inactive";
	        			
	        			td = tr.insertCell(-1);
	        			var checkBoxCon = document.createElement("div");
	        			var input = document.createElement("input");
	        			input.type = "checkbox";
	        			input.id = "taxonomyRecordDeleteCheckbox_"+count;
	        			input.name = "taxonomyRecord_"+count;
	        			input.taxonomy = info[i].name;
	        			checkBoxCon.appendChild(input);
	        			td.appendChild(checkBoxCon);
	        			
	        			td = tr.insertCell(-1);
	        			var buttonCon = document.createElement("div");
	        			input = document.createElement("button");
	        			input.id = "taxonomyRecordEditButton_"+count;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-default";
	        			input.value = "Edit";
	        			input.innerHTML = "Edit";
	        			input.taxonomy = info[i].name;
	        			input.ordinal = count;
	        			input.addEventListener('click', editButtonListener);
	        			buttonCon.appendChild(input);
	        			td.appendChild(buttonCon);
	        			
	        			td = tr.insertCell(-1);
	        			buttonCon = document.createElement("div");
	        			input = document.createElement("button");
	        			input.id = "taxonomyRecordActivateButton_"+count;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-default";
	        			input.value = info[i].active ? "Deactivate" : "Activate";
	        			input.innerHTML = info[i].active ? "Deactivate" : "Activate";
	        			input.taxonomy = info[i].name;
	        			input.ordinal = count;
	        			input.addEventListener('click', taxonomyActivateButtonToggleListener);
	        			buttonCon.appendChild(input);
	        			td.appendChild(buttonCon);
	        			
	        			td = tr.insertCell(-1);
	        			buttonCon = document.createElement("div");
	        			input = document.createElement("button");
	        			input.id = "taxonomyRecordManageButton_"+count;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-primary";
	        			input.value = "Manage Terms"
	        			input.innerHTML = "Manage Terms"
	        			input.taxonomy = info[i].name;
	        			input.ordinal = count;
	        			input.addEventListener('click', taxonomyManageTermsButtonListener);
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
}

function createTermTable(data, empty) {
	
    var tbl  = document.createElement('table');
    //tbl.className = "geopolis-admin-widget geopolis-admin-widget-content";
    
    tbl.id = 'termTbl';
    tbl.className = "table table-striped table-hover";
    
    var tr = tbl.insertRow(-1);
    var td;
//    var th = document.createElement("th");
//    th.colSpan = "6";
//    th.className = "heading";
//    th.innerHTML = "User Results";
//    tr.appendChild(th);
    
    tr = tbl.insertRow(-1);
    var th = document.createElement("th");
    th.innerHTML = "Name";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Taxonomy";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Class";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Parent";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Order";
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
    button.id = "termDeleteButton";
    button.addEventListener('click', function(ev)
    								 {
    									deleteTerms(ev, []);
    								 });
    th.appendChild(button);
    tr.appendChild(th);
    
    th = document.createElement("th");
    tr.appendChild(th);
    th = document.createElement("th");
    tr.appendChild(th);
    th = document.createElement("th");
    tr.appendChild(th);
    th = document.createElement("th");
    tr.appendChild(th);
    
    var parent = document.getElementById('termTable');
    var old = document.getElementById('termTbl');
    parent.removeChild(old);
    parent.appendChild(tbl);
    
    if(isPresent(empty) && empty == true)
    	return tbl;
    
    //var form = document.getElementById('searchForm');
    //var values = $(form).serializeArray();
    //var paramsToConvert = [];
    //if(formParameterPresent(values, 'termNames')) paramsToConvert.push('termNames');
    //formConvertDelimitedToList(values, paramsToConvert);
    
    //formRemoveIfValue(values, 'taxonomyNames', 'None');
    var reqTaxonomy = $('#termTable')[0].taxonomy;
    
    var url = "taxonomies/retrieveTerms";
    var req = "taxonomy="+reqTaxonomy;
    
    $('#termTableHeading').html("Terms of " + reqTaxonomy);
   
    var reqTerm;
    if(isPresent($('#termTable')[0].term))
    {
    	reqTerm = $('#termTable')[0].term;
    	req+='&term=' + reqTerm;
    	if($('#termTable')[0].descendants == true)
    	{
    		$('#termTableHeading > p').html("Descendants of " + reqTaxonomy + ":" + reqTerm);
    		url = "/taxonomies/retrieveTermDescendants";
    	}
    	if($('#termTable')[0].classDescendants == true) 
    	{
    		$('#termTableHeading > p').html("Class descendants of " + reqTaxonomy + ":" + reqTerm);
    		url = "/taxonomies/retrieveTermClassDescendants";
    	}
    }
    
    $.ajax({ 
        url : url,
        type : "post", 
        data : req,
        success : function(info) 
        		  { 
		        	var count = 0;
	
	        		for(var i=0; i<info.length; i++)
	        		{
	        			tr = tbl.insertRow(-1);
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "termName_"+count;
	        			td.className = "termNames";
	        			td.innerHTML = info[i].name;
	        			td.taxonomy = info[i].name;
	        				
	        			td = tr.insertCell(-1);
	        			td.id = "termTaxonomy_"+count;
	        			td.className = "termTaxonomy";
	        			td.innerHTML = info[i].taxonomy;
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "termClass_"+count;
	        			td.className = "termClasses";
	        			if(isPresent(info[i].classTaxonomy) && isPresent(info[i]).classTerm)
	        				td.innerHTML = info[i].classTaxonomy + ":" + info[i].classTerm;
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "termParent_"+count;
	        			td.className = "termParents";
	        			if(isPresent(info[i].parentTaxonomy) && isPresent(info[i].parentTerm))
	        				td.innerHTML = info[i].parentTaxonomy + ":" + info[i].parentTerm;
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "termOrder_"+count;
	        			td.className = "termOrders";
	        			td.innerHTML = info[i].order;
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "termIsActive_"+count;
	        			td.className = "activeStatus";
	        			td.innerHTML = info[i].active ? "Active" : "Inactive";
	        			
	        			td = tr.insertCell(-1);
	        			var checkBoxCon = document.createElement("div");
	        			var input = document.createElement("input");
	        			input.type = "checkbox";
	        			input.id = "termRecordDeleteCheckbox_"+count;
	        			input.name = "termRecord_"+count;
	        			input.taxonomy = info[i].taxonomy;
	        			input.term = info[i].name;
	        			checkBoxCon.appendChild(input);
	        			td.appendChild(checkBoxCon);
	        			
	        			td = tr.insertCell(-1);
	        			var buttonCon = document.createElement("div");
	        			input = document.createElement("button");
	        			input.id = "termRecordEditButton_"+count;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-default";
	        			input.value = "Edit";
	        			input.innerHTML = "Edit";
	        			input.taxonomy = info[i].taxonomy;
	        			input.term = info[i].name;
	        			input.ordinal = count;
	        			input.addEventListener('click', termEditButtonListener);
	        			buttonCon.appendChild(input);
	        			td.appendChild(buttonCon);
	        			
	        			td = tr.insertCell(-1);
	        			buttonCon = document.createElement("div");
	        			input = document.createElement("button");
	        			input.id = "termRecordActivateButton_"+count;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-default";
	        			input.value = info[i].active ? "Deactivate" : "Activate";
	        			input.innerHTML = info[i].active ? "Deactivate" : "Activate";
	        			input.taxonomy = info[i].taxonomy;
	        			input.term = info[i].name;
	        			input.ordinal = count;
	        			input.addEventListener('click', function termActivateButtonToggleListener(){});
	        			buttonCon.appendChild(input);
	        			td.appendChild(buttonCon);
	        			
	        			td = tr.insertCell(-1);
	        			buttonCon = document.createElement("div");
	        			input = document.createElement("button");
	        			input.id = "termRecordDescendantsButton_"+count;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-primary";
	        			input.value = "Descendants"
	        			input.innerHTML = "Descendants"
	        			input.taxonomy = info[i].taxonomy;
	        			input.term = info[i].name;
	        			input.ordinal = count;
	        			input.addEventListener('click', function termsDescendantsButtonListener() {}); //TODO
	        			buttonCon.appendChild(input);
	        			td.appendChild(buttonCon);
	        			
	        			td = tr.insertCell(-1);
	        			buttonCon = document.createElement("div");
	        			input = document.createElement("button");
	        			input.id = "termRecordClassDescendantsButton_"+count;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-primary";
	        			input.value = "Class Descendants"
	        			input.innerHTML = "Class Descendants"
	        			input.taxonomy = info[i].taxonomy;
	        			input.term = info[i].name;
	        			input.ordinal = count;
	        			input.addEventListener('click', function termsClassDescendantsButtonListener(){}); //TODO
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
}

function createTaxonomyEditForm()
{
	input = document.getElementById('editTaxonFormBtnisActive');
	input.addEventListener('click', toggleButton);
	
	input = document.getElementById("editTaxonFormCancelButton");
	input.addEventListener('click', function(ev)
			   						{
									 unsplitResults();
									 enableActionControls();
								   }
	 					  );
	
	input = document.getElementById("editTaxonFormSaveButton");
	input.addEventListener('click', function(ev)
								   {
									 unsplitResults();
									 updateTaxonomy(ev, $('#editTaxonFormObj')[0], false);
									 enableActionControls();
								   }
						 );
}

function createTaxonomyConfigForm(postProcessingFuncs)
{
	$.ajax({ 
        url : 'taxonomies/listTaxonomyConfig',
        type : "post", 
        success : function(taxonomyConfig) 
        		  { 
        			var form = $('#taxonomyConfigForm')[0];
        			var formGroup;
        			for(var i=0; i<taxonomyConfig.length; i++)
        			{
        				
        				if(i%2 == 0)
        				{
        					formGroup = document.createElement("div");
        					formGroup.className="form-group";
        				}
        				var label = document.createElement("label");
        				label.className = "taxonomyConfigFormElement col-md-3 control-label";
        				label.htmlFor = "taxonomyConfigForm_"+i;
        				label.innerHTML = taxonomyConfig[i];
        				formGroup.appendChild(label);
        				
        				var inputCon = document.createElement("div");
        				inputCon.className = "taxonomyConfigFormElement col-md-3";
        				var select = document.createElement("select");
        				select.id = "taxonomyConfigForm_"+i;
        				select.className = "taxonomyConfigFormElement form-control";
        				select.name = taxonomyConfig[i];
        				inputCon.appendChild(select);
        				formGroup.appendChild(inputCon);
        				
        				if(i%2 == 0 || i == length -1) 
        					form.insertBefore(formGroup, $('#taxonomyConfigFormFooter')[0]);
        			}
        			
        			applyFuncs(postProcessingFuncs, null);
        		  },
	
	    error : function(jqXHR, textStatus, errorThrown) 
				{
					 alert("The following error occured: " + textStatus, errorThrown);
			    }
		}); 
	
	var button = $('#taxonomyConfigFormSaveButton')[0];
	button.addEventListener('click', taxonomyConfigSaveButtonListener);
	
	button = $('#taxonomyConfigFormCancelButton')[0];
	button.addEventListener('click', taxonomyConfigCancelButtonListener);
		       	
}

function retrieveTaxonomyConfig(postProcessingFuncs)
{
	$.ajax({ 
        url : 'taxonomies/retrieveTaxonomyConfig',
        type : "post", 
        success : function(taxonomyConfig) 
        		  { 
        			applyFuncs(postProcessingFuncs, taxonomyConfig);
        		  },
        
        error : function(jqXHR, textStatus, errorThrown) 
  				{
  					 alert("The following error occured: " + textStatus, errorThrown);
  			    }
  		});
}

function createTermEditForm()
{
	input = document.getElementById('editTermFormBtnisActive');
	input.addEventListener('click', toggleButton);
	
	input = document.getElementById("editTermFormListBoxclassTaxonomy");
	input.addEventListener('change', populateTerms);
	
	input = document.getElementById("editTermFormListBoxparentTaxonomy");
	input.addEventListener('change', populateTerms);
	
	input.addEventListener('click', function(ev)
			   {
				 $('#addTermModal').modal('hide');
			   }
	 );

	input = document.getElementById("editTermFormCancelButton");
	input.addEventListener('click', function(ev)
			   {
			      unsplitTermResults();
				  enableActionControls();
			   }
		 );
	
	input = document.getElementById("editTermFormSaveButton");
	input.addEventListener('click', function(ev)
			   {
			      unsplitTermResults();
				  updateTerm(ev, $('#editTermFormObj')[0], false);
				  enableActionControls();
			   }
		 );
}

function populateTaxonomySelectors()
{
	listTaxonomies([{func: populateSelector, args:[$('#listBoxTaxonomies')[0],"__funcRet", "None", "All taxonomies", false]},
	                
	                {func: populateSelector, args:[$('#editTaxonFormListBoxtaxonomyClass')[0],"__funcRet", "None", "--None--", false]},
	                
	                {func: populateSelector, args:[$('#editTermFormListBoxtaxonomy')[0],"__funcRet", "None", "Select a taxonomy", true]},
	                {func: populateSelector, args:[$('#editTermFormListBoxclassTaxonomy')[0],"__funcRet", "None", "--None--", false]},
	                {func: populateSelector, args:[$('#editTermFormListBoxparentTaxonomy')[0],"__funcRet", "None", "--None--", false]},
	                
	                {func: populateSelector, args:[$('#addTaxonFormListBoxtaxonomyClass')[0],"__funcRet", "None", "--None--", false]},
	                
	                {func: populateSelector, args:[$('#addTermFormListBoxtaxonomy')[0],"__funcRet", "None", "Select a taxonomy", true]},
	                {func: populateSelector, args:[$('#addTermFormListBoxclassTaxonomy')[0],"__funcRet", "None", "--None--", false]},
	                {func: populateSelector, args:[$('#addTermFormListBoxparentTaxonomy')[0],"__funcRet", "None", "--None--", false]},
	                {func: populateTaxonomyConfigSelectors, args:["__funcRet"]}],
	                true);
}

function populateTaxonomyConfigSelectors(taxonomies)
{
	var doPopulate =  function(taxonomies)
					  {
						var selectors = $('#taxonomyConfigForm select');
						for(var i=0; i<selectors.length; i++)
							populateSelector(selectors[i], taxonomies, 'None', "Select a taxonomy", false);
						retrieveTaxonomyConfig([{func: selectByCurrentTaxonomyConfig, args:["__funcRet"]}]);
					  } 
	if(isPresent(taxonomies))
	{
		doPopulate(taxonomies);
	}else
	{
		listTaxonomies([{func: doPopulate,
						 args: ["__funcRet"]
						},
						{func: retrieveTaxonomyConfig,
						 args: [[{func: selectByCurrentTaxonomyConfig, args:["__funcRet"]}]]
						}],
			            true);
	}
	
}

function selectByCurrentTaxonomyConfig(taxonomyConfig)
{
	var selects = $('#taxonomyConfigForm select');
	for(var i=0; i<taxonomyConfig.length; i++)
	{
		for(var s=0; s<selects.length; s++)
		{
			if(selects[s].name == taxonomyConfig[i].type)
				$(selects[s]).find('option[value="'+taxonomyConfig[i].id+'"]').prop('selected', true);
		}
	}
}

function populateTerms(ev)
{
	var targetId;
	if(ev.target.id == 'editTermFormListBoxclassTaxonomy') targetId = 'editTermFormListBoxclassTerm';
	else if(ev.target.id == 'editTermFormListBoxparentTaxonomy') targetId = 'editTermFormListBoxparentTerm';
	else if(ev.target.id == 'addTermFormListBoxclassTaxonomy') targetId = 'addTermFormListBoxclassTerm';
	else if(ev.target.id == 'addTermFormListBoxparentTaxonomy') targetId = 'addTermFormListBoxparentTerm';
	
	$('#'+targetId).empty();
	var taxonomy = $('#'+ev.target.id+' option:selected').val(); //get selected taxonomy from dropdown
	var select = document.getElementById(targetId);
	option = document.createElement("option");
	option.value = "None";
	option.text = taxonomy != 'None' ? "Select a term" : '--None--';
	option.selected = true;
	option.disabled = true;
	select.appendChild(option);
	
	if(taxonomy == 'None') return;
	
	var req = 'taxonomy='+taxonomy;
	 $.ajax({ 
        url : "taxonomies/listTerms",
        type : "post", 
        data : req,
        success : function(terms) 
        		  {
		        	for(var i=0; i<terms.length; i++)
		        	{
		        		var option = document.createElement("option");
		        		option.value = terms[i];
		        		option.text = terms[i];
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

function toggleButton(ev)
{  if(ev.target.value == 'Activate')
   {
	ev.target.value = 'Deactivate';
	ev.target.innerHTML = 'Deactivate';
   }else if(ev.target.value == "Deactivate")
   {
	ev.target.value = 'Activate';
	ev.target.innerHTML = 'Activate';
   }  
}

function listTaxonomies(postProcessingFuncs, active)
{
	$.ajax({
        url : "taxonomies/listTaxonomies",
        type : "post", 
        data : active,
        
        success : function(taxons) {
        	applyFuncs(postProcessingFuncs, taxons);
        },
        
        error : function(jqXHR, textStatus, errorThrown) 
		 {
			alert("The following error occured: " + textStatus, errorThrown);
		 }
	});
}

function createTaxonomyModalAddForm()
{
	input = document.getElementById("addTaxonFormCancelButton");
	input.addEventListener('click', function(ev)
			   {
				 $('#addTaxonomyModal').modal('hide');
			   }
	 );
	
	input = document.getElementById("addTaxonFormSaveButton");
	input.addEventListener('click', function(ev)
			   {
				  if(document.getElementById('taxonomyTable').split == true) unsplitResults();
				  updateTaxonomy(ev, $('#addTaxonFormObj')[0], true, [{func: populateTaxonomySelectors}]);
				  //clear form
				    $('#addTaxonFormObj').find("input[type=text] , textarea").each(function(){
				                $(this).val('');            
				    });
				  $('#addTaxonomyModal').modal('hide');
				   }
		 );
}

function createTermModalAddForm()
{

	input = document.getElementById("addTermFormCancelButton");
	input.addEventListener('click', function(ev)
			   {
				 $('#addTermModal').modal('hide');
			   }
	 );
	
	input = document.getElementById("addTermFormSaveButton");
	input.addEventListener('click', function(ev)
			   {
				  if(document.getElementById('taxonomyTable').split == true) unsplitResults();
				  updateTerm(ev, $('#addTermFormObj')[0], true);
				  //clear form
				    $('#addTermFormObj').find("input[type=text] , textarea").each(function(){
				                $(this).val('');            
				    });
				  $('#addTermModal').modal('hide');
				   }
		 );
	
	input = document.getElementById("addTermFormListBoxclassTaxonomy");
	input.addEventListener('change', populateTerms);
	
	input = document.getElementById("addTermFormListBoxparentTaxonomy");
	input.addEventListener('change', populateTerms);
}

function showEditBetweenResults(ev)
{
	var offset = 2; //+2 for table header and empty heading row TODO depends on table heading
	var pos = ev.target.ordinal+offset; 
	
	var tableCon = $('#taxonomyTable')[0];
	var table = $('#taxonomyTbl')[0];
	var editCon = $('#editTaxonomyForm')[0];
	var mainScreen = $('.mainScreen')[0];
	
	
	trs = table.getElementsByTagName("tr");
	var removedTrs = [];
	var originalLength = trs.length;
	
	var splitTbl = document.createElement("table");
	splitTbl.id = 'splitTaxonomyTbl';
	splitTbl.className = 'table table-striped table-hover';
	
	var info = [];
	for(var i=pos; i<originalLength; i++)
	{
		var cl = trs[pos].cloneNode(true);
		cl.extraData = trs[pos].extraData;
		var editBtn = document.getElementById("taxonomyRecordEditButton_"+(i-offset));
		
		if(i>pos)
		{
			removedTrs.push(cl);
			info[i-pos-1] = {};
			info[i-pos-1].taxonomy = editBtn.taxonomy;
			info[i-pos-1].ordinal = editBtn.ordinal;
		}
		else
		{
			splitTbl.stash = cl;
			splitTbl.stash.taxonomy = editBtn.taxonomy;
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
		var editBtn = $(removedTrs[i]).find("#taxonomyRecordEditButton_"+(pos-offset+i+1))[0];
		editBtn.taxonomy = info[i].taxonomy;
		editBtn.ordinal = info[i].ordinal;
		editBtn.addEventListener('click', editButtonListener);
		
		var activateBtn = $(removedTrs[i]).find("#taxonomyRecordActivateButton_"+(pos-offset+i+1))[0];
		activateBtn.taxonomy = info[i].taxonomy;
		activateBtn.addEventListener('click', taxonomyActivateButtonToggleListener);
		
		var manageTermsBtn = $(removedTrs[i]).find("#taxonomyRecordManageButton_"+(pos-offset+i+1))[0];
		manageTermsBtn.taxonomy = info[i].taxonomy;
		manageTermsBtn.addEventListener('click', taxonomyManageTermsButtonListener);
		
		var deleteCheckbox = $(removedTrs[i]).find("#taxonomyRecordDeleteCheckbox_"+(pos-offset+i+1))[0];
		deleteCheckbox.taxonomy = info[i].taxonomy;
	}
	
	$(editCon).show();
	
	tableCon.split = true;
}

function showTermEditBetweenResults(ev)
{
	var offset = 2; //+2 for table header and empty heading row TODO depends on table heading
	var pos = ev.target.ordinal+offset; 
	
	var tableCon = $('#termTable')[0];
	var table = $('#termTbl')[0];
	var editCon = $('#editTermForm')[0];
	var mainScreen = $('.mainScreen')[0];
	
	
	trs = table.getElementsByTagName("tr");
	var removedTrs = [];
	var originalLength = trs.length;
	
	var splitTbl = document.createElement("table");
	splitTbl.id = 'splitTermTbl';
	splitTbl.className = 'table table-striped table-hover';
	
	var info = [];
	for(var i=pos; i<originalLength; i++)
	{
		var cl = trs[pos].cloneNode(true);
		var editBtn = document.getElementById("termRecordEditButton_"+(i-offset));
		
		if(i>pos)
		{
			removedTrs.push(cl);
			info[i-pos-1] = {};
			info[i-pos-1].taxonomy = editBtn.taxonomy;
			info[i-pos-1].term = editBtn.term;	
			info[i-pos-1].ordinal = editBtn.ordinal;
		}
		else
		{
			splitTbl.stash = cl;
			splitTbl.stash.taxonomy = editBtn.taxonomy;
			splitTbl.stash.term = editBtn.term;
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
		var editBtn = $(removedTrs[i]).find("#termRecordEditButton_"+(pos-offset+i+1))[0];
		editBtn.taxonomy = info[i].taxonomy;
		splitTbl.stash.term = editBtn.term;
		editBtn.ordinal = info[i].ordinal;
		editBtn.addEventListener('click', termEditButtonListener);
		
		var activateBtn = $(removedTrs[i]).find("#termRecordActivateButton_"+(pos-offset+i+1))[0];
		activateBtn.taxonomy = info[i].taxonomy;
		activateBtn.term = info[i].term;
		activateBtn.addEventListener('click', termActivateButtonToggleListener);
		
		var deleteCheckbox = $(removedTrs[i]).find("#termRecordDeleteCheckbox_"+(pos-offset+i+1))[0];
		deleteCheckbox.taxonomy = info[i].taxonomy;
		deleteCheckbox.term = info[i].term;
		
	}
	
	$(editCon).show();
	
	tableCon.split = true;
}

function unsplitResults()
{
	var tableCon = $('#taxonomyTable')[0];
	var table = $('#taxonomyTbl')[0];
	var splitTable = $('#splitTaxonomyTbl')[0];
	var editCon = $('#editTaxonomyForm')[0];
	var mainScreen = $('.mainScreen')[0];
	var offset = 2; //+2 for table header and empty heading row TODO depends on table heading
	$(editCon).hide();
	
	var parent = table.getElementsByTagName("tr")[0].parentNode;
	
	var info = [];
	var trs = splitTable.getElementsByTagName("tr");
	var pos = table.getElementsByTagName("tr").length-2; //TODO depends on table heading
	var splitLen = trs.length;
	
	info[0] = {};
	info[0].taxonomy = splitTable.stash.taxonomy;
	info[0].ordinal = splitTable.stash.ordinal;
	parent.appendChild(splitTable.stash);
	for(var i=0; i<splitLen; i++)
	{
		var editBtn = document.getElementById("taxonomyRecordEditButton_"+(i+pos+1));
		info[i+1] = {};
		info[i+1].taxonomy = editBtn.taxonomy;
		info[i+1].ordinal = editBtn.ordinal;
		parent.appendChild(trs[0]);
	}
	
	tableCon.removeChild(editCon);
	tableCon.removeChild(splitTable);
	
	mainScreen.appendChild(editCon);
	
	var len = table.getElementsByTagName("tr").length-offset; 
	for(var i=pos; i<len; i++)
	{
		var editBtn = document.getElementById("taxonomyRecordEditButton_"+(i));
		editBtn.taxonomy = info[i-pos].taxonomy;
		editBtn.ordinal = info[i-pos].ordinal;
		editBtn.addEventListener('click', editButtonListener);
		
		var activateBtn = document.getElementById("taxonomyRecordActivateButton_"+(i));
		activateBtn.taxonomy = info[i-pos].taxonomy;
		activateBtn.addEventListener('click', taxonomyActivateButtonToggleListener);
		
		var manageBtn = document.getElementById("taxonomyRecordManageButton_"+(i));
		manageBtn.taxonomy = info[i-pos].taxonomy;
		manageBtn.addEventListener('click', taxonomyManageTermsButtonListener);
		
		var deleteCheckbox = document.getElementById("taxonomyRecordDeleteCheckbox_"+(i));
		deleteCheckbox.taxonomy = info[i-pos].taxonomy;
	}
	
	tableCon.split = false;
	
}

function unsplitTermResults()
{
	var tableCon = $('#termTable')[0];
	var table = $('#termTbl')[0];
	var splitTable = $('#splitTermTbl')[0];
	var editCon = $('#editTermForm')[0];
	var mainScreen = $('.mainScreen')[0];
	var offset = 2; //+2 for table header and empty heading row TODO depends on table heading
	$(editCon).hide();
	
	var parent = table.getElementsByTagName("tr")[0].parentNode;
	
	var info = [];
	var trs = splitTable.getElementsByTagName("tr");
	var pos = table.getElementsByTagName("tr").length-2; //TODO depends on table heading
	var splitLen = trs.length;
	
	info[0] = {};
	info[0].taxonomy = splitTable.stash.taxonomy;
	info[0].term = splitTable.stash.term;
	info[0].ordinal = splitTable.stash.ordinal;
	parent.appendChild(splitTable.stash);
	
	for(var i=0; i<splitLen; i++)
	{
		var editBtn = document.getElementById("termRecordEditButton_"+(i+pos+1));
		info[i+1] = {};
		info[i+1].taxonomy = editBtn.taxonomy;
		info[i+1].term = editBtn.term;
		info[i+1].ordinal = editBtn.ordinal;
		parent.appendChild(trs[0]);
	}
	
	tableCon.removeChild(editCon);
	tableCon.removeChild(splitTable);
	
	mainScreen.appendChild(editCon);
	
	var len = table.getElementsByTagName("tr").length-offset; 
	for(var i=pos; i<len; i++)
	{
		var editBtn = document.getElementById("termRecordEditButton_"+(i));
		editBtn.taxonomy = info[i-pos].taxonomy;
		editBtn.term = info[i-pos].term;
		editBtn.ordinal = info[i-pos].ordinal;
		editBtn.addEventListener('click', termEditButtonListener);
		
		var activateBtn = document.getElementById("termRecordActivateButton_"+(i));
		activateBtn.taxonomy = info[i-pos].taxonomy;
		activateBtn.term = info[i-pos].term;
		activateBtn.addEventListener('click', termActivateButtonToggleListener);
		
		var deleteCheckbox = document.getElementById("termRecordDeleteCheckbox_"+(i));
		deleteCheckbox.taxonomy = info[i-pos].taxonomy;
		deleteCheckbox.term = info[i-pos].term;
	}
	
	tableCon.split = false;
	
}

function editButtonListener(ev)
{ 
	disableActionControls();
	copyToTaxonomyEditForm(ev);
	$('#editTaxonFormSaveButton')[0].ordinal = ev.target.ordinal;
	showEditBetweenResults(ev); 
}

function termEditButtonListener(ev)
{ 
	disableActionControls();
	copyToTermEditForm(ev);
	$('#editTermFormSaveButton')[0].ordinal = ev.target.ordinal;
	showTermEditBetweenResults(ev); 
}

function copyToTaxonomyEditForm(ev)
{
	var ordinal = ev.target.ordinal;
	$('#editTaxonFormTextBoxname').prop('value', $('#taxonomyName_'+(ordinal)).html());
	
	$('#editTaxonFormListBoxtaxonomyClass option').prop("selected", false);
	$('#editTaxonFormListBoxtaxonomyClass option[value="'+$("#taxonomyClass_"+ordinal).html()+'"]').prop('selected', true);
	
	$('#editTaxonFormCheckBoxuserTaxonomy').prop('checked', false);
	if($('#taxonomyIsUserTaxonomy_'+ordinal).html() == 'Yes')
		$('#editTaxonFormCheckBoxuserTaxonomy').prop('checked', true);
	
	if($('#taxonomyIsActive_'+ordinal).val() == 'Active')
	{
		$('#editTaxonomyFormBtnisActive').prop('value', 'Deactivate');
		$('#editTaxonomyFormBtnisActive').html('Deactivate');
	}else
	{
		$('#editTaxonomyFormBtnisActive').prop('value', 'Activate');
		$('#editTaxonomyFormBtnisActive').html('Activate');
	}
	
	var extraData = $(ev.target).closest('tr')[0].extraData;
	if(isPresent(extraData))
		$('#editTaxonFormTextBoxextraData').val(extraData);
	else
		$('#editTaxonFormTextBoxextraData').val("");
}

function copyToTermEditForm(ev)
{
	var ordinal = ev.target.ordinal;
	$('#editTermFormTextBoxname').prop('value', $('#termName_'+(ordinal)).html());
	$('#editTermFormTextBoxorder').prop('value', $('#termOrder_'+(ordinal)).html());
	
	$('#editTermFormListBoxtaxonomy option').prop("selected", false);
	var val = $("#termTaxonomy_"+ordinal+" option:selected").val();
	$('#editTermFormListBoxtaxonomy option[value="'+$("#termTaxonomy_"+ordinal).html()+'"]').prop('selected', true);
	
	$('#editTermFormListBoxclassTaxonomy option').prop("selected", false);
	$('#editTermFormListBoxclassTaxonomy option[value="'+$("#termClass_"+ordinal).html().split(":")[0]+'"]').prop('selected', true);
	
	$('#editTermFormListBoxclassTerm option').prop("selected", false);
	$('#editTermFormListBoxclassTerm option[value="'+$("#termClass_"+ordinal).html().split(":")[1]+'"]').prop('selected', true);
	
	$('#editTermFormListBoxparentTaxonomy option').prop("selected", false);
	$('#editTermFormListBoxparentTaxonomy option[value="'+$("#termParent_"+ordinal).html().split(":")[0]+'"]').prop('selected', true);
	
	$('#editTermFormListBoxparentTerm option').prop("selected", false);
	$('#editTermFormListBoxparentTerm option[value="'+$("#termParent_"+ordinal).html().split(":")[1]+'"]').prop('selected', true);
	
	if($('#termIsActive_'+ordinal).html() == 'Active')
	{
		$('#editTermFormBtnisActive').prop('value', 'Deactivate');
		$('#editTermFormBtnisActive').html('Deactivate');
	}else
	{
		$('#editTermFormBtnisActive').prop('value', 'Activate');
		$('#editTermFormBtnisActive').html('Activate');
	}
}

function taxonomyActivateButtonToggleListener(ev) 
{
	var updateEditFormActiveBtn =  function(val) 
								   { 
										$('#editTaxonFormBtnisActive').prop('value', val ? 'Deactivate' : 'Activate');
										$('#editTaxonFormBtnisActive').html(val ? 'Deactivate' : 'Activate');
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
//	retrieveUser(ev, [{func: updateEditFormActiveBtn, args: [updateEditFormActiveBtnArg]},
//	                  {func: updateTaxonomy, args: [$('#editTaxonFormObj')[0], false, {func: updateActivateBtnOnSuccess, args: [ev]}]},
//	                  ]);
	copyToTaxonomyEditForm(ev);
	updateEditFormActiveBtn(updateEditFormActiveBtnArg);
	updateTaxonomy(ev, $('#editTaxonFormObj')[0], false, {func: updateActivateBtnOnSuccess, args: [ev]});
}

function termActivateButtonToggleListener(ev) 
{
	var updateEditFormActiveBtn =  function(val) 
								   { 
										$('#editTermFormBtnisActive').prop('value', val ? 'Deactivate' : 'Activate');
										$('#editTermFormBtnisActive').html(val ? 'Deactivate' : 'Activate');
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
//	retrieveUser(ev, [{func: updateEditFormActiveBtn, args: [updateEditFormActiveBtnArg]},
//	                  {func: updateTaxonomy, args: [$('#editTaxonFormObj')[0], false, {func: updateActivateBtnOnSuccess, args: [ev]}]},
//	                  ]);
	copyToTermEditForm(ev);
	updateEditFormActiveBtn(updateEditFormActiveBtnArg);
	updateTerm(ev, $('#editTermFormObj')[0], false, {func: updateActivateBtnOnSuccess, args: [ev]});
}

//function retrieveUser(ev, postProcessingFuncs)
//{
//	name = ev.target.user;
//	customer = ev.target.customer;
//	req = "name="+name+((customer != 'None') ? ("&customer="+customer) : "");
//	var request = $.ajax({ 
//        url : "users/show", 
//        type : "post", 
//        data : req,
//        success : function(userInfo) 
//        		  { 
//        			if(!isPresent(userInfo))
//        			{
//        				alert("User " + name + " not found!");
//        				return;
//        			}
//		        	var attrs = Object.keys(userInfo);
//		        	
//		        	for(var i=0; i<attrs.length; i++)
//		        	{
//		        		if(attrs[i] != 'customer' && attrs[i] != 'isActive')
//		        			$('#textBox'+attrs[i]).prop('value', userInfo[attrs[i]]);
//		        		if(attrs[i] == 'customer')
//		        		{
//		        			$('#listBox'+attrs[i] + ' option').prop("selected", false);
//		        			$('#listBox'+attrs[i]+ ' option[value="'+customer+'"]').prop('selected', true); //make the current taxonomy selected in list selector
//		        		}
//		        		if(attrs[i] == 'isActive' && userInfo.isActive == true)
//		        		{
//		        			$('#btn'+attrs[i]).prop('value', 'Deactivate');
//		        			$('#btn'+attrs[i]).html('Deactivate');
//		        			
//		        		}else
//		        		{
//		        			$('#btn'+attrs[i]).prop('value', 'Activate');
//		        			$('#btn'+attrs[i]).html('Activate');
//		        		}
//		        	    //$('#editButton').prop('value', 'Edit');
//		        		//$('#editButton').html('Edit');
//		        	}
//		        	var expDate = new Date(userInfo['expirationDate']);
//		      
//		        	function addZero(num) { return (num >= 0 && num < 10) ? "0" + num : num + ""; }
//		        	var expDateStr = addZero(expDate.getDate()) + '/' + addZero(expDate.getMonth()+1) + "/" + expDate.getFullYear();
//		        	$('#textBoxexpirationDate').prop('value', expDateStr);
//		        	
//		        	applyFuncs(postProcessingFuncs, userInfo);
//        		  },
//        error : function(jqXHR, textStatus, errorThrown) 
//        		 {
//	   			   alert("The following error occured: " + textStatus, errorThrown);
//        		 }
//      }); 
//}

function taxonomyManageTermsButtonListener(ev)
{
	$('#addTaxonomyButton').prop('disabled', true);
	$('#taxonomyConfigButton').prop('disabled', true);
	$('#taxonomyTable').hide();
	//$("#termTable").css('clear', "none");
	//$("#termTable").css('padding-top', '10%');
	$("#termTable")[0].taxonomy = ev.target.taxonomy;
//	$('#addTaxonomyButton').hide();
//	$('#taxonomyConfigButton').hide();
	//$('.addTaxonomyButtonCon').hide();
	$('#termTable').show();
	createTermTable();
}

function taxonomyConfigButtonListener(ev)
{
	$('#searchCon').hide();
	$('#taxonomyTable').hide();
	$('#taxonomyConfig').css('clear', "none");
//	$('#taxonomyConfig').css('padding-top', '10%');
	$('#taxonomyConfigButton').hide();
	$('#taxonomyConfigForm').find('option[value="None"]').text('Select a taxonomy');
	$('#taxonomyConfigForm select').click(function(ev)
				{
					$(ev.target).find('option[value="None"]').text('None');
					$(ev.target).off('click');
				});
	$('#taxonomyConfig').show();
}

function taxonomyConfigSaveButtonListener(ev)
{
	var selects = $('#taxonomyConfigForm select');
	var req = [];
	var cnt = 0;
	for(var i=0; i<selects.length; i++)
	{
		var selectedConfig = $(selects[i]).find("option:selected").val();
		//if(selectedConfig == 'None')
		//	continue;
		req.push({});
		req[cnt].type = selects[cnt].name;
		req[cnt].id = selectedConfig;
		cnt++;
	}
	
	req = JSON.stringify(req);
	
	$.ajax({
        url : "taxonomies/updateTaxonomyConfig",
        type : "post", 
        data : req,
        dataType : "json",
        contentType : "application/json",
        
        success : function(response) {
        	if(response.status == false)
        	{
        		alert("An error has occurred: " + response.message);
        		return;
        	}
        },
        
        error : function(jqXHR, textStatus, errorThrown) 
		 {
			alert("The following error occured: " + textStatus, errorThrown);
		 }
	});
	
	$('#searchCon').show();
	$('#taxonomyTable').show();
	$('#taxonomyConfig').hide();
	$('#taxonomyConfigButton').show();
}

function taxonomyConfigCancelButtonListener(ev)
{
	$('#searchCon').show();
	$('#taxonomyTable').show();
	$('#taxonomyConfig').hide();
	$('#taxonomyConfigButton').show();
}

function updateTaxonomy(ev, form, create, postProcessingFuncs)
{

	var values = $(form).serializeArray();
	values = formRemoveIfValue(values, 'taxonomyClass', 'None');//[{name: 'taxonomyClass', value: 'None'}], [{name: 'isActive', [{value: 'on', newValue: 'true'}, {value: 'off', newValue: 'false'}]
	var userTax = formFindValue(values, 'userTaxonomy');
	if(userTax == null) formReplaceValue(values, 'userTaxonomy', false);
	else
	{
		//userTax = userTax.toLowerCase();
		//if(userTax == "on" || userTax == "yes") formReplaceValue(values, 'userTaxonomy', true);
		//else if(userTax == "off" || userTax == "no") formReplaceValue(values, 'userTaxonomy', false);
	}
	
	var isActive = $('#editTaxonFormBtnisActive').val() == 'Deactivate' ? true : false;
	var toPush = {};
	toPush.name = 'active';
	toPush.value = isActive;
	values.push(toPush);
	
	var originalName;
	if(!create) originalName = $('#taxonomyName_'+ev.target.ordinal).html();
	//var dateParts = formFindValue(values, 'expirationDate').split('/');
	// new Date(year, month [, date [, hours[, minutes[, seconds[, ms]]]]])
	  
	//values = formReplaceValue(values, 'expirationDate',  new Date(dateParts[2], dateParts[1]-1, dateParts[0]).getTime());
	//var isActive = $('#btnisActive').val() == 'Deactivate' ? true : false;
	if(!create)
	{
		toPush = {};
		toPush.name = 'originalName';
		toPush.value = originalName;
		values.push(toPush);
	}
	
	var taxonomyInfo = jQuery.param(values);
	
    $.ajax({ 
        url : create ? "taxonomies/addTaxonomy" : "taxonomies/updateTaxonomy",
        type : "post", 
        data : taxonomyInfo,
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
					createTaxonomyTable(data, false);
					applyFuncs(postProcessingFuncs, response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function deleteTaxonomies(ev, postProcessingFuncs)
{
	var len = $('#taxonomyTbl')[0].entryCount;
	var values = [];
	
	for(var i=0; i<len; i++)
	{
		if($('#taxonomyRecordDeleteCheckbox_'+i).is(':checked'))
		{
			var chkBox = $('#taxonomyRecordDeleteCheckbox_'+i)[0];
			values.push(chkBox.taxonomy);
		}
	}
	
	var req = JSON.stringify(values);
	$.ajax({ 
        url : "taxonomies/deleteTaxonomies",
        type : "post", 
        data : req,
        dataType: "json",
        contentType: "application/json",
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
					createTaxonomyTable(data, false);
					applyFuncs(postProcessingFuncs, response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function deleteTerms(ev, postProcessingFuncs)
{
	var len = $('#termTbl')[0].entryCount;
	var values = [];
	
	for(var i=0; i<len; i++)
	{
		if($('#termRecordDeleteCheckbox_'+i).is(':checked'))
		{
			var chkBox = $('#termRecordDeleteCheckbox_'+i)[0];
			var toPush = {};
			toPush.taxonomy = chkBox.taxonomy;
			toPush.term = chkBox.term;
			values.push(toPush);
		}
	}
	
	var req = JSON.stringify(values);
	$.ajax({ 
        url : "taxonomies/deleteTerms",
        type : "post", 
        dataType : "json",
        contentType : "application/json",
        data : req,
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
					createTermTable(data, false);
					applyFuncs(postProcessingFuncs, response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function updateTerm(ev, form, create, postProcessingFuncs)
{

	var values = $(form).serializeArray();
	values = formRemoveIfValue(values, 'classTaxonomy', 'None');//[{name: 'taxonomyClass', value: 'None'}], [{name: 'isActive', [{value: 'on', newValue: 'true'}, {value: 'off', newValue: 'false'}]
	values = formRemoveIfValue(values, 'classTerm', 'None');
	values = formRemoveIfValue(values, 'parentTaxonomy', 'None');
	values = formRemoveIfValue(values, 'parentTerm', 'None');
	values = formRemoveIfValue(values, 'order', '');
	values = formRemoveIfValue(values, 'configuration', '');
	
	var isActive = true;
	if(!create) isActive = $('#editTermFormBtnisActive').val() == 'Deactivate' ? true : false;
	var toPush = {};
	toPush.name = 'active';
	toPush.value = isActive;
	values.push(toPush);
	
	if(!create)
	{
		var originalName = $('#termName_'+ev.target.ordinal).html();
		var originalTaxonomyName = $('#termTaxonomy_'+ev.target.ordinal).html();
		
		
		toPush = {};
		toPush.name = 'originalName';
		toPush.value = originalName;
		values.push(toPush);
		
		toPush = {};
		toPush.name = "originalTaxonomyName";
		toPush.value = originalTaxonomyName;
		values.push(toPush);
	}
	
	var termInfo = jQuery.param(values);
	
    $.ajax({ 
        url : create ? "taxonomies/addTerm" : "taxonomies/updateTerm",
        type : "post", 
        data : termInfo,
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
					createTermTable(data, false);
					applyFuncs(postProcessingFuncs, response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function conditionalDisable(controlId)
{
	if(!isPresent(window.disableInfo)) window.disableInfo = {};
	if($('#'+controlId).prop('disabled') == true) 
		window.disableInfo[controlId] = true;
	else
	{
		window.disableInfo[controlId] = false;
		$('#'+controlId).prop('disabled', true);
	}
}

function conditionalEnable(controlId)
{
	if(!window.disableInfo || window.disableInfo[controlId] == false)
		$('#'+controlId).prop('disabled', false);
}

function disableActionControls()
{
	conditionalDisable('searchButton');
	conditionalDisable('addTaxonomyButton');
	conditionalDisable('addTermButton');
	conditionalDisable('taxonomyConfigButton');
	
	$('#taxonomyDeleteButton').prop('disabled', true);
	$('#termDeleteButton').prop('disabled', true);
	var table = $('#taxonomyTbl')[0];
	var len = 0;
	if(isPresent(table)) len = table.entryCount;
	for(var i=0; i<len; i++)
	{
		$('#taxonomyRecordEditButton_'+i).prop('disabled', true);
		$('#taxonomyRecordActivateButton_'+i).prop('disabled', true);
		$('#taxonomyRecordManageButton_'+i).prop('disabled', true);
		$('#taxonomyRecordDeleteCheckbox_'+i).prop('disabled', true);
	}
	table = $('#termTbl')[0];
	var len = 0;
	if(isPresent(table)) len = table.entryCount;
	for(var i=0; i<len; i++)
	{
		$('#termRecordEditButton_'+i).prop('disabled', true);
		$('#termRecordActivateButton_'+i).prop('disabled', true);
		$('#termRecordDescendantsButton_'+i).prop('disabled', true);
		$('#termRecordClassDescendantsButton_'+i).prop('disabled', true);
		$('#termRecordDeleteCheckbox_'+i).prop('disabled', true);
	}
}

function enableActionControls()
{
	conditionalEnable('searchButton');
	conditionalEnable('addTaxonomyButton');
	conditionalEnable('addTermButton');
	conditionalEnable('taxonomyConfigButton');
	$('#taxonomyDeleteButton').prop('disabled', false);
	$('#termDeleteButton').prop('disabled', false);
	var table = $('#taxonomyTbl')[0];
	var len = 0;
	if(isPresent(table)) len = table.entryCount;
	for(var i=0; i<len; i++)
	{
		$('#taxonomyRecordEditButton_'+i).prop('disabled', false);
		$('#taxonomyRecordActivateButton_'+i).prop('disabled', false);
		$('#taxonomyRecordManageButton_'+i).prop('disabled', false);
		$('#taxonomyRecordDeleteCheckbox_'+i).prop('disabled', false);
	}
	table = $('#termTbl')[0];
	len = 0;
	if(isPresent(table)) len = table.entryCount;
	for(var i=0; i<len; i++)
	{
		$('#termRecordEditButton_'+i).prop('disabled', false);
		$('#termRecordActivateButton_'+i).prop('disabled', false);
		$('#termRecordDescendantsButton_'+i).prop('disabled', false);
		$('#termRecordClassDescendantsButton_'+i).prop('disabled', false);
		$('#termRecordDeleteCheckbox_'+i).prop('disabled', false);
	}
}