package org.gcube.portlets.user.td.resourceswidget.client.save;

import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.SaveResourceSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;

import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class SaveResourceWizard extends WizardWindow {

	private String WIZARDWIDTH = "844px";

	/**
	 * 
	 * @param saveResourceSession
	 * @param title
	 * @param eventBus
	 */
	public SaveResourceWizard(SaveResourceSession saveResourceSession,
			String title, EventBus eventBus) {
		super(title, eventBus);
		setWidth(WIZARDWIDTH);

		DestinationSelectionCard destinationSelectionCard = new DestinationSelectionCard(
				saveResourceSession);
		addCard(destinationSelectionCard);
		destinationSelectionCard.setup();

	}

}