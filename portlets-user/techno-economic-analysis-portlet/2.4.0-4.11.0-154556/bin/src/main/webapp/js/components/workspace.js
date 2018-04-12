(function() {
	'use strict';
	
	var dom = window.dom;
	var dialogs = window.dialogs;
	var noty = window.noty;
	
	var workspace = {
		config : null,
		tree : null,
		selectedNode : null,
		action : null,
		loading: false,
		createWorkspace : function(){
			var getFoldersUrl = this.config.getFoldersUrl;
			var getWorkspaceUrl = this.config.getWorkspaceUrl;
			var currentUrl = null;
			var currentFolderText = null;
			
			$('#tea-workspace').jstree({
		    	plugins : [	"search","sort", "types", "unique"],
		 		core: {
					check_callback: true,	
		 			multiple : false,
					data : {
					    url : function (node){
					    	currentUrl = node.id === "#" ? getWorkspaceUrl : getFoldersUrl;					    	
					    	return currentUrl;
					    },
						dataType : "json",
						data : function (node) {
							currentFolderText = node.text;
					    	return { folderId : node.id };
					    },
					    beforeSend : function(){
					    	if(currentUrl === getWorkspaceUrl){
					    		$(".jstree-icon.jstree-ocl").css("display", "inline");
					    	}
					    	workspace.disableWorkspace();					    	
					    },
					    complete : function(){					    	
					    	workspace.enableWorkspace();
					    },
					    success : function(data){
					    	if(currentUrl === getWorkspaceUrl){
					    		$(".jstree-icon.jstree-ocl").css("display", "inline-block");
					    	}
					    },
					    error: function (jqXHR, exception){
					    	if(currentUrl === getWorkspaceUrl){
						    	$(".jstree-anchor").empty();
						    	$(".jstree-anchor").html('<i class="jstree-icon jstree-themeicon-hidden"></i>Failed to load Workspace. Please try again later</a>');
					    	}else{
					    		noty.showText(workspace.notificator,"Failed to open folder \"" + currentFolderText + "\"" ,"error");	
					    	}
					    },
					    timeout: 20000
					}
				},
				types : {
					folder : {
						icon : "folder-icon"
					},
					default : {
						icon : " empty-file-icon"
					},
					"application/pdf" : {
						icon : "pdf-icon"
					},
					"application/json" : {
						icon : "fa fa-lg fa-file-code-o"
					},
					"text/plain" : {
						icon : "text-icon"
				  	},
					"analysis" : {
						icon : "chart-icon"
				  	},
				  	"application/vnd.openxmlformats-officedocument.presentationml.presentation" : {
				  		icon : "powerpoint-icon"
				  	},
				  	"application/vnd.ms-powerpoint" : {
				  		icon : "powerpoint-icon"
				  	},
				  	"application/vnd.openxmlformats-officedocument.wordprocessingml.document" : {
						icon : "word-icon"
				  	},
					"application/msword" : {
						icon : "word-icon"
					},
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : {
						icon : "excel-icon"
					},
					"image/png" : {
						icon : "image-icon"
					},
					"image/jpeg" : {
						icon : "image-icon"
					},
					"application/zip" : {
						icon : "archive-icon"
					},
					"application/x-rar-compressed" : {
						icon : "archive-icon"
					},
					"text/csv" : {
						icon : "csv-icon"
					},
					"war" : {
						icon : "war-icon"
					},
					"VRE/Folder" : {
						icon : "fa fa-lg fa-users"
					}
				} 
			}).on('contextmenu', '.jstree-anchor', function (e) {
				e.preventDefault();					 
				
				workspace.selectedNode = workspace.tree.get_node(e.target);
				
				if(!workspace.loading && typeof workspace.selectedNode.id !== "undefined"){
					$(".tea-context-menu").show();
					$(".tea-context-menu").offset({left:e.pageX, top:e.pageY});					
					
					$(".remove-file").show();
					$(".rename-file").show();
					$(".info").show();
					if(workspace.selectedNode.type === "folder" || workspace.selectedNode.type === "VRE/Folder"){
						$('.create-folder').show();
						$('.refresh').show();
						if(workspace.tree.get_parent(workspace.selectedNode) == "#" || workspace.selectedNode.type === "VRE/Folder"){
							$(".remove-file").hide();
							$(".rename-file").hide();			
						}						
					}else {
						$('.create-folder').hide();
						$('.refresh').hide();
					}	
				}
			}).on('click', '.jstree-anchor', function (e) {
				workspace.selectedNode = workspace.tree.get_node(e.target);
				
				$("#tea-workspace-dialog").closest(".ui-dialog").find(".tea-action-button").show();
				
				if(workspace.action == "Load" || workspace.action == "Open"){
					if(workspace.selectedNode.type === "analysis"){
						workspace.action = "Load";					
					}else if(workspace.selectedNode.type === "folder" || workspace.selectedNode.type === "VRE/Folder")	{
						workspace.action = "Open";	
					}else{
						$("#tea-workspace-dialog").closest(".ui-dialog").find(".tea-action-button").hide();
					}
				}else{
					if(workspace.selectedNode.type === "folder"){
						workspace.action = "Save";					
					} else {
						$("#tea-workspace-dialog").closest(".ui-dialog").find(".tea-action-button").hide();
					}
				}
				
				$("#tea-workspace-dialog").closest(".ui-dialog").find(".tea-action-button").html(workspace.action);
			});
			
			workspace.tree = $('#tea-workspace').jstree(true);	
		 	
			$("body").click( function(e)  {
				if(e.target.className !== "tea-context-menu"){
					$(".tea-context-menu").hide();
				}
			});
		},
		init : function(config) {		
			this.config = config;
			this.createWorkspace();	
			this.action = null;
			
			$(".save-analysis").click(function() {	
				if(window.currentAnalysis != null){
					dialogs.openWorkspaceDialog("Save");
				}else{
					$("#tea-message-dialog").dialog({
						  resizable: false,
						  draggable: true,
						  height: "auto",
						  width: 500,
						  modal: true,
						  dialogClass: 'tea-dialog-title',
						  buttons: [
							{
								text:"OK",
								"class": "tea-action-button",
								click: function() {	
									$(this).dialog("close");
								}
							}
						  ]
					});	
				}
				return false;
			});	
			
			$(".load-analysis").click(function() {		
				dialogs.openWorkspaceDialog("Load");
				return false;
			});		
			
			$('.tea-context-menu .create-folder').click(function() {	
				dialogs.openCreateFolderDialog();
			});
			
			$('.tea-context-menu .remove-file').click(function() {
				dialogs.openRemoveFileDialog();
			});
			
			$('.tea-context-menu .rename-file').click(function() {
				dialogs.openRenameFileDialog();
			});
			
			// Remove files by pressing "Delete Key"
			
			$('html').keyup(function(e){
			    if(e.keyCode == 46 && workspace.tree.get_node(workspace.selectedNode.id) != null) {
			    	if(workspace.tree.get_parent(workspace.selectedNode) != "#"){
				    	if ($('#tea-workspace-dialog').dialog('isOpen') === true) {
				    	   if($('.tea-dialog:visible').length === 1){
				    		   dialogs.openRemoveFileDialog();
				    	   }
				    	} 
			    	}
			    }
			});
			
			$('.tea-context-menu .refresh').click(function() {
				workspace.tree.refresh(workspace.selectedNode);	
			});
			
			$('.tea-context-menu .info').click(function() {
				workspace.getInfo(workspace.selectedNode.id);	
			});	
		},
		createFolder : function(folderName, folderDescription, destinationFolderId){				
			var resourceUrl = this.config.createFolderUrl;
			var dialog = dialogs.createDialog;
			
			var data = { 
				folderName : folderName,
				folderDescription : folderDescription,
				destinationFolderId : destinationFolderId
			};	
			
			var onSuccessCallback = function (data){
				dialogs.closeDialog(dialog);
			    workspace.tree.create_node(data.parent , data);	
				workspace.tree.open_node(destinationFolderId, function () {
					workspace.tree.select_node(data.id)
			    });			
				noty.showText(workspace.notificator, "Folder \"" + folderName + "\" has been created successfully!", "success");
			};	
			
			var onErrorCallback = function (jqXHR, exception) {
				dialogs.errorHandling(dialog, jqXHR, exception);
			};
			
			var beforeSendCallback = function (data){
				dialogs.disableDialog(dialog);
			};
			
			var completeCallback = function (jqXHR, exception) {
				dialogs.enableDialog(dialog);
			};		
			
			window.ajax.post(data, resourceUrl, onSuccessCallback, onErrorCallback, beforeSendCallback, completeCallback);
		},
		removeFile : function(fileId){
			var resourceUrl = this.config.removeFileUrl;
			var dialog = dialogs.removeDialog;
			var fileName = workspace.tree.get_node(fileId).text;
			
			var data = { fileId : fileId };	
			
			var onSuccessCallback = function (data){
				workspace.tree.delete_node(data);
				workspace.selectedNode = null;
				noty.showText(workspace.notificator,"File \"" + fileName + "\" has been removed successfully!", "success");
			};			
			
			var onErrorCallback = function (jqXHR, exception) {
				dialogs.errorHandling(dialog, jqXHR, exception);
			};
			
			var beforeSendCallback = function (data){
				dialogs.disableDialog(dialog);
			};
			
			var completeCallback = function (jqXHR, exception) {
				dialogs.enableDialog(dialog);
				dialogs.closeDialog(dialog);
			};
			
			window.ajax.post(data, resourceUrl, onSuccessCallback, onErrorCallback, beforeSendCallback, completeCallback);
		},
		renameFile : function(fileId, fileNewName){
			var resourceUrl = this.config.renameFileUrl;
			var dialog = dialogs.renameDialog;
			var fileName = workspace.tree.get_node(fileId).text;

			var data = { 
				fileId 		: fileId,
				fileNewName : fileNewName
			};				
			
			var onSuccessCallback = function (data){
				dialogs.closeDialog(dialog);
				workspace.tree.rename_node(fileId, fileNewName);
				noty.showText(workspace.notificator,"File \"" + fileName + "\" has been renamed successfully to \"" + fileNewName + "\"", "success");
			};
			
			var onErrorCallback = function (jqXHR, exception) {
				dialogs.errorHandling(dialog, jqXHR, exception);
			};
			
			var beforeSendCallback = function (data){
				dialogs.disableDialog(dialog);
			};
			
			var completeCallback = function (jqXHR, exception) {
				dialogs.enableDialog(dialog);
			};
			
			window.ajax.post(data, resourceUrl, onSuccessCallback, onErrorCallback, beforeSendCallback, completeCallback);			
		},
		saveAnalysis : function(analysis, analysisName, analysisDescription, destinationFolderId){ 		
			var resourceUrl = this.config.saveAnalysisUrl;
			var dialog = dialogs.saveDialog;
			
			var data = {
				analysis : JSON.stringify(analysis),
				analysisName : analysisName,
				analysisDescription : analysisDescription,
				destinationFolderId : destinationFolderId,
			};
			
			var onSuccessCallback = function (data){
				dialogs.closeDialog(dialog);			
			    workspace.tree.create_node(data.parent , data);	
				workspace.tree.open_node(destinationFolderId, function () {
					workspace.tree.select_node(data.id)
			    });	
				noty.showText(workspace.notificator,"Analysis \"" + analysisName + "\" has been saved successfully!" ,"success");				
			};
			
			var onErrorCallback = function (jqXHR, exception) {
				dialogs.errorHandling(dialog, jqXHR, exception);
			};
			
			var beforeSendCallback = function (data){
				dialogs.disableDialog(dialog);
			};
			
			var completeCallback = function (jqXHR, exception) {
				dialogs.enableDialog(dialog);
			};
			
			window.ajax.post(data, resourceUrl, onSuccessCallback, onErrorCallback, beforeSendCallback, completeCallback);			
		},		
		loadAnalysis : function(analysisId){
			var resourceUrl = this.config.loadAnalysisUrl;
			var analysisName = workspace.tree.get_node(analysisId).text;			
			
			var data = {
				analysisId : analysisId
			};
			
			var beforeSendCallback = function (data){
				window.currentAnalysis = null;
				workspace.tree.get_node(analysisId, true).addClass("jstree-loading");
		    	workspace.disableWorkspace();
			};
			
			var completeCallback = function (jqXHR, exception) {
				dialogs.closeDialog(dialogs.workspaceDialog);
				workspace.tree.get_node(analysisId, true).removeClass("jstree-loading");
		    	workspace.enableWorkspace();
			};
			
			var onSuccessCallback = function (data){				
				dom.resetValidationHints();
				
				window.analytics.showParameters(data.parameters);
			    window.analytics.showResult(data);				    
			    window.currentAnalysis = data;				    
			    
			    var notyDom = dom.moveWorkspaceButtonsToNoty(analysisName, data.date);
				noty.showNoty(window.notificator, notyDom, "warning", false);
				$('.techno-economic-analysis-portlet #tea-info-container').hide();
			};
			
			var onErrorCallback = function (jqXHR, exception) {
				noty.errorHandlingNoty(window.notificator, jqXHR, exception);
			};			
			
			window.ajax.get(data, resourceUrl, onSuccessCallback, onErrorCallback, beforeSendCallback, completeCallback);
		},		
		getInfo: function(fileId){
			var resourceUrl = this.config.getInfoUrl;
			var dialog = dialogs.infoDialog;
			
			var data = {
				fileId : fileId
			};
			
			var beforeSendCallback = function (data){
				workspace.tree.get_node(fileId, true).addClass("jstree-loading");
			};
			
			var completeCallback = function (jqXHR, exception) {
				workspace.tree.get_node(fileId, true).removeClass("jstree-loading");
			};
			
			var onSuccessCallback = function (data){
				dialogs.openInfoDialog(data);
			};
			
			var onErrorCallback = function (jqXHR, exception) {
				dialogs.errorHandling(dialog, jqXHR, exception);
			};			
			
			window.ajax.get(data, resourceUrl, onSuccessCallback, onErrorCallback, beforeSendCallback, completeCallback);
		},
		disableWorkspace : function(){
	    	workspace.loading = true;	
	    	dialogs.workspaceDialog.closest(".ui-dialog").find("button").prop("disabled", true);			
		},
		enableWorkspace : function(){
	    	dialogs.workspaceDialog.closest(".ui-dialog").find("button").prop("disabled", false);
	    	workspace.loading = false;			
		}
	};	

	window.workspace = workspace;
	dialogs.init();
})();