/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface SearchEventHandler extends EventHandler {
	
	public void onSearch(SearchEvent event);

}
