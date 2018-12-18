package org.gcube.portlets.widgets.widgettour.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async version of the TourManagerServices
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public interface TourManagerServicesAsync {

	void setShowNextTime(String callerIdentifier, int versionNumber,
			boolean show, AsyncCallback<Boolean> callback);

	void isTourShowable(String callerIdentifier, int versionNumber,
			AsyncCallback<Boolean> callback);

}
