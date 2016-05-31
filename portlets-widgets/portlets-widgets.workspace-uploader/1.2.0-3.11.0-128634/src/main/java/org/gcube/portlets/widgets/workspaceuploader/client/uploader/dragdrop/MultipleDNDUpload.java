/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop;

import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.widgets.workspaceuploader.client.ConstantsWorkspaceUploader;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.HasWorskpaceUploadNotificationListener;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.WorskpaceUploadNotificationListener;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploaderListenerController;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.DialogUpload.UPLOAD_TYPE;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.WorkspaceFieldsUploadManager;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class MultipleDNDUpload.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 28, 2015
 */
public class MultipleDNDUpload extends LayoutPanel implements
		HasWorskpaceUploadNotificationListener {

	// private WorkspaceFieldsUploadManager fields;
	private String idFolder;
	private UPLOAD_TYPE type;

	// private String jsonKeys;
	private String servlet = ConstantsWorkspaceUploader.WORKSPACE_UPLOADER_SERVLET_STREAM;
	private String service = ConstantsWorkspaceUploader.WORKSPACE_UPLOADER_SERVICE;
	private String workspaceUtil = ConstantsWorkspaceUploader.WORKSPACE_UPLOADER_WS_UTIL;

	private String folder_parent_id = ConstantsWorkspaceUploader.FOLDER_PARENT_ID;
	private String item_name = ConstantsWorkspaceUploader.ITEM_NAME;

	public static final String FILE_DELEMITER = ";";
	private Widget onlyChild;

	private Map<String, WorkspaceFieldsUploadManager> fields = new HashMap<String, WorkspaceFieldsUploadManager>();
	private Map<String, String> jsonKeys = new HashMap<String, String>();
	protected WorkspaceUploaderListenerController controller = new WorkspaceUploaderListenerController();

	private String currentJsonKey;
	private boolean isLimitExceeded;

	/**
	 * Instantiates a new DND file reader.
	 */
	public MultipleDNDUpload() {
		this.getElement().setId("drop_target");
		HTML html = new HTML(
				"<div id=\"drop_target_outer\"><div id=\"drop_target_inner\"></div></div>");
		html.getElement().setClassName("container-drop_target");
		this.add(html);

		// ScriptInjector.fromUrl("workspaceuploader/dndhtmlfileupload.js")
		// .setWindow(ScriptInjector.TOP_WINDOW).inject();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.LayoutPanel#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		if(onlyChild!=null){
			onlyChild.getElement().setId("drop_target_only_child");
		}
	}

	/**
	 * Adds the unique container. You must add a unique panel in order to DND
	 * works fine.
	 *
	 * @param w
	 *            the w
	 */
	public void addUniqueContainer(Widget w) {

		if (onlyChild != null) {
			try {
				remove(onlyChild);
			} catch (Exception e) {
			}
		}
		onlyChild = w;
//		onlyChild.getElement().setId("drop_target_only_child");
		super.add(onlyChild);
	}

	/**
	 * Sets the parameters.
	 *
	 * @param parentId
	 *            the parent id
	 * @param uploadType
	 *            the upload type
	 */
	public void setParameters(String parentId, UPLOAD_TYPE uploadType) {
		this.idFolder = parentId;
		this.type = uploadType;
	}

	/**
	 * Sets the visible child.
	 *
	 * @param bool
	 *            the new visible child
	 */
	public void setVisibleOnlyChild(boolean bool) {
		if (onlyChild != null) {
			GWT.log("setVisibleOnlyChild: "+bool );
//			onlyChild.setVisible(bool);
			if(bool)
				onlyChild.getElement().getStyle().setOpacity(1.0);
			else
				onlyChild.getElement().getStyle().setOpacity(0.2);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.google.gwt.user.client.ui.Widget#onLoad()
	 */
	@Override
	protected void onLoad() {
		super.onLoad();
		initW3CFileReader(this, FILE_DELEMITER);
	}

	/**
	 * Inits the workspace fields.
	 *
	 * @param uploadUUID
	 *            the upload uuid
	 */
	private void initWorkspaceFields(String uploadUUID) {
		fields.put(uploadUUID, new WorkspaceFieldsUploadManager());
	}


	/**
	 * Limit is exceeded.
	 *
	 * @param numbOfFiles the numb of files
	 * @return true, if successful
	 */
	private boolean limitIsExceeded(int numbOfFiles) {
		GWT.log("# of items dropped: " + numbOfFiles);
		if (numbOfFiles > ConstantsWorkspaceUploader.LIMIT_UPLOADS) {
			Window.alert("Multiple upload limit is "
					+ ConstantsWorkspaceUploader.LIMIT_UPLOADS + " files");
			return isLimitExceeded = true;

		}

		return isLimitExceeded = false;
	}

	/**
	 * Sets the limit exceeded.
	 *
	 * @param isLimitExceeded the isLimitExceeded to set
	 */
	private void setLimitExceeded(boolean isLimitExceeded) {
		this.isLimitExceeded = isLimitExceeded;
	}

	/**
	 * Adds the new submit to monitor.
	 *
	 * @param uploadUUID
	 *            the upload uuid
	 */
	private void addNewSubmitToMonitor(String uploadUUID) {
		WorkspaceFieldsUploadManager field = getField(uploadUUID);
		field.addNewSubmitToMonitor();
	}

	/**
	 * Creates the json key for files.
	 *
	 * @param uploadUUID
	 *            the upload uuid
	 */
	private void createJsonKeyForFiles(String uploadUUID) {
		WorkspaceFieldsUploadManager field = getField(uploadUUID);
		field.createJsonKeyForFiles();
	}

	/**
	 * Generate fake uploaders.
	 *
	 * @param filesSelected
	 *            the files selected
	 * @param parentId
	 *            the parent id
	 * @param uploadUUID
	 *            the upload uuid
	 */
	private void generateFakeUploaders(String filesSelected, String parentId,
			String uploadUUID) {
		WorkspaceFieldsUploadManager field = getField(uploadUUID);
		field.generateFakeUploaders(filesSelected, parentId);
	}

	/**
	 * Gets the fields.
	 *
	 * @param uploadUUID
	 *            the upload uuid
	 * @return the fields
	 */
	private WorkspaceFieldsUploadManager getField(String uploadUUID) {
		return fields.get(uploadUUID);
	}

	/**
	 * Update json keys.
	 *
	 * @param uploadUUID
	 *            the upload uuid
	 */
	private void updateJsonKeys(String uploadUUID) {
		WorkspaceFieldsUploadManager field = getField(uploadUUID);
		jsonKeys.put(uploadUUID, field.getJsonKeys());
		// this.jsonKeys = field.getJsonKeys();
	}

	/**
	 * Sets the current json ke value.
	 *
	 * @param uploadUUID
	 *            the new current json ke value
	 */
	public void setCurrentJsonKeValue(String uploadUUID) {
		currentJsonKey = jsonKeys.get(uploadUUID);
	}

	/**
	 * Fire error.
	 *
	 * @param msg
	 *            the msg
	 */
	private void showAlert(String msg) {
		Window.alert(msg);
		resetPanel(this);
	}

	/**
	 * Reset.
	 */
	private void reset() {
		resetPanel(this);
	}

	/**
	 * Gets the upload type.
	 *
	 * @return the upload type
	 */
	public UPLOAD_TYPE getUploadType() {
		return type;
	}

	/**
	 * Reset panel.
	 *
	 * @param instance
	 *            the instance
	 */
	private static native void resetPanel(MultipleDNDUpload instance) /*-{
		var drop = $wnd.$('#drop_target')[0];

		if (drop === null || drop === undefined) {
			return;
		}

		drop.className = "over-false";
		$wnd.$('#drop_target_inner')[0].className = "";
		$wnd.$('#drop_target_outer')[0].style.display = "none";
//		$wnd.$('#drop_target_only_child')[0].style.display = "";
		instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::setVisibleOnlyChild(Z)(true);
	}-*/;

	/**
	 * Gets the files selected.
	 *
	 * @param instance
	 *            the instance
	 * @param fileDelimiter
	 *            the file delimiter
	 * @return the files selected
	 */
	public static native void initW3CFileReader(MultipleDNDUpload instance,
			String fileDelimiter) /*-{

		console.log("initW3CFileReader");

		function isFileOverwrite(url, params, msgText){
    		var xhReq = new XMLHttpRequest();
			xhReq.open("GET", url+"?"+params, false); //SYNCRONUS CALL
			xhReq.send(msgText);
			 if(xhReq.readyState==4 && xhReq.status==200) {
			 	var content = xhReq.responseText;
				console.log("responseText:" +content);
				if(content!=undefined && content=='null'){
		            console.log("isFileOverwrite response ok, responseText is null, returning null");
		            return null;
		        }else{
	            	console.log("isFileOverwrite response ok, responseText "+content);
	            	return content;
		         }

			}else if(xhReq.readyState==4 && xhReq.status!=200){
				console.log("isFileOverwrite error on status, returning null");
				return null;
			 }
		}

		function generateUUID() {
			var d = new Date().getTime();
			var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g,
					function(c) {
						var r = (d + Math.random() * 16) % 16 | 0;
						d = Math.floor(d / 16);
						return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
					});
			return uuid;
		}

		//DROP CHECK IF CONTAINS FILES
		function containsFiles(items) {
			if (items) {

				for (i = 0; i < items.length; i++) {
					entry = items[i]
					if (entry.getAsEntry) { //Standard HTML5 API
						entry = entry.getAsEntry();
					} else if (entry.webkitGetAsEntry) { //WebKit implementation of HTML5 API.
						entry = entry.webkitGetAsEntry();
					}
					if (entry.isFile) {
						console.log("entry is file");
						//Handle FileEntry
						//						readFile(entry, uploadFile);
					} else if (entry.isDirectory) {
						console.log("entry is directory");
						return false;
						//Handle DirectoryEntry
						//						readFileTree(entry, uploadFile);
					}
				}
				return true;
			}
			return false;
		}

		if (window.FileReader) {
			console.log("before load");
			var drop = $wnd.$('#drop_target')[0];
			$wnd.$('#drop_target_outer')[0].style.display = "none";

			console.log("drop is " + drop);

			function cancel(e) {
				if (e.preventDefault) {
					e.preventDefault();
				}
				return false;
			}

			function addEventHandler(obj, evt, handler) {
				if (obj.addEventListener) {
					// W3C method
					obj.addEventListener(evt, handler, false);
				} else if (obj.attachEvent) {
					// IE method.
					obj.attachEvent('on' + evt, handler);
				} else {
					// Old school method.
					obj['on' + evt] = handler;
				}
			}

			function containsFiles(event) {
				if (event.dataTransfer.types) {
					for (var i = 0; i < event.dataTransfer.types.length; i++) {
						if (event.dataTransfer.types[i] == "Files") {
							return true;
						}
					}
				}
				return false;
			}

			function isFolder(file) {

				if (file != null && !file.type && file.size % 4096 == 0) {
					return true;
				}
				return false;
			}

			addEventHandler(
					drop,
					'dragenter',
					function(e) {
						console.log('dragenter');
						drop.className = "over-true";
						$wnd.$('#drop_target_inner')[0].className = "";
//						$wnd.$('#drop_target_only_child')[0].style.display = "none";
						instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::setVisibleOnlyChild(Z)(false);

						e.preventDefault();
						if (containsFiles(e)) {
							console.log("The drag event contains files");
							// The drag event contains files
							// Do something
						} else {
							console.log("The drag event doesn't contain files");
							// The drag event doesn't contain files
							// Do something else
						}
					});

			// Event Listener for when the dragged file leaves the drop zone.
			addEventHandler(
					drop,
					'dragleave',
					function(e) {
						console.log('dragleave');
						drop.className = "over-false";
						$wnd.$('#drop_target_inner')[0].className = "";
						$wnd.$('#drop_target_outer')[0].style.display = "none";
//						$wnd.$('#drop_target_only_child')[0].style.display = "";
						instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::setVisibleOnlyChild(Z)(true);
						instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::setLimitExceeded(Z)(false);
					});

			addEventHandler(drop, 'dragover', function(e) {
				e = e || window.event; // get window.event if e argument missing (in IE)
				if (e.preventDefault) {
					e.preventDefault();
				} //
				console.log('dragover');
				drop.className = "over-true";
				$wnd.$('#drop_target_outer')[0].style.display = "";
				instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::setVisibleOnlyChild(Z)(false);
//				$wnd.$('#drop_target_only_child')[0].style.display = "none";
			});

			addEventHandler(
					drop,
					'drop',
					function(e) {
						e = e || window.event; // get window.event if e argument missing (in IE)
						if (e.preventDefault) {
							e.preventDefault();
						} // stops the browser from redirecting off to the image.

						var uploadUUID = generateUUID();
						console.log("uploadKey: " + uploadUUID);
						//NEW WORKSPACE FIELDS
						instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::initWorkspaceFields(Ljava/lang/String;)(uploadUUID);

						var servlet = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::servlet;

						var opts = {
							url : servlet,
							type : "POST",
							processData : false
						};

						var idfolder = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::idFolder;
						console.log("idfolder: " + idfolder);

						var uploadType = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::type;
						console.log("uploadType: " + uploadType);

						if (idfolder === null || idfolder === undefined
								|| uploadType === null
								|| uploadType === undefined) {

							$wnd.$('#drop_target_inner')[0].className = "drop_target_inner_error";
							var error = "Folder destionation or upload type not specified";
							instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::showAlert(Ljava/lang/String;)(error);
							return;
						}

						var dt = e.dataTransfer;
						//dt.dropEffect = 'copy';
						var files = dt.files;

						instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::limitIsExceeded(I)(files.length);

						var limitExceeded = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::isLimitExceeded;

						if(limitExceeded){
							console.log("limitExceeded, return");
							instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::reset()();
							return;
						}

						if (files.length == 0) {
							console.log("No file dragged, return");
							instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::reset()();
							return;
						}

						console.log("# of file/s: " + files.length);

						var numFolder = 0;
						for (var i = 0; i < files.length; i++) {
							var file = files[i];
							var filesSelected = files[i].name + fileDelimiter;
							if (!isFolder(file)) {

								console.log("filesSelected: " + filesSelected);
								console.log("files: " + files);
								instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::generateFakeUploaders(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(filesSelected,idfolder,uploadUUID);
								instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::createJsonKeyForFiles(Ljava/lang/String;)(uploadUUID);
								instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::updateJsonKeys(Ljava/lang/String;)(uploadUUID);

								var jsonKeysMap = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::jsonKeys;
								console.log("jsonKeysMap: " + jsonKeysMap);
								instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::setCurrentJsonKeValue(Ljava/lang/String;)(uploadUUID);
								var keyVal = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::currentJsonKey;
								console.log("keyVal: " + keyVal);
								var xhr = new XMLHttpRequest();
								//					var upload = xhr.upload;

								//					upload.fileIndex = i;
								//					upload.fileObj = file;
								//					upload.downloadStartTime = new Date().getTime();
								//					upload.currentStart = upload.downloadStartTime;
								//					upload.currentProgress = 0;
								//					upload.startData = 0;

								//					console.log("upload: " + upload.toString());
								// add listeners

								//					upload.addEventListener("progress", progress, false);
								//					upload.addEventListener("load", load, false);

								xhr.open(opts.type, opts.url, true);
								//					var boundary = "AJAX--------------" + (new Date).getTime();
								//					var contentType = "multipart/form-data; boundary=" + boundary;
								//					xhr.setRequestHeader("Content-Type", contentType);
								// Use native function(Chrome 5+ ,Safari 5+ and Firefox 4+), for dealing
								// with multipart/form-data and boundray generation

								var formdata = new FormData();
								formdata.append("client_upload_keys", keyVal);
								formdata.append("uploadType", uploadType);
								formdata.append("idFolder", idfolder);
								//OVERWRITE??
								var overwrite = false;
								if(files.length == 1){
									console.log("one drag, overwrite?");
									var wsUtil = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::workspaceUtil;
									var folder_parent_id = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::folder_parent_id;
									var item_name = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::item_name;
									console.log("item_name: " + item_name);
									var params = folder_parent_id+"="+idfolder+"&"+item_name+"="+files[i].name
									console.log("params: " + params);
									var response = isFileOverwrite(wsUtil, params, "");
									console.log("response overwrite: " + response);
									if(response!=null && response!=undefined){
										console.log("overwrite confirm?");
										if($wnd.confirm(files[i].name+" exists in folder. Overwrite?")){
											overwrite = true;
											console.log("overwrite confirmed!");
										}else{
											instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::reset()();
											return;
										}
									}
								}

								formdata.append("isOverwrite", overwrite);
								//*********uploadFormElement MUST BE THE LAST!!!
								formdata.append('uploadFormElement', file);
								xhr.send(formdata);
								instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::addNewSubmitToMonitor(Ljava/lang/String;)(uploadUUID);

								//					reader.readAsText(file);
							}else{
								numFolder++;
							}
						}

						if(numFolder>0){
							var msg;
							if(numFolder==files.length){
								msg = "Is not possible to upload a folder";
								instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::showAlert(Ljava/lang/String;)(msg);
								return;
							}

							var msg = "Ignored ";
							msg += numFolder > 1? numFolder+" folders": numFolder+" folder";
							msg+= " during upload";
							console.log(msg);
							instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::showAlert(Ljava/lang/String;)(msg);
						}
						instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::reset()();
					});
			// Tells the browser that we *can* drop on this target
			//			addEventHandler(drop, 'dragover', cancel);
			//			addEventHandler(drop, 'dragenter', cancel);
		} else {
			$wnd.$('#drop_target')[0].innerHTML = 'Your browser does not support the HTML5 FileReader.';
		}
	}-*/;

	/**
	 * Test.
	 *
	 * @param parentIdentifier the parent identifier
	 * @param name the name
	 */
	public static native void test(String parentIdentifier, String name)/*-{
		var servlet = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::service;

		var jqxhr = $.get("itemExistsInWorkpaceFolder?", function() {
			alert( "success" );
			})
			.done(function() {
			alert( "second success" );
			})
			.fail(function() {
			alert( "error" );
			})
			.always(function() {
			alert( "finished" );
			});
			// Perform other work here ...
			// Set another completion function for the request above
			jqxhr.always(function() {
			alert( "second finished" );
			});
	}-*/;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.gcube.portlets.widgets.workspaceuploader.client.
	 * WorkspaceUploadNotification.HasWorskpaceUploadNotificationListener#
	 * addWorkspaceUploadNotificationListener
	 * (org.gcube.portlets.widgets.workspaceuploader
	 * .client.WorkspaceUploadNotification.WorskpaceUploadNotificationListener)
	 */
	@Override
	public void addWorkspaceUploadNotificationListener(
			WorskpaceUploadNotificationListener handler) {
		controller.addWorkspaceUploadListener(handler);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.gcube.portlets.widgets.workspaceuploader.client.
	 * WorkspaceUploadNotification.HasWorskpaceUploadNotificationListener#
	 * removeWorkspaceUploadNotificationListener
	 * (org.gcube.portlets.widgets.workspaceuploader
	 * .client.WorkspaceUploadNotification.WorskpaceUploadNotificationListener)
	 */
	@Override
	public void removeWorkspaceUploadNotificationListener(
			WorskpaceUploadNotificationListener handler) {
		controller.removeWorkspaceUploadListener(handler);
	}
}
