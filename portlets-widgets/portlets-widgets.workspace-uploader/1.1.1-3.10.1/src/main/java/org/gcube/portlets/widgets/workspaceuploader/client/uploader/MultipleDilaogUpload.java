/**
 * 
 */
package org.gcube.portlets.widgets.workspaceuploader.client.uploader;

import java.util.ArrayList;

import org.gcube.portlets.widgets.workspaceuploader.client.ConstantsWorkspaceUploader;
import org.gcube.portlets.widgets.workspaceuploader.client.DialogResult;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploaderServiceAsync;
import org.gcube.portlets.widgets.workspaceuploader.client.uploader.dragdrop.MultipleDNDUpload;
import org.gcube.portlets.widgets.workspaceuploader.shared.HandlerResultMessage;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploadFile;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem.UPLOAD_STATUS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;

/**
 * The Class MultipleDilaogUploadStream.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Oct 2, 2015
 */
public class MultipleDilaogUpload extends DialogUpload {

	public static final String FILE_DELEMITER = ";";
	private String fileUploadID;
	private MultipleDNDUpload dnd;
	public MultipleDilaogUpload instance = this;
	private String jsonKeys;
	
	private String idFolder;
	private UPLOAD_TYPE type;
	private String servlet = ConstantsWorkspaceUploader.WORKSPACE_UPLOADER_SERVLET_STREAM;

	/**
	 * Instantiates a new multiple dilaog upload stream.
	 *
	 * @param headerTitle
	 *            the header title
	 * @param parentId
	 *            the parent id
	 * @param uploadType
	 *            the upload type
	 */
	public MultipleDilaogUpload(String headerTitle, String parentId, UPLOAD_TYPE uploadType) {
		super(headerTitle, parentId, uploadType);
		
		this.type =uploadType;
		this.idFolder = parentId;
		
		fileUploadID = GenerateUUID.get(10, 16); // is tagID
		fileUpload.getElement().setId(fileUploadID);
		this.addHandlers();
//		exportGenerateFakeUploaders();
//		exportCreateJsonKeyForFiles();
//		exportAddNewSubmitToMonitor();
	}

	/**
	 * Generate new upload client keys.
	 *
	 * @param files            the files
	 * @param parentId            the parent id
	 * @return the list
	 */
	public void generateFakeUploaders(String filesSelected, String parentId) {
		
		if(filesSelected==null || filesSelected.isEmpty())
			return;
		
		String[] files = filesSelected.split(FILE_DELEMITER);
		
		// NORMALIZE FILE NAMES
		for (int i = 0; i < files.length; i++) {
			String normalizedFileName = files[i];
			if (normalizedFileName.contains("\\")) {
				files[i] = normalizedFileName.substring(normalizedFileName.lastIndexOf("\\") + 1); // remove
																				// C:\fakepath\
																				// if
																				// exists
			}
		}
		
		GWT.log("generating fake uploaders on: "+files.toString());
		fakeUploaders = new ArrayList<WorkspaceUploaderItem>(files.length);
		for (int i = 0; i < files.length; i++) {
			WorkspaceUploaderItem fakeItem = new WorkspaceUploaderItem();
			fakeItem.setClientUploadKey(GenerateUUID.get());
			fakeItem.setUploadStatus(UPLOAD_STATUS.WAIT);
			WorkspaceUploadFile fakeFile = new WorkspaceUploadFile();
			fakeFile.setFileName(files[i]);
			fakeFile.setParentId(parentId);
			fakeItem.setFile(fakeFile);
			fakeUploaders.add(fakeItem);
		}
		
		GWT.log("fakeUploaders generated: "+fakeUploaders.toString());
//		return fakeUploaders;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.widgets.workspaceuploader.client.uploader.
	 * DialogUploadStream#addHandlers()
	 */
	@Override
	protected void addHandlers() {

		// handle the post
		formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				GWT.log("onSubmitComplete");
				hide();
				// isStatusCompleted = true;
				String result = event.getResults();

				if (result == null) {
					removeLoading();
					new DialogResult(null, "Error during upload",
							"An error occurred during file upload.").center();
					return;
				}
				String strippedResult = new HTML(result).getText();
				final HandlerResultMessage resultMessage = HandlerResultMessage
						.parseResult(strippedResult);

				switch (resultMessage.getStatus()) {
				case ERROR:
					removeLoading();
					GWT.log("Error during upload " + resultMessage.getMessage());
					break;
				case UNKNOWN:
					removeLoading();
					GWT.log("Error during upload " + resultMessage.getMessage());
					break;
				case WARN: {
					GWT.log("Upload completed with warnings "
							+ resultMessage.getMessage());
					removeLoading();
					break;
				}
				case OK: {
					// removeLoading();
					// UploaderMonitor.getInstance().pollWorkspaceUploaderId(resultMessage.getMessage());

					/*
					 * Timer t = new Timer() { public void run() {
					 * GWT.log("Upload started with id: "
					 * +resultMessage.getMessage());
					 * UploaderMonitor.getInstance(
					 * ).pollWorkspaceUploaderId(resultMessage.getMessage()); }
					 * };
					 * 
					 * t.schedule(250);
					 */
				}
				}
			}
		});

		formPanel.addSubmitHandler(new SubmitHandler() {

			@Override
			public void onSubmit(SubmitEvent event) {
				GWT.log("SubmitEvent");
				addLoading();
				enableButtons(false);
				addNewSubmitToMonitor();
			}
		});

		fileUpload.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				GWT.log("browse return...");
				if (fileUpload.getFilename() == null
						|| fileUpload.getFilename().isEmpty()) {
					GWT.log("No file specified ");
					MultipleDilaogUpload.this.hide();
					return;
				}

				String[] files = null;
				String filesSelected = getFilesSelected(fileUploadID, FILE_DELEMITER);
