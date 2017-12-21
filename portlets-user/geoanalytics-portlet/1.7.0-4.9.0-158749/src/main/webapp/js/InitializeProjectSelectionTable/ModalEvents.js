function modalEvents() {
	allModalsEvents();
	bboxModalEvents();
	layersModalEvents();
	relateUsersToProjectModalEvents();
	RelateUsersGroupsToProjectModalEvents();
	nameAndDescriptionModalEvents();
	GroupsManipulationModalEvents();
	assignGroupsOrIndividualsToProject();
	AssignUsersToProjectGroupsModalEvents();
	deleteGroupModalEvents();
}

function allModalsEvents(){
	$('.wizard .cancelBtns:not(.GroupsManipulationModalCancelButton)').off('click').on('click', function(){
		clearModals();
	});
	
	$('.wizard').on('hidden', function(){
		$('#createNewProject').removeClass('clicked');
	});
	
	$('#BBOXModal').on('shown', function() {
		userinfoObject.editMode = EDITMODE;
		coords = {};
		mapBBOX.updateSize();
		if(EDITMODE && !goBackwardsToBBOXModal) {
			showSpinner();
			var url = bboxURL;
			var callback = function(data){
				hideSpinner();
				if(data.status === "Success") {
					extractCoordinates(data.response);
				} else {
					$('.wizard').modal('hide');
					$('#InternalServerErrorModal').modal('show');
				}
			};
			var data = userinfoObject;
			AJAX_Call_POST(bboxURL, callback, userinfoObject);
		}
	});
	
	$('#ChooseLayersModal').on('shown', function(){
		layersMap.updateSize();
		var theExtent;
		if(EDITMODE){
			if(editeModeCoordinatesLayersModal.length !== 0){
				layersMap.getView().fit(editeModeCoordinatesLayersModal, layersMap.getSize());
			}
			if(typeof coords.extent === "undefined"){
				theExtent = editeModeCoordinates;
				layersMap.getView().fit(theExtent, layersMap.getSize());
			}else{
				theExtent = coords.extent;
				layersMap.getView().fit(theExtent, layersMap.getSize());
			}
		}else{
			theExtent = coords.extent;
			layersMap.getView().fit(theExtent, layersMap.getSize());
		}
		
		theExtent = ol.proj.transformExtent(theExtent, ol.proj.get('EPSG:3857'),ol.proj.get('EPSG:4326'));
		
		layersMap.updateSize();
		if(LISTLAYERSFLAG) {
			var JSTREEToServerToken = {};
			JSTREEToServerToken.type = "LAYERTAXONOMY";
			JSTREEToServerToken.taxonomyID = null;
			JSTREEToServerToken.geographyExtent = theExtent;
			
			//fetch all layers
			$('#treeviewLayers')
			.off()
			.on('select_node.jstree', function(e, data) {
//				var indexOfLayer = layersObject.jstreeLayers.indexOf(data.node.text);
				var indexOfLayer = layersObject.jstreeLayers.indexOf(data.node.id);
				if(indexOfLayer === -1) {
//					layersObject.jstreeLayers.push(data.node.text);
//					layersMap.addLayer(fetchLayerByLayerNameModal(data.node.text));
					layersObject.jstreeLayers.push(data.node.id);
					layersMap.addLayer(fetchLayerByLayerNameModal(data.node.id));
				}
			})
			.on('deselect_node.jstree', function(e, data) {
				var indexOfLayer = layersObject.jstreeLayers.indexOf(data.node.id);
				if(indexOfLayer > -1) {
					layersObject.jstreeLayers.splice(indexOfLayer, 1);
				}
				layersMap.removeLayer(layersByNameModal[data.node.id]);
				layersMap.updateSize();
			})
			.on('init.jstree Event',function(event, data){
				jstreeIsLoaded = true;
			})
			.on('ready.jstree refresh.jstree',function(event, data){
				layersIntersectingWithCurrentBBOX;
				
				if(EDITMODE && !EDITMODE_USER_PRESSED_CANCEL) {
					showSpinner();
					
					var url = listLayersByProjectUrl;
					var callback = function(data){
						var projectLayers = [];
						$.each(data, function(index, value){
							projectLayers.push(value.text);
						});
						
						var mapNamesToIDs = {};
						$.each($('#treeviewLayers ul li'), function(index, value){
							var id = this.id;
							var name = $(this).find('a').text();
							mapNamesToIDs[name] = id;
						});
						
						$.each(projectLayers, function(index, value){
							$('#treeviewLayers').jstree().select_node(mapNamesToIDs[value]);
						});
						
						var treeData = $('#treeviewLayers').jstree(true).get_json('#', {flat:false});
						var treeNodesValues = [];
						$.each(treeData, function(index, value){
							treeNodesValues.push(value.text);
						});
						
						var layerNotListed = false;
						$.each(projectLayers, function(i, v){
							if(treeNodesValues.indexOf(v) < 0){
								layerNotListed = true;
								return false;
							}
						});
						if(layerNotListed){
							$('#fetchAllAvailableLayers').click();
						}
						
						hideSpinner();
					};
					
					AJAX_Call_POST(url, callback, userinfoObject);
				}
			})
			.jstree({
				plugins : [ 'checkbox', 'sort', 'wholerow', 'search' ],
				search : {
					'show_only_matches' : true
				},
				checkbox : {
					keep_selected_style : false
				},
				core : {
					themes : {
						'stripes' : true
					},
					data : {
						url : function(node) {
							if (node.id === '#') {
								if($('#fetchAllAvailableLayers').prop('checked') === false){
									return listOfAllAvailableLayersURL;
								}else{
									return listOfAllLayers;
								}
							}
						},
						type : 'post',
						dataType : "json",
						contentType : 'application/json',
						data : function(node) {
							var JSTREEToServerToken = {};
							JSTREEToServerToken.type = "LAYERTAXONOMY";
							JSTREEToServerToken.taxonomyID = null;
							JSTREEToServerToken.geographyExtent = theExtent;
							JSTREEToServerToken.tenantName = userinfoObject.tenant;
							if (node.id !== '#') {
								JSTREEToServerToken.taxonomyID = node.id;
							}
							return JSON.stringify(JSTREEToServerToken);
						},
						contentType : 'application/json',
						success : function(serverResponse) {
							var layerNames = [];
							for (i = 0; i < serverResponse.length; i++) {
								layerNames.push(serverResponse[i].text);
							}
							if($('#fetchAllAvailableLayers').prop('checked') === false){
								allLayers = layerNames;
							}else{
								layersIntersectingWithCurrentBBOX = layerNames;
							}
							
							initPluginForLayersOnChooseLayersModal(layerNames);
						},
						error : function(jqXHR, textStatus, errorThrown) {
							$('#errorModal').modal('show');
						},
						complete : function(data) {
							
						}
					}
				}
			});
			
			LISTLAYERSFLAG = false;
		}
	});
}

