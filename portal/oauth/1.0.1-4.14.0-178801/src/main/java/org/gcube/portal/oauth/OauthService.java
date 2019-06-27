package org.gcube.portal.oauth;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.DatatypeConverter;

import org.gcube.common.authorization.library.ClientType;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.oauth.cache.CacheBean;
import org.gcube.portal.oauth.cache.CacheCleaner;
import org.gcube.portal.oauth.input.PushCodeBean;
import org.gcube.portal.oauth.output.AccessTokenBeanResponse;
import org.gcube.portal.oauth.output.AccessTokenErrorResponse;
import org.gcube.smartgears.Constants;
import org.slf4j.LoggerFactory;


@Path("/v2")
@Singleton
public class OauthService {

	public static final String OAUTH_TOKEN_GET_METHOD_NAME_REQUEST = "access-token";
	private static final String GRANT_TYPE_VALUE = "authorization_code";

	private static final String AUTHORIZATION_HEADER = "Authorization";
	
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OauthService.class);

	/**
	 * This map contains couples <code, {qualifier-token, insert time, scope, redirect uri, client id}>
	 */
	private Map<String, CacheBean> entries;

	/**
	 * Cleaner thread
	 */
	CacheCleaner cleaner;

	/**
	 * Since this is a singleton sub-service, there will be just one call to this constructor and one running thread
	 * to clean up expired codes.
	 */
	public OauthService() {
		logger.info("Singleton gcube-oauth service built.");
		entries = new ConcurrentHashMap<String, CacheBean>();
		cleaner = new CacheCleaner(entries);
		cleaner.start();
	}

	@Override
	protected void finalize(){
		if(cleaner != null)
			cleaner.interrupt();
	}

	/**
	 * Used to check that the token type is of type user
	 * @param clientType 
	 * @param token
	 * @return
	 */
	private boolean checkIsQualifierTokenType(ClientType clientType){
		return clientType.equals(ClientType.USER);
	}

	/**
	 * Used to check that the token type is of type application
	 * @param clientType 
	 * @param token
	 * @return
	 */
	private boolean checkIsapplicationTokenType(ClientType clientType){
		return clientType.equals(ClientType.EXTERNALSERVICE);
	}

	@GET
	@Path("check")
	@Produces(MediaType.TEXT_PLAIN)
	public Response checkService(){
		return Response.status(Status.OK).entity("Ready!").build();
	}


	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("push-authentication-code")
	/**
	 * The portal will push a qualified token together a code 
	 * @return Response with status 201 if the code has been saved correctly
	 */
	public Response pushAuthCode(PushCodeBean bean) {

		logger.info("Request to push ");

		Caller caller = AuthorizationProvider.instance.get();
		String token = SecurityTokenProvider.instance.get();
		Status status = Status.CREATED;

		if(!checkIsQualifierTokenType(caller.getClient().getType())){
			status = Status.FORBIDDEN;
			logger.warn("Trying to access users method via a token different than USER is not allowed");
			return Response.status(status).entity("{\"error\"=\"Trying to access push-authentication-code method via a token different than USER is not allowed\"").build();
		}else{

			// check parameters
			String code = bean.getCode();
			String clientId = bean.getClientId();
			String redirectUri = bean.getRedirectUri();

			if(code == null || code.isEmpty())
				return Response.status(Status.BAD_REQUEST).
						entity("{\"error\"=\"'code' cannot be null or missing\"").build();

			if(clientId == null || clientId.isEmpty())
				return Response.status(Status.BAD_REQUEST).
						entity("{\"error\"=\"'client_id' cannot be null or missing\"").build();

			if(redirectUri == null || redirectUri.isEmpty())
				return Response.status(Status.BAD_REQUEST).
						entity("{\"error\"=\"'redirect_uri' cannot be null or missing\"").build();

			logger.info("Saving entry defined by " + bean + " in cache, token is " + token.substring(0, 10) + "***************");
			entries.put(code, new CacheBean(token, ScopeProvider.instance.get(), redirectUri, clientId, System.currentTimeMillis()));
			return Response.status(status).build();
		}

	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Path(OAUTH_TOKEN_GET_METHOD_NAME_REQUEST)
	/**
	 * The method should accept input values or in a json object or as FormParam. The request is validated here and not from SmartGears.
	 * @param requestInJson
	 * @param clientId
	 * @param clientSecret
	 * @param redirectUri
	 * @param code
	 * @param grantType
	 * @return
	 */
	public Response tokenRequest(
			@FormParam("client_id") String clientId,
			@FormParam("client_secret") String clientSecret, // i.e., application token
			@FormParam("redirect_uri") String redirectUri,
			@FormParam("code") String code,
			@FormParam("grant_type") String grantType, // it must always be equal to "authorization_code"
			@Context HttpServletRequest request
			){

		Status status = Status.BAD_REQUEST;
		logger.info("Request to exchange code for token");
		
		

		try{
			CredentialsBean credentials = new CredentialsBean(clientId, clientSecret);
			
			if (clientId == null) 
				credentials = getCredentialFromBasicAuthorization(request);
			else if (request.getHeader(AUTHORIZATION_HEADER)!=null)
				throw new Exception("the client MUST NOT use more than one authentication method");
			
			logger.info("Params are client_id = " + credentials.getClientId() + ", client_secret = " + credentials.getClientSecret() +
					"*******************"+ ", redirect_uri = " +redirectUri + ", code = " + code + "*******************" + ", grant_type = " + grantType);
			
			// check if something is missing
			String errorMessage = checkRequest(credentials, redirectUri, code, grantType, request);
			
			if(errorMessage != null){
				logger.error("The request fails because of " + errorMessage);
				return Response.status(status).entity(new AccessTokenErrorResponse(errorMessage, null)).build();
			}else{
				logger.info("The request is ok");
				String tokenToReturn = entries.get(code).getToken();
				String scope = entries.get(code).getScope();
				status = Status.OK;
				return Response.status(status).entity(new AccessTokenBeanResponse(tokenToReturn, scope)).build();
			}
		}catch(Exception e){
			logger.error("Failed to perform this operation", e);
			status = Status.BAD_REQUEST;
			return Response.status(status).entity(new AccessTokenErrorResponse("invalid_request", null)).build();
		}
	}

	private CredentialsBean getCredentialFromBasicAuthorization(HttpServletRequest request) {
		String basicAuthorization = request.getHeader(AUTHORIZATION_HEADER);
		String base64Credentials = basicAuthorization.substring("Basic".length()).trim();
		String credentials = new String(DatatypeConverter.parseBase64Binary(base64Credentials));
		// credentials = username:password
		String[] splitCreds = credentials.split(":");
		String clientId = URLDecoder.decode(splitCreds[0]);
		String clientSecret = URLDecoder.decode(splitCreds[1]);
		return new CredentialsBean(clientId, clientSecret);
		
	}

	/**
	 * Check request parameters
	 * @param clientId
	 * @param clientSecret
	 * @param redirectUri
	 * @param code
	 * @param grantType
	 * @return see https://tools.ietf.org/html/rfc6749#section-5.2
	 */
	private String checkRequest(CredentialsBean credentials,
			String redirectUri, String code, String grantType, HttpServletRequest request) {
		try{
			if(credentials.getClientId() == null || credentials.getClientSecret() == null || redirectUri == null || code == null || grantType == null ) 
				return "invalid_request";
			if(credentials.getClientId().isEmpty() || credentials.getClientSecret().isEmpty() || redirectUri.isEmpty() || code.isEmpty() || grantType.isEmpty())
				return "invalid_request";
			if(!checkIsapplicationTokenType(authorizationService().get(credentials.getClientSecret()).getClientInfo().getType())) // it is not an app token or it is not a token
				return "invalid_client";
			if(!entries.containsKey(code) || CacheBean.isExpired(entries.get(code)))
				return "invalid_grant";
			CacheBean entry = entries.get(code);
			if(!entry.getRedirectUri().equals(redirectUri) || !entry.getClientId().equals(credentials.getClientId()))
				return "invalid_grant";
			if(!grantType.equals(GRANT_TYPE_VALUE))
				return "unsupported_grant_type";
			return null;
		}catch(Exception e){
			logger.error("Failed to check the correctness of the request", e);
			return "invalid_request";
		}
	}
}