//				GWT.log("getFilesSelected: " + filesSelected);
				files = filesSelected.split(FILE_DELEMITER);

				
				if(isLimitExceeded(files.length))
					return;
				
//				if (files.length > ConstantsWorkspaceUploader.LIMIT_UPLOADS) {
//					Window.alert("Multiple upload limit is "
//							+ ConstantsWorkspaceUploader.LIMIT_UPLOADS
//							+ " files");
//					MultipleDilaogUpload.this.hide();
//					return;
//				}



				// GENERATE NEW UPLOADERS
				generateFakeUploaders(filesSelected, parentIdentifier);
				GWT.log(fakeUploaders.toString());
				createJsonKeyForFiles();
				GWT.log(jsonKeys);

				if (jsonKeys == null) {
					Window.alert("Sorry an error occurred during file/s submit. Try again");
					return;
				}

				// ADD TO FORM PANEL
				// initJsonClientKeys();
				jsonClientKeys.setValue(jsonKeys);

				// CASE 1 FILE
				if (files.length == 1) {

					// recall: Some browser would write in
					// fileUploadField.getValue() C:\fakepath\$fileName
					final String label = files[0];
					WorkspaceUploaderServiceAsync.Util.getInstance()
							.itemExistsInWorkpaceFolder(parentIdentifier,
									label, new AsyncCallback<String>() {

										@Override
										public void onSuccess(
												final String itemId) {
											GWT.log("itemExistsInWorkpaceFolder: "
													+ itemId);
											if (itemId != null) {
												// HANDLE OWERWRITE
												if (Window
														.confirm(label
																+ " exists in folder. Overwrite?")) {
													removeItemAndSubmitForm(itemId);
													updateItemSubmitForm(itemId);
												}
											} else
												submitForm(); // ITEM does NOT
																// EXIST SO
																// SUBMIT FORM;
										}

										@Override
										public void onFailure(Throwable caught) {
											GWT.log("Sorry an error occurred on the server "
													+ caught.getLocalizedMessage()
													+ ". Please try again later");
											Window.alert(caught.getMessage());
										}

									});
				} else
					submitForm();

			}
		});
	}
	
	
	
	/**
	 * Checks if is limit exceeded.
	 *
	 * @param numbOfFiles the numb of files
	 * @return true, if is limit exceeded
	 */
	public boolean isLimitExceeded(int numbOfFiles){
		
		if (numbOfFiles > ConstantsWorkspaceUploader.LIMIT_UPLOADS) {
			Window.alert("Multiple upload limit is "
					+ ConstantsWorkspaceUploader.LIMIT_UPLOADS
					+ " files");
			MultipleDilaogUpload.this.hide();
			return true;
		}
		
		return false;
	} 
	
	/**
	 * Adds the new submit to monitor.
	 */
	public void addNewSubmitToMonitor(){
		GWT.log("addNewSubmitToMonitor...");
		for (final WorkspaceUploaderItem workspaceUploaderItem : fakeUploaders) {
			UploaderMonitor.getInstance().addNewSubmit(workspaceUploaderItem, workspaceUploaderItem.getFile().getFileName());
			setVisible(false);
			removeLoading();
			UploaderMonitor.getInstance().pollWorkspaceUploader(workspaceUploaderItem);
		}
	}

	
	/**
	 * Creates the json key for files.
	 *
	 * @return the string
	 */
	protected void createJsonKeyForFiles() {

		try {
			JSONObject productObj = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			productObj.put(ConstantsWorkspaceUploader.JSON_CLIENT_KEYS, jsonArray);
//			GWT.log("Creating json keys on fakeUploaders: "+fakeUploaders.toString());
			
			for (int i = 0; i < fakeUploaders.size(); i++) {
				WorkspaceUploaderItem file = fakeUploaders.get(i);
				JSONObject obj = new JSONObject();
				obj.put(file.getClientUploadKey(), new JSONString(file.getFile().getFileName()));
				jsonArray.set(i, obj);
			}
			
			jsonKeys = productObj.toString();
			GWT.log("updated jsonKeys: "+jsonKeys);
		} catch (Exception e) {
			GWT.log("error " + e.getMessage());
			jsonKeys = null;
		}
	}

