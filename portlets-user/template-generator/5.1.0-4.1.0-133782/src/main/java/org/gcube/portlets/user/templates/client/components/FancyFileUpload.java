package org.gcube.portlets.user.templates.client.components;

import org.gcube.portlets.user.templates.client.dialogs.ImageUploaderDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWordWrap;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * <code> FancyFileUpload </code> class is use to upload images from client in the UI
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version October 2008 (0.2) 
 */
public class FancyFileUpload extends Composite implements HasText, HasWordWrap, SourcesChangeEvents{

	/**
	 * State definitions
	 */
	public final int EMPTY_STATE = 1;
	/**
	 * 
	 */
	public final int PENDING_STATE = 2;
	/**
	 * 
	 */
	public final int UPLOADING_STATE = 3;
	/**
	 * 
	 */
	public final int UPLOADED_STATE = 4;
	/**
	 * 
	 */
	public final int DELETED_STATE = 5;
	/**
	 * 
	 */
	public final int FAILED_STATE = 6;

	/**
	 * Initial State of the widget.
	 */
	private int widgetState = EMPTY_STATE;

	/**
	 * Default delay to check an empty FileUpload widget for
	 * arrival of a  filename.
	 *
	 */
	private int searchUpdateDelay = 500;

	/**
	 * Default delay for pending state, when delay over the form is
submitted.
	 */
	private int pendingUpdateDelay = 5000;

	/**
	 * the panel where this widget is in
	 */
	private ImageUploaderDialog theOwner;
	

	/**
	 * OK message expected from file upload servlet to indicate successful
upload.
	 */
	//private String retursOKMessage = "<pre>OK</pre>";

	private FormPanel uploadForm = new FormPanel();
	private VerticalPanel mainPanel = new VerticalPanel();

	/**
	 * Internal timer for checking fileupload text for a value.
	 */
	private Timer t;

	/**
	 * Internal timer for checking if pending delay is over.
	 */
	private Timer p;

	/**
	 * Widget representing file to be uploaded.
	 */
	private UploadDisplay uploadItem;

	/**
	 * FileName to be uploaded
	 */
	String fileName = "";


	/**
	 * Class used for the display of filename to be uploaded,
	 * and handling the update of the display states.
	 * 
	 *
	 */
	protected class UploadDisplay extends Composite{

		/**
		 * FileUpload Widget
		 */
		FileUpload uploadFileWidget = new FileUpload();

		/**
		 * Label to display after file widget is filled with a filename
		 */
		Label uploadFileName = new Label();


		/**
		 * Panel to hold the widget
		 */
		FlowPanel mainPanel = new FlowPanel();

		/**
		 * Panel to hold pending, loading, loaded or failed state details.
		 */
		HorizontalPanel pendingPanel = new HorizontalPanel();

		/**
		 * Constructor
		 *
		 */
		public UploadDisplay(){
			
			mainPanel.add(uploadFileWidget);
			pendingPanel.add(uploadFileName);
			uploadFileName.setStyleName("HTMLObjectStyle-font");
			uploadFileName.setWordWrap(true);
			uploadFileWidget.setWidth("100%");

			mainPanel.add(pendingPanel);
			pendingPanel.setVisible(false);
			initWidget(mainPanel);
		}

		/**
		 * Set the widget into pending mode by altering style
		 * of pending panel and displaying it.  Hide the FileUpload
		 * widget and finally set the state to Pending.
		 *
		 */
		private void setPending(){
			uploadFileName.setText("Please wait, fetching image from your file system.. ");//uploadFileWidget.getFilename());
			uploadFileWidget.setVisible(false);
			pendingPanel.setVisible(true);
			pendingPanel.setStyleName("fancyfileupload-pending");
			widgetState = PENDING_STATE;
		}

		/**
		 * Set the widget into Loading mode by changing the style name
		 * and updating the widget State to Uploading.
		 *
		 */
		private void setLoading(){
			pendingPanel.setStyleName("fancyfileupload-loading");
			widgetState = UPLOADING_STATE;
		}

