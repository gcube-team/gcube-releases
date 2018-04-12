function drawCoordinates(){
	$('.ol-scale-line-inner-inner').unbind().bind(
			'DOMCharacterDataModified',
			function(event) {
			}
	);
	
//	$('.ol-mouse-position').unbind().bind(
//			'DOMCharacterDataModified',
//			function(event) {
//				if($('.ol-mouse-position').text() !== ''){
//					var coords = $('.ol-mouse-position').text().split(',');
//					$('#coord-long-section span').text("Long: " + coords[0]);
//					$('#coord-lat-section span').text("Lat: " + coords[1]);
//				}
//			}
//	);
	
	var targetMousePosition = $('.ol-mouse-position')[0];
	
//	var observer = new WebKitMutationObserver(function(mutations) {
	var observer = new MutationObserver(function(mutations) {
		mutations.forEach(function(mutation) {
			if($('.ol-mouse-position').text() !== ''){
				var coords = $('.ol-mouse-position').text().split(',');
				if(typeof coords[0] === "undefined" || typeof coords[1] === "undefined"){
					coords[0]='';
					coords[1]='';
				}
				
				$('#coord-long-section span').text("Long: " + coords[0]);
				$('#coord-lat-section span').text("Lat: " + coords[1]);
			}
		});    
	});
	
	observer.observe(targetMousePosition, { attributes: true, childList: true, characterData: true, subtree: true });
}