/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 *
 */
public interface TemplateComplitedEventHandler extends EventHandler{
	/**
	 * @param templateValidEvent
	 */
	void onTemplateComplitedEvent(TemplateCompletedEvent templateValidEvent);

}
