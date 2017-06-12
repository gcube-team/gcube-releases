<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="theme" %>
<theme:defineObjects/>
<portlet:defineObjects />
<!-- CSS -->
<!-- jquery datatables css -->
<link rel="stylesheet" type="text/css" href="<c:url value="/css/pickProject/pickProejct.css?01" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/jquery_datatables/jquery.dataTables.css?01" />" />
<!-- font-awesome -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
<link rel="stylesheet" href="<c:url value="/css/fonts/slick.ttf" />" />
<link rel="stylesheet" href="<c:url value="/css/fonts/slick.woff" />" />
<!-- jstree style -->
<link rel="stylesheet" href="<c:url value="/css/jstree/style.css?01" />">
<!-- my css -->
<link rel="stylesheet" type="text/css" href="<c:url value="/css/pickProject/ProjectTile.css?01" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/pickProject/Responsive/projectsDataTable.css?01" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/pickProject/Modals/Modal.css?01" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/Controls/NavToolbarControl.css?01" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/jstree/checkboxStyling.css?01" />" />

<!-- jstree style -->
	
	<link rel="stylesheet" href="<c:url value="/css/jstree/style.css?01" />">
	
	<!-- slick carouse -->
	
	<link rel="stylesheet" href="<c:url value="/css/slick.css?01" />">
	<link rel="stylesheet" href="<c:url value="/css/slick-theme.css?01" />">
	<link rel="stylesheet" href="<c:url value="/css/CoordinatesLine.css?01" />" />
	<link rel="stylesheet" href="<c:url value="/css/Widgets/FunctionsAccordion.css?01" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/CollapsibleMenuLeft.css?01" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/SearchBar.css?01" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/CollapsibleMenuRight.css?01" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/DockedGISTools.css?01" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/popoverOverlays.css?01" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/Map-Viewport-Containers.css?01" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/Modals.css?01" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/ShowMoreLessText.css?01" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/Markers.css?01" />" />
	<%-- <link rel="stylesheet" type="text/css" href="<c:url value="/css/ResponsiveTabletToolbarPanes.css?01" />" /> --%>
	<%-- <link rel="stylesheet" type="text/css" href="<c:url value="/css/MediaQueries/Responsive.css?01" />" /> --%>
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/Controls/NavToolbarControl.css?01" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/css/geoanalyticsSettings.css?01" />" />
	<link rel="icon" type="image/png" href="img/logo3.png">
	<link rel="stylesheet" href="<c:url value="/css/openlayers3/ol.css?01" />" type="text/css">
	<link rel="stylesheet" href="<c:url value="/css/FullScreenMode.css?01" />" type="text/css">
	<link rel="stylesheet" href="<c:url value="/js/jquery-ui-1.12.0/jquery-ui.css" />" type="text/css">
	<link rel="stylesheet" href="<c:url value="/js/tagit/css/jquery.tagit.css" />" type="text/css">

	<script src="<c:url value="/js/jspdf/jspdf.min.js?01" />"
		type="text/javascript"></script>
	<script src="<c:url value="/js/Assets/proj4.js?01" />"
		type="text/javascript"></script>

	<script src="<c:url value="/js/jquery-1.10.2.min.js?01" />"></script>
	<script src="<c:url value="/js/jquery-ui-1.12.0/jquery-ui.js?01" />"></script>
	<!-- slick -->
	<script src="<c:url value="/js/slick.js?01" />"></script>
	<!-- itemslide -->
	<script src="<c:url value="/js/itemslide.min.js?01" />"></script>
	<%-- <script src="<c:url value="/js/tagit/js/tag-it.js?01" />" type="text/javascript"></script> --%>
	<script src="<c:url value="/js/jstree/jstree.js?01" />"	type="text/javascript"></script>
	<script src='<c:url value="/js/Utils/AjaxCalls.js?01" />'></script>
	<script src='<c:url value="/js/Utils/CreateLink.js?01" />'></script>
	<script src='<c:url value="/js/Utils/HasRight.js?01" />'></script>
	<script src='<c:url value="/js/Layers/RetrieveLayers.js?01" />'></script>
	<script src='<c:url value="/js/bootstrap.js?01" />'></script>
	<script src='<c:url value="/js/DrawCoordinates.js?01" />'></script>
	<script src='<c:url value="/js/LeftSidePanel.js?01" />'></script>
	<script src='<c:url value="/js/RightSidePanel.js?01" />'></script>
	<script src='<c:url value="/js/Searchbar.js?01" />'></script>
	<script src='<c:url value="/js/Export.js?01" />'></script>
	<script src='<c:url value="/js/ShowMoreLessText.js?01" />'></script>
	<script src='<c:url value="/js/geoanalytics.js?01" />'></script>
	<script src='<c:url value="/js/Controls/CustomControls.js?01" />'></script>
	<script src='<c:url value="/js/ExportMap.js?01" />'></script>
	<script src='<c:url value="/js/Widgets/Functions.js?01" />'></script>
	<script src='<c:url value="/js/ResponsiveMode.js?01" />'></script>
	<script src='<c:url value="/js/LoadProject.js?01" />'></script>
	<script src="<c:url value="/js/Modals/viewMoreModal.js?01" />"> </script>


<p id="currentURL" class="hidden" hidden="true"><%=themeDisplay.getURLCurrent() %></p>
<p id="portletInfo" data-namespace="<portlet:namespace/>" data-loginurl="<portlet:resourceURL />"></p>


<div id="pickProjectContainer" class="">
	<div id="spinner" class="hidden">
		<i class="fa fa-spinner fa-spin"></i>
	</div>
