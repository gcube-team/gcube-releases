package org.gcube.common.authorizationservice;

import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.QualifiersList;
import org.gcube.common.authorizationservice.filters.AuthorizedCallFilter;
import org.gcube.common.authorizationservice.util.Constants;
import org.gcube.common.authorizationservice.util.TokenPersistence;

@Path("apikey")
@Slf4j
public class ApiKeyManager {

	@Inject
	TokenPersistence persistence;


	/**
	 * 
	 * Generates an api key.
	 * 
	 * @param serviceName the name of the client that will use this apikey
	 * @return the generated api key
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public String generateApiKey(@QueryParam(value="qualifier")String qualifier, @Context HttpServletRequest req) {
		try{

			AuthorizationEntry info = (AuthorizationEntry)req.getAttribute(AuthorizedCallFilter.AUTH_ATTRIBUTE);

			if (qualifier==null || qualifier.isEmpty() || qualifier.equals(Constants.DEFAULT_TOKEN_QUALIFIER)) 
				throw new IllegalArgumentException();

			log.info("generator called with user {} in context {} ",info.getClientInfo(), info.getContext());

			if (info.getClientInfo().getId().split(":").length>1) throw new Exception("invalid user id: "+info.getClientInfo());

			String token = persistence.getExistingToken(info.getClientInfo().getId(), info.getContext(), qualifier);

			if (token==null){
				token = UUID.randomUUID().toString();
				persistence.saveAuthorizationEntry(token, info.getContext(), info.getClientInfo(), qualifier, info.getClientInfo().getId());
			}

			return token;
		}catch(Exception e){
			log.error("error generating apikey ",e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error Generating ApiKey: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		} 
	}

	/**
	 * 
	 * retrieves the AuthorzationEntry connected to the specified token
	 * 
	 * @param token
	 * @return the authorization entry
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public QualifiersList retrieveApiKeys(@Context HttpServletRequest req) {
		log.info("calling getApiKey");
		try{
			AuthorizationEntry info = (AuthorizationEntry)req.getAttribute(AuthorizedCallFilter.AUTH_ATTRIBUTE);
			
			return new QualifiersList(persistence.getExistingApiKeys(info.getClientInfo().getId(), info.getContext()));
						
		}catch(Exception e){
			log.error("error retrieving apikey",e);
		}
		return null;
	}

	/**
	 * 
	 * removes an api key.
	 * 
	 * @param the key to remove
	 * @return 
	 */
	@Path("{key}")
	@DELETE
	public void removeApiKey(@PathParam(value="key")String key, @Context HttpServletRequest req) {
		try{

			//TODO


		}catch(Exception e){
			log.error("error generating apikey ",e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error Generating ApiKey: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		} 
	}


}
