function showDataImport(data)
{
	enableZMaxIndex();
	
	createImportForm();
	populateMainTaxonomySelectors();
	createImportButton();
	createReplaceMergeModal();
	createSuccessfulImportButtons();
	
	$('#importButton').hide();
	
	$('#updateInfoButton').click( function()
			{
				$.ajax({
					contentType: "application/json",
			        url : "import/updateProjects",
			        type : "post",
			        success : function(response) 
			        		  {
					        	if(response.status == 'Failure')
			        			{
			        				alert("An error has occurred: " + response.message);
			        				return;
			        			}
					        	else if(response.status == 'Success')
					        		alert("Info updated");
			        		  },
				  error: 	function(jqXHR, textStatus, errorThrown) 
						    {
						      alert("The following error occured: " + textStatus, errorThrown);
						    }}
				);
			});
	
	$('#importCon').show();
	$('#attributeTable').hide();
	$('#importFormBoundaryGroup').hide();
	$('#successfulImportVisualization').hide();
	$('#replaceMergeModal').hide();
	$('#loadingModal').hide();
	$('#successfulImport').hide();
    createAdminMenu(data);
	setPrimaryMenuButton("btnDataImport");
	enableActionControls();
}

function populateMainTaxonomySelectors()
{
	var select = $('#listBoxTaxonomy')[0];
	var boundarySelect = $('#listBoxBoundaryTaxonomy')[0];
	$(select).empty();
	$(boundarySelect).empty();
	var option = document.createElement("option");
	option.value = "None";
	option.text = "Select a taxonomy";
	option.selected = true;
	option.disabled = true;
	option.style = "display:none";
	select.appendChild(option);
	boundarySelect.appendChild(option.cloneNode());
	
	var req = "active=true";
	$.ajax({ 
        url : "taxonomies/listTaxonomies",
        type : "post", 
        data : req,
        success : function(taxons) 
        		  {
        			for(var i=0; i<taxons.length; i++)
        			{
        				option = document.createElement("option");
        				option.value = taxons[i];
        				option.text = taxons[i];
        				select.appendChild(option);
        				boundarySelect.appendChild(option.cloneNode());
        			}
        		  },
	  error: 	function(jqXHR, textStatus, errorThrown) 
			    {
			      alert("The following error occured: " + textStatus, errorThrown);
			    }});
}

function populateTermSelector(ev, select)
{
	$(select).empty();
	var option = document.createElement("option");
	
	option.value = "None";
	option.text = "Select a term";
	option.selected = true;
	option.disabled = true;
	option.style = "display:none";
	select.appendChild(option);
	
	var taxonomy = $(":selected",ev.target).val();
	$.ajax({ 
        url : "taxonomies/listTerms",
        type : "post", 
        data : "taxonomy="+taxonomy,
        success : function(terms) 
        		  {
        			for(var i=0; i<terms.length; i++)
        			{
        				var option = document.createElement("option");
        				option.value = terms[i];
        				option.text = terms[i];
        				select.appendChild(option);
        			}
        		  },
	  error: 	function(jqXHR, textStatus, errorThrown) 
			    {
			      alert("The following error occured: " + textStatus, errorThrown);
			    }});
}

function populateMainTermSelector(ev)
{
	var select = $('#listBoxTerm')[0];
	populateTermSelector(ev, select);
}

function populateBoundaryTermSelector(ev)
{
	var select = $('#listBoxBoundaryTerm')[0];
	populateTermSelector(ev, select);
}

function createImportForm()
{
	var attrTable = $('#attributeTable');
	if(isPresent(attrTable)) attrTable.hide();
	
	input = document.getElementById("analyzeButton");
	
	input.addEventListener('click', function()
			   {
				requestAnalyze([{func: function()
									   {
											$('#attributeTable').show(); 
											$('#importButton').show();
											$('body').addClass('data-import-attributes');
									   }}]);
			   }
	 );
	
	input = document.getElementById("inputBoxFiles");
	input.addEventListener('change', function(ev)
									{
										var inputFiles = ev.target.files;
										var found = false;
										for(var i=0; i<inputFiles.length; i++)
									    {
									    	if(pathToFile(inputFiles[i].name).extension == "prj")
									    	{
									    		//store present selector so that it can be restored if a later selection of files does not include a .prj file
									    		if(!isPresent(ev.target.storedSelector))
									    		{
										    		ev.target.storedSelector = $('#listBoxCRS')[0];
										    		var select = ev.target.storedSelector.cloneNode();
													$(select).empty();
										    		select.innerHTML = '<option value="" disabled="disabled" selected="selected">Defined in .prj file</option>';
													$(select).prop('disabled', true);
													select.id = "listBoxCRS";
													var parent = ev.target.storedSelector.parentNode;
													parent.removeChild(ev.target.storedSelector);
													parent.appendChild(select);
									    		}
												found = true;
												
									    	}
									    	if(!found && isPresent(ev.target.storedSelector))
									    	{
									    		var sel = document.getElementById('listBoxCRS');
									    		var parent = sel.parentNode;
									    		parent.removeChild(sel);
									    		parent.appendChild(ev.target.storedSelector);
									    		ev.target.storedSelector = null;
									    	}
									    }
									});
	
	$('#checkBoxBoundary').prop('checked', false);
	input = document.getElementById('checkBoxBoundary');
	input.addEventListener('change', function(ev)
									 {
										if($(ev.target).is(':checked'))
										{
											$('#importFormBoundaryGroup').show();
											$('#listBoxBoundaryTaxonomy').prop('disabled', false);
											$('#listBoxBoundaryTerm').prop('disabled', false);
										}else
										{
											$('#importFormBoundaryGroup').hide();
											$('#listBoxBoundaryTaxonomy').prop('disabled', true);
											$('#listBoxBoundaryTerm').prop('disabled', true);
										}
									 }
			);
	
	
	$('#listBoxCharset').empty();
	var select = $('#listBoxCharset')[0];
	var option = document.createElement("option");
	option.value = "windows-1253";
	option.text = "windows-1253";
	$(option).prop('selected', true);
	select.appendChild(option);
	
	option = document.createElement("option");
	option.value = "UTF-8";
	option.text = "UTF-8";
	select.appendChild(option);
	
	$('#listBoxCRS').empty();
	var select = $('#listBoxCRS')[0];
	var option = document.createElement("option");
	option.value = "2100";
	option.text = "EPSG:2100 (Greek Grid)";
	$(option).prop('selected', true);
	select.appendChild(option);
	
	option = document.createElement("option");
	option.value = "4326";
	option.text = "EPSG:4326 (WGS84)";
	select.appendChild(option);
	
	select = $('#listBoxTaxonomy')[0];
	select.addEventListener('change', populateMainTermSelector);
	
	select = $('#listBoxBoundaryTaxonomy')[0];
	select.addEventListener('change', populateBoundaryTermSelector);
}

function createImportButton()
{
	var input = document.getElementById('importButton');
	input.addEventListener('click', function(ev)
									{
										requestImport([{func: function(importInfo)
															  {
																$('#attributeTable').hide(); 
																$('#importButton').hide();
																$('body').removeClass('data-import-attributes');
																$('#importCon').hide();
																$('#successfulImportText').html("Import was successful. Layer name: " + importInfo.message);
																$('#successfulImport').show();
															   }, 
													    args: ["__funcRet"]}]);
									});
								
}

