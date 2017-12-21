function showPreloader(){
	setTimeout(function(){
		$('div#blanket').removeClass('hiddenBlanket');
		$('p#preloader').removeClass('hiddenPreloader');
	}, 500);
}
function hidePreloader(){
	setTimeout(function(){
		$('div#blanket').addClass('hiddenBlanket');
		$('p#preloader').addClass('hiddenPreloader');
	}, 500);
}