<div id="blanket">
	<p id="portletInfo" data-namespace="<portlet:namespace/>" data-loginurl="<portlet:resourceURL />"></p>
	
	
	<ul class="nav nav-tabs" id="appTabs">
	  <li class="active appTabItem">
	  	<a class="tabLink notClicked" href="#projectManagement">Project Management</a>
	  </li>
	  <li class="appTabItem">
	  	<a class="tabLink" href="#DSS" data-toggle="tab">Decision Support System</a>
	  </li>
	</ul>
	
	
	<div class="tab-content">
		<div class="tab-pane active" id="projectManagement">
		
			<div id="projectsHeader">
				<h4 id="projects">Projects</h4>
				<div id="projectsSubheading">choose a project or create a new one</div>
			</div>
			
			<div id="projectTilesContainer">
				<div id="tableSection">
					<table id="ProjectSelectionTable" class="display no-wrap" style="width: 100%;">
						<thead>
							<tr role="row">
								<th>
									name
								</th>
								<th>
									email
								</th>
								<th>
									groups
								</th>
								<th>
									projects
								</th>
								<th>
									buttons
								</th>
								<th>
									auxiliaryColumn
								</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
		
		</div>
		<div class="tab-pane" id="DSS">
		
			<div id="DecisionSupportSystem">
				<div id="spinner" class="hidden">
					<i class="fa fa-spinner fa-spin"></i>
				</div>
				
				<div id="projectAndDate">
					<p id="nameOfProject">No projects available(Select or create a new project using the Project Management tool)</p>
					<p id="dateOfProject">00.00.0000</p>
				</div>
				
				<div id="mainScreen">
					<div id="mapCon">
						<div id="mapContainer" class="row-fluid">
							<!-- <div id="toolbarNotVisibleInDesktops" class="hidden-desktop-large"> -->
							<div id="toolbarNotVisibleInDesktops" class="hidden">
								<div id="toolbarMenuNotVisibleInDesktops" class="hidden-desktop-large">
									<div id="layersResponsive" class="hidden-desktop-large toolbarMenuNotVisibleInDesktopsBtns row-fluid">
									
										<div class="closeResponsivePane hidden" title="close">
										</div>
									
										<div id="layersIconPlaceHolder" class="hidden-desktop-large span1 offset1"></div>
										<div id="layersButton" class="hidden-desktop-large span6">
											<div class="hidden-desktop-large">
												AVAILABLE LAYERS
											</div>
											<div class="hidden-desktop-large">
												<!-- Toggle on or off the layers -->
												<span id="layersCount">0</span> layers selected
											</div>
										</div>
										<div id="layersButtonCaret" class="hidden-desktop-large span4">
											<i class="fa fa-caret-down hidden-desktop-large"></i>
										</div>
									</div>
									<div id="settingsResponsive" class="hidden-desktop-large toolbarMenuNotVisibleInDesktopsBtns hidden">
										<div id="gearContainerResponsive" class="span2 offset2 hidden-desktop-large"></div>
			
										<div id="settingResponsive" class="span8 hidden-desktop-large">Settings</div>
									</div>
								</div>
							
								<div id="toolbarPaneNotVisibleInDesktops" class="hidden-desktop-large">
									<div id="selectedLayersResponsivePane" class="hidden-desktop-large hidden responsivePanes">
										Active layers:
										
										<p id="noLayersSelected">
											No active layers.
										</p>
										
										<div id="tagsInputContainer" class="hidden">
											<input id="layersTagsInput" type="text" name="layersTagsInput" class="hidden" style="display:none;">
										</div>
										
									</div>
									
									<div id="layersSelectorResponsivePane" class="hidden-desktop-large  hidden responsivePanes">
										<div id="treeviewTaxonomiesLayersResponsive"></div>
									</div>
									
									<div id="selectedFunctionsResponsivePane" class="hidden-desktop-large hidden responsivePanes">
										<div>
											selectedFunctionsResponsivePane
										</div>
									</div>
									
									
									<div id="functionsSelectionResponsivePane" class="hidden-desktop-large  responsivePanes">
										<div id="functionsResponsiveSlider" class="">
											<span class="span4 offset5 zeroFunctions">No functions available</span>
											<ul id="functionsResponsiveList" class="">
												<!-- <li>
											        <span class="functionResponsiveName">function 1</span>
											    </li>
											    <li>
											        <span class="functionResponsiveName">function 2</span>
											    </li>
											    <li>
											        <span class="functionResponsiveName">function 3</span>
											    </li>
											    <li>
											        <span class="functionResponsiveName">function 4</span>
											    </li>
											    <li>
											        <span class="functionResponsiveName">function 5</span>
											    </li>
											    <li>
											        <span class="functionResponsiveName">function 6</span>
											    </li>
											    <li>
											        <span class="functionResponsiveName">function 7</span>
											    </li>
											    <li>
											        <span class="functionResponsiveName">function 8</span>
											    </li>
											    <li>
											        <span class="functionResponsiveName">function 9</span>
											    </li>
											    <li>
											        <span class="functionResponsiveName">function 10</span>
											    </li> -->
											</ul>
											<div id="sliderRightButton" class="sliderButtons disabledSliderButtons">
												<i class="fa fa-arrow-circle-right"></i>
											</div>
											<div id="sliderLeftButton" class="sliderButtons disabledSliderButtons">
												<i class="fa fa-arrow-circle-left"></i>
											</div>
										</div>
									</div>
								</div>
							</div>
			
							<!-- <div id="collapsibleMenuLeft" class="shown visible-desktop"> -->
							<div id="collapsibleMenuLeft" class="shown">
								<div id="collapsibleMenuLeftContainer" class="row-fluid">
								
									<div id="availableLayersContainer">
											<div id="titleAndIcon" class="row-fluid">
												<div id="availableLayersIcon" class="span2"></div>
												<div id="availableLayersTiteleAndSubtextContainer"
													class="span10">
													<div id="availableLayersTitle">AVAILABLE LAYERS</div>
													<div id="availableLayersTitleSubtext">Toggle on or off
														the layers</div>
												</div>
											</div>
										</div>
								
									<div id="layersPanel" class="span11">
									
										<div id="treeviewTaxonomiesLayers">
			
										</div>
									</div>
								</div>
							</div>
			
							<div id="map" class="">
			
								<div id="coord-info" class="span12">
									<div id='coord-info-con' class="row-fluid">
										<div id="coord-long-section" class="span4">
											<span>Long:</span>
										</div>
										<div id="coord-lat-section" class="span4">
											<span>Lat: </span>
										</div>
										<div id="eye-section" class="span4 eye-section">
											<span>Eye: </span>
										</div>
									</div>
								</div>
			
								<div id="searchBarCenter" class="row-fluid span12">
									<div id="searchBarCenterElementsContainer"
										class="input-append row-fluid">
										<div id="searchbarInputContainer" class="span7">
											<input id="DSSSearchbar" type="text" placeholder="Search..."  class="span11" spellcheck="false"> 
											<input id="autocomplete-hint" type="text"  disabled="disabled" class="span11" >
										</div>
										<div id="searchBarRadioButtonsContainer" class="span5 row-fluid">
											<div class="span6 ">
												<div id="thisViewPort" class="pickViewport clicked">
													<i class="fa fa-dot-circle-o"></i>
												</div>
												THIS VIEWPORT <span class="span11 radiobuttonSubheading">
																	Apply to this viewport
																</span>
											</div>
											<div class="span6 ">
												<div id="globallyViewport" class="pickViewport">
													<i class="fa fa-circle-o"></i>
												</div>
												GLOBALLY <span class="span11 radiobuttonSubheading">Apply globally</span>
											</div>
										</div>
									</div>
								</div>
			
							</div>
			
							<!-- <div id="collapsibleMenuRight" class="shown visible-desktop"> -->
							<div id="collapsibleMenuRight" class="shown">
									
								<div id="collapsibleMenuRightContainer" class="row-fluid">
									<div id="functionsPanel" class="span12">

											<div id="spinnerPlugin" class="hidden">
												<i class="fa fa-spinner fa-spin"></i>
											</div>

											<div class="functionsAccordion new">
										
										</div>
			
			
										<div id="adminSettingsButtonContainer" class="row-fluid span12">
			
											<div id="gearContainer">
											</div>
			
											<div id="setting">Settings</div>
			
										</div>
									</div>
			
			
			
								</div>
			
							</div>
							
							<div id="overlayForSingleClickingOnMapContainer" class="">
								<div id="overlayForSingleClickingOnMap" class="">
									<div id="popoverTitleContainer" class="">
										<div id="popoverUpperSection" class="span12">
											<div id="coordsOnPopoverTitleContainer" class="span9">
												<div id="coordsOnPopoverTitle" class="">37° 09′ 53″ N 21°
													21′ 46″ E</div>
												<div id="regionInPathForm" class=""></div>
											</div>
											<div id="closePopover" class="span1"></div>
											<div id="popoverMapPin" class="span1"></div>
										</div>
										<div id="popoverBodyContainingInfo" class="row-fluid">
											<div class="row-fluid popoverInfoRow">
												<div class="popoverInfoRowLabel span6">
													</div>
												<div class="popoverInfoRowData span5"></div>
											</div>
											<div class="row-fluid popoverViewAllRow">
												<div class="viewAllContainer row-fluid span12">
													<button id="popoverInfoViewAll" class="span5 offset6">View
														more</button>
												</div>
											</div>
										</div>
									</div>
								</div>
			
							</div>
						</div>
					</div>
				</div>
			
				<div id="right-pane-container">
					<div id="right-pane">
						<div id="right-pane-overlay">
							<div id="right-pane-overlay-content"></div>
						</div>
					</div>
				</div>
				
				<!-- view more modal  -->
					<div id="mapExportModal" class="modal fade in"  tabindex="-1" role="dialog"
					aria-labelledby="settingsModal" aria-hidden="true" style="display:none">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">×</button>
								<div class="row-fluid">
									<div id="coordsOnModalContainer" class="span11">
										<div id="coordsOnModalTitleContainer" class="span10">
											<div id="coordsOnModalTitle" class="">37° 09′ 53″ N 21° 21′
												46″ E</div>
											<div id="regionInPathFormInModal" class=""> / /
												 /  / </div>
										</div>
									</div>
								</div>
						</div>
						<div class="modal-body">
							<div id="mapAreaContainer" class="span6"></div>
							<div id="modalInfo" class="span6">
								<div id="modalAttributesContainer">
									<div class="row-fluid modalInfoRow">
										<div class="modalInfoRowLabel span6">Data :</div>
										<div class="modalInfoRowData span5">Not found</div>
									</div>
								</div>
								
							
							</div>
							
						</div>
						<div class="modal-footer">
							<div id="functionRunAndExportButtons" class="row-fluid btn-group">
								<button id="closeButtonModalBottom" class="span1 pull-right">
									Close
								</button>
								<div id="exportAsButtonModalBottom"
									class="span1 dropdown pull-right">
									<a class="dropdown-toggle" role="button" data-toggle="dropdown"
										href="#" id="dropMenu"> Export as<i
										class="fa fa-caret-down"></i>
									</a>
									<ul class="dropdown-menu" role="menu"
										aria-labelledby="dropMenu">
										<li role="presentation"><a role="menuitem" tabindex="-1"
											href="#" id="exportAsPNG" download="map.png"> <i
												class="fa fa-download" aria-hidden="true"></i>&nbsp;PNG
										</a></li>
										<li role="presentation"><a role="menuitem" tabindex="-1"
											href="#" id="exportAsJPEG" download="map.jpg"> <i
												class="fa fa-download" aria-hidden="true"></i>&nbsp;JPEG
										</a></li>
										<li role="presentation"><a role="menuitem" tabindex="-1"
											href="#" id="exportAsPDF" download="map.pdf"> <i
												class="fa fa-print" aria-hidden="true"></i>&nbsp;PDF
										</a></li>
									</ul>
								</div>
							</div>
						</div>
					</div>
					
						<!-- Upload plugins Modal -->
					<div id="addPluginModal" class="modal fade in"  role="dialog" data-backdrop="static" data-keyboard="false" href="#"
						 aria-hidden="true" style="display:none" >
						 <!-- modal attributes that make modal hide on pressing esc -->
						<!-- tabindex="-1" -->
						<!-- aria-labelledby="addPluginModal" -->
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">×</button>
							<h5 id="modalLabel">Upload a plugin library</h5>
						</div>
						<div class="modal-body">
							<div class="pluginLibrarySection">
								
								<div class="row-fluid">
									<label class="span6" for="pluginInLibraryName">
										<span class="pull-right asterisk">*</span>
										Library Name:
									</label>
									<input class="span6" id="pluginInLibraryName" name="pluginInLibraryName" type="text" required/>
								</div>
								
								<div class="row-fluid">
									<label for="pluginLibraryJAR" class="span6">
										Choose a plugin library	from your disk
										<span class="pull-right asterisk">*</span>
									</label>
									<button class="span2" id="goToPluginLibraryJAR">Browse</button>
									<div id="pluginFileName" class="span3" title="Choose file">Choose file </div>
									<input class="span6" type="file" name="pluginLibraryJAR" id="pluginLibraryJAR" />
								</div>
								
							</div>
							
							<div class="pluginsSection">
								<h4 class="pluginsHeader">Specify the plugins contained in your plugin library</h4>
								
								<div class="row-fluid pluginContainers" data-copy="0">
									
									<div id="pluginTile" class="pluginTiles">
										Plugin 
										<i class="icon-chevron-down"></i>
										<i class="icon-chevron-right"></i>
									</div>
									
									<div class="well pluginFormSection">
									
										<div class="row-fluid">
											<label for="pluginInName" class="span6">
												Name:
												<span class="pull-right asterisk">*</span>
											</label>
											<input id="pluginInName"  class="span6" name="pluginInName" type="text" required/>
										</div>
										
										<div class="row-fluid">
											<label for="pluginDescription" class="span6">
												Description:
												<span class="pull-right asterisk">*</span>
											</label>
											<textarea id="pluginDescription" class="span6" name="pluginDescription" rows="4" cols="50" required>
											</textarea>
										</div>
										
										<div class="row-fluid">
											<label class="span6" for="widgetName">
												Widget name:
												<span class="pull-right asterisk">*</span>
											</label>
											<input class="span6" id="widgetName" name="widgetName" type="text" required/>
										</div>
										
										<div class="row-fluid">
											<label class="span6" for="className">
												Qualified name of JAVA class:
												<span class="pull-right asterisk">*</span>
											</label>
											<input class="span6" id="className" name="className" type="text" required/>
										</div>
										
										<div class="row-fluid">
											<label class="span6" for="methodName">
												JAVA-Method name:
												<span class="pull-right asterisk">*</span>
											</label>
											<input class="span6" id="methodName" name="methodName" type="text" required/>
										</div>
										
										<div class="row-fluid">
											<label class="span6" for="jsFileName">
												Script file name:
												<span class="pull-right asterisk">*</span>
											</label>
											<input class="span6" id="jsFileName" name="jsFileName" type="text" required/>
										</div>
										
										<div class="row-fluid">
											<label class="span6" for="configurationClass">
												Configuration class:
												<span class="pull-right asterisk">*</span>
											</label>
											<input class="span6" id="configurationClass" name="configurationClass" type="text" required/>
										</div>
										
									</div>
									
								</div>
								
							</div>
							<div class="row-fluid">
								<a id="addPlugin" class="">
									Next plugin
									<i class="icon-plus-sign"></i>
								</a>
							</div>
						</div>
						<div class="modal-footer">
							<div class="span7 addPluginMessages hidden validation">This field is required</div>
							<div class="span7 addPluginMessages hidden successfullUpload"> Plugin uploaded successfully</div>
							<div class="span7 addPluginMessages hidden failedUpload"> Error during the upload of the plugin</div>
							<button class="span2 pull-right" data-dismiss="modal" aria-hidden="true">Cancel</button>
							<button id="pluginSubmit" class="span2 pull-right">Submit</button>
						</div>
					</div>
					
					<div id="assignRightsToModal" class="modal fade in" tabindex="-1" role="dialog"
						aria-labelledby="settingsModal" aria-hidden="true" style="display:none">
						<div class="modal-header">
							<div id="blueLineBottom">
								<span id="deleteRoleHeader">Project details</span>
								<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
							</div>
						</div>
						<div class="modal-body">
							<div class="projectRightsContainer">
							</div>
						</div>
						<div class="modal-footer">
							<button id="projectRightsOKButton" class="btn" aria-hidden="true">OK</button>
							<button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
						</div>
					</div>
					
	
	
		</div>
	</div>
	
	<div id="projectViewModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span id="deleteRoleHeader">Project details</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<div class="row-fluid">
	  		<div class="span4 viewDetailsLabels">Name:</div>
	  		<div id="viewModalProjectName" class="viewModalProjectAttributes"></div>
	  	</div>
	  	
	  	<div class="row-fluid">
	  		<div class="span4 viewDetailsLabels">Description:</div>
		  	<div id="viewModalProjectDescription" class="viewModalProjectAttributes"></div>
	  	</div>
	  	
	  	<div class="row-fluid">
	  		<div class="span4 viewDetailsLabels">Creator:</div>
	  		<div id="viewModalProjectCreator" class="viewModalProjectAttributes"></div>	
	  	</div>
	  	
	  	<div class="row-fluid">
	  		<div class="span4 viewDetailsLabels">Creation date:</div>
	  		<div id="viewModalProjectDate" class="viewModalProjectAttributes"></div>	
	  	</div>
	  	
	  	<div class="row-fluid">
	  		<div class="span4 viewDetailsLabels">Members:</div>
	  		<div id="viewModalProjectParticipants" class="viewModalProjectAttributes span8"></div>	
	  	</div>
	  	
	  	<div class="row-fluid">
	  		<div class="span4 viewDetailsLabels">Layers:</div>
			<div id="viewModalProjectLayerNames" class="viewModalProjectAttributes span8"></div>	
	  	</div>
	  </div>
	  <div class="modal-footer">
	    <button id="OKOnProjectViewModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">OK</button>
	  </div>
	</div>
	
	
	<div id="InternalServerErrorModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span id="deleteRoleHeader">Internal server error</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<p>An internal server error has occured. Please try again later</p>
	  </div>
	  <div class="modal-footer">
	    <button id="closeInternalServerModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">OK</button>
	  </div>
	</div>
	
	<div id="nullLayerModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span id="deleteRoleHeader">No layer loaded</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<p>Please select a layer.</p>
	  </div>
	  <div class="modal-footer">
	    <button id="closeNullLayerModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">OK</button>
	  </div>
	</div>
	
	<div id="onDeleteSure" class="modal fade in" tabindex="-1" role="dialog" aria-hidden="true" style="display:none">
		<div class="modal-header">
			<div id="blueLineBottom">
			    <span class="">Project deletion</span>
			    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		    </div>
		</div>
		<div class="modal-body">
			<p>Are you sure you want to permanently delete this project?</p>
		</div>
		<div class="modal-footer">
		    <button id="closeOnDeleteSureModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">Cancel</button>
		    <button id="OKOnDeleteSureModal" class="btn btn-link btn-large">OK</button>
		  </div>
	</div>
		
	<div id="onDeleteGroupSure" class="modal fade in" tabindex="-1" role="dialog" aria-hidden="true" style="display:none">
		<div class="modal-header">
			<div id="blueLineBottom">
			    <span class="">Group deletion</span>
			    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		    </div>
		</div>
		<div class="modal-body">
			<p>Are you sure you want to permanently delete this group?</p>
		</div>
		<div class="modal-footer">
		    <button id="closeOnDeleteSureModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">Cancel</button>
		    <button id="OKOnDeleteGroupSureModal" class="btn btn-link btn-large">OK</button>
		  </div>
	</div>
	
	<div id="projectTableErrorModal" class="modal fade" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span id="errorHeader">Project related error</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<p id="projectError"></p>
	  </div>
	  <div class="modal-footer">
	    <button id="closeInternalServerModal" class="btn btn-link btn-large" data-dismiss="modal" aria-hidden="true">OK</button>
	  </div>
	</div>
	
	<div id="BBOXModal" class="modal fade wizard" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span class="modalHeader createHeader">Create a new project</span>
		    <span class="modalHeader editHeader">Edit your project</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<div class="modalBodyTitleContainer">
	  		<p class="modalBodyTitle">1. Zoom to enclose the area of interest within the bounding box</p>
	  		<!-- <p class="modalBodyTitleSubheader">Some notes could be placed here</p> -->
	  	</div>
	  	<div id="mapContainer">
	  		<div id="mapBBOX">
	  			
	  		</div>
	  		<div id="bboxCoordsAndDrawButton">
	  			<div id='boxCoordsTitle' class="span3">You have selected the area between:</div>
	  			<button id="drawShape" class="offset6 span3 hidden"><span id='polygon'></span> Or draw custom shape</button>
	  			<button id="clearMap" class="offset7 span2">Clear map</button>
	  			<div class="span11 row-fluid coordLabelContainer">
		  			<div id="coord0" class="coordLabel span5 hidden"></div>
		  			<div id="coord1" class="coordLabel span5 hidden offset2"></div>
	  			</div>
	  			<div class="span11 row-fluid coordLabelContainer">
		  			<div id="coord2" class="coordLabel span5 hidden"></div>
		  			<div id="coord3" class="coordLabel span5 hidden offset2"></div>
	  			</div>
	  		</div>
	  	</div>
	  </div>
	  <div class="modal-footer">
	  	<span id='chooseAreaMessage' class="hidden" style='color : red; margin-right: 1em;;'>
	  		Please, select an area of interest
	  	</span>
	    <button id="goToChooseLayersModalFromBBOXModal" class="btn btn-link btn-large goToNextModal">
	    	Next <i class="fa fa-caret-right" aria-hidden="true"></i>
	    </button>
	    <button id="" class="btn btn-link btn-large cancelBtns" data-dismiss="modal" aria-hidden="true">Cancel</button>
	  </div>
	</div>
	
	<div id="ChooseLayersModal" class="modal fade wizard" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span class="modalHeader createHeader">Create a new project</span>
		    <span class="modalHeader editHeader">Edit your project</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
	  	<div class="modalBodyTitleContainer">
	  		<p class="modalBodyTitle">2. Enter the layers for this project</p>
	  	</div>
	  	
	  	<div id="modalLayerMapContainer">
			<div id="modalLayerMap">
				
			</div>
		
		</div>
	  	
	  	<div id='layerTabsContainer'>
	  		<ul class="nav nav-tabs" id="layerTabs">
			  <li class="active tabListItem">
			  	<a class="tabLink notClicked" href="#availableLayers">Available layers</a>
			  </li>
			  <!-- <li class="tabListItem">
			  	<a class="tabLink" href="#fromURL">From URL</a>
			  </li> -->
			</ul>
			 
			<div class="tab-content">
				<div class="tab-pane active" id="availableLayers">
					<div class="row-fluid">
						<div id="fetchAllAvailableLayers" class="projectManagementButtons span2">
							Show all layers
						</div>
					</div>
					<div id='treeviewLayers'>
						
					</div>
				</div>
			  <div class="tab-pane" id="fromURL">
			  	<div id="layersFromUrlContainer">
			  		<label for='layersFromURLInput' class="span8">
			  			<input type="text" id='layersFromURLInput' name='layersFromURLInput'/> URL
			  		</label>
			  	</div>
			  </div>
			</div>
	  	</div>
	  	
	  </div>
	  <div class="modal-footer">
	  	<span id='chooseLayersMessage' class="hidden" style='color : red; margin-right: 1em;;'>
	  		Please, select layers of your interest
	  	</span>
	  	<button id="goToBBOXModal" class="btn btn-link btn-large">
	    	Back <i class="fa fa-caret-left" aria-hidden="true"></i>
	    </button>
	    <button id="goToRelateUsersToProjectModal" class="btn btn-link btn-large">
	    	Next <i class="fa fa-caret-right" aria-hidden="true"></i>
	    </button>
	    <button id="goToRelateUsersToProjectModalSkipButton" class="btn btn-link btn-large">
	    	Skip
	    </button>
	    <button id="" class="btn btn-link btn-large cancelBtns" data-dismiss="modal" aria-hidden="true">Cancel</button>
	  </div>
	</div>
	
	<div id="RelateUsersGroupsToProjectModal" class="modal fade wizard" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span class="modalHeader createHeader">Create a new project</span>
		    <span class="modalHeader editHeader">Edit your project</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
		<div class="modalBodyTitleContainer">
			<p class="modalBodyTitle">4. Choose who can see your project</p>
	  	</div>
	  	<div class="tableTagsContainer">
		  	<div class="tableContainer">
		  		<table id="relateUsersGroupsToProjectsTable" class="no-wrap " style="width: 100%;">
					<tbody>
					</tbody>
				</table>
		  	</div>
		  	<div class="tagsContainer">
		  	</div>
	  	</div>
	  	<!-- <div class="selectedUsersSection">
	  		<div class="selectedUsersHeader">
	  			Selected Groups (<span id="numOfSelectedUsersGroups"></span>)
	  		</div>
	  		<div class="selectdUsersGroupsTagSection hidden">
	  			<input type="text" value="" hidden="true" class="hidden usersGroupsTagsinput"/>
	  		</div>
	  	</div> -->
	  </div>
	  <div class="modal-footer">
	  	<!-- <button id="goToChooseLayersModalFromRelateUsersToProjectModal" class="btn btn-link btn-large"> -->
	  	<button id="goToAssignGroupOrIndividualModalFromRelateUsersGroupsToProjectModal" class="btn btn-link btn-large">
	    	Back <i class="fa fa-caret-left" aria-hidden="true"></i>
	    </button>
	    <button id="goToProjectNameAndDescriptionModalFromGroups" class="btn btn-link btn-large">
	    	Next <i class="fa fa-caret-right" aria-hidden="true"></i>
	    </button>
	    <button id="" class="btn btn-link btn-large cancelBtns" data-dismiss="modal" aria-hidden="true">Cancel</button>
	  </div>
	</div>
	
	<div id="RelateUsersToProjectModal" class="modal fade wizard" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span class="modalHeader createHeader">Create a new project</span>
		    <span class="modalHeader editHeader">Edit your project</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
		<div class="modalBodyTitleContainer">
			<p class="modalBodyTitle">4. Choose who can see your project</p>
	  	</div>
	  	<div class="tableTagsContainer">
		  	<div class="tableContainer">
		  		<table id="relateUsersToProjectsTable" class="no-wrap " style="width: 100%;">
					<tbody>
					</tbody>
				</table>
		  	</div>
		  	<div class="tagsContainer">
		  	</div>
	  	</div>
	  	<!-- <div class="selectedUsersSection">
	  		<div class="selectedUsersHeader">
	  			Selected Users (<span id="numOfSelectedUsers"></span>)
	  		</div>
	  		<div class="selectdUsersTagSection hidden">
	  			<input type="text" value="" hidden="true" class="hidden usersTagsinput"/>
	  		</div>
	  	</div> -->
	  </div>
	  <div class="modal-footer">
	  	<!-- <button id="goToChooseLayersModalFromRelateUsersToProjectModal" class="btn btn-link btn-large"> -->
	  	<button id="goToAssignGroupOrIndividualModalFromRelateUsersToProjectModal" class="btn btn-link btn-large">
	    	Back <i class="fa fa-caret-left" aria-hidden="true"></i>
	    </button>
	    <button id="goToProjectNameAndDescriptionModalFromUsers" class="btn btn-link btn-large">
	    	Next <i class="fa fa-caret-right" aria-hidden="true"></i>
	    </button>
	    <button id="" class="btn btn-link btn-large cancelBtns" data-dismiss="modal" aria-hidden="true">Cancel</button>
	  </div>
	</div>
	
	<div id="AssignUsersToProjectGroupsModal" class="modal fade wizard" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <!-- <span class="modalHeader createHeader">Create a new project</span>
		    <span class="modalHeader editHeader">Edit your project</span> -->
		    <span class="modalHeader editGroup">Edit group</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
		<div class="modalBodyTitleContainer">
			<p class="modalBodyTitle">3. Choose who can see your project</p>
	  	</div>
	  	<div class="tableTagsContainer">
		  	<div class="tableContainer">
		  		<table id="assignUsersToProjectGroupssTable" class="no-wrap " style="width: 100%;">
					<tbody>
					</tbody>
				</table>
		  	</div>
		  	<div class="tagsContainer">
		  	</div>
	  	</div>
	  	<div class="selectedUsersSection">
	  		<div class="selectedUsersHeader">
	  			Assigned Users (<span id="numOfAssignedUsers"></span>)
	  		</div>
	  		<div class="assignedUsersTagSection hidden">
	  			<input type="text" value="" hidden="true" class="hidden assignedUsersTagsinput"/>
	  		</div>
	  	</div>
	  </div>
	  <div class="modal-footer">
	  	<!-- <button id="goToChooseLayersModalFromRelateUsersToProjectModal" class="btn btn-link btn-large"> -->
	  	<button id="goToGroupsManipulationModalFromAssignUsersToProjectGroupsModal" class="btn btn-link btn-large">
	    	Back <i class="fa fa-caret-left" aria-hidden="true"></i>
	    </button>
	    <button id="assignUsersToProjectGroupsModalOKButton" class="btn btn-link btn-large">
	    	OK
	    </button>
	    <button id="" class="btn btn-link btn-large cancelBtns" data-dismiss="modal" aria-hidden="true">Cancel</button>
	  </div>
	</div>
	
	<div id="assignGroupOrIndividualModal" class="modal fade wizard" hidden="true" tabindex="-1" role="dialog">
		<div class="modal-header">
			<div id="blueLineBottom">
				<span class="modalHeader createHeader">Create a new project</span>
		    	<span class="modalHeader editHeader">Edit your project</span>
			    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		    </div>
		</div>
		<div class="modal-body">
			<div class="modalBodyTitleContainer">
				<p class="modalBodyTitle">3. Assign group or individual to project</p>
		  	</div>
	  		<div class="row-fluid">
	  			<div class="groupOrIndividual span12 showIndividualsModal">Individuals</div>
	  		</div>
	  		<div class="row-fluid">
	  			<div class="groupOrIndividual span12 showGroupsModal">Groups</div>
	  		</div>
		</div>
		<div class="modal-footer">
			<button id="assignGroupOrIndividualModalBackBtn" class="btn btn-link btn-large">
				Back <i class="fa fa-caret-left" aria-hidden="true"></i>
			</button>
			<button id="assignGroupOrIndividualModalNextBtn" class="btn btn-link btn-large">
				Next <i class="fa fa-caret-right" aria-hidden="true"></i>
			</button>
			<button id="" class="btn btn-link btn-large cancelBtns" data-dismiss="modal" aria-hidden="true">Cancel</button>
		</div>
	</div>
	
	<div id="projectNameAndDescriptionModal" class="modal fade wizard" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span class="modalHeader createHeader">Create a new project</span>
		    <span class="modalHeader editHeader">Edit your project</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
	    </div>
	  </div>
	  <div class="modal-body">
		<div class="modalBodyTitleContainer">
			<p class="modalBodyTitle">5. Choose a name and a description for your project</p>
			<p class="modalBodyTitleSubheader">Some notes could be placed here</p>
			<p id="projectNameDescriptionValidation" class="hidden" style="color:red;">Project name cannot be empty</p>
			<p id="projectAlreadyExists" class="hidden" style="color:red;"></p>
	  	</div>
	  	<div class="projectAttributes">
	  		<div class="row-fluid">
	  			<div class="span3">
	  				<label for="projectName">Name:</label>
	  			</div>
	  			<div class="span9">
	  				<input type="text" id="projectName" class="span12" placeholder="Name of project">
	  			</div> 
	  		</div>
	  		<div class="row-fluid">
	  			<div class="span3">
	  				<label for="projectDescription">Description:</label>
	  			</div>
	  			<div class="span9">
	  				<textarea id="projectDescription" class="span12" rows="4" placeholder="Describe the project here"></textarea>
	  			</div>
	  		</div>
	  	</div>
	  </div>
	  <div class="modal-footer">
	  	<button id="goBackToRelateUsersToProjectModal" class="btn btn-link btn-large">
	    	Back <i class="fa fa-caret-left" aria-hidden="true"></i>
	    </button>
	    <button id="CreateProjectButton" class="btn btn-link btn-large">OK</button>
	    <button id="" class="btn btn-link btn-large cancelBtns" data-dismiss="modal" aria-hidden="true">Cancel</button>
	  </div>
	</div>

	<div id="authorizationMessageModal" class="modal fade wizard" hidden="true" tabindex="-1" role="dialog">
	  <div class="modal-header">
		<div id="blueLineBottom">
		    <span class="modalHeader">Access denied</span>
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
</div>
</div>
	  <div class="modal-body">
		<p id="messageRights"></p>
	  </div>
	  <div class="modal-footer">
	    <button id="" class="btn btn-link btn-large cancelBtns" data-dismiss="modal" aria-hidden="true">OK</button>
	  </div>
	</div>

