package org.gcube.portlets.user.workspaceexplorerapp.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface BreadcrumbClickEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 3, 2015
 */
public interface BreadcrumbClickEventHandler extends EventHandler {
	
	/**
	 * On breadcrumb click.
	 *
	 * @param breadcrumbClickEvent the breadcrumb click event
	 */
	void onBreadcrumbClick(BreadcrumbClickEvent breadcrumbClickEvent);
}