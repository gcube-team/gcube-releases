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
public interface TemplateSelectedEventHandler extends EventHandler{
	
	void onTemplateSelectedEvent(TemplateSelectedEvent event);

}
