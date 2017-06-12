package org.gcube.portlets.admin.createusers.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * The main module that contains the entry point of the portlet.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CreateUsers implements EntryPoint {

	@Override
	public void onModuleLoad() {
		RootPanel.get("create-users-container").add(new CreateUsersPanel());
	}
}