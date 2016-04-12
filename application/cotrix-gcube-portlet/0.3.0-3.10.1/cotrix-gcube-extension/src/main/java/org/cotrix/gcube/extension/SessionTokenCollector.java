package org.cotrix.gcube.extension;

import static org.cotrix.gcube.stubs.SessionToken.*;

import org.cotrix.security.LoginRequest;
import org.cotrix.security.TokenCollector;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
public class SessionTokenCollector implements TokenCollector {

	public static final String URL_TOKEN_ATTRIBUTE_NAME = "TOKEN";
	
	
	@Override
	public Object token(LoginRequest request) {
		
		String token = (String) request.getAttribute(URL_TOKEN_ATTRIBUTE_NAME);
		
		return token ==null ? null: valueOf(token) ;
	}
}
