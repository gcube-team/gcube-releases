function buildAccordion(number, pluginObjects) {
	if(number === RIGHTSIDE_ACCORDION){
		buildAccordionForRightSidePanel(pluginObjects);
	}else if(number === RESPONSIVE_TOOLBAR_ACCORDION){
		buildAccordionForResponsiveToolbar();
	}
}

function buildAccordionForResponsiveToolbar(){
	populateAccordionWithHTMLEelemmentsBeforeInitializing();
	populateAccordionWithHTMLEelemmentsBeforeInitializing2();
	showMoreLessText();
	
	$('.levelOfDetailSlider').slider({
		range: 'min',
		value: 100,
		min: 0,
		max: 100,
		step: 1,
		slide: function(event, ui){
			$('.levelOfDetailValue').text(ui.value);
		}
	});
	$('.levelOfDetailValue').text(100);
	$( ".functionsAccordion .modelChooseALayer" ).selectmenu();
	$('#slider').slider();
	$('.functionsAccordion').accordion({
		active : 0,
		collapsible: true,
		header : 'h4',
		icons: false,
		heightStyle: "fill"
	});
}

function buildAccordionForRightSidePanel(pluginObjects){
	populateAccordionWithHTMLEelemmentsBeforeInitializing(pluginObjects);
	showMoreLessText();
	rightSidePanelHandlersAndEvents();
	
	$('.levelOfDetailSlider').slider({
		range: 'min',
		value: 100,
		min: 0,
		max: 100,
		step: 1,
		slide: function(event, ui){
			$('.levelOfDetailValue').text(ui.value);
		}
	});
	$('.levelOfDetailValue').text(100);
	$( ".functionsAccordion .modelChooseALayer" ).selectmenu({
		select : function(event, ui){
			var principalName = userinfoObject.fullname;
			var tenantName = userinfoObject.tenant;
			var pluginName = ui.item.label;
			var projectName = userinfoObject.projectName;
			var pluginId = ui.item.value;
			
			pluginIdOfLatestLoadedPlugin = pluginId;
			
			var parameters = "principalName=" + encodeURIComponent(principalName)
							+ "&tenantName=" + encodeURIComponent(tenantName)
							+ "&pluginName=" + encodeURIComponent(pluginName)
							+ "&projectName=" + encodeURIComponent(projectName)
							+ "&pluginId=" + encodeURIComponent(pluginId);
			
			var callback = function(data){
				hideSpinner();
			};
			
			var urlMVC = encodeURIComponent("plugin/loadPluginByNameAndTenant");
			var url = createLink(resourceURL, urlMVC, parameters);
			var context = null;
			
			showSpinner();
			appendFunctionContainerToDom(pluginName);
			
			$.getScript(url)
				.done(function( script, textStatus ) {
					hideSpinner();
					removeNewPluginHelperClasses();
				})
				.fail(function( jqxhr, settings, exception ) {
					$('.wizard').modal('hide');
					$('#InternalServerErrorModal').modal('show');
					destroyNewPluginContainer();
					hideSpinner();
				});
		}
	});
	$('#slider').slider();
	$('.functionsAccordion').accordion({
		active : 0,
		collapsible: true,
		header : 'h4',
		icons: false,
		heightStyle: "fill"
	});
}

