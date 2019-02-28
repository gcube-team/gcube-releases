/**
 *
 */
package org.gcube.datatransfer.resolver.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.gcube.datatransfer.resolver.services.error.ExceptionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 22, 2018
 */
@Path("/")
public class UriResolverIndex {

	private static Logger logger = LoggerFactory.getLogger(UriResolverIndex.class);

	@GET
	@Produces({MediaType.TEXT_HTML})
	@Path("index")
	public InputStream index(@Context HttpServletRequest req) throws WebApplicationException{

		String indexFile = "/WEB-INF/jsp/index.jsp";

		try{
			logger.info("UriResolverIndex called");
	        String realPath = req.getServletContext().getRealPath(indexFile);
	        return new FileInputStream(new File(realPath));
		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Index.jsp not found. Please, contact the support!";
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), null);
			}
			//ALREADY MANAGED AS WebApplicationException
			logger.error("Exception:", e);
			throw (WebApplicationException) e;
		}
	}

	@GET
	@Produces({MediaType.TEXT_HTML})
	@Path("info")
	public InputStream info(@Context HttpServletRequest req){
		return index(req);
	}
}