function createSuccessfulImportButtons()
{
	$('#successfulImportFormButtonView')[0].addEventListener('click', function(ev)
																	  {
																		$('#successfulImportVisualization').show();
																		if(isPresent($('#successfulImportVisualization')[0].map)) 
																			$('#successfulImportVisualization')[0].map.destroy();
																		$('#successfulImportVisualization')[0].map = 
																			showShape($('#successfulImportVisualization')[0], window.shapeImportInfo.layerName, window.shapeImportInfo.bounds,
																					window.shapeImportInfo.boundaryTermTaxonomy, window.shapeImportInfo.boundaryTerm);
																	  }
	);
	
	$('#successfulImportFormButtonDone')[0].addEventListener('click', function(ev)
																	   {
																		if(isPresent($('#successfulImportVisualization')[0].map)) 
																			$('#successfulImportVisualization')[0].map.destroy();
																		 $('#successfulImportVisualization').hide();
																		 $('#successfulImport').hide();
																		 $('#importCon').show();
																	   }
	);
}

function createReplaceMergeModal()
{
	$('#replaceMergeModal').modal('hide');
	$('#replaceMergeModalVisualization').hide();
	
	$('#replaceMergeModalReplaceButton')[0].addEventListener('click', function(ev)
																	  {
																		 requestImport([{func: function(importInfo)
																							   {
																			 					$('#importCon').hide();
																							 	$('#attributeTable').hide(); 
																							 	$('body').removeClass('data-import-attributes');
																								$('#importButton').hide();
																								$('#successfulImportText').html("Import was successful. Layer name: " + importInfo.message);
																								$('#successfulImport').show();
																							   }, 
																					     args: ["__funcRet"]
																						}],
																					     true, false);
																		 if(isPresent($('#replaceMergeModalVisualization')[0].map)) $('#replaceMergeModalVisualization')[0].map.destroy();
																		 $('#replaceMergeModalVisualization').hide();
																		 $('#replaceMergeModal').modal('hide');
																	  }
	);
	
	$('#replaceMergeModalMergeButton')[0].addEventListener('click', function(ev)
																    {
																		requestImport([{func: function(importInfo)
																							  {
																								$('#importCon').hide();
																							 	$('#attributeTable').hide(); 
																								$('#importButton').hide();
																								$('body').removeClass('data-import-attributes');
																								$('#successfulImportText').html("Import was successful. Layer name: " + importInfo.message);
																								$('#successfulImport').show();
																							   }, 
																					     args: ["__funcRet"]
																						}],
																					   false, true);
																		if(isPresent($('#replaceMergeModalVisualization')[0].map)) $('#replaceMergeModalVisualization')[0].map.destroy();
																		$('#replaceMergeModalVisualization').hide();
																		$('#replaceMergeModal').modal('hide');
																   }
	);
	
	
	$('#replaceMergeModalCancelButton')[0].addEventListener('click', function(ev)
																	  {
																		 if(isPresent($('#replaceMergeModalVisualization')[0].map)) $('#replaceMergeModalVisualization')[0].map.destroy();
																		 $('#replaceMergeModalVisualization').hide();
																		 $('#replaceMergeModal').modal('hide');
																	  }
	);
	
	$('#replaceMergeModalViewButton')[0].addEventListener('click', function(ev)
																   {
		 																if(isPresent($('#replaceMergeModalVisualization')[0].map)) $('#replaceMergeModalVisualization')[0].map.destroy();
																		$('#replaceMergeModalVisualization').show();
																		$('#replaceMergeModalVisualization')[0].map =
																			showShape($('#replaceMergeModalVisualization')[0], $('#listBoxTerm option:selected').val(), window.shapeImportInfo.bounds);
																   }
	);
}

