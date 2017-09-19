package org.gcube.portlets.widgets.sessionchecker.client;

import org.gcube.portlets.widgets.sessionchecker.shared.SessionInfoBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SessionCheckerServiceAsync {

	void checkSession(AsyncCallback<SessionInfoBean> callback);

}
