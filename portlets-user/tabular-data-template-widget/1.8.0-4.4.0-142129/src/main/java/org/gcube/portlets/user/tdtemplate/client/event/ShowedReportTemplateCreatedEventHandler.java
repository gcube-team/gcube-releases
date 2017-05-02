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
public interface ShowedReportTemplateCreatedEventHandler extends EventHandler{

	/**
	 * On showed report.
	 *
	 * @param showedReportTemplateCreatedEvent the showed report template created event
	 */
	void onShowedReport(
			ShowedReportTemplateCreatedEvent showedReportTemplateCreatedEvent);

}