/**************** bboxModal Events ****************/
function bboxModalEvents(){
	
	initializeMapForWizardModal();
	
	$('#clearMap').on('click', function(){
		moveEndForMapBBOX();
		editeModeCoordinatesLayersModal = [];
	});
	
	$('#goToChooseLayersModalFromBBOXModal').off().on('click', function(){
		var condition = $('.coordLabel').hasClass('hidden');
		
		if(!condition) {
			coordsObjectToBeSendToDSS = coords;
			$('#chooseAreaMessage').addClass('hidden');
		} else if(EDITMODE){
			if(coords !== undefined && coords !== null){
				coordsObjectToBeSendToDSS = {};
			}else{
				coordsObjectToBeSendToDSS = coords;
			}
			$('#chooseAreaMessage').addClass('hidden');
		}else {
			coordsObjectToBeSendToDSS = {};
			$('#chooseAreaMessage').removeClass('hidden');
			return;
		}
		
		$('#BBOXModal').modal('hide');
		
		$('#ChooseLayersModal').modal('show');
	});
}

function initializeMapForWizardModal() {
	source = new ol.source.Vector({wrapX: false});
	
	var maxPoints = 2;
	
	var baseLayer = new ol.layer.Tile({
    	source : new ol.source.OSM()
    });
	
	var vector = new ol.layer.Vector({
        source: source,
        style: new ol.style.Style({
          fill: new ol.style.Fill({
            color: 'rgba(255, 255, 255, 0.2)'
          }),
          stroke: new ol.style.Stroke({
            color: '#ffcc33',
            width: 2
          }),
          image: new ol.style.Circle({
            radius: 7,
            fill: new ol.style.Fill({
              color: '#ffcc33'
            })
          })
        })
      });
	
	mapBBOX = new ol.Map({
    	target: 'mapBBOX',
        controls: ol.control.defaults({
            zoom: true,
            attribution: false,
            rotate: false
          }),
        layers: [
                 baseLayer, vector
        ],
        view: new ol.View({
        	center: ol.proj.fromLonLat([22.00, 37.00]),
        	zoom: 4
        })
    });
	mapBBOX.set('mapId','mapBBOX');
	initialMapExtent = mapBBOX.getView().calculateExtent(mapBBOX.getSize());
	
	var geometryFunction = function(coordinates, geometry) {
		moveEndForMapBBOX();
		$('.coordLabel').removeClass('hidden');
        if (!geometry) {
        	geometry = new ol.geom.Polygon(null);
        }
        
        var start = coordinates[0];
        var end = coordinates[1];
        geometry.setCoordinates([
                                 [start, [start[0], end[1]], end, [end[0], start[1]], start]
                                 ]);
        var extent = geometry.getExtent();
        
//        $('#coord0').text(extent[0].toString() + '  , ' + extent[1].toString());
//        $('#coord1').text(extent[2].toString() + '  , ' + extent[1].toString());
//        $('#coord2').text(extent[0].toString() + '  , ' + extent[3].toString());
//        $('#coord3').text(extent[2].toString() + '  , ' + extent[3].toString());
        
        var divCoords;
        coords.coord0 = [extent[0], extent[1]];
        $('#coord0').text(transformTo4326(coords.coord0));
        
        coords.coord1 = [extent[2], extent[1]];
        $('#coord1').text(transformTo4326(coords.coord1));
        
        coords.coord2 = [extent[0], extent[3]];
        $('#coord2').text(transformTo4326(coords.coord2));
        
        coords.coord3 = [extent[2], extent[3]];
        $('#coord3').text(transformTo4326(coords.coord3));
        
        coords.extent = extent;
        
        if(EDITMODE){
        	editeModeCoordinatesLayersModal = extent;
        }
        
        $('#chooseAreaMessage').addClass('hidden');
        
        return geometry;
	};
	
	draw = new ol.interaction.Draw({
		source: source,
		type: /** @type {ol.geom.GeometryType} */ ('LineString'),//LineString-->Box
		geometryFunction: geometryFunction,
        maxPoints: maxPoints
	});
	
	mapBBOX.addInteraction(draw);
	mapBBOX.on('moveend', moveEndForMapBBOX);
	addControls('navcross', mapBBOX);
	addControls('zoomSlider', mapBBOX);
	addControls('setInitialExten', mapBBOX);
	
	initializemapForLayersModal();
}

