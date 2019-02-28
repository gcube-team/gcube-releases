function showSpinner(){
	$('#pickProjectContainer').addClass('spinner');
	$('#spinner').removeClass('hidden');
	$('.modal-backdrop.fade.in').addClass('hidden');
}

function hideSpinner(){
	$('#pickProjectContainer').removeClass('spinner');
	$('#spinner').addClass('hidden');
	$('.modal-backdrop.fade.in').removeClass('hidden');
}

function showPluginSpinner(){
	$('.functionsAccordion').addClass('hidden');
	$('#spinnerPlugin').removeClass('hidden');
}

function hidePluginSpinner(){
	$('#spinnerPlugin').addClass('hidden');
	$('.functionsAccordion').removeClass('hidden');
}

function showPluginLoadingUISpinner($headerElement){
	$headerElement.find('.pluginLoadingUISpinner').removeClass('hidden');
}

function hidePluginLoadingUISpinner($headerElement){
	$headerElement.find('.pluginLoadingUISpinner').addClass('hidden');
}

function showPluginProcessingSpinnerForPluginBody($headerElement, $element){
	$headerElement.find('.pluginProcessingSpinner').removeClass('hidden');
	$element.find('.pluginProcessingSpinnerForPluginBody').removeClass('hidden');
}

function hidePluginProcessingSpinnerForPluginBody($headerElement, $element){
	$headerElement.find('.pluginProcessingSpinner').addClass('hidden');
	$element.find('.pluginProcessingSpinnerForPluginBody').addClass('hidden');
}