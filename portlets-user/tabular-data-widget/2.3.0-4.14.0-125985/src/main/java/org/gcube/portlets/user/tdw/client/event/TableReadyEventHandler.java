/**
 * 
 */
package org.gcube.portlets.user.tdw.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface TableReadyEventHandler extends EventHandler {
	
	public void onTableReady(TableReadyEvent event);

}
