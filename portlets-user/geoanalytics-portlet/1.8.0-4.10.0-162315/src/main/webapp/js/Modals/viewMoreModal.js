$('button#closeButtonModalBottom').off('click').on('click',function(){
	$('div#mapExportModal').modal('hide');
});

function addLayersToViewMoreModalMap() {
	for(let layer in layersByNameViewMoreModal) {
		mapInsdideViewMoreModal.addLayer(layersByNameViewMoreModal[layer]);
	}
}

function removeLayersToViewMoreModalMap() {
	for(let layer in layersByNameViewMoreModal) {
		mapInsdideViewMoreModal.removeLayer(layersByNameViewMoreModal[layer]);
	}
}

function updateLayersForViewMoreModal() {
	layersByNameViewMoreModal = [];
	
	let selectedLayers = $('#treeviewTaxonomiesLayers').jstree('get_selected');// array
	$.each(featureInfoLayers, function(i, v) {
		$.each(selectedLayers, function(index, value) {
			if (v.indexOf(value) !== -1) {
				layersByNameViewMoreModal.push(layersByName[value]);
			}
		});
	});
}