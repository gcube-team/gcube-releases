package org.gcube.datatransfer.resolver.requesthandler;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Provider
public class TokenSetter implements ContainerRequestFilter, ContainerResponseFilter {

	public static final String ROOT_SCOPE = "root-scope";

	public static final String ROOT_APP_TOKEN = "root-app-token";

	private static final Logger log = LoggerFactory.getLogger(TokenSetter.class);

	@Context ServletContext context;

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
    	log.info("TokenSetter Request called");

    	if(SecurityTokenProvider.instance.get()==null)
    		SecurityTokenProvider.instance.set(context.getInitParameter(ROOT_APP_TOKEN));

    	if(ScopeProvider.instance.get()==null)
    		ScopeProvider.instance.set(context.getInitParameter(ROOT_SCOPE));
    }

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		log.info("TokenSetter Response called");
		SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();

	}
}