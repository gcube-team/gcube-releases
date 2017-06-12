package org.gcube.portlets.admin.authportletmanager.client;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

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
				loadController();
			}
		});

	}
	
	

	protected void loadController() {
		rootPanel = new AuthManagerController();
	}
}

