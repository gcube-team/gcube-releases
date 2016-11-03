package org.gcube.portal.social.networking.ws.filters;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.social.networking.ws.outputs.ResponseBean;
import org.slf4j.LoggerFactory;


/**
 * Requests filter: is invoked before any request reaches a service method
 * @author Costantino Perciante at ISTI-CNR
 */
@Provider
public class RequestsAuthFilter implements ContainerRequestFilter{

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RequestsAuthFilter.class);
	public static final String AUTH_TOKEN = "gcube-token";
	private static final String MISSING_OR_WRONG_TOKEN = "Invalid or missing gcube-token";

	@Context UriInfo info;

	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {

		logger.info("Intercepted request, checking if it contains authorization token");

		// if swagger json or yaml is requested, go ahead
		logger.info("Requested method is " + info.getAbsolutePath());
		String pathRequest = info.getAbsolutePath().toString();
		if(pathRequest.endsWith("swagger.json") || pathRequest.endsWith("swagger.yaml") 
				|| pathRequest.endsWith("/rest/") || pathRequest.endsWith("/rest")){
			logger.debug("No authorization is requested in this case, returning");
			return;
		}

		// check if the request contains gcube-token
		String tokenInHeader = null, tokenAsQueryParameter = null;
		MultivaluedMap<String, String> headers = requestContext.getHeaders();
		if( headers != null && headers.containsKey(AUTH_TOKEN))
			tokenInHeader = headers.get(AUTH_TOKEN).get(0);

		MultivaluedMap<String, String> queryParameters = requestContext.getUriInfo().getQueryParameters();
		if(queryParameters != null && queryParameters.containsKey(AUTH_TOKEN))
			tokenAsQueryParameter = queryParameters.get(AUTH_TOKEN).get(0);

		//if missing
		if(tokenAsQueryParameter == null && tokenInHeader == null){
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).entity(new ResponseBean(false, MISSING_OR_WRONG_TOKEN, null)).build());
			return;
		}

		AuthorizationEntry ae = null;

		if(tokenInHeader != null){
			logger.info("Token in " + tokenInHeader.substring(0, 5) + "********************");
			ae = validateToken(tokenInHeader);
		}else if(tokenAsQueryParameter != null){
			logger.info("Token is " + tokenAsQueryParameter.substring(0, 5) + "********************");
			ae = validateToken(tokenAsQueryParameter);
		}

		if(ae != null){
			logger.debug("Setting scope " + ae.getContext());
			AuthorizationProvider.instance.set(new Caller(ae.getClientInfo(), ae.getQualifier()));
			ScopeProvider.instance.set(ae.getContext());
			logger.info("Authorization entry set in thread local");
			return;
		}else
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).entity(new ResponseBean(false, MISSING_OR_WRONG_TOKEN, null)).build());

	}

	/**
	 * Validate token.
	 * @param token
	 * @return null if validation fails
	 */
	private static AuthorizationEntry validateToken(String token){
		AuthorizationEntry res = null;
		try {
			ScopeProvider.instance.set("/" + PortalContext.getConfiguration().getInfrastructureName());
			logger.debug("Validating token " + token);
			res = authorizationService().get(token);
			logger.debug("Token seems valid for scope " + res.getContext() + " and user " + res.getClientInfo().getId());
		} catch (Exception e) {
			logger.error("The token is not valid. This request will be rejected!!! (" + token + ")");
		}finally{
			ScopeProvider.instance.reset();
		}

		return res;
	}
}
