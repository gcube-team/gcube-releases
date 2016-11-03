/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface SaveAsTemplateCreatedEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 16, 2015
 */
public interface SaveAsTemplateCreatedEventHandler extends EventHandler{

	/**
	 * On save as template.
	 *
	 * @param saveAsTemplateCreatedEvent the save as template created event
	 */
	void onSaveAsTemplate(SaveAsTemplateCreatedEvent saveAsTemplateCreatedEvent);

}
