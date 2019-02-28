function wireLeftNavbar(defs)
{
	$('#search').click(function(ev)
			   {
					toggleLeftNavbarButton(ev.target, defs, showSearchPane);
			   });
	$('#legend').click(function(ev)
			   {
					toggleLeftNavbarButton(ev.target, defs, showLayersPane);
			   });
	$('#projects').click(function(ev)
			   {
					toggleLeftNavbarButton(ev.target, defs, showProjectsPane);
			   });
	$('#settings').click(function(ev)
			   {
					toggleLeftNavbarButton(ev.target, defs, showSettingsPane);
			   });
	
}

function toggleLeftNavbarButton(target, defs, func)
{
	if($(target).hasClass('navbar-left-active')) return;
	$('#right-pane-overlay').children().hide();
	$('#back-right-pane').hide();
	var active = $('.navbar-left-active');
	if(active.length == 1)
	{
		$('#right-pane-overlay').children().detach().appendTo($('#rpo-'+active[0].id));
		if($('#right-pane-overlay-content').length == 1)
				$('#right-pane-overlay-content')[0].id = 'rpoc-'+active[0].id;
	}else
	{
		var selected = $('.navbar-left-selected');
		if(selected.length == 1)
		{
			$('#right-pane-overlay').children().detach().appendTo($('#rpo-'+selected[0].id));
			if($('#right-pane-overlay-content').length == 1)
				$('#right-pane-overlay-content')[0].id = 'rpoc-'+selected[0].id;
		}
	}
	$('#right-pane-overlay').empty();
	$('#right-pane').show();
	$('#right-pane-container').addClass('pane-open');
	$('#close-right-pane').show();
	$('#tools-info').addClass('pane-open');
	$('#user-widget').addClass('pane-open');
	$('#tools').addClass('pane-open');
	$('#loading').addClass('pane-open');
	$('.navbar-left-item').removeClass('navbar-left-active');
	$('.navbar-left-item').removeClass('navbar-left-selected');
	$(target).addClass('navbar-left-active');
	$(target).addClass('navbar-left-selected');
	
	if($('body').hasClass('anonymous') && target.id != 'search')
	{
		var emptyPaneMsg = document.createElement("div");
		emptyPaneMsg.id = "promotional-msg";
		emptyPaneMsg.innerHTML = defs.i18n.nav.promotional;
		$('#right-pane-overlay').append(emptyPaneMsg);
		$('#promotional-msg').show();
	}
	else
		func(defs);
}

function cssImgLoc(defs, id, themed)
{
	return 'url(' + imgLoc(defs, id, themed) + ")";
}

function imgLoc(defs, id, themed)
{
	return defs.imgLoc + "/" + (themed ? defs.imgThemeLoc + "/" : "")+ defs.img[id];
}

function getHeight(el, cssProp)
{
	var cssProp = isPresent(cssProp) ? cssProp : 'height';
	if(!isPresent(el)) return -1;
	var height = el.css(cssProp);
	if(height.indexOf("px", height.length - 2) !== -1)
		height = height.substring(0, height.length-2);
	if(!isFinite(height)) return -1;
	return height;
}

function calculateOverlayHeight()
{
	var iWidth = (window.innerWidth > 0) ? window.innerWidth : screen.width;
	var sWidth = screen.width;
	var width = Math.min(iWidth, sWidth);
	
	if(width < 800) return -1; //do not adjust in small form factor
	var h1 = getHeight($('#right-pane'));
	if(h1 == -1) return -2;
	var h2 = getHeight($('#right-pane-overlay :first-child'));
	if(h2 == -1) return -2;
	
	return h1-h2-30;
}

function adjustOverlayHeight()
{
	var maxHeight = calculateOverlayHeight();
	if(maxHeight == -2) { console.log("Skipping height adjustment"); return; }
	if(maxHeight == -1) 
	{ 
		$('#mapContainer').css('height', (getHeight($('#navbar-left')) - getHeight($('#navbar-top')))+'px');  
		$('#right-pane-overlay-content').css('max-height', 'none'); 
		$('#mainScreen').css('height', '100%');
		return;
	}
	$('#right-pane-overlay-content').css('max-height', maxHeight+'px');
	$('#mapContainer').css('height', '100%');
}

function adjustDropdownHeight()
{
	var ddowns = $('.dropdown-menu.scroll-menu');
	var pageHeight = getHeight($('html'));
	for(var i=0; i<ddowns.length; i++)
	{
		var mHeight = getHeight($(ddowns[i]), 'max-height');
		if(mHeight > pageHeight-100)
			$(ddowns[i]).css('max-height', pageHeight-100);
		else
			$(ddowns[i]).css('max-height', '');
	}
}
function calculateResultsPerPage()
{
	var maxHeight = calculateOverlayHeight();
	if(maxHeight < 0) return 20; //default
	var headerHeight = getHeight($('#right-pane-overlay-content table th'));
	maxHeight -= headerHeight;
	var rowHeight = getHeight($('#right-pane-overlay-content table tr'));
	var rpp = Math.floor(maxHeight/rowHeight) - 2;
	if(rpp < 1) rpp = 1;
	return rpp;
}

function optimizeSortIconPosition(defs)
{
	$('.sorting').css('background', 'none');
	$('.sorting_asc').css('background', 'none');
	$('.sorting_desc').css('background', 'none');
	$('.sorting_asc_disabled').css('background', 'none');
	$('.sorting_desc_disabled').css('background', 'none');
	
	$('table thead > tr > th').css('padding-left', '0');
	
	$('.sorting div.sorting-image').remove();
	$('.sorting').append('<div class="sorting-image"></div>');
	$('.sorting_asc div.sorting-image').remove();
	$('.sorting_asc').append('<div class="sorting-image"></div>');
	$('.sorting_desc div.sorting-image').remove();
	$('.sorting_desc').append('<div class="sorting-image"></div>');
	$('.sorting_asc_disabled div.sorting-image').remove();
	$('.sorting_asc_disabled').append('<div class="sorting-image"></div>');
	$('.sorting_desc_disabled div.sorting-image').remove();
	$('.sorting_desc_disabled').append('<div class="sorting-image"></div>');
	 
}

function enhanceScrollbars()
{
	return !!window.webkitURL == false && !navigator.userAgent.match(/(chrome|opera|safari)\/?\s*(\.?\d+(\.\d+)*)/i);
}

function showSearchPane(defs)
{
	var transformer = function()
	{
		var searchIcon = $(this).parent().find('img');
		$(this).empty();
		var id = $(this)[0].id;
		var type = $(this)[0].type;
		var placeholder = $(this)[0].placeholder;
		if(this.tagName.toLowerCase() == 'div')
		{
			$(this).replaceWith($(this).clone().wrap('<p>').parent().html().replace("div", "input"));
			var ev = {};
			ev.target = $('#'+id);
			$('#'+id)[0].type = type;
			$('#'+id)[0].placeholder = placeholder;
			$('#'+id)[0].transform = transformer;
			$('#'+id)[0].addEventListener('keyup', function(ev) { dataTableSearchHandler(ev, 'searchTbl'); });
			$(searchIcon).off();
			//$(searchIcon).click() //TODO datatable handler
		}
		else if(this.tagName.toLowerCase() == 'input')
		{
			$(this).replaceWith($(this).clone().wrap('<p>').parent().html().replace("input", "div"));
			$(searchIcon).off('click').click(searchShapesByAttributes);
			$('#'+id)[0].transform = transformer;
			$('#'+id)[0].type = type;
			$('#'+id)[0].placeholder = placeholder;
		}
	};
	
	$('#title_bar').css('background-image', cssImgLoc(defs, "NAV_SEARCH_ICON", true));
	$('#title > h1').html(defs.i18n.nav.search);
	if(!$.trim($('#search-pane').html()))
	{
		var overlay = document.createElement('div');
		overlay.id = 'rpo-search';
		var d = document.createElement("div");
		d.appendChild(gridRow({qualifier: "xs", els: [searchWidget({defs: defs, searchIconHandler: searchShapesByAttributes, customInput: true, editable: false, transformer: transformer}), 
		                             newItemWidget(defs, function(ev) 
									 { 
//		                            	window.mapLayers.markers.clearMarkers();
		                            	var searchInput = $('#search-items')[0];
		                            	if(searchInput.tagName.toLowerCase() == 'input')
		                            		searchInput.transform();
										enableSearchDropdown([searchInput.id], {
											                                url : "geography/terms", 
											                                name:defs.i18n.taxon[defs.geographyHierarchy[0]], 
											                                contentType : "application/json", 
											                                data : defs.geographyHierarchy[0], 
											                                defs : defs}); 
									 }, "newSearch")], 
									 widths: ["9", "3"],
									 rowId: "search-create-toolbar-search",
									 rowClass: "search-create-toolbar",
									 colClasses: "search-create-toolbar-item"}));
		d.appendChild(gridRow({qualifier: "xs", els: searchSelectorWidget(defs), widths: ["4", "5"], offsets: ["2","0"]}));
		$(overlay).prepend(d);
		$(overlay).append('<div id="rpoc-search"></div>');
		$('#search-pane').append(overlay);
		moveSearchInfoToPane();
		$('#right-pane-overlay div.search-widget')[0].id = 'search-items';
		var searchInput = $('#right-pane-overlay div.search-widget');
		enableSearchDropdown([searchInput[0].id], {url : "geography/terms", name:defs.i18n.taxon[defs.geographyHierarchy[0]], contentType : "application/json", data : defs.geographyHierarchy[0], defs : defs});
		$('#searchTypeSelectorMap').prop('checked', true);
	}
	else
		moveSearchInfoToPane();
	
}

function showLayersPane(defs)
{
	$('#title_bar')[0].style.backgroundImage=cssImgLoc(defs, "NAV_LEGEND_ICON", true);
	$('#title > h1').html(defs.i18n.nav.legend);
	if(!$.trim($('#legend-pane').html()))
	{
		var overlay = document.createElement('div');
		overlay.id = 'rpo-legend';
		var d = document.createElement("div");
		d.appendChild(gridRow({
								qualifier: "xs", 
								els: [searchWidget({defs: defs})], 
								widths: ["9"], 
								rowId: "search-create-toolbar-legend", 
								rowClass: "search-create-toolbar", 
								colClasses: "search-create-toolbar-item"
							  }));
		$(overlay).prepend(d);
		$(overlay).append('<div id="rpoc-legend"></div>');
		$('#legend-pane').append(overlay);
		
		moveLegendInfoToPane();
		
		/**********kkk******/
		$('#legend-nav').detach().prependTo($('#right-pane-overlay'));
		$('#legend-nav li').removeClass('active');
		$('#legend-nav').show();
		$('#rpoc-legend-tabs').detach().appendTo('#right-pane-overlay');
		$('#right-pane-overlay-content').remove();
		//$('#right-pane-overlay').append($('#rpoc-legend-tabs'));
		$('#rpoc-legend-tabs')[0].id = 'right-pane-overlay-content';
		$('#right-pane-overlay-content').show();
		
		var setTabHandler = function() {
			$('#legend-nav a[data-toggle="tab"]').off('show.bs.tab').on('show.bs.tab', function (e) {
				  var tblId = null, tableCreator = null;
				  var filterIds = null;
				  var dims = ["9"];
				  $('#empty-pane-msg').remove();
				  var emptyPaneMsg = document.createElement("div");
				  emptyPaneMsg.id = "empty-pane-msg";
				  var layerType = null;
					
				  switch(e.target.getAttribute('href'))
				  {
				  case '#legend-sites':
					  tblId = 'sitesTbl';
					  filterIds = [tblId];
					  if($('#'+tblId).find('tbody tr').length == 0)
					  {
						$('#right-pane-overlay-content').hide();
						emptyPaneMsg.innerHTML = defs.i18n.info.noInfo;
						$('#right-pane-overlay').append(emptyPaneMsg);
						$('#empty-pane-msg').show();
					  }else
						  $('#right-pane-overlay-content').show();
					  tableCreator = createLegendTable;
					  layerType = "SITETAXONOMY";
					  break;
				  case '#legend-planning':
					  tblId = 'planningTbl';
					  filterIds = [tblId];
					  if($('#'+tblId).find('tbody tr').length == 0)
					  {
						$('#right-pane-overlay-content').hide();
						emptyPaneMsg.innerHTML = defs.i18n.info.noInfo;
						$('#right-pane-overlay').append(emptyPaneMsg);
						$('#empty-pane-msg').show();
					  }else
						  $('#right-pane-overlay-content').show();
					  tableCreator = createLegendTable;
					  layerType = "LANDUSETAXONOMY";
					  break;
				  case '#legend-maps':
					  tblId = 'mapsTbl';
					  filterIds = [tblId, tblId+'overlay'];
					  if($('#'+tblId).find('tbody tr').length == 0 && $('#'+tblId+'overlay').find('tbody tr').length == 0)
					  {
						$('#right-pane-overlay-content').hide();
						emptyPaneMsg.innerHTML = defs.i18n.info.noInfo;
						$('#right-pane-overlay').append(emptyPaneMsg);
						$('#empty-pane-msg').show();
					  }else
						  $('#right-pane-overlay-content').show();
					  tableCreator = createMapsTable;
					  layerType = "LAYERTAXONOMY";
					  break;
				  case '#legend-poi':
					  tblId = 'poiTbl';
					  filterIds = [tblId];
					  if($('#'+tblId).find('tbody tr').length == 0)
					  {
						$('#right-pane-overlay-content').hide();
						emptyPaneMsg.innerHTML = defs.i18n.info.noInfo;
						$('#right-pane-overlay').append(emptyPaneMsg);
						$('#empty-pane-msg').show();
					  }else
						  $('#right-pane-overlay-content').show();
					  tableCreator = createLegendTable;
					  layerType = "POITAXONOMY";
					  break;
				  }
				  $('#search-create-toolbar-legend').remove();
				  var widgets = [];
				  if(isPresent(tblId) && isPresent(filterIds))
					  widgets.push(searchWidget({defs: defs, 
						  						changeObjId: filterIds, 
						  						changeHandler: dataTableSearchHandler, 
						  						searchIconHandler: function() { retrieveLayerInfo(defs, tableCreator, tblId, layerType, []); }
						  						}));
				  
				  if(widgets.length != 0)
					  $('#legend-nav').after(gridRow({
						  								qualifier: "xs", 
						  								els: widgets, 
						  								widths: dims,
						  								rowId: 'search-create-toolbar-legend', 
						  								rowClass: 'search-create-toolbar',
						  								colClasses: 'search-create-toolbar-item'
					  								}));
				  if(widgets.length != 2)
					  $('#right-pane-overlay-content').addClass('nav-overlay-spaced');
				  else
					  $('#right-pane-overlay-content').removeClass('nav-overlay-spaced');
				});
		};
		
		var clickTab = function() { $('#legend-nav li a[href="#legend-sites"]').click(); };
		
		retrieveLayerInfo(defs, createLegendTable, 'siteTbl', "SITETAXONOMY", 
				[{func: retrieveLayerInfo, args: [defs, createLegendTable, 'planningTbl', 'LANDUSETAXONOMY', 
				        [{func: retrieveLayerInfo, args:[defs, createMapsTable, 'mapsTbl', 'LAYERTAXONOMY', 
				        	[{func: retrieveLayerInfo, args:[defs, createLegendTable, 'poiTbl', 'POITAXONOMΥ', 
				        	                                 [{func: setTabHandler, args:[]}, {func: clickTab, args:[]}]]}]]}]]}]);
		/**********************/
	}
	else
		moveLegendInfoToPane();
}

function showProjectsPane(defs, postProcessingFuncs)
{
	$('#title_bar')[0].style.backgroundImage=cssImgLoc(defs, "NAV_PROJECTS_ICON", true);
	$('#title > h1').html(defs.i18n.nav.projects);
	if(!$.trim($('#projects-pane').html()))
	{
		var overlay = document.createElement('div');
		overlay.id = 'rpo-projects';
		$(overlay).prepend(
				gridRow({
						 	qualifier: "xs", 
						 	els: [searchWidget({defs: defs, changeObjId: 'projectTbl', changeHandler: dataTableSearchHandler, searchIconHandler: retrieveProjects}), 
				               newItemWidget(defs, function(ev) { showCreateProject(ev, defs, []); }, "newProject")], 
						    widths: ["9", "3"],
						    rowId: "search-create-toolbar-project", 
						    rowClass: "search-create-toolbar",
						    colClasses: "search-create-toolbar-item"
						}));
		$(overlay).append('<div id="rpoc-projects"></div>');
		$('#projects-pane').append(overlay);
		moveProjectInfoToPane();
		retrieveProjects(null, defs, postProcessingFuncs);
	}else
	{
		moveProjectInfoToPane();
		applyFuncs(postProcessingFuncs, null);
	}
	
	
}

function backToProjectsPane(defs, postProcessingFuncs)
{
	$('#back-right-pane').hide();
	$('#title > h1').html(defs.i18n.nav.projects);
	var overlay = $('#right-pane-overlay');
	$(overlay).children().hide();
	$('#project-nav').detach().appendTo($('body'));
	$('#editTaskForm').detach().appendTo($('body'));
	$('#editDocumentForm').detach().appendTo($('body'));
	$(overlay).find('#right-pane-overlay-content').appendTo('body');
	$('#right-pane-overlay-content')[0].id = 'rpoc-project-tabs';
	$(overlay).empty();
	
	$(overlay).prepend(
			gridRow({
						qualifier: "xs", 
						els: [searchWidget({defs: defs, changeObjId: 'projectTbl', changeHandler: dataTableSearchHandler, searchIconHandler: retrieveProjects}), 
			               newItemWidget(defs, function(ev) { showCreateProject(ev, defs, []); }, "newProject")], 
			            widths: ["9", "3"],
			            rowId: "search-create-toolbar-project", 
			            rowClass: "search-create-toolbar",
			            colClasses: "search-create-toolbar-item"
					}));
	$(overlay).append('<div id="right-pane-overlay-content"></div>');
	retrieveProjects(null, defs, postProcessingFuncs);
	
}

function showProjectDetailsPane(options)
{
	var projectName = isPresent(options.ordinal) ? $('#projectName_'+options.ordinal).html() : options.projectName; 
	$('#back-right-pane').show();
	$('#back-right-pane').off('click').click(function(ev) { backToProjectsPane(options.defs); });
	$('#title > h1').html(projectName);
	$('#project-nav').detach().prependTo($('#right-pane-overlay'));
	$('#project-nav li').removeClass('active');
	$('#project-nav').show();
	$('#right-pane-overlay-content').remove();
	$('#right-pane-overlay').append($('#rpoc-project-tabs'));
	$('#rpoc-project-tabs')[0].id = 'right-pane-overlay-content';
	$('#right-pane-overlay-content').show();
	
	var setTabHandler = function() {
		$('#project-nav a[data-toggle="tab"]').off('show.bs.tab').on('show.bs.tab', function (e) {
			  var changeIds = null, tblId = null, actionHandler = null, searchHandler = null, newItemHandler = null, newItemTextId = null;
			  var dims = ["9"];
			  $('#empty-pane-msg').remove();
			  var emptyPaneMsg = document.createElement("div");
			  emptyPaneMsg.id = "empty-pane-msg";
				
			  switch(e.target.getAttribute('href'))
			  {
			  case '#project-info':
				  tblId = 'project-info-pane';
				  changeIds = [];
				  $('.project-category-content table').each(function(){ changeIds.push(this.id);});
				  if($('#'+tblId).length == 0)
				  {
					$('#right-pane-overlay-content').hide();
					emptyPaneMsg.innerHTML = options.defs.i18n.info.noInfo;
					$('#right-pane-overlay').append(emptyPaneMsg);
					$('#empty-pane-msg').show();
				  }else
					  $('#right-pane-overlay-content').show();
				  actionHandler = retrieveInfo;
				  searchHandler = dataTableSearchHandler;
				  newItemHandler = function(ev) { showCreateAttribute(ev, options.projectId, options.defs, []); };
				  newItemTextId = "newInfo";
				  dims.push("3");
				  break;
			  case '#project-tasks':
				  tblId = 'taskTbl';
				  changeIds = tblId;
				  if($('#'+tblId).find('tbody tr').length == 0)
				  {
					$('#right-pane-overlay-content').hide();
					emptyPaneMsg.innerHTML = options.defs.i18n.info.noTasks;
					$('#right-pane-overlay').append(emptyPaneMsg);
					$('#empty-pane-msg').show();
				  }else
					  $('#right-pane-overlay-content').show();
				  actionHandler = retrieveTasks;
				  searchHandler = dataTableSearchHandler;
				  newItemHandler = function(ev) { showCreateTask(ev, options.projectId, options.defs, []);};
				  newItemTextId = "newTask";
				  dims.push("3");
				  break;
			  case '#project-documents':
				  tblId = 'documentTbl';
				  changeIds = tblId;
				  if($('#'+tblId).find('tbody tr').length == 0)
				  {
					$('#right-pane-overlay-content').hide();
					emptyPaneMsg.innerHTML = options.defs.i18n.info.noDocuments;
					$('#right-pane-overlay').append(emptyPaneMsg);
					$('#empty-pane-msg').show();
				  }else
					  $('#right-pane-overlay-content').show();
				  actionHandler = retrieveDocuments;
				  searchHandler = dataTableSearchHandler;
				  newItemHandler = function(ev) { showCreateDocument(ev, options.projectId, options.defs, []);}; //TODO
				  newItemTextId = "newDocument";
				  dims.push("3");
				  break;
			  case '#project-report':
				  $('#right-pane-overlay-content').hide();
				  break;
			  }
			  $('#search-create-toolbar-project').remove();
			  var widgets = [];
			  if(isPresent(tblId))
				  widgets.push(searchWidget({defs: options.defs, changeObjId: changeIds, changeHandler: searchHandler, searchIconHandler: function() { actionHandler(options.projectId, options.defs, null); }}));
			  if(isPresent(newItemHandler))
				  widgets.push(newItemWidget(options.defs, newItemHandler, newItemTextId));
			  if(widgets.length != 0)
				  $('#project-nav').after(gridRow({
					  								qualifier: "xs", 
					  								els: widgets, 
					  								widths: dims,
					  								rowId: 'search-create-toolbar-project', 
					  								rowClass: 'search-create-toolbar',
					  								colClasses: 'search-create-toolbar-item'
				  								  }));
			  if(widgets.length != 2)
				  $('#right-pane-overlay-content').addClass('nav-overlay-spaced');
			  else
				  $('#right-pane-overlay-content').removeClass('nav-overlay-spaced');
			});
	};
	
	var clickTab = function() { $('#project-nav li a[href="#project-tasks"]').click(); };
	
	retrieveTasks(options.projectId, options.defs, [{func: retrieveInfo, args: [options.projectId, options.defs, [{func: retrieveDocuments, args:[options.projectId, options.defs, [{func: setTabHandler, args:[]}, {func: clickTab, args:[]}]]}]]}]);
	
	//retrieveInfo(options.projectId, options.defs, null);
	//retrieveDocuments(options.projectId, options.defs, null);
}

function dataTableSearchHandler(ev, changeObjId) {
	if(!(changeObjId instanceof Array))
		changeObjId = [changeObjId];
	for(var i=0; i<changeObjId.length; i++)
	{
		var dataTbl = $('#'+changeObjId[i]).dataTable();
		dataTbl.fnFilter($(ev.target).val());
		updatePaginationInfo(dataTbl, changeObjId[i]);
	}
}

function moveSearchInfoToPane()
{
	$('#rpo-search').children().detach().appendTo('#right-pane-overlay');
	$('#rpoc-search')[0].id = 'right-pane-overlay-content';
	
	$('#right-pane-overlay').children().show();
}

function moveProjectInfoToPane()
{
	$('#rpo-projects').children().detach().appendTo('#right-pane-overlay');
	$('#rpoc-projects')[0].id = 'right-pane-overlay-content';
	
	$('#right-pane-overlay').children().show();
	if($('#right-pane-overlay > #project-nav').length == 1)
		$('#back-right-pane').show();
	
	
	 switch($('#project-nav li.active a').attr('href'))
	  {
	  case '#project-info':
		  if($('#project-info-pane').length == 0)
		  {
			$('#right-pane-overlay-content').hide();
			//emptyPaneMsg.innerHTML = options.defs.i18n.info.noInfo;
			///$('#right-pane-overlay').append(emptyPaneMsg);
			//$('#empty-pane-msg').show();
		  }
		  break;
	  case '#project-tasks':
		  if($('#taskTbl').find('tbody tr').length == 0)
		  {
			$('#right-pane-overlay-content').hide();
			//emptyPaneMsg.innerHTML = options.defs.i18n.info.noTasks;
			//$('#right-pane-overlay').append(emptyPaneMsg);
			//$('#empty-pane-msg').show();
		  }
		  break;
	  case '#project-documents':
		  if($('#documentTbl').find('tbody tr').length == 0)
		  {
			$('#right-pane-overlay-content').hide();
			//emptyPaneMsg.innerHTML = options.defs.i18n.info.noDocuments;
			//$('#right-pane-overlay').append(emptyPaneMsg);
			//$('#empty-pane-msg').show();
		  }
		  break;
	  case '#project-report':
		  $('#right-pane-overlay-content').hide();
		  break;
	  }
}

function moveLegendInfoToPane()
{
	$('#rpo-legend').children().detach().appendTo('#right-pane-overlay');
	$('#rpoc-legend')[0].id = 'right-pane-overlay-content';
	
	$('#right-pane-overlay').children().show();
	
	 switch($('#legend-nav li.active a').attr('href'))
	  {
	  case '#legend-sites':
		  if($('#sitesTbl').find('tbody tr').length == 0)
		  {
			$('#right-pane-overlay-content').hide();
			/*emptyPaneMsg.innerHTML = defs.i18n.info.noInfo;
			$('#right-pane-overlay').append(emptyPaneMsg);
			$('#empty-pane-msg').show();*/
		  }
		  break;
	  case '#legend-planning':
		  if($('#planningTbl').find('tbody tr').length == 0)
		  {
			$('#right-pane-overlay-content').hide();
			/*emptyPaneMsg.innerHTML = defs.i18n.info.noInfo;
			$('#right-pane-overlay').append(emptyPaneMsg);
			$('#empty-pane-msg').show();*/
		  }
		  break;
	  case '#legend-maps':
		  if($('#mapsTbl').find('tbody tr').length == 0 && $('#mapsTbloverlay').find('tbody tr').length == 0)
		  {
			$('#right-pane-overlay-content').hide();
			/*emptyPaneMsg.innerHTML = defs.i18n.info.noInfo;
			$('#right-pane-overlay').append(emptyPaneMsg);
			$('#empty-pane-msg').show();*/
		  }
		  break;
	  case '#legend-poi':
		  if($('#poiTbl').find('tbody tr').length == 0)
		  {
			$('#right-pane-overlay-content').hide();
		/*	emptyPaneMsg.innerHTML = defs.i18n.info.noInfo;
			$('#right-pane-overlay').append(emptyPaneMsg);
			$('#empty-pane-msg').show();*/
		  }
		  break;
	  }
}