</div>
</div>
<!-- ProjectManagement URLs -->
<portlet:resourceURL var="theResourceURL" />
<portlet:resourceURL id="projects/authorizedToRead" var="authorizedToReadProjectURL"/>
<portlet:resourceURL id="projects/authorizedToEdit" var="authorizedToEditProjectURL"/>
<portlet:resourceURL id="projects/authorizedToDelete" var="authorizedToDeleteProjectURL"/>
<portlet:resourceURL id="projects/brief" var="projectsSummaryURL"/>
<portlet:resourceURL id="projects/create" var="projectCreateURL"/>
<portlet:resourceURL id="projects/updateProject" var="projectUpdateURL"/>
<portlet:resourceURL id="projects/retrieveAllUsersInDB" var="retrieveAllUsers"/>
<portlet:resourceURL id="projects/delete" var="deleteProjectURL"/>
<portlet:resourceURL id="projects/viewDetails" var="viewDetailsProjectURL"/>
<portlet:resourceURL id="projects/bbox" var="bboxURL"/>
<portlet:resourceURL id="projects/participants" var="participantsURL"/>
<portlet:resourceURL id="projects/groupmembers" var="groupMembersURL"/>
<portlet:resourceURL id="projects/retrieveGroups" var="retrieveGroups"/>
<portlet:resourceURL id="projects/retrieveGroupsAndNumOfUsers" var="retrieveGroupsAndNumOfUsersURL"/>
<portlet:resourceURL id="projects/newProjectGroup" var="newProjectGroupURL"/>
<portlet:resourceURL id="projects/deleteProjectGroup" var="deleteProjectGroupURL"/>
<portlet:resourceURL id="projects/assignUsersToProjectGroup" var="assignUsersToProjectGroupURL"/>
<portlet:resourceURL id="shapes/listLayersOfTypeOrderedByTaxonomy" var="listLayersOfType"/>
<portlet:resourceURL id="shapes/listLayersOfType" var="listOfAllLayers"/>
<portlet:resourceURL id="shapes/listOfAllAvailableLayers" var="listOfAllAvailableLayers"/>
<portlet:resourceURL id="shapes/listLayersByProject" var="listLayersByProject"/>
<!-- DSS Urls -->
<portlet:resourceURL id="shapes/listLayersOfTypeOrderedByTaxonomyID" var="listLayersByTaxonomyID"/>
<portlet:resourceURL id="shapes/mostSpecificBreadcrumbsByCoordinates" var="mostSpecificBreadcrumbsByCoordinates"/>
<portlet:resourceURL id="shapes/breadcrumbsByCoordinates" var="breadcrumbsByCoordinates"/>
<portlet:resourceURL id="shapes/mostSpecificBreadcrumbsByCoordinates" var="breadcrumbsByCoordinatesMostSpecific"/>
<portlet:resourceURL id="wms" var="wms"/>
<portlet:resourceURL id="importTsv" var="importTsv"/>
<portlet:resourceURL id="/shapes/calculateSample" var="calculateSample"/>
<portlet:resourceURL id="/projects/bbox" var="getProjectBBOX"/>
<portlet:resourceURL id="shapes/geoServerBridgeWorkspace" var="geoserverWorkspaceURL"/>
<portlet:resourceURL id="plugin/listProjectPlugins" var="listProjectPluginsUrl"/>
<portlet:resourceURL id="plugin/loadPluginByNameAndTenant" var="loadPluginByNameAndTenantURL"/>
<portlet:resourceURL id="plugin/executeFunction" var="executeFunctionURL"/>
<portlet:resourceURL id="plugin/upload" var="pluginUploadURL"/>
<portlet:resourceURL id="plugin/fetchPluginsByPluginLibraryId" var="fetchPluginsOfLibraryURL"/>
<portlet:resourceURL id="plugin/fetchConfiguration" var="fetchPluginConfigurationClassURL"/>

