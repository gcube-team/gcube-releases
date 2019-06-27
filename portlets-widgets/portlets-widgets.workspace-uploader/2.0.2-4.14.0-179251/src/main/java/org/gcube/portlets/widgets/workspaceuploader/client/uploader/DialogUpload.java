package org.gcube.portlets.widgets.workspaceuploader.client.uploader;


import java.util.ArrayList;
import java.util.List;

import org.gcube.portal.clientcontext.client.GCubeClientContext;
import org.gcube.portlets.widgets.workspaceuploader.client.ClosableDialog;
import org.gcube.portlets.widgets.workspaceuploader.client.ConstantsWorkspaceUploader;
import org.gcube.portlets.widgets.workspaceuploader.client.DialogResult;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.HasWorskpaceUploadNotificationListener;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.WorskpaceUploadNotificationListener;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploaderListenerController;
import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploaderServiceAsync;
import org.gcube.portlets.widgets.workspaceuploader.shared.HandlerResultMessage;
import org.gcube.portlets.widgets.workspaceuploader.shared.WorkspaceUploaderItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * The Class DialogUploadStream.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 24, 2015
 */
public class DialogUpload extends ClosableDialog implements HasWorskpaceUploadNotificationListener{

	private Hidden hiddenOverwrite = new Hidden(ConstantsWorkspaceUploader.IS_OVERWRITE, "true");
	protected final FormPanel formPanel = new FormPanel();
	protected FileUpload fileUpload;
	protected Hidden jsonClientKeys;
	protected List<WorkspaceUploaderItem> fakeUploaders = new ArrayList<WorkspaceUploaderItem>();
	protected WorkspaceUploaderListenerController controller = new WorkspaceUploaderListenerController();

	/**
	 * The Enum UPLOAD_TYPE.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Sep 11, 2015
	 */
	public static enum UPLOAD_TYPE {File, Archive};
	private HTML LOADING = new HTML("Upload starting...");

	protected String parentIdentifier = "";
	protected VerticalPanel panel;
	private HorizontalPanel hpBottom;
//	private MonitorPanel monitorPanel;
	protected String clientUploadKey;
	private Anchor anchorMyUploads;
	protected UPLOAD_TYPE uploadType;

	/**
	 * Instantiates a new dialog upload stream.
	 *
	 * @param headerTitle the header title
	 * @param parentId the parent id
	 * @param uploadType the upload type
	 */
	public DialogUpload(String headerTitle, final String parentId, UPLOAD_TYPE uploadType){
		super(false, true, headerTitle);
		setWidth("400px");
		fileUpload = new FileUpload();

		//ONLY IN CASE OF FILE MULTIPLE UPLOAD IS AVAILABLE
		if(uploadType.equals(UPLOAD_TYPE.File))
			fileUpload.getElement().setAttribute("multiple", "multiple");

		this.parentIdentifier = parentId;
		this.uploadType = uploadType;
		this.addStyleName("fileInputUploader");
		formPanel.setAction(ConstantsWorkspaceUploader.WORKSPACE_UPLOADER_SERVLET_STREAM__MODIFIED);
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.setWidth("auto");


		// Create a panel to hold all of the form widgets.
		panel = new VerticalPanel();
		formPanel.setWidget(panel);
		setModal(false);

		fileUpload.setName(ConstantsWorkspaceUploader.UPLOAD_FORM_ELEMENT);

		// Add hidden parameters
		panel.add(new Hidden(ConstantsWorkspaceUploader.CURR_GROUP_ID, GCubeClientContext.getCurrentContextId()));
		//panel.add(new Hidden(ConstantsWorkspaceUploader.CURR_USER_ID, GCubeClientContext.getCurrentUserId()));
		panel.add(new Hidden(ConstantsWorkspaceUploader.ID_FOLDER,parentId));
		panel.add(new Hidden(ConstantsWorkspaceUploader.UPLOAD_TYPE,uploadType.toString()));

		panel.add(hiddenOverwrite);

		initJsonClientKeys();
		panel.add(jsonClientKeys);
		panel.add(fileUpload);

		HorizontalPanel hp = new HorizontalPanel();
//		hp.getElement().setId("hpID");
		hp.getElement().getStyle().setWidth(100, Unit.PCT);
		hp.getElement().getStyle().setMarginTop(5, Unit.PX);
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		anchorMyUploads = new Anchor(ConstantsWorkspaceUploader.MY_UPLOADS);
		anchorMyUploads.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				UploaderMonitor.getInstance().showMonitor();
			}
		});
		hp.add(anchorMyUploads);
		panel.add(hp);

		hpBottom = new HorizontalPanel();
		hpBottom.setWidth("100%");
//		hpBottom.getElement().getStyle().setMarginTop(5, Unit.PX);
		hpBottom.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		if(uploadType.equals(UPLOAD_TYPE.File)){
			HTML msg = new HTML("<p>To select multiple files, press and hold down the Ctrl key, and then</p><p style='margin:-3px 0;'>click each item that you want to select</p>");
			msg.getElement().getStyle().setFontSize(10, Unit.PX);
			hpBottom.add(msg);
		}

		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.getElement().getStyle().setMarginTop(5, Unit.PX);
