/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client.source.workspace;

import org.gcube.portlets.widgets.file_dw_import_wizard.client.ImportSession;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.general.WizardCard;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgressListener;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgressUpdater;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.rpc.ImportService;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.source.local.ProgressBarUpdater;

import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceUploadPanel extends FormPanel {

	public static final int STATUS_POLLING_DELAY = 1000;

	protected WorkspaceUploadField fileUpload;
	protected Button uploadButton;
	protected ImportSession session;
	protected OperationProgressUpdater progressUpdater;

	protected ProgressBar uploadProgressBar;

	protected FormButtonBinding uploadButtonBinding;

	protected Button cancelButton;

	protected WizardCard card;

	public WorkspaceUploadPanel(final WizardCard card, ImportSession session) {

		setHeaderVisible(false);
		setLabelAlign(LabelAlign.TOP);

		this.session = session;
		this.card = card;

		setWidth("100%");
		
		fileUpload = new WorkspaceUploadField("Select the csv file from your workspace");
		add(fileUpload, new FormData("100%"));

		//TODO find a way to add vertical space
		add(new HTML("<br>"));

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

				if (fileUpload.getWorkspaceItemId()==null){
					MessageBox.alert("File missing", "Please specify a file.", new Listener<MessageBoxEvent>() {

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
		WorkspaceUploadProgressSource localUploadProgressSource = new WorkspaceUploadProgressSource(session);

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
		
		ImportService.Utility.getInstance().startWorkspaceUpload(session.getId(), fileUpload.getWorkspaceItemId(), new AsyncCallback<Void>() {
			
			public void onSuccess(Void result) {
				progressUpdater.scheduleRepeating(STATUS_POLLING_DELAY);
			}
			
			public void onFailure(Throwable caught) {
				card.showErrorAndHide("Error uploading the csv file", "", "", caught);
			}
		});

		
	}

	protected void disableUpload()
	{
		fileUpload.disable();
		uploadButton.disable();
		uploadButtonBinding.stopMonitoring();

		uploadProgressBar.show();
		cancelButton.show();
	}

}
