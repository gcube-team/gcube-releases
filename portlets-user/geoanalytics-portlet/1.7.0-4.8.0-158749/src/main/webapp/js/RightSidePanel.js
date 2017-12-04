//div#DecisionSupportSystem, portlet specific jquery
function rightSidePanelHandlersAndEvents(){
	$('div#DecisionSupportSystem div#collapsibleMenuRightButtonClosed').off('click').on('click', function(){
		$(this).addClass('hidden');
		$('div#DecisionSupportSystem  div#collapsibleMenuRight').toggleClass('shown');
		$('div#DecisionSupportSystem div#collapsibleMenuRightContainer').addClass('rightButtonClicked');
		$('div#DecisionSupportSystem div#collapsibleMenuRightButton').toggleClass('clicked');
	});
	
	$('div#DecisionSupportSystem div#collapsibleMenuRightButton').off('click').on('click', function(){
		$('div#DecisionSupportSystem div#collapsibleMenuRightButtonClosed').removeClass('hidden');
		$('div#DecisionSupportSystem div#collapsibleMenuRightButton').toggleClass('clicked');
		$('div#DecisionSupportSystem  div#collapsibleMenuRight').toggleClass('shown');
	});
	
	$('div#DecisionSupportSystem #rightSideAccordion div.accordion-heading').off('click').on('click', function(){
		$(this).toggleClass('clicked');
		$('div#DecisionSupportSystem #rightSideAccordion div.accordion-heading').not(this).removeClass('clicked');
	});
}