function requestAnalyze(postProcessingFuncs, empty) {

	var attrTypes = ["integer", "short", "long", "float", "double", "date", "string"];
	var attrTypesText = ["Integer", "Short integer", "Long integer", "Floating point", "Double precision fp", "Date", "String"];
	
    var tbl  = document.createElement('table');
    //tbl.className = "geopolis-admin-widget geopolis-admin-widget-content";
    
    tbl.id = 'attrTbl';
    tbl.className = "table table-striped table-hover";
    tbl.completeMappingCount = 0;
    
    var thead = document.createElement('thead');
    tbl.appendChild(thead);
    
    var td;
//    var th = document.createElement("th");
//    th.colSpan = "6";
//    th.className = "heading";
//    th.innerHTML = "User Results";
//    tr.appendChild(th);
    
    var tr = thead.insertRow(-1);
    var th;
    var ths = ["Name", "Type", "Taxonomy", "Store", "Presentable", "Manual Mapping", "Auto Value Mapping",
               "Taxonomy of Parent Term", "Term Linking", "Link Verb", "Auto Document Mapping"];
    
    for(var i=0; i<ths.length; i++)
    {
	    th = document.createElement("th");
	    th.innerHTML = ths[i];
	    tr.appendChild(th);
    }
    
    var tbody = document.createElement('tbody');
    tbl.appendChild(tbody);
    
    var parent = document.getElementById('attributeTable');
    var old = document.getElementById('attrTbl');
    tbl.token = old.token;
    parent.removeChild(old);
    parent.appendChild(tbl);
    
    if(isPresent(empty) && empty == true)
    	return tbl;
    
    var inputFiles = $('#inputBoxFiles')[0].files;
    var formData = new FormData();
    for(var i=0; i<inputFiles.length; i++)
    {
    	var ext = pathToFile(inputFiles[i].name).extension;
    	switch(ext)
    	{
    	case "prj":
    	case "shp":
    	case "shx":
    	case "dbf":
    	case "sbn":
    	case "sbx":
    	case "spg":
    	case "xml":
    		
    		break;
    	default:
    		alert("Unrecognized input!");
    		//clear form
    		$('#importFormObj').find("input[type=text] , textarea").each(function(){$(this).val(''); });
    		$('#inputBoxFiles')[0].reset();
    		return;
    	}
    	formData.append(ext+'File', inputFiles[i]); //server expects argument names in <ext>File form
    }
    formData.append('crs', $('#listBoxCRS').val());
    formData.append('dbfCharset', $('#listBoxCharset').val());

    $.ajax({ 
        url : "import/analyze", 
        type : "post", 
        data : formData,
        contentType: false,
        processData: false,
        success : function(attrInfo) 
        		  { 
        				if(attrInfo.status == false)
        				{
        					alert("An error has occured: " + attrInfo.token);
        					return;
        				}
        				var req = "active=true";
        				$.ajax({ 
        			        url : "taxonomies/listTaxonomies",
        			        type : "post", 
        			        data : req,
        			        success : function(taxons) 
        			        		  {
        			        			$.ajax({
        			        				url : "taxonomies/retrieveLinkVerbs",
        			        				type : "post",
        			        				success : 
        			        				function(verbs)
        			        				{
        			        					var count = 0;
        				        				var attrs = Object.keys(attrInfo.attrs);
        						        		for(var i=0; i<attrs.length; i++)
        						        		{
        						        			tr = tbody.insertRow(-1);
        						        			
        						        			td = tr.insertCell(-1);
        						        			td.id = "attrName_"+count;
        						        			td.className = "attrNames";
        						        			td.innerHTML = attrs[i];//.name;
        						        				
        						        			td = tr.insertCell(-1);
        						        			td.id = "attrType_"+count;
        						        			td.className = "attrTypes";
        						        			
        						        			var select = document.createElement("select");
        						        			select.id = "attrTypeSelector_"+count;
        						        			for(var j=0; j<attrTypes.length; j++)
        						        			{
        						        				var option = document.createElement("option");
        						        				option.value = attrTypes[j];
        						        				option.text = attrTypesText[j];
        						        				select.appendChild(option);
        						        			}
        						        			td.appendChild(select);
        						        			$('#attrTypeSelector_'+count+' option[value="'+attrInfo.attrs[attrs[i]]+'"]').prop('selected', true); //make the retrieved selected in list selector
        						        			
        						        			td = tr.insertCell(-1);
        						        			td.id = "attrTaxon_"+count;
        						        			td.className = "attrTaxons";
        						        			select = document.createElement("select");
        						        			select.id = "attrTaxonSelector_"+count;
        						        			select.ordinal = count;
        						        			//select.addEventListener('change', populateTerms);
        						        			
        						        			option = document.createElement("option");
        					        				option.value = "None";
        					        				option.text = "None";
        					        				option.selected = true;
        					        				select.appendChild(option);
        					        				for(var j=0; j<taxons.length; j++)
        								        	{
        								        		var option = document.createElement("option");
        								        		option.value = taxons[j];
        								        		option.text = taxons[j];
        								        		select.appendChild(option);
        								        	}
        					        				td.appendChild(select);
        						        			
        						        			td = tr.insertCell(-1);
        						        			var chkBox = document.createElement("input");
        						        			chkBox.type = "checkbox";
        						        			chkBox.id = "attrStoreChkBox_"+count;
        						        			chkBox.checked = 'checked';
        						        			chkBox.ordinal = count;
        						        			td.appendChild(chkBox);
        						        			
        						        			chkBox.addEventListener('click', function(ev)
        						        											 {
        						        												if($(ev.target).prop('checked') == false)
        						        												{
        						        													var attrValbtn = $('#attrValueMappingAddButton_'+ev.target.ordinal)[0];
        						        													var attDocbtn = $('#attrDocMappingAddButton_'+ev.target.ordinal)[0];
        						        													if((isPresent(attrValbtn.valueMappingCount) && attrValbtn.valueMappingCount > 0)
        						        															|| (isPresent(attrDocbtn.valueMappingCount) && attrDocbtn.valueMappingCount >0))
        						        													{							
        							        													$('#removeValueMappingsModalYesButton').off('click').click(function()
        							        																	{
        							        																		removeValueMappings(ev.target.ordinal);
        							        																		removeDocMappings(ev.target.ordinal);
        							        																		$('#attrValueMappingAddButton_'+ev.target.ordinal).prop('disabled', true);
        							        																		$('#attrPresentableChkBox_'+ev.target.ordinal).prop('checked', false);
        							        																		$('#attrPresentableChkBox_'+ev.target.ordinal).prop('disabled', true);
        							        																		//auto value mapping is not checked at this point
        							        																		var autoCheckbox = $('#attrValueMappingAutoChkBox_'+ev.target.ordinal)[0];
        							        																		autoCheckbox.prevDisabled = $(autoCheckbox).prop('disabled'); 
        							        																		$(autoCheckbox).prop('disabled', true); 
        							        																		var linkCheckbox = $('#attrValueMappingLinkChkBox_'+ev.target.ordinal); 
        							        																		if($(linkCheckbox).prop('checked') == true)
        							        																			$(linkCheckbox).click();
        							        																		var autoDocCheckbox = $('#attrDocMappingAutoChkBox_'+ev.target.ordinal)[0];
        							        																		$(autoDocCheckbox).prop('checked', false);
        							        																		$(autoDocCheckbox).prop('disabled', true);
        							        																		$('#removeValueMappingsModal').modal('hide');
        							        																	});
        							        													
        							        													$('#removeValueMappingsModalNoButton').off('click').click(function() 
        							        																	{
        							        																		$(ev.target).prop('checked', true);
        							        																		$('#removeValueMappingsModal').modal('hide');
        							        																	});
        							        													$('#removeValueMappingsModal').modal('show');
        						        													}else
        						        													{
        						        														$('#attrValueMappingAddButton_'+ev.target.ordinal).prop('disabled', true);
        		        																		$('#attrPresentableChkBox_'+ev.target.ordinal).prop('checked', false);
        		        																		$('#attrPresentableChkBox_'+ev.target.ordinal).prop('disabled', true);
        		        																		
        		        																		var autoCheckbox = $('#attrValueMappingAutoChkBox_'+ev.target.ordinal)[0]; 
        		        																		if($(autoCheckbox).prop('checked') == true)
        		        																			$(autoCheckbox).click();
        		        																		autoCheckbox.prevDisabled = $(autoCheckbox).prop('disabled'); 
        		        																		$(autoCheckbox).prop('disabled', true);
        		        																		var linkCheckbox = $('#attrValueMappingLinkChkBox_'+ev.target.ordinal); 
        		        																		if($(linkCheckbox).prop('checked') == true)
				        																			$(linkCheckbox).click();
        		        																		linkChekcbox.prevDisabled = $(linkCheckbox).prop('disabled');
        		        																		$(linkCheckbox).prop('disabled', true);
        		        																		var autoDocCheckbox = $('#attrDocMappingAutoChkBox_'+ev.target.ordinal)[0];
				        																		$(autoDocCheckbox).prop('checked', false);
				        																		$(autoDocCheckbox).prop('disabled', true);
        						        													}
        						        									
        						        												}
        						        												else
        						        												{
        						        													$('#attrValueMappingAddButton_'+ev.target.ordinal).prop('disabled', false);
        						        													$('#attrPresentableChkBox_'+ev.target.ordinal).prop('checked', true);
        						        													$('#attrPresentableChkBox_'+ev.target.ordinal).prop('disabled', false);
        						        													
        						        													var prevDisabled = $('#attrValueMappingAutoChkBox_'+ev.target.ordinal)[0].prevDisabled; 
        						        													$('#attrValueMappingAutoChkBox_'+ev.target.ordinal).prop('disabled', isPresent(prevDisabled) ? prevDisabled : false);
        						        													var prevDisabled = $('#attrLinkChkBox_'+ev.target.ordinal)[0].prevDisabled; 
        						        													$('#attrLinkChkBox_'+ev.target.ordinal).prop('disabled', isPresent(prevDisabled) ? prevDisabled : false);
        						        													$('#attrDocMappingAutoChkBox_'+ev.target.ordinal).prop('disabled', false);
        						        													
        						        												}
        						        											 });
        						        			
        						        			td = tr.insertCell(-1);
        						        			chkBox = document.createElement("input");
        						        			chkBox.type = "checkbox";
        						        			chkBox.id = "attrPresentableChkBox_"+count;
        						        			chkBox.checked = 'checked';
        						        			td.appendChild(chkBox);
        						        			
        						        			td = tr.insertCell(-1);
        						        			td.className = 'attrMappings';
        						        			var btnGroup = document.createElement('div');
        						        			btnGroup.className = 'btn-group';
        					        				
        						        			var button = document.createElement("button");
        					        				button.id = "attrValueMappingAddButton_"+count;
        					        				button.className = "btn btn-sm btn-primary";
        					        				button.value = "Value";
        					        				button.innerHTML = "Value";
        					        				button.ordinal = count;
        					        				button.addEventListener('click', function(ev) { addAttributeValueMapping(ev, tbl, taxons); });
        						        			btnGroup.appendChild(button);
        						        			
        						        			button = document.createElement('button');
        						        			button.id = 'attrDocMappingAddButton_'+count;
        						        			button.className = 'btn btn-sm btn-primary';
        						        			button.value = 'Document';
        						        			button.innerHTML = 'Document';
        						        			button.ordinal = count;
        						        			button.addEventListener('click', function(ev) { addAttributeDocumentMapping(ev, tbl, taxons); });
        						        			btnGroup.appendChild(button);
        						        			
        					        				td.appendChild(btnGroup);
        						        			
        						        			td = tr.insertCell(-1);
        						        			chkBox = document.createElement('input');
        						        			chkBox.type = 'checkbox';
        						        			chkBox.id = 'attrValueMappingAutoChkBox_'+count;
        						        			chkBox.className = 'attrValueMappingAutoChkbox';
        						        			chkBox.ordinal = count;
        						        			td.appendChild(chkBox);
        						        			chkBox.addEventListener('click', function(ev)
        							       											 {
        							       												if($(ev.target).prop('checked') == true)
        							       												{
        							       													var attrValbtn = $('#attrValueMappingAddButton_'+ev.target.ordinal)[0];
        							       													if(isPresent(attrValbtn.valueMappingCount) && attrValbtn.valueMappingCount > 0)
        							       													{							
        								        													$('#removeValueMappingsModalYesButton').off('click').click(function()
        								        																	{
        								        																		removeValueMappings(ev.target.ordinal);
        								        																		$('#attrValueMappingAddButton_'+ev.target.ordinal).prop('disabled', true);
        								        																		getTextNodesInChildrenOf($('#attrParentTaxon_'+ev.target.ordinal)).remove();
        								        																		$('#attrParentTaxonSelector_'+ev.target.ordinal).show();
        								        																		$('#removeValueMappingsModal').modal('hide');
        								        																		$('.attrLinkChkbox').prop('disabled', false);
        								        																	});
        								        													
        								        													$('#removeValueMappingsModalNoButton').off('click').click(function() 
        								        																	{
        								        																		$(ev.target).prop('checked', false);
        								        																		$('#removeValueMappingsModal').modal('hide');
        								        																	});
        								        													$('#removeValueMappingsModal').modal('show');
        							       													}else
        							       													{
        							       														$('#attrValueMappingAddButton_'+ev.target.ordinal).prop('disabled', true);
        							       														getTextNodesInChildrenOf($('#attrParentTaxon_'+ev.target.ordinal)).remove();
        							       														$('#attrParentTaxonSelector_'+ev.target.ordinal).show();
        							       														$('.attrLinkChkbox').prop('disabled', false);
        							       													}
        							       									
        							       												}
        							       												else
        							       												{
        							       													$('#attrValueMappingAddButton_'+ev.target.ordinal).prop('disabled', false);
        							       													var textNode = document.createTextNode('None');
        							       													$('#attrParentTaxon_'+ev.target.ordinal).append(textNode);
        							       													$('#attrParentTaxonSelector_'+ev.target.ordinal).hide();
        							       													var checkedAutovalue = $('.attrValueMappingAutoChkbox:checked');
        							       													if(checkedAutovalue.length == 0 && $('#attrTbl')[0].completeMappingCount == 0)
        							       													{
        							       														var checkedLinks = $('.attrLinkChkbox:checked');
        							       														for(var i=0; i< checkedLinks.length; i++)
        							       															$(checkedLinks[i]).click();
        							       														$('.attrLinkChkbox').prop('disabled', true);
        							       													}
        							       												}
        							       											 });
        						        			
        						        			td = tr.insertCell(-1);
        						        			td.id = "attrParentTaxon_"+count;
        						        			td.className = "attrParentTaxons";
        						        			select = document.createElement("select");
        						        			select.id = "attrParentTaxonSelector_"+count;
        						        			select.ordinal = count;
        						        			
        						        			option = document.createElement("option");
        					        				option.value = "None";
        					        				option.text = "Select a taxonomy";
        					        				option.selected = true;
        					        				option.disabled = true;
        					        				select.appendChild(option);
        					        				for(var j=0; j<taxons.length; j++)
        								        	{
        								        		var option = document.createElement("option");
        								        		option.value = taxons[j];
        								        		option.text = taxons[j];
        								        		select.appendChild(option);
        								        	}
        					        				td.appendChild(select);
        					        				var textNode = document.createTextNode('None');
        					        				$(td).append(textNode);
        					        				$(select).hide();
        					        				
        					        				td = tr.insertCell(-1);
        						        			chkBox = document.createElement('input');
        						        			chkBox.type = 'checkbox';
        						        			chkBox.id = 'attrValueMappingLinkChkBox_'+count;
        						        			chkBox.ordinal = count;
        						        			chkBox.className='attrLinkChkbox';
        						        			td.appendChild(chkBox);
        						        			chkBox.addEventListener('click', function(ev)
			       											 {
			       												if($(ev.target).prop('checked') == true)
			       												{
			       													getTextNodesInChildrenOf($('#attrLinkVerb_'+ev.target.ordinal)).remove();
			       													$('#attrLinkVerbSelector_'+ev.target.ordinal).show();
			       												}
			       												else
			       												{
			       													$('#attrLinkVerb_'+ev.target.ordinal).append(textNode);
			       													$('#attrLinkVerbSelector_'+ev.target.ordinal).hide();
			       												}
			       											 });
        						        			
        						        			td = tr.insertCell(-1);
        						        			td.id = "attrLinkVerb_"+count;
        						        			td.className = "attrLinkVerbs";
        						        			select = document.createElement("select");
        						        			select.id = "attrLinkVerbSelector_"+count;
        						        			select.ordinal = count;
        						        			
        						        			option = document.createElement("option");
        					        				option.value = "None";
        					        				option.text = "Select a verb";
        					        				option.selected = true;
        					        				option.disabled = true;
        					        				select.appendChild(option);
        					        				for(var j=0; j<verbs.length; j++)
        								        	{
        								        		var option = document.createElement("option");
        								        		option.value = verbs[j];
        								        		option.text = verbs[j];
        								        		select.appendChild(option);
        								        	}
        					        				td.appendChild(select);
        					        				var textNode = document.createTextNode('None');
        					        				$(td).append(textNode);
        					        				$(select).hide();
        						        			
        					        				td = tr.insertCell(-1);
        						        			chkBox = document.createElement('input');
        						        			chkBox.type = 'checkbox';
        						        			chkBox.id = 'attrDocMappingAutoChkBox_'+count;
        						        			chkBox.className = 'attrDocMappingAutoChkbox';
        						        			chkBox.ordinal = count;
        						        			td.appendChild(chkBox);
        						        			chkBox.addEventListener('click', function(ev)
        							       											 {
        							       												if($(ev.target).prop('checked') == true)
        							       												{
        							       													var attrDocbtn = $('#attrDocMappingAddButton_'+ev.target.ordinal)[0];
        							       													if(isPresent(attrDocbtn.valueMappingCount) && attrDocbtn.valueMappingCount > 0)
        							       													{							
        								        													$('#removeDocumentMappingsModalYesButton').off('click').click(function()
        								        																	{
        								        																		removeDocumentMappings(ev.target.ordinal);
        								        																		$('#attrDocMappingAddButton_'+ev.target.ordinal).prop('disabled', true);
        								        																		$('#removeDocumentMappingsModal').modal('hide');
        								        																	});
        								        													
        								        													$('#removeDocumentMappingsModalNoButton').off('click').click(function() 
        								        																	{
        								        																		$(ev.target).prop('checked', false);
        								        																		$('#removeDocumentMappingsModal').modal('hide');
        								        																	});
        								        													$('#removeDocumentMappingsModal').modal('show');
        							       													}else
        							       														$('#attrDocMappingAddButton_'+ev.target.ordinal).prop('disabled', true);
        							       									
        							       												}
        							       												else
        							       													$('#attrDocMappingAddButton_'+ev.target.ordinal).prop('disabled', false);
        							       											 });
        					        				/*select = document.createElement("select");
        						        			select.id = "attrTermSelector_"+count;
        						        			td = tr.insertCell(-1);
        						        			td.id = "attrTerm_"+count;
        						        			td.className = "attrTerms";
        						        			
        						        			option = document.createElement("option");
        					        				option.value = "None";
        					        				option.text = "None";
        					        				option.selected = true;
        					        				option.disabled = true;
        					        				select.appendChild(option);
        					        				
        						        			td.appendChild(select);*/
        						        			count++;
        						        		}
        						        		tbl.entryCount = count;
        						        		tbl.token = attrInfo.token;
        						        		$('.attrLinkChkbox').prop('disabled', true);
        			        				}
        			        			});
						        		
        			        		  },
        			        error: function(jqXHR, textStatus, errorThrown) 
        			        	   {
        			        	      alert("The following error occured: " + textStatus, errorThrown);
        			        	   }
        				}); 
     		        	applyFuncs(postProcessingFuncs, attrInfo);
        		},
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
}

function removeValueMappings(ordinal)
{
	$('#attrValueMappingAddButton_'+ordinal)[0].valueMappingCount = undefined;
	$('#attrValueMappingTable_'+ordinal).closest('tr').remove();
}

function removeDocumentMappings(ordinal)
{
	$('#attrDocMappingAddButton_'+ordinal)[0].valueMappingCount = undefined;
	$('#attrDocMappingTable_'+ordinal).closest('tr').remove();
}

function createValueMappingTable(ordinal)
{
	var tbl = document.createElement('table');
	tbl.id = 'attrValueMappingTable_'+ordinal;
	tbl.className = "table table-striped table-hover";
	
	var ths = ['Name', 'Value', 'Taxonomy', 'Term', 'Map Value to Term Name', ''];
	var thead = tbl.createTHead();
	var tr = thead.insertRow(-1);
	
	var th;
	
	for(var i=0; i<ths.length; i++)
	{
		th = document.createElement('th');
		th.innerHTML = ths[i];
		tr.appendChild(th);
	}
	
	var tbody = document.createElement('tbody');
	tbl.appendChild(tbody);
	
	return tbl;
}

function createDocumentMappingTable(ordinal)
{
	var tbl = document.createElement('table');
	tbl.id = 'attrDocMappingTable_'+ordinal;
	tbl.className = "table table-striped table-hover";
	
	var ths = ['Name', 'Value', 'Taxonomy', 'Term', 'Document', ''];
	var thead = tbl.createTHead();
	var tr = thead.insertRow(-1);
	
	var th;
	
	for(var i=0; i<ths.length; i++)
	{
		th = document.createElement('th');
		th.innerHTML = ths[i];
		tr.appendChild(th);
	}
	
	var tbody = document.createElement('tbody');
	tbl.appendChild(tbody);
	
	return tbl;
}


function addAttributeValueMapping(ev, tbl, taxons)
{
	tbl = $(tbl).find('tbody')[0];
	var ordinal = ev.target.ordinal;
	
	if(!isPresent(ev.target.valueMappingCount))
		ev.target.valueMappingCount = 0;
	
	var count = ev.target.valueMappingCount;
	
	var pos = 1;
	for(var i=0; i<ordinal; i++)
	{
		pos++;
		var mappings = $('#attrValueMappingAddButton_'+i)[0].valueMappingCount;
		if(isPresent(mappings) && mappings > 0) pos += 1;
		mappings = $('#attrDocMappingAddButton_'+i)[0].valueMappingCount;
		if(isPresent(mappings) && mappings > 0) pos += 1;
	}
	if(ev.target.valueMappingCount == 0)
	{
		tr = tbl.insertRow(pos);
		td = tr.insertCell(-1);
		td.className = "valueMappingContainer";
		td = tr.insertCell(-1);
		td.className = "valueMappingContainer";
		td.colSpan = "5";
		td.appendChild(createValueMappingTable(ordinal));
	}
	
	tbl = $('#attrValueMappingTable_'+ordinal + ' tbody')[0];
	tbl.isComplete = false;
	pos = ev.target.valueMappingCount;
	tr = tbl.insertRow(pos);
	tr.valueMappingIndex = count;
	
	td = tr.insertCell(-1);
	td.id = "valueMappingAttrName_"+ordinal+"_"+count;
	td.className = "mappingAttrNames";
	td.innerHTML = $('#attrName_'+ordinal).html();
		
	td = tr.insertCell(-1);
	td.id = "valueMappingAttrValue_"+ordinal+"_"+count;
	td.className = "mappingAttrValues";
	
	var input = document.createElement("select");
	input.id = "valueMappingAttrValueSelector_"+ordinal+"_"+count;
	td.appendChild(input);
	
	$(input).change(function(innerEv)
			{
				var completeMappingCount = $('#attrTbl')[0].completeMappingCount;
				var existingValues = {};
				$(innerEv.target).closest('table').find('.valueMappingAttrValues > select').each(
						function()
						{
							existingValues[$(this).val()] = true;
						});
    			var complete = true;
				for(var i=0; i < ev.target.attributeValues.length; i++)
				{
					if(!isPresent(existingValues[ev.target.attributeValues[i]]))
					{
						complete = false;
						break;
					}
				}	
				if(complete == true)
				{
					$('#attrTbl')[0].completeMappingCount++;
					tbl.isComplete = true;
					$('.attrLinkChkbox').prop('disabled', false);
				}else
				{
					if(tbl.isComplete == true && isPresent(completeMappingCount) && completeMappingCount == 1)
					{
						var checkedLinks = $('.attrLinkChkbox:checked');
							for(var i=0; i< checkedLinks.length; i++)
								$(checkedLinks[i]).click();
						$('.attrLinkChkbox').prop('disabled', true);
					}
					if(tbl.isComplete == true)
						$('#attrTbl')[0].completeMappingCount--;
					tbl.isComplete = false;
				}
			}
	);

	td = tr.insertCell(-1);
	td.id = "valueMappingAttrTaxon_"+ordinal+"_"+count;
	td.className = "mappingAttrTaxons";
	var select = document.createElement("select");
	select.id = "valueMappingAttrTaxonSelector_"+ordinal+"_"+count;
	select.ordinal = ordinal;
	select.valueMappingIndex = count;
	select.addEventListener('change', populateTerms);
	
	option = document.createElement("option");
	option.value = "None";
	option.text = "None";
	option.selected = true;
	select.appendChild(option);
	for(var j=0; j<taxons.length; j++)
	{
		var option = document.createElement("option");
		option.value = taxons[j];
		option.text = taxons[j];
		select.appendChild(option);
	}
	td.appendChild(select);
	
	select = document.createElement("select");
	select.id = "valueMappingAttrTermSelector_"+ordinal+"_"+count;
	td = tr.insertCell(-1);
	td.id = "valueMappingAttrTerm_"+ordinal+"_"+count;
	td.className = "mappingAttrTerms";
	
	option = document.createElement("option");
	option.value = "None";
	option.text = "None";
	option.selected = true;
	option.disabled = true;
	select.appendChild(option);
	
	td.appendChild(select);
	
	var sel = document.getElementById("valueMappingAttrValueSelector_"+ordinal+"_"+count);
	if(!isPresent(ev.target.attributeValues))
	{
		req = "token=" + $('#attrTbl')[0].token;
		req += "&charset=" + $('#listBoxCharset option:selected').val();
		req += "&attribute=" + $('#attrName_'+ordinal).html();
		
		$(sel).prop('disabled', true);
		populateSelector(sel, [], 'None', 'Loading...', true, false);
		
		$.ajax({ 
	        url : "import/attributeValues", 
	        type : "post", 
	        data : req,
	        success : function(response) 
	        		  { 
	        			$(sel).prop('disabled', false);
	        			populateSelector(sel, response);
	        			ev.target.attributeValues = response;
	        			if(ev.target.attributeValues.length == 1 && $(tbl).find('tr').length == 1)
	        			{
	        				//if a single value exists, it is by default selected and therefore the corresponding mapping is complete
	        				$('#attrTbl')[0].completeMappingCount++;
	        				tbl.isComplete = true;
	        				$('.attrLinkChkbox').prop('disabled', false);
	        			}
	        		  },
	        		  error : function(jqXHR, textStatus, errorThrown) 
	          		 {
	  	   			   alert("The following error occured: " + textStatus, errorThrown);
	          		 }
	        }); 		  
	}else
	{
		populateSelector(sel, ev.target.attributeValues);
	
		if(ev.target.attributeValues.length == 1 && $(tbl).find('tr').length == 1)
		{
			//if a single value exists, it is by default selected and therefore the corresponding mapping is complete
			$('#attrTbl')[0].completeMappingCount++;
			tbl.isComplete = true;
			$('.attrLinkChkbox').prop('disabled', false);
		}
	}
	
	td = tr.insertCell(-1);
	
	var chkBox = document.createElement('input');
	chkBox.type = 'checkbox';
	chkBox.id = 'valueMappingMapValueChkBox_'+ordinal+"_"+count;
	td.appendChild(chkBox);
	
	td = tr.insertCell(-1);
	var btn = document.createElement('button');
	btn.id = 'valueMappingDeleteButton_'+ordinal+"_"+count;
	btn.type = 'button';
	btn.className = 'btn btn-sm btn-default';
	btn.innerHTML = 'Delete';
	btn.addEventListener('click', function(innerEv)
						 {
							deleteAttributeValueMapping(innerEv, ordinal, ev.target);
						 });
	td.appendChild(btn);
	ev.target.valueMappingCount++;
}

function addAttributeDocumentMapping(ev, tbl, taxons)
{
	tbl = $(tbl).find('tbody')[0];
	var ordinal = ev.target.ordinal;
	
	if(!isPresent(ev.target.valueMappingCount))
		ev.target.valueMappingCount = 0;
	
	var count = ev.target.valueMappingCount;
	
	var pos = 1;
	for(var i=0; i<ordinal; i++)
	{
		pos++;
		var mappings = $('#attrValueMappingAddButton_'+i)[0].valueMappingCount;
		if(isPresent(mappings) && mappings > 0) pos += 1;
		mappings = $('#attrDocMappingAddButton_'+i)[0].valueMappingCount;
		if(isPresent(mappings) && mappings > 0) pos += 1;
	}
	if(ev.target.valueMappingCount == 0)
	{
		tr = tbl.insertRow(pos);
		td = tr.insertCell(-1);
		td.className = "valueMappingContainer";
		td = tr.insertCell(-1);
		td.className = "valueMappingContainer";
		td.colSpan = "5";
		td.appendChild(createDocumentMappingTable(ordinal));
	}
	
	tbl = $('#attrDocMappingTable_'+ordinal + ' tbody')[0];
	tbl.isComplete = false;
	pos = ev.target.valueMappingCount;
	tr = tbl.insertRow(pos);
	tr.valueMappingIndex = count;
	
	td = tr.insertCell(-1);
	td.id = "docMappingAttrName_"+ordinal+"_"+count;
	td.className = "mappingAttrNames";
	td.innerHTML = $('#attrName_'+ordinal).html();
		
	td = tr.insertCell(-1);
	td.id = "docMappingAttrValue_"+ordinal+"_"+count;
	td.className = "mappingAttrValues";
	
	var input = document.createElement("select");
	input.id = "docMappingAttrValueSelector_"+ordinal+"_"+count;
	td.appendChild(input);

	td = tr.insertCell(-1);
	td.id = "docMappingAttrTaxon_"+ordinal+"_"+count;
	td.className = "mappingAttrTaxons";
	var select = document.createElement("select");
	select.id = "docMappingAttrTaxonSelector_"+ordinal+"_"+count;
	select.ordinal = ordinal;
	select.valueMappingIndex = count;
	select.addEventListener('change', populateTerms);
	
	option = document.createElement("option");
	option.value = "None";
	option.text = "None";
	option.selected = true;
	select.appendChild(option);
	for(var j=0; j<taxons.length; j++)
	{
		var option = document.createElement("option");
		option.value = taxons[j];
		option.text = taxons[j];
		select.appendChild(option);
	}
	td.appendChild(select);
	
	select = document.createElement("select");
	select.id = "docMappingAttrTermSelector_"+ordinal+"_"+count;
	td = tr.insertCell(-1);
	td.id = "docMappingAttrTerm_"+ordinal+"_"+count;
	td.className = "mappingAttrTerms";
	
	option = document.createElement("option");
	option.value = "None";
	option.text = "None";
	option.selected = true;
	option.disabled = true;
	select.appendChild(option);
	
	td.appendChild(select);
	
	var sel = document.getElementById("docMappingAttrValueSelector_"+ordinal+"_"+count);
	if(!isPresent(ev.target.attributeValues))
	{
		req = "token=" + $('#attrTbl')[0].token;
		req += "&charset=" + $('#listBoxCharset option:selected').val();
		req += "&attribute=" + $('#attrName_'+ordinal).html();
		
		$(sel).prop('disabled', true);
		populateSelector(sel, [], 'None', 'Loading...', true, false);
		
		$.ajax({ 
	        url : "import/attributeValues", 
	        type : "post", 
	        data : req,
	        success : function(response) 
	        		  { 
	        			$(sel).prop('disabled', false);
	        			populateSelector(sel, response);
	        			ev.target.attributeValues = response;
	        		  },
	        		  error : function(jqXHR, textStatus, errorThrown) 
	          		 {
	  	   			   alert("The following error occured: " + textStatus, errorThrown);
	          		 }
	        }); 		  
	}else
		populateSelector(sel, ev.target.attributeValues);
	
	td = tr.insertCell(-1);
	td.id = "docMappingDoc_"+ordinal+"_"+count;
	select = document.createElement('select');
	select.id = 'docMappingDocSelector_'+ordinal+"_"+count;
	if(!isPresent(window.retrievedDocs))
	{
		$.ajax({ 
	        url : "documents/systemDocuments", 
	        type : "post", 
	        data : req,
	        success : function(response) 
	        		  { 
	        			populateDocumentSelector(select, response);
	        			window.retrievedDocs = response;
	        		  },
	        		  error : function(jqXHR, textStatus, errorThrown) 
	          		 {
	  	   			   alert("The following error occured: " + textStatus, errorThrown);
	          		 }
	        });
	}else
		populateDocumentSelector(select, window.retrievedDocs);
	td.appendChild(select);
	
	td = tr.insertCell(-1);
	var btn = document.createElement('button');
	btn.id = 'docMappingDeleteButton_'+ordinal+"_"+count;
	btn.type = 'button';
	btn.className = 'btn btn-sm btn-default';
	btn.innerHTML = 'Delete';
	btn.addEventListener('click', function(innerEv)
						 {
							deleteAttributeDocumentMapping(innerEv, ordinal, ev.target);
						 });
	td.appendChild(btn);
	ev.target.valueMappingCount++;
}

function populateDocumentSelector(select, documents)
{
	var option;
	
	$(select).empty();
	
	for(var i=0; i<documents.length; i++)
	{
		option = document.createElement("option");
		option.value = documents[i].id;
		option.text = documents[i].name;
		select.appendChild(option);
	}
}

function deleteAttributeValueMapping(ev, ordinal, addValueMappingButton)
{
	var mappingTable = $(ev.target).closest('table').find('tbody')[0];
	var tr = $(ev.target).closest('tr');
	var valueMappingIndex = tr[0].valueMappingIndex;
	$(tr).remove();
	
	for(var i=valueMappingIndex+1; i<addValueMappingButton.valueMappingCount; i++)
	{
		$('#valueMappingAttrName_'+ordinal+'_'+i).closest('tr')[0].valueMappingIndex--;
		
		var attrCell = $('#valueMappingAttrName_'+ordinal+'_'+i)[0];
		attrCell.id = 'valueMappingAttrName_'+ordinal+'_'+(i-1);
		
		attrCell = $('#valueMappingAttrValue_'+ordinal+'_'+i)[0];
		attrCell.id = 'valueMappingAttrValue_'+ordinal+'_'+(i-1);
		var attrControl = $(attrCell).find('select')[0];
		attrControl.id = 'valueMappingAttrValueSelector_'+ordinal+'_'+(i-1);
		
		attrCell = $('#valueMappingAttrTaxon_'+ordinal+'_'+i)[0];
		attrCell.id = 'valueMappingAttrTaxon_'+ordinal+'_'+(i-1);
		attrControl = $(attrCell).find('select')[0];
		attrControl.id = 'valueMappingAttrTaxonSelector_'+ordinal+'_'+(i-1);
		attrControl.valueMappingIndex--;
		
		attrCell = $('#valueMappingAttrTerm_'+ordinal+'_'+i)[0];
		attrCell.valueMappingIndex--;
		attrCell.id = 'valueMappingAttrTerm_'+ordinal+'_'+(i-1);
		attrControl = $(attrCell).find('select')[0];
		attrControl.id = 'valueMappingAttrTermSelector_'+ordinal+'_'+(i-1);
		
		attrControl =  $('#valueMappingMapValueChkBox_'+ordinal+'_'+i)[0];
		attrControl.id = 'valueMappingMapValueChkBox_'+ordinal+'_'+(i-1);
		attrControl.valueMappingIndex--;
		
		attrControl =  $('#valueMappingDeleteButton_'+ordinal+'_'+i)[0];
		attrControl.id = 'valueMappingDeleteButton_'+ordinal+'_'+(i-1);
		attrControl.valueMappingIndex--;
		
	}
	addValueMappingButton.valueMappingCount--;
	
	if(addValueMappingButton.valueMappingCount == 0)
		$('#attrValueMappingTable_'+ordinal).closest('tr').remove();
	
	if(mappingTable.isComplete == true)
	{
		mappingTable.isComplete = false;
		
		var completeMappingCount = $('#attrTbl')[0].completeMappingCount;
		if(isPresent(completeMappingCount) && completeMappingCount == 1)
		{
			var checkedLinks = $('.attrLinkChkbox:checked');
				for(var i=0; i< checkedLinks.length; i++)
					$(checkedLinks[i]).click();
			$('.attrLinkChkbox').prop('disabled', true);
		}
		$('#attrTbl')[0].completeMappingCount--;
		mappingTable.isComplete = false;
	}
}

function deleteAttributeDocumentMapping(ev, ordinal, addDocMappingButton)
{
	var tr = $(ev.target).closest('tr');
	var valueMappingIndex = tr[0].valueMappingIndex;
	$(tr).remove();
	
	var cells = ['docMappingAttrValue', 'docMappingAttrTaxon', 'docMappingAttrTerm', 'docMappingDoc'];
	
	for(var i=valueMappingIndex+1; i<addDocMappingButton.valueMappingCount; i++)
	{
		$('#docMappingAttrName_'+ordinal+'_'+i).closest('tr')[0].valueMappingIndex--;
		
		var attrCell = $('#docMappingAttrName_'+ordinal+'_'+i)[0];
		attrCell.id = 'docMappingAttrName_'+ordinal+'_'+(i-1);
		var attrControl;
		
		for(var j=0; j<cells.length; j++)
		{
			attrCell = $('#'+cells[j]+'_'+ordinal+'_'+i)[0];
			attrCell.id = cells[j]+'_'+ordinal+'_'+(i-1);
			attrControl = $(attrCell).find('select')[0];
			attrControl.id = cells[j]+'Selector_'+ordinal+'_'+(i-1);
		}
		$('#docMappingAttrTaxonSelector_'+ordinal+'_'+(i-1))[0].valueMappingIndex--;
		$('#docMappingAttrTerm_'+ordinal+'_'+(i-1))[0].valueMappingIndex--;
		$('#docMappingDoc_'+ordinal+'_'+(i-1))[0].valueMappingIndex--;
		
		attrControl =  $('#docMappingDeleteButton_'+ordinal+'_'+i)[0];
		attrControl.id = 'docMappingDeleteButton_'+ordinal+'_'+(i-1);
		attrControl.valueMappingIndex--;
		
	}
	addDocMappingButton.valueMappingCount--;
	
	if(addDocMappingButton.valueMappingCount == 0)
		$('#attrDocMappingTable_'+ordinal).closest('tr').remove();
}

function requestImport(postProcessingFuncs, replace, merge)
{
	var importData = {};
	var attrTable = $('#attrTbl')[0];
	importData.crs = $('#listBoxCRS option:selected').val();
	importData.token = attrTable.token;
	importData.dbfCharset = $('#listBoxCharset option:selected').val();
	importData.forceLonLat = $('#checkBoxForceLonLat').prop('checked');
	var taxonomyTermTaxonomy = $('#listBoxTaxonomy option:selected').val();
	if(taxonomyTermTaxonomy != 'None') importData.taxonomyTermTaxonomy = taxonomyTermTaxonomy;
	var taxonomyTerm = $('#listBoxTerm option:selected').val();
	if(taxonomyTerm != 'None') importData.taxonomyTerm = taxonomyTerm;
	var boundaryTermTaxonomy = $('#listBoxBoundaryTaxonomy option:selected').val();
	if(boundaryTermTaxonomy != 'None') importData.boundaryTermTaxonomy = boundaryTermTaxonomy;
	var boundaryTerm = $('#listBoxBoundaryTerm option:selected').val();
	if(boundaryTerm != 'None') importData.boundaryTerm = boundaryTerm;
	
	var checkedLinks = $('.attrLinkChkbox:checked');
	
	var cnt = 0;
	for(var i=0; i<attrTable.entryCount; i++)
	{
		if(i==0) importData.attributeConfig = [];
		importData.attributeConfig.push({});
		importData.attributeConfig[cnt].name = $('#attrName_'+i).html();
		importData.attributeConfig[cnt].type = $('#attrTypeSelector_'+i + ' option:selected').val();
		importData.attributeConfig[cnt].store = $('#attrStoreChkBox_'+i).prop('checked');
		importData.attributeConfig[cnt].presentable = $('#attrPresentableChkBox_'+i).prop('checked');
		var val = $('#attrTaxonSelector_'+i + ' option:selected').val();
		if(val != 'None') importData.attributeConfig[cnt].taxonomy = val;
		
		//else importData.attributeConfig[i].taxonomy="";
		/*val = $('#attrTermSelector_'+i + ' option:selected').val();
		if(val != 'None') importData.attributeConfig[i].term = val;*/ //obsolete: attribute mappings are now allowed to be only taxonomies
		//else importData.attributeConfig[i].term = "";
		var valueMappingCount = $('#attrValueMappingAddButton_'+i)[0].valueMappingCount;
		if(!valueMappingCount)
		{
			if($('#attrValueMappingAutoChkBox_'+i).is(':checked'))
			{
				importData.attributeConfig[cnt].autoValueMapping = true;
				if(checkedLinks.length == 0) //TODO should also check if taxonomy is geographic
				{
					var parentVal = $('#attrParentTaxonSelector_'+i + ' option:selected').val();
					if(parentVal == 'None')
					{
						alert("Please select the taxonomy which contains the parent terms of all automatically created terms");
						return;
					}
				}
				if(val == 'None')
				{
					alert("Automatic value mapping requires that you specify a taxonomy to which all automatically created terms will belong");
					return;
				}
				importData.attributeConfig[cnt].termParentTaxonomy = parentVal;
			}
		}
		
		var docMappingCount = $('#attrDocMappingAddButton_'+i)[0].valueMappingCount;
		if(!docMappingCount)
		{
			if($('#attrDocMappingAutoChkBox_'+i).is(':checked'))
			{
				importData.attributeConfig[cnt].autoDocumentMapping = true;
				if(val == 'None')
				{
					alert("Automatic document mapping requires that you specify a taxonomy to which all automatically created terms will belong");
					return;
				}
			}
		}
		
		if($('#attrValueMappingLinkChkBox_'+i).is(':checked'))
		{
			val = $('#attrLinkVerbSelector_'+i + ' option:selected').val();
			if(val != 'None')
				importData.attributeConfig[cnt].linkVerb = val;
		}
		
		cnt++;
		var attributeConfigs = {};
		if(isPresent(valueMappingCount))
		{
			for(var j=0; j<valueMappingCount; j++)
			{
				var mappingKey =  $('#valueMappingAttrValueSelector_'+i+"_"+j + ' option:selected').val();
				if(isPresent(attributeConfigs[mappingKey]))
				{
					alert("Non-unique value mapping");
					return;
				}
				attributeConfigs[mappingKey] = {};
				attributeConfigs[mappingKey].name = $('#valueMappingAttrName_'+i+"_"+j).html();
				attributeConfigs[mappingKey].value = mappingKey;

				val = $('#valueMappingAttrTaxonSelector_'+i+"_"+j + ' option:selected').val();
				if(val == 'None') 
				{
					alert("Attribute value mappings correspond to taxonomy terms. Please define taxonomy");
					return;
				}
				attributeConfigs[mappingKey].taxonomy = val;
				
				val = $('#valueMappingAttrTermSelector_'+i+"_"+j + ' option:selected').val();
				if(val == 'None')
				{
					alert("Attribute value mappings correspond to taxonomy terms. Please define term");
					return;
				}
				attributeConfigs[mappingKey].term = val;
				attributeConfigs[mappingKey].mapValue = $('#valueMappingMapValueChkBox_'+i+"_"+j).prop('checked');
			}
		}
		
		if(isPresent(docMappingCount))
		{
			var docMappingKeys = {};
			for(var j=0; j<docMappingCount; j++)
			{
				var mappingKey =  $('#docMappingAttrValueSelector_'+i+"_"+j + ' option:selected').val();
				if(isPresent(docMappingKeys[mappingKey]))
				{
					alert("Non-unique document mapping");
					return;
				}
				docMappingKeys[mappingKey] = {};
				var presentValueMapping = attributeConfigs[mappingKey];
				if(!isPresent(presentValueMapping))
					attributeConfigs[mappingKey] = {};
				attributeConfigs[mappingKey].name = $('#docMappingAttrName_'+i+"_"+j).html(); //same as that of corresponding value mapping, if exists
				attributeConfigs[mappingKey].value = mappingKey; //same as that of corresponding value mapping, if exists
				
				val = $('#docMappingAttrTaxonSelector_'+i+"_"+j + ' option:selected').val();
				if(val == 'None') 
				{
					alert("Attribute document mappings correspond to taxonomy terms. Please define taxonomy");
					return;
				}
				if(isPresent(presentValueMapping) && val != presentValueMapping.taxonomy)
				{
					alert("Taxonomies of corresponding value and document mappings must be the same");
					return;
				}
				attributeConfigs[mappingKey].taxonomy = val;
				
				val = $('#docMappingAttrTermSelector_'+i+"_"+j + ' option:selected').val();
				if(val == 'None')
				{
					alert("Attribute document mappings correspond to taxonomy terms. Please define term");
					return;
				}
				if(isPresent(presentValueMapping) && val != presentValueMapping.term)
				{
					alert("Terms of corresponding value and document mappings must be the same");
					return;
				}
				attributeConfigs[mappingKey].term = val;
				
				attributeConfigs[mappingKey].document = $('#docMappingDoc_'+i+"_"+j + ' option:selected').val();
			}
		}
		
		var attrCfgKeys = Object.keys(attributeConfigs);
		for(var j=0; j<attrCfgKeys.length; j++)
		{
			importData.attributeConfig.push({});
			var currAttributeConfig = importData.attributeConfig[cnt];
			var currAttrCfg = attributeConfigs[attrCfgKeys[j]];
			currAttributeConfig.name = currAttrCfg.name;
			currAttributeConfig.value = currAttrCfg.value;
			currAttributeConfig.taxonomy = currAttrCfg.taxonomy;
			currAttributeConfig.term = currAttrCfg.term;
			if(isPresent(currAttrCfg.mapValue))
				currAttributeConfig.mapValue = currAttrCfg.mapValue;
			if(isPresent(currAttrCfg.document))
				currAttributeConfig.document = currAttrCfg.document;
			cnt++;
		}
	}
	
	importData.replace = replace;
	importData.merge = merge;
	 	
	var req = JSON.stringify(importData);
	$('#loadingModal').modal({backdrop: 'static', keyboard: false});
	$.ajax({ 
        url : "import/importData", 
        type : "post", 
        data : req,
        dataType: 'json',
        contentType: "application/json",
        success : function(response) 
        		  { 
        			$('#loadingModal').modal('hide');
        			if(response.status == 'Failure')
        			{
        				alert("An error has occurred: " + response.message);
        				return;
        			}
        			else if(response.status == 'Existing')
        			{
        				window.shapeImportInfo = {};
    					window.shapeImportInfo.layerName = response.message;
    					window.shapeImportInfo.bounds = response.bounds;
    					if(isPresent(importData.boundaryTermTaxonomy) && isPresent(importData.boundaryTerm))
    					{
    						window.shapeImportInfo.boundaryTermTaxonomy = importData.boundaryTermTaxonomy;
    						window.shapeImportInfo.boundaryTerm = importData.boundaryTerm;
    					}
    						
        				$('#replaceMergeModalVisualization').hide();
        				$('#replaceMergeModal').modal({backdrop: 'static', keyboard: false});
        			}
        			else
        			{
        				window.shapeImportInfo = {};
    					window.shapeImportInfo.layerName = response.message;
    					window.shapeImportInfo.bounds = response.bounds;
    					if(isPresent(importData.boundaryTermTaxonomy) && isPresent(importData.boundaryTerm))
    					{
    						window.shapeImportInfo.boundaryTermTaxonomy = importData.boundaryTermTaxonomy;
    						window.shapeImportInfo.boundaryTerm = importData.boundaryTerm;
    					}
    					
        				applyFuncs(postProcessingFuncs, response);
        			}
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
         		 {
        	       $('#loadingModal').modal('hide');
 	   			   alert("The following error occured: " + textStatus, errorThrown);
         		 },
        complete : function(jqXHR, textStatus)
        		   {
        			 
        		   }
       }); 	
        	
}

function populateTerms(ev)
{
	var select = $(ev.target).closest('tr').find('.mappingAttrTerms select')[0];
	$(select).empty();
	var taxonomy = $(ev.target).find('option:selected').val(); //get selected taxonomy from dropdown
	option = document.createElement("option");
	option.value = "None";
	option.text = "None";
	option.selected = true;
	select.appendChild(option);
	
	if(taxonomy == 'None') return;
	
	var req = "taxonomy="+taxonomy;
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

function disableActionControls()
{
	$('#importButton').prop('disabled', true);
	$('#analyzeButton').prop('disabled', true);
}

function enableActionControls()
{
	$('#importButton').prop('disabled', false);
	$('#analyzeButton').prop('disabled', false);
}