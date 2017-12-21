/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.source.local;

import org.gcube.portlets.user.csvimportwizard.client.CSVImportSession;
import org.gcube.portlets.user.csvimportwizard.client.general.WizardCard;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgressListener;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgressUpdater;

import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HTML;
import com.extjs.gxt.ui.client.widget.layout.FormData;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class LocalUploadPanel extends FormPanel {
	
	protected static final String UPLOAD_SERVLET = "LocalUploadServlet";

	public static final int STATUS_POLLING_DELAY = 1000;

	protected FileUploadField fileUploadField;
	protected Button uploadButton;
	protected CSVImportSession session;
	protected OperationProgressUpdater progressUpdater;

	protected ProgressBar uploadProgressBar;

	protected FormButtonBinding uploadButtonBinding;

	protected Button cancelButton;
	
	protected WizardCard card;
	
	public LocalUploadPanel(final WizardCard card, CSVImportSession session) {
		
		setHeaderVisible(false);
		setLabelAlign(LabelAlign.TOP);

		this.session = session;
		this.card = card;

		setAction(GWT.getModuleBaseURL()+UPLOAD_SERVLET);
		setWidth("100%");

		setEncoding(Encoding.MULTIPART);
		setMethod(Method.POST);

		/*
		fileUploadField = new FileUpload();
	    fileUploadField.ensureDebugId("cwFileUpload");
	    fileUploadField.setName("uploadFormElement");
		//fileUploadField.setFieldLabel("Select the csv file to import");
		//fileUploadField.setEmptyText("a CSV file...");
		//fileUploadField.setAllowBlank(false); 
		//TODO check if works
		//fileUploadField.setAccept("text/csv");
		fileUploadField.setStyleName("fileUploadFix");
	    add(fileUploadField);
		*/
		
		fileUploadField = new FileUploadField();
		fileUploadField.setName("uploadFormElement");
		fileUploadField.setFieldLabel("Select the csv file to import");
		fileUploadField.setEmptyText("a CSV file...");
		fileUploadField.setAllowBlank(false); 
		//TODO check if works
		fileUploadField.setAccept("text/csv");
		fileUploadField.setStyleName("fileUploadFix");
		add(fileUploadField);
	
		
		uploadButton = new Button("Upload");
		add(uploadButton);
	    uploadButtonBinding = new FormButtonBinding(this);  
	    uploadButtonBinding.addButton(uploadButton);
	   	    
		//TODO find a way to add vertical space
		add(new HTML("<br>"));
		
		uploadProgressBar = new ProgressBar();
		//fix for issue with label
		uploadProgressBar.setHideMode(HideMode.VISIBILITY);
		add(uploadProgressBar, new FormData("100%"));
		uploadProgressBar.hide();
		
		//TODO find a way to add vertical space
		add(new HTML("<br>"));
		
	    cancelButton = new Button("Cancel");
	    cancelButton.hide();
	    add(cancelButton);
		
		uploadButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				GWT.log("request upload");
				
				if (fileUploadField.getValue()==null || fileUploadField.getValue().equals("")){
					MessageBox.alert("CSV file missing", "Please specify a CSV file.", new Listener<MessageBoxEvent>() {
						
						/**
						 * @param be
						 */
						
						public void handleEvent(MessageBoxEvent be) {}
					});
					return;
				} else {
					startUpload();
				}
				
			}
		});
		
		//this source retrieve information upload status from server side through RPC calls.
		LocalUploadProgressSource localUploadProgressSource = new LocalUploadProgressSource(session);
		
		progressUpdater = new OperationProgressUpdater(localUploadProgressSource);
		
		//the progress bar information are updated with the operation progress information
		ProgressBarUpdater progressBarUpdater = new ProgressBarUpdater(uploadProgressBar);
		progressUpdater.addListener(progressBarUpdater);
		
		progressUpdater.addListener(new OperationProgressListener() {
			
			/**
			 * {@inheritDoc}
			 */
			public void operationUpdate(long total, long elaborated) {
			}
			
			/**
			 * {@inheritDoc}
			 */
			public void operationFailed(Throwable caught, String reason, String errorDetails) {
				card.showErrorAndHide("Error uploading the csv file", reason, errorDetails, caught);
			}
			
			/**
			 * {@inheritDoc}
			 */
			public void operationComplete() {
				card.setEnableNextButton(true);
				cancelButton.disable();
			}
		});

	}
	
	protected void startUpload()
	{
		disableUpload();
		
		//we update the action url with the session id
		//this is necessary in order to let the servlet retrieve the session id before the POST request parsing
		StringBuilder actionUrl = new StringBuilder();
		actionUrl.append(GWT.getModuleBaseURL());
		actionUrl.append(UPLOAD_SERVLET+"?sessionId=");
		actionUrl.append(session.getId());
		setAction(actionUrl.toString());
		
		submit();
		
		progressUpdater.scheduleRepeating(STATUS_POLLING_DELAY);
	}
	
	protected void disableUpload()
	{
		fileUploadField.disable();
		uploadButton.disable();
	    uploadButtonBinding.stopMonitoring();
	    
	    uploadProgressBar.show();
	    cancelButton.show();
	}

}
