package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface ShowUserGroupsEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 23, 2016
 */
public interface ShowUserGroupsEventHandler extends EventHandler {


	/**
	 * On show groups.
	 *
	 * @param showUserDatasetsEvent the show user datasets event
	 */
	void onShowGroups(ShowUserGroupsEvent showUserDatasetsEvent);

}