function populateAccordionWithHTMLEelemmentsBeforeInitializing(pluginObjects) {
	$.each(pluginObjects, function(index, value){
		var header = $('<h4></h4>', {
			class: 'functionsAccordionTitle',
			text: value.pluginName
		});
		
		var contentPanel = $('<div></div>', {
			class : 'functionsAccordionContentPanel'
		});
		
		var descriptionElement = $('<div></div>', {
			class : 'more functionDescription',
			text: value.pluginDescription + ' This is a test function.'//got to find a way to pass the description to the element
		});
		
		var form = $('<form></form>', {
			class: 'formFunction'
		});
		var levelOfDetailContainer = $('<div></div>', {
			class: 'row-fluid levelOfDetailContainer'
		});
		var levelOfDetailHeadingContainer = $('<div></div>', {
			class: 'levelOfDetailHeadingContainer span12'
		});
		var levelOfDetailHeading = $('<h5></h5>', {
			class : 'levelOfDetailHeading',
			text : 'LEVEL OF DETAIL'
		});
		var levelOfDetailSliderElement = $('<div></div>', {
			class: 'levelOfDetailSliderElement row-fluid span9'
		});
		var levelOfDetailSlider = $('<div></div>',{
			class : 'levelOfDetailSlider'
		});
		var levelOfDetailSubHeading = $('<div></div>',{
			class : 'levelOfDetailSubHeading span2'
		});
		var levelOfDetailSubHeadingText = $('<span></span>', {
			class : 'levelOfDetailSubHeadingText'
		});
		var levelOfDetailValue = $('<span></span>', {
			class : 'levelOfDetailValue',
			text : 100
		});
		var chooseSomeAttributeContainer = $('<div></div>', {
			class: 'row-fluid chooseSomeAttributeContainer',
			id: value.pluginId
		});
		
//		Reference to the plugin container element
		value.pluginContainerElement = chooseSomeAttributeContainer;
		value.isPluginLoaded = false;
		var chooseSomeAttributeTitleAndSubtextContainer = $('<div></div>',{
			class : 'chooseSomeAttributeTitleAndSubtextContainer'
		});
		var chooseSomeAttributeTitle = $('<h5></h5>', {
			class : 'chooseSomeAttributeTitle',
			text : 'Choose Plugins'
		});
		var functionAtrributesDropdownContainer = $('<div></div>',{
			class : 'functionAtrributesDropdownContainer selector-wrapper input-large chooseLayer1'
		});
		var modelChooseALayer = $('<select></select>',{
			class: 'modelChooseALayer span12'
		});
		var option = $('<option></option>', {
			text : 'Choose a Layer',
			hidden: 'true',
			disabled:'',
			selected: ''
		});
		var exportButtonContainer = $('<div></div>', {
			class : 'exportButtonContainer'
		});
		var btngrp = $('<div></div>', {
			class : 'btn-group row-fluid'
		});
		var exportBtn = $('<button></button>', {
			class : 'exportButton span6',
			text : 'Export As '
		});
		exportBtn.prop('disabled',true);
		var caret = $('<i></i>',{
			class : 'fa fa-caret-down'
		});
		var btnsbmt = $('<button></button>',{
			class: 'runFunctionButton span6',
			type : 'submit',
			text: 'Run function'
		});
		btnsbmt.prop('disabled',true);
		value.pluginFunctionRunButton = btnsbmt;
		
		pluginObjects[index]=value;
		
		btnsbmt.click(function(event){
			event.preventDefault();
			setRunFunctionButtonClickHandler(btnsbmt, pluginObjects[index]);
			$(this).click();
		});
		
		exportBtn.append(caret);
		btngrp.append(exportBtn).append(btnsbmt);
		exportButtonContainer.append(btngrp);
		modelChooseALayer.append(option);
		functionAtrributesDropdownContainer.append(modelChooseALayer);
		chooseSomeAttributeTitleAndSubtextContainer.append(chooseSomeAttributeTitle).append(functionAtrributesDropdownContainer);
//		chooseSomeAttributeContainer.append(chooseSomeAttributeTitleAndSubtextContainer);
		levelOfDetailSubHeadingText.append(levelOfDetailValue);
		levelOfDetailSubHeading.append(levelOfDetailSubHeadingText);
		levelOfDetailSubHeadingText.html(levelOfDetailSubHeadingText.html() + '%');
		levelOfDetailSliderElement.append(levelOfDetailSlider);
		levelOfDetailHeadingContainer.append(levelOfDetailHeading);
//		levelOfDetailContainer
//		.append(levelOfDetailHeadingContainer)
//		.append(levelOfDetailSliderElement)
//		.append(levelOfDetailSubHeading);
		form.append(levelOfDetailContainer).append(chooseSomeAttributeContainer).append(exportButtonContainer);
		
		contentPanel.append(descriptionElement).append(form);
		$('.functionsAccordion').append(header).append(contentPanel);

//		$('.functionsAccordion.new').append(header).append(contentPanel);
//		$('.functionsAccordion.new').removeClass('new');
		
//		var $nextContainer = $('<div></div>', {
//			class : 'functionsAccordion new'
//		});
//		
//		$nextContainer.insertAfter('.functionsAccordion:last');
	});
}

