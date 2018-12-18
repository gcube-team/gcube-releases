package org.gcube.portlets.widgets.wsexplorer.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface BreadcrumbInitEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 2, 2016
 */
public interface BreadcrumbInitEventHandler extends EventHandler {

	/**
	 * On breadcrumb init.
	 *
	 * @param breadcrumbInitEvent the breadcrumb init event
	 */
	void onBreadcrumbInit(BreadcrumbInitEvent breadcrumbInitEvent);
}