<!-- scripts -->
<script type="text/javascript">
if (typeof jQuery == "undefined") {
	document.write('<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"><\/script>');
}
</script>
<script src="<c:url value="/js/bootstrap.js?01" />"></script>
<script src="<c:url value="/js/Utils/CreateLink.js?01" />"></script>

<!-- jquery datatables -->
<script src="<c:url value="/js/jquery_datatables/jquery.dataTables.js?01" />"></script>
<script src="<c:url value="/js/jquery_datatables/dataTables.responsive.min.js?01" />"></script>

<!-- jstree -->
<script src="<c:url value="/js/jstree/jstree.js?01" />"	type="text/javascript"></script>

<!-- openlayers -->
<link rel="stylesheet" href="<c:url value="/css/openlayers3/ol.css?01" />" type="text/css">
<script src="<c:url value="/js/openlayers3/ol-debug.js?01" />" type="text/javascript"></script>

<script src="<c:url value="/js/InitializeProjectSelectionTable/RetrieveLayersForProject.js?01" />"></script>

<!-- utils -->
<script src='<c:url value="/js/Utils/AjaxCalls.js?01" />'></script>
<script src='<c:url value="/js/Utils/Spinner.js?01" />'></script>
<script src="<c:url value="/js/InitializeProjectSelectionTable/EditMode.js?01" />"></script>

