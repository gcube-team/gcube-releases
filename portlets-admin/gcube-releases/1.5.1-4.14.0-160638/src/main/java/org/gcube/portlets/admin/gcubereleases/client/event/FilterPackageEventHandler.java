package org.gcube.portlets.admin.gcubereleases.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface FilterPackageEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public interface FilterPackageEventHandler extends EventHandler {
	
	/**
	 * On filter package.
	 *
	 * @param accountingHistoryEvent the accounting history event
	 */
	void onFilterPackage(FilterPackageEvent accountingHistoryEvent);
}