function populateAccordionWithHTMLEelemmentsBeforeInitializing2(){
	$.each(pluginObjects, function(index, value){
		var header = $('<h4></h4>', {
			class: 'functionsAccordionTitle ' + value,
			text: value
		});
		var contentPanel = $('<div></div>', {
			class : 'functionsAccordionContentPanel'
		});
		var descriptionElement = $('<div></div>', {
			class : 'more functionDescription',
			text: value + ' This is a test function.'//got to find a way to pass the description to the element
		});
		
		var form = $('<form></form>', {
			class: 'formFunction'
		});
		var levelOfDetailContainer = $('<div></div>', {
			class: 'row-fluid levelOfDetailContainer'
		});
		var levelOfDetailHeadingContainer = $('<div></div>', {
			class: 'levelOfDetailHeadingContainer span12'
		});
		var levelOfDetailHeading = $('<h5></h5>', {
			class : 'levelOfDetailHeading',
			text : 'LEVEL OF DETAIL'
		});
		var levelOfDetailSliderElement = $('<div></div>', {
			class: 'levelOfDetailSliderElement row-fluid span9'
		});
		var levelOfDetailSlider = $('<div></div>',{
			class : 'levelOfDetailSlider'
		});
		var levelOfDetailSubHeading = $('<div></div>',{
			class : 'levelOfDetailSubHeading span2'
		});
		var levelOfDetailSubHeadingText = $('<span></span>', {
			class : 'levelOfDetailSubHeadingText'
		});
		var levelOfDetailValue = $('<span></span>', {
			class : 'levelOfDetailValue',
			text : 100
		});
		var chooseSomeAttributeContainer = $('<div></div>', {
			class: 'row-fluid chooseSomeAttributeContainer'
		});
		var chooseSomeAttributeTitleAndSubtextContainer = $('<div></div>',{
			class : 'chooseSomeAttributeTitleAndSubtextContainer'
		});
		var chooseSomeAttributeTitle = $('<h5></h5>', {
			class : 'chooseSomeAttributeTitle',
			text : 'Choose Layers'
		});
		var functionAtrributesDropdownContainer = $('<div></div>',{
			class : 'functionAtrributesDropdownContainer selector-wrapper input-large chooseLayer1'
		});
		var modelChooseALayer = $('<select></select>',{
			class: 'modelChooseALayer span12'
		});
		var option = $('<option></option>', {
			text : 'Choose a Layer',
			hidden: 'true',
			disabled:'',
			selected: ''
		});
		var exportButtonContainer = $('<div></div>', {
			class : 'exportButtonContainer'
		});
		var btngrp = $('<div></div>', {
			class : 'btn-group row-fluid'
		});
		var exportBtn = $('<button></button>', {
			class : 'exportButton span6',
			text : 'Export As '
		});
		exportBtn.prop('disabled',true);
		var caret = $('<i></i>',{
			class : 'fa fa-caret-down'
		});
		var btnsbmt = $('<button></button>',{
			class: 'runFunctionButton span6',
			type : 'submit',
			text: 'Run function'
		});
		btnsbmt.prop('disabled',true);
		exportBtn.append(caret);
		btngrp.append(exportBtn).append(btnsbmt);
		exportButtonContainer.append(btngrp);
		modelChooseALayer.append(option);
		functionAtrributesDropdownContainer.append(modelChooseALayer);
		chooseSomeAttributeTitleAndSubtextContainer.append(chooseSomeAttributeTitle).append(functionAtrributesDropdownContainer);
		chooseSomeAttributeContainer.append(chooseSomeAttributeTitleAndSubtextContainer);
		levelOfDetailSubHeadingText.append(levelOfDetailValue);
		levelOfDetailSubHeading.append(levelOfDetailSubHeadingText);
		levelOfDetailSubHeadingText.html(levelOfDetailSubHeadingText.html() + '%');
		levelOfDetailSliderElement.append(levelOfDetailSlider);
		levelOfDetailHeadingContainer.append(levelOfDetailHeading);
		levelOfDetailContainer
		.append(levelOfDetailHeadingContainer)
		.append(levelOfDetailSliderElement)
		.append(levelOfDetailSubHeading);
		form.append(levelOfDetailContainer).append(chooseSomeAttributeContainer).append(exportButtonContainer);
		
		contentPanel.append(descriptionElement).append(form);
		var $div = $('<div></div>',{
			id:'auxRespDiv'
		});
		$div.append(header).append(contentPanel);
		$('#functionsResponsiveSlider').append($div);
	});
}

