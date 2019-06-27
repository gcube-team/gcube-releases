/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author francesco-mangiacrapa
 *
 */
public interface CompletedLoadDataSourceEventHandler extends EventHandler {

	public void onCompletedLoadDataSource(
			CompletedLoadDataSourceEvent completedLoadDataSourceEvent);

}
