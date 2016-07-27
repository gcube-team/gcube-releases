package org.gcube.data.access.ckanconnector;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import lombok.extern.slf4j.Slf4j;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.scope.api.ScopeProvider;

import com.google.gson.Gson;

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.exceptions.CkanNotFoundException;
import eu.trentorise.opendata.jackan.model.CkanOrganization;


@Path("organization")
@Slf4j
public class OrganizationManager {

	@Context ServletContext context;

	@PUT
	@Path("/{name}")
	public Response create(@PathParam("name") String organizationName) {
		log.info("create called");
		try{
			if (AuthorizationProvider.instance.get()==null || AuthorizationProvider.instance.get().getUserName() == null ) return Response.status(Status.UNAUTHORIZED).build();

			String ckanKey = context.getInitParameter("ckanKey");
			int internalPort = Integer.parseInt(context.getInitParameter("internalPort"));		
			CkanClient ckanClient = new CkanClient("http://127.0.0.1:"+internalPort, ckanKey);

			CkanOrganization org = new CkanOrganization(organizationName.replaceAll(" ", "_").replace(".", "_").toLowerCase());
			org.setDisplayName(organizationName);
			org.setTitle(organizationName);
			ckanClient.createOrganization(org);

			log.info("create organizzation {} called from user {} in scope {}",organizationName, AuthorizationProvider.instance.get().getUserName(), ScopeProvider.instance.get());
			return Response.ok().build();
		}catch(Exception e){
			log.error("error trying to create organization "+organizationName,e);
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@PathParam("name") String organizationName) {
		log.info("get called with name {}", organizationName);

		if (AuthorizationProvider.instance.get()==null || AuthorizationProvider.instance.get().getUserName() == null ) throw new WebApplicationException("user authentication needed",Response.Status.UNAUTHORIZED);

		String ckanKey = context.getInitParameter("ckanKey");
		int internalPort = Integer.parseInt(context.getInitParameter("internalPort"));		
		
		try{
			CkanClient ckanClient = new CkanClient("http://127.0.0.1:"+internalPort, ckanKey);
			CkanOrganization org = ckanClient.getOrganization(organizationName.replaceAll(" ", "_").replace(".", "_").toLowerCase());
			log.trace("organization {} found",organizationName);
			return new Gson().toJson(org);
		}catch(CkanNotFoundException e){
			log.error("organization {} doesn't exist",organizationName);
			throw new WebApplicationException("organization "+organizationName+" doesn't exist",Response.Status.NOT_FOUND);
		} catch (Exception e) {
			log.error("error trying to contect ckan",e);
			throw new WebApplicationException("error trying to contect ckan",Response.Status.INTERNAL_SERVER_ERROR);
		}


	}

}
