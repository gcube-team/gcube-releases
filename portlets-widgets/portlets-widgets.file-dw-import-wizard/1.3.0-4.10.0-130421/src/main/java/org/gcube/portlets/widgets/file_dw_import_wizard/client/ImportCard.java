/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.client;



import org.gcube.portlets.widgets.file_dw_import_wizard.client.fileimport.ImportProgressBarUpdater;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.fileimport.ImportProgressSource;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.general.WizardCard;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgressListener;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationProgressUpdater;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.rpc.ImportService;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;



public class ImportCard extends WizardCard {
	
	public static final int STATUS_POLLING_DELAY = 500;
	
	protected ImportSession session;
	protected OperationProgressUpdater progressUpdater;

	public ImportCard(ImportSession session)
	{
		//FIXME step message calculated
		super("Import", "Step 4 of 4");

		this.session = session;
		
		setContent(getPanel());
	}

	public FormPanel getPanel()
	{
		FormData formData = new FormData("-20");
		FormPanel panel = new FormPanel();
		panel.setHeaderVisible(false);

		Label progressLabel = new Label("Import progress");
		panel.add(progressLabel);
		
		ImportProgressSource progressSource = new ImportProgressSource(session);
		progressUpdater = new OperationProgressUpdater(progressSource);
		
		ProgressBar importProgess = new ProgressBar();
		progressUpdater.addListener(new ImportProgressBarUpdater(importProgess));
		
		panel.add(importProgess, formData);
		
		
		panel.add(new HTML("<br>"));
		
		final Button errorDetailsButton = new Button("Error details");
		errorDetailsButton.setVisible(false);
		panel.add(errorDetailsButton);
		
		progressUpdater.addListener(new OperationProgressListener() {
			
			@Override
			public void operationUpdate(long total, long elaborated) {}
			
			@Override
			public void operationFailed(final Throwable caught, String reason, final String failureDetails) {
				errorDetailsButton.setVisible(true);
				
				//not a good way
				errorDetailsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
					
					@Override
					public void componentSelected(ButtonEvent ce) {
						showErrorAndHide("Error details", "An error occured import the  file", failureDetails, caught);
					}
				});
			}
			
			@Override
			public void operationComplete() {
				setEnableNextButton(true);
				getWizardWindow().fireCompleted();
			}
		});
		
		
		return panel;
	}
	
	public void importFile()
	{
		ImportService.Utility.getInstance().startImport(session.getId(), session.getColumnToImportMask(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				progressUpdater.scheduleRepeating(STATUS_POLLING_DELAY);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showErrorAndHide("Error importing the fail", caught.getMessage(), caught.toString(), caught);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup() {
		setEnableBackButton(false);
		setNextButtonText("Close");
		setEnableNextButton(false);
		importFile();
		setNextButtonToFinish();
	}
	
}
