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