function transformTo4326(coordinates){
	return ol.proj.transform(coordinates, ol.proj.get('EPSG:3857'),ol.proj.get('EPSG:4326'));
}

function moveEndForMapBBOX(){
	source.clear();
	$('.coordLabel').text('');
	$('.coordLabel').addClass('hidden');
}

/*********** layers Modal ***********/
function layersModalEvents(){
	$('#goToRelateUsersToProjectModal').off().on('click', function() {

		$('#ChooseLayersModal').modal('hide');
//		$('#RelateUsersToProjectModal').modal('show');
		$('#assignGroupOrIndividualModal').modal('show');
		
//		if(! $.fn.DataTable.isDataTable( '#relateUsersToProjectsTable' )){
//			initializeAssignUsersToProjectTable();	
//			relateUsersToProjectsTableInitialized = false;
//		}
//		if(EDITMODE) {
//			var url = participantsURL;
//			var callback = function(data){
//				$.each(data.response, function(index, value){
//					$(".usersTagsinput").tagsinput('add', value);
//				});
//			};
//			var data = userinfoObject;
//			
//			AJAX_Call_POST(participantsURL, callback, data);
//		}
		if(EDITMODE){
			$('#make-project-public').prop('checked', projectNameAndDescriptionObject.publicProject);
		}
	});
	
	$('#goToRelateUsersToProjectModalSkipButton').off().on('click', function() {
		$('#ChooseLayersModal').modal('hide');
//		$('#RelateUsersToProjectModal').modal('show');
		$('#assignGroupOrIndividualModal').modal('show');
		if(EDITMODE){
			$('#make-project-public').prop('checked', projectNameAndDescriptionObject.publicProject);
		}
	});
	
	$('#goToBBOXModal').off().on('click', function(){
		goBackwardsToBBOXModal = true;
		$('#ChooseLayersModal').modal('hide');
		$('#BBOXModal').modal('show');
		$('#treeviewLayers').jstree().destroy();
		LISTLAYERSFLAG = true;
	});
	
	$('#goToRelateUsersToProjectModalSkipButton').off('click').on('click', function(){
		layersObject.skipped = false;
		if(! $.fn.DataTable.isDataTable( '#relateUsersToProjectsTable' )){
			initializeAssignUsersToProjectTable();	
			relateUsersToProjectsTableInitialized = false;
		}
		$('#ChooseLayersModal').modal('hide');
//		$('#RelateUsersToProjectModal').modal('show');
		$('#assignGroupOrIndividualModal').modal('show');
	});
	
//	$('#fetchAllAvailableLayers').off('click').on('click', function(){
	$('#fetchAllAvailableLayers').off('change').on('change', function(){
//		if($(this).hasClass('clicked')){
//			return;
//		}
//		$(this).addClass('clicked');
		$('#treeviewLayers').jstree().deselect_all(true);
		removeLayersFromMapModal();
		$('#treeviewLayers').jstree().refresh();
	});
}

