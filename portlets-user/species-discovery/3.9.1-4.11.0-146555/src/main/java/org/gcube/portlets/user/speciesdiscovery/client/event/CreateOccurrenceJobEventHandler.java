/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public interface CreateOccurrenceJobEventHandler extends EventHandler {

	public void onCreateSpeciesJob(CreateOccurrenceJobEvent createOccurrenceJobEvent);

}
