package org.gcube.portlets.user.td.openwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.tr.open.TDOpenSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;
import org.gcube.portlets.user.td.wizardwidget.client.dataresource.ResourceBundle;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TDOpen  extends WizardWindow   {
	protected static final int WITHWIZARD=820;
	protected static final int HEIGHTWIZARD=520;
	
	protected TDOpenSession tdOpenSession;
	private TDOpenMessages msgsTDOpen;
	
	/**
	 * The id of the {@link CSVTarget} to use.
	 * @param targetId
	 */
	
	public TDOpen(TRId trId,String title,EventBus eventBus)	{
		super(title,eventBus);
		Log.info("TDOpen: "+title+", current tr: "+trId);
		this.msgsTDOpen = GWT.create(TDOpenMessages.class);
		
		setWidth(WITHWIZARD);
		setHeight(HEIGHTWIZARD);
		nextButton.setText(msgsTDOpen.buttonOpenLabel());
		nextButton.setIcon(ResourceBundle.INSTANCE.wizardGo());
		nextButton.setIconAlign(IconAlign.RIGHT);
		
		setEnableNextButton(false);
		tdOpenSession= new TDOpenSession();
		
		
		TabResourcesSelectionCard tabResourcesSelection= new TabResourcesSelectionCard(trId,tdOpenSession);
		addCard(tabResourcesSelection);
		tabResourcesSelection.setup();
		
	}
		
}