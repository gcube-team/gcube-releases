package org.gcube.portlets.user.tokengenerator.client;

import org.gcube.portlets.user.tokengenerator.shared.UserBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async version of the rpc methods.
 * @author Costantino Perciante, costantino.perciante@isti.cnr.it
 */
public interface TokenServiceAsync {

	void getServiceToken(AsyncCallback<UserBean> callback);

}
