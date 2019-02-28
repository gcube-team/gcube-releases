/**
 *
 */
package org.gcube.datatransfer.resolver.services;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.gcube.datatransfer.resolver.ConstantsResolver;
import org.gcube.datatransfer.resolver.services.error.ExceptionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class SMPResolver.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Dec 14, 2018
 */
@Path("/")
public class SMPResolver {

	/**
	 *
	 */
	private static final String helpURI = "https://wiki.gcube-system.org/gcube/URI_Resolver#SMP_Resolver";

	private static final String SMP_URI = "smp-uri";

	private static Logger logger = LoggerFactory.getLogger(SMPResolver.class);

	/**
	 * Gets the smpuri.
	 *
	 * @param req the req
	 * @param smpURI the smp uri
	 * @param fileName the file name
	 * @param contentType the content type
	 * @param validation the validation
	 * @return the smpuri
	 * @throws WebApplicationException the web application exception
	 */
	@GET
	@Path("smp")
	public Response getSMPURI(@Context HttpServletRequest req,
		@QueryParam(SMP_URI) @Nullable
		String smpURI,
		@QueryParam(ConstantsResolver.QUERY_PARAM_FILE_NAME) String fileName,
		@QueryParam(ConstantsResolver.QUERY_PARAM_CONTENT_TYPE) String contentType,
		@QueryParam(ConstantsResolver.QUERY_PARAM_VALIDATION) Boolean validation) throws WebApplicationException{

		logger.info(this.getClass().getSimpleName()+" GET starts...");

		try{
			//Checking mandatory parameter smpURI
			if(smpURI==null || smpURI.isEmpty()){
				logger.error(SMP_URI+" not found");
				throw ExceptionManager.badRequestException(req, "Missing mandatory parameter "+SMP_URI, SMPResolver.class, helpURI);
			}

			return StorageIDResolver.resolveStorageId(req, smpURI, fileName, contentType, validation);

		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Error occurred on resolving the smpURI "+smpURI+". Please, contact the support!";
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), helpURI);
			}
			//ALREADY MANAGED AS WebApplicationException
			logger.error("Exception:", e);
			throw (WebApplicationException) e;
		}

	}
}
