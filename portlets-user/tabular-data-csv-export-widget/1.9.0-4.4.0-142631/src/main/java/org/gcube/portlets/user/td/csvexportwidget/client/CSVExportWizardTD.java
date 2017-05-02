package org.gcube.portlets.user.td.csvexportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVExportSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;

import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class CSVExportWizardTD extends WizardWindow {
	
	private static final String WIZARDWIDTH = "844px";
	private CSVExportSession exportSession;
	
	
	/**
	 * 
	 * @param title
	 * @param eventBus
	 */
	public CSVExportWizardTD(String title, EventBus eventBus) {
		super(title, eventBus);
		setWidth(WIZARDWIDTH);
	
		exportSession = new CSVExportSession();

		CSVExportConfigCard csvExportConfigCard = new CSVExportConfigCard(
				exportSession);
		addCard(csvExportConfigCard);
		csvExportConfigCard.setup();

	}

	

}