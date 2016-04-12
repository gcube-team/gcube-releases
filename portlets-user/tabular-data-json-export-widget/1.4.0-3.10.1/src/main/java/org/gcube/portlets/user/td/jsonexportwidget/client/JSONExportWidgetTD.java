package org.gcube.portlets.user.td.jsonexportwidget.client;



import org.gcube.portlets.user.td.gwtservice.shared.json.JSONExportSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;

import com.google.web.bindery.event.shared.EventBus;



/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class JSONExportWidgetTD  extends WizardWindow  {

	protected JSONExportSession exportSession;
	protected String WIZARDWIDTH = "844px";
	
	
	
	
	/**
	 * 
	 * @param title
	 * @param eventBus
	 */
	public JSONExportWidgetTD(String title, EventBus eventBus)	{
		super(title,eventBus);
		setWidth(WIZARDWIDTH);
		
		
		exportSession= new JSONExportSession();
		
		JSONExportConfigCard jsonExportConfigCard=new JSONExportConfigCard(exportSession);
		addCard(jsonExportConfigCard);
		jsonExportConfigCard.setup();
	
	
	}
	
	
	
	
}