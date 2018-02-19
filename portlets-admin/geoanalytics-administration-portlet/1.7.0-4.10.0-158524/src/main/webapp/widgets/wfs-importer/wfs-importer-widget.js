$.widget("cite.WFSImport", {
	
	notificator : null,
	dataTable : null,
	options : {
		headerDiv: {},
		content: {}
	},
	_create : function() { 
		this.wfsImportLayoutHead ='	<div class="span4" id="urlInput">															\
										<input type="text" id="url" class="span12" placeholder="http://..."> 				\
										<label id="urlInputError">Invalid url</label>	\
					   			  	</div>																					\
									<div class="span3" id="moreOptions"> 																		\
										<div class="spinner" style="display: none"></div>												\
										<div class="row"> 																				\
											<div class="span6" id="selectVersionWfs"> 													\
												<span id="selectVersion">Select Version</span> 		\
												<select id="wfs-importer-version">	\
													<option value="1.0.0" selected="selected">1.0.0</option>	\
												</select>	\
											</div> 																				\
											<div class="span4" id="fetchLayers"> 														\
												<button disabled class="btn portlet-button" id="fetchLayersWfs">Fetch</button>																	\
											</div>													\
										</div> \
									</div>		\
									<br><br>';
		this.wfsImportLayoutContent = '<div class="row layers">																			\
										<span class="headerDescription">LAYERS</span>																\
										<hr>\
										<p id="wfs-importer-notificator" style="height:20px;"></p> \
		                                <div align="center" style="display: inline-block;" class="portlet-datatable-toolbar" id="wfs-importer-layers-toolbar">  \
											<div align="center" style="display: inline-block;" class="portlet-datatable-buttons"> \
												<button type="button" id="wfs-importer-import-selected-button" class="toggle-on-row-selection" data-toggle="modal" data-target="#layerInfoModal" disabled>	\
													<i class="fa fa-plus-circle" aria-hidden="true"></i> Import Layers \
												</button>\
											</div> \
										</div>     \
										<table id="wfs-importer-layers-datatable"></table>																							\
									</div>';
		
		this.wfsImportLayerInfo = ' <div class="modal fade" id="layerInfoModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">	\
									    <div class="modal-dialog">																									\
									      <div class="modal-content">																								\
									        <div class="modal-header">																								\
									          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>							\
									          <h4 class="modal-title">Import WFS Layers</h4>																			\
									        </div>																													\
									        <div class="modal-body" id="layerInfoModalBody">																		\
																																			\
									        </div>																													\
											<div class="modal-footer">				\
			   									<button type="button" id="importWfsLayers" class="btn portlet-button pull-left">Import</button>									\
			   									<button type="button" id="cancelWfsLayers" class="btn portlet-button pull-right" data-dismiss="modal">Close</button>									\
										    </div>																													\
									      </div>																						\
									    </div>																						\
									</div>';
			
		this.selfId = this.element.attr('id');
	},
	createAsModal: function(pageState) {   /* modal version has not ever used*/
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
								'</div>' +
						  '</div>';
		$('.adminContainer').append(modalLayout);
		$('.adminContainer #' + this.selfId+"Modal .bodyOfModal").append(this.wfsImportLayoutHead);
		$('.adminContainer #' + this.selfId+"Modal .bodyOfModal").append(this.wfsImportLayoutContent);
		$('.adminContainer #' + this.selfId+"Modal .bodyOfModal").append(this.wfsImportLayerInfo);
		

	},
	createAsDiv: function(pageState) {
//		$(this.options.headerDiv).append(this.wfsImportLayoutHead);
		$(this.options.content).append(this.wfsImportLayoutHead);
		$(this.options.content).append(this.wfsImportLayoutContent);
		$(this.options.content).append(this.wfsImportLayerInfo);
		
		this._initializeContent(pageState);
	},
	_initializeContent: function(pageState) {
		var self = this;
		var notificator = this.notificator = $('#wfs-importer-notificator');		
		
		pageState.url = "";
		
		function isValidUrl(url){
			var myVariable = url;
		    if(/^(http|https|ftp):\/\/[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$/i.test(myVariable)) {
		    	return 1;
		    } else {
		    	return -1;
		    }   
		}
		
		// validation of url while texting
		$('#url').on('input', function(e) {
			if ($(this).val().replace(/\s/g, '').length){
				pageState.url = $(this).val();
		        
		        if (isValidUrl(pageState.url)==1){
		        	$('#fetchLayersWfs').prop('disabled', false); 
		        	$('#url').css({"border":"1px solid #DDD"});
					$('#urlInputError').css({"display":"none"});
		        } else {
		        	pageState.url = "";
		        	$('#fetchLayers button').prop('disabled', true); 
		        	$('#url').css({"border":"1px solid #b94a48"});
					$('#urlInputError').css({"display":"block"});
		        }				
			}else {
				pageState.url = "";
				$('#fetchLayersWfs').prop('disabled', false); 
				$('#url').css({"border":"1px solid #b94a48"});
				$('#urlInputError').css({"display":"block"});
			}
		});
		
		$('#fetchLayersWfs').on('click', function(e){
			self._fetchLayers(pageState);
		});
		
		this._populateLayers(pageState);
	},
	_fetchLayers : function(pageState) {
		var dataTable = this.dataTable;		
		var notificator = this.notificator;
		
	    window.notificator.setText(notificator, "", "success");

		var wfsRequestMessenger = {
			url: pageState.url,
			version: pageState.version
		};
		
		var url = window.config.createResourceUrlWithParameters('import/getCapabilities', wfsRequestMessenger);
		
        // Create datatable

		if(dataTable != null){
		    dataTable.destroyDataTable();
		}
	
        $('#wfs-importer-layers-datatable').PortletDataTable({
            ajax :  {
                url : url,
                type : "GET",
                dataType: "json",
                beforeSend : function() {
                    $(".spinner").show();
                },
                dataSrc : function(data) {              
                    // success callback
                },
                error : function(jqXHR, exception) {
        		    window.notificator.errorHandling(notificator, jqXHR, exception);
                },
                complete : function() {
                   $(".spinner").hide();
                },
                timeout : 20000
            },
            columnDefs : [ 
            {
                title : "Name",
                fieldName : "name"
            },
            {
                title : "Title",
                fieldName : "title"
            },
            {
                title : "Abstract",
                fieldName : "abstractText"
            },
            {
                title : "Keywords",
                fieldName : "keywords"
            },
            {
                title : "SRS",
                fieldName : "srs"
            }],
            order : [[0, "asc"]],
            selectStyle : "multi",
            toolbar : $("#wfs-importer-layers-toolbar")
        });

        // Get Widget Instance
        
        this.dataTable = $('#wfs-importer-layers-datatable').data("dt-PortletDataTable");			
	},
	_populateLayers: function(pageState) {	
		var widget = this;
		var notificator = this.notificator;
		var dataTable = null;
		
		$('#wfs-importer-import-selected-button').on('click', function(e) {	    
			dataTable = widget.dataTable;
		    var selectedLayers = dataTable.getSelectedRowsData();
		    
			if(selectedLayers){				
				var modalBody = $('#layerInfoModalBody');				
				var form = $('<form class="form-horizontal" autocomplete="off"></form>');				
				modalBody.empty();
				modalBody.append(form);
				
				for(i=0; i < selectedLayers.length; i++){
					var wfsLayerName = '<h4>'+selectedLayers[i].name+'</h4>';					
					var layerName = '<div class="control-group" class="span12">' +
										'<label class="control-label" for="modalLayerName'+i+'">Layer Name <span class="makeMeOrange">*</span></label>' +
										'<div class="controls" class="span8">' +
											'<input type="text" class="span11 modalLayerName" id="modalLayerName'+i+'" placeholder="Please fill in your Layer Name"></input>' +
											'<label class="control-label modalLayerNameError" id="modalLayerNameError'+i+'">This field is required</label>' +
										'</div>' +
									'</div> ';
					var layerStyle ='<div class="control-group" class="span12">' +
										'<label class="control-label" for="modalLayerStyle'+i+'">Layer Style <span class="makeMeOrange">*</span></label>' +
										'<div class="controls" class="span8">' +
											'<select class="span11 modalLayerStyle" id="modalLayerStyle'+i+'"><option  value="" disabled selected>Choose a Style</option></select>' +
											'<label class="control-label modalLayerStyleError" id="modalLayerStyleError'+i+'">This field is required</label>' +
										'</div>' +
									'</div> ';
					var layerDescription = 	'<div class="control-group" class="span12">' +
												'<label class="control-label" for="modalLayerDescription'+i+'">Layer Description</label>' +
												'<div class="controls" class="span8">' +
													'<textarea rows="4" cols="50" class="span11 modalLayerDescription" id="modalLayerDescription'+i+'" placeholder="Please fill in your Layer Description"></textarea>' +
												'</div>' +
											'</div> ';
					var publishLayerOnGeoNetwork = 	'<div class="control-group" class="span12">' +
												'<label class="control-label" for="wfs-importer-layer-toggle-geonetwork-metadata-' + i + '">Publish on GeoNetwork</label>' +
												'<div class="controls" class="span8">' +
													'<input type="button" id="wfs-importer-layer-toggle-geonetwork-metadata-' + i + '"/>' +
												'</div>' +
											'</div> ';
					
					form.append('<hr>');
					form.append(wfsLayerName);
					form.append('<hr>');
					form.append(layerName);
					form.append(layerStyle);
					form.append(layerDescription);
					form.append(publishLayerOnGeoNetwork);
					
					$("#wfs-importer-layer-toggle-geonetwork-metadata-" + i ).styledCheckbox({
						initiallyChecked : true						
					});
			    } 

				$.ajax({
					url: window.config.createResourceURL('styles/getAllStyles'),
					type: 'GET',
					cache : false,
					dataType: 'json',
					success: function(response) {
						$.each(response, function(i, v){
							var $option = $('<option></option>', {
								text : v,
								value : i
							});
							$('.modalLayerStyle').append($option);
						});
					},
					error : function(jqXHR, exception) {
	        		    window.notificator.errorHandling(notificator, jqXHR, exception);
					}
				});					
			}
		});
		
		//while typing layerName, remove error message for empty field
		
		$('body').on('keyup', '.modalLayerName',function(e) {
			var selectedLayers = dataTable.getSelectedRowsData();
			
			if(selectedLayers){			
				for(i=0; i < selectedLayers.length; i++){
					if($('#modalLayerName'+i+'').val()!=null && $('#modalLayerName'+i+'').val().replace(/\s/g, '').length > 0) {
						$('#modalLayerName'+i+'').css({"border":"1px solid #DDD"});
						$('#modalLayerNameError'+i+'').css({"display":"none"});
					}				
				}
			}
		});
		
		$('body').on('change', '.modalLayerStyle' ,function(e) {			
			var selectedLayers = dataTable.getSelectedRowsData();
			
			if(selectedLayers){			
				for(i=0; i < selectedLayers.length; i++){
					if($('#modalLayerStyle'+i+'').val()!=null && $('#modalLayerStyle'+i+'').val().replace(/\s/g, '').length > 0) {
						$('#modalLayerStyle'+i+'').css({"border":"1px solid #DDD"});
						$('#modalLayerStyleError'+i+'').css({"display":"none"});
					}			
				}
			}
		});
		
		$('#importWfsLayers').on('click', function(e) {		
			var wfsRequestMessenger;
			var layersInfo = [];
			var emptyLayerName = false;
			var emptyLayerStyle = false;
			
			var selectedLayers = dataTable.getSelectedRowsData();
	         
			for(i=0; i < selectedLayers.length; i++){
				
				//Check if there are Layer names for each wfs layer
				if($('#modalLayerName'+i+'').val()==null || $('#modalLayerName'+i+'').val().replace(/\s/g, '').length == 0) {					
					$('#modalLayerName'+i+'').css({"border":"1px solid #b94a48"});
					$('#modalLayerNameError'+i+'').css({"display":"block"});					
					emptyLayerName = true;					
				}
				
				if($('#modalLayerStyle'+i+'').val()==null || $('#modalLayerStyle'+i+'').val().replace(/\s/g, '').length == 0) {					
					$('#modalLayerStyle'+i+'').css({"border":"1px solid #b94a48"});
					$('#modalLayerStyleError'+i+'').css({"display":"block"});					
					emptyLayerStyle = true;					
				}
				
				var wfsReaquestLayer = {
					layerName : $('#modalLayerName'+i+'').val().trim(),
					layerDescription : $('#modalLayerDescription'+i+'').val().trim(),
					featureTypes : selectedLayers[i].name,
					style : $('#modalLayerStyle'+i+' option:selected').text(),
					publishOnGeoNetwork : $("#wfs-importer-layer-toggle-geonetwork-metadata-" + i).styledCheckbox("isChecked")
				};
				
				layersInfo.push(wfsReaquestLayer);				
		    } 
			
			if(emptyLayerName == true || emptyLayerStyle == true){
				return;
			}
			
			$('#layerInfoModal').modal('hide');
			
			var wfsRequestMessenger = {
		    	url: $('#url').val(),
		    	version: $('#wfs-importer-version').find(":selected").text(),
		    	layersInfo: layersInfo
			};	

		    $.ajax({
		        url : window.config.createResourceURL('import/storeShapeFilesForFeatureType'),
		        type : "POST",
		        cache : false,
		        data : JSON.stringify(wfsRequestMessenger),
		        contentType : "application/json",
                beforeSend : function() {
                    $(".spinner").show();
                },
                success : function(message) { 
        		    window.notificator.setText(notificator, message, "success");
                },
                error : function(jqXHR, exception) {
        		    window.notificator.errorHandling(notificator, jqXHR, exception);
                },
                complete : function() {
                   $(".spinner").hide();
                   widget.dataTable.deselectAllRows();
                },
		    });	
		});
	},
	cleanMe: function(e) {
		$(this.options.content).children().remove();
	}	
});