package org.gcube.portlets.user.td.unionwizardwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.tr.union.UnionSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;

import com.allen_sauer.gwt.log.client.Log;
import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class UnionWizardTD extends WizardWindow {
	private static final int WITHWIZARD = 800;
	private static final int HEIGHTWIZARD = 520;
	private UnionSession unionSession;

	/**
	 * 
	 * @param trId tabular resource id
	 * @param title wizard title
	 * @param eventBus event bus
	 */
	public UnionWizardTD(TRId trId, String title, EventBus eventBus) {
		super(title, eventBus);
		Log.debug("UnionWizardTD: " + trId);
		setWidth(WITHWIZARD);
		setHeight(HEIGHTWIZARD);

		unionSession = new UnionSession();
		unionSession.setTrId(trId);

		TabResourcesSelectionCard tabResourcesSelection = new TabResourcesSelectionCard(
				unionSession);
		addCard(tabResourcesSelection);
		tabResourcesSelection.setup();

	}

}