//		hp2.getElement().getStyle().setWidth(100, Unit.PCT);
		hp2.addStyleName("align-right-close");

		Button bClose = new Button("Close");
		bClose.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				DialogUpload.this.hide();
			}
		});

		hp2.add(bClose);
		hpBottom.add(hp2);
		panel.add(hpBottom);

		add(formPanel);
	}

	/**
	 * @return the panel
	 */
	public VerticalPanel getPanel() {
		return panel;
	}

	protected void initJsonClientKeys(){
		jsonClientKeys = new Hidden(ConstantsWorkspaceUploader.CLIENT_UPLOAD_KEYS,"");
	}

	/**
	 * Bind events.
	 */
	public void bindEvents(){
		this.addHandlers();
//		this.addListeners();
	}

	/**
	 * Adds the handlers.
	 */
	protected void addHandlers() {

		// handle the post
		formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				GWT.log("onSubmitComplete");
				hide();
//				isStatusCompleted = true;
				String result = event.getResults();

				if (result == null)	{
					removeLoading();
					new DialogResult(null, "Error during upload", "An error occurred during file upload.").center();
					return;
				}
				String strippedResult = new HTML(result).getText();
				final HandlerResultMessage resultMessage = HandlerResultMessage.parseResult(strippedResult);

				switch (resultMessage.getStatus()) {
				case ERROR:
					removeLoading();
					GWT.log("Error during upload "+resultMessage.getMessage());
					break;
				case UNKNOWN:
					removeLoading();
					GWT.log("Error during upload "+resultMessage.getMessage());
					break;
				case WARN: {
					GWT.log("Upload completed with warnings "+resultMessage.getMessage());
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
////					removeLoading();
////					UploaderMonitor.getInstance().pollWorkspaceUploaderId(resultMessage.getMessage());
//
//					Timer t = new Timer() {
//						public void run() {
//							GWT.log("Upload started with id: "+resultMessage.getMessage());
//							UploaderMonitor.getInstance().pollWorkspaceUploaderId(resultMessage.getMessage());
//						}
//					};
//
//					t.schedule(250);
				}
				}
			}
		});

		//TODO NEVER USED, WE ARE USING MULTIPLE DIALOG UPLOAD
		formPanel.addSubmitHandler(new SubmitHandler() {

			@Override
			public void onSubmit(SubmitEvent event) {
				GWT.log("SubmitEvent");
				addLoading();
				enableButtons(false);
				WorkspaceUploaderItem fakeUploader = new WorkspaceUploaderItem();
				fakeUploader.setClientUploadKey(clientUploadKey);
				int queueIndex = UploaderMonitor.getInstance().newQueue();
				UploaderMonitor.getInstance().addNewUploaderToMonitorPanel(fakeUploader, fileUpload.getFilename());
				setVisible(false);
				removeLoading();
				UploaderMonitor.getInstance().addNewUploaderToQueue(queueIndex, fakeUploader);
				UploaderMonitor.getInstance().doStartPollingQueue(queueIndex);
			}
		});

		fileUpload.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				GWT.log("btnSubmit click");
				if (fileUpload.getFilename()==null || !(fileUpload.getFilename().length()>2)) {
					GWT.log("No file specified ");
					return;
				}

				GWT.log("fileUpload.getFilename() "+fileUpload.getFilename());
				/*
				 * TODO: recall: Some browser would write in fileUploadField.getValue() C:\fakepath\$fileName
				 */
				String normalizedFileName = fileUpload.getFilename();
				if (normalizedFileName.contains("\\")) {
					normalizedFileName = normalizedFileName.substring(normalizedFileName.lastIndexOf("\\")+1); //remove C:\fakepath\ if exists
				}
				final String label = normalizedFileName;
				WorkspaceUploaderServiceAsync.Util.getInstance().itemExistsInWorkpaceFolder(parentIdentifier, normalizedFileName, new AsyncCallback<String>() {

					@Override
					public void onSuccess(final String itemId) {
						GWT.log("itemExistsInWorkpaceFolder: "+itemId);
						if(itemId!=null){
							removeItemAndSubmitForm(itemId);
							updateItemSubmitForm(itemId);
						}else
							submitForm(); //ITEM does NOT EXIST SO SUBMIT FORM;
					}

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Sorry an error occurred on the server "+caught.getLocalizedMessage() + ". Please try again later");
						Window.alert(caught.getMessage());
					}

				});
			}
		});
	}

	/**
	 * Adds the loading.
	 */
	protected void addLoading(){
		panel.add(LOADING);
	}

	/**
	 * Removes the loading.
	 */
	protected void removeLoading(){
		try{
			panel.remove(LOADING);
		}catch(Exception e){

		}
	}

	/**
	 * Enable buttons.
	 *
	 * @param bool the bool
	 */
	protected void enableButtons(boolean bool){
//		btnUpload.setEnabled(bool);
//		btnCancel.setEnabled(bool);
//		upload.setEnabled(bool);
	}

	/**
	 * Submit form.
	 */
	public void submitForm(){
		formPanel.submit();
	}


	/**
	 * Removes the item and submit form.
	 *
	 * @param itemId the item id
	 */
	protected void removeItemAndSubmitForm(String itemId){
		/*
		AppControllerExplorer.rpcWorkspaceService.removeItem(itemId, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Error", caught.getMessage());


			}

			@Override
			public void onSuccess(Boolean result) {
				if(result){
					hiddenOverwrite.setValue("true");
					submitForm();
				}

			}

		});*/
	}

	/**
	 * Update item submit form.
	 *
	 * @param itemId the item id
	 */
	protected void updateItemSubmitForm(String itemId){
		hiddenOverwrite.setValue("true");
		submitForm();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.HasWorskpaceUploadNotificationListener#addWorkspaceUploadNotificationListener(org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.WorskpaceUploadNotificationListener)
	 */
	@Override
	public void addWorkspaceUploadNotificationListener(WorskpaceUploadNotificationListener handler) {
		controller.addWorkspaceUploadListener(handler);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.HasWorskpaceUploadNotificationListener#removeWorkspaceUploadNotificationListener(org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.WorskpaceUploadNotificationListener)
	 */
	@Override
	public void removeWorkspaceUploadNotificationListener(WorskpaceUploadNotificationListener handler) {
		controller.removeWorkspaceUploadListener(handler);
	}
}

