package org.gcube.portlets.admin.authportletmanager.client;
import org.gcube.portlets.user.gcubewidgets.client.ClientScopeHelper;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */
public class AuthManager implements EntryPoint {
	@SuppressWarnings("unused")
	private AuthManagerController rootPanel;

	/**
	 * {@inheritDoc}
	 */
	public void onModuleLoad() {

		GWT.log("AuthManager - Start Portlet");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				loadScope();
			}
		});

	}
	private void loadScope() {
		ClientScopeHelper.getService().setScope(Location.getHref(),
				new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				GWT.log("AuthManager - Scope Location set!");
				if (result) {
					loadController();
				} else {
					GWT.log("AuthManager - Attention ClientScopeHelper has returned a false value!");
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("AuthManager - Error setting scope: "
						+ caught.getLocalizedMessage());
				caught.printStackTrace();
			}
		});
	}

	protected void loadController() {
		rootPanel = new AuthManagerController();
	}
}

