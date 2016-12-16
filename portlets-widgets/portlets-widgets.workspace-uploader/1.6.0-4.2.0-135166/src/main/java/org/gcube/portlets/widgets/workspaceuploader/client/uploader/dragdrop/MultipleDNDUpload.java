/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop;

import java.util.HashMap;
import java.util.Map;

import org.gcube.portal.clientcontext.client.GCubeClientContext;
import org.gcube.portlets.widgets.workspaceuploader.client.ConstantsWorkspaceUploader;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.HasWorskpaceUploadNotificationListener;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.WorskpaceUploadNotificationListener;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploaderListenerController;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.DialogUpload.UPLOAD_TYPE;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.WorkspaceFieldsUploadManager;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class MultipleDNDUpload.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 18, 2016
 */
public class MultipleDNDUpload extends LayoutPanel implements HasWorskpaceUploadNotificationListener {

	private String idFolder;
	private UPLOAD_TYPE type;

	// private String jsonKeys;
	private String servlet = ConstantsWorkspaceUploader.WORKSPACE_UPLOADER_SERVLET_STREAM__MODIFIED;
	private String service = ConstantsWorkspaceUploader.WORKSPACE_UPLOADER_SERVICE_MODIFIED;
	private String workspaceUtil = ConstantsWorkspaceUploader.WORKSPACE_UPLOADER_WS_UTIL_MODIFIED;

	private String folder_parent_id = ConstantsWorkspaceUploader.FOLDER_PARENT_ID;
	private String item_name = ConstantsWorkspaceUploader.ITEM_NAME;
	private String scopeGroupId = ConstantsWorkspaceUploader.CURR_GROUP_ID;

	public static final String FILE_DELEMITER = ";";
	private Widget onlyChild;

	private Map<String, WorkspaceFieldsUploadManager> fields = new HashMap<String, WorkspaceFieldsUploadManager>();
	private Map<String, String> jsonKeys = new HashMap<String, String>();
	protected WorkspaceUploaderListenerController controller = new WorkspaceUploaderListenerController();

	private String currentJsonKey;
	private boolean isLimitExceeded;


	/** The drop target css classes. */
	public static final String DROP_TARGET_CLASS = "drop_target";
	public static final String DROP_TARGET_OUTER_CLASS = "drop_target_outer";
	public static final String DROP_TARGET_INNER_CLASS = "drop_target_inner";
	public static final String DROP_TARGET_UNIQUE_CHILD_ID = "drop_target_unique_child";

	/** The drop target ids. */
	private String randomID = Random.nextInt() + "_" +Random.nextInt();
	private String dropTargetID = DROP_TARGET_CLASS +"-" + randomID;
	private String dropTargetOuterID = DROP_TARGET_OUTER_CLASS +"-"+ randomID;
	private String dropTargetInnerID = DROP_TARGET_INNER_CLASS +"-"+ randomID;
	private String dropTargetUniqueChildID = DROP_TARGET_UNIQUE_CHILD_ID +"-"+ randomID;

