package org.gcube.portlets.user.td.extractcodelistwidget.client;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ExtractCodelistWizardTDEntry implements EntryPoint {

	private ExtractCodelistMessages msgs;

	public void onModuleLoad() {
		initMessages();
		SimpleEventBus eventBus = new SimpleEventBus();
		TRId trId = new TRId("10");
		ExtractCodelistWizardTD extractWizard = new ExtractCodelistWizardTD(
				trId, msgs.extractCodelistWizardHead(), eventBus);
		Log.info(extractWizard.getId());
	}

	protected void initMessages() {
		msgs = GWT.create(ExtractCodelistMessages.class);
	}

}
