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
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 22, 2018
 */
@Path("/")
public class SMPIDResolver {

	/**
	 *
	 */
	private static final String helpURI = "https://wiki.gcube-system.org/gcube/URI_Resolver#SMP-ID_Resolver";

	private static final String SMP_ID = "smp-id";

	private static Logger logger = LoggerFactory.getLogger(SMPIDResolver.class);


	/**
	 * Gets the smpid.
	 *
	 * @param req the req
	 * @param smpId the smp id
	 * @param fileName the file name
	 * @param contentType the content type
	 * @param validation the validation
	 * @return the smpid
	 * @throws WebApplicationException the web application exception
	 */
	@GET
	@Path("id")
	public Response getSMPID(@Context HttpServletRequest req,
		@QueryParam(SMP_ID) @Nullable String smpId,
		@QueryParam(ConstantsResolver.QUERY_PARAM_FILE_NAME) String fileName,
		@QueryParam(ConstantsResolver.QUERY_PARAM_CONTENT_TYPE) String contentType,
		@QueryParam(ConstantsResolver.QUERY_PARAM_VALIDATION) Boolean validation) throws WebApplicationException{
		logger.info(this.getClass().getSimpleName()+" GET starts...");

		try{
			//Checking mandatory parameter smpId
			if(smpId==null || smpId.isEmpty()){
				logger.error(SMP_ID+" not found");
				throw ExceptionManager.badRequestException(req, "Missing mandatory parameter "+SMP_ID, SMPIDResolver.class, helpURI);
			}

			return StorageIDResolver.resolveStorageId(req, smpId, fileName, contentType, validation);

		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Error occurred on resolving the "+SMP_ID+": "+smpId+". Please, contact the support!";
				if(e.getCause()!=null)
					error+="\n\nCaused: "+e.getCause().getMessage();
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), helpURI);
			}
			//ALREADY MANAGED AS WebApplicationException
			logger.error("Exception:", e);
			throw (WebApplicationException) e;
		}
	}
}