<!-- tables initialization -->
<script src="<c:url value="/js/InitializeProjectSelectionTable/InitializeProjectSelectionTable.js?01" />"></script>
<script src="<c:url value="/js/InitializeProjectSelectionTable/CustomSearch.js?01" />"></script>
<script src="<c:url value="/js/InitializeProjectSelectionTable/Sorting.js?01" />"></script>
<script src="<c:url value="/js/InitializeProjectSelectionTable/ToolbarEvents.js?01" />"></script>
<script src="<c:url value="/js/Utils/Utils.js?01" />"></script>

<!-- modal events -->
<script src="<c:url value="/js/InitializeProjectSelectionTable/ModalEvents.js?01" />"></script>
<script src="<c:url value="/js/InitializeProjectSelectionTable/SelectLayersTabs.js?01" />"></script>

<!-- cutom map controls -->
<script src='<c:url value="/js/Controls/CustomControls.js?01" />'></script>

<!-- Tags input -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/bootstrap.tagsinput/0.4.2/bootstrap-tagsinput.css?01" />
<script src="https://cdn.jsdelivr.net/bootstrap.tagsinput/0.4.2/bootstrap-tagsinput.min.js?01"></script>

<script type="text/javascript">
//Project tab
var renderURL = '<portlet:renderURL><portlet:param name="jspPage" value="{url}.jsp" /><portlet:param name="getParams" value="{params}" /></portlet:renderURL>';
var theResourceURL = '<%=theResourceURL%>';
var resourceURL = '<portlet:resourceURL id="{url}?{params}" />';
var projectNameWhenNotAvailable = $('#nameOfProject').text();
var projectDateWhenNotAvailable = $('#dateOfProject').text();
var nameSpace = $('#portletInfo').data('namespace');
var LISTLAYERSFLAG = true;
var EDITMODE_USER_PRESSED_CANCEL = false;
var EDITMODE = false;
var $table = $('#ProjectSelectionTable');
var $relateUsersToProjectsTable;
var $relateUsersToProjectsTable2;
var $relateUsersGroupsToProjectsTableD;
var $relateUsersGroupsToProjectsTabled;
var useridOfItemToBeRemoved;
var initialState;
var numOfCols = 4;//$table.find('tr:first td').length;
var mapNumbersInWordFormWihtOthers = [];
mapNumbersInWordFormWihtOthers[0] = 'zero';
mapNumbersInWordFormWihtOthers[1] = 'one';
mapNumbersInWordFormWihtOthers[2] = 'two';
mapNumbersInWordFormWihtOthers[3]= 'three';
mapNumbersInWordFormWihtOthers[4] = 'four';
mapNumbersInWordFormWihtOthers[5] = 'helperColumn';
var projectsSummaryURL = '<%= projectsSummaryURL %>';
var listLayersOfType = '<%= listLayersOfType %>';
var listOfAllLayers = '<%=listOfAllLayers %>';
var listOfAllAvailableLayersURL = '<%=listOfAllAvailableLayers %>';
var retrieveAllUsers = '<%=retrieveAllUsers%>';
var projectCreateURL = '<%=projectCreateURL%>';
var projectUpdateURL = '<%=projectUpdateURL%>';
var deleteProjectURL = '<%=deleteProjectURL%>';
var viewDetailsProjectURL = '<%=viewDetailsProjectURL%>';
var bboxURL = '<%=bboxURL%>';
var listLayersByProjectUrl = '<%=listLayersByProject%>';
var participantsURL = '<%=participantsURL%>';
var groupMembersURL = '<%=groupMembersURL%>';
var retrieveGroupsURL = '<%=retrieveGroups%>';
var retrieveGroupsAndNumOfUsersURL = '<%=retrieveGroupsAndNumOfUsersURL%>';
var newProjectGroupURL = '<%=newProjectGroupURL%>';
var deleteProjectGroupURL = '<%=deleteProjectGroupURL%>';
var assignUsersToProjectGroupURL = '<%=assignUsersToProjectGroupURL%>';
var authorizedToReadProjectURL = '<%=authorizedToReadProjectURL%>';
var authorizedToEditProjectURL = '<%=authorizedToEditProjectURL%>';
var authorizedToDeleteProjectURL = '<%=authorizedToDeleteProjectURL%>';
var listProjectPluginsUrl = '<%=listProjectPluginsUrl%>';
var loadPluginByNameAndTenantURL = '<%=loadPluginByNameAndTenantURL%>';
var executeFunctionURL = '<%=executeFunctionURL%>';
var pluginUploadURL = '<%=pluginUploadURL%>';
var fetchPluginsOfLibraryURL = '<%=fetchPluginsOfLibraryURL%>';
var fetchPluginConfigurationClassURL = '<%=fetchPluginConfigurationClassURL%>';
var projectDataForTheTable = [];
var tileElementsGlobal = [];
var SORT_ASC = 1;
var SORT_DESC = 2;
var searchVal = '';
var mapBBOX;
var updateMapInModalTheFirstTime = false;
var draw;
var source;
var coords = {};
var coordsObjectToBeSendToDSS = {};
var app = {};
var activeControls = {};
var layersObject = {};
var layersObjectModal = {};
var layersIntersectingWithCurrentBBOX = [];
var allLayers = [];
var userinfoObject = {};
layersObject.jstreeLayers = [];
layersObject.skipped = false;
var jstreeIsLoaded = false;
var globalDataObjectForDSSApp = {};
var usersArray = [];
var usersAndRightsArray = [];
var projectNameAndDescriptionObject = {};
var layersByName = {};
var layersByNameModal = {};
var layerNamesObject = [];
var layerNamesObjectModal = [];
var layersMap = null;
var currentPage;
var setPage = false;
var deleteProjectCallback;
var viewDetailsProjectCallback;
var justLayerNames;
var editeModeCoordinates = [];
var editeModeCoordinatesLayersModal = [];
var initTableOnFirstTime = true;
var relateUsersToProjectsTableInitialized = false;
var initializeGroupNamesTableForTheFirstTime = false;
var fromUsersAssignment = false;
var fromUsersGroupsAssignment = false;
var fromPickUsersOrUsersGroup = false;
var goBackwardsToBBOXModal = false;
var fetchAllAvailableLayers = false;
var usersToRowsMap = {};
var usersGroupsToRowsMap = {};
var assignedUsersToProjectGroupToRowsMap = {};
var projectGroupNameAssignUsersToProjectGroupsModal;
var initialMapExtent = null;

