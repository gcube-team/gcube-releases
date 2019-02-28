/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.client.uploader;

import java.util.ArrayList;
import java.util.Arrays;

import org.gcube.portlets.widgets.workspaceuploader.client.ConstantsWorkspaceUploader;
import org.gcube.portlets.widgets.workspaceuploader.client.DialogResult;
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
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.DialogBox#show()
	 */
	@Override
	public void show() {
		super.show();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.PopupPanel#center()
	 */
	@Override
	public void center() {
		super.center();
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
				files[i] = normalizedFileName.substring(normalizedFileName.lastIndexOf("\\") + 1); // remove C:\fakepath if exists
			}
		}

		GWT.log("generating fake uploaders on: "+Arrays.asList(files.toString()));
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
					GWT.log("Upload completed with warnings "+ resultMessage.getMessage());
					removeLoading();
					break;
				}
				case SESSION_EXPIRED:{
					GWT.log("Upload aborted due to session expired: "+ resultMessage.getMessage());
					Window.alert("Session expired, please reload the page");
					removeLoading();
					break;
				}
				case OK: {
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
				GWT.log("Current Uploader has id: "+fileUploadID);
				String filesSelected = getFilesSelected(fileUploadID, FILE_DELEMITER);
				GWT.log("getFilesSelected: " + filesSelected);
				files = filesSelected.split(FILE_DELEMITER);

				if(isLimitExceeded(files.length))
					return;

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

//				// CASE 1 FILE
//				if (files.length == 1) {
//
//					// recall: Some browser would write in
//					// fileUploadField.getValue() C:\fakepath\$fileName
//					final String label = files[0];
//					WorkspaceUploaderServiceAsync.Util.getInstance()
//							.itemExistsInWorkpaceFolder(parentIdentifier,
//									label, new AsyncCallback<String>() {
//
//										@Override
//										public void onSuccess(
//												final String itemId) {
//											GWT.log("itemExistsInWorkpaceFolder: "
//													+ itemId);
//											if (itemId != null) {
//												removeItemAndSubmitForm(itemId);
//												updateItemSubmitForm(itemId);
////												if (Window.confirm(label+ " exists in folder. If you continue, a new version will be created")) {
////													removeItemAndSubmitForm(itemId);
////													updateItemSubmitForm(itemId);
////												}
//											} else
//												submitForm(); // ITEM does NOT
//																// EXIST SO
//																// SUBMIT FORM;
//										}
//
//										@Override
//										public void onFailure(Throwable caught) {
//											GWT.log("Sorry an error occurred on the server "
//													+ caught.getLocalizedMessage()
//													+ ". Please try again later");
//											Window.alert(caught.getMessage());
//										}
//
//									});
//				} else
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
		int queueIndex = UploaderMonitor.getInstance().newQueue();
		for (final WorkspaceUploaderItem workspaceUploaderItem : fakeUploaders) {
			UploaderMonitor.getInstance().addNewUploaderToMonitorPanel(workspaceUploaderItem, workspaceUploaderItem.getFile().getFileName());
			setVisible(false);
			removeLoading();
			UploaderMonitor.getInstance().addNewUploaderToQueue(queueIndex, workspaceUploaderItem);
//			UploaderMonitor.getInstance().pollWorkspaceUploader(workspaceUploaderItem);
		}

		UploaderMonitor.getInstance().doStartPollingQueue(queueIndex);
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

	/**
	 * Submit form.
	 */
	@Override
	public void submitForm() {
		formPanel.submit();
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

	/**
	 * Gets the files selected.
	 *
	 * @param tagId
	 *            the tag id
	 * @param fileDelimiter
	 *            the file delimiter
	 * @return the files selected
	 */
	public static native String getFilesSelected(final String tagId, final String fileDelimiter) /*-{
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
}
