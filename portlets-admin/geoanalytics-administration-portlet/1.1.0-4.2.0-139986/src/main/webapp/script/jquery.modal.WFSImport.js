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
	   												'<input type="text" id="url" class="input-small" placeholder="http://...">' +
	   											'</td>' +
	   										'</tr>' +
	   										'<tr>' +
	   											'<td>' +
	   												'<label class="labelVersion">Type Version</label>' +
	   											'</td>	' +
	   											'<td>	' +
//	   												'<input type="text" id="version" class="input-small" placeholder="Version">' +
	   												'<input type="text" id="version" class="input-small">' +
	   												'<div class="error onVersion hide">* Not a valid wfs version</div>' +
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
	   								'<button class="saveModal getLayers">Get Layers</button>' +
	   								'<div class="listContainer hide">' +
	   									'<span class = "label" id="selectLayer">Select the layer you want to store</span>' +
	   						      		'<ul class="featureTypeList" role="menu">' +
	   						      		'</ul>' +
	   						      	'</div>' +
	   						      	'<div id="containerOfActions" class="hide">' +
		   						      	'<div class="selectTaxonomy">'+
		   						      		'<div class="btn-group" id="taxonomiesDropdownDiv">' +
		   						      			'<a class="btn dropdown-toggle" data-toggle="dropdown" id="taxonomiesDropdown">' +
		   						      				'Select Taxonomy' +
		   						      				'<span class="caret"></span>' +
												'</a>' +
												'<ul class="dropdown-menu">' +
												
												'</ul>' +
											'</div>' +
										'</div>' +
		   						      	'<div class="selectTerm">'+
		   						      		'<div class="btn-group" id="termsDropdownDiv">' +
		   						      			'<a class="btn dropdown-toggle" data-toggle="dropdown" id="termsDropdown">' +
		   						      				'Select Term' +
		   						      				'<span class="caret"></span>' +
		   						      			'</a>' +
		   						      			'<ul class="dropdown-menu">' +
				   						      	    	
		   						      			'</ul>' +
		   						      		'</div>' +
		   						      			'<label class="labelUrl">Or create new</label>' +
												'<input type="text" id="newTerm" class="input-small" placeholder="New Term">' +
		   						      	'</div>' +
		   						      	
		   						    '</div>' +
		   						    '<button class="saveModal saveChecked hide">Save selected feature types</button>' +
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