function showSettingsPane(defs)
{
	$('#title_bar')[0].style.backgroundImage=cssImgLoc(defs, "NAV_SETTINGS_ICON", true);
	$('#title > h1').html(defs.i18n.nav.settings);
	if(!$.trim($('#settings-pane').html()))
	{
		var overlay = document.createElement('div');
		overlay.id = 'rpo-settings';
		$(overlay).append('<div id="rpoc-settings"></div>"');
		$('#settings-pane').append(overlay);
	}
	$('#rpo-settings').children().detach().appendTo('#right-pane-overlay');
	$('#rpoc-settings')[0].id = 'right-pane-overlay-content';
	$('#right-pane-overlay').children().show();
}

function gridRow(options)
{
	var row = document.createElement("div");
	row.className = "row";
	
	var offsetQualifier = options.qualifier;
	if(options.qualifier=='xs')
		offsetQualifier = 'md';
	
	for(var i=0; i<options.els.length; i++)
	{
		var col = document.createElement("div");
		col.className = (isPresent(options.colClasses) ? options.colClasses + " " : "") +  "col-" + options.qualifier + "-" + options.widths[i];
		if(isPresent(options.offsets))
		{
			if(isPresent(options.offsets[i]) && options.offsets[i] > 0)
				col.className += " col-" + offsetQualifier + "-offset-" + options.offsets[i];
		}
		col.appendChild(options.els[i]);
		row.appendChild(col);
	}
	if(isPresent(options.rowId))
		row.id = options.rowId;
	if(isPresent(options.rowClass))
		row.className += ' ' +options.rowClass;
	return row;
}

function searchWidget(options)
{
	var defs = options.defs;
	var form = document.createElement("form");
	var iGroup = document.createElement("div");
	iGroup.className="input-group";
	
	var addon = document.createElement("span");
	addon.className = "search-widget input-group-addon";
	
	var addonImage = document.createElement("img");
	addonImage.id = "search-widget-control";
	addonImage.src =  imgLoc(defs, "SEARCH_ICON", true);
	if(isPresent(options.searchIconHandler))
		addonImage.addEventListener('click', function(ev) { options.searchIconHandler(ev, defs, options.postProcessingFuncs);});
	addon.appendChild(addonImage);
	
	var input = document.createElement(options.customInput ? 'div' : 'input');
	input.type = "text";
	input.className = "search-widget input-sm form-control";
	input.placeholder = defs.i18n.placeholder["SEARCH"];
	if(options.editable) input.contentEditable = 'true';
	if(isPresent(options.changeHandler))
		input.addEventListener('keyup', function(ev) { options.changeHandler(ev, options.changeObjId); });
	
	iGroup.appendChild(addon);
	iGroup.appendChild(input);
	
	form.appendChild(iGroup);
	
	 $(form).keydown(function(event)
			 		 {
					    if(event.keyCode == 13) {
					      event.preventDefault();
					      return false;
					    }
					 });
	 
	 if(isPresent(options.transformer))
		 input.transform = options.transformer;
	return form;
}

function paginationWidget(defs, tableId)
{
	var widgetRow = document.createElement("div");
	widgetRow.className="pagination row";
	
	var widget = document.createElement("div");
	//widget.className = "col-xs-6 col-sm-offset-4";
	//widgetRow.appendChild(widget);
	
	//var row = document.createElement("div");
	//row.className = "row";
	
	widget = document.createElement("div");
	widget.className = "col-xs-3 pagination-left";
	
	var paginationControl = document.createElement("img");
	paginationControl.className = "pagination-first";
	paginationControl.src = imgLoc(defs, "PAGINATION_FIRST", true);
	$(paginationControl).click(function() { pageChange(tableId, 'first'); });
	widget.appendChild(paginationControl);
	
	paginationControl = document.createElement("img");
	paginationControl.className = "pagination-previous";
	paginationControl.src = imgLoc(defs, "PAGINATION_PREVIOUS", true);
	$(paginationControl).click(function() { pageChange(tableId, 'previous'); });
	widget.appendChild(paginationControl);
	
	widgetRow.appendChild(widget);
	
	widget = document.createElement("div");
	widget.className = "col-xs-6 pagination-info";
	widgetRow.appendChild(widget);

	widget = document.createElement("div");
	widget.className = "col-xs-3 pagination-right";
	
	paginationControl = document.createElement("img");
	paginationControl.className = "pagination-last";
	paginationControl.src = imgLoc(defs, "PAGINATION_LAST", true);
	$(paginationControl).click(function() { pageChange(tableId, 'last'); });
	widget.appendChild(paginationControl);
	
	paginationControl = document.createElement("img");
	paginationControl.className = "pagination-next";
	paginationControl.src = imgLoc(defs, "PAGINATION_NEXT", true);
	$(paginationControl).click(function() { pageChange(tableId, 'next'); });
	widget.appendChild(paginationControl);
	
	
	widgetRow.appendChild(widget);
	
	return widgetRow;
}

function updatePaginationInfo(dataTbl, tableId)
{
	var info = dataTbl.fnPagingInfo();
	$('#'+tableId).parent().parent().find('.pagination-info').html(
			(info.iStart+1) + ' - ' + info.iEnd + ' '+ defs.i18n.from + ' ' + info.iFilteredTotal);
}

function pageChange(tableId, target)
{
	var tbl = $('#'+tableId);
	if(!$.fn.DataTable.fnIsDataTable(tbl[0])) return;
	var dataTbl = $(tbl).dataTable();
	dataTbl.fnPageChange(target);
	updatePaginationInfo(dataTbl, tableId);
}

function searchSelectorWidget(defs)
{
/*	<div class="form-group">
	<input type="radio" id="addTaskFormRadiocriticalityNB" class="" name="criticality" value="NONBLOCKING">
	<label id="addTaskFormLabelcriticalityNB" for="addTaskFormRadiocriticalityNB" class="addTaskFormElement">
		<span></span>
	</label>
	<input type="radio" id="addTaskFormRadiocriticalityB" class="" name="criticality" value="BLOCKING">
	<label id="addTaskFormLabelcriticalityB" for="addTaskFormRadiocriticalityB" class="addTaskFormElement">
		<span></span>
	</label>
	<input type="radio" id="addTaskFormRadiocriticalityC" class="" name="criticality" value="CRITICAL">
	<label id="addTaskFormLabelcriticalityC" for="addTaskFormRadiocriticalityC" class="addTaskFormElement">
		<span></span>
	</label>
</div>*/
	return $('<div><input type="radio" id="searchTypeSelectorMap" class="chkbox-small" name="searchType" value="MAP">\
	<label id="searchTypeLabelMap" for="searchTypeSelectorMap">\
		<span></span>'+defs.i18n.search.typeMap+'\
	</label></div><div><input type="radio" class="chkbox-small" id="searchTypeSelectorProject" name="searchType" value="PROJECTS">\
	<label id="searchTypeLabelProject" for="searchTypeSelectorProject">\
		<span></span>'+defs.i18n.search.typeProjects+'\
	</label></div>');
}

function newItemWidget(defs, handler, textId)
{
	var widget = document.createElement("div");
	
	var img = document.createElement("img");
	img.src = imgLoc(defs, "ITEM_NEW_ICON", true);
	img.className = "newItem-widget";
	
	var h = document.createElement("h2");
	h.className = "newItem-widget";
	h.innerHTML = defs.i18n.action[textId];
	
	widget.appendChild(img);
	widget.appendChild(h);
	
	widget.addEventListener('click', handler);
	return widget;
}

