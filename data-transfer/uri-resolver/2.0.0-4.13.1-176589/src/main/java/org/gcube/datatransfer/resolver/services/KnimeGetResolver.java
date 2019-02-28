/**
 *
 */
package org.gcube.datatransfer.resolver.services;

import java.net.URI;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.gcube.datatransfer.resolver.applicationprofile.ApplicationProfileReader;
import org.gcube.datatransfer.resolver.caches.LoadingVREsScopeCache;
import org.gcube.datatransfer.resolver.services.error.ExceptionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheLoader.InvalidCacheLoadException;


/**
 * The Class KnimeGetResolver.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 13, 2018
 */
@Path("/knime")
public class KnimeGetResolver {

	private static Logger logger = LoggerFactory.getLogger(KnimeGetResolver.class);

	private static String helpURI = "https://gcube.wiki.gcube-system.org/gcube/URI_Resolver#KNIME_Resolver";

	private static final String ORG_GCUBE_PORTLETS_USER_KNIMEMODELSIMULATION_MANAGER_SERVICE_IMPL =
					"org.gcube.portlets.user.model-simulation-configuration.server.KnimeModelSimulationManagerServiceImpl";

	private static final String APPLICATION_PROFILE = "ApplicationProfile";

	private static final String KNIME_EXECUTOR_APPLICATION = null;

	/**
	 * Resolve knime url.
	 *
	 * @param req the req
	 * @param vreName the vre name
	 * @return the response
	 */
	@GET
	@Path("/get/{vreName}")
	public Response resolveKnimeURL(@Context HttpServletRequest req, @PathParam("vreName") String vreName) {
		logger.info(this.getClass().getSimpleName()+" GET starts...");

		try {
			if(vreName==null || vreName.isEmpty()){
				logger.error("The path parameter 'vreName' not found or empty in the path");
				throw ExceptionManager.badRequestException(req, "Mandatory path parameter 'vreName' not found or empty", this.getClass(), helpURI);
			}

			String fullScope = "";
			try{
				 fullScope = LoadingVREsScopeCache.get(vreName);
			}catch(ExecutionException | InvalidCacheLoadException e){
				logger.error("Error on getting the fullscope from cache for vreName "+vreName, e);
				throw ExceptionManager.wrongParameterException(req, "Error on getting full scope for the VRE name "+vreName+". Is it registered as VRE in the D4Science Infrastructure System?", this.getClass(), helpURI);
			}
			ApplicationProfileReader reader = null;
			try{
				reader = new ApplicationProfileReader(fullScope, APPLICATION_PROFILE, ORG_GCUBE_PORTLETS_USER_KNIMEMODELSIMULATION_MANAGER_SERVICE_IMPL, false);
			}catch(Exception e){
				logger.error("Error on reading the "+APPLICATION_PROFILE+" with APPID: "+ORG_GCUBE_PORTLETS_USER_KNIMEMODELSIMULATION_MANAGER_SERVICE_IMPL, e);
				throw ExceptionManager.internalErrorException(req, "Error on reading the Application Profile for the "+KNIME_EXECUTOR_APPLICATION+". Please contact the support", this.getClass(), helpURI);
			}

			//READ THE KNIME URL PORTLET FROM APPLICATION PROFRILE IN THE SCOPE fullScope
			String knimeExecutorEndPoint = reader.getApplicationProfile().getUrl();
			//CHECKING THE QUERY STRING
			String queryString = req.getQueryString()!=null?req.getQueryString():"";
			String knimeExecutorURL = String.format("%s?%s", knimeExecutorEndPoint, queryString);
			logger.info("Resolving the Knime URL with: "+knimeExecutorURL);
			return Response.seeOther(new URI(knimeExecutorURL)).build();

		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Sorry, an error occurred on resolving the Knime URL. Please, contact the support!";
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
