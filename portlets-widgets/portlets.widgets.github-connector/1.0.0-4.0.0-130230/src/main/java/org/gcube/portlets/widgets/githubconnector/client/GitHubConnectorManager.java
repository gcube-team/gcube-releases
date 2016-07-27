package org.gcube.portlets.widgets.githubconnector.client;

import org.gcube.portlets.user.gcubewidgets.client.ClientScopeHelper;
import org.gcube.portlets.widgets.githubconnector.client.util.GWTMessages;
import org.gcube.portlets.widgets.githubconnector.client.wizard.event.WizardEvent;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class GitHubConnectorManager implements EntryPoint {

	// private static final String SM_DIV = "contentDiv";

	@SuppressWarnings("unused")
	private GitHubConnectorController TestController;

	/**
	 * {@inheritDoc}
	 */
	public void onModuleLoad() {

		/*
		 * Install an UncaughtExceptionHandler which will produce
		 * <code>FATAL</code> log messages
		 */

		// use deferred command to catch initialization exceptions in
		// onModuleLoad2
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
						if (result) {
							loadMainPanel();
						} else {
							GWTMessages
									.alert("Attention",
											"ClientScopeHelper has returned a false value!",-1);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						GWTMessages.alert("Error", "Error setting scope: "
								+ caught.getLocalizedMessage(),-1);
						caught.printStackTrace();
					}
				});

	}

	private void loadMainPanel() {
		GWT.log("GitHubConnectorManager");

		// Example
		WizardEvent.WizardEventHandler handler = new WizardEvent.WizardEventHandler() {

			@Override
			public void onResponse(WizardEvent event) {
				GWT.log("Wizard Response: " + event.getWizardEventType());

			}
		};

		GitHubConnectorWizard wizard = new GitHubConnectorWizard(
				"0cfde9e2-a6f0-451f-a048-adbd42d7d57f");
		wizard.addWizardEventHandler(handler);
		wizard.show();

		/*
		 * WaitDialog progressDialog=new WaitDialog(); progressDialog.show();
		 */

		/*
		 * WizardWindow wizardWindow = new WizardWindow("TestWindow");
		 * SimpleWizardCard simple1=new
		 * SimpleWizardCard("Test Title 1","Test Footer 1", "This is 1 card");
		 * wizardWindow.addCard(simple1); SimpleWizardCard simple2=new
		 * SimpleWizardCard("Test Title 2","Test Footer 2", "This is 2 card");
		 * wizardWindow.addCard(simple2); SimpleWizardCard simple3=new
		 * SimpleWizardCard("Test Title 3","Test Footer 3", "This is 3 card");
		 * wizardWindow.addCard(simple3);
		 * 
		 * wizardWindow.show();
		 */

	}
	/*
	 * private void bind() {
	 * 
	 * }
	 * 
	 * /**
	 * 
	 * @param mainWidget
	 */
	/*
	 * private void bindWindow(Widget mainWidget) { try { RootPanel root =
	 * RootPanel.get(SM_DIV); Log.info("Root Panel: " + root); if (root == null)
	 * { Log.info("Div with id " + SM_DIV + " not found, starting in dev mode");
	 * Viewport viewport = new Viewport(); viewport.setWidget(mainWidget);
	 * viewport.onResize(); RootPanel.get().add(viewport); } else {
	 * Log.info("Application div with id " + SM_DIV +
	 * " found, starting in portal mode"); PortalViewport viewport = new
	 * PortalViewport(); Log.info("Created Viewport");
	 * viewport.setEnableScroll(false); viewport.setWidget(mainWidget);
	 * Log.info("Set Widget"); Log.info("getOffsetWidth(): " +
	 * viewport.getOffsetWidth()); Log.info("getOffsetHeight(): " +
	 * viewport.getOffsetHeight()); viewport.onResize(); root.add(viewport);
	 * Log.info("Added viewport to root"); } } catch (Exception e) {
	 * e.printStackTrace(); Log.error("Error in attach viewport:" +
	 * e.getLocalizedMessage()); } }
	 */
}
