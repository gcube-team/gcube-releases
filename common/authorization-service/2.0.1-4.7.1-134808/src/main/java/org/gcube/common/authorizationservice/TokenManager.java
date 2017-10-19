package org.gcube.common.authorizationservice;

import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
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
import org.gcube.common.authorization.library.ExternalServiceList;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.authorization.library.provider.ExternalServiceInfo;
import org.gcube.common.authorization.library.provider.ServiceInfo;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.authorizationservice.filters.AuthorizedCallFilter;
import org.gcube.common.authorizationservice.util.Constants;
import org.gcube.common.authorizationservice.util.TokenPersistence;

@Path("token")
@Slf4j
public class TokenManager {

	@Inject
	TokenPersistence persistence;


	/**
	 * 
	 * retrieves the AuthorzationEntry connected to the specified token
	 * 
	 * @param token
	 * @return the authorization entry
	 */
	@GET
	@Path("{token}")
	@Produces(MediaType.APPLICATION_XML)
	public AuthorizationEntry retrieveToken(@NotNull @PathParam("token") String token ) {

		log.info("token retreiver called with token {}",token);

		AuthorizationEntry info = persistence.getAuthorizationEntry(token);

		log.info("info retrieved {}",info);

		if (info == null){
			log.error("token {} not found ", token);
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
					.entity("token "+token+" not found").type(MediaType.TEXT_PLAIN).build());
		}

		//TODO: re-add it with common-scope 2.0
		/* 
		try{
			ServiceMap map =   .instance.getMap(info.getContext());
			info.setMap(map);
		}catch(Exception e){
			log.error("error retrieving map for {}", info.getContext(), e);
			throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error retrieving map").type(MediaType.TEXT_PLAIN).build());
		}*/

