function createAdminMenu(data)
{
	$('#dbOfflineModalYesButton')[0].addEventListener('click', function(ev) {
													systemOffline(data, [{func : function(){$('#dbOfflineModal').modal('hide');}}, 
													                     {func : function() { window.location.href = "/geoanalytics/admin/dbBackup"; }}
																		]);
												});
	$('#dbOfflineModalNoButton')[0].addEventListener('click', function(ev) {
																			$('#dbOfflineModal').modal('hide');
																			}
													);
	
	var inputElement = document.getElementById('btnHome');
	inputElement.addEventListener('click', function(ev)
											{
												goToHome(ev);
									 	    }
								  );

	inputElement = document.getElementById('btnUserManagement');
	inputElement.addEventListener('click', function(ev)
											{
												manageUsers(ev);
									 	    }
								  );
	 
	inputElement = document.getElementById('btnCustomerManagement');
	inputElement.addEventListener('click', function(ev)
											{
												manageCustomers(ev);
			 								}
								  );
	
	inputElement = document.getElementById('btnTaxonomyManagement');
	inputElement.addEventListener('click', function(ev)
			 								{
												manageTaxonomies(ev);
			 								}
								 );
	 
	inputElement = document.getElementById('btnShapeManagement');
	inputElement.addEventListener('click', function(ev)
			 								{
												manageShapes(ev);
			 								}
								 );
	 
	inputElement = document.getElementById('btnDocumentManagement');
	inputElement.addEventListener('click', function(ev)
										   {
												manageDocuments(ev);
										   }
								 );
	inputElement = document.getElementById('btnDataImport');
	inputElement.addEventListener('click', function(ev)
										   {
												importData(ev);
										   }
								 );
	
	inputElement = document.getElementById('btnAccounting');
	inputElement.addEventListener('click', function(ev)
										   {
												accounting(ev);
										   }
								 );
	
	inputElement = document.getElementById('btnPresentation');
	inputElement.addEventListener('click', function(ev)
										   {
												presentation(ev);
										   }
								 );
	
	if(isPresent(data.systemOnline))
	{
		inputElement = document.getElementById('btnDatabaseBackup');
		inputElement.addEventListener('click', function(ev)
											   {
													dbBackup(ev, data);
											   }
									 );
	}else $('#btnDatabaseBackup').prop('disable', true);
	
}

function setPrimaryMenuButton(id)
{
	var buttons = $('.menuBtns');
	for(var i=0; i<buttons.length; i++)
	{
		$(buttons[i]).removeClass("btn-primary");
		$(buttons[i]).removeClass("btn-default");
		$(buttons[i]).addClass("btn-default");
		$(buttons[i]).primary = false;
	}
	$('#'+id).removeClass("btn-default");
	$('#'+id).addClass("btn-primary");
	$('#'+id)[0].primary = true;
	//$('#'+id)[0].removeEventListener('click');
}

function goToHome(ev)
{
	if(ev.target.primary == true) return;
	var str = resolveLocation("admin");
	window.location.href=resolveLocation("admin");
}

function manageUsers(ev)
{
	if(ev.target.primary == true) return;
	var str = resolveLocation("admin/users");
	window.location.href=resolveLocation("admin/users");
}

function manageCustomers(ev)
{
	if(ev.target.primary == true) return;
	window.location.href=resolveLocation("admin/customers");
}

function manageDocuments(ev)
{
	if(ev.target.primary == true) return;
	window.location.href=resolveLocation("admin/documents");
}

function manageShapes(ev)
{
	if(ev.target.primary == true) return;
	window.location.href = resolveLocation("admin/shapes");
}


function manageTaxonomies(ev)
{
	if(ev.target.primary == true) return;
	window.location.href = resolveLocation("admin/taxonomies");
}

function importData(ev)
{
	if(ev.target.primary == true) return;
	window.location.href = resolveLocation("admin/import");
}

function accounting(ev)
{
	if(ev.target.primary == true) return;
	window.location.href = resolveLocation("admin/accounting");
}

function presentation(ev)
{
	if(ev.target.primary == true) return;
	window.location.href = resolveLocation("admin/presentation");
}

function dbBackup(ev, data)
{
	if(ev.target.primary == true) return;
	if(!isPresent(data.systemOnline)) return;
	if(data.systemOnline == true)
		$('#dbOfflineModal').modal('show'); 
	else
		window.location.href = resolveLocation("dbBackup");
}

function disableMenuButtons()
{
	$('.menuBtns').prop('disabled', true);
}

function enableMenuButtons()
{
	$('.menuBtns').prop('disabled', false);
}

function resolveLocation(loc)
{
	var index = window.location.pathname.lastIndexOf("/");
	var last = index != -1 ? window.location.pathname.substring(index + 1, window.location.pathname.length) : "";
	
	if(last == "admin") return loc;
	else return "../"+loc;
}

function systemOffline(data, postProcessingFuncs)
{
	//if(!data.systemOnline)
	//{
		var toggleButton = $("#systemToggleBtn");
		if(toggleButton) toggleButton.prop('disabled', true);
		$("#dbOfflineModalYesButton").prop('disabled', true);
		$("#dbOfflineModalNoButton").prop('disabled', true);
		
		var origText = $('#dbOfflineModalBody').html();
		$('#dbOfflineModalBody').html(origText + "<p>Please wait...</p>");
		disableMenuButtons();
		
		var request = $.ajax({ 
	         url : "/systemOffline", 
	         type : "post", 
	         
	         success: 	function(resp)
	         		  	{
	        	 			if(resp.error == true)
	        	 				alert("An error has occurred. System " + (resp.status ? "remains offline" : "is now online"));
	        	 			else data.systemOnline = resp.status;
	        	 			
	        	 			applyFuncs(postProcessingFuncs, resp);
	         		  	},
	         		  	
	         error: 	function(jqXHR, textStatus, errorThrown)
	         			{
	        	 			alert("The following error occured: " + textStatus, errorThrown);
	         			},
	         complete: function(jqXHR, textStatus)
	         		   {
	        	 			enableMenuButtons();
	        	 			toggleButton = $("#systemToggleBtn");
	        	 			if(toggleButton) toggleButton.prop('disabled', false);
	        	 			$('#dbOfflineModalBody').html(origText);
	        	 			$("#dbOfflineModalYesButton").prop('disabled', false);
	        	 			$("#dbOfflineModalNoButton").prop('disabled', false);
	         		   }
	       }); 
	//}
	//window.location.reload(true); 
}