(function() {
'use strict';

var pageState = {};


function init(contextPath, renderURL, resourceURL) {
	pageState.renderURL = renderURL;
	pageState.resourceURL = resourceURL;

	$('#rootwizard').bootstrapWizard({'tabClass': 'nav nav-tabs'});

	$( document ).ajaxStart(function() {
		$(".loaderContainer").show();
	});
	$( document ).ajaxStop(function() {
		$(".loaderContainer").hide();
	});
	
	
	$('#wfsImporter').WFSImport().WFSImport("createAsDiv");
	
	
	
	$('ul.tabUl li').on('click', function() {
		$('ul.tabUl li#blankLi').remove();
		
		$(this).attr('id');
		
		var heightOfTab = $('.tab-content #tab'+$(this).attr('id')).height();
		
		
		
		$('ul.tabUl').append('<li id="blankLi"></li>');
		$('#blankLi').css('height', heightOfTab-20);
		
		
	});
	
	
//only for this release. Is about to change on next release when the supported output format
//will not be only shape-zip. Thats the reason we hardcode id of modal and we not
//return it from widget
//	 $('#wfsImporterModal').on('show.bs.modal', function (e) {
//			$('#outputFormat').val('shape-zip');
//			$('#outputFormat').attr('readonly', true);
//	 });
	
	$('#outputFormat').val('shape-zip');
	$('#outputFormat').attr('readonly', true);	 
	
	$('.getLayers').on('click', function() {
		if ($(this).hasClass('idleMe')) return;
		
		console.log($('#url').val());
		
		console.log($('#version').val());
		if ($('#version').val().trim()!="1.0.0") {
//			&&
//			$('#version').val().trim()!="1.1.0" &&
//			$('#version').val().trim()!="2.0.0") {
			alert("Not a valid wfs version");
			return;
		}

		pageState.featureTypesToSave = [];
		var wfsRequestMessenger = {
		    	url: $('#url').val(),
		    	version: $('#version').val(),
		    	featureTypes: pageState.featureTypesToSave
		};
		
		postDataToServer(wfsRequestMessenger,  createLink(pageState.resourceURL, 'admin/import/getCapabilities'), function(response) {
			if(response.status) {
				
				$('.getLayers').addClass('idleMe');
				
				$('#url').attr('readonly', true);
				$('#version').attr('readonly', true);
				
				$('.listContainer').removeClass('hide');
				$('.clearAll').removeClass('hide');
				
				for (var i=0; i<response.data.length; i++) {
					var li = '<li>' +
     							'<label class="checkbox">' +
									'<input type="checkbox" class="checkMe" id="'+ response.data[i] +'">' + response.data[i]
								'</label>' +
							 '</li>';
					$('.listContainer ul').append(li);
				}
				

				$('.saveChecked').removeClass('hide');
	
				$('.checkMe').on('click', function() {
					$(this).attr('id');
		
					var found = false;
					for(i=0; i < pageState.featureTypesToSave.length; i++){
				        if(pageState.featureTypesToSave[i].match($(this).attr('id'))){
				        	pageState.featureTypesToSave.splice( pageState.featureTypesToSave.indexOf($(this).attr('id')), 1 );
				        	found = true;
				        	return;
				        }
				    } 
					
					if (!found)
						pageState.featureTypesToSave.push($(this).attr('id'));
					
				});
				
				
				$('.saveChecked').on('click', function() {
					

					var wfsRequestMessenger = {
					    	url: $('#url').val(),
					    	version: $('#version').val(),
					    	featureTypes: pageState.featureTypesToSave
					};
					
					postDataToServer(wfsRequestMessenger,  createLink(pageState.resourceURL, 'admin/import/storeShapeFilesForFeatureType'), function(response){
						if (response.status)
							alert("Layers stored");
						
						
					});
				});
				
				$('.clearAll').on('click', function() {
					
					$('.getLayers').removeClass('idleMe');
					$('#url').attr('readonly', false);
					$('#version').attr('readonly', false);
					
					$('.listContainer ul').empty();
					$('.listContainer').addClass('hide');
					$('.clearAll').addClass('hide');
					
					$('#url').val('');
					$('#version').val('');
					
					$('.saveChecked').addClass('hide');
					pageState.featureTypesToSave = [];
				});
				
				
			} else {
				alert(response.message);
			}
		});
		
		
		
		
		
		
		
		
		
	});
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}


function postDataToServer(json, postUrl, callback) {
	$.ajax({
		url: postUrl,
		type: 'post',
		cache: false,
	    contentType: "application/json",
	    dataType: "json",
		data: JSON.stringify(json),
		success: function(response) {
			if(callback) callback(response);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert("An error occured");
	     }
	});	
}


function createLink(url, resource, param) {
	var link;
	if (param === undefined) {
		link = url.replace('%7Burl%7D', resource).replace('%7Bparams%7D', '');
		if (link.charAt(link.length - 1) == "?")
			link = link.slice(0, -1);		
	} else link = url.replace('%7Burl%7D', resource).replace('%7Bparams%7D', param);
	return link;
}


window.Admin = {};
window.Admin.init = init;
}());