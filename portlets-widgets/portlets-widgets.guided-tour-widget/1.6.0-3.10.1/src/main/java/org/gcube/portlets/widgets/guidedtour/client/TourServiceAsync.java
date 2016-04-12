package org.gcube.portlets.widgets.guidedtour.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>TourService</code>.
 */
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 1.0 Feb 1st 2012
 *
 */
public interface TourServiceAsync {
	void showTour(String portletUniqueId, AsyncCallback<Boolean> callback);

	void setNotShowItAgain(String portletUniqueId, AsyncCallback<Void> callback);
}
