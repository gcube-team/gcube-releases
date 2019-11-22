package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Jul 8, 2014
 *
 */
public interface AddAdministratorEventHandler extends EventHandler {


	void onAddAdministrator(AddAdministratorEvent addAdministratorEvent);
}