/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client;

import org.gcube.portlets.user.csvimportwizard.client.csvimport.ImportProgressBarUpdater;
import org.gcube.portlets.user.csvimportwizard.client.csvimport.ImportProgressSource;
import org.gcube.portlets.user.csvimportwizard.client.general.WizardCard;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgressListener;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgressUpdater;
import org.gcube.portlets.user.csvimportwizard.client.rpc.CSVImportService;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CSVImportCard extends WizardCard {
	
	public static final int STATUS_POLLING_DELAY = 1000;
	
	protected CSVImportSession session;
	protected OperationProgressUpdater progressUpdater;


	public CSVImportCard(CSVImportSession session)
	{
		//FIXME step message calculated
		super("CSV Import", "Step 4 of 4");

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
			
			
			public void operationUpdate(long total, long elaborated) {}
			
			
			public void operationFailed(final Throwable caught, String reason, final String failureDetails) {
				errorDetailsButton.setVisible(true);
				
				//not a good way
				errorDetailsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
					
					
					public void componentSelected(ButtonEvent ce) {
						showErrorAndHide("Error details", "An error occured import the CSV file", failureDetails, caught);
					}
				});
			}
			
			
			public void operationComplete() {
				setEnableNextButton(true);
				getWizardWindow().fireCompleted();
			}
		});
		
		
		return panel;
	}
	
	public void importCSV()
	{
		CSVImportService.Util.getInstance().startImport(session.getId(), session.getColumnToImportMask(), new AsyncCallback<Void>() {
			
			
			public void onSuccess(Void result) {
				progressUpdater.scheduleRepeating(STATUS_POLLING_DELAY);
			}
			
			
			public void onFailure(Throwable caught) {
				showErrorAndHide("Error importing the fail", "An error occured import the CSV file", "", caught);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	
	public void setup() {
		setEnableBackButton(false);
		setNextButtonText("Close");
		setEnableNextButton(false);
		importCSV();
		setNextButtonToFinish();
	}
	
}
