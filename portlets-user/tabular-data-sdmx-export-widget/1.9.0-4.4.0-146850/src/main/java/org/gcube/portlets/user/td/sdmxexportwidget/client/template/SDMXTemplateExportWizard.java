package org.gcube.portlets.user.td.sdmxexportwidget.client.template;

import org.gcube.portlets.user.td.gwtservice.shared.sdmx.SDMXTemplateExportSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;

import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 *         Entry point classes define <code>onModuleLoad()</code>.
 * 
 */
public class SDMXTemplateExportWizard extends WizardWindow {

	private SDMXTemplateExportSession sdmxTemplateExportSession;
	private static final String WIDTH = "850px";
	private static final String HEIGHT = "530px";
	
	/**
	 * The id of the {@link CSVTarget} to use.
	 * 
	 * @param targetId
	 */

	public SDMXTemplateExportWizard(String title, final EventBus eventBus) {
		super(title, eventBus);
		setWidth(WIDTH);
		setHeight(HEIGHT);

		sdmxTemplateExportSession = new SDMXTemplateExportSession();

		SDMXTemplateExportRegistrySelectionCard sdmxTemplateExportRegistrySelectionCard = new SDMXTemplateExportRegistrySelectionCard(
				sdmxTemplateExportSession);
		addCard(sdmxTemplateExportRegistrySelectionCard);
		sdmxTemplateExportRegistrySelectionCard.setup();

	}
}