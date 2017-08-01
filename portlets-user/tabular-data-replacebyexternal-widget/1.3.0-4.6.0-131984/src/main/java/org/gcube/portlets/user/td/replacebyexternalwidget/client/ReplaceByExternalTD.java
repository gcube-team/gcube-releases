package org.gcube.portlets.user.td.replacebyexternalwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal.ReplaceByExternalSession;
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
public class ReplaceByExternalTD extends WizardWindow {
	private static final int WITHWIZARD = 800;
	private static final int HEIGHTWIZARD = 520;

	private ReplaceByExternalSession replaceByExternalSession;
	/**
	 * 
	 * @param trId
	 * @param title
	 * @param eventBus
	 */
	public ReplaceByExternalTD(TRId trId, String title, EventBus eventBus) {
		super(title, eventBus);
		Log.debug("ReplaceByExternalTD: " + trId);
		setWidth(WITHWIZARD);
		setHeight(HEIGHTWIZARD);

		replaceByExternalSession = new ReplaceByExternalSession();
		replaceByExternalSession.setTrId(trId);

		CurrentColumnSelectionCard currentColumnSelectionCard = new CurrentColumnSelectionCard(
				replaceByExternalSession);
		addCard(currentColumnSelectionCard);
		currentColumnSelectionCard.setup();
	}

	/**
	 * 
	 * @param trId
	 * @param title
	 * @param eventBus
	 */
	public ReplaceByExternalTD(TRId trId, String columnName, String title,
			EventBus eventBus) {
		super(title, eventBus);
		Log.debug("ReplaceByExternalTD: " + trId+" columnName: "+columnName);
		setWidth(WITHWIZARD);
		setHeight(HEIGHTWIZARD);
		replaceByExternalSession = new ReplaceByExternalSession();
		replaceByExternalSession.setTrId(trId);

		CurrentColumnSelectionCard currentColumnSelectionCard = new CurrentColumnSelectionCard(
				columnName, replaceByExternalSession);
		addCard(currentColumnSelectionCard);
		currentColumnSelectionCard.setup();
	}

	
}