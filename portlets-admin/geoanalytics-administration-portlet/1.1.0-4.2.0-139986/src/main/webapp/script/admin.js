(function() {
'use strict';

var pageState = {};

function init(contextPath, renderURL, resourceURL) {
	pageState.contextPath = contextPath;
	pageState.renderURL = renderURL;
	pageState.resourceURL = resourceURL;
	pageState.notificator = $('#notificationContainer').notification();
	
	$('#rootwizard').bootstrapWizard({'tabClass': 'nav nav-tabs'});

	$( document ).ajaxStart(function() {
		$(".loadContainer").show();
	});
	$( document ).ajaxStop(function() {
		$(".loadContainer").hide();
	});
	
	var listTemplateLayers = createLink(resourceURL, "shapes/listTemplateLayers");
	var importTsvPath = createLink(resourceURL, "importTsv");
	
	$('#tsvimporter').tsvimporter({
		mode: 				"div" 	,					// or "button" 
		templateLayersURL: 	listTemplateLayers ,
		importTsvURL:		importTsvPath,
		notificator: 	$('#notificationContainer')
	});
					/********************************************************************* initialize div's content **/
	
	$('#wfsImporter').WFSImport().WFSImport("createAsDiv");
	
	$('#tsvimporter').tsvimporter({
		mode: 				"div" 	,					// or "button" 
		templateLayersURL: 	listTemplateLayers ,
		importTsvURL:		importTsvPath 	
	});
	
	taxonomiesManagement();
	layersManagement();
	
	$('ul.tabUl li').on('click', function() {
		$('ul.tabUl li#blankLi').remove();
		
		$(this).attr('id');
		
		var heightOfTab = $('.tab-content #tab'+$(this).attr('id')).height();
		
		$('ul.tabUl').append('<li id="blankLi"></li>');
		$('#blankLi').css('height', heightOfTab-20);
		
	});
	
	$('ul.tabUl li:first').trigger('click');
//only for this release. Is about to change on next release when the supported output format
//will not be only shape-zip. Thats the reason we hardcode id of modal and we not
//return it from widget
//	 $('#wfsImporterModal').on('show.bs.modal', function (e) {
//			$('#outputFormat').val('shape-zip');
//			$('#outputFormat').attr('readonly', true);
//	 });
	
	$('#outputFormat').val('shape-zip');
	$('#outputFormat').attr('readonly', true);
	
	$('#version').val('1.0.0');
	$('#version').attr('readonly', true);
	
	
	$('.getLayers').on('click', function() {
		if ($(this).hasClass('idleMe')) return;
		
//		console.log($('#url').val());
//		
//		console.log($('#version').val());
//		if ($('#version').val().trim()!="1.0.0") {
////			&&
////			$('#version').val().trim()!="1.1.0" &&
////			$('#version').val().trim()!="2.0.0") {
//			
//			$('.error.onVersion').removeClass("hide");
//			$('#version').addClass("errorOnInput");
//			return;
//		}

		pageState.featureTypesToSave = [];
		var wfsRequestMessenger = {
		    	url: $('#url').val(),
//		    	version: $('#version').val(),
		    	version: "1.0.0",
		    	featureTypes: pageState.featureTypesToSave
		};
		
		postDataToServer(wfsRequestMessenger,  createLink(pageState.resourceURL, 'admin/import/getCapabilities'), function(response) {
			if(response.status) {
				pageState.notificator.notification("success", response.message);
				$("#notification").addClass("in");
				
				
				$('.getLayers').addClass('idleMe');
				
				$('#url').attr('readonly', true);
				$('#version').attr('readonly', true);
				
				$('.listContainer').removeClass('hide');
				$('.clearAll').removeClass('hide');
				
				for (var i=0; i<response.data.length; i++) {
					var li = '<li>' +
     							'<label class="checkbox">' +
									'<input type="checkbox" class="checkMe" id="'+ response.data[i] +'">' + response.data[i].split(":")[1]
								'</label>' +
							 '</li>';
					
					$('.listContainer ul').append(li);
				}
				$('.saveChecked').removeClass('hide');
			
			} else {
				pageState.notificator.notification("error", response.message);
				$("#notification").addClass("in");
				return;
			}
			
			$("#containerOfActions").removeClass("hide");
			
			var taxonomyTransfer = {
				active: true
			};
			postDataToServer(taxonomyTransfer,  createLink(pageState.resourceURL, 'admin/taxonomies/listTaxonomies'), function(response) {
				$("#taxonomiesDropdown li").remove();
				$("#termsDropdown li").remove();
				$('.saveChecked').addClass('idleMe');
				
				if(response.status) {
					
					for (var i=0; i<response.data.length; i++) {
						var li = '<li id="'+response.data[i]+'">'+response.data[i]+'</li>';
						$('#taxonomiesDropdownDiv ul').append(li);
					}
					
					$('#taxonomiesDropdownDiv ul li').click(function(event){
						$('#taxonomiesDropdownDiv a').html(event.target.textContent+'<span class="caret"></span>');
						pageState.taxonomyValue = event.target.textContent;
						
						var taxonomyNameTrasnfer = {
								taxonomyName: pageState.taxonomyValue,
						};
						postDataToServer(taxonomyNameTrasnfer,  createLink(pageState.resourceURL, 'admin/taxonomies/listTerms'), function(response) {
							if(response.status) {
								
								for (var i=0; i<response.data.length; i++) {
									var li = '<li id="'+response.data[i]+'">'+response.data[i]+'</li>';
									$('#termsDropdownDiv ul').append(li);
								}
								
								$('#termsDropdownDiv ul li').click(function(event){
									$('#termsDropdownDiv a').html(event.target.textContent+'<span class="caret"></span>');
									pageState.termValue = event.target.textContent;
									
									if (pageState.featureTypesToSave.length!=0 && !(pageState.taxonomyValue==undefined) && !(pageState.newTerm==undefined && pageState.termValue==undefined))
										$('.saveChecked').removeClass('idleMe');
								});
								
							} else {
								pageState.notificator.notification("error", response.message);
								$("#notification").addClass("in");
								return;
							}
						});
						
						
						if (pageState.featureTypesToSave.length!=0 && !(pageState.taxonomyValue==undefined) && !(pageState.newTerm==undefined && pageState.termValue==undefined))
							$('.saveChecked').removeClass('idleMe');
						
					});
					
					$('#newTerm').on('input', function() {
						if ($(this).val().replace(/\s/g, '').length) {
						
							pageState.newTerm = $(this).val();
							
							
							if (pageState.featureTypesToSave.length!=0 && !(pageState.taxonomyValue==undefined))
								$('.saveChecked').removeClass('idleMe');
							else 
								$('.saveChecked').addClass('idleMe');
							
						} else {
							if (pageState.featureTypesToSave.length!=0 && !(pageState.termValue==undefined))
								$('.saveChecked').removeClass('idleMe');
							else 
								$('.saveChecked').addClass('idleMe');
							
						}
					});
					
					
				} else {
					pageState.notificator.notification("error", response.message);
					$("#notification").addClass("in");
					return;
				}
				
			});
				
			
			
			$('.checkMe').on('click', function() {
				console.log($(this));
				
				if (pageState.taxonomyValue!=undefined && (!(pageState.termValue==undefined && pageState.newTermValue==undefined)) && pageState.featureTypesToSave.length!=0)
					$('.saveChecked').removeClass('idleMe');
	
				var found = false;
				for(i=0; i < pageState.featureTypesToSave.length; i++){
			        if(pageState.featureTypesToSave[i].match($(this).attr('id'))){
			        	pageState.featureTypesToSave.splice( pageState.featureTypesToSave.indexOf($(this).attr('id')), 1 );
			        	found = true;
			        	return;
			        }
			    } 
				
				if (!found) {
					pageState.featureTypesToSave.push($(this).attr('id'));
					if (pageState.taxonomyValue!=undefined && (!(pageState.termValue==undefined && pageState.newTermValue==undefined)))
						$('.saveChecked').removeClass('idleMe');
				} else {
					pageState.featureTypesToSave.splice( pageState.featureTypesToSave.indexOf($(this).attr('id')), 1 );
					
					if (pageState.featureTypesToSave.length==0) 
						$('.saveChecked').addClass('idleMe');
					else if (pageState.taxonomyValue==undefined || 
							((pageState.termValue==undefined && pageState.newTermValue==undefined)))
						$('.saveChecked').addClass('idleMe');
				}
				
			});
				
				
			$('.saveChecked').on('click', function() {
				if ($(this).hasClass('idleMe')) return;
				
				
				var tt;
				if ((pageState.newTerm!=undefined))
					tt = pageState.newTerm;
				else tt = pageState.termValue;
				
				var wfsRequestMessenger = {
				    	url: $('#url').val(),
//				    	version: $('#version').val(),
				    	version: "1.0.0",
				    	featureTypes: pageState.featureTypesToSave,
				    	taxonomyName: pageState.taxonomyValue,
				    	termName: tt
				};
				
				postDataToServer(wfsRequestMessenger,  createLink(pageState.resourceURL, 'admin/import/storeShapeFilesForFeatureType'), function(response){
					if (response.status) {
						pageState.notificator.notification("success", response.message);
						$("#notification").addClass("in");
					} else {
						pageState.notificator.notification("error", response.message);
						$("#notification").addClass("in");
					}
				});
			});
				
			$('.clearAll').on('click', function() {
				
				$('.getLayers').removeClass('idleMe');
				$('#url').attr('readonly', false);
//				$('#version').attr('readonly', false);
				
				$('.listContainer ul').empty();
				$('.listContainer').addClass('hide');
				$('.clearAll').addClass('hide');
				
				$('#url').val('');
//				$('#version').val('');
				
				$('.saveChecked').addClass('hide');
				pageState.featureTypesToSave = [];
				
				$("#containerOfActions").addClass("hide");
								
			});
			
		});
		
	});
	
	$("#version").keyup(function() {
		if (!$('.error.onVersion').hasClass("hide")) {
			$('.error.onVersion').addClass('hide');
			$('#version').removeClass("errorOnInput");
		}
	}).keydown(function() {
		if (!$('.error.onVersion').hasClass("hide")) {
			$('.error.onVersion').addClass('hide');
			$('#version').removeClass("errorOnInput");
		}
	});
	
}

function taxonomiesManagement() {
	$('#taxonomiesManagement').load(pageState.contextPath+"html/taxonomyManagement.jsp", function() {
		var data = new Object();
		data.systemOnline = true;
		showTaxonomyManagement(pageState.resourceURL, data, pageState.notificator);
	});
}

function layersManagement() {
	$('#layersManagement').load(pageState.contextPath+"html/shapeManagement.jsp", function(){
		  var data = new Object();
		  data.systemOnline = true;
		  showShapeManagement(pageState.resourceURL, pageState.contextPath, data, pageState.notificator);
	});
}

window.Admin = {};
window.Admin.init = init;
}());