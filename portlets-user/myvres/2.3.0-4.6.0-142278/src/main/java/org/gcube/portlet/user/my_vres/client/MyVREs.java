package org.gcube.portlet.user.my_vres.client;

import org.gcube.portlet.user.my_vres.shared.AuthorizationBean;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * @author massi
 *
 */
public class MyVREs implements EntryPoint {
	public static final String GET_REDIRECTURI_PARAMETER = "redirect_uri";
	public static final String GET_STATE_PARAMETER = "state";
	public static final String GET_CONTEXT_PARAMETER = "scope";
	public static final String GET_CLIENT_ID_PARAMETER = "client_id";
	public static final String GET_CLIENT_SECRET_PARAMETER = "client_secret";
	public static final String GET_RESPONSE_TYPE_PARAMETER = "response_type";
	
	public static final String GET_AUTH_TOKEN_PARAMETER = "code";

	private final MyVREsServiceAsync myVREsService = GWT.create(MyVREsService.class);

	public void onModuleLoad() {
		//if no redirectUri is present acts normally
		if (Window.Location.getParameter(GET_REDIRECTURI_PARAMETER) == null)
			RootPanel.get("myVREsDIV").add(new VresPanel(null));
		else {
			handleAuthorisation();
		}


	}
	/**
	 * if the context is present proceed with the call and redirect otherwise display the VREs for scope selection
	 */
	private void handleAuthorisation() {
		final GetParameters params = getParameters();
		if (! params.redirectURI.startsWith("https")) {
			RootPanel.get("myVREsDIV").add(new HTML("ERROR: the redirectURI parameter must use HTTP Secure protocol (https)"));
			return;
		}
		if (params.context == null || params.context.compareTo("") == 0) {
			RootPanel.get("myVREsDIV").add(new VresPanel(params));
		}
		else {
			myVREsService.getOAuthTempCode(params.context, params.state, params.clientId, params.redirectURI, new AsyncCallback<AuthorizationBean>() {				
				@Override
				public void onSuccess(AuthorizationBean result) {
					if (result.isSuccess()) {						
						Location.assign(params.redirectURI+"?"
								+GET_AUTH_TOKEN_PARAMETER+"="+result.getOAuth2TemporaryCode()+"&"
								+GET_STATE_PARAMETER+"="+result.getState()+"&"
								+GET_RESPONSE_TYPE_PARAMETER+"=code");
					} else {
						HTML message = new HTML("There were issues in managing this request: " + result.getErrorDescription());
						message.setStyleName("portlet-msg-error");
						RootPanel.get("myVREsDIV").insert(message, 0);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					HTML message = new HTML("An error occurred in the server: " + caught.getMessage());
					message.setStyleName("portlet-msg-error");
					RootPanel.get("myVREsDIV").insert(message, 0);
				}
			});
		}

	}
	/**
	 * check if it has to show just one feed
	 * @return
	 */
	private GetParameters getParameters() {
		String redirectURI = Window.Location.getParameter(GET_REDIRECTURI_PARAMETER);
		String state = Window.Location.getParameter(GET_STATE_PARAMETER);
		String context = Window.Location.getParameter(GET_CONTEXT_PARAMETER);
		String clientId = Window.Location.getParameter(GET_CLIENT_ID_PARAMETER);
		
		return new GetParameters(redirectURI, state, context, clientId);
	}
}