//	/**
//	 * Export generate fake uploaders.
//	 */
//	public native void exportGenerateFakeUploaders() /*-{
//		var that = this;
//		$wnd.add = $entry(function(files,parentId) {
//			that.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload::generateFakeUploaders([Ljava/lang/String;Ljava/lang/String;)(files,parentId);
//		});
//	}-*/;
//	
//	/**
//	 * Export create json key for files.
//	 */
//	public native void exportCreateJsonKeyForFiles() /*-{
//	var that = this;
//	$wnd.add = $entry(function() {
//		that.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload::createJsonKeyForFiles()();
//	});
//	}-*/;
//	
//	/**
//	 * Export create json key for files.
//	 */
//	public native void exportAddNewSubmitToMonitor() /*-{
//	var that = this;
//	$wnd.add = $entry(function() {
//		that.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload::addNewSubmitToMonitor()();
//	});
//	}-*/;


	/**
	 * Submit form.
	 */
	@Override
	public void submitForm() {
		formPanel.submit();
		// for (final WorkspaceUploaderItem workspaceUploaderItem :
		// fakeUploaders) {
		//
		// UploaderMonitor.getInstance().addNewSubmit(workspaceUploaderItem,
		// workspaceUploaderItem.getFile().getFileName());
		// setVisible(false);
		// removeLoading();
		// //
		// UploaderMonitor.getInstance().pollWorkspaceUploaderId(workspaceUploaderItem.getClientUploadKey());
		//
		// //WAITING 0.1 SEC TO FIRST UPDATES
		// /*new Timer() {
		//
		// @Override
		// public void run() {
		// removeLoading();
		// UploaderMonitor.getInstance().pollWorkspaceUploaderId(workspaceUploaderItem.getClientUploadKey());
		//
		// }
		// }.schedule(100);*/
		// }
	}

	/**
	 * Gets the files selected.
	 *
	 * @param iFrameName the i frame name
	 * @return the files selected
	 */
	private static native String stopIFrame(final String iFrameName) /*-{
		console.log("iFrameName: " + iFrameName);
		//	   	var iframe= window.frames[iFrameName];
		var iframe = $wnd.$('iframe[name=' + iFrameName + ']', parent.document)[0];
		var iframewindow = iframe.contentWindow ? iframe.contentWindow
				: iframe.contentDocument.defaultView;
		if (iframe == null)
			console.log("iframe is null");
		else
			console.log("iframe is not null");

		if (navigator.appName == 'Microsoft Internet Explorer'
				&& iframewindow.document.execCommand) { // IE browsers
			console.log("IE browsers");
			iframewindow.document.execCommand("Stop");
		} else { // other browsers
			console.log("other browsers");
			iframewindow.stop();
		}

	}-*/;
	
	/**
	 * Gets the parent id.
	 *
	 * @return the parentId
	 */
	public String getParentId() {
		return parentIdentifier;
	}

	/**
	 * Gets the upload type.
	 *
	 * @return the uploadType
	 */
	public UPLOAD_TYPE getUploadType() {
		return uploadType;
	}
	
