package org.gcube.portal.social.networking.swagger.config;

import io.swagger.jaxrs.config.BeanConfig;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Configuration servlet for swagger.
 * @author Costantino Perciante at ISTI-CNR
 */
@SuppressWarnings("serial")
public class Bootstrap extends HttpServlet{
	
	public static final String GCUBE_TOKEN_IN_QUERY_DEF = "gcube-token-query";
	public static final String GCUBE_TOKEN_IN_HEADER_DEF = "gcube-token-header";

	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setResourcePackage("org.gcube.portal.social.networking.ws");
		beanConfig.setPrettyPrint(true); // print pretty json
		beanConfig.setHost("socialnetworking1.d4science.org");
		beanConfig.setBasePath("social-networking-library-ws/rest");
		beanConfig.setScan(true);

	}
}