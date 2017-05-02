function drawCoordinates(){
	$('.ol-scale-line-inner-inner').unbind().bind(
			'DOMCharacterDataModified',
			function(event) {
			}
	);
	
	$('.ol-mouse-position').unbind().bind(
			'DOMCharacterDataModified',
			function(event) {
				if($('.ol-mouse-position').text() !== ''){
					var coords = $('.ol-mouse-position').text().split(',');
					$('#coord-long-section span').text("Long: " + coords[0]);
					$('#coord-lat-section span').text("Lat: " + coords[1]);
				}
			}
	);
	
}