/*********** relateUsersToProjectModal ***********/
function relateUsersToProjectModalEvents(){
	$('#goToChooseLayersModalFromRelateUsersToProjectModal').off().on('click', function() {
		$('#RelateUsersToProjectModal').modal('hide');
		$('#ChooseLayersModal').modal('show');
	});
	
	$('#goToAssignGroupOrIndividualModalFromRelateUsersToProjectModal')
		.off('click').on('click', function(){
			$('#RelateUsersToProjectModal').modal('hide');
			$('#assignGroupOrIndividualModal').modal('show');
		});
	
	$('#goToProjectNameAndDescriptionModalFromUsers').off().on('click', function(){
		usersArray = [];
		usersAndRightsArray = [];
		
//		var items = $(".usersTagsinput").tagsinput('items');
//		$.each(items, function(index, value){
//			usersArray.push(value.text);
//			var auxObject = {};
//			auxObject.id = value.value;
//			auxObject.rights = value.data;
//			usersAndRightsArray.push(auxObject);
//		});
		
		usersAndRightsArray = getUsersAndRights();
		
		$('#RelateUsersToProjectModal').modal('hide');
		$('#projectNameAndDescriptionModal').modal('show');
		
		fromUsersAssignment = true;
		fromUsersGroupsAssignment = false;
		fromPickUsersOrUsersGroup = false
		
		if(EDITMODE){
			$("#projectName").val(projectNameAndDescriptionObject.name);
			$("#projectDescription").val(projectNameAndDescriptionObject.description );
			$('#make-project-public').prop('checked', projectNameAndDescriptionObject.publicProject);
		}
	});
	
	$('#RelateUsersToProjectModal').on('shown', function(){
		if($.fn.DataTable.isDataTable( '#relateUsersToProjectsTable' )){
			$('table#relateUsersToProjectsTable').DataTable().columns.adjust().draw();
		}
	});
	
//	$('.usersTagsinput')
//	.on('itemAdded', function(e){
//		$('.selectdUsersTagSection').removeClass('hidden');
//		var numOfTags = $('.usersTagsinput').tagsinput('items').length;
//		$('#numOfSelectedUsers').text(numOfTags);
//		
//		var $lastItemAdded = $('.selectdUsersTagSection .bootstrap-tagsinput').find('.tag.label.label-info').last();
//	}).on('itemRemoved', function(e){
//		
//		var currentPage = $('#relateUsersToProjectsTable').DataTable().page.info().page;
//		var numOfTags = $('.usersTagsinput').tagsinput('items').length;
//		if(numOfTags === 0){
//			$('.selectdUsersTagSection').addClass('hidden');
//			$('#numOfSelectedUsers').text('');
//		}else {
//			$('#numOfSelectedUsers').text(numOfTags);
//		}
//		
//		var rowsNum = $relateUsersToProjectsTable.rows().count();
//		
//		for(var index=0;index<rowsNum;index++){
//			var rowData = $relateUsersToProjectsTable.row(index).data();
//			var $row = $relateUsersToProjectsTable.row(index);
//			if(typeof e.item !== "undefined" && rowData[7] === e.item.value){
//				rowData[4] = createAssignRightsToUserCheckboxes(index, {});
//				rowData[5] = createAddUserToProjecButton();
//				$row.data(rowData).draw();
//			}
//		}
//		
//		$('#relateUsersToProjectsTable').dataTable().fnPageChange( currentPage );
//	}).tagsinput({
//		itemText : 'text',
//		itemValue : 'value'
//	});
}

/********** RelateUsersGroupsToProjectModalEvents **********/
function AssignUsersToProjectGroupsModalEvents(){
	$('#goToGroupsManipulationModalFromAssignUsersToProjectGroupsModal')
		.off('click').on('click', function(){
			$('#AssignUsersToProjectGroupsModal').modal('hide');
			$('#GroupsManipulationModal').modal('show');
		});
	
	$('#assignUsersToProjectGroupsModalOKButton').off('click')
		.on('click', function(){
			showSpinner();
			var url = assignUsersToProjectGroupURL;
			var callback = function(data){
				hideSpinner();
				$('#AssignUsersToProjectGroupsModal').modal('hide');
				if(data.status === "Success"){
				} else {
					$('.wizard').modal('hide');
					$('#InternalServerErrorModal').modal('show');
				}
			};
			var ProjectGroupMessenger = {};
			userinfoObject.groupName = $('#projectGroupName').text();
			
			var row = $('.projectManagementButtons.clicked').closest('tr');
			var data = $('#groupsTable').DataTable().rows(row).data();
			var groupID = data[0][4];
			
			ProjectGroupMessenger.uio = userinfoObject;
			ProjectGroupMessenger.users = [];
			ProjectGroupMessenger.usersUUIDs = [];
			
			$.each($('.assignedUsersTagsinput').tagsinput('items'), function(i,v){
				ProjectGroupMessenger.usersUUIDs.push(v.value);
			});
			
			ProjectGroupMessenger.projectGroupID = groupID;
			
			AJAX_Call_POST(url, callback, ProjectGroupMessenger);
		});
	
	$('#AssignUsersToProjectGroupsModal').on('shown', function(){
		$('#projectGroupName').text(projectGroupNameAssignUsersToProjectGroupsModal);
		if($.fn.DataTable.isDataTable( '#assignUsersToProjectGroupssTable' )){
			$('table#assignUsersToProjectGroupssTable').DataTable().columns.adjust().draw();
		}
	});
	
	$('.assignedUsersTagsinput')
	.on('itemAdded', function(e){
		$('#AssignUsersToProjectGroupsModal .assignedUsersTagSection').removeClass('hidden');
		var numOfTags = $('.assignedUsersTagsinput').tagsinput('items').length;
		$('#numOfAssignedUsers').text(numOfTags);
		
		var $lastItemAdded = $('#AssignUsersToProjectGroupsModal .selectdUsersTagSection .bootstrap-tagsinput').find('.tag.label.label-info').last();
	})
	.on('itemRemoved', function(e){
		var currentPage = $('#assignUsersToProjectGroupssTable').DataTable().page.info().page;
		var numOfTags = $('.assignedUsersTagsinput').tagsinput('items').length;
		if(numOfTags === 0){
			$('#AssignUsersToProjectGroupsModal .assignedUsersTagSection').addClass('hidden');
			$('#numOfAssignedUsers').text('');
		}else {
			$('#numOfAssignedUsers').text(numOfTags);
		}
		
		var rowsNum = $('#assignUsersToProjectGroupssTable').DataTable().rows().count();
		
		for(var index=0;index<rowsNum;index++){
			var rowData = $('#assignUsersToProjectGroupssTable').DataTable().row(index).data();
			var $row = $('#assignUsersToProjectGroupssTable').DataTable().row(index);
			if(typeof e.item !== "undefined" && rowData[6] === e.item.value){
				rowData[4] = createAddUserToProjecButton();
				$row.data(rowData).draw();
			}
		}
		
		$('#assignUsersToProjectGroupssTable').dataTable().fnPageChange( currentPage );
	})
	.tagsinput({
		itemText : 'text',
		itemValue : 'value'
	});
}

