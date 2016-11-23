function buildAccordion(number) {
	if(number === RIGHTSIDE_ACCORDION){
		buildAccordionForRightSidePanel();
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

function buildAccordionForRightSidePanel(){
	populateAccordionWithHTMLEelemmentsBeforeInitializing();
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

function populateAccordionWithHTMLEelemmentsBeforeInitializing(){
	$.each(layersNamesTest, function(index, value){
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
		$('.functionsAccordion').append(header).append(contentPanel);
		
	});
}
function populateAccordionWithHTMLEelemmentsBeforeInitializing2(){
	$.each(layersNamesTest, function(index, value){
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