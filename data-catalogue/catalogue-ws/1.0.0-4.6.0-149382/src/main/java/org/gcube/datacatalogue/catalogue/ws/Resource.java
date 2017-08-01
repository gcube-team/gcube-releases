package org.gcube.datacatalogue.catalogue.ws;


import javax.servlet.http.HttpServletRequest;
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
import org.gcube.datacatalogue.catalogue.utils.CatalogueUtils;
import org.gcube.datacatalogue.catalogue.utils.Constants;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

@Path(Constants.RESOURCES)
/**
 * Resource service endpoint.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Resource {

	@GET
	@Path(Constants.SHOW_METHOD)
	@Produces(MediaType.APPLICATION_JSON)
	public String show(@Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.resource_show
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return CatalogueUtils.delegateGet(caller, context, Constants.RESOURCE_SHOW, uriInfo);

	}

	@POST
	@Path(Constants.CREATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.resource_create
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return CatalogueUtils.delegatePost(caller, context, Constants.RESOURCE_CREATE, json, uriInfo);

	}

	@POST
	@Path(Constants.CREATE_METHOD)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(
			FormDataMultiPart multiPart, @Context UriInfo uriInfo,
			@Context final HttpServletRequest request
			){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.resource_create
		// see also multipart https://www.mkyong.com/webservices/jax-rs/file-upload-example-in-jersey/
		/*List<BodyPart> bodyParts = multiPart.getBodyParts();
		for (BodyPart bodyPart : bodyParts) {
			logger.info("Body name is " + bodyPart.getContentDisposition().getFileName());
		}

		Map<String, List<FormDataBodyPart>> fields = multiPart.getFields();
		logger.info(fields);*/

		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();		
		try{
			return CatalogueUtils.delegatePost(caller, context, Constants.RESOURCE_CREATE, multiPart, uriInfo);
		}catch(Exception e){
			return CatalogueUtils.createJSONOnFailure(e.getMessage());
		}

	}

	@DELETE
	@Path(Constants.DELETE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.resource_delete
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return CatalogueUtils.delegatePost(caller, context, Constants.RESOURCE_DELETE, json, uriInfo);

	}

	@POST
	@Path(Constants.UPDATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String update(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.resource_update
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return CatalogueUtils.delegatePost(caller, context, Constants.RESOURCE_UPDATE, json, uriInfo);

	}

	@POST
	@Path(Constants.PATCH_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String patch(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.resource_patch
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return CatalogueUtils.delegatePost(caller, context, Constants.RESOURCE_PATCH, json, uriInfo);

	}

}
