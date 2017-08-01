package org.gcube.portlets.user.td.replacebyexternalwidget.client;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ReplaceByExternalTDEntry  implements EntryPoint  {

	public void onModuleLoad() {
		TRId trId=new TRId();
		SimpleEventBus eventBus=new SimpleEventBus();
		ReplaceByExternalTD  replaceByExternalTDWizard= new ReplaceByExternalTD(trId, "Replace By External",eventBus); 
		Log.info(replaceByExternalTDWizard.getId());
	}
}