	public MultipleDNDUpload() {
		this.getElement().setId(dropTargetID);
		String dnd = "<div id='"+this.dropTargetOuterID+"' class='"+DROP_TARGET_OUTER_CLASS+"'><div id='"+this.dropTargetInnerID+"' class='"+DROP_TARGET_INNER_CLASS+"'></div></div>";
		GWT.log(dnd);
		HTML html = new HTML(dnd);
		this.add(html);
		scopeGroupId = GCubeClientContext.getCurrentContextId();
	}
	/**
	 * Instantiates a new multiple dnd upload.
	 *
	 * @param parentId the parent id
	 * @param uploadType the upload type
	 */
	public MultipleDNDUpload(String parentId, UPLOAD_TYPE uploadType) {
		this.getElement().setId(dropTargetID);
		String dnd = "<div id='"+this.dropTargetOuterID+"' class='"+DROP_TARGET_OUTER_CLASS+"'><div id='"+this.dropTargetInnerID+"' class='"+DROP_TARGET_INNER_CLASS+"'></div></div>";
		GWT.log(dnd);
		HTML html = new HTML(dnd);
		this.add(html);
		scopeGroupId = GCubeClientContext.getCurrentContextId();
		setParameters(parentId, uploadType);
	}


	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.LayoutPanel#onAttach()
	 */
	@Override
	protected void onAttach() {
		super.onAttach();

		if(onlyChild!=null){
			onlyChild.getElement().setId(dropTargetUniqueChildID);
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
		this.scopeGroupId = GCubeClientContext.getCurrentContextId();
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
	 * New workspace fields manager for uuid.
	 *
	 * @param uploadUUID the upload uuid
	 */
	private void newWorkspaceFieldsManagerForUUID(String uploadUUID) {
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
		WorkspaceFieldsUploadManager field = getFieldsUploadManager(uploadUUID);
		field.addNewSubmitToMonitor();
	}

	/**
	 * Creates the json key for files.
	 *
	 * @param uploadUUID
	 *            the upload uuid
	 */
	private void createJsonKeyForFiles(String uploadUUID) {
		WorkspaceFieldsUploadManager field = getFieldsUploadManager(uploadUUID);
		field.createJsonKeyForFiles();
	}


	/**
	 * Gets the json key for files.
	 *
	 * @param uploadUUID the upload uuid
	 * @return the json key for files
	 */
	private String getJsonKeyForFiles(String uploadUUID) {
		WorkspaceFieldsUploadManager field = getFieldsUploadManager(uploadUUID);
		return field.getJsonKeys();
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
	private void generateFakeUploaders(String filesSelected, String parentId, String uploadUUID) {
		WorkspaceFieldsUploadManager field = getFieldsUploadManager(uploadUUID);
		field.generateFakeUploaders(filesSelected, parentId);
	}


	/**
	 * Gets the field upload manager.
	 *
	 * @param uploadUUID the upload uuid
	 * @return the field upload manager
	 */
	private WorkspaceFieldsUploadManager getFieldsUploadManager(String uploadUUID) {
		return fields.get(uploadUUID);
	}

	/**
	 * Update json keys.
	 *
	 * @param uploadUUID
	 *            the upload uuid
	 */
	private void updateJsonKeys(String uploadUUID) {
		WorkspaceFieldsUploadManager field = getFieldsUploadManager(uploadUUID);
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
	 * Gets the drop target id.
	 *
	 * @return the dropTargetID
	 */
	public String getDropTargetID() {

		return dropTargetID;
	}

	/**
	 * Gets the drop target outer id.
	 *
	 * @return the dropTargetOuterID
	 */
	public String getDropTargetOuterID() {

		return dropTargetOuterID;
	}


	/**
	 * Gets the drop target inner id.
	 *
	 * @return the dropTargetInnerID
	 */
	public String getDropTargetInnerID() {

		return dropTargetInnerID;
	}

	/**
	 * Reset panel.
	 *
	 * @param instance
	 *            the instance
	 */
	private static native void resetPanel(MultipleDNDUpload instance) /*-{
		var drop_target_inner = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::dropTargetInnerID;
		var drop_target_outer = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::dropTargetOuterID;
		var drop_target = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::dropTargetID;
		var drop = $wnd.$('#'+drop_target)[0];
		if (drop === null || drop === undefined) {
			return;
		}

		drop.className = "over-false";
		$wnd.$('#'+drop_target_inner)[0].className = "";
		$wnd.$('#'+drop_target_outer)[0].style.display = "none";
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
	public static native void initW3CFileReader(MultipleDNDUpload instance, String fileDelimiter) /*-{

		console.log("initW3CFileReader");
		var drop_target = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::dropTargetID;
		var drop_target_inner = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::dropTargetInnerID;
		var drop_target_outer = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::dropTargetOuterID;
		var DROP_TARGET_INNER_CLASS = @org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::DROP_TARGET_INNER_CLASS;

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
			console.log("before load "+drop_target);
			var drop = $wnd.$('#'+drop_target)[0];
			$wnd.$('#'+drop_target_outer)[0].style.display = "none";

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
						$wnd.$('#'+drop_target_inner)[0].className = "";
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
						$wnd.$('#'+drop_target_inner)[0].className = "";
						$wnd.$('#'+drop_target_outer)[0].style.display = "none";
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
				$wnd.$('#'+drop_target_inner)[0].className = DROP_TARGET_INNER_CLASS;
				$wnd.$('#'+drop_target_outer)[0].style.display = "";
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
//						console.log("uploadKey: " + uploadUUID);
						//NEW WORKSPACE FIELDS
						instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::newWorkspaceFieldsManagerForUUID(Ljava/lang/String;)(uploadUUID);

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
						
						var scopeGroupId = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::scopeGroupId;
						console.log("scopeGroupId: " + scopeGroupId);

						if (idfolder === null || idfolder === undefined
								|| uploadType === null
								|| uploadType === undefined) {

							$wnd.$('#'+drop_target_inner)[0].className = "drop_target_inner_error";
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

						var filesSelected = "";

						for (i = 0; i < files.length; i++) {
							filesSelected += files[i].name + fileDelimiter;
						}
						console.log("filesSelected: " + filesSelected);

						instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::generateFakeUploaders(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(filesSelected,idfolder,uploadUUID);
						instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::createJsonKeyForFiles(Ljava/lang/String;)(uploadUUID);
						instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::updateJsonKeys(Ljava/lang/String;)(uploadUUID);
						instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::setCurrentJsonKeValue(Ljava/lang/String;)(uploadUUID);
						var jsonKeys = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::currentJsonKey;
//						var jsonKeys = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::getJsonKeyForFiles(Ljava/lang/String;)
//						var jsonKeysMap = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::jsonKeys;
//						console.log("jsonKeys: " + jsonKeys);

						var xhr = new XMLHttpRequest();

						xhr.onreadystatechange=function() {
						    if (xhr.readyState === 4){   //if complete
						        if(xhr.status === 200 || xhr.status === 202){  //either is "OK" (200) or is "Accepted" (202)
						        	console.log("status is: "+xhr.status);
						            //success
						        } else {
						           //otherwise, some other code was returned

						           if(xhr.status === 401){ //ASL session is expired
//						           	  console.log("status is 401");
						              instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::showLogoutDialog()();
						           }else{
						           	  console.log("error status: "+xhr.status);
						              instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::showAlert(Ljava/lang/String;)("Sorry, an error occurred during file/s upload. Try again");
						           }
						        }
						    }
						}

						xhr.open(opts.type, opts.url, true);

						var formdata = new FormData();
						formdata.append("client_upload_keys", jsonKeys);
						formdata.append("uploadType", uploadType);
						formdata.append("idFolder", idfolder);
						formdata.append("currGroupId", scopeGroupId);

						var overwrite = false;
						formdata.append("isOverwrite", overwrite);

						var numFolder = 0;
						for (var i = 0; i < files.length; i++) {
							var file = files[i];
							var fileSelected = files[i].name + fileDelimiter;
							if (!isFolder(file)) {
								console.log("fileSelected: " + fileSelected);
								console.log("files: " + files);
//								var jsonKeysMap = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::jsonKeys;
//								console.log("jsonKeysMap: " + jsonKeysMap);
//								instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::setCurrentJsonKeValue(Ljava/lang/String;)(uploadUUID);
//								var keyVal = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::currentJsonKey;
//								console.log("keyVal: " + keyVal);

								if(files.length == 1){
									console.log("one drag, overwrite?");
									var wsUtil = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::workspaceUtil;
									var folder_parent_id = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::folder_parent_id;
									var item_name = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::item_name;
									var scopegroupid = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::scopeGroupId;
									
									console.log("item_name: " + item_name);
									var params = folder_parent_id+"="+idfolder+"&"+item_name+"="+files[i].name+"&currGroupId="+scopeGroupId;
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

//								var xhr = new XMLHttpRequest();
//								xhr.open(opts.type, opts.url, true);
								formdata.append("isOverwrite", overwrite);
//								formdata.append("isOverwrite", overwrite);
								//*********uploadFormElement MUST BE THE LAST!!!
								formdata.append('uploadFormElement', file);
//								xhr.send(formdata);
								//					reader.readAsText(file);
							}else{
								numFolder++;
							}
						}

						xhr.send(formdata);
						instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload::addNewSubmitToMonitor(Ljava/lang/String;)(uploadUUID);

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
			$wnd.$('#'+drop_target)[0].innerHTML = 'Your browser does not support the HTML5 FileReader.';
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


	/**
	 * Show logout dialog.
	 */
	private void showLogoutDialog(){
		Window.alert("Session expired, please reload the page");
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