function appendFunctionContainerToDom(pluginName) {
		var header = $('<h4></h4>', {
//			class: 'functionsAccordionTitle ' + value,
			class: 'functionsAccordionTitle',
			text: pluginName
		});
		var contentPanel = $('<div></div>', {
			class : 'functionsAccordionContentPanel newPlugin'
		});
		var descriptionElement = $('<div></div>', {
			class : 'more functionDescription',
			text: pluginName + ' This is a test function.'//got to find a way to pass the description to the element
		});
		
		var form = $('<form></form>', {
			class: 'formFunction'
		});
		var levelOfDetailContainer = $('<div></div>', {
			class: 'row-fluid levelOfDetailContainer'
		});
		var levelOfDetailHeadingContainer = $('<div></div>', {
			class: 'levelOfDetailHeadingContainer span12'
		});
		var levelOfDetailHeading = $('<h5></h5>', {
			class : 'levelOfDetailHeading',
			text : 'LEVEL OF DETAIL'
		});
		var levelOfDetailSliderElement = $('<div></div>', {
			class: 'levelOfDetailSliderElement row-fluid span9'
		});
		var levelOfDetailSlider = $('<div></div>',{
			class : 'levelOfDetailSlider'
		});
		var levelOfDetailSubHeading = $('<div></div>',{
			class : 'levelOfDetailSubHeading span2'
		});
		var levelOfDetailSubHeadingText = $('<span></span>', {
			class : 'levelOfDetailSubHeadingText'
		});
		var levelOfDetailValue = $('<span></span>', {
			class : 'levelOfDetailValue',
			text : 100
		});
		var chooseSomeAttributeContainer = $('<div></div>', {
			class: 'row-fluid chooseSomeAttributeContainer'
		});
		var chooseSomeAttributeTitleAndSubtextContainer = $('<div></div>',{
			class : 'chooseSomeAttributeTitleAndSubtextContainer'
		});
		var chooseSomeAttributeTitle = $('<h5></h5>', {
			class : 'chooseSomeAttributeTitle',
			text : 'Choose Plugins'
		});
		var functionAtrributesDropdownContainer = $('<div></div>',{
			class : 'functionAtrributesDropdownContainer selector-wrapper input-large chooseLayer1'
		});
		var modelChooseALayer = $('<select></select>',{
			class: 'modelChooseALayer span12'
		});
		var option = $('<option></option>', {
			text : 'Choose a Layer',
			hidden: 'true',
			disabled:'',
			selected: ''
		});
		var exportButtonContainer = $('<div></div>', {
			class : 'exportButtonContainer'
		});
		var btngrp = $('<div></div>', {
			class : 'btn-group row-fluid'
		});
		var exportBtn = $('<button></button>', {
			class : 'exportButton span6',
			text : 'Export As '
		});
		exportBtn.prop('disabled',true);
		var caret = $('<i></i>',{
			class : 'fa fa-caret-down'
		});
		var btnsbmt = $('<button></button>',{
			class: 'runFunctionButton span6',
			type : 'submit',
			text: 'Run function'
		});
		btnsbmt.prop('disabled',true);
		exportBtn.append(caret);
		btngrp.append(exportBtn).append(btnsbmt);
		exportButtonContainer.append(btngrp);
		modelChooseALayer.append(option);
		functionAtrributesDropdownContainer.append(modelChooseALayer);
		chooseSomeAttributeTitleAndSubtextContainer.append(chooseSomeAttributeTitle).append(functionAtrributesDropdownContainer);
		chooseSomeAttributeContainer.append(chooseSomeAttributeTitleAndSubtextContainer);
		levelOfDetailSubHeadingText.append(levelOfDetailValue);
		levelOfDetailSubHeading.append(levelOfDetailSubHeadingText);
		levelOfDetailSubHeadingText.html(levelOfDetailSubHeadingText.html() + '%');
		levelOfDetailSliderElement.append(levelOfDetailSlider);
		levelOfDetailHeadingContainer.append(levelOfDetailHeading);
		levelOfDetailContainer
		.append(levelOfDetailHeadingContainer)
		.append(levelOfDetailSliderElement)
		.append(levelOfDetailSubHeading);
		form.append(levelOfDetailContainer).append(chooseSomeAttributeContainer).append(exportButtonContainer);
		
		contentPanel.append(descriptionElement).append(form);
		$('.functionsAccordion').append(header).append(contentPanel);
		
}

