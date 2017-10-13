package org.gcube.portal.social.networking.ws.utils;

import org.gcube.common.authorization.library.ClientType;
import org.gcube.common.authorization.library.utils.Caller;


/**
 * Tokens utils methods
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class TokensUtils {
	
	// a user context token (not qualified) has as qualifier the word "TOKEN"
	private static final String DEFAULT_QUALIFIER_USER_TOKEN = "TOKEN";

	/**
	 * Check if it is a service token
	 * @return a boolean value
	 */
	public static boolean isServiceToken(Caller caller){

		return caller.getClient().getType().equals(ClientType.SERVICE);

	}

	/**
	 * Check if it is an application token
	 * @return a boolean value
	 */
	public static boolean isApplicationToken(Caller caller){

		return caller.getClient().getType().equals(ClientType.EXTERNALSERVICE);

	}

	/**
	 * Check if it is a container token
	 * @return a boolean value
	 */
	public static boolean isContainerToken(Caller caller){

		return caller.getClient().getType().equals(ClientType.CONTAINER);

	}

	/**
	 * Check if it is a user token
	 * @return a boolean value
	 */
	public static boolean isUserToken(Caller caller){

		return caller.getClient().getType().equals(ClientType.USER);

	}

	/**
	 * Check if it is a user token (not qualified)
	 * @return a boolean value
	 */
	public static boolean isUserTokenDefault(Caller caller){

		return caller.getClient().getType().equals(ClientType.USER) && caller.getTokenQualifier().equals(DEFAULT_QUALIFIER_USER_TOKEN);

	}

	/**
	 * Check if it is a user token (qualified)
	 * @return a boolean value
	 */
	public static boolean isUserTokenQualified(Caller caller){

		return caller.getClient().getType().equals(ClientType.USER) && !caller.getTokenQualifier().equals(DEFAULT_QUALIFIER_USER_TOKEN);

	}

}
