//div#DecisionSupportSystem, portlet specific jquery
function searchBar(){
	$('div#DecisionSupportSystem button#DSSSearchbarDropdown').off('click').on('click', function(){
		$(this).toggleClass('clicked');
		$('div#DecisionSupportSystem  ul#searchBarDropdown-menu').toggleClass('shown');
	});
	
	$('div.pickViewport').off('click').on('click', function(){
		if(!$(this).hasClass('clicked')){
			$.each($('div.pickViewport.clicked'),function(){
				$(this).find('i').removeClass('fa-dot-circle-o').addClass('fa-circle-o');
			});
			$('div.pickViewport.clicked').removeClass('clicked');
			$(this).addClass('clicked');
			$(this).find('i').removeClass('fa-circle-o').addClass('fa-dot-circle-o');
		}
	});
}

function radioButtonToggle(){
	$.each($('div.pickViewport'),function(){
		if($(this).hasClass('clicked')){
			$(this).find('i').removeClass('fa-circle-o').addClass('fa-dot-circle-o');
		}else {
			$(this).find('i').removeClass('fa-dot-circle-o').addClass('fa-circle-o');
		}
	});
}