function retrieveProjects(ev, defs, postProcessingFuncs)
{
	$.ajax({ 
        url : "projects/summary",
        type : "post", 
        success : function(summaries) 
        		  {
        			
        			populateProjectLayer(summaries.response, defs);
					var ret = createProjectTable(summaries.response, defs);
					var tbl = ret[0];
					var count = ret[1];
					if(count == 0)
					{
						$('#projectTable').hide();
						if($('#empty-pane-msg').length == 0)
						{
							var emptyPaneMsg = document.createElement("div");
							emptyPaneMsg.id = "empty-pane-msg";
							emptyPaneMsg.innerHTML = defs.i18n.info.noProjects;
							$('#right-pane-overlay').append(emptyPaneMsg);
						}
						$('#empty-pane-msg').show();
					}else
					{	
						$('#empty-pane-msg').hide();
						var t = $('#projectTbl');
						if(t.length > 0)
							$(t).dataTable().fnDestroy();
						$(t).remove();
						$('#right-pane-overlay-content').empty();
						var tblCon = document.createElement('div');
						$(tblCon).append(tbl);
						$('#right-pane-overlay-content').append(tblCon);
						$('.projectRecordDeleteButtons').hide();
						adjustOverlayHeight();
						var rpp = calculateResultsPerPage();
						$('#projectTbl').dataTable({"bFilter" : true,
													 "bInfo" : true,
													 "bLengthChange" : false,
													 //"aaSorting" : [[]],
													 "sDom" : "<'row'<'col-xs-6'l>r>t",
													 "aoColumnDefs": [
													                  { "bSortable": false, "aTargets": [0,4] },
													                  { "bSearchable": false, "aTargets": [0,4] }
													                ],
									                "oLanguage": {
											             "sZeroRecords": defs.i18n.info.noInfo
											         },
													 "iDisplayLength": rpp,
													 "bAutoWidth": false,
													 "bDestroy" : true});
						optimizeSortIconPosition(defs);
						$(tblCon).append(paginationWidget(defs, 'projectTbl'));
						updatePaginationInfo($('#projectTbl').dataTable(), 'projectTbl');
					}
					applyFuncs(postProcessingFuncs, summaries.response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
        			alert(defs.i18n.error.failure + ":" + textStatus, errorThrown);
        		 },
        		 
        statusCode: {
			 302: function(jqXHR) {
				 	window.location.href = jqXHR.getResponseHeader("Location");
			 	 },
			 303: function(jqXHR) {
				 	window.location.href = jqXHR.getResponseHeader("Location");
			 	 }
			 }
      }); 	
}

function retrieveTasks(projectId, defs, postProcessingFuncs)
{
	
	$.ajax({ 
        url : "projects/retrieveTasks",
        type : "post", 
        data: projectId, 
        dataType : "json",
        contentType : "application/json",
        success : function(tasks) 
        		  {
		        	if(tasks.status == "NotFound") alert(defs.i18n.project.error.notFound);
					if(tasks.status == "Unauthorized") alert(defs.i18n.project.error.unauthorized);
					if(tasks.status == "Failure") alert(defs.i18n.error.failure + ":" + tasks.message);
					var ret = createTaskTable(tasks.response, projectId, defs);
					var tbl = ret[0];
					var count = ret[1];
					var t = $('#taskTbl');
					if(t.length > 0)
						$(t).dataTable().fnDestroy();
					$(t).remove();
					if(count == 0)
					{
						$('#taskTable').hide();
						$('#right-pane-overlay-content').hide();
						if($('#empty-pane-msg').length == 0)
						{
							var emptyPaneMsg = document.createElement("div");
							emptyPaneMsg.id = "empty-pane-msg";
							emptyPaneMsg.innerHTML = defs.i18n.info.noTasks;
							$('#right-pane-overlay').append(emptyPaneMsg);
						}
						$('#empty-pane-msg').show();
					}else
					{	
						$('#empty-pane-msg').hide();
						$('#right-pane-overlay-content').show();
						$('#project-tasks').empty();
						var tblCon = document.createElement('div');
						$(tblCon).append(tbl);
						$('#project-tasks').append(tblCon);
						//$('.taskRecordDeleteButtons').hide();
						adjustOverlayHeight();
						var rpp = calculateResultsPerPage();
						$('#taskTbl').dataTable({"bFilter" : true,
													 "bInfo" : true,
													 "bLengthChange" : false,
													 //"aaSorting" : [[]],
													 "sDom" : "<'row'<'col-xs-6'l>r>t",
													 "aoColumnDefs": [
													                  { "bSortable": false, "aTargets": [0,6] },
													                  { "bSearchable": false, "aTargets": [0,6] }
													                ],
									                 "oLanguage": {
											             "sZeroRecords": defs.i18n.info.noInfo
											         },
													 "iDisplayLength": rpp,
													 "bAutoWidth": false,
													 "bDestroy" : true});
						optimizeSortIconPosition(defs);
						$(tblCon).append(paginationWidget(defs, 'taskTbl'));
						updatePaginationInfo($('#taskTbl').dataTable(), 'taskTbl');
					}
					applyFuncs(postProcessingFuncs, tasks.response);
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

function retrieveDocuments(projectId, defs, postProcessingFuncs)
{
	
	$.ajax({ 
        url : "projects/retrieveDocuments",
        type : "post", 
        data: projectId, 
        dataType : "json",
        contentType : "application/json",
        success : function(documents) 
        		  {
		        	if(documents.status == "ProjectNotFound") alert(defs.i18n.project.error.notFound);
					if(documents.status == "Unauthorized") alert(defs.i18n.project.error.unauthorized);
					if(documents.status == "Failure") alert(defs.i18n.error.failure + ":" + documents.message);
					
					var ret = createDocumentTable(documents.response, projectId, defs);
					var tbl = ret[0];
					var count = ret[1];
					var t = $('#documentTbl');
					if(t.length > 0)
						$(t).dataTable().fnDestroy();
					$(t).remove();
					if(count == 0)
					{
						$('#documentTable').hide();
						$('#right-pane-overlay-content').hide();
						if($('#empty-pane-msg').length == 0)
						{
							var emptyPaneMsg = document.createElement("div");
							emptyPaneMsg.id = "empty-pane-msg";
							emptyPaneMsg.innerHTML = defs.i18n.info.noDocuments;
							$('#right-pane-overlay').append(emptyPaneMsg);
						}
						$('#empty-pane-msg').show();
					}else
					{	
						$('#empty-pane-msg').hide();
						$('#right-pane-overlay-content').show();
						$('#project-documents').empty();
						var tblCon = document.createElement('div');
						$(tblCon).append(tbl);
						$('#project-documents').append(tblCon);
						adjustOverlayHeight();
						var rpp = calculateResultsPerPage();
						$('#documentTbl').dataTable({"bFilter" : true,
													 "bInfo" : true,
													 "bLengthChange" : false,
													 //"aaSorting" : [[]],
													 "sDom" : "<'row'<'col-xs-6'l>r>t",
													 "aoColumnDefs": [
													                  { "bSortable": false, "aTargets": [3] },
													                  { "bSearchable": false, "aTargets": [3] }
													                ],
									                 "oLanguage": {
											             "sZeroRecords": defs.i18n.info.noInfo
											         },
													 "iDisplayLength": rpp,
													 "bAutoWidth": false,
													 "bDestroy" : true});
						optimizeSortIconPosition(defs);
						$(tblCon).append(paginationWidget(defs, 'documentTbl'));
						updatePaginationInfo($('#documentTbl').dataTable(), 'documentTbl');
					}
					applyFuncs(postProcessingFuncs, documents.response);
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

function retrieveLayerInfo(defs, tableCreator, tblId, layerType, postProcessingFuncs)
{
    $.ajax({ 
        url : "shapes/listLayersOfType",
        type : "post", 
        data : layerType,
        dataType : "json",
        contentType : "application/json",
        success : function(response) 
        		  {
        			if(response.status == "Unauthorized") alert(defs.i18n.project.error.unauthorized);
        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					
					var ret = tableCreator(defs, response.response, tblId, layerType);
					var tbl = ret[0];
					var count = ret[tblId != 'mapsTbl' ? 1 : 2];
					var t = $('#'+tblId);
					if(t.length > 0)
						$(t).dataTable().fnDestroy();
					$(t).remove();
					if(count == 0)
					{
						$('#'+tblId).hide();
						$('#right-pane-overlay-content').hide();
						if($('#empty-pane-msg').length == 0)
						{
							var emptyPaneMsg = document.createElement("div");
							emptyPaneMsg.id = "empty-pane-msg";
							emptyPaneMsg.innerHTML = defs.i18n.info.noInfo;
							$('#right-pane-overlay').append(emptyPaneMsg);
						}
						$('#empty-pane-msg').show();
					}else
					{	
						$('#empty-pane-msg').hide();
						$('#right-pane-overlay-content').show();
						var tabPane = null;
						switch (layerType) {
						case "SITETAXONOMY":
							tabPane = $('#legend-sites')[0];
							break;
						case "LANDUSETAXONOMY":
							tabPane = $('#legend-planning')[0];
							break;
						case "LAYERTAXONOMY":
							tabPane = $('#legend-maps')[0];
							break;
						case "POITAXONOMY":
							tabPane = $('#legend-poi')[0];
							break;
						}
						$(tabPane).empty();
						var tblCon = document.createElement('div');
						$(tblCon).append(tbl);
						$(tabPane).append(tblCon);
						//$('.taskRecordDeleteButtons').hide();
						adjustOverlayHeight();
						$('#'+tblId).dataTable({"bFilter" : true,
													 "bInfo" : false,
													 "bLengthChange" : false,
													 "bSort" : false,
													 "sDom" : "<'row'<'col-xs-6'l>r>t", /*
													   "aoColumnDefs": [
													                  { "bSortable": false, "aTargets": [0,6] },
													                  { "bSearchable": false, "aTargets": [0,6] }
													                ],*/
													 "oLanguage": {
												            "sZeroRecords": defs.i18n.info.noInfo
												      },
													 "iDisplayLength": count,
													 "bAutoWidth": false,
													 "bDestroy" : false});
					 if(tblId == 'mapsTbl')
					 {
						 $(tblCon).append(ret[1]);
						 adjustOverlayHeight();
						 $('#'+tblId+'overlay').dataTable({"bFilter" : true,
																 "bInfo" : false,
																 "bLengthChange" : false,
																 "bSort" : false,
																 "sDom" : "<'row'<'col-xs-6'l>r>t", /*
																 "aoColumnDefs": [
																                  { "bSortable": false, "aTargets": [0,6] },
																                  { "bSearchable": false, "aTargets": [0,6] }
																                ],*/
																 "oLanguage": {
															            "sZeroRecords": defs.i18n.info.noInfo
															      },
																 "iDisplayLength": $('#'+tblId+'overlay')[0].entryCount,
																 "bAutoWidth": false,
																 "bDestroy" : false});
					 }
					
					}
					applyFuncs(postProcessingFuncs, response.response);
        		  },
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

function retrieveInfo(projectId, defs, postProcessingFuncs)
{	
	$.ajax({ 
        url : "projects/retrieveInfo",
        type : "post", 
        data: projectId, 
        dataType : "json",
        contentType : "application/json",
        success : function(info) 
        		  {
		        	if(info.status == "NotFound") alert(defs.i18n.project.error.notFound);
					if(info.status == "Unauthorized") alert(defs.i18n.project.error.unauthorized);
					if(info.status == "Failure") alert(defs.i18n.error.failure + ":" + info.message);
					if(info.status != "Success") return;
					var ret = createInfoPane(info.response, projectId, defs);
					var pane = ret[0];
					var count = ret[1];
					if(count == 0)
					{
						$('#project-info-pane').hide();
						$('#right-pane-overlay-content').hide();
						if($('#empty-pane-msg').length == 0)
						{
							var emptyPaneMsg = document.createElement("div");
							emptyPaneMsg.id = "empty-pane-msg";
							emptyPaneMsg.innerHTML = defs.i18n.info.noInfo;
							$('#right-pane-overlay').append(emptyPaneMsg);
						}
						$('#empty-pane-msg').show();
					}else
					{	
						$('#empty-pane-msg').hide();
						$('#right-pane-overlay-content').show();
						$('#project-info').empty();
						var paneCon = document.createElement('div');
						$(paneCon).append(pane);
						$('#project-info').append(paneCon);
						//$('.taskRecordDeleteButtons').hide();
						
						$('.project-category-content > table').each(function() {
							$(this).dataTable({"bFilter" : true,
								 "bInfo" : false,
								 "bLengthChange" : false,
								 "bSort" : false,
								 "sDom" : "<'row'<'col-xs-6'l>r>t", /*
								   "aoColumnDefs": [
								                  { "bSortable": false, "aTargets": [0,6] },
								                  { "bSearchable": false, "aTargets": [0,6] }
								                ],*/
								 "oLanguage": {
							            "sZeroRecords": defs.i18n.info.noInfo
							        },
								 "iDisplayLength": this.count,
								 "bAutoWidth": false,
								 "bDestroy" : false});
						});
						
						$('#project-info-pane table > tbody tr').hover(
								function(ev)
								{
									var editDiv;
									if($(ev.target).hasClass('row'))
									{
										if($(ev.target).find('.pA-value input').length > 0)
											return;
										editDiv = $(ev.target).find('.pA-edit');
									}
									else
									{
										if($(ev.target).closest('.row').find('.pA-value input').length > 0)
											return;
										editDiv = $(ev.target).closest('.row').find('.pA-edit');
									}
									if(editDiv.length == 0)
										return;
									editDiv = editDiv[0];
									if(!editDiv.attrInfo)
										return;
									if($.inArray(editDiv.attrInfo.taxonomy, defs.projectEditableTaxonomies) != -1)
									{
										$(editDiv).show();
									}
								},
								function(ev)
								{
									var editDiv;
									if($(ev.target).hasClass('row'))
										editDiv = $(ev.target).find('.pA-edit');
									else
										editDiv = $(ev.target).closest('.row').find('.pA-edit');
									if(editDiv.length == 0)
										return;
									editDiv = editDiv[0];
									$(editDiv).hide();
									if(!editDiv.attrInfo)
										return;
								});
						
						adjustOverlayHeight();
					}
					applyFuncs(postProcessingFuncs, info.response);
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

function datepickerListener(ev, orientation)
{
	var opts = {format : "dd/mm/yyyy", language: 'el' };
	if(isPresent(orientation))
		opts.orientation = orientation;
	
	$(ev.target).datepicker(opts)
		.on('show', function(ev) {
		$('.datepicker').maxZIndex(); 
	});
	$(ev.target).datepicker('show');
}

function enableInputPlugin(els, traits, valueProcessing, doEnablePlugin, postProcessingFuncs)
{

	if(!isPresent(valueProcessing))
		valueProcessing = function(values) { return values;};
		
	$.ajax({ 
        url : traits.url,
        type : "post", 
        contentType : isPresent(traits.contentType) ? traits.contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
        data : isPresent(traits.data) ? traits.data : undefined,
        success : function(response) 
        		  {
        			doEnablePlugin(valueProcessing(response), els);
        			applyFuncs(postProcessingFuncs, response);
        			return response;
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

function enableTypeahead(els, traits, valueProcessing)
{
	var doEnableTh = function (values, els)
					{
						for(var i=0; i<els.length; i++)
						{
							$(els[i]).typeahead('destroy');
							$(els[i]).typeahead([
										{
											name: traits.name,
											local: values,
											limit: 10
											}
											]);
							var cls = ['input-sm', 'input-lg', 'input-xs'];
							var i; 
							for(i=0; i<cls.length; i++)
							{
				    			if($(els[i]).hasClass(cls[i]))
				    				$(els[i]).parent().find('.tt-hint').addClass(cls[i] + " form-control");
							}
						  }
					};
					
	enableInputPlugin(els, traits, valueProcessing, doEnableTh);
		
}

function enableTagAutoComplete(els, traits, valueProcessing)
{
	var doEnableTh = function (values, els)
					{
						for(var i=0; i<els.length; i++)
						{
							$(els[i]).tagautocomplete('destroy');
							$(els[i]).tagautocomplete([
										{
											name: traits.name,
											source: values,
											}
											]);
							var cls = ['input-sm', 'input-lg', 'input-xs'];
							var i; 
							for(i=0; i<cls.length; i++)
							{
				    			if($(els[i]).hasClass(cls[i]))
				    				$(els[i]).parent().find('.tt-hint').addClass(cls[i] + " form-control");
							}
						  }
					};
		
	enableInputPlugin(els, traits, valueProcessing, doEnableTh);
	
}

function enableSearchDropdown(elIds, traits, valueProcessing, postProcessingFuncs)
{
	var els = [];
	var doEnableDropdown = 
		function(values, els)
		{
			for(var i=0; i<els.length; i++)
			{
				$(els[i]).empty();
				$(els[i]).append('<ul class="search-attributes"></ul>');
				if($('#'+$(els[i])[0].id+'-dropdown').length == 0)
				{	
					$(els[i]).parent().after('<div id="' + $(els[i])[0].id+'-dropdown" class="dropdown">\
									  <a class="dropdown-toggle" role="button" data-toggle="dropdown" href="#"></a>\
							  		  <div class="scroll-menu">\
									  <ul class="dropdown-menu scroll-menu" role="menu">\
							  		  </ul>\
									  </div>\
									 </div>');
				}
				var ddown = $(els[i]).parent().parent().find('#'+$(els[i])[0].id+'-dropdown')[0];
				ddown.geographicAttribute = true;
				ddown.currTaxonomy = traits.defs.geographyHierarchy[0];
				ddown.currAttribute = null;
				ddown.geogTaxonomy = traits.defs.geographyTaxonomyType;
				ddown.geographicTaxonomies = traits.defs.geographyHierarchy; //TODO alt taxonomies
				ddown.remainingAttributes = [];
				ddown.remainingAttributes = ddown.remainingAttributes.concat(traits.defs.projectInfoCategoryTypes['Planning']);//TODO searchable attributes
				ddown.selectedAttributes = [];
				ddown.selectedNonGeographic = false;
				ddown.selectedNGValue = false;
				ddown.nonGeographicTransition = false;
				ddown.allowClose = false;
				ddown.searchInput = els[i];
				var ddownOpts = $(ddown).find('ul')[0];
				$(ddownOpts).empty();
				$(ddownOpts).append('<li class="ddown-header"><a href="#">'+ defs.i18n.select + ' ' + defs.i18n.taxon.geogCausative[0] + '</a></li>');
				$(ddownOpts).append('<li class="divider"></li>');
				for(var j=0; j<values.length; j++)
				{
					var li = document.createElement('li');
					var val = document.createElement('a');
					val.href = '#';
					val.innerHTML = values[j].mappedValue;
					val.searchValue = values[j];
					val.searchValue.term = values[j].name;
					li.appendChild(val);
					ddownOpts.appendChild(li);
					$(li).find('a').click(function(ev) { searchDropdownSelectListener(ev); });
				}
				//$(ddown).dropdown();
				//$(ddown).find('.dropdown-toggle').dropdown();
				$(ddown).find('.dropdown-toggle').dropdown('toggle');
				
				adjustDropdownHeight();
				//if(enhanceScrollbars())
				//	$(ddown).find('div.scroll-menu').jScrollPane({mouseWheelSpeed: 40});
				
				$(els[i]).click(function() { $(ddown).find('.dropdown-toggle').dropdown('toggle');});
				$(ddown).on('hide.bs.dropdown', 
					function(ev)
					{
						if(ddown.allowClose == false && ddown.remainingAttributes.length > 0)
						{
							ev.stopPropagation();
							return false;
						}else
						{
							searchShapesByAttributes();
							$(ddown).remove();
						}
					});
			}
		}
	
	for(var i=0; i<elIds.length; i++)
		els.push($('#'+elIds[i]))[0];
	enableInputPlugin(els, traits, valueProcessing, doEnableDropdown, postProcessingFuncs);
}

function searchDropdownSelectListener(ev, options)
{
	if(isPresent(ev)) $(ev.target).off();
	if(isPresent(ev) && $(ev.target).parent().hasClass('ddown-header'))
		return;
	var ddown = isPresent(ev) ? $(ev.target).closest('.dropdown')[0] : options.ddown;
	var searchValue = isPresent(ev) ? ev.target.searchValue : options.searchValue;
	var ddownOpts = $(ddown).find('ul');

	$(ddownOpts).find('li').off('click');
	
	if(ev && ev.target.searchValue && ev.target.searchValue.taxonomy) //handle multiple clicks before dropdown is repopulated
	{
		if(ddown.geographicAttribute == true)
		{
			if(ev.target.searchValue.taxonomy !=  ddown.currTaxonomy)
				return;
		}else
		{
			if(ev.target.searchValue.taxonomy != ddown.currAttribute)
				return;
		}
		
	}
	var selNonGeographic = ddown.selectedNonGeographic;
	if(ddown.geographicAttribute == false)
	{
		if(ddown.selectedNonGeographic == false)
		{
			ddown.currAttribute = searchValue;
			ddown.selectedNonGeographic = true;
			ddown.selectedNGValue = false;
		}else if(ddown.selectedNGValue == false)
		{
			ddown.selectedNGValue = true;
		}else
		{
			if(ddown.nonGeographicTransition == true)
			{
				ddown.nonGeographicTransition = false;
				ddown.selectedNonGeographic = false;
				ddown.selectedNGValue = false;
			}else
			{
				ddown.selectedNonGeographic = true;
				ddown.selectedNGValue = false;
			}
		}
	}
	
	var taxonIndex = null;
	if(ddown.geographicAttribute == true)
	{
		taxonIndex = $.inArray(ddown.currTaxonomy, ddown.geographicTaxonomies);
		taxonIndex++;
		ddown.currTaxonomy = ddown.geographicTaxonomies[taxonIndex];
	}
	
	var nextSelect = function(selected, values) 
	{ 
		if((ddown.geographicAttribute == true || ddown.selectedNGValue == true) && isPresent(selected))
		{
			var val = document.createElement('li');
			val.className = 'search-attribute ' +( ddown.geographicAttribute ? 'ga' : 'nga');
			val.attribute = {name : ddown.geographicAttribute == true ? ddown.geographicTaxonomies[taxonIndex-1] : ddown.currAttribute, value : selected.mappedValue};
			if(ddown.geographicAttribute == true) val.attribute.term = selected.term;
			val.innerHTML = (ddown.geographicAttribute ? defs.i18n.taxon.geog[taxonIndex-1] : defs.i18n.taxon[val.attribute.name]) + ": " + val.attribute.value;
			var attrClose = document.createElement('a');
			attrClose.href = '#';
			attrClose.className = 'search-attribute-close ' + (ddown.geographicAttribute ? 'ga' : 'nga');
			val.appendChild(attrClose);
			$(attrClose).click(removeSearchAttributeListener);
			$(ddown.searchInput).find('.search-attributes').append(val);
		}
		
		if(ddown.geographicAttribute == true)
		{
			$(ddownOpts).empty();
			$(ddownOpts).append('<li class="ddown-header"><a href="#"></a></li>');
			$(ddownOpts).append('<li class="divider"></li>');
			//taxonIndex++;
			if(taxonIndex == ddown.geographicTaxonomies.length)
			{
				ddown.geographicAttribute = false;
				ddown.currAttribute = ddown.remainingAttributes[0];
			}
			var geogIndex = $.inArray(ddown.geogTaxonomy, ddown.geographicTaxonomies);
			if(taxonIndex > geogIndex)
				$(ddownOpts).append('<li><a href="#">'+defs.i18n.search.all[taxonIndex]+'</a></li>');
			$(ddownOpts).find('li').click(function(ev) { searchDropdownSelectListener(ev);});
		}else if(ddown.selectedNonGeographic == true && ddown.selectedNGValue == true)
		{
			var attrIndex = $.inArray(ddown.currAttribute, ddown.remainingAttributes);
			if(attrIndex != -1)
			{
				ddown.selectedAttributes.push(ddown.remainingAttributes[attrIndex]);
				ddown.remainingAttributes.splice(attrIndex, 1);
			}
		}
		
		var valsToAppend = values;
		
		var taxonVals = ddown.geographicAttribute == false && (ddown.selectedNonGeographic == false || (ddown.selectedNonGeographic == true && ddown.selectedNGValue == true));
		var attrVals = ddown.geographicAttribute == false && ddown.selectedNonGeographic == true && ddown.selectedNGValue == false;
		
		var headerText = null;
		if(ddown.geographicAttribute == true)
		{
			if(taxonIndex < defs.i18n.taxon.geog.length)
				headerText = defs.i18n.select + ' ' + defs.i18n.taxon.geogCausative[taxonIndex];
			else
				headerText = defs.i18n.search.selectAttribute;
		}
		else
		{
			if(taxonVals)
				headerText = defs.i18n.search.selectAttribute;
			else
				headerText = defs.i18n.search.selectValueForAttribute + ': ' + defs.i18n.taxon[ddown.currAttribute];
		}
		$(ddownOpts).find('li.ddown-header a').html(headerText);

		var closeDiv='';
		if(taxonVals)
		{
			valsToAppend = ddown.remainingAttributes;
			closeDiv = '<div class="col-xs-2 dropdown-close"></div>';
		}
		
		if(ddown.geographicAttribute == false)
		{
			$(ddownOpts).empty();
			$(ddownOpts).append('<li>'+(taxonVals ? '<div class="row"><div class="col-xs-10">' : '') + '<a href="#">'+ headerText + '</a>' + (taxonVals? '</div>'+closeDiv+'</div>' : '') + '</li>');
			$(ddownOpts).append('<li class="divider"></li>');
		}
		
		if(taxonVals)
			$(ddownOpts).find('.dropdown-close').click(function()
				{
				 	if($('input[name=searchType]:checked').val() == 'MAP' && ddown.selectedAttributes.length == 0)
				 		alert(defs.i18n.search.typeRequiresAttributes);
				 	else
				 		ddown.allowClose = true;
				});
		
		var appended = 0;
		for(var i=0; i<valsToAppend.length; i++)
		{
			if(attrVals == true && !isPresent(valsToAppend[i]))
				continue;
			var li = document.createElement('li');
			var val = document.createElement('a');
			val.href = '#';
			val.innerHTML = taxonVals ? defs.i18n.taxon[valsToAppend[i]] : (attrVals ? valsToAppend[i] : valsToAppend[i].mappedValue);
			val.searchValue = taxonVals ? valsToAppend[i] : 
				(attrVals ? {name: ddown.currAttribute, taxonomy: ddown.currAttribute, mappedValue: valsToAppend[i]} : 
					{name: valsToAppend[i].name, term : valsToAppend[i].name, taxonomy: valsToAppend[i].taxonomy, mappedValue: valsToAppend[i].mappedValue});
			li.appendChild(val);
			ddownOpts[0].appendChild(li);
			$(li).find('a').click(function(ev) { searchDropdownSelectListener(ev); });
			appended++;
		}
		if(attrVals == true && appended == 0)
		{
			var li = document.createElement('li');
			var val = document.createElement('a');
			val.href = '#';
			val.innerHTML = defs.i18n.search.noValues;
			li.appendChild(val);
			ddownOpts[0].appendChild(li);
			$(li).find('a').click(function(ev) { $(this).off('click'); searchDropdownSelectListener(ev); });
			setTimeout(function(){$(li).find('a').click();}, 1000);
		}
		$(ddown).find('.dropdown-toggle').dropdown('toggle');
		adjustDropdownHeight();
		//if(enhanceScrollbars())
		//	$(ddown).find('div.scroll-menu').jScrollPane({mouseWheelSpeed: 40});
	};
	

	if(ddown.geographicAttribute == false && ddown.remainingAttributes.length == 0)
		return;
	
	
	if(ddown.geographicAttribute == true && !isPresent(searchValue))
	{
		if($.inArray($(ev.target).html(), defs.i18n.search.all) != -1)
		{
			ddown.geographicAttribute = false;
			ddown.nonGeographicTransition = true;
			ddown.currAttribute = ddown.remainingAttributes[0];
		}
	}
	

	if(ddown.geographicAttribute == true || (ddown.selectedNonGeographic == true && ddown.selectedNGValue == false))
	{
		if(ddown.geographicAttribute == false)
			ddown.currAttribute = searchValue;
		
		$.ajax({ 
	        url : ddown.geographicAttribute == true ? 'geography/children' : 'geography/attributeValues',
	        type : "post", 
	        contentType : 'application/json',
	        data : ddown.geographicAttribute == true ? searchValue.name : searchValue,
	        success : function(response) 
	        		  {
	        			/*if(ddown.geographicAttribute == true) 
	        				ddown.currTaxonomy = ddown.geographicTaxonomies[i-1];*/
	        			nextSelect(searchValue, response);
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
	}else
	{
		nextSelect(searchValue);
	}
}

function removeSearchAttributeListener(ev)
{
	var attribute = $(ev.target).parent()[0].attribute;
	var ddown = $('#'+$(ev.target).closest('.search-widget')[0].id+'-dropdown')[0];
	/*ddown.geographicAttribute = true;
	ddown.currTaxonomy = traits.defs.geographyHierarchy[0];
	ddown.currAttribute = null;
	ddown.geogTaxonomy = traits.defs.geographyTaxonomyType;
	ddown.geographicTaxonomies = traits.defs.geographyHierarchy; //TODO alt taxonomies
	ddown.remainingAttributes = [];
	ddown.remainingAttributes = ddown.remainingAttributes.concat(defs.projectInfoCategoryTypes['Planning']);//TODO searchable attributes
	ddown.selectedAttributes = [];
	ddown.selectedNonGeographic = false;
	ddown.selectedNGValue = false;
	ddown.nonGeographicTransition = false;
	ddown.allowClose = false;
	ddown.searchInput = els[i];*/
	if($(ev.target).parent().hasClass('ga'))
	{
		var geogIndex = $.inArray(attribute.name, ddown.geographicTaxonomies);

		var ga = $(ddown.searchInput).find('li.search-attribute.ga');
		var previousGa = null;
		var previousGaAttribute = null;
		
		for(var i=0; i<ga.length; i++)
		{
			gaIndex = $.inArray(ga[i].attribute.name, ddown.geographicTaxonomies);
			if(gaIndex >= geogIndex)
				$(ga[i]).remove();
			if(gaIndex == geogIndex-1)
			{
				previousGa = ga[i];
				previousGaAttribute = ga[i].attribute;
				$(ga[i]).remove();
			}
		}
		if(!isPresent(previousGa))
		{
			var storedNga = $(ddown.searchInput).find('li.search-attribute.nga').detach();
			enableSearchDropdown([$('#search-items')[0].id], 
				{
                	url : "geography/terms", 
                	name:defs.i18n.taxon[ddown.geographicTaxonomies[0]], 
                	contentType : "application/json", 
                	data : ddown.geographicTaxonomies[0], 
                	defs : defs
                }, 
                null,
                [{func : function()
		                {
		                	$(ddown.searchInput).find('ul').append(storedNga);
		                	for(var i=0; i<storedNga.length; i++)
		                	{
		                		var attrIndex = $.inArray(storedNga[i].attribute.name, ddown.remainingAttributes);
		            			ddown.selectedAttributes.push(ddown.remainingAttributes[attrIndex]);
		            			ddown.remainingAttributes.splice(attrIndex, 1);
		                	}
		                }, args: []}]); 
		}
		else
		{
			ddown.geographicAttribute = true;
			ddown.currTaxonomy = previousGaAttribute.name;//ddown.geographicTaxonomies[geogIndex];
			ddown.selectedNonGeographic = false;
			ddown.selectedNGValue = false;
			ddown.nonGeographicTransition = false;
			ddown.allowClose = false;
			//	Object { name="MunicipalityD._spartis", term="MunicipalityD._spartis", mappedValue="Δ. ΣΠΑΡΤΗΣ"}
			//	Object { name="Prefecture", value="Ν. ΛΑΚΩΝΙΑΣ", term="PrefLakonia"}
			var opts = {
				ddown: ddown,
				searchValue: {
					name: previousGaAttribute.term,
					term: previousGaAttribute.term,
					mappedValue : previousGaAttribute.value,
				}
			};
			searchDropdownSelectListener(null, opts);
		}
	}else if($(ev.target).parent().hasClass('nga'))
	{
		var attrIndex = $.inArray(attribute.name, ddown.selectedAttributes);
		if(attrIndex != -1)
		{
			ddown.remainingAttributes.push(ddown.selectedAttributes[attrIndex]);
			ddown.selectedAttributes.splice(attrIndex, 1);
		}
		
		$(ev.target).parent().remove();
		
		var prevAttr = $(ddown.searchInput).find('li.search-attribute.nga');
		var opts = null;
		if(prevAttr.length == 0)
		{
			var ga = $(ddown.searchInput).find('li.search-attribute.ga');
			var maxGa = null;
			maxGaIndex = -1;
			var maxGaAttribute = null;
			
			for(var i=0; i<ga.length; i++)
			{
				gaIndex = $.inArray(ga[i].attribute.name, ddown.geographicTaxonomies);
				if(gaIndex >= maxGaIndex)
				{
					maxGaIndex = gaIndex;
					maxGa = ga[i];
					maxGaAttribute = ga[i].attribute;
				}
			}
			$(maxGa).remove();
			
			ddown.geographicAttribute = true;
			ddown.selectedNonGeographic = false;
			ddown.selectedNGValue = false;
			ddown.currTaxonomy = maxGaAttribute.name;
			
			opts = {
				ddown: ddown,
				searchValue: {
					name: maxGaAttribute.term,
					term: maxGaAttribute.term,
					mappedValue: maxGaAttribute.value
				}
			};
			
		}else
		{
			var prevAttribute = prevAttr[0].attribute;
			$(prevAttr).remove();
			ddown.selectedNonGeographic = true;
			ddown.selectedNGValue = false;
			ddown.currAttribute = prevAttribute.name;
			opts = {
					ddown: ddown,
					searchValue: {
						name: prevAttribute.name,
						mappedValue: prevAttribute.value
					}
				};

		}
		
		searchDropdownSelectListener(null, opts);
	}
}

function searchShapesByAttributes()
{
	var items = $('#search-items li.search-attribute.nga');
	var attributes = {};
	for(var i=0; i<items.length; i++)
		attributes[items[i].attribute.name] = items[i].attribute.value;
	
	items = $('#search-items li.search-attribute.ga');
	for(var i=0; i<items.length; i++)
		attributes[items[i].attribute.name] = items[i].attribute.term;
	
	/*var mostSpecific = null;
	var maxIndex = -1;
	for(var i=0; i<items.length; i++)
	{
		var ind;
		if((ind = $.inArray(items[i].attribute.name, defs.geographyHierarchy)) > maxIndex)
		{
			maxIndex = ind;
			mostSpecific = items[i].attribute.term;
		}
		if((ind = $.inArray(items[i].attribute.name, defs.altGeographyHierarchy)) > maxIndex)
		{
			maxIndex = ind;
			mostSpecific = items[i].attribute.term;
		}
	}*/
	
	var req = {};
	//req.term = mostSpecific;
	var searchType = $('input[name=searchType]:checked').val();
	req.attributes = attributes;
	req.type = searchType;
	req = JSON.stringify(req);
	
	$.ajax({ 
        url : "shapes/attributeLocate",
        type : "post", 
        data : req,
        dataType : "json",
        contentType : "application/json",
        success : function(response) 
        		  {
        			if(response.status == "Unauthorized") alert(defs.i18n.search.error.unauthorized);
        			if(response.status == "Failure") alert(defs.i18n.search.failure + ":" + response.message);
        			$('#search-items')[0].transform();
        			
        			var ret = createSearchTable(searchType, response.response, defs);
					var tbl = ret[0];
					var count = ret[1];
					if(count == 0)
					{
						$('#searchTbl').hide();
						if($('#empty-pane-msg').length == 0)
						{
							var emptyPaneMsg = document.createElement("div");
							emptyPaneMsg.id = "empty-pane-msg";
							emptyPaneMsg.innerHTML = defs.i18n.info.noInfo;
							$('#right-pane-overlay').append(emptyPaneMsg);
						}
						var t = $('#searchTbl');
						if(t.length > 0)
							$(t).dataTable().fnDestroy();
						$('#right-pane-overlay-content').empty();
						$('#empty-pane-msg').show();
					}else
					{	
						$('#empty-pane-msg').hide();
						var t = $('#searchTbl');
						if(t.length > 0)
							$(t).dataTable().fnDestroy();
						$(t).remove();
						$('#right-pane-overlay-content').empty();
						var tblCon = document.createElement('div');
						$(tblCon).append(tbl);
						$('#right-pane-overlay-content').append(tblCon);
						adjustOverlayHeight();
						var rpp = calculateResultsPerPage();
						$('#searchTbl').dataTable({"bFilter" : true,
													 "bInfo" : true,
													 "bLengthChange" : false,
													 //"aaSorting" : [[]],
													 "sDom" : "<'row'<'col-xs-6'l>r>t",
													 "aoColumnDefs": [
													                  { "bSortable": false, "aTargets": [0, searchType=='MAP'?1:2] },
													                  { "bSearchable": false, "aTargets": [searchType=='MAP'?1:2] }
													                ],
										              "oLanguage": {
												          "sZeroRecords": defs.i18n.info.noInfo
												      },						   
													 "iDisplayLength": rpp,
													 "bAutoWidth": false,
													 "bDestroy" : true});
						optimizeSortIconPosition(defs);
						$(tblCon).append(paginationWidget(defs, 'searchTbl'));
						updatePaginationInfo($('#searchTbl').dataTable(), 'searchTbl');
					}
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

function showCreateProject(ev, defs, postProcessingFuncs)
{
	//var popup = $('#addProjectPopup').modal('show');
	
	$('#addProjectPopup').modal({backdrop: false});
	
	$('#addProjectFormOptional').collapse();
	$('#addProjectPopup').draggable(/*{handle: ".modal-header"} */);
	$('#addProjectPopup').css('left', ($('html').innerWidth()-$('#addProjectPopup').find('.modal-content').outerWidth())*0.5).
	   css('top', ($('html').innerHeight()-$('#addProjectPopup').find('.modal-content').outerHeight())*0.5);
							
	$('#addProjectFormTextBoxwstartDate')[0].addEventListener('click', datepickerListener);
	$('#addProjectFormTextBoxwendDate')[0].addEventListener('click', datepickerListener);
	$('#addProjectFormTextBoxwreminderDate')[0].addEventListener('click', datepickerListener);
	
	enableTypeahead($('#addProjectFormTextBoxclient'), {url : 'projects/listClients', name: 'clients'}, function(values) { return values.response;});
	
	$('#addProjectFormShapeButton').off('click').click(ev, function(ev) 
											 { 
												$('#addProjectPopup').modal('hide');
												$('#tools-drawing').show();
												window.drawingToolbarOn = true;
//												window.mapLayers.editableVector.setVisibility(true);
												$('#close-right-pane').click();
												freezeMainInterface();
												$('#tools-drawing-done').off('click').click(function() 
														{ 
															shapeDrawingDoneListener(); 
															unfreezeMainInterface(); 
															$('.navbar-left-item.navbar-left-selected').click();
															$('#addProjectPopup').modal('show');
														}
													);
												$('#tools-drawing-cancel').off('click').click(function() {
													shapeDrawingCancelListener();
													unfreezeMainInterface(); 
													$('.navbar-left-item.navbar-left-selected').click();
													$('#addProjectPopup').modal('show');
												});
											 });
	$('#addProjectFormSaveButton').off('click').click(ev, function(ev) 
			 { 
				updateProject(null, true, [{func: retrieveProjects, args: [null, defs, [{func: function(){ $('#addProjectPopup').modal('hide'); 
				//window.mapLayers.editableVector.removeAllFeatures(); 
				disableDrawingControls();}, args:[]}]]}]); 
			 });
}

function showCreateTask(ev, projectId, defs, postProcessingFuncs)
{
	$('#addTaskPopup').modal({backdrop: false});
	$('#addTaskPopup').draggable(/*{handle: ".modal-header"} */);
	$('#addTaskPopup').css('left', ($('html').innerWidth()-$('#addTaskPopup').find('.modal-content').outerWidth())*0.5).
	   css('top', ($('html').innerHeight()-$('#addTaskPopup').find('.modal-content').outerHeight())*0.5);
							
	$('#addTaskFormTextBoxstartDate').off('click').click(datepickerListener);
	$('#addTaskFormTextBoxendDate').off('click').click(datepickerListener);
	$('#addTaskFormTextBoxreminderDate').off('click').click(datepickerListener);
	
	$('#addTaskFormSaveButton').off('click').click(ev, function(ev) 
											 { 
												updateTask(projectId, true, [{func: retrieveTasks, args: [projectId, defs, []]}]); 
												$('#addTaskPopup').modal('hide');
											 });
}

function showCreateAttribute(ev, projectId, defs, postProcessingFuncs)
{
	$('#addAttributeFormRadiosourceExisting').prop('disabled', false);
	
	$('#addAttributePopup').modal({backdrop: false});
	$('#addAttributePopup').draggable(/*{handle: ".modal-header"} */);
	$('#addAttributePopup').css('left', ($('html').innerWidth()-$('#addAttributePopup').find('.modal-content').outerWidth())*0.5).
	   css('top', ($('html').innerHeight()-$('#addAttributePopup').find('.modal-content').outerHeight())*0.5);
	
	$('#addAttributeForm input[name=attributeSource]').click(
			function(ev)
			{
				var existingAttrs = [];
				$('#project-info-pane .pA-edit').each(
						function() 
						{ 
							if(this.attrInfo)
								existingAttrs.push(this.attrInfo.taxonomy);
						}
				);
				
				var radioVal = $(ev.target).val();
				if(radioVal == 'existing')
				{
					$('#addAttributeFormLabelselector').text(defs.i18n.attribute.attribute);
					$.ajax({ 
				        url : "projects/commonUserAttributes",
				        type : "post", 
				        data : projectId,
				        dataType : "json",
				        contentType : "application/json",
				        success : function(response) 
				        		  {
				        			if(response.status == "NotFound") alert(defs.i18n.project.error.notFound);
				        			if(response.status == "Unauthorized") alert(defs.i18n.project.error.unauthorized);
				        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
									if(response.status == "Success")
				        			{
										var attrs = response.response;
										var selector = $('#addAttributeFormselector');
										$(selector).empty();
										var count = 0;
										for(var i=0; i<attrs.length; i++)
										{
											if($.inArray(attrs[i].taxonomy, existingAttrs) == -1)
											{
												var option = document.createElement('option');
												option.innerHTML = attrs[i].name;
												option.value = attrs[i].taxonomy;
												$(selector).append(option);
												count++;
											}
										}
										if(count == 0)
										{
											$('#addAttributeFormRadiosourceExisting').prop('disabled', true);
											$('#addAttributeFormRadiosourceNew').click();
										}else
										{
											$('#addAttributeFormname').prop('disabled', true);
											$('#addAttributeFormname').html($(selector).val());
											$('#addAttributeFormselector').off('change').change(
													function(ev)
													{
														$('#addAttributeFormname').val($(selector).find('option:selected').text());
													});
											$('#addAttributeFormselector').change();
										}
				        			}
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
				}else
				{
					$('#addAttributeFormLabelselector').text(defs.i18n.attribute.category);
					$('#addAttributeFormname').prop('disabled', false);
					$('#addAttributeFormname').val('');
					var selector = $('#addAttributeFormselector');
					$(selector).off('change');
					$(selector).empty();
					var count = 0;
					for(var i=0; i<defs.projectInfoCategories.length; i++)
					{
						var option = document.createElement('option');
						option.innerHTML = defs.i18n.taxon[defs.projectInfoCategories[i]];
						option.value = defs.projectInfoCategories[i];
						$(selector).append(option);
						count++;
					}
				}
			});
	
	$('#addAttributeFormSaveButton').off('click').click(ev, 
			function(ev) 
			 { 
				var category, attrName, attrValue;
				var radioVal = $('#addAttributeForm input[name=attributeSource]:checked').val();
				if(radioVal == 'existing')
				{
					category = findAttributeCategory($('#addAttributeFormselector').val(), defs);
				}else
					category = $('#addAttributeFormselector').val();
				attrName = $('#addAttributeFormname').val();
				attrValue = $('#addAttributeFormvalue').val();
				projectAttributeCreate(projectId, category, attrName, attrValue, 
						[{func: retrieveInfo, args: [projectId, defs, []]},
						 {func: function(taxName) {/*defs.customUserTaxonomyNames[taxName] = attrName;*/}, args: ["__funcRet"]}]);
				$('#addAttributePopup').modal('hide');
			 });
	$('#addAttributeFormRadiosourceExisting').click();
}

function showCreateDocument(ev, projectId, defs, postProcessingFuncs)
{
	$('#addDocumentPopup').modal({backdrop: false});
	$('#addDocumentPopup').draggable(/*{handle: ".modal-header"} */);
	$('#addDocumentPopup').css('left', ($('html').innerWidth()-$('#addDocumentPopup').find('.modal-content').outerWidth())*0.5).
						   css('top', ($('html').innerHeight()-$('#addDocumentPopup').find('.modal-content').outerHeight())*0.5);
							
	$('#addDocumentFormSaveButton').off('click').click(ev, function(ev) 
											 {
												updateDocument(null, true, true, [{func: addProjectDocument, args:[projectId, "__funcRet", [{func: retrieveDocuments, args: [projectId, defs, []]}]]}]); 
												$('#addDocumentPopup').modal('hide');
											 });
}

function retrieveProjectDetails(id, postProcessingFuncs)
{
	$.ajax({ 
        url : "projects/retrieve",
        type : "post", 
        data : id,
        dataType : "json",
        contentType : "application/json",
        success : function(response) 
        		  {
        			if(response.status == "NotFound") alert(defs.i18n.project.error.notFound);
        			if(response.status == "Unauthorized") alert(defs.i18n.project.error.unauthorized);
        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					applyFuncs(postProcessingFuncs, response.response);
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

function retrieveTaskDocuments(id, postProcessingFuncs)
{
	$.ajax({ 
        url : "projects/retrieveTaskDocuments",
        type : "post", 
        data : id,
        dataType : "json",
        contentType : "application/json",
        success : function(response) 
        		  {
        			if(response.status == "NotFound") alert(defs.i18n.task.error.notFound);
        			if(response.status == "Unauthorized") alert(defs.i18n.task.error.unauthorized);
        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					applyFuncs(postProcessingFuncs, response.response);
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

function retrieveDocumentTasks(id, projectId, postProcessingFuncs)
{
	req = "projectId=" + projectId + "&documentId="+id;
	$.ajax({ 
        url : "projects/retrieveDocumentTasks",
        type : "post", 
        data : req,
        success : function(response) 
        		  {
		        	if(response.status == "ProjectNotFound") alert(defs.i18n.project.error.notFound);
					if(response.status == "DocumentNotFound") alert(defs.i18n.document.error.notFound);
        			if(response.status == "Unauthorized") alert(defs.i18n.task.error.unauthorized);
        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					applyFuncs(postProcessingFuncs, response.response);
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


function updateProject(id, create, postProcessingFuncs)
{
	var projectForm = $('#'+(create ? 'add' : 'edit')+'ProjectForm')[0];
	if(!create && projectForm.dataChanged == false)
	{
		applyFuncs(postProcessingFuncs, null);
		return;
	}
	
	var values = $(projectForm).serializeArray();
	
	var reqVals = {};
	for(var i=0; i<values.length; i++)
	{
		reqVals[values[i].name] = {};
		reqVals[values[i].name] = values[i].value;
	}

	var req = {};
	req.project = {};
	
	if(!create)
		req.project.id = id;
	
	if(!isPresent(reqVals["name"]) || $.trim(reqVals["name"]) == "")
	{
		alert(defs.i18n.project.error.noName);
		return;
	}
	req.project.name = reqVals["name"];
	if(isPresent(reqVals["description"]) && $.trim(reqVals["description"]) != "") 
		req.project.description = reqVals["description"];
	if(!isPresent(reqVals["client"]) || $.trim(reqVals["client"]) == "")
	{
		alert(defs.i18n.project.error.noClient);
		return;
	}
	
	if(create /*&& window.mapLayers.editableVector.features.length == 0*/)
	{
		alert(defs.i18n.project.error.noShape);
		return;
	}
	
	if(create /*&& window.mapLayers.editableVector.features.length != 1*/)
	{
		alert(defs.i18n.project.error.singleShape);
		return;
	}
	
	req.project.client = reqVals["client"];
	if(isPresent(reqVals["template"]))
	{
		if(reqVals["template"] == "on" || reqVals["template"] == "true")
			req.project.template = true;
	}
	
//	if(window.mapLayers.editableVector.features.length > 0)
//	{
//		window.mapLayers.editableVector.features[0].geometry.transform(map.getProjectionObject(), map.displayProjection);
//		req.project.shape = window.mapLayers.editableVector.features[0].geometry.toString();
//	}
	
	var workflow = {};
	var workflowDefined = false;
	if(isPresent(reqVals["wname"]) && $.trim(reqVals["wname"]) != "")
	{
		workflow.name = reqVals["wname"];
		workflowDefined = true;
	}
	if(isPresent(reqVals["wstartDate"]) && $.trim(reqVals["wstartDate"]) != "")
	{
		workflow.startDate = reqVals["wstartDate"];
		var dateParts = workflow.startDate.split('/');
		workflow.startDate = new Date(dateParts[2], dateParts[1]-1, dateParts[0], 23, 59, 59).getTime();
		workflowDefined = true;
	}
	if(isPresent(reqVals["wendDate"]) && $.trim(reqVals["wendDate"]) != "")
	{
		workflow.endDate = reqVals["wendDate"];
		var dateParts = workflow.endDate.split('/');
		workflow.endDate = new Date(dateParts[2], dateParts[1]-1, dateParts[0], 23, 59, 59).getTime();
		workflowDefined = true;
	}
	if(isPresent(reqVals["wreminderDate"]) && $.trim(reqVals["wreminderDate"]) != "")
	{
		workflow.reminderDate = reqVals["wreminderDate"];
		var dateParts = workflow.reminderDate.split('/');
		workflow.reminderDate = new Date(dateParts[2], dateParts[1]-1, dateParts[0], 23, 59, 59).getTime();
		workflowDefined = true;
	}
	if(isPresent(reqVals["wdescription"]) && $.trim(reqVals["wdescription"]) != "")
	{
		workflow.description = reqVals["wdescription"];
		workflowDefined = true;
	}
	if(workflowDefined == true)
		req.workflow = workflow;
	
	req = JSON.stringify(req);
	$.ajax({ 
        url : create ? "projects/add" : "projects/update",
        type : "post", 
        data : req,
        dataType : "json",
        contentType : "application/json",
        success : function(response) 
        		  {
        			if(response.status == "NotFound") alert(defs.i18n.project.error.notFound);
        			if(response.status == "Existing") alert(defs.i18n.project.error.existing);
        			if(response.status == "Unauthorized") alert(defs.i18n.project.error.unauthorized);
        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					applyFuncs(postProcessingFuncs, response.response);
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

function updateTask(id, create, postProcessingFuncs)
{
	var taskForm = $('#'+(create ? 'add' : 'edit')+'TaskForm')[0];
	if(!create && taskForm.dataChanged == false)
	{
		applyFuncs(postProcessingFuncs, null);
		return;
	}
	
	var values = $(taskForm).serializeArray();
	
	var reqVals = {};
	for(var i=0; i<values.length; i++)
	{
		reqVals[values[i].name] = {};
		reqVals[values[i].name] = values[i].value;
	}

	var req = {};
	
	if(!create)
		req.id = id;
	else
		req.project = id;
	
	if(!isPresent(reqVals["name"]) || $.trim(reqVals["name"]) == "")
	{
		alert(defs.i18n.task.error.noName);
		return;
	}
	req.name = reqVals["name"];
	
	if(isPresent(reqVals["startDate"]) && $.trim(reqVals["startDate"]) != "")
	{
		req.startDate = reqVals["startDate"];
		var dateParts = req.startDate.split('/');
		req.startDate = new Date(dateParts[2], dateParts[1]-1, dateParts[0], 23, 59, 59).getTime();
	}
	if(isPresent(reqVals["endDate"]) && $.trim(reqVals["endDate"]) != "")
	{
		req.endDate = reqVals["endDate"];
		var dateParts = req.endDate.split('/');
		req.endDate = new Date(dateParts[2], dateParts[1]-1, dateParts[0], 23, 59, 59).getTime();
	}
	if(isPresent(reqVals["reminderDate"]) && $.trim(reqVals["reminderDate"]) != "")
	{
		req.reminderDate = reqVals["reminderDate"];
		var dateParts = req.reminderDate.split('/');
		req.reminderDate = new Date(dateParts[2], dateParts[1]-1, dateParts[0], 23, 59, 59).getTime();
	}
	if(isPresent(reqVals["criticality"]))
		req.critical = reqVals["criticality"];
	
	req = JSON.stringify(req);
	$.ajax({ 
        url : create ? "projects/addTask" : "projects/updateTask",
        type : "post", 
        data : req,
        dataType : "json",
        contentType : "application/json",
        success : function(response) 
        		  {
        			if(response.status == "NotFound") alert(defs.i18n.project.error.notFound);
        			if(response.status == "Existing") alert(defs.i18n.project.error.existing);
        			if(response.status == "Unauthorized") alert(defs.i18n.project.error.unauthorized);
        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					applyFuncs(postProcessingFuncs, response.response);
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

function updateDocument(id, create, contentUpdate, postProcessingFuncs)
{
	var documentForm = $('#'+(create ? 'add' : 'edit')+'DocumentForm')[0];
	
	if(!create && documentForm.dataChanged == false)
	{
		applyFuncs(postProcessingFuncs, null);
		return;
	}
	
	var values = $(documentForm).serializeArray();

	var formData = new FormData();
	
	var inputFile = $(documentForm).find("input[name='documentFile']")[0].files;
	if(create == true && inputFile.length != 1)
	{
		alert(defs.i18n.error.document.noFile);
		return;
	}
	if(inputFile.length == 1 && contentUpdate) formData.append('file', inputFile[0]);
	
	if(!create) formData.append('id', id);
	if(create || !contentUpdate)
	{
		var name = formFindValue(values, 'name');
		var description = formFindValue(values, 'description');
		if(isPresent(name) && name != 'None') formData.append('name', $.trim(name));
		formData.append('description', $.trim(description));
	}
	
    $.ajax({ 
        url : create ? "documents/add" : "documents/update",
        type : "post", 
        data : formData,
        contentType : false,
        processData : false,
        
        success : function(response) 
        		  {
		        	if(response.status == "NotFound") alert(defs.i18n.document.error.notFound);
					if(response.status == "Unauthorized") alert(defs.i18n.document.error.unauthorized);
					if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					applyFuncs(postProcessingFuncs, response.response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function addProjectDocument(projectId, documentId, postProcessingFuncs)
{
	var req = "projectId=" + projectId + "&documentId=" + documentId;
	
    $.ajax({ 
        url : "projects/addProjectDocument",
        type : "post", 
        data : req,
        
        success : function(response) 
        		  {
        			if(response.status == "ProjectNotFound") alert(defs.i18n.project.error.notFound);
        			if(response.status == "DocumentNotFound") alert(defs.i18n.document.error.notFound);
					if(response.status == "Unauthorized") alert(defs.i18n.document.error.unauthorized);
					if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					applyFuncs(postProcessingFuncs, response.response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}


function removeProjectDocument(projectId, documentId, postProcessingFuncs)
{
	var req = "projectId=" + projectId + "&documentId=" + documentId;
	
    $.ajax({ 
        url : "projects/removeProjectDocument",
        type : "post", 
        data : req,
        
        success : function(response) 
        		  {
		        	if(response.status == "ProjectNotFound") alert(defs.i18n.project.error.notFound);
					if(response.status == "DocumentNotFound") alert(defs.i18n.document.error.notFound);
					if(response.status == "Unauthorized") alert(defs.i18n.document.error.unauthorized);
					if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					applyFuncs(postProcessingFuncs, response.response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function addTaskDocument(taskId, documentId, defs, direction, postProcessingFuncs)
{
	var req = 'taskId=' + taskId + "&documentId=" + documentId;
	
    $.ajax({ 
        url : "projects/addTaskDocument",
        type : "post", 
        data : req,
        success : function(response) 
        		  {
		        	if(response.status == "TaskNotFound") alert(defs.i18n.task.error.notFound);
					if(response.status == "DocumentNotFound") alert(defs.i18n.document.error.notFound);
        			if(response.status == "Existing") alert(direction == "taskDocument" ? defs.i18n.document.error.existing : defs.i18n.task.error.existing);
        			if(response.status == "Unauthorized") alert(direction == "taskDocument" ? defs.i18n.task.error.unauthorized : defs.i18n.document.error.unauthorized);
        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					applyFuncs(postProcessingFuncs, response.response);
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

function removeTaskDocument(taskId, documentId, postProcessingFuncs)
{
	var req = "taskId=" + taskId + "&documentId=" + documentId;
	
    $.ajax({ 
        url : "projects/removeTaskDocument",
        type : "post", 
        data : req,
        
        success : function(response) 
        		  {
		        	if(response.status == "TaskNotFound") alert(defs.i18n.task.error.notFound);
					if(response.status == "DocumentNotFound") alert(defs.i18n.document.error.notFound);
					if(response.status == "Unauthorized") alert(defs.i18n.task.error.unauthorized);
					if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					applyFuncs(postProcessingFuncs, response.response);
		          }
        		  ,
        error : function(jqXHR, textStatus, errorThrown) 
        		 {
	   			   alert("The following error occured: " + textStatus, errorThrown);
        		 }
      }); 	
}

function createSearchTable(type, items, defs) {
	
    var tbl  = document.createElement('table');
    
    tbl.id = 'searchTbl';
    tbl.className = "table table-condensed table-hover info-pane-table";
    
    var thead = tbl.createTHead();
    var tr = thead.insertRow(-1);
    var th = document.createElement('th');
    
    th.innerHTML = defs.i18n.search.location;
    tr.appendChild(th);
    
    if(type.toLowerCase()=='projects')
    {
    	th = document.createElement('th');
    	th.innerHTML = defs.i18n.project.tableHeader.title;
    	tr.appendChild(th);
    }
    th = document.createElement('th');
    tr.appendChild(th);
 
    var td;
    
    var tbody = document.createElement("tbody");
    tbl.appendChild(tbody);
    
	var count = 0;
	var i;
	for(i=0; i<items.length; i++)
	{
		tr = tbody.insertRow(-1);
		tr.ordinal = count;
		
		td = tr.insertCell(-1);
		td.id = "search-item-breadcrumb_"+count;
		td.className = "search-item-breadcrumb";
		
		for(var j=0; j<items[i].tags.length; j++)
		{
			var bcLink = document.createElement('a');
			bcLink.href='#';
			bcLink.x = items[i].tags[j].x;
			bcLink.y = items[i].tags[j].y;
			bcLink.bounds = items[i].tags[j].bounds;
			bcLink.innerHTML = items[i].tags[j].tag;
			td.appendChild(bcLink);
			
			bcLink.addEventListener('click', function(ev) 
					{ 
						var bounds = ev.target.bounds;
						bounds = new OpenLayers.Bounds(bounds.minx, bounds.miny, bounds.maxx, bounds.maxy);
						var tBounds = bounds.transform(window.map.displayProjection, window.map.getProjectionObject());
						window.map.zoomToExtent(tBounds);
						tBounds = adjustZoomBounds(tBounds);
						window.map.zoomToExtent(tBounds); 
					});
			
			if(j < items[i].tags.length-1)
				$(td).append('<span>/</span>');
		}
		
		if(type.toLowerCase() == 'projects')
		{
			td = tr.insertCell(-1);
			td.id = "search-item-name"+count;
			td.className = "search-item-name";
			td.innerHTML = items[i].name;
			td.projectId = items[i].id;
		}
		
		td = tr.insertCell(-1);
		td.className = 'search-item-controls';
		var buttonCon = document.createElement("div");
		buttonCon.className = "search-item-zoom-button";
		icon = document.createElement("img");
		icon.id = "search-item-zoom-button_"+count;
		icon.point = {};
		icon.point.x = items[i].x;
		icon.point.y = items[i].y;
		icon.bounds = items[i].bounds;
		icon.ordinal = i;
		icon.src = imgLoc(defs, "ITEM_ZOOM_ICON", true);
		icon.addEventListener('click', function(ev) 
				{ 
					var bounds = ev.target.bounds;
					bounds = new OpenLayers.Bounds(bounds.minx, bounds.miny, bounds.maxx, bounds.maxy);
					var tBounds = bounds.transform(window.map.displayProjection, window.map.getProjectionObject());
					window.map.zoomToExtent(tBounds);
					tBounds = adjustZoomBounds(tBounds);
					window.map.zoomToExtent(tBounds); 
				});
		
		buttonCon.appendChild(icon);
		td.appendChild(buttonCon);
		
		if(type.toLowerCase() == 'projects')
		{
			buttonCon = document.createElement("div");
			buttonCon.className = "search-item-details-button";
			icon = document.createElement("img");
			icon.src = imgLoc(defs, "ITEM_DETAILS_ICON", true);
			icon.id = "search-item-details-button_"+count;
			icon.projectId = items[i].id;
			icon.projectName = items[i].name;
			icon.ordinal = i;
			icon.addEventListener('click', function(ev) 
											{ 
												toggleLeftNavbarButton($('#projects')[0], defs, function(defs) 
												 { 
													$('#right-pane-overlay').hide();
													showProjectsPane(defs, [{func: function()
																						 {
																							var opts = {
																									   	defs: defs,
																									   	projectId: ev.target.projectId,
																									   	projectName: ev.target.projectName
																									   };
																							if($('#right-pane-overlay #project-nav').length > 0)
																							{
																								var overlay = $('#right-pane-overlay');
																								$(overlay).children().hide();
																								$('#project-nav').detach().appendTo($('body'));
																								$('#editTaskForm').detach().appendTo($('body'));
																								$('#editDocumentForm').detach().appendTo($('body'));
																								$(overlay).find('#right-pane-overlay-content').appendTo('body');
																								$('#right-pane-overlay-content')[0].id = 'rpoc-project-tabs';
																								$(overlay).empty();
																								
																								$(overlay).prepend(
																										gridRow({
																													qualifier: "xs", 
																													els: [searchWidget({defs: defs, changeObjId: 'projectTbl', changeHandler: dataTableSearchHandler, searchIconHandler: retrieveProjects}), 
																										               newItemWidget(defs, function(ev) { showCreateProject(ev, defs, []); }, "newProject")], 
																										            widths: ["9", "3"],
																										            rowId: "search-create-toolbar-project", 
																										            rowClass: "search-create-toolbar",
																										            colClasses: "search-create-toolbar-item"
																												}));
																								$(overlay).append('<div id="right-pane-overlay-content"></div>');
																								showProjectDetailsPane(opts);
																							}
																								//backToProjectsPane(defs, [{func: showProjectDetailsPane, args: [opts]}]);
																							else
																								showProjectDetailsPane(opts);
																							
																						 }, args: []},
																				  {func: function() { $('#right-pane-overlay').show();}, args: []}]); 
													
												 });
												
											});
			
			buttonCon.appendChild(icon);
			td.appendChild(buttonCon);
		}
		
		var size = new OpenLayers.Size(25,35);
        var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
        var icon = new OpenLayers.Icon(imgLoc(defs, 'MARKER_ICON_LARGE', true), size, offset);   
        var lonlat = new OpenLayers.LonLat(items[i].x,items[i].y);
        lonlat.transform(window.map.displayProjection, window.map.getProjectionObject());
        window.mapLayers.markers.addMarker(new OpenLayers.Marker(lonlat,icon));
        
		count++;
	}
	tbl.entryCount = count;
    return [tbl, count];
}

function createProjectTable(projects, defs) {
	
    var tbl  = document.createElement('table');
    
    tbl.id = 'projectTbl';
    tbl.className = "table table-condensed table-hover info-pane-table";
    
    var thead = tbl.createTHead();
 
    var td;
    
    var tr = thead.insertRow(-1);
    var th = document.createElement("th");
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = defs.i18n.project.tableHeader.title;
    th.className = "projectNames";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = defs.i18n.project.tableHeader.start;
    th.className = "projectStartDates";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = defs.i18n.project.tableHeader.client;
    th.className = "projectClients";
    tr.appendChild(th);
    
    th = document.createElement("th");
    tr.appendChild(th);
    
    var tbody = document.createElement("tbody");
    tbl.appendChild(tbody);
    
	var count = 0;
	var i;
	for(i=0; i<projects.length; i++)
	{
		tr = tbody.insertRow(-1);
		tr.ordinal = count;
		
		td = tr.insertCell(-1);
		td.id = "projectStatus_"+count;
		td.className = "projectStatus";
		
		var icon = document.createElement("div");
		icon.className = "statusIcon";
		var iconId = null;
		tr.projectStatus = projects[i].status;
		tr.projectId = projects[i].id;
		tr.ordinal = i;
		switch(projects[i].status)
		{
		case "INACTIVE":
			iconId = "INACTIVE_ICON";
			break;
		case "ACTIVE":
			iconId = "ACTIVE_ICON";
			break;
		case "COMPLETED":
			iconId = "COMPLETED_ICON";
			break;
		case "CANCELLED":
			iconId = "CANCELLED_ICON";
			break;			
		}
		$(icon).css('background-image', cssImgLoc(defs, iconId, false));
		td.appendChild(icon);
		
		td = tr.insertCell(-1);
		td.id = "projectName_"+count;
		td.className = "projectNames";
		td.innerHTML = projects[i].name;
		td.projectId = projects[i].id;
			
		td = tr.insertCell(-1);
		td.id = "projectStartDate_"+count;
		td.className = "projectStartDates";
		td.innerHTML = timestampToDateString(projects[i].startDate);
		
		td = tr.insertCell(-1);
		td.id = "projectClient_"+count;
		td.className = "projectClient";
		td.innerHTML = projects[i].client;
		
		td = tr.insertCell(-1);
		td.className = "projectRecordActions";
		
		var buttonCon = document.createElement("div");
		buttonCon.className = "projectRecordZoomButtons";
		icon = document.createElement("img");
		icon.id = "projectRecordZoomButton_"+count;
		icon.projectId = projects[i].id;
		icon.ordinal = i;
		icon.src = imgLoc(defs, "ITEM_ZOOM_ICON", true);
		icon.addEventListener('click', function(ev) { zoomToProjectButtonListener(ev, defs); });
		
		buttonCon.appendChild(icon);
		td.appendChild(buttonCon);
		
		buttonCon = document.createElement("div");
		buttonCon.className = "projectRecordEditButtons";
		icon = document.createElement("img");
		icon.src = imgLoc(defs, "ITEM_EDIT_ICON", true);
		icon.id = "projectRecordEditButton_"+count;
		icon.projectId = projects[i].id;
		icon.ordinal = i;
		icon.addEventListener('click', function(ev) { projectEditButtonListener(ev, defs); });
		
		buttonCon.appendChild(icon);
		td.appendChild(buttonCon);
		
		buttonCon = document.createElement("div");
		buttonCon.className = "projectRecordDetailsButtons";
		icon = document.createElement("img");
		icon.src = imgLoc(defs, "ITEM_DETAILS_ICON", true);
		icon.id = "projectRecordDetailsButton_"+count;
		icon.projectId = projects[i].id;
		icon.ordinal = i;
		icon.addEventListener('click', function(ev) { projectDetailsButtonListener(ev, defs); });
		
		buttonCon.appendChild(icon);
		td.appendChild(buttonCon);
		
		buttonCon = document.createElement("div");
		
		icon = document.createElement("img");
		icon.src = imgLoc(defs, "ITEM_DELETE_ICON", true);
		icon.id = "projectRecordDeleteButton_"+count;
		icon.className = "projectRecordDeleteButtons";
		icon.projectId = projects[i].id;
		icon.ordinal = i;
		icon.addEventListener('click', (function(pId, o) 
										{ 
											return function(ev) { projectDeleteButtonListener(ev, pId, o, [{func: retrieveProjects, args: [null, defs, []]}]); }; 
										})(icon.projectId, icon.ordinal));
		
		buttonCon.appendChild(icon);
		td.appendChild(buttonCon);
		
		count++;
	}
	tbl.entryCount = count;
	//var parent = document.getElementById('userTable');
	//$('#projectTbl').dataTable().fnDestroy();
    //var old = document.getElementById('userTbl');
   // parent.removeChild(old);
    //parent.appendChild(tbl);
   //$(document).ready(function(){$('#userTbl').dataTable({"bSort" : false});});
   // $('#userTbl').dataTable();
    return [tbl, count];
}

function populateProjectLayer(projects, defs)
{
	var wkt = new OpenLayers.Format.WKT();
	window.mapLayers.vector.removeAllFeatures();
	for(var i=0; i<projects.length; i++)
	{
		if(isPresent(projects[i].shape))
		{
		    var polygonFeature = wkt.read(projects[i].shape);
		    polygonFeature.geometry.transform(map.displayProjection, map.getProjectionObject());
		    polygonFeature.attributes = {'title' : projects[i].name, 'id': projects[i].id};
		    window.mapLayers.vector.addFeatures([polygonFeature]);
		}
	}
}

function findAttributeCategory(attribute, defs) 
{
		var projectInfoCategory = null;
		for(var i=0; i<defs.projectInfoCategories.length; i++)
		{
			var categoryTypes = defs.projectInfoCategoryTypes[defs.projectInfoCategories[i]];
			for(var j=0; j<categoryTypes.length; j++)
			{
				if(attribute.toLowerCase() == categoryTypes[j].toLowerCase())
					return defs.projectInfoCategories[i];
			}
		}
		
		for(var i=0; i<defs.geographyHierarchy.length; i++)
		{
			if(attribute.toLowerCase() == defs.geographyHierarchy[i].toLowerCase())
				return defs.planningTaxonomyType;
		}
		for(var i=0; i<defs.altGeographyHierarchy.length; i++)
		{
			if(attribute.toLowerCase() == defs.altGeographyHierarchy[i].toLowerCase())
				return defs.planningTaxonomyType;
		}
		return projectInfoCategory;
};

function createInfoPane(info, projectId, defs)
{
	//[{type: {name, value, type, taxonomy}]
	var count = 0;
	var pane = document.createElement('div');
	pane.id = 'project-info-pane';
	pane.projectId = projectId;
	
	var i;
	for(i=0; i<defs.projectInfoCategories.length; i++)
	{
		var categoryRow = document.createElement('div');
		categoryRow.className = 'row';
		pane.appendChild(categoryRow);
		
		var categoryTitle = document.createElement('div');
		categoryTitle.id = 'pCTitle' + defs.projectInfoCategories[i];
		categoryTitle.className = 'col-xs-3 project-category-title';
		categoryTitle.innerHTML = defs.i18n.taxon[defs.projectInfoCategories[i]];
		categoryRow.appendChild(categoryTitle);
		
		var categoryContent = document.createElement('div');
		categoryContent.id = 'pCContent' + defs.projectInfoCategories[i];
		categoryContent.className = 'col-xs-9 project-category-content';
		categoryRow.appendChild(categoryContent);
		
	}

	for(i=0; i<defs.projectInfoCategories.length; i++)
	{
		$(pane).find('#pCContent' + defs.projectInfoCategories[i]).append(
				'<table id="pCCTable'+defs.projectInfoCategories[i]+'" class="table table-condensed table-hover info-pane-table">\
				<thead><tr><th></th></tr></thead>\
				<tbody></tbody>\
				</table>');
		$(pane).find('#pCCTable'+defs.projectInfoCategories[i])[0].count = 0;
		$(pane).find('#pCCTable'+defs.projectInfoCategories[i])[0].category = defs.projectInfoCategories[i];
	}
	
	var keys;
	//geographic attributes go first
	for(i=0; i<defs.geographyHierarchy.length; i++)
	{
		keys = Object.keys(info);
		for(var j=0; j<keys.length; j++)
		{
			if(info[keys[j]].taxonomy == defs.geographyHierarchy[i])
			{
				var geogCategory = findAttributeCategory(info[keys[j]].taxonomy, defs);
				
				var row = createAttributeRow({
					type: info[keys[j]].taxonomy, 
					category: geogCategory, 
					isGeographic: true, 
					value: info[keys[j]].value, 
					defs: defs
				});
				var tr = document.createElement('tr');
				var td = document.createElement('td');
				tr.appendChild(td);
				td.appendChild(row);
				
				var categoryTable = $(pane).find('#pCContent'+geogCategory + ' > table')[0];
				if(!categoryTable.count)
					categoryTable.count = 0;
				categoryTable.count++;
				$(categoryTable).find('tbody').append(tr);
				
				count++;
			}
		}
	}

	for(i=0; i<defs.projectInfoCategories.length; i++)
	{
		var categoryTypes = defs.projectInfoCategoryTypes[defs.projectInfoCategories[i]];
		for(var j=0; j<categoryTypes.length; j++)
		{
			var foundInfo = null;
			keys = Object.keys(info);
			for(var k=0; k<keys.length; k++)
			{
				if(info[keys[k]].type == categoryTypes[j])
				{
					foundInfo = info[keys[k]];
					break;
				}
			}
			var category = findAttributeCategory(categoryTypes[j], defs);
			var attrValue = null;
			if(isPresent(foundInfo))
				attrValue = foundInfo.value;
			var row = createAttributeRow({
				 type: categoryTypes[j],
				 category: category, 
				 isGeographic: false, 
				 value: attrValue, 
				 defs: defs
			});
			
			if($.inArray(categoryTypes[j], defs.projectEditableTaxonomies) != -1)
			{
				$(row).append('<div id="pAttrEdit'+categoryTypes[j]+'" class="pA-edit"><img src="' + imgLoc(defs, "ITEM_EDIT_ICON", true) +'"/></div>');
				var editDiv = $(row).find('.pA-edit')[0];
				$(editDiv).hide();
				editDiv.attrInfo = {
									 taxonomy: isPresent(foundInfo) ? foundInfo.taxonomy : categoryTypes[j],
								     term: isPresent(foundInfo) ? foundInfo.term : null,
									 value: isPresent(foundInfo) ? foundInfo.value : null
									};
				$(editDiv).click(function(ev) { projectAttributeEditListener(ev, defs);});
			}
			
			if(isPresent(foundInfo) && isPresent(foundInfo.document))
			{
				var icon = document.createElement('img');
				icon.src = imgLoc(defs, 'VIEW_DOCUMENT_ICON', true);
				icon.documentId = foundInfo.document;
					
				var iconCon = document.createElement('div');
				iconCon.appendChild(icon);
				iconCon.addEventListener('click', documentViewButtonListener);
				$(row).find("div:last-child").append('<div class="pA-actions"></div>');
				$(row).find("div:last-child .pA-actions").append(iconCon);
				
				icon = document.createElement('img');
				icon.src = imgLoc(defs, 'DOWNLOAD_DOCUMENT_ICON', true);
				icon.documentId = foundInfo.document;
					
				iconCon = document.createElement('div');
				iconCon.appendChild(icon);
				iconCon.addEventListener('click', documentDownloadButtonListener);
				$(row).find("div:last-child .pA-actions").append(iconCon);
			}
			var tr = document.createElement('tr');
			var td = document.createElement('td');
			tr.appendChild(td);
			td.appendChild(row);
			var categoryTable = $(pane).find('#pCContent'+category + ' > table')[0];
			if(!categoryTable.count)
				categoryTable.count = 0;
			categoryTable.count++;
			$(categoryTable).find('tbody').append(tr);
			count++;
		}
	}
	
	return [pane, count];
}

function createAttributeRow(options)
{
	var assignIds = options.assignIds;
	var assignClasses = options.assignClasses;
	var widths = options.widths;
	if(!isPresent(assignIds))
		assignIds = true;
	if(!isPresent(assignClasses))
		assignClasses = true;
	if(!isPresent(widths))
		widths = [6,6];
	
	var attrName = null;
	if(options.isGeographic)
	{
		for(var i=0; i<options.defs.geographyHierarchy.length; i++)
		{
			if(options.defs.geographyHierarchy[i].toLowerCase() == options.type.toLowerCase())
			{
				attrName = options.defs.i18n.taxon.geog[i];
				break;
			}
		}
		if(!isPresent(attrName))
		{
			alert("Invalid geographic attribute: " + options.type);
			return;
		}
	}else
		attrName = options.defs.i18n.taxon[options.type] ? options.defs.i18n.taxon[options.type] : options.defs.customUserTaxonomyNames[options.type];
	
	var row = document.createElement('div');
	row.className = 'row';
	
	var attrNameCol = document.createElement('div');
	if(assignIds) 
		attrNameCol.id = 'pAttrTitle'+options.type;
	attrNameCol.className = 'col-xs-'+ widths[0] + (assignClasses == true ? ' pA-title' : '');
	attrNameCol.innerHTML = attrName;
	row.appendChild(attrNameCol);
	
	var attrValueCol = document.createElement('div');
	if(assignIds)
		attrValueCol.id = 'pAttrValue'+options.type;
	attrValueCol.className = 'col-xs-' + widths[1] + (assignClasses == true ? ' pA-value' : '');
	attrValueCol.innerHTML = formatAttribute(options.value);
	row.appendChild(attrValueCol);
	
	for(var i=2; i<widths.length; i++)
		$(row).append('<div class="col-xs-'+widths[i]+'"></div>');
	
	if(!isPresent(options.value))
		$(attrValueCol).append('<div ' + (assignClasses == true ? 'class="pA-empty"' : '') + '></div>');
	return row;
}

function formatAttribute(value)
{
	if(!isPresent(value))
		return "";
	
	if(!isNaN(value) && isFinite(value))
	{
		if(!/^\d+$/.test(value))
			return parseFloat(value).toFixed(5);
		var intVal = parseInt(value);
		if(intVal === NaN)
			return "";
		return intVal;
	}else
	{
		var formatted = "";
		var parts = value.split(',');
		if(parts.length > 1)
		{
			for(var i=0; i<parts.length; i++)
			{
				if(!isNaN(parts[i]) && isFinite(parts[i]))
					formatted += ((i!=0) ? ", " : "") + parseFloat(parts[i]).toFixed(5);
				else
					formatted += ((i!=0) ? ", " : "") + parts[i];
			}
			return formatted;
		}else
			return toTitleCase(value);
	}
}

function projectAttributeEditListener(ev, defs)
{
	var row = $(ev.target).closest('.row');
	var attrInfo;
	if($(ev.target).hasClass('pA-edit'))
	{
		attrInfo = ev.target.attrInfo;
		$(ev.target).hide();
	}
	else
	{
		attrInfo = $(ev.target).closest('.pA-edit')[0].attrInfo;
		$(ev.target).closest('.pA-edit').hide();
	}
	var valCon = $(row).find('.pA-value');
	if(valCon.length > 0)
	{
		var txtNodes =  getTextNodesIn(valCon);
		if(txtNodes.length > 0)
			valCon[0].savedValue = txtNodes[0].nodeValue;
		$(valCon).empty();
	}else
	{
		valCon = $(row).find('.pA-empty');
		$(valCon).removeClass('pA-empty').addClass('pA-value');
	}
	$(valCon).append('<input type="text" class="input input-sm form-control" />');
	if(valCon[0].savedValue && valCon[0].savedValue.trim !== '')
		$(valCon).find('input').val(valCon[0].savedValue);
	$(row).append('<div class="pA-controls">\
											<img class="pA-save" src="' + imgLoc(defs, "ITEM_DONE_ICON", true) + '"/>\
											<img class="pA-cancel" src="' + imgLoc(defs, "ITEM_CANCEL_ICON", true) + '"/>\
										</div>');
	$(row).find('.pA-save').click(function(ev) 
								  { 
									projectAttributeUpdateListener(ev, defs);
									$(row).find('.pA-controls').remove();
								  });
	$(row).find('.pA-save')[0].attrInfo = attrInfo;
	
	$(row).find('.pA-cancel').click(function()
			{	
				$(valCon).empty();
				if(!valCon[0].savedValue || valCon[0].savedValue.trim() === '')
					$(valCon).append('<div class="pA-empty"></div>');
				else
					$(valCon).html(valCon[0].savedValue);
				$(row).find('.pA-controls').remove();
			});
}

function projectAttributeCreate(projectId, category, name, value, postProcessingFuncs)
{
	var req = {};
	req.projectId = projectId;
	req.attributeClassType = category;
	req.attribute = {
						taxonomy: name,
						term: value,
						name: name,
						value : value
					 };
	$.ajax({ 
        url : "projects/addAttribute",
        type : "post", 
        data : JSON.stringify(req),
        dataType : "json",
        contentType : "application/json",
        success : function(response) 
        		  {
        			if(response.status == "NotFound") alert(defs.i18n.project.error.notFound);
        			if(response.status == "Unauthorized") alert(defs.i18n.project.error.unauthorized);
        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
        			if(response.status == "Success")
        				applyFuncs(postProcessingFuncs);
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

function projectAttributeUpdateListener(ev, defs)
{
	var req = {};
	req.projectId = $('#project-info-pane')[0].projectId;
	var val = $(ev.target).closest('.row').find('input').val();
	if(val.trim() === '')
		return;
	req.attributeClassType = $(ev.target).closest('table')[0].category;
	req.attribute = {
						taxonomy: ev.target.attrInfo.taxonomy, 
						term: ev.target.attrInfo.term ? ev.target.attrInfo.term : val, 
						value: val
					};
	$.ajax({ 
        url : "projects/updateInfo",
        type : "post", 
        data : JSON.stringify(req),
        dataType : "json",
        contentType : "application/json",
        success : function(response) 
        		  {
        			if(response.status == "NotFound") alert(defs.i18n.project.error.notFound);
        			if(response.status == "Unauthorized") alert(defs.i18n.project.error.unauthorized);
        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
        			if(response.status == "Success")
        				retrieveInfo($('#project-info-pane')[0].projectId, defs);
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

function createTaskTable(tasks, projectId, defs) 
{
	
    var tbl  = document.createElement('table');
    
    tbl.id = 'taskTbl';
    tbl.className = "table table-condensed table-hover info-pane-table";
    
    var thead = tbl.createTHead();
 
    var td;
    
    var tr = thead.insertRow(-1);
    var th = document.createElement("th");
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = defs.i18n.task.tableHeader.title;
    th.className = "taskNames";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = defs.i18n.task.tableHeader.start;
    th.className = "taskStartDates taskDates";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = defs.i18n.task.tableHeader.reminder;
    th.className = "taskReminderDates taskDates";
    tr.appendChild(th);
  
    th = document.createElement("th");
    th.innerHTML = defs.i18n.task.tableHeader.end;
    th.className = "taskEndDates taskDates";
    tr.appendChild(th);
    
    th = document.createElement("th");
    th.innerHTML = defs.i18n.task.tableHeader.documentCount;
    th.className = "projectDocumentCount";
    tr.appendChild(th);
    
    th = document.createElement("th");
    tr.appendChild(th);
    
    var tbody = document.createElement("tbody");
    tbl.appendChild(tbody);
    
	var count = 0;
	var i;
	for(i=0; i<tasks.length; i++)
	{
		tr = tbody.insertRow(-1);
		tr.ordinal = count;
		
		td = tr.insertCell(-1);
		td.id = "taskStatus_"+count;
		td.className = "taskStatus";
		
		var icon = document.createElement("div");
		icon.className = "statusIcon";
		var iconId = null;
		tasks[i].projectId = projectId;
		tr.taskStatus = tasks[i].status;
		tr.taskId = tasks[i].id;
		tr.task = tasks[i];
		switch(tasks[i].status)
		{
		case "INACTIVE":
			iconId = "INACTIVE_ICON";
			break;
		case "ACTIVE":
			iconId = "ACTIVE_ICON";
			break;
		case "COMPLETED":
			iconId = "COMPLETED_ICON";
			break;
		case "CANCELLED":
			iconId = "CANCELLED_ICON";
			break;			
		}
		$(icon).css('background-image', cssImgLoc(defs, iconId, false));
		td.appendChild(icon);
		
		td = tr.insertCell(-1);
		td.id = "taskName_"+count;
		td.className = "taskNames";
		td.innerHTML = tasks[i].name;
		td.projectId = tasks[i].id;
			
		td = tr.insertCell(-1);
		td.id = "taskStartDate_"+count;
		td.className = "taskStartDates";
		td.innerHTML = timestampToDateString(tasks[i].startDate);
		
		td = tr.insertCell(-1);
		td.id = "taskEndDate_"+count;
		td.className = "taskEndDates";
		td.innerHTML = timestampToDateString(tasks[i].endDate);
		
		td = tr.insertCell(-1);
		td.id = "taskReminderDate_"+count;
		td.className = "taskReminderDates";
		td.innerHTML = timestampToDateString(tasks[i].reminderDate);
		
		td = tr.insertCell(-1);
		td.id = "projectDocumentCount_"+count;
		td.className = "projectDocumentCount";
		td.innerHTML = tasks[i].numDocuments;
		
		td = tr.insertCell(-1);
		td.className = "taskRecordActions";
		
		var buttonCon = document.createElement("div");
		buttonCon.className = "taskRecordEditButtons";
		icon = document.createElement("img");
		icon.src = imgLoc(defs, "ITEM_EDIT_ICON", true);
		icon.id = "taskRecordEditButton_"+count;
		icon.taskId = tasks[i].id;
		icon.ordinal = i;
		icon.addEventListener('click', function(ev) { taskEditButtonListener(ev, defs); });
		
		buttonCon.appendChild(icon);
		td.appendChild(buttonCon);
		
		buttonCon = document.createElement("div");
		
		icon = document.createElement("img");
		icon.src = imgLoc(defs, "ITEM_DELETE_ICON", true);
		icon.id = "taskRecordDeleteButton_"+count;
		icon.className = "taskRecordDeleteButtons";
		icon.taskId = tasks[i].id;
		icon.ordinal = i;
		icon.addEventListener('click', (function(tId, o) 
										{ 
											return function(ev) { taskDeleteButtonListener(ev, tId, o, [{func: retrieveTasks, args: [projectId, defs, []]}]); }; 
										})(icon.taskId, icon.ordinal));
		
		buttonCon.appendChild(icon);
		td.appendChild(buttonCon);
		
		count++;
	}
	tbl.entryCount = count;
	//var parent = document.getElementById('userTable');
	//$('#projectTbl').dataTable().fnDestroy();
    //var old = document.getElementById('userTbl');
   // parent.removeChild(old);
    //parent.appendChild(tbl);
   //$(document).ready(function(){$('#userTbl').dataTable({"bSort" : false});});
   // $('#userTbl').dataTable();
    return [tbl, count];
}

function createDocumentTable(documents, projectId, defs)
{
	  var tbl  = document.createElement('table');
	    
	    tbl.id = 'documentTbl';
	    tbl.className = "table table-condensed table-hover info-pane-table";
	    
	    var thead = tbl.createTHead();
	 
	    var td;
	    
	    var tr = thead.insertRow(-1);
	    
	    var th = document.createElement("th");
	    th.innerHTML = defs.i18n.document.tableHeader.name;
	    th.className = "documentNames";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
	    th.innerHTML = defs.i18n.document.tableHeader.creationDate;
	    th.className = "documentCreationDates documentDates";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
	    th.innerHTML = defs.i18n.document.tableHeader.taskCount;
	    th.className = "documentTaskCount";
	    tr.appendChild(th);
	    
	    th = document.createElement("th");
	    tr.appendChild(th);
	    
	    var tbody = document.createElement("tbody");
	    tbl.appendChild(tbody);
	    
		var count = 0;
		var i;
		for(i=0; i<documents.length; i++)
		{
			tr = tbody.insertRow(-1);
			tr.ordinal = count;
			
			documents[i].projectId = projectId;
			tr.documentId = documents[i].id;
			tr.document = documents[i];
			
			td = tr.insertCell(-1);
			td.id = "documentName_"+count;
			td.className = "documentNames";
			td.innerHTML = documents[i].name;
			td.projectId = documents[i].id;
				
			td = tr.insertCell(-1);
			td.id = "documentCreationDate_"+count;
			td.className = "documentCreationDates";
			td.innerHTML = timestampToDateString(documents[i].creationDate);
			
			td = tr.insertCell(-1);
			td.id = "documentTaskCount_"+count;
			td.className = "documentTaskCount";
			td.innerHTML = documents[i].numOfWorkflowTasks;
			
			td = tr.insertCell(-1);
			td.className = "documentRecordActions";
			
			var buttonCon = document.createElement("div");
			buttonCon.className = "documentRecordViewButtons";
			icon = document.createElement("img");
			icon.src = imgLoc(defs, "VIEW_DOCUMENT_ICON", true);
			icon.id = "documentRecordViewButton_"+count;
			icon.documentId = documents[i].id;
			icon.ordinal = i;
			icon.addEventListener('click', function(ev) { documentViewButtonListener(ev, defs); });
			
			buttonCon.appendChild(icon);
			td.appendChild(buttonCon);
			
			buttonCon = document.createElement("div");
			buttonCon.className = "documentRecordDownloadButtons";
			icon = document.createElement("img");
			icon.src = imgLoc(defs, "DOWNLOAD_DOCUMENT_ICON", true);
			icon.id = "documentRecordDownloadButton_"+count;
			icon.documentId = documents[i].id;
			icon.ordinal = i;
			icon.addEventListener('click', function(ev) { documentDownloadButtonListener(ev, defs); });
			
			buttonCon.appendChild(icon);
			td.appendChild(buttonCon);
			
			buttonCon = document.createElement("div");
			buttonCon.className = "documentRecordEditButtons";
			icon = document.createElement("img");
			icon.src = imgLoc(defs, "ITEM_EDIT_ICON", true);
			icon.id = "documentRecordEditButton_"+count;
			icon.documentId = documents[i].id;
			icon.ordinal = i;
			icon.addEventListener('click', function(ev) { documentEditButtonListener(ev, defs); });
			
			buttonCon.appendChild(icon);
			td.appendChild(buttonCon);
			
			buttonCon = document.createElement("div");
			
			icon = document.createElement("img");
			icon.src = imgLoc(defs, "ITEM_DELETE_ICON", true);
			icon.id = "documentRecordDeleteButton_"+count;
			icon.className = "documentRecordDeleteButtons";
			icon.documentId = documents[i].id;
			icon.ordinal = i;
			icon.addEventListener('click', (function(dId, o) 
											{ 
												return function(ev) { documentDeleteButtonListener(ev, dId, projectId, o, [{func: retrieveDocuments, args: [projectId, defs, []]}]); }; 
											})(icon.documentId, icon.ordinal));
			
			buttonCon.appendChild(icon);
			td.appendChild(buttonCon);
			
			count++;
		}
		tbl.entryCount = count;
		
	    return [tbl, count];
}

function createLegendTable(defs, layers, tblId, layerType)
{
	var tbl  = document.createElement('table');
    
    tbl.id = tblId;
    tbl.className = "table table-condensed info-pane-table legend-table";

    var tbody = document.createElement("tbody");
    tbl.appendChild(tbody);
    
	var i;
	var count = 0;
	for(i=0; i<layers.length; i++)
	{
		/*<div id="rememberme"> 
		<input type="checkbox" id="rememberme-checkbox" name="_spring_security_remember_me" class="chkbox-bg-white chkbox-large shadowed"> 
		<label id="rememberme-checkbox-label" for="rememberme-checkbox"><span></span></label>
		<p> <h2> Remember me </h2> </p>
		</div> */
		var tr = tbody.insertRow(-1);
		
		var td = tr.insertCell(-1);
		
		var chkBox = document.createElement("div");
		chkBox.id = 'layerChkBoxCon' + layers[i];
		
		var input = document.createElement("input");
		input.id = 'layerChkBox' + layers[i];
		input.type = 'checkbox';
		input.name = layers[i];
		input.className = "chkbox-bg-grey chkbox-large";
		$(input).prop('checked', window.mapLayers.overlays[layers[i]].getVisibility());
		
		var chkBoxLabel = document.createElement('label');
		chkBoxLabel.id = 'layerChkBoxLabel' + layers[i];
		chkBoxLabel.htmlFor = 'layerChkBox' + layer[i];
		var sp = document.createElement('span');
		chkBoxLabel.appendChild(sp);
		
		chkBox.appendChild(input);
		chkBox.appendChild(chkBoxLabel);
		
		td.appendChild(chkBox);
		
		td = tr.insertCell(-1);
		var txt = document.createTextNode(layers[i]);
		td.appendChild(txt);
		
		chkBox.addEventListener('click', (function(layerName)
										 {
											return function(ev)
											{
												window.mapLayers.overlays[layerName].setVisibility($(ev.target).closest('input').prop('checked'));
											};
										 })(layers[i]));
		count++;
	}
	
	return [tbl, count];
}

function createMapsTable(defs, layers, tblId, layerType)
{
	var tbl  = document.createElement('table');
    
    tbl.id = tblId;
    tbl.className = "table table-condensed info-pane-table legend-table";
    
	var i;
	var count = 0;
	
	var tbody = document.createElement("tbody");
    tbl.appendChild(tbody);
    
    var thead = tbl.createTHead();
    
    var tr = thead.insertRow(-1);
    
    var th = document.createElement("th");
    th.colSpan = "2";
    th.innerHTML = defs.i18n.nav.legendMenu.baseMap;
    tr.appendChild(th);
    
    tr = thead.insertRow(-1);
    th = document.createElement("th");
    tr.appendChild(th);
    th = document.createElement("th");
    tr.appendChild(th);
    $(tr).hide(); //created just to satisfy dataTables unique column constraint
    
    for(i=0; i<Object.keys(window.mapLayers.base).length; i++)
    {
    	tr = tbody.insertRow(-1);
		
		td = tr.insertCell(-1);
		
		var chkBox = document.createElement("div");
		chkBox.id = 'layerChkBoxConbase' + i;
		
		var input = document.createElement("input");
		input.id = 'layerChkBoxbase' + i;
		input.type = 'checkbox';
		input.name = Object.keys(window.mapLayers.base)[i];
		input.className = "chkbox-bg-grey chkbox-large";
		$(input).prop('checked', window.mapLayers.base[Object.keys(window.mapLayers.base)[i]].getVisibility());
		
		var chkBoxLabel = document.createElement('label');
		chkBoxLabel.id = 'layerChkBoxLabelbase' + i;
		chkBoxLabel.htmlFor = 'layerChkBoxbase' + i;
		var sp = document.createElement('span');
		chkBoxLabel.appendChild(sp);
		
		chkBox.appendChild(input);
		chkBox.appendChild(chkBoxLabel);
		
		td.appendChild(chkBox);
		
		td = tr.insertCell(-1);
		var txt = document.createTextNode(Object.keys(window.mapLayers.base)[i]);
		td.appendChild(txt);
		
		input.addEventListener('click', (function(layerName)
										 {
											return function(ev)
											{
												var ch = $(ev.target).prop('checked');
												if(ch == false)
												{
													$(ev.target).prop('checked', true);
													return;
												}
												var rows = $("#"+tblId).dataTable().fnGetNodes();
										        for(var i=0;i<rows.length;i++)
										            $(rows[i]).find("td:eq(0) input").prop('checked', false); 
												for(var k=0; k<Object.keys(window.mapLayers.base).length; k++)
												{
													if(window.mapLayers.base[Object.keys(window.mapLayers.base)[k]].name != layerName)
														window.mapLayers.base[Object.keys(window.mapLayers.base)[k]].setVisibility(false);
												}
												window.mapLayers.base[layerName].setVisibility(true);
												$(ev.target).prop('checked', true);
											};
										 })(Object.keys(window.mapLayers.base)[i]));	
		count++;
    }
    tbl.entryCount = count;
    
    var tblOverlay = document.createElement('table');
    tblOverlay.id = tblId+'overlay';
    tblOverlay.className = "table table-condensed info-pane-table legend-table";
    
    tbody = document.createElement("tbody");
    tblOverlay.appendChild(tbody);
    
    thead = tblOverlay.createTHead();
    
    tr = thead.insertRow(-1);
    
    th = document.createElement("th");
    th.colSpan = "2";
    th.innerHTML = defs.i18n.nav.legendMenu.additional;
    tr.appendChild(th);
    
    tr = thead.insertRow(-1);
    th = document.createElement("th");
    tr.appendChild(th);
    th = document.createElement("th");
    tr.appendChild(th);
    $(tr).hide(); //created just to satisfy dataTables unique column constraint
    
	for(i=0; i<layers.length; i++)
	{
		/*<div id="rememberme"> 
		<input type="checkbox" id="rememberme-checkbox" name="_spring_security_remember_me" class="chkbox-bg-white chkbox-large shadowed"> 
		<label id="rememberme-checkbox-label" for="rememberme-checkbox"><span></span></label>
		<p> <h2> Remember me </h2> </p>
		</div> */
		tr = tbody.insertRow(-1);
		
		td = tr.insertCell(-1);
		
		var chkBox = document.createElement("div");
		chkBox.id = 'layerChkBoxCon' + layers[i];
		
		var input = document.createElement("input");
		input.id = 'layerChkBox' + layers[i];
		input.type = 'checkbox';
		input.name = layers[i];
		input.className = "chkbox-bg-grey chkbox-large";
		$(input).prop('checked', window.mapLayers.overlays[layers[i]].getVisibility());
		
		var chkBoxLabel = document.createElement('label');
		chkBoxLabel.id = 'layerChkBoxLabel' + layers[i];
		chkBoxLabel.htmlFor = 'layerChkBox' + layers[i];
		var sp = document.createElement('span');
		chkBoxLabel.appendChild(sp);
		
		chkBox.appendChild(input);
		chkBox.appendChild(chkBoxLabel);
		
		td.appendChild(chkBox);
		
		td = tr.insertCell(-1);
		var txt = document.createTextNode(layers[i]);
		td.appendChild(txt);
		
		chkBox.addEventListener('click', (function(layerName)
										 {
											return function(ev)
											{
												window.mapLayers.overlays[layerName].setVisibility($(ev.target).closest('input').prop('checked'));
											};
										 })(layers[i]));
		count++;
	}
	
	tblOverlay.entryCount = count;
	return [tbl, tblOverlay, tbl.entryCount];
}

function zoomToProjectButtonListener(ev, defs)
{ 
	var feature = window.mapLayers.vector.getFeaturesByAttribute("id", ev.target.projectId);
	if(!isPresent(feature) || feature.length == 0)
	{
		alert(defs.i18n.project.error.noShapeFound);
		return;
	}
	var bounds = feature[0].geometry.getBounds();
	window.map.zoomToExtent(bounds);
	bounds = adjustZoomBounds(bounds);
	window.map.zoomToExtent(bounds); 
}

function projectEditButtonListener(ev,defs)
{
	retrieveProjectDetails(ev.target.projectId, [{func: showProjectDetails, args: [ev, defs]},
	                            {func: copyToProjectEditForm, args: [$(ev.target).closest('tr')[0], "__funcRet", defs]},
	                            {func: adjustOverlayHeight, args: []}]); 
}

function taskEditButtonListener(ev, defs)
{
	var task = $(ev.target).closest('tr')[0].task;
	retrieveTaskDocuments(task.id, [{func: showTaskDetails, args:[ev, defs, "__funcRet", [{func: copyToTaskEditForm, args: [task, task.projectId, "__funcRet", defs]}, {func: adjustOverlayHeight, args: []}]]}]);
}

function documentDownloadButtonListener(ev)
{
	var id = ev.target.documentId;
	
	var params = {};
	params.id = id;
	
	postToUrl({path: "documents/retrieve", params: params});
}

function documentViewButtonListener(ev)
{
/*	 jQuery.post('documents/retrieveg', {id: ev.target.documentId}, 
			    function(data) {
			      window.open("data:application/pdf," + escape(data));
			    });*/
	
	 $.ajax({ 
	        url : "documents/retrievet",
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
	        				window.open('documents/retrieveg?t='+tok, response.response[tok]);
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
	//alert(ev.target.documentId);
	//window.open('documents/retrieveg?id='+ev.target.documentId);
/*	var id = ev.target.documentId;
	
	var params = {};
	params.id = id;
	
	//var doc = window.open();
	postToUrl({path: "documents/retrieve", params: params, target: "_blank"});*/
}

function documentEditButtonListener(ev, defs)
{
	var doc = $(ev.target).closest('tr')[0].document;
	retrieveDocumentTasks(doc.id, doc.projectId, [{func: showDocumentDetails, args:[ev, defs, "__funcRet", [{func: copyToDocumentEditForm, args: [doc, doc.projectId, "__funcRet", defs]}, {func: adjustOverlayHeight, args: []}]]}]);
}

function documentDeleteButtonListener(ev, documentId, projectId, ordinal, postProcessingFuncs)
{
	$('#confirmationModalTitle').text(defs.i18n.document.confirm.deletionTitle);
	$('#confirmationModalBody').text(defs.i18n.document.confirm.deletionText + " " + $('#documentName_'+ordinal).html() + defs.i18n.questionMark);
	
	$('#confirmationModalYesButton').click(function()
										    {
												$('#confirmationModalYesButton').off('click');
												$('#confirmationModalNoButton').off('click');
												$('#confirmationModal').modal('hide');
												deleteProjectDocument(projectId, documentId, postProcessingFuncs);
										    });
	$('#confirmationModal').modal({backdrop: 'static', keyboard: false});
	$('#confirmationModal').draggable();
	return false;
}

function projectDetailsButtonListener(ev, defs)
{
	showProjectDetailsPane({projectId: ev.target.projectId, ordinal: ev.target.ordinal, defs: defs});
}

function showProjectDetails(ev, defs)
{
	var undoSelection = function()
	{
		if(tableCon.split == false) return;
		$(targetTr).find('.statusIcon').css('visibility', 'visible'); 
		$('.projectRecordActions').find('div').css('visibility', 'visible');
		$(targetTr).find("td").removeClass('selected');
		$(editForm).hide();
			$(editForm).appendTo("body");
		tableCon.split = false;
		$('#search-widget-control').click($('#search-widget-control').data('click'));
	};
	
	var tableCon = $('#right-pane-overlay-content')[0];
	var table = $('#projectTbl')[0];
	window.savedProjectTable = table;
	var clonedTable = $(table).clone();
	$(table).detach();
	tableCon = $(tableCon).find('div')[0];
	$(tableCon).prepend(clonedTable);
	var trs = $(clonedTable).find("tbody > tr");
	
	$('.projectRecordActions').find('div').css('visibility', 'hidden');
	var targetTr = $(ev.target).closest('tr'); //ev.target still points to the original (saved) table
	var projectId = ev.target.projectId;
	var ord = targetTr[0].ordinal;
	var pId = targetTr[0].projectId;
	targetTr = $('#'+ev.target.id).closest('tr');
	targetTr[0].ordinal = ord;
	targetTr[0].projectId = pId;
	$(targetTr).find('.statusIcon').css('visibility', 'hidden');
	$(targetTr).addClass('selected');
	$(targetTr).find("td").addClass('selected');
	$('#projectRecordDeleteButton_'+targetTr[0].ordinal).css('visibility', 'visible').show();
	$('#projectRecordEditButton_'+targetTr[0].ordinal).css('visibility', 'visible').show();
	$('#projectRecordDetailsButton_'+targetTr[0].ordinal).hide();
	$('#projectRecordZoomButton_'+targetTr[0].ordinal).hide();
	
	
	
	var editForm = $('#editProjectForm')[0];
	editForm.dataChanged = false;
	var pos = $('#projectTbl tbody > tr').index($(targetTr));
	
	var removedTrs = [];
	var originalLength = trs.length;
	
	var i;
	for(i=pos+1; i<originalLength; i++)
	{
		var cl = $(trs[i]).clone()[0];
		removedTrs.push(cl);
	}
	
	var tbody = $(clonedTable).find('tbody')[0];
	
	for(i=pos+1; i<originalLength; i++)
		tbody.deleteRow(pos+1);
	
	clonedTable.append("<tbody></tbody>");
	tbody = $(clonedTable).find('tbody')[1];
	
	var formTr = document.createElement("tr");
	var formTd = document.createElement("td");
	formTd.className = "detailsElement";
	formTd.colSpan = "5";
	$(formTr).append(formTd);
	$(tbody).append(formTr);
	$(formTd).append(editForm);
	
	clonedTable.append("<tbody></tbody>");
	tbody = $(clonedTable).find('> tbody')[2];
	
	var splitTblBody = tbody;
	
	$(splitTblBody).append($(removedTrs));
	
	$(editForm).find('input, textarea, select').change(
			function()
			{
				editForm.dataChanged = true;
			}
		);
	
	$(editForm).show();
	
	$('#projectTbl tbody:eq(0) tr td, #projectTbl tbody:eq(2) tr td').off('click').click(function(ev)
			  {
				updateProject(projectId, false, 
						[
						 {func: undoSelection, args: []},
						 {func: function() { 
							 				 window.mapLayers.editableVector.removeAllFeatures();
							 				 if(editForm.dataChanged) 
							 					retrieveProjects(ev, defs, [{func: adjustOverlayHeight, args: []}]);
						 					 else
						 					 {
						 						$('#projectTbl').remove();
						 						$(tableCon).prepend(window.savedProjectTable);
						 					 }
						 				   }
						 		, args: []}]
				);
			  });
	$('#projectTbl tbody:eq(0) tr:eq('+pos+') .projectRecordActions').off('click');

	$('#projectRecordEditButton_'+targetTr[0].ordinal).off('click').click(function(ev)
					{
						updateProject(projectId, false,
							[
							 {func: undoSelection, args: []},
							 {func: function() { 
					 							 window.mapLayers.editableVector.removeAllFeatures();
								 				 if(editForm.dataChanged) 
								 					retrieveProjects(ev, defs, [{func: adjustOverlayHeight, args: []}]);
							 					 else
							 					 {
							 						$('#projectTbl').remove();
							 						$(tableCon).prepend(window.savedProjectTable);
							 					 }
							 				   }
											 	, args: []}]
						);
					});
	
	
	$('#projectRecordDetailsButton_'+targetTr[0].ordinal).off('click');
	
	$('#projectRecordDeleteButton_'+targetTr[0].ordinal).off('click').click(function(ev)
					{
						projectDeleteButtonListener(ev, targetTr[0].projectId, targetTr[0].ordinal,
						[
						 {func: undoSelection, args: []},
						 {func: retrieveProjects, args: [ev, defs, [{func: adjustOverlayHeight, args: []}]]}]
						);
					});
	
	$('#search-widget-control').data('click', $('#search-widget-control').prop('click'));
	$('#search-widget-control').off('click'); //TODO not working! why?
	
	$('#editProjectFormEditShape').off('click').click(function()
			{
				freezeProjectActions(targetTr[0].ordinal);
				window.drawingToolbarOn = true;
				$('#tools-drawing').show();
				
				var feature = window.mapLayers.vector.getFeaturesByAttribute('id', projectId);
				var featureClone = null;
				
				window.mapLayers.editableVector.removeAllFeatures();
				if(isPresent(feature) && feature.length == 1)
				{
					featureClone = feature[0].clone();
					window.mapLayers.vector.removeFeatures(feature);
					window.mapLayers.editableVector.addFeatures(feature);
					var tBounds = feature[0].geometry.getBounds().transform(window.map.displayProjection, window.map.getProjectionObject());
					window.map.zoomToExtent(tBounds);
					tBounds = adjustZoomBounds(tBounds);
					window.map.zoomToExtent(tBounds); 
				}
				
				window.mapLayers.editableVector.setVisibility(true);
				$('#tools-drawing-done').off('click').click(function() { shapeDrawingDoneListener(editForm, targetTr[0].ordinal); });
				$('#tools-drawing-cancel').off('click').click(function() { shapeDrawingCancelListener(featureClone, targetTr[0].ordinal); });
				
			});
	tableCon.split = true;
}

function shapeDrawingDoneListener(form, ordinal)
{
	disableDrawingControls();
	if(window.mapLayers.editableVector.features.length == 0)
	{
		alert(defs.i18n.project.error.noShape);
		return;
	}
	if(window.mapLayers.editableVector.features.length != 1)
	{
		alert(defs.i18n.project.error.singleShape);
		return;
	}
	
	$('#tools-drawing-point').show();
	$('#tools-drawing-polygon').show();
	$('#tools-drawing-freehand').show();
	$('#tools-drawing-select').show();
	//$('#tools-drawing-cancel').show();
	//$('#tools-drawing-done').show();
	$('#tools-drawing-modify').hide();
	$('#tools-drawing-drag').hide();
	$('#tools-drawing-delete').hide();
	
	$('#tools-drawing').hide();
	window.drawingToolbarOn = false;
	
	window.mapLayers.editableVector.setVisibility(false);
	window.mapLayers.vector.addFeatures([window.mapLayers.editableVector.features[0].clone()]);
	if(isPresent(ordinal))
		unfreezeProjectActions(ordinal);
	if(form)
		form.dataChanged = true;
}

function shapeDrawingCancelListener(feature, ordinal)
{
	$('#tools-drawing').hide();
	window.drawingToolbarOn = false;
	disableDrawingControls();
	
	$('#tools-drawing-point').show();
	$('#tools-drawing-polygon').show();
	$('#tools-drawing-freehand').show();
	$('#tools-drawing-select').show();
	//$('#tools-drawing-cancel').show();
	//$('#tools-drawing-done').show();
	$('#tools-drawing-modify').hide();
	$('#tools-drawing-drag').hide();
	$('#tools-drawing-delete').hide();
	
	if(isPresent(feature))
		window.mapLayers.vector.addFeatures([feature]);
	window.mapLayers.editableVector.removeAllFeatures();
	window.mapLayers.editableVector.setVisibility(false);
	if(isPresent(ordinal))
		unfreezeProjectActions(ordinal);
}

function freezeMainInterface()
{
	$('.navbar-left-item').each(function(){ disableElement(this);});
	disableElement($('.newItem-widget').closest('div'));
}

function unfreezeMainInterface()
{
	$('.navbar-left-item').each(function(){ enableElement(this);});
	enableElement($('.newItem-widget').closest('div'));
}

function freezeProjectActions(ordinal)
{
	disableElement($('#editProjectFormEditShape'));
	var tblRows = $('#projectTbl tbody:eq(0) tr td, #projectTbl tbody:eq(2) tr td');
	for(var i=0; i<tblRows.length; i++)
		disableElement(tblRows[i]);
	disableElement($('#projectRecordEditButton_'+ordinal));
	disableElement($('#projectRecordDeleteButton_'+ordinal));
}

function unfreezeProjectActions(ordinal)
{
	enableElement($('#editProjectFormEditShape'));
	var tblRows = $('#projectTbl tbody:eq(0) tr td, #projectTbl tbody:eq(2) tr td');
	for(var i=0; i<tblRows.length; i++)
		enableElement(tblRows[i]);
	enableElement($('#projectRecordEditButton_'+ordinal));
	enableElement($('#projectRecordDeleteButton_'+ordinal));
}

function showTaskDetails(ev, defs, taskDocuments, postProcessingFuncs)
{
	var tableCon = $('#project-tasks')[0];
	var table = $('#taskTbl')[0];
	table.undoSelection = function()
	{
		if(tableCon.split == false) return;
		$(tableCon.splitTr).find('.statusIcon').css('visibility', 'visible'); 
		$('.taskRecordActions').find('div').css('visibility', 'visible');
		$(tableCon.splitTr).find("td").removeClass('selected');
		$(editForm).hide();
		$(editForm).appendTo("body");
		tableCon.split = false;
		
		if(editForm.dataChanged) 
			retrieveTasks(targetTr[0].task.projectId, defs, [{func: adjustOverlayHeight, args: []}]);
		 else
		 {
			$('#taskTbl').remove();
			$(tableCon).prepend(window.savedTaskTable);
			var rpp = calculateResultsPerPage();
			$('#taskTbl').dataTable({"bFilter" : true,
				 "bInfo" : true,
				 "bLengthChange" : false,
				 //"aaSorting" : [[]],
				 "sDom" : "<'row'<'col-xs-6'l>r>t",
				 "aoColumnDefs": [
				                  { "bSortable": false, "aTargets": [0,6] },
				                  { "bSearchable": false, "aTargets": [0,6] }
				                ],
                 "oLanguage": {
		             "sZeroRecords": defs.i18n.info.noInfo
		         },
				 "iDisplayLength": rpp,
				 "bAutoWidth": false,
				 "bDestroy" : true});
		 }
		$('input[type="text"].search-widget').keyup();
		$('#search-widget-control').click($('#search-widget-control').data('click'));
		
	};
	
	var targetTr;
	if($(ev.target).closest('table')[0].id == 'taskTbl')
	{
		targetTr = $(ev.target).closest('tr'); //ev.target still points to the original (saved) table
		
	}else
	{
		//locate and go to target tr
		var scanTrs = $(table).dataTable().$('tr');
		for(var i=0; i<scanTrs.length; i++)
		{
			if(scanTrs[i].taskId == ev.target.taskId)
			{	
				targetTr = $(scanTrs[i]);
				break;
			}
		    if(editForm.dataChanged) 
				retrieveTasks(projectId, defs, [{func: adjustOverlayHeight, args: []}]);
			else
		    {
			    $('#taskTbl').remove();
				$(tableCon).prepend(window.savedTaskTable);
			}
		}
		var pg = Math.floor(i/$(table).dataTable().iDisplayLength);
		$(table).dataTable().fnPageChange('first');
		for(var i=0; i<pg; i++)
			$(table).dataTable().fnPageChange('next');
		updatePaginationInfo($(table).dataTable(), 'taskTbl');
	}
	
	var clonedTable = $(table).clone();
	clonedTable[0].undoSelection = table.undoSelection;
	if(tableCon.split == true)
		table.undoSelection();
	
	$(table).dataTable().fnDestroy();
	$(table).detach();
	window.savedTaskTable = table;
	tableCon = $(tableCon).find('div')[0];
	
	$(tableCon).prepend(clonedTable);
	var trs = $(clonedTable).find("tbody > tr");
	
	$('.taskRecordActions').find('div').css('visibility', 'hidden');
	//targetTr = $(ev.target).closest('tr'); //ev.target still points to the original (saved) table
	var taskId = ev.target.taskId;
	var task = targetTr.task;
	var ord = targetTr[0].ordinal;
	var tId = targetTr[0].taskId;
	targetTr = $('#'+ev.target.id).closest('tr');
	targetTr[0].ordinal = ord;
	targetTr[0].taskId = tId;
	targetTr[0].task = task;
	$(targetTr).find('.statusIcon').css('visibility', 'hidden');
	$(targetTr).addClass('selected');
	$(targetTr).find("td").addClass('selected');
	$('#taskRecordDeleteButton_'+targetTr[0].ordinal).css('visibility', 'visible').show();
	$('#taskRecordEditButton_'+targetTr[0].ordinal).css('visibility', 'visible').show();
	
	var editForm = $('#editTaskForm')[0];
	editForm.dataChanged = false;
	var pos = $('#taskTbl tbody > tr').index($(targetTr));
	
	var removedTrs = [];
	var originalLength = trs.length;
	
	var i;
	for(i=pos+1; i<originalLength; i++)
	{
		var cl = $(trs[i]).clone()[0];
		removedTrs.push(cl);
	}
	
	var tbody = $(clonedTable).find('tbody')[0];
	
	for(i=pos+1; i<originalLength; i++)
		tbody.deleteRow(pos+1);
	
	clonedTable.append("<tbody></tbody>");
	tbody = $(clonedTable).find('tbody')[1];
	
	var formTr = document.createElement("tr");
	var formTd = document.createElement("td");
	formTd.className = "detailsElement";
	formTd.colSpan = "7";
	$(formTr).append(formTd);
	$(tbody).append(formTr);
	$(formTd).append(editForm);
	
	clonedTable.append("<tbody></tbody>");
	tbody = $(clonedTable).find('> tbody')[2];
	
	var splitTblBody = tbody;
	
	$(splitTblBody).append($(removedTrs));
	
	$(editForm).find('input, textarea, select').change(
			function()
			{
				editForm.dataChanged = true;
			}
		);
	
	$(editForm).show();
	
	$('#taskTbl tbody:eq(0) tr td, #taskTbl tbody:eq(2) tr td').off('click').click(function(ev)
			  {
				updateTask(task, false, 
						[
						 {func: table.undoSelection, args: []}]
				);
			  });
	$('#taskTbl tbody:eq(0) tr:eq('+pos+') .taskRecordActions').off('click');

	$('#taskRecordEditButton_'+targetTr[0].ordinal).off('click').click(function(ev)
					{
						updateTask(task, false,
							[
							 {func: table.undoSelection, args: []}]
						);
					});
	
	$('#taskRecordDeleteButton_'+targetTr[0].ordinal).off('click').click(function(ev)
					{
						taskDeleteButtonListener(ev, targetTr[0].taskId, targetTr[0].ordinal,
						[
						 {func: function() { editForm.dataChanged = true; }, args: []},
						 {func: table.undoSelection, args: []}]
						);
					});
	
	/*$('#search-widget-control').off('click').click(function(ev)
			{
				table.undoSelection();
			});*/
	$('#search-widget-control').data('click', $('#search-widget-control').prop('click'));
	$('#search-widget-control').off('click'); //TODO not working! why?
	tableCon.split = true;
	tableCon.splitTr = targetTr[0];
	applyFuncs(postProcessingFuncs, taskDocuments);
}

function showDocumentDetails(ev, defs, documentTasks, postProcessingFuncs)
{
	var tableCon = $('#project-documents')[0];
	var table = $('#documentTbl')[0];
	table.undoSelection = function()
	{
		if(tableCon.split == false) return;
		$(targetTr).find('.statusIcon').css('visibility', 'visible'); 
		$('.documentRecordActions').find('div').css('visibility', 'visible');
		$(targetTr).find("td").removeClass('selected');
		$(editForm).hide();
		$(editForm).appendTo("body");
		tableCon.split = false;
		
		if(editForm.dataChanged) 
				retrieveDocuments(targetTr[0].document.projectId, defs, [{func: adjustOverlayHeight, args: []}]);
		 else
		 {
			$('#documentTbl').remove();
			$(tableCon).prepend(window.savedDocumentTable);
			var rpp = calculateResultsPerPage();
			$('#documentTbl').dataTable({"bFilter" : true,
										 "bInfo" : true,
										 "bLengthChange" : false,
										 //"aaSorting" : [[]],
										 "sDom" : "<'row'<'col-xs-6'l>r>t",
										 "aoColumnDefs": [
										                  { "bSortable": false, "aTargets": [3] },
										                  { "bSearchable": false, "aTargets": [3] }
										                ],
						                 "oLanguage": {
								             "sZeroRecords": defs.i18n.info.noInfo
								         },
										 "iDisplayLength": rpp,
										 "bAutoWidth": false,
										 "bDestroy" : true});
			$('input[type="text"].search-widget').keyup();
		 }
		$('#search-widget-control').click($('#search-widget-control').data('click'));
	};
	
	var clonedTable = $(table).clone();
	clonedTable[0].undoSelection = table.undoSelection;
	if(tableCon.split == true)
		table.undoSelection();
	
	$(table).dataTable().fnDestroy();
	$(table).detach();
	window.savedDocumentTable = table;
	tableCon = $(tableCon).find('div')[0];
	
	$(tableCon).prepend(clonedTable);
	var trs = $(clonedTable).find("tbody > tr");
	
	$('.documentRecordActions').find('div').css('visibility', 'hidden');
	var targetTr = $(ev.target).closest('tr'); //ev.target still points to the original (saved) table
	var documentId = ev.target.documentId;
	var doc = targetTr[0].document;
	var ord = targetTr[0].ordinal;
	var dId = targetTr[0].documentId;
	targetTr = $('#'+ev.target.id).closest('tr');
	targetTr[0].ordinal = ord;
	targetTr[0].documentId = dId;
	targetTr[0].document = doc;
	$(targetTr).find('.statusIcon').css('visibility', 'hidden');
	$(targetTr).addClass('selected');
	$(targetTr).find("td").addClass('selected');
	$('#documentRecordDeleteButton_'+targetTr[0].ordinal).css('visibility', 'visible').show();
	$('#documentRecordEditButton_'+targetTr[0].ordinal).css('visibility', 'visible').show();
	
	var editForm = $('#editDocumentForm')[0];
	editForm.dataChanged = false;
	var pos = $('#documentTbl tbody > tr').index($(targetTr));
	
	var removedTrs = [];
	var originalLength = trs.length;
	
	var i;
	for(i=pos+1; i<originalLength; i++)
	{
		var cl = $(trs[i]).clone()[0];
		removedTrs.push(cl);
	}
	
	var tbody = $(clonedTable).find('tbody')[0];
	
	for(i=pos+1; i<originalLength; i++)
		tbody.deleteRow(pos+1);
	
	clonedTable.append("<tbody></tbody>");
	tbody = $(clonedTable).find('tbody')[1];
	
	var formTr = document.createElement("tr");
	var formTd = document.createElement("td");
	formTd.className = "detailsElement";
	formTd.colSpan = "7";
	$(formTr).append(formTd);
	$(tbody).append(formTr);
	$(formTd).append(editForm);
	
	clonedTable.append("<tbody></tbody>");
	tbody = $(clonedTable).find('> tbody')[2];
	
	var splitTblBody = tbody;
	
	$(splitTblBody).append($(removedTrs));
	
	$(editForm).find('input, textarea, select').change(
			function()
			{
				editForm.dataChanged = true;
			}
		);
	
	$(editForm).show();
	
	$('#editDocumentFormuploadWidget').off('click').click(function(ev)
	{
				updateDocument(doc.id, false, true,
						[
						 {func: table.undoSelection, args: []}]
				);
			  });
			  
	$('#documentTbl tbody:eq(0) tr td, #documentTbl tbody:eq(2) tr td').off('click').click(function(ev)
			  {
				updateDocument(doc.id, false, false,
						[
						 {func: table.undoSelection, args: []}]
				);
			  });
	$('#documentTbl tbody:eq(0) tr:eq('+pos+') .documentRecordActions').off('click');

	$('#documentRecordEditButton_'+targetTr[0].ordinal).off('click').click(function(ev)
					{
						updateDocument(doc.id, false, false,
							[
							 {func: table.undoSelection, args: []}]
						);
					});
	
	$('#documentRecordDeleteButton_'+targetTr[0].ordinal).off('click').click(function(ev)
					{
						documentDeleteButtonListener(ev, targetTr[0].documentId, targetTr[0].document.projectId, targetTr[0].ordinal,
						[
						 {func: function() { editForm.dataChanged = true; }, args: []},
						 {func: table.undoSelection, args: []}]
						);
					});
	
	/*$('#search-widget-control').off('click').click(function(ev)
			{
				table.undoSelection();
			});*/
	$('#search-widget-control').data('click', $('#search-widget-control').prop('click'));
	$('#search-widget-control').off('click'); //TODO not working! why?
	tableCon.split = true;
	applyFuncs(postProcessingFuncs, documentTasks);
}

function copyToProjectEditForm(targetTr, project, defs)
{
	var iconId = null;
	switch(targetTr.projectStatus)
	{
	case "INACTIVE":
		iconId = "INACTIVE_ICON";
		break;
	case "ACTIVE":
		iconId = "ACTIVE_ICON";
		break;
	case "COMPLETED":
		iconId = "COMPLETED_ICON";
		break;
	case "CANCELLED":
		iconId = "CANCELLED_ICON";
		break;			
	}
	formReset($('#editProjectForm'));
	$('#editProjectFormStatusIcon').css('background-image', cssImgLoc(defs, iconId, false));
	$('#editProjectFormStatusText').html(defs.i18n.project.status[project.workflow.status.toLowerCase()] + ", " + defs.i18n.lastUpdate + ": " + timestampToDateString(project.workflow.statusDate));
	
	$('#editProjectFormTextBoxname').val(project.project.name);
	$('#editProjectFormTextBoxdescription').val(project.project.description);
	$('#editProjectFormTextBoxclient').val(project.project.client);
	$('#editProjectFormTextBoxwstartDate').val(timestampToDateString(project.workflow.startDate));
	if(isPresent(project.workflow.endDate)) $('#editProjectFormTextBoxwendDate').val(timestampToDateString(project.workflow.endDate));
	if(isPresent(project.workflow.reminderDate)) $('#editProjectFormTextBoxwreminderDate').val(timestampToDateString(project.workflow.reminderDate));
	
	enableTypeahead($('#editProjectFormTextBoxclient'), {url : 'projects/listClients', name : 'clients'}, function(values) { return values.response; });
}

function copyToTaskEditForm(task, projectId, taskDocuments, defs)
{
	var iconId = null;
	switch(task.status)
	{
	case "INACTIVE":
		iconId = "INACTIVE_ICON";
		break;
	case "ACTIVE":
		iconId = "ACTIVE_ICON";
		break;
	case "COMPLETED":
		iconId = "COMPLETED_ICON";
		break;
	case "CANCELLED":
		iconId = "CANCELLED_ICON";
		break;			
	}
	formReset($('#editTaskForm'));
	$('#editTaskFormStatusIcon').css('background-image', cssImgLoc(defs, iconId, false));
	$('#editTaskFormStatusText').html(defs.i18n.project.status[task.status.toLowerCase()] + ", " + defs.i18n.lastUpdate + ": " + timestampToDateString(task.statusDate));
	
	$('#editTaskFormTextBoxname').val(task.name);
	$('#editTaskFormTextBoxstartDate').val(timestampToDateString(task.startDate));
	if(isPresent(task.endDate)) $('#editTaskFormTextBoxendDate').val(timestampToDateString(task.endDate));
	if(isPresent(task.reminderDate)) $('#editTaskFormTextBoxreminderDate').val(timestampToDateString(task.reminderDate));
	
	switch(task.critical)
	{
	case "NONBLOCKING":
		$('#editTaskFormRadiocriticalityNB').prop('checked', true);
		break;
	case "BLOCKING":
		$('#editTaskFormRadiocriticalityB').prop('checked', true);
		break;
	case "CRITICAL":
		$('#editTaskFormRadiocriticalityC').prop('checked', true);
		break;
	}
	
	createTaskDocumentTable(task.id, projectId, taskDocuments, defs);
	/*var docTbl = $('#editTaskFormTabledocuments');
	$(docTbl).empty();
	var tbody = document.createElement("tbody");
	$(docTbl).append($(tbody));
	
	$('#editTaskFormDocuments').css('visibility', 'visible');
	$('#editTaskFormLabeldocuments').css('visibility', 'visible');
	if(taskDocuments.length == 0)
	{
		$('#editTaskFormDocuments').css('visibility', 'hidden');
		$('#editTaskFormLabeldocuments').css('visibility', 'hidden');
	}
	
	for(var i=0; i<taskDocuments.length; i++)
	{
		var tr = tbody.insertRow(-1);
		var td = tr.insertCell(-1);
		td.className = "taskDocumentActions";
		
		var buttonCon = document.createElement("div");
		buttonCon.className = "taskDocumentViewButtons";
		icon = document.createElement("img");
		icon.src = imgLoc(defs, "VIEW_DOCUMENT_ICON", true);
		icon.id = "taskDocumentViewButton_"+i;
		icon.taskId = task.id;
		icon.documentId = taskDocuments[i].id;
		icon.ordinal = i;
		icon.addEventListener('click', function(ev) { taskDocumentViewButtonListener(ev, defs); });
		
		buttonCon.appendChild(icon);
		td.appendChild(buttonCon);
		
		buttonCon = document.createElement("div");
		buttonCon.className = "taskDocumentDeleteButtons";
		icon = document.createElement("img");
		icon.src = imgLoc(defs, "DELETE_DOCUMENT_ICON", true);
		icon.id = "taskDocumentDeleteButton_"+i;
		icon.documentId = taskDocuments[i].id;
		icon.taskId = task.id;
		icon.ordinal = i;
		icon.addEventListener('click', (function(ev) 
										{ 
											taskDocumentDeleteButtonListener(ev, [{func: copyToTaskEditForm, args: [task, taskDocuments, defs]}]);
										}));
		
		buttonCon.appendChild(icon);
		td.appendChild(buttonCon);
		
		td = tr.insertCell(-1);
		td.className = "taskDocumentNames";
		
		td.innerHTML = taskDocuments[i].name;
		
	}*/
	
	var addDocumentWidget = document.createElement("div");
	$('#editTaskFormnewDocumentWidget').empty();
	$('#editTaskFormnewDocumentWidget').append(addDocumentWidget);
	
	var addIcon = document.createElement("img");
	addIcon.className = "newItem-widget";
	addIcon.src = imgLoc(defs, "ADD_DOCUMENT_ICON", true);
	
	var addIconText = document.createElement("div");
	addIconText.className = "newItem-widget";
	
	$(addDocumentWidget).append(addIcon);
	$(addDocumentWidget).append(addIconText);
	
	$(addIconText).text(defs.i18n.action.addItem);
	
	$(addIcon).closest("div").click(function(ev) { addTaskDocumentListener(ev, task, defs); });
}

function copyToDocumentEditForm(doc, projectId, documentTasks, defs)
{
	var iconId = null;
	formReset($('#editDocumentForm'));
	
	$('#editDocumentFormTextBoxname').val(doc.name);
	$('#editDocumentFormTextBoxdescription').val(doc.description);
	$('#editDocumentFormTextBoxcreationDate').val(timestampToDateString(doc.creationDate));
	$('#editDocumentFormTextBoxcreationDate').prop('disabled', true);
	
	createDocumentTaskTable(doc.id, projectId, documentTasks, defs);
	
	var addTaskWidget = document.createElement("div");
	$('#editDocumentFormnewTaskWidget').empty();
	$('#editDocumentFormnewTaskWidget').append(addTaskWidget);
	
	var addIcon = document.createElement("img");
	addIcon.className = "newItem-widget";
	addIcon.src = imgLoc(defs, "ADD_DOCUMENT_ICON", true);
	
	var addIconText = document.createElement("div");
	addIconText.className = "newItem-widget";
	
	$(addTaskWidget).append(addIcon);
	$(addTaskWidget).append(addIconText);
	
	$(addIconText).text(defs.i18n.action.addItem);
	
	$(addIcon).closest("div").click(function(ev) { addDocumentTaskListener(ev, doc, defs); });
}

function createDocumentTaskTable(documentId, projectId, documentTasks, defs)
{
	var taskTbl = $('#editDocumentFormTabletasks');
	$(taskTbl).empty();
	var tbody = document.createElement("tbody");
	$(taskTbl).append($(tbody));
	
	$('#editDocumentFormTasks div').remove();
	if(documentTasks.length == 0)
	{
		$('#editDocumentFormTasks').append('<div>'+defs.i18n.info.noTasks+'</div>');
	}
	
	taskTbl[0].projectId = projectId;
	
	for(var i=0; i<documentTasks.length; i++)
	{
		var tr = tbody.insertRow(-1);
		tr.taskId = documentTasks[i].id;
		tr.documentId = documentId;
		var td = tr.insertCell(-1);
		td.className = "documentTaskActions";
		
		var buttonCon = document.createElement("div");
		buttonCon.className = "documentTaskViewButtons";
		icon = document.createElement("img");
		icon.src = imgLoc(defs, "VIEW_DOCUMENT_ICON", true);
		icon.id = "documentTaskViewButton_"+i;
		icon.taskId = documentTasks[i].id;
		icon.documentId = documentId;
		icon.ordinal = i;
		icon.addEventListener('click', function(ev) { documentTaskViewButtonListener(ev, defs); });
		
		buttonCon.appendChild(icon);
		td.appendChild(buttonCon);
		
		buttonCon = document.createElement("div");
		buttonCon.className = "documentTaskDeleteButtons";
		icon = document.createElement("img");
		icon.src = imgLoc(defs, "DELETE_DOCUMENT_ICON", true);
		icon.id = "documentTaskDeleteButton_"+i;
		icon.taskId = documentTasks[i].id;
		icon.documentId = documentId;
		icon.ordinal = i;
		icon.addEventListener('click', (function(ev) 
										{ 
											documentTaskDeleteButtonListener(ev, defs);
										}));
		
		buttonCon.appendChild(icon);
		td.appendChild(buttonCon);
		
		td = tr.insertCell(-1);
		td.className = "documentTaskNames";
		
		td.innerHTML = documentTasks[i].name;
		
	}	
}

function createTaskDocumentTable(taskId, projectId, taskDocuments, defs)
{
	var documentTbl = $('#editTaskFormTabledocuments');
	$(documentTbl).empty();
	var tbody = document.createElement("tbody");
	$(documentTbl).append($(tbody));
	
	$('#editTaskFormDocuments div').remove();
	if(taskDocuments.length == 0)
	{
		$('#editTaskFormDocuments').append('<div>'+defs.i18n.info.noDocuments+'</div>');
	}
	
	documentTbl[0].projectId = projectId;
	
	for(var i=0; i<taskDocuments.length; i++)
	{
		var tr = tbody.insertRow(-1);
		tr.documentId = taskDocuments[i].id;
		tr.taskId = taskId;
		var td = tr.insertCell(-1);
		td.className = "taskDocumentActions";
		
		var buttonCon = document.createElement("div");
		buttonCon.className = "taskDocumentViewButtons";
		icon = document.createElement("img");
		icon.src = imgLoc(defs, "VIEW_DOCUMENT_ICON", true);
		icon.id = "taskDocumentViewButton_"+i;
		icon.documentId = taskDocuments[i].id;
		icon.taskId = taskId;
		icon.ordinal = i;
		icon.addEventListener('click', function(ev) { taskDocumentViewButtonListener(ev, defs); });
		
		buttonCon.appendChild(icon);
		td.appendChild(buttonCon);
		
		buttonCon = document.createElement("div");
		buttonCon.className = "taskDocumentDeleteButtons";
		icon = document.createElement("img");
		icon.src = imgLoc(defs, "DELETE_DOCUMENT_ICON", true);
		icon.id = "taskDocumentDeleteButton_"+i;
		icon.documentId = taskDocuments[i].id;
		icon.taskId = taskId;
		icon.ordinal = i;
		icon.addEventListener('click', (function(ev) 
										{ 
											taskDocumentDeleteButtonListener(ev, defs);
										}));
		
		buttonCon.appendChild(icon);
		td.appendChild(buttonCon);
		
		td = tr.insertCell(-1);
		td.className = "taskDocumentNames";
		
		td.innerHTML = taskDocuments[i].name;
		
	}	
}

function projectDeleteButtonListener(ev, projectId, ordinal, postProcessingFuncs)
{
	$('#confirmationModalTitle').text(defs.i18n.project.confirm.deletionTitle);
	$('#confirmationModalBody').text(defs.i18n.project.confirm.deletionText + " " + $('#projectName_'+ordinal).html() + defs.i18n.questionMark);
	
	$('#confirmationModalYesButton').click(function()
										    {
												$('#confirmationModalYesButton').off('click');
												$('#confirmationModalNoButton').off('click');
												$('#confirmationModal').modal('hide');
												deleteProject(projectId, postProcessingFuncs);
										    });
	$('#confirmationModal').modal({backdrop: 'static', keyboard: false});
	$('#confirmationModal').draggable();
	return false;

}

function taskDeleteButtonListener(ev, taskId, ordinal, postProcessingFuncs)
{
	$('#confirmationModalTitle').text(defs.i18n.task.confirm.deletionTitle);
	$('#confirmationModalBody').text(defs.i18n.task.confirm.deletionText + " " + $('#taskName_'+ordinal).html() + defs.i18n.questionMark);
	
	$('#confirmationModalYesButton').click(function()
										    {
												$('#confirmationModalYesButton').off('click');
												$('#confirmationModalNoButton').off('click');
												$('#confirmationModal').modal('hide');
												deleteTask(taskId, postProcessingFuncs);
										    });
	$('#confirmationModal').modal({backdrop: 'static', keyboard: false});
	$('#confirmationModal').draggable();
	return false;

}

function taskDocumentViewButtonListener(ev, defs)
{
	var tbl = $('#documentTbl');
	if($('#project-documents').find('div')[0].split == true) tbl[0].undoSelection();
	var dataTbl = $('#documentTbl').dataTable();
	var scanTrs = dataTbl.$('tr');
	var ordinal = null;
	for(var i=0; i<scanTrs.length; i++)
	{
		if(scanTrs[i].documentId == ev.target.documentId)
		{	
		    ordinal = scanTrs[i].ordinal;
			break;
		}
	}
	
	dataTbl.fnPageChange('first');
	var info = dataTbl.fnPagingInfo();
	var pg = Math.floor(i/(info.iEnd - info.iStart));
	for(var i=0; i<pg; i++) 
		dataTbl.fnPageChange('next');
	$('#project-nav li a[href="#project-documents"]').click();
	updatePaginationInfo(dataTbl, 'documentTbl');
	$('#documentRecordEditButton_'+ordinal).click();
}

function taskDocumentDeleteButtonListener(ev, defs)
{
	var targetTr = $(ev.target).closest("tr");
	var projectId = $('#editTaskFormTabledocuments')[0].projectId;
	removeTaskDocument(targetTr[0].taskId, targetTr[0].documentId, [{func: retrieveTaskDocuments, args:[targetTr[0].taskId,
		                                                                          	    		          [{func: createTaskDocumentTable, args:[targetTr[0].documentId, projectId, "__funcRet", defs]}]]}]);
}

function documentTaskViewButtonListener(ev, defs)
{
	var tbl = $('#taskTbl');
	if($('#project-tasks').find('div')[0].split == true) tbl[0].undoSelection();
	var dataTbl = $('#taskTbl').dataTable();
	var scanTrs = dataTbl.$('tr');
	var ordinal = null;
	for(var i=0; i<scanTrs.length; i++)
	{
		if(scanTrs[i].taskId == ev.target.taskId)
		{	
		    ordinal = scanTrs[i].ordinal;
			break;
		}
	}
	
	dataTbl.fnPageChange('first');
	var info = dataTbl.fnPagingInfo();
	var pg = Math.floor(i/(info.iEnd - info.iStart));
	for(var i=0; i<pg; i++) 
		dataTbl.fnPageChange('next');
	$('#project-nav li a[href="#project-tasks"]').click();
	updatePaginationInfo(dataTbl, 'taskTbl');
	$('#taskRecordEditButton_'+ordinal).click();
	/*retrieveTaskDocuments(ev.target.taskId, [{func: updatePaginationInfo, args: [dataTbl]}, 
	                                         {func: showTaskDetails, args:[ev, defs, "__funcRet", 
	                                                                       [{func: copyToTaskEditForm, args: [task, "__funcRet", defs]}, {func: adjustOverlayHeight, args: []}]]}]);*/
}

function addTaskDocumentListener(ev, task, defs)
{
	var tbl = $('#documentTbl');
	if(tbl.length == 0) return;
	if($('#project-documents').find('div')[0].split == true) tbl[0].undoSelection();
	dataTable = $('#documentTbl').dataTable();
	
	var scanTrs;
	
	scanTrs = dataTable.$('tr');
	$.each(scanTrs, function(index, value)
					{
						$(value).data('onclick', $(value).prop('onclick'));
					});
	
	
	dataTable.$('tr').click( function (evv) {
	    dataTable.$('tr').off('click');
	    var targetTr = $(evv.target).closest("tr");
	    addTaskDocument(task.id, targetTr[0].document.id, defs, "taskDocument",
	    		[{func: retrieveTaskDocuments, args:[task.id,
	    		          [{func: createTaskDocumentTable, args:[task.id, targetTr[0].document.projectId,"__funcRet", defs]}, {func: function() {$('#project-nav li a[href="#project-tasks"]').click();}, args:[]}]]}]);
	    $.each(scanTrs, function(index, value)
				{
					$(value).prop('onclick', $(value).data('onclick'));
				});

	  } );
	
	$('#project-nav li a[href="#project-documents"]').click();
}

function addDocumentTaskListener(ev, doc, defs)
{
	var tbl = $('#taskTbl');
	if(tbl.length == 0) return;
	if($('#project-tasks').find('div')[0].split == true) tbl[0].undoSelection();
	dataTable = $('#taskTbl').dataTable();
	
	var scanTrs;
	
	scanTrs = dataTable.$('tr');
	$.each(scanTrs, function(index, value)
					{
						$(value).data('onclick', $(value).prop('onclick'));
					});
	
	
	dataTable.$('tr').click( function (evv) {
	    dataTable.$('tr').off('click');
	    var targetTr = $(evv.target).closest("tr");
	    addTaskDocument(targetTr[0].task.id, doc.id, defs, "documentTask",
	    		[{func: retrieveDocumentTasks, args:[doc.id, targetTr[0].task.projectId, 
	    		          [{func: createDocumentTaskTable, args:[doc.id, targetTr[0].task.projectId,"__funcRet", defs]}, {func: function() {$('#project-nav li a[href="#project-documents"]').click();}, args:[]}]]}]);
	    $.each(scanTrs, function(index, value)
				{
					$(value).prop('onclick', $(value).data('onclick'));
				});

	  } );
	
	$('#project-nav li a[href="#project-tasks"]').click();
}

function documentTaskDeleteButtonListener(ev, defs)
{
	var targetTr = $(ev.target).closest("tr");
	var projectId = $('#editDocumentFormTabletasks')[0].projectId;
	removeTaskDocument(targetTr[0].taskId, targetTr[0].documentId, [{func: retrieveDocumentTasks, args:[targetTr[0].documentId, projectId, 
		                                                                          	    		          [{func: createDocumentTaskTable, args:[targetTr[0].documentId, projectId, "__funcRet", defs]}]]}]);
}

function deleteProject(id, postProcessingFuncs)
{
	$.ajax({ 
        url : "projects/remove",
        type : "post", 
        data : id,
        dataType : "json",
        contentType : "application/json",
        success : function(response) 
        		  {
        			if(response.status == "NotFound") alert(defs.i18n.project.error.notFound);
        			if(response.status == "Unauthorized") alert(defs.i18n.project.error.unauthorized);
        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					applyFuncs(postProcessingFuncs, response.response);
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

function deleteTask(id, postProcessingFuncs)
{
	$.ajax({ 
        url : "projects/removeTask",
        type : "post", 
        data : id,
        dataType : "json",
        contentType : "application/json",
        success : function(response) 
        		  {
        			if(response.status == "NotFound") alert(defs.i18n.project.error.notFound);
        			if(response.status == "Unauthorized") alert(defs.i18n.task.error.unauthorized);
        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					applyFuncs(postProcessingFuncs, response.response);
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

function deleteProjectDocument(projectId, docId, postProcessingFuncs)
{
	var req = "projectId=" + projectId + "&documentId=" + docId;
	
	$.ajax({ 
        url : "projects/removeProjectDocument",
        type : "post", 
        data : req,
        success : function(response) 
        		  {
        			if(response.status == "NotFound") alert(defs.i18n.project.error.notFound);
        			if(response.status == "Unauthorized") alert(defs.i18n.project.error.unauthorized);
        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					applyFuncs(postProcessingFuncs, response.response);
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

function init(defs)
{
	window.drawingToolbarOn = false;
	
	window.onerror = function myErrorHandler(errorMsg, url, lineNumber) {
		  alert(errorMsg +" : " + url + ' : ' + lineNumber);
		  return false;
		};
	
	$( document ).ajaxStart(function() {
		$('#loading').show();
		$('#loading').maxZIndex();
		$('body').addClass('loading');
	});
	$( document ).ajaxStop(function() {
		$('#loading').hide();
		$('body').removeClass('loading');
	});
/*	$(document).ajaxSuccess(function(event,request,settings){
	    if(request.status == 302)
	    	window.location.href = request.getResponseHeader("Location");
	});
	$(document).ajaxError(function(event,request,settings){
	    if(request.status == 302)
	    	window.location.href = request.getResponseHeader("Location");
	});*/
	
	/*$('#logout').on("click", function(e){
		e.preventDefault();
	    $.post(this.href ,function(data) {
	    });
	});*/
	
	Proj4js.defs["EPSG:2100"] = "+title= GGRS87 / Greek Grid EPSG:2100 +proj=tmerc +lat_0=0 +lon_0=24 +k=0.9996 +x_0=500000 +y_0=0 +ellps=GRS80 +towgs84=-199.87,74.79,246.62,0,0,0,0 +units=m +no_defs";
	//
	
	
	$('#close-right-pane').click(function(ev) {
		$('.navbar-left-item').removeClass('navbar-left-active');
		$('#right-pane').hide();
		$('#right-pane-container').removeClass('pane-open');
		$('#tools-info').removeClass('pane-open');
		$('#user-widget').removeClass('pane-open');
		$('#tools').removeClass('pane-open');
		$('#loading').removeClass('pane-open');
		$('#close-right-pane').hide();
		$('#back-right-pane').hide();
		$('#title_bar').css('background-image', 'none');
		$('#title > h1').html('');
	});
	
	wireLeftNavbar(defs);
	
	enableZMaxIndex();
	adjustOverlayHeight();
	
	$('#project-nav').hide();
	$('#legend-nav').hide();
	$('.geopolis-pane').hide();
	$('#editProjectForm').hide();
	$('#editTaskForm').hide();
	$('#editDocumentForm').hide();
	$('#tools-drawing-modify').hide();
	$('#tools-drawing-drag').hide();
	$('#tools-drawing-delete').hide();
	$('#tools-drawing-hand').hide();
	
	$('#editProjectFormTextBoxwstartDate')[0].addEventListener('click', function(ev) { datepickerListener(ev); });
	$('#editProjectFormTextBoxwendDate')[0].addEventListener('click', function(ev) { datepickerListener(ev); });
	$('#editProjectFormTextBoxwreminderDate')[0].addEventListener('click', function(ev) { datepickerListener(ev); });
	
	$('#editTaskFormTextBoxstartDate')[0].addEventListener('click', function(ev) { datepickerListener(ev); });
	$('#editTaskFormTextBoxendDate')[0].addEventListener('click', function(ev) { datepickerListener(ev); });
	$('#editTaskFormTextBoxreminderDate')[0].addEventListener('click', function(ev) { datepickerListener(ev); });
	
	$( window ).resize(function() {
		adjustOverlayHeight();
		adjustDropdownHeight();
	});
	
	$('#tools-drawing-point').tooltip({placement: 'bottom', title: defs.i18n.drawing.point});
	$('#tools-drawing-polygon').tooltip({placement: 'bottom', title: defs.i18n.drawing.polygon});
	$('#tools-drawing-freehand').tooltip({placement: 'bottom', title: defs.i18n.drawing.freehand});
	$('#tools-drawing-select').tooltip({placement: 'bottom', title: defs.i18n.drawing.select});
	$('#tools-drawing-hand').tooltip({placement: 'bottom', title: defs.i18n.drawing.hand});
	$('#tools-drawing-modify').tooltip({placement: 'bottom', title: defs.i18n.drawing.modify});
	$('#tools-drawing-drag').tooltip({placement: 'bottom', title: defs.i18n.drawing.drag});
	$('#tools-drawing-delete').tooltip({placement: 'bottom', title: defs.i18n.drawing['delete']});
	$('#tools-drawing-done').tooltip({placement: 'bottom', title: defs.i18n.drawing.done});
	$('#tools-drawing-cancel').tooltip({placement: 'bottom', title: defs.i18n.drawing.cancel});
	
	
	$('#tools-drawing-point').click(function() {
										disableDrawingControls();
										//$('#tools-drawing-hand').show();
										window.mapControls.point.activate();
									}
	);
	
	$('#tools-drawing-polygon').click(function() {
										disableDrawingControls();
										//$('#tools-drawing-hand').show();
										window.mapControls.polygon.activate();
									}
	);

	$('#tools-drawing-freehand').click(function() {
										disableDrawingControls();
										//$('#tools-drawing-hand').show();
										window.mapControls.freehand.activate();
									}
								);

	$('#tools-drawing-select').click(function() {
										disableDrawingControls();
										//$('#tools-drawing-hand').show();
										window.mapControls.select.activate();
									}
								);
	
	$('#tools-drawing-modify').click(function() {
										disableDrawingControls();
										window.mapControls.modify.activate();
										window.mapControls.modify.mode =  OpenLayers.Control.ModifyFeature.RESHAPE;
									}
								);
	
	$('#tools-drawing-drag').click(function() {
										disableDrawingControls();
										window.mapControls.modify.activate();
										window.mapControls.modify.mode =  OpenLayers.Control.ModifyFeature.DRAG;
									}
								);
	
	$('#tools-drawing-hand').click(function() {
										$('#tools-drawing-hand').hide();
										disableDrawingControls();
									}
								);
	
	$('#tools-drawing-delete').click(function() {
										window.mapLayers.editableVector.removeFeatures(window.mapLayers.editableVector.selectedFeatures);
										if(window.mapLayers.editableVector.features.length == 0)
										{
											$('#tools-drawing-point').show();
						            		$('#tools-drawing-polygon').show();
						            		$('#tools-drawing-freehand').show();
						            		$('#tools-drawing-select').hide();
						            		$('#tools-drawing-cancel').show();
						            		$('#tools-drawing-done').show();
						            		$('#tools-drawing-modify').hide();
						            		$('#tools-drawing-drag').hide();
						            		$('#tools-drawing-delete').hide();
						            		$('#tools-drawing-hand').click();
										}
									}
								);
	
	$('#tools-info-coords-wgs').click(function() {
		map.removeControl(mousePositionControl);
		mousePositionControl = new ol.control.MousePosition({
	    	projection: 'EPSG:4326',
	    	coordinateFormat: ol.coordinate.createStringXY(5),
	    	target: document.getElementById('tools-info-con')
	    });
	    
	    map.addControl(mousePositionControl);
	    $('.ol-mouse-position').insertBefore('.dropup');
//	    $('.ol-mouse-position').addClass('col-lg-7');
//										var proj = new OpenLayers.Projection("EPSG:4326");
//										window.map.displayProjection = proj;
//										setControlDisplayProjection(proj);	
									}
								);

	$('#tools-info-coords-gwm').click(
			function() {
				map.removeControl(mousePositionControl);
				mousePositionControl = new ol.control.MousePosition({
			    	projection: 'EPSG:900913',
			    	coordinateFormat: ol.coordinate.createStringXY(5),
			    	target: document.getElementById('tools-info-con')
			    });
			    
			    map.addControl(mousePositionControl);
			    $('.ol-mouse-position').insertBefore('.dropup');
			    
//				var proj = new ol.proj.Projection({
//					code : "EPSG:900913"
//				});
//		
//		
//		
//				var proj = new OpenLayers.Projection("EPSG:900913");
//				window.map.displayProjection = proj;
//				setControlDisplayProjection(proj);	
			}
		);
	
	$('#tools-info-coords-gg').click(function() {
		map.removeControl(mousePositionControl);
		mousePositionControl = new ol.control.MousePosition({
	    	projection: 'EPSG:2100',
	    	coordinateFormat: ol.coordinate.createStringXY(5),
	    	target: document.getElementById('tools-info-con')
	    });
	    
	    map.addControl(mousePositionControl);
	    $('.ol-mouse-position').insertBefore('.dropup');
//										var proj = new OpenLayers.Projection("EPSG:2100");
//										window.map.displayProjection = proj;
//										setControlDisplayProjection(proj);	
		}
	);
	//TODO remove following when server parameterization is implemented
	$('#user-widget p a').html($('body').hasClass('anonymous') ?  defs.i18n.nav.login : defs.i18n.nav.logout);
	$('#user-widget p:last a').html($('body').hasClass('anonymous') ?  defs.i18n.nav.testMapPage: defs.i18n.nav.logout);
	$('#confirmationModalYesButton').text(defs.i18n.confirm.yes);
	$('#confirmationModalNoButton').text(defs.i18n.confirm.no);
	$('form').find('.input-group-addon').prepend('<img src = "'+imgLoc(defs, "CALENDAR_ICON", true)+'"/>');
	$('#editDocumentFormuploadWidget').prepend('<img src="' + imgLoc(defs, "UPLOAD_ICON", true)+'"/>');
	$('#project-nav a[href="#project-info"]').text(defs.i18n.nav.projectMenu.info);
	$('#project-nav a[href="#project-tasks"]').text(defs.i18n.nav.projectMenu.tasks);
	$('#project-nav a[href="#project-documents"]').text(defs.i18n.nav.projectMenu.documents);
	$('#project-nav a[href="#project-report"]').text(defs.i18n.nav.projectMenu.report);

	$('#legend-nav a[href="#legend-sites"]').text(defs.i18n.nav.legendMenu.sites);
	$('#legend-nav a[href="#legend-planning"]').text(defs.i18n.nav.legendMenu.planning);
	$('#legend-nav a[href="#legend-maps"]').text(defs.i18n.nav.legendMenu.maps);
	$('#legend-nav a[href="#legend-poi"]').text(defs.i18n.nav.legendMenu.poi);
	
	var labels = Object.keys(defs.i18n.project.attr);
	for(var i=0; i<labels.length; i++)
		$('#editProjectFormLabel'+labels[i]).text(defs.i18n.project.attr[labels[i]]);
	labels = Object.keys(defs.i18n.workflow.attr);
	for(var i=0; i<labels.length; i++)
		$('#editProjectFormLabel'+'w'+labels[i]).text(defs.i18n.workflow.attr[labels[i]]);
	
	
	$('#addTaskFormLabelcriticalityNB').append(document.createTextNode(defs.i18n.task.criticality.nonBlocking));
	$('#addTaskFormLabelcriticalityB').append(document.createTextNode(defs.i18n.task.criticality.blocking));
	$('#addTaskFormLabelcriticalityC').append(document.createTextNode(defs.i18n.task.criticality.critical));
	
	labels = Object.keys(defs.i18n.task.attr);
	for(var i=0; i<labels.length; i++)
		$('#editTaskFormLabel'+labels[i]).text(defs.i18n.task.attr[labels[i]]);
	$('#editTaskFormLabeldocuments').text(defs.i18n.task.attr.documents);
	$('#editTaskFormLabelcriticalityNB').append(document.createTextNode(defs.i18n.task.criticality.nonBlocking));
	$('#editTaskFormLabelcriticalityB').append(document.createTextNode(defs.i18n.task.criticality.blocking));
	$('#editTaskFormLabelcriticalityC').append(document.createTextNode(defs.i18n.task.criticality.critical));
	
	$('#addProjectFormSaveButton').text(defs.i18n.action.newProjectSave);
	$('#addProjectTitle').text(defs.i18n.header.newProject);
	var labels = Object.keys(defs.i18n.project.attr);
	for(var i=0; i<labels.length; i++)
		$('#addProjectFormLabel'+labels[i]).text(defs.i18n.project.attr[labels[i]]);
	$('#addProjectFormLabeltemplate').prepend('<span></span>');
	labels = Object.keys(defs.i18n.workflow.attr);
	for(var i=0; i<labels.length; i++)
		$('#addProjectFormLabel'+'w'+labels[i]).text(defs.i18n.workflow.attr[labels[i]]);
	$('#addProjectFormOptionalA').text(defs.i18n.header.workflowInfo + " (" + defs.i18n.header.optional + ")");
	
	$('#addAttributeFormSaveButton').text(defs.i18n.action.newAttributeSave);
	$('#addAttributeTitle').text(defs.i18n.header.newAttribute);
	$('#addAttributeFormLabelsourceExisting').append(document.createTextNode(defs.i18n.attribute.sourceExisting));
	$('#addAttributeFormLabelsourceNew').append(document.createTextNode(defs.i18n.attribute.sourceNew));
	$('#addAttributeFormLabelname').text(defs.i18n.attribute.name);
	$('#addAttributeFormLabelvalue').text(defs.i18n.attribute.value);
	
	$('#addTaskFormSaveButton').text(defs.i18n.action.newTaskSave);
	$('#addTaskTitle').text(defs.i18n.header.newTask);
	labels = Object.keys(defs.i18n.task.attr);
	for(var i=0; i<labels.length; i++)
		$('#addTaskFormLabel'+labels[i]).text(defs.i18n.task.attr[labels[i]]);
	
	$('#addDocumentFormSaveButton').text(defs.i18n.action.newDocumentSave);
	$('#addDocumentTitle').text(defs.i18n.header.newDocument);
	var labels = Object.keys(defs.i18n.document.attr);
	for(var i=0; i<labels.length; i++)
		$('#addDocumentFormLabel'+labels[i]).text(defs.i18n.document.attr[labels[i]]);
	
	labels = Object.keys(defs.i18n.document.attr);
	for(var i=0; i<labels.length; i++)
		$('#editDocumentFormLabel'+labels[i]).text(defs.i18n.document.attr[labels[i]]);
	$('#editDocumentFormLabeltasks').text(defs.i18n.document.attr.tasks);
	
}

function disableDrawingControls(excludeSelect) {
	window.mapControls.point.deactivate();
	window.mapControls.polygon.deactivate();
	window.mapControls.freehand.deactivate();
	if(!isPresent(excludeSelect) || excludeSelect == false)
	{
		window.mapControls.select.unselectAll();
	}
	window.mapControls.select.deactivate();
	window.mapControls.modify.deactivate();
}

function setControlDisplayProjection(proj)
{
	var keys = Object.keys(window.mapControls);
	for(var i=0; i<keys.length; i++)
	{	
		if(isPresent(window.mapControls[keys[i]].displayProjection))
			window.mapControls[keys[i]].displayProjection = proj;
	}
}

function show(defs, layers, bounds)
{
	init(defs);
	
	if(enhanceScrollbars())
		$('.scrollbar-test').jScrollPane({
			mouseWheelSpeed: 40
		});
	
	var layer1 = new ol.layer.Tile({
    	source : new ol.source.OSM()
    });
    
    var layer2 = new ol.layer.Tile({
    	source: new ol.source.TileWMS({
    		url : 'http://localhost:8082/geoserver/wms',
    		serverType : 'geoserver',
    		params : {
    			'LAYERS' : 'geoanalytics:Taxon1Lakonia',
    		}
    	})
    });
    
    var layer3 = new ol.layer.Tile({
    	source: new ol.source.TileWMS({
    		url : 'http://localhost:8082/geoserver/wms',
    		serverType : 'geoserver',
    		params : {
    			'LAYERS' : 'geoanalytics:Taxon3Term1',
    		}
    	})
    });
    
    var map = new ol.Map({
    	target: 'map',
        controls: ol.control.defaults({
            zoom: false,
            attribution: false,
            rotate: false
          }),
        layers: [
          layer1,layer2,layer3
        ],
        view: new ol.View({
          center: ol.proj.fromLonLat([22.00, 37.00]),
          zoom: 8
        })
    });
      
    var dragPanInteraction = new ol.interaction.DragPan();
      
    mousePositionControl = new ol.control.MousePosition({
    	projection: 'EPSG:4326',
    	coordinateFormat: ol.coordinate.createStringXY(5),
    	target: document.getElementById('tools-info-con')
    });
    
    map.on('pointerup', function(evt) {
    	map.getViewport().style.cursor = "default";
    });
    
    map.on('pointerdrag', function(evt) {
    	map.getViewport().style.cursor = "move";
    });
    
    map.on('moveend', moveEnd);
    
    map.getView().on('change:resolution', function(evt){
    	var resolution = evt.target.get('resolution');
        var units = map.getView().getProjection().getUnits();
        var dpi = 25.4 / 0.28;
        var mpu = ol.proj.METERS_PER_UNIT[units];
        var scale = resolution * mpu * 39.37 * dpi;
        if (scale >= 9500 && scale <= 950000) {
          scale = Math.round(scale / 1000) + "K";
        } else if (scale >= 950000) {
          scale = Math.round(scale / 1000000) + "M";
        } else {
          scale = Math.round(scale);
        }
        $('#tools-info-scale').text("1 : " + scale);
    });
    
    map.addControl(new ol.control.ScaleLine({
    	units: 'metric',
    	//className : "ol-scale-line-inner"
    }));
      
    map.addControl(new ol.control.ScaleLine({
    	units: 'us',
    	className : 'ol-scale-line-inner2',
    	target : $('.ol-scale-line.ol-unselectable')[0]
    }));
    
    map.addControl(mousePositionControl);
    map.addInteraction(dragPanInteraction);
    
    $('.ol-mouse-position').insertBefore('.dropup');
//    $('.ol-mouse-position').addClass('col-lg-7');
    $('div#tools-info-scale').text("1 : "+getCurrentScale());
      
    function moveEnd(){
    	var res = getResolutionFromScale(getCurrentScale());
    	res = displayResolutionProperly(res);
//    	  alert(getCurrentScale());
    }
      
    function getResolutionFromScale(scale){
    	var units = map.getView().getProjection().getUnits();
    	var dpi = 25.4 / 0.28;
    	var mpu = ol.proj.METERS_PER_UNIT[units];
    	var resolution = scale/(mpu * 39.37 * dpi);
    	return resolution;
    }
      
    function getCurrentScaleNotRounded(){
    	var thisMap = map;
    	var view = thisMap.getView(); ;
    	var resolution = view.getResolution();
    	var units = thisMap.getView().getProjection().getUnits();
    	var dpi = 25.4 / 0.28;
    	var mpu = ol.proj.METERS_PER_UNIT[units];
    	var scale = resolution * mpu * 39.37 * dpi;
    	
    	return scale;
    }
      
    function getCurrentScale(){
    	var thisMap = map;
    	var view = thisMap.getView(); ;
    	var resolution = view.getResolution();
    	var units = thisMap.getView().getProjection().getUnits();
    	var dpi = 25.4 / 0.28;
    	var mpu = ol.proj.METERS_PER_UNIT[units];
    	var scale = resolution * mpu * 39.37 * dpi;
    	if (scale >= 9500 && scale <= 950000) {
            scale = Math.round(scale / 1000) + "K";
    	} else if (scale >= 950000) {
            scale = Math.round(scale / 1000000) + "M";
    	} else {
            scale = Math.round(scale);
    	}
    	
    	return scale;
    }
      
    function displayResolutionProperly(resolution){
    	return resolution;
    }
      
//      var wms = new OpenLayers.Layer.WMS( "OpenLayers WMS",
//          "http://vmap0.tiles.osgeo.org/wms/vmap0", {layers: 'basic', projection: new OpenLayers.Projection("EPSG:4326"), displayProjection: new OpenLayers.Projection("EPSG:4326")} );
//
//     //map.addLayer(wms);
//	   
//      var osm = new OpenLayers.Layer.OSM();
//      
//		var gphy = new OpenLayers.Layer.Google(
//			"Google Physical",
//			{type: google.maps.MapTypeId.TERRAIN, projection: new OpenLayers.Projection("EPSG:4326"), displayProjection: new OpenLayers.Projection("EPSG:4326"), visibility: false}
//		);
//		var gmap = new OpenLayers.Layer.Google(
//			"Google Streets", // the default
//			{numZoomLevels: 20, projection: new OpenLayers.Projection("EPSG:4326"), displayProjection: new OpenLayers.Projection("EPSG:4326"), visibility: false}
//		);
//		var ghyb = new OpenLayers.Layer.Google(
//			"Google Hybrid",
//			{type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20, projection: new OpenLayers.Projection("EPSG:4326"), displayProjection: new OpenLayers.Projection("EPSG:4326"), visibility: false}
//		);
//		var gsat = new OpenLayers.Layer.Google(
//			"Google Satellite",
//			{type: google.maps.MapTypeId.SATELLITE, numZoomLevels: 22, projection: new OpenLayers.Projection("EPSG:4326"), displayProjection: new OpenLayers.Projection("EPSG:4326"), visibility: false}
//		);
//		
//		var ktim = new OpenLayers.Layer.WMS('Κτηματολόγιο Α.Ε.', 'http://gis.ktimanet.gr/wms/wmsopen/wmsserver.aspx', {
//          layers: 'BASEMAP',
//          transparent: true,
//          format: 'image/jpeg'
//      }, {
//          projection: "EPSG:4326",
//          visibility: false,
//          isBaseLayer: false,
//          type: 'wms-ktimatologio',
//      });
//		 ktim.setTileSize(new OpenLayers.Size(512, 512));
		 
//		 var renderer = OpenLayers.Util.getParameters(window.location.href).renderer;
//		 renderer = (renderer) ? [renderer] : OpenLayers.Layer.Vector.prototype.renderers;
	        
		 window.map = map;
		 window.mapLayers = {};
		 window.mapLayers.base = {};
		 window.mapLayers.overlays = {};
			
//		window.mapLayers.vector = new OpenLayers.Layer.Vector("User Projects", 
//				{
//					isBaseLayer: false, 
//					visibility: true,
//					minScale: 100000,
//					styleMap: new OpenLayers.StyleMap({'default':{
//		                 strokeColor: "#ea6300",
//		                 strokeOpacity: 1,
//		                 strokeWidth: 3,
//		                 fillColor: "#ea6300",
//		                 fillOpacity: 0.5,
//		                 pointRadius: 6,
//		                 pointerEvents: "visiblePainted",
//		                 // label with \n linebreaks
//		                 label : "${title}\n",
//		                 
//		                 fontColor: "#eeeeee",
//		                 fontSize: "12px",
//		                 fontFamily: '"Century Gothic", CenturyGothic, "Open Sans", "Didact Gothic", AppleGothic, "Avant Garde", Avantgarde, Futura, sans-serif', 
//		                 fontWeight: "bold",
//		                 /*labelAlign: "${align}",
//		                 labelXOffset: "${xOffset}",
//		                 labelYOffset: "${yOffset}",*/
//		                 labelOutlineColor: "#eeeeee",
//		                 labelOutlineWidth: 0
//		             }}),
//		             renderers: renderer
//				});
		
//			window.mapLayers.editableVector =  new OpenLayers.Layer.Vector("Project Editable", {isBaseLayer: false, visibility: false});
//			window.mapLayers.editableVector.events.on({
//	            'featureselected': function(feature) {
//	            	if(this.selectedFeatures.length == 1)
//	            	{	
//	            		$('#tools-drawing-point').hide();
//	            		$('#tools-drawing-polygon').hide();
//	            		$('#tools-drawing-freehand').hide();
//	            		$('#tools-drawing-select').hide();
//	            		//$('#tools-drawing-cancel').hide();
//	            		//$('#tools-drawing-done').hide();
//	            		$('#tools-drawing-modify').show();
//	            		$('#tools-drawing-drag').show();
//	            		$('#tools-drawing-delete').show();
//	            	}
//	            	if(this.selectedFeatures.length > 1)
//	            	{
//	            		$('#tools-drawing-modify').hide();
//	            		$('#tools-drawing-drag').hide();
//	            	}
//	            },
//	            'featureunselected': function(feature) {
//	            },
//	            'featureadded': function(feature) {
//	            	if(this.features.length == 1)
//	            	{
//	            		$('#tools-drawing-select').show();
//	            	}
//	            }
//	        });
		 
			//TODO openlayers 2 migrate
//			window.mapLayers.base[osm.name] = osm;
//			window.mapLayers.base[gphy.name] = gphy;
//			window.mapLayers.base[gmap.name] = gmap;
//			window.mapLayers.base[ghyb.name] = ghyb;
//			window.mapLayers.base[gsat.name] = gsat;
//			window.mapLayers.base[ktim.name] = ktim;
			
			
//			var projWGS84 = ol.proj.get('EPSG:4326');
//			
//			var wms_layers = [osm, gphy, gmap, ghyb, gsat, ktim];
//			var layerNames = Object.keys(layers);
//			for(var i=0; i<layerNames.length; i++)
//			{
//				var opts = {isBaseLayer: false, visibility: true};
//				if(isPresent(layers[layerNames[i]].minScale))
//					opts.minScale = layers[layerNames[i]].minScale;
//				if(isPresent(layers[layerNames[i]].maxScale))
//					opts.maxScale = layers[layerNames[i]].maxScale;
//				var wms_layer = new OpenLayers.Layer.WMS(
//					layerNames[i],
//					"wms",
//					{
//						layers: "geopolis:"+layerNames[i],
//						transparent: "true",
//						format: "image/png",
//						bgcolor: "0xcccccc"
//					},
//					opts
//				);
//				wms_layers.push(wms_layer);
//				window.mapLayers.overlays[layerNames[i]] =  wms_layer;
//			}
//
//			window.mapControls = {
//		        layerSwitcher : new OpenLayers.Control.LayerSwitcher(),
//		        navigation : new OpenLayers.Control.Navigation(),
//		        mousePosition: new OpenLayers.Control.MousePosition({
//		        	element: $('#tools-info-coords')[0],
//		        	displayProjection: projWGS84
//		        }),
//		        scale : new OpenLayers.Control.Scale($('#ol-info-scale')[0], {
//		        	geodesic: true
//		        }),
//		        scaleLine : new OpenLayers.Control.ScaleLine({geodesic: true}),
//		        point: new OpenLayers.Control.DrawFeature(window.mapLayers.editableVector, OpenLayers.Handler.Point),
//		        polygon: new OpenLayers.Control.DrawFeature(window.mapLayers.editableVector, OpenLayers.Handler.Polygon, {handlerOptions: {freehand: false}}),
//		        freehand: new OpenLayers.Control.DrawFeature(window.mapLayers.editableVector, OpenLayers.Handler.Polygon, {handlerOptions: {freehand: true}}),
//		        select: new OpenLayers.Control.SelectFeature(
//			            window.mapLayers.editableVector,
//			            {
//			                clickout: false, toggle: true,
//			                multiple: false, hover: false,
//			                toggleKey: "ctrlKey", // ctrl key removes from selection
//			                box: true
//			            }
//			        ),
//			    modify: new OpenLayers.Control.ModifyFeature(window.mapLayers.editableVector)
//			};
			
//			window.mapControls.polygon.handler.freehandToggle = null;
//			window.mapControls.freehand.handler.freehandToggle = null;
			
//	        for(var i=0; i<Object.keys(window.mapControls).length; i++)
//	        	map.addControl(window.mapControls[Object.keys(window.mapControls)[i]]);
			
//	        map.events.register("zoomend", this, function (e) {
//	        	var tis = $('#ol-info-scale').text();
//	        	$('#tools-info-scale').text(tis.substring("Scale = ".length));
//	        });
	        
//	        map.events.register('click', map, function (e) {
//	        	if(window.drawingToolbarOn == true)
//	        		return;
//		        //document.getElementById(container.id+'_showShapeFeatureInfo').innerHTML = "Loading... please wait...";
//	        	var lNames = "";
//	        	for(var i=0; i<layerNames.length; i++)
//	        	{
//	        		lNames += "geopolis:" + layerNames[i];
//	        		if(i<layerNames.length-1)
//	        			lNames += ",";
//	        	}
//	        	
//	        	var params = {
//		            REQUEST: "GetFeatureInfo",
//		            EXCEPTIONS: "application/vnd.ogc.se_xml",
//		            BBOX: map.getExtent().toBBOX(),
//		            SERVICE: "WMS",
//		            INFO_FORMAT: 'application/json',
//		            QUERY_LAYERS: lNames,
//		            FEATURE_COUNT: 50,
//		            Layers: lNames,
//		            WIDTH: map.size.w,
//		            HEIGHT: map.size.h,
//		            format: "application/json",
//		            styles: "",
//		            srs: "EPSG:900913"
//	        	};
//		        
//		        // handle the wms 1.3 vs wms 1.1 madness
//		        if(map.layers[0].params.VERSION == "1.3.0") {
//		            params.version = "1.3.0";
//		            params.j = parseInt(e.xy.x);
//		            params.i = parseInt(e.xy.y);
//		        } else {
//		            params.version = "1.1.1";
//		            params.x = parseInt(e.xy.x);
//		            params.y = parseInt(e.xy.y);
//		        }
//	            
//		        // merge filters
//		        if(map.layers[0].params.CQL_FILTER != null) {
//		            params.cql_filter = map.layers[0].params.CQL_FILTER;
//		        } 
//		        if(map.layers[0].params.FILTER != null) {
//		            params.filter = map.layers[0].params.FILTER;
//		        }
//		        if(map.layers[0].params.FEATUREID) {
//		            params.featureid = map.layers[0].params.FEATUREID;
//		        }
//		        
//		        var responseParams = {ev: e, defs: defs};
//		        //setHTML.container = container;
//		        OpenLayers.Request.GET({
//		        						url: "wms", 
//		        						params: params, 
//		        						callback:  OpenLayers.Function.bind(showFeatureInfo, null, responseParams) });
//	        
//		        OpenLayers.Event.stop(e);
//		    });
	        
//			window.map.zoomToExtent(bounds);
//			bounds = adjustZoomBounds(bounds);
//			window.map.zoomToExtent(bounds);	 
}

function adjustZoomBounds(bounds)
{
	var boundsToZoom = new OpenLayers.Bounds(bounds.left, bounds.bottom, bounds.right, bounds.top);
	var mapBounds = window.map.calculateBounds();
	if(!mapBounds)
		return bounds;
    if($('#right-pane-container').hasClass('pane-open'))
    {
    	var percentWidth = parseFloat($('#right-pane-container').css('width')) / parseFloat($('#right-pane-container').parent().css('width') );
    	boundsToZoom.left += (mapBounds.right-mapBounds.left)*(0.5*percentWidth);
    	boundsToZoom.right += (mapBounds.right-mapBounds.left)*(0.5*percentWidth);
    }else
    {
    	boundsToZoom.left += 0.075*(mapBounds.right-mapBounds.left);
    	boundsToZoom.right += 0.075*(mapBounds.right-mapBounds.left);
    }
    return boundsToZoom;
}

function showFeatureInfo(params, response)
{
	var e = params.ev;
	var defs = params.defs;
	
	if(!isPresent(response) || !isPresent(response.responseText))
		return;
	var featureInfo = JSON.parse(response.responseText);
	
	var infoPopup = document.createElement("div");
	//"{"type":"FeatureCollection","features":[{"type":"Feature","id":"Taxon1Term1.fid-503632a5_143fd227962_-4b2","geometry":null,"properties":{"municipality":"Δ. ΕΥΡΩΤΑ"}}]}"
	//"{"type":"FeatureCollection","features":[{"type":"Feature","id":"Taxon1Term6.fid-503632a5_143fd227962_-4aa","geometry":null,"properties":{"sd":2.8,"settlement":"ΣΠΑΡΤΗ"}},
	////{"type":"Feature","id":"Taxon1Term6.fid-503632a5_143fd227962_-4a9","geometry":null,"properties":{"sd":2.4,"settlement":"ΣΠΑΡΤΗ"}},
	////{"type":"Feature","id":"Taxon1Term6.fid-503632a5_143fd227962_-4a8","geometry":null,"properties":{"sd":0.8,"settlement":"ΣΠΑΡΤΗ"}},
	////{"type":"Feature","id":"Taxon1Term6.fid-503632a5_143fd227962_-4a7","geometry":null,"properties":{"sd":0.8,"settlement":"ΣΠΑΡΤΗ"}},
	////{"type":"Feature","id":"Taxon1Term6.fid-503632a5_143fd227962_-4a6","geometry":null,"properties":{"sd":null,"settlement":"ΚΑΜΑΡΕΣ"}},{"type":"Feature","id":"Taxon1Term6.fid-503632a5_143fd227962_-4a5","geometry":null,"properties":{"sd":0.8,"settlement":"ΣΠΑΡΤΗ"}},{"type":"Feature","id":"Taxon1Term1.fid-503632a5_143fd227962_-4a4","geometry":null,"properties":{"municipality":"Δ. ΣΠΑΡΤΗΣ"}}]}"
	var featureProps = {};
	for(var i=0; i<featureInfo.features.length; i++)
	{
		var props = Object.keys(featureInfo.features[i].properties);
		for(var j=0; j<props.length; j++)
		{
			if(isPresent(featureProps[props[j]]))
				return; //location refers to multiple features
			featureProps[props[j]] = featureInfo.features[i].properties[props[j]];
		}
	}
	
	if(Object.keys(featureProps).length == 0)
		return;
	
	var keys;
	//geographic attributes go first
	for(i=0; i<defs.geographyHierarchy.length; i++)
	{
		keys = Object.keys(featureProps);
		for(var j=0; j<keys.length; j++)
		{
			if(keys[j].toLowerCase() ==  defs.geographyHierarchy[i].toLowerCase())
			{
				var geogCategory = findAttributeCategory(keys[j], defs);
				var row = createAttributeRow({
					 type: keys[j],
					 category: geogCategory, 
					 isGeographic: true, 
					 value: featureProps[keys[j]],
					 defs: defs, 
					 assignIds: false, 
					 assignClasses: true,
					 widths: [6,6]
				});
				infoPopup.appendChild(row);
			}
		}
	}

	var attrVals = {};
	for(i=0; i<defs.projectInfoCategories.length; i++)
	{
		var categoryTypes = defs.projectInfoCategoryTypes[defs.projectInfoCategories[i]];
		for(var j=0; j<categoryTypes.length; j++)
		{
			var foundInfo = null;
			keys = Object.keys(featureProps);
			for(var k=0; k<keys.length; k++)
			{
				if(keys[k].toLowerCase() ==  categoryTypes[j].toLowerCase())
				{
					foundInfo = featureProps[keys[k]];
					break;
				}
			}
			if(!isPresent(foundInfo))
				continue;
			var category = findAttributeCategory(categoryTypes[j], defs);
			var row = createAttributeRow({
				type: categoryTypes[j],
				category: category, 
				isGeographic: false, 
				value: foundInfo, 
				defs: defs, 
				assignIds: false, 
				assignClasses: true,
				widths: [6,6]
			});
			attrVals[categoryTypes[j]] = foundInfo;
			infoPopup.appendChild(row);
		}
	}
	
	var position = map.getLonLatFromPixel(e.xy);
    var size = new OpenLayers.Size(25,35);
    var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
    var icon = new OpenLayers.Icon(imgLoc(defs, 'MARKER_ICON_LARGE', true), size, offset);   

   // var infoMarker = new OpenLayers.Marker(position,icon);
   // infoMarker.display(false);
   // window.mapLayers.markers.addMarker(infoMarker);
    
    var feature = new OpenLayers.Feature(window.mapLayers.markers, position, {icon: icon}); 
    feature.closeBox = true;
    feature.data.autoSize = true;
    feature.data.overflow = 'hidden';
    feature.data.closeBoxCallback = function() {  //TODO check why not called
   	 this.marker.display(false);
	 this.marker.destroy();
	 this.hide();
	 this.destroy();
};
    feature.popupClass = OpenLayers.Class(OpenLayers.Popup.Anchored, {
        'autoSize': true
    });
    feature.data.popupContentHTML = 	'<div class="modal-header">\
    	<h4 class="modal-title">'+defs.i18n.nav.locationInfo+'</h4>\
    	</div>' + infoPopup.innerHTML;
    
    var infoMarker = feature.createMarker();
    infoMarker.display(false);
    window.mapLayers.markers.addMarker(infoMarker);
    /*var markerClick = function (evt) {
        if (this.popup == null) {
            this.popup = this.createPopup(this.closeBox);
            map.addPopup(this.popup);
            this.popup.show();
        } else {
            this.popup.toggle();
        }
        OpenLayers.Event.stop(evt);
    };
    infoMarker.events.register("mousedown", feature, markerClick);*/
    feature.createPopup(feature.closeBox);
    map.addPopup(feature.popup);
    feature.popup.show();
    

	$.ajax({ 
        url : "documents/attributeDocuments",
        type : "post", 
        contentType: "application/json",
        dataType: 'json',
        data : JSON.stringify(attrVals),
        success : function(response) 
        		  {
        			if(response.status == "Unauthorized") alert(defs.i18n.project.error.unauthorized);
        			if(response.status == "Failure") alert(defs.i18n.error.failure + ":" + response.message);
					var docs = response.response;
					var docKeys = Object.keys(docs);
					for(var i=0; i<docKeys.length; i++)
					{
						var attrRow = $(feature.popup.contentDiv).find(".pA-title:contains('"+defs.i18n.taxon[docKeys[i]]+"')").parent();
						var icon = document.createElement('img');
						icon.src = imgLoc(defs, 'VIEW_DOCUMENT_ICON', true);
						icon.documentId = docs[docKeys[i]];
						
						var iconCon = document.createElement('div');
						iconCon.appendChild(icon);
						iconCon.addEventListener('click', documentViewButtonListener);
						$(attrRow).find("div:last-child").append('<div class="pA-actions"></div>');
						$(attrRow).find("div:last-child .pA-actions").append(iconCon);
						
						icon = document.createElement('img');
						icon.src = imgLoc(defs, 'DOWNLOAD_DOCUMENT_ICON', true);
						icon.documentId = docs[docKeys[i]];
						
						iconCon = document.createElement('div');
						iconCon.appendChild(icon);
						iconCon.addEventListener('click', documentDownloadButtonListener);
						$(attrRow).find("div:last-child .pA-actions").append(iconCon);
					}
					
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
    //window.mapLayers.markers.addMarker(marker);
    
	return;
}