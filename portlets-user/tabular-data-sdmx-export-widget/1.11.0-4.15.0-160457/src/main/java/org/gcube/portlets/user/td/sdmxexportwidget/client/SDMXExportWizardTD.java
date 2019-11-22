package org.gcube.portlets.user.td.sdmxexportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXExportSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;

import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 * 
 */
public class SDMXExportWizardTD extends WizardWindow {

	protected SDMXExportSession exportSession;

	/**
	 * 
	 * 
	 * @param title
	 *            Title
	 * @param eventBus
	 *            Event bus
	 */
	public SDMXExportWizardTD(String title, final EventBus eventBus) {
		super(title, eventBus);
		setWidth(550);
		setHeight(520);

		exportSession = new SDMXExportSession();

		
		SDMXRegistrySelectionCard sdmxRegistrySelectionCard = new SDMXRegistrySelectionCard(exportSession);
		addCard(sdmxRegistrySelectionCard);
		sdmxRegistrySelectionCard.setup();

	}

	

}