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

	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion("2.0.0");
		beanConfig.setDescription("This is the official documentation of the second version of the Social-Networking RESTful interface.\nEnjoy it!");
		beanConfig.setSchemes(new String[]{"https"});
		beanConfig.setTitle("Social-Networking RESTful service documentation");
		beanConfig.setBasePath("social-networking-library-ws/rest/");
		beanConfig.setResourcePackage("org.gcube.portal.social.networking.ws");
		beanConfig.setPrettyPrint(true); // print pretty json
		beanConfig.setContact("costantino.perciante@isti.cnr.it");
		beanConfig.setHost("i-marine.d4science.org");
		beanConfig.setSchemes(new String[]{"https", "http"});
		beanConfig.setScan(true);

		// For security add the following to the yaml file
		//		securityDefinitions: 
		//			  gcube-token: 
		//			    type: apiKey
		//			    name: gcube-token
		//			    in: header/query
	}
}