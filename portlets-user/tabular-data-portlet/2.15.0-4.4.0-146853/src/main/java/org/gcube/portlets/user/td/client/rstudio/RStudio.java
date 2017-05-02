package org.gcube.portlets.user.td.client.rstudio;

import org.gcube.portal.clientcontext.client.GCubeClientContext;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * RStudio Widget
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class RStudio {
	private static final String TAB_RESOURCE_ID_PARAMETER = "TabResourceId";
	private TRId trId;
	private EventBus eventBus;

	public RStudio(TRId trId, EventBus eventBus) {
		this.trId = trId;
		this.eventBus = eventBus;
		callCheckSession();

	}

	private void callRStudioServlet() {
		Log.debug("Request: " + trId);

		String url = GWT.getModuleBaseURL() + "TDRStudioServlet?"
				+ TAB_RESOURCE_ID_PARAMETER + "=" + trId.getId() + "&"
				+ Constants.CURR_GROUP_ID + "="
				+ GCubeClientContext.getCurrentContextId();
		Log.debug("Server URL: " + url);
		Window.open(url, "RStudio", "");

	}

	private void callCheckSession() {
		TDGWTServiceAsync.INSTANCE.hello(new AsyncCallback<UserInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.info("No valid user found: " + caught.getMessage());
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(
							SessionExpiredType.EXPIREDONSERVER));
				} else {
					eventBus.fireEvent(new SessionExpiredEvent(
							SessionExpiredType.EXPIREDONSERVER));
				}
			}

			@Override
			public void onSuccess(UserInfo result) {
				Log.info("Hello: " + result.getUsername());
				callRStudioServlet();
			}

		});

	}

}