function destroyNewPluginContainer() {
	$('.newPlugin').remove();
}

function removeNewPluginHelperClasses() {
	$('.newPlugin').removeClass('newPlugin');
}

function pluginsCallBackendDSSUtilityFunction(jsonData, callback, id) {
	var url = executeFunctionURL;
	
	var PluginExecutionMessenger = {};
	PluginExecutionMessenger.parameters = jsonData;
	PluginExecutionMessenger.userInfoObject = userinfoObject;
	PluginExecutionMessenger.pluginId = id;
	
	showSpinner();
	AJAX_Call_POST(url, callback, PluginExecutionMessenger);
}

function getPluginIdDSSUtility() {
	return pluginIdOfLatestLoadedPlugin;
}

function loadPlugin(pluginObject){
	var $accordionHeader = $('#'+pluginObject.pluginId.toString()).closest('.functionsAccordionContentPanel').prev();
	
	var principalName = userinfoObject.fullname;
	var tenantName = userinfoObject.tenant;
	var pluginName = pluginObject.pluginName//$accordionHeader.text();
	var projectName = userinfoObject.projectName;
	var pluginId = pluginObject.pluginId;
	
//	pluginIdOfLatestLoadedPlugin = pluginId;
	
	var parameters = "principalName=" + encodeURIComponent(principalName)
					+ "&tenantName=" + encodeURIComponent(tenantName)
					+ "&pluginName=" + encodeURIComponent(pluginName)
					+ "&projectName=" + encodeURIComponent(projectName)
					+ "&pluginId=" + encodeURIComponent(pluginId);
	
	var urlMVC = encodeURIComponent("plugin/loadPluginByNameAndTenant");
	var url = createLink(resourceURL, urlMVC, parameters);
	var context = null;
	
	showSpinner();
//	appendFunctionContainerToDom(pluginName);
	
	$.getScript(url)
		.done(function( script, textStatus ) {
			hideSpinner();
			createControl(pluginObject);
			pluginObject.isPluginLoaded = true;
			pluginObject.pluginFunctionRunButton.prop('disabled', false);
			var layers = $('#layersPanel').data('layers');
			var name = getWidgetName(pluginObject);
			pluginObject.pluginContainerElement[name]('setLayers', layers);
//			removeNewPluginHelperClasses();
		})
		.fail(function( jqxhr, settings, exception ) {
			$('.wizard').modal('hide');
			$('#InternalServerErrorModal').modal('show');
			destroyNewPluginContainer();
			hideSpinner();
		});
}

function loadPluginOnAccordionHeaderClick() {
	$('#functionsPanel').off('click').on('click','.functionsAccordionTitle', function(event) {
		var $accordionHeader = $(this);
		var $accordionBody = $(this).next();
		var pluginId = $accordionBody.find('.chooseSomeAttributeContainer').attr('id');
		if($accordionHeader.hasClass('pluginNotLoaded')) {
			loadPlugin(pluginId);
		}
	});
}

function getProjectLayers() {
	return $('#layersPanel').data('layers');
}

function getWidgetNamespace(pluginObject) {
	var namespaceParts = pluginObject.widgetName.split('.');
	var namespace = namespaceParts[0];
	
	return namespace;
}

function getWidgetName(pluginObject) {
	var namespaceParts = pluginObject.widgetName.split('.');
	var namespace = namespaceParts[0];
	var name = namespaceParts[1];
	
	return name;
}

function createControl(pluginObject) {
	var name = getWidgetName(pluginObject);
	
	pluginObject.pluginContainerElement[name]();
}

function setRunFunctionButtonClickHandler($runButton, pluginObject ) {
	$runButton.off('click').click(function(event){
		event.preventDefault();
		
		var name = getWidgetName(pluginObject);
		var jsonData = pluginObject.pluginContainerElement[name]('getParameters');
		
		var callback = function(data){
			pluginObject.pluginContainerElement[name]('setExecutionResult', data.response);
			hideSpinner();
		}
		
		pluginsCallBackendDSSUtilityFunction(jsonData, callback, pluginObject.pluginId);
	});
}