		/**
		 * Set the widget to Loaded mode by changing the style name
		 * and updating the widget State to Loaded.
		 *
		 */
		private void setLoaded(){
			pendingPanel.setStyleName("fancyfileupload-loaded");
			uploadFileName.setText("Image Successfully uploaded");
			widgetState = UPLOADED_STATE;
		}


		/**
		 * Set the widget to Failed mode by changing the style name
		 * and updating the widget State to Failed.
		 * Additionally, hide the pending panel and display the FileUpload
		 * widget.
		 *
		 */
		private void setFailed(){
			widgetState = FAILED_STATE;
			uploadFileName.setText("Operation Failed");
		}

	}

	/**
	 * Perform the uploading of a file by changing state of display widget
	 * and then calling form.submit() method.
	 *
	 */
	private void uploadFiles(){
		fileName = uploadItem.uploadFileWidget.getFilename();

		uploadItem.setLoading();
		uploadForm.submit();

	}


	/**
	 * Put the widget into a Pending state, set the Pending delay timer
	 * to call the upload file method when ran out.
	 *
	 */
	private void pendingUpload(){
		// Fire an onChange event to anyone who is listening
		uploadItem.setPending();
		p = new Timer(){
			public void run() {
				uploadFiles();
			}
		};
		p.schedule(pendingUpdateDelay);
	}

	/**
	 * Method to check if FileUpload Widget has a filename within it.
	 * If so, cancel the timer that was set to call this method and then
	 * call the pendingUpload() method.
	 * If not, do nothing.
	 *
	 */
	private void checkForFileName(){
//		GWT.log(uploadItem.uploadFileWidget.getFilename()+" :"+fileName,null);
//		if (!uploadItem.uploadFileWidget.getFilename().equals("")){
//		if (!uploadItem.uploadFileWidget.getFilename().equals(fileName)){
//		t.cancel();
//		pendingUpload();
//		}
//		}
	}

	/**
	 * This method sets up a repeating schedule to call the
checkforfilename
	 * method to see if the FileUpload widget has any text in it.
	 *
	 */
	private void startWaiting(){
		t = null;
		t = new Timer(){
			public void run() {
				checkForFileName();
			}
		};
		t.scheduleRepeating(searchUpdateDelay);
	}

	/**
	 * 
	 * @param owner the caller
	 * @param templateName  .
	 */
	public FancyFileUpload(ImageUploaderDialog owner, String templateName){

		this.theOwner = owner;
		// Set Form details
		// Set the action to call on submit

		uploadForm.setAction(GWT.getModuleBaseURL() + "ImagesUploadServlet?currTemplateName=" + templateName);


		// Set the form encoding to multipart to indicate a file upload
		uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		// Set the method to Post
		uploadForm.setMethod(FormPanel.METHOD_POST);
		uploadForm.setWidget(mainPanel);

		// Create a new upload display widget
		uploadItem = new UploadDisplay();
		// Set the name of the upload file form element
		uploadItem.uploadFileWidget.setName("uploadFormElement");
		// Add the new widget to the panel.
		mainPanel.add(uploadItem);
		HorizontalPanel wrapper = new HorizontalPanel();
		wrapper.setSpacing(4);
		// Add a 'submit' button.
		wrapper.add(new Button("Submit", new ClickListener() {
			public void onClick(Widget sender) {
				String fName =  uploadItem.uploadFileWidget.getFilename();


				int slashPosition = fName.lastIndexOf("/");

				String fileNameToCheck = "" ;

				if (slashPosition == -1) //it is windows 
					slashPosition = fName.lastIndexOf("\\");

				if (slashPosition != -1)
					fileNameToCheck = fName.substring(slashPosition+1, fName.length());

				if (fName.equals("")) {
					Window.alert("The text box must not be empty");
				}
				else if (fileNameToCheck.indexOf(" ") > -1) {
					Window.alert("File name cannot contain empty spaces");

				}
				else {
					t.cancel();
					pendingUpload();					 				 	 
				}
			}
		}));

		// Add a 'close' button.
		wrapper.add(new Button("Cancel", new ClickListener() {
			public void onClick(Widget sender) {
				theOwner.hide();
			}
		}));
		mainPanel.add(wrapper);

		// Start the waiting for a name to appear in the file upload widget.
		startWaiting();
		// Initialise the widget.
		initWidget(uploadForm);

		// Add an event handler to the form.
		uploadForm.addFormHandler(new FormHandler() {
			public void onSubmitComplete(FormSubmitCompleteEvent event) {
				// Fire an onChange Event
				fireChangeEvent();
				// Cancel all timers to be absolutely sure nothing is going on.
				t.cancel();
				p.cancel();
				// Ensure that the form encoding is set correctly.
				uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
				// Check the result to see if an OK message is returned from the server.

				if(event.getResults().toString().contains("OK") || event.getResults().toString().startsWith("OK")) {
					uploadItem.setLoaded();
					String fName = uploadItem.uploadFileWidget.getFilename();

					String nameToPass = "";
					if (fName.lastIndexOf("/") == -1) //windows
						nameToPass = fName.substring(fName.lastIndexOf("\\")+1);
					else
						nameToPass = fName.substring(fName.lastIndexOf("/")+1);

					theOwner.insertImage(nameToPass);
					theOwner.hide(); 					

				} else {
					// If no, set the widget to failed state.
					uploadItem.setFailed();
					Window.alert("There were some errors during File Uploading  processing,Please try again");
				}
			}

			public void onSubmit(FormSubmitEvent event) {
				//No validation in this version.
			}
		});
	}

