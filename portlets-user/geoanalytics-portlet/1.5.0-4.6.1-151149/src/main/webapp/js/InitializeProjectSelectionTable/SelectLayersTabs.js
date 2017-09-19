function selectLayersTabs(){
	$('#layerTabs a').off().on('click', function (event) {
		event.preventDefault();
		$(this).tab('show');
	});
	
	$('#appTabs a').off().on('click', function (event) {
		event.preventDefault();
		$(this).tab('show');
	});
	
	$('#appTabs a[data-toggle="tab"]').on('shown', function (event) {
		if($(this).attr('href')==='#DSS'){
			map.updateSize();
			$('#setToInitialExtent').click();
			map.updateSize();
		}
	});
}