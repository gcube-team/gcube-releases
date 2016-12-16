package org.gcube.portlets.admin.vredefinition.client;

import org.gcube.portlets.user.gcubewidgets.client.ClientScopeHelper;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class VREDefinition implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		ClientScopeHelper.getService().setScope(Location.getHref(), new AsyncCallback<Boolean>() {

			public void onSuccess(Boolean result) {
				RootPanel.get("VREDefinitionDIV").add(new VREDefinitionPanel());
			}

			public void onFailure(Throwable caught) {
				AlertBlock errorScope = new AlertBlock(AlertType.ERROR);
				errorScope.setClose(false);
				errorScope.setText("Sorry, something wrong happened. Try to refresh the page or logout.");
				RootPanel.get("VREDefinitionDIV").add(errorScope);
			}
		});
	}
}
