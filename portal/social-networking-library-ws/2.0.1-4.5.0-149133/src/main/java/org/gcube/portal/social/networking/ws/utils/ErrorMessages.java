package org.gcube.portal.social.networking.ws.utils;


/**
 * Class that contains error messages to be returned in the HTTP responses.
 * @author Costantino Perciante at ISTI-CNR
 */
public class ErrorMessages {

	public static final String MISSING_TOKEN = "Missing token.";
	public static final String MISSING_PARAMETERS = "Missing request parameters.";
	public static final String INVALID_TOKEN = "Invalid token.";
	public static final String TOKEN_GENERATION_APP_FAILED = "Token generation failed.";
	public static final String NOT_APP_TOKEN  = "Invalid token: not belonging to an application.";
	public static final String NOT_APP_ID = "Invalid application id: it doesn't belong to an application.";
	public static final String NO_APP_PROFILE_FOUND = "There is no application profile for this app id/scope.";
	public static final String BAD_REQUEST = "Please check the parameter you passed, it seems a bad request";
	public static final String ERROR_IN_API_RESULT = "The error is reported into the 'message' field of the returned object";
	public static final String POST_OUTSIDE_VRE = "A post cannot be written into a context that is not a VRE";
}
