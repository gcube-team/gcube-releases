(function(){
	'use strict';
	
	var workspace;
	var dom = window.dom;
	
	var dialogs = {
		workspaceDialog : $("#tea-workspace-dialog"),
		createDialog  	: $("#tea-create-dialog"),
		renameDialog 	: $("#tea-rename-dialog"),
		removeDialog 	: $("#tea-remove-dialog"),
		saveDialog 		: $("#tea-save-dialog"),
		infoDialog 		: $("#tea-info-dialog"),	
		contextMenu		: $(".tea-context-menu"),
		init : function (){
			workspace = window.workspace;
		},
		openWorkspaceDialog: function(action){
			workspace.action = action;
			
			if (dialogs.workspaceDialog.hasClass('ui-dialog-content')) {		
				dialogs.workspaceDialog.dialog("open");
			} else{
				workspace.actionText = $("<p id='actionText'></p>");
				workspace.notificator = $("<div id='tea-workspace-notifier' style='overflow-x:auto'></div>");
				
				dialogs.workspaceDialog.dialog({
					resizable: false,
					draggable: true,
					height: 400,
					width: 700, 
					modal: true,
					dialogClass: 'tea-workspace-dialog-title',
					open: function(event, ui, action) {		
						if(workspace.action === "Load"){
							workspace.actionText.text("Select an Analysis to Load");
						} else {
							workspace.actionText.text("Select a Save location");
						}
						
						dialogs.workspaceDialog.closest(".ui-dialog").find(".tea-action-button").html(workspace.action);
						dialogs.workspaceDialog.closest(".ui-dialog").find(".tea-action-button").hide();
						workspace.notificator.html('');
					},
					close: function(){
						workspace.selectedNode = null;						  
						workspace.tree.deselect_all();
						dialogs.contextMenu.hide();
					},
					buttons: [
						{
							text: "Action" ,
							class: "tea-action-button",
							click: function() {	
								if(workspace.action === "Save"){
									dialogs.openSaveAnalysisDialog();
								} else if(workspace.action === "Load"){
									workspace.loadAnalysis(workspace.selectedNode.id);
								} else if(workspace.action === "Open"){
									workspace.tree.open_node(workspace.selectedNode, false);
								}								  
							}
						},
						{
							text:"Cancel",
							class: "tea-cancel-button",
							click: function() {	
								$(this).dialog("close");
							}
						}

					  ]
				}).dialogExtend({
					minimizable : true,
					minimize : function(evt, dlg){
						$('.ui-widget-overlay').addClass('overlay-hidden');
					},
					restore : function(evt, dlg){ 
						$('.ui-widget-overlay').removeClass('overlay-hidden');
					}
				});

				var div = $("<div></div>");
				div.append(workspace.actionText);
				div.append(workspace.notificator);
				div.insertBefore("#tea-workspace-dialog");
			}
		},
		openSaveAnalysisDialog: function(){				
			if (dialogs.saveDialog.hasClass('ui-dialog-content')) {		
				dialogs.saveDialog.dialog("destroy");
			}
			
			dialogs.saveDialog.dialog({
				  resizable: false,
				  draggable: true,
				  height: "auto",
				  width: 500,
				  modal: true,
				  dialogClass: 'tea-dialog-title',
				  open: function(event, ui, action) {	
					  dialogs.resetDialog($(this));
//					  dialogs.createDialog.closest(".ui-dialog").find(".tea-action-button").focus();
				  },
				  buttons: [
					  {
						  text:"Cancel",
						  "class": "tea-cancel-button",
						  click: function() {	
							  $(this).dialog("close");
						  }
					  },
					  {
						  text: "Save" ,
						  "class": "tea-action-button",
						  click: function() {	
							  workspace.saveAnalysis(window.currentAnalysis, dom.analysisName.val(), dom.analysisDescription.val(), workspace.selectedNode.id);								  
						  }
					  }
				  ]
			});		
		},
		openCreateFolderDialog: function(){	
			
			if (dialogs.createDialog.hasClass('ui-dialog-content')) {		
				dialogs.createDialog.dialog("destroy");
			}	
			
			dialogs.createDialog.dialog({
				  resizable: false,
				  draggable: true,
				  height: "auto",
				  width: 500,
				  modal: true,
				  dialogClass: 'tea-dialog-title',
				  open: function(event, ui) {
					  dialogs.resetDialog($(this));
//					  dialogs.createDialog.closest(".ui-dialog").find(".tea-action-button").focus();
				  },
				  buttons: [
					{
						text:"Cancel",
						"class": "tea-cancel-button",
						click: function() {	
							$(this).dialog("close");
						}
					},
					{
						text: "Create",
						"class": "tea-action-button",
						click: function() {	
							workspace.createFolder(dom.folderName.val(), dom.folderDescription.val(), workspace.selectedNode.id);
						}
					}
				  ]
			});	
		},
		openRemoveFileDialog: function(){
			
			if (dialogs.removeDialog.hasClass('ui-dialog-content')) {		
				dialogs.removeDialog.dialog("destroy");
			}		
			
			dialogs.removeDialog.dialog({
				  resizable: false,
				  draggable: true,
				  height: "auto",
				  width: 400,
				  modal: true,
				  dialogClass: 'tea-remove-dialog-title',
				  open: function(event, ui) {
					  $(this).dialog('option', 'title', "Remove " + workspace.selectedNode.text);
				      $("#remove-file-name").html('<i>' + workspace.selectedNode.text + '</i>');				      
				  },
				  buttons: [
					{
						text:"Cancel",
						"class": "tea-cancel-button",
						click: function() {	
							$(this).dialog("close");
						}
					},
					{
						text:"Remove",
						"class": "tea-remove-button",
						click: function() {	
							workspace.removeFile(workspace.selectedNode.id);
						}
					}
				  ]
			});	
		},		
		openRenameFileDialog: function(){	
			
			if (dialogs.renameDialog.hasClass('ui-dialog-content')) {		
				dialogs.renameDialog.dialog('destroy');
			}	
			
			dialogs.renameDialog.dialog({
				  resizable: false,
				  draggable: true,
				  height: "auto",
				  width: 500,
				  modal: true,
				  dialogClass: 'tea-dialog-title',
				  open: function(event, ui) {
					  dialogs.resetDialog($(this));
					  $(this).dialog('option', 'title', "Rename " + workspace.selectedNode.text);		
					  
					  // Highlight till extension
					  
					  dom.fileNewName.val(workspace.selectedNode.text);
					  dom.fileNewName[0].setSelectionRange(0, dom.fileNewName.val().lastIndexOf(".")); 
					  dom.fileNewName.focus();
				  },
				  buttons: [
					{
						text:"Cancel",
						"class": "tea-cancel-button",
						click: function() {	
							$(this).dialog("close");
						}
					},
					{
						text:"Rename",
						"class": "tea-action-button",
						click: function() {									
							workspace.renameFile(workspace.selectedNode.id, dom.fileNewName.val());								
						}
					}
				  ]
			});	
			
		},
		openInfoDialog : function (info) {	
			
			if (dialogs.infoDialog.hasClass('ui-dialog-content')) {		
				dialogs.infoDialog.dialog('destroy');
			}

			dialogs.infoDialog.dialog({
				resizable: false,
				draggable: true,
				height: "auto",
				width: 500,
				modal: true,
				open: function(){
					$(this).dialog('option', 'title', "Properties of " + info.Name);
					$(".info-window").remove();
					
					var infoWindow = "<form class='form-horizontal custom-col info-window'><br>";					

					for (var key in info) {
						if (info.hasOwnProperty(key)) {
							infoWindow += 
								"<div class='control-group  custom-row'>" +
									"<label class='control-label' > " + key + "</label>" +
									"<div class='controls'>";
							if((key + "") === "Description"){							
								infoWindow += "<textarea rows='4' cols='60' style='resize:none;' readonly>" + info[key] + "</textarea>";
							}else{
								infoWindow += "<input type='text' style='float: left;' autocomplete='off' value='" + info[key] + "' readonly>";
							}
							infoWindow += "<br><br> </div> </div>";
						}
					}
					infoWindow+= "</form>";
					$(this).append(infoWindow);
				},
				dialogClass: 'tea-dialog-title',
				buttons: [{
					text:"Close",
					"class": "tea-action-button",
					click: function() {	
						$(this).dialog("close");
					}
				}]
			});				
		},
		errorHandling: function(dialog, jqXHR, exception){
			if(jqXHR.status == 422 || jqXHR.status == 400){
				dialogs.showHints(dialog, JSON.parse(jqXHR.responseText));
			} else {
				dialogs.closeDialog(dialog);
				window.noty.errorHandlingText(workspace.notificator, jqXHR, exception);
			}
		},
		showHints : function(dialog, text){
			dialog.find('.help-inline').text(text);
			dialog.find('.help-inline').addClass('tea-color-red');
		},
		clearHints : function(dialog){
			dialog.find('.help-inline').text("");
			dialog.find('.help-inline').removeClass('tea-color-red');
		},
		disableDialog : function(dialog){
			this.clearHints(dialog);
			dialog.find('input').prop("disabled", true);
			dialog.find('textarea').prop("disabled", true);
			dialog.closest(".ui-dialog").find("button").prop("disabled", true);
			dialog.find(".tea_dialog_loader").show();
		},
		enableDialog : function(dialog, text){
			dialog.find('input').prop("disabled", false);
			dialog.find('textarea').prop("disabled", false);
			dialog.closest(".ui-dialog").find("button").prop("disabled", false);
			dialog.find(".tea_dialog_loader").hide();			
		},
		resetDialog : function(dialog){
			this.clearHints(dialog);
			dialog.find('input').val('');
			dialog.find('textarea').val('');		
		},
		closeDialog : function(currentDialog){
			currentDialog.dialog("close");		
		} 
	}	
	
	window.dialogs = dialogs;
})();