function showPresentation(data)
{
	enableZMaxIndex();
	
	//createSearchForm(data);
	createAddStyleButton();
	createAddThemeButton();
	createAddLayerConfigButton();
	var stylesCon = document.getElementById('styleTable');
	$(stylesCon).hide();
	
	var themeCon = document.getElementById('themeTable');
	$(themeCon).hide();
    
    var editStyleCon = document.getElementById("editStyleForm");
    $(editStyleCon).hide();
    
    createEditStyleForm();
    createModalAddStyleForm();
    createModalAddThemeForm();
    createAdminMenu(data);
	setPrimaryMenuButton("btnPresentation");
	enableActionControls();
	
	createStyleTable(data, true, [{func: createStyleTable, args:[data, false, 
	                        [{func:createThemeTable, args:[data, true, 
	                                    [{func:createThemeTable, args:[data, false, [
	                                         {func: function() { createLayerConfigTable(data, null, []);}, args: []}]]}]]}]]}]);
	
	$(stylesCon).show();
	$(themeCon).show();
	
	$('#presentation-nav li a[href="#presentation-styles"]').click();
}

function requestStyleInfo(postProcessingFuncs)
{
	$.ajax({ 
        url : "presentation/listStyles", 
        type : "post", 
        success : function(styleInfo) 
        		  { 
		        	applyFuncs(postProcessingFuncs, styleInfo);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
}


function requestThemeInfo(postProcessingFuncs)
{
	$.ajax({ 
        url : "presentation/listThemes", 
        type : "post", 
        success : function(themeInfo) 
        		  { 
		        	applyFuncs(postProcessingFuncs, themeInfo);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
}

function createAddStyleButton()
{
	var input = document.getElementById('addStyleButton');
	input.addEventListener('click', function()
			   {
				 $('#addStyleModal').modal();
			   }
	 );
}

function createAddThemeButton()
{
	var input = document.getElementById('addThemeButton');
	input.addEventListener('click', function()
			   {
				 $('#addThemeModal').modal();
			   }
	 );
}

function createAddLayerConfigButton()
{
	var input = document.getElementById('addLayerConfigButton');
	input.addEventListener('click', function()
			   {
					$.ajax({ 
				        url : "presentation/listStyles", 
				        type : "post", 
				        success : function(styleInfo) 
				        		  { 
						        	$.ajax({ 
								        url : "presentation/listLayers", 
								        type : "post",
								        dataType : "json",
								        contentType : "application/json",
								        	
								        success : function(layers) 
								        		  { 
										        	 var theme = $('#addLayerConfigButton')[0].themeName;
												     $.ajax({ 
												        url : "presentation/getThemeStyles", 
												        type : "post",
												        data: "theme=" + theme,
												        dataType : "json",
												        contentType : "application/json",
												        	
												        success : function(styles) 
												        		  { 
														        	var tbl = $('#layerConfigTbl')[0];
																	var tbody = $(tbl).find('tbody')[0];
																	if(!isPresent(tbody))
																	{
																		$(tbl).append('<tbody></tbody>');
																		tbody = $(tbl).find('tbody')[0];
																	}
																	var tr = tbody.insertRow(0);
																	if(!isPresent(tbl.entryCount))
																		tbl.entryCount = 0;
																	var count = tbl.entryCount;
																	tbl.entryCount++;
																	
																	td = tr.insertCell(-1);
																	td.id = "layerConfigTheme_"+count;
																	td.className = "layerConfigThemes";
																	td.innerHTML = isPresent(theme) ? theme : "default";
																	
																	td = tr.insertCell(-1);
																	td.id = "layerConfigLayerName_"+count;
																	td.className = "layerConfigLayerNames";
																	var select = document.createElement('select');
																	select.id = "layerConfigListBoxLayerName_"+count;
																	for(var i=0; i<layers.length; i++)
																	{
																		var found = false;
																		for(var j=0; j<styles.length; j++)
																		{
																			if(layers[i].layerName == styles[j].layerName)
																			{
																				found = true;
																				break;
																			}
																		}
																		if(!found)
																		{
																			var option = document.createElement("option");
																			option.layer = layers[i];
																			option.value = layers[i].layerName;
																			option.text = layers[i].layerName;
																			select.appendChild(option);
																		}
																	}
																	select.addEventListener('change', function(ev)
																									 {
																										var val = $('#layerConfigListBoxLayerName_'+count).find(':selected').val();
																										var l = $('#layerConfigListBoxLayerName_'+count).find(':selected')[0].layer;
																										
																										if(isPresent(l.minScale))
																											$('#layerConfigTextBoxMinScale_'+count).find('option[value="'+l.minScale+'"]').prop('selected', true);
																										if(isPresent(l.maxScale))
																											$('#layerConfigTextBoxMaxScale_'+count).find('option[value="'+l.maxScale+'"]').prop('selected', true);
																										$('#layerConfigRecordUpdateButton_'+count)[0].termId = l.termId;
																										
																									 });	
																	td.appendChild(select);
																	
																	td = tr.insertCell(-1);
																	td.id = "layerConfigStyle_"+count;
																	td.className = "layerConfigStyles";
																	select = document.createElement('select');
																	select.id = "layerConfigListBoxStyle_" + count;
																	populateSelector(select, styleInfo);
																	td.appendChild(select);
																	
																	var val = $('#layerConfigListBoxLayerName_'+count).find(':selected').val();
																	var l = $('#layerConfigListBoxLayerName_'+count).find(':selected')[0].layer;
																	
																	td = tr.insertCell(-1);
																	td.id = "layerConfigMinScale_"+count;
																	td.className = "layerConfigMinScales";
																	select = document.createElement('select');
																	select.id = "layerConfigListBoxMinScale_" + count;
																	populateScaleSelector(select);
																	td.appendChild(select);
																	if(isPresent(l.minScale))
																		$(select).find('option[value="'+l.minScale+'"]').prop('selected', true);
																	
																	td = tr.insertCell(-1);
																	td.id = "layerConfigMaxScale_"+count;
																	td.className = "layerConfigMaxScale";
																	select = document.createElement('select');
																	select.id = "layerConfigListBoxMaxScale_" + count;
																	populateScaleSelector(select);
																	td.appendChild(select);
																	if(isPresent(l.maxScale))
																		$(select).find('option[value="'+l.maxScale+'"]').prop('selected', true);
																	
																	td = tr.insertCell(-1);
																	var buttonCon = document.createElement("div");
																	var input = document.createElement("button");
																	input.id = "layerConfigRecordUpdateButton_"+count;
																	input.type = "button";
																	input.className = "btn btn-sm btn-default layerConfigRecordUpdateButtons";
																	input.value = "Update";
																	input.innerHTML = "Update";
																	input.ordinal = count;
																	input.termId = $('#layerConfigListBoxLayerName_'+count).find(':selected')[0].layer.termId;
																	input.addEventListener('click', function(ev) 
																									{ 
																										var v = $("#layerConfigListBoxLayerName_"+count).find(':selected')[0];
																										if(isPresent(v))
																										{
																											$("#layerConfigLayerName_"+count).empty();
																											$("#layerConfigLayerName_"+count).html(v.layer.layerName);
																										}
																										updateLayerConfigButtonListener(ev);							
																									}
																							);
																	buttonCon.appendChild(input);
																	td.appendChild(buttonCon);
												        		  },
											        		  error : function(jqXHR, textStatus, errorThrown) 
												           		 {
												   	   			   alert("The following error occured: " + textStatus, errorThrown);
												           		 }
										        		  });
												     },
												     error : function(jqXHR, textStatus, errorThrown) 
									           		 {
									   	   			   alert("The following error occured: " + textStatus, errorThrown);
									           		 }
												  });
				        		},
			        	        error : function(jqXHR, textStatus, errorThrown) 
				           		 {
				   	   			   alert("The following error occured: " + textStatus, errorThrown);
				           		 }
				         });
			   }
	 );
}

function createStyleTable(data, empty, postProcessingFuncs) {
	
    tbl  = document.createElement('table');
    //tbl.className = "geopolis-admin-widget geopolis-admin-widget-content";
    
    tbl.id = 'styleTbl';
    tbl.className = "table table-striped table-hover";
    
    var thead = tbl.createTHead();
 
    var td;
    
    var tr = thead.insertRow(-1);
    var th = document.createElement("th");
    th.innerHTML = "Style";
    tr.appendChild(th);
    
    th = document.createElement("th");
    
    button = document.createElement("button");
    button.className = "btn btn-default";
    button.type = "button";
    button.value = "Delete";
    button.innerHTML = "Delete";
    button.id = "styleDeleteButton";
    button.addEventListener('click', function(ev) { deleteStyles(ev, [{func: requestStyleInfo, args: [{func: populateSelector, args: [$('#editStyleFormListBoxexisting')[0], "__funcRet"]},
                                      						                                   {func: populateSelector, args: [$('#addStyleFormListBoxexisting')[0], "__funcRet"]}]}]);});
    th.appendChild(button);
    tr.appendChild(th);
    
    th = document.createElement("th");
    tr.appendChild(th);
    
    var tbody = document.createElement("tbody");
    tbl.appendChild(tbody);
    
    if(isPresent(empty) && empty == true)
    {
    	var parent = document.getElementById('styleTable');
        var old = document.getElementById('styleTbl');
        if(isPresent(old)) parent.removeChild(old);
        parent.appendChild(tbl);
        applyFuncs(postProcessingFuncs, null);
    	return tbl;
    }
    
     $.ajax({ 
        url : "presentation/listStyles", 
        type : "post", 
        success : function(styles) 
        		  { 
        			var count = 0;
	        		for(var i=0; i<styles.length; i++)
	        		{
	        			tr = tbody.insertRow(-1);
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "styleName_"+count;
	        			td.className = "styleNames";
	        			td.innerHTML = styles[i];
	        			
	        			td = tr.insertCell(-1);
	        			var checkBoxCon = document.createElement("div");
	        			var input = document.createElement("input");
	        			input.type = "checkbox";
	        			input.id = "styleRecordDeleteCheckbox_"+count;
	        			input.name = "styleRecord_"+count;
	        			input.className = "styleRecordDeleteCheckboxes";
	        			input.styleName = styles[i];
	        			if(styles[i] == "default")
	        				input.disabled = true;
	        			checkBoxCon.appendChild(input);
	        			td.appendChild(checkBoxCon);
	        			
	        			td = tr.insertCell(-1);
	        			var buttonCon = document.createElement("div");
	        			input = document.createElement("button");
	        			input.id = "styleRecordEditButton_"+count;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-default styleRecordEditButtons";
	        			input.value = "Edit";
	        			input.innerHTML = "Edit";
	        			input.styleName = styles[i];
	        			input.ordinal = count;
	        			input.addEventListener('click', editStyleButtonListener);
	        			buttonCon.appendChild(input);
	        			td.appendChild(buttonCon);
	        			
	        			count++;
	        			
	        		}
		        	tbl.entryCount = count;
		        	$('#styleTbl').dataTable().fnDestroy();
		        	var parent = document.getElementById('styleTable');
		            var old = document.getElementById('styleTbl');
		            if(isPresent(old)) parent.removeChild(old);
		            parent.appendChild(tbl);
		           //$(document).ready(function(){$('#userTbl').dataTable({"bSort" : false});});
		            $('#styleTbl').dataTable();
		            
		            applyFuncs(postProcessingFuncs, styles);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
}

function createThemeTable(data, empty, postProcessingFuncs) {
	
    tbl  = document.createElement('table');
    //tbl.className = "geopolis-admin-widget geopolis-admin-widget-content";
    
    tbl.id = 'themeTbl';
    tbl.className = "table table-striped table-hover";
    
    var thead = tbl.createTHead();
 
    var td;
    
    var tr = thead.insertRow(-1);
    var th = document.createElement("th");
    th.innerHTML = "Theme";
    tr.appendChild(th);
    
    th = document.createElement("th");
    
    button = document.createElement("button");
    button.className = "btn btn-default";
    button.type = "button";
    button.value = "Delete";
    button.innerHTML = "Delete";
    button.id = "themeDeleteButton";
    button.addEventListener('click', function(ev) { deleteThemes(ev, [{func: requestThemeInfo, args: [{func: populateSelector, args: [$('#editThemeFormListBoxtemplate')[0], "__funcRet", "None", "--None--", false]},
                                      						                                   {func: populateSelector, args: [$('#addThemeFormListBoxtemplate')[0], "__funcRet", "None", "--None--", false]}]}]);});
    th.appendChild(button);
    tr.appendChild(th);
    
    th = document.createElement("th");
    tr.appendChild(th);
    
    var tbody = document.createElement("tbody");
    tbl.appendChild(tbody);
    
    if(isPresent(empty) && empty == true)
    {
    	var parent = document.getElementById('themeTable');
        var old = document.getElementById('themeTbl');
        if(isPresent(old)) parent.removeChild(old);
        parent.appendChild(tbl);
        applyFuncs(postProcessingFuncs, null);
    	return tbl;
    }
    
     $.ajax({ 
        url : "presentation/listThemes", 
        type : "post", 
        success : function(themes) 
        		  { 
        			var count = 0;
	        		for(var i=0; i<themes.length; i++)
	        		{
	        			tr = tbody.insertRow(-1);
	        			
	        			td = tr.insertCell(-1);
	        			td.id = "themeName_"+count;
	        			td.className = "themeNames";
	        			td.innerHTML = themes[i];
	        			
	        			td = tr.insertCell(-1);
	        			var checkBoxCon = document.createElement("div");
	        			var input = document.createElement("input");
	        			input.type = "checkbox";
	        			input.id = "themeRecordDeleteCheckbox_"+count;
	        			input.name = "themeRecord_"+count;
	        			input.className = "themeRecordDeleteCheckboxes";
	        			input.themeName = themes[i];
	        			if(themes[i] == 'default')
	        				input.disabled = 'disabled';
	        			checkBoxCon.appendChild(input);
	        			td.appendChild(checkBoxCon);
	        			
	        			td = tr.insertCell(-1);
	        			var buttonCon = document.createElement("div");
	        			input = document.createElement("button");
	        			input.id = "themeRecordEditButton_"+count;
	        			input.type = "button";
	        			input.className = "btn btn-sm btn-default themeRecordEditButtons";
	        			input.value = "Edit";
	        			input.innerHTML = "Edit";
	        			input.themeName = themes[i];
	        			input.ordinal = count;
	        			input.addEventListener('click', editThemeButtonListener);
	        			buttonCon.appendChild(input);
	        			td.appendChild(buttonCon);
	        			
	        			count++;
	        			
	        		}
		        	tbl.entryCount = count;
		        	$('#themeTbl').dataTable().fnDestroy();
		        	var parent = document.getElementById('themeTable');
		            var old = document.getElementById('themeTbl');
		            if(isPresent(old)) parent.removeChild(old);
		            parent.appendChild(tbl);
		           //$(document).ready(function(){$('#userTbl').dataTable({"bSort" : false});});
		            $('#themeTbl').dataTable();
		            
		            applyFuncs(postProcessingFuncs, themes);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
}

function createLayerConfigTable(data, theme, postProcessingFuncs) {
	
    tbl  = document.createElement('table');
    //tbl.className = "geopolis-admin-widget geopolis-admin-widget-content";
    
    tbl.id = 'layerConfigTbl';
    tbl.className = "table table-striped table-hover";
    
    var thead = tbl.createTHead();
 
    var td;
    
    var tr = thead.insertRow(-1);
    var th = document.createElement("th");
    th.innerHTML = "Theme";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Layer";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Style";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Min Scale";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = "Max Scale";
    tr.appendChild(th);
    
    th = document.createElement("th");
    tr.appendChild(th);
    
    var tbody = document.createElement("tbody");
    tbl.appendChild(tbody);
    
    $.ajax({ 
        url : "presentation/listStyles", 
        type : "post", 
        success : function(styleInfo) 
        		  { 
 
				     $.ajax({ 
				        url : "presentation/getThemeStyles", 
				        type : "post",
				        data:  "theme=" + theme,
				        success : function(styles) 
				        		  { 
				        			var count = 0;
					        		for(var i=0; i<styles.length; i++)
					        		{
					        			tr = tbody.insertRow(-1);
					        			
					        			td = tr.insertCell(-1);
					        			td.id = "layerConfigTheme_"+count;
					        			td.className = "layerConfigThemes";
					        			td.innerHTML = styles[i].theme;
				
					        			td = tr.insertCell(-1);
					        			td.id = "layerConfigLayerName_"+count;
					        			td.className = "layerConfigLayerNames";
					        			td.innerHTML = styles[i].layerName;
					        			
					        			td = tr.insertCell(-1);
					        			td.id = "layerConfigStyle_"+count;
					        			td.className = "layerConfigStyles";
					        			var select = document.createElement('select');
					        			select.id = "layerConfigListBoxStyle_" + count;
					        			populateSelector(select, styleInfo);
					        			td.appendChild(select);
					        			
					        			$(select).find('option[value="'+styles[i].style+'"]').prop('selected', true);
					        			
					        			td = tr.insertCell(-1);
					        			td.id = "layerConfigMinScale_"+count;
					        			td.className = "layerConfigMinScales";
					        			select = document.createElement('select');
					        			select.id = "layerConfigListBoxMinScale_" + count;
					        			populateScaleSelector(select);
					        			td.appendChild(select);
					        			
					        			if(isPresent(styles[i].minScale))
					        				$(select).find('option[value="'+styles[i].minScale+'"]').prop('selected', true);
					        			
					        			td = tr.insertCell(-1);
					        			td.id = "layerConfigMaxScale_"+count;
					        			td.className = "layerConfigMaxScale";
					        			select = document.createElement('select');
					        			select.id = "layerConfigListBoxMaxScale_" + count;
					        			populateScaleSelector(select);
					        			td.appendChild(select);
					        			
					        			if(isPresent(styles[i].maxScale))
					        				$(select).find('option[value="'+styles[i].maxScale+'"]').prop('selected', true);
					        			
					        			td = tr.insertCell(-1);
					        			var buttonCon = document.createElement("div");
					        			var input = document.createElement("button");
					        			input.id = "layerConfigRecordUpdateButton_"+count;
					        			input.type = "button";
					        			input.className = "btn btn-sm btn-default layerConfigRecordUpdateButtons";
					        			input.value = "Update";
					        			input.innerHTML = "Update";
					        			input.ordinal = count;
					        			input.termId = styles[i].termId;
					        			input.addEventListener('click', updateLayerConfigButtonListener);
					        			buttonCon.appendChild(input);
					        			td.appendChild(buttonCon);
					        			
					        			count++;
					        			
					        		}
						        	tbl.entryCount = count;
						        	var parent = document.getElementById('layerConfigTable');
						            var old = document.getElementById('layerConfigTbl');
						            if(isPresent(old)) parent.removeChild(old);
						            parent.appendChild(tbl);
						            
						            applyFuncs(postProcessingFuncs, styles);
				        		  },
				        		  
				        		  error : function(jqXHR, textStatus, errorThrown) 
				         		 {
				 	   			   alert("The following error occured: " + textStatus, errorThrown);
				         		 }
				     });
        		  },
        	        error : function(jqXHR, textStatus, errorThrown) 
           		 {
   	   			   alert("The following error occured: " + textStatus, errorThrown);
           		 }
         });
}

function populateScaleSelector(select)
{
	var options = ["None", "100", "200", "500", "1000", "2000", "5000", "10000", "20000", "50000", "100000", 
	               "200000", "300000", "400000", "500000", "1000000", "2000000", "5000000", "10000000", "20000000", "50000000"];
	var optionTexts = ["None", "1:100", "1:200", "1:500", "1:1K", "1:2K", "1:5K", "1:10K", "1:20K", "1:50K", "1:100K", 
	                   "1:200K", "1:300K", "1:400K", "1:500K", "1:1M", "1:2M", "1:5M", "1:10M", "1:20M", "1:50M"];
	$(select).empty();
		
	for(var i=0; i<options.length; i++)
	{
		option = document.createElement("option");
		option.value = options[i];
		option.text = optionTexts[i];
		select.appendChild(option);
	}
}

function createEditStyleForm()
{
	var input = document.getElementById("editStyleFormCancelButton");
	input.addEventListener('click', function(ev)
			   {
				 $('#editStyleModal').modal('hide');
				 enableActionControls();
			   }
	 );
	
	input = document.getElementById("editStyleFormSaveButton");
	input.addEventListener('click', function()
			   {
				  updateStyle($('#editStyleFormObj')[0], false, 
						  [{func: requestStyleInfo, args: [[{func: populateSelector, args: [$('#editStyleFormListBoxexisting')[0], "__funcRet"]},
						                                   {func: populateSelector, args: [$('#addStyleFormListBoxexisting')[0], "__funcRet"]}]]}]);
				  //clear form
				    $('#editStyleFormObj').find("input[type=text] , textarea").each(function(){
				                $(this).val('');            
				    });
				  $('#editStyleModal').modal('hide');
				  enableActionControls();
			   }
	 );
	
	var select = document.getElementById("editStyleFormListBoxexisting");
	requestStyleInfo([{func: populateSelector, args: [select, "__funcRet"]}, 
	                  {func: function() {
	                	  		$('#editStyleFormListBoxexisting').change(function(ev) 
	                	  													{
	                	  														var selected = $('#editStyleFormListBoxexisting :selected').val();
	                	  														if(selected == 'None') return;
	                	  														retrieveStyle(selected, [{ 
	                	  															func:function(sld) {$('#editStyleFormTextBoxstyle').prop('value', sld.style);},
	                	  															args:["__funcRet"]}]);
	                	  													});
	                  		 }, args: []
	                  }]);
}

function createModalAddStyleForm()
{
	var input = document.getElementById("addStyleFormCancelButton");
	input.addEventListener('click', function(ev)
			   {
				 $('#addStyleModal').modal('hide');
				 enableActionControls();
			   }
	 );
	
	input = document.getElementById("addStyleFormSaveButton");
	input.addEventListener('click', function()
			   {
				  updateStyle($('#addStyleFormObj')[0], true, [{func: requestStyleInfo, args: [[{func: populateSelector, args: [$('#editStyleFormListBoxexisting')[0], "__funcRet"]},
				                    						                                   {func: populateSelector, args: [$('#addStyleFormListBoxexisting')[0], "__funcRet"]}]]}]);
				  //clear form
				    $('#addStyleFormObj').find("input[type=text] , textarea").each(function(){
				                $(this).val('');            
				    });
				  $('#addStyleModal').modal('hide');
			   }
	 );
	
	var select = document.getElementById("addStyleFormListBoxexisting");
	requestStyleInfo([{func: populateSelector, args: [select, "__funcRet"]}, 
	                  {func: function() {
	                	  		$('#addStyleFormListBoxexisting').change(function(ev) 
	                	  													{
												                	  			var selected = $('#addStyleFormListBoxexisting :selected').val();
																				if(selected == 'None') return;
	                	  														retrieveStyle(selected, [{ 
	                	  															func:function(sld) {$('#addStyleFormTextBoxstyle').prop('value', sld.style);},
	                	  															args:["__funcRet"]}]);
	                	  													});
	                  		 }, args: []
	                  }]);
}

function createModalAddThemeForm()
{
	var input = document.getElementById("addThemeFormCancelButton");
	input.addEventListener('click', function(ev)
			   {
				 $('#addThemeModal').modal('hide');
				 enableActionControls();
			   }
	 );
	
	input = document.getElementById("addThemeFormSaveButton");
	input.addEventListener('click', function()
			   {
				  addTheme($('#addThemeFormObj')[0], true, [{func: requestThemeInfo, args: [[{func: populateSelector, args: [$('#editThemeFormListBoxtemplate')[0], "__funcRet", "None", "--None--", false]},
				                    						                                   {func: populateSelector, args: [$('#addThemeFormListBoxtemplate')[0], "__funcRet", "None", "--None--", false]}]]}]);
				  //clear form
				    $('#addThemeFormObj').find("input[type=text] , textarea").each(function(){
				                $(this).val('');            
				    });
				  $('#addThemeModal').modal('hide');
			   }
	 );
	
	var select = document.getElementById("addThemeFormListBoxtemplate");
	requestThemeInfo([{func: populateSelector, args: [select, "__funcRet", "None", "--None--", false]}]);
}

function editStyleButtonListener(ev)
{ 
	disableActionControls();
	retrieveStyle(ev.target.styleName, [{func: showStyleEditForm, args: [ev, "__funcRet"]}]); 
}

function editThemeButtonListener(ev, data)
{
	$('#addLayerConfigButton')[0].themeName = ev.target.themeName;
	createLayerConfigTable(data, ev.target.themeName, [{func: function() {$('#presentation-nav li a[href="#presentation-layers"]').click();}, args: []}]);
}

function updateLayerConfigButtonListener(ev)
{
	var i = ev.target.ordinal;
	req = {};
	req.theme = $('#layerConfigTheme_' + i).html();
	req.layerName = $('#layerConfigLayerName_' + i).html();
	req.termId = ev.target.termId;
	req.style = $('#layerConfigListBoxStyle_' + i + ' :selected').val();
	
	var minScale = $('#layerConfigListBoxMinScale_' + i + ' :selected').val();
	if(isPresent(minScale) && minScale != 'None')
		req.minScale = minScale;
	var maxScale = $('#layerConfigListBoxMaxScale_' + i + ' :selected').val();
	if(isPresent(maxScale) && maxScale != 'None')
		req.maxScale = maxScale;
	if(isPresent(req.minScale) && isPresent(req.maxScale) && parseInt(req.minScale) > parseInt(req.maxScale))
	{
		alert("Minimum scale must be less than maximum scale");
		return;
	}
	
	req = JSON.stringify(req);
	$.ajax({ 
        url : "presentation/updateLayerStyle", 
        type : "post",
        dataType: "json",
        contentType: "application/json",
        data : req,
        success : function(response) 
        		  { 
		        	if(response.status == false)
		        		alert("An error has occurred: " + response.message);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
}

function showStyleEditForm(ev, sld)
{
	$('#editStyleFormTextBoxname').prop('value', sld.name);
	$('#editStyleFormTextBoxstyle').prop('value', sld.style);
	$('#editStyleModal').modal('show');
	$('#editStyleFormTextBoxname').data('origName', sld.name);
}

function retrieveStyle(name, postProcessingFuncs)
{
	$.ajax({ 
        url : "presentation/retrieveStyle", 
        type : "post",
        dataType: "json",
        contentType: "application/json",
        data : name,
        success : function(sld) 
        		  { 
        			if(!isPresent(sld.style))
        			{
        				alert("Style " + name + " not found!");
        				return;
        			}
		        	sld.name = name;
		        	applyFuncs(postProcessingFuncs, sld);
        		  },
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 
}

function updateStyle(form, create, postProcessingFuncs)
{

	var style = {};
	style.name = $('#'+(create ? 'add' : 'edit')+"StyleFormTextBoxname").val();
	if(create == false && isPresent($('#editStyleFormTextBoxname').data('origName')))
		style.origName = $('#editStyleFormTextBoxname').data('origName');
	style.style = $('#'+(create ? 'add' : 'edit')+"StyleFormTextBoxstyle").val();
	
	style = JSON.stringify(style);
    $.ajax({ 
        url : "presentation/updateStyle",
        type : "post", 
        dataType : "json",
        contentType : "application/json",
        data : style,
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
					createStyleTable(data, false);
					applyFuncs(postProcessingFuncs, response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function deleteStyles(ev, postProcessingFuncs)
{
	var len = $('#styleTbl')[0].entryCount;
	var req = [];
	
	for(var i=0; i<len; i++)
	{
		if($('#styleRecordDeleteCheckbox_'+i).is(':checked'))
			req.push($('#styleName_'+i)[0].innerHTML);
	}
	
	req = JSON.stringify(req);
	$.ajax({ 
        url : "presentation/removeStyles",
        type : "post", 
        data : req,
        dataType : "json",
        contentType : "application/json",
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
					createStyleTable(data, false);
					
					applyFuncs(postProcessingFuncs, response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function addTheme(form, postProcessingFuncs)
{

	var theme = {};
	theme.name = $('#addThemeFormTextBoxname').val();
	theme.template = $('#addThemeFormTextBoxtemplate :selected').val();
	
	theme = JSON.stringify(theme);
    $.ajax({ 
        url : "presentation/addTheme",
        type : "post", 
        dataType : "json",
        contentType : "application/json",
        data : theme,
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
					createThemeTable(data, false);
					applyFuncs(postProcessingFuncs, response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function deleteThemes(ev, postProcessingFuncs)
{
	var len = $('#themeTbl')[0].entryCount;
	var req = [];
	
	for(var i=0; i<len; i++)
	{
		if($('#themeRecordDeleteCheckbox_'+i).is(':checked'))
			req.push($('#themeName_'+i)[0].innerHTML);
	}
	
	req = JSON.stringify(req);
	$.ajax({ 
        url : "presentation/removeThemes",
        type : "post", 
        data : req,
        dataType : "json",
        contentType : "application/json",
        success : function(response) 
        		  {
        			if(response.status == false) alert("An error has occurred: " + response.message);
					createThemeTable(data, false);
					
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
	$('#addStyleButton').prop('disabled', true);
	$('#addThemeButton').prop('disabled', true);
	$('#deleteStyleButton').prop('disabled', true);
	$('#deleteThemeButton').prop('disabled', true);
	$(".styleRecordDeleteCheckboxes").prop('disabled', true);
	$(".styleRecordEditButtons").prop('disabled', true);
	$(".themeRecordDeleteCheckboxes").data('disabled', $('.themeRecordDeleteCheckboxes').prop('disabled'));
	$(".themeRecordEditButtons").prop('disabled', true);
}

function enableActionControls()
{
	$('#addStyleButton').prop('disabled', false);
	$('#addThemeButton').prop('disabled', false);
	$('#deleteStyleButton').prop('disabled', false);
	$('#deleteThemeButton').prop('disabled', false);
	$(".styleRecordDeleteCheckboxes").prop('disabled', false);
	$(".styleRecordEditButtons").prop('disabled', false);
	$(".themeRecordDeleteCheckboxes").prop('disabled', $('.themeRecordDeleteCheckboxes').data('disabled'));
	$(".themeRecordEditButtons").prop('disabled', false);
}