package org.gcube.datatransfer.resolver.services.tobackward;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.datatransfer.resolver.catalogue.CatalogueRequest;
import org.gcube.datatransfer.resolver.services.CatalogueResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CatalogueResolver.
 *
 * To Backward compatibility
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Nov 16, 2018
 */
@Path("catalogue")
public class BackCatalogueResolver {

	private static Logger logger = LoggerFactory.getLogger(BackCatalogueResolver.class);

	@POST
	@Path("")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postCatalogue(@Context HttpServletRequest req, CatalogueRequest jsonRequest) throws WebApplicationException{
		logger.info(this.getClass().getSimpleName()+" POST starts...");
		return new CatalogueResolver().postCatalogue(req, jsonRequest);

	}
}
