function mapExportEvents(){
	$('div#DecisionSupportSystem button#popoverInfoViewAll').off('click').on('click', function(){
		$('div#DecisionSupportSystem div#mapExportModal').modal('show');
		if(firstTimeOpeningMapExportModal)
			setTimeout(function(){
				var extent = map.getView().calculateExtent(map.getSize());
				mapInsdideViewMoreModal.getView().fit(extent, mapInsdideViewMoreModal.getSize());
				mapInsdideViewMoreModal.updateSize();
				setTimeout(function(){
					var extent = map.getView().calculateExtent(map.getSize());
					mapInsdideViewMoreModal.getView().fit(extent, mapInsdideViewMoreModal.getSize());
					mapInsdideViewMoreModal.updateSize();
					firstTimeOpeningMapExportModal = false;

					addLayersToViewMoreModalMap();
				}, 1000);
			}, 1000);
	});
	
	$('div#DecisionSupportSystem div#closePopover').off('click').on('click', function(){
		$('div#DecisionSupportSystem div.ol-overlay-container').addClass('hidden');
	});
	
	$('button#closeButtonModalBottom').off('click').on('click',function(){
		$('div#mapExportModal').modal('hide');
	});
	
	$('div#DecisionSupportSystem div#mapExportModal').off().on('shown', function() {
		var extent = map.getView().calculateExtent(map.getSize());
		mapInsdideViewMoreModal.getView().fit(extent, mapInsdideViewMoreModal.getSize());
		mapInsdideViewMoreModal.updateSize();

		removeLayersToViewMoreModalMap();
		addLayersToViewMoreModalMap();
	});
	
	$('div#DecisionSupportSystem div#mapExportModal').on('hidden', function() {
		removeLayersToViewMoreModalMap();
	});
	
	$('#exportAsButtonModalBottom').off().on('click', function(){
		$('#exportAsButtonModalBottom .dropdown-toggle').dropdown('toggle');
	});
}