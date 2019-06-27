package org.gcube.gcat.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.gcube.gcat.ResourceInitializer;
import org.gcube.gcat.profile.ISProfile;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Path(Profile.PROFILES)
public class Profile extends BaseREST implements org.gcube.gcat.api.interfaces.Profile<Response,Response> {
	
	public static final String PROFILE_NAME_PARAMETER = "PROFILE_NAME";
	
	@Context
	private UriInfo uriInfo;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String list() {
		setCalledMethod("GET /" + PROFILES);
		try {
			ISProfile isProfile = new ISProfile();
			ArrayNode arrayNode = isProfile.list();
			return isProfile.getMapper().writeValueAsString(arrayNode);
		} catch(WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;
	
	@GET
	@Path("/{" + PROFILE_NAME_PARAMETER + "}")
	@Produces({MediaType.APPLICATION_XML, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	public String read(@PathParam(PROFILE_NAME_PARAMETER) String name,
			@DefaultValue(MediaType.APPLICATION_XML) @HeaderParam("Accept") String accept) {
		setCalledMethod("GET /" + PROFILES + "/{" + PROFILE_NAME_PARAMETER + "}");
		try {
			ISProfile isProfile = new ISProfile();
			boolean xml = false;
			if(accept.startsWith(MediaType.APPLICATION_XML)) {
				xml = true;
			}
			return isProfile.read(name, xml);
		} catch(WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	@PUT
	@Path("/{" + PROFILE_NAME_PARAMETER + "}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response createOrUpdate(@PathParam(PROFILE_NAME_PARAMETER) String name, String xml) {
		setCalledMethod("PUT /" + PROFILES + "/{" + PROFILE_NAME_PARAMETER + "}");
		try {
			ISProfile isProfile = new ISProfile();
			boolean created = isProfile.createOrUpdate(name, xml);
			ResponseBuilder responseBuilder = null;
			if(created) {
				responseBuilder = Response.status(Status.CREATED);
				responseBuilder.header(LOCATION_HEADER, uriInfo.getAbsolutePath());
			} else {
				responseBuilder = Response.status(Status.OK);
			}
			responseBuilder.entity(xml);
			return responseBuilder.type(MediaType.APPLICATION_XML).build();
		} catch(WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	@DELETE
	@Path("/{" + PROFILE_NAME_PARAMETER + "}")
	public Response delete(@PathParam(PROFILE_NAME_PARAMETER) String name) {
		setCalledMethod("DELETE /" + PROFILES + "/{" + PROFILE_NAME_PARAMETER + "}");
		try {
			ISProfile isProfile = new ISProfile();
			isProfile.delete(name);
			return Response.status(Status.NO_CONTENT).build();
		} catch(WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	@Override
	public Response create(String name, String xml) {
		return createOrUpdate(name, xml);
		
	}
	
	@Override
	public String read(String name) {
		return read(name, MediaType.APPLICATION_XML);
	}
	
	@Override
	public String update(String name, String xml) {
		return createOrUpdate(name, xml).getEntity().toString();
	}
	
}
