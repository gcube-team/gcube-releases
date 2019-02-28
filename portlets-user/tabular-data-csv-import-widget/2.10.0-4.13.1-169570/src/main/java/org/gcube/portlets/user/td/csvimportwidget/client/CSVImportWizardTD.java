package org.gcube.portlets.user.td.csvimportwidget.client;

import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVImportSession;

import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class CSVImportWizardTD  extends WizardWindow  {

	protected CSVImportSession importSession;
	protected String WIZARDWIDTH = "727px";
	protected String WIZARDHEIGHT = "520px";
	//private CSVImportWizardTD wizard;
	
	
	
	public CSVImportWizardTD(String title, EventBus eventBus)	{
		super(title,eventBus);
		setWidth(WIZARDWIDTH);
		
		importSession= new CSVImportSession();
		//this.wizard=this;
		
		SourceSelectionCard sourceSelection= new SourceSelectionCard(importSession);
		addCard(sourceSelection);
		sourceSelection.setup();
		
	}
	
	
	
	
}