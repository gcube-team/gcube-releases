$('button#closeButtonModalBottom').off('click').on('click',function(){
	$('div#mapExportModal').modal('hide');
});
$('div#mapExportModal').on('shown',function(){
	var extent = map.getView().calculateExtent(map.getSize());
	mapInsdideViewMoreModal.getView().fit(extent, mapInsdideViewMoreModal.getSize());
	mapInsdideViewMoreModal.updateSize();
});