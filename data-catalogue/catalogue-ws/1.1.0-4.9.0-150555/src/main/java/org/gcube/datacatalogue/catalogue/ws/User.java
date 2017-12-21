package org.gcube.datacatalogue.catalogue.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.catalogue.utils.Constants;
import org.gcube.datacatalogue.catalogue.utils.Delegator;


@Path(Constants.USERS)
/**
 * User service endpoint.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class User {

	@GET
	@Path(Constants.SHOW_METHOD)
	@Produces(MediaType.TEXT_PLAIN)
	public String show(@Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.user_show
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return Delegator.delegateGet(caller, context, Constants.USER_SHOW, uriInfo);

	}

	@POST
	@Path(Constants.CREATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.user_create
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return Delegator.delegatePost(caller, context, Constants.USER_CREATE, json, uriInfo);

	}

	@DELETE
	@Path(Constants.DELETE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.user_delete
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return Delegator.delegatePost(caller, context, Constants.USER_DELETE, json, uriInfo);

	}

	@POST
	@Path(Constants.UPDATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String update(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.user_update
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return Delegator.delegatePost(caller, context, Constants.USER_UPDATE, json, uriInfo);

	}

}
