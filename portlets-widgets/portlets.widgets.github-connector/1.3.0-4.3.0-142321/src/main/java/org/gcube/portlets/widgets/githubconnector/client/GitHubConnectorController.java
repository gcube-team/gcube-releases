package org.gcube.portlets.widgets.githubconnector.client;

import org.gcube.portlets.widgets.githubconnector.client.rpc.GitHubConnectorServiceAsync;
import org.gcube.portlets.widgets.githubconnector.client.util.GWTMessages;
import org.gcube.portlets.widgets.githubconnector.shared.exception.ExpiredSessionServiceException;
import org.gcube.portlets.widgets.githubconnector.shared.session.UserInfo;


import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class GitHubConnectorController {
	private UserInfo userInfo;
	

	public GitHubConnectorController() {
		init();
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}


	private void init() {
		bind();
		callHello();
		checkSession();
	}

	private void checkSession() {
		// if you do not need to something when the session expire
		//CheckSession.getInstance().startPolling();
	}

	private void sessionExpiredShow() {
		//CheckSession.showLogoutDialog();
	}

	private void sessionExpiredShowDelayed() {
		Timer timeoutTimer = new Timer() {
			public void run() {
				sessionExpiredShow();

			}
		};
		int TIMEOUT = 3; // 3 second timeout

		timeoutTimer.schedule(TIMEOUT * 1000); // timeout is in milliseconds

	}

	private void callHello() {
		GitHubConnectorServiceAsync.INSTANCE
				.hello(new AsyncCallback<UserInfo>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("No valid user found: " + caught.getMessage());
						if (caught instanceof ExpiredSessionServiceException) {
							GWTMessages.alert("Error", "Expired Session",-1);
							sessionExpiredShowDelayed();
							
						} else {
							GWTMessages.alert(
									"Error",
									"No user found: "
											+ caught.getLocalizedMessage(),-1);
						}
					}

					@Override
					public void onSuccess(UserInfo result) {
						userInfo = result;
						GWT.log("Hello: " + userInfo.getUsername());
					
					}

				});

	}
	
	


	private void bind() {

	

	}

	

}
