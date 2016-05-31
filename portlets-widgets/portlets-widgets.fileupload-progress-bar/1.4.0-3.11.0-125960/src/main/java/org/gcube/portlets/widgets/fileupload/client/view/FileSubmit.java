package org.gcube.portlets.widgets.fileupload.client.view;

import org.gcube.portlets.widgets.fileupload.client.bundle.ProgressBarCssAndImages;
import org.gcube.portlets.widgets.fileupload.client.controller.ProgressController;
import org.gcube.portlets.widgets.fileupload.client.events.FileUploadSelectedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public final class FileSubmit extends Composite {
	/**
	 * VERY IMPORTANT, must be the same in web.xml
	 */
	public static final String URL = GWT.getModuleBaseURL()+"../FileUpload/upload";

	ProgressBarCssAndImages images = GWT.create(ProgressBarCssAndImages.class);

	// maximum size of a file that can be attached in MB
	public static final int MAX_SIZE_ATTACHED_FILE_MB = 50;

	// too large file selected
	private static final String TOO_LARGE_FILE_ERROR = "The chosen file can't be uploaded since it is too large! Sorry...";

	private HTML registeringLabel = new HTML("<span style=\"font-size: 15px; height: 20px; padding-top: 10px; display:inline-block; vertical-align:middle: 5px;\">"
			+ "Applying required operations, please wait ... </span><img style=\"margin: 0; padding-left: 10px; vertical-align: middle; \" src='" + images.spinner().getSafeUri().asString() + "'>");

	private FileUpload fileUpload;
	private FormPanel form;
	private Panel uploadPanel = new SimplePanel();
	private UploadProgressDialog dlg;
	private HandlerRegistration submitHandler;

	/**
	 * Constructor
	 * @param eventBus
	 * @param isDND if the file has to be loaded through DND
	 */
	public FileSubmit(HandlerManager eventBus, boolean isDND) {

		if(!isDND){

			ProgressController.start(eventBus);

			fileUpload = new FileUpload();
			fileUpload.setName("fileUpload");
			fileUpload.setTitle("select a file to upload");


			uploadPanel.setStyleName("FileSubmit");
			uploadPanel.add(fileUpload);


			form = new FormPanel();
			form.setEncoding(FormPanel.ENCODING_MULTIPART);
			form.setMethod(FormPanel.METHOD_POST);
			form.getElement().setAttribute("acceptcharset", "UTF-8");
			form.setAction(URL);
			form.setWidget(uploadPanel);

			this.initWidget(form);

			//triggered on selected file form user
			submitHandler = fileUpload.addChangeHandler(new FormSubmitChangeHandler());

			form.addSubmitCompleteHandler(new FormSubmitCompleteHandler());

		}else{

			ProgressController.start(eventBus);
			initWidget(new SimplePanel());
		}

	}

	public FileSubmit(UploadProgressDialog dlg, final HandlerManager eventBus) {
		this(eventBus, false);
		this.dlg = dlg;
		if (dlg.isFormSubmitHandled()) {
			submitHandler.removeHandler();
			fileUpload.addChangeHandler(new ChangeHandler() {				
				@Override
				public void onChange(ChangeEvent event) {
					eventBus.fireEvent(new FileUploadSelectedEvent(fileUpload.getFilename()));
				}
			});
		}

	}

	protected void showRegisteringResult(boolean success, String customLabel) {
		if (customLabel == null || customLabel.compareTo("") == 0) {
			customLabel = success ? "Operation Completed Successfully" : "Sorry, an error occurred in the Server";
		}
		uploadPanel.clear();
		if (success)
			uploadPanel.add( new HTML("<span style=\"font-size: 15px; height: 20px; padding-top: 10px; display:inline-block; vertical-align:middle : 5px;\">"
					+ customLabel + " </span><img style=\"margin: 0; padding-left: 10px; vertical-align: middle; \" src='" + images.ok().getSafeUri().asString() + "'>"));
		else
			uploadPanel.add( new HTML("<span style=\"font-size: 15px; height: 20px; padding-top: 10px; display:inline-block; vertical-align:middle : 5px;\">"
					+ customLabel + " </span><img style=\"margin: 0; padding-left: 10px; vertical-align: middle; \" src='" + images.error().getSafeUri().asString() + "'>"));
		if (dlg != null)  //if dialog mode
			dlg.showFinalCloseButton();
	}


	private class FormSubmitCompleteHandler implements SubmitCompleteHandler {

		@Override
		public void onSubmitComplete(final SubmitCompleteEvent event) {
			//form.reset();
			uploadPanel.remove(fileUpload);
			if (dlg != null) { //if dialog mode
				uploadPanel.add(registeringLabel);
				dlg.hideCloseButton();
			}
		}
	} 

	private class FormSubmitChangeHandler implements ChangeHandler {
		@Override
		public void onChange(ChangeEvent event) {

			// check the size of the chosen file
			GWT.log("SIZE is " + getChosenFileSize(fileUpload.getElement()));
			if(getChosenFileSize(fileUpload.getElement()) > MAX_SIZE_ATTACHED_FILE_MB){
				Window.alert(TOO_LARGE_FILE_ERROR);
				return;
			}

			form.submit();			
		}		
	}

	protected FileUpload getFileUpload() {
		return fileUpload;
	}

	protected void submitForm() {
		form.submit();					
	}

	private static native int getChosenFileSize(final Element data) /*-{
		
		// convert from bytes to MB
    	return (data.files[0].size / 1024 / 1024);
	}-*/;

}
