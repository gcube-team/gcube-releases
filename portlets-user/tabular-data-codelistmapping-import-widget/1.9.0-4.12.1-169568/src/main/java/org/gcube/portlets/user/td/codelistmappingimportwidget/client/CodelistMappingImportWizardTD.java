package org.gcube.portlets.user.td.codelistmappingimportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.codelisthelper.CodelistMappingSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;

import com.allen_sauer.gwt.log.client.Log;
import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * @author Giancarlo Panichi
 *   
 *
 */
public class CodelistMappingImportWizardTD  extends WizardWindow  {
	private static final int WITHWIZARD=800;
	private static final int HEIGHTWIZARD=520;
	private CodelistMappingSession codelistMappingSession;
	
	
	
	public CodelistMappingImportWizardTD(TRId trId, String title, EventBus eventBus)	{
		super(title,eventBus);
		Log.debug("CodelistMappingImportWizardTD: "+trId);
		setWidth(WITHWIZARD);
		setHeight(HEIGHTWIZARD);
		
		codelistMappingSession= new CodelistMappingSession();
		codelistMappingSession.setTrId(trId);
		
		SourceSelectionCard sourceSelection= new SourceSelectionCard(codelistMappingSession);
		addCard(sourceSelection);
		sourceSelection.setup();
		
	}
	
	
	
	
}