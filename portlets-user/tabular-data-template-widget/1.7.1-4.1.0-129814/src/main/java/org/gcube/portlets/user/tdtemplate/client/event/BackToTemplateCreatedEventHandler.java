/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface ShowedReportTemplateCreatedEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 */
public interface BackToTemplateCreatedEventHandler extends EventHandler{

	/**
	 * On back.
	 *
	 * @param backToTemplateCreatedEvent the back to template created event
	 */
	void onBack(BackToTemplateCreatedEvent backToTemplateCreatedEvent);

}