		log.debug("returning info {}", info);
		return info;

	}

	/**
	 * 
	 * retrieves the AuthorzationEntry connected to the specified token
	 * 
	 * @param token
	 * @return the authorization entry
	 */
	@GET
	@Path("resolve/{user}")
	public String getTokenByUserAndContext(@NotNull @PathParam("user") String user, @QueryParam("context") String context ) {

		log.info("resolving token for user {} in context {}",user, context);
		
		if (context==null){
			log.error("null context found");
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
					.entity("null context found").type(MediaType.TEXT_PLAIN).build());
		}
		
		String token = persistence.getExistingToken(user, context, Constants.DEFAULT_TOKEN_QUALIFIER);

		
		if (token == null){
			log.error("token {} not found ", token);
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
					.entity("token for user "+user+" in context "+context+" not found").type(MediaType.TEXT_PLAIN).build());
		}

		//TODO: re-add it with common-scope 2.0
		/* 
		try{
			ServiceMap map =   .instance.getMap(info.getContext());
			info.setMap(map);
		}catch(Exception e){
			log.error("error retrieving map for {}", info.getContext(), e);
			throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error retrieving map").type(MediaType.TEXT_PLAIN).build());
		}*/

		return token;

	}
	
	/**
	 * 
	 * Generates a token for a user (saving the passed roles) if it doesn't exist yet.
	 * 
	 * @param userName
	 * @param roles
	 * @return the generated token or the token related to the user (if it was already created)
	 */
	@Path("user")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public String generateUserToken(UserInfo clientId, 
			@NotNull @QueryParam("context") String context) {

		try{

			log.info("generator called with user {} in context {} ",clientId, context);

			if (clientId.getId().split(":").length>1) throw new Exception("invalid user id: "+clientId.getId());

			String token = persistence.getExistingToken(clientId.getId(), context, Constants.DEFAULT_TOKEN_QUALIFIER);

			if (token==null){
				token = UUID.randomUUID().toString();
				persistence.saveAuthorizationEntry(token, context, clientId , Constants.DEFAULT_TOKEN_QUALIFIER, null);
			}

			return token;
		}catch(Exception e){
			log.error("error generating token ",e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error Generating Token: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		} 
	}

	/**
	 * 
	 * Generates a token for a service if it doesn't exist yet.
	 * 
	 * @param userName
	 * @param roles
	 * @return the generated token or the token related to the user (if it was already created)
	 */
	@Path("service")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public String generateServiceToken(ServiceInfo serviceInfo, 
			@Context HttpServletRequest req) {
		try{
			AuthorizationEntry authInfo = (AuthorizationEntry)req.getAttribute(AuthorizedCallFilter.AUTH_ATTRIBUTE);
			log.info("generator called with service {} in context {} ",serviceInfo.getId(), authInfo.getContext());
			return generateTokenForServiceInfo(serviceInfo, authInfo);
		}catch(Exception e){
			log.error("error generating token ",e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error Generating Token: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		} 
	}




	/**
	 * 
	 * Generates a token for an external service if it doesn't exist yet.
	 * 
	 * @param externalServiceInfo
	 * @return the generated token or the token related to the external service (if it was already created)
	 */
	@Path("external/{serviceId}")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public String generateExternalServiceToken(@PathParam("serviceId") String serviceId, @Context HttpServletRequest req) {
		try{
			AuthorizationEntry info = (AuthorizationEntry)req.getAttribute(AuthorizedCallFilter.AUTH_ATTRIBUTE);
			log.info("generator called for external service {} in context {} ",serviceId, info.getContext());

			if (serviceId.split(":").length>1) throw new Exception("invalid external service id: "+serviceId);

			String token = persistence.getExistingToken(serviceId, info.getContext(), Constants.DEFAULT_TOKEN_QUALIFIER);

			if (token==null){
				token= UUID.randomUUID().toString();
				persistence.saveAuthorizationEntry(token, info.getContext(), new ExternalServiceInfo(serviceId, info.getClientInfo().getId()), Constants.DEFAULT_TOKEN_QUALIFIER, info.getClientInfo().getId());
			}

			return token;
		}catch(Exception e){
			log.error("error generating token ",e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error Generating Token: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		} 
	}

	/**
	 * 
	 * Generates a token for an external service if it doesn't exist yet.
	 * 
	 * @param externalServiceInfo
	 * @return the generated token or the token related to the external service (if it was already created)
	 */
	@Path("external")
	@GET
	@Consumes(MediaType.APPLICATION_XML)
	public ExternalServiceList getExternalServiceCreated(@Context HttpServletRequest req) {
		try{
			AuthorizationEntry info = (AuthorizationEntry)req.getAttribute(AuthorizedCallFilter.AUTH_ATTRIBUTE);
			log.info("get External Service called in context {} by {} ",info.getContext(), info.getClientInfo().getId());
			ExternalServiceList toReturn = new ExternalServiceList(persistence.getExistingExternalServices(info.getClientInfo().getId(), info.getContext()));
			return toReturn;
		}catch(Exception e){
			log.error("error generating token ",e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error Generating Token: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		} 
	}
	
	@Path("node")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public String generateContainerToken(@NotNull ContainerInfo containerInfo, @QueryParam("context") String context,
			@Context HttpServletRequest req) {
		try{
			
			AuthorizationEntry info = (AuthorizationEntry)req.getAttribute(AuthorizedCallFilter.AUTH_ATTRIBUTE);
						
			if (context!=null)
				return generateTokenForContainerInfo(containerInfo, context);
			else if (info!=null){
				log.info("generator called for node {} in context {} ",containerInfo.getId(), info.getContext());
				return generateTokenForContainerInfo(containerInfo, info);
			}
			 
			throw new Exception("error trying to activate node (token and context are empty)");
		}catch(Exception e){
			log.error("error generating token ",e);
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
					.entity("Error Generating Token: "+e.getMessage()).type(MediaType.TEXT_PLAIN).build());
		} 
	}

	private String generateTokenForContainerInfo(ContainerInfo containerInfo,String context) throws Exception{
		if (containerInfo.getId().split(":").length!=2) throw new Exception("invalid container id: "+containerInfo.getId());

		String token = persistence.getExistingToken(containerInfo.getId(), context, Constants.DEFAULT_TOKEN_QUALIFIER);

		if( token ==null){
			token = UUID.randomUUID().toString();;
			persistence.saveAuthorizationEntry(token, context, containerInfo, Constants.DEFAULT_TOKEN_QUALIFIER, null);

		}
		return token;
	}
	
	private String generateTokenForContainerInfo(ContainerInfo containerInfo, AuthorizationEntry authInfo) throws Exception{
		if (containerInfo.getId().split(":").length!=2) throw new Exception("invalid container id: "+containerInfo.getId());

		String token = persistence.getExistingToken(containerInfo.getId(),authInfo.getContext(), Constants.DEFAULT_TOKEN_QUALIFIER);

		if( token ==null){
			token = UUID.randomUUID().toString();;
			persistence.saveAuthorizationEntry(token, authInfo.getContext(), containerInfo, Constants.DEFAULT_TOKEN_QUALIFIER, authInfo.getClientInfo().getId() );

		}
		return token;
	}
	
	private String generateTokenForServiceInfo(ServiceInfo serviceInfo, AuthorizationEntry authInfo) throws Exception{
		if (serviceInfo.getId().split(":").length!=3) throw new Exception("invalid service id: "+serviceInfo.getId());

		String token = persistence.getExistingToken(serviceInfo.getId(), authInfo.getContext(), Constants.DEFAULT_TOKEN_QUALIFIER);

		if( token ==null){
			token = UUID.randomUUID().toString();;
			persistence.saveAuthorizationEntry(token, authInfo.getContext(), serviceInfo, Constants.DEFAULT_TOKEN_QUALIFIER, authInfo.getClientInfo().getId() );
		}
		return token;
	}

}
