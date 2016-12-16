package org.gcube.portlets.user.tokengenerator.client;

import java.util.List;

import org.gcube.portlets.user.tokengenerator.shared.TokenBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async version of the TokenService's methods
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public interface TokenServiceAsync {

	void createQualifiedToken(String qualifier,
			AsyncCallback<TokenBean> callback);

	void getQualifiedTokens(AsyncCallback<List<TokenBean>> callback);

	void getServiceToken(AsyncCallback<TokenBean> callback);

	void createApplicationToken(String applicationIdentifier,
			AsyncCallback<TokenBean> callback);

	void getApplicationTokens(AsyncCallback<List<TokenBean>> callback);

}
