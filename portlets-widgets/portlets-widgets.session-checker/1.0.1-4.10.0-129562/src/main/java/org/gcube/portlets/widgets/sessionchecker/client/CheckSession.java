package org.gcube.portlets.widgets.sessionchecker.client;

import java.util.HashMap;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.widgets.sessionchecker.client.bundle.CheckSessionBundle;
import org.gcube.portlets.widgets.sessionchecker.client.elements.Div;
import org.gcube.portlets.widgets.sessionchecker.client.event.SessionTimeoutEvent;
import org.gcube.portlets.widgets.sessionchecker.shared.SessionInfoBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CheckSession {
	private final int MILLI_SECONDS = 55 * 1000; //(milli)seconds

	//for css and images
	private static CheckSessionBundle images = GWT.create(CheckSessionBundle.class);

	static {
		CheckSessionBundle.INSTANCE.css().ensureInjected();
	}
	/**
	 * the eventbus where to launch the events 
	 */
	private HandlerManager eventBus = null;
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final SessionCheckerServiceAsync checkerService = GWT.create(SessionCheckerService.class);

	private String username;
	private String scope;
	private Timer t;

	//we give the user the possibility to show the dialog or not
	private boolean showSessionExpiredDialog = true;

	//needed to mask the page when session id up!
	private Div maskDiv = new Div();

	private static CheckSession singleton;
	/**
	 * use this method if you want to get an event when session expires
	 * @param eventBus your GWT webapp instance of {@link HandlerManager}
	 */
	public static CheckSession getInstance(HandlerManager eventBus) {
		if (singleton == null)
			singleton = new CheckSession(eventBus);
		return singleton;
	}
	/**
	 * method with no events launching when session expires
	 */
	public static CheckSession getInstance() {
		if (singleton == null)
			singleton = new CheckSession();
		return singleton;
	}
	/**
	 * use this constructor if you want to get an event when session expires
	 * @param eventBus GWT webapp instance of {@link HandlerManager}
	 */
	private CheckSession(HandlerManager eventBus) {	
		this();
		this.eventBus = eventBus;
	}
	/**
	 * constructor with no events launching when session expires
	 */
	private CheckSession() {	

		addMaskDiv2DOM();
		maskDiv.setStyleName("div-mask");

		//polling timer
		t = new Timer() {			
			@Override
			public void run() {
				checkerService.checkSession(new AsyncCallback<SessionInfoBean>() {					
					@Override
					public void onSuccess(SessionInfoBean result) {
						if (result != null) {

							username = result.getUsername();
							scope = result.getScope();
							boolean userValid = ( username != null) ? true : false;
							boolean scopeValid = (scope != null) ? true : false;

							if (! (userValid && scopeValid) ) { //if session expired
								mask(true);
								stopPolling();
								if (showSessionExpiredDialog) {
									showLogoutDialog();									
								} else {
									if (eventBus == null)
										throw new NullPointerException("Hey, it seems you chose to handle yourself session expiration "
												+ "but also not to get informed about it (eventbus is null) what's the point then?");
								}
								if (eventBus != null) {
									eventBus.fireEvent(new SessionTimeoutEvent(result));
								}
							}
							else if (result.isDevMode()) {
								GWT.log("Stopping polling because i think you're in development mode and not in a real portal");
								stopPolling();
							}
						}	
						else {
							GWT.log("result null");
							stopPolling();
						}
					}					
					@Override
					public void onFailure(Throwable caught) {
						mask(true);
						stopPolling();
						showLogoutDialog();
					}
				});
			}
		};	
	}

	private void addMaskDiv2DOM() {	
		RootPanel.get().insert(maskDiv, 0);
	}
	/**
	 * set visible the masking div setting/unsetting css display property
	 * @param mask
	 */
	private void mask(boolean mask) {
		GWT.log("Masking");
		if (mask) 
			maskDiv.getElement().getStyle().setDisplay(Display.BLOCK);
		else
			maskDiv.getElement().getStyle().setDisplay(Display.NONE);			
	}
	/**
	 * shows the logout dialog
	 */
	public static void showLogoutDialog() {
		showLogoutDialog(null);
	}

	/**
	 * shows the logout dialog with redirection appended with parametersMap
	 * @param paramsMap a map containing the attrs and related values of what you want after the ?
	 *	e.g. aUrl?name=foo&lastname=fie
	 */
	public static void showLogoutDialog(HashMap<String, String> paramsMap) {
		String href = "javascript:location.reload();";

		String params = "?";
		if (! (paramsMap == null || paramsMap.isEmpty()) ) {
			for (String attr : paramsMap.keySet()) {
				params += attr+"="+paramsMap.get(attr)+"&";
			}

			href = Window.Location.getHref();
			if (Window.Location.getHref().contains("?")) 
				href = href.substring(0, href.indexOf("?"));

			href += params;
		}
		
		GCubeDialog dlg = new GCubeDialog();
		dlg.setText("Your Session Expired!");

		VerticalPanel topPanel = new VerticalPanel();
		topPanel.setPixelSize(420, 290);

		topPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);

		HTML toShow = new HTML("<div style=\"margin-top: 40px;\">"
				+ "<img style=\"margin: 0; vertical-align: middle; \" src='" + images.expired().getSafeUri().asString() + "'>"
				+ "</div><div style=\"font-size: 18px; height: 20px; padding-top: 20px;\">"
				+ "Please try <a href=\""+href+"\">reload</a> this page or <a href=\"/c/portal/logout\">logout</div>");


		topPanel.add(toShow);
		dlg.add(topPanel);
		dlg.center();
		dlg.show();
	}


	public String getUsername() {
		return username;
	}

	public String getScope() {
		return scope;
	}	
	/**
	 * use to start checking if the session expired
	 */
	public void startPolling() {
		t.scheduleRepeating(MILLI_SECONDS);	
	}
	/**
	 * use to stop checking if the session expired
	 */
	public void stopPolling() {
		t.cancel();
	}	

	public boolean isShowSessionExpiredDialog() {
		return showSessionExpiredDialog;
	}
	public void setShowSessionExpiredDialog(boolean showSessionExpiredDialog) {
		this.showSessionExpiredDialog = showSessionExpiredDialog;
	}
}
