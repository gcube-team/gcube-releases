/**
 *
 */

package org.gcube.datatransfer.resolver.services;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.datatransfer.resolver.UriResolverServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * The Class ResourceListingResource.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Feb 14, 2019
 */
@Path("resources")
@Singleton
public class UriResolverResources {

	private static Logger log = LoggerFactory.getLogger(UriResolverResources.class);

	/**
	 * Show all.
	 *
	 * @param application the application
	 * @param request the request
	 * @return the response
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServices(@Context Application application, @Context HttpServletRequest request) {
		log.info("Get Services called");

		ObjectNode rootResources = UriResolverServices.getInstance().getListOfResourceNode(application.getClasses());
		return Response.ok().entity(rootResources).build();
	}

}