//	public native void exportParentId() /*-{
//		var that = this;
//		$wnd.add = $entry(function() {
//			that.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload::getParentId();
//		});
//	}-*/;
//	
//	public native void exportUploadType() /*-{
//		var that = this;
//		$wnd.add = $entry(function() {
//			that.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload::getUploadType();
//		});
//	}-*/;

	
	/**
	 * Gets the files selected.
	 *
	 * @param tagId
	 *            the tag id
	 * @param fileDelimiter
	 *            the file delimiter
	 * @return the files selected
	 */
	public static native String getFilesSelected(final String tagId,
			final String fileDelimiter) /*-{
		var count = $wnd.$("#" + tagId)[0].files.length;
		console.log(count);
		var out = "";

		for (i = 0; i < count; i++) {
			var file = $wnd.$("#" + tagId)[0].files[i];
			//	        out += file.name + fileDelimiter + file.size + fileDelimiter;
			out += file.name + fileDelimiter;
		}
		return out;
	}-*/;
	
	
	/**
	 * Inits the file reader.
	 */
	public void initFileReader() {
		initW3CFileReader(instance, MultipleDilaogUpload.FILE_DELEMITER);
	}
	
	/**
	 * Gets the files selected.
	 *
	 * @param instance the instance
	 * @param fileDelimiter the file delimiter
	 * @return the files selected
	 */
	public static native String initW3CFileReader(MultipleDilaogUpload instance, String fileDelimiter) /*-{
		console.log("initW3CFileReader");

		if (window.FileReader) {
			console.log("before load");
			var drop = $wnd.$('#drop_target')[0];
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

			addEventHandler(drop, 'drop', function(e) {
				e = e || window.event; // get window.event if e argument missing (in IE)
				if (e.preventDefault) {
					e.preventDefault();
				} // stops the browser from redirecting off to the image.
				
				var servlet = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload::servlet;

				var opts = {
					url : servlet,
					type : "POST",
					processData : false
				};
				
				var idfolder = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload::idFolder;
				console.log("idfolder: " + idfolder);
		
				var uploadType = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload::type;
				console.log("uploadType: " + uploadType);

				var dt = e.dataTransfer;
				var files = dt.files;

				var filesSelected = "";

				for (i = 0; i < files.length; i++) {
					filesSelected += files[i].name + fileDelimiter;
				}
				console.log("filesSelected: " + filesSelected);
				
//				var files = filese
//				var files = filesSelected.split(fileDelimiter);
				console.log("files: " + files);
	
				instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload::isLimitExceeded(I)(files.length);
	
				instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload::generateFakeUploaders(Ljava/lang/String;Ljava/lang/String;)(filesSelected,idfolder);

				instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload::createJsonKeyForFiles()();
				
				var jsonKeys = instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload::jsonKeys;
				console.log("jsonKeys: " + jsonKeys);

				for (var i = 0; i < files.length; i++) {
					var file = files[i];
//					var reader = new FileReader();

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

					var formdata = new FormData(); // see
					// https://developer.mozilla.org/En/XMLHttpRequest/Using_XMLHttpRequest#Using_FormData_objects
					// 'file' can be any string which you would like to associte with
					// uploaded file even for example file.type eg:
					// formdata.append(file.type, file);
					// formdata.append(file.fileName, file);

					formdata.append("isOverwrite", "false");

					// $wnd.exportCreateJsonKeyForFiles();
					//		
					// $wnd.exportAddNewSubmitToMonitor();

					formdata.append("client_upload_keys", jsonKeys);
					formdata.append("uploadType", uploadType);
					formdata.append("idFolder", idfolder);
					formdata.append('uploadFormElement', file);

					xhr.send(formdata);
					instance.@org.gcube.portlets.widgets.workspaceuploader.client.uploader.MultipleDilaogUpload::addNewSubmitToMonitor()();
//					reader.readAsText(file);
				}
				return false;
			})

			// Tells the browser that we *can* drop on this target
			addEventHandler(drop, 'dragover', cancel);
			addEventHandler(drop, 'dragenter', cancel);
		} else {
			$wnd.$('#drop_target')[0].innerHTML = 'Your browser does not support the HTML5 FileReader.';
		}
	}-*/;
}
