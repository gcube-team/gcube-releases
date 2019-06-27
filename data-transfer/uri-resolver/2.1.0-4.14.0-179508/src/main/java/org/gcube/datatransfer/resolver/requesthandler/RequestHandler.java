package org.gcube.datatransfer.resolver.requesthandler;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.resolver.ConstantsResolver;
import org.gcube.datatransfer.resolver.UriResolverServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class RequestHandler.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Mar 15, 2019
 */
@Provider
@PreMatching
public class RequestHandler implements ContainerRequestFilter, ContainerResponseFilter {

	public static final String ROOT_SCOPE = "root-scope";

	public static final String ROOT_APP_TOKEN = "root-app-token";

	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	@Context ServletContext context;

	@Context
	HttpServletRequest webRequest;

	@Context
	Application application;

	@Context
	ResourceContext resourceContext;

    /* (non-Javadoc)
     * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
     */
    @Override
    public void filter(ContainerRequestContext reqContext) throws IOException {
    	log.info(RequestHandler.class.getSimpleName() +" Request called");
    	
    	if(SecurityTokenProvider.instance.get()==null)
    		SecurityTokenProvider.instance.set(context.getInitParameter(ROOT_APP_TOKEN));

    	if(ScopeProvider.instance.get()==null)
    		ScopeProvider.instance.set(context.getInitParameter(ROOT_SCOPE));
    	
    	log.info("Token and Scope Provider set called");

		List<String> listOfPath = UriResolverServices.getInstance().getListOfResourcePath(application.getClasses());
	    log.debug("The resources are: {}", listOfPath);

        String path = reqContext.getUriInfo().getPath();
        log.debug("The path is: {}", path);
        
        if(path==null || path.isEmpty()) {
        	log.debug("The path is null or empty, redirecting to /index");
       	 	URI newRequestURI = reqContext.getUriInfo().getBaseUriBuilder().path("/index").build();
       	 	reqContext.setRequestUri(newRequestURI);
       	 	return;
        }
        
        String[] splittePath = null;
        boolean resourceToRedirectFound = false;
        String candidateResource = "";
        try {
        	splittePath = path.split("/");
        	if(splittePath!=null && splittePath.length>0) {
        		String requestedResourceName = splittePath[0];
        		log.debug("The resource requested is: {}",requestedResourceName);
        		
        		if(requestedResourceName!=null && !requestedResourceName.isEmpty()) {
        			for (String resource : listOfPath) {
        				log.trace("Is resource '{}' starting with '{}' ?",resource,requestedResourceName);
						if(resource.startsWith(requestedResourceName)) {
							log.trace("Yes it starts!");
							candidateResource = requestedResourceName;
							log.info("The candidate resource to manage the request is: {}",candidateResource);
							resourceToRedirectFound = true;
							break;
						}
					}
        			
        			//Try to manage as Catalogue Request ctlg, ctlg-p, etc.
        			if(!resourceToRedirectFound) {
        				log.info("Trying to manage as hard-coded case: {}", ConstantsResolver.resourcesHardCoded.toString());
        				String[] hardCode = ConstantsResolver.resourcesHardCoded;
        				
        				for (String resource : hardCode) {
        					log.trace("Is requested resource '{}' starting with hard-coded resource '{}'?",requestedResourceName,resource);
							if(requestedResourceName.startsWith(resource)) {
								log.trace("Yes it starts!");
								candidateResource = resource;
								log.info("The candidate resource to manage the request is the hard-coded resource: {}",candidateResource);
								resourceToRedirectFound = true;
								break;
							}
						}
        				
        			}
        			
        		}else
        			log.warn("It was not possible to get the resource name from the splitted path {}. No action performed", path);
        	}else {
        		  log.warn("It was not possible to split the path {}. No action performed", path);
        	}
        }catch (Exception e) {
			log.error("Error trying to retrieve the service able to manage the request. No action performed", e);
		}
        
        if(resourceToRedirectFound) {
        	 log.debug("The input request '{}' can be managed by the service '{}'. No redirect performed", path, candidateResource);
        }else {
        	 log.info("No resource/service found to manage the input request '{}'", path);
        	 String newPath = String.format("/%s/%s", ConstantsResolver.defaultServiceToRedirect,path);
        	 //log.debug("The path to redirect is '{}'", newPath);
        	 URI newRequestURI = reqContext.getUriInfo().getBaseUriBuilder().path(newPath).build();
        	 log.info("Redirect to URI path '{}'", newRequestURI.toString());
	    	 reqContext.setRequestUri(newRequestURI);
        }
    }

	/* (non-Javadoc)
	 * @see javax.ws.rs.container.ContainerResponseFilter#filter(javax.ws.rs.container.ContainerRequestContext, javax.ws.rs.container.ContainerResponseContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		log.info(RequestHandler.class.getSimpleName() +" Response called");
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
		log.info("Token and Scope Provider reset called");

	}
}