//DSS tab
var mapLayersLoaded = false;
var firstTimeOpeningMapExportModal = true;
var coordsDSSTab;
		var popupSticksOutOfMapArea = false;
		var mousePositionControl = null;
		var map = null;
		var overlay = null;
		//var layersByName = {};
		var layer1 = null;
		var wmsLayer = null;
		var vectorSource = null;
		var iconStyle = null;
		var vectorLayer = null;
		var lNames = [];
		var layerNamesOnTheLeft = [];
		var layerNamesOnTheLeftModal = []; 
		var mapInsdideViewMoreModal = null;
		var listLayersByTaxonomyID = '<%=listLayersByTaxonomyID %>';
		var mostSpecificBreadcrumbsByCoordinates = '<%=mostSpecificBreadcrumbsByCoordinates %>';
		var breadcrumbsByCoordinates = '<%=breadcrumbsByCoordinates %>';
		var breadcrumbsByCoordinatesMostSpecific = '<%=breadcrumbsByCoordinatesMostSpecific %>';
		var importTsv = '<%= importTsv %>';
		var wms = '<%= wms %>';
		var getProjectBBOXURL= '<%= getProjectBBOX %>';
		var geoserverWorkspaceURL = '<%=geoserverWorkspaceURL%>'
		var clickRadioButtonFirstTime = true;
		var iconSource = '<c:url value="/img/location_marker_pin-512.png" />';
		var destroySelectorWrapper = true;
		var dockeGISControlButtonsPressed = {};
		var pluginObjects = [];
//		pluginObjects.push('Function1');
		var iconFeatureArray = [];
		var RIGHTSIDE_ACCORDION = 0;
		var RESPONSIVE_TOOLBAR_ACCORDION = 1;
		var functionsSliderDivWidth = 1;
		var projectName;
		var extent;
		var closeButtonClicked=false;
		var requestingPopoverInfo = false;
		var featureInfoLayers = [];
		var loadProjectObject = {};
		var extentForCenteringDSSMap;
		var geoserverWorkspaceName;
		var pluginIdOfLatestLoadedPlugin = null;


$(document).ready(function() {
//	ProjectManagement
	setUserInfoObject();
	CustomSearch();
	modalEvents();
	selectLayersTabs();

//	DSS
	 leftSidePanelHandlersAndEvents();
	 rightSidePanelHandlersAndEvents();
	 searchBar();
	 mapInit();
	 mapExportEvents();
	 showMoreLessText();
	 exportMap();
//	 buildAccordion(RIGHTSIDE_ACCORDION);
	 responsiveMode();
	 uploadPlugin();
});
</script>