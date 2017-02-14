package org.gcube.portlets.admin.createusers.client;

import org.gcube.portlets.user.gcubewidgets.client.ClientScopeHelper;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * The main module that contains the entry point of the portlet.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CreateUsers implements EntryPoint {

	@Override
	public void onModuleLoad() {

		ClientScopeHelper.getService().setScope(Location.getHref(), new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				RootPanel.get("create-users-container").add(new CreateUsersPanel());
			}				
			@Override
			public void onFailure(Throwable caught) {	
			}
		});
	}
}