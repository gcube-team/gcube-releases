package org.gcube.portlets.user.tokengenerator.client;

import java.util.List;

import org.gcube.portlets.user.tokengenerator.shared.QualifiedToken;
import org.gcube.portlets.user.tokengenerator.shared.TokenBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("tokenservice")
public interface TokenService extends RemoteService {
	
	/**
	 * Get the standard token for this scope
	 * @return Token on success, null otherwise
	 */
	TokenBean getServiceToken();
	
	/**
	 * Retrieve the qualified tokens, if any.
	 * @return Tokens on success, null otherwise
	 */
	List<QualifiedToken> getQualifiedTokens(); 
	
	/**
	 * Create a new qualified token
	 */
	QualifiedToken createQualifiedToken(String qualifier);
}
