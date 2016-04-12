package org.gcube.portlets.user.td.unionwizardwidget.client;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class UnionWizardTDEntry implements EntryPoint {

	public UnionWizardMessages msgs = GWT.create(UnionWizardMessages.class);

	public void onModuleLoad() {
		TRId trId = new TRId();
		SimpleEventBus eventBus = new SimpleEventBus();
		UnionWizardTD unionWizard = new UnionWizardTD(trId,
				msgs.unionWizardHead(), eventBus);
		Log.info(unionWizard.getId());
	}
}