	/**
	 * Fire a change event to anyone listening to us.
	 *
	 */
	private void fireChangeEvent(){
		if (changeListeners != null)
			changeListeners.fireChange(this);
	}

	/**
	 * Get the text from the widget - which in reality will be retrieving any
	 * value set in the Label element of the display widget.
	 * @return .
	 */
	public String getText() {
		return uploadItem.uploadFileName.getText();
	}

	/**
	 * Cannot set the text of a File Upload Widget, so raise an exception.
	 * @param text .
	 */
	public void setText(String text) {
		throw new RuntimeException("Cannot set text of a FileUpload Widget");
	}

	/**
	 * Retrieve the status of the upload widget.
	 * @return Status of upload widget.
	 * 
	 */
	public int getUploadState(){
		return widgetState;
	}

	/**
	 * Set the delay for checking for a filename to appear in the FileUpload widget
	 * Might be useful if there are performance issues.
	 * @param newDelay .
	 */
	public void setCheckForFileNameDelay(int newDelay){
		searchUpdateDelay = newDelay;
	}

	/**
	 * Set the delay value indicating how long a file will remain in
pending mode
	 * prior to the upload action taking place.
	 * @param newDelay .
	 */
	public void setPendingDelay(int newDelay){
		pendingUpdateDelay = newDelay;
	}

	/**
	 * Return the delay value set for checking a file.
	 * @return .
	 */
	public int getCheckForFileNameDelay(){
		return searchUpdateDelay;
	}

	/**
	 * Return value set for pending delay.
	 * @return .
	 */
	public int getPendingDelay(){
		return pendingUpdateDelay;
	}

	/**
	 * Return if the label in the display widget is wordwrapped or not.
	 * @return .
	 */
	public boolean getWordWrap() {
		return uploadItem.uploadFileName.getWordWrap();
	}

	/**
	 * Set the word wrap value of the label in the display widget.
	 * @param wrap .
	 */
	public void setWordWrap(boolean wrap) {
		uploadItem.uploadFileName.setWordWrap(wrap);
	}

	/**
	 * 
	 */
	private ChangeListenerCollection changeListeners;

	/**
	 * Add a change listener
	 * @param listener ,
	 */
	public void addChangeListener(ChangeListener listener) {
		if (changeListeners == null)
			changeListeners = new ChangeListenerCollection();
		changeListeners.add(listener);
	}

	/**
	 * Remove a change listener
	 * @param listener .
	 */
	public void removeChangeListener(ChangeListener listener) {
		if (changeListeners != null)
			changeListeners.remove(listener);
	}

} 