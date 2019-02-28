/**
 *
 */
package org.gcube.datatransfer.resolver.services;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.datatransfer.resolver.requesthandler.TokenSetter;
import org.gcube.datatransfer.resolver.services.error.ExceptionManager;
import org.gcube.datatransfer.resolver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 13, 2018
 */
@Path("/knime")
public class KnimeCreateResolver {

	private static Logger logger = LoggerFactory.getLogger(KnimeCreateResolver.class);

	private static String helpURI = "https://gcube.wiki.gcube-system.org/gcube/URI_Resolver#KNIME_Resolver";

	/**
	 * Post catalogue.
	 *
	 * @param req the req
	 * @param body the body
	 * @return the response
	 */
	@POST
	@Path("/create")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public Response createKnimeURL(@Context HttpServletRequest req) throws WebApplicationException{

		logger.info(this.getClass().getSimpleName()+" POST starts...");

		try{
			String contextToken = SecurityTokenProvider.instance.get();
			String scope = ScopeProvider.instance.get();
	//		logger.info("SecurityTokenProvider contextToken: "+contextToken);
			logger.info("ScopeProvider has scope: "+scope);

			String appToken = req.getServletContext().getInitParameter(TokenSetter.ROOT_APP_TOKEN);

			if(contextToken.compareTo(appToken)==0){
				logger.error("Token not passed, SecurityTokenProvider contains the root app token: "+appToken.substring(0,10)+"...");
				throw ExceptionManager.unauthorizedException(req, "You are not authorized. You must pass a token of VRE", this.getClass(), helpURI);
			}

			ScopeBean scopeBean = new ScopeBean(scope);

			if(scopeBean.is(Type.VRE)){

				String vreName = scopeBean.name();
				String knimeGetResolverURL = String.format("%s/%s/%s",  Util.getServerURL(req), "knime/get", vreName);
				String queryString =req.getQueryString()==null?"":req.getQueryString();

				if(req.getQueryString()!=null)
					knimeGetResolverURL+="?"+queryString;

				logger.info("Returning Knime Resolver URL: "+knimeGetResolverURL);
				return Response.ok(knimeGetResolverURL).header("Location", knimeGetResolverURL).build();

			}else{
				logger.error("The input scope "+scope+" is not a VRE");
				throw ExceptionManager.badRequestException(req, "Working in the "+scope+" scope that is not a VRE. Use a token of VRE", this.getClass(), helpURI);
			}
		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Sorry, an error occurred on creating Knime URL. Please, contact the support!";
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
