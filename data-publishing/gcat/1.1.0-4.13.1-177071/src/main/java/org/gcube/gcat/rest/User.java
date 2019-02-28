package org.gcube.gcat.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.gcube.gcat.ResourceInitializer;
import org.gcube.gcat.persistence.ckan.CKANUser;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Path(User.USERS)
public class User extends REST<CKANUser> implements org.gcube.gcat.api.interfaces.User<Response,Response> {
	
	protected static final String USER_ID_PARAMETER = "USER_ID";
	
	public User() {
		super(USERS, USER_ID_PARAMETER, CKANUser.class);
	}
	
	@GET
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String list() {
		return super.list(-1,-1);
	}
	
	@POST
	@Consumes(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Override
	public Response create(String json) {
		return super.create(json);
	}
	
	@GET
	@Path("/{" + USER_ID_PARAMETER + "}")
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Override
	public String read(@PathParam(USER_ID_PARAMETER) String username) {
		return super.read(username);
	}
	
	@PUT
	@Path("/{" + USER_ID_PARAMETER + "}")
	@Consumes(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Override
	public String update(@PathParam(USER_ID_PARAMETER) String username, String json) {
		return super.update(username, json);
	}
	
	@DELETE
	@Path("/{" + USER_ID_PARAMETER + "}")
	public Response delete(@PathParam(USER_ID_PARAMETER) String username) {
		return super.delete(username, false);
	}
	
}
