//div#DecisionSupportSystem, portlet specific jquery
function leftSidePanelHandlersAndEvents(){
	$('div#DecisionSupportSystem div#collapsibleMenuLeftButtonClosed').off('click').on('click', function(){
		$(this).addClass('hidden');
		$('div#DecisionSupportSystem div#collapsibleMenuLeftButton').toggleClass('clicked');
		$('div#DecisionSupportSystem  div#collapsibleMenuLeft').toggleClass('shown');
	});
	
	$('div#DecisionSupportSystem div#collapsibleMenuLeftButton').off('click').on('click', function(){
		$('div#DecisionSupportSystem div#collapsibleMenuLeftButtonClosed').removeClass('hidden');
		$('div#DecisionSupportSystem div#collapsibleMenuLeftButton').toggleClass('clicked');
		$('div#DecisionSupportSystem  div#collapsibleMenuLeft').toggleClass('shown');
	});
	
	$('div#DecisionSupportSystem').off('click').on('click', '#layersAccordion div.accordion-heading', function(){
		$('#layersAccordion div.accordion-heading').not(this).removeClass('clicked');
		$(this).toggleClass('clicked');
	});
}