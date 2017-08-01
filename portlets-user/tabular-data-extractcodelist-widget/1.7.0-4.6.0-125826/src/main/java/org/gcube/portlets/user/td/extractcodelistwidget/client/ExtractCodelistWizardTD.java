package org.gcube.portlets.user.td.extractcodelistwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.extract.ExtractCodelistSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;

import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 */
public class ExtractCodelistWizardTD extends WizardWindow {

	private ExtractCodelistSession exportSession;

	/**
	 * 
	 * @param title
	 * @param eventBus
	 */
	public ExtractCodelistWizardTD(TRId trId, String title, EventBus eventBus) {
		super(title, eventBus);

		exportSession = new ExtractCodelistSession();
		exportSession.setTrId(trId);
		SourceColumnsSelectionCard sourceColumnsSelectionCard = new SourceColumnsSelectionCard(
				exportSession);
		addCard(sourceColumnsSelectionCard);
		sourceColumnsSelectionCard.setup();

	}

}