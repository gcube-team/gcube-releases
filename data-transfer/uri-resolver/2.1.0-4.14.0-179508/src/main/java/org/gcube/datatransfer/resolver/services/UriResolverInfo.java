/**
 *
 */
package org.gcube.datatransfer.resolver.services;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 22, 2018
 */
@Path("info")
public class UriResolverInfo {

	@GET
	@Produces({MediaType.TEXT_HTML})
	@Path("")
	public InputStream info(@Context HttpServletRequest req) throws WebApplicationException{
		return new UriResolverIndex().index(req);
	}
}

