function showPreloader(){
	setTimeout(function(){
		$('div#blanket').addClass('shown');
		$('div#blanket p#preloader').removeClass('hiddenPreloader');
	}, 500);
}
function hidePreloader(){
	setTimeout(function(){
		$('div#blanket').removeClass('shown');
		$('div#blanket p#preloader').addClass('hiddenPreloader');
	}, 500);
}