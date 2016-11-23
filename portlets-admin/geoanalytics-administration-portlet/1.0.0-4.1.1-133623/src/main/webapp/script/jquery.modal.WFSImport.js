$.widget("modal.WFSImport", {
	options : {
		
	},
	_create : function() { 	/* happens only once*/

		this.wfsImportLayout = 	'<div class="wfsLayout">' +
									'<button class="clearAll hide">Clear inputs</button>' +
									'<table class="table">' +
										'<tbody>' +
											'<tr>' +
	   											'<td>' +
	   												'<label class="labelUrl">Type Url</label>' +
	   											'</td>	' +
	   											'<td>	' +
	   												'<input type="text" id="url" class="input-small" placeholder="Endpoint">' +
	   											'</td>' +
	   										'</tr>' +
	   										'<tr>' +
	   											'<td>' +
	   												'<label class="labelVersion">Type Version</label>' +
	   											'</td>	' +
	   											'<td>	' +
	   												'<input type="text" id="version" class="input-small" placeholder="Version">' +
	   											'</td>' +
	   										'</tr>' +
	   										'<tr>' +
   												'<td>' +
   													'<label class="labelOutputFormat">Selected output format for feature types</label>' +
   												'</td>	' +
   												'<td>	' +
   													'<input type="text" id="outputFormat" class="input-small">' +
   												'</td>' +
   											'</tr>' +
	   									'</tbody>' +
	   								'</table>' +
	   								'<div class="listContainer hide">' +
	   						      		'<ul class="featureTypeList" role="menu">' +
	   						      		'</ul>' +
	   						      	'</div>' +
	   						      	'<button class="saveModal saveChecked hide">Save selected feature types</button>' +
									'<button class="saveModal getLayers">Get Layers</button>' +
									
	   							 '</div>';
		this.selfId = this.element.attr('id');
	},
	createAsModal: function() {
		this.element.append('<button class="wfsImporterButton"></button>');

		
		$('.wfsImporterButton').attr('data-target', "#"+this.selfId+"Modal")
		   .attr('data-toggle', 'modal')
		   .html("WFS importer");
		

		var modalLayout = '<div id="'+this.selfId+"Modal"+'" class="modal fade " tabindex="-1" role="dialog" aria-labelledby="wfsImportModalLabel" aria-hidden="true">' +
								'<div>' +
									'<div class="header-top row">' +
										'<div class="headerOfModal">' +
											'<h1 class="titleOfModal">Wfs Importer</h1>' +
										'</div>' +
									'</div>' +
									'<div class="bodyOfModal">' +
				
									'</div>' +
//									'<div class="footerOfModal">' +
//										'<table class="table borderless">' +
//											'<tbody>' +
//												'<tr>' +
//													'<td>' +
//														'<button class="cancelModal" data-dismiss="modal">Cancel</button>' +
//													'</td>' +
//												'</tr>' +
//											'</tbody>' +
//										'</table>' +
//									'</div>' +
								'</div>' +
						  '</div>';
		$('.adminContainer').append(modalLayout);
		
		console.log( this.selfId);
		$('.adminContainer #' + this.selfId+"Modal .bodyOfModal").append(this.wfsImportLayout);
		
		
//		$('.wfsImporterButton').on('click', function() {
//			if (!('.modal').hasClass('in'))
//				$('.modal').addClass('in');
//			else $('.modal').removeClass('in');
//		});
		
	},
	createAsDiv: function() {
		
		$("#"+this.selfId).append(this.wfsImportLayout);
		
		
	}
});