/********** RelateUsersGroupsToProjectModalEvents **********/
function RelateUsersGroupsToProjectModalEvents(){
	$('#goToAssignGroupOrIndividualModalFromRelateUsersGroupsToProjectModal')
		.off('click').on('click', function(){
			$('#RelateUsersGroupsToProjectModal').modal('hide');
			$('#assignGroupOrIndividualModal').modal('show');
		});
	
	$('#goToProjectNameAndDescriptionModalFromGroups').off().on('click', function(){
		usersArray = [];
		usersAndRightsArray = [];
		
//		var items = $(".usersGroupsTagsinput").tagsinput('items');
//		$.each(items, function(index, value){
//			usersArray.push(value.text);
//			var auxObject = {};
//			auxObject.id = value.value;
//			auxObject.rights = value.data;
//			usersAndRightsArray.push(auxObject);
//		});
		usersAndRightsArray = getUsersGroupsAndRights();
		
		$('#RelateUsersGroupsToProjectModal').modal('hide');
		$('#projectNameAndDescriptionModal').modal('show');
		
		if(EDITMODE){
			$("#projectName").val(projectNameAndDescriptionObject.name);
			$("#projectDescription").val(projectNameAndDescriptionObject.description );
			$('#make-project-public').prop('checked', projectNameAndDescriptionObject.publicProject);
		}
		
		fromUsersAssignment = false;
		fromUsersGroupsAssignment = true;
		fromPickUsersOrUsersGroup = false;
	});
	
//	$('.usersGroupsTagsinput')
//	.on('itemAdded', function(e){
//		$('.selectdUsersGroupsTagSection').removeClass('hidden');
//		var numOfTags = $('.usersGroupsTagsinput').tagsinput('items').length;
//		$('#numOfSelectedUsersGroups').text(numOfTags);
//		
//		var $lastItemAdded = $('.selectdUsersGroupsTagSection .bootstrap-tagsinput').find('.tag.label.label-info').last();
//	}).on('itemRemoved', function(e){
//		var currentPage = $('#relateUsersGroupsToProjectsTable').DataTable().page.info().page;
//		var numOfTags = $('.usersGroupsTagsinput').tagsinput('items').length;
//		if(numOfTags === 0){
//			$('.selectdUsersGroupsTagSection').addClass('hidden');
//			$('#numOfSelectedUsersGroups').text('');
//		}else {
//			$('#numOfSelectedUsersGroups').text(numOfTags);
//		}
//		
//		var rowsNum = $('#relateUsersGroupsToProjectsTable').DataTable().rows().count();
//
//		for(var index=0;index<rowsNum;index++){
//			var rowData = $('#relateUsersGroupsToProjectsTable').DataTable().row(index).data();
//			var $row = $('#relateUsersGroupsToProjectsTable').DataTable().row(index);
//			if(typeof e.item !== "undefined" && rowData[5] === e.item.value){
//				rowData[3] = createAssignRightsToUserCheckboxes(index, {});
//				rowData[4] = createAddUserGroupsToProjecButton();
//				$row.data(rowData).draw();
//			}
//		}
//		
//		$('#relateUsersGroupsToProjectsTable').dataTable().fnPageChange( currentPage );
//	}).tagsinput({
//		itemText : 'text',
//		itemValue : 'value'
//	});
}

function clearModals(){
//	Projects table
	createdOrEditedOrDeletedProject = false;
	
//	BBOXModal
	moveEndForMapBBOX();
	$('#chooseAreaMessage').addClass('hidden');
	coordsObjectToBeSendToDSS = {};
	goBackwardsToBBOXModal = false;
	
//	LayersModal
	if(jstreeIsLoaded){
//		$('#treeviewLayers').jstree().deselect_all(true);
		$('#treeviewLayers').jstree().destroy();
		LISTLAYERSFLAG = true;
		jstreeIsLoaded = false;
		layersObject.jstreeLayers = [];
		layersObject.skipped = false;
	}
	removeLayersFromMapModal();
	$('#ChooseLayersModal').removeClass('clicked');
	$('#fetchAllAvailableLayers').removeClass('clicked');
	layersIntersectingWithCurrentBBOX = {};
	$('#fetchAllAvailableLayers').prop('checked',true);
	allLayers = [];
	layersIntersectingWithCurrentBBOX = [];
	
//	Users or groups modals
	$('.groupOrIndividual').removeClass('clicked');
	
//	RelateUsersModal
	if($.fn.DataTable.isDataTable( '#relateUsersToProjectsTable' )){
		$relateUsersToProjectsTable.clear();
		$relateUsersToProjectsTable.destroy();
	}
	usersArray = [];
	usersAndRightsArray = [];
//	$('.usersTagsinput').tagsinput('removeAll');
	$('.selectdUsersTagSection').addClass('hidden');
	
//	relateUsersGroupsToProjectsTable
	if($.fn.DataTable.isDataTable( '#relateUsersGroupsToProjectsTable' )){
		$('#relateUsersGroupsToProjectsTable').DataTable().clear();
		$('#relateUsersGroupsToProjectsTable').DataTable().destroy();
	}
	usersArray = [];
	$('.assignedUsersTagsinput').tagsinput('removeAll');
	$('.selectdUsersGroupsTagSection').addClass('hidden');
	
//	ProjectNameAndDescriptionModal
	$("#projectName").val('');
	$("#projectDescription").val('');
	$('#make-project-public').prop('checked', false);
	projectNameAndDescriptionObject = {};
	$('#projectNameDescriptionValidation').addClass('hidden');
	$('#numOfSelectedUsers').text('');
	$('#projectAlreadyExists').text('');
	$('#projectAlreadyExists').addClass('hidden');
	
	if(EDITMODE){
		EDITMODE_USER_PRESSED_CANCEL = true;
		
		if(layerNamesObject.length !== 0){
			for(var i in layersByName){
				layersMap.removeLayer(layersByName[i]);
			}
			layerNamesObject = [];
			layersByName = {};
		}
		

		EDITMODE_USER_PRESSED_CANCEL = false;
	}
	
	fromUsersAssignment = false;
	fromUsersGroupsAssignment = false;
	fromPickUsersOrUsersGroup = false;
}

function retrieveUsersAndGroups(){
	var url = theResourceURL;
	var callback = function(data){
		var dataObject = {};
		JSON.parse(data);
		
		if(data !== null || data !== ''){
			dataObject = JSON.parse(data);
			
			var usersArray = dataObject.users;
			
			$.each(usersArray, function(index, value) {
				for(var userData in value){
//					console.log(value[userData]);
				}
			});
		}
	};
	
	var theData = {};
	theData[nameSpace + 'usersAndGroups'] = true;
	
	$.ajax(
			{
				url: url,
				type: 'post',
				datatype:'json',
				data: theData,
				success: function(data){
					callback(data);
				},
				error: function (xhr, ajaxOptions, thrownError) {
//					alert();
					$('#AssignUsersToProjectGroupsModal').modal('hide');
				}
			}
		);
}

function nameAndDescriptionModalEvents(){
	$('#goBackToRelateUsersToProjectModal').off().on('click', function(){
		$('#projectNameAndDescriptionModal').modal('hide');
		if(fromUsersAssignment){
			$('#RelateUsersToProjectModal').modal('show');
		}else if(fromUsersGroupsAssignment){
			$('#RelateUsersGroupsToProjectModal').modal('show');
		}else if(fromPickUsersOrUsersGroup){
			$('#assignGroupOrIndividualModal').modal('show');
		}
	});
	
	$('#CreateProjectButton').off().on('click', function(){
		var projectName = $.trim($("#projectName").val());
		var projectDescription = $.trim($("#projectDescription").val());
		projectNameAndDescriptionObject = {};
		projectNameAndDescriptionObject.name = projectName;
		projectNameAndDescriptionObject.description = projectDescription;
		
		if(projectName === ''){
			$('#projectNameDescriptionValidation').removeClass('hidden');
			return;
		}
//		sendDataToServer set globalObject
		
		var NewProjectData = setProjectData();
		var url;
		if(EDITMODE){
			url = projectUpdateURL;
		}else{
			url = projectCreateURL;
		}
		
		showSpinner();
		
		$.ajax({ 
		url: url,
        type: 'post',
        dataType : 'json',
        contentType: 'application/json',
        data: JSON.stringify(NewProjectData),
        success: function(serverResponse){
        	hideSpinner();
        	if(serverResponse.status === "Success"){
        		var date;
        		var extent;
        		
        		createdOrEditedOrDeletedProject = true;
    			
        		if(EDITMODE){
        			if(typeof coords.extent === "undefined"){
        				extent = editeModeCoordinates;
        			}else{
        				extent = coords.extent;//mapBBOX.getView().calculateExtent(mapBBOX.getSize());
        			}
//             		extent = extent.toString().replace(/\./g,"d");
//             		extent = extent.toString().replace(new RegExp(",",'g'),"c");
        			
        			date = projectDateToBeEdited;
        			
        			loadProjectObject.projectName = projectName;
        			loadProjectObject.date = date;
        			loadProjectObject.extent = extent;
        			loadProjectObject.projectID = userinfoObject.projectId;
        			loadProject(loadProjectObject);
        			
        			$('#projectNameAndDescriptionModal').modal('hide');
        			userinfoObject.projectName = projectName;
        			$table.DataTable().ajax.reload();
        			extentForCenteringDSSMap = extent;
//            		projectName = encodeURIComponent(projectName);
//            		window.location.href = createLink(renderURL, "dss", "&projectName=" + projectName + "~~" + "&projectDate="+date + "~~" + "&projectExtent="+extent + "~~");
        		}else{
//        			extent = coords.extent;
//             		extent = extent.toString().replace(/\./g,"d");
//             		extent = extent.toString().replace(new RegExp(",",'g'),"c");
//            		projectName = encodeURIComponent(projectName);
        			extent = coords.extent;//mapBBOX.getView().calculateExtent(mapBBOX.getSize());
            		date = new Date().getTime();
        			
        			loadProjectObject.projectName = projectName;
        			loadProjectObject.date = date;
        			loadProjectObject.extent = extent;
        			loadProjectObject.projectID = serverResponse.response;
        			loadProject(loadProjectObject);

        			extentForCenteringDSSMap = extent;
        			$('#projectNameAndDescriptionModal').modal('hide');
        			$table.DataTable().ajax.reload();
//        			window.location.href = createLink(renderURL, "dss", "&projectName=" + projectName + "~~" + "&projectDate="+date + "~~" + "&projectExtent="+extent + "~~");
        			
        			userinfoObject.projectId = serverResponse.response;
        		}
        		
        		
//        		$('#treeviewTaxonomiesLayers').html('');
        		if(!mapLayersLoaded){
        			userinfoObject.projectName = projectName;
        			retrieveAvailableLayersAndPlaceThemOnTheLeft(userinfoObject);
        		}else{
        			removeLayersFromMap();
        			userinfoObject.projectName = projectName;
        			$('#treeviewTaxonomiesLayers').jstree().deselect_all(true);
        			$('#treeviewTaxonomiesLayers').jstree().refresh();
        		}
//        		retrieveAvailableLayersAndPlaceThemOnTheLeft(userinfoObject);
    			
    			removePlugins();
    			
    			$('#layersPanel').data('layers', []);

    			updateAvailablePluginsAccordion();
        	}

        	if(serverResponse.status === "Existing"){
        		$('#projectAlreadyExists').text(serverResponse.message).removeClass('hidden');
        		$('#projectNameDescriptionValidation').addClass('hidden');
        	}

        	if(serverResponse.status === "Failure" && EDITMODE){
        		$('#projectAlreadyExists').text(serverResponse.message).removeClass('hidden');
        		$('#projectNameDescriptionValidation').addClass('hidden');
        	} else if(serverResponse.status === "Failure") {
        		hideSpinner();
            	$('.wizard').modal('hide');
            	$('#InternalServerErrorModal').modal('show');
        	}
        	
        },error: function(jqXHR, textStatus, errorThrown) {
        	hideSpinner();
        	$('.wizard').modal('hide');
        	$('#InternalServerErrorModal').modal('show');
        }
      });
		
	});
}

function initializemapForLayersModal(){
	var baseLayer2 = new ol.layer.Tile({
    	source : new ol.source.OSM()
    });
	
	layersMap = new ol.Map({
    	target: 'modalLayerMap',
        controls: ol.control.defaults({
            zoom: true,
            attribution: false,
            rotate: false
          }),
        layers: [ baseLayer2 ],
        view: new ol.View({
        	center: ol.proj.fromLonLat([22.00, 37.00]),
        	zoom: 4,
        })
    });
	layersMap.set('mapId','modalLayerMap');
	
	addControls('navcross', layersMap);
	addControls('zoomSlider', layersMap);
}

function setProjectData(){
	var projectData = {};
	
	projectData.coords = coordsObjectToBeSendToDSS;
//	if(layersObject.skipped === true){
//		projectData.layers = {};
//	}
	
	projectData.layers = layersObject;
	projectData.users = [];
	projectData.users = usersArray;
	projectData.userRights = usersAndRightsArray;
	projectData.nameAndDescriptionObject = projectNameAndDescriptionObject;
	projectData.userinfoObject = userinfoObject;
	projectData.publicProject = $('#make-project-public').prop('checked');
	
	if(EDITMODE){
		projectData.nameAndDescriptionObject.oldName =projectNameToBeEdited;
	}else{
		projectData.nameAndDescriptionObject.oldName = '';
	}
	return projectData;
}

function GroupsManipulationModalEvents(){
	$('#GroupsManipulationModal').on('hidden', function(){
		$('#manipulateProjectGroups').removeClass('clicked');
	}).on('shown', function(){
		if(! $.fn.DataTable.isDataTable( '#groupsTable' )){
			initializeGroupsTable();
		}else if(!initializeGroupNamesTableForTheFirstTime){
			$( '#groupsTable' ).DataTable().ajax.reload();
		}
		initializeGroupNamesTableForTheFirstTime = false;
	});
	
	$('#newGroupCreateGroup').off('click').on('click', function(){
		var $button = $(this);
		if($button.hasClass('createNew')){
			showSpinner();
			var url = newProjectGroupURL;
			var callback = function(data){
				hideSpinner();
				if(data.status === "Success"){
					$('.newGroupContainer').addClass('hidden');
					$button.text('New group');
					$button.removeClass('createNew');
					$('table#groupsTable').DataTable().ajax.reload();
				} else {
				}
			};
			var data = userinfoObject;
			userinfoObject.groupName = $('#groupName').val();
			AJAX_Call_POST(newProjectGroupURL, callback, userinfoObject);
			
		}else{
			$('.newGroupContainer').removeClass('hidden');
			$button.text('Create');
			$button.addClass('createNew');
			$('#groupName').val('');
		}
	});
	
	$('#groupName').off('keydown').on('keydown', function(e){
		if ( event.which == 13 ) {
			event.preventDefault();
			$('#newGroupCreateGroup').click();
		}
	});
}

function assignGroupsOrIndividualsToProject(){
	$('.showIndividualsModal').off('click').on('click', function(){
		if(! $.fn.DataTable.isDataTable( '#relateUsersToProjectsTable' )){
			initializeAssignUsersToProjectTable();	
			relateUsersToProjectsTableInitialized = false;
		}else {
			$('table#relateUsersToProjectsTable').DataTable().ajax.reload();
		}
		
//		$('#assignGroupOrIndividualModal').on('hidden', function() {
//		});
		
		$('#assignGroupOrIndividualModal').modal('hide');
		$('#RelateUsersToProjectModal').modal('show');
	});
	
	$('.showGroupsModal').off('click').on('click', function(){
		if(! $.fn.DataTable.isDataTable( 'table#relateUsersGroupsToProjectsTable' )){
			initializeAssignUsersGroupsToProjectTable();
		}else $( 'table#relateUsersGroupsToProjectsTable' ).DataTable().ajax.reload();
		
//		$('#assignGroupOrIndividualModal').on('hidden', function() {
//		});
		
		$('#assignGroupOrIndividualModal').modal('hide');
		$('#RelateUsersGroupsToProjectModal').modal('show');
		
	});
	
	$('.groupOrIndividual').on('click',function(){
		$(this).addClass('clicked');
	});
	
	$('#assignGroupOrIndividualModal').on('hidden',function(){
		$('.groupOrIndividual').removeClass('clicked');
	});
	
	$('#assignGroupOrIndividualModalBackBtn')
	.off('click').on('click', function(){
		$('#assignGroupOrIndividualModal').modal('hide');
		$('#ChooseLayersModal').modal('show');
	});
	
	$('#assignGroupOrIndividualModalNextBtn')
	.off('click').on('click', function(){
		fromUsersAssignment = false;
		fromUsersGroupsAssignment = false;
		fromPickUsersOrUsersGroup = true
		
		$('#assignGroupOrIndividualModal').modal('hide');
		$('#projectNameAndDescriptionModal').modal('show');
		
		if(EDITMODE) {
			$("#projectName").val(projectNameAndDescriptionObject.name);
			$("#projectDescription").val(projectNameAndDescriptionObject.description);
			$('#make-project-public').prop('checked', projectNameAndDescriptionObject.publicProject);
		}
	});
}

function deleteGroupModalEvents(){
	$('#OKOnDeleteGroupSureModal').off('click').on('click', function(){
		showSpinner();
		var url = deleteProjectGroupURL;
		var callback = function(data){
			hideSpinner();
			$('#onDeleteGroupSure').modal('hide');
			$( '#groupsTable' ).DataTable().ajax.reload();
			$('#GroupsManipulationModal').modal('show');
		};
		var data = userinfoObject;
		var htmlRow = $('#groupsTable .groupNamesDeleteButton.active').closest('tr')[0];
		var rowData = $('#groupsTable').DataTable().rows(htmlRow).data();
		
		var row = $('.groupNamesDeleteButton.clicked').closest('tr');
		var data = $('#groupsTable').DataTable().rows(row).data();
		var groupID = data[0][4];
		
		userinfoObject.groupName = rowData[0][0];
		
		var ProjectGroupMessenger = {};
		ProjectGroupMessenger.uio = userinfoObject;
		ProjectGroupMessenger.users = null;
		ProjectGroupMessenger.projectGroupID = groupID;
		
		
		AJAX_Call_POST(url, callback, ProjectGroupMessenger);
	});
}

function getUsersAndRights() {
	var usersAndRights = [];
	if($.fn.DataTable.isDataTable( '#relateUsersToProjectsTable' )){
		$relateUsersToProjectsTable.rows().every(function(rowIdx, tableLoop, rowLoop){
			var $checkBoxesContainer = $(this.node()).find('.allRightsContainerInTableCell');
			var userId = this.data()[7];
			var rightsObject = {};
			
			rightsObject['read'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'read');
			rightsObject['edit'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'edit');
			rightsObject['delete'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'delete');
			
			if(rightsObject['read'] === 1 || rightsObject['edit'] === 1 || rightsObject['delete'] === 1) {
				var auxObject = {};
				auxObject.id = userId;
				auxObject.rights = rightsObject;
				usersAndRights.push(auxObject);
			}
		})
	}
	
	return usersAndRights;
}

function getUsersGroupsAndRights() {
	var usersGroupsAndRights = [];
	if($.fn.DataTable.isDataTable( '#relateUsersGroupsToProjectsTable' )){
		$relateUsersGroupsToProjectsTableD.rows().every(function(rowIdx, tableLoop, rowLoop){
			var $checkBoxesContainer = $(this.node()).find('.allRightsContainerInTableCell');
			var userId = this.data()[5];
			var rightsObject = {};
			
			rightsObject['read'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'read');
			rightsObject['edit'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'edit');
			rightsObject['delete'] = checkIfRightsCheckboxIsCheckedByRightName($checkBoxesContainer, 'delete');
			
			if(rightsObject['read'] === 1 || rightsObject['edit'] === 1 || rightsObject['delete'] === 1) {
				var auxObject = {};
				auxObject.id = userId;
				auxObject.rights = rightsObject;
				usersGroupsAndRights.push(auxObject);
			}
		})
	}
	
	return usersGroupsAndRights;
}