(function(){
	"use strict";
	var usersListing = {
			$usersListingContainer : $('#usersListing'),
			createDataTable : function() {
				var theURL = window.config.createResourceURL('users/listUsers');
			    
			    // Create datatable
			    $('#geoadmin-users-datatable').PortletDataTable({
			    	ajax :	{
				        url : theURL,
				        type : 'POST',
				        cache : false,
				        dataType : "json",
				        beforeSend : function(xhr) {
				        	xhr.setRequestHeader("Accept", "application/json");
				        	xhr.setRequestHeader("Content-Type", "application/json");
				        	
				        	usersListing.spinner.show();
				        },
				        dataSrc : function(response) {
				        	var users = [];
				        	
					        if(response !== null && response.data !== null) {
					        	for(var prop in response.data) {
					        		users.push({
					        			principalName : response.data[prop]	
						        	});
					        	}
					        }
					        
					        return users;
				        },
				        error : function(jqXHR, exception) {
				        	usersListing.errorHandling(jqXHR, exception);
				        },
				        complete : function() {
				        	usersListing.spinner.hide();
				        },
				        timeout : 20000
					},
					columnDefs : [{
			        	title : "Name",
			        	fieldName : "principalName",
			            targets : 0,
			        }],
					checkBox : false,
			        order : [[0, "asc"]],
			    	toolbar : $('#geoadmin-users-toolbar')
			    });
			    
			    // Get Widget Instance
			    this.dataTable = $('#geoadmin-users-datatable').data("dt-PortletDataTable");
		    },
		    dataTable : null,
			errorHandling : function(jqXHR, exception) {
				window.notificator.errorHandling($("#geoadmin-users-notificator"), jqXHR, exception);
			},
			init : function() {
				$('.usersTab').one('click', function(){
					usersListing.loadTab();
				});
			},
			initUIbindings : function(){
				$(document.body).on("click", '#geoadmin-refresh-user-button', function() {
					usersListing.reloadDataTable();
			    });
			},
			getUsers : function() {
				usersListing.createDataTable();
			},
			loadTab : function() {
			    this.$usersListingContainer.load(window.config.contextPath + "modules/users/usersListing.jsp", function() {
			    	usersListing.notificator = $("#geoadmin-users-notificator");
			    	usersListing.spinner = $("#usersListing .spinner");
			    	usersListing.initUIbindings();
			    	usersListing.getUsers();
			    });
			},
			reloadDataTable : function() {
				usersListing.dataTable.refreshData();
			}
	};
	
	window.usersListing = usersListing;
})();