/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client;

import java.util.ArrayList;

import org.gcube.portlets.user.csvimportwizard.client.general.WizardCard;
import org.gcube.portlets.user.csvimportwizard.client.general.WizardWindow;
import org.gcube.portlets.user.csvimportwizard.client.rpc.CSVImportService;
import org.gcube.portlets.user.csvimportwizard.client.source.CSVSource;
import org.gcube.portlets.user.csvimportwizard.client.source.local.LocalSource;
import org.gcube.portlets.user.csvimportwizard.client.util.Util;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVTarget;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The CSV import window.
 * @author Federico De Faveri defaveri@isti.cnr.it
 */
public class ImportWizard extends WizardWindow {

	public static final ArrayList<CSVSource> DEFAULT_SOURCES = new ArrayList<CSVSource>();
	static {
		DEFAULT_SOURCES.add(new LocalSource());
	}

	protected CSVImportSession importSession;

	/**
	 * The id of the {@link CSVTarget} to use.
	 * @param targetId
	 */
	public ImportWizard(String targetId)
	{
		this(targetId, DEFAULT_SOURCES);
	}
	
	/**
	 * The id of the {@link CSVTarget} to use.
	 * @param targetId
	 */
	public ImportWizard(String targetId, CSVSource ... sources)
	{
		this(targetId, Util.asList(sources));
	}

	/**
	 * Creates a new Import Wizard with the specified {@link CSVSource} sources.
	 * @param sources the csv source to use.
	 */
	public ImportWizard(final String targetId, ArrayList<CSVSource> sources)
	{
		super("CSV import wizard");
		setWidth(550);
		setHeight(500);

		importSession = new CSVImportSession();

		WizardCard sourceSelectionCard = new SourceSelectionCard(importSession, sources);
		addCard(sourceSelectionCard);

		WizardCard uploadCard = new UploadCard(importSession, sources);
		addCard(uploadCard); 

		WizardCard configCard = new CSVConfigCard(importSession, this);
		addCard(configCard);

		WizardCard creationCard = new CSVImportCard(importSession);
		addCard(creationCard);

		addWindowListener(new WindowListener(){

			
			public void windowShow(WindowEvent we) {
				mask("Initializing...");

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					
					public void execute() {
						CSVImportService.Util.getInstance().createCSVSessionId(targetId, new AsyncCallback<String>() {

							
							public void onSuccess(String result) {
								Log.trace("sessionId: "+result);
								importSession.setId(result);
								unmask();
							}

							
							public void onFailure(Throwable caught) {
								Log.error("Error getting the sessionId", caught);
								showErrorAndHide("Error initializing the wizard", "Error getting the sessionId", "", caught);
							}
						});
					}

				});
			}
		});	
	}
}
