package org.gcube.portlets.user.tokengenerator.client;

import java.util.List;

import org.gcube.portlets.user.tokengenerator.shared.QualifiedToken;
import org.gcube.portlets.user.tokengenerator.shared.TokenBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async version of the TokenService's methods
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public interface TokenServiceAsync {

	void createQualifiedToken(String qualifier,
			AsyncCallback<QualifiedToken> callback);

	void getQualifiedTokens(AsyncCallback<List<QualifiedToken>> callback);

	void getServiceToken(AsyncCallback<